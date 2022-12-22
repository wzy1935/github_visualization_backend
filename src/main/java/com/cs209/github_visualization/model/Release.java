package com.cs209.github_visualization.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.sql.Timestamp;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Release {
    String owner;
    String repo;
    Long last_updated;

    String name;
    Long published_at;
}
