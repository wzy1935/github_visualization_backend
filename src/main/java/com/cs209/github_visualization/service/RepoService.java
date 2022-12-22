package com.cs209.github_visualization.service;

import com.cs209.github_visualization.mapper.RepoMapper;
import com.cs209.github_visualization.model.Repo;
import com.cs209.github_visualization.utils.GithubUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class RepoService {
    @Autowired
    RepoMapper repoMapper;

    public static final int NORMAL = 0;
    public static final int EMPTY = 1;
    public static final int INVALID = -1;

    public boolean isEmpty(String owner, String repo) {
        return repoMapper.selectOne(owner, repo) == null;
    }

    public Repo getRepo(String owner, String repo) {
        return repoMapper.selectOne(owner, repo);
    }
}
