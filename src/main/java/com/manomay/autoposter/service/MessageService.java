package com.manomay.autoposter.service;

import com.manomay.autoposter.model.Message;
import com.manomay.autoposter.repository.MessageRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;



@Service
public class MessageService {

    private final MessageRepository messageRepository;

    public MessageService(MessageRepository messageRepository) {
        this.messageRepository = messageRepository;
    }
    
    public Message saveMessage(String sender, String content) {
        Message message = new Message();
        message.setSender(sender);
        message.setContent(content);
        message.setScheduledTime(LocalDateTime.now());
        return messageRepository.save(message);
    }

    public List<Message> getAllMessages() {
        return messageRepository.findAll();
    }

    public List<Message> getPendingMessages() {
        return messageRepository.findByStatus("PENDING");
    }

    public void markAsPosted(Message message) {
        message.setStatus("POSTED");
        messageRepository.save(message);
    }

}
