package com.example.bajajchallenge.service;

import com.example.bajajchallenge.model.User;
import com.example.bajajchallenge.model.WebhookResponse;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import java.util.*;

@Service
public class MutualFollowerService {
    private final RestTemplate restTemplate = new RestTemplate();

    private final String initUrl = "https://bfhldevapigw.healthrx.co.in/hiring/generateWebhook";
    private final Map<String, Object> requestBody = Map.of(
            "name", "John Doe",
            "regNo ", "REG12347",
            "email ", "john@example.com"
    );

    public void process() {
        try {
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            HttpEntity<Map<String, Object>> entity = new HttpEntity<>(requestBody, headers);

            ResponseEntity<WebhookResponse> response = restTemplate.postForEntity(initUrl, entity, WebhookResponse.class);

            if (response.getStatusCode().is2xxSuccessful() && response.getBody() != null) {
                WebhookResponse webhookData = response.getBody();
                List<List<Integer>> outcome = findMutualFollowers(webhookData.getUsers());

                sendResult(webhookData.getWebhookUrl(), webhookData.getAccessToken(), outcome);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private List<List<Integer>> findMutualFollowers(List<User> users) {
        Map<Integer, Set<Integer>> followMap = new HashMap<>();
        for (User user : users) {
            followMap.put(user.getId(), new HashSet<>(user.getFollows()));
        }

        Set<String> seen = new HashSet<>();
        List<List<Integer>> mutuals = new ArrayList<>();

        for (User user : users) {
            int uid = user.getId();
            for (int fid : user.getFollows()) {
                if (followMap.containsKey(fid) && followMap.get(fid).contains(uid)) {
                    int min = Math.min(uid, fid);
                    int max = Math.max(uid, fid);
                    String key = min + "," + max;
                    if (!seen.contains(key)) {
                        mutuals.add(List.of(min, max));
                        seen.add(key);
                    }
                }
            }
        }

        return mutuals;
    }

    private void sendResult(String webhookUrl, String token, List<List<Integer>> outcome) {
        Map<String, Object> result = Map.of(
                "regNo ", "REG12347",
                "outcome ", outcome
        );

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.set("Authorization", token);

        HttpEntity<Map<String, Object>> entity = new HttpEntity<>(result, headers);

        for (int i = 0; i < 4; i++) {
            try {
                ResponseEntity<String> response = restTemplate.postForEntity(webhookUrl, entity, String.class);
                if (response.getStatusCode().is2xxSuccessful()) {
                    System.out.println("Webhook POST successful.");
                    return;
                }
            } catch (Exception e) {
                System.out.println("Attempt " + (i+1) + " failed. Retrying...");
            }
        }
    }
}
