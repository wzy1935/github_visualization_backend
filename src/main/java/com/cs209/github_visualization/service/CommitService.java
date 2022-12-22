package com.cs209.github_visualization.service;

import com.cs209.github_visualization.POJO.CommitTimeWrapper;
import com.cs209.github_visualization.POJO.RankWrap;
import com.cs209.github_visualization.mapper.CommitMapper;
import org.springframework.stereotype.Service;
import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;

@Service
public class CommitService {
    @Resource
    CommitMapper commitMapper;

    private List<CommitTimeWrapper> getInfoTime(List<Long> times) {
        int[][] flag = new int[7][24];
        long eightHours = 8 * 60 * 60 * 1000L;
        long oneWeek = 7 * 24 * 60 * 60 * 1000L;
        long fourDays = 4 * 24 * 60 * 60 * 1000L;
        long oneDay = 24 * 60 * 60 * 1000L;
        long oneHour = 60 * 60 * 1000L;
        for (var commit : times) {
            long tmp = commit + fourDays + eightHours;
            long weekRemainder = tmp % oneWeek;
            long dayRemainder = weekRemainder % oneDay;
            int week = (int) (weekRemainder / oneDay);
            int hour = (int) (dayRemainder / oneHour);
            flag[week][hour]++;
        }
        var res = new ArrayList<CommitTimeWrapper>();
        for (int i = 0; i < flag.length; i++) {
            for (int j = 0; j < flag[i].length; j++) {
                res.add(new CommitTimeWrapper(i, j, flag[i][j]));
            }
        }
        return res;
    }

    public Integer getCount(String owner, String repo){
        return commitMapper.getCount(owner, repo);
    }

    public Integer getDeveloperCount(String owner, String repo){
        return commitMapper.getDeveloperCount(owner, repo);
    }


    public List<RankWrap> getCommitRank(String owner, String repo, Integer limit) {
        return commitMapper.getRank(owner, repo, limit);
    }

    public List<CommitTimeWrapper> getDistribution(String owner, String repo) {
        return getInfoTime(commitMapper.getCommitTime(owner, repo));
    }
}
