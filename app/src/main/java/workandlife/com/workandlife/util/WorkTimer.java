package workandlife.com.workandlife.util;

import android.os.SystemClock;
import android.util.Log;

import java.util.Calendar;

public class WorkTimer {
    private final static String TAG = WorkTimer.class.getSimpleName();

    private static long started = 0;
    private static long paused = 0;
    private static long stopped = 0;
    private static long workedTime = 0;
    private static long pausedTime = 0;
    private static long totalTime = 0;

    private static Calendar resetTime = Calendar.getInstance();

    public static void start() {
        started = SystemClock.elapsedRealtime();
    }

    public static void pause() {
        paused = SystemClock.elapsedRealtime();
    }

    public static void resume() {
        pausedTime = pausedTime + (SystemClock.elapsedRealtime() - paused);
    }

    public static String stop() {
        totalTime = SystemClock.elapsedRealtime() - started;
        workedTime = totalTime - pausedTime;
        return formatTime(workedTime);
    }

    public static void reset( ){

    }

    public static Calendar getResetTime() {
        return resetTime;
    }

    public static void setResetTime(Calendar calendar) {

    }

    private static String formatTime (long millis) {
        String output = "00:00";
        long seconds = millis / 1000;
        long minutes = seconds / 60;
        long hours = minutes / 60;

        minutes = minutes % 60;
        hours = hours % 60;

        String minutesD = String.valueOf(minutes);
        String hoursD = String.valueOf(hours);

        if (minutes < 10)
            minutesD = "0" + minutes;
        if (hours < 10)
            hoursD = "0" + hours;

        output = hoursD + "h" + minutesD + "m";
        return output;
    }
}
