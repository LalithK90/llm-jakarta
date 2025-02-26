package learning.jakarta.ai.bookstore.web;

import jakarta.annotation.PostConstruct;
import jakarta.faces.view.ViewScoped;
import jakarta.inject.Inject;
import jakarta.inject.Named;
import learning.jakarta.ai.bookstore.domain.Book;
import learning.jakarta.ai.bookstore.service.BookStoreService;
import learning.jakarta.ai.bookstore.service.CartSession;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.util.List;
import java.util.ArrayList;

@Named
@ViewScoped
public class BookStoreBean implements Serializable {

    @Inject
    private BookStoreService bookStoreService;

    @Getter
    private List<Book> books;

    @Getter @Setter
    private String searchQuery;

    @Getter
    private String userId;

    private CartSession currentCart;

    @PostConstruct
    public void init() {
        // Generate userId in the same format as JavaScript
        long timestamp = System.currentTimeMillis();
        String randomStr = Long.toString(Math.abs(java.util.UUID.randomUUID().getLeastSignificantBits()), 36).substring(0, 13);
        userId = String.format("user-%d-%s", timestamp, randomStr);

        // Initialize cart
        currentCart = bookStoreService.getOrCreateCart(userId);

        // Load initial book list
        showAllBooks();
    }

    public void showAllBooks() {
        books = bookStoreService.getAllBooks();
    }

    public void searchBooks() {
        if (searchQuery != null && !searchQuery.trim().isEmpty()) {
            books = bookStoreService.searchBooks(searchQuery);
        } else {
            showAllBooks();
        }
    }

    public void filterByCategory(String category) {
        books = bookStoreService.searchByCategory(category);
    }

    public void addToCart(String isbn) {
        if (currentCart == null) {
            currentCart = bookStoreService.getOrCreateCart(userId);
        }

        try {
            bookStoreService.addToCart(userId, isbn, 1);
            books = bookStoreService.getAllBooks();
        } catch (IllegalArgumentException e) {
            // Handle error (e.g., show message to user)
        }
    }

    public int getCartItemCount() {
        return currentCart != null ? currentCart.getCart().getItems().size() : 0;
    }

    public double getCartTotal() {
        return currentCart != null ? currentCart.getCart().getTotal() : 0.0;
    }

    public void removeFromCart(String isbn) {
        if (currentCart != null) {
            bookStoreService.removeFromCart(userId, isbn);
        }
    }
}
