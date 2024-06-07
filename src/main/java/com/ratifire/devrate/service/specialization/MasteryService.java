package com.ratifire.devrate.service.specialization;

import com.ratifire.devrate.dto.MasteryDto;
import com.ratifire.devrate.entity.Mastery;
import com.ratifire.devrate.exception.ResourceNotFoundException;
import com.ratifire.devrate.mapper.DataMapper;
import com.ratifire.devrate.repository.MasteryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

/**
 * The service responsible for managing user`s Mastery.
 */
@Service
@RequiredArgsConstructor
public class MasteryService {

  private final MasteryRepository masteryRepository;
  private final DataMapper<MasteryDto, Mastery> masteryMapper;

  /**
   * Retrieves Mastery by ID.
   *
   * @param id the ID of the Mastery
   * @return the Mastery as a DTO
   * @throws ResourceNotFoundException if Mastery is not found
   */
  public MasteryDto findById(long id) {
    return masteryMapper.toDto(getMasteryById(id));
  }

  /**
   * Retrieves Mastery by ID.
   *
   * @param id the ID of the Mastery
   * @return the Mastery as entity
   * @throws ResourceNotFoundException if Mastery is not found
   */
  public Mastery getMasteryById(long id) {
    return masteryRepository.findById(id)
        .orElseThrow(() -> new ResourceNotFoundException("Mastery not found with id: " + id));
  }

  /**
   * Updates Mastery information: HardSkillMark and SoftSkillMark.
   *
   * @param masteryDto the updated Mastery as a DTO
   * @return the updated Mastery as a DTO
   */
  public MasteryDto update(MasteryDto masteryDto) {
    long id = masteryDto.getId();
    Mastery mastery = getMasteryById(id);

    masteryMapper.updateEntity(masteryDto, mastery);
    masteryRepository.save(mastery);

    return masteryMapper.toDto(mastery);
  }

}
