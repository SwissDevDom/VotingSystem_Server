package ch.fhnw.kvanc.server.integration;

import java.io.File;
import java.io.IOException;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardWatchEventKinds;
import java.nio.file.WatchEvent;
import java.nio.file.WatchKey;
import java.nio.file.WatchService;

import javax.annotation.PostConstruct;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.FileSystemResource;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import ch.fhnw.kvanc.server.repository.VoteRepository;
import ch.fhnw.kvanc.server.service.AuthorizationService;
import ch.fhnw.kvanc.server.web.WebSocketEndpoint;

/**
 * WatchService
 */
@Component
public class QuestionWatchService {
    private Logger logger = LoggerFactory.getLogger(QuestionWatchService.class);

    @Value("${watchservice.file:question.txt}")
    private String filename;

    @Value("${watchservice.path:.}")
    private String pathname;

    @Autowired
    private VoteRepository voteRepository;

    @Autowired
    private AuthorizationService authorizationService;

    @Autowired
    private WebSocketEndpoint websocket;

    private WatchService watchService;

    private Path path;

    private String questionContent = null;

    @PostConstruct
    public void afterPropertiesSet() throws IOException, InterruptedException {
        watchService = FileSystems.getDefault().newWatchService();
        path = Paths.get(pathname);
        path.register(watchService, StandardWatchEventKinds.ENTRY_MODIFY);
        logger.info("Watching file '{}/{}'", pathname, filename);
    }

    @Scheduled(fixedRate = 100)
    public void watch() throws IOException, InterruptedException {
        WatchKey key = watchService.poll();
        if (key != null) {
            for (WatchEvent<?> event : key.pollEvents()) {
                final Path changed = (Path) event.context();
                if (changed.endsWith(filename)) {
                    logger.info("File '{}' has changed", filename);
                    readFile(pathname + "/" + filename);
                    if (questionContent.length() > 0) {
                        logger.info("File content is '{}'", questionContent);
                    }
                }
            }
            key.reset();
        }
    }

    private void readFile(String filepath) throws IOException {
        File file = new FileSystemResource(filepath).getFile();
        questionContent = new String(Files.readAllBytes(file.toPath())).trim();
        if (questionContent.length() == 0) {
            // reset everything
            voteRepository.reset();
            authorizationService.reset();
            websocket.reset();
            logger.info("Reset all services");
        } else {
            authorizationService.reset();
            voteRepository.reOpenAll();
            websocket.pushMessage(questionContent);
            logger.info("New Question has arrived: '{}'", questionContent);
        }
    }

    /**
     * @return the questionContent
     */
    public String getQuestionContent() {
        return questionContent;
    }

}