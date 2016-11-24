package com.example.pullrequest;

import org.kohsuke.github.GHEventPayload;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
public class PullRequestController {
    private PullRequestService pullRequestService;

    public PullRequestController(PullRequestService pullRequestService) {
        this.pullRequestService = pullRequestService;
    }

    @PostMapping("/pull-requests")
    public void doPost(GHEventPayload.PullRequest pullRequest) {
        pullRequestService.doPullRequest(pullRequest);
    }
}
