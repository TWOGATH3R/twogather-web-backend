package com.twogather.twogatherwebbackend.repository;

import com.twogather.twogatherwebbackend.domain.Member;
import com.twogather.twogatherwebbackend.exception.MemberException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static com.twogather.twogatherwebbackend.util.TestConstants.*;
import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

class MemberRepositoryTest extends RepositoryTest {

    @Test
    @DisplayName("find: 저장된 멤버를 이메일을 통해 찾아올 수 있다.")
    void findByEmail() {
        // given
        Member expected = memberRepository.save(MEMBER);

        // when
        Member findMember = memberRepository.findActiveMemberByUsername(MEMBER_USERNAME)
                .orElseThrow(()-> new MemberException(MemberException.MemberErrorCode.NO_SUCH_MEMBER));

        // then
        assertThat(findMember.getEmail()).isEqualTo(expected.getEmail());
        assertThat(findMember.getPassword()).isEqualTo(expected.getPassword());
        assertThat(findMember.getName()).isEqualTo(expected.getName());
        assertThat(findMember.getUsername()).isEqualTo(expected.getUsername());
    }

    @Test
    @DisplayName("특정 이메일을 가진 멤버가 있는지 확인할 수 있다.")
    void existsByEmail() {
        // given
        memberRepository.save(MEMBER);

        // when
        boolean actual = memberRepository.existsByActiveUsername(MEMBER.getUsername());

        // then
        assertThat(actual).isTrue();
    }

}