package ocm.sharki.tv;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.SystemClock;
import android.support.annotation.IdRes;
import android.util.Log;
import android.view.Display;
import android.view.GestureDetector;
import android.view.GestureDetector.SimpleOnGestureListener;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceHolder.Callback;
import android.view.SurfaceView;
import android.view.View;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.animation.TranslateAnimation;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.PopupWindow.OnDismissListener;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RadioGroup.LayoutParams;
import android.widget.RadioGroup.OnCheckedChangeListener;
import android.widget.TextView;
import android.widget.Toast;
import java.util.ArrayList;
import java.util.List;
import ocm.sharki.tv.adapter.ProgramChannelAdapter;
import ocm.sharki.tv.entity.ColumnRoot;
import ocm.sharki.tv.entity.StreamItem;
import ocm.sharki.tv.model.Global;
import ocm.sharki.tv.utils.NetWorkUtils;
import org.apache.http.HttpStatus;
import org.videolan.libvlc.IVLCVout;
import org.videolan.libvlc.LibVLC;
import org.videolan.libvlc.Media;
import org.videolan.libvlc.MediaPlayer;
import org.videolan.libvlc.MediaPlayer.Event;
import org.videolan.libvlc.MediaPlayer.EventListener;

public class LivePlayerActivity extends Activity implements OnCheckedChangeListener, OnTouchListener {
    private static final int CODE_GONE_PROGRAMINFO = 3;
    private static final int CODE_HIDE_BLACK = 5;
    private static final int CODE_NET_STATE = 4;
    private static final int CODE_SHOWLOADING = 1;
    private static final int CODE_STOP_SHOWLOADING = 2;
    private static final String PROGRAM_KEY = "lastProIndex";
    private static final String TAG = LivePlayerActivity.class.getName();
    private final int HIDE_CHANNEL_NUM = 10;
    private ProgramChannelAdapter adapter;
    private TranslateAnimation animIn;
    private Handler cHandler = new Handler();
    private TextView channelNum;
    private List<ColumnRoot> columnRoots = new ArrayList();
    private Uri contentUri;
    private int currentListItemID = 0;
    private TranslateAnimation exitAnim;
    private RadioGroup group;
    private Handler handler = new C02151();
    private IVLCVout ivlcVout;
    private String key = "";
    private LibVLC libvlc = null;
    private ListView listView;
    private float mBrightness = -1.0f;
    private GestureDetector mGestureDetector;
    private ImageView mOperationBg;
    private ImageView mOperationPercent;
    private FrameLayout mSurfaceFrame;
    private Runnable mTicker;
    private int mVolume = -1;
    private View mVolumeBrightnessLayout;
    private Media media;
    private MediaPlayer mediaPlayer = null;
    Handler myHandler = new C02175();
    private NetworkReceiver networkReceiver;
    private int order = 0;
    private PopupWindow popupWindow;
    private int programIndex = 0;
    private LinearLayout progressBar;
    private ImageView searchIcon;
    private TextView search_key;
    private LinearLayout search_lay;
    private TextView search_title;
    int sec;
    private List<StreamItem> streamItems = new ArrayList();
    private List<StreamItem> streamItems2 = new ArrayList();
    private SurfaceHolder surfaceHolder;
    private SurfaceView surfaceview;
    private int tempChaSel = 0;
    private int tempPacCheckPos = 0;
    private int tempPacPos = 0;
    private ColumnRoot tempRoot;

    /* renamed from: ocm.sharki.tv.LivePlayerActivity$1 */
    class C02151 extends Handler {
        C02151() {
        }

        public void handleMessage(Message msg) {
            switch (msg.what) {
                case 1:
                    LivePlayerActivity.this.handler.sendEmptyMessageDelayed(1, 1000);
                    return;
                case 2:
                    LivePlayerActivity.this.handler.removeMessages(1);
                    return;
                default:
                    return;
            }
        }
    }

    /* renamed from: ocm.sharki.tv.LivePlayerActivity$2 */
    class C02162 implements Callback {
        C02162() {
        }

        public void surfaceCreated(SurfaceHolder holder) {
        }

        public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
        }

        public void surfaceDestroyed(SurfaceHolder holder) {
        }
    }

    /* renamed from: ocm.sharki.tv.LivePlayerActivity$5 */
    class C02175 extends Handler {
        C02175() {
        }

        private void playUrl(String paramAnonymousString1, String paramAnonymousString2, int paramAnonymousInt) {
        }

        public void handleMessage(Message paramAnonymousMessage) {
            if (paramAnonymousMessage.what == 10) {
                LivePlayerActivity.this.channelNum.setVisibility(View.INVISIBLE);
            }
        }
    }

    /* renamed from: ocm.sharki.tv.LivePlayerActivity$6 */
    class C02186 implements Runnable {
        C02186() {
        }

        public void run() {
            if (LivePlayerActivity.this.sec <= 3 || LivePlayerActivity.this.key.isEmpty()) {
                LivePlayerActivity.this.runTicker();
                return;
            }
            int in_key = Integer.parseInt(LivePlayerActivity.this.key) - 1;
            if (in_key < LivePlayerActivity.this.streamItems.size() - 1) {
                LivePlayerActivity.this.search_lay.setVisibility(View.INVISIBLE);
                LivePlayerActivity.this.order = in_key;
                LivePlayerActivity.this.tempChaSel = LivePlayerActivity.this.order;
                LivePlayerActivity.this.play((StreamItem) LivePlayerActivity.this.streamItems.get(LivePlayerActivity.this.order), LivePlayerActivity.this.tempRoot);
                return;
            }
            LivePlayerActivity.this.key = "";
            LivePlayerActivity.this.search_lay.setVisibility(View.INVISIBLE);
        }
    }

    /* renamed from: ocm.sharki.tv.LivePlayerActivity$7 */
    class C02197 implements OnDismissListener {
        C02197() {
        }

        public void onDismiss() {
            if (LivePlayerActivity.this.tempPacPos != LivePlayerActivity.this.tempPacCheckPos) {
                LivePlayerActivity.this.tempRoot = (ColumnRoot) LivePlayerActivity.this.columnRoots.get(LivePlayerActivity.this.tempPacPos);
                if (LivePlayerActivity.this.tempRoot.getId().equals("0")) {
                    LivePlayerActivity.this.streamItems.clear();
                    LivePlayerActivity.this.streamItems.addAll(LivePlayerActivity.this.addFavList());
                } else {
                    LivePlayerActivity.this.streamItems.clear();
                    LivePlayerActivity.this.streamItems.addAll(LivePlayerActivity.this.tempRoot.getStreamItems());
                }
            }
            LivePlayerActivity.this.adapter.notifyDataSetChanged();
        }
    }

    /* renamed from: ocm.sharki.tv.LivePlayerActivity$8 */
    class C02208 implements OnItemClickListener {
        C02208() {
        }

        public void onItemClick(AdapterView<?> adapterView, View paramAnonymousView, int paramAnonymousInt, long paramAnonymousLong) {
            LivePlayerActivity.this.tempChaSel = paramAnonymousInt;
            LivePlayerActivity.this.tempPacPos = LivePlayerActivity.this.tempPacCheckPos;
            LivePlayerActivity.this.play((StreamItem) LivePlayerActivity.this.streamItems.get(paramAnonymousInt), LivePlayerActivity.this.tempRoot);
            LivePlayerActivity.this.popupWindow.dismiss();
        }
    }

    /* renamed from: ocm.sharki.tv.LivePlayerActivity$9 */
    class C02219 implements OnItemLongClickListener {
        C02219() {
        }

        public boolean onItemLongClick(AdapterView<?> adapterView, View view, int position, long l) {
            StreamItem localMovieItem = (StreamItem) LivePlayerActivity.this.streamItems.get(position);
            if (Global.g_dbManager.isFavourite(localMovieItem)) {
                Global.g_dbManager.removeFavourite(localMovieItem);
                if (LivePlayerActivity.this.tempRoot.getCaption().equals("FAVOURITE")) {
                    LivePlayerActivity.this.streamItems.clear();
                    LivePlayerActivity.this.streamItems.addAll(Global.g_dbManager.getFavouriteList());
                }
                LivePlayerActivity.this.adapter.notifyDataSetChanged();
            } else {
                Global.g_dbManager.addFavourite(localMovieItem);
                LivePlayerActivity.this.adapter.notifyDataSetChanged();
            }
            return true;
        }
    }

    private class MyGestureListener extends SimpleOnGestureListener {
        private MyGestureListener() {
        }

        public boolean onScroll(MotionEvent paramMotionEvent1, MotionEvent paramMotionEvent2, float paramFloat1, float paramFloat2) {
            float f1 = paramMotionEvent1.getX();
            float f2 = paramMotionEvent1.getY();
            int i = (int) paramMotionEvent2.getRawY();
            Display localDisplay = LivePlayerActivity.this.getWindowManager().getDefaultDisplay();
            int j = localDisplay.getWidth();
            int k = localDisplay.getHeight();
            if (((double) f1) > (4.0d * ((double) j)) / 5.0d) {
                LivePlayerActivity.this.onVolumeSlide((f2 - ((float) i)) / ((float) k));
            } else if (((double) f1) < ((double) j) / 5.0d) {
                LivePlayerActivity.this.onBrightnessSlide((f2 - ((float) i)) / ((float) k));
            }
            return super.onScroll(paramMotionEvent1, paramMotionEvent2, paramFloat1, paramFloat2);
        }

        public boolean onSingleTapUp(MotionEvent paramMotionEvent) {
            if (!(LivePlayerActivity.this.popupWindow == null || LivePlayerActivity.this.popupWindow.isShowing())) {
                LivePlayerActivity.this.showPopup();
            }
            return true;
        }
    }

    class NetworkReceiver extends BroadcastReceiver {
        NetworkReceiver() {
        }

        public void onReceive(Context context, Intent intent) {
            if (NetWorkUtils.getNetState(context)) {
                if (!LivePlayerActivity.this.mediaPlayer.isPlaying()) {
                    LivePlayerActivity.this.mediaPlayer.play();
                }
            } else if (LivePlayerActivity.this.mediaPlayer.isPlaying()) {
                LivePlayerActivity.this.mediaPlayer.pause();
            }
        }
    }

    /* renamed from: ocm.sharki.tv.LivePlayerActivity$4 */
    class C03414 implements IVLCVout.Callback {
        C03414() {
        }

        public void onSurfacesCreated(IVLCVout vlcVout) {
            int sw = LivePlayerActivity.this.getWindow().getDecorView().getWidth();
            int sh = LivePlayerActivity.this.getWindow().getDecorView().getHeight();
            if (sw * sh == 0) {
                Log.e(LivePlayerActivity.TAG, "Invalid surface size");
                return;
            }
            LivePlayerActivity.this.mediaPlayer.getVLCVout().setWindowSize(sw, sh);
            LivePlayerActivity.this.mediaPlayer.setAspectRatio("16:9");
            LivePlayerActivity.this.mediaPlayer.setScale(0.0f);
        }

        public void onSurfacesDestroyed(IVLCVout vlcVout) {
        }
    }

    /* renamed from: ocm.sharki.tv.LivePlayerActivity$3 */
    class C03633 implements EventListener {
        C03633() {
        }

        public void onEvent(Event event) {
            switch (event.type) {
                case Event.Buffering /*259*/:
                    if (LivePlayerActivity.this.mediaPlayer.isPlaying()) {
                        LivePlayerActivity.this.mediaPlayer.pause();
                    }
                    if (event.getBuffering() >= 100.0f) {
                        Log.i(LivePlayerActivity.TAG, "onEvent: buffer success...");
                        LivePlayerActivity.this.handler.sendEmptyMessageDelayed(5, 500);
                        LivePlayerActivity.this.handler.sendEmptyMessageDelayed(3, 5000);
                        LivePlayerActivity.this.handler.sendEmptyMessageDelayed(5, 500);
                        LivePlayerActivity.this.handler.sendEmptyMessageDelayed(3, 5000);
                        LivePlayerActivity.this.mediaPlayer.play();
                        return;
                    }
                    return;
                case Event.Playing /*260*/:
                    Log.i(LivePlayerActivity.TAG, "onEvent: playing...");
                    return;
                case Event.EncounteredError /*266*/:
                    Log.i(LivePlayerActivity.TAG, "onEvent: error...");
                    LivePlayerActivity.this.mediaPlayer.stop();
                    Toast.makeText(LivePlayerActivity.this, "Play error！", Toast.LENGTH_LONG).show();
                    return;
                default:
                    return;
            }
        }
    }

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_player_live_vlc);
        this.surfaceview = (SurfaceView) findViewById(R.id.surface);
        this.mSurfaceFrame = (FrameLayout) findViewById(R.id.player_surface_frame);
        this.surfaceHolder = this.surfaceview.getHolder();
        initView();
        initData();
    }

    private void initView() {
        this.channelNum = (TextView) findViewById(R.id.channel_num);
        this.progressBar = (LinearLayout) findViewById(R.id.loading);
        this.mVolumeBrightnessLayout = findViewById(R.id.operation_volume_brightness);
        this.mOperationBg = (ImageView) findViewById(R.id.operation_bg);
        this.mOperationPercent = (ImageView) findViewById(R.id.operation_percent);
        this.search_lay = (LinearLayout) findViewById(R.id.search_lay);
        this.search_title = (TextView) findViewById(R.id.search_title);
        this.search_key = (TextView) findViewById(R.id.input_key);
        this.searchIcon = (ImageView) findViewById(R.id.search_icon);
        this.progressBar.setVisibility(View.INVISIBLE);
    }

    private void initData() {
        this.columnRoots = Global.g_allLives;
        this.mGestureDetector = new GestureDetector(this, new MyGestureListener());
        if (Global.g_selectedLiveIdx < this.columnRoots.size()) {
            this.tempRoot = (ColumnRoot) this.columnRoots.get(Global.g_selectedLiveIdx);
            this.tempPacPos = Global.g_selectedLiveIdx;
            this.tempChaSel = Global.g_selectedLiveChannelIdx;
            if (Global.g_selectedLiveIdx != 0) {
                this.contentUri = Uri.parse(getChannelUrl(((StreamItem) this.tempRoot.getStreamItems().get(this.tempChaSel)).getStreaming_url()));
                initPlayer(this.contentUri);
            } else {
                this.streamItems2.clear();
                this.streamItems2.addAll(addFavList());
                this.contentUri = Uri.parse(getChannelUrl(((StreamItem) this.streamItems2.get(this.tempChaSel)).getStreaming_url()));
                initPlayer(this.contentUri);
            }
            initPopupWindow();
            return;
        }
        finish();
    }

    protected void onStart() {
        super.onStart();
    }

    protected void onStop() {
        super.onStop();
        this.mediaPlayer.stop();
        this.mediaPlayer.getVLCVout().detachViews();
    }

    private void registerNetReceiver() {
        IntentFilter filter = new IntentFilter("android.net.conn.CONNECTIVITY_CHANGE");
        this.networkReceiver = new NetworkReceiver();
        registerReceiver(this.networkReceiver, filter);
    }

    private void initPlayer(Uri videoPath) {
        ArrayList<String> options = new ArrayList();
        options.add("--aout=opensles");
        options.add("--audio-time-stretch");
        options.add("--no-sub-autodetect-file");
        options.add("--swscale-mode=0");
        options.add("--network-caching=400");
        options.add("--no-drop-late-frames");
        options.add("--no-skip-frames");
        options.add("--avcodec-skip-frame");
        options.add("--avcodec-hw=any");
        options.add("-vvv");
        this.libvlc = new LibVLC(this,options);
        this.surfaceHolder = this.surfaceview.getHolder();
        this.surfaceHolder.setKeepScreenOn(true);
        this.surfaceHolder.addCallback(new C02162());
        this.mediaPlayer = new MediaPlayer(this.libvlc);
        this.mediaPlayer.setEventListener(new C03633());
        this.media = new Media(this.libvlc, videoPath);
        this.media.setHWDecoderEnabled(true, true);
        this.media.addOption(":network-caching=5000");
        this.media.addOption(":clock-jitter=0");
        this.media.addOption(":clock-synchro=0");
        this.media.addOption(":codec=all");
        this.mediaPlayer.setMedia(this.media);
        this.ivlcVout = this.mediaPlayer.getVLCVout();
        this.ivlcVout.setVideoView(this.surfaceview);
        this.ivlcVout.attachViews();
        this.ivlcVout.addCallback(new C03414());
        this.mediaPlayer.play();
    }

    private void play(StreamItem selectedChannel, ColumnRoot selectedCategory) {
        showChannelNumDelay((this.tempChaSel + 1) + ":" + selectedChannel.getCaption());
        this.contentUri = Uri.parse(getChannelUrl(selectedChannel.getStreaming_url()));
        this.ivlcVout.detachViews();
        this.media = new Media(this.libvlc, this.contentUri);
        this.mediaPlayer.setMedia(this.media);
        this.ivlcVout.setVideoView(this.surfaceview);
        this.ivlcVout.attachViews();
        this.mediaPlayer.play();
        this.key = "";
    }

    private void showChannelNumDelay(String paramString) {
        this.myHandler.removeMessages(10);
        this.channelNum.setVisibility(View.VISIBLE);
        this.channelNum.setText(paramString);
        hideChannelNumDelay();
    }

    private void hideChannelNumDelay() {
        this.myHandler.sendEmptyMessageDelayed(10, 3000);
    }

    public boolean onKeyDown(int keyCode, KeyEvent event) {
        switch (keyCode) {
            case 4:
                if (this.popupWindow == null || !this.popupWindow.isShowing()) {
                    onBackPressed();
                } else {
                    this.popupWindow.dismiss();
                }
                return true;
            case 7:
                if (this.search_lay.getVisibility() == View.INVISIBLE) {
                    this.search_lay.setVisibility(View.VISIBLE);
                }
                this.key += "0";
                this.search_key.setText(this.key);
                int in_key = Integer.parseInt(this.key) - 1;
                if (in_key > this.streamItems.size() - 1) {
                    this.search_title.setText("No exist data");
                } else {
                    this.search_title.setText(((StreamItem) this.streamItems.get(in_key)).getCaption());
                    moveChannel();
                }
                return true;
            case 8:
                if (this.search_lay.getVisibility() == View.INVISIBLE) {
                    this.search_lay.setVisibility(View.VISIBLE);
                }
                this.key += "1";
                this.search_key.setText(this.key);
                int in_key1 = Integer.parseInt(this.key) - 1;
                if (in_key1 > this.streamItems.size() - 1) {
                    this.search_title.setText("No exist data");
                } else {
                    this.search_title.setText(((StreamItem) this.streamItems.get(in_key1)).getCaption());
                    moveChannel();
                }
                return true;
            case 9:
                if (this.search_lay.getVisibility() == View.INVISIBLE) {
                    this.search_lay.setVisibility(View.VISIBLE);
                }
                this.key += "2";
                this.search_key.setText(this.key);
                int in_key2 = Integer.parseInt(this.key) - 1;
                if (in_key2 > this.streamItems.size() - 1) {
                    this.search_title.setText("No exist data");
                } else {
                    this.search_title.setText(((StreamItem) this.streamItems.get(in_key2)).getCaption());
                    moveChannel();
                }
                return true;
            case 10:
                if (this.search_lay.getVisibility() == View.INVISIBLE) {
                    this.search_lay.setVisibility(View.VISIBLE);
                }
                this.key += "3";
                this.search_key.setText(this.key);
                int in_key3 = Integer.parseInt(this.key) - 1;
                if (in_key3 > this.streamItems.size() - 1) {
                    this.search_title.setText("No exist data");
                } else {
                    this.search_title.setText(((StreamItem) this.streamItems.get(in_key3)).getCaption());
                    moveChannel();
                }
                return true;
            case 11:
                if (this.search_lay.getVisibility() == View.INVISIBLE) {
                    this.search_lay.setVisibility(View.VISIBLE);
                }
                this.key += "4";
                this.search_key.setText(this.key);
                int in_key4 = Integer.parseInt(this.key) - 1;
                if (in_key4 > this.streamItems.size() - 1) {
                    this.search_title.setText("No exist data");
                } else {
                    this.search_title.setText(((StreamItem) this.streamItems.get(in_key4)).getCaption());
                    moveChannel();
                }
                return true;
            case 12:
                if (this.search_lay.getVisibility() == View.INVISIBLE) {
                    this.search_lay.setVisibility(View.VISIBLE);
                }
                this.key += "5";
                this.search_key.setText(this.key);
                int in_key5 = Integer.parseInt(this.key) - 1;
                if (in_key5 > this.streamItems.size() - 1) {
                    this.search_title.setText("No exist data");
                } else {
                    this.search_title.setText(((StreamItem) this.streamItems.get(in_key5)).getCaption());
                    moveChannel();
                }
                return true;
            case 13:
                if (this.search_lay.getVisibility() == View.INVISIBLE) {
                    this.search_lay.setVisibility(View.VISIBLE);
                }
                this.key += "6";
                this.search_key.setText(this.key);
                int in_key6 = Integer.parseInt(this.key) - 1;
                if (in_key6 > this.streamItems.size() - 1) {
                    this.search_title.setText("No exist data");
                } else {
                    this.search_title.setText(((StreamItem) this.streamItems.get(in_key6)).getCaption());
                    moveChannel();
                }
                return true;
            case 14:
                if (this.search_lay.getVisibility() == View.INVISIBLE) {
                    this.search_lay.setVisibility(View.VISIBLE);
                }
                this.key += "7";
                this.search_key.setText(this.key);
                int in_key7 = Integer.parseInt(this.key) - 1;
                if (in_key7 > this.streamItems.size() - 1) {
                    this.search_title.setText("No exist data");
                } else {
                    this.search_title.setText(((StreamItem) this.streamItems.get(in_key7)).getCaption());
                    moveChannel();
                }
                return true;
            case 15:
                if (this.search_lay.getVisibility() == View.INVISIBLE) {
                    this.search_lay.setVisibility(View.VISIBLE);
                }
                this.key += "8";
                this.search_key.setText(this.key);
                int in_key8 = Integer.parseInt(this.key) - 1;
                if (in_key8 > this.streamItems.size() - 1) {
                    this.search_title.setText("No exist data");
                } else {
                    this.search_title.setText(((StreamItem) this.streamItems.get(in_key8)).getCaption());
                    moveChannel();
                }
                return true;
            case 16:
                if (this.search_lay.getVisibility() == View.INVISIBLE) {
                    this.search_lay.setVisibility(View.VISIBLE);
                }
                this.key += "9";
                this.search_key.setText(this.key);
                int in_key9 = Integer.parseInt(this.key) - 1;
                if (in_key9 > this.streamItems.size() - 1) {
                    this.search_title.setText("No exist data");
                } else {
                    this.search_title.setText(((StreamItem) this.streamItems.get(in_key9)).getCaption());
                    moveChannel();
                }
                return true;
            case 19:
                if (!(this.popupWindow == null || this.popupWindow.isShowing())) {
                    if (this.tempChaSel == this.streamItems.size() - 1) {
                        this.tempChaSel = 0;
                    } else {
                        this.tempChaSel++;
                    }
                    play((StreamItem) this.streamItems.get(this.tempChaSel), this.tempRoot);
                }
                return true;
            case 20:
                if (!(this.popupWindow == null || this.popupWindow.isShowing())) {
                    if (this.tempChaSel == 0) {
                        this.tempChaSel = this.streamItems.size() - 1;
                    } else {
                        this.tempChaSel--;
                    }
                    play((StreamItem) this.streamItems.get(this.tempChaSel), this.tempRoot);
                }
                return true;
            case 23:
                if (!(this.popupWindow == null || this.popupWindow.isShowing())) {
                    showPopup();
                }
                return true;
            case 66:
                if (!(this.popupWindow == null || this.popupWindow.isShowing())) {
                    showPopup();
                }
                return true;
            case 166:
                if (!(this.popupWindow == null || this.popupWindow.isShowing())) {
                    if (this.tempChaSel == this.streamItems.size() - 1) {
                        this.tempChaSel = 0;
                    } else {
                        this.tempChaSel++;
                    }
                    play((StreamItem) this.streamItems.get(this.tempChaSel), this.tempRoot);
                }
                return true;
            case 167:
                if (!(this.popupWindow == null || this.popupWindow.isShowing())) {
                    if (this.tempChaSel == 0) {
                        this.tempChaSel = this.streamItems.size() - 1;
                    } else {
                        this.tempChaSel--;
                    }
                    play((StreamItem) this.streamItems.get(this.tempChaSel), this.tempRoot);
                }
                return true;
            default:
                return super.onKeyUp(keyCode, event);
        }
    }

    private void moveChannel() {
        this.sec = 0;
        this.mTicker = new C02186();
        this.mTicker.run();
    }

    private void runTicker() {
        this.sec++;
        this.cHandler.postAtTime(this.mTicker, SystemClock.uptimeMillis() + 1000);
    }

    public void onBackPressed() {
        if (this.mediaPlayer != null) {
            this.mediaPlayer.stop();
        }
        finish();
    }

    protected void onResume() {
        super.onResume();
        if (this.networkReceiver == null) {
            registerNetReceiver();
        }
        this.mediaPlayer.play();
    }

    protected void onPause() {
        super.onPause();
        if (this.mediaPlayer.isPlaying()) {
            this.mediaPlayer.pause();
            this.ivlcVout.detachViews();
        }
    }

    protected void onRestart() {
        super.onRestart();
        this.ivlcVout.setVideoView(this.surfaceview);
        this.ivlcVout.attachViews();
    }

    protected void onDestroy() {
        super.onDestroy();
        if (this.networkReceiver != null) {
            unregisterReceiver(this.networkReceiver);
        }
        if (this.mediaPlayer != null) {
            this.mediaPlayer.release();
            this.ivlcVout.detachViews();
            this.libvlc.release();
        }
        finish();
    }

    public boolean onTouch(View v, MotionEvent event) {
        return false;
    }

    private void showPopup() {
        ((RadioButton) this.group.findViewById(this.tempPacPos + 32)).setChecked(true);
        this.group.clearFocus();
        this.popupWindow.showAtLocation(this.mSurfaceFrame, 3, 0, 0);
        this.listView.clearFocus();
        this.listView.requestFocus();
        this.listView.setSelectionFromTop(this.tempChaSel, HttpStatus.SC_BAD_REQUEST);
    }

    protected void initPopupWindow() {
        View localView = getLayoutInflater().inflate(R.layout.popupwindow, null, false);
        this.popupWindow = new PopupWindow(localView, -2, -1, true);
        this.popupWindow.setAnimationStyle(C0222R.style.AnimationFade);
        this.popupWindow.setBackgroundDrawable(new ColorDrawable(0));
        this.group = (RadioGroup) localView.findViewById(R.id.pac_content);
        this.listView = (ListView) localView.findViewById(R.id.chn_list);
        this.listView.setNextFocusDownId(R.id.chn_list);
        this.listView.setNextFocusUpId(R.id.chn_list);
        for (int i = 0; i < this.columnRoots.size(); i++) {
            RadioButton localRadioButton = (RadioButton) LayoutInflater.from(this).inflate(R.layout.package_item2, this.group, false);
            localRadioButton.setText(((ColumnRoot) this.columnRoots.get(i)).getCaption());
            localRadioButton.setId(i + 32);
            LayoutParams localLayoutParams = new LayoutParams(-1, -2);
            localLayoutParams.setMargins(Global.dip2px(this, 3.0f), Global.dip2px(this, 3.0f), Global.dip2px(this, 3.0f), 0);
            this.group.addView(localRadioButton, localLayoutParams);
        }
        this.group.setOnCheckedChangeListener(this);
        int j = 32;
        if (this.tempRoot != null) {
            j = 32 + this.tempPacPos;
        }
        ((RadioButton) this.group.findViewById(Integer.valueOf(j).intValue())).setChecked(true);
        this.popupWindow.setOnDismissListener(new C02197());
    }

    private List<StreamItem> addFavList() {
        return Global.g_dbManager.getFavouriteList();
    }

    public void onCheckedChanged(RadioGroup paramRadioGroup, @IdRes int paramInt) {
        int i = paramInt - 32;
        this.tempPacCheckPos = i;
        this.tempRoot = (ColumnRoot) this.columnRoots.get(i);
        if (this.tempRoot.getId().equals("0")) {
            this.streamItems.clear();
            this.streamItems.addAll(addFavList());
        } else {
            this.streamItems.clear();
            this.streamItems.addAll(this.tempRoot.getStreamItems());
        }
        if (this.adapter != null) {
            this.adapter.notifyDataSetChanged();
        } else {
            this.adapter = new ProgramChannelAdapter(this, this.streamItems);
            this.listView.setAdapter(this.adapter);
            this.listView.setOnItemClickListener(new C02208());
            this.listView.setOnItemLongClickListener(new C02219());
        }
        paramRadioGroup.clearFocus();
        this.listView.clearFocus();
        this.listView.requestFocus();
        this.listView.requestFocusFromTouch();
        this.listView.setSelection(0);
        this.listView.setNextFocusLeftId(paramInt);
    }

    public boolean onTouchEvent(MotionEvent paramMotionEvent) {
        if (this.mGestureDetector.onTouchEvent(paramMotionEvent)) {
            return true;
        }
        if (paramMotionEvent.getAction() == 1) {
            endGesture();
        }
        return super.onTouchEvent(paramMotionEvent);
    }

    private void endGesture() {
        this.mVolume = -1;
        this.mBrightness = -1.0f;
        this.myHandler.removeMessages(21);
        this.myHandler.sendEmptyMessageDelayed(21, 500);
    }

    private void onVolumeSlide(float paramFloat) {
    }

    private void onBrightnessSlide(float paramFloat) {
        if (this.mBrightness < 0.0f) {
            this.mBrightness = getWindow().getAttributes().screenBrightness;
            if (this.mBrightness <= 0.0f) {
                this.mBrightness = 0.5f;
            }
            if (this.mBrightness < 0.01f) {
                this.mBrightness = 0.01f;
            }
            this.mOperationBg.setImageResource(R.drawable.video_brightness_bg);
            this.mVolumeBrightnessLayout.setVisibility(View.INVISIBLE);
        }
        WindowManager.LayoutParams localLayoutParams = getWindow().getAttributes();
        localLayoutParams.screenBrightness = this.mBrightness + paramFloat;
        if (localLayoutParams.screenBrightness > 1.0f) {
            localLayoutParams.screenBrightness = 1.0f;
        } else if (localLayoutParams.screenBrightness < 0.01f) {
            localLayoutParams.screenBrightness = 0.01f;
        }
        getWindow().setAttributes(localLayoutParams);
        ViewGroup.LayoutParams localLayoutParams1 = this.mOperationPercent.getLayoutParams();
        localLayoutParams1.width = (int) (((float) findViewById(R.id.operation_full).getLayoutParams().width) * localLayoutParams.screenBrightness);
        this.mOperationPercent.setLayoutParams(localLayoutParams1);
    }

    public String getChannelUrl(String streamId) {
        return Global.SERVER_URL + "live/" + Global.UserName + "/" + Global.Password + "/" + streamId + ".ts";
    }
}
