package com.ratifire.devrate.repository.chat;

import com.ratifire.devrate.entity.chat.Message;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Repository interface for accessing Event entity.
 */
@Repository
@RepositoryRestResource(exported = false)
public interface MessageRepository extends JpaRepository<Message, Long> {

  List<Message> findAllMessagesByTopicName(String topicName);
}
