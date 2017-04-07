package cy.com.allview.act;

import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;

import cy.com.allview.R;

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
        Uri uri=Uri.parse("content://cy.com.allview.contentprovider.BookProvider");
        getContentResolver().query(uri,null,null,null,null);
        getContentResolver().query(uri,null,null,null,null);
        getContentResolver().query(uri,null,null,null,null);
    }
}
