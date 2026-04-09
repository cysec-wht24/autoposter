package com.manomay.autoposter.repository;

import java.util.List;
import com.manomay.autoposter.model.Message;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface MessageRepository extends JpaRepository<Message, Long> {
    List<Message> findByStatus(String status);
}