package com.bookshelf.bookshelf.repository;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import com.bookshelf.bookshelf.model.Author;
import java.util.List;

@Repository
public interface AuthorRepository extends JpaRepository<Author, Long> {
    Author findByName(String name);
    @Query("SELECT a FROM Author a ORDER BY a.birthYear ASC")
    List<Author> findAllAuthors();
}