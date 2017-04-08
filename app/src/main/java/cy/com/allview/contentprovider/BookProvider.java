package cy.com.allview.contentprovider;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.Context;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.support.annotation.Nullable;

import com.orhanobut.logger.Logger;

import java.util.IllegalFormatException;

import cy.com.allview.db.DbOpenHelper;

/**
 * Created by Administrator
 * on 2017/4/7.
 * des:
 */

public class BookProvider extends ContentProvider {
    private static final String AUTHORITIES = "cy.com.allview.contentprovider.bookprovider";
    public static final Uri BOOK_CONTENT_URI = Uri.parse("content://" + AUTHORITIES + "/book");
    public static final Uri USER_CONTENT_URI = Uri.parse("content://" + AUTHORITIES + "/user");
    public static final int BOOK_URI_CODE = 0;
    public static final int USER_URI_CODE = 1;
    private static final UriMatcher mUriMather = new UriMatcher(UriMatcher.NO_MATCH);

    static {
        mUriMather.addURI(AUTHORITIES, "book", BOOK_URI_CODE);
        mUriMather.addURI(AUTHORITIES, "user", USER_URI_CODE);
    }

    private SQLiteDatabase mDb;
    private Context mContext;

    @Override
    public boolean onCreate() {
        Logger.d("threadName=========" + Thread.currentThread().getName());
        mContext = getContext();
        mDb = new DbOpenHelper(mContext).getWritableDatabase();
        mDb.execSQL("insert into book values(3,'小李飞刀');");
        mDb.execSQL("insert into book values(4,'陆小凤传奇');");
        mDb.execSQL("insert into user values(1,'古龙',1);");
        mDb.execSQL("insert into user values(2,'金庸',0);");
        return true;
    }

    @Nullable
    @Override
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {
        Logger.d("threadName=========" + Thread.currentThread().getName());
        String tableName=getTableName(uri);
        if(tableName==null){
            throw new IllegalArgumentException("Un supported Uri:"+uri);
        }
        return mDb.query(tableName,projection,selection,selectionArgs,null,null,sortOrder,null);
    }

    @Nullable
    @Override
    public String getType(Uri uri) {
        return null;
    }

    @Nullable
    @Override
    public Uri insert(Uri uri, ContentValues values) {
        Logger.d("insert=========");
        String tableName=getTableName(uri);
        if(tableName==null){
            throw new IllegalArgumentException("Un supported Uri:"+uri);
        }
        mDb.insert(tableName,null,values);
        //注意插入数据库的时候，数据库数据会变化，所以需要通知外界
        mContext.getContentResolver().notifyChange(uri,null);
        return uri;
    }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        Logger.d("delete=========" );
        String tableName=getTableName(uri);
        if(tableName==null){
            throw new IllegalArgumentException("Un supported Uri:"+uri);
        }
        int count=mDb.delete(tableName,selection,selectionArgs);
        //注意插入数据库的时候，数据库数据会变化，所以需要通知外界
        if(count>0){
            mContext.getContentResolver().notifyChange(uri,null);
        }
        return count;
    }

    @Override
    public int update(Uri uri, ContentValues values, String selection, String[] selectionArgs) {
        Logger.d("update=========");
        String tableName=getTableName(uri);
        if(tableName==null){
            throw new IllegalArgumentException("Un supported Uri:"+uri);
        }
        int count=mDb.update(tableName,values,selection,selectionArgs);
        //注意插入数据库的时候，数据库数据会变化，所以需要通知外界
        if(count>0){
            mContext.getContentResolver().notifyChange(uri,null);
        }
        return count;
    }

    //通过Uri查询表名
    private String getTableName(Uri uri) {
        String tableName = null;
        switch (mUriMather.match(uri)) {
            case BOOK_URI_CODE:
                tableName = DbOpenHelper.BOOK_TABLE_NAME;
                break;
            case USER_URI_CODE:
                tableName = DbOpenHelper.USER_TABLE_NAME;
                break;
            default:
                break;
        }
        return tableName;
    }
}
