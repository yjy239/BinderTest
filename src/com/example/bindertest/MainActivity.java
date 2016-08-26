package com.example.bindertest;

import java.util.List;

import com.example.bindertest.aidl.Book;
import com.example.bindertest.aidl.BookManagerService;
import com.example.bindertest.aidl.IBookManager;
import com.example.bindertest.aidl.IOnNewBookArrived;

import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.RemoteException;
import android.R.integer;
import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.util.Log;
import android.view.Menu;

public class MainActivity extends Activity {
	private static final String TAG = "BookManagerActvity";
	private static final int MESSAGE_NEW_BOOK_ARRIVED = 1;
	private IBookManager mRemoteBookManager;
	
	private Handler mHandler = new Handler(){
		@Override
		public void handleMessage(Message msg){
			switch (msg.what) {
			case MESSAGE_NEW_BOOK_ARRIVED:
				Log.e(TAG, "received a new book"+msg.obj);
				break;

			default:
				super.handleMessage(msg);
				break;
			}
		}
	};
	
	private ServiceConnection conn = new ServiceConnection() {
		
		@Override
		public void onServiceDisconnected(ComponentName classname) {
			// TODO Auto-generated method stub
			
		}
		
		@Override
		public void onServiceConnected(ComponentName classname, IBinder service) {
			// TODO Auto-generated method stub
			//判断是本地还是远程的
			IBookManager bookManager = IBookManager.Stub.asInterface(service);
			try{
				List<Book> list = bookManager.getBookList();
				Log.e(TAG, "query listType:"+list.getClass().getCanonicalName());
				Log.e(TAG, "query list element:"+list.toString());
				Book newbook = new Book(323, "a new book");
				bookManager.addBook(newbook);
				List<Book> list2 = bookManager.getBookList();
				Log.e(TAG, "query list2 element:"+list2.toString());
				mRemoteBookManager = bookManager;
				Book book2 = new Book(3, "Android up");
				mRemoteBookManager.addBook(book2);
				List<Book> list3=mRemoteBookManager.getBookList();
				Log.e(TAG, "query list2 element:"+list3.toString());
				bookManager.registerListener(listener);
			}catch(RemoteException e){
				e.printStackTrace();
			}
		}
	};
	
	private IOnNewBookArrived listener = new IOnNewBookArrived.Stub() {
		
		@Override
		public void onNewBookArrived(Book book) throws RemoteException {
			// TODO Auto-generated method stub
			mHandler.obtainMessage(MESSAGE_NEW_BOOK_ARRIVED,book).sendToTarget();
		}
	};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Intent intent = new Intent(MainActivity.this,BookManagerService.class);
        bindService(intent, conn, Context.BIND_AUTO_CREATE);
    }
    
    @Override
    protected void onDestroy(){
    	if((mRemoteBookManager != null)&&mRemoteBookManager.asBinder().isBinderAlive()){
    		try {
				Log.e(TAG, "unregister listener:"+listener);
				mRemoteBookManager.unregisterListener(listener);
			} catch (RemoteException e) {
				// TODO: handle exception
				e.printStackTrace();
			}
    	}
    	unbindService(conn);
    	super.onDestroy();
    }

    
    
}
