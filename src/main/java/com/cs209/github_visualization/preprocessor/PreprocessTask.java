package com.cs209.github_visualization.preprocessor;

import cn.hutool.json.JSONArray;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import com.cs209.github_visualization.mapper.*;
import com.cs209.github_visualization.model.*;
import com.cs209.github_visualization.utils.WordCloudUtil;
import lombok.Getter;
import lombok.Setter;

import java.util.*;

@Getter
@Setter
public class PreprocessTask {
    CommitMapper commitMapper;
    IssueMapper issueMapper;
    ReleaseMapper releaseMapper;
    RepoMapper repoMapper;
    WordCloudMapper wordCloudMapper;
    IntroMapper introMapper;


    String githubToken;
    String owner;
    String repo;
    Long startTime = null;
    PreprocessCrawler crawler;
    PreprocessStatus status;

    boolean fastCrawler;
    boolean chineseTokenizer;

    LinkedHashMap<String, Integer> wordFrequency = new LinkedHashMap<>();
    List<String> textTmp = new ArrayList<>();

    public PreprocessTask(String githubToken, String owner, String repo, boolean fastCrawler, boolean chineseTokenizer) {
        this.githubToken = githubToken;
        this.owner = owner;
        this.repo = repo;
        this.fastCrawler = fastCrawler;
        this.chineseTokenizer = chineseTokenizer;
        status = new PreprocessStatus(owner + "/" + repo, List.of(
                Crawler.COMMIT_TASK,
                Crawler.ISSUE_TASK,
                Crawler.RELEASE_TASK,
                Crawler.INTRO_TASK
        ));
        crawler = new PreprocessCrawler(githubToken, status);
        crawler.setResultCallback(this::crawlerCallback);
    }

    public void setMappers(CommitMapper commitMapper, IssueMapper issueMapper, ReleaseMapper releaseMapper,
                           RepoMapper repoMapper, WordCloudMapper wordCloudMapper,
                           IntroMapper introMapper) {
        this.commitMapper = commitMapper;
        this.issueMapper = issueMapper;
        this.releaseMapper = releaseMapper;
        this.repoMapper = repoMapper;
        this.wordCloudMapper = wordCloudMapper;
        this.introMapper = introMapper;
    }

    public boolean run() {
        status.queuing = false;
        startTime = System.currentTimeMillis();
        try {
            boolean finished = crawler.temptRun();
            if (!crawler.isFunctional()) throw new RuntimeException();
            if (!finished) return false;
            washFrequencyMap(10000, 10000);
            updateFreqMapToDatabase();

            if (repoMapper.selectOne(owner, repo) == null) {
                repoMapper.insertOne(new Repo(owner, repo, startTime));
            } else {
                repoMapper.update(new Repo(owner, repo, startTime));
            }
            cleanOutdated();
        } catch (Exception e) {
            System.out.println(e.getMessage());
            System.out.println("UNEXPECTED ERROR");
        }
        return true;
    }

    private void cleanOutdated() {
        commitMapper.cleanOutdated(owner, repo);
        introMapper.cleanOutdated(owner, repo);
        issueMapper.cleanOutdated(owner, repo);
        releaseMapper.cleanOutdated(owner, repo);
        wordCloudMapper.cleanOutdated(owner, repo);
    }


    private void crawlerCallback(Crawler.Result crawlerResult) {
        System.out.println(crawlerResult.repo + " " + crawlerResult.type + " " + crawlerResult.page);
        try {
            int type = crawlerResult.type;
            String result = crawlerResult.result;
            switch (type) {
                case Crawler.INTRO_TASK -> {
                    Intro intro = new Intro(owner, repo, startTime, result);
                    introMapper.insertOne(intro);
                }
                case Crawler.COMMIT_TASK -> {
                    JSONArray arr = JSONUtil.parseArray(result);
                    for (Object o : arr) {
                        Commit commit = parseCommit((JSONObject) o);
                        if (commit != null) commitMapper.insertOne(commit);
                    }
                }
                case Crawler.ISSUE_TASK -> {
                    JSONArray arr = JSONUtil.parseArray(result);
                    for (Object o : arr) {
                        Issue issue = parseIssue((JSONObject) o);
                        if (issue != null) issueMapper.insertOne(issue);
                        parseWordCloud((JSONObject) o);
                    }
                }
                case Crawler.RELEASE_TASK -> {
                    JSONArray arr = JSONUtil.parseArray(result);
                    for (Object o : arr) {
                        Release release = parseRelease((JSONObject) o);
                        if (release != null) releaseMapper.insertOne(release);
                    }
                }
                case Crawler.ISSUE_COMMENTS_TASK -> {
                    JSONArray arr = JSONUtil.parseArray(result);
                    for (Object o : arr) {
                        String comment = parseIssueComment((JSONObject) o);
                        textTmp.add(comment);
                    }
                }
                default -> {}
            }
        } catch (Exception e) {
            System.out.println(e.getMessage());
            System.out.println("UNEXPECTED ERROR IN CALLBACK");
            crawler.stop();
        }


    }

    public Commit parseCommit(JSONObject j) {
        try {
            JSONObject author = j.getJSONObject("author");
            Long authorId = Long.valueOf(author.getInt("id"));
            String authorName = author.getStr("login");
            Date commitTime = j.getJSONObject("commit").getJSONObject("committer").getDate("date");
            return new Commit(owner, repo, startTime, authorId, authorName, commitTime.getTime());
        } catch (Exception e) {
            return null;
        }
    }

    public Issue parseIssue(JSONObject j) {
        try {
            Long createdAt = j.getDate("created_at").getTime();
            Long closedAt = null;
            if (j.getDate("closed_at") != null) {
                closedAt = j.getDate("closed_at").getTime();
            }
            String title = j.getStr("title");
            return new Issue(owner, repo, startTime, title, createdAt, closedAt);
        } catch (Exception e) {
            return null;
        }
    }

    public Release parseRelease(JSONObject j) {
        try {
            String name = j.getStr("name");
            Long publishAt = j.getDate("published_at").getTime();
            return new Release(owner, repo, startTime, name, publishAt);
        } catch (Exception e) {
            return null;
        }
    }

    public void parseWordCloud(JSONObject j) {
        try {
            textTmp.add(j.getStr("title"));
            textTmp.add(j.getStr("body"));
            String commentUrl = j.getStr("comments_url");
            if (!fastCrawler && commentUrl != null) {
                crawler.downloadAll(owner + "/" + repo,
                        Crawler.ISSUE_COMMENTS_TASK, Map.of("url", commentUrl));
            }
            List<String> texts = textTmp.stream().filter(Objects::nonNull).toList();
            textTmp.clear();
            HashMap<String, Integer> freq = chineseTokenizer
                    ? WordCloudUtil.getFrequencyWithChinese(texts) : WordCloudUtil.getFrequency(texts);
            fillFrequencyMap(freq);
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }

    }

    public String parseIssueComment(JSONObject j) {
        try {
            return j.getStr("body");
        } catch (Exception e) {
            System.out.println(e.getMessage());
            return null;
        }
    }

    private void fillFrequencyMap(Map<String, Integer> newFrequency) {
        for (String word : newFrequency.keySet()) {
            int newFreq = newFrequency.get(word);
            wordFrequency.putIfAbsent(word, 0);
            wordFrequency.put(word, wordFrequency.get(word) + newFreq);
        }
        washFrequencyMap(10000, 50000);
    }

    private void washFrequencyMap(int valid, int threshold) {
        if (wordFrequency.size() > threshold) {
            System.out.println("wash");
            LinkedHashMap<String, Integer> newWordFrequency = new LinkedHashMap<>();
            wordFrequency.entrySet().stream()
                    .sorted(Map.Entry.comparingByValue(Comparator.reverseOrder()))
                    .limit(valid)
                    .forEachOrdered(s -> newWordFrequency.put(s.getKey(), s.getValue()));
            wordFrequency = newWordFrequency;
        }
    }

    private void updateFreqMapToDatabase() {
        for (String word : wordFrequency.keySet()) {
            int freq = wordFrequency.get(word);

            WordCloud old = wordCloudMapper.selectOne(word, owner, repo, startTime);
            if (old == null) {
                wordCloudMapper.insertOne(new WordCloud(owner, repo, startTime, word, freq));
            } else {
                old.setFrequency(old.getFrequency() + freq);
                wordCloudMapper.updateFrequency(old);
            }
        }
        wordFrequency.clear();
    }
}
