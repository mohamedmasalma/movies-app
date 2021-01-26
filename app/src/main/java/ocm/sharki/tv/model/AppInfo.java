package ocm.sharki.tv.model;

import android.content.Intent;
import android.graphics.drawable.Drawable;

public class AppInfo {
    private Drawable appIcon;
    private String appLabel;
    private Intent intent;
    private String pkgName;

    public Drawable getAppIcon() {
        return this.appIcon;
    }

    public String getAppLabel() {
        return this.appLabel;
    }

    public Intent getIntent() {
        return this.intent;
    }

    public String getPkgName() {
        return this.pkgName;
    }

    public void setAppIcon(Drawable paramDrawable) {
        this.appIcon = paramDrawable;
    }

    public void setAppLabel(String paramString) {
        this.appLabel = paramString;
    }

    public void setIntent(Intent paramIntent) {
        this.intent = paramIntent;
    }

    public void setPkgName(String paramString) {
        this.pkgName = paramString;
    }
}
