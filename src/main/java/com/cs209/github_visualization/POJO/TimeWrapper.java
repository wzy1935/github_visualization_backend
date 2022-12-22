package com.cs209.github_visualization.POJO;

@SuppressWarnings("all")
public enum TimeWrapper {
    LESS_ONE_DAY(0L,1L * 60 * 60 * 24 , "< 1 day"),
    LEES_ONE_WEEK(1L * 60 * 60 * 24,1L * 60 * 60 * 24 * 7,"1 day - 1 week"),
    LESS_ONE_MONTH(1L * 60 * 60 * 24 * 7,1L * 60 * 60 * 24 * 7 * 30,"1 week - 1 month"),
    LESS_ONE_YEAR(1L * 60 * 60 * 24 * 7 * 30,1L * 60 * 60 * 24 * 7 * 30 * 12,"1 month - 1 year"),
    MORE_ONE_YEAR(1L * 60 * 60 * 24 * 7 * 30 * 12,1L * 60 * 60 * 24 * 7 * 30 * 12 * 100,"> 1 year");

    public final Long begin;
    public final Long end;
    public final String label;

    TimeWrapper(Long begin, Long end, String label){
        this.begin = begin;
        this.end = end;
        this.label = label;
    }
}
