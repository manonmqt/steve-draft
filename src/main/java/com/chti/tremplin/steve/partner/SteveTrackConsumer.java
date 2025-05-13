package com.chti.tremplin.steve.partner;

import com.chti.tremplin.steve.country.CountryService;
import com.chti.tremplin.steve.location.Location;
import com.chti.tremplin.steve.location.LocationController;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Service
@Slf4j
@RequiredArgsConstructor
public class SteveTrackConsumer {

    private final CountryService countryService;
    private final NationalGeographicController partnerController;

    @KafkaListener(topics = {"${steve.kafka.consumer.topics}"}, containerFactory = "kafkaListenerContainerFactory")
    public void listen(Location newLocation) {
        LocationController.initKafkaStats();

        if (this.countryService.isKnown(newLocation.getCountryCode()))
                this.partnerController.sendData(newLocation);

        LocationController.completeKafkaStats();
    }

}
