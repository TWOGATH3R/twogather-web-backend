package com.twogather.twogatherwebbackend.docs;

import org.springframework.restdocs.snippet.Attributes;

import static org.springframework.restdocs.snippet.Attributes.key;

public interface DocumentFormatGenerator {
    static Attributes.Attribute getTimeFormat() {
        return key("format").value("HH:mm");
    }
    static Attributes.Attribute getDayOfWeekFormat() {
        return key("format").value("MONDAY, TUESDAY, ... SUNDAY");
    }
    static Attributes.Attribute getStorePhoneFormat() {
        return key("format").value("xx-xxx-xxxx / xxx-xxxx-xxxx / xxx-xxx-xxxx");
    }
    static Attributes.Attribute getRatingFormat() {
        return key("format").value("소수점 첫째짜리까지만. 0~5점 사이");
    }
    static Attributes.Attribute getPasswordFormat() {
        return key("format").value("비밀번호는 영어와 숫자를 포함해서 8자 이상 20자 이내로");
    }
    static Attributes.Attribute getDateFormat() {
        return key("format").value("yyyy-MM-dd");
    }
    static Attributes.Attribute getBusinessNumberFormat() {
        return key("format").value("숫자 10자리여야한다");
    }
    static Attributes.Attribute getScoreFormat() {
        return key("format").value("숫자 0~5 사이의 소수점 한자리 형태( x.0 or x.5 의 형태여야합니다)");
    }
    static Attributes.Attribute getStoreType() {
        return key("format").value("TOP_RATED or MOST_REVIEWED or MOST_LIKES_COUNT");
    }
    static Attributes.Attribute getStoreStatus() {
        return key("format").value("APPROVED, PENDING, DENIED, DELETED");
    }
    static Attributes.Attribute getKeywordFormat() {
        return key("format").value("키워드는 형용사로 입력해주세요");
    }
    static Attributes.Attribute getUsernameFormat() {
        return key("format").value("아이디는 영어와 숫자를 포함해서 8자 이상 15자 이내로");
    }
    static Attributes.Attribute getStoreSortFormat() {
        return key("format").value("<TOP_RATED,desc> or <MOST_REVIEWED,asc> or <MOST_LIKES_COUNT,asc>... 등");
    }
    static Attributes.Attribute getMemberNameFormat() {
        return key("format").value("한글 혹은 영어로만 10자 이내로 입력해주세요");
    }

}
