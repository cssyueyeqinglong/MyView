// IOnNewBookArrivedLisenter.aidl
package cy.com.allview;
import cy.com.allview.bean.Book;
// Declare any non-default types here with import statements

interface IOnNewBookArrivedLisenter {
   void onNewBookArrived(in Book book);
}
