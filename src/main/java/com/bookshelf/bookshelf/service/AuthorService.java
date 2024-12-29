
package com.bookshelf.bookshelf.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;
import com.bookshelf.bookshelf.model.Author;
import com.bookshelf.bookshelf.repository.AuthorRepository;

@Service
public class AuthorService {

    @Autowired
    private AuthorRepository authorRepository;

    public List<Author> getAllAuthors() {
        return authorRepository.findAllAuthors();
    }
}
