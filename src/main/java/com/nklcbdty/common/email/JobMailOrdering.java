package com.nklcbdty.common.email;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import com.nklcbdty.common.vo.Job_mst;

// 수동(nklcbdty)/자동(nklcbdty-batch) EmailService 가 동일한 필터·정렬 정책을 쓰도록 통합한 헬퍼.
// 정책 차이로 한쪽에서 종료/장기 공고가 새는 격차 버그 재발 방지를 위한 단일 진입점.
public final class JobMailOrdering {

    private JobMailOrdering() { }

    // null/"영입종료시" 만 살아있음으로 간주. 파싱불가("error" 등) 는 손상 데이터로 보고 제외.
    // reconciliation 으로 종료된 공고는 endDate=어제 가 박혀 여기서 걸러진다.
    public static boolean isLive(Job_mst job, LocalDate today) {
        String endDateStr = job.getEndDate();
        if (endDateStr == null || "영입종료시".equals(endDateStr)) {
            return true;
        }
        LocalDate endDate = parseEndDate(endDateStr);
        if (endDate == null) {
            return false;
        }
        return !endDate.isBefore(today);
    }

    // 정렬된 공고 리스트에서 종료일이 현재 시점 + 1년을 넘는 항목은 뒤로 보낸다.
    // 1년 이내 그룹은 원본 정렬을 유지하고, 초과 그룹도 자체 정렬을 유지한다.
    // 종료일 파싱이 실패하는 값(예: "영입종료시")은 1년 이내 그룹으로 둔다.
    public static List<Job_mst> pushFarFutureEndDateToBottom(List<Job_mst> jobs) {
        LocalDate threshold = LocalDate.now().plusYears(1);
        List<Job_mst> within = new ArrayList<>();
        List<Job_mst> beyond = new ArrayList<>();
        for (Job_mst job : jobs) {
            LocalDate endDate = parseEndDate(job.getEndDate());
            if (endDate != null && endDate.isAfter(threshold)) {
                beyond.add(job);
            } else {
                within.add(job);
            }
        }
        List<Job_mst> ordered = new ArrayList<>(within.size() + beyond.size());
        ordered.addAll(within);
        ordered.addAll(beyond);
        return ordered;
    }

    private static LocalDate parseEndDate(String endDate) {
        if (endDate == null || endDate.length() < 10) {
            return null;
        }
        try {
            return LocalDate.parse(endDate.substring(0, 10));
        } catch (Exception e) {
            return null;
        }
    }
}
