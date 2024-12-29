package com.bookshelf.bookshelf.repository;

import com.bookshelf.bookshelf.model.Book;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.util.List;

@Repository
public interface BookRepository extends JpaRepository<Book, Integer> {
    @Query("SELECT b FROM Book b WHERE b.languages LIKE %:languages%")
    List<Book> findBooksByLanguages(@Param("languages") String languages);
}
