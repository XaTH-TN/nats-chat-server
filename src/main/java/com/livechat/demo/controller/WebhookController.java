package com.livechat.demo.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.livechat.demo.config.AppProperties;
import com.livechat.demo.dto.MessageDto;
import com.livechat.demo.utils.PublishMessageUtil;
import io.nats.client.Connection;
import io.nats.client.Dispatcher;
import io.nats.client.Nats;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/webhook")
@CrossOrigin(origins = "*", allowedHeaders = "*")
public class WebhookController {
    private static final Logger LOGGER = LoggerFactory.getLogger(WebhookController.class);

    @Autowired
    private AppProperties appProperties;

    @Autowired
    private PublishMessageUtil publishMessageUtil;

    @PostMapping()
    public ResponseEntity<?> receiveMessage(@RequestBody HashMap<String, Object> req) {

        try {
            LOGGER.info("request");

            Connection nats = null;
            nats = Nats.connect(appProperties.getNatsConfig());
            ObjectMapper objectMapper = new ObjectMapper();

            // message dispatcher
            Dispatcher dispatcher = nats.createDispatcher(msg -> {
                LOGGER.info(msg.getReplyTo());
            });

            // subscribes to nats.demo.service channel
            dispatcher.subscribe("nats.demo.service", msg -> {
                String msgJson = new String(msg.getData());
                MessageDto dtoReceive = new MessageDto();
                try {
                    dtoReceive = objectMapper.readValue(msgJson, MessageDto.class);
                } catch (JsonProcessingException e) {
                    e.printStackTrace();
                }
                LOGGER.info("Received : " + dtoReceive.getRecipientId());
            });
            publishMessageUtil.processEvent(req);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
        return ResponseEntity.ok("EVENT_RECEIVED");
    }

    @GetMapping
    public ResponseEntity<?> verifyWebhook(@RequestParam("hub.mode") String hubMode, @RequestParam("hub.challenge") String hubChallenge, @RequestParam("hub.verify_token") String verifyToken) {
        String verifyTokenServer = "a8493a30-00f1-11ec-9a03-0242ac130003";

        // Checks if a token and mode is in the query string of the request
        if (hubMode != null && verifyToken != null) {

            // Checks the mode and token sent is correct
            if (hubMode.equalsIgnoreCase("subscribe") && verifyToken.equalsIgnoreCase(verifyTokenServer)) {

                // Responds with the challenge token from the request
                LOGGER.info("WEBHOOK_VERIFIED");
                return ResponseEntity.ok(hubChallenge);
            } else {
                // Responds with '403 Forbidden' if verify tokens do not match
                return ResponseEntity.status(403).build();
            }
        }
        return ResponseEntity.status(403).build();
    }

}
