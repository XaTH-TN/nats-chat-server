package com.livechat.demo.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.sql.Timestamp;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class MessageDto {
    private String senderId;
    private String recipientId;
    private Timestamp timestamp;
    private String text;
    private String mid;
    private String appId;
}
