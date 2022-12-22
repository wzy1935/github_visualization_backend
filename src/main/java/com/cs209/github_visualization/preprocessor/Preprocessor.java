package com.cs209.github_visualization.preprocessor;

import cn.hutool.core.thread.ThreadUtil;
import com.cs209.github_visualization.mapper.*;
import com.cs209.github_visualization.model.Repo;
import com.cs209.github_visualization.utils.GithubUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import java.util.stream.Collectors;

@Service
public class Preprocessor {
    @Autowired
    CommitMapper commitMapper;
    @Autowired
    IssueMapper issueMapper;
    @Autowired
    ReleaseMapper releaseMapper;
    @Autowired
    RepoMapper repoMapper;
    @Autowired
    WordCloudMapper wordCloudMapper;
    @Autowired
    IntroMapper introMapper;

    CopyOnWriteArrayList<PreprocessTask> tasks = new CopyOnWriteArrayList<>();
    Lock lock = new ReentrantLock();
    int current = 0;

    public static final int NORMAL = 0;
    public static final int OUTDATED = 1;
    public static final int COLLECTING = 2;
    public static final int QUEUING = 3;
    public static final int EMPTY = 4;
    public static final int INVALID = -1;

    public Preprocessor() {
        new Thread(this::start).start();
    }

    private void start() {
        while (true) {
            if (current < tasks.size()) {
                PreprocessTask task = tasks.get(current);
                System.out.println("run task" + current);
                boolean finished = task.run();
                if (finished) tasks.remove(task);
                current = tasks.size() == 0 ? 0 : (current + (finished ? 0 : 1)) % Math.min(tasks.size(), 9);
            }
            ThreadUtil.sleep(1000);
        }
    }

    public List<PreprocessStatus> getQueueStatus() {
        return tasks.stream().map(task -> task.status).toList();
    }

    public int submit(PreprocessTask task) {
        if (tasks.size() > 100) return -2; // queue denied
        int status = generalStatus(task.getOwner(), task.getRepo());
        if (status == EMPTY || status == OUTDATED) {
            if (!GithubUtil.verifyToken(task.getGithubToken())) return -3; // unauthorized

            lock.lock();
            for (PreprocessTask taskAgain : tasks) {
                if (taskAgain.repo.equals(task.repo) && taskAgain.owner.equals(task.owner)) return 1; // waiting
            }
            task.setMappers(commitMapper, issueMapper, releaseMapper, repoMapper, wordCloudMapper, introMapper);
            tasks.add(task);
            lock.unlock();
            return 0; // success
        }
        if (status == QUEUING || status == COLLECTING) return 1; // waiting
        if (status == NORMAL) return 2; // no need to collect

        return -1; // no repo
    }

    public int generalStatus(String owner, String repo) {
        if (tasks.size() > 0 && tasks.get(0).repo.equals(repo) && tasks.get(0).owner.equals(owner)) return COLLECTING;
        for (PreprocessTask task : tasks) {
            if (task.repo.equals(repo) && task.owner.equals(owner)) {
                if (task.status.queuing || task.status.pausing) return QUEUING;
                return COLLECTING;
            }
        }
        Repo r = repoMapper.selectOne(owner, repo);
        if (r == null) {
            if (!GithubUtil.repoExist(owner, repo)) return INVALID;
            return EMPTY;
        }
        if (System.currentTimeMillis() - r.getLast_updated() < 1000 * 3600 * 24) return NORMAL;

        return OUTDATED;
    }
}
