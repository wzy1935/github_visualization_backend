package com.cs209.github_visualization.controller;

import com.cs209.github_visualization.utils.Result;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@RestController
@RequestMapping("/api")
public class AuthController {
    @Value("${github_visualization.client_secret}")
    String clientSecret;

    @Value("${github_visualization.client_id}")
    String clientId;

    @CrossOrigin
    @GetMapping("/oauth/code")
    public Result<String> sendClientId() {
        return Result.success(clientId);
    }

    @CrossOrigin
    @GetMapping("/oauth/redirect")
    public void getCode(HttpServletResponse resp, String code) throws IOException {
        // fetch bearer token
        RestTemplate template = new RestTemplate();
        String tokenResp = template.getForObject(
                "https://github.com/login/oauth/access_token?" +
                        "client_id={clientID}" +
                        "&client_secret={clientSecret}" +
                        "&code={requestToken}"
                , String.class,
                clientId, clientSecret, code);

        // operate resp string
        assert tokenResp != null;
        String[] tokenRespList = tokenResp.split("&");
        String token = null;
        for (String s : tokenRespList) {
            if (s.startsWith("access_token")) {
                token = s.split("=")[1];
                break;
            }
        }

        // store token into cookie
        Cookie cookie = new Cookie("github_token", token);
        cookie.setPath("/");
        resp.addCookie(cookie);
        resp.sendRedirect("/");
    }
}
