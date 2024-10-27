package com.example.testcontainer.post;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;

import static org.assertj.core.api.Assertions.assertThat;


@JsonTest
public class PostJsonTest {

    @Autowired
    private JacksonTester<Post> jacksonTester;

    @Test
    public void shouldSerializePost() throws Exception {

        Post post = new Post(1,1,"Hello, World!", "This is my first post.",null);
        String expected = """
                {
                    "id":%d,
                    "userId":%d,
                    "title":"%s",
                    "body":"%s",
                    "version": %s
                }
                """.formatted(post.id(), post.userId(), post.title(), post.body(), null);
        assertThat(jacksonTester.write(post)).isEqualToJson(expected);
    }

    @Test
    public void shouldDeserializePost() throws Exception {
        Post post = new Post(1,1,"Hello, World!", "This is my first post.",null);
        String content = """
                {
                    "id":%d,
                    "userId":%d,
                    "title":"%s",
                    "body":"%s",
                    "version": %s
                }
                """.formatted(post.id(), post.userId(), post.title(), post.body(), null);

        assertThat(jacksonTester.parse(content)).isEqualTo(post);
        assertThat(jacksonTester.parseObject(content).id()).isEqualTo(1);
        assertThat(jacksonTester.parseObject(content).userId()).isEqualTo(1);
        assertThat(jacksonTester.parseObject(content).title()).isEqualTo("Hello, World!");
        assertThat(jacksonTester.parseObject(content).body()).isEqualTo("This is my first post.");
    }
}
