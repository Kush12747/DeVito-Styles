package learn.DeVitoStyles.domain;

import learn.DeVitoStyles.data.interfaces.AppointmentRepository;
import learn.DeVitoStyles.data.interfaces.ReviewRepository;
import learn.DeVitoStyles.models.Appointment;
import learn.DeVitoStyles.models.Review;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ReviewServiceTest {

    @Mock private ReviewRepository repository;
    @Mock private AppointmentRepository appointmentRepository;

    @InjectMocks private ReviewService service;

    // =========================
    // FIND BY ID
    // =========================

    @Test
    void shouldFindReviewById() {
        Review review = makeReview(1);
        when(repository.findById(1)).thenReturn(review);

        Result<Review> result = service.findById(1);

        assertTrue(result.isSuccess());
        assertSame(review, result.getpayload());
    }

    @Test
    void shouldNotFindReviewByInvalidId() {
        Result<Review> result = service.findById(0);

        assertFalse(result.isSuccess());
        assertEquals(ResultType.INVALID, result.getResultType());
        assertEquals(List.of("Review ID must be greater than 0."), result.getMessages());
        verifyNoInteractions(repository);
    }

    @Test
    void shouldNotFindReviewById() {
        when(repository.findById(999)).thenReturn(null);

        Result<Review> result = service.findById(999);

        assertFalse(result.isSuccess());
        assertEquals(ResultType.NOT_FOUND, result.getResultType());
        assertEquals(List.of("Review not found"), result.getMessages());
    }

    // =========================
    // ADD - happy path
    // =========================

    @Test
    void shouldAddReview() {
        Review review = makeReview(0);
        Review added = makeReview(5);

        when(repository.findByAppointmentId(1)).thenReturn(null);
        when(appointmentRepository.findById(1)).thenReturn(makeAppointment(1, "COMPLETED"));
        when(repository.add(review)).thenReturn(added);

        Result<Review> result = service.add(review);

        assertTrue(result.isSuccess());
        assertEquals(5, result.getpayload().getReviewId());
        verify(repository).add(review);
    }

    @Test
    void shouldAddReviewForCompletedStatusIgnoringCase() {
        Review review = makeReview(0);

        when(repository.findByAppointmentId(1)).thenReturn(null);
        when(appointmentRepository.findById(1)).thenReturn(makeAppointment(1, "completed"));
        when(repository.add(review)).thenReturn(makeReview(5));

        Result<Review> result = service.add(review);

        assertTrue(result.isSuccess());
    }

    @Test
    void shouldAddReviewWithoutText() {
        Review review = makeReview(0);
        review.setReviewText(null);

        when(repository.findByAppointmentId(1)).thenReturn(null);
        when(appointmentRepository.findById(1)).thenReturn(makeAppointment(1, "COMPLETED"));
        when(repository.add(review)).thenReturn(makeReview(5));

        Result<Review> result = service.add(review);

        assertTrue(result.isSuccess());
    }

    // =========================
    // ADD - validation
    // =========================

    @Test
    void shouldNotAddNullReview() {
        assertAddInvalid(null, "Review can't be null");
    }

    @Test
    void shouldNotAddWithoutUserId() {
        Review review = makeReview(0);
        review.setUserId(0);

        assertAddInvalid(review, "User is required");
    }

    @Test
    void shouldNotAddWithoutAppointmentId() {
        Review review = makeReview(0);
        review.setAppointmentId(0);

        assertAddInvalid(review, "Appointment id is required");
    }

    @Test
    void shouldNotAddWithoutBarberId() {
        Review review = makeReview(0);
        review.setBarberId(0);

        assertAddInvalid(review, "Barber id is required");
    }

    @Test
    void shouldNotAddWithRatingAboveFive() {
        Review review = makeReview(0);
        review.setRating(6);

        assertAddInvalid(review, "Rating must be between 1 and 5");
    }

    @Test
    void shouldNotAddWithRatingBelowOne() {
        Review review = makeReview(0);
        review.setRating(0);

        assertAddInvalid(review, "Rating must be between 1 and 5");
    }

    @Test
    void shouldNotAddWithTooLongText() {
        Review review = makeReview(0);
        review.setReviewText("a".repeat(1001));

        assertAddInvalid(review, "Review must be less than 1000 characters");
    }

    // =========================
    // ADD - business rules
    // =========================

    @Test
    void shouldNotAddDuplicateReview() {
        Review review = makeReview(0);
        when(repository.findByAppointmentId(1)).thenReturn(makeReview(99));

        Result<Review> result = service.add(review);

        assertFalse(result.isSuccess());
        assertEquals(ResultType.CONFLICT, result.getResultType());
        assertEquals(List.of("Review already exists for appointment"), result.getMessages());
        verifyNoInteractions(appointmentRepository);
        verify(repository, never()).add(any());
    }

    @Test
    void shouldNotAddWhenAppointmentNotFound() {
        Review review = makeReview(0);
        when(repository.findByAppointmentId(1)).thenReturn(null);
        when(appointmentRepository.findById(1)).thenReturn(null);

        Result<Review> result = service.add(review);

        assertFalse(result.isSuccess());
        assertEquals(ResultType.NOT_FOUND, result.getResultType());
        assertEquals(List.of("Appointment not found"), result.getMessages());
        verify(repository, never()).add(any());
    }

    @Test
    void shouldNotAddForIncompleteAppointment() {
        Review review = makeReview(0);
        when(repository.findByAppointmentId(1)).thenReturn(null);
        when(appointmentRepository.findById(1)).thenReturn(makeAppointment(1, "BOOKED"));

        Result<Review> result = service.add(review);

        assertFalse(result.isSuccess());
        assertEquals(ResultType.INVALID, result.getResultType());
        assertEquals(List.of("Only completed appointments can be reviewed"), result.getMessages());
        verify(repository, never()).add(any());
    }

    @Test
    void shouldNotAddWhenUserDoesNotMatchAppointment() {
        Review review = makeReview(0);
        when(repository.findByAppointmentId(1)).thenReturn(null);
        when(appointmentRepository.findById(1)).thenReturn(makeAppointment(999, "COMPLETED"));

        Result<Review> result = service.add(review);

        assertFalse(result.isSuccess());
        assertEquals(ResultType.CONFLICT, result.getResultType());
        assertEquals(List.of("User does not match appointment"), result.getMessages());
        verify(repository, never()).add(any());
    }

    // =========================
    // HELPERS
    // =========================

    private void assertAddInvalid(Review review, String expectedMessage) {
        Result<Review> result = service.add(review);

        assertFalse(result.isSuccess());
        assertEquals(ResultType.INVALID, result.getResultType());
        assertEquals(List.of(expectedMessage), result.getMessages());
        verifyNoInteractions(repository, appointmentRepository);
    }

    private Review makeReview(int reviewId) {
        Review review = new Review();
        review.setReviewId(reviewId);
        review.setBarberId(1);
        review.setRating(3);
        review.setAppointmentId(1);
        review.setUserId(1);
        review.setReviewText("Cool");
        return review;
    }

    private Appointment makeAppointment(int userId, String status) {
        Appointment appointment = new Appointment();
        appointment.setAppointmentId(1);
        appointment.setUserId(userId);
        appointment.setBarberId(1);
        appointment.setStatus(status);
        return appointment;
    }
}
