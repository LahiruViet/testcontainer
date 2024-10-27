package com.example.testcontainer.post;

import com.example.testcontainer.ApplicationConfiguration;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.context.annotation.Import;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.annotation.Rollback;
import org.springframework.transaction.annotation.Transactional;
import org.testcontainers.containers.PostgreSQLContainer;
import java.util.Objects;

import static org.assertj.core.api.Assertions.assertThat;


@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Transactional
@Import(ApplicationConfiguration.class)
class PostControllerIntegrationTest {

    @Autowired
    private PostgreSQLContainer<?> postgres;

    @Autowired
    private TestRestTemplate restTemplate;

    @Test
    public void connectionEstablished() {
        assertThat(postgres.isCreated()).isTrue();
        assertThat(postgres.isRunning()).isTrue();
    }

    @Test
    public void shouldFindAllPosts() {
        Post[] posts = restTemplate.getForObject("/api/v1/posts", Post[].class);
        assertThat(posts.length).isGreaterThan(100);
    }

    @Test
    public void shouldFindPostWhenValidPostID() {
        ResponseEntity<Post> response = restTemplate.exchange("/api/v1/posts/1", HttpMethod.GET, null, Post.class);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotNull();
    }

    @Test
    public void shouldThrowNotFoundWhenInvalidPostID() {
        ResponseEntity<Post> response = restTemplate.exchange("/api/v1/posts/999", HttpMethod.GET, null, Post.class);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
    }

    @Test
    @Rollback
    public void shouldCreateNewPostWhenPostIsValid() {
        Post post = new Post(101,1,"101 Title","101 Body",null);

        ResponseEntity<Post> response = restTemplate.exchange("/api/v1/posts", HttpMethod.POST, new HttpEntity<Post>(post), Post.class);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        assertThat(response.getBody()).isNotNull();
        assertThat(Objects.requireNonNull(response.getBody()).id()).isEqualTo(101);
        assertThat(response.getBody().userId()).isEqualTo(1);
        assertThat(response.getBody().title()).isEqualTo("101 Title");
        assertThat(response.getBody().body()).isEqualTo("101 Body");
    }

    @Test
    public void shouldNotCreateNewPostWhenValidationFails() {
        Post post = new Post(101,1,"","",null);
        ResponseEntity<Post> response = restTemplate.exchange("/api/v1/posts", HttpMethod.POST, new HttpEntity<Post>(post), Post.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
    }

    @Test
    @Rollback
    public void shouldUpdatePostWhenPostIsValid() {

        Post post = new Post(null, null,"NEW POST TITLE #1", "NEW POST BODY #1", null);
        ResponseEntity<Post> response = restTemplate.exchange("/api/v1/posts/99", HttpMethod.PUT, new HttpEntity<Post>(post), Post.class);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);

        Post updated = response.getBody();
        assertThat(updated).isNotNull();

        assertThat(updated.id()).isEqualTo(99);
        assertThat(updated.userId()).isEqualTo(10);
        assertThat(updated.title()).isEqualTo("NEW POST TITLE #1");
        assertThat(updated.body()).isEqualTo("NEW POST BODY #1");
    }

    @Test
    @Rollback
    public void shouldDeleteWithValidID() {
        ResponseEntity<Void> response = restTemplate.exchange("/api/v1/posts/88", HttpMethod.DELETE, null, Void.class);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NO_CONTENT);
    }
}
