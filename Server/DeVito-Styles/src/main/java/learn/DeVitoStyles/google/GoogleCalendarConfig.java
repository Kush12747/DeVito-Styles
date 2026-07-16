package learn.DeVitoStyles.google;

import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.json.gson.GsonFactory;
import com.google.api.services.calendar.Calendar;
import com.google.auth.http.HttpCredentialsAdapter;
import com.google.auth.oauth2.GoogleCredentials;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.List;

@Configuration
public class GoogleCalendarConfig {

    @Bean
    public Calendar calendar() throws GeneralSecurityException, IOException {

        GoogleCredentials credentials =
                GoogleCredentials
                        .fromStream(
                                new ClassPathResource("google-credentials.json")
                                        .getInputStream())
                        .createScoped(List.of(
                                "https://www.googleapis.com/auth/calendar"
                        ));

        return new Calendar.Builder(
                GoogleNetHttpTransport.newTrustedTransport(),
                GsonFactory.getDefaultInstance(),
                new HttpCredentialsAdapter(credentials))
                .setApplicationName("DeVito Styles")
                .build();
    }
}