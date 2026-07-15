package learn.DeVitoStyles.google;

import com.google.api.client.util.DateTime;
import com.google.api.services.calendar.Calendar;
import com.google.api.services.calendar.model.Event;
import com.google.api.services.calendar.model.EventDateTime;
import learn.DeVitoStyles.dto.GoogleCalendarResponse;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.ZoneId;

@Service
public class GoogleCalendarService {

    private final Calendar calendar;
    private static final String CALENDAR_ID = "devitostyles80@gmail.com";
    private static final ZoneId CHICAGO_ZONE = ZoneId.of("America/Chicago");

    public GoogleCalendarService(Calendar calendar) {
        this.calendar = calendar;
    }


    public GoogleCalendarResponse createAppointment(String customerName,
                                                    String customerEmail,
                                                    String barberName,
                                                    String serviceName,
                                                    LocalDateTime appointmentTime,
                                                    int durationMinutes) throws Exception {

        if (appointmentTime == null) {
            throw new IllegalArgumentException("Appointment time cannot be null");
        }

        Event event = new Event();

        event.setSummary(serviceName + " - " + customerName);

        event.setDescription(
                "Customer: " + customerName + "\n" +
                "Barber: " + barberName + "\n" +
                "Service: " + serviceName + "\n" +
                "Email: " + customerEmail
        );

        LocalDateTime endTime = appointmentTime.plusMinutes(durationMinutes);

        DateTime startDateTime =
                new DateTime(
                        appointmentTime
                                .atZone(CHICAGO_ZONE)
                                .toInstant()
                                .toEpochMilli()
                );


        DateTime endDateTime =
                new DateTime(
                        endTime
                                .atZone(CHICAGO_ZONE)
                                .toInstant()
                                .toEpochMilli()
                );


        EventDateTime start =
                new EventDateTime()
                        .setDateTime(startDateTime)
                        .setTimeZone("America/Chicago");


        EventDateTime end =
                new EventDateTime()
                        .setDateTime(endDateTime)
                        .setTimeZone("America/Chicago");


        event.setStart(start);
        event.setEnd(end);



        Event createdEvent =
                calendar.events()
                        .insert(CALENDAR_ID, event)
                        .execute();

        GoogleCalendarResponse response = new GoogleCalendarResponse();
        response.setEventId(createdEvent.getId());
        response.setEventUrl(createdEvent.getHtmlLink());

        return response;
    }

    public void deleteAppointment(String googleEventId) throws IOException {

        if (googleEventId == null || googleEventId.isBlank()) {
            return;
        }

        calendar.events().delete(CALENDAR_ID, googleEventId).execute();
    }
}