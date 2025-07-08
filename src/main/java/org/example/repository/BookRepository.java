package org.example.repository;

import org.example.model.Book;
import java.util.List;
import java.util.Optional;

public interface BookRepository {
    Book save(Book book);

    List<Book> findAllBooks();

    Optional<Book> findBookById(Long bookId);

    void deleteBookById(Long bookId);
}
