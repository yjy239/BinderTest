package com.example.bindertest.aidl;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.atomic.AtomicBoolean;

import javax.security.auth.PrivateCredentialPermission;

import android.app.Service;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Binder;
import android.os.IBinder;
import android.os.RemoteCallbackList;
import android.os.RemoteException;
import android.util.Log;

public class BookManagerService extends Service{
	private static final String TAG = "BMS";
	
	private CopyOnWriteArrayList<Book> mBookList = new CopyOnWriteArrayList<Book>();
	private AtomicBoolean mIsServiceDestroyed = new AtomicBoolean(false);
	private RemoteCallbackList<IOnNewBookArrived> mListenerList = new RemoteCallbackList<IOnNewBookArrived>();
	
	private Binder mBinder = new IBookManager.Stub() {
		
		@Override
		public List<Book> getBookList() throws RemoteException {
			// TODO Auto-generated method stub
			return mBookList;
		}
		
		@Override
		public void addBook(Book book) throws RemoteException {
			// TODO Auto-generated method stub
			mBookList.add(book);
		}

		@Override
		public void registerListener(IOnNewBookArrived listener)
				throws RemoteException {
			// TODO Auto-generated method stub
			mListenerList.register(listener);
			
		}

		@Override
		public void unregisterListener(IOnNewBookArrived listener)
				throws RemoteException {
			// TODO Auto-generated method stub
			mListenerList.unregister(listener);
		}
	};

	@Override
	public IBinder onBind(Intent intent) {
		// TODO Auto-generated method stub
//		int check = checkCallingOrSelfPermission("com.example.bindertest.permission.ACCESS_BOOK_SERVICE");
//		if(check == PackageManager.PERMISSION_DENIED){
//			return null;
//		}
		return mBinder;
	}
	
	@Override
	public void onCreate(){
		super.onCreate();
		mBookList.add(new Book(1, "Android"));
		mBookList.add(new Book(2, "IOS"));
		new Thread(new ServiceWorker()).start();
		
	}
	
	@Override
	public void onDestroy(){
		mIsServiceDestroyed.set(true);
		super.onDestroy();
	}
	
	private void onNewBookArrived(Book book)throws RemoteException{
		mBookList.add(book);
		final int N = mListenerList.beginBroadcast();
		for(int i=0;i<N;i++){
			IOnNewBookArrived l = mListenerList.getBroadcastItem(i);
			if(l != null){
				try{
					l.onNewBookArrived(book);
				}catch (RemoteException e) {
					// TODO: handle exception
					e.printStackTrace();
				}
			}
		}
		mListenerList.finishBroadcast();
	}
	
	private class ServiceWorker implements Runnable{

		@Override
		public void run() {
			// TODO Auto-generated method stub
			while(!mIsServiceDestroyed.get()){
				try {
					Thread.sleep(5000);
				} catch (Exception e) {
					// TODO: handle exception
					e.printStackTrace();
				}
				int bookid = mBookList.size()+1;
				Book newBook = new Book(bookid, "new book#"+bookid);
				try {
					onNewBookArrived(newBook);
				} catch (Exception e) {
					// TODO: handle exception
					e.printStackTrace();
				}
			}
		}
		
	}
	
}
