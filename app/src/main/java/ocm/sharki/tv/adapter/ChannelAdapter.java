package ocm.sharki.tv.adapter;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.request.target.Target;

import java.net.URL;
import java.util.List;
import ocm.sharki.tv.R;

import ocm.sharki.tv.entity.StreamItem;
import ocm.sharki.tv.model.Global;

public class ChannelAdapter extends BaseAdapter {
    private Context context;
    ViewHolder holder;
    private LayoutInflater inflater = null;
    private StreamItem streamItem;
    private List<StreamItem> streamItems = null;

    class ViewHolder {
        ImageView channelIcon;
        TextView channelName;
        ImageView fav;

        public ViewHolder(View view) {
            this.channelIcon = (ImageView) view.findViewById(R.id.channel_icon);
            this.channelName = (TextView) view.findViewById(R.id.channel_name);
            this.fav = (ImageView) view.findViewById(R.id.fav);
        }
    }

    public ChannelAdapter(Context context, List<StreamItem> streamItems) {
        this.inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        this.streamItems = streamItems;
        this.context = context;
    }

    public int getCount() {
        System.out.println("size" + this.streamItems.size());
        return this.streamItems.size();
    }

    public Object getItem(int position) {
        return this.streamItems.get(position);
    }

    public long getItemId(int position) {
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
        this.streamItem = (StreamItem) getItem(position);

        RequestOptions options = new RequestOptions()
                .centerCrop()
                .placeholder(R.drawable.channel_laoding).fitCenter().transform(new GlideRoundTransform(this.context, 10))
                .error(R.drawable.noimage);
        Glide.with(this.context).load(this.streamItem.getIcon_url()).apply(options).into(this.holder.channelIcon);

       // Glide.with(view.getContext()).load(this.streamItem.getIcon_url()).apply(options).into(this.holder.channelIcon);

        this.holder.channelName.setText((position + 1) + ". " + this.streamItem.getCaption());
        this.holder.channelName.setSelected(true);
        if (Global.g_dbManager.isFavourite(this.streamItem)) {
            this.holder.fav.setVisibility(View.VISIBLE);
        } else {
            this.holder.fav.setVisibility(View.INVISIBLE);
        }
        return view;
    }
}
