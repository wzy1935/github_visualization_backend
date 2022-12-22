package com.cs209.github_visualization.controller;

import com.cs209.github_visualization.service.CommitService;
import com.cs209.github_visualization.service.ReleaseService;
import com.cs209.github_visualization.service.RepoService;
import com.cs209.github_visualization.utils.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/repo")
public class ReleaseController {
    @Autowired
    ReleaseService releaseService;
    @Autowired
    RepoService repoService;

    @CrossOrigin
    @GetMapping("/{owner}/{repo}/releases/amount")
    public Result<?> getReleasesAmount(@PathVariable("owner") String owner,
                                       @PathVariable("repo") String repo) {
        if (repoService.isEmpty(owner, repo)) {
            return Result.repoEmpty();
        }
        return Result.success(Map.of("amount", releaseService.getCount(owner, repo)));
    }



    @CrossOrigin
    @GetMapping("/{owner}/{repo}/releases/commits")
    public Result<?> getReleaseCommitAmount(@PathVariable("owner") String owner,
                                            @PathVariable("repo") String repo,
                                            @RequestParam("limit") Integer limit) {
        if (repoService.isEmpty(owner, repo)) {
            return Result.repoEmpty();
        }
        return Result.success(releaseService.getReleaseCommitAmount(owner, repo, limit));
    }


}
