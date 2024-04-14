package com.ratifire.devrate.util.avatar;

import com.ratifire.devrate.exception.MissingFileDataException;
import com.ratifire.devrate.exception.UnsupportedFileTypeException;
import java.util.UUID;
import lombok.SneakyThrows;
import org.apache.tomcat.util.http.fileupload.impl.FileSizeLimitExceededException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

/**
 * Utility component providing functionalities related to avatar file handling. Includes generating
 * a unique file name for avatar images and validating avatar files based on size, content type, and
 * presence of file data.
 */
@Component
public class AvatarUtils {
  @Value("${avatar.maxSize}")
  private long maxSize;

  /**
   * Generates a unique file name for an avatar image based on the original file name. Appends a
   * universally unique identifier (UUID) to the original file extension.
   *
   * @param originalFilename the original file name of the avatar image.
   * @return a String representing the unique file name.
   */
  public String generateUniqueFileName(String originalFilename) {
    String extension = originalFilename.substring(originalFilename.lastIndexOf("."));
    String uniqueId = UUID.randomUUID().toString();
    return uniqueId + extension;
  }

  /**
   * Validates an avatar file against several criteria: presence of file data, adherence to a
   * maximum size, and matching an acceptable content type. Throws specific exceptions for each type
   * of validation failure.
   *
   * @param file the MultipartFile object representing the avatar image to be validated.
   * @throws MissingFileDataException if the file data is missing or the file name is empty.
   * @throws FileSizeLimitExceededException if the file exceeds the specified maximum size.
   * @throws UnsupportedFileTypeException if the file's content type is not supported.
   */
  @SneakyThrows
  public void fileValidation(MultipartFile file) {
    if (file != null
        && (file.getOriginalFilename() == null || file.getOriginalFilename().isEmpty())) {
      throw new MissingFileDataException("Missing file data");
    }
    if (file.getSize() > maxSize) {
      throw new FileSizeLimitExceededException("Incorrect file size", file.getSize(), maxSize);
    }
    String contentType = file.getContentType();
    if (contentType == null
        || (!contentType.equals("image/jpeg") && !contentType.equals("image/png"))) {
      throw new UnsupportedFileTypeException("Unsupported file type");
    }
  }
}
