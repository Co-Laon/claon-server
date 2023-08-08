package com.claon.post.repository;

import com.claon.post.domain.Post;
import com.claon.post.domain.PostReport;
import com.claon.user.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface PostReportRepository extends JpaRepository<PostReport, String> {
    Optional<PostReport> findByReporterAndPost(User reporter, Post post);
}