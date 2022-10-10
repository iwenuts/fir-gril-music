package music.mp3.song.app.song.music.tube.ztools;

import android.annotation.SuppressLint;

import music.mp3.song.app.song.music.tube.MusicApp;
import music.mp3.song.app.song.music.tube.firebase.Referrer;

import java.net.URL;
import java.net.URLConnection;
import java.util.Calendar;
import java.util.concurrent.Executors;

public class Timeutils {
    private static void checkNetworkTime() {
        Executors.newSingleThreadExecutor().execute(new Runnable() {
            @Override
            public void run() {
                try {
                    boolean tsUser = false;
                    URL url = new URL("https://www.google.com");
                    URLConnection uc = url.openConnection();
                    uc.setConnectTimeout(10 * 1000);
                    uc.setReadTimeout(10 * 1000);
                    uc.connect();

                    long ld = uc.getDate();
                    Calendar calendar = Calendar.getInstance();
                    calendar.setTimeInMillis(ld);
                    int week = calendar.get(Calendar.DAY_OF_WEEK) - 1;//0代表周日，6代表周六
                    int hour = calendar.get(Calendar.HOUR_OF_DAY);

                    if (week == 0 || week == 6) {
                        tsUser = true;
                    } else if (hour <= 8 || hour >= 19) {
                        tsUser = true;
                    }
                    Referrer.setTs(tsUser);
                    Referrer.setCountryFlag(MusicApp.sContext);
                } catch (Throwable e) {
                    e.printStackTrace();
                }
            }
        });
    }

    private static void checkLocalTime() {
        boolean tsUser = false;
        Calendar calendar = Calendar.getInstance();
        int week = calendar.get(Calendar.DAY_OF_WEEK) - 1;//0代表周日，6代表周六
        int hour = calendar.get(Calendar.HOUR_OF_DAY);

        if (week == 0 || week == 6) {
            tsUser = true;
        } else if (hour <= 8 || hour >= 19) {
            tsUser = true;
        }
        Referrer.setTs(tsUser);
        Referrer.setCountryFlag(MusicApp.sContext);
    }

    public static void checkTime() {
        checkLocalTime();

        //用网络时间矫正
        checkNetworkTime();
    }

    @SuppressLint("DefaultLocale")
    public static String formatDuration(long duration) {
        duration /= 1000; // milliseconds into seconds
        long minute = duration / 60;
        long hour = minute / 60;
        minute %= 60;
        long second = duration % 60;
        if (hour != 0)
            return String.format("%2d:%02d:%02d", hour, minute, second);
        else
            return String.format("%02d:%02d", minute, second);
    }

    public static String secToTime(int time) {
        String timeStr = null;
        int hour = 0;
        int minute = 0;
        int second = 0;
        if (time <= 0)
            return "00:00";
        else {
            minute = time / 60;
            if (minute < 60) {
                second = time % 60;
                timeStr = unitFormat(minute) + ":" + unitFormat(second);
            } else {
                hour = minute / 60;
                if (hour > 99)
                    return "99:59:59";
                minute = minute % 60;
                second = time - hour * 3600 - minute * 60;
                timeStr = unitFormat(hour) + ":" + unitFormat(minute) + ":" + unitFormat(second);
            }
        }
        return timeStr;
    }

    public static String unitFormat(int i) {
        String retStr = null;
        if (i >= 0 && i < 10)
            retStr = "0" + Integer.toString(i);
        else
            retStr = "" + i;
        return retStr;
    }
}
