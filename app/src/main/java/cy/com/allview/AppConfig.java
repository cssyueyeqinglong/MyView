package cy.com.allview;

import android.app.ActivityManager;
import android.app.Application;
import android.content.Context;
import android.os.Process;
import android.util.Log;

import com.orhanobut.logger.LogLevel;
import com.orhanobut.logger.Logger;

/**
 * Created by Administrator
 * on 2017/3/31.
 * des:
 */

public class AppConfig extends Application {
    @Override
    public void onCreate() {
        super.onCreate();

        Logger.init().logLevel(LogLevel.FULL);

        int pid=Process.myPid();
        Logger.d("pid=="+pid);
        String processName="";
        ActivityManager manager = (ActivityManager) getApplicationContext().getSystemService(Context.ACTIVITY_SERVICE);
        for (ActivityManager.RunningAppProcessInfo process: manager.getRunningAppProcesses()) {
            if(process.pid == pid)
            {
                processName = process.processName;
                break;
            }
        }
        Log.d("appInfo","application start:"+processName);
    }
}
