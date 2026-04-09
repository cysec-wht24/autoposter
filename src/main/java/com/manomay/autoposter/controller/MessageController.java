package com.manomay.autoposter.controller;

import com.manomay.autoposter.model.Message;
import com.manomay.autoposter.service.MessageService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/messages")
public class MessageController {

    private final MessageService messageService;

    public MessageController(MessageService messageService) {
        this.messageService = messageService;
    }

    @PostMapping
    public Message saveMessage(@RequestParam String sender, @RequestParam String content) {
        return messageService.saveMessage(sender, content);
    }

    @GetMapping
    public List<Message> getAllMessages() {
        return messageService.getAllMessages();
    }
}