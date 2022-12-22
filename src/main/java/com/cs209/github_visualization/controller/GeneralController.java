package com.cs209.github_visualization.controller;
import com.cs209.github_visualization.model.Repo;
import com.cs209.github_visualization.preprocessor.PreprocessTask;
import com.cs209.github_visualization.preprocessor.Preprocessor;
import com.cs209.github_visualization.service.IntroService;
import com.cs209.github_visualization.service.RepoService;
import com.cs209.github_visualization.utils.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/repo")
public class GeneralController {
    @Autowired
    Preprocessor preprocessor;
    @Autowired
    RepoService repoService;
    @Autowired
    IntroService introService;


    @CrossOrigin
    @GetMapping("/{owner}/{repo}/general/code")
    public Result<?> generalExist(@PathVariable String owner, @PathVariable String repo) {
        if (repoService.isEmpty(owner, repo)) {
            return Result.repoEmpty();
        } else {
            return Result.success();
        }
    }

    @CrossOrigin
    @GetMapping("/{owner}/{repo}/general/status")
    public Result<?> generalStatus(@PathVariable String owner, @PathVariable String repo) {
        HashMap<String, Object> data = new HashMap<>();
        int fullCode = preprocessor.generalStatus(owner, repo);
        data.put("status", fullCode);

        long lastUpdated = -1;
        Repo repoObj = repoService.getRepo(owner, repo);
        if (repoObj != null) {
            lastUpdated = repoObj.getLast_updated();
        }
        data.put("last_updated", lastUpdated);

        return Result.success(data);
    }

    @CrossOrigin
    @GetMapping("/{owner}/{repo}/general/introduction")
    public Result<?> generalIntroduction(@PathVariable String owner, @PathVariable String repo) {
        if (repoService.isEmpty(owner, repo)) {
            return Result.repoEmpty();
        }

        String intro = introService.getIntro(owner, repo);
        return Result.success(intro);
    }
}
