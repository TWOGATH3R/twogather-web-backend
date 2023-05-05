package com.twogather.twogatherwebbackend;

import org.springframework.restdocs.snippet.Attributes;

import static org.springframework.restdocs.snippet.Attributes.key;

public interface DocumentFormatGenerator {
    static Attributes.Attribute getTimeFormat() {
        return key("format").value("HH:mm");
    }
    static Attributes.Attribute getDayOfWeekFormat() {
        return key("format").value("MONDAY, TUESDAY, ... SUNDAY");
    }
}
