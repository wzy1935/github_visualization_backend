package com.cs209.github_visualization.mapper;


import com.cs209.github_visualization.POJO.ReleaseJS;
import com.cs209.github_visualization.model.Issue;
import com.cs209.github_visualization.model.Release;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Mapper
@Repository
public interface ReleaseMapper {
    @Insert("insert into release (owner, repo, last_updated, name, published_at) " +
            "values (#{owner}, #{repo}, #{last_updated}, #{name}, #{published_at});")
    void insertOne(Release release);

    @Delete("delete from commit where owner=#{owner} and repo=#{repo} " +
            " and last_updated < (select last_updated from repo where owner=#{owner} and repo=#{repo})")
    void cleanOutdated(String owner, String repo);

    Integer getCount(@Param("owner") String owner,@Param("repo") String repo);

    List<ReleaseJS> getAll(@Param("owner") String owner,@Param("repo") String repo,@Param("limit") Integer limit);
}
