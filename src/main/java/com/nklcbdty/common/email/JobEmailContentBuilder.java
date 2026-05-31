package com.nklcbdty.common.email;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;

import com.nklcbdty.common.dto.JobPosting;

// 메인/배치 양 프로젝트가 동일 정책으로 채용 공고 메일 컨텐츠를 생성하도록 통합한 헬퍼.
// 정책 차이로 인한 "수동/자동 메일 격차 버그" 재발 방지를 위한 단일 진입점.
public final class JobEmailContentBuilder {

    private static final DateTimeFormatter TITLE_DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyy년 MM월 dd일");

    private JobEmailContentBuilder() { }

    public static String buildDailyTitle() {
        return "[네카라쿠배] " + LocalDate.now().format(TITLE_DATE_FORMATTER) + " 맞춤 채용 공고가 도착했어요!";
    }

    public static String generateHtml(String keyword, List<JobPosting> jobPostings) {
        String today = LocalDate.now().format(TITLE_DATE_FORMATTER);

        StringBuilder htmlBuilder = new StringBuilder();
        htmlBuilder.append("<table width=\"100%\" cellpadding=\"0\" cellspacing=\"0\" style=\"padding: 20px 0\">");
        htmlBuilder.append("<tbody><tr><td align=\"center\">");
        htmlBuilder.append("<table width=\"600\" cellpadding=\"0\" cellspacing=\"0\" style=\"background: #ffffff; border-radius: 8px; overflow: hidden\">");
        htmlBuilder.append("<tbody><tr><td style=\"padding: 24px; background-color: #222; color: #fff; text-align: center; font-size: 20px;\">");
        htmlBuilder.append("🎯 <strong>").append(today).append(" 맞춤 채용 공고가 도착했어요!</strong>");
        htmlBuilder.append("</td></tr>");
        htmlBuilder.append("<tr><td style=\"padding: 16px 24px; font-size: 16px; color: #333\">");
        htmlBuilder.append("<p>");
        htmlBuilder.append("네카라쿠배 채용 공고 모음 서비스에서 구독해주신 키워드 ");
        htmlBuilder.append("<strong style=\"color: #007bff\">").append(keyword).append("</strong>에 해당하는 새로운 채용 소식을 알려드려요.");
        htmlBuilder.append("</p>");
        htmlBuilder.append("<p>");
        htmlBuilder.append("더 많은 채용 공고를 보시려면 ");
        htmlBuilder.append("<a href=\"https://www.nklcb.co.kr\" style=\"color: #007bff; text-decoration: underline\" rel=\"noreferrer noopener\" target=\"_blank\"><strong>nklcb.co.kr</strong></a>");
        htmlBuilder.append("에서 확인하실 수 있어요.");
        htmlBuilder.append("</p>");
        htmlBuilder.append("</td></tr>");

        for (JobPosting job : jobPostings) {
            htmlBuilder.append("<tr><td style=\"padding: 16px; border-bottom: 1px solid #eee\">");
            htmlBuilder.append("<a href=\"").append(job.getUrl()).append("\" style=\"font-size: 16px; font-weight: bold; color: #222; text-decoration: none;\" rel=\"noreferrer noopener\" target=\"_blank\">");
            htmlBuilder.append(job.getTitle());
            htmlBuilder.append("</a>");
            htmlBuilder.append("<div style=\"font-size: 14px; color: #666; margin-top: 4px\">");
            htmlBuilder.append(job.getCompany()).append(" | ").append(job.getJobType());

            String deadline = job.getEndDate();
            if (deadline != null && !deadline.isEmpty()) {
                htmlBuilder.append(" | ").append(deadline);
            }

            // 정책: deadline 유무와 무관하게 경력 정보가 의미 있으면 항상 표시 (batch 정책 채택).
            if (job.getPersonalHistory() == 0 && job.getPersonalHistoryEnd() == 0) {
                htmlBuilder.append(" | 경력 무관");
            } else if (job.getPersonalHistory() > 0 && job.getPersonalHistoryEnd() > 0) {
                htmlBuilder.append(" | ").append(job.getPersonalHistory()).append("년 ~ ").append(job.getPersonalHistoryEnd()).append("년");
            } else if (job.getPersonalHistory() > 0) {
                htmlBuilder.append(" | ").append(job.getPersonalHistory()).append("년 이상");
            } else if (job.getPersonalHistoryEnd() > 0) {
                htmlBuilder.append(" | ").append(job.getPersonalHistoryEnd()).append("년 이하");
            }

            htmlBuilder.append("</div>");
            htmlBuilder.append("</td></tr>");
        }

        htmlBuilder.append("</tbody></table>");
        htmlBuilder.append("</td></tr></tbody></table>");
        return htmlBuilder.toString();
    }
}
