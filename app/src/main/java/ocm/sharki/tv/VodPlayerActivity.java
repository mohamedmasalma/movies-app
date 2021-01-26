package ocm.sharki.tv;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.SystemClock;
import android.util.Log;
import android.view.KeyEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceHolder.Callback;
import android.view.SurfaceView;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.widget.TextView;
import android.widget.Toast;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;
import ocm.sharki.tv.entity.ColumnRoot;
import ocm.sharki.tv.entity.MovieItem;
import ocm.sharki.tv.model.Global;
import ocm.sharki.tv.utils.NetWorkUtils;
import org.videolan.libvlc.IVLCVout;
import org.videolan.libvlc.LibVLC;
import org.videolan.libvlc.Media;
import org.videolan.libvlc.MediaPlayer;
import org.videolan.libvlc.MediaPlayer.Event;
import org.videolan.libvlc.MediaPlayer.EventListener;

public class VodPlayerActivity extends Activity {
    private static final int CODE_GONE_PROGRAMINFO = 3;
    private static final int CODE_HIDE_BLACK = 5;
    private static final int CODE_NET_STATE = 4;
    private static final int CODE_SHOWLOADING = 1;
    private static final int CODE_STOP_SHOWLOADING = 2;
    private static final String TAG = VodPlayerActivity.class.getName();
    private boolean bControlsActive = true;
    private boolean bIsPlaying = false;
    private ImageButton btnFwd;
    private ImageButton btnPlay;
    private ImageButton btnRew;
    private Handler cHandler = new Handler();
    private Uri contentUri;
    private PopupWindow control;
    private View controlView;
    private Handler handler = new C02231();
    private MovieItem item;
    private IVLCVout ivlcVout;
    private LibVLC libvlc = null;
    private Runnable mTicker;
    int maxTime = 8;
    private Media media;
    private LinearLayout mediaController;
    private MediaPlayer mediaPlayer = null;
    private List<MovieItem> movieItem = new ArrayList();
    private NetworkReceiver networkReceiver;
    private OnSeekBarChangeListener seekBarChangeListener = new C02255();
    private SeekBar seekPlayerProgress;
    private SurfaceHolder surfaceHolder;
    private SurfaceView surfaceview;
    private TextView text_vod_name;
    private TextView txtCurrentTime;
    private TextView txtEndTime;
    ImageButton m_controller;

    /* renamed from: ocm.sharki.tv.VodPlayerActivity$1 */
    class C02231 extends Handler {
        C02231() {
        }

        public void handleMessage(Message msg) {
            switch (msg.what) {
                case 1:
                    VodPlayerActivity.this.handler.sendEmptyMessageDelayed(1, 1000);
                    return;
                case 2:
                    VodPlayerActivity.this.handler.removeMessages(1);
                    return;
                default:
                    return;
            }
        }
    }

    /* renamed from: ocm.sharki.tv.VodPlayerActivity$2 */
    class C02242 implements Callback {
        C02242() {
        }

        public void surfaceCreated(SurfaceHolder holder) {
        }

        public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
        }

        public void surfaceDestroyed(SurfaceHolder holder) {
        }
    }

    /* renamed from: ocm.sharki.tv.VodPlayerActivity$5 */
    class C02255 implements OnSeekBarChangeListener {
        C02255() {
        }

        public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
            if (seekBar.getId() == R.id.mediacontroller_progress && fromUser) {
                VodPlayerActivity.this.mediaPlayer.setTime((long) progress);
            }
        }

        public void onStartTrackingTouch(SeekBar seekBar) {
        }

        public void onStopTrackingTouch(SeekBar seekBar) {
        }
    }

    /* renamed from: ocm.sharki.tv.VodPlayerActivity$6 */
    class C02266 implements OnClickListener {
        C02266() {
        }

        public void onClick(View view) {
            VodPlayerActivity.this.toggleMediaControls();
            VodPlayerActivity.this.startTimer();
        }
    }

    /* renamed from: ocm.sharki.tv.VodPlayerActivity$7 */
    class C02277 implements OnClickListener {
        C02277() {
        }

        public void onClick(View view) {
            if (VodPlayerActivity.this.bIsPlaying) {
                VodPlayerActivity.this.mediaPlayer.pause();
                VodPlayerActivity.this.bIsPlaying = false;
                m_controller.setImageResource(R.drawable.mediacontroller_pause);
                return;
            }
            VodPlayerActivity.this.mediaPlayer.play();
            VodPlayerActivity.this.bIsPlaying = true;
            m_controller.setImageResource(R.drawable.mediacontroller_play);
            VodPlayerActivity.this.setProgress();
        }
    }

    /* renamed from: ocm.sharki.tv.VodPlayerActivity$8 */
    class C02288 implements OnClickListener {
        C02288() {
        }

        public void onClick(View view) {
            VodPlayerActivity.this.mediaPlayer.setTime(VodPlayerActivity.this.mediaPlayer.getTime() - 5000);
            VodPlayerActivity.this.mediaController.setVisibility(View.VISIBLE);
            VodPlayerActivity.this.setProgress();
            VodPlayerActivity.this.maxTime = 8;
            VodPlayerActivity.this.startTimer();
        }
    }

    /* renamed from: ocm.sharki.tv.VodPlayerActivity$9 */
    class C02299 implements OnClickListener {
        C02299() {
        }

        public void onClick(View view) {
            VodPlayerActivity.this.mediaPlayer.setTime(VodPlayerActivity.this.mediaPlayer.getTime() + 5000);
            VodPlayerActivity.this.mediaController.setVisibility(View.VISIBLE);
            VodPlayerActivity.this.setProgress();
            VodPlayerActivity.this.maxTime = 8;
            VodPlayerActivity.this.startTimer();
        }
    }

    class NetworkReceiver extends BroadcastReceiver {
        NetworkReceiver() {
        }

        public void onReceive(Context context, Intent intent) {
            if (NetWorkUtils.getNetState(context)) {
                if (!VodPlayerActivity.this.mediaPlayer.isPlaying()) {
                    VodPlayerActivity.this.mediaPlayer.play();
                }
            } else if (VodPlayerActivity.this.mediaPlayer.isPlaying()) {
                VodPlayerActivity.this.mediaPlayer.pause();
            }
        }
    }

    /* renamed from: ocm.sharki.tv.VodPlayerActivity$4 */
    class C03424 implements IVLCVout.Callback {
        C03424() {
        }

        public void onSurfacesCreated(IVLCVout vlcVout) {
            int sw = VodPlayerActivity.this.getWindow().getDecorView().getWidth();
            int sh = VodPlayerActivity.this.getWindow().getDecorView().getHeight();
            if (sw * sh == 0) {
                Log.e(VodPlayerActivity.TAG, "Invalid surface size");
                return;
            }
            VodPlayerActivity.this.mediaPlayer.getVLCVout().setWindowSize(sw, sh);
            VodPlayerActivity.this.mediaPlayer.setAspectRatio("16:9");
            VodPlayerActivity.this.mediaPlayer.setScale(0.0f);
        }

        public void onSurfacesDestroyed(IVLCVout vlcVout) {
        }
    }

    /* renamed from: ocm.sharki.tv.VodPlayerActivity$3 */
    class C03643 implements EventListener {
        C03643() {
        }

        public void onEvent(Event event) {
            switch (event.type) {
                case Event.Buffering /*259*/:
                    if (VodPlayerActivity.this.mediaPlayer.isPlaying()) {
                        VodPlayerActivity.this.mediaPlayer.pause();
                    }
                    if (event.getBuffering() >= 100.0f) {
                        Log.i(VodPlayerActivity.TAG, "onEvent: buffer success...");
                        VodPlayerActivity.this.handler.sendEmptyMessageDelayed(5, 500);
                        VodPlayerActivity.this.handler.sendEmptyMessageDelayed(3, 5000);
                        VodPlayerActivity.this.handler.sendEmptyMessageDelayed(5, 500);
                        VodPlayerActivity.this.handler.sendEmptyMessageDelayed(3, 5000);
                        VodPlayerActivity.this.mediaPlayer.play();
                        return;
                    }
                    return;
                case Event.Playing /*260*/:
                    Log.i(VodPlayerActivity.TAG, "onEvent: playing...");
                    return;
                case Event.EncounteredError /*266*/:
                    Log.i(VodPlayerActivity.TAG, "onEvent: error...");
                    VodPlayerActivity.this.mediaPlayer.stop();
                    Toast.makeText(VodPlayerActivity.this, "Play error！", Toast.LENGTH_LONG).show();
                    return;
                default:
                    return;
            }
        }
    }

    protected void onCreate(Bundle paramBundle) {
        super.onCreate(paramBundle);
        initPlayer();
        initView();
        initData();
        startTimer();
    }

    private void initPlayer() {
        setContentView(R.layout.activity_player_vod_vlc);
        this.surfaceview = (SurfaceView) findViewById(R.id.surface);
        this.mediaController = (LinearLayout) findViewById(R.id.lin_media_controller);
        m_controller=findViewById(R.id.btnPlay);
        getWindow().addFlags(128);
    }

    private void play(Uri videoPath) {
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
        this.libvlc = new LibVLC(this, options);
        this.surfaceHolder = this.surfaceview.getHolder();
        this.surfaceHolder.setKeepScreenOn(true);
        this.surfaceHolder.addCallback(new C02242());
        this.mediaPlayer = new MediaPlayer(this.libvlc);
        this.mediaPlayer.setEventListener(new C03643());
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
        this.ivlcVout.addCallback(new C03424());
        this.mediaPlayer.play();
    }

    protected void onStart() {
        super.onStart();
    }

    protected void onStop() {
        super.onStop();
        this.mediaPlayer.stop();
        this.mediaPlayer.getVLCVout().detachViews();
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
        this.bIsPlaying = false;
        if (this.mediaPlayer != null) {
            this.mediaPlayer.release();
            this.ivlcVout.detachViews();
            this.libvlc.release();
        }
        finish();
    }

    private void registerNetReceiver() {
        IntentFilter filter = new IntentFilter("android.net.conn.CONNECTIVITY_CHANGE");
        this.networkReceiver = new NetworkReceiver();
        registerReceiver(this.networkReceiver, filter);
    }

    private List<MovieItem> addFavList() {
        return Global.g_dbManager.getFavouriteListVOD();
    }

    private void initData() {
        if (Global.g_selectedVodIdx != 0) {
            this.item = (MovieItem) ((ColumnRoot) Global.g_allVods.get(Global.g_selectedVodIdx)).getMovieItems().get(Global.g_selectedVodChannelIdx);
            this.text_vod_name.setText(this.item.getCaption());
            this.contentUri = Uri.parse(getChannelUrl(this.item.getV_url()));
        } else {
            this.movieItem.clear();
            this.movieItem.addAll(addFavList());
            this.contentUri = Uri.parse(getChannelUrl(((MovieItem) this.movieItem.get(Global.g_selectedVodChannelIdx)).getV_url()));
        }
        play(this.contentUri);
        initMediaControls();
        this.bIsPlaying = true;
        setProgress();
    }

    private void initMediaControls() {
        initSurfaceView();
        initPlayButton();
        initSeekBar();
        initTxtTime();
        initFwd();
        initRew();
    }

    public boolean dispatchKeyEvent(KeyEvent event) {
        if (event.getAction() == 0) {
            switch (event.getKeyCode()) {
                case 21:
                    try {
                        this.mediaPlayer.setTime(this.mediaPlayer.getTime() - 5000);
                        this.mediaController.setVisibility(View.VISIBLE);
                        setProgress();
                        this.maxTime = 8;
                        break;
                    } catch (Exception e) {
                        break;
                    }
                case 22:
                    try {
                        this.mediaPlayer.setTime(this.mediaPlayer.getTime() + 5000);
                        this.mediaController.setVisibility(View.VISIBLE);
                        setProgress();
                        this.maxTime = 8;
                        break;
                    } catch (Exception e2) {
                        break;
                    }
                case 23:
                    if (this.bIsPlaying) {
                        this.mediaPlayer.pause();
                        this.bIsPlaying = false;
                    } else {
                        this.mediaPlayer.play();
                        this.bIsPlaying = true;
                        setProgress();
                    }
                    this.mediaController.setVisibility(View.VISIBLE);
                    this.maxTime = 8;
                    break;
            }
            this.bControlsActive = true;
            startTimer();
        }
        return super.dispatchKeyEvent(event);
    }

    private void initSurfaceView() {
        this.surfaceview.setOnClickListener(new C02266());
    }

    private void toggleMediaControls() {
        if (this.bControlsActive) {
            hideMediaController();
            this.bControlsActive = false;
            return;
        }
        showController();
        this.bControlsActive = true;
        setProgress();
    }

    private void initPlayButton() {
        this.btnPlay = (ImageButton) findViewById(R.id.btnPlay);
        this.btnPlay.requestFocus();
        this.btnPlay.setOnClickListener(new C02277());
    }

    private void initTxtTime() {
        this.txtCurrentTime = (TextView) findViewById(R.id.time_current);
        this.txtEndTime = (TextView) findViewById(R.id.player_end_time);
    }

    private void initRew() {
        this.btnRew = (ImageButton) findViewById(R.id.rew);
        this.btnRew.requestFocus();
        this.btnRew.setOnClickListener(new C02288());
    }

    private void initFwd() {
        this.btnFwd = (ImageButton) findViewById(R.id.ffwd);
        this.btnFwd.requestFocus();
        this.btnFwd.setOnClickListener(new C02299());
    }

    private void initSeekBar() {
        this.seekPlayerProgress = (SeekBar) findViewById(R.id.mediacontroller_progress);
        this.seekPlayerProgress.requestFocus();
        this.seekPlayerProgress.setOnSeekBarChangeListener(new OnSeekBarChangeListener() {
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if (fromUser) {
                    VodPlayerActivity.this.mediaPlayer.setTime((long) progress);
                }
            }

            public void onStartTrackingTouch(SeekBar seekBar) {
            }

            public void onStopTrackingTouch(SeekBar seekBar) {
            }
        });
        int time = (int) this.mediaPlayer.getTime();
        this.seekPlayerProgress.setMax((int) this.mediaPlayer.getLength());
        this.seekPlayerProgress.setProgress(time);
    }

    private void setProgress() {
        this.seekPlayerProgress.setProgress(0);
        this.seekPlayerProgress.setMax(0);
        this.seekPlayerProgress.setMax((int) this.mediaPlayer.getLength());
        this.handler = new Handler();
        this.handler.post(new Runnable() {
            public void run() {
                if (VodPlayerActivity.this.mediaPlayer != null && VodPlayerActivity.this.bIsPlaying) {
                    String totDur = String.format("%02d.%02d.%02d", new Object[]{Long.valueOf(TimeUnit.MILLISECONDS.toHours(VodPlayerActivity.this.mediaPlayer.getLength())), Long.valueOf(TimeUnit.MILLISECONDS.toMinutes(VodPlayerActivity.this.mediaPlayer.getLength()) - TimeUnit.HOURS.toMinutes(TimeUnit.MILLISECONDS.toHours(VodPlayerActivity.this.mediaPlayer.getLength()))), Long.valueOf(TimeUnit.MILLISECONDS.toSeconds(VodPlayerActivity.this.mediaPlayer.getLength()) - TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(VodPlayerActivity.this.mediaPlayer.getLength())))});
                    String curDur = String.format("%02d.%02d.%02d", new Object[]{Long.valueOf(TimeUnit.MILLISECONDS.toHours(VodPlayerActivity.this.mediaPlayer.getTime())), Long.valueOf(TimeUnit.MILLISECONDS.toMinutes(VodPlayerActivity.this.mediaPlayer.getTime()) - TimeUnit.HOURS.toMinutes(TimeUnit.MILLISECONDS.toHours(VodPlayerActivity.this.mediaPlayer.getTime()))), Long.valueOf(TimeUnit.MILLISECONDS.toSeconds(VodPlayerActivity.this.mediaPlayer.getTime()) - TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(VodPlayerActivity.this.mediaPlayer.getTime())))});
                    VodPlayerActivity.this.seekPlayerProgress.setMax(0);
                    VodPlayerActivity.this.seekPlayerProgress.setMax((int) VodPlayerActivity.this.mediaPlayer.getLength());
                    VodPlayerActivity.this.seekPlayerProgress.setProgress((int) VodPlayerActivity.this.mediaPlayer.getTime());
                    VodPlayerActivity.this.txtCurrentTime.setText(curDur);
                    VodPlayerActivity.this.txtEndTime.setText(totDur);
                    VodPlayerActivity.this.handler.postDelayed(this, 1000);
                }
            }
        });
    }

    private void initView() {
        this.controlView = getLayoutInflater().inflate(R.layout.controler, null);
        this.text_vod_name = (TextView) this.controlView.findViewById(R.id.text_vod_name);
        this.control = new PopupWindow(this.controlView);
        this.control.setFocusable(true);
        this.control.setBackgroundDrawable(new BitmapDrawable());
        this.control.setOutsideTouchable(true);
        this.control.setAnimationStyle(C0222R.style.PopupAnimation);
    }

    private void runNextTicker() {
        this.maxTime--;
        this.cHandler.postAtTime(this.mTicker, SystemClock.uptimeMillis() + 1000);
    }

    private void startTimer() {
        this.maxTime = 8;
        this.mTicker = new Runnable() {
            public void run() {
                if (VodPlayerActivity.this.maxTime >= 1) {
                    VodPlayerActivity.this.runNextTicker();
                } else if (VodPlayerActivity.this.bControlsActive) {
                    VodPlayerActivity.this.hideMediaController();
                    VodPlayerActivity.this.bControlsActive = false;
                }
            }
        };
        this.mTicker.run();
    }

    private void showController() {
        this.mediaController.setVisibility(View.VISIBLE);
        getWindow().clearFlags(1024);
    }

    private void hideMediaController() {
        this.mediaController.setVisibility(View.INVISIBLE);
        getWindow().addFlags(1024);
    }

    public String getChannelUrl(String streamId) {
        return Global.SERVER_URL + "movie/" + Global.UserName + "/" + Global.Password + "/" + streamId;
    }
}
