package learning.jakarta.ai.bookstore.tools;

import io.quarkiverse.mcp.server.Tool;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.json.bind.Jsonb;
import learning.jakarta.ai.bookstore.client.BookClient;
import learning.jakarta.ai.bookstore.client.Book;
import org.eclipse.microprofile.rest.client.inject.RestClient;

import java.math.BigDecimal;

@ApplicationScoped
public class BookTool {

    @Inject
    @RestClient
    private BookClient bookClient;

    @Inject
    Jsonb jsonb;

    @Tool(description = "Get all books from the bookstore")
    public String getAllBooks() {
        return jsonb.toJson(bookClient.getAllBooks());
    }

    @Tool(description = "Find a book by its ISBN")
    public String findBookByIsbn(String isbn) {
        return jsonb.toJson(bookClient.getBookByIsbn(isbn));
    }

    @Tool(description = "Get all book categories available in the bookstore")
    public String getAllCategories() {
        return jsonb.toJson(bookClient.getAllCategories());
    }

    @Tool(description = "Find books by category")
    public String getBooksByCategory(String category) {
        return jsonb.toJson(bookClient.getBooksByCategory(category));
    }

    @Tool(description = "Search for books by title or author")
    public String searchBooks(String query) {
        return jsonb.toJson(bookClient.searchBooks(query));
    }

    @Tool(description = "Create a new book in the bookstore")
    public String createBook(String isbn, String title, String author, String description,
                           double price, int stockQuantity, String category, String imageUrl) {
        Book book = new Book(
            null, 
            isbn, 
            title, 
            author, 
            description, 
            new BigDecimal(price), 
            stockQuantity, 
            category, 
            imageUrl
        );
        return jsonb.toJson(bookClient.createBook(book));
    }

    @Tool(description = "Update an existing book by ISBN")
    public String updateBook(String isbn, String title, String author, String description,
                          double price, int stockQuantity, String category, String imageUrl) {
        Book book = new Book(
            null, 
            isbn, 
            title, 
            author, 
            description, 
            new BigDecimal(price), 
            stockQuantity, 
            category, 
            imageUrl
        );
        return jsonb.toJson(bookClient.updateBook(isbn, book));
    }

    @Tool(description = "Update the stock quantity of a book")
    public String updateStockQuantity(String isbn, int quantity) {
        bookClient.updateStockQuantity(isbn, quantity);
        return "Updated stock quantity of book with ISBN: " + isbn + " to " + quantity;
    }

    @Tool(description = "Get information about a book to display to the user")
    public String getBookInformation(String isbn) {
        Book book = bookClient.getBookByIsbn(isbn);
        if (book == null) {
            return "No book found with ISBN: " + isbn;
        }
        
        return String.format("""
            Title: %s
            Author: %s
            ISBN: %s
            Category: %s
            Price: $%.2f
            Stock: %d
            Description: %s
            """,
            book.title(),
            book.author(),
            book.isbn(),
            book.category(),
            book.price(),
            book.stockQuantity(),
            book.description()
        );
    }
    
    @Tool(description = "Check if a book is in stock")
    public String isBookInStock(String isbn) {
        Book book = bookClient.getBookByIsbn(isbn);
        if(book != null && book.stockQuantity() > 0)
        {
            return "Book is in stock";
        }
        return "Book is out of stock";
    }
}