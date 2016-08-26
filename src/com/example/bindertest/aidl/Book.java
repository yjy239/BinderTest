package com.example.bindertest.aidl;

import android.os.Parcel;
import android.os.Parcelable;

public class Book implements Parcelable{
	public int bookId;
	public String bookName;
	
	public Book(int bookId,String bookName){
		this.bookId = bookId;
		this.bookName = bookName;
	}

	@Override
	public int describeContents() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public void writeToParcel(Parcel out, int flags) {
		// TODO Auto-generated method stub
		out.writeInt(bookId);
		out.writeString(bookName);
	}
	
	public static final Parcelable.Creator<Book> CREATOR = new Parcelable.Creator<Book>() {

		@Override
		public Book createFromParcel(Parcel in) {
			// TODO Auto-generated method stub
			return new Book(in);
		}

		@Override
		public Book[] newArray(int size) {
			// TODO Auto-generated method stub
			return new Book[size];
		}
		
	};
	
	private Book(Parcel in){
		bookId = in.readInt();
		bookName = in.readString();
	}
	
	public String toString(){
		return bookName;
	}
	
}
