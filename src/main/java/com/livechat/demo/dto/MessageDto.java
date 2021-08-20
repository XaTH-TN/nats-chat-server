package com.livechat.demo.dto;

import lombok.Builder;

import java.sql.Time;
import java.sql.Timestamp;

@Builder
public class MessageDto {
    private String senderId;
    private String recipientId;
    private Timestamp timestamp;
    private String text;
    private String mid;
    private String appId;
}
