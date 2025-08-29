package com.myweb.chat.controller;

import com.myweb.chat.dto.ChatRoomDto;
import com.myweb.chat.service.ChatRoomService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;

@RestController
@RequestMapping("/api/rooms")
public class ChatRoomController {

	private final ChatRoomService roomService;

	public ChatRoomController(ChatRoomService roomService) {
		this.roomService = roomService;
	}

	@Operation(summary = "Danh sách phòng")
	@ApiResponses({
			@ApiResponse(responseCode = "200", description = "Thành công"),
			@ApiResponse(responseCode = "401", description = "Chưa xác thực")
	})
	@GetMapping
	public List<ChatRoomDto> listRooms() {
		return roomService.listRooms();
	}

	@Operation(summary = "Tạo phòng mới")
	@ApiResponses({
			@ApiResponse(responseCode = "200", description = "Thành công"),
			@ApiResponse(responseCode = "400", description = "Tên phòng đã tồn tại/không hợp lệ"),
			@ApiResponse(responseCode = "401", description = "Chưa xác thực")
	})
	@PostMapping
	public ChatRoomDto createRoom(@RequestParam String name, @RequestParam(required = false) String description) {
		return roomService.createRoom(name, description);
	}

	@Operation(summary = "Tham gia phòng")
	@ApiResponses({
			@ApiResponse(responseCode = "200", description = "Thành công"),
			@ApiResponse(responseCode = "404", description = "Không tìm thấy phòng/người dùng"),
			@ApiResponse(responseCode = "401", description = "Chưa xác thực")
	})
	@PostMapping("/{roomId}/join")
	public String join(@PathVariable Long roomId, Principal principal) {
		roomService.joinRoom(principal.getName(), roomId);
		return "Đã tham gia phòng";
	}

	@Operation(summary = "Rời phòng")
	@ApiResponses({
			@ApiResponse(responseCode = "200", description = "Thành công"),
			@ApiResponse(responseCode = "404", description = "Không tìm thấy phòng/người dùng"),
			@ApiResponse(responseCode = "401", description = "Chưa xác thực")
	})
	@PostMapping("/{roomId}/leave")
	public String leave(@PathVariable Long roomId, Principal principal) {
		roomService.leaveRoom(principal.getName(), roomId);
		return "Đã rời phòng";
	}
}



