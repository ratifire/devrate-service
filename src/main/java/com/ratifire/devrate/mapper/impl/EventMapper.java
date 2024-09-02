package com.ratifire.devrate.mapper.impl;

import com.ratifire.devrate.dto.EventDto;
import com.ratifire.devrate.dto.EventDto.ParticipantDto;
import com.ratifire.devrate.entity.Event;
import com.ratifire.devrate.entity.User;
import com.ratifire.devrate.enums.InterviewRequestRole;
import com.ratifire.devrate.exception.UserNotFoundException;
import com.ratifire.devrate.mapper.DataMapper;
import com.ratifire.devrate.repository.UserRepository;
import java.util.List;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * Interface for mapping Event Dto to Event entities.
 */
@Mapper(componentModel = "spring")
public abstract class EventMapper implements DataMapper<EventDto, Event> {

  private UserRepository userRepository;

  @Autowired
  public void setUserRepository(UserRepository userRepository) {
    this.userRepository = userRepository;
  }

  @Mapping(source = "hostId", target = "host", qualifiedByName = "mapHostParticipant")
  @Mapping(source = "participantIds", target = "participantDtos",
      qualifiedByName = "mapParticipants")
  @Mapping(source = "roomLink", target = "link")
  public abstract EventDto toDto(Event event);

  /**
   * Maps the host's user ID to a {@link ParticipantDto} object with the role of
   * {@link InterviewRequestRole#INTERVIEWER}.
   *
   * @param userId      the ID of the user to be converted to a {@link ParticipantDto}
   * @return a {@link ParticipantDto} object containing the host's details INTERVIEWER
   */
  @Named("mapHostParticipant")
  public ParticipantDto mapHostParticipant(long userId) {
    return mapParticipant(userId, InterviewRequestRole.INTERVIEWER);
  }

  /**
   * Maps a list of participant IDs to a list of {@link ParticipantDto} objects with the role of
   * {@link InterviewRequestRole#CANDIDATE}.
   *
   * @param participantIds the list of participant IDs to be converted
   * @return a list of {@link ParticipantDto} objects
   */
  @Named("mapParticipants")
  public List<ParticipantDto> mapParticipants(List<Long> participantIds) {
    return participantIds.stream()
        .map(participantId -> mapParticipant(participantId, InterviewRequestRole.CANDIDATE))
        .toList();
  }

  /**
   * Maps a user ID to a {@link ParticipantDto} object.
   *
   * @param userId      the ID of the user to be converted to a {@link ParticipantDto}
   * @param role        the role of the participant in the event
   * @return a {@link ParticipantDto} object containing the user's details and role
   */
  public ParticipantDto mapParticipant(long userId, InterviewRequestRole role) {
    User user = userRepository.findById(userId)
        .orElseThrow(() -> new UserNotFoundException("The user not found with id " + userId));
    return ParticipantDto.builder()
        .name(user.getFirstName())
        .surname(user.getLastName())
        .status(user.getStatus())
        .role(role)
        .build();
  }
}
