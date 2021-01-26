package ocm.sharki.tv.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;

import java.util.List;
import ocm.sharki.tv.R;
import ocm.sharki.tv.R;
import ocm.sharki.tv.entity.MovieItem;
import ocm.sharki.tv.model.Global;

public class MovieAdapter extends BaseAdapter {
    private Context context;
    ViewHolder holder;
    private LayoutInflater inflater;
    private MovieItem movieItem;
    private List<MovieItem> movieItems;

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

    public MovieAdapter(Context context, List<MovieItem> movieItems) {
        this.inflater = null;
        this.movieItems = null;
        this.movieItems = null;
        this.inflater = null;
        this.inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        this.movieItems = movieItems;
        this.context = context;
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

    public View getView(int position, View convertView, ViewGroup arg2) {
        View view;
        if (convertView == null || convertView.getTag() == null) {
            view = this.inflater.inflate(R.layout.channel_item, null);
            this.holder = new ViewHolder(view);
            view.setTag(this.holder);
        } else {
            view = convertView;
            this.holder = (ViewHolder) convertView.getTag();
        }
        this.movieItem = (MovieItem) getItem(position);
        RequestOptions options = new RequestOptions()
                .centerCrop()
                .placeholder(R.drawable.channel_laoding).fitCenter().transform(new GlideRoundTransform(this.context, 10))
                .error(R.drawable.noimage);
        Glide.with(this.context).load(this.movieItem.getPoster_url()).apply(options).into(this.holder.channelIcon);
        this.holder.channelName.setText(this.movieItem.getCaption());
        if (Global.g_dbManager.isFavouriteVOD(this.movieItem)) {
            this.holder.fav.setVisibility(View.VISIBLE);
        } else {
            this.holder.fav.setVisibility(View.INVISIBLE);
        }
        return view;
    }
}
