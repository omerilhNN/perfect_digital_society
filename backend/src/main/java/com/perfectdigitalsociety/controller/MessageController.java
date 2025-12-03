package com.perfectdigitalsociety.controller;

import com.perfectdigitalsociety.dto.request.CreateMessageRequest;
import com.perfectdigitalsociety.dto.request.FlagRequest;
import com.perfectdigitalsociety.dto.request.UpdateMessageRequest;
import com.perfectdigitalsociety.dto.response.MessageResponse;
import com.perfectdigitalsociety.dto.response.StatusResponse;
import com.perfectdigitalsociety.service.MessageService;
import com.perfectdigitalsociety.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/messages")
@RequiredArgsConstructor
@Slf4j
@CrossOrigin(origins = "*", maxAge = 3600)
@Tag(name = "Message Management", description = "Mesaj yönetimi endpoint'leri - mesaj oluşturma, güncelleme, silme ve moderasyon işlemleri")
public class MessageController {
    
    private final MessageService messageService;
    private final UserService userService;
    
    /**
     * Get all messages
     * GET /api/messages
     */
    @Operation(
        summary = "Tüm Mesajları Getir",
        description = "Sistemdeki tüm görünür mesajları getirir. Bu endpoint public erişime açıktır.",
        tags = {"Message Management"}
    )
    @ApiResponses({
        @ApiResponse(
            responseCode = "200",
            description = "Mesajlar başarıyla getirildi",
            content = @Content(mediaType = "application/json", schema = @Schema(implementation = MessageResponse.class))
        ),
        @ApiResponse(
            responseCode = "500",
            description = "Sunucu hatası",
            content = @Content(mediaType = "application/json")
        )
    })
    @GetMapping
    public ResponseEntity<List<MessageResponse>> getAllMessages() {
        log.info("Get all messages request");
        
        try {
            List<MessageResponse> messages = messageService.getAllVisibleMessages();
            log.info("Retrieved {} messages", messages.size());
            return ResponseEntity.ok(messages);
        } catch (Exception e) {
            log.error("Failed to get messages", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
    
    /**
     * Create new message
     * POST /api/messages
     */
    @PostMapping
    public ResponseEntity<MessageResponse> createMessage(
            @AuthenticationPrincipal UserDetails userDetails,
            @Valid @RequestBody CreateMessageRequest request) {
        
        log.info("Create message request from user: {}", userDetails.getUsername());
        
        try {
            Long userId = getCurrentUserId(userDetails.getUsername());
            MessageResponse response = messageService.createMessage(userId, request);
            
            log.info("Message created successfully with ID: {}", response.getId());
            return ResponseEntity.status(HttpStatus.CREATED).body(response);
            
        } catch (Exception e) {
            log.error("Failed to create message for user: {}", userDetails.getUsername(), e);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }
    }
    
    /**
     * Get specific message by ID
     * GET /api/messages/{id}
     */
    @GetMapping("/{id}")
    public ResponseEntity<MessageResponse> getMessageById(@PathVariable Long id) {
        log.info("Get message request for ID: {}", id);
        
        try {
            MessageResponse response = messageService.getMessageById(id);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            log.error("Failed to get message with ID: {}", id, e);
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
    }
    
    /**
     * Update message
     * PUT /api/messages/{id}
     */
    @PutMapping("/{id}")
    public ResponseEntity<MessageResponse> updateMessage(
            @PathVariable Long id,
            @AuthenticationPrincipal UserDetails userDetails,
            @Valid @RequestBody UpdateMessageRequest request) {
        
        log.info("Update message request for ID: {} from user: {}", id, userDetails.getUsername());
        
        try {
            Long userId = getCurrentUserId(userDetails.getUsername());
            MessageResponse response = messageService.updateMessage(id, userId, request);
            
            log.info("Message updated successfully: {}", id);
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            log.error("Failed to update message ID: {} for user: {}", id, userDetails.getUsername(), e);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }
    }
    
    /**
     * Delete message
     * DELETE /api/messages/{id}
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<StatusResponse> deleteMessage(
            @PathVariable Long id,
            @AuthenticationPrincipal UserDetails userDetails) {
        
        log.info("Delete message request for ID: {} from user: {}", id, userDetails.getUsername());
        
        try {
            Long userId = getCurrentUserId(userDetails.getUsername());
            StatusResponse response = messageService.deleteMessage(id, userId);
            
            log.info("Message deleted successfully: {}", id);
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            log.error("Failed to delete message ID: {} for user: {}", id, userDetails.getUsername(), e);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }
    }
    
    /**
     * Flag message
     * POST /api/messages/{id}/flag
     */
    @PostMapping("/{id}/flag")
    public ResponseEntity<StatusResponse> flagMessage(
            @PathVariable Long id,
            @AuthenticationPrincipal UserDetails userDetails,
            @Valid @RequestBody FlagRequest request) {
        
        log.info("Flag message request for ID: {} from user: {}", id, userDetails.getUsername());
        
        try {
            Long userId = getCurrentUserId(userDetails.getUsername());
            StatusResponse response = messageService.flagMessage(id, userId, request);
            
            log.info("Message flagged successfully: {}", id);
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            log.error("Failed to flag message ID: {} for user: {}", id, userDetails.getUsername(), e);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }
    }
    
    /**
     * Get user's messages
     * GET /api/messages/user/{userId}
     */
    @GetMapping("/user/{userId}")
    public ResponseEntity<List<MessageResponse>> getUserMessages(@PathVariable Long userId) {
        log.info("Get user messages request for user ID: {}", userId);
        
        try {
            List<MessageResponse> messages = messageService.getUserMessages(userId);
            log.info("Retrieved {} messages for user ID: {}", messages.size(), userId);
            return ResponseEntity.ok(messages);
        } catch (Exception e) {
            log.error("Failed to get messages for user ID: {}", userId, e);
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
    }
    
    /**
     * Get current user's messages
     * GET /api/messages/my-messages
     */
    @GetMapping("/my-messages")
    public ResponseEntity<List<MessageResponse>> getCurrentUserMessages(@AuthenticationPrincipal UserDetails userDetails) {
        log.info("Get current user messages request for: {}", userDetails.getUsername());
        
        try {
            Long userId = getCurrentUserId(userDetails.getUsername());
            List<MessageResponse> messages = messageService.getUserMessages(userId);
            log.info("Retrieved {} messages for user: {}", messages.size(), userDetails.getUsername());
            return ResponseEntity.ok(messages);
        } catch (Exception e) {
            log.error("Failed to get messages for user: {}", userDetails.getUsername(), e);
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
    }
    
    /**
     * Get flagged messages (for moderators)
     * GET /api/messages/flagged
     */
    @GetMapping("/flagged")
    public ResponseEntity<List<MessageResponse>> getFlaggedMessages(@AuthenticationPrincipal UserDetails userDetails) {
        log.info("Get flagged messages request from user: {}", userDetails.getUsername());
        
        try {
            // Check if user has moderation rights
            Long userId = getCurrentUserId(userDetails.getUsername());
            if (! hasModeratorRights(userId)) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
            }
            
            List<MessageResponse> messages = messageService.getFlaggedMessages();
            log.info("Retrieved {} flagged messages", messages.size());
            return ResponseEntity.ok(messages);
        } catch (Exception e) {
            log.error("Failed to get flagged messages for user: {}", userDetails.getUsername(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
    
    /**
     * Get pending moderation messages (for moderators)
     * GET /api/messages/pending
     */
    @GetMapping("/pending")
    public ResponseEntity<List<MessageResponse>> getPendingModerationMessages(@AuthenticationPrincipal UserDetails userDetails) {
        log.info("Get pending moderation messages request from user: {}", userDetails.getUsername());
        
        try {
            // Check if user has moderation rights
            Long userId = getCurrentUserId(userDetails.getUsername());
            if (!hasModeratorRights(userId)) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
            }
            
            List<MessageResponse> messages = messageService.getPendingModerationMessages();
            log.info("Retrieved {} pending moderation messages", messages.size());
            return ResponseEntity.ok(messages);
        } catch (Exception e) {
            log.error("Failed to get pending moderation messages for user: {}", userDetails.getUsername(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
    
    // Helper methods
    private Long getCurrentUserId(String username) {
        return userService.getUserByUsername(username).getId();
    }
    
    private boolean hasModeratorRights(Long userId) {
        return userService.hasModeratorRights(userId);
    }
}