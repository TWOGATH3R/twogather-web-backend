package com.twogather.twogatherwebbackend.domain;

import lombok.Getter;

import javax.persistence.*;
import java.time.DayOfWeek;
import java.time.LocalTime;

@Entity
@Getter
public class BusinessHour {
    @Id
    @GeneratedValue
    @Column(name="business_hour_id")
    private Long id;

    @ManyToOne
    private Store store;

    private LocalTime startTime;
    private LocalTime endTime;
    private DayOfWeek dayOfWeek;
    private boolean isClosed;
}
