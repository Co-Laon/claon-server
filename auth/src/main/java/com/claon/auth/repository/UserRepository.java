package com.claon.auth.repository;

import com.claon.auth.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, String> {
    Optional<User> findByNickname(String nickname);

    @Query(value = """
            SELECT * 
            FROM TB_USER AS u 
            WHERE u.email = :email
            """, nativeQuery = true)
    Optional<User> findByEmail(@Param("email") String email);
}
