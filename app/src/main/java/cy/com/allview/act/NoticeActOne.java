package cy.com.allview.act;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.RemoteViews;

import cy.com.allview.R;

/**
 * Created by Administrator on 2017/5/4.
 * 模拟通知栏，冲当通知栏
 */

public class NoticeActOne extends AppCompatActivity {
    private LinearLayout mContainer;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.act_provide);
        mContainer = (LinearLayout) findViewById(R.id.home_view);
        IntentFilter filter = new IntentFilter(NoticeActTwo.MONI_ACTION);
        registerReceiver(mReceiver, filter);
        ActionBar actionBar = getSupportActionBar();
        actionBar.setTitle(this.getClass().getName());
    }

    private BroadcastReceiver mReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (NoticeActTwo.MONI_ACTION.equals(intent.getAction())) {//通知栏改变了
                RemoteViews mRemoteViews = intent.getParcelableExtra("remoteViews");
                if (mRemoteViews != null) {
                    updateUI(mRemoteViews);
                }
            }
        }
    };

    private void updateUI(RemoteViews remoteViews) {
        int identifier = getResources().getIdentifier("notity_for_me", "layout", getPackageName());
        View applyView = getLayoutInflater().inflate(identifier, mContainer, false);
        remoteViews.reapply(this, applyView);
        mContainer.addView(applyView);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(mReceiver);
    }

    public void sendNext(View view) {
        startActivity(new Intent(this, NoticeActTwo.class));
    }
}
