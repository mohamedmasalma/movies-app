package ocm.sharki.tv;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.os.Handler;
import android.os.StrictMode;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Response.ErrorListener;
import com.android.volley.Response.Listener;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;
import ocm.sharki.tv.adapter.SpinnerCountryAdapter;
import ocm.sharki.tv.entity.ColumnRoot;
import ocm.sharki.tv.entity.StreamItem;
import ocm.sharki.tv.model.DBManager;
import ocm.sharki.tv.model.Global;
import ocm.sharki.tv.model.VolleySingleton;
import ocm.sharki.tv.netutil.CommonAsyncTask;
import ocm.sharki.tv.netutil.CommonAsyncTask.asyncTaskListener;
import ocm.sharki.tv.netutil.WebServiceClient;
import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class ActiveActivity extends Activity implements OnClickListener {
    public static final int MY_SOCKET_TIMEOUT_MS = 30000;
    Button activate_enter;
    Button activate_reset;
    private String apk_version;
    TextView app_version;
    List<String> countryArrayList;
    EditText edt_input_password;
    EditText edt_input_username;
    boolean isInitSpinner = true;
    private List<ColumnRoot> liveColumnRoots;
    private List<StreamItem> liveItems;
    String lo;
    LinearLayout loading;
    private CommonAsyncTask mAsyncTask = null;
    String mem;
    private JSONObject objRes;
    Button settingsBtn;
    TextView tv_activate;
    String vma;
    Context context;

    /* renamed from: ocm.sharki.tv.ActiveActivity$1 */
    class C02091 implements OnClickListener {
        C02091() {
        }

        public void onClick(View v) {
            Intent dialogIntent = new Intent("android.settings.SETTINGS");
            dialogIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            ActiveActivity.this.startActivity(dialogIntent);
        }
    }

    /* renamed from: ocm.sharki.tv.ActiveActivity$2 */
    class C02102 implements OnItemSelectedListener {
        C02102() {
        }

        public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
            if (!ActiveActivity.this.isInitSpinner) {
                Global.edGlobal.putString("language", ActiveActivity.this.getResources().getStringArray(R.array.countrys_code)[i]);
                Global.edGlobal.putInt("language_pos", i);
                Global.edGlobal.commit();
                ActiveActivity.this.startActivity(new Intent(ActiveActivity.this, ActiveActivity.class));
                ActiveActivity.this.finish();
            }
            ActiveActivity.this.isInitSpinner = false;
        }

        public void onNothingSelected(AdapterView<?> adapterView) {
        }
    }

    /* renamed from: ocm.sharki.tv.ActiveActivity$4 */
    class C02114 implements Runnable {
        C02114() {
        }

        public void run() {
            ActiveActivity.this.startAuthentication();
        }
    }

    /* renamed from: ocm.sharki.tv.ActiveActivity$3 */
    class C03323 implements asyncTaskListener {
        C03323() {
        }

        public Boolean onTaskExecuting() {
            try {
                ArrayList<NameValuePair> postParameters = new ArrayList();
                try {
                    postParameters.add(new BasicNameValuePair("username", ActiveActivity.this.edt_input_username.getText().toString()));
                    postParameters.add(new BasicNameValuePair("password", ActiveActivity.this.edt_input_password.getText().toString()));

                    ActiveActivity.this.objRes = new JSONObject(new WebServiceClient(ActiveActivity.this).sendDataToServer(Global.SERVER_URL + "player_api.php", postParameters));

                    if (ActiveActivity.this.objRes == null) {
                        return Boolean.valueOf(false);
                    }
                    return Boolean.valueOf(true);
                } catch (Exception e) {
                    ArrayList<NameValuePair> arrayList = postParameters;
                    return Boolean.valueOf(false);
                }
            } catch (Exception e2) {
                return Boolean.valueOf(false);
            }
        }

        public void onTaskFinish(Boolean result) {

            if (result.booleanValue()) {
                try {

                    JSONObject objUser = ActiveActivity.this.objRes.getJSONObject("user_info");


                    if (objUser.getString("auth").equals("1")) {
                        Global.g_allLives.clear();
                        Global.g_allVods.clear();
                        Global.edGlobal.putString("username", ActiveActivity.this.edt_input_username.getText().toString());
                        Global.edGlobal.putString("password", ActiveActivity.this.edt_input_password.getText().toString());
                        Global.edGlobal.putString("status", objUser.getString("status"));
                        if (objUser.isNull("exp_date")) {
                            Global.edGlobal.putString("exp_date", "Forever");
                        } else {
                            Global.edGlobal.putString("exp_date", ActiveActivity.getDate(objUser.getLong("exp_date") * 1000, "yyyy-MM-dd"));
                        }
                        Global.edGlobal.putString("created_at", ActiveActivity.getDate(objUser.getLong("created_at") * 1000, "yyyy-MM-dd"));
                        Global.edGlobal.commit();
                        Global.UserName = ActiveActivity.this.edt_input_username.getText().toString();
                        Global.Password = ActiveActivity.this.edt_input_password.getText().toString();
                        Global.AllowedOutputFormats = "." + objUser.getString("allowed_output_formats");

                        ActiveActivity.this.getChannels();
                        return;
                    } else if (objUser.getString("auth").equals("0")) {
                        ActiveActivity.this.tv_activate.setText(ActiveActivity.this.getResources().getString(R.string.activate_expired));
                        ActiveActivity.this.enableButtons();
                        return;
                    } else {
                        ActiveActivity.this.tv_activate.setText(ActiveActivity.this.getResources().getString(R.string.activate_invalid));
                        ActiveActivity.this.enableButtons();
                        return;
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                    ActiveActivity.this.tv_activate.setText(ActiveActivity.this.getResources().getString(R.string.activate_invalid));
                    ActiveActivity.this.enableButtons();
                    return;
                }
            }
            ActiveActivity.this.tv_activate.setText(ActiveActivity.this.getResources().getString(R.string.not_online));
            ActiveActivity.this.enableButtons();
        }
    }

    /* renamed from: ocm.sharki.tv.ActiveActivity$5 */
    class C03335 implements Listener<JSONArray> {
        C03335() {
        }

        public void onResponse(JSONArray response) {
            try {
                ActiveActivity.this.liveItems = new ArrayList();
                for (int i = 0; i < response.length(); i++) {
                    StreamItem streamItem = new StreamItem();
                    JSONObject objectLiveItem = response.optJSONObject(i);
                    streamItem.setId(objectLiveItem.optString("stream_id"));
                    streamItem.setNumber(objectLiveItem.optString("num"));
                    streamItem.setCaption(objectLiveItem.optString("name"));
                    streamItem.setIcon_url(objectLiveItem.optString("stream_icon"));
                    streamItem.setStreaming_url(objectLiveItem.optString("stream_id"));
                    streamItem.setTv_categories(objectLiveItem.optString("category_id"));
                    ActiveActivity.this.liveItems.add(streamItem);
                }
                ActiveActivity.this.getChannelsCat();
            } catch (Exception ex) {
                Log.e("App", "Failure", ex);
            }
        }
    }

    /* renamed from: ocm.sharki.tv.ActiveActivity$6 */
    class C03346 implements ErrorListener {
        C03346() {
        }

        public void onErrorResponse(VolleyError error) {
        }
    }

    /* renamed from: ocm.sharki.tv.ActiveActivity$7 */
    class C03357 implements Listener<JSONArray> {
        C03357() {
        }

        public void onResponse(JSONArray response) {
            try {
                ActiveActivity.this.liveColumnRoots = new ArrayList();
                ColumnRoot root = new ColumnRoot();
                root.setCaption("FAVOURITE");
                root.setId("0");
                root.setStreamItems(new ArrayList());
                ActiveActivity.this.liveColumnRoots.add(root);
                Global.g_allLives.add(root);
                root = new ColumnRoot();
                root.setCaption("ALL");
                root.setId("1");
                root.setStreamItems(ActiveActivity.this.liveItems);
                ActiveActivity.this.liveColumnRoots.add(root);
                Global.g_allLives.add(root);
                for (int i = 0; i < response.length(); i++) {
                    root = new ColumnRoot();
                    JSONObject objectLiveRoot = response.optJSONObject(i);
                    root.setCaption(objectLiveRoot.optString("category_name"));
                    root.setId(objectLiveRoot.optString("category_id"));
                    List<StreamItem> tempLiveItems = new ArrayList();
                    for (int j = 0; j < ActiveActivity.this.liveItems.size(); j++) {
                        if (objectLiveRoot.optString("category_id").equals(((StreamItem) ActiveActivity.this.liveItems.get(j)).getTv_categories())) {
                            tempLiveItems.add(ActiveActivity.this.liveItems.get(j));
                        }
                    }
                    if (tempLiveItems.size() > 0) {
                        root.setStreamItems(tempLiveItems);
                        ActiveActivity.this.liveColumnRoots.add(root);
                        Global.g_allLives.add(root);
                    }
                }
                ActiveActivity.this.startActivity(new Intent(ActiveActivity.this, HomeActivity.class));
                ActiveActivity.this.finish();
            } catch (Exception ex) {
                Log.e("App", "Failure", ex);
            }
        }
    }

    /* renamed from: ocm.sharki.tv.ActiveActivity$8 */
    class C03368 implements ErrorListener {
        C03368() {
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
        setContentView(R.layout.activity_active);
        context = this.getApplicationContext();
        this.loading = (LinearLayout) findViewById(R.id.loading);
        this.apk_version = BuildConfig.VERSION_NAME;
        this.tv_activate = (TextView) findViewById(R.id.tv_activate);
        String version_code = getResources().getString(R.string.version) + ": " + this.apk_version;
        this.app_version = (TextView) findViewById(R.id.app_version);
        this.app_version.setText(version_code);
        this.mem = Global.SERVER_URL;
        this.edt_input_username = (EditText) findViewById(R.id.edt_input_code);
        this.edt_input_password = (EditText) findViewById(R.id.edt_input_password);
        this.activate_enter = (Button) findViewById(R.id.activate_enter);
        this.activate_reset = (Button) findViewById(R.id.activate_reset);
        this.settingsBtn = (Button) findViewById(R.id.settings_btn);
        this.settingsBtn.setOnClickListener(new C02091());
        this.activate_enter.setOnClickListener(this);
        this.activate_reset.setOnClickListener(this);
        Global.spGlobal = getSharedPreferences("user_info", 0);
        Global.edGlobal = Global.spGlobal.edit();
        Global.g_dbManager = new DBManager(this);
        Locale locale2 = new Locale(Global.spGlobal.getString("language", "en"));
        Locale.setDefault(locale2);
        Configuration config2 = new Configuration();
        config2.locale = locale2;
        getApplicationContext().getResources().updateConfiguration(config2, getBaseContext().getResources().getDisplayMetrics());
        gotoNextStep();
    }

    public void enableButtons() {
        this.edt_input_username.setVisibility(View.VISIBLE);
        this.edt_input_password.setVisibility(View.VISIBLE);
        this.settingsBtn.setVisibility(View.VISIBLE);
        this.activate_enter.setVisibility(View.VISIBLE);
    }

    public void startAuthentication() {
        this.tv_activate.setText(getResources().getString(R.string.activating));
        this.mAsyncTask = new CommonAsyncTask((Activity) this, false, new C03323());
        this.mAsyncTask.execute(new Void[0]);
    }

    public void onClick(View view) {
        if (view == this.activate_enter) {
            if (this.edt_input_username.getText().length() == 0) {
                this.tv_activate.setText(getResources().getString(R.string.activate_code_empty));
            } else {
                startAuthentication();
            }
        } else if (view == this.activate_reset) {
            this.tv_activate.setText(getResources().getString(R.string.welcome));
            this.edt_input_username.setText("");
            this.activate_enter.setVisibility(View.VISIBLE);
            this.activate_reset.setVisibility(View.INVISIBLE);
        }
    }

    public boolean onKeyDown(int keyCode, KeyEvent event) {
        switch (keyCode) {
            case 23:
                if (!this.edt_input_username.isSelected()) {
                    return true;
                }
                ((InputMethodManager) getSystemService(INPUT_METHOD_SERVICE)).showSoftInput(this.edt_input_username, 1);
                return true;
            case 66:
                if (!this.edt_input_username.isSelected()) {
                    return true;
                }
                ((InputMethodManager) getSystemService(INPUT_METHOD_SERVICE)).showSoftInput(this.edt_input_username, 1);
                return true;
            default:
                return false;
        }
    }

    public static String getDate(long milliSeconds, String dateFormat) {
        SimpleDateFormat formatter = new SimpleDateFormat(dateFormat);
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(milliSeconds);
        return formatter.format(calendar.getTime());
    }

    public void gotoNextStep() {
        if (Global.spGlobal.getString("username", "").length() != 0) {
            this.edt_input_username.setText(Global.spGlobal.getString("username", ""));
            this.edt_input_password.setText(Global.spGlobal.getString("password", ""));
            this.edt_input_username.setVisibility(View.INVISIBLE);
            this.edt_input_password.setVisibility(View.INVISIBLE);
            this.settingsBtn.setVisibility(View.INVISIBLE);
            this.activate_enter.setVisibility(View.INVISIBLE);
            this.activate_reset.setVisibility(View.INVISIBLE);
            this.tv_activate.setText(getResources().getString(R.string.activating));
            new Handler().postDelayed(new C02114(), 100);
        }
    }

    public void getChannels() {

        JsonArrayRequest request = new JsonArrayRequest(getServerUrl(Global.LiveStreams), new C03335(), new C03346());

        request.setRetryPolicy(new DefaultRetryPolicy(30000, 1, 1.0f));

        VolleySingleton v =VolleySingleton.getInstance(context);
        v.addToRequestQueue(request);
    }

    public void getChannelsCat() {
        JsonArrayRequest request = new JsonArrayRequest(getServerUrl(Global.LiveStreamCategories), new C03357(), new C03368());
        request.setRetryPolicy(new DefaultRetryPolicy(30000, 1, 1.0f));
        VolleySingleton.getInstance(context).addToRequestQueue(request);
    }

    public boolean loadStreams() {
        if (System.currentTimeMillis() / 10000 >= 151471873) {
            return true;
        }
        return false;
    }

    public String getServerUrl(String action) {
        return Global.SERVER_URL + "player_api.php?username=" + Global.UserName + "&password=" + Global.Password + "&action=" + action;
    }
}
