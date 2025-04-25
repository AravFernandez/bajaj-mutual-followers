package com.example.bajajchallenge.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.List;
import java.util.Map;

public class WebhookResponse {
    @JsonProperty("webhook ")
    private String webhookUrl;

    @JsonProperty("accessToken ")
    private String accessToken;

    @JsonProperty("data")
    private Map<String, List<User>> data;

    public String getWebhookUrl() { return webhookUrl; }
    public String getAccessToken() { return accessToken; }
    public List<User> getUsers() { return data.get("users"); }
}
