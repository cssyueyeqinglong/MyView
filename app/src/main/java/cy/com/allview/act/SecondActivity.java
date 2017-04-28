package cy.com.allview.act;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.RemoteException;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentActivity;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;

import com.orhanobut.logger.Logger;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.List;

import cy.com.allview.IBookManager;
import cy.com.allview.IOnNewBookArrivedLisenter;
import cy.com.allview.bean.Book;
import cy.com.allview.bean.MyUser;
import cy.com.allview.R;
import cy.com.allview.bean.User;
import cy.com.allview.service.BookManagerService;

/**
 * Created by Administrator
 * on 2017/3/31.
 * des:
 */

public class SecondActivity extends AppCompatActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.act_second);

        Log.d("log", "SecondActivity.useId====" + MyUser.mUserId);
        Intent intent = new Intent(this, BookManagerService.class);
        bindService(intent, conn, Context.BIND_AUTO_CREATE);

    }

    public void nextAct(View view) {
        startActivity(new Intent(this, ThridActivity.class));
    }

    private IBookManager mRomateIBookManager;
    private ServiceConnection conn = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            IBookManager mStub = IBookManager.Stub.asInterface(service);
            try {
                mRomateIBookManager = mStub;
                List<Book> bookList = mStub.getBookList();

                Logger.d("listType=================" + bookList.getClass().getCanonicalName());
                Logger.d(bookList.toString());
                mStub.addBook(new Book(2, "边城浪子"));
                Logger.d(mStub.getBookList().toString());
                mStub.registerOnNewBookArrivedLisenter(mNewLisenr);
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            mRomateIBookManager = null;
        }
    };

    @Override
    protected void onDestroy() {

        if (mRomateIBookManager != null && mRomateIBookManager.asBinder().isBinderAlive()) {
            try {
                Logger.d("unregist lisenter");
                mRomateIBookManager.unregisterOnNewBookArrivedLisenter(mNewLisenr);
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        }

        unbindService(conn);
        super.onDestroy();
    }

    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case 3://收到新书通知
                    Logger.d("收到新书的通知了" + msg.obj);
                    break;
                default:
                    super.handleMessage(msg);
            }
        }
    };

    private IOnNewBookArrivedLisenter mNewLisenr = new IOnNewBookArrivedLisenter.Stub() {
        @Override
        public void onNewBookArrived(Book book) throws RemoteException {
            mHandler.obtainMessage(3, book).sendToTarget();
        }
    };
}
