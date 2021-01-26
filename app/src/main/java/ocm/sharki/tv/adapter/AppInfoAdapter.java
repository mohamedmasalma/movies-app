package ocm.sharki.tv.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import java.util.List;
import ocm.sharki.tv.R;
import ocm.sharki.tv.model.AppInfo;

public class AppInfoAdapter extends BaseAdapter {
    LayoutInflater infater = null;
    private List<AppInfo> mlistAppInfo = null;

    class ViewHolder {
        ImageView appIcon;
        TextView tvAppLabel;

        public ViewHolder(View paramView) {
            this.appIcon = (ImageView) paramView.findViewById(R.id.imgApp);
            this.tvAppLabel = (TextView) paramView.findViewById(R.id.tvAppLabel);
        }
    }

    public AppInfoAdapter(Context paramContext, List<AppInfo> paramList) {
        this.infater = (LayoutInflater) paramContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        this.mlistAppInfo = paramList;
    }

    public int getCount() {
        System.out.println("size" + this.mlistAppInfo.size());
        return this.mlistAppInfo.size();
    }

    public Object getItem(int paramInt) {
        return this.mlistAppInfo.get(paramInt);
    }

    public long getItemId(int paramInt) {
        return 0;
    }

    public View getView(int paramInt, View paramView, ViewGroup paramViewGroup) {
        ViewHolder localViewHolder;
        View localView = paramView;
        if (paramView == null || paramView.getTag() == null) {
            localView = this.infater.inflate(R.layout.browse_app_item, null);
            localViewHolder = new ViewHolder(localView);
            localView.setTag(localViewHolder);
        } else {
            localViewHolder = (ViewHolder) paramView.getTag();
        }
        AppInfo localAppInfo = (AppInfo) getItem(paramInt);
        localViewHolder.appIcon.setImageDrawable(localAppInfo.getAppIcon());
        localViewHolder.tvAppLabel.setText(localAppInfo.getAppLabel());
        return localView;
    }
}
