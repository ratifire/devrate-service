package com.ratifire.devrate.service.specialization;

import com.ratifire.devrate.dto.SkillDto;
import com.ratifire.devrate.entity.Skill;
import com.ratifire.devrate.exception.ResourceNotFoundException;
import com.ratifire.devrate.mapper.DataMapper;
import com.ratifire.devrate.repository.SkillRepository;
import java.math.BigDecimal;
import java.math.RoundingMode;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

/**
 * The service responsible for managing user`s Skill.
 */
@Service
@RequiredArgsConstructor
public class SkillService {

  private final SkillRepository skillRepository;
  private final DataMapper<SkillDto, Skill> skillMapper;

  /**
   * Retrieves Skill by ID.
   *
   * @param id the ID of the Skill
   * @return the Skill as a DTO
   * @throws ResourceNotFoundException if Skill is not found
   */
  public SkillDto findById(long id) {
    return skillMapper.toDto(getSkillById(id));
  }

  /**
   * Retrieves Skill by ID.
   *
   * @param id the ID of the Skill
   * @return the Skill as entity
   * @throws ResourceNotFoundException if Skill is not found
   */
  private Skill getSkillById(long id) {
    return skillRepository.findById(id)
        .orElseThrow(() -> new ResourceNotFoundException("Skill not found with id: " + id));
  }

  /**
   * Calculates averageMark and update skill information: averageMark, counter and grows.
   *
   * @param id - skill ID.
   */
  public SkillDto updateMark(long id, BigDecimal mark) {
    Skill skill = getSkillById(id);
    BigDecimal oldAverageMark = skill.getAverageMark();
    long counter = skill.getCounter();
    if (counter > 0) {
      BigDecimal newAverageMark = calculateAverageMark(counter, oldAverageMark, mark);
      return setMarkCounterGrowAndSave(skill, newAverageMark, counter + 1);
    }
    return setMarkCounterGrowAndSave(skill, mark, 1);
  }

  /**
   * Calculates the new average mark for the skill.
   *
   * @param counter the current counter value for the skill
   * @param oldMark the current average mark for the skill
   * @param newMark the new mark to be added to the average
   * @return the new calculated average mark
   */
  private BigDecimal calculateAverageMark(long counter, BigDecimal oldMark, BigDecimal newMark) {
    BigDecimal oldNumberOfMarks = oldMark.multiply(BigDecimal.valueOf(counter));
    BigDecimal newNumberOfMarks = oldNumberOfMarks.add(newMark);
    return newNumberOfMarks.divide(BigDecimal.valueOf(counter + 1), 2,
        RoundingMode.HALF_UP);
  }

  /**
   * Sets the mark, counter, and grows status for the skill and saves it.
   *
   * @param skill   the skill to be updated and saved
   * @param mark    the new mark to set for the skill
   * @param counter the new counter value to set for the skill
   * @return the updated skill information as a DTO
   */
  private SkillDto setMarkCounterGrowAndSave(Skill skill, BigDecimal mark, long counter) {
    int comparisonResult = skill.getAverageMark().compareTo(mark);
    skill.setGrows(comparisonResult >= 0);
    skill.setAverageMark(mark);
    skill.setCounter(counter);
    return skillMapper.toDto(skillRepository.save(skill));
  }

  /**
   * Deletes skill by ID.
   *
   * @param id the ID of the skill whose is to be deleted
   */
  public void delete(long id) {
    skillRepository.deleteById(id);
  }
}
