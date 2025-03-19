package com.ratifire.devrate.security.configuration.properties;

import com.ratifire.devrate.util.JsonConverter;
import jakarta.annotation.PostConstruct;
import java.util.List;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldNameConstants;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

/**
 * Configuration properties for managing whitelisted paths.
 */
@Component
@FieldNameConstants
@Getter
@Setter
public class WhitelistPathProperties {

  @Value("${security.whitelist.file-path}")
  private String whitelistFilePath;
  private List<String> whitelistedPaths;

  @PostConstruct
  public void init() {
    this.whitelistedPaths = JsonConverter.loadStringFromJson(whitelistFilePath);
  }
}