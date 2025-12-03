package com.perfectdigitalsociety.service;

import com.perfectdigitalsociety.dto.request.CreateMessageRequest;
import com.perfectdigitalsociety.dto.request.FlagRequest;
import com.perfectdigitalsociety.dto.request.UpdateMessageRequest;
import com.perfectdigitalsociety.dto.response.MessageResponse;
import com.perfectdigitalsociety.dto.response.StatusResponse;
import com.perfectdigitalsociety.entity.Message;
import com.perfectdigitalsociety.entity.User;
import com.perfectdigitalsociety.exception.MessageNotFoundException;
import com.perfectdigitalsociety.exception.UnauthorizedException;
import com.perfectdigitalsociety.exception.UserNotFoundException;
import com.perfectdigitalsociety.mapper.MessageMapper;
import com.perfectdigitalsociety.repository.MessageRepository;
import com.perfectdigitalsociety.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class MessageService {
    
    private final MessageRepository messageRepository;
    private final UserRepository userRepository;
    private final MessageMapper messageMapper;
    private final BalanceService balanceService;
    private final CommunityService communityService;
    
    // Business Logic Methods as specified in documentation
    
    /**
     * Create new message
     */
    public MessageResponse createMessage(Long userId, CreateMessageRequest request) {
        log.info("Creating message for user ID: {}", userId);
        
        User user = userRepository.findById(userId)
            .orElseThrow(() -> new UserNotFoundException("User not found with ID: " + userId));
        
        if (! user.getIsActive()) {
            throw new UnauthorizedException("User account is not active");
        }
        
        // Create message entity
        Message message = new Message();
        message.setUser(user);
        message.setContent(request.getContent());
        message.setMessageType(Message.MessageType.valueOf(request.getMessageType().toUpperCase()));
        message.setIsVisible(true);
        message.setModerationStatus(Message.ModerationStatus.PENDING);
        message.setFlagCount(0);
        
        // Calculate impact scores
        analyzeMessageImpact(message);
        
        Message savedMessage = messageRepository.save(message);
        
        // Trigger balance recalculation
        balanceService.analyzeImpact(savedMessage);
        
        log.info("Message created successfully with ID: {}", savedMessage.getId());
        return messageMapper.toMessageResponse(savedMessage);
    }
    
    /**
     * Update message
     */
    public MessageResponse updateMessage(Long messageId, Long userId, UpdateMessageRequest request) {
        log.info("Updating message ID: {} by user ID: {}", messageId, userId);
        
        Message message = messageRepository.findById(messageId)
            .orElseThrow(() -> new MessageNotFoundException("Message not found with ID: " + messageId));
        
        // Check if user owns the message or has moderation rights
        if (!message.getUser().getId().equals(userId) && !hasModeratorRights(userId)) {
            throw new UnauthorizedException("User not authorized to update this message");
        }
        
        // Update content
        message.setContent(request.getContent());
        message.setUpdatedAt(LocalDateTime.now());
        
        // Recalculate impact scores
        analyzeMessageImpact(message);
        
        // Reset moderation status if content changed
        if (! message.getUser().getId().equals(userId)) {
            message.setModerationStatus(Message.ModerationStatus.PENDING);
        }
        
        Message savedMessage = messageRepository.save(message);
        
        log.info("Message updated successfully: {}", messageId);
        return messageMapper.toMessageResponse(savedMessage);
    }
    
    /**
     * Delete message
     */
    public StatusResponse deleteMessage(Long messageId, Long userId) {
        log.info("Deleting message ID: {} by user ID: {}", messageId, userId);
        
        Message message = messageRepository.findById(messageId)
            .orElseThrow(() -> new MessageNotFoundException("Message not found with ID: " + messageId));
        
        // Check authorization
        if (!message.getUser().getId().equals(userId) && !hasModeratorRights(userId)) {
            throw new UnauthorizedException("User not authorized to delete this message");
        }
        
        // Soft delete - mark as invisible
        message.setIsVisible(false);
        message.setUpdatedAt(LocalDateTime.now());
        messageRepository.save(message);
        
        log.info("Message deleted successfully: {}", messageId);
        
        return new StatusResponse(true, "Message deleted successfully", LocalDateTime.now());
    }
    
    /**
     * Flag message
     */
    public StatusResponse flagMessage(Long messageId, Long userId, FlagRequest request) {
        log.info("Flagging message ID: {} by user ID: {}", messageId, userId);
        
        Message message = messageRepository.findById(messageId)
            .orElseThrow(() -> new MessageNotFoundException("Message not found with ID: " + messageId));
        
        User user = userRepository.findById(userId)
            .orElseThrow(() -> new UserNotFoundException("User not found with ID: " + userId));
        
        // Increase flag count
        message.setFlagCount(message.getFlagCount() + 1);
        
        // Check if message should be hidden based on community rules
        if (shouldHideMessage(message)) {
            message.setIsVisible(false);
            message.setModerationStatus(Message.ModerationStatus.REJECTED);
        }
        
        messageRepository.save(message);
        
        // Trigger balance adjustment
        balanceService.adjustBalance(message.getUser().getId(), -5, 5, "Message flagged by community");
        
        log.info("Message flagged successfully: {}, Total flags: {}", messageId, message.getFlagCount());
        
        return new StatusResponse(true, "Message flagged successfully", LocalDateTime.now());
    }
    
    /**
     * Moderate message (admin/moderator function)
     */
    public MessageResponse moderateMessage(Long messageId, Long moderatorId, Message.ModerationStatus status) {
        log.info("Moderating message ID: {} by moderator ID: {} with status: {}", messageId, moderatorId, status);
        
        if (! hasModeratorRights(moderatorId)) {
            throw new UnauthorizedException("User not authorized to moderate messages");
        }
        
        Message message = messageRepository.findById(messageId)
            .orElseThrow(() -> new MessageNotFoundException("Message not found with ID: " + messageId));
        
        message.setModerationStatus(status);
        message.setIsVisible(status == Message.ModerationStatus.APPROVED);
        message.setUpdatedAt(LocalDateTime.now());
        
        Message savedMessage = messageRepository.save(message);
        
        // Adjust user scores based on moderation result
        if (status == Message.ModerationStatus.REJECTED) {
            balanceService.adjustBalance(message.getUser().getId(), -10, 10, "Message rejected by moderator");
        } else if (status == Message.ModerationStatus.APPROVED) {
            balanceService.adjustBalance(message.getUser().getId(), 5, 0, "Message approved by moderator");
        }
        
        log.info("Message moderation completed: {}", messageId);
        return messageMapper.toMessageResponse(savedMessage);
    }
    
    // Additional service methods
    
    public List<MessageResponse> getAllVisibleMessages() {
        log.info("Getting all visible messages");
        return messageRepository.findVisibleMessagesOrderedByDate()
            .stream()
            .map(messageMapper::toMessageResponse)
            .toList();
    }
    
    public List<MessageResponse> getUserMessages(Long userId) {
        log.info("Getting messages for user ID: {}", userId);
        return messageRepository.findVisibleMessagesByUser(userId)
            .stream()
            .map(messageMapper::toMessageResponse)
            .toList();
    }
    
    public List<MessageResponse> getFlaggedMessages() {
        log.info("Getting flagged messages for moderation");
        return messageRepository.findAllFlaggedMessages()
            .stream()
            .map(messageMapper::toMessageResponse)
            .toList();
    }
    
    public List<MessageResponse> getPendingModerationMessages() {
        log.info("Getting pending moderation messages");
        return messageRepository.findByModerationStatus(Message.ModerationStatus.PENDING)
            .stream()
            .map(messageMapper::toMessageResponse)
            .toList();
    }
    
    public MessageResponse getMessageById(Long messageId) {
        log.info("Getting message by ID: {}", messageId);
        Message message = messageRepository.findById(messageId)
            .orElseThrow(() -> new MessageNotFoundException("Message not found with ID: " + messageId));
        
        return messageMapper.toMessageResponse(message);
    }
    
    // Private helper methods
    
    private void analyzeMessageImpact(Message message) {
        // Content analysis for freedom and security impact
        String content = message.getContent().toLowerCase();
        
        // Simple keyword-based analysis (would be enhanced with NLP)
        Integer freedomImpact = calculateFreedomImpact(content);
        Integer securityImpact = calculateSecurityImpact(content);
        
        message.setFreedomImpact(freedomImpact);
        message.setSecurityImpact(securityImpact);
    }
    
    private Integer calculateFreedomImpact(String content) {
        // Positive freedom keywords
        String[] freedomKeywords = {"freedom", "liberty", "choice", "expression", "open", "transparent"};
        Integer impact = 0;
        
        for (String keyword : freedomKeywords) {
            if (content.contains(keyword)) {
                impact += 5;
            }
        }
        
        return Math.min(impact, 50); // Cap at 50
    }
    
    private Integer calculateSecurityImpact(String content) {
        // Security-related keywords
        String[] securityKeywords = {"secure", "safe", "protect", "privacy", "moderation", "guidelines"};
        Integer impact = 0;
        
        for (String keyword : securityKeywords) {
            if (content.contains(keyword)) {
                impact += 5;
            }
        }
        
        return Math.min(impact, 50); // Cap at 50
    }
    
    private boolean shouldHideMessage(Message message) {
        // Apply community rules to determine if message should be hidden
        Integer flagThreshold = communityService.getActiveFlagThreshold();
        return message.getFlagCount() >= flagThreshold;
    }
    
    private boolean hasModeratorRights(Long userId) {
        return userRepository.findById(userId)
            .map(user -> user.getRole() == User.Role.MODERATOR || user.getRole() == User.Role.ADMIN)
            .orElse(false);
    }
}