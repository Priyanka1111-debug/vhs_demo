package com.subaru.tele.vhs.model;

import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.*;

/**
 * @author gauraaga
 */
@DynamoDbBean
public class VhsItem {

    private String vin;
    private String eventCreationTime;
    private String eventId;
    private Long createDate;
    private Long createDateTime;
    private Long expirationDate;
    private String market_EventCreationTime_Vin;
    private String payload;

    @DynamoDbPartitionKey
    public String getVin() {
        return vin;
    }

    public void setVin(String vin) {
        this.vin = vin;
    }

    @DynamoDbSortKey
    public String getEventCreationTime() {
        return eventCreationTime;
    }

    public void setEventCreationTime(String eventCreationTime) {
        this.eventCreationTime = eventCreationTime;
    }

    public String getEventId() {
        return eventId;
    }

    public void setEventId(String eventId) {
        this.eventId = eventId;
    }

    @DynamoDbSecondaryPartitionKey(indexNames = {"CreateDate-Market_EventCreationTime_Vin-Index"})
    public Long getCreateDate() {
        return createDate;
    }

    public void setCreateDate(Long createDate) {
        this.createDate = createDate;
    }

    @DynamoDbSecondarySortKey(indexNames = {"Vin-CreateDateTime-Index"})
    public Long getCreateDateTime() {
        return createDateTime;
    }

    public void setCreateDateTime(Long createDateTime) {
        this.createDateTime = createDateTime;
    }

    @DynamoDbAttribute("ExpirationDate")
    public Long getExpirationDate() {
        return expirationDate;
    }

    public void setExpirationDate(Long expirationDate) {
        this.expirationDate = expirationDate;
    }

    @DynamoDbSecondarySortKey(indexNames = {"CreateDate-Market_EventCreationTime_Vin-Index"})
    public String getMarket_EventCreationTime_Vin() {
        return market_EventCreationTime_Vin;
    }

    public void setMarket_EventCreationTime_Vin(String market_EventCreationTime_Vin) {
        this.market_EventCreationTime_Vin = market_EventCreationTime_Vin;
    }

    public String getPayload() {
        return payload;
    }

    public void setPayload(String payload) {
        this.payload = payload;
    }
}
