package learn.DeVitoStyles.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import learn.DeVitoStyles.domain.BarberService;
import learn.DeVitoStyles.domain.Result;
import learn.DeVitoStyles.domain.ResultType;
import learn.DeVitoStyles.models.Barber;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasSize;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
class BarberControllerTest {

    @Mock
    private BarberService service;

    private MockMvc mvc;
    private final ObjectMapper mapper = new ObjectMapper().findAndRegisterModules();

    @BeforeEach
    void setUp() {
        mvc = MockMvcBuilders.standaloneSetup(new BarberController(service)).build();
    }

    private static Barber barber(int id) {
        return new Barber(id, "Vito", "DeVito", "AVAILABLE", "Fades", "https://img/vito.png",
                "Master Barber", "20 years cutting hair", 2006, "https://instagram.com/vito", 1, true);
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
    void findAll_returns200WithBarbers() throws Exception {
        when(service.findAll()).thenReturn(success(List.of(barber(1), barber(2))));

        mvc.perform(get("/api/barber"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].barberId").value(1))
                .andExpect(jsonPath("$[1].barberId").value(2))
                .andExpect(jsonPath("$[0].firstName").value("Vito"));

        verify(service).findAll();
    }

    @Test
    void findById_whenFound_returns200WithBarber() throws Exception {
        when(service.findById(1)).thenReturn(success(barber(1)));

        mvc.perform(get("/api/barber/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.barberId").value(1))
                .andExpect(jsonPath("$.lastName").value("DeVito"))
                .andExpect(jsonPath("$.specialization").value("Fades"))
                .andExpect(jsonPath("$.startYear").value(2006));

        verify(service).findById(1);
    }

    @Test
    void findById_whenNotFound_returns404() throws Exception {
        when(service.findById(99)).thenReturn(failure("Barber not found", ResultType.NOT_FOUND));

        mvc.perform(get("/api/barber/99"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$[0]").value("Barber not found"));
    }

    @Test
    void findById_whenInvalid_returns400() throws Exception {
        when(service.findById(0)).thenReturn(failure("Barber id must be positive", ResultType.INVALID));

        mvc.perform(get("/api/barber/0"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$[0]").value("Barber id must be positive"));
    }

    @Test
    void create_whenSuccess_returns201WithCreatedBarber() throws Exception {
        when(service.create(any(Barber.class))).thenReturn(success(barber(5)));

        mvc.perform(post("/api/barber")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(barber(0))))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.barberId").value(5))
                .andExpect(jsonPath("$.firstName").value("Vito"));

        ArgumentCaptor<Barber> captor = ArgumentCaptor.forClass(Barber.class);
        verify(service).create(captor.capture());
        assertThat(captor.getValue().getBarberId()).isZero();
        assertThat(captor.getValue().getFirstName()).isEqualTo("Vito");
        assertThat(captor.getValue().getLastName()).isEqualTo("DeVito");
        assertThat(captor.getValue().getSpecialization()).isEqualTo("Fades");
    }

    @Test
    void create_whenInvalid_returns400() throws Exception {
        when(service.create(any(Barber.class))).thenReturn(failure("First name is required", ResultType.INVALID));

        mvc.perform(post("/api/barber")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(barber(0))))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$[0]").value("First name is required"));
    }

    @Test
    void create_whenConflict_returns409() throws Exception {
        when(service.create(any(Barber.class))).thenReturn(failure("Barber already exists", ResultType.CONFLICT));

        mvc.perform(post("/api/barber")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(barber(0))))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$[0]").value("Barber already exists"));
    }

    @Test
    void update_whenSuccess_returns200WithUpdatedBarber() throws Exception {
        Barber updated = barber(3);
        updated.setSpecialization("Beards");
        when(service.update(any(Barber.class))).thenReturn(success(updated));

        mvc.perform(put("/api/barber/3")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(updated)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.barberId").value(3))
                .andExpect(jsonPath("$.specialization").value("Beards"));

        ArgumentCaptor<Barber> captor = ArgumentCaptor.forClass(Barber.class);
        verify(service).update(captor.capture());
        assertThat(captor.getValue().getBarberId()).isEqualTo(3);
        assertThat(captor.getValue().getSpecialization()).isEqualTo("Beards");
    }

    @Test
    void update_whenPathIdMismatch_returns409WithoutCallingService() throws Exception {
        mvc.perform(put("/api/barber/4")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(barber(3))))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$[0]").value("Path IO and barber ID don't match"));

        verifyNoInteractions(service);
    }

    @Test
    void update_whenNotFound_returns404() throws Exception {
        when(service.update(any(Barber.class))).thenReturn(failure("Barber not found", ResultType.NOT_FOUND));

        mvc.perform(put("/api/barber/3")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(barber(3))))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$[0]").value("Barber not found"));
    }

    @Test
    void update_whenInvalid_returns400() throws Exception {
        when(service.update(any(Barber.class))).thenReturn(failure("Last name is required", ResultType.INVALID));

        mvc.perform(put("/api/barber/3")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(barber(3))))
                .andExpect(status().isBadRequest());
    }

    @Test
    void delete_whenSuccess_returns204() throws Exception {
        when(service.delete(3)).thenReturn(new Result<>());

        mvc.perform(delete("/api/barber/3"))
                .andExpect(status().isNoContent())
                .andExpect(content().string(""));

        verify(service).delete(3);
    }

    @Test
    void delete_whenNotFound_returns404() throws Exception {
        when(service.delete(99)).thenReturn(failure("Barber not found", ResultType.NOT_FOUND));

        mvc.perform(delete("/api/barber/99"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$[0]").value("Barber not found"));
    }

    @Test
    void delete_whenConflict_returns409() throws Exception {
        when(service.delete(3)).thenReturn(failure("Barber has appointments", ResultType.CONFLICT));

        mvc.perform(delete("/api/barber/3"))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$[0]").value("Barber has appointments"));
    }
}
