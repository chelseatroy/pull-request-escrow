package com.example.pullrequest;

import org.kohsuke.github.GHEventPayload;
import org.springframework.stereotype.Service;

@Service
public class PullRequestService {
    public void doPullRequest(GHEventPayload.PullRequest pullRequest) {
        // TODO: 11/24/16 Send escrow $ to pull request submitter
    }
}
