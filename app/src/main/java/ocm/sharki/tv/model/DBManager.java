package ocm.sharki.tv.model;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;
import java.io.File;
import java.util.ArrayList;
import ocm.sharki.tv.entity.MovieItem;
import ocm.sharki.tv.entity.StreamItem;

public class DBManager extends SQLiteOpenHelper {
    public static final String DBNAME = "appdb";
    public static final int LIVE_FAVOURITE = 1;
    public static final String TABLE_FAVOURITE = "table_favourite";
    public static final int VOD_FAVOURITE = 2;
    public static final String db_path = "/data/data/com.sharki.tv/appdb";
    private Context mContext;
    private SQLiteDatabase mDB;

    public DBManager(Context context) {
        super(context, DBNAME, null, 1);
        try {
            File file = new File(db_path);
            if (!file.exists()) {
                file.createNewFile();
            }
            if (file.exists() && !file.isDirectory()) {
                SQLiteDatabase.openOrCreateDatabase(db_path, null);
            }
        } catch (SQLiteException e) {
            Log.d("Exception:%s", e.getMessage());
        } catch (Exception e2) {
            Log.d("Exception:%s", e2.getMessage());
        }
        this.mDB = getWritableDatabase();
        this.mContext = context;
    }

    public void closeDB() {
        this.mDB.close();
    }

    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        sqLiteDatabase.execSQL("CREATE TABLE IF NOT EXISTS table_favourite(id varchar(100), title varchar(200), image varchar(1024), url varchar(1024), type integer);");
    }

    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS table_favourite");
        onCreate(sqLiteDatabase);
    }

    public void addFavourite(StreamItem product) {
        ContentValues values = new ContentValues();
        values.put("id", product.getId());
        values.put("title", product.getCaption());
        values.put("url", product.getStreaming_url());
        values.put("image", product.getIcon_url());
        values.put("type", Integer.valueOf(1));
        this.mDB.insert(TABLE_FAVOURITE, null, values);
    }

    public ArrayList<StreamItem> getFavouriteList() {
        Cursor cursor = this.mDB.rawQuery("select * from table_favourite where type ='1'", null);
        cursor.moveToFirst();
        ArrayList<StreamItem> res = new ArrayList();
        for (int i = 0; i < cursor.getCount(); i++) {
            StreamItem productRecord = new StreamItem();
            productRecord.setId(cursor.getString(cursor.getColumnIndex("id")));
            productRecord.setCaption(cursor.getString(cursor.getColumnIndex("title")));
            productRecord.setIcon_url(cursor.getString(cursor.getColumnIndex("image")));
            productRecord.setStreaming_url(cursor.getString(cursor.getColumnIndex("url")));
            res.add(productRecord);
            cursor.moveToNext();
        }
        return res;
    }

    public boolean isFavourite(StreamItem product) {
        Cursor cursor = this.mDB.rawQuery("select * from table_favourite where id = '" + product.getId() + "' and type = '" + 1 + "'", null);
        cursor.moveToFirst();
        if (cursor.getCount() > 0) {
            return true;
        }
        return false;
    }

    public boolean isFavourite(MovieItem product) {
        Cursor cursor = this.mDB.rawQuery("select * from table_favourite where id = '" + product.getId() + "' and type = '" + 1 + "'", null);
        cursor.moveToFirst();
        if (cursor.getCount() > 0) {
            return true;
        }
        return false;
    }

    public boolean removeFavourite(StreamItem product) {
        try {
            this.mDB.delete(TABLE_FAVOURITE, "id=?and type=1", new String[]{product.getId()});
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    public void addFavouriteVOD(MovieItem product) {
        ContentValues values = new ContentValues();
        values.put("id", product.getId());
        values.put("title", product.getCaption());
        values.put("url", product.getV_url());
        values.put("image", product.getPoster_url());
        values.put("type", Integer.valueOf(2));
        this.mDB.insert(TABLE_FAVOURITE, null, values);
    }

    public ArrayList<MovieItem> getFavouriteListVOD() {
        Cursor cursor = this.mDB.rawQuery("select * from table_favourite where type = '2'", null);
        cursor.moveToFirst();
        ArrayList<MovieItem> res = new ArrayList();
        for (int i = 0; i < cursor.getCount(); i++) {
            MovieItem productRecord = new MovieItem();
            productRecord.setId(cursor.getString(cursor.getColumnIndex("id")));
            productRecord.setCaption(cursor.getString(cursor.getColumnIndex("title")));
            productRecord.setPoster_url(cursor.getString(cursor.getColumnIndex("image")));
            productRecord.setV_url(cursor.getString(cursor.getColumnIndex("url")));
            res.add(productRecord);
            cursor.moveToNext();
        }
        return res;
    }

    public boolean isFavouriteVOD(MovieItem product) {
        Cursor cursor = this.mDB.rawQuery("select * from table_favourite where id = '" + product.getId() + "' and type = '" + 2 + "'", null);
        cursor.moveToFirst();
        if (cursor.getCount() > 0) {
            return true;
        }
        return false;
    }

    public boolean removeFavouriteVOD(MovieItem product) {
        try {
            this.mDB.delete(TABLE_FAVOURITE, "id=?and type=2", new String[]{product.getId()});
            return true;
        } catch (Exception e) {
            return false;
        }
    }
}
