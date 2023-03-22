package com.twogather.twogatherwebbackend.domain;

import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.time.DayOfWeek;
import java.time.LocalTime;

@Entity
@Getter
@NoArgsConstructor
public class BusinessHour {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name="business_hour_id")
    private Long businessHourId;

    @ManyToOne
    @JoinColumn(name = "store_id")
    private Store store;

    private LocalTime startTime;
    private LocalTime endTime;
    private DayOfWeek dayOfWeek;
    private boolean isOpen;

    public void updateStartTime(LocalTime startTime){
        if(startTime!=null && !startTime.equals(this.startTime)){
            this.startTime = startTime;
        }
    }
    public void updateEndTime(LocalTime endTime){
        if(endTime!=null && !endTime.equals(this.endTime)){
            this.endTime = endTime;
        }
    }
    public void updateDayOfWeek(DayOfWeek dayOfWeek){
        if(dayOfWeek!=null && !dayOfWeek.equals(this.dayOfWeek)){
            this.dayOfWeek = dayOfWeek;
        }
    }
    public void updateIsClosed(boolean isClosed){
        if(isClosed != this.isOpen){
            this.isOpen = isClosed;
        }
    }
    public BusinessHour(Long id, LocalTime startTime, LocalTime endTime, DayOfWeek dayOfWeek, boolean isOpen){
        this.businessHourId = id;
        this.startTime = startTime;
        this.endTime = endTime;
        this.dayOfWeek = dayOfWeek;
        this.isOpen = isOpen;
    }
    public BusinessHour(Store store, LocalTime startTime, LocalTime endTime, DayOfWeek dayOfWeek, boolean isOpen){
        this.store = store;
        this.startTime = startTime;
        this.endTime = endTime;
        this.dayOfWeek = dayOfWeek;
        this.isOpen = isOpen;
    }
}
