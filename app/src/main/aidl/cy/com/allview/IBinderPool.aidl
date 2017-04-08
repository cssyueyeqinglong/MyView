// IBinderPool.aidl
package cy.com.allview;

// Declare any non-default types here with import statements

interface IBinderPool {
/**
* @param binderCode,unique binderCode query
* @return accroding binderCode get Binder
*/
   IBinder queryBinder(int binderCode);
}
