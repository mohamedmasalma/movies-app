package ocm.sharki.tv.model;

import android.content.Context;
import android.graphics.Bitmap;
import android.support.v4.util.LruCache;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.ImageLoader.ImageCache;
import com.android.volley.toolbox.Volley;

public class VolleySingleton {
    private static Context mCtx;
    private static VolleySingleton mInstance;
    private ImageLoader mImageLoader = new ImageLoader(this.mRequestQueue, new C03431());
    private RequestQueue mRequestQueue = getRequestQueue();

    /* renamed from: ocm.sharki.tv.model.VolleySingleton$1 */
    class C03431 implements ImageCache {
        private final LruCache<String, Bitmap> cache = new LruCache(20);

        C03431() {
        }

        public Bitmap getBitmap(String url) {
            return (Bitmap) this.cache.get(url);
        }

        public void putBitmap(String url, Bitmap bitmap) {
            this.cache.put(url, bitmap);
        }
    }

    private VolleySingleton(Context context) {
        mCtx = context;
    }

    public static synchronized VolleySingleton getInstance(Context context) {
        mCtx=context;
        VolleySingleton volleySingleton;
        synchronized (VolleySingleton.class) {
            if (mInstance == null) {
                mInstance = new VolleySingleton(context);
            }
            volleySingleton = mInstance;
        }
        return volleySingleton;
    }

    public RequestQueue getRequestQueue() {
        if (this.mRequestQueue == null) {
            this.mRequestQueue = Volley.newRequestQueue(mCtx.getApplicationContext());
        }
        return this.mRequestQueue;
    }

    public <T> void addToRequestQueue(Request<T> req) {
        getRequestQueue().add(req);
    }

    public ImageLoader getImageLoader() {
        return this.mImageLoader;
    }
}
