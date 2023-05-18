package com.twogather.twogatherwebbackend.repository;

import com.twogather.twogatherwebbackend.domain.AuthenticationType;
import com.twogather.twogatherwebbackend.domain.Member;
import com.twogather.twogatherwebbackend.exception.MemberException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import java.util.Objects;

import static com.twogather.twogatherwebbackend.TestConstants.*;
import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

class MemberRepositoryTest extends RepositoryTest {
    @Autowired
    private MemberRepository memberRepository;

    @Test
    @DisplayName("find: 저장된 멤버를 이메일을 통해 찾아올 수 있다.")
    @Transactional
    void findByEmail() {
        // given
        Member expected = memberRepository.save(MEMBER);

        // when
        Member findMember = memberRepository.findByEmail(MEMBER_EMAIL)
                .orElseThrow(()-> new MemberException(MemberException.MemberErrorCode.NO_SUCH_EMAIL));

        // then
        assertThat(findMember).usingRecursiveComparison().isEqualTo(expected);
    }

    @Test
    @DisplayName("특정 이메일을 가진 멤버가 있는지 확인할 수 있다.")
    @Transactional
    void existsByEmail() {
        // given
        memberRepository.save(MEMBER);

        // when
        boolean actual = memberRepository.existsByEmail(MEMBER_EMAIL);

        // then
        assertThat(actual).isTrue();
    }

}