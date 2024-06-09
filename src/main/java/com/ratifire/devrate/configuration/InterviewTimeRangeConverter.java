package com.ratifire.devrate.configuration;

import com.ratifire.devrate.entity.InterviewTimeRange;
import jakarta.persistence.AttributeConverter;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.stream.Collectors;
import org.springframework.util.StringUtils;

public class InterviewTimeRangeConverter implements AttributeConverter<List<InterviewTimeRange>, String> {

  @Override
  public String convertToDatabaseColumn(List<InterviewTimeRange> attribute) {
    if (attribute == null || attribute.isEmpty()) {
      return null;
    }
    return attribute.stream()
        .map(range -> String.format("[%s,%s)", range.getStartTime(), range.getEndTime()))
        .collect(Collectors.joining(","));
  }

  @Override
  public List<InterviewTimeRange> convertToEntityAttribute(String dbData) {
    if (!StringUtils.hasText(dbData)) {
      return List.of();
    }
    return List.of(dbData.split(",")).stream()
        .map(range -> {
          String[] times = range.replace("[", "").replace(")", "").split(",");
          OffsetDateTime startTime = OffsetDateTime.parse(times[0]);
          OffsetDateTime endTime = OffsetDateTime.parse(times[1]);
          return new InterviewTimeRange(startTime, endTime);
        })
        .collect(Collectors.toList());
  }
}
