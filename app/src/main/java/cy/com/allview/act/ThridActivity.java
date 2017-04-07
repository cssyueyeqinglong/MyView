package cy.com.allview.act;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.Messenger;
import android.os.RemoteException;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import com.orhanobut.logger.Logger;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;

import cy.com.allview.R;
import cy.com.allview.bean.User;
import cy.com.allview.service.IPCService1;

/**
 * Created by Administrator
 * on 2017/3/31.
 * des:
 */

public class ThridActivity extends AppCompatActivity {

    private Messenger mMessenger;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.act_thrid);
        new Thread(new Runnable() {
            @Override
            public void run() {
                getCacheData();
            }
        }).start();
        Intent intent = new Intent(this, IPCService1.class);
        bindService(intent, mConnection, Context.BIND_AUTO_CREATE);
    }

    private ServiceConnection mConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            mMessenger = new Messenger(service);//先实例化Messenger对象
            Message message = Message.obtain(null, 1);
            Bundle bundle = new Bundle();
            bundle.putString("msg", "来自于客户端的请求");
            message.setData(bundle);//Message数据封装Message数据
            //将客户端响应服务端的Messenger通过这个参数传递给服务端，这样服务端接受到数据之后就会通过这个replyMsg将数据反馈回来，从而达到接收的效果
            message.replyTo = replyMsg;
            try {
                mMessenger.send(message);//通过Messgener发送数据，让服务端接受
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {

        }
    };

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unbindService(mConnection);
    }

    //为了接受服务端的传递数据，客户端需要也定义一个Handler和Messenger
    private static class MsgHandler extends Handler {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case 2://服务端回应
                    Logger.d(msg.getData().getString("reply"));
                    break;
                default:
                    super.handleMessage(msg);
                    break;
            }
        }
    }

    private Messenger replyMsg = new Messenger(new MsgHandler());

    private void getCacheData() {
        User user = null;
        String path = null;
        if (Environment.getExternalStorageState()
                .equals(Environment.MEDIA_MOUNTED)) {
            //sd卡存在
            path = Environment.getExternalStorageDirectory() +
                    "/Android/" + getPackageName() + "/data";

        } else {
            path = getCacheDir() + getPackageName() + "/data";
        }
        File mFile = new File(path, "user.text");
        if (mFile.exists()) {
            ObjectInputStream ois = null;
            try {
                ois = new ObjectInputStream(new FileInputStream(mFile));
                user = (User) ois.readObject();
                Logger.d("user=22222222222222=============" + user);
            } catch (IOException e) {
                e.printStackTrace();
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                try {
                    if (ois != null) {
                        ois.close();
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public void nextTo(View view) {
        startActivity(new Intent(this, ProviderAct.class));
    }
}
