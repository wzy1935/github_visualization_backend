package com.cs209.github_visualization.POJO;


import lombok.AllArgsConstructor;
import lombok.Data;

import java.sql.Timestamp;

@Data
@AllArgsConstructor
public class IssueJS {
    private String state;
    private String title;
    private String body;
    private Timestamp created_at;
    private Timestamp closed_at;
}
