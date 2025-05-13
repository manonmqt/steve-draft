package com.chti.tremplin.steve.partner;

import com.chti.tremplin.steve.location.Location;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class NationalGeographicController {

    public void sendData(Location location) {
        //TBD data sent
        log.debug("done -- {}", location);
    }
}
