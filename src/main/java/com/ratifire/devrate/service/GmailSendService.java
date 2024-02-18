package com.ratifire.devrate.service;

import static com.google.api.services.gmail.GmailScopes.GMAIL_SEND;
import static javax.mail.Message.RecipientType.TO;

import com.google.api.client.auth.oauth2.Credential;
import com.google.api.client.extensions.java6.auth.oauth2.AuthorizationCodeInstalledApp;
import com.google.api.client.extensions.jetty.auth.oauth2.LocalServerReceiver;
import com.google.api.client.googleapis.auth.oauth2.GoogleAuthorizationCodeFlow;
import com.google.api.client.googleapis.auth.oauth2.GoogleClientSecrets;
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.googleapis.json.GoogleJsonError;
import com.google.api.client.googleapis.json.GoogleJsonResponseException;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.gson.GsonFactory;
import com.google.api.client.util.store.FileDataStoreFactory;
import com.google.api.services.gmail.Gmail;
import com.google.api.services.gmail.model.Message;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.file.Paths;
import java.util.Properties;
import java.util.Set;
import javax.mail.Session;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import org.apache.commons.codec.binary.Base64;
import org.springframework.stereotype.Service;

/**
 * Service responsible for sending emails using the Gmail API.
 */
@Service
public class GmailSendService {

  private final Gmail service;
  private static final String CREDENTIALS_ID = "/clients_credentials_id.json";

  /**
   * Constructor for the GmailSendService class.
   * Initializes the Gmail service for sending messages.
   *
   * @throws Exception if an error occurs during service initialization.
   */
  public GmailSendService() throws Exception {
    NetHttpTransport httpTransport = GoogleNetHttpTransport.newTrustedTransport();
    GsonFactory jsonFactory = GsonFactory.getDefaultInstance();
    service = new Gmail.Builder(
        httpTransport, jsonFactory, getCredentials(httpTransport, jsonFactory))
            .setApplicationName("DR")
            .build();
  }

  private static Credential getCredentials(
      final NetHttpTransport httpTransport, GsonFactory jsonFactory)
          throws IOException {
    GoogleClientSecrets clientSecrets =
            GoogleClientSecrets.load(jsonFactory,
                new InputStreamReader(
                    GmailSendService.class.getResourceAsStream(CREDENTIALS_ID)));

    GoogleAuthorizationCodeFlow flow = new GoogleAuthorizationCodeFlow.Builder(
            httpTransport, jsonFactory, clientSecrets, Set.of(GMAIL_SEND))
            .setDataStoreFactory(new FileDataStoreFactory(Paths.get("tokens").toFile()))
            .setAccessType("offline")
            .build();

    LocalServerReceiver receiver = new LocalServerReceiver.Builder().setPort(8888).build();
    return new AuthorizationCodeInstalledApp(flow, receiver).authorize("user");
  }

  /**
   * Sends an email message using the Gmail API.
   *
   * @param subject The subject of the email message.
   * @param message The content of the email message.
   * @param mail    The email address of the recipient.
   * @throws IOException If an error occurs while sending the email.
   */
  public void sendMail(String subject, String message, String mail) throws Exception {
    Properties props = new Properties();
    Session session = Session.getDefaultInstance(props, null);
    MimeMessage email = new MimeMessage(session);
    email.setFrom(new InternetAddress(mail));
    email.addRecipient(TO, new InternetAddress(mail));
    email.setSubject(subject);
    email.setText(message);

    ByteArrayOutputStream buffer = new ByteArrayOutputStream();
    email.writeTo(buffer);
    byte[] rawMessageBytes = buffer.toByteArray();
    String encodedEmail = Base64.encodeBase64URLSafeString(rawMessageBytes);
    Message msg = new Message();
    msg.setRaw(encodedEmail);

    try {
      msg = service.users().messages().send("me", msg).execute();
      System.out.println("Message id: " + msg.getId());
      System.out.println(msg.toPrettyString());
    } catch (GoogleJsonResponseException e) {
      GoogleJsonError error = e.getDetails();
      if (error.getCode() == 403) {
        System.err.println("Unable to send message: " + e.getDetails());
      } else {
        throw e;
      }
    }
  }
}
