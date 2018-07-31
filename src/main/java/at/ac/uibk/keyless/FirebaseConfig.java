package at.ac.uibk.keyless;

import com.google.auth.oauth2.GoogleCredentials;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
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

  @PostConstruct
  public void init() throws IOException {
    FileInputStream serviceAccount = new FileInputStream(
      ResourceUtils.getFile("classpath:firebase-adminsdk.json"));

    FirebaseOptions options = new FirebaseOptions.Builder()
      .setCredentials(GoogleCredentials.fromStream(serviceAccount))
      .setDatabaseUrl("https://keyless-app.firebaseio.com")
      .build();

    FirebaseApp.initializeApp(options);
  }
}
