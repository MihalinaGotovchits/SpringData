package org.example.service;

import org.example.model.Book;

import java.util.List;

public interface BookService {
    Book create(Book book);
    Book update(Long bookId, Book book);
    List<Book> getAllBooks();
    Book getBookById(Long bookId);
    void deleteBookById(Long bookId);
}
