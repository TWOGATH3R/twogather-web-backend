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
    boolean existsByUsername(String username);
}
