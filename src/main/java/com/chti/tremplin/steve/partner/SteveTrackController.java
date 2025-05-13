package com.chti.tremplin.steve.partner;

import com.chti.tremplin.steve.location.Location;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.ArrayList;
import java.util.List;

@Component
public class SteveTrackController {

    private final RestTemplate restTemplate;
    private final String baseUrl = "http://localhost:1080/locations";

    public SteveTrackController(RestTemplateBuilder restTemplateBuilder) {
        this.restTemplate = restTemplateBuilder.build();
    }

    public List<Location> getLocations() {
        List<Location> locations = new ArrayList<>();
        int page = 0;
        boolean hasMore = true;

        try {
            while (hasMore) {
                String url = UriComponentsBuilder.fromHttpUrl(baseUrl)
                        .queryParam("page", page)
                        .toUriString();

                ResponseEntity<List<Location>> response = restTemplate.exchange(
                        url,
                        HttpMethod.GET,
                        null, // Pas de corps de requête
                        new ParameterizedTypeReference<>() {}
                );

                if (!response.getStatusCode().is2xxSuccessful())
                    throw new RuntimeException("Erreur lors de la récupération des localisations. Code de statut : " + response.getStatusCode());

                List<Location> partLocations = response.getBody();
                if (partLocations != null && !partLocations.isEmpty()) {
                    locations.addAll(partLocations);
                } else {
                    hasMore = false; // Arrêter si la page est vide
                }
            }
        } catch (Exception e) {
            throw new RuntimeException("Erreur lors de l'appel de l'API de localisation : " + e.getMessage(), e);
        }
        return locations;
    }
}
