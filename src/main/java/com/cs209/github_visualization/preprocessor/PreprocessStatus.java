package com.cs209.github_visualization.preprocessor;

import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
public class PreprocessStatus {
    String repoStr;
    List<String> logs = new ArrayList<>();
    List<Integer> taskList;

    int taskIndex = 0;
    int taskPage = 1;
    boolean pausing = false;
    boolean queuing = true;

    public PreprocessStatus(String repoStr, List<Integer> taskList) {
        this.repoStr = repoStr;
        this.taskList = taskList;
    }

    public void log(String str) {
        logs.add(str);
        if (logs.size() > 10) logs.remove(0);
    }

}
