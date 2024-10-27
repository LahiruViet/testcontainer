package com.example.testcontainer.post;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.io.InputStream;

@Component
@RequiredArgsConstructor
@Slf4j
class PostDataLoader implements CommandLineRunner {

    private final ObjectMapper objectMapper;
    private final PostRepository postRepository;

    @Override
    public void run(String... args) throws Exception {
        if(postRepository.count() == 0){
            String POSTS_JSON = "/data/posts.json";
            log.info("Loading posts into database from JSON: {}", POSTS_JSON);
            try (InputStream inputStream = TypeReference.class.getResourceAsStream(POSTS_JSON)) {
                Posts response = objectMapper.readValue(inputStream, Posts.class);
                postRepository.saveAll(response.posts());
            } catch (IOException e) {
                throw new RuntimeException("Failed to read JSON data", e);
            }
        }
    }

}
