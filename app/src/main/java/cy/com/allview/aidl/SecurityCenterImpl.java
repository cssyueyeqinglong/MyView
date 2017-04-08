package cy.com.allview.aidl;

import android.os.RemoteException;

import cy.com.allview.ISecurityCenter;

/**
 * Created by Administrator
 * on 2017/4/8.
 * des:其中一个加解密的AIDL实现类
 */

public class SecurityCenterImpl extends ISecurityCenter.Stub {
    @Override
    public String encrypt(String content) throws RemoteException {
        char[] chars = content.toCharArray();
        for (int i = 0; i < chars.length; i++) {
            chars[i]^=5;
        }
        return new String(chars);
    }

    @Override
    public String decrypt(String content) throws RemoteException {
        return encrypt(content);
    }
}
