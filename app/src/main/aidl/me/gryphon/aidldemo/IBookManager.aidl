// IBookManager.aidl
package me.gryphon.aidldemo;

import me.gryphon.aidldemo.Book;
import me.gryphon.aidldemo.IOnNewBookArrivedListener;

interface IBookManager {
    List<Book> getBooks();
    void addBook(in Book book);
    void registerListener(IOnNewBookArrivedListener listener);
    void unRegisterListener(IOnNewBookArrivedListener listener);
}
