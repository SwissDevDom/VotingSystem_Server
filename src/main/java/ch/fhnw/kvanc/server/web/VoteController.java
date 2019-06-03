package ch.fhnw.kvanc.server.web;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import ch.fhnw.kvanc.server.domain.Vote;
import ch.fhnw.kvanc.server.integration.QuestionWatchService;
import ch.fhnw.kvanc.server.repository.VoteRepository;
import ch.fhnw.kvanc.server.service.AuthorizationService;

@RestController
@RequestMapping("/votes")
public class VoteController {
    private Logger logger = LoggerFactory.getLogger(VoteController.class);

    @Autowired
    private QuestionWatchService watchService;

    @Autowired
    private AuthorizationService authorizationService;

    @Autowired
    private VoteRepository voteRepository;

    @GetMapping
    public ResponseEntity<String> sayHello() {
        logger.debug("Server called successfully!!");
        return new ResponseEntity<String>("Hello from server", HttpStatus.OK);
    }

    @CrossOrigin
    @GetMapping("/question")
    ResponseEntity<QuestionDTO> getQuestion() {
        String content = watchService.getQuestionContent();
        if (content == null) {
            return new ResponseEntity<QuestionDTO>(HttpStatus.NO_CONTENT);
        }
        QuestionDTO dto = new QuestionDTO(content);
        return new ResponseEntity<QuestionDTO>(dto, HttpStatus.OK);
    }

    @CrossOrigin
    @PostMapping
    public ResponseEntity<TokenDTO> createVoteForUser(@RequestBody TokenDTO token, HttpServletRequest request) {
        String ipAddress = request.getRemoteAddr();
        if (!authorizationService.checkAndAddAddress(ipAddress)) {
            logger.info("Vote already created from host '{}'", ipAddress);
            return new ResponseEntity<>(HttpStatus.PRECONDITION_FAILED);
        }
        Vote vote = voteRepository.createVote(token.getEmail());
        if (vote == null) {
            logger.info("Vote already created for '{}'", token.getEmail());
            return new ResponseEntity<>(HttpStatus.PRECONDITION_FAILED);
        }
        TokenDTO tokenDTO = new TokenDTO(vote.getId(), token.getEmail());
        logger.debug("Vote created for '" + vote.getEmail() + "'");
        return new ResponseEntity<TokenDTO>(tokenDTO, HttpStatus.CREATED);
    }

    @CrossOrigin
    @PutMapping("/{id}")
    public ResponseEntity<Void> vote(@PathVariable String id, @RequestBody @Valid VoteDTO dto, BindingResult result) {
        if (result.hasErrors()) {
            logger.error("Validation failed!");
            return new ResponseEntity<Void>(HttpStatus.PRECONDITION_FAILED); 
        }
        Vote vote = voteRepository.findVote(id);
        if (vote == null) {
            logger.error("No vote found for '{}'", id);
            return new ResponseEntity<Void>(HttpStatus.NOT_FOUND);
        }
        if (vote.isClosed()) {
            logger.info("Already voted: '" + vote.getEmail() + "'");
            return new ResponseEntity<Void>(HttpStatus.TOO_MANY_REQUESTS);
        }
        vote.setTrue(dto.getVote());
        voteRepository.updateVote(vote);
        logger.debug("Vote updated for '" + vote.getEmail() + "'");
        return new ResponseEntity<Void>(HttpStatus.OK);
    }

}