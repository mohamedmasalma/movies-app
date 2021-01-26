package ocm.sharki.tv.entity;

import java.io.Serializable;

public class StreamItem implements Serializable {
    private String caption;
    private String icon_url;
    private String id;
    private String num_future_epg_days;
    private String num_past_epg_days;
    private String number;
    private String streaming_url;
    private String tv_categories;
    private String tv_parent_caption;

    public String getTv_parent_caption() {
        return this.tv_parent_caption;
    }

    public void setTv_parent_caption(String tv_parent_caption) {
        this.tv_parent_caption = tv_parent_caption;
    }

    public String getId() {
        return this.id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getNumber() {
        return this.number;
    }

    public void setNumber(String number) {
        this.number = number;
    }

    public String getCaption() {
        return this.caption;
    }

    public void setCaption(String caption) {
        this.caption = caption;
    }

    public String getIcon_url() {
        return this.icon_url;
    }

    public void setIcon_url(String icon_url) {
        this.icon_url = icon_url;
    }

    public String getNum_past_epg_days() {
        return this.num_past_epg_days;
    }

    public void setNum_past_epg_days(String num_past_epg_days) {
        this.num_past_epg_days = num_past_epg_days;
    }

    public String getNum_future_epg_days() {
        return this.num_future_epg_days;
    }

    public void setNum_future_epg_days(String num_future_epg_days) {
        this.num_future_epg_days = num_future_epg_days;
    }

    public String getStreaming_url() {
        return this.streaming_url;
    }

    public void setStreaming_url(String streaming_url) {
        this.streaming_url = streaming_url;
    }

    public String getTv_categories() {
        return this.tv_categories;
    }

    public void setTv_categories(String tv_categories) {
        this.tv_categories = tv_categories;
    }

    public String toString() {
        return this.caption;
    }
}
