package com.example.testcontainer.post;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.skyscreamer.jsonassert.JSONAssert;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@WebMvcTest(PostController.class)
class PostControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private PostRepository repository;

    private List<Post> posts = new ArrayList<>();

    @BeforeEach
    public void setUp() {
        posts = List.of(
                new Post(1,1,"Hello, World!", "This is my first post.",null),
                new Post(2,1,"Second Post", "This is my second post.",null)
        );
    }

    @Test
    public void shouldFindAllPosts() throws Exception {
        String jsonResponse = """
                [
                    {
                        "id":1,
                        "userId":1,
                        "title":"Hello, World!",
                        "body":"This is my first post.",
                        "version": null
                    },
                    {
                        "id":2,
                        "userId":1,
                        "title":"Second Post",
                        "body":"This is my second post.",
                        "version": null
                    }
                ]
                """;

        when(repository.findAll()).thenReturn(posts);

        ResultActions resultActions = mockMvc.perform(get("/api/v1/posts"))
                .andExpect(status().isOk())
                .andExpect(content().json(jsonResponse));

        JSONAssert.assertEquals(jsonResponse, resultActions.andReturn().getResponse().getContentAsString(), false);
    }

    @Test
    public void shouldFindPostWhenGivenValidId() throws Exception {

        Post post = new Post(1,1,"Test Title", "Test Body",null);
        when(repository.findById(1)).thenReturn(Optional.of(post));

        String json = """
                {
                    "id": %d,
                    "userId": %d,
                    "title":"%s",
                    "body":"%s",
                    "version": %s
                }
                """.formatted(post.id(), post.userId(), post.title(), post.body(), null);

        mockMvc.perform(get("/api/v1/posts/1"))
                .andExpect(status().isOk())
                .andExpect(content().json(json));
    }

    @Test
    public void shouldCreateNewPostWhenGivenValidId() throws Exception {

        Post post = new Post(3,1,"This is my brand new post", "TEST BODY",null);
        when(repository.save(post)).thenReturn(post);

        String json = """
                {
                    "id": %d,
                    "userId": %d,
                    "title":"%s",
                    "body":"%s",
                    "version": null
                }
                """.formatted(post.id(), post.userId(), post.title(), post.body());

        mockMvc.perform(post("/api/v1/posts")
                        .contentType("application/json")
                        .content(json))
                .andExpect(status().isCreated())
                .andExpect(content().json(json));
    }

    @Test
    public void shouldUpdatePostWhenGivenValidPost() throws Exception {

        Post updated = new Post(1,1,"This is my brand new post", "UPDATED BODY",1);
        when(repository.findById(1)).thenReturn(Optional.of(posts.getFirst()));
        when(repository.save(updated)).thenReturn(updated);

        String requestBody = """
                {
                    "id": %d,
                    "userId": %d,
                    "title":"%s",
                    "body":"%s",
                    "version": null
                }
                """.formatted(updated.id(), updated.userId(), updated.title(), updated.body());

        mockMvc.perform(put("/api/v1/posts/1")
                        .contentType("application/json")
                        .content(requestBody))
                .andExpect(status().isOk());
    }

    @Test
    public void shouldNotUpdateAndThrowNotFoundWhenGivenAnInvalidPostId() throws Exception {

        Post updated = new Post(50,1,"This is my brand new post", "UPDATED BODY",1);
        when(repository.save(updated)).thenReturn(updated);

        String json = """
                {
                    "id": %d,
                    "userId": %d,
                    "title":"%s",
                    "body":"%s",
                    "version": %s
                }
                """.formatted(updated.id(), updated.userId(), updated.title(), updated.body(), updated.version());

        mockMvc.perform(put("/api/v1/posts/999")
                        .contentType("application/json")
                        .content(json))
                .andExpect(status().isNotFound());
    }

    @Test
    public void shouldDeletePostWhenGivenValidId() throws Exception {

        doNothing().when(repository).deleteById(1);

        mockMvc.perform(delete("/api/v1/posts/1"))
                .andExpect(status().isNoContent());

        verify(repository, times(1)).deleteById(1);
    }
}
