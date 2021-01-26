package ocm.sharki.tv.model;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.BitmapFactory.Options;
import android.media.ThumbnailUtils;
import java.util.ArrayList;
import ocm.sharki.tv.entity.ColumnRoot;

public class Global {
    public static String APP_URL = "http://522.184.110.34/update/control.php";
    public static String AllowedOutputFormats;
    public static String LiveStreamCategories = "get_live_categories";
    public static String LiveStreams = "get_live_streams";
    public static String Password;
    public static String SERVER_IMAGE_ROOT = "";
    public static String SERVER_URL = "http://62.210.92.98:8000/";
    public static String UserName;
    public static String VodStreamCategories = "get_vod_categories";
    public static String VodStreams = "get_vod_streams";
    public static Editor edGlobal;
    public static ArrayList<ColumnRoot> g_allLives = new ArrayList();
    public static ArrayList<ColumnRoot> g_allVods = new ArrayList();
    public static DBManager g_dbManager;
    public static int g_selectedLiveChannelIdx;
    public static int g_selectedLiveIdx;
    public static int g_selectedVodChannelIdx;
    public static int g_selectedVodIdx;
    public static String mac;
    public static SharedPreferences spGlobal;

    public static int dip2px(Context paramContext, float paramFloat) {
        return (int) (0.5f + (paramContext.getResources().getDisplayMetrics().density * paramFloat));
    }

    public static Bitmap getIconBitmapR(Resources paramResources, int paramInt1, int paramInt2, int paramInt3) {
        Options localOptions = new Options();
        localOptions.inJustDecodeBounds = true;
        BitmapFactory.decodeResource(paramResources, paramInt1, localOptions);
        int i = localOptions.outWidth;
        int j = localOptions.outHeight;
        localOptions.inJustDecodeBounds = false;
        int m = j / paramInt3;
        int n = i / paramInt2 > m ? m : m;
        if (n <= 0) {
            n = 1;
        }
        localOptions.inSampleSize = n;
        return ThumbnailUtils.extractThumbnail(BitmapFactory.decodeResource(paramResources, paramInt1, localOptions), paramInt2, paramInt3, 2);
    }
}
