package ocm.sharki.tv.fragments;

import android.app.Fragment;
import android.content.Intent;
import android.os.Bundle;
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
import ocm.sharki.tv.R;
import ocm.sharki.tv.HomeActivity;
import ocm.sharki.tv.VodPlayerActivity;
import ocm.sharki.tv.adapter.MovieAdapter;
import ocm.sharki.tv.entity.ColumnRoot;
import ocm.sharki.tv.entity.MovieItem;
import ocm.sharki.tv.model.Global;

public class VodChannelFragment extends Fragment implements OnClickListener, OnCheckedChangeListener {
    private List<ColumnRoot> columnRoots = new ArrayList();
    private TextView empty;
    private GridView gridView;
    private RadioGroup group;
    private HomeActivity homeActivity;
    MovieAdapter movieAdapter = null;
    ArrayList<MovieItem> pChannels = new ArrayList();
    private LinearLayout progressBar;
    ColumnRoot tempRoot;

    /* renamed from: ocm.sharki.tv.fragments.VodChannelFragment$1 */
    class C02371 implements OnItemLongClickListener {
        C02371() {
        }

        public boolean onItemLongClick(AdapterView<?> adapterView, View view, int position, long l) {
            MovieItem localMovieItem = (MovieItem) VodChannelFragment.this.pChannels.get(position);
            if (Global.g_dbManager.isFavouriteVOD(localMovieItem)) {
                Global.g_dbManager.removeFavouriteVOD(localMovieItem);
                if (VodChannelFragment.this.tempRoot.getCaption().equals("FAVOURITE")) {
                    VodChannelFragment.this.pChannels.clear();
                    VodChannelFragment.this.pChannels.addAll(Global.g_dbManager.getFavouriteListVOD());
                }
                VodChannelFragment.this.movieAdapter.notifyDataSetChanged();
            } else {
                Global.g_dbManager.addFavouriteVOD(localMovieItem);
                VodChannelFragment.this.movieAdapter.notifyDataSetChanged();
            }
            return true;
        }
    }

    /* renamed from: ocm.sharki.tv.fragments.VodChannelFragment$2 */
    class C02382 implements OnItemClickListener {
        C02382() {
        }

        public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
            Global.g_selectedVodChannelIdx = i;
            VodChannelFragment.this.startActivity(new Intent(VodChannelFragment.this.getActivity(), VodPlayerActivity.class));
        }
    }

    public VodChannelFragment(HomeActivity mainActivity) {
        this.homeActivity = mainActivity;
    }

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_vod_channel, container, false);
        this.progressBar = (LinearLayout) rootView.findViewById(R.id.loading);
        this.empty = (TextView) rootView.findViewById(R.id.package_empty_tv);
        this.gridView = (GridView) rootView.findViewById(R.id.movie_list);
        this.group = (RadioGroup) rootView.findViewById(R.id.movie_package_content);
        this.progressBar.setVisibility(View.INVISIBLE);
        this.columnRoots = Global.g_allVods;
        addFilmSortView();
        return rootView;
    }

    private void addFilmSortView() {
        for (int i = 0; i < Global.g_allVods.size(); i++) {
            RadioButton localRadioButton = (RadioButton) LayoutInflater.from(getActivity()).inflate(R.layout.package_item, this.group, false);
            localRadioButton.setText(((ColumnRoot) Global.g_allVods.get(i)).getCaption());
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

    private List<MovieItem> addFavList() {
        return Global.g_dbManager.getFavouriteListVOD();
    }

    public void onCheckedChanged(RadioGroup radioGroup, int paramInt) {
        Global.g_selectedVodIdx = paramInt;
        this.tempRoot = (ColumnRoot) this.columnRoots.get(paramInt);
        if (this.tempRoot.getId().equals("0")) {
            this.pChannels.clear();
            this.pChannels.addAll(addFavList());
        } else {
            this.pChannels.clear();
            this.pChannels.addAll(this.tempRoot.getMovieItems());

        }
        if (this.movieAdapter == null) {
            this.movieAdapter = new MovieAdapter(getActivity(), this.pChannels);
            this.gridView.setAdapter(this.movieAdapter);
        } else {
            this.movieAdapter.notifyDataSetChanged();
        }
        this.gridView.setOnItemLongClickListener(new C02371());
        this.gridView.setOnItemClickListener(new C02382());
    }
}
