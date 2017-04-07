// IBookManager.aidl
package cy.com.allview;

// Declare any non-default types here with import statements
import cy.com.allview.bean.Book;
import cy.com.allview.IOnNewBookArrivedLisenter;
interface IBookManager {
    /**
     * Demonstrates some basic types that you can use as parameters
     * and return values in AIDL.
     */
    List<Book> getBookList();
    void addBook(in Book book);
    void registerOnNewBookArrivedLisenter(IOnNewBookArrivedLisenter lisenter);
    void unregisterOnNewBookArrivedLisenter(IOnNewBookArrivedLisenter lisenter);
}
