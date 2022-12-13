package com.khtn.mybooks.databases;

import android.annotation.SuppressLint;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteQueryBuilder;

import com.khtn.mybooks.model.BookItem;
import com.readystatesoftware.sqliteasset.SQLiteAssetHelper;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class DatabaseViewed extends SQLiteAssetHelper {
    private static final String DB_NAME = "MyBooksDB.db";
    private static final int DB_VERSION = 1;

    public DatabaseViewed(Context context) {
        super(context, DB_NAME, null, DB_VERSION);
    }

    @SuppressLint("Range")
    public List<BookItem> getListsViewed(){
        SQLiteDatabase database = getReadableDatabase();
        SQLiteQueryBuilder builder = new SQLiteQueryBuilder();

        String[] sqlSelected = {"BookId", "BookImage", "BookName", "BookAmount", "DatePosted", "BookPrice", "BookDiscount", "PublisherId"};
        String sqlTable = "RecentlyViewed";

        builder.setTables(sqlTable);
        Cursor cursor = builder.query(database, sqlSelected, null, null, null, null, null);

        final List<BookItem> result = new ArrayList<>();
        if (cursor.moveToFirst()){
            do {
                result.add(new BookItem(Collections.singletonList(cursor.getString(cursor.getColumnIndex("BookImage"))),
                        cursor.getInt(cursor.getColumnIndex("BookPrice")),
                        cursor.getInt(cursor.getColumnIndex("BookDiscount")),
                        cursor.getInt(cursor.getColumnIndex("BookAmount")),
                        cursor.getString(cursor.getColumnIndex("BookName")),
                        cursor.getString(cursor.getColumnIndex("DatePosted")),
                        cursor.getString(cursor.getColumnIndex("BookId")),
                        cursor.getString(cursor.getColumnIndex("PublisherId"))));
            } while (cursor.moveToNext());
        }
        return result;
    }

    @SuppressLint("Range")
    public void addViewed(BookItem book){
        SQLiteDatabase database = getReadableDatabase();
        String queryFind = String.format("SELECT BookId FROM RecentlyViewed WHERE BookId = '%s'", book.getId());
        @SuppressLint("Recycle") Cursor cursor = database.rawQuery(queryFind, null);
        if (!cursor.moveToFirst()) {
            @SuppressLint("DefaultLocale") String query = String.format("INSERT INTO RecentlyViewed(BookId, BookImage," +
                            " BookName, BookAmount, DatePosted, BookPrice, BookDiscount, PublisherId)" +
                            " VALUES('%s', '%s', '%s', '%d', '%s', '%d', '%d', '%s')",
                    book.getId(),
                    book.getImage().get(0),
                    book.getName(),
                    book.getAmount(),
                    book.getDatePosted(),
                    book.getOriginalPrice(),
                    book.getDiscount(),
                    book.getPublisher());
            database.execSQL(query);
        }
    }

    public void cleanViewed(){
        SQLiteDatabase database = getReadableDatabase();
        String query = "DELETE FROM RecentlyViewed";
        database.execSQL(query);
    }
}
