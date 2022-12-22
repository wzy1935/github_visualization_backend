package com.cs209.github_visualization.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Repo {
    String owner;
    String repo;
    Long last_updated;
}
