package com.cs209.github_visualization.controller;

import com.cs209.github_visualization.preprocessor.PreprocessTask;
import com.cs209.github_visualization.preprocessor.Preprocessor;
import com.cs209.github_visualization.utils.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api")
public class SubmitController {
    @Autowired
    Preprocessor preprocessor;
    @Value("${github_visualization.fast_crawler}")
    boolean fastCrawler;
    @Value("${github_visualization.chinese_tokenizer}")
    boolean chineseTokenizer;

    @CrossOrigin
    @PostMapping("submit/repo/{owner}/{repo}")
    public Result<?> submitTask(@RequestHeader(value = "github_token", required = false) String githubToken,
                                @PathVariable String owner, @PathVariable String repo) {
        int result = preprocessor.submit(new PreprocessTask(githubToken, owner, repo, fastCrawler, chineseTokenizer));
        return Result.success(Map.of("result", result, "message", switch (result) {
            case -3 -> "unauthorized";
            case -2 -> "queue denied";
            case -1 -> "no repo";
            case 0 -> "success";
            case 1 -> "waiting";
            case 2 -> "no need to collect";
            default -> "unknown";
        }));
    }

    @CrossOrigin
    @GetMapping("/submit/queue")
    public  Result<?> queueStatus() {
        return Result.success(preprocessor.getQueueStatus());
    }
}
