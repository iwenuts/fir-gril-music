/*
 * Copyright 2019 Google LLC
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package music.mp3.song.app.song.music.tube.firebase;

import android.content.Context;
import android.content.pm.PackageManager;
import android.os.AsyncTask;
import android.text.TextUtils;

import com.android.volley.AuthFailureError;
import com.android.volley.ClientError;
import com.android.volley.NetworkError;
import com.android.volley.NetworkResponse;
import com.android.volley.NoConnectionError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.TimeoutError;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.HttpHeaderParser;
import com.android.volley.toolbox.RequestFuture;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.ads.identifier.AdvertisingIdClient;
import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.common.GooglePlayServicesRepairableException;
import music.mp3.song.app.song.music.tube.BuildConfig;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;


/**
 * CampaignTrack task
 */
public class CampaignTrackTask extends AsyncTask<Void, Void, Void> {

    private int retryTimes;
    private int timeout;
    private int lookbackWindow;
    private String token;
    private String linkId;
    private Context context;

    private CampaignTracker campaignTracker;
    private DDLcallback successCallback;
    private DDLcallback failCallback;


    /**
     * 初始化构造函数
     *
     * @param token           广告的devToken, 用来调用app conversion tracking API
     * @param linkId          对应的app的linkid, 用来调用app conversion tracking API
     * @param context         应用执行上下文
     * @param retryTimes      重试次数，不重试设为0
     * @param timeout         单位是milliseconds
     * @param lookbackWindow  回看的窗口，例如只看过去30天内是否有关联的广告点击
     * @param successCallback 获取到广告点击事件后的回调逻辑
     * @param failCallback    失败的广告回调逻辑
     */
    public CampaignTrackTask(String token, String linkId, Context context, int retryTimes, int timeout, int lookbackWindow, DDLcallback successCallback, DDLcallback failCallback) {
        this.retryTimes = retryTimes;
        this.timeout = timeout;
        this.lookbackWindow = lookbackWindow;
        this.token = token;
        this.linkId = linkId;
        this.context = context;
        this.successCallback = successCallback;
        this.failCallback = failCallback;
    }

    @Override
    protected Void doInBackground(Void... voids) {
        campaignTracker = new CampaignTracker(token, linkId, timeout, context);

        if (!campaignTracker.selfcheckPass()) {
            Referrer.sendReferrer("", "5:rdid empty", "s2s");
            return null;
        }

        campaignTracker.acquireCampaignInfo();
        for (int i = 0; i < retryTimes; i++) {

            // Escape early if no need to retry
            if (!campaignTracker.isNeedRetry()) break;

            // Escape early if cancel() is called
            if (isCancelled()) break;

            campaignTracker.acquireCampaignInfo();
        }
        return null;
    }

    /**
     * execute after do in doInBackground
     */
    @Override
    protected void onPostExecute(Void result) {

        String campaignId = campaignTracker.getCampaignIdWithinDays(lookbackWindow);

        if (campaignId != null && !campaignId.isEmpty()) {

            // todo call server api to get the mapped deep link
            successCallback.execute(campaignId);

        } else {
            // todo fail to get the campaign information
            failCallback.execute("");
        }

    }


    /**
     * inner class, campaign tracker
     */
    class CampaignTracker {

        private String devToken;

        private String linkId;

        private String appEventType = "first_open";

        private String rdid;

        private String idType = "advertisingid";

        private String lat;

        private String appVersion;

        private String osVersion;

        private String sdkVersion;

        private AdvertisingIdClient.Info adInfo;

        private Context context;

        private String campaignId;

        private String campaignName;

        private double adClickTime = 0;

        private boolean isAttributed = false;

        // milliseconds
        private int timeout;

        private boolean needRetry = false;

        // 可定制化的重试回退时间, 重试次数大于配置的重试时间个数时，默认为3秒
        private int[] backoffTimes = {1, 3, 10, 20, 60, 120, 300};

        // 累计回退次数
        private int backoffCount = 0;

        // 回退时间
        private int backOffTime = 0;

        public CampaignTracker(String devToken, String linkId, int timeout, Context context) {

            this.devToken = devToken;
            this.linkId = linkId;
            this.context = context;
            this.timeout = timeout;

            setAppVersion(context);
            sdkVersion = appVersion;

            setOsVersion();
            initAdInfo(context);
        }

        public boolean selfcheckPass() {
            // mainly check rdid is obtained
            return rdid != null && !rdid.isEmpty();
        }

        public boolean isNeedRetry() {
            return needRetry;
        }

        public String getCampaignId() {
            return campaignId;
        }

        public String getCampaignName() {
            return campaignName;
        }

        public boolean isAttributed() {
            return isAttributed;
        }

        /**
         * 根据传入时间回看对应时间窗口内是否有广告点击
         *
         * @param days 回看时间窗口，天为单位
         * @return
         */
        public String getCampaignIdWithinDays(int days) {
            if (isNotInLookbackWindow(days)) return null;
            return campaignId;
        }


        /**
         * 根据传入时间回看对应时间窗口内是否有广告点击
         *
         * @param days 回看时间窗口，天为单位
         * @return
         */
        public String getCampaignNameWithinDays(int days) {
            if (isNotInLookbackWindow(days)) return null;
            return campaignName;
        }


        /**
         * 获取广告点击信息
         */
        public void acquireCampaignInfo() {
            RequestFuture<String> future = RequestFuture.newFuture();
            String curTimestampInSeconds = getCurTimestampInSeconds();

            String trackingURL = "https://www.googleadservices.com/pagead/conversion/app/1.0?dev_token=" + devToken + "&link_id=" + linkId + "&app_event_type=" + appEventType + "&rdid=" + rdid + "&id_type=" + idType + "&lat=" + lat + "&app_version=" + appVersion + "&os_version=" + osVersion + "&sdk_version=" + sdkVersion + "&timestamp=" + curTimestampInSeconds;

            StringRequest request = new StringRequest(Request.Method.POST, trackingURL, future, future) {
                @Override
                public Map<String, String> getHeaders() throws AuthFailureError {

                    Map<String, String> param = new HashMap<String, String>();
                    param.put("Content-Type", "application/json; charset=utf-8");

                    // no body, manually set the content-length to 0
                    param.put("Content-Length", "0");
                    return param;
                }

            };
            RequestQueue queue = Volley.newRequestQueue(this.context);
            queue.add(request);

            try {
                String response = future.get(timeout, TimeUnit.MILLISECONDS); // this will block

                parseSuccessResult(new JSONObject(response));

                Referrer.sendReferrer("", "100:response", "s2s");
            } catch (InterruptedException e) {
                // exception handling
                Referrer.sendReferrer("", "7", "s2s");
                parseFailResult(e);
            } catch (ExecutionException e) {
                // exception handling
                Object error = e.getCause();
                if (error instanceof TimeoutError || isNetworkProblem(error)) {
                    this.needRetry = true;
                    Referrer.sendReferrer("", "8:retry", "s2s");
                } else if (isClientProblem(error)) {
                    handleClientError(error);
                    Referrer.sendReferrer("", "15", "s2s");
                } else {
                    Referrer.sendReferrer("", "16", "s2s");
                }
                parseFailResult(e);
            } catch (TimeoutException e) {
                this.needRetry = true;
                Referrer.sendReferrer("", "9", "s2s");
                parseFailResult(e);
            } catch (JSONException e) {
                Referrer.sendReferrer("", "10", "s2s");
                parseFailResult(e);

            } catch (Exception e) {
                Referrer.sendReferrer("", "11", "s2s");
                parseFailResult(e);
            }
        }

        /**
         * 根据传入时间判断广告点击时间是否在回溯窗口内
         *
         * @param days
         * @return
         */
        private boolean isNotInLookbackWindow(int days) {
            if (days <= 0 || days >= 365) {
                //todo 打点，记录参数非法
                Referrer.sendReferrer("", "22" + days, "s2s");
                return true;
            }
            if (!isAttributed) {
                return true;
            }
            Calendar calendar = Calendar.getInstance();
            calendar.add(Calendar.DAY_OF_YEAR, 0 - days);
            if (calendar.getTime().getTime() > adClickTime * 1000) {
                // todo 打点，记录campaign信息被过滤
                Referrer.sendReferrer("", "17:cid timeout", "s2s");
                return true;
            }
            //todo 打点，记录campaign信息成功获取到
            Referrer.sendReferrer("", "18:campaign succ:cid valid", "s2s");
            return false;
        }


        private String getCurTimestampInSeconds() {
            double curTimestampInSeconds = Calendar.getInstance().getTimeInMillis() / 1000.0;

            return String.format("%.6f", curTimestampInSeconds - backOffTime);

        }

        /**
         * 计算回退时间
         *
         * @return
         */
        private int calBackOffTime() {
            int backTime = 3; // 默认3s

            // 重试次数在配置的重试时间个数以内
            if (backoffCount <= backoffTimes.length - 1) {
                backTime = backoffTimes[backoffCount];
            }
            backoffCount++;
            return backTime;

        }

        /**
         * 处理客户端异常，尤其是http-400错误
         *
         * @param error
         */
        private void handleClientError(Object error) {

            VolleyError er = (VolleyError) error;
            NetworkResponse response = er.networkResponse;
            if (response != null && response.statusCode == 400) {
                Referrer.sendReferrer("", "19", "s2s");
                JSONObject jsonObject = null;
                try {
                    jsonObject = new JSONObject(new String(response.data, HttpHeaderParser.parseCharset(response.headers)));
                    JSONArray errors = jsonObject.getJSONArray("errors");
                    if (errors.length() == 0) {
                        return;
                    }
                    for (int i = 0; i < errors.length(); i++) {
                        //for timestamp_invalid error code, need retry

                        if (errors.get(i).toString().equals("timestamp_invalid")) {
                            this.needRetry = true;
                            this.backOffTime = calBackOffTime();
                            return;
                        }
                    }
                } catch (JSONException e) {
                    Referrer.sendReferrer("", "20", "s2s");
                    parseFailResult(e);

                } catch (UnsupportedEncodingException e) {
                    Referrer.sendReferrer("", "21", "s2s");
                    parseFailResult(e);
                }
            }

        }

        private boolean isClientProblem(Object error) {
            return error instanceof ClientError;
        }

        private boolean isNetworkProblem(Object error) {
            return (error instanceof NetworkError || error instanceof NoConnectionError);
        }

        private void parseSuccessResult(JSONObject response) {
            try {
                JSONObject jsonObject = response;

                this.isAttributed = jsonObject.getBoolean("attributed");
                this.needRetry = false;
                JSONArray adEvents = jsonObject.getJSONArray("ad_events");
                if (!isAttributed) {
                    //todo 打点记录请求成功，但未找到关联的广告点击信息
                    Referrer.sendReferrer("", "6:isAttributed false", "s2s");
                    return;
                }
                for (int i = 0; i < adEvents.length(); i++) {
                    JSONObject adEvent = (JSONObject) adEvents.get(i);
                    double curAdClickTime = adEvent.getDouble("timestamp");
                    String campaignType = adEvent.getString("campaign_type");
                    if (campaignType != null && !campaignType.equals("UAC")) {
                        continue;
                    }
                    //get the most recently click campaign
                    if (curAdClickTime > adClickTime) {
                        adClickTime = curAdClickTime;
                        campaignId = adEvent.getString("campaign_id");
                        campaignName = adEvent.getString("campaign_name");
                    }
                }
                //todo 打点记录请求成功，找到了关联的广告点击信息
                if (!TextUtils.isEmpty(campaignId)) {
                    Referrer.sendReferrer("", "12:isAttributed true:found cid", "s2s");
                } else {
                    Referrer.sendReferrer("", "13:isAttributed true:no cid", "s2s");
                }
            } catch (JSONException e) {
                Referrer.sendReferrer("", "14", "s2s");
                parseFailResult(e);
            }
        }

        private void parseFailResult(Exception e) {
            //todo 打点记录错误信息
            Referrer.sendReferrer(e.getMessage(), "-1:parseFailResult", "s2s");
        }

        // Do not call this function from the main thread. Otherwise,
        // an IllegalStateException will be thrown.
        private AdvertisingIdClient.Info fetchAdInfo(Context context) {

            AdvertisingIdClient.Info adInfo = null;
            try {
                adInfo = AdvertisingIdClient.getAdvertisingIdInfo(context.getApplicationContext());

            } catch (GooglePlayServicesNotAvailableException e) {
                Referrer.sendReferrer("", "2", "s2s");
                parseFailResult(e);
            } catch (GooglePlayServicesRepairableException e) {
                Referrer.sendReferrer("", "3", "s2s");
                parseFailResult(e);
            } catch (IOException e) {
                Referrer.sendReferrer("", "4", "s2s");
                parseFailResult(e);
            }
            return adInfo;
        }


        private void setAppVersion(Context context) {
            try {
                PackageManager packageManager = context.getPackageManager();
                appVersion = packageManager.getPackageInfo(context.getPackageName(),
                        PackageManager.GET_META_DATA).versionName;
            } catch (PackageManager.NameNotFoundException e) {
                appVersion = BuildConfig.VERSION_NAME;
                Referrer.sendReferrer("", "1", "s2s");
                parseFailResult(e);
            }
        }

        private void setOsVersion() {
            osVersion = String.valueOf(android.os.Build.VERSION.RELEASE);

        }

        private void initAdInfo(final Context context) {
            adInfo = fetchAdInfo(context);
            if (adInfo != null) {
                rdid = adInfo.getId();
                lat = adInfo.isLimitAdTrackingEnabled() ? "1" : "0";
            }
        }

    }
}
