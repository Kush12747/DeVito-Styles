package learn.DeVitoStyles.domain;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mail.MailSendException;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class EmailServiceTest {

    private static final String TO = "customer@example.com";
    private static final String BODY = "Your appointment is on Friday at 10am.";

    @Mock private JavaMailSender mailSender;

    @InjectMocks private EmailService emailService;

    @Test
    void sendAppointmentConfirmation_sendsMessageWithConfirmedSubject() {
        emailService.sendAppointmentConfirmation(TO, BODY);

        assertSent("DeVito Styles - Appointment Confirmed");
    }

    @Test
    void sendCancellationEmail_sendsMessageWithCancelledSubject() {
        emailService.sendCancellationEmail(TO, BODY);

        assertSent("DeVito Styles - Appointment Cancelled");
    }

    @Test
    void sendUpdatedAppointmentEmail_sendsMessageWithUpdatedSubject() {
        emailService.sendUpdatedAppointmentEmail(TO, BODY);

        assertSent("DeVito Styles - Appointment Updated");
    }

    @Test
    void sendAppointmentConfirmation_whenMailSenderFails_propagatesException() {
        MailSendException failure = new MailSendException("SMTP down");
        doThrow(failure).when(mailSender).send(any(SimpleMailMessage.class));

        MailSendException thrown = assertThrows(MailSendException.class,
                () -> emailService.sendAppointmentConfirmation(TO, BODY));

        assertSame(failure, thrown);
    }

    private void assertSent(String expectedSubject) {
        ArgumentCaptor<SimpleMailMessage> captor = ArgumentCaptor.forClass(SimpleMailMessage.class);
        verify(mailSender, times(1)).send(captor.capture());
        verifyNoMoreInteractions(mailSender);

        SimpleMailMessage message = captor.getValue();
        assertArrayEquals(new String[]{TO}, message.getTo());
        assertEquals(expectedSubject, message.getSubject());
        assertEquals(BODY, message.getText());
    }
}
