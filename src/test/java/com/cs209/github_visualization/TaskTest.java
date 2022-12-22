package com.cs209.github_visualization;

import cn.hutool.core.io.FileUtil;
import cn.hutool.json.JSONArray;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import com.cs209.github_visualization.model.Commit;
import com.cs209.github_visualization.model.Issue;
import com.cs209.github_visualization.model.Release;
import com.cs209.github_visualization.preprocessor.Crawler;
import com.cs209.github_visualization.preprocessor.PreprocessTask;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.StringReader;
import java.nio.charset.StandardCharsets;
import java.util.Scanner;

public class TaskTest {
    static PreprocessTask task = new PreprocessTask(null, "donnemartin", "system-design-primer", true, false);

    public static Crawler.Result loadCommit() {
        File file = new File("C:\\Users\\Zengyi Wang\\Desktop\\data\\commits\\donnemartin\\system-design-primer\\2.json");
        String in = FileUtil.readString(file, StandardCharsets.UTF_8);
        return new Crawler.Result(
                Crawler.COMMIT_TASK,
                "donnemartin",
                "system-design-primer",
                2,
                in
        );
    }

    public static Crawler.Result loadIssue() {
        File file = new File("C:\\Users\\Zengyi Wang\\Desktop\\data\\issues\\donnemartin\\system-design-primer\\2.json");
        String in = FileUtil.readString(file, StandardCharsets.UTF_8);
        return new Crawler.Result(
                Crawler.ISSUE_TASK,
                "donnemartin",
                "system-design-primer",
                2,
                in
        );
    }

    public static Crawler.Result loadRelease() {
        File file = new File("C:\\Users\\Zengyi Wang\\Desktop\\data\\releases\\vuejs\\vue\\1.json");
        String in = FileUtil.readString(file, StandardCharsets.UTF_8);
        return new Crawler.Result(
                Crawler.ISSUE_TASK,
                "vuejs",
                "vue",
                2,
                in
        );
    }


    public static void main(String[] args) throws Exception {
//        Crawler.Result commit = loadCommit();
//        JSONArray arr = JSONUtil.parseArray(commit.result);
//        for (Object o : arr) {
//            Commit c = task.parseCommit((JSONObject) o);
//            System.out.println();
//        }

//        Crawler.Result issue = loadIssue();
//        JSONArray arr = JSONUtil.parseArray(issue.result);
//        for (Object o : arr) {
//            Issue i = task.parseIssue((JSONObject) o);
//            System.out.println();
//        }

        Crawler.Result release = loadRelease();
        JSONArray arr = JSONUtil.parseArray(release.result);
        for (Object o : arr) {
            Release i = task.parseRelease((JSONObject) o);
            System.out.println();
        }
    }
}
