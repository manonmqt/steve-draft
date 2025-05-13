package com.chti.tremplin.steve.country;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CountryService {

    private final CountryRepository countryRepository;

    public Country saveCountry(Country country) {
        return countryRepository.save(country);
    }

    public boolean isKnown(String countryCode) {
        return true; //TBD return if know in H2
    }
}
