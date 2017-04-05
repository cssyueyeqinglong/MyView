package cy.cy.com.allview.act;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentActivity;
import android.util.Log;

import cy.cy.com.allview.bean.MyUser;
import cy.cy.com.allview.R;

/**
 * Created by Administrator
 * on 2017/3/31.
 * des:
 */

public class SecondActivity extends FragmentActivity {
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.act_second);

        Log.d("log", "SecondActivity.useId====" + MyUser.mUserId);
        startActivity(new Intent(this, ThridActivity.class));
    }
}
