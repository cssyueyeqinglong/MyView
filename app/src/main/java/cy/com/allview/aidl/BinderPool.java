package cy.com.allview.aidl;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Binder;
import android.os.IBinder;
import android.os.RemoteException;

import java.util.concurrent.CountDownLatch;

import cy.com.allview.IBinderPool;
import cy.com.allview.service.BinderPoolService;

/**
 * Created by Administrator
 * on 2017/4/8.
 * des:binder池AIDL的实现类
 */

public class BinderPool {
    public static final int COMPUTE_AIDL_CODE = 0;//计算和的bindercode;
    public static final int SECTURY_CENTER_AIDL_CODE = 1;//加解密的的bindercode;
    private Context mContext;
    private IBinderPool mBinderPool;
    private static volatile BinderPool mInstance;
    private CountDownLatch mConnectBinderPoolCountDownLatch;

    private BinderPool(Context context) {
        mContext = context.getApplicationContext();
        connectBinderPoolService();
    }

    //单实例模式，使用饿汉式
    public static BinderPool getInstance(Context context) {
        if (mInstance == null) {
            synchronized (BinderPool.class) {
                if (mInstance == null) {
                    mInstance = new BinderPool(context);
                }
            }
        }
        return mInstance;
    }

    //发起请求
    private synchronized void connectBinderPoolService() {
        mConnectBinderPoolCountDownLatch = new CountDownLatch(1);
        Intent service = new Intent(mContext, BinderPoolService.class);
        mContext.bindService(service, mServiceConnection, Context.BIND_AUTO_CREATE);
        try {
            mConnectBinderPoolCountDownLatch.await();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public IBinder queryBinder(int binderCode) {
        IBinder binder = null;
        try {
            if (mBinderPool != null) {
                binder = mBinderPool.queryBinder(binderCode);
            }
        } catch (RemoteException e) {
            e.printStackTrace();
        }
        return binder;
    }

    private ServiceConnection mServiceConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            mBinderPool = IBinderPool.Stub.asInterface(service);
            try {
                mBinderPool.asBinder().linkToDeath(mDeathRecipient, 0);//服务假死后重启开启服务
            } catch (RemoteException e) {
                e.printStackTrace();
            }
            mConnectBinderPoolCountDownLatch.countDown();
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {

        }
    };

    private IBinder.DeathRecipient mDeathRecipient=new IBinder.DeathRecipient() {
        @Override
        public void binderDied() {
            //当连接断开后重新连接
            mBinderPool.asBinder().unlinkToDeath(mDeathRecipient,0);
            mBinderPool=null;
            connectBinderPoolService();
        }
    };

    //实现了Binder池AIDL的类
    public static class BinderPoolImpl extends IBinderPool.Stub{

        public BinderPoolImpl(){
            super();
        }
        @Override
        public IBinder queryBinder(int binderCode) throws RemoteException {
            IBinder binder = null;
            switch (binderCode) {
                case COMPUTE_AIDL_CODE:
                    binder = new ComputeImpl();
                    break;
                case SECTURY_CENTER_AIDL_CODE:
                    binder = new SecurityCenterImpl();
                    break;
                default:break;
            }
            return binder;
        }
    }
}
