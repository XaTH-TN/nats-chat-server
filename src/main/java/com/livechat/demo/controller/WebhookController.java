package com.livechat.demo.controller;

import io.nats.client.Connection;
import io.nats.client.Dispatcher;
import io.nats.client.Message;
import io.nats.client.Nats;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;

@RestController
@RequestMapping("/api/webhook")
@CrossOrigin(origins = "*", allowedHeaders = "*")
public class WebhookController {
    private static final Logger LOGGER = LoggerFactory.getLogger(WebhookController.class);

    @PostMapping
    public ResponseEntity<?> receiveMessage() {

        try {
            // connect to nats server
            Connection nats = Nats.connect();
            // message dispatcher
            Dispatcher dispatcher = nats.createDispatcher(msg -> {
            });

            // subscribes to nats.demo.service channel
            dispatcher.subscribe("nats.demo.service", msg -> {
                LOGGER.info("Received : " + new String(msg.getData()));
            });
            // publish a message to the channel
            nats.publish("nats.demo.service", "Hello NATS".getBytes());


            LOGGER.info("-------------");
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }

        return ResponseEntity.ok(null);
    }

    private void publisher(String message) {

    }
}
