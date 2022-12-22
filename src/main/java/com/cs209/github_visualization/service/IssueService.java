package com.cs209.github_visualization.service;

import com.cs209.github_visualization.POJO.*;
import com.cs209.github_visualization.mapper.IssueMapper;
import com.cs209.github_visualization.mapper.WordCloudMapper;
import com.cs209.github_visualization.model.Issue;
import com.cs209.github_visualization.model.WordCloud;
import com.cs209.github_visualization.utils.Result;
import lombok.AllArgsConstructor;
import lombok.Data;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.text.SimpleDateFormat;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
public class IssueService {
    @Autowired
    WordCloudMapper wordCloudMapper;

    @Autowired
    IssueMapper issueMapper;

    public IssueCountWrapper getIssuesNum(String owner, String repo) {
        return issueMapper.getIssueCount(owner, repo);
    }

    private List<IssueDistributeWrapper> distributeWrap(long step, List<Long> issues, Long from, int dataCnt, int based) {
        List<IssueDistributeWrapper> res = new ArrayList<>();
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
        for (int i = 1; i <= dataCnt; i++) {
            long tmp = i * step + from;
            int amount = (int) issues.stream()
                    .filter(e -> e != null && e < tmp
                            && e >= tmp - step
                    ).count();
            based += amount;
            String label = simpleDateFormat.format(new Date(tmp));
            res.add(new IssueDistributeWrapper(tmp, label, based));
        }
        return res;
    }

    public Map<String, List<IssueDistributeWrapper>> getDistribution(String owner, String repo, Long from, Long to, Integer datapoint) {
        long step = (to - from) / datapoint;
        Integer openCount = issueMapper.getOpenCount(owner, repo, from);
        Integer closedCount = issueMapper.getClosedCount(owner, repo, from);
        Integer totalCount = issueMapper.getTotalCount(owner, repo, from);
        List<Long> getOpen = issueMapper.getOpenIssueTimes(owner, repo, from, to);
        List<Long> getClosed = issueMapper.getCloseIssueTimes(owner, repo, from, to);
        List<Long> getTotal = issueMapper.getTotalIssueTimes(owner, repo, from, to);

        Map<String, List<IssueDistributeWrapper>> res = new HashMap<>(3);
        res.put("open", distributeWrap(step, getOpen, from, datapoint, openCount));
        res.put("closed", distributeWrap(step, getClosed, from, datapoint, closedCount));
        res.put("total", distributeWrap(step, getTotal, from, datapoint, totalCount));
        return res;
    }

    @Data
    @AllArgsConstructor
    public static class KeyWord {
        String label;
        int weight;
    }

    public List<KeyWord> topKeywords(String owner, String repo, int limit) {
        List<WordCloud> wordClouds = wordCloudMapper.selectTopLatest(owner, repo, limit);
        return wordClouds.stream()
                .map(wordCloud -> new KeyWord(wordCloud.getWord(), wordCloud.getFrequency())).toList();
    }


    private final Function<Long, TimeWrapper> mapTime = (i) -> {
        long day = 1000L * 60 * 60 * 24;
        long month = day * 7;
        if (i < day) {
            return TimeWrapper.LESS_ONE_DAY;
        } else if (i < day * 7) {
            return TimeWrapper.LEES_ONE_WEEK;
        } else if (i < month) {
            return TimeWrapper.LESS_ONE_MONTH;
        } else if (i < 12 * month) {
            return TimeWrapper.LESS_ONE_YEAR;
        } else {
            return TimeWrapper.MORE_ONE_YEAR;
        }
    };

    private double getStd(List<Double> arr, Double avg) {
        double variance = 0.0;
        for (double p : arr) {
            variance += (p - avg) * (p - avg);
        }
        return variance / arr.size();
    }

    public Map<String, Object> getIssueDuration(String owner, String repo) {
        var issues = issueMapper.getSolutionTimes(owner, repo);
        List<IssueDurationWrapper> res = issues.stream()
                .collect(
                        Collectors.groupingBy(
                                mapTime,
                                Collectors.summingInt(e -> 1)
                        )
                ).entrySet()
                .stream()
                .map(
                        i -> new IssueDurationWrapper(i.getKey().begin, i.getKey().end, i.getKey().label, i.getValue())
                ).toList();

        Long avg = (long) issues.stream()
                .collect(Collectors.summarizingLong(Long::longValue))
                .getAverage();
        Double std = getStd(issues.stream().map(i->(double)i/(1000L*24*3600)).collect(Collectors.toList()),
                (double)avg/(1000L*24*3600));
        Map<String, Object> ans = new LinkedHashMap<>(3);
        ans.put("avg", avg);
        ans.put("std", std);
        ans.put("durations", res);
        return ans;
    }


}
