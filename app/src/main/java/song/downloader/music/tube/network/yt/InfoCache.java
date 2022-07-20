/*
 * Copyright 2017 Mauricio Colli <mauriciocolli@outlook.com>
 * InfoCache.java is part of NewPipe
 *
 * License: GPL-3.0+
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see <http://www.gnu.org/licenses/>.
 */

package song.downloader.music.tube.network.yt;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.collection.LruCache;
import android.util.Log;

import org.schabi.newpipe.extractor.Info;
import org.schabi.newpipe.extractor.InfoItem;

import java.util.Map;


public final class InfoCache {
    private static final boolean DEBUG = false;
    private final String TAG = getClass().getSimpleName();

    private static final InfoCache instance = new InfoCache();
    private static final int MAX_ITEMS_ON_CACHE = 60;
    /**
     * Trim the cache to this size
     */
    private static final int TRIM_CACHE_TO = 30;

    private static final LruCache<String, CacheData> lruCache = new LruCache<>(MAX_ITEMS_ON_CACHE);

    private InfoCache() {
        //no instance
    }

    public static InfoCache getInstance() {
        return instance;
    }

    @Nullable
    public Info getFromKey(int serviceId, @NonNull String url, @NonNull InfoItem.InfoType infoType) {
        if (DEBUG)
            Log.d(TAG, "getFromKey() called with: serviceId = [" + serviceId + "], url = [" + url + "]");
        synchronized (lruCache) {
            return getInfo(keyOf(serviceId, url, infoType));
        }
    }

    public void putInfo(int serviceId, @NonNull String url, @NonNull Info info, @NonNull InfoItem.InfoType infoType) {
        if (DEBUG) Log.d(TAG, "putInfo() called with: info = [" + info + "]");

        final long expirationMillis = ServiceHelper.getCacheExpirationMillis(info.getServiceId());
        synchronized (lruCache) {
            final CacheData data = new CacheData(info, expirationMillis);
            lruCache.put(keyOf(serviceId, url, infoType), data);
        }
    }

    public void removeInfo(int serviceId, @NonNull String url, @NonNull InfoItem.InfoType infoType) {
        if (DEBUG)
            Log.d(TAG, "removeInfo() called with: serviceId = [" + serviceId + "], url = [" + url + "]");
        synchronized (lruCache) {
            lruCache.remove(keyOf(serviceId, url, infoType));
        }
    }

    public void clearCache() {
        if (DEBUG) Log.d(TAG, "clearCache() called");
        synchronized (lruCache) {
            lruCache.evictAll();
        }
    }

    public void trimCache() {
        if (DEBUG) Log.d(TAG, "trimCache() called");
        synchronized (lruCache) {
            removeStaleCache();
            lruCache.trimToSize(TRIM_CACHE_TO);
        }
    }

    public long getSize() {
        synchronized (lruCache) {
            return lruCache.size();
        }
    }

    @NonNull
    private static String keyOf(final int serviceId, @NonNull final String url, @NonNull InfoItem.InfoType infoType) {
        return serviceId + url + infoType.toString();
    }

    private static void removeStaleCache() {
        for (Map.Entry<String, CacheData> entry : InfoCache.lruCache.snapshot().entrySet()) {
            final CacheData data = entry.getValue();
            if (data != null && data.isExpired()) {
                InfoCache.lruCache.remove(entry.getKey());
            }
        }
    }

    @Nullable
    private static Info getInfo(@NonNull final String key) {
        final CacheData data = InfoCache.lruCache.get(key);
        if (data == null) return null;

        if (data.isExpired()) {
            InfoCache.lruCache.remove(key);
            return null;
        }

        return data.info;
    }

    final private static class CacheData {
        final private long expireTimestamp;
        final private Info info;

        private CacheData(@NonNull final Info info, final long timeoutMillis) {
            this.expireTimestamp = System.currentTimeMillis() + timeoutMillis;
            this.info = info;
        }

        private boolean isExpired() {
            return System.currentTimeMillis() > expireTimestamp;
        }
    }
}
