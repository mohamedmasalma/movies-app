package ocm.sharki.tv;

import android.app.Activity;
import android.app.AlertDialog.Builder;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.StrictMode;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RadioGroup.LayoutParams;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Response.ErrorListener;
import com.android.volley.Response.Listener;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import java.util.ArrayList;
import java.util.List;
import ocm.sharki.tv.entity.ColumnRoot;
import ocm.sharki.tv.entity.MovieItem;
import ocm.sharki.tv.fragments.AppLaunchFragment;
import ocm.sharki.tv.fragments.LiveChannelFragment;
import ocm.sharki.tv.fragments.SettingFragment;
import ocm.sharki.tv.fragments.VodChannelFragment;
import ocm.sharki.tv.model.Global;
import ocm.sharki.tv.model.TabbarModel;
import ocm.sharki.tv.model.VolleySingleton;
import org.json.JSONArray;
import org.json.JSONObject;

public class HomeActivity extends Activity {
    public static final int MY_SOCKET_TIMEOUT_MS = 30000;
    TextView addToFav;
    FrameLayout container;
    RadioGroup localRadioGroup;
    RelativeLayout mainbglayout;
    private List<MovieItem> movieItems;
    ArrayList<TabbarModel> pTabs = new ArrayList();
    private List<ColumnRoot> vodColumnRoots;

    /* renamed from: ocm.sharki.tv.HomeActivity$2 */
    class C02132 implements OnClickListener {
        C02132() {
        }

        public void onClick(DialogInterface dialog, int which) {
            dialog.dismiss();
            HomeActivity.this.finish();
        }
    }

    /* renamed from: ocm.sharki.tv.HomeActivity$3 */
    class C02143 implements OnClickListener {
        C02143() {
        }

        public void onClick(DialogInterface dialog, int which) {
            dialog.dismiss();
        }
    }

    /* renamed from: ocm.sharki.tv.HomeActivity$4 */
    class C03374 implements Listener<JSONArray> {
        C03374() {
        }

        public void onResponse(JSONArray response) {
            try {
                HomeActivity.this.movieItems = new ArrayList();

                for (int i = 0; i < response.length(); i++) {
                    MovieItem movieItem = new MovieItem();
                    JSONObject objectLiveItem = response.optJSONObject(i);
                    movieItem.setId(objectLiveItem.optString("stream_id"));
                    movieItem.setCaption(objectLiveItem.optString("name"));
                    movieItem.setPoster_url(objectLiveItem.optString("stream_icon"));
                    movieItem.setV_url(objectLiveItem.optString("stream_id") + "." + objectLiveItem.optString("container_extension"));
                    movieItem.setVod_category_id(objectLiveItem.optString("category_id"));
                    HomeActivity.this.movieItems.add(movieItem);

                }
                HomeActivity.this.getVODStreamsCategory();
            } catch (Exception ex) {
                Log.e("App", "Failure", ex);
            }
        }
    }

    /* renamed from: ocm.sharki.tv.HomeActivity$5 */
    class C03385 implements ErrorListener {
        C03385() {
        }

        public void onErrorResponse(VolleyError error) {
        }
    }

    /* renamed from: ocm.sharki.tv.HomeActivity$6 */
    class C03396 implements Listener<JSONArray> {
        C03396() {
        }

        public void onResponse(JSONArray response) {
            try {
                HomeActivity.this.vodColumnRoots = new ArrayList();
                ColumnRoot root = new ColumnRoot();
                root.setCaption("FAVOURITE");
                root.setId("0");
                root.setMovieItems(new ArrayList());
                HomeActivity.this.vodColumnRoots.add(root);
                Global.g_allVods.add(root);
                for (int i = 0; i < response.length(); i++) {
                    root = new ColumnRoot();
                    JSONObject objectLiveRoot = response.optJSONObject(i);
                    root.setCaption(objectLiveRoot.optString("category_name"));
                    root.setId(objectLiveRoot.optString("category_id"));
                    List<MovieItem> tempLiveItems = new ArrayList();
                    for (int j = 0; j < HomeActivity.this.movieItems.size(); j++) {
                        if (objectLiveRoot.optString("category_id").equals(((MovieItem) HomeActivity.this.movieItems.get(j)).getVod_category_id())) {
                            tempLiveItems.add(HomeActivity.this.movieItems.get(j));
                        }
                    }
                    if (tempLiveItems.size() > 0) {
                        root.setMovieItems(tempLiveItems);
                        HomeActivity.this.vodColumnRoots.add(root);
                        Global.g_allVods.add(root);
                    }
                }
            } catch (Exception ex) {
                Log.e("App", "Failure", ex);
            }
        }
    }

    /* renamed from: ocm.sharki.tv.HomeActivity$7 */
    class C03407 implements ErrorListener {
        C03407() {
        }

        public void onErrorResponse(VolleyError error) {
        }
    }

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (android.os.Build.VERSION.SDK_INT > 9)
        {
            StrictMode.ThreadPolicy policy = new
                    StrictMode.ThreadPolicy.Builder().permitAll().build();
            StrictMode.setThreadPolicy(policy);
        }
        setContentView(R.layout.activity_guide);
        getVODStreams();
        this.mainbglayout = (RelativeLayout) findViewById(R.id.gudie_bg);
        this.localRadioGroup = (RadioGroup) findViewById(R.id.tabbar);
        this.container = (FrameLayout) findViewById(R.id.container);
        this.addToFav = (TextView) findViewById(R.id.top_tips);
        this.addToFav.setSelected(true);
        this.pTabs.add(new TabbarModel("Lives", R.drawable.style1_live, 10));
        this.pTabs.add(new TabbarModel("Vods", R.drawable.style1_vod, 11));
        this.pTabs.add(new TabbarModel("Apps", R.drawable.style1_app, 13));
        this.pTabs.add(new TabbarModel("Youtube", R.drawable.tube_icon, 14));
        this.pTabs.add(new TabbarModel("Setting", R.drawable.style1_set, 12));
        for (int i = 0; i < this.pTabs.size(); i++) {
            TabbarModel model = (TabbarModel) this.pTabs.get(i);
            final RadioButton localRadioButton = (RadioButton) LayoutInflater.from(this).inflate(R.layout.tab_item, this.localRadioGroup, false);
            localRadioButton.setText(model.title);
            localRadioButton.setCompoundDrawablesWithIntrinsicBounds(0, model.icon, 0, 0);
            Drawable localDrawable = getResources().getDrawable(model.icon);
            localDrawable.setBounds(0, 0, Global.dip2px(this, (float) getResources().getInteger(R.integer.top_bar_img_width)), Global.dip2px(this, (float) getResources().getInteger(R.integer.top_bar_img_height)));
            localRadioButton.setCompoundDrawables(null, localDrawable, null, null);
            localRadioButton.setId(model.id);
            localRadioButton.setPadding(Global.dip2px(this, (float) getResources().getInteger(R.integer.top_bar_img_padding_hor)), Global.dip2px(this, (float) getResources().getInteger(R.integer.top_bar_img_padding_ver)), Global.dip2px(this, (float) getResources().getInteger(R.integer.top_bar_img_padding_hor)), Global.dip2px(this, (float) getResources().getInteger(R.integer.top_bar_img_padding_ver)));
            LayoutParams localLayoutParams = new LayoutParams(-2, -2, 1.0f);
            localLayoutParams.setMargins(Global.dip2px(this, (float) getResources().getInteger(R.integer.top_bar_rg_margin)), 0, 0, 0);
            this.localRadioGroup.addView(localRadioButton, localLayoutParams);
            if ((localRadioButton.getId()) == 10) {
                localRadioButton.setChecked(true);
                getFragmentManager().beginTransaction().replace(R.id.container, new LiveChannelFragment(HomeActivity.this)).commit();
            }
            localRadioButton.setOnClickListener(new View.OnClickListener() {
                public void onClick(View view) {
                    if ((localRadioButton.getId()) == 10) {
                        HomeActivity.this.getFragmentManager().beginTransaction().replace(R.id.container, new LiveChannelFragment(HomeActivity.this)).commit();
                    } else if ((localRadioButton.getId()) == 11) {
                        HomeActivity.this.getFragmentManager().beginTransaction().replace(R.id.container, new VodChannelFragment(HomeActivity.this)).commit();
                    } else if ((localRadioButton.getId()) == 12) {
                        HomeActivity.this.getFragmentManager().beginTransaction().replace(R.id.container, new SettingFragment(HomeActivity.this)).commit();
                    } else if ((localRadioButton.getId()) == 13) {
                        try {
                            HomeActivity.this.askForPermission("android.permission.ACCESS_FINE_LOCATION", Integer.valueOf(1));

                            HomeActivity.this.getFragmentManager().beginTransaction().replace(R.id.container, new AppLaunchFragment(HomeActivity.this)).commit();
                            Log.i("mym","111");} catch (Exception e) {

                        }

                    } else if ((localRadioButton.getId()) == 14) {

                        try {
                            if (HomeActivity.this.isAppInstalled("com.google.android.youtube")) {
                                try {
                                    Intent i = new Intent("android.intent.action.MAIN");
                                    i.addCategory("android.intent.category.LAUNCHER");
                                    i.setPackage("com.google.android.youtube");
                                    HomeActivity.this.startActivity(i);
                                    return;
                                } catch (Exception e2) {
                                    e2.printStackTrace();
                                    return;
                                }
                            }
                            HomeActivity.this.startActivity(new Intent("android.intent.action.VIEW", Uri.parse("market://details?id=com.google.android.youtube")));
                        } catch (Exception e22) {
                            Log.d("error youtube", e22.getMessage());
                        }
                    }
                }
            });
        }
    }

    public boolean isAppInstalled(String targetPackage) {
        for (ApplicationInfo packageInfo : getPackageManager().getInstalledApplications(0)) {
            if (packageInfo.packageName.equals(targetPackage)) {
                return true;
            }
        }
        return false;
    }

    private void askForPermission(String permission, Integer requestCode) {
        if (ContextCompat.checkSelfPermission(this, permission) == 0) {
            Toast.makeText(this, "" + permission + " is already granted.", Toast.LENGTH_SHORT).show();
        } else if (ActivityCompat.shouldShowRequestPermissionRationale(this, permission)) {
            ActivityCompat.requestPermissions(this, new String[]{permission}, requestCode.intValue());
        } else {
            ActivityCompat.requestPermissions(this, new String[]{permission}, requestCode.intValue());
        }
    }

    public void onBackPressed() {
        Builder alert = new Builder(this);
        alert.setCancelable(true);
        alert.setTitle("Alert!");
        alert.setNeutralButton("Yes", new C02132());
        alert.setPositiveButton("No", new C02143());
        alert.setMessage("Close the App");
        alert.show();
    }

    public void getVODStreams() {
        JsonArrayRequest request = new JsonArrayRequest(getServerUrl(Global.VodStreams), new C03374(), new C03385());
        request.setRetryPolicy(new DefaultRetryPolicy(30000, 1, 1.0f));
        VolleySingleton.getInstance(getApplicationContext()).addToRequestQueue(request);
    }

    public void getVODStreamsCategory() {
        JsonArrayRequest request = new JsonArrayRequest(getServerUrl(Global.VodStreamCategories), new C03396(), new C03407());
        request.setRetryPolicy(new DefaultRetryPolicy(30000, 1, 1.0f));
        VolleySingleton.getInstance(getApplicationContext()).addToRequestQueue(request);
    }

    public String getServerUrl(String action) {
        return Global.SERVER_URL + "player_api.php?username=" + Global.UserName + "&password=" + Global.Password + "&action=" + action;
    }
}
