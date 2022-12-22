package com.cs209.github_visualization.POJO;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.sql.Timestamp;

@Data
@AllArgsConstructor
public class ReleaseJS implements Comparable<ReleaseJS> {
    String name;
    Long published_at;

    @Override
    public int compareTo(ReleaseJS o) {
        return this.published_at > o.published_at ? -1 : 1;
    }
}
