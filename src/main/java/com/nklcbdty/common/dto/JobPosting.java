package com.nklcbdty.common.dto;

import lombok.Data;

@Data
public class JobPosting {
    private String title;
    private String url;
    private String company;
    private String jobType;
    private String startDate;
    private String endDate;
    private long personalHistory;
    private long personalHistoryEnd;
}
