package ch.fhnw.kvanc.server.web;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/votes")
public class VoteController {
    private Logger logger = LoggerFactory.getLogger(VoteController.class);

    @GetMapping
    public ResponseEntity<String> sayHello() {
        logger.debug("Server called successfully");
        return new ResponseEntity<String>("Hello from Spring Boot server", HttpStatus.OK);
    }
}
