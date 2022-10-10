package music.mp3.song.app.song.music.tube.bean;

import java.io.Serializable;
import java.util.List;

public
class Mp3JuiceBean extends BaseBean implements Serializable {

    private List<ItemsBean> items;

    public List<ItemsBean> getItems() {
        return items;
    }

    public void setItems(List<ItemsBean> items) {
        this.items = items;
    }

    public static class ItemsBean implements Serializable {
        /**
         * id : KY44zvhWhp4
         * url : https://www.youtube.com/watch?v=KY44zvhWhp4
         * title : Lil Wayne - Love Me ft. Drake, Future (Explicit) (Official Music Video)
         * thumbHigh : https://i.ytimg.com/vi/KY44zvhWhp4/mqdefault.jpg
         * channelTitle : Lil Wayne
         * channelId : UCO9zJy7HWrIS3ojB4Lr7Yqw
         * channelUrl : https://www.youtube.com/channel/UCO9zJy7HWrIS3ojB4Lr7Yqw
         * publishedAt : il y a 8 ans
         * duration : 4:25
         * viewCount : 421292139
         */

        private String id;
        private String url;
        private String title;
        private String thumbHigh;
        private String channelTitle;
        private String channelId;
        private String channelUrl;
        private String publishedAt;
        private String duration;
        private String viewCount;

        public String getId() {
            return id == null ? "" : id;
        }

        public void setId(String id) {
            this.id = id;
        }

        public String getUrl() {
            return url == null ? "" : url;
        }

        public void setUrl(String url) {
            this.url = url;
        }

        public String getTitle() {
            return title == null ? "" : title;
        }

        public void setTitle(String title) {
            this.title = title;
        }

        public String getThumbHigh() {
            return thumbHigh == null ? "" : thumbHigh;
        }

        public void setThumbHigh(String thumbHigh) {
            this.thumbHigh = thumbHigh;
        }

        public String getChannelTitle() {
            return channelTitle == null ? "" : channelTitle;
        }

        public void setChannelTitle(String channelTitle) {
            this.channelTitle = channelTitle;
        }

        public String getChannelId() {
            return channelId == null ? "" : channelId;
        }

        public void setChannelId(String channelId) {
            this.channelId = channelId;
        }

        public String getChannelUrl() {
            return channelUrl == null ? "" : channelUrl;
        }

        public void setChannelUrl(String channelUrl) {
            this.channelUrl = channelUrl;
        }

        public String getPublishedAt() {
            return publishedAt == null ? "" : publishedAt;
        }

        public void setPublishedAt(String publishedAt) {
            this.publishedAt = publishedAt;
        }

        public String getDuration() {
            return duration == null ? "" : duration;
        }

        public void setDuration(String duration) {
            this.duration = duration;
        }

        public String getViewCount() {
            return viewCount == null ? "" : viewCount;
        }

        public void setViewCount(String viewCount) {
            this.viewCount = viewCount;
        }
    }
}
