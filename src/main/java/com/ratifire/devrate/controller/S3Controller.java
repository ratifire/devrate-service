package com.ratifire.devrate.controller;

import com.ratifire.devrate.service.S3Service;
import java.net.URL;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * AWS S3 controller.
 */
@RestController
@RequiredArgsConstructor
public class S3Controller {

  private final S3Service s3Service;

  /**
   * Return s3 url.
   *
   * @param fileName filename
   * @return s3 url
   */
  @GetMapping("/s3/presign")
  public Map<String, String> getPresignedUrl(@RequestParam String fileName) {
    String key = "recordings/" + fileName;
    URL url = s3Service.generatePresignedUploadUrl(key);
    return Map.of(
        "url", url.toString(),
        "key", key
    );
  }
}
