package com.myweb.chat.service;

import com.myweb.chat.dto.MessageDto;
import com.myweb.chat.model.ChatRoom;
import com.myweb.chat.model.Message;
import com.myweb.chat.model.User;
import com.myweb.chat.repo.ChatRoomRepository;
import com.myweb.chat.repo.MessageRepository;
import com.myweb.chat.repo.UserRepository;
import org.springframework.http.HttpStatus;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class MessageService {

	private final MessageRepository messageRepo;
	private final UserRepository userRepo;
	private final ChatRoomRepository roomRepo;
	private final SimpMessagingTemplate messagingTemplate;

	public MessageService(MessageRepository messageRepo, UserRepository userRepo, ChatRoomRepository roomRepo, SimpMessagingTemplate messagingTemplate) {
		this.messageRepo = messageRepo;
		this.userRepo = userRepo;
		this.roomRepo = roomRepo;
		this.messagingTemplate = messagingTemplate;
	}

	public List<MessageDto> listByRoom(Long roomId) {
		ChatRoom room = roomRepo.findById(roomId)
				.orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Không tìm thấy phòng"));
		return room.getMessages() == null ? List.of() : room.getMessages().stream()
				.sorted(java.util.Comparator.comparing(Message::getCreatedAt))
				.map(m -> new MessageDto(m.getId(), m.getContent(), m.getCreatedAt(), m.getSender().getId(), m.getSender().getUsername(), room.getId()))
				.collect(Collectors.toList());
	}

	public MessageDto sendMessage(String username, Long roomId, String content) {
		if (content == null || content.isBlank()) {
			throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Nội dung tin nhắn không được để trống");
		}
		User user = userRepo.findByUsername(username)
				.orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Không tìm thấy người dùng"));
		ChatRoom room = roomRepo.findById(roomId)
				.orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Không tìm thấy phòng"));

		Message msg = new Message();
		msg.setContent(content);
		msg.setSender(user);
		msg.setRoom(room);
		msg = messageRepo.save(msg);
		MessageDto dto = new MessageDto(msg.getId(), msg.getContent(), msg.getCreatedAt(), user.getId(), user.getUsername(), room.getId());
		try {
			System.out.println("[WS] Broadcast to /topic/rooms/" + room.getId() + " content=\"" + dto.getContent() + "\"");
			messagingTemplate.convertAndSend("/topic/rooms/" + room.getId(), dto);
			// broadcast preview for all sidebars
			com.myweb.chat.dto.RoomPreviewDto pv = new com.myweb.chat.dto.RoomPreviewDto(room.getId(), dto.getContent(), user.getUsername(), dto.getCreatedAt());
			messagingTemplate.convertAndSend("/topic/rooms/preview", pv);
		} catch (Exception ex) {
			System.out.println("[WS] Broadcast error: " + ex.getMessage());
		}
		return dto;
	}
}



