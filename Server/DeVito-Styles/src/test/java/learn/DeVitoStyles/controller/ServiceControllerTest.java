package learn.DeVitoStyles.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import learn.DeVitoStyles.domain.Result;
import learn.DeVitoStyles.domain.ResultType;
import learn.DeVitoStyles.domain.ShopService;
import learn.DeVitoStyles.models.Service;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.math.BigDecimal;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasSize;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
class ServiceControllerTest {

    @Mock
    private ShopService service;

    private MockMvc mvc;
    private final ObjectMapper mapper = new ObjectMapper().findAndRegisterModules();

    @BeforeEach
    void setUp() {
        mvc = MockMvcBuilders.standaloneSetup(new ServiceController(service)).build();
    }

    private static Service shopService(int id) {
        return new Service(id, "Haircut", 30, new BigDecimal("35.00"), "Classic cut");
    }

    private static <T> Result<T> success(T payload) {
        Result<T> result = new Result<>();
        result.setpayload(payload);
        return result;
    }

    private static <T> Result<T> failure(String message, ResultType type) {
        Result<T> result = new Result<>();
        result.addErrorMessage(message, type);
        return result;
    }

    @Test
    void findAll_returns200WithServices() throws Exception {
        when(service.findAll()).thenReturn(success(List.of(shopService(1), shopService(2))));

        mvc.perform(get("/api/service"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].serviceId").value(1))
                .andExpect(jsonPath("$[0].name").value("Haircut"))
                .andExpect(jsonPath("$[1].serviceId").value(2));

        verify(service).findAll();
    }

    @Test
    void findById_whenFound_returns200WithService() throws Exception {
        when(service.findById(1)).thenReturn(success(shopService(1)));

        mvc.perform(get("/api/service/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.serviceId").value(1))
                .andExpect(jsonPath("$.durationMinutes").value(30))
                .andExpect(jsonPath("$.price").value(35.00))
                .andExpect(jsonPath("$.description").value("Classic cut"));

        verify(service).findById(1);
    }

    @Test
    void findById_whenNotFound_returns404() throws Exception {
        when(service.findById(99)).thenReturn(failure("Service not found", ResultType.NOT_FOUND));

        mvc.perform(get("/api/service/99"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$[0]").value("Service not found"));
    }

    @Test
    void findById_whenInvalid_returns400() throws Exception {
        when(service.findById(0)).thenReturn(failure("Service id must be positive", ResultType.INVALID));

        mvc.perform(get("/api/service/0"))
                .andExpect(status().isBadRequest());
    }

    @Test
    void create_whenSuccess_returns201WithService() throws Exception {
        when(service.create(any(Service.class))).thenReturn(success(shopService(8)));

        mvc.perform(post("/api/service")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(shopService(0))))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.serviceId").value(8))
                .andExpect(jsonPath("$.name").value("Haircut"));

        ArgumentCaptor<Service> captor = ArgumentCaptor.forClass(Service.class);
        verify(service).create(captor.capture());
        assertThat(captor.getValue()).isEqualTo(shopService(0));
    }

    @Test
    void create_whenInvalid_returns400() throws Exception {
        when(service.create(any(Service.class))).thenReturn(failure("Name is required", ResultType.INVALID));

        mvc.perform(post("/api/service")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(shopService(0))))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$[0]").value("Name is required"));
    }

    @Test
    void create_whenConflict_returns409() throws Exception {
        when(service.create(any(Service.class))).thenReturn(failure("Service already exists", ResultType.CONFLICT));

        mvc.perform(post("/api/service")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(shopService(0))))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$[0]").value("Service already exists"));
    }

    @Test
    void update_whenSuccess_returns200WithService() throws Exception {
        Service updated = new Service(3, "Beard Trim", 15, new BigDecimal("15.00"), "Shape up");
        when(service.update(any(Service.class))).thenReturn(success(updated));

        mvc.perform(put("/api/service/3")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(updated)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.serviceId").value(3))
                .andExpect(jsonPath("$.name").value("Beard Trim"));

        ArgumentCaptor<Service> captor = ArgumentCaptor.forClass(Service.class);
        verify(service).update(captor.capture());
        assertThat(captor.getValue()).isEqualTo(updated);
    }

    @Test
    void update_whenPathIdMismatch_returns409WithoutCallingService() throws Exception {
        mvc.perform(put("/api/service/4")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(shopService(3))))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$[0]").value("Path ID and body ID must match"));

        verifyNoInteractions(service);
    }

    @Test
    void update_whenNotFound_returns404() throws Exception {
        when(service.update(any(Service.class))).thenReturn(failure("Service not found", ResultType.NOT_FOUND));

        mvc.perform(put("/api/service/3")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(shopService(3))))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$[0]").value("Service not found"));
    }

    @Test
    void update_whenInvalid_returns400() throws Exception {
        when(service.update(any(Service.class))).thenReturn(failure("Price must be positive", ResultType.INVALID));

        mvc.perform(put("/api/service/3")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(shopService(3))))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$[0]").value("Price must be positive"));
    }

    @Test
    void delete_whenSuccess_returns204() throws Exception {
        when(service.deleteById(3)).thenReturn(new Result<>());

        mvc.perform(delete("/api/service/3"))
                .andExpect(status().isNoContent())
                .andExpect(content().string(""));

        verify(service).deleteById(3);
    }

    @Test
    void delete_whenNotFound_returns404() throws Exception {
        when(service.deleteById(99)).thenReturn(failure("Service not found", ResultType.NOT_FOUND));

        mvc.perform(delete("/api/service/99"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$[0]").value("Service not found"));
    }

    @Test
    void delete_whenConflict_returns409() throws Exception {
        when(service.deleteById(3)).thenReturn(failure("Service is in use", ResultType.CONFLICT));

        mvc.perform(delete("/api/service/3"))
                .andExpect(status().isConflict());
    }
}
