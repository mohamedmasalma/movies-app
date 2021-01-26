package ocm.sharki.tv.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import com.bumptech.glide.Glide;
import java.util.List;
import ocm.sharki.tv.R;
import ocm.sharki.tv.entity.MovieItem;
import ocm.sharki.tv.model.Global;

public class VodAdapter extends BaseAdapter {
    private Context context;
    ViewHolder holder;
    private LayoutInflater inflater = null;
    private MovieItem movieItem;
    private List<MovieItem> movieItems = null;

    class ViewHolder {
        ImageView channelIcon;
        TextView channelName;
        ImageView fav;

        public ViewHolder(View paramView) {
            this.channelIcon = (ImageView) paramView.findViewById(R.id.channel_icon);
            this.channelName = (TextView) paramView.findViewById(R.id.channel_name);
            this.fav = (ImageView) paramView.findViewById(R.id.fav);
        }
    }

    public VodAdapter(Context paramContext, List<MovieItem> paramList) {
        this.inflater = (LayoutInflater) paramContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        this.movieItems = paramList;
        this.context = paramContext;
    }

    public int getCount() {
        System.out.println("size" + this.movieItems.size());
        return this.movieItems.size();
    }

    public Object getItem(int paramInt) {
        return this.movieItems.get(paramInt);
    }

    public long getItemId(int paramInt) {
        return 0;
    }

    public View getView(int paramInt, View paramView, ViewGroup paramViewGroup) {
        View localView = paramView;
        if (paramView == null || paramView.getTag() == null) {
            localView = this.inflater.inflate(R.layout.channel_item, null);
            this.holder = new ViewHolder(localView);
            localView.setTag(this.holder);
        } else {
            this.holder = (ViewHolder) paramView.getTag();
        }
        this.movieItem = (MovieItem) getItem(paramInt);
        Glide.with(context).load(Global.SERVER_IMAGE_ROOT + this.movieItem.getPoster_url()).into(this.holder.channelIcon);
        this.holder.channelName.setText(this.movieItem.getCaption());
        if (Global.g_dbManager.isFavouriteVOD(this.movieItem)) {
            this.holder.fav.setVisibility(View.INVISIBLE);
        } else {
            this.holder.fav.setVisibility(View.VISIBLE);
        }
        return localView;
    }
}
