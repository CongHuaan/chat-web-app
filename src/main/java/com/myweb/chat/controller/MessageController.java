package com.myweb.chat.controller;

import com.myweb.chat.dto.MessageDto;
import com.myweb.chat.service.MessageService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;

@RestController
@RequestMapping("/api/messages")
public class MessageController {

	private final MessageService messageService;

	public MessageController(MessageService messageService) {
		this.messageService = messageService;
	}

	@Operation(summary = "Danh sách tin nhắn theo phòng")
	@ApiResponses({
			@ApiResponse(responseCode = "200", description = "Thành công"),
			@ApiResponse(responseCode = "404", description = "Không tìm thấy phòng")
	})
	@GetMapping("/room/{roomId}")
	public List<MessageDto> listByRoom(@PathVariable Long roomId) {
		return messageService.listByRoom(roomId);
	}

	@Operation(summary = "Gửi tin nhắn vào phòng")
	@ApiResponses({
			@ApiResponse(responseCode = "200", description = "Thành công"),
			@ApiResponse(responseCode = "400", description = "Nội dung không hợp lệ"),
			@ApiResponse(responseCode = "404", description = "Không tìm thấy phòng/người dùng"),
			@ApiResponse(responseCode = "401", description = "Chưa xác thực")
	})
	@PostMapping("/room/{roomId}")
	public MessageDto send(@PathVariable Long roomId, @RequestParam String content, Principal principal) {
		return messageService.sendMessage(principal.getName(), roomId, content);
	}
}



