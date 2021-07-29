package dev.rjma;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Random;

@SpringBootApplication
@RestController
public class Application implements ApplicationRunner {

    private final ResourceLoader resourceLoader;

    private final ObjectMapper objectMapper;

    private List<String> words;

    public Application(ResourceLoader resourceLoader, ObjectMapper objectMapper) {
        this.resourceLoader = resourceLoader;
        this.objectMapper = objectMapper;
    }

    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }

    @GetMapping
    public String random() {
        return words.get(new Random().nextInt(words.size()));
    }

    @Override
    public void run(ApplicationArguments args) throws Exception {

        Resource resource = resourceLoader.getResource("classpath:/words.json");
        this.words = objectMapper.readValue(resource.getFile(), new TypeReference<>() {});
    }
}
