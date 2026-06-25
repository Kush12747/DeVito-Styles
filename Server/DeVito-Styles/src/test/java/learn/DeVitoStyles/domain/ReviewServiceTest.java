package learn.DeVitoStyles.domain;

import learn.DeVitoStyles.data.AppointmentRepository;
import learn.DeVitoStyles.data.ReviewRepository;
import learn.DeVitoStyles.models.Appointment;
import learn.DeVitoStyles.models.Review;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.MOCK)
class ReviewServiceTest {

    @Autowired
    ReviewService service;

    @MockBean
    ReviewRepository repository;

    @MockBean
    AppointmentRepository appointmentRepository;

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

    @Test
    void shouldFindReviewById() {
        Review review = makeReview(1);

        when(repository.findById(review.getReviewId())).thenReturn(review);

        Result<Review> result = service.findById(review.getReviewId());

        assertTrue(result.isSuccess());
        assertEquals(1, result.getpayload().getReviewId());
    }

    @Test
    void shouldNotFindReviewByInvalidId() {
        Result<Review> result = service.findById(0);

        assertFalse(result.isSuccess());
        assertEquals(ResultType.INVALID, result.getResultType());
    }

    @Test
    void shouldNotFindReviewById() {
        when(repository.findById(999)).thenReturn(null);

        Result<Review> result = service.findById(999);

        assertFalse(result.isSuccess());
        assertEquals(ResultType.NOT_FOUND, result.getResultType());
    }

    @Test
    void add() {
        Review review = makeReview(0);

        Appointment appointment = new Appointment();
        appointment.setAppointmentId(1);
        appointment.setUserId(1);
        appointment.setBarberId(1);
        appointment.setStatus("COMPLETED");

        when(repository.findByAppointmentId(1)).thenReturn(null);

        when(appointmentRepository.findById(1)).thenReturn(appointment);

        Review added = makeReview(5);

        when(repository.add(review)).thenReturn(added);

        Result<Review> result = service.add(review);

        assertTrue(result.isSuccess());
        assertEquals(5, result.getpayload().getReviewId());
    }

    @Test
    void shouldNotAddNullReview() {
        Result<Review> result = service.add(null);

        assertFalse(result.isSuccess());
        assertEquals(ResultType.INVALID, result.getResultType());
    }

    @Test
    void shouldNotAddWithoutUserId() {
        Review review = makeReview(0);
        review.setUserId(0);

        Result<Review> result = service.add(review);

        assertFalse(result.isSuccess());
        assertEquals(ResultType.INVALID, result.getResultType());
    }

    @Test
    void shouldNotAddWithoutAppointmentId() {
        Review review = makeReview(0);
        review.setAppointmentId(0);

        Result<Review> result = service.add(review);
        assertFalse(result.isSuccess());
        assertEquals(ResultType.INVALID, result.getResultType());
    }

    @Test
    void shouldNotAddWithoutBarberId() {
        Review review = makeReview(0);
        review.setBarberId(0);

        Result<Review> result = service.add(review);
        assertFalse(result.isSuccess());
        assertEquals(ResultType.INVALID, result.getResultType());
    }

    @Test
    void shouldNotAddWithInvaliRating() {
        Review review = makeReview(0);
        review.setRating(6);

        Result<Review> result = service.add(review);

        assertFalse(result.isSuccess());
        assertEquals(ResultType.INVALID, result.getResultType());
    }

    @Test
    void shouldNotAddDuplicateReview() {

        Review review = makeReview(0);

        when(repository.findByAppointmentId(1)).thenReturn(makeReview(99));

        Result<Review> result = service.add(review);

        assertFalse(result.isSuccess());
        assertEquals(ResultType.CONFLICT, result.getResultType());
    }

    @Test
    void shouldNotAddWhenAppointmentNotFound() {

        Review review = makeReview(0);

        when(repository.findByAppointmentId(1)).thenReturn(null);

        when(appointmentRepository.findById(1)).thenReturn(null);

        Result<Review> result = service.add(review);

        assertFalse(result.isSuccess());
        assertEquals(ResultType.NOT_FOUND, result.getResultType());
    }

    @Test
    void shouldNotAddForIncompleteAppointment() {

        Review review = makeReview(0);

        Appointment appointment = new Appointment();
        appointment.setAppointmentId(1);
        appointment.setUserId(1);
        appointment.setBarberId(1);
        appointment.setStatus("BOOKED");

        when(repository.findByAppointmentId(1)).thenReturn(null);

        when(appointmentRepository.findById(1)).thenReturn(appointment);

        Result<Review> result = service.add(review);

        assertFalse(result.isSuccess());
        assertEquals(ResultType.INVALID, result.getResultType());
    }

    @Test
    void shouldNotAddWhenUserDoesNotMatchAppointment() {

        Review review = makeReview(0);

        Appointment appointment = new Appointment();
        appointment.setAppointmentId(1);
        appointment.setUserId(999);
        appointment.setBarberId(1);
        appointment.setStatus("COMPLETED");

        when(repository.findByAppointmentId(1)).thenReturn(null);

        when(appointmentRepository.findById(1)).thenReturn(appointment);

        Result<Review> result = service.add(review);

        assertFalse(result.isSuccess());
        assertEquals(ResultType.CONFLICT, result.getResultType());
    }
}