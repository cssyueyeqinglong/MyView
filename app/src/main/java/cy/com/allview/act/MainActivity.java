package cy.com.allview.act;

import android.Manifest;
import android.app.Activity;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Build;
import android.os.Environment;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.IBinder;
import android.os.Looper;
import android.os.RemoteException;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.NotificationCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.RemoteViews;
import android.widget.Toast;

import com.orhanobut.logger.Logger;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;

import cy.com.allview.ISecurityCenter;
import cy.com.allview.aidl.BinderPool;
import cy.com.allview.aidl.SecurityCenterImpl;
import cy.com.allview.bean.Book;
import cy.com.allview.bean.MyUser;
import cy.com.allview.R;
import cy.com.allview.bean.User;
import cy.com.allview.contentprovider.MyWidget01;
import cy.com.allview.view.PwdView;
import cy.com.allview.view.WeatherView;

import static cy.com.allview.contentprovider.MyWidget01.CLICK_ACTION;

public class MainActivity extends AppCompatActivity implements PwdView.InputCompleteLisenter {

    private PwdView mPv;
    private HandlerThread mHandlerThread;
    private Handler mHandler;
    private static final int REQUEST_SELECT_PICTURE = 0x01;
    private WeatherView mWeatherView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mPv = (PwdView) findViewById(R.id.pwd);
        mWeatherView = (WeatherView) findViewById(R.id.weatherView);

        mPv.setInputCompleteLisenter(this);
        MyUser.mUserId = 2;
        Log.d("log", "MainActivity.useId====" + MyUser.mUserId);
        //模拟通过本地文件来实现IPC
        mHandlerThread = new HandlerThread("Test", 5);
        mHandlerThread.start();
        mHandler = new Handler(mHandlerThread.getLooper());
        mHandler.post(mTask);


    }

    public void chooseImgs(View view) {

        startActivity(new Intent(this, ImageAct.class));
    }

    public void chooseImgs02(View view) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN
                && ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {
            Logger.d("权限不足");
        } else {
            Intent intent = new Intent();
            intent.setType("image/*");
            intent.setAction(Intent.ACTION_GET_CONTENT);
            intent.addCategory(Intent.CATEGORY_OPENABLE);
            startActivityForResult(Intent.createChooser(intent, "Select Picture"), REQUEST_SELECT_PICTURE);
        }
    }

    private Runnable mTask = new Runnable() {
        @Override
        public void run() {

            //通过Binder池来实现当多个AIDL文件同时调起服务端而避免实现很多个Service，从而简化到只需要开启一个服务，然后通过
            //Binder池来找到对应的Binder，
            //如果需要在添加一个AIDL，就不需要再申请一个服务来实现，而只用在BinderPool中多加一个binderCode就好了
            BinderPool binderPool = BinderPool.getInstance(MainActivity.this);
            IBinder binder = binderPool.queryBinder(BinderPool.SECTURY_CENTER_AIDL_CODE);
            ISecurityCenter iSecurityCenter = SecurityCenterImpl.Stub.asInterface(binder);
            try {
                String abcde = iSecurityCenter.encrypt("abcde");
                Logger.d("encode===" + abcde);
            } catch (RemoteException e) {
                e.printStackTrace();
            }
            if (true) return;
            //写入文件到本地
            User user = new User("张三", 20, 60);
            String path;
            if (Environment.getExternalStorageState()
                    .equals(Environment.MEDIA_MOUNTED)) {
                //sd卡存在
                path = Environment.getExternalStorageDirectory() +
                        "/Android/" + getPackageName() + "/data";

            } else {
                path = getCacheDir() + getPackageName() + "/data";
            }
            File file = new File(path);
            if (!file.exists()) {
                file.mkdirs();
            }
            File mFile = new File(file, "user.text");
            ObjectOutputStream oos = null;
            try {
                oos = new ObjectOutputStream(new FileOutputStream(mFile));
                oos.writeObject(user);
                Logger.d("user===" + user);
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                try {
                    if (oos != null) {
                        oos.close();
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        startActivity(new Intent(getApplicationContext(), SecondActivity.class));
                    }
                });
            }
        }

    };

    @Override
    public void inputComplete() {
        String content = mPv.getEditContent();
        Toast.makeText(this, content, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void deleteContent(boolean isDelete) {

    }

    @Override
    protected void onStart() {
        super.onStart();
        mWeatherView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainActivity.this, LocationActivity.class));
            }
        });
        mWeatherView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                startActivity(new Intent(MainActivity.this, LocationFilter.class));
                return true;
            }
        });
    }

    //生成通知栏
    public void notifyStates(View view) {
        Notification.Builder notification = new Notification.Builder(this);
        notification.setSmallIcon(R.mipmap.ic_launcher);
        notification.setWhen(System.currentTimeMillis());
        notification.setContentTitle("This is Title");
        notification.setContentText("Hello World!");
//        notification.setDefaults( Notification.FLAG_AUTO_CANCEL);
        Intent intent = new Intent(this, SecondActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivities(this, 0, new Intent[]{intent}, PendingIntent.FLAG_UPDATE_CURRENT);
        notification.setContentIntent(pendingIntent);
        Notification nf = notification.build();
        nf.flags = Notification.FLAG_AUTO_CANCEL;
        //通过RemoteViews自定义通知栏布局
        RemoteViews remoteViews = new RemoteViews(getPackageName(), R.layout.notity_for_me);
        remoteViews.setImageViewResource(R.id.iv_01, R.mipmap.bg_verify_press);
        remoteViews.setTextViewText(R.id.tv_01, "右标题");
        remoteViews.setTextViewText(R.id.tv_02, "右内容");
        remoteViews.setOnClickPendingIntent(R.id.tv_02, PendingIntent.getActivities(this, 0, new Intent[]{new Intent(this, ThridActivity.class)}, PendingIntent.FLAG_UPDATE_CURRENT));
        nf.contentView = remoteViews;
        NotificationManager manager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        manager.notify(1, nf);
    }

    //生成桌面小控件
    public void tableCreate(View view) {
        Intent intent = new Intent();
        intent.setAction(CLICK_ACTION);
        sendBroadcast(intent);
    }

    //模拟通知栏
    public void modifyNotify(View view) {
        Intent intent = new Intent(this, NoticeActOne.class);
        startActivity(intent);
    }
}
