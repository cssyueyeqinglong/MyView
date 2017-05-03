package cy.com.allview.contentprovider;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.os.SystemClock;
import android.widget.RemoteViews;

import com.orhanobut.logger.Logger;

import cy.com.allview.R;


/**
 * Created by Administrator on 2017/5/3.
 */

public class MyWidget01 extends AppWidgetProvider {
    public static final String CLICK_ACTION = "myWidget01_click_action";

    public MyWidget01() {
        super();
    }

    @Override
    public void onReceive(final Context context, final Intent intent) {
        super.onReceive(context, intent);
        Logger.d("onReceive:" + intent.getAction());
        if (CLICK_ACTION.equals(intent.getAction())) {
            new Thread(new Runnable() {
                @Override
                public void run() {
                    Bitmap bitmap = BitmapFactory.decodeResource(context.getResources(), R.mipmap.preview);
                    AppWidgetManager manager = AppWidgetManager.getInstance(context);
                    for (int i = 0; i < 37; i++) {
                        float degree = (i * 10) % 360;
                        RemoteViews remoteViews = new RemoteViews(context.getPackageName(), R.layout.weight_01);
                        remoteViews.setImageViewBitmap(R.id.iv_01, rotateBitmap(context, bitmap, degree));
                        Intent data = new Intent();
                        data.setAction(CLICK_ACTION);
                        PendingIntent pendingIntent = PendingIntent.getBroadcast(context, 0, data, 0);
                        remoteViews.setOnClickPendingIntent(R.id.iv_01, pendingIntent);
                        manager.updateAppWidget(new ComponentName(context, MyWidget01.class), remoteViews);
                        SystemClock.sleep(300);
                    }
                }
            }).start();
        }

    }

    //每次桌面小部件更新时都调用
    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        super.onUpdate(context, appWidgetManager, appWidgetIds);
        Logger.d("onUpdate");
        int count = appWidgetIds.length;
        Logger.d("onUpdate:count==" + count);
        for (int i = 0; i < count; i++) {
            int appWidgetId = appWidgetIds[i];
            onWidgetUpdate(context, appWidgetManager, appWidgetId);
        }

    }

    /**
     * 桌面小部件跟新
     *
     * @param context
     * @param appWidgetManager
     * @param appWidgetId
     */
    private void onWidgetUpdate(Context context, AppWidgetManager appWidgetManager, int appWidgetId) {
        Logger.d("appWidgetId===" + appWidgetId);
        RemoteViews remoteViews = new RemoteViews(context.getPackageName(), R.layout.weight_01);
        Intent data = new Intent();
        data.setAction(CLICK_ACTION);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(context, 0, data, 0);
        remoteViews.setOnClickPendingIntent(R.id.iv_01, pendingIntent);
        appWidgetManager.updateAppWidget(appWidgetId, remoteViews);
    }

    private Bitmap rotateBitmap(Context context, Bitmap src, float degree) {
        Matrix matrix = new Matrix();
        matrix.reset();
        matrix.setRotate(degree);
        return Bitmap.createBitmap(src, 0, 0, src.getWidth(), src.getHeight(), matrix, true);

    }
}
