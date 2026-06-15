package com.applyflow;

import static org.junit.Assert.assertEquals;

import com.applyflow.data.db.ApplicationEntity;
import com.applyflow.util.Constants;
import com.applyflow.util.DateUtils;
import com.applyflow.util.StatsCalculator;

import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

public class StatsCalculatorTest {

    private ApplicationEntity app(String status, String dateApplied) {
        return new ApplicationEntity("Co", "Role", null, status, null, dateApplied,
                "2026-01-01T00:00:00");
    }

    @Test
    public void emptyListGivesZeroTotals() {
        StatsCalculator.Stats stats = StatsCalculator.compute(new ArrayList<>());
        assertEquals(0, stats.total);
        assertEquals(0, stats.responseRate);
        assertEquals(-1, stats.avgDaysSinceApplied);
    }

    @Test
    public void computesRatesAndCounts() {
        List<ApplicationEntity> apps = new ArrayList<>();
        apps.add(app(Constants.STATUS_APPLIED, DateUtils.dateMinusDays(10)));
        apps.add(app(Constants.STATUS_PHONE_SCREEN, DateUtils.dateMinusDays(10)));
        apps.add(app(Constants.STATUS_INTERVIEW, DateUtils.dateMinusDays(10)));
        apps.add(app(Constants.STATUS_OFFER_RECEIVED, DateUtils.dateMinusDays(10)));

        StatsCalculator.Stats stats = StatsCalculator.compute(apps);

        assertEquals(4, stats.total);
        assertEquals(1, (int) stats.countByStatus.get(Constants.STATUS_APPLIED));
        assertEquals(1, (int) stats.countByStatus.get(Constants.STATUS_OFFER_RECEIVED));
        assertEquals(75, stats.responseRate);
        assertEquals(50, stats.interviewRate);
        assertEquals(25, stats.offerRate);
        assertEquals(10, stats.avgDaysSinceApplied);
    }

    @Test
    public void ignoresNullDatesInAverageAndBuckets() {
        List<ApplicationEntity> apps = new ArrayList<>();
        apps.add(app(Constants.STATUS_APPLIED, null));
        apps.add(app(Constants.STATUS_APPLIED, DateUtils.dateMinusDays(4)));

        StatsCalculator.Stats stats = StatsCalculator.compute(apps);

        assertEquals(2, stats.total);
        assertEquals(4, stats.avgDaysSinceApplied);
        assertEquals(1, stats.overTime.size());
    }
}
