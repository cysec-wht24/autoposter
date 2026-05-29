package com.manomay.autoposter.controller;

import com.manomay.autoposter.model.Message;
import com.manomay.autoposter.service.LlmService;
import com.manomay.autoposter.service.MessageService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/messages")
public class MessageController {

    private final MessageService messageService;
    private final LlmService llmService;

    public MessageController(MessageService messageService, LlmService llmService) {
        this.messageService = messageService;
        this.llmService = llmService;
    }

    @PostMapping
    public Message saveMessage(@RequestParam String sender, @RequestParam String content, @RequestParam String streamLink) {
        return messageService.saveMessage(sender, content, streamLink);
    }

    @GetMapping
    public List<Message> getAllMessages() {
        return messageService.getAllMessages();
    }

    @GetMapping("/test-llm")
    public String testLlm() {
        return llmService.generatePost("Going live now!", "https://youtube.com/live/test", "TELEGRAM");
    }
}