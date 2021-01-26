package ocm.sharki.tv.adapter;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;
import java.util.List;
import ocm.sharki.tv.C0222R;
import ocm.sharki.tv.R;

public class SpinnerCountryAdapter extends BaseAdapter {

    private Context m_context;
    private List<String> m_data;
    private LayoutInflater m_inflater = null;
    private String vehicleNo;

    public static class ViewHolder {
        public TextView txtSpinnerItem;
    }

    public SpinnerCountryAdapter(Context ctx, List<String> data) {

        this.m_context = ctx;
        this.m_data = data;
        this.vehicleNo = this.vehicleNo;
        this.m_inflater = (LayoutInflater) this.m_context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

    }

    public int getCount() {
        return this.m_data.size();
    }

    public Object getItem(int position) {
        return this.m_data.get(position);
    }

    public long getItemId(int position) {
        return (long) position;
    }

    public View getView(int position, View convertView, ViewGroup parent) {

        ViewHolder holder;
        View vi = convertView;
        if (convertView == null) {
            vi = this.m_inflater.inflate(R.layout.spinner_item, parent,false);

            holder = new ViewHolder();
            holder.txtSpinnerItem = (TextView) vi.findViewById(R.id.txtSpinnerItem);
            vi.setTag(holder);
        } else {
            holder = (ViewHolder) vi.getTag();
        }
        holder.txtSpinnerItem.setText((String) this.m_data.get(position));
        return vi;
    }
}
