package cy.com.allview.act;

import android.content.Intent;
import android.os.Environment;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Looper;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.orhanobut.logger.Logger;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;

import cy.com.allview.bean.Book;
import cy.com.allview.bean.MyUser;
import cy.com.allview.R;
import cy.com.allview.bean.User;
import cy.com.allview.view.PwdView;

public class MainActivity extends AppCompatActivity implements PwdView.InputCompleteLisenter {

    private PwdView mPv;
    private HandlerThread mHandlerThread;
    private Handler mHandler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mPv = (PwdView) findViewById(R.id.pwd);
        mPv.setInputCompleteLisenter(this);
        MyUser.mUserId = 2;
        Log.d("log", "MainActivity.useId====" + MyUser.mUserId);
        mHandlerThread = new HandlerThread("Test", 5);
        mHandlerThread.start();
        mHandler = new Handler(mHandlerThread.getLooper());
        mHandler.post(mTask);
    }

    private Runnable mTask = new Runnable() {
        @Override
        public void run() {
            User user = new User("张三", "20", 60);
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
}
