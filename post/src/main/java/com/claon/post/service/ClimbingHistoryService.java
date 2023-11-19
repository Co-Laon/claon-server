package com.claon.post.service;

import com.claon.post.domain.ClimbingHistory;
import com.claon.post.dto.UserPostInfoResponseDto;
import com.claon.post.dto.ClimbingHistoryResponseDto;
import com.claon.post.repository.ClimbingHistoryRepository;
import com.claon.post.repository.PostRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ClimbingHistoryService {
    private final PostRepository postRepository;
    private final ClimbingHistoryRepository climbingHistoryRepository;

    @Transactional(readOnly = true)
    public List<UserPostInfoResponseDto> findClimbingHistory(String userId) {
        List<String> postIds = this.postRepository.selectPostIdsByUserId(userId);

        return climbingHistoryRepository.findByPostIds(postIds)
                .stream().collect(Collectors.groupingBy(history -> history.getPost().getCenterId(),
                        Collectors.toMap(
                                ClimbingHistory::getHoldInfoId,
                                ClimbingHistory::getClimbingCount,
                                Integer::sum
                        )))
                .entrySet().stream()
                .map(entry -> UserPostInfoResponseDto.from(
                        entry.getKey(),
                        postIds.size(),
                        entry.getValue()
                                .entrySet().stream()
                                .map(en -> ClimbingHistoryResponseDto.from(en.getKey(), en.getValue()))
                                .collect(Collectors.toList())))
                .collect(Collectors.toList());
    }
}
