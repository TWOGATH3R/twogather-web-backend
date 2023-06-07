package com.twogather.twogatherwebbackend.repository;

import com.twogather.twogatherwebbackend.domain.Member;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;
public interface MemberRepository extends JpaRepository<Member, Long> {
    @Query("select m from Member m where m.username =:username and m.isActive = true")
    Optional<Member> findActiveMemberByUsername(@Param("username") String username);
    @Query("select m from Member m where m.memberId = :id and m.isActive = true")
    Optional<Member> findActiveMemberById(@Param("id")Long id);
    @Query("SELECT COUNT(c) > 0 FROM Member c WHERE c.username = :username AND c.isActive = true")
    boolean existsByActiveUsername(@Param("username") String username);
    @Query("SELECT COUNT(c) > 0 FROM Member c WHERE c.email = :email AND c.isActive = true")
    boolean existsByActiveEmail(@Param("email") String email);
}
