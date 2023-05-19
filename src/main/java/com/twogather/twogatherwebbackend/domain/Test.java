package com.twogather.twogatherwebbackend.domain;

import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;


@Entity
@Getter
@NoArgsConstructor
@TableGenerator(
        name = "TEST_NAME", //식별자 생성기 이름
        table = "sequence_table", //키 생성 테이블명
        pkColumnName = "sequence_name", //시퀀스 컬럼명
        valueColumnName = "next_val", //시퀀스 값 컬럼명
        initialValue = 1, //초기 값
        allocationSize = 20
)
public class Test {
    @Id
    @GeneratedValue(
            strategy = GenerationType.TABLE
            , generator = "GENERATOR_NAME"
    )
    @Column(name = "test_id")
    private Long testId;

    @Column(unique = true)
    private Integer name;

    public Test(Integer name) {
        this.name = name;
    }
}
