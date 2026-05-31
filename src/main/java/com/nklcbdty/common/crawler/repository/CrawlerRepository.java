package com.nklcbdty.common.crawler.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.nklcbdty.common.vo.Job_mst;

// annoId 기반 단일/벌크 조회 + 회사별 reconciliation 조회.
@Repository
public interface CrawlerRepository extends JpaRepository<Job_mst, Long> {
    boolean existsByAnnoId(String annoIdVarchar);
    Job_mst findByAnnoId(String annoIdVarchar);
    List<Job_mst> findAllByAnnoIdIn(List<String> annoIds);
    List<Job_mst> findAllByCompanyCd(String companyCd);
}
