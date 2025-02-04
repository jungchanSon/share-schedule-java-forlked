package com.schedule.share.user.adaptor.inbound.api.dto;

import lombok.Builder;

import java.time.LocalDateTime;

public class UserResponseDTO {

    @Builder
    public record Response(
        long id,
        long recentCalendarId,
        String nickname,
        String method,
        byte[] image,
        LocalDateTime registeredAt,
        LocalDateTime modifiedAt
    ) {
    }
}
