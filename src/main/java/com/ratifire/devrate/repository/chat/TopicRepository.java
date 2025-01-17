package com.ratifire.devrate.repository.chat;

import com.ratifire.devrate.entity.Event;
import com.ratifire.devrate.entity.chat.Topic;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * Repository interface for accessing Event entity.
 */
@Repository
@RepositoryRestResource(exported = false)
public interface TopicRepository extends JpaRepository<Topic, Long> {

  Optional<Topic> findByTopicName(String topicName);
  Optional<Topic> findAllTopicsByUserId(long userId);
}
