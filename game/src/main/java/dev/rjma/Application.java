package dev.rjma;

import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.Tags;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.server.ResponseStatusException;

import java.util.*;
import java.util.stream.Collectors;

@SpringBootApplication
@RestController
@RequestMapping("games")
public class Application {

    private final WebClient webClient;

    private final Map<String, Game> games = new HashMap<>();

    private final Counter correctGuessesCounter;

    private final Counter incorrectGuessesCounter;

    private final Counter wonGamesCounter;

    private final Counter lostGamesCounter;

    public Application(WebClient.Builder webClientBuilder, MeterRegistry meterRegistry) {
        this.webClient = webClientBuilder.build();
        meterRegistry.gauge("game.games.total", Tags.empty(), games, Map::size);
        this.correctGuessesCounter = meterRegistry.counter("game.guesses.correct.total");
        this.incorrectGuessesCounter = meterRegistry.counter("game.guesses.incorrect.total");
        this.wonGamesCounter = meterRegistry.counter("game.games.won.total");
        this.lostGamesCounter = meterRegistry.counter("game.games.lost.total");
    }

    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }

    public static class Game {
        String word;

        int remainingGuesses = 6;

        Set<Character> correctGuesses = new HashSet<>();
    }

    @PostMapping
    public String create() {

        Game game = new Game();
        game.word = getRandomWord();

        String gameId = UUID.randomUUID().toString();
        games.put(gameId, game);
        return gameId;
    }

    @PostMapping("{gameId}")
    public String play(@PathVariable String gameId, @RequestBody Character guess) {

        Game game = games.get(gameId);

        if (game == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND);
        }

        if (game.word.indexOf(guess) == -1) {
            game.remainingGuesses--;
            incorrectGuessesCounter.increment();
        } else {
            game.correctGuesses.add(guess);
            correctGuessesCounter.increment();
        }

        if (game.remainingGuesses < 1) {
            games.remove(gameId);
            lostGamesCounter.increment();
            return "You lose! Word: " + game.word;
        }

        String maskedWord = Arrays.stream(game.word.split(""))
                .map(c -> game.correctGuesses.contains(c.charAt(0)) ? c : "*")
                .collect(Collectors.joining(""));

        if (maskedWord.equals(game.word)) {
            games.remove(gameId);
            wonGamesCounter.increment();
            return "You win! Word: " + game.word;
        } else {
            return "Remaining guesses: " + game.remainingGuesses + ". Word " + maskedWord;
        }
    }

    private String getRandomWord() {
        return webClient.get()
                .uri("words:8080")
                .retrieve()
                .bodyToMono(String.class)
                .block();
    }

}
