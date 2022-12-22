package com.cs209.github_visualization.controller;

import com.cs209.github_visualization.service.IssueService;
import com.cs209.github_visualization.service.RepoService;
import com.cs209.github_visualization.utils.Result;
import com.cs209.github_visualization.utils.WordCloudUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.imageio.ImageIO;
import javax.servlet.http.HttpServletResponse;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
    @GetMapping("/{owner}/{repo}/issues/wordcloud_pic/{limit}/{width}/{height}")
    public void issueWordCloudPic(@PathVariable String owner, @PathVariable String repo,
                                  @PathVariable  int limit, @PathVariable int width, @PathVariable int height,
                                  HttpServletResponse response) {
        width = Math.min(Math.max(0, width), 1000);
        height = Math.min(Math.max(0, height), 1000);
        response.setContentType("image/jpeg");
        if (repoService.isEmpty(owner, repo)) {
            return;
        }
        List<IssueService.KeyWord> keyWord = issueService.topKeywords(owner, repo, limit);
        Map<String, Integer> map = new HashMap<>();
        keyWord.forEach(item -> map.put(item.getLabel(), item.getWeight()));
        BufferedImage image = WordCloudUtil.generatePic(map, width, height);
        try {
            ImageIO.write(image, "PNG", response.getOutputStream());
        } catch (IOException e) {
            System.out.println(e.getMessage());
        }
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
