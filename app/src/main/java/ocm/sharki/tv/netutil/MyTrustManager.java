package ocm.sharki.tv.netutil;

import java.security.cert.X509Certificate;
import javax.net.ssl.X509TrustManager;

public class MyTrustManager implements X509TrustManager {
    public void checkClientTrusted(X509Certificate[] chain, String authType) {
    }

    public void checkServerTrusted(X509Certificate[] chain, String authType) {
    }

    public X509Certificate[] getAcceptedIssuers() {
        return null;
    }
}
