package com.bookshelf.bookshelf.model;

public class Book {

    private String title;
    private String author;

    // Constructors
    public Book() {
    }

    public Book(String title, String author) {
        this.title = title;
        this.author = author;
    }

    // Getters and Setters
    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }
}
