package com.chti.tremplin.steve.location;

import com.chti.tremplin.steve.country.CountryService;
import com.chti.tremplin.steve.partner.NationalGeographicController;
import com.chti.tremplin.steve.partner.SteveTrackController;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;

@RestController
@RequiredArgsConstructor
@Slf4j
public class LocationController {

    public static final String SPLIT_CHAR = ",";
    public static final String LOCATION_FILE = "src/main/resources/static/steve-location.csv";
    public static final ExportStats kafkaExportStats = new ExportStats();

    private final CountryService countryService;
    private final NationalGeographicController partnerController;
    private final SteveTrackController steveController;

    public static void initKafkaStats() {
        kafkaExportStats.setExportType(ExportType.KAFKA);
        kafkaExportStats.setImportStartingTime(LocalDateTime.now());
    }

    public static void completeKafkaStats() {
        kafkaExportStats.setImportStoppingTime(LocalDateTime.now());
    }

    @GetMapping("/location/export/file")
    public ExportStats exportLocationUsingFile() {
        var stats = new ExportStats();
        stats.setExportType(ExportType.FILE);
        stats.setImportStartingTime(LocalDateTime.now());

        try (BufferedReader reader = new BufferedReader(new FileReader(LOCATION_FILE))) {
            String line;
            boolean firstLine = true;

            while ((line = reader.readLine()) != null) {
                if (firstLine) {
                    firstLine = false;
                    continue;
                }

                String[] values = line.split(SPLIT_CHAR);
                if (values.length >= 3) {
                    String id = values[0].trim();
                    String time = values[1].trim();
                    String place = values[2].trim();

                    Location location = Location.builder()
                            .fishId(id)
                            .timestamp(time)
                            .countryCode(place)
                            .build();

                    if (this.countryService.isKnown(place)) this.partnerController.sendData(location) ;
                    else log.warn("Unknow place {} for {} fish at {}", place, id, time);

                } else {
                    log.warn("Ligne CSV invalide : {}",line);
                }
            }
            stats.setImportStoppingTime(LocalDateTime.now());
            log.info("Importation CSV terminée en {} ms. -- Starting time = {}. Stopping time = {}.",
                    stats.getDuration(), stats.getImportStartingTime(), stats.getImportStoppingTime());
        } catch (IOException e) {
            log.error("Erreur lors de la lecture du fichier CSV : {}", e.getMessage());
        }
        return stats;
    }

    @GetMapping("/location/export/api")
    public ExportStats exportLocationUsingAPI() {
        var stats = new ExportStats();
        stats.setExportType(ExportType.API);
        stats.setImportStartingTime(LocalDateTime.now());

        List<Location> locations = this.steveController.getLocations();
        locations.stream()
                .filter(location -> this.countryService.isKnown(location.getCountryCode()))
                .forEach(this.partnerController::sendData);

        stats.setImportStoppingTime(LocalDateTime.now());
        log.info("Importation par API terminée en {} ms. -- Starting time = {}. Stopping time = {}.",
                stats.getDuration(), stats.getImportStartingTime(), stats.getImportStoppingTime());

        return stats;
    }

    @GetMapping("/location/export/kafka")
    public ExportStats exportLocationUsingKafka() {
        return LocationController.kafkaExportStats;
    }

    @Data
    public static class ExportStats {
        private LocalDateTime importStartingTime;
        private LocalDateTime importStoppingTime;
        private ExportType exportType;
        private float duration;

        private void setImportStoppingTime(LocalDateTime importStoppingTime) {
            this.importStoppingTime = importStoppingTime;
            if (this.importStartingTime != null)
                this.duration = ChronoUnit.MICROS.between(this.importStartingTime, this.importStoppingTime)/1000f;
        }
    }

    private enum ExportType {
        API,
        FILE,
        KAFKA
    }
}
