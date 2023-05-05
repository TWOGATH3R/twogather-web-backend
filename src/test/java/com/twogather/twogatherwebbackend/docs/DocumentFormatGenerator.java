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

}
