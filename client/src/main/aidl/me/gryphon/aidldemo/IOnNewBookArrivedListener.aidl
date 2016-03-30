// IOnNewBookArrivedListener.aidl
package me.gryphon.aidldemo;

import me.gryphon.aidldemo.Book;

interface IOnNewBookArrivedListener {
    void onNewBookArrived(in Book book);
}
