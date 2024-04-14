package com.ratifire.devrate.util.avatar;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.ratifire.devrate.exception.MissingFileDataException;
import com.ratifire.devrate.exception.UnsupportedFileTypeException;
import java.util.regex.Pattern;
import org.apache.tomcat.util.http.fileupload.impl.FileSizeLimitExceededException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.web.multipart.MultipartFile;

/**
 * Unit tests for the AvatarUtils class.
 */
class AvatarUtilsTest {

  private AvatarUtils avatarUtils;

  @BeforeEach
  void setUp() {
    avatarUtils = new AvatarUtils();
    ReflectionTestUtils.setField(avatarUtils, "maxSize", 1024 * 1024 * 5);
  }

  @Test
  void generateUniqueFileNameShouldContainUuid() {
    String originalFilename = "test.jpg";
    String generatedFilename = avatarUtils.generateUniqueFileName(originalFilename);

    assertTrue(Pattern.matches("[0-9a-fA-F-]+\\.jpg", generatedFilename));
  }

  @Test
  void fileValidationShouldPassForValidFile() {
    MultipartFile validFile =
        new MockMultipartFile("avatar", "test.jpg", "image/jpeg", new byte[1024]);

    assertDoesNotThrow(() -> avatarUtils.fileValidation(validFile));
  }

  @Test
  void fileValidationShouldThrowForMissingFileData() {
    MultipartFile fileWithNoName = new MockMultipartFile("avatar", "", null, new byte[0]);

    assertThrows(MissingFileDataException.class, () -> avatarUtils.fileValidation(fileWithNoName));
  }

  @Test
  void fileValidationShouldThrowForIncorrectFileSize() {
    MultipartFile largeFile =
        new MockMultipartFile("avatar", "test.jpg", "image/jpeg", new byte[1024 * 1024 * 6]);

    assertThrows(FileSizeLimitExceededException.class, () -> avatarUtils.fileValidation(largeFile));
  }

  @Test
  void fileValidationShouldThrowForUnsupportedFileType() {
    MultipartFile unsupportedFile =
        new MockMultipartFile("avatar", "test.txt", "text/plain", new byte[1024]);

    assertThrows(
        UnsupportedFileTypeException.class, () -> avatarUtils.fileValidation(unsupportedFile));
  }
}
