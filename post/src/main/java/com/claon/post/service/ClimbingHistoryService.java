package com.claon.post.service;

import com.claon.post.domain.ClimbingHistory;
import com.claon.post.dto.CenterClimbingHistoryResponseDto;
import com.claon.post.dto.ClimbingHistoryResponseDto;
import com.claon.post.repository.ClimbingHistoryRepository;
import com.claon.post.repository.PostRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ClimbingHistoryService {
    private final PostRepository postRepository;
    private final ClimbingHistoryRepository climbingHistoryRepository;

    @Transactional(readOnly = true)
    public List<CenterClimbingHistoryResponseDto> findClimbingHistory(String userId) {
        List<String> postIds = this.postRepository.selectPostIdsByUserId(userId);

        List<ClimbingHistory> climbingHistories = climbingHistoryRepository.findByPostIds(postIds);

        Map<String, Map<String, Integer>> historyMap = climbingHistories.stream().collect(
                Collectors.groupingBy(history -> history.getPost().getCenterId(),
                        Collectors.toMap(
                                ClimbingHistory::getHoldInfoId,
                                ClimbingHistory::getClimbingCount,
                                Integer::sum
                        )
                ));

        return historyMap.entrySet()
                .stream()
                .map(entry -> CenterClimbingHistoryResponseDto.from(
                        entry.getKey(),
                        postIds.size(),
                        entry.getValue().entrySet()
                                .stream()
                                .map(en -> ClimbingHistoryResponseDto.from(en.getKey(), en.getValue()))
                                .collect(Collectors.toList())))
                .collect(Collectors.toList());
    }
}
