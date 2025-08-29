package com.myweb.chat.service;

import com.myweb.chat.dto.ChatRoomDto;
import com.myweb.chat.model.ChatRoom;
import com.myweb.chat.model.User;
import com.myweb.chat.repo.ChatRoomRepository;
import com.myweb.chat.repo.UserRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class ChatRoomService {

	private final ChatRoomRepository roomRepo;
	private final UserRepository userRepo;

	public ChatRoomService(ChatRoomRepository roomRepo, UserRepository userRepo) {
		this.roomRepo = roomRepo;
		this.userRepo = userRepo;
	}

	public List<ChatRoomDto> listRooms() {
		return roomRepo.findAll().stream()
				.map(r -> new ChatRoomDto(r.getId(), r.getName(), r.getDescription()))
				.collect(Collectors.toList());
	}

	public ChatRoomDto createRoom(String name, String description) {
		if (name == null || name.isBlank()) {
			throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Tên phòng không được để trống");
		}
		if (roomRepo.findAll().stream().anyMatch(r -> r.getName().equalsIgnoreCase(name))) {
			throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Tên phòng đã tồn tại");
		}
		ChatRoom room = new ChatRoom();
		room.setName(name);
		room.setDescription(description);
		room = roomRepo.save(room);
		return new ChatRoomDto(room.getId(), room.getName(), room.getDescription());
	}

	public void joinRoom(String username, Long roomId) {
		User user = userRepo.findByUsername(username)
				.orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Không tìm thấy người dùng"));
		ChatRoom room = roomRepo.findById(roomId)
				.orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Không tìm thấy phòng"));

		Set<ChatRoom> rooms = user.getRooms();
		if (rooms == null) {
			throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Dữ liệu phòng của người dùng không hợp lệ");
		}
		if (!rooms.contains(room)) {
			rooms.add(room);
			user.setRooms(rooms);
			userRepo.save(user);
		}
	}

	public void leaveRoom(String username, Long roomId) {
		User user = userRepo.findByUsername(username)
				.orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Không tìm thấy người dùng"));
		ChatRoom room = roomRepo.findById(roomId)
				.orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Không tìm thấy phòng"));

		Set<ChatRoom> rooms = user.getRooms();
		if (rooms != null && rooms.contains(room)) {
			rooms.remove(room);
			user.setRooms(rooms);
			userRepo.save(user);
		}
	}
}



