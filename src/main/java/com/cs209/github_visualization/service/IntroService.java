package com.cs209.github_visualization.service;

import com.cs209.github_visualization.mapper.IntroMapper;
import com.cs209.github_visualization.model.Intro;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class IntroService {
    @Autowired
    IntroMapper introMapper;

    public String getIntro(String owner, String repo) {
        Intro intro = introMapper.selectLatest(owner, repo);
        if (intro == null) return null;
        return intro.getIntro();
    }
}
