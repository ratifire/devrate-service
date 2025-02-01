package com.ratifire.devrate.repository.chat;

import com.ratifire.devrate.entity.chat.Message;
import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;
import org.springframework.stereotype.Repository;

/**
 * Repository for accessing {@link Message} entities.
 */
@Repository
@RepositoryRestResource(exported = false)
public interface MessageRepository extends JpaRepository<Message, Long> {

  // TODO: or we can implement the same logic by using JPA Criteria API (EntityManager)
  @Query("SELECT m FROM Message m "
      + "WHERE (m.sender.id = :userId OR m.receiver.id = :userId) "
      + " AND m.sentAt = ("
      + "     SELECT MAX(m2.sentAt) "
      + "     FROM Message m2 "
      + "     WHERE (m2.sender.id = :userId OR m2.receiver.id = :userId) "
      + "       AND (CASE WHEN m2.sender.id = :userId THEN m2.receiver.id ELSE m2.sender.id END) = "
      + "           (CASE WHEN m.sender.id = :userId THEN m.receiver.id ELSE m.sender.id END)"
      + "  ) "
      + "ORDER BY m.sentAt DESC")
  List<Message> findLastMessagesByUserId(@Param("userId") Long userId);

  // TODO: or we can use @Query as above
  Page<Message> findBySenderIdAndReceiverIdOrSenderIdAndReceiverIdOrderBySentAtAsc(
      Long senderId, Long receiverId, Long senderId2, Long receiverId2, Pageable pageable);
}
