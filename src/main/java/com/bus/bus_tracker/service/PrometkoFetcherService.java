package com.bus.bus_tracker.service;
/*
import com.bus.bus_tracker.entity.BusEntity;
import com.bus.bus_tracker.entity.BusPositionEntity;
import com.bus.bus_tracker.repository.BusPositionRepository;
import com.bus.bus_tracker.repository.BusRepository;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class PrometkoFetcherService {

    private final BusPositionRepository positionRepository;
    private final BusRepository busRepository;
    private final RestTemplate restTemplate = new RestTemplate();

    private final String API_URL = "https://api.split.prometko.si/vehicles";

    @Scheduled(fixedRate = 5000)
    public void fetchRealData() {
        try {
            HttpHeaders headers = new HttpHeaders();
            headers.set("User-Agent",
                    "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/120.0.0.0 Safari/537.36");

            HttpEntity<String> entity = new HttpEntity<>(headers);

            ResponseEntity<String> response = restTemplate.exchange(API_URL, HttpMethod.GET, entity, String.class);
            String jsonResponse = response.getBody();

            ObjectMapper mapper = new ObjectMapper();
            JsonNode root = mapper.readTree(jsonResponse);
            JsonNode vehicles = root.get("data");

            int count = 0;
            if (vehicles != null && vehicles.isArray()) {
                for (JsonNode v : vehicles) {
                    JsonNode garageNode = v.get("garageNumber");
                    if (garageNode == null || garageNode.isNull()) continue;

                    String garageNumberRaw = garageNode.asText();
                    String digitsOnly = garageNumberRaw.replaceAll("[^\\d]", "");
                    if (digitsOnly.isEmpty()) continue;

                    String garageNumber = String.valueOf(Long.parseLong(digitsOnly));

                    BusEntity bus = busRepository.findByBusNumber(garageNumber)
                            .orElseGet(() -> createNewBus(garageNumber));

                    JsonNode latNode = v.get("latitude");
                    JsonNode lngNode = v.get("longitude");
                    if (latNode == null || lngNode == null || latNode.isNull() || lngNode.isNull()) continue;

                    BusPositionEntity pos = new BusPositionEntity();
                    pos.setBus(bus);
                    pos.setGpsLat(latNode.asDouble());
                    pos.setGpsLng(lngNode.asDouble());
                    pos.setTimestamp(LocalDateTime.now());
                    JsonNode rsnNode = v.get("routeShortName");
                    if (rsnNode != null && !rsnNode.isNull()) {
                        String rsn = rsnNode.asText();
                        if (rsn != null && !rsn.trim().isEmpty()) {
                            pos.setRouteShortName(rsn.trim());
                        }
                    }

                    positionRepository.save(pos);
                    count++;
                }
            }
            System.out.println("Successfully fetched " + count + " buses from the Prometko API!");

        } catch (Exception e) {
            System.err.println(" Error in fetch: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private BusEntity createNewBus(String garageNumber) {
        System.out.println("New bus detected: " + garageNumber);
        BusEntity newBus = new BusEntity();
        newBus.setBusNumber(garageNumber);
        newBus.setRegistration("NEPOZNATO");
        return busRepository.save(newBus);
    }
}
*/