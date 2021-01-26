package ocm.sharki.tv.netutil;

import android.content.Context;
import android.net.ConnectivityManager;

public class NetworkManager {
    public static boolean isNetworkConnected(Context context) {
        if (((ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE)).getActiveNetworkInfo() == null) {
            return false;
        }
        return true;
    }
}
