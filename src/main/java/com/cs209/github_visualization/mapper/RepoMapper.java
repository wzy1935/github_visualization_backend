package com.cs209.github_visualization.mapper;


import com.cs209.github_visualization.model.Repo;
import org.apache.ibatis.annotations.*;

@Mapper
@org.springframework.stereotype.Repository
public interface RepoMapper {
    @Insert("insert into repo (owner, repo, last_updated) " +
            "values (#{owner}, #{repo}, #{last_updated});")
    void insertOne(Repo repo);

    @Select("select * from repo " +
            "where owner=#{owner} and repo=#{repo} order by last_updated desc limit 1;")
    Repo selectOne(String owner, String repo);

    @Update("update repo set last_updated=#{last_updated} where owner=#{owner} and repo=#{repo};")
    void update(Repo repo);


}
