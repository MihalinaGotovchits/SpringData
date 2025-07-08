package org.example.service;

import org.example.model.Book;
import org.example.repository.BookRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class BookServiceIml implements BookService {
    private final BookRepository bookRepository;

    @Autowired
    public BookServiceIml(BookRepository bookRepository) {
        this.bookRepository = bookRepository;
    }

    @Override
    public Book create(Book book) {
        return bookRepository.save(book);
    }

    @Override
    public Book update(Long bookId, Book book) {
        Book book1 = bookRepository.findBookById(bookId).orElseThrow(() ->
                new RuntimeException("Книга не найдена"));
        book1.setTitle(book1.getTitle());
        book1.setAuthor(book.getAuthor());
        book1.setPublicationYear(book1.getPublicationYear());
        return bookRepository.save(book1);
    }

    @Override
    public List<Book> getAllBooks() {
        return bookRepository.findAllBooks();
    }

    @Override
    public Book getBookById(Long bookId) {
        return bookRepository.findBookById(bookId).orElseThrow(() ->
                new RuntimeException("Книга не найдена"));
    }

    @Override
    public void deleteBookById(Long bookId) {
        bookRepository.deleteBookById(bookId);
    }
}
