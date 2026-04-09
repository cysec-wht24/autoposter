package com.manomay.autoposter.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "messages")
public class Message {
    
    @Column(nullable = false)
    private String status = "PENDING";

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String sender;
    private String content;
    private LocalDateTime scheduledTime;

    public Message() {}

    public Long getId() { return id; }
    public String getSender() { return sender; }
    public String getContent() { return content; }
    public LocalDateTime getScheduledTime() { return scheduledTime; }
    public String getStatus() { return status; }
    
    public void setStatus(String status) { this.status = status; }
    public void setSender(String sender) { this.sender = sender; }
    public void setContent(String content) { this.content = content; }
    public void setScheduledTime(LocalDateTime scheduledTime) { this.scheduledTime = scheduledTime; }
}
