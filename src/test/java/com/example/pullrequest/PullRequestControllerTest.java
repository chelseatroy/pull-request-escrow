package com.example.pullrequest;

import org.apache.commons.io.IOUtils;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kohsuke.github.GHEventPayload;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.core.io.Resource;
import org.springframework.http.*;
import org.springframework.test.context.junit4.SpringRunner;

import java.io.IOException;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class PullRequestControllerTest {
    @Autowired
    TestRestTemplate testRestTemplate;

    @MockBean
    PullRequestService pullRequestService;

    @Captor
    ArgumentCaptor<GHEventPayload.PullRequest> pullRequestArgumentCaptor;

    @Value("classpath:com/example/pullrequest/pull-request-opened.json")
    Resource resource;

    @Test
    public void doPost() throws IOException {
        String body = IOUtils.toString(resource.getInputStream());
        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.set("X-HUB-SIGNATURE", "sha1=bcb8d9e26af15f22982bd59769c4e5db1ad76936");
        HttpEntity<String> httpEntity = new HttpEntity<>(body, httpHeaders);

        ResponseEntity<Void> responseEntity =
                testRestTemplate.postForEntity(
                        "/pull-requests",
                        httpEntity,
                        Void.class
                );

        assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.OK);
        verify(pullRequestService).doPullRequest(pullRequestArgumentCaptor.capture());
        assertThat(pullRequestArgumentCaptor.getValue().getAction()).isEqualTo("opened");
    }

    @Test
    public void doPost_withInvalidSignatureHeader_returnsBadRequest() throws IOException {
        String body = IOUtils.toString(resource.getInputStream());
        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.set("X-HUB-SIGNATURE", "sha1=invalid-signature");
        HttpEntity<String> httpEntity = new HttpEntity<>(body, httpHeaders);

        ResponseEntity<Map<String, Object>> responseEntity =
                testRestTemplate.exchange(
                        "/pull-requests",
                        HttpMethod.POST,
                        httpEntity,
                        new ParameterizedTypeReference<Map<String, Object>>() {
                        }
                );

        assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        assertThat(responseEntity.getBody().get("message")).isEqualTo("Invalid X-Hub-Signature");
    }
}