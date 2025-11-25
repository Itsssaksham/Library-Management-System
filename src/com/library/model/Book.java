package com.library.model;
public class Book {
    private long id; private String title; private String author; private String category;
    private int totalCopies; private int availableCopies;
    public Book() {}
    public Book(long id, String title, String author, String category, int totalCopies, int availableCopies) {
        this.id = id; this.title = title; this.author = author; this.category = category;
        this.totalCopies = totalCopies; this.availableCopies = availableCopies;
    }
    public long getId() { return id; } public void setId(long id) { this.id = id; }
    public String getTitle() { return title; } public void setTitle(String title) { this.title = title; }
    public String getAuthor() { return author; } public void setAuthor(String author) { this.author = author; }
    public String getCategory() { return category; } public void setCategory(String category) { this.category = category; }
    public int getTotalCopies() { return totalCopies; } public void setTotalCopies(int totalCopies) { this.totalCopies = totalCopies; }
    public int getAvailableCopies() { return availableCopies; } public void setAvailableCopies(int availableCopies) { this.availableCopies = availableCopies; }
    @Override public String toString() { return id + "," + title + "," + author + "," + category + "," + totalCopies + "," + availableCopies; }
}
