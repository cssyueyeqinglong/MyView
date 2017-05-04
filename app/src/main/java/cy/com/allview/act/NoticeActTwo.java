package cy.com.allview.act;

import android.app.PendingIntent;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.RemoteViews;

import cy.com.allview.R;

/**
 * Created by Administrator on 2017/5/4.
 * 客户端；；向通知栏发消息
 */

public class NoticeActTwo extends AppCompatActivity {
    public static final String MONI_ACTION = "moni_action01";

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.act_thrid);
        ActionBar actionBar = getSupportActionBar();
        actionBar.setTitle(this.getClass().getName());
        RemoteViews remoteViews = new RemoteViews(getPackageName(), R.layout.notity_for_me);
        remoteViews.setImageViewResource(R.id.iv_01, R.mipmap.huaji);
        remoteViews.setTextViewText(R.id.tv_01, "哈利路亚");
        remoteViews.setTextViewText(R.id.tv_02, "黄飞鸿");
        PendingIntent pendingIntent = PendingIntent.getActivities(this, 0, new Intent[]{new Intent(this, SecondActivity.class)}, PendingIntent.FLAG_UPDATE_CURRENT);
        remoteViews.setOnClickPendingIntent(R.id.iv_01, pendingIntent);
        Intent intent = new Intent();
        intent.setAction(MONI_ACTION);
        intent.putExtra("remoteViews", remoteViews);
        sendBroadcast(intent);
    }


}
