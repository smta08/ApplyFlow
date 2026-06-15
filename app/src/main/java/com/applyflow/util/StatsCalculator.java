package com.applyflow.util;

import com.applyflow.data.db.ApplicationEntity;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.TreeMap;

public final class StatsCalculator {

    private StatsCalculator() {
    }

    public static final class MonthCount {
        public final String key;
        public final String label;
        public final int count;

        MonthCount(String key, String label, int count) {
            this.key = key;
            this.label = label;
            this.count = count;
        }
    }

    public static final class Stats {
        public int total;
        public final Map<String, Integer> countByStatus = new LinkedHashMap<>();
        public int responseRate;
        public int interviewRate;
        public int offerRate;
        public int avgDaysSinceApplied = -1;
        public final List<MonthCount> overTime = new ArrayList<>();
    }

    public static Stats compute(List<ApplicationEntity> applications) {
        Stats stats = new Stats();
        for (String status : Constants.STATUSES) {
            stats.countByStatus.put(status, 0);
        }
        if (applications == null || applications.isEmpty()) {
            return stats;
        }

        stats.total = applications.size();

        int responded = 0;
        int interviewing = 0;
        int offers = 0;
        long daysSum = 0;
        int daysCount = 0;
        Map<String, Integer> months = new TreeMap<>();

        for (ApplicationEntity app : applications) {
            Integer current = stats.countByStatus.get(app.status);
            stats.countByStatus.put(app.status, (current == null ? 0 : current) + 1);

            if (!Constants.STATUS_APPLIED.equals(app.status)) {
                responded++;
            }
            if (Constants.STATUS_INTERVIEW.equals(app.status)
                    || Constants.STATUS_FINAL_ROUND.equals(app.status)
                    || Constants.STATUS_OFFER_RECEIVED.equals(app.status)) {
                interviewing++;
            }
            if (Constants.STATUS_OFFER_RECEIVED.equals(app.status)) {
                offers++;
            }

            int days = DateUtils.daysBetweenDateAndToday(app.dateApplied);
            if (days >= 0) {
                daysSum += days;
                daysCount++;
            }

            String monthKey = DateUtils.monthKey(app.dateApplied);
            if (monthKey != null) {
                Integer mc = months.get(monthKey);
                months.put(monthKey, (mc == null ? 0 : mc) + 1);
            }
        }

        stats.responseRate = percent(responded, stats.total);
        stats.interviewRate = percent(interviewing, stats.total);
        stats.offerRate = percent(offers, stats.total);
        if (daysCount > 0) {
            stats.avgDaysSinceApplied = (int) Math.round((double) daysSum / daysCount);
        }

        for (Map.Entry<String, Integer> entry : months.entrySet()) {
            stats.overTime.add(new MonthCount(
                    entry.getKey(), monthLabel(entry.getKey()), entry.getValue()));
        }

        return stats;
    }

    private static int percent(int part, int total) {
        return total == 0 ? 0 : (int) Math.round(100.0 * part / total);
    }

    private static String monthLabel(String key) {
        try {
            return new SimpleDateFormat("MMM yyyy", Locale.US)
                    .format(new SimpleDateFormat("yyyy-MM", Locale.US).parse(key));
        } catch (ParseException e) {
            return key;
        }
    }
}
