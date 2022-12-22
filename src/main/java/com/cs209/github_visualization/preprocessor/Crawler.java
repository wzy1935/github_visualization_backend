package com.cs209.github_visualization.preprocessor;

import cn.hutool.core.thread.ThreadUtil;
import cn.hutool.http.HttpRequest;
import cn.hutool.http.HttpResponse;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

public class Crawler {
    String githubToken;
    Consumer<Result> resultCallback = result -> {};
    boolean functional = true;

    public static int PARTLY = 0;
    public static int LIMIT_EXCEED = -1;
    public static int FAIL = -2;
    public static int PASS = 2;
    public static int FINISHED = 1;

    public static final int COMMIT_TASK = 0;
    public static final int ISSUE_TASK = 1;
    public static final int RELEASE_TASK = 2;
    public static final int INTRO_TASK = 3;
    public static final int ISSUE_COMMENTS_TASK = 4;

    public Crawler(String githubToken) {
        this.githubToken = githubToken;
    }

    public void stop() {
        this.functional = false;
    }

    public boolean isFunctional() {
        return functional;
    }

    public void setResultCallback(Consumer<Result> resultCallback) {
        this.resultCallback = resultCallback;
    }

    private boolean emptyResult(String result) {
        if (result.length() < 300) {
            String r = result.replaceAll("\\s", "");
            if (r.equals("[]") || r.equals("{}")) return true;
            try {
                JSONObject o = JSONUtil.parseObj(result);
                if ("Git Repository is empty.".equals(o.getStr("message"))) return true;
            } catch (Exception e) {
                return false;
            }
        }
        return false;
    }

    public int downloadPage(int type, String url, String repoStr, Map<String, List<String>> headers, Map<String, Object> params) {
        int page = params.get("page") == null ? 1 : ((int) params.get("page"));
        try (HttpResponse resp = HttpRequest.get(url).header(headers).form(params).timeout(5000).execute()) {
            if (emptyResult(resp.body())) return FINISHED;
            if (resp.getStatus() == 403) return LIMIT_EXCEED;
            resultCallback.accept(new Result(type, repoStr.split("/")[0], repoStr.split("/")[1], page, resp.body()));
        } catch (Exception e) {
            System.out.println(e.getMessage());
            return FAIL;
        }
        return PARTLY;
    }

    public int downloadCommits(String repoStr, int page) {
        return downloadPage(
                COMMIT_TASK,
                "https://api.github.com/repos/" + repoStr + "/commits",
                repoStr,
                Map.of("Authorization", List.of("Bearer " + githubToken)),
                Map.of("until", "2022-12-29T00:00:00Z", "per_page", 100, "page", page)
        );
    }

    public int downloadIssues(String repoStr, int page) {
        return downloadPage(
                ISSUE_TASK,
                "https://api.github.com/repos/" + repoStr + "/issues",
                repoStr,
                Map.of("Authorization", List.of("Bearer " + githubToken)),
//                Map.of("state", "all", "sort", "created", "direction", "asc", "per_page", 100, "page", page)
                Map.of("state", "all", "per_page", 100, "page", page)
        );
    }


    public int downloadReleases(String repoStr, int page) {
        return downloadPage(
                RELEASE_TASK,
                "https://api.github.com/repos/" + repoStr + "/releases",
                repoStr,
                Map.of("Authorization", List.of("Bearer " + githubToken)),
                Map.of("per_page", 100, "page", page)
        );
    }

    public int downloadIntro(String repoStr, int page) {
        if (page > 1) return FINISHED;
        return downloadPage(
               INTRO_TASK,
               "https://api.github.com/repos/" + repoStr,
                repoStr,
                Map.of("Authorization", List.of("Bearer " + githubToken)),
                Map.of()
        );
    }

    public int downloadIssueComments(String repoStr, String url, int page) {
        if (page > 1) return FINISHED;
        return downloadPage(
                ISSUE_COMMENTS_TASK,
                url,
                repoStr,
                Map.of("Authorization", List.of("Bearer " + githubToken)),
                Map.of("per_page", 100, "page", page)
        );
    }

    public void downloadAll(String repoStr, int task, Map<String, Object> args) {
        System.out.println(repoStr + " - " + task);
        for (int i = 1; i < Integer.MAX_VALUE; i++) {
            if (!isFunctional()) return;
            System.out.println("turn " + i);
            int code = switch (task) {
                case COMMIT_TASK -> downloadCommits(repoStr, i);
                case ISSUE_TASK -> downloadIssues(repoStr, i);
                case RELEASE_TASK -> downloadReleases(repoStr, i);
                case INTRO_TASK -> downloadIntro(repoStr, i);
                case ISSUE_COMMENTS_TASK -> downloadIssueComments(repoStr, (String) args.get("url"), i);
                default -> PASS;
            };
            if (code == FINISHED) break;
            if (code == LIMIT_EXCEED) {
                ThreadUtil.sleep(3600 * 1000);
                i = Math.max(0, i - 1);
            }
            if (code == FAIL) {
                i = Math.max(0, i - 1);
            }
        }
    }

    public void downloadAllComplete(List<String> repoStrList, List<Integer> taskList) {
        for (int task : taskList) {
            for (String repoStr : repoStrList) {
                downloadAll(repoStr, task, null);
            }
        }
    }

    public static class Result {
        public int type;
        public String owner;
        public String repo;
        public int page;
        public String result;

        public Result(int type, String owner, String repo, int page, String result) {
            this.page = page;
            this.type = type;
            this.result = result;
            this.owner = owner;
            this.repo = repo;
        }
    }

}
