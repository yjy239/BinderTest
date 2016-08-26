package com.example.bindertest.aidl;

import com.example.bindertest.aidl.Book;

interface IOnNewBookArrived{
	void onNewBookArrived(in Book book);
}