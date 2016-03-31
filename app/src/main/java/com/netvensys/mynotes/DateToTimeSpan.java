package com.netvensys.mynotes;

/**
 * Created by Srinivasa.Nettem on 3/3/2016.
 */
public class DateToTimeSpan {

    /*
     * converts a date to a relative time span ("8h", "3d", etc.)
     */
    public static String DateToTimeSpan(final long NoteCreatedDate) {
        final String timeSpan;
        if (NoteCreatedDate == 0)
            return "";

        //long passedTime = date.getTime();
        long currentTime = System.currentTimeMillis();

        // return "now" if less than a minute has elapsed
        long secondsSince = (currentTime - NoteCreatedDate) / 1000;
        if (secondsSince < 60)
            return "now";

        // less than an hour (ex: 12m)
        long minutesSince = secondsSince / 60;
        if (minutesSince < 60)
            return Long.toString(minutesSince) + "m";

        // less than a day (ex: 17h)
        long hoursSince = minutesSince / 60;
        if (hoursSince < 24)
            return Long.toString(hoursSince) + "h";

        // less than a week (ex: 5d)
        long daysSince = hoursSince / 24;
        if (daysSince < 7)
            return Long.toString(daysSince) + "d";

        return Long.toString(daysSince/7) + "wk";
        // less than a year old, so return day/month without year (ex: Jan 30)
        //if (daysSince < 365)
            //return DateUtils.formatDateTime(WordPress.getContext(), passedTime, DateUtils.FORMAT_NO_YEAR | DateUtils.FORMAT_ABBREV_ALL);

        // date is older, so include year (ex: Jan 30, 2013)
        //return DateUtils.formatDateTime(WordPress.getContext(), passedTime, DateUtils.FORMAT_ABBREV_ALL);
    }
}
