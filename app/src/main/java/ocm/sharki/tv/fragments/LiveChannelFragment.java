package ocm.sharki.tv.fragments;

import android.app.Fragment;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.GridView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RadioGroup.LayoutParams;
import android.widget.RadioGroup.OnCheckedChangeListener;
import android.widget.TextView;
import java.util.ArrayList;
import java.util.List;
import ocm.sharki.tv.C0222R;
import ocm.sharki.tv.HomeActivity;
import ocm.sharki.tv.LivePlayerActivity;
import ocm.sharki.tv.R;
import ocm.sharki.tv.adapter.ChannelAdapter;
import ocm.sharki.tv.entity.ColumnRoot;
import ocm.sharki.tv.entity.StreamItem;
import ocm.sharki.tv.model.Global;

public class LiveChannelFragment extends Fragment implements OnClickListener, OnCheckedChangeListener {

    ChannelAdapter channelAdapter = null;
    private List<ColumnRoot> columnRoots = new ArrayList();
    private TextView empty;
    private GridView gridView;
    private RadioGroup group;
    private HomeActivity homeActivity;
    ArrayList<StreamItem> pChannels = new ArrayList();
    private LinearLayout progressBar;
    ColumnRoot tempRoot;

    /* renamed from: ocm.sharki.tv.fragments.LiveChannelFragment$1 */
    class C02301 implements OnItemLongClickListener {
        C02301() {
        }

        public boolean onItemLongClick(AdapterView<?> adapterView, View view, int position, long l) {

            StreamItem localMovieItem = (StreamItem) LiveChannelFragment.this.pChannels.get(position);
            if (Global.g_dbManager.isFavourite(localMovieItem)) {
                Global.g_dbManager.removeFavourite(localMovieItem);
                if (LiveChannelFragment.this.tempRoot.getCaption().equals("FAVOURITE")) {
                    LiveChannelFragment.this.pChannels.clear();
                    LiveChannelFragment.this.pChannels.addAll(Global.g_dbManager.getFavouriteList());
                }
                LiveChannelFragment.this.channelAdapter.notifyDataSetChanged();
            } else {
                Global.g_dbManager.addFavourite(localMovieItem);
                LiveChannelFragment.this.channelAdapter.notifyDataSetChanged();
            }
            return true;
        }
    }

    /* renamed from: ocm.sharki.tv.fragments.LiveChannelFragment$2 */
    class C02312 implements OnItemClickListener {
        C02312() {
        }

        public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {

            Global.g_selectedLiveChannelIdx = i;
            try {
                LiveChannelFragment.this.startActivity(new Intent(LiveChannelFragment.this.getActivity(), LivePlayerActivity.class));
            } catch (Exception e) {
                Log.d("Click ", e.getMessage());
            }
        }
    }

    public LiveChannelFragment(HomeActivity home) {
        this.homeActivity=home;

    }

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.fragment_live_channel, container, false);
        this.progressBar = (LinearLayout) rootView.findViewById(R.id.loading);
        this.empty = (TextView) rootView.findViewById(R.id.package_empty_tv);
        this.gridView = (GridView) rootView.findViewById(R.id.movie_list);
        this.group = (RadioGroup) rootView.findViewById(R.id.movie_package_content);
        this.progressBar.setVisibility(View.INVISIBLE);
        this.columnRoots = Global.g_allLives;
        addFilmSortView();
        return rootView;
    }

    private void addFilmSortView() {

        for (int i = 0; i < Global.g_allLives.size(); i++) {
            RadioButton localRadioButton = (RadioButton) LayoutInflater.from(getActivity()).inflate(R.layout.package_item2, this.group, false);
            localRadioButton.setText(((ColumnRoot) Global.g_allLives.get(i)).getCaption());
            localRadioButton.setId(i);
            LayoutParams localLayoutParams = new LayoutParams(-1, -2);
            localLayoutParams.setMargins(Global.dip2px(getActivity(), 3.0f), Global.dip2px(getActivity(), 3.0f), Global.dip2px(getActivity(), 3.0f), 0);
            this.group.addView(localRadioButton, localLayoutParams);
        }
        this.group.setOnCheckedChangeListener(this);
        ((RadioButton) this.group.findViewById(Integer.valueOf(0).intValue())).setChecked(true);
    }

    public void onClick(View v) {
    }

    private List<StreamItem> addFavList() {
        return Global.g_dbManager.getFavouriteList();
    }

    public void onCheckedChanged(RadioGroup radioGroup, int paramInt) {
        Global.g_selectedLiveIdx = paramInt;
        this.tempRoot = (ColumnRoot) this.columnRoots.get(paramInt);
        if (this.tempRoot.getId().equals("0")) {
            this.pChannels.clear();
            this.pChannels.addAll(addFavList());
        } else {
            this.pChannels.clear();
            this.pChannels.addAll(this.tempRoot.getStreamItems());
        }
        if (this.channelAdapter == null) {
            this.channelAdapter = new ChannelAdapter(homeActivity, this.pChannels);
            this.gridView.setAdapter(this.channelAdapter);
        } else {
            this.channelAdapter.notifyDataSetChanged();
        }
        this.gridView.setOnItemLongClickListener(new C02301());
        this.gridView.setOnItemClickListener(new C02312());
    }
}
