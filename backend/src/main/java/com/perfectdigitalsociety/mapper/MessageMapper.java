package com.perfectdigitalsociety.mapper;

import com.perfectdigitalsociety.dto.response.MessageResponse;
import com.perfectdigitalsociety.entity.Message;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;

import java.util.List;

@Mapper(
    componentModel = "spring",
    unmappedTargetPolicy = ReportingPolicy.IGNORE
)
public interface MessageMapper {
    
    /**
     * Convert Message entity to MessageResponse DTO
     */
    @Mapping(target = "userId", source = "user.id")
    @Mapping(target = "username", source = "user.username")
    @Mapping(target = "messageType", expression = "java(message.getMessageType().toString())")
    @Mapping(target = "moderationStatus", expression = "java(message.getModerationStatus().toString())")
    MessageResponse toMessageResponse(Message message);
    
    /**
     * Convert list of Message entities to list of MessageResponse DTOs
     */
    List<MessageResponse> toMessageResponseList(List<Message> messages);
    
    /**
     * Convert Message.MessageType enum to string
     */
    default String mapMessageType(Message.MessageType messageType) {
        return messageType != null ? messageType.toString() : null;
    }
    
    /**
     * Convert string to Message.MessageType enum
     */
    default Message.MessageType mapMessageType(String messageType) {
        return messageType != null ? Message.MessageType.valueOf(messageType.toUpperCase()) : null;
    }
    
    /**
     * Convert Message.ModerationStatus enum to string
     */
    default String mapModerationStatus(Message.ModerationStatus moderationStatus) {
        return moderationStatus != null ? moderationStatus.toString() : null;
    }
    
    /**
     * Convert string to Message.ModerationStatus enum
     */
    default Message.ModerationStatus mapModerationStatus(String moderationStatus) {
        return moderationStatus != null ?  Message.ModerationStatus.valueOf(moderationStatus.toUpperCase()) : null;
    }
}