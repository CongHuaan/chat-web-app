package com.myweb.chat.dto;

import java.time.Instant;

public class MessageDto {
    private Long id;
    private String content;
    private Instant createdAt;
    private Long senderId;
    private String senderName;
    private Long roomId;

    public MessageDto() {}

    public MessageDto(Long id, String content, Instant createdAt, Long senderId, String senderName, Long roomId) {
        this.id = id;
        this.content = content;
        this.createdAt = createdAt;
        this.senderId = senderId;
        this.senderName = senderName;
        this.roomId = roomId;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getContent() { return content; }
    public void setContent(String content) { this.content = content; }
    public Instant getCreatedAt() { return createdAt; }
    public void setCreatedAt(Instant createdAt) { this.createdAt = createdAt; }
    public Long getSenderId() { return senderId; }
    public void setSenderId(Long senderId) { this.senderId = senderId; }
    public String getSenderName() { return senderName; }
    public void setSenderName(String senderName) { this.senderName = senderName; }
    public Long getRoomId() { return roomId; }
    public void setRoomId(Long roomId) { this.roomId = roomId; }
}



