package com.livechat.demo.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/webhook")
@CrossOrigin(origins = "*", allowedHeaders = "*")
public class WebhookController {
    private static final Logger LOGGER = LoggerFactory.getLogger(WebhookController.class);

    @PostMapping()
    public ResponseEntity<?> receiveMessage(@RequestBody HashMap<String, Object> req) {

        try {
            LOGGER.info("request");
            String objectValue = (String) req.get("object");
            List<Object> entry = (List<Object>) req.get("entry");
            if (objectValue.equalsIgnoreCase("page")) {
                for (Object e :
                        entry) {

                    Map eHash = (Map) e;

                    List<Object> messaging = (List<Object>) eHash.get("messaging");
                    Map webhookEvent = (Map) messaging.get(0);
                    Map sender = (Map) webhookEvent.get("sender");
                    String pageScopeId = (String) sender.get("id");
                    LOGGER.info("Recipient id: " + pageScopeId);
                }
            }
        } catch (Exception e) {
            LOGGER.error(e.getMessage());
            ResponseEntity.badRequest();
        }

//        try {
//            // connect to nats server
//            Connection nats = Nats.connect();
//            // message dispatcher
//            Dispatcher dispatcher = nats.createDispatcher(msg -> {
//            });
//
//            // subscribes to nats.demo.service channel
//            dispatcher.subscribe("nats.demo.service", msg -> {
//                LOGGER.info("Received : " + new String(msg.getData()));
//            });
//            // publish a message to the channel
//            nats.publish("nats.demo.service", "Hello NATS".getBytes());
//
//
//            LOGGER.info("-------------");
//        } catch (IOException | InterruptedException e) {
//            e.printStackTrace();
//        }

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
