package com.applyflow.util;

import android.content.Context;
import android.content.SharedPreferences;

import androidx.appcompat.app.AppCompatDelegate;

public final class ThemeManager {

    private ThemeManager() {
    }

    private static SharedPreferences prefs(Context context) {
        return context.getApplicationContext()
                .getSharedPreferences(Constants.PREFS_NAME, Context.MODE_PRIVATE);
    }

    public static int getSavedMode(Context context) {
        return prefs(context).getInt(Constants.KEY_THEME_MODE, Constants.THEME_SYSTEM);
    }

    public static void applySavedMode(Context context) {
        AppCompatDelegate.setDefaultNightMode(toNightMode(getSavedMode(context)));
    }

    public static void setMode(Context context, int mode) {
        prefs(context).edit().putInt(Constants.KEY_THEME_MODE, mode).apply();
        AppCompatDelegate.setDefaultNightMode(toNightMode(mode));
    }

    private static int toNightMode(int mode) {
        switch (mode) {
            case Constants.THEME_LIGHT:
                return AppCompatDelegate.MODE_NIGHT_NO;
            case Constants.THEME_DARK:
                return AppCompatDelegate.MODE_NIGHT_YES;
            default:
                return AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM;
        }
    }

    public static int getFollowUpDays(Context context) {
        return prefs(context).getInt(Constants.KEY_FOLLOWUP_DAYS, Constants.DEFAULT_FOLLOWUP_DAYS);
    }

    public static void setFollowUpDays(Context context, int days) {
        prefs(context).edit().putInt(Constants.KEY_FOLLOWUP_DAYS, days).apply();
    }
}
