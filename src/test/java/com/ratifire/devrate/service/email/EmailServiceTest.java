package com.ratifire.devrate.service.email;

import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import jakarta.mail.internet.MimeMessage;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;

@ExtendWith(MockitoExtension.class)
class EmailServiceTest {

  @Mock
  private JavaMailSender mockMailSender;

  @Mock
  private MimeMessage mimeMessage;

  @InjectMocks
  private EmailService emailService;

  @Test
  void sendEmail_Successful() {
    SimpleMailMessage simpleMailMessage = new SimpleMailMessage();
    simpleMailMessage.setSubject("Test Subject");
    simpleMailMessage.setText("Test Content");
    simpleMailMessage.setTo("test@example.com");

    when(mockMailSender.createMimeMessage()).thenReturn(mimeMessage);

    emailService.sendEmail(simpleMailMessage, false);

    verify(mockMailSender, times(1)).send(mimeMessage);
  }
}