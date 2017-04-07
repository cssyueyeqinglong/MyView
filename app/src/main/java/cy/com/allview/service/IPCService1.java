package cy.com.allview.service;

import android.app.Service;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.Messenger;
import android.os.RemoteException;
import android.support.annotation.Nullable;

import com.orhanobut.logger.Logger;

/**
 * Created by Administrator
 * on 2017/4/5.
 * des:服务端Service,通过Messenger实现进程间通讯
 */

public class IPCService1 extends Service {
    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return mMessenger.getBinder();//返回Messenger的Binder.即handler
    }

    private static class IPCHandler extends Handler {
        @Override
        public void handleMessage(Message msg) {

            switch (msg.what) {
                case 1://客户端响应返回的数据
                    Logger.d("serviceData====" + msg.getData().getString("msg"));
                    Messenger client = msg.replyTo;//获取客户端的Messenger;
                    Message message = Message.obtain(null, 2);
                    Bundle bundle = new Bundle();
                    bundle.putString("reply", "服务端已经收到请求，响应了客户端");
                    message.setData(bundle);
                    try {
                        client.send(message);

                    } catch (RemoteException e) {
                        e.printStackTrace();
                    }

                    break;
                default:
                    super.handleMessage(msg);
                    break;
            }
        }
    }

    private final Messenger mMessenger = new Messenger(new IPCHandler());
}
