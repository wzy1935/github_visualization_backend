package com.cs209.github_visualization.controller;

import com.cs209.github_visualization.service.IssueService;
import com.cs209.github_visualization.service.RepoService;
import com.cs209.github_visualization.utils.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/repo")
public class IssueController {
    @Autowired
    RepoService repoService;
    @Autowired
    IssueService issueService;

    @CrossOrigin
    @GetMapping("/{owner}/{repo}/issues/wordcloud")
    public Result<?> issueWordCloud(@PathVariable String owner, @PathVariable String repo, int limit) {
        if (repoService.isEmpty(owner, repo)) {
            return Result.repoEmpty();
        }
        List<IssueService.KeyWord> keyWord = issueService.topKeywords(owner, repo, limit);
        return Result.success(keyWord);
    }

    @CrossOrigin
    @GetMapping("/{owner}/{repo}/issues/amount")
    public Result<?> getIssuesNum(@PathVariable("owner") String owner, @PathVariable("repo") String repo) {
        if (repoService.isEmpty(owner, repo)) {
            return Result.repoEmpty();
        }
        return Result.success(issueService.getIssuesNum(owner, repo));
    }


    @CrossOrigin
    @GetMapping("/{owner}/{repo}/issues/duration")
    public Result<?> getIssueDuration(@PathVariable("owner") String owner, @PathVariable("repo") String repo) {
        if (repoService.isEmpty(owner, repo)) {
            return Result.repoEmpty();
        }
        return Result.success(issueService.getIssueDuration(owner, repo));
    }



    @CrossOrigin
    @GetMapping("/{owner}/{repo}/issues/distribution")
    public Result<?> getDistribution(@PathVariable("owner") String owner, @PathVariable("repo") String repo,
                                     @RequestParam("from") Long from, @RequestParam("to") Long to,
                                     @RequestParam("datapoint") Integer datapoint) {
        if (repoService.isEmpty(owner, repo)) {
            return Result.repoEmpty();
        }
        return Result.success(issueService.getDistribution(owner, repo, from, to, datapoint));
    }

}
