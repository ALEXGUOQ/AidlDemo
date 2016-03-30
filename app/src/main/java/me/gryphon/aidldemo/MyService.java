package me.gryphon.aidldemo;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.os.RemoteCallbackList;
import android.os.RemoteException;
import android.util.Log;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.atomic.AtomicBoolean;

public class MyService extends Service {
    private CopyOnWriteArrayList<Book> bookLists = new CopyOnWriteArrayList<Book>();
    private RemoteCallbackList<IOnNewBookArrivedListener> listeners = new RemoteCallbackList<IOnNewBookArrivedListener>();
    private AtomicBoolean mIsServiceDestoryed = new AtomicBoolean(false);

    private IBinder mBinder = new IBookManager.Stub(){

        @Override
        public List<Book> getBooks() throws RemoteException {
            return bookLists;
        }

        @Override
        public void addBook(Book book) throws RemoteException {
            bookLists.add(book);
        }

        @Override
        public void registerListener(IOnNewBookArrivedListener listener) throws RemoteException {
            listeners.register(listener);
            Log.d("MyService", "registerListener finish");
        }

        @Override
        public void unRegisterListener(IOnNewBookArrivedListener listener) throws RemoteException {
            listeners.unregister(listener);

            Log.d("MyService", "unRegisterListener finish");
        }
    };

    @Override
    public IBinder onBind(Intent intent) {
        return mBinder;
    }

    @Override
    public void onCreate() {
        super.onCreate();

        bookLists.add(new Book(1, "android"));
        bookLists.add(new Book(2,"ios"));
        bookLists.add(new Book(3,"python"));

        new Thread(new AddBookThread()).start();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mIsServiceDestoryed.set(true);
    }

    private void onNewBookArrived(Book book){
        bookLists.add(book);
        Log.d("MainActivity ----", "bookLists.size():" + bookLists.size());

        final int N = listeners.beginBroadcast();
        Log.d("MainActivity", "N:" + "listeners size :" + N);

        for(int i = 0; i < N; i++){
            IOnNewBookArrivedListener listener = (IOnNewBookArrivedListener) listeners.getBroadcastItem(i);

            Log.d("MyService", "listener:" + listener);
            if (null != listener){
                try {
                    listener.onNewBookArrived(book);
                } catch (RemoteException e) {
                    e.printStackTrace();
                }
            }
        }

        listeners.finishBroadcast();

        Log.d("MyService", "looper finish ....");
    }

    private class AddBookThread implements Runnable{
        @Override
        public void run() {
            while (!mIsServiceDestoryed.get()) {
                Log.d("MainActivity", "run: add a Book");
                try {
                    Thread.sleep(2000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

                int bookId = bookLists.size() + 1;
                String bookName = "new Book" + bookId;
                onNewBookArrived(new Book(bookId, bookName));
            }
        }
    }
}
