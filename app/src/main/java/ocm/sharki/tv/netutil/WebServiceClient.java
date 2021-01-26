package ocm.sharki.tv.netutil;

import android.content.Context;
import android.util.Log;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.security.KeyStore;
import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.List;
import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import org.apache.http.HttpHost;
import org.apache.http.HttpVersion;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.utils.URLEncodedUtils;
import org.apache.http.conn.scheme.PlainSocketFactory;
import org.apache.http.conn.scheme.Scheme;
import org.apache.http.conn.scheme.SchemeRegistry;
import org.apache.http.conn.ssl.SSLSocketFactory;
import org.apache.http.entity.mime.MIME;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.conn.tsccm.ThreadSafeClientConnManager;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.apache.http.params.HttpProtocolParams;
import org.apache.http.util.EntityUtils;

public class WebServiceClient {
    private static int some_reasonable_timeout = 1800000;
    private String baseURL = "";
    private Context mContext = null;

    public WebServiceClient(Context context) {
        this.mContext = context;
    }

    public File downloadFileFromServer(String filepath) {
        File file;
        URL url;
        String fileURL = filepath.trim();
        File out_File = null;
        try {
            URL url2 = new URL(fileURL);
            try {
                SSLContext.getInstance("TLS").init(null, new TrustManager[]{new MyTrustManager()}, new SecureRandom());
                HttpURLConnection httpConn = (HttpURLConnection) url2.openConnection();
                httpConn.setDoInput(true);
                httpConn.connect();
                if (httpConn.getResponseCode() == 200) {
                    InputStream inputStream;
                    FileOutputStream file_outputStream;
                    byte[] buffer;
                    int bytesRead;
                    String fileName = null;
                    String disposition = httpConn.getHeaderField(MIME.CONTENT_DISPOSITION);
                    String contentType = httpConn.getContentType();
                    int contentLength = httpConn.getContentLength();
                    if (disposition != null) {
                        int index = disposition.indexOf("filename=");
                        if (index > 0) {
                            fileName = disposition.substring(index + 10, disposition.length() - 1);
                        }
                    } else {
                        fileName = fileURL.substring(fileURL.lastIndexOf("/") + 1, fileURL.length());
                    }
                    if (contentType != null) {
                        if (disposition == null) {
                            if (fileName != null) {
                                file = new File("" + File.separator + fileName);
                            }
                        }
                        file = new File("" + File.separator + fileName);
                        if (file.exists()) {
                            file.delete();
                        }
                        file.createNewFile();
                        if (file.exists()) {
                            inputStream = httpConn.getInputStream();
                            file_outputStream = new FileOutputStream(file);
                            if (contentLength != -1) {
                                buffer = new byte[contentLength];
                                while (true) {
                                    bytesRead = inputStream.read(buffer);
                                    if (bytesRead != -1) {
                                        file_outputStream.write(buffer, 0, bytesRead);
                                    }
                                }

                            }
                            file_outputStream.close();
                            inputStream.close();
                        }
                        out_File = file;
                    } else {
                        if (disposition == null) {
                            if (fileName != null) {
                                file = new File("" + File.separator + fileName);
                            }
                        }
                        file = new File("" + File.separator + fileName);
                        if (file.exists()) {
                            file.delete();
                        }
                        file.createNewFile();
                        if (file.exists()) {
                            inputStream = httpConn.getInputStream();
                            file_outputStream = new FileOutputStream(file);
                            if (contentLength != -1) {
                                buffer = new byte[contentLength];
                                while (true) {
                                    bytesRead = inputStream.read(buffer);
                                    if (bytesRead != -1) {
                                        file_outputStream.write(buffer, 0, bytesRead);
                                    }
                                }

                            }
                            file_outputStream.close();
                            inputStream.close();
                        }
                        out_File = file;
                    }
                    if (fileName != null) {
                        file = new File("" + File.separator + fileName);
                    } else {
                        file = new File("" + File.separator + fileName);
                    }
                    if (file.exists()) {
                        file.delete();
                    }
                    try {
                        file.createNewFile();
                    } catch (IOException e) {
                    }
                    try {
                        if (file.exists()) {
                            inputStream = httpConn.getInputStream();
                            file_outputStream = new FileOutputStream(file);
                            if (contentLength != -1) {
                                buffer = new byte[contentLength];
                                while (true) {
                                    bytesRead = inputStream.read(buffer);
                                    if (bytesRead != -1) {
                                        file_outputStream.write(buffer, 0, bytesRead);
                                    }
                                }

                            }
                            file_outputStream.close();
                            inputStream.close();
                        }
                        out_File = file;
                    } catch (Exception e2) {
                        out_File = file;
                        url = url2;
                        return null;
                    }
                }
                httpConn.disconnect();
                url = url2;
                return out_File;
            } catch (Exception e3) {
                url = url2;
                return null;
            }
        } catch (Exception e4) {
            return null;
        }
    }

    public String sendDataToServer(String url) {
        String responseBody = "";
        try {
            responseBody = EntityUtils.toString(getHttpClient().execute(new HttpPost(this.baseURL + url)).getEntity());
            if (responseBody == null || responseBody.equalsIgnoreCase("null")) {
                return "";
            }
            return responseBody;
        } catch (Exception e) {
            return "";
        }
    }

    public String sendDataToServer(String url, ArrayList<NameValuePair> PostParamsValue) {
        String responseBody = "";
        try {
            String finalURL = this.baseURL + url;
            HttpClient httpclient = getHttpClient();
            HttpPost httppost = new HttpPost(finalURL);
            if (PostParamsValue != null) {
                logParams(PostParamsValue);
                httppost.setEntity(new UrlEncodedFormEntity(PostParamsValue, "UTF-8"));
            }
            responseBody = EntityUtils.toString(httpclient.execute(httppost).getEntity());

            if (responseBody == null || responseBody.equalsIgnoreCase("null")) {
                return "";
            }
            return responseBody;
        } catch (Exception e) {
            return "";
        }
    }

    public String sendDataToServerGet(String url, ArrayList<NameValuePair> PostParamsValue) {
        String responseBody = "";
        try {
            String finalURL = this.baseURL + url;
            HttpClient httpclient = getHttpClient();
            if (PostParamsValue != null) {
                logParams(PostParamsValue);
                finalURL = finalURL + "?" + URLEncodedUtils.format(PostParamsValue, "utf-8");
            }
            responseBody = EntityUtils.toString(httpclient.execute(new HttpGet(finalURL)).getEntity());
            if (responseBody == null || responseBody.equalsIgnoreCase("null")) {
                return "";
            }
            return responseBody;
        } catch (Exception e) {
            return "";
        }
    }

    private static void logParams(List<NameValuePair> nameValuePairs) {
        for (NameValuePair nameValuePair : nameValuePairs) {
        }
    }

    public HttpClient getNewHttpClient() {
        try {
            KeyStore trustStore = KeyStore.getInstance(KeyStore.getDefaultType());
            trustStore.load(null, null);
            SSLSocketFactory sf = new MySSLSocketFactory(trustStore);
            sf.setHostnameVerifier(SSLSocketFactory.ALLOW_ALL_HOSTNAME_VERIFIER);
            HttpParams params = new BasicHttpParams();
            HttpProtocolParams.setVersion(params, HttpVersion.HTTP_1_1);
            HttpProtocolParams.setContentCharset(params, "UTF-8");
            SchemeRegistry registry = new SchemeRegistry();
            registry.register(new Scheme(HttpHost.DEFAULT_SCHEME_NAME, PlainSocketFactory.getSocketFactory(), 80));
            registry.register(new Scheme("https", sf, 443));
            return new DefaultHttpClient(new ThreadSafeClientConnManager(params, registry), params);
        } catch (Exception e) {
            return new DefaultHttpClient();
        }
    }

    private static HttpClient getHttpClient() {
        HttpParams httpParams = new BasicHttpParams();
        HttpConnectionParams.setConnectionTimeout(httpParams, some_reasonable_timeout);
        HttpConnectionParams.setSoTimeout(httpParams, some_reasonable_timeout);
        return new DefaultHttpClient(httpParams);
    }
}
