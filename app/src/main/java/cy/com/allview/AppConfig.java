package cy.com.allview;

import android.app.ActivityManager;
import android.app.Application;
import android.content.Context;
import android.os.Process;
import android.util.Log;

import com.baidu.mapapi.SDKInitializer;
import com.orhanobut.logger.LogLevel;
import com.orhanobut.logger.Logger;

import cy.com.allview.service.LocationService;

/**
 * Created by Administrator
 * on 2017/3/31.
 * des:
 */

public class AppConfig extends Application {
    public LocationService locationService;

    @Override
    public void onCreate() {
        super.onCreate();
/***
 * 初始化定位sdk，建议在Application中创建
 */
        locationService = new LocationService(getApplicationContext());

        Logger.init().logLevel(LogLevel.FULL).hideThreadInfo();
        SDKInitializer.initialize(this);
        int pid = Process.myPid();
        Logger.d("pid==" + pid);
        String processName = "";
        ActivityManager manager = (ActivityManager) getApplicationContext().getSystemService(Context.ACTIVITY_SERVICE);
        for (ActivityManager.RunningAppProcessInfo process : manager.getRunningAppProcesses()) {
            if (process.pid == pid) {
                processName = process.processName;
                break;
            }
        }
        Log.d("appInfo", "application start:" + processName);
    }
}
