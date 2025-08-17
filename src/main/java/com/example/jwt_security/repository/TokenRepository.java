package com.example.jwt_security.repository;

import com.example.jwt_security.entity.Token;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface TokenRepository extends JpaRepository<Token, Long> {


    @Query("""
select t from Token t inner join User u on t.user_obj.id = u.id
where t.user_obj.id = :userId and t.loggedOut = false
""")
    List<Token> findAllAccessTokensByUser(Long userId);

    Optional<Token> findByAccessToken(String token);

    Optional<Token> findByRefreshToken(String token);
}