package com.ratifire.devrate.repository.chat;

import com.ratifire.devrate.entity.chat.Topic;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;
import org.springframework.stereotype.Repository;

/**
 * Repository interface for accessing Topic entity.
 */
@Repository
@RepositoryRestResource(exported = false)
public interface TopicRepository extends JpaRepository<Topic, Long> {

  @Query("SELECT t FROM Topic t JOIN t.users u WHERE u.id = :userId")
  List<Topic> findTopicsByUserId(@Param("userId") Long userId);

  @Query("SELECT CASE WHEN COUNT(t) > 0 THEN true ELSE false END "
      + "FROM Topic t JOIN t.users u "
      + "WHERE t.topicName = :topicName AND u.id = :userId")
  boolean existsByTopicNameAndUserId(@Param("topicName") Long topicName,
      @Param("userId") Long userId);

  boolean existsTopicByTopicName(Long topicName);

  Optional<Topic> findTopicByTopicName(Long topicName);
}
