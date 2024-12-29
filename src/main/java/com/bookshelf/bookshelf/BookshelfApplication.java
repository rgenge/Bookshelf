package com.bookshelf.bookshelf;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

import java.util.List;
import java.util.Scanner;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.net.URI;
import java.io.BufferedReader;
import java.io.InputStreamReader;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.bookshelf.bookshelf.model.Book;
import com.bookshelf.bookshelf.service.BookService;
import com.bookshelf.bookshelf.model.Author;
import com.bookshelf.bookshelf.service.AuthorService;

@EnableJpaRepositories(basePackages = "com.bookshelf.bookshelf.repository")
@SpringBootApplication

public class BookshelfApplication implements CommandLineRunner {

    private final BookService bookService;
    private final AuthorService authorService;

    @Autowired
    public BookshelfApplication(BookService bookService, AuthorService authorService) {
        this.bookService = bookService;
        this.authorService = authorService;
    }

    public static void main(String[] args) {
        SpringApplication.run(BookshelfApplication.class, args);
    }

    @Override
    public void run(String... args) throws Exception {
        BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));

        System.out.println("=== Aplicação de Busca de Livros ===");
        System.out.println("Aplicação iniciada! Pronta para buscar livros.");

        try {
            while (true) {
                System.out.println("\nEscolha uma opção:");
                System.out.println("1. Digite o nome do livro que deseja buscar (ou 'sair' para encerrar)");
                System.out.println("2. Listar autores por nascimento");
                System.out.println("3. Listar livros por linguagem");
                System.out.print("Opção: ");
                System.out.flush();

                String option = reader.readLine();
                System.out.println("Você escolheu: " + option);

                if (option == null || option.equalsIgnoreCase("sair")) {
                    System.out.println("Encerrando a aplicação...");
                    System.exit(0);
                    break;
                }

                switch (option) {
                    case "1":
                        System.out.println("\nDigite o nome do livro que deseja buscar (ou 'sair' para encerrar):");
                        String query = reader.readLine();
                        System.out.println("Você digitou: " + query);

                        if (query == null || query.equalsIgnoreCase("sair")) {
                            System.out.println("Encerrando a aplicação...");
                            System.exit(0);
                            break;
                        }
                        System.out.println("Buscando livros com o termo: " + query);
                        List<Book> books = bookService.searchBooks(query);
                        if (books.isEmpty()) {
                            System.out.println("Nenhum livro encontrado com esse termo.");
                        } else {
                            System.out.println("\nLivros encontrados:");
                            for (Book book : books) {
                                System.out.println("- " + book.getTitle() + " (ID: " + book.getId() + ", Languages: " + book.getLanguages() + ")");
                            }
                        }
                        break;

                    case "2":
                        System.out.println("Listando autores:");
                        List<Author> authors = authorService.getAllAuthors();
                        if (authors.isEmpty()) {
                            System.out.println("Nenhum autor encontrado.");
                        } else {
                            for (Author author : authors) {
                                System.out.println("- " + author.getName() + " (Birth Year: " + author.getBirthYear() + ", Death Year: " + author.getDeathYear() + ")");
                            }
                        }
                        break;

                    case "3":
                        System.out.println("\nDigite a linguagem do livro que deseja buscar (ou 'sair' para encerrar):");
                        String languages = reader.readLine();
                        System.out.println("Você digitou: " + languages);
                        if (languages == null || languages.equalsIgnoreCase("sair")) {
                            System.out.println("Encerrando a aplicação...");
                            System.exit(0); break; }
                        System.out.println("Buscando livros na linguagem: " + languages);
                        List<Book> booksByLanguages = bookService.findBooksByLanguages(languages);
                        if (booksByLanguages.isEmpty()) {
                            System.out.println("Nenhum livro encontrado nessa linguagem.");
                        } else {
                            System.out.println("\nLivros encontrados:");
                            for (Book book : booksByLanguages) {
                                System.out.println("- " + book.getTitle() + " (ID: " + book.getId() + ", Languages: " + book.getLanguages() + ")"); }
                        }
                        System.out.println("Total de livros na linguagem " + languages + ": " + booksByLanguages.size());
                        break;

                    default:
                        System.out.println("Opção inválida. Tente novamente.");
                        break;
                }
            }
        } catch (Exception e) {
            System.out.println("Erro durante a execução: " + e.getMessage());
            e.printStackTrace();
        } finally {
            reader.close();
        }

    }
}