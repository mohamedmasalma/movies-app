package ocm.sharki.tv;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build.VERSION;
import android.support.v4.content.FileProvider;
import android.util.Log;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

public class UpdateApp extends AsyncTask<String, Void, Void> {
    private Context context;

    public void setContext(Context contextf) {
        this.context = contextf;
    }

    protected Void doInBackground(String... arg0) {
        try {
            Uri myuri;
            HttpURLConnection c = (HttpURLConnection) new URL(arg0[0]).openConnection();
            c.setRequestMethod("GET");
            c.setDoOutput(true);
            c.connect();
            File file = new File("/mnt/sdcard/Download/");
            file.mkdirs();
            File outputFile = new File(file, "update.apk");
            if (outputFile.exists()) {
                outputFile.delete();
            }
            FileOutputStream fos = new FileOutputStream(outputFile);
            InputStream is = c.getInputStream();
            byte[] buffer = new byte[1024];
            while (true) {
                int len1 = is.read(buffer);
                if (len1 == -1) {
                    break;
                }
                fos.write(buffer, 0, len1);
            }
            fos.close();
            is.close();
            if (VERSION.SDK_INT < 24) {
                myuri = Uri.parse("file:///mnt/sdcard/Download/update.apk");
            } else {
                myuri = FileProvider.getUriForFile(this.context, this.context.getApplicationContext().getPackageName() + ".provider", new File(new File(this.context.getFilesDir(), "Download"), "update.apk"));
            }
            Intent intent = new Intent("android.intent.action.VIEW").setDataAndType(myuri, "application/vnd.android.package-archive");
            intent.addFlags(intent.getFlags());
            intent.setFlags(0);
            this.context.startActivity(intent);
        } catch (Exception e) {
            Log.e("UpdateAPP", "Update error! " + e.getMessage());
        }
        return null;
    }
}
