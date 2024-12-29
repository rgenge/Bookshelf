package com.bookshelf.bookshelf.service;

import org.springframework.transaction.annotation.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.bookshelf.bookshelf.model.Book;
import com.bookshelf.bookshelf.model.Author;
import com.bookshelf.bookshelf.repository.BookRepository;
import com.bookshelf.bookshelf.repository.AuthorRepository;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

@Service
public class BookService {
    private final AuthorRepository authorRepository;
    private final BookRepository bookRepository;
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Autowired
    public BookService(BookRepository bookRepository, AuthorRepository authorRepository) {
        this.bookRepository = bookRepository;
        this.authorRepository = authorRepository;
    }
    public List<Book> findBooksByLanguages(String languages) {
        return bookRepository.findBooksByLanguages(languages);}
    public List<Book> searchBooks(String query) {
        List<Book> books = new ArrayList<>();
        try {
            String encodedQuery = java.net.URLEncoder.encode(query, "UTF-8");

            HttpClient client = HttpClient.newHttpClient();
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(new URI("https://gutendex.com/books/?search=" + encodedQuery))
                    .GET()
                    .build();

            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
            books = parseBooksFromJson(response.body());
            bookRepository.saveAll(books);

        } catch (Exception e) {
            System.out.println("Erro ao buscar livros: " + e.getMessage());
            e.printStackTrace();
        }
        return books;
    }

    private List<Book> parseBooksFromJson(String json) {
        List<Book> books = new ArrayList<>();
        try {
            JsonNode rootNode = objectMapper.readTree(json);
            JsonNode resultsNode = rootNode.get("results");

            if (resultsNode != null && resultsNode.isArray()) {
                for (JsonNode bookNode : resultsNode) {
                    Book book = new Book();
                    System.out.println(bookNode);
                    book.setId(bookNode.get("id").asInt());
                    book.setTitle(bookNode.get("title").asText());
                    JsonNode languagesNode = bookNode.get("languages");
                    if (languagesNode.isArray()) {
                        List<String> languagesList = new ArrayList<>();
                        for (JsonNode languageNode : languagesNode) {
                            languagesList.add(languageNode.asText());
                        }
                        String languages = String.join(", ", languagesList);
                        book.setLanguages(languages);
                    } else {
                        book.setLanguages(null);
                    }
                    JsonNode authorsNode = bookNode.get("authors");
                    if (authorsNode != null && authorsNode.isArray()) {
                        for (JsonNode authorNode : authorsNode) {
                            Author author = createOrFindAuthor(authorNode);
                            book.addAuthor(author);
                        }
                    }
                    books.add(book);
                }
            }
        } catch (Exception e) {
            System.out.println("Erro ao processar JSON: " + e.getMessage());
            e.printStackTrace();
        }
        return books;
    }

    @Transactional
    private Author createOrFindAuthor(JsonNode authorNode) {
        String name = authorNode.get("name").asText();

        Author existingAuthor = authorRepository.findByName(name);
        if (existingAuthor != null) {
            return existingAuthor;
        }
        Author author = new Author();
        author.setName(name);
        JsonNode birthYearNode = authorNode.get("birth_year");
        if (birthYearNode != null && !birthYearNode.isNull()) {
            author.setBirthYear(birthYearNode.asInt());
        }
        JsonNode deathYearNode = authorNode.get("death_year");
        if (deathYearNode != null && !deathYearNode.isNull()) {
            author.setDeathYear(deathYearNode.asInt());
        }
        return authorRepository.save(author);
    }
}
