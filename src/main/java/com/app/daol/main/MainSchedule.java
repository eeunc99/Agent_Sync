package com.app.daol.main;

import com.app.daol.service.OriginalDatabaseService;
import com.app.daol.service.TargetDatabaseService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * cron 주기로 원본 DB ↔ 대상 DB 사이의 사용자/조직 정보를 비교하여 동기화한다.
 * - 원본에는 있지만 대상에 없으면 INSERT
 * - 둘 다 있으면 변경분만 UPDATE
 * - 대상에는 있지만 원본에 없으면 비활성화 처리(soft delete)
 *
 * 실제 컬럼명/식별 조건 등 세부 로직은 사내 비공개 구현체를 참고.
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class MainSchedule {

    private final OriginalDatabaseService originalDatabaseService;
    private final TargetDatabaseService targetDatabaseService;

    @Scheduled(cron = "${set.cron.user.time}")
    public void agentUserPeristalsis() {
        List<Map<String, Object>> originalList = originalDatabaseService.selectUserQueryList();
        Set<String> targetIds = targetDatabaseService.userIdList();

        deactivateMissing(originalList, targetIds);
        upsert(originalList, targetIds);
    }

    @Scheduled(cron = "${set.cron.group.time}")
    public void agentGroupPeristalsis() {
        List<Map<String, Object>> originalList = originalDatabaseService.selectGroupQueryList();
        List<Map<String, Object>> targetList = targetDatabaseService.groupList();

        log.info("조직 동기화 시작 - 원본 {}건, 대상 {}건",
                originalList == null ? 0 : originalList.size(),
                targetList == null ? 0 : targetList.size());

        // 세부 비교/등록/수정 로직은 사내 비공개 구현체 참고.
    }

    private void deactivateMissing(List<Map<String, Object>> originalList, Set<String> targetIds) {
        for (String id : targetIds) {
            if (id == null || id.isEmpty() || "admin".equals(id)) {
                continue;
            }
            boolean existsInOriginal = originalList.stream()
                    .anyMatch(row -> id.equals(String.valueOf(row.get("id"))));
            if (!existsInOriginal) {
                targetDatabaseService.userUpdate(Map.of("id", id, "active", "N"));
                log.info("{} : 비활성화 처리 완료", id);
            }
        }
    }

    private void upsert(List<Map<String, Object>> originalList, Set<String> targetIds) {
        for (Map<String, Object> row : originalList) {
            try {
                String id = String.valueOf(row.get("id"));
                if (id == null || id.trim().isEmpty()) {
                    continue;
                }
                if (!targetIds.contains(id)) {
                    targetDatabaseService.userInsert(row);
                    log.info("{} : insert 성공", id);
                } else {
                    targetDatabaseService.update(row);
                    log.info("{} : update 완료", id);
                }
            } catch (Exception e) {
                log.error("동기화 처리 중 오류", e);
            }
        }
    }
}
