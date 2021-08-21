package com.livechat.demo.utils;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.livechat.demo.config.AppProperties;
import com.livechat.demo.dto.MessageDto;
import io.nats.client.Connection;
import io.nats.client.Dispatcher;
import io.nats.client.Nats;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.sql.Timestamp;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class PublishMessageUtil {

    private static final Logger LOGGER = LoggerFactory.getLogger(PublishMessageUtil.class);

    @Autowired
    private AppProperties appProperties;

    @Async
    public void processEvent(HashMap<String, Object> req) {
        MessageDto dto = new MessageDto();

        ObjectMapper objectMap = new ObjectMapper();
        String objMap = null;
        try {
            objMap = objectMap.writeValueAsString(req);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }
        LOGGER.info("Request log: " + objMap);
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
                Map recipient = (Map) webhookEvent.get("recipient");
                dto.setRecipientId((String) recipient.get("id"));
                dto.setSenderId(pageScopeId);
                dto.setTimestamp(new Timestamp((Long) webhookEvent.get("timestamp")));

                Map message = (Map) webhookEvent.get("message");
                dto.setMid(message.get("mid") != null ? (String) message.get("mid") : null);
                dto.setText((String) message.get("text"));
            }
        }
        if (dto != null) {
            publishEvent(dto);
        }
    }

    private void publishEvent(MessageDto dto) {

        try {
            ObjectMapper objectMapper = new ObjectMapper();
            String json = objectMapper.writeValueAsString(dto);
            // connect to nats server
            Connection nats = Nats.connect(appProperties.getNatsConfig());
            // message dispatcher
//            Dispatcher dispatcher = nats.createDispatcher(msg -> {
//            });
//
//            // subscribes to nats.demo.service channel
//            dispatcher.subscribe("nats.demo.service", msg -> {
//                String msgJson = new String(msg.getData());
//                MessageDto dtoReceive = new MessageDto();
//                try {
//                    dtoReceive = objectMapper.readValue(msgJson, MessageDto.class);
//                } catch (JsonProcessingException e) {
//                    e.printStackTrace();
//                }
//                LOGGER.info("Received : " + dtoReceive.getRecipientId());
//            });
            // publish a message to the channel
            nats.publish("nats.demo.service", json.getBytes());


            LOGGER.info("-------------");
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }
    }
}
