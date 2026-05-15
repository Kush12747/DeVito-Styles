package learn.DeVitoStyles.domain;

import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
public class EmailService {

    private final JavaMailSender mailSender;

    public EmailService(JavaMailSender mailSender) {
        this.mailSender = mailSender;
    }

    public void sendAppointmentConfirmation(String sendTo, String body) {
        SimpleMailMessage message = new SimpleMailMessage();

        message.setTo(sendTo);
        message.setSubject("DeVito Styles - Appointment Confirmed");
        message.setText(body);

        mailSender.send(message);
    }

    public void sendCancellationEmail(String sendTo, String body) {
        SimpleMailMessage message = new SimpleMailMessage();

        message.setTo(sendTo);
        message.setSubject("DeVito Styles - Appointment Cancelled");
        message.setText(body);

        mailSender.send(message);
    }

    public void sendUpdatedAppointmentEmail(String sendTo, String body) {
        SimpleMailMessage message = new SimpleMailMessage();

        message.setTo(sendTo);
        message.setSubject("DeVito Styles - Appointment Updated");
        message.setText(body);

        mailSender.send(message);
    }
}
