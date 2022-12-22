package com.cs209.github_visualization.mapper;


import com.cs209.github_visualization.model.Intro;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;
import org.springframework.stereotype.Repository;

@Mapper
@Repository
public interface IntroMapper {
    @Insert("insert into intro (owner, repo, last_updated, intro) " +
            "values (#{owner}, #{repo}, #{last_updated}, #{intro});")
    void insertOne(Intro intro);

    @Delete("delete from commit where owner=#{owner} and repo=#{repo} " +
            " and last_updated < (select last_updated from repo where owner=#{owner} and repo=#{repo})")
    void cleanOutdated(String owner, String repo);

    @Select("select * from intro where owner=#{owner} and repo=#{repo} " +
            "and last_updated in (select last_updated from repo where owner=#{owner} and repo=#{repo})")
    Intro selectLatest(String owner, String repo);
}
