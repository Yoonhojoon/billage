package com.team01.billage.chatting.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.team01.billage.chatting.dto.object.CustomChatResponseUser;
import com.team01.billage.chatting.dto.querydsl.ChatWithSenderDTO;
import com.team01.billage.user.domain.Users;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Setter
@Getter
public class ChatResponseDto {
    private Long chatId;
    private CustomChatResponseUser sender;
    private String message;
    private boolean read;

    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime createdAt;

    public ChatResponseDto(
            Long chatId,
            ChatWithSenderDTO.User sender,
            ChatWithSenderDTO.Message message,
            boolean isRead
    ) {
        this.chatId = chatId;
        this.sender = new CustomChatResponseUser(sender.getId(), sender.getNickname());
        this.message = message.getMessage();
        this.createdAt = message.getCreatedAt();
        this.read = isRead;
    }

    public ChatResponseDto(Long chatId, Users sender, String message, boolean isRead, LocalDateTime createdAt) {
        this.chatId = chatId;
        this.sender = new CustomChatResponseUser(sender);
        this.message = message;
        this.read = isRead;
        this.createdAt = createdAt;
    }

    // builder 패턴

    public ChatResponseDto(Builder builder) {
        this.chatId = builder.chatId;
        this.sender = builder.sender;
        this.message = builder.message;
        this.read = builder.read;
        this.createdAt = builder.createdAt;
    }

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private Long chatId;
        private CustomChatResponseUser sender;
        private String message;
        private boolean read;

        private LocalDateTime createdAt;

        public Builder chatId(Long chatId) {
            this.chatId = chatId;
            return this;
        }
        public Builder sender(Users sender) {
            this.sender = new CustomChatResponseUser(sender);
            return this;
        }
        public Builder message(String message) {
            this.message = message;
            return this;
        }
        public Builder isRead(boolean read) {
            this.read = read;
            return this;
        }
        public Builder createdAt(LocalDateTime createdAt) {
            this.createdAt = createdAt;
            return this;
        }

        public ChatResponseDto build() {
            return new ChatResponseDto(this);
        }
    }
}