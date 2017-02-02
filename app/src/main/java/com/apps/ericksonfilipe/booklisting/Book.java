package com.apps.ericksonfilipe.booklisting;

import java.util.ArrayList;
import java.util.List;

public class Book {

    private String title;
    private String publisher;
    private List<String> authors;


    public Book() {
        this.authors = new ArrayList<>();
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getPublisher() {
        return publisher;
    }

    public void setPublisher(String publisher) {
        this.publisher = publisher;
    }

    public List<String> getAuthors() {
        return authors;
    }

    public void setAuthors(List<String> authors) {
        this.authors = authors;
    }
}
