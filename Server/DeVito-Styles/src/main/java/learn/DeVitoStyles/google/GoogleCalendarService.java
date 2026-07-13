package learn.DeVitoStyles.google;

import com.google.api.client.util.DateTime;
import com.google.api.services.calendar.Calendar;
import com.google.api.services.calendar.model.Event;
import com.google.api.services.calendar.model.EventDateTime;
import org.springframework.stereotype.Service;

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


    public void createAppointment(String customerName, String serviceName) throws Exception {

        Event event = new Event();

        event.setSummary(serviceName);

        event.setDescription("Customer: " + customerName);

        // Appointment time in Chicago timezone
        LocalDateTime appointmentTime =
                LocalDateTime.of(
                        2026,
                        7,
                        20,
                        10,
                        0
                );


        LocalDateTime endTime =
                appointmentTime.plusHours(1);


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


        System.out.println(createdEvent);

    }
}