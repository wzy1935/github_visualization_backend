package com.cs209.github_visualization.mapper;


import com.cs209.github_visualization.POJO.IssueCountWrapper;
import com.cs209.github_visualization.model.Issue;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Mapper
@Repository
public interface IssueMapper {
    @Insert("insert into issue (owner, repo, last_updated, title, created_at, closed_at) " +
            "values (#{owner}, #{repo}, #{last_updated}, #{title}, #{created_at}, #{closed_at});")
    void insertOne(Issue issue);

    @Delete("delete from commit where owner=#{owner} and repo=#{repo} " +
            " and last_updated < (select last_updated from repo where owner=#{owner} and repo=#{repo})")
    void cleanOutdated(String owner, String repo);

    IssueCountWrapper getIssueCount(@Param("owner") String owner,@Param("repo") String repo);

    List<Long> getOpenIssueTimes(@Param("owner") String owner,@Param("repo") String repo,
                             @Param("from") Long from,@Param("to") Long to);

    List<Long> getCloseIssueTimes(@Param("owner") String owner,@Param("repo") String repo,
                                  @Param("from") Long from,@Param("to") Long to);

    List<Long> getTotalIssueTimes (@Param("owner") String owner,@Param("repo") String repo,
                                   @Param("from") Long from,@Param("to") Long to);


    List<Long> getSolutionTimes(@Param("owner") String owner,@Param("repo") String repo);

    Integer getOpenCount(@Param("owner") String owner,@Param("repo") String repo,@Param("to")Long to);
    Integer getClosedCount(@Param("owner") String owner,@Param("repo") String repo,@Param("to")Long to);
    Integer getTotalCount(@Param("owner") String owner,@Param("repo") String repo,@Param("to")Long to);
}
