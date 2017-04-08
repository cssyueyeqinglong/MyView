package cy.com.allview.act;

import android.content.ContentValues;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;

import com.orhanobut.logger.Logger;

import cy.com.allview.R;
import cy.com.allview.bean.User;

/**
 * Created by Administrator
 * on 2017/4/7.
 * des:内容提供者Act
 */

public class ProviderAct extends AppCompatActivity {
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.act_provide);

        Uri uri=Uri.parse("content://cy.com.allview.contentprovider.bookprovider/user");
        ContentValues values=new ContentValues();
        values.put("_id",5);
        values.put("name","萧十一郎");
        getContentResolver().insert(uri,values);
        Cursor bookCursor = getContentResolver().query(uri, new String[]{"_id,name"}, null, null, null);
        while (bookCursor.moveToNext()){//有数据
            User user=new User();
            user.id=bookCursor.getInt(0);
            user.name=bookCursor.getString(1);
            Logger.d("book=="+user);
        }
        bookCursor.close();

        Cursor cursor = getContentResolver().query(uri, new String[]{"_id,name,sex"}, null, null, null);
        while (cursor.moveToNext()){//有数据
            User user=new User();
            user.id=cursor.getInt(0);
            user.name=cursor.getString(1);
            user.sex=cursor.getInt(2);
            Logger.d(user);
        }
        cursor.close();
    }
}
