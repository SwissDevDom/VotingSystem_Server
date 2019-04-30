package ch.fhnw.kvanc.server.web;

import ch.fhnw.kvanc.server.domain.Vote;
import ch.fhnw.kvanc.server.integration.QuestionWatchService;
import ch.fhnw.kvanc.server.repository.VoteRepository;
import ch.fhnw.kvanc.server.service.AuthorizationService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.RequestEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;

@RestController
@RequestMapping("/votes")
public class VoteController {
    private Logger logger = LoggerFactory.getLogger(VoteController.class);

    @GetMapping
    public ResponseEntity<String> sayHello() {
        logger.debug("Server called successfully");
        return new ResponseEntity<String>("Hello from Spring Boot server", HttpStatus.OK);
    }

    @Autowired
    private QuestionWatchService watchService;

    @Autowired
    private AuthorizationService authorizationService;

    @Autowired
    private VoteRepository voteRepository;

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

    @CrossOrigin
    @PostMapping("/votes")
    public ResponseEntity<TokenDTO> createVoteForUser(@RequestBody TokenDTO token, HttpServletRequest request){
        String ipAdress = request.getRemoteAddr();
        if(!authorizationService.checkAndAddAddress(ipAdress)){
            logger.info("Vote already created from host '{}'", ipAdress);
            return new ResponseEntity<>(HttpStatus.PRECONDITION_FAILED);
        }
        Vote vote = voteRepository.createVote(token.getEmail());
        if(vote == null){
            logger.info("Vote already created for '{}",token.getEmail());
        return new ResponseEntity<>(HttpStatus.PRECONDITION_FAILED);
        }

        TokenDTO tokenDto = new TokenDTO(vote.getId(), token.getEmail());
        logger.debug("Vote created for '"+vote.getEmail()+"'");
        return new ResponseEntity<>(tokenDto, HttpStatus.CREATED);
    }

    @CrossOrigin
    @PutMapping("/votes/{token}")
    public ResponseEntity<VoteDTO> vote(@Valid VoteDTO voteDTO, BindingResult bindingResult){
        if(bindingResult.hasErrors()){
            return new ResponseEntity<>(HttpStatus.PRECONDITION_FAILED);
        }
        if(voteDTO.getVote() == null){
            return new ResponseEntity<>((HttpStatus.NOT_FOUND));
        }
        if (voteDTO.getVote()){
         return new ResponseEntity<>((HttpStatus.TOO_MANY_REQUESTS));
        }else {
            return new ResponseEntity<>(HttpStatus.OK);
        }
    }
}
