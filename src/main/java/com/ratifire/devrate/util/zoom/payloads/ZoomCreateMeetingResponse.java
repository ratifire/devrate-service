package com.ratifire.devrate.util.zoom.payloads;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.List;
import lombok.Data;

/**
 * Response object for creating a meeting in Zoom.
 */
@Data
public class ZoomCreateMeetingResponse {
  public String assistantId;
  public String hostEmail;
  public long id;
  public String registrationUrl;
  public String agenda;
  public String createdAt;
  public int duration;
  public String encryptedPassword;
  public String pstnPassword;
  public String h323Password;
  @JsonProperty("join_url")
  public String joinUrl;
  public String chatJoinUrl;
  public List<Occurrence> occurrences;
  public String password;
  public String pmi;
  public boolean preSchedule;
  public Recurrence recurrence;
  public Settings settings;
  public String startTime;
  public String startUrl;
  public String timezone;
  public String topic;
  public List<TrackingField> trackingFields;
  public int type;
  public String dynamicHostKey;

  /** Represents an occurrence of the meeting. */
  @Data
  public static class Occurrence {
    public int duration;
    public String occurrenceId;
    public String startTime;
    public String status;
  }

  /** Represents the recurrence details for the meeting. */
  @Data
  public static class Recurrence {
    public String endDateTime;
    public int endTimes;
    public int monthlyDay;
    public int monthlyWeek;
    public int monthlyWeekDay;
    public int repeatInterval;
    public int type;
    public String weeklyDays;
  }

  /** Represents the settings configuration for the meeting. */
  @Data
  public static class Settings {
    public boolean allowMultipleDevices;
    public String alternativeHosts;
    public boolean alternativeHostsEmailNotification;
    public boolean alternativeHostUpdatePolls;
    public int approvalType;
    public ApprovedOrDeniedCountriesOrRegions approvedOrDeniedCountriesOrRegions;
    public String audio;
    public String audioConferenceInfo;
    public String authenticationDomains;
    public List<AuthenticationException> authenticationException;
    public String authenticationName;
    public String authenticationOption;
    public String autoRecording;
    public BreakoutRoom breakoutRoom;
    public int calendarType;
    public boolean closeRegistration;
    public String contactEmail;
    public String contactName;
    public List<CustomKey> customKeys;
    public boolean emailNotification;
    public String encryptionType;
    public boolean focusMode;
    public List<String> globalDialInCountries;
    public List<GlobalDialInNumber> globalDialInNumbers;
    public boolean hostVideo;
    public int jbhTime;
    public boolean joinBeforeHost;
    public LanguageInterpretation languageInterpretation;
    public SignLanguageInterpretation signLanguageInterpretation;
    public boolean meetingAuthentication;
    public boolean muteUponEntry;
    public boolean participantVideo;
    public boolean privateMeeting;
    public boolean registrantsConfirmationEmail;
    public boolean registrantsEmailNotification;
    public int registrationType;
    public boolean showShareButton;
    public boolean usePmi;
    public boolean waitingRoom;
    public boolean watermark;
    public boolean hostSaveVideoOrder;
    public boolean internalMeeting;
    public List<MeetingInvitee> meetingInvitees;
    public ContinuousMeetingChat continuousMeetingChat;
    public boolean participantFocusedMeeting;
    public boolean pushChangeToCalendar;
    public List<Resource> resources;
    public boolean autoStartMeetingSummary;
    public boolean autoStartAiCompanionQuestions;

    /** Represents a ApprovedOrDeniedCountriesOrRegions field associated with the meeting. */
    @Data
    public static class ApprovedOrDeniedCountriesOrRegions {
      public List<String> approvedList;
      public List<String> deniedList;
      public boolean enable;
      public String method;
    }

    /** Represents a AuthenticationException field associated with the meeting. */
    @Data
    public static class AuthenticationException {
      public String email;
      public String name;
      public String joinUrl;
    }

    /** Represents a BreakoutRoom field associated with the meeting. */
    @Data
    public static class BreakoutRoom {
      public boolean enable;
      public List<Room> rooms;

      /** Represents a Room field associated with the meeting. */
      @Data
      public static class Room {
        public String name;
        public List<String> participants;
      }
    }

    /** Represents a CustomKey field associated with the meeting. */
    @Data
    public static class CustomKey {
      public String key;
      public String value;
    }

    /** Represents a GlobalDialInNumber field associated with the meeting. */
    @Data
    public static class GlobalDialInNumber {
      public String city;
      public String country;
      public String countryName;
      public String number;
      public String type;
    }

    /** Represents a LanguageInterpretation field associated with the meeting. */
    @Data
    public static class LanguageInterpretation {
      public boolean enable;
      public List<Interpreter> interpreters;

      /** Represents a Interpreter field associated with the meeting. */
      @Data
      public static class Interpreter {
        public String email;
        public String languages;
      }
    }

    /** Represents a SignLanguageInterpretation field associated with the meeting. */
    @Data
    public static class SignLanguageInterpretation {
      public boolean enable;
      public List<SignInterpreter> interpreters;

      /** Represents a SignInterpreter field associated with the meeting. */
      @Data
      public static class SignInterpreter {
        public String email;
        public String signLanguage;
      }
    }

    /** Represents a MeetingInvitee field associated with the meeting. */
    @Data
    public static class MeetingInvitee {
      public String email;
    }

    /** Represents a ContinuousMeetingChat field associated with the meeting. */
    @Data
    public static class ContinuousMeetingChat {
      public boolean enable;
      public boolean autoAddInvitedExternalUsers;
      public String channelId;
    }

    /** Represents a Resource field associated with the meeting. */
    @Data
    public static class Resource {
      public String resourceType;
      public String resourceId;
      public String permissionLevel;
    }
  }

  /** Represents a tracking field associated with the meeting. */
  @Data
  public static class TrackingField {
    public String field;
    public String value;
    public boolean visible;
  }
}
