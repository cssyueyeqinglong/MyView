package cy.com.allview.service;

import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;
import android.support.annotation.Nullable;

import cy.com.allview.aidl.BinderPool;

/**
 * Created by Administrator
 * on 2017/4/8.
 * des:实现Binder池的服务端
 */

public class BinderPoolService extends Service {
    private Binder mBinderPool=new BinderPool.BinderPoolImpl();
    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return mBinderPool;
    }
}
