package ocm.sharki.tv.fragments;

import android.app.AlertDialog.Builder;
import android.app.Fragment;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.Button;
import android.widget.GridView;
import android.widget.Spinner;
import android.widget.TextView;
import java.util.Arrays;
import ocm.sharki.tv.ActiveActivity;
import ocm.sharki.tv.BuildConfig;
import ocm.sharki.tv.R;
import ocm.sharki.tv.HomeActivity;
import ocm.sharki.tv.adapter.SpinnerCountryAdapter;
import ocm.sharki.tv.model.Global;

public class SettingFragment extends Fragment {

    private static final String TAG = "SettingFragment";
    private final int HEIGHT = 90;
    private final int WIDTH = 160;
    private GridView bgvImg;
    private TextView code;
    private String[] countrys;
    private TextView expires;
    HomeActivity homeActivity;
    private LayoutInflater infater;
    private boolean isInitSpinner = true;
    private TextView password;
    private Spinner ply;
    private Spinner spn;
    private TextView version;

    /* renamed from: ocm.sharki.tv.fragments.SettingFragment$1 */
    class C02321 implements OnItemSelectedListener {
        C02321() {
        }

        public void onItemSelected(AdapterView<?> adapterView, View view, int position, long id) {
            Global.edGlobal.putString("language", SettingFragment.this.getResources().getStringArray(R.array.countrys_code)[position]);
            Global.edGlobal.putInt("language_pos", position);
            Global.edGlobal.commit();
        }

        public void onNothingSelected(AdapterView<?> adapterView) {
        }
    }

    /* renamed from: ocm.sharki.tv.fragments.SettingFragment$2 */
    class C02332 implements OnItemSelectedListener {
        C02332() {
        }

        public void onItemSelected(AdapterView<?> adapterView, View view, int position, long id) {
            Global.edGlobal.putString("player", SettingFragment.this.getResources().getStringArray(R.array.players)[position]);
            Global.edGlobal.putInt("player_pos", position);
            Global.edGlobal.commit();
        }

        public void onNothingSelected(AdapterView<?> adapterView) {
        }
    }

    /* renamed from: ocm.sharki.tv.fragments.SettingFragment$3 */
    class C02343 implements OnClickListener {
        C02343() {
        }

        public void onClick(View v) {
            SettingFragment.this.startActivity(new Intent(SettingFragment.this.homeActivity, ActiveActivity.class));
            SettingFragment.this.homeActivity.finish();
        }
    }

    /* renamed from: ocm.sharki.tv.fragments.SettingFragment$4 */
    class C02364 implements OnClickListener {

        /* renamed from: ocm.sharki.tv.fragments.SettingFragment$4$1 */
        class C02351 implements DialogInterface.OnClickListener {
            C02351() {
            }

            public void onClick(DialogInterface dialog, int which) {
                Global.edGlobal.putString("username", "");
                Global.edGlobal.putString("password", "");
                Global.edGlobal.commit();
                SettingFragment.this.startActivity(new Intent(SettingFragment.this.homeActivity, ActiveActivity.class));
                SettingFragment.this.homeActivity.finish();
            }
        }

        C02364() {
        }

        public void onClick(View v) {
            new Builder(SettingFragment.this.getActivity()).setTitle(R.string.reset_label).setMessage(R.string.reset_freagment).setPositiveButton(R.string.btn_yes, new C02351()).setNegativeButton(R.string.btn_no, null).show();
        }
    }

    public SettingFragment(HomeActivity home) {
     this.homeActivity=home;
    }

    public View onCreateView(LayoutInflater paramLayoutInflater, ViewGroup paramViewGroup, Bundle paramBundle) {
        View localView = paramLayoutInflater.inflate(R.layout.fragment_setting, null, false);
        this.spn = (Spinner) localView.findViewById(R.id.spn_setting_language);
        this.spn.setAdapter(new SpinnerCountryAdapter(getActivity(), Arrays.asList(getResources().getStringArray(R.array.countrys))));
        this.ply = (Spinner) localView.findViewById(R.id.spn_setting_player);
        this.ply.setAdapter(new SpinnerCountryAdapter(getActivity(), Arrays.asList(getResources().getStringArray(R.array.players))));
        this.ply.setSelection(Global.spGlobal.getInt("player_pos", 0));
        this.spn.setSelection(Global.spGlobal.getInt("language_pos", 0));
        Log.d("LanguagePositionDef", Integer.toString(Global.spGlobal.getInt("player_pos", 0)));
        Log.d("LanguagePositionDef", Integer.toString(Global.spGlobal.getInt("language_pos", 0)));
        this.spn.setOnItemSelectedListener(new C02321());
        this.ply.setOnItemSelectedListener(new C02332());
        ((Button) localView.findViewById(R.id.SaveBtn)).setOnClickListener(new C02343());
        ((Button) localView.findViewById(R.id.ResBtn)).setOnClickListener(new C02364());
        this.code = (TextView) localView.findViewById(R.id.tv_value_code);
        this.password = (TextView) localView.findViewById(R.id.tv_value_password);
        this.expires = (TextView) localView.findViewById(R.id.tv_value_expires);
        this.version = (TextView) localView.findViewById(R.id.tv_value_version);
        this.code.setText(Global.spGlobal.getString("username", ""));
        this.password.setText(Global.spGlobal.getString("password", ""));
        this.expires.setText(Global.spGlobal.getString("exp_date", ""));
        this.version.setText(BuildConfig.VERSION_NAME);
        return localView;
    }
}
