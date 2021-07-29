package dev.rjma;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.WebApplicationType;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.http.MediaType;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.Random;
import java.util.stream.IntStream;

@SpringBootApplication
public class Application implements ApplicationRunner {

    private final WebClient webClient;

    private static final Logger LOGGER = LoggerFactory.getLogger(Application.class);

    public Application(WebClient.Builder webClientBuilder) {
        this.webClient = webClientBuilder.build();
    }

    public static void main(String[] args) {
        new SpringApplicationBuilder(Application.class)
                .web(WebApplicationType.NONE)
                .run(args);
    }

    @Override
    public void run(ApplicationArguments args) {

        while (true) {
            IntStream.range(0, 50)
                    .parallel()
                    .forEach(i -> playGame());
        }

    }

    private void playGame() {
        String gameId = createGame();

        try {
            Thread.sleep((long)(Math.random() * 10000));
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }

        LOGGER.info("Playing game: " + gameId);
        boolean keepPlaying = true;
        while (keepPlaying) {
            keepPlaying = makeGuess(gameId);
        }
    }

    private boolean makeGuess(String gameId) {

        try {
            Character guess = getRandomCharacter();
            String result = webClient.post()
                    .uri("localhost:8080/games/{gameId}", gameId)
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(BodyInserters.fromValue(guess))
                    .retrieve()
                    .bodyToMono(String.class)
                    .block();

            LOGGER.info("Guessed: " + guess + ". " + result);

            return result != null && result.contains("Remaining guesses");
        } catch (RuntimeException ex) {
            LOGGER.error("Failed!", ex);
            return false;
        }
    }

    private String createGame() {
        return webClient.post()
                .uri("localhost:8080/games")
                .retrieve()
                .bodyToMono(String.class)
                .block();
    }

    private Character getRandomCharacter() {
        Random r = new Random();
        return (char) (r.nextInt(26) + 'a');
    }
}
