package ocm.sharki.tv.entity;

import java.io.Serializable;

public class MovieItem implements Serializable {
    private String caption;
    private String detail_caption;
    private String detail_value;
    private String id;
    private String poster_url;
    private String v_url;
    private String vod_category_id;

    public String getVod_category_id() {
        return this.vod_category_id;
    }

    public void setVod_category_id(String vod_category_id) {
        this.vod_category_id = vod_category_id;
    }

    public String getDetail_caption() {
        return this.detail_caption;
    }

    public void setDetail_caption(String detail_caption) {
        this.detail_caption = detail_caption;
    }

    public String getDetail_value() {
        return this.detail_value;
    }

    public void setDetail_value(String detail_value) {
        this.detail_value = detail_value;
    }

    public String getId() {
        return this.id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getCaption() {
        return this.caption;
    }

    public void setCaption(String caption) {
        this.caption = caption;
    }

    public String getV_url() {
        return this.v_url;
    }

    public void setV_url(String v_url) {
        this.v_url = v_url;
    }

    public String getPoster_url() {
        return this.poster_url;
    }

    public void setPoster_url(String poster_url) {
        this.poster_url = poster_url;
    }
}
