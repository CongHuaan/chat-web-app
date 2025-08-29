package com.myweb.chat.dto;

import java.time.Instant;

public class RoomPreviewDto {
    private Long roomId;
    private String content;
    private String senderName;
    private Instant createdAt;

    public RoomPreviewDto() {}

    public RoomPreviewDto(Long roomId, String content, String senderName, Instant createdAt) {
        this.roomId = roomId;
        this.content = content;
        this.senderName = senderName;
        this.createdAt = createdAt;
    }

    public Long getRoomId() { return roomId; }
    public void setRoomId(Long roomId) { this.roomId = roomId; }
    public String getContent() { return content; }
    public void setContent(String content) { this.content = content; }
    public String getSenderName() { return senderName; }
    public void setSenderName(String senderName) { this.senderName = senderName; }
    public Instant getCreatedAt() { return createdAt; }
    public void setCreatedAt(Instant createdAt) { this.createdAt = createdAt; }
}


