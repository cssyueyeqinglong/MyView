package cy.com.allview.aidl;

import android.os.RemoteException;

import cy.com.allview.bean.IComputeSum;

/**
 * Created by Administrator
 * on 2017/4/8.
 * des:另外一个相加计算的AIDL实现类
 */

public class ComputeImpl extends IComputeSum.Stub {
    @Override
    public int add(int a, int b) throws RemoteException {
        return a + b;
    }
}
