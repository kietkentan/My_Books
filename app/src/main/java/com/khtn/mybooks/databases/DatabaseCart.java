package com.khtn.mybooks.databases;

import android.annotation.SuppressLint;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteQueryBuilder;

import com.khtn.mybooks.model.Order;
import com.readystatesoftware.sqliteasset.SQLiteAssetHelper;

import java.util.ArrayList;
import java.util.List;

public class DatabaseCart extends SQLiteAssetHelper {
    private static final String DB_NAME = "MyBooksDB.db";
    private static final int DB_VERSION = 1;

    public DatabaseCart(Context context) {
        super(context, DB_NAME, null, DB_VERSION);
    }

    @SuppressLint("Range")
    public List<Order> getCarts(){
        SQLiteDatabase database = getReadableDatabase();
        SQLiteQueryBuilder builder = new SQLiteQueryBuilder();

        String[] sqlSelected = {"BookId", "BookName", "BookImage", "PublisherId", "BookQuantity", "BookPrice", "BookDiscount"};
        String sqlTable = "OrderDetail";

        builder.setTables(sqlTable);
        Cursor cursor = builder.query(database, sqlSelected, null, null, null, null, null);

        final List<Order> result = new ArrayList<>();
        if (cursor.moveToFirst()){
            do {
                result.add(new Order(cursor.getString(cursor.getColumnIndex("BookId")),
                        cursor.getString(cursor.getColumnIndex("BookName")),
                        cursor.getString(cursor.getColumnIndex("BookImage")),
                        cursor.getString(cursor.getColumnIndex("PublisherId")),
                        cursor.getInt(cursor.getColumnIndex("BookQuantity")),
                        cursor.getInt(cursor.getColumnIndex("BookPrice")),
                        cursor.getInt(cursor.getColumnIndex("BookDiscount"))));
            } while (cursor.moveToNext());
        }
        return result;
    }

    @SuppressLint("Range")
    public void addCart(Order order){
        SQLiteDatabase database = getReadableDatabase();
        String queryFind = String.format("SELECT BookQuantity FROM OrderDetail WHERE BookId = '%s'", order.getBookId());
        @SuppressLint("Recycle") Cursor cursor = database.rawQuery(queryFind, null);
        if (cursor.moveToFirst())
            updateCart(order.getBookId(), cursor.getInt(cursor.getColumnIndex("BookQuantity")) + 1);
        else {
            @SuppressLint("DefaultLocale") String query = String.format("INSERT INTO OrderDetail(BookId, BookName, BookImage, PublisherId, BookQuantity, BookPrice, BookDiscount)" +
                            " VALUES('%s', '%s', '%s', '%s', %d, %d, %d)",
                    order.getBookId(),
                    order.getBookName(),
                    order.getBookImage(),
                    order.getPublisherId(),
                    order.getBookQuantity(),
                    order.getBookPrice(),
                    order.getBookDiscount());
            database.execSQL(query);
        }
    }

    public void updateCart(String BookId, int BookQuantity){
        SQLiteDatabase database = getReadableDatabase();
        @SuppressLint("DefaultLocale") String queryUpdate = String.format("UPDATE OrderDetail SET BookQuantity = %d WHERE BookId = '%s'",
                BookQuantity,
                BookId);
        database.execSQL(queryUpdate);
        database.close();
    }

    public void cleanCarts(){
        SQLiteDatabase database = getReadableDatabase();
        String query = String.format("DELETE FROM OrderDetail");
        database.execSQL(query);
    }

    public void removeCarts(String bookId){
        SQLiteDatabase database = getReadableDatabase();
        String query = String.format("DELETE FROM OrderDetail WHERE BookId = '%s'", bookId);
        database.execSQL(query);
    }
}
