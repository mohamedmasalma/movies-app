package ocm.sharki.tv.utils;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

public class NetWorkUtils {
    public static boolean getNetState(Context context) {
        NetworkInfo isNetWorkInfo = ((ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE)).getActiveNetworkInfo();
        boolean isNetWork = false;
        if (isNetWorkInfo != null) {
            isNetWork = isNetWorkInfo.isAvailable();
        }
        if (isNetWork) {
            return true;
        }
        return false;
    }
}
