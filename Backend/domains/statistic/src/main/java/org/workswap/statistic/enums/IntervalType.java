package org.workswap.statistic.enums;

import java.time.Duration;

import lombok.Getter;

@Getter
public enum IntervalType {
    MINUTE(Duration.ofMinutes(1)),
    HOUR(Duration.ofHours(1)),
    DAY(Duration.ofDays(1)),
    WEEK(Duration.ofDays(7)),
    MONTH(Duration.ofDays(7));

    IntervalType(Duration duration) {
        this.duration = duration;
    }
    
    private Duration duration;
}

