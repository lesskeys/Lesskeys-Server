package at.ac.uibk.keyless;

import com.google.auth.oauth2.GoogleCredentials;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.util.ResourceUtils;

import javax.annotation.PostConstruct;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;

/**
 * Created by Lukas Dötlinger.
 */
@Configuration
public class FirebaseConfig {

  private final Logger log = LoggerFactory.getLogger(this.getClass());

  @PostConstruct
  public void init() {
    try {
      FileInputStream serviceAccount = new FileInputStream(
        ResourceUtils.getFile("classpath:firebase-adminsdk.json"));

      FirebaseOptions options = new FirebaseOptions.Builder()
        .setCredentials(GoogleCredentials.fromStream(serviceAccount))
        .setDatabaseUrl("https://keyless-app.firebaseio.com")
        .build();

      FirebaseApp.initializeApp(options);
    } catch (IOException e) {
      log.warn("FirebaseApp could not be initialized. Check if the firebase-adminsdk.json exists in resources.");
    }
  }
}
