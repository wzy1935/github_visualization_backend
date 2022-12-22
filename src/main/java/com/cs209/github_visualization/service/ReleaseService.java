package com.cs209.github_visualization.service;

import com.cs209.github_visualization.POJO.ReleaseCommitWrapper;
import com.cs209.github_visualization.POJO.ReleaseJS;
import com.cs209.github_visualization.mapper.CommitMapper;
import com.cs209.github_visualization.mapper.ReleaseMapper;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;

@Service
public class ReleaseService {
    @Resource
    ReleaseMapper releaseMapper;

    @Resource
    CommitMapper commitMapper;


    private ReleaseCommitWrapper mapCommitCount(String owner, String repo, Long from, Long end, ReleaseJS release) {
        var commits = commitMapper.getCountBetween(owner, repo, from, end);
        return new ReleaseCommitWrapper(release.getName(), end, commits);
    }

    public List<ReleaseCommitWrapper> getReleaseCommitAmount(String owner, String repo, Integer limit) {
        var releases = releaseMapper.getAll(owner, repo, limit);
        long to = System.currentTimeMillis();
        List<ReleaseCommitWrapper> res = new ArrayList<>();
        for (var release : releases) {
            res.add(mapCommitCount(owner, repo, release.getPublished_at(), to, release));
            to = release.getPublished_at();
        }
        return res;
    }

    public Integer getCount(String owner, String repo) {
        return releaseMapper.getCount(owner, repo);
    }
}
