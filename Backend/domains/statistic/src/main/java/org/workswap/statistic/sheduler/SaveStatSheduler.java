package org.workswap.statistic.sheduler;

import org.springframework.context.annotation.Profile;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.workswap.statistic.enums.StatSaveIntervalType;
import org.workswap.statistic.services.StatisticCommandService;

import lombok.RequiredArgsConstructor;

@Service
@Profile("statistic")
@RequiredArgsConstructor
public class SaveStatSheduler {

    private final StatisticCommandService statisticCommandService;
    
    @Scheduled(fixedRate = 5 * 60 * 1000) // 5 минут (5 * 60 * 1000)
    public void create5minStatSnapshot() {
        statisticCommandService.saveListingsStat(StatSaveIntervalType.FIVE_MINUTES);
    }

    @Scheduled(fixedRate = 60 * 60 * 1000) // 5 минут (5 * 60 * 1000)
    public void createHourStatSnapshot() {
        statisticCommandService.saveListingsStat(StatSaveIntervalType.HOURLY);
    }

    @Scheduled(cron = "0 0 0 * * *", zone = "Europe/Helsinki")
    public void createDayStatSnapshot() {
        statisticCommandService.saveListingsStat(StatSaveIntervalType.DAILY);
    }

    @Scheduled(cron = "0 0 0 * * SUN", zone = "Europe/Helsinki")
    public void createWeekStatSnapshot() {
        statisticCommandService.saveListingsStat(StatSaveIntervalType.WEEKLY);
    }

    @Scheduled(cron = "0 0 3 * * *")
    public void cleanUpDuplicateListingsStat() {
        statisticCommandService.cleanUpDuplicateListingsStat();
    }
}
