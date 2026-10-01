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
    // job_mst.emp_type_cd_nm 원문. 정규/비정규/계약직/인턴/기간제 등 크롤러마다 표기가 다르다.
    private String empType;
}
