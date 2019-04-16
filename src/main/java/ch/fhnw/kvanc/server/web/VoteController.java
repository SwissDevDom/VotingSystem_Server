package ch.fhnw.kvanc.server.web;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/votes")
@EnableScheduling
public class VoteController {
    private Logger logger = LoggerFactory.getLogger(VoteController.class);

    @GetMapping
    public ResponseEntity<String> sayHello() {
        logger.debug("Server called successfully");
        return new ResponseEntity<String>("Hello from Spring Boot server", HttpStatus.OK);
    }

    @Autowired
    private QuestionWatchService watchService;

    @CrossOrigin
    @GetMapping("/question")
    public ResponseEntity<QuestionDTO> getQuestion(){
        String content = watchService.getQuestionContent();
        QuestionDTO dto = new QuestionDTO(content);
        if(content == null || content.isEmpty()){
            logger.debug("No questions found");
            return new ResponseEntity<>(dto, HttpStatus.NO_CONTENT);
        }else {
            logger.debug("Found question");
            return new ResponseEntity<>(dto, HttpStatus.OK);
        }
    }
}
