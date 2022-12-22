package com.cs209.github_visualization.mapper;


import com.cs209.github_visualization.POJO.RankWrap;
import com.cs209.github_visualization.model.Commit;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;
import java.util.List;

@Mapper
@Repository
public interface CommitMapper {

    @Insert("insert into commit (owner, repo, last_updated, developer_id, developer_name, commit_time) " +
            "values (#{owner}, #{repo}, #{last_updated}, #{developer_id}, #{developer_name}, #{commit_time});")
    void insertOne(Commit commit);

    @Delete("delete from commit where owner=#{owner} and repo=#{repo} " +
            " and last_updated < (select last_updated from repo where owner=#{owner} and repo=#{repo})")
    void cleanOutdated(String owner, String repo);

    int getCount(@Param("owner") String owner, @Param("repo") String repo);

    int getDeveloperCount(@Param("owner") String owner, @Param("repo") String repo);

    int getCountBetween(@Param("owner") String owner, @Param("repo") String repo,
                 @Param("from") Long from, @Param("to") Long to);

    List<RankWrap> getRank(@Param("owner") String owner, @Param("repo") String repo, @Param("limit") Integer limit);

    List<Long> getCommitTime(@Param("owner") String owner, @Param("repo") String repo);

}
