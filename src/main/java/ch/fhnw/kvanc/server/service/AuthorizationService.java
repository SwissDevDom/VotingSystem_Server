package ch.fhnw.kvanc.server.service;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.PostConstruct;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

/**
 * AutorizationService
 *
 * Dieser Service stellt sicher, dass sich ein Benutzer vom gleichen
 * Host nur einmal während einer Frage/Antwort Session registrieren kann. Die Kontrolle basiert
 * auf der IP-Adresse, die aus dem HTTP-Header herausgelesen werden kann.
 */
@Component
public class AuthorizationService {
    private Logger logger = LoggerFactory.getLogger(AuthorizationService.class);

    @Value("${authorizationservice.enable:true}")
    private boolean isActive;

    private List<String> ipAddresses = new ArrayList<String>();

    public boolean checkAndAddAddress(String addr) {
        if (!isActive) {
            return true;
        }
        if (ipAddresses.contains(addr)) {
            return false;
        }
        ipAddresses.add(addr);
        return true;
    }
    
    public void reset() {
        ipAddresses.clear();
        logger.debug("Reset was successful");
    }

    @PostConstruct
    public void afterPropertiesSet() {
        logger.info("AuthorizationService is {}", isActive ? "enabled" : "disabled");
    }
}