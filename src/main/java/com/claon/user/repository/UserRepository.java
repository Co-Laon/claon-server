package com.claon.user.repository;

import com.claon.user.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, String> {
    Optional<User> findByNickname(String nickname);

    @Query(value = "SELECT * " +
            "FROM TB_USER AS u " +
            "WHERE u.email = :email AND u.oauth_id = :oauth_id", nativeQuery = true)
    Optional<User> findByEmailAndOAuthId(@Param("email") String email, @Param("oauth_id") String oAuthId);

    @Query(value = "SELECT * " +
            "FROM TB_USER AS u " +
            "WHERE u.instagram_oauth_id = :oauth_id", nativeQuery = true)
    Optional<User> findByInstagramOAuthId(@Param("oauth_id") String oAuthId);
}
