package cy.com.allview.contentprovider;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.database.Cursor;
import android.net.Uri;
import android.support.annotation.Nullable;

import com.orhanobut.logger.Logger;

/**
 * Created by Administrator
 * on 2017/4/7.
 * des:
 */

public class BookProvider extends ContentProvider {
    @Override
    public boolean onCreate() {
        Logger.d("threadName========="+Thread.currentThread().getName());
        return false;
    }

    @Nullable
    @Override
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {
        Logger.d("threadName========="+Thread.currentThread().getName());
        return null;
    }

    @Nullable
    @Override
    public String getType(Uri uri) {
        return null;
    }

    @Nullable
    @Override
    public Uri insert(Uri uri, ContentValues values) {
        Logger.d("threadName========="+Thread.currentThread().getName());
        return null;
    }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        Logger.d("threadName========="+Thread.currentThread().getName());
        return 0;
    }

    @Override
    public int update(Uri uri, ContentValues values, String selection, String[] selectionArgs) {
        Logger.d("threadName========="+Thread.currentThread().getName());
        return 0;
    }
}
