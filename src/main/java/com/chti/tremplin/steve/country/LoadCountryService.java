package com.chti.tremplin.steve.country;

import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.io.*;

@Service
@RequiredArgsConstructor
@Slf4j
public class LoadCountryService {

    public static final String SPLIT_CHAR = ";";
    public static final String COUNTRY_FILE = "src/main/resources/static/curiexplore-pays.csv";
    private final CountryService countryService;

    @PostConstruct
    public void loadCountries() {
        try (BufferedReader reader = new BufferedReader(new FileReader(COUNTRY_FILE))) {
            String line;
            boolean firstLine = true;

            while ((line = reader.readLine()) != null) {
                if (firstLine) {
                    firstLine = false;
                    continue;
                }

                String[] values = line.split(SPLIT_CHAR);
                if (values.length >= 2) {
                    String code = values[0].trim();
                    String name = values[1].trim();

                    Country country = Country.builder()
                            .code(code)
                            .name(name)
                            .build();

                    countryService.saveCountry(country);

                    //log.info("Pays traité et envoyé pour enregistrement : {}", country.getName());
                } else {
                    log.warn("Ligne CSV invalide : {}",line);
                }
            }
            log.info("Importation CSV terminée.");

        } catch (IOException e) {
            log.error("Erreur lors de la lecture du fichier CSV : " + e.getMessage());
        }
    }
}