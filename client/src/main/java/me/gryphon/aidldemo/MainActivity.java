package me.gryphon.aidldemo;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.RemoteException;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    private IBookManager manager;
    private ArrayList<Book> books;
    private static final int MSG_FROM_SERVICE = 1;

    private Handler mMessageHandle = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what){
                case MSG_FROM_SERVICE:
                    Log.d("MainActivity", "on new book comming:" + msg.obj);
                    break;
                default:
                    super.handleMessage(msg);
            }
        }
    };

    private IOnNewBookArrivedListener listener = new IOnNewBookArrivedListener.Stub() {
        @Override
        public void onNewBookArrived(Book book) throws RemoteException {
            Log.d("MainActivity", "onNewBookArrived: revice msg..............");
            mMessageHandle.obtainMessage(MSG_FROM_SERVICE,book).sendToTarget();
        }
    };

    private ServiceConnection conn = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            manager = IBookManager.Stub.asInterface(service);

            try {
                manager.registerListener(listener);

                books = (ArrayList<Book>) manager.getBooks();

                Log.d("MainActivity", "books:" + books);

            } catch (RemoteException e) {
                e.printStackTrace();
            }
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            manager = null;
        }
    };


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        Intent intent = new Intent();
        intent.setComponent(new ComponentName("me.gryphon.aidldemo","me.gryphon.aidldemo.MyService"));
        bindService(intent,conn, Context.BIND_AUTO_CREATE);

    }

    public void addBook(View view) throws RemoteException {
        manager.addBook(new Book(1,"Android"));

        Log.d("MainActivity", "manager.getBooks():" + manager.getBooks());
    }

    @Override
    protected void onDestroy() {
        if(null != manager && manager.asBinder().isBinderAlive()){
            try {
                manager.unRegisterListener(listener);
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        }

        unbindService(conn);

        super.onDestroy();
    }
}
