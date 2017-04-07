package cy.com.allview.service;

import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;
import android.os.RemoteCallbackList;
import android.os.RemoteException;
import android.support.annotation.Nullable;

import com.orhanobut.logger.Logger;

import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.atomic.AtomicBoolean;

import cy.com.allview.IBookManager;
import cy.com.allview.IOnNewBookArrivedLisenter;
import cy.com.allview.bean.Book;

/**
 * Created by Administrator
 * on 2017/4/6.
 * des:aidl进程间通讯用例Service
 */

public class BookManagerService extends Service {
    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return mBinder;//2.返回Binder实例
    }

    private CopyOnWriteArrayList<Book> mBookList = new CopyOnWriteArrayList<Book>();//CopyOnWriteArrayList支持并发读写
    //注意，当客户端同一个对象多次传入到服务端的时候，服务端会生成一个新的对象，所以尽管客户端传输的是同一个对象，但是解除注册传进来客户端同一个的
    //Lisenter在服务端是两个不同的对象，所以接触主册的时候找不到原来注册的那个对象，就需要用到RemoteCallbackList,这个是专门用键值对来存储远程的Lisetner
    //根据binder对象来找到lisenter，从而删除掉，
//    private CopyOnWriteArrayList<IOnNewBookArrivedLisenter> mLisenters = new CopyOnWriteArrayList<IOnNewBookArrivedLisenter>();
    private RemoteCallbackList<IOnNewBookArrivedLisenter> mLisenters = new RemoteCallbackList<IOnNewBookArrivedLisenter>();
    private AtomicBoolean mIsServiceDetoryed = new AtomicBoolean(false);
    //1.声明通过AIDL生成的Binder
    private Binder mBinder = new IBookManager.Stub() {
        @Override
        public List<Book> getBookList() throws RemoteException {
            return mBookList;//服务端数据
        }

        @Override
        public void addBook(Book book) throws RemoteException {
            mBookList.add(book);//服务端方法
        }

        @Override
        public void registerOnNewBookArrivedLisenter(IOnNewBookArrivedLisenter lisenter) throws RemoteException {
//            if (!mLisenters.contains(lisenter)) {
//                mLisenters.add(lisenter);
//            } else {
//                Logger.d("lisenter is already exist!");
//            }
            mLisenters.register(lisenter);
        }

        @Override
        public void unregisterOnNewBookArrivedLisenter(IOnNewBookArrivedLisenter lisenter) throws RemoteException {
//            if (mLisenters.contains(lisenter)) {
//                mLisenters.remove(lisenter);
//                Logger.d("unregister lisenter is Succeed!");
//            } else {
//                Logger.d("lisenter is not found out!");
//            }
            mLisenters.unregister(lisenter);

        }
    };

    @Override
    public void onCreate() {
        super.onCreate();
        //初始化的时候添加两本数进去，从而方便服务端获取
        mBookList.add(new Book(0, "萧十一郎"));
        mBookList.add(new Book(1, "多情剑客无情剑"));
        new Thread(new WorkService()).start();
    }

    @Override
    public void onDestroy() {
        mIsServiceDetoryed.set(true);
        super.onDestroy();
    }

    private class WorkService implements Runnable {

        @Override
        public void run() {
            while (!mIsServiceDetoryed.get()) {//在服务没有销毁之前每5秒加一本书
                try {
                    Thread.sleep(5000);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                int bookId = mBookList.size() + 1;
                Book book = new Book(bookId, "古龙小说集(" + bookId + ")");
                try {
                    onNewBookArraived(book);
                } catch (RemoteException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    private void onNewBookArraived(Book book) throws RemoteException {
        mBookList.add(book);
//        for (int i = 0; i < mLisenters.size(); i++) {
//            mLisenters.get(i).onNewBookArrived(book);
//        }
        int num = mLisenters.beginBroadcast();
        for (int i = 0; i < num; i++) {
            IOnNewBookArrivedLisenter item = mLisenters.getBroadcastItem(i);
            if (item != null) {
                item.onNewBookArrived(book);
            }
        }
        mLisenters.finishBroadcast();
    }
}
