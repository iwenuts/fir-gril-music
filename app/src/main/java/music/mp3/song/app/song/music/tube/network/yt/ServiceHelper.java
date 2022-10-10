package music.mp3.song.app.song.music.tube.network.yt;

import org.schabi.newpipe.extractor.StreamingService;

import java.util.concurrent.TimeUnit;

import static org.schabi.newpipe.extractor.ServiceList.SoundCloud;

public class ServiceHelper {

    public static long getCacheExpirationMillis(final int serviceId) {
        if (serviceId == SoundCloud.getServiceId()) {
            return TimeUnit.MILLISECONDS.convert(5, TimeUnit.MINUTES);
        } else {
            return TimeUnit.MILLISECONDS.convert(1, TimeUnit.HOURS);
        }
    }

    public static boolean isBeta(final StreamingService s) {
        switch (s.getServiceInfo().getName()) {
            case "YouTube":
                return false;
            default:
                return true;
        }
    }
}
