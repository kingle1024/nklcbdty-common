package com.nklcbdty.common.crawler.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.stereotype.Repository;

import com.nklcbdty.common.vo.Job_mst;

// 메인 + 배치 공통. derived query 4종 + QueryDSL custom 결합.
// annoId 단일/IN 조회는 CrawlerRepository 참고.
@Repository
public interface JobRepository extends JpaRepository<Job_mst, Long>, JobRepositoryCustom {
    List<Job_mst> findAllByCompanyCdAndSubJobCdNmIsNotNullOrderByEndDateAsc(String company);
    List<Job_mst> findAllBySubJobCdNmIsNotNull();
    List<Job_mst> findAllByCompanyCdInAndSubJobCdNmInOrderByEndDateDesc(List<String> companyCds, List<String> subJobCdNms);

    @Modifying
    void deleteByCompanyCd(String companyCd);
}
