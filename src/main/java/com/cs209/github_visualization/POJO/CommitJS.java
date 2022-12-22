package com.cs209.github_visualization.POJO;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.Date;

@Data
@AllArgsConstructor
public class CommitJS {
    Wrapper commit;

    public String getDeveloper() {
        return commit.committer.name;
    }

    public Long getTime() {
        return commit.committer.getDate().getTime();
    }
}



@Data
@AllArgsConstructor
class Committer {
    String name;
    String email;
    Date date;
}

@Data
@AllArgsConstructor
class Wrapper {
    Committer committer;
    String message;
}

