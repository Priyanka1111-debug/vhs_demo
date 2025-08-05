package com.subaru.tele.vhs.handler;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.amazonaws.services.lambda.runtime.events.SQSEvent;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.subaru.tele.vhs.model.VhsItem;
import com.subaru.tele.vhs.utils.DateUtils;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbEnhancedClient;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbTable;
import software.amazon.awssdk.enhanced.dynamodb.TableSchema;
import software.amazon.awssdk.enhanced.dynamodb.model.PutItemEnhancedRequest;
import software.amazon.awssdk.services.dynamodb.DynamoDbClient;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.time.Instant;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author gauraaga
 */
public class LambdaSQSHandler implements RequestHandler<SQSEvent, String> {

    private static final String TABLE_NAME = "TELE_VEH_HEALTH_STATUS_INGEST";
    private final DynamoDbEnhancedClient enhancedClient;
    private final DynamoDbTable<VhsItem> vhsTable;
    private final ObjectMapper objectMapper;

    public LambdaSQSHandler() {
        DynamoDbClient client = DynamoDbClient.create();
        this.enhancedClient = DynamoDbEnhancedClient.builder()
                .dynamoDbClient(client)
                .build();
        this.vhsTable = enhancedClient.table(TABLE_NAME, TableSchema.fromBean(VhsItem.class));
        this.objectMapper = new ObjectMapper();

    }

    @Override
    public String handleRequest(SQSEvent sqsEvent, Context context) {
        context.getLogger().log("Context Logger " + "LambdaSQSHandler");
        List<SQSEvent.SQSMessage> records = sqsEvent.getRecords();
       // Map<String, Object> input =   new HashMap<>();


        try {
           // context.getLogger().log("VHS Lambda Input : "+input);
            for (SQSEvent.SQSMessage msg : records) {
                String sqsInput = msg.getBody();
                Map<String, Object> input = objectMapper.readValue(sqsInput, new TypeReference<>() {});

                context.getLogger().log("Received message from SQS: " + input);
                String payloadJson = objectMapper.writeValueAsString(input);

                Map<String, Object> hist = (Map<String, Object>) input.get("hist");
                Map<String, Object> vehicle = (Map<String, Object>) hist.get("vehicle");
                Map<String, Object> event = (Map<String, Object>) hist.get("event");

                String vin = (String) vehicle.get("vin");
                context.getLogger().log("Vin: "+ vin);

                String eventCreationTime = (String) event.get("eventCreationTime");
                String market = (String) vehicle.get("market");

                long createDate = DateUtils.toEpochDay(Instant.now());
                long createDateTime = Instant.now().toEpochMilli();
                long expirationDate = Instant.now().plusSeconds(365 * 24 * 60 * 60).getEpochSecond();

                String marketEventCreationVin = market + "_" + eventCreationTime + "_" + vin;

                VhsItem item = new VhsItem();
                item.setVin(vin);
                item.setEventCreationTime(eventCreationTime);
                item.setEventId((String) event.get("eventId"));
                item.setCreateDate(createDate);
                item.setCreateDateTime(createDateTime);
                item.setExpirationDate(expirationDate);
                item.setMarket_EventCreationTime_Vin(marketEventCreationVin);
                item.setPayload(payloadJson);

                vhsTable.putItem(PutItemEnhancedRequest.builder(VhsItem.class)
                        .item(item)
                        .build());

                context.getLogger().log("Data Saved successfully.");

            }
            return "Success";
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
        catch (Exception e) {
            context.getLogger().log("Error: " + e.getMessage());
            return "Failed";
        }

//        for (SQSEvent.SQSMessage msg : records) {
//            String body = msg.getBody();
//            context.getLogger().log("Received message: " + body);
//
//            try {
//                JsonNode jsonNode = objectMapper.readTree(body);
//
//            } catch (com.fasterxml.jackson.core.JsonProcessingException e) {
//                context.getLogger().log("Invalid JSON format: " + e.getMessage());
//                throw new RuntimeException("Invalid JSON format", e);
//
//            } catch (Exception e) {
//                context.getLogger().log("Retriable error occurred: " + e.getMessage());
//                throw new RuntimeException(e);
//            }
//        }

    }
}
