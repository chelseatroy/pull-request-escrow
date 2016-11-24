package com.example.github;

import org.apache.commons.codec.digest.HmacUtils;
import org.apache.commons.io.IOUtils;
import org.kohsuke.github.GHEventPayload;
import org.kohsuke.github.GitHub;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.MethodParameter;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.ServletRequestBindingException;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.ModelAndViewContainer;

import javax.servlet.http.HttpServletRequest;
import java.io.StringReader;

@Component
public class GHEventPayloadPullRequestArgumentResolver implements HandlerMethodArgumentResolver {
    private static final String X_HUB_SIGNATURE = "X-HUB-SIGNATURE";

    private GitHub gitHub;
    private String secret;

    public GHEventPayloadPullRequestArgumentResolver(GitHub gitHub, @Value("${github-webhook-secret}") String secret) {
        this.gitHub = gitHub;
        this.secret = secret;
    }

    @Override
    public boolean supportsParameter(MethodParameter parameter) {
        return GHEventPayload.PullRequest.class.equals(parameter.getParameterType());
    }

    @Override
    public Object resolveArgument(MethodParameter parameter,
                                  ModelAndViewContainer mavContainer,
                                  NativeWebRequest webRequest,
                                  WebDataBinderFactory binderFactory) throws Exception {
        HttpServletRequest servletRequest = webRequest.getNativeRequest(HttpServletRequest.class);
        String body = IOUtils.toString(servletRequest.getInputStream());
        String hmacSha1Hex = HmacUtils.hmacSha1Hex(secret, body);
        String calculatedSignature = String.format("sha1=%s", hmacSha1Hex);
        String givenSignature = webRequest.getHeader(X_HUB_SIGNATURE);
        if (!calculatedSignature.equals(givenSignature)) {
            throw new ServletRequestBindingException("Invalid X-Hub-Signature");
        }
        return gitHub.parseEventPayload(new StringReader(body), GHEventPayload.PullRequest.class);
    }
}
