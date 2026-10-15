package learn.DeVitoStyles.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import learn.DeVitoStyles.domain.Result;
import learn.DeVitoStyles.domain.ResultType;
import learn.DeVitoStyles.domain.ReviewService;
import learn.DeVitoStyles.models.Review;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
class ReviewControllerTest {

    @Mock
    private ReviewService service;

    private MockMvc mvc;
    private final ObjectMapper mapper = new ObjectMapper().findAndRegisterModules();

    @BeforeEach
    void setUp() {
        mvc = MockMvcBuilders.standaloneSetup(new ReviewController(service)).build();
    }

    private static Review review(int id) {
        Review review = new Review();
        review.setReviewId(id);
        review.setUserId(2);
        review.setAppointmentId(7);
        review.setBarberId(4);
        review.setRating(5);
        review.setReviewText("Best fade in town");
        review.setCreatedAt(LocalDateTime.of(2026, 10, 1, 12, 0));
        return review;
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
    void findById_whenFound_returns200WithReview() throws Exception {
        when(service.findById(3)).thenReturn(success(review(3)));

        mvc.perform(get("/api/review/3"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.reviewId").value(3))
                .andExpect(jsonPath("$.appointmentId").value(7))
                .andExpect(jsonPath("$.barberId").value(4))
                .andExpect(jsonPath("$.rating").value(5))
                .andExpect(jsonPath("$.reviewText").value("Best fade in town"));

        verify(service).findById(3);
    }

    @Test
    void findById_whenNotFound_returns404() throws Exception {
        when(service.findById(99)).thenReturn(failure("Review not found", ResultType.NOT_FOUND));

        mvc.perform(get("/api/review/99"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$[0]").value("Review not found"));
    }

    @Test
    void findById_whenInvalid_returns400() throws Exception {
        when(service.findById(0)).thenReturn(failure("Review id must be positive", ResultType.INVALID));

        mvc.perform(get("/api/review/0"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$[0]").value("Review id must be positive"));
    }

    @Test
    void create_whenSuccess_returns201WithReview() throws Exception {
        when(service.add(any(Review.class))).thenReturn(success(review(11)));

        mvc.perform(post("/api/review")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(review(0))))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.reviewId").value(11))
                .andExpect(jsonPath("$.rating").value(5));

        ArgumentCaptor<Review> captor = ArgumentCaptor.forClass(Review.class);
        verify(service).add(captor.capture());
        Review sent = captor.getValue();
        assertThat(sent.getReviewId()).isZero();
        assertThat(sent.getUserId()).isEqualTo(2);
        assertThat(sent.getAppointmentId()).isEqualTo(7);
        assertThat(sent.getBarberId()).isEqualTo(4);
        assertThat(sent.getRating()).isEqualTo(5);
        assertThat(sent.getReviewText()).isEqualTo("Best fade in town");
        assertThat(sent.getCreatedAt()).isEqualTo(LocalDateTime.of(2026, 10, 1, 12, 0));
    }

    @Test
    void create_whenInvalid_returns400() throws Exception {
        when(service.add(any(Review.class))).thenReturn(failure("Rating must be between 1 and 5", ResultType.INVALID));

        mvc.perform(post("/api/review")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(review(0))))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$[0]").value("Rating must be between 1 and 5"));
    }

    @Test
    void create_whenAppointmentNotFound_returns404() throws Exception {
        when(service.add(any(Review.class))).thenReturn(failure("Appointment not found", ResultType.NOT_FOUND));

        mvc.perform(post("/api/review")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(review(0))))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$[0]").value("Appointment not found"));
    }

    @Test
    void create_whenAlreadyReviewed_returns409() throws Exception {
        when(service.add(any(Review.class))).thenReturn(failure("Appointment already reviewed", ResultType.CONFLICT));

        mvc.perform(post("/api/review")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(review(0))))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$[0]").value("Appointment already reviewed"));
    }
}
