package com.twogather.twogatherwebbackend.repository;

import com.twogather.twogatherwebbackend.domain.Member;
import com.twogather.twogatherwebbackend.exception.MemberException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

class MemberRepositoryTest extends RepositoryTest {
    public static final String EMAIL = "pobi@email.com";
    public static final String PASSWORD = "test1234!!!";
    public static final String NAME = "루터";
    public static final String PHONE = "01012341234";


    @Autowired
    private MemberRepository memberRepository;

    private final Member MEMBER = returnMember();

    @Test
    @DisplayName("저장된 멤버를 이메일을 통해 찾아올 수 있다.")
    void findByEmail() {
        // given
        Member expected = memberRepository.save(MEMBER);

        // when
        Member findMember = memberRepository.findByEmail(EMAIL)
                .orElseThrow(()-> new MemberException(MemberException.MemberErrorCode.NO_SUCH_EMAIL));

        // then
        assertThat(findMember).isEqualTo(expected);
    }

    @Test
    @DisplayName("특정 이메일을 가진 멤버가 있는지 확인할 수 있다.")
    void existsByEmail() {
        // given
        memberRepository.save(MEMBER);

        // when
        boolean actual = memberRepository.existsByEmail(EMAIL);

        // then
        assertThat(actual).isTrue();
    }
    private Member returnMember(){
        return new Member(EMAIL, PASSWORD,NAME, PHONE);
    }
}