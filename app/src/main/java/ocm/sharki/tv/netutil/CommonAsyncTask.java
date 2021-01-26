package ocm.sharki.tv.netutil;

import android.app.Activity;
import android.content.Context;
import android.os.AsyncTask;
import android.support.v4.app.Fragment;
import java.lang.ref.WeakReference;

public class CommonAsyncTask extends AsyncTask<Void, Void, Boolean> {
    private Activity mActivity = null;
    private WeakReference<Activity> mActivityWeakRef;
    private asyncTaskListener mCallback = null;
    private Fragment mFragment = null;
    private ProgressHUD mProgressHud = null;
    private boolean showProgress = false;

    public interface asyncTaskListener {
        Boolean onTaskExecuting();

        void onTaskFinish(Boolean bool);
    }

    public CommonAsyncTask(Activity activity, boolean showProgress, asyncTaskListener listener) {
        this.mActivity = activity;
        this.showProgress = showProgress;
        this.mActivityWeakRef = new WeakReference(activity);
        this.mCallback = listener;
    }

    public CommonAsyncTask(Activity activity, boolean showProgress) {
        this.mActivity = activity;
        this.showProgress = showProgress;
        this.mActivityWeakRef = new WeakReference(activity);
        this.mCallback = (asyncTaskListener) this.mActivity;
    }

    public CommonAsyncTask(Fragment fragment, Context context, boolean showProgress) {
        this.mFragment = fragment;
        this.showProgress = showProgress;
        this.mActivityWeakRef = new WeakReference((Activity) context);
        this.mCallback = (asyncTaskListener) this.mFragment;
    }

    protected void onPreExecute() {
        if (this.showProgress) {
            this.mProgressHud = ProgressHUD.show(this.mActivity, "Loading...", true, false, null);
        }
    }

    protected Boolean doInBackground(Void... params) {
        return this.mCallback.onTaskExecuting();
    }

    protected void onPostExecute(Boolean result) {
        if (this.mProgressHud != null && this.mProgressHud.isShowing()) {
            this.mProgressHud.dismiss();
        }
        if (this.mActivityWeakRef != null && !((Activity) this.mActivityWeakRef.get()).isFinishing()) {
            this.mCallback.onTaskFinish(result);
        }
    }

    public void onError() {
        cancel(true);
    }
}
