package org.example.repository;

import org.example.model.Book;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

import java.sql.Statement;
import java.sql.PreparedStatement;
import java.util.List;
import java.util.Optional;

@Repository
public class JdbcBookRepository implements BookRepository {
    private final JdbcTemplate jdbcTemplate;

    @Autowired
    public JdbcBookRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    private final RowMapper<Book> rowMapper = ((rs, rowNum) -> new Book(
            rs.getLong("id"),
            rs.getString("title"),
            rs.getString("author"),
            rs.getInt("publication_year")
    ));

    @Override
    public Book save(Book book) {
        if (book.getId() == null) {
            String sql = "INSERT INTO books (title, author, publication_year) VALUES (?,?,?)";
            KeyHolder keyHolder = new GeneratedKeyHolder();

            jdbcTemplate.update(con -> {
                PreparedStatement ps = con.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
                ps.setString(1, book.getTitle());
                ps.setString(2, book.getAuthor());
                ps.setInt(3, book.getPublicationYear());
                return ps;
            }, keyHolder);

            book.setId(keyHolder.getKey().longValue());
        } else {
            String sql = "UPDATE books SET title = ?, author = ?, publication_year = ? WHERE id = ?";
            jdbcTemplate.update(sql,
                    book.getId(),
                    book.getTitle(),
                    book.getAuthor(),
                    book.getPublicationYear());
        }
        return book;
    }

    @Override
    public List<Book> findAllBooks() {
        String sql = "SELECT * FROM books";
        return jdbcTemplate.query(sql, rowMapper);
    }

    @Override
    public Optional<Book> findBookById(Long bookId) {
        String sql = "SELECT * FROM books WHERE id = ?";
        return jdbcTemplate.query(sql, rowMapper, bookId).stream().findFirst();
    }

    @Override
    public void deleteBookById(Long bookId) {
        String sql = "DELETE FROM books WHERE id = ?";
        jdbcTemplate.update(sql, bookId);
    }
}
