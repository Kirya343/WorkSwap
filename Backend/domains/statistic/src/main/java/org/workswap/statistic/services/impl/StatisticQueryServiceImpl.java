package org.workswap.statistic.services.impl;

import java.text.NumberFormat;
import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.IntSummaryStatistics;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;
import org.workswap.statistic.dto.ListingsStatsMetricDTO;
import org.workswap.statistic.dto.OnlineStatsMetricsDTO;
import org.workswap.statistic.dto.UsersStatsMetricDTO;
import org.workswap.statistic.dto.ViewsStatsMetricDTO;
import org.workswap.statistic.enums.IntervalType;
import org.workswap.statistic.enums.StatSaveIntervalType;
import org.workswap.statistic.services.StatisticQueryService;
import org.workswap.listing.datasource.repository.ListingRepository;
import org.workswap.statistic.datasource.model.AllListingsStatSnapshot;
import org.workswap.statistic.datasource.model.ListingStatSnapshot;
import org.workswap.statistic.datasource.model.OnlineStatSnapshot;
import org.workswap.statistic.datasource.model.UsersStatSnapshot;
import org.workswap.statistic.datasource.repository.AllListingsStatRepository;
import org.workswap.statistic.datasource.repository.ListingStatRepository;
import org.workswap.statistic.datasource.repository.ListingViewRepository;
import org.workswap.statistic.datasource.repository.OnlineStatRepository;
import org.workswap.statistic.datasource.repository.UsersStatRepository;

import lombok.RequiredArgsConstructor;

@Service
@Profile({"server", "statistic"})
@RequiredArgsConstructor
public class StatisticQueryServiceImpl implements StatisticQueryService {

    private static final Logger logger = LoggerFactory.getLogger(StatisticQueryService.class);

    private final ListingStatRepository listingStatRepository;
    private final ListingRepository listingRepository;
    private final OnlineStatRepository onlineStatRepository;
    private final UsersStatRepository usersStatRepository;
    private final ListingViewRepository listingViewRepository;
    private final AllListingsStatRepository allListingsStatRepository;
   
    public long getTotalViews(Long userId) {

        return listingRepository.sumViewsByAuthorId(userId);
    }

    public int getMonthlyListingStats(Long listingId, int daysBack, String metric) {
        LocalDateTime dateEnd = LocalDateTime.now().minusDays(daysBack);
        LocalDateTime dateStart = LocalDateTime.now().minusDays(30 + daysBack);

        return countStats(listingId, dateStart, dateEnd, metric);
    }

    public Map<String, Object> getUserStats(Long userId, Locale locale) {
        Map<String, Object> stats = new HashMap<>();

        // Получаем статистику пользователя
        long totalViews = getTotalViews(userId);
        int totalResponses = 0;
        int completedDeals = 0;

        // Форматируем числа в зависимости от локали
        NumberFormat numberFormat = NumberFormat.getInstance(locale);

        stats.put("totalViews", numberFormat.format(totalViews));
        stats.put("totalResponses", numberFormat.format(totalResponses));
        stats.put("completedDeals", numberFormat.format(completedDeals));

        return stats;
    }

    private int countStats(Long listingId, LocalDateTime dateStart, LocalDateTime dateEnd, String metric) {
        ListingStatSnapshot statMin = listingStatRepository.findMinByMetric(listingId, dateStart, dateEnd, null, metric);
        ListingStatSnapshot statMax = listingStatRepository.findMaxByMetric(listingId, dateStart, dateEnd, null, metric);

        if (statMin == null || statMax == null) {
            return 0;
        }

        switch (metric) {
            case "views":
                return statMax.getViews() - statMin.getViews();
            case "favorites":
                return statMax.getFavorites() - statMin.getFavorites();
            case "rating":
                // Assuming rating is a double, you may want to return (int) or change return type
                return (int) (statMax.getRating() - statMin.getRating());
            default:
                throw new IllegalArgumentException("Unknown metric: " + metric);
        }
    }

    public int getLastOnlineSnapshot() {
        OnlineStatSnapshot snapshot = onlineStatRepository.findFirstByOrderByTimestampDesc();
        return snapshot.getOnline();
    }

    public OnlineStatsMetricsDTO getMonthlyMetrics() {
        List<OnlineStatSnapshot> snapshots = onlineStatRepository.findByTimestampAfter(LocalDateTime.now().minusMonths(1));

        if (snapshots.isEmpty()) {
            return new OnlineStatsMetricsDTO(0, 0, 0.0, 0.0, 0.0, 0.0, 0, LocalDate.now(), 0); // или кинуть exception
        }

        // Достаём список значений онлайна
        List<Integer> values = snapshots.stream()
                .map(OnlineStatSnapshot::getOnline)
                .sorted()
                .toList();

        IntSummaryStatistics stats = values.stream()
                .mapToInt(Integer::intValue)
                .summaryStatistics();

        int min = stats.getMin();
        int max = stats.getMax();
        double avg = stats.getAverage();

        // Медиана
        double median;
        int size = values.size();
        if (size % 2 == 0) {
            median = (values.get(size / 2 - 1) + values.get(size / 2)) / 2.0;
        } else {
            median = values.get(size / 2);
        }

        // p95
        int p95Index = (int) Math.ceil(0.95 * size) - 1;
        double p95 = values.get(Math.max(p95Index, 0));

        // Стандартное отклонение
        double variance = values.stream()
                .mapToDouble(v -> Math.pow(v - avg, 2))
                .sum() / size;
        double stdDev = Math.sqrt(variance);

        // Сумма человеко-часов (онлайн * время)
        // один снапшот = 15 секунд
        int totalUserSeconds = values.stream()
                .mapToInt(Integer::intValue)
                .sum() * 15;
        int totalUserHours = totalUserSeconds / 3600;

        Map<LocalDate, Double> avgByDay = snapshots.stream()
                .collect(Collectors.groupingBy(
                        snap -> snap.getTimestamp().toLocalDate(),
                        Collectors.averagingInt(OnlineStatSnapshot::getOnline)
                ));

        LocalDate peakDay = avgByDay.entrySet().stream()
                .max(Map.Entry.comparingByValue())
                .map(Map.Entry::getKey)
                .orElse(null);

        // 2. Час пика (по среднему за час суток)
        Map<Integer, Double> avgByHour = snapshots.stream()
                .collect(Collectors.groupingBy(
                        snap -> snap.getTimestamp().getHour(),
                        Collectors.averagingInt(OnlineStatSnapshot::getOnline)
                ));

        Integer peakHour = avgByHour.entrySet().stream()
                .max(Map.Entry.comparingByValue())
                .map(Map.Entry::getKey)
                .orElse(null);

        return new OnlineStatsMetricsDTO(
                min,
                max,
                avg,
                median,
                p95,
                stdDev,
                totalUserHours,
                peakDay,
                peakHour
        );
    }

    public UsersStatsMetricDTO getUsersCountMetrics(IntervalType intervalType, int multiplier) {
        if (multiplier < 1) throw new IllegalStateException("Множитель не может быть меньше 1");
        UsersStatSnapshot actual = usersStatRepository.findFirstByOrderByTimestampDesc();

        LocalDateTime cutoff = LocalDateTime.now()
            .minus(intervalType.getDuration().multipliedBy(multiplier));
        UsersStatSnapshot preview = usersStatRepository.findFirstByTimestampLessThanEqualOrderByTimestampDesc(cutoff);

        return new UsersStatsMetricDTO(
            actual.getUsersCount(), 
            actual.getStandartsUsers(), 
            actual.getTempUsers(), 
            actual.getUsersCount() - (preview != null ? preview.getUsersCount() : 0), 
            actual.getStandartsUsers() - (preview != null ? preview.getStandartsUsers() : 0), 
            actual.getTempUsers() - (preview != null ? preview.getTempUsers() : 0)
        );
    }

    public ListingsStatsMetricDTO getListingsCountMetrics(IntervalType intervalType, int multiplier) {
        if (multiplier < 1) throw new IllegalStateException("Множитель не может быть меньше 1");
        
        AllListingsStatSnapshot actual = allListingsStatRepository.findFirstByOrderByTimestampDesc();

        LocalDateTime cutoff = LocalDateTime.now()
            .minus(intervalType.getDuration().multipliedBy(multiplier));
        AllListingsStatSnapshot preview = allListingsStatRepository.findFirstByTimestampLessThanEqualOrderByTimestampDesc(cutoff);

        return new ListingsStatsMetricDTO(
            actual.getListingsCount(), 
            actual.getPublichedListings(),
            actual.getTempListings(), 
            actual.getListingsCount() - preview.getListingsCount(), 
            actual.getPublichedListings() - preview.getPublichedListings(), 
            actual.getTempListings() - preview.getTempListings()
        );
    }

    public ViewsStatsMetricDTO getViewsCountMetrics(IntervalType intervalType, int multiplier) {
        if (multiplier < 1) throw new IllegalStateException("Множитель не может быть меньше 1");

        int viewsTempCount = listingViewRepository.countByTemporary(true);
        int viewsStandartCount = listingViewRepository.countByTemporary(false);

        LocalDateTime cutoff = LocalDateTime.now()
            .minus(intervalType.getDuration().multipliedBy(multiplier));

        int viewsTempCountChange = listingViewRepository.countByTemporaryAndCreatedAtAfter(true, cutoff);
        int viewsStandartCountChange = listingViewRepository.countByTemporaryAndCreatedAtAfter(false, cutoff);

        return new ViewsStatsMetricDTO(
            viewsTempCount + viewsStandartCount,
            viewsStandartCount,
            viewsTempCount,
            viewsTempCountChange + viewsStandartCountChange,
            viewsStandartCountChange,
            viewsTempCountChange
        );
    }

    public List<Map<String, Object>> getListingStatGrath(
        Long listingId, StatSaveIntervalType interval, int days) {

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm dd.MM.yyyy");

        LocalDateTime now = LocalDateTime.now();
        LocalDateTime fromTime = LocalDateTime.now().minusDays(days);

        List<ListingStatSnapshot> stats = listingStatRepository.findByListingIdAndIntervalTypeAndTimeAfter(
                listingId,
                interval,
                fromTime
        );

        Optional<ListingStatSnapshot> lastBefore = listingStatRepository.findTopByListingIdAndIntervalTypeAndTimeBeforeOrderByTimeDesc(
                listingId,
                interval,
                fromTime
        );

        // Печать, если не было вообще данных
        if (stats.isEmpty() && lastBefore.isEmpty()) {
            logger.debug("Вообще нет данных в базе даже до fromTime");
        }

        int lastKnownViews = lastBefore.map(ListingStatSnapshot::getViews).orElse(0);

        logger.debug("Найдено снапшотов в базе: {}", stats.size());

        // Выбираем шаг в зависимости от типа интервала
        Duration step = switch (interval) {
            case FIVE_MINUTES -> Duration.ofMinutes(5);
            case HOURLY -> Duration.ofHours(1);
            case DAILY -> Duration.ofDays(1);
            case WEEKLY -> Duration.ofDays(7);
            // Добавь другие варианты, если нужно
        };

        Map<LocalDateTime, Integer> timeToViews = stats.stream()
            .collect(Collectors.toMap(
                s -> roundToStep(s.getTime(), step),
                ListingStatSnapshot::getViews,
                (a, b) -> b
            ));

        List<Map<String, Object>> chartData = new ArrayList<>();
        
        int fakePoints = 0;
        int realPoints = 0;
        int points = 0;

        for (LocalDateTime time = roundToStep(fromTime, step); !time.isAfter(now); time = time.plus(step)) {
            // Проверка, есть ли реальная точка в данных
            if (timeToViews.containsKey(time)) {
                lastKnownViews = timeToViews.get(time);
                realPoints++;
            } else {
                fakePoints++;            
            }
            points++;

            Map<String, Object> point = new HashMap<>();
            point.put("x", time.format(formatter)); // можно заменить на time.format(formatter) для читаемости
            point.put("y", lastKnownViews);
            chartData.add(point);
        }

        logger.debug("Всего точек на графике создано: {}", points);
        logger.debug("Реальных точек найдено: {}", realPoints);
        logger.debug("Фейковых точек создано: {}", fakePoints);

        return chartData;
    }

    private LocalDateTime roundToStep(LocalDateTime time, Duration step) {
        long seconds = step.getSeconds();
        long timestamp = time.toEpochSecond(ZoneOffset.UTC);
        long rounded = (timestamp / seconds) * seconds;
        return LocalDateTime.ofEpochSecond(rounded, 0, ZoneOffset.UTC);
    }
}