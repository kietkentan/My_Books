package com.khtn.mybooks.model;

public class Order {
    private String bookId;
    private String bookName;
    private String bookImage;
    private String publisherId;
    private int bookQuantity;
    private int bookPrice;
    private int bookDiscount;
    private boolean selected;

    public Order() {}

    public Order(String bookId, int bookQuantity) {
        this.bookId = bookId;
        this.bookQuantity = bookQuantity;
    }

    public Order(String bookId, String bookName, String bookImage, String publisherId, int bookQuantity, int bookPrice, int bookDiscount) {
        this.bookId = bookId;
        this.bookName = bookName;
        this.bookImage = bookImage;
        this.publisherId = publisherId;
        this.bookQuantity = bookQuantity;
        this.bookPrice = bookPrice;
        this.bookDiscount = bookDiscount;
    }

    public Order(Book book, int bookQuantity){
        bookId = book.getId();
        bookName = book.getName();
        bookImage = book.getImage().get(0);
        publisherId = book.getPublisher();
        this.bookQuantity = bookQuantity;
        bookPrice = book.getOriginalPrice();
        bookDiscount = book.getDiscount();
    }

    public String getBookId() {
        return bookId;
    }

    public void setBookId(String bookId) {
        this.bookId = bookId;
    }

    public String getBookName() {
        return bookName;
    }

    public void setBookName(String bookName) {
        this.bookName = bookName;
    }

    public String getBookImage() {
        return bookImage;
    }

    public void setBookImage(String bookImage) {
        this.bookImage = bookImage;
    }

    public String getPublisherId() {
        return publisherId;
    }

    public void setPublisherId(String publisherId) {
        this.publisherId = publisherId;
    }

    public int getBookQuantity() {
        return bookQuantity;
    }

    public void setBookQuantity(int bookQuantity) {
        this.bookQuantity = bookQuantity;
    }

    public int getBookPrice() {
        return bookPrice;
    }

    public void setBookPrice(int bookPrice) {
        this.bookPrice = bookPrice;
    }

    public int getBookDiscount() {
        return bookDiscount;
    }

    public void setBookDiscount(int bookDiscount) {
        this.bookDiscount = bookDiscount;
    }

    public boolean isSelected() {
        return selected;
    }

    public void setSelected(boolean selected) {
        this.selected = selected;
    }
}
