package com.cs209.github_visualization.controller;

import com.cs209.github_visualization.service.CommitService;
import com.cs209.github_visualization.service.RepoService;
import com.cs209.github_visualization.utils.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/repo")
public class DeveloperController {
    @Autowired
    CommitService commitService;
    @Autowired
    RepoService repoService;

    @CrossOrigin
    @GetMapping("/{owner}/{repo}/developers/amount")
    public Result<?> geDeveloperNum(@PathVariable("owner") String owner, @PathVariable("repo") String repo) {
        if (repoService.isEmpty(owner, repo)) {
            return Result.repoEmpty();
        }
        return Result.success(Map.of(
                "amount", commitService.getDeveloperCount(owner, repo)
        ));
    }

    @CrossOrigin
    @GetMapping("/{owner}/{repo}/developers/rank")
    public Result<?> getCommitRank(@PathVariable("owner") String owner,
                                   @PathVariable("repo") String repo,
                                   @RequestParam("limit") Integer limit) {
        if (repoService.isEmpty(owner, repo)) {
            return Result.repoEmpty();
        }
       return Result.success(commitService.getCommitRank(owner, repo, limit));
    }

    @CrossOrigin
    @GetMapping("/{owner}/{repo}/commits/distribution")
    public Result<?> getDistribution(@PathVariable("owner") String owner, @PathVariable("repo") String repo){
        if (repoService.isEmpty(owner, repo)) {
            return Result.repoEmpty();
        }
        return Result.success(commitService.getDistribution(owner, repo));
    }

    @CrossOrigin
    @GetMapping("/{owner}/{repo}/commits/amount")
    public Result<?> getAmount(@PathVariable("owner") String owner, @PathVariable("repo") String repo){
        if (repoService.isEmpty(owner, repo)) {
            return Result.repoEmpty();
        }
        return Result.success(Map.of(
                "amount", commitService.getCount(owner, repo)
        ));
    }


}
