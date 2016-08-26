package com.example.bindertest.aidl;

import com.example.bindertest.aidl.Book;
import com.example.bindertest.aidl.IOnNewBookArrived;

interface IBookManager{
	List<Book> getBookList();
	void addBook(in Book book);
	void registerListener(IOnNewBookArrived listener);
	void unregisterListener(IOnNewBookArrived listener);
}