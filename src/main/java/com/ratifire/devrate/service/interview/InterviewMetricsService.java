package com.ratifire.devrate.service.interview;

import static com.ratifire.devrate.enums.InterviewRequestRole.CANDIDATE;
import static com.ratifire.devrate.enums.InterviewRequestRole.INTERVIEWER;

import com.ratifire.devrate.entity.Mastery;
import com.ratifire.devrate.entity.Skill;
import com.ratifire.devrate.entity.Specialization;
import com.ratifire.devrate.entity.User;
import com.ratifire.devrate.entity.interview.InterviewHistory;
import com.ratifire.devrate.enums.InterviewRequestRole;
import com.ratifire.devrate.enums.SkillType;
import com.ratifire.devrate.repository.MasteryRepository;
import com.ratifire.devrate.repository.SkillRepository;
import com.ratifire.devrate.repository.SpecializationRepository;
import com.ratifire.devrate.service.MasteryHistoryService;
import com.ratifire.devrate.service.UserService;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.ObjectUtils;
import org.springframework.stereotype.Service;

/**
 * Service for managing interview metrics.
 */
@Service
@RequiredArgsConstructor
public class InterviewMetricsService {

  private final UserService userService;
  private final MasteryHistoryService masteryHistoryService;
  private final SpecializationRepository specializationRepository;
  private final MasteryRepository masteryRepository;
  private final SkillRepository skillRepository;


  /**
   * Updates all relevant marks and counters after an interview is completed and feedback is
   * received. This includes updating the interview counters for both the interviewer and candidate,
   * recalculating skill marks, and updating mastery average marks for both parties.
   *
   * @param interviewHistoryMap a map containing the interview history for both the interviewer and
   *                            the candidate, keyed by their respective roles
   */
  public void updateAllMarksAndCountersAfterGettingFeedback(
      Map<InterviewRequestRole, InterviewHistory> interviewHistoryMap) {
    InterviewHistory interviewer = interviewHistoryMap.get(INTERVIEWER);
    InterviewHistory candidate = interviewHistoryMap.get(CANDIDATE);
    long interviewerMasteryId = interviewer.getMasteryId();
    long candidateMasteryId = candidate.getMasteryId();

    Map<Long, Mastery> masteryById = masteryRepository.findAllById(
            List.of(interviewerMasteryId, candidateMasteryId))
        .stream()
        .collect(Collectors.toMap(Mastery::getId, Function.identity()));

    Mastery interviewerMastery = masteryById.getOrDefault(interviewerMasteryId, null);
    Mastery candidateMastery = masteryById.getOrDefault(candidateMasteryId, null);

    incrementTotalInterviewCounters(interviewer.getUserId(), candidate.getUserId());
    incrementInterviewCountersBySpecialization(interviewerMastery, candidateMastery);

    Map<String, BigDecimal> interviewerEvaluatedSkills = interviewer.getSoftSkills();
    Map<String, BigDecimal> candidateEvaluatedSkills = new HashMap<>(candidate.getSoftSkills());
    candidateEvaluatedSkills.putAll(candidate.getHardSkills());

    updateSkillMarks(interviewerEvaluatedSkills, interviewerMasteryId);
    updateSkillMarks(candidateEvaluatedSkills, candidateMasteryId);

    updateMasteryAverageMark(interviewerMastery, INTERVIEWER);
    updateMasteryAverageMark(candidateMastery, CANDIDATE);
  }

  /**
   * Increments the interview counters for both the interviewer and candidate.
   *
   * @param interviewerId the ID of the interviewer whose conducted interviews count is to be
   *                      incremented
   * @param candidateId   the ID of the candidate whose completed interviews count is to be
   *                      incremented
   */
  public void incrementTotalInterviewCounters(long interviewerId, long candidateId) {
    Map<Long, User> userById = userService.findByIds(List.of(interviewerId, candidateId)).stream()
        .collect(Collectors.toMap(User::getId, Function.identity()));

    User interviewer = userById.get(interviewerId);
    User candidate = userById.get(candidateId);

    interviewer.setConductedInterviews(interviewer.getConductedInterviews() + 1);
    candidate.setCompletedInterviews(candidate.getCompletedInterviews() + 1);

    userService.saveAll(new ArrayList<>(userById.values()));
  }

  /**
   * Increments the interview counters for both the interviewer and candidate specializations.
   * Updates the conducted interviews count for the interviewer's specialization and the completed
   * interviews count for the candidate's specialization.
   *
   * @param interviewerMastery the mastery of the interviewer, used to retrieve the specialization
   * @param candidateMastery   the mastery of the candidate, used to retrieve the specialization
   */
  public void incrementInterviewCountersBySpecialization(
      Mastery interviewerMastery, Mastery candidateMastery) {
    if (ObjectUtils.isEmpty(interviewerMastery) || ObjectUtils.isEmpty(candidateMastery)) {
      throw new IllegalStateException("Not enough providing interview data");
    }

    Specialization interviewer = interviewerMastery.getSpecialization();
    Specialization candidate = candidateMastery.getSpecialization();

    interviewer.setConductedInterviews(interviewer.getConductedInterviews() + 1);
    candidate.setCompletedInterviews(candidate.getCompletedInterviews() + 1);

    specializationRepository.saveAll(List.of(interviewer, candidate));
  }

  /**
   * Updates the average mark for a given mastery based on the specified skill type.
   *
   * @param mastery   the mastery entity whose average mark is to be updated
   * @param skillType the type of skill (soft or hard) to filter and update the average mark
   */
  public void updateMasteryAverageMark(Mastery mastery, SkillType skillType) {
    List<Skill> skills = mastery.getSkills().stream()
        .filter(s -> s.getType() == skillType)
        .toList();

    BigDecimal updatedAverageMark = computeMasteryAverageMark(skills);

    if (skillType == SkillType.SOFT_SKILL) {
      mastery.setSoftSkillMark(updatedAverageMark);
    } else {
      mastery.setHardSkillMark(updatedAverageMark);
    }
  }

  /**
   * Updates the average mark for a given mastery based on the specified user role during an
   * interview.
   *
   * @param mastery the mastery entity whose average mark is to be updated
   * @param role    the role of the user during the interview
   */
  public void updateMasteryAverageMark(Mastery mastery, InterviewRequestRole role) {
    if (ObjectUtils.isEmpty(mastery)) {
      throw new IllegalStateException("Not enough providing interview data");
    }

    Map<SkillType, List<Skill>> skillsByType = mastery.getSkills().stream()
        .collect(Collectors.groupingBy(Skill::getType));

    BigDecimal updatedSoftSkillMark = computeMasteryAverageMark(
        skillsByType.getOrDefault(SkillType.SOFT_SKILL, List.of()));
    mastery.setSoftSkillMark(updatedSoftSkillMark);

    if (role == CANDIDATE) {
      BigDecimal updatedHardSkillMark = computeMasteryAverageMark(
          skillsByType.getOrDefault(SkillType.HARD_SKILL, List.of()));
      mastery.setHardSkillMark(updatedHardSkillMark);
    }

    masteryRepository.save(mastery);
    masteryHistoryService.saveHistory(mastery);
  }

  /**
   * Computes the average mark for a list of skills.
   *
   * @param skills the list of Skill objects whose average marks are to be computed
   * @return the computed average mark as a BigDecimal
   */
  private BigDecimal computeMasteryAverageMark(List<Skill> skills) {
    BigDecimal totalSkillMarks = skills.stream()
        .map(Skill::getAverageMark)
        .reduce(BigDecimal.ZERO, BigDecimal::add);

    if (skills.isEmpty()) {
      return BigDecimal.ZERO;
    }

    return totalSkillMarks.divide(BigDecimal.valueOf(skills.size()), 2, RoundingMode.HALF_UP);
  }

  /**
   * Updates the average marks and growth status of skills based on the provided map of skill IDs
   * and their new marks.
   *
   * @param skillsMap a map where the key is the skill ID and the value is the new mark to be
   *                  considered for updating the skill's average mark
   */
  private void updateSkillMarks(Map<String, BigDecimal> skillsMap, long masteryId) {
    List<String> skillNames = new ArrayList<>(skillsMap.keySet());
    List<Skill> skills = skillRepository.findByMasteryIdAndNameIn(masteryId, skillNames);

    if (skills.size() != skillNames.size()) {
      throw new IllegalArgumentException("Not all skills were found for masteryId: " + masteryId);
    }

    skills.forEach(skill -> {
      BigDecimal newMark = skillsMap.get(skill.getName());
      BigDecimal previousAverageMark = skill.getAverageMark();
      long counter = skill.getCounter();

      BigDecimal updatedAverageMark = counter > 0
          ? computeSkillAverageMark(counter, previousAverageMark, newMark)
          : newMark;

      updateSkillGrowthStatus(skill, updatedAverageMark, counter + 1);
      skillRepository.save(skill);
    });
  }

  /**
   * Computes the new average mark for the skill.
   *
   * @param counter the current counter value for the skill
   * @param oldMark the current average mark for the skill
   * @param newMark the new mark to be added to the average
   * @return the new calculated average mark
   */
  private BigDecimal computeSkillAverageMark(long counter, BigDecimal oldMark,
      BigDecimal newMark) {
    BigDecimal oldNumberOfMarks = oldMark.multiply(BigDecimal.valueOf(counter));
    BigDecimal newNumberOfMarks = oldNumberOfMarks.add(newMark);
    return newNumberOfMarks.divide(BigDecimal.valueOf(counter + 1), 2,
        RoundingMode.HALF_UP);
  }

  /**
   * Sets the mark, counter, and grows status for the skill.
   *
   * @param skill   the skill to be updated and saved
   * @param mark    the new mark to set for the skill
   * @param counter the new counter value to set for the skill
   */
  private void updateSkillGrowthStatus(Skill skill, BigDecimal mark, long counter) {
    int comparisonResult = skill.getAverageMark().compareTo(mark);
    skill.setGrows(comparisonResult <= 0);
    skill.setAverageMark(mark);
    skill.setCounter(counter);
  }
}