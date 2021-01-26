package ocm.sharki.tv.entity;

import android.util.Log;

import java.util.List;

public class ColumnRoot {
    private String caption;
    private String icon_url;
    private String id;
    private List<MovieItem> movieItems;
    private List<StreamItem> streamItems;

    public List<MovieItem> getMovieItems() {
        Log.i("mym","moovie"+this.movieItems);
        return this.movieItems;
    }

    public void setMovieItems(List<MovieItem> movieItems) {
        this.movieItems = movieItems;
    }

    public List<StreamItem> getStreamItems() {
        return this.streamItems;
    }

    public void setStreamItems(List<StreamItem> streamItems) {
        this.streamItems = streamItems;
    }

    public String getCaption() {
        return this.caption;
    }

    public void setCaption(String caption) {
        this.caption = caption;
    }

    public String getId() {
        return this.id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getIcon_url() {
        return this.icon_url;
    }

    public void setIcon_url(String icon_url) {
        this.icon_url = icon_url;
    }

    public String toString() {
        return getCaption();
    }
}
