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

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "store_id")
    private Store store;

    private LocalTime startTime;
    private LocalTime endTime;
    private DayOfWeek dayOfWeek;
    private Boolean isOpen;
    private Boolean hasBreakTime;
    private LocalTime breakStartTime;
    private LocalTime breakEndTime;

    public void update(LocalTime startTime, LocalTime endTime, DayOfWeek dayOfWeek, Boolean isOpen,
                       Boolean hasBreakTime, LocalTime breakStartTime, LocalTime breakEndTime){
        if(startTime!=null){
            this.startTime = startTime;
        }
        if(endTime!=null){
            this.endTime = endTime;
        }
        if(dayOfWeek!=null){
            this.dayOfWeek = dayOfWeek;
        }
        if(breakStartTime!=null){
            this.breakStartTime = breakStartTime;
        }
        if(breakEndTime!=null){
            this.breakEndTime = breakEndTime;
        }
        if(isOpen!=null){
            this.isOpen = isOpen;
        }
        if(hasBreakTime!=null){
            this.hasBreakTime = hasBreakTime;
        }

    }
    public BusinessHour(Long id, LocalTime startTime, LocalTime endTime, DayOfWeek dayOfWeek, Boolean isOpen,
                        Boolean hasBreakTime, LocalTime breakStartTime, LocalTime breakEndTime){
        this.businessHourId = id;
        this.startTime = startTime;
        this.endTime = endTime;
        this.dayOfWeek = dayOfWeek;
        this.isOpen = isOpen;
        this.hasBreakTime = hasBreakTime;
        this.breakStartTime = breakStartTime;
        this.breakEndTime = breakEndTime;
    }
    public BusinessHour(Store store, LocalTime startTime, LocalTime endTime, DayOfWeek dayOfWeek, Boolean isOpen,
                        Boolean hasBreakTime, LocalTime breakStartTime, LocalTime breakEndTime){
        this.store = store;
        this.startTime = startTime;
        this.endTime = endTime;
        this.dayOfWeek = dayOfWeek;
        this.isOpen = isOpen;
        this.hasBreakTime = hasBreakTime;
        this.breakStartTime = breakStartTime;
        this.breakEndTime = breakEndTime;
    }
    public BusinessHour(Long id,Store store, LocalTime startTime, LocalTime endTime, DayOfWeek dayOfWeek, Boolean isOpen,
                        Boolean hasBreakTime, LocalTime breakStartTime, LocalTime breakEndTime){
        this.businessHourId = id;
        this.store = store;
        this.startTime = startTime;
        this.endTime = endTime;
        this.dayOfWeek = dayOfWeek;
        this.isOpen = isOpen;
        this.hasBreakTime = hasBreakTime;
        this.breakStartTime = breakStartTime;
        this.breakEndTime = breakEndTime;
    }
}
