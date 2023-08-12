package com.claon.post.repository;

import com.claon.post.domain.Notice;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface NoticeRepository extends JpaRepository<Notice, String> {
    @Query(value = "SELECT n FROM Notice n", countQuery = "SELECT count(n) FROM Notice n")
    Page<Notice> findAllWithPagination(Pageable pageable);
}
