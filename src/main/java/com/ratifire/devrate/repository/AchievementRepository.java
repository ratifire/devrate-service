package com.ratifire.devrate.repository;

import com.ratifire.devrate.entity.Achievement;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AchievementRepository extends JpaRepository<Achievement, Long> {

  List<Achievement> findAllByUserId(Long id);
}
