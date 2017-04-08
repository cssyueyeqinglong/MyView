// ISecurityCenter.aidl
package cy.com.allview;

// Declare any non-default types here with import statements

interface ISecurityCenter {
    //加密
   String encrypt(String content);
   //解密
   String decrypt(String content);
}
