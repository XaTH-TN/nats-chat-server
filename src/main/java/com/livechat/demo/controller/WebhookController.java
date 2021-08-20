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

}
