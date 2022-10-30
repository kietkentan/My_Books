package com.khtn.mybooks.model;

public class Order {
    private String BookId;
    private String BookName;
    private String BookImage;
    private String PublisherId;
    private int BookQuantity;
    private int BookPrice;
    private int BookDiscount;
    private boolean selected;

    public Order() {}

    public Order(String bookId, String bookName, String bookImage, String publisherId, int bookQuantity, int bookPrice, int bookDiscount) {
        BookId = bookId;
        BookName = bookName;
        BookImage = bookImage;
        PublisherId = publisherId;
        BookQuantity = bookQuantity;
        BookPrice = bookPrice;
        BookDiscount = bookDiscount;
    }

    public String getBookId() {
        return BookId;
    }

    public void setBookId(String bookId) {
        BookId = bookId;
    }

    public String getBookName() {
        return BookName;
    }

    public void setBookName(String bookName) {
        BookName = bookName;
    }

    public String getBookImage() {
        return BookImage;
    }

    public void setBookImage(String bookImage) {
        BookImage = bookImage;
    }

    public String getPublisherId() {
        return PublisherId;
    }

    public void setPublisherId(String publisherId) {
        PublisherId = publisherId;
    }

    public int getBookQuantity() {
        return BookQuantity;
    }

    public void setBookQuantity(int bookQuantity) {
        BookQuantity = bookQuantity;
    }

    public int getBookPrice() {
        return BookPrice;
    }

    public void setBookPrice(int bookPrice) {
        BookPrice = bookPrice;
    }

    public int getBookDiscount() {
        return BookDiscount;
    }

    public void setBookDiscount(int bookDiscount) {
        BookDiscount = bookDiscount;
    }

    public boolean isSelected() {
        return selected;
    }

    public void setSelected(boolean selected) {
        this.selected = selected;
    }
}
