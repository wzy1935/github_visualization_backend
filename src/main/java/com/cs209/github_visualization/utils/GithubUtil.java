package com.cs209.github_visualization.utils;

import cn.hutool.http.HttpResponse;
import cn.hutool.http.HttpUtil;
import cn.hutool.http.Method;

public class GithubUtil {
    public static boolean repoExist(String owner, String repo) {
        try (HttpResponse resp = HttpUtil
                .createRequest(Method.HEAD, "https://github.com/" + owner + "/" + repo)
                .timeout(5000).execute()) {
            int code = resp.getStatus();
            return code == 200;
        } catch (Exception e) {
            System.out.println(e.getMessage());
            return false;
        }
    }

    public static boolean verifyToken(String githubToken) {
        if (githubToken == null) {
            return false;
        }
        try (HttpResponse resp = HttpUtil
                .createRequest(Method.HEAD, "https://api.github.com/rate_limit")
                .header("Authorization", "Bearer " + githubToken).timeout(5000).execute()) {
            int code = resp.getStatus();
            return code == 200;
        } catch (Exception e) {
            System.out.println(e.getMessage());
            return false;
        }
    }
}
