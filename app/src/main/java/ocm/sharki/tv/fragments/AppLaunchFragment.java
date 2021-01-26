package ocm.sharki.tv.fragments;

import android.app.Activity;
import android.app.Fragment;
import android.content.ComponentName;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.content.pm.ResolveInfo.DisplayNameComparator;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v4.app.BundleCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.GridView;
import android.widget.LinearLayout;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import ocm.sharki.tv.R;
import ocm.sharki.tv.HomeActivity;
import ocm.sharki.tv.adapter.AppInfoAdapter;
import ocm.sharki.tv.model.AppInfo;

public class AppLaunchFragment extends Fragment implements OnItemClickListener {




    private static final String TAG = "AppLuncherFragment";
    private GridView gridView = null;
    public static  HomeActivity homeActivity;
    private LinearLayout loading;
    private List<AppInfo> mlistAppInfo = null;
    private View rootView;


    public AppLaunchFragment(HomeActivity home) {
        this.homeActivity=home;
        Log.i("mym","2"+this.homeActivity);
    }

    public View onCreateView(LayoutInflater paramLayoutInflater, ViewGroup paramViewGroup, Bundle paramBundle) {
        this.rootView = paramLayoutInflater.inflate(R.layout.fragment_app_launcher, null, false);
        this.gridView = (GridView) this.rootView.findViewById(R.id.app_grid_view);
        this.gridView.setNextFocusUpId(50);
        this.gridView.setNextFocusRightId(50);
        this.loading = (LinearLayout) this.rootView.findViewById(R.id.loading);
        this.loading.setVisibility(View.INVISIBLE);

        this.mlistAppInfo = new ArrayList();
        queryAppInfo();

        this.gridView.setAdapter(new AppInfoAdapter(this.homeActivity, this.mlistAppInfo));

        this.gridView.setOnItemClickListener(this);
        this.gridView.clearFocus();
        this.gridView.requestFocusFromTouch();
        this.gridView.requestFocus();
        this.gridView.setSelection(0);
        this.loading.setVisibility(View.INVISIBLE);

        return this.rootView;
    }

    public void queryAppInfo() {
        PackageManager localPackageManager = this.homeActivity.getPackageManager();
        Intent localIntent1 = new Intent("android.intent.action.MAIN", null);
        localIntent1.addCategory("android.intent.category.LAUNCHER");
        List<ResolveInfo> localList = localPackageManager.queryIntentActivities(localIntent1, 0);
        Collections.sort(localList, new DisplayNameComparator(localPackageManager));

        if (this.mlistAppInfo != null) {
            this.mlistAppInfo.clear();
            for (ResolveInfo localResolveInfo : localList) {
                String str1 = localResolveInfo.activityInfo.name;
                String str2 = localResolveInfo.activityInfo.packageName;
                String str3 = (String) localResolveInfo.loadLabel(localPackageManager);
                Drawable localDrawable = localResolveInfo.loadIcon(localPackageManager);
                Intent localIntent2 = new Intent();
                localIntent2.setComponent(new ComponentName(str2, str1));
                AppInfo localAppInfo = new AppInfo();
                localAppInfo.setAppLabel(str3);
                localAppInfo.setPkgName(str2);
                localAppInfo.setAppIcon(localDrawable);
                localAppInfo.setIntent(localIntent2);
                this.mlistAppInfo.add(localAppInfo);
            }
        }
    }

    public void onItemClick(AdapterView<?> adapterView, View paramView, int paramInt, long paramLong) {
        startActivity(((AppInfo) this.mlistAppInfo.get(paramInt)).getIntent());
    }
}
