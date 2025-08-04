package com.ratifire.devrate.service;

import java.net.URL;
import java.time.Duration;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;
import software.amazon.awssdk.services.s3.presigner.S3Presigner;
import software.amazon.awssdk.services.s3.presigner.model.PutObjectPresignRequest;


/**
 * AWS S3 service.
 */
@Service
@RequiredArgsConstructor
public class S3Service {

  private final String bucketName = "devrate-recordings";

  /**
   * Return s3 url.
   *
   * @param key filename
   * @return s3 url
   */
  public URL generatePresignedUploadUrl(String key) {
    try (S3Presigner presigner = S3Presigner.create()) {
      PutObjectRequest objectRequest = PutObjectRequest.builder().bucket(bucketName).key(key)
          .contentType("video/webm") // або mp4
          .build();

      PutObjectPresignRequest presignRequest = PutObjectPresignRequest.builder()
          .signatureDuration(Duration.ofMinutes(10)) // 10 хв дійсний
          .putObjectRequest(objectRequest).build();

      return presigner.presignPutObject(presignRequest).url();
    }
  }
}
