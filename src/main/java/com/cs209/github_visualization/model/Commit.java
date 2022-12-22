package com.cs209.github_visualization.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;


@Data
@AllArgsConstructor
@NoArgsConstructor
public class Commit {
    String owner;
    String repo;
    Long last_updated;

    Long developer_id;
    String developer_name;
    Long commit_time;

}
