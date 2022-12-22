package com.cs209.github_visualization.mapper;


import com.cs209.github_visualization.model.Release;
import com.cs209.github_visualization.model.WordCloud;
import org.apache.ibatis.annotations.*;
import org.springframework.stereotype.Repository;

import java.util.List;

@Mapper
@Repository
public interface WordCloudMapper {
    @Insert("insert into wordcloud (owner, repo, last_updated, word, frequency) " +
            "values (#{owner}, #{repo}, #{last_updated}, #{word}, #{frequency});")
    void insertOne(WordCloud wordCloud);

    @Delete("delete from commit where owner=#{owner} and repo=#{repo} " +
            " and last_updated < (select last_updated from repo where owner=#{owner} and repo=#{repo})")
    void cleanOutdated(String owner, String repo);

    @Select("select * from wordcloud where word=#{word} " +
            " and last_updated =#{lastUpdated}")
    WordCloud selectOne(String word, String owner, String repo, long lastUpdated);

    @Select("select * from wordcloud where word=#{word} " +
            " and last_updated in (select last_updated from repo where owner=#{owner} and repo=#{repo})")
    WordCloud selectLatest(String word, String owner, String repo);

    @Update("update wordcloud set frequency=#{frequency} " +
            " where last_updated=#{last_updated} and word=#{word} and owner=#{owner} and repo=#{repo}")
    void updateFrequency(WordCloud wordCloud);

    @Select("select * from wordcloud where owner=#{owner} and repo=#{repo} " +
            " and last_updated in (select last_updated from repo where owner=#{owner} and repo=#{repo})" +
            " order by frequency desc limit #{limit};")
    List<WordCloud> selectTopLatest(String owner, String repo, int limit);

}
