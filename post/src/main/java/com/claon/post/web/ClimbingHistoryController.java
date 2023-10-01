package com.claon.post.web;

import com.claon.post.dto.UserPostInfoResponseDto;
import com.claon.post.service.ClimbingHistoryService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Repository;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;

import java.util.List;

@Repository
@RequiredArgsConstructor
@RequestMapping("/api/v1/histories")
public class ClimbingHistoryController {
    private final ClimbingHistoryService climbingHistoryService;

    @GetMapping
    @ResponseStatus(value = HttpStatus.OK)
    public List<UserPostInfoResponseDto> findClimbingHistory(@RequestParam String userId) {
        return climbingHistoryService.findClimbingHistory(userId);
    }
}
