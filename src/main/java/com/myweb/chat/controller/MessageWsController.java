package com.myweb.chat.controller;

import com.myweb.chat.dto.MessageDto;
import com.myweb.chat.dto.RoomPreviewDto;
import com.myweb.chat.service.MessageService;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;

import java.security.Principal;

@Controller
public class MessageWsController {

	private final MessageService messageService;
	private final SimpMessagingTemplate messagingTemplate;

	public MessageWsController(MessageService messageService, SimpMessagingTemplate messagingTemplate) {
		this.messageService = messageService;
		this.messagingTemplate = messagingTemplate;
	}

	@MessageMapping("rooms/{roomId}/send")
	public void sendToRoom(@DestinationVariable Long roomId, @Payload String content, Principal principal) {
		MessageDto saved = messageService.sendMessage(principal.getName(), roomId, content);
		// Broadcast message to room
		messagingTemplate.convertAndSend("/topic/rooms/" + roomId, saved);
		// Broadcast preview to all clients to update sidebar, except the sender
		messagingTemplate.convertAndSend("/topic/rooms/preview", new RoomPreviewDto(roomId, content, principal.getName(), saved.getCreatedAt()));
	}
}



