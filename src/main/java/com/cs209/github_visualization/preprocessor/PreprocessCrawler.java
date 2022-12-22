package com.cs209.github_visualization.preprocessor;

import cn.hutool.core.thread.ThreadUtil;

import java.util.List;
import java.util.Map;

public class PreprocessCrawler extends Crawler {
    PreprocessStatus preprocessStatus;
    boolean pausing = false;
    int pausedTaskIndex = 0;
    int pausedPage = 1;
    long pausedTime = 0;

    public PreprocessCrawler(String githubToken, PreprocessStatus status) {
        super(githubToken);
        this.preprocessStatus = status;
    }

    @Override
    public void downloadAll(String repoStr, int task, Map<String, Object> args) {
        for (int i = pausedPage; i < Integer.MAX_VALUE; i++) {
            if (!isFunctional() || pausing) return;
            preprocessStatus.taskPage = i;
            int code = switch (task) {
                case COMMIT_TASK -> downloadCommits(repoStr, i);
                case ISSUE_TASK -> downloadIssues(repoStr, i);
                case RELEASE_TASK -> downloadReleases(repoStr, i);
                case INTRO_TASK -> downloadIntro(repoStr, i);
                case ISSUE_COMMENTS_TASK -> downloadIssueComments(repoStr, (String) args.get("url"), i);
                default -> PASS;
            };
            // System.out.println(" --- " + repoStr + task + " " + i + " " + code);
            if (code == FINISHED) {
                pausedPage = 1;
                break;
            }
            if (code == LIMIT_EXCEED) {
                if (task == ISSUE_COMMENTS_TASK) return;
                pausedPage = Math.max(0, i);
                pausedTime = System.currentTimeMillis();
                pausing = true;
                preprocessStatus.pausing = true;
            }
            if (code == FAIL) {
                i = Math.max(0, i - 1);
            }
        }
    }

    public boolean temptRun() {
        if (System.currentTimeMillis() - pausedTime < 3600 * 1000) return false;
        pausing = false;
        preprocessStatus.pausing = false;
        for (int i = pausedTaskIndex; i < preprocessStatus.taskList.size(); i++) {
            preprocessStatus.taskIndex = i;
            int task = preprocessStatus.taskList.get(i);
            downloadAll(preprocessStatus.repoStr, task, Map.of());
            if (pausing) {
                pausedTaskIndex = i;
                return false;
            }
        }
        return true;
    }


}
