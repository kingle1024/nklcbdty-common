package com.nklcbdty.common.vo;

import java.time.LocalDateTime;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Lob;
import jakarta.persistence.Table;
import lombok.Data;

@Entity
@Table(name = "job_mst")
@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class Job_mst {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = true)
    private String companyCd;

    @Column(nullable = true)
    private String annoId;

    @Column(nullable = true)
    private String classCdNm;

    @Column(nullable = true)
    private String empTypeCdNm;

    @Column(nullable = true)
    private String annoSubject;

    @Column(nullable = true)
    private String subJobCdNm;

    @Column(nullable = true)
    private String sysCompanyCdNm;

    @Column(nullable = true)
    private String jobDetailLink;

    @Column(nullable = true)
    private String workplace;

    @Column(nullable = true)
    private String startDate;

    @Column(nullable = true)
    private String endDate;

    // primitive long 으로 통일. null 컬럼은 0 으로 매핑됨 (메인의 기존 동작 유지).
    @Column(nullable = true)
    private long personalHistory;

    @Column(nullable = true)
    private long personalHistoryEnd;

    // RAG 의미검색용 임베딩. batch 는 사용 안 함.
    @Lob
    @Column(columnDefinition = "MEDIUMBLOB", nullable = true)
    @JsonIgnore
    private byte[] embedding;

    @Column(nullable = true)
    private String embeddingVersion;

    @CreationTimestamp
    private LocalDateTime insertDts;

    @UpdateTimestamp
    private LocalDateTime updateDts;
}
