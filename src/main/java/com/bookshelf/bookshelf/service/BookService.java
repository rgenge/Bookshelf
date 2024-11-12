package com.bookshelf.bookshelf.service;

import com.bookshelf.bookshelf.model.Book;
import org.springframework.stereotype.Service;

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

    public List<Book> searchBooks(String query) {
        List<Book> books = new ArrayList<>();

        try {
            // Create HttpClient
            HttpClient client = HttpClient.newHttpClient();

            // Create HttpRequest
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(new URI("https://gutendex.com/books/?search=" + query))
                    .GET()
                    .build();

            // Send HttpRequest and get HttpResponse
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

            // Parse the JSON response and map it to the list of Book objects
            books = parseBooksFromJson(response.body());
        } catch (Exception e) {
            e.printStackTrace();
        }

        return books;
    }

    private List<Book> parseBooksFromJson(String json) throws Exception {
        List<Book> books = new ArrayList<>();
        ObjectMapper objectMapper = new ObjectMapper();
        JsonNode rootNode = objectMapper.readTree(json);
        JsonNode results = rootNode.path("results");

        if (results.isArray()) {
            for (JsonNode node : results) {
                Book book = new Book();
                book.setTitle(node.path("title").asText());

                // Check if authors array is present and has elements
                JsonNode authorsNode = node.path("authors");
                if (authorsNode.isArray() && authorsNode.size() > 0) {
                    book.setAuthor(authorsNode.get(0).path("name").asText());
                } else {
                    book.setAuthor("Unknown Author");
                }

                books.add(book);
            }
        }

        return books;
    }
}
