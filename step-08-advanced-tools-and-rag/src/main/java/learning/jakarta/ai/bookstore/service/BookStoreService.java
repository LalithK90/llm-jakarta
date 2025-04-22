package learning.jakarta.ai.bookstore.service;

import dev.langchain4j.agent.tool.P;
import dev.langchain4j.agent.tool.Tool;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import learning.jakarta.ai.bookstore.domain.Book;
import learning.jakarta.ai.bookstore.domain.Cart;
import learning.jakarta.ai.bookstore.domain.CartItem;
import learning.jakarta.ai.bookstore.repository.BookRepository;
import learning.jakarta.ai.bookstore.web.BookUpdateWebSocket;
import lombok.extern.slf4j.Slf4j;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;

@ApplicationScoped
@Slf4j
public class BookStoreService {

	@Inject
	private BookRepository bookRepository;

	@Inject
	private CartSessionManager cartSessionManager;

	@Inject
	private BookUpdateWebSocket bookUpdateWebSocket;

	@Tool(name = "allCategories", value = "Retrieves a list of all distinct book categories/kinds available in the store.")
	public List<String> findAllCategories() {
		log.debug("Finding all distinct book categories.");
		List<String> categories = bookRepository.findAllCategories();
		log.info("Found {} distinct categories.", categories.size());
		return categories;
	}

	@Tool(name = "allBooks", value = "Retrieves a list of all available books, sorted by title.")
	public List<Book> getAllBooks() {
		log.debug("Retrieving all books from the repository.");
		List<Book> books = bookRepository.findAll();
		books.sort(Comparator.comparing(Book::getTitle, String.CASE_INSENSITIVE_ORDER));
		log.info("Retrieved and sorted {} books.", books.size());
		return books;
	}

	@Tool(name = "searchByCategory", value = "Searches for books within a specific category, sorted by title. Parameter: category (string)")
	public List<Book> searchByCategory(@P("category") String category) {
		String trimmedCategory = (category != null) ? category.trim() : "";
		if (trimmedCategory.isEmpty()) {
			log.warn("Category search query is blank, returning empty list.");
			return Collections.emptyList();
		}
		log.debug("Searching books in category: '{}'", trimmedCategory);
		List<Book> books = bookRepository.findByCategory(trimmedCategory);
		if (books.isEmpty()) {
			log.info("No books found in category: '{}'", trimmedCategory);
		} else {
			books.sort(Comparator.comparing(Book::getTitle, String.CASE_INSENSITIVE_ORDER));
			log.info("Found {} books in category '{}'.", books.size(), trimmedCategory);
		}
		return books;
	}

	@Tool(name = "searchBooks", value = "Searches books where the title OR author contains the query, sorted by title. Parameter: query (string)")
	public List<Book> searchBooks(@P("query") String query) {
		String trimmedQuery = (query != null) ? query.trim() : "";
		if (trimmedQuery.isEmpty()) {
			log.warn("Search query is blank, returning empty list.");
			return Collections.emptyList();
		}

		log.debug("Searching books where title or author contains: '{}'", trimmedQuery);
		List<Book> books = bookRepository.findByTitleOrAuthor(trimmedQuery, trimmedQuery);

		if (books.isEmpty()) {
			log.info("No books found matching query: '{}'", trimmedQuery);
		} else {
			books.sort(Comparator.comparing(Book::getTitle, String.CASE_INSENSITIVE_ORDER));
			log.info("Found {} books matching query '{}'.", books.size(), trimmedQuery);
		}
		return books;
	}

	@Tool(name = "getBookByIsbn", value = "Retrieves detailed information for a specific book using its ISBN. Parameter: isbn (string)")
	public Book getBookByIsbn(@P("isbn") String isbn) {
		String trimmedIsbn = (isbn != null) ? isbn.trim() : "";
		if (trimmedIsbn.isEmpty()) {
			throw new IllegalArgumentException("ISBN cannot be blank.");
		}
		log.debug("Getting book details for ISBN: {}", trimmedIsbn);
		return bookRepository.findByIsbn(trimmedIsbn)
				.orElseThrow(() -> new BookNotFoundException("No book found with ISBN: " + trimmedIsbn));
	}

	@Tool(name = "getOrCreateCart", value = "Retrieves the existing shopping cart for a user or creates a new one if none exists. Parameter: userId (string)")
	public CartSession getOrCreateCart(@P("userId") String userId) {
		log.debug("Getting or creating cart for user: {}", userId);
		return cartSessionManager.getOrCreateSession(userId);
	}

	@Tool(name = "addToCart", value = "Adds a specified quantity of a book (by ISBN) to the user's shopping cart. Parameters: userId (string), isbn (string), quantity (integer)")
	@Transactional
	public String addToCart(@P("userId") String userId, @P("isbn") String isbn, @P("quantity") int quantity) {
		log.debug("Attempting to add {} item(s) of book ISBN {} to cart for user {}", quantity, isbn, userId);

		if (quantity <= 0) {
			throw new IllegalArgumentException("Quantity must be a positive number.");
		}

		Book book = getBookByIsbn(isbn); // Uses the improved method which handles trimming and not found

		if (book.getStockQuantity() < quantity) {
			log.warn("Insufficient stock for book '{}' (ISBN: {}). Available: {}, Requested: {}", book.getTitle(), isbn, book.getStockQuantity(), quantity);
			throw new InsufficientStockException("Not enough stock for book '" + book.getTitle() + "'. Only " + book.getStockQuantity() + " available.");
		}

		int newStock = book.getStockQuantity() - quantity;
		log.info("Updating stock for book ISBN {} from {} to {}", isbn, book.getStockQuantity(), newStock);
		bookRepository.updateStockQuantity(isbn, newStock);
		bookUpdateWebSocket.notifyBookUpdate(); // Notify clients about stock change

		String result = cartSessionManager.addToCart(userId, book, quantity);
		log.info("Successfully added book ISBN {} to cart for user {}. Cart status: {}", isbn, userId, result);
		return result;
	}

	@Tool(name = "removeFromCart", value = "Removes a book (by ISBN) entirely from the user's shopping cart, restoring stock. Parameters: userId (string), isbn (string)")
	@Transactional // Added Transactional as it modifies stock
	public String removeFromCart(@P("userId") String userId, @P("isbn") String isbn) {
		String trimmedIsbn = (isbn != null) ? isbn.trim() : "";
		if (trimmedIsbn.isEmpty()) {
			throw new IllegalArgumentException("ISBN cannot be blank.");
		}

		log.debug("Attempting to remove book ISBN {} from cart for user {}", trimmedIsbn, userId);

		CartSession cartSession = getOrCreateCart(userId);
		Cart cart = cartSession.getCart();

		Optional<CartItem> itemToRemoveOpt = cart.getItems().stream()
				.filter(item -> item.getBook().getIsbn().equals(trimmedIsbn))
				.findFirst();

		if (itemToRemoveOpt.isPresent()) {
			CartItem itemToRemove = itemToRemoveOpt.get();
			int removedQuantity = itemToRemove.getQuantity();

			// Fetch the book again to ensure we have the latest stock count before updating
			Book book = getBookByIsbn(trimmedIsbn);
			int currentStock = book.getStockQuantity();
			int newStock = currentStock + removedQuantity;

			log.info("Restoring stock for book ISBN {} from {} to {} due to cart removal.", trimmedIsbn, currentStock, newStock);
			bookRepository.updateStockQuantity(trimmedIsbn, newStock);
			bookUpdateWebSocket.notifyBookUpdate(); // Notify clients about stock change

			String result = cartSessionManager.removeFromCart(userId, book);
			log.info("Successfully removed book ISBN {} from cart for user {}. Cart status: {}", trimmedIsbn, userId, result);
			return result;
		} else {
			log.warn("Book ISBN {} not found in cart for user {}. No action taken.", trimmedIsbn, userId);
			return "Book with ISBN " + trimmedIsbn + " was not found in the cart.";
		}
	}

	@Tool(name = "getRecommendations", value = "Gets up to 3 book recommendations from a specific category. Parameter: category (string)")
	public List<Book> getRecommendations(@P("category") String category) {
		String trimmedCategory = (category != null) ? category.trim() : "";
		if (trimmedCategory.isEmpty()) {
			log.warn("Recommendation category is blank, returning empty list.");
			return Collections.emptyList();
		}

		log.debug("Getting recommendations for category: '{}'", trimmedCategory);
		List<Book> recommendations = bookRepository.findTop3ByCategory(trimmedCategory); // Assuming a more direct repository method

		log.info("Found {} recommendations for category '{}'.", recommendations.size(), trimmedCategory);
		return recommendations;
	}

	@Tool(name = "getCartSummary", value = "Provides a summary of the contents and total price of the user's shopping cart. Parameter: userId (string)")
	public String getCartSummary(@P("userId") String userId) {
		log.debug("Getting cart summary for user: {}", userId);
		CartSession cartSession = getOrCreateCart(userId);
		String summary = cartSession.getCartSummary();
		log.info("Cart summary for user {}: {}", userId, summary);
		return summary;
	}

	@Tool(name = "getStatus", value = "Retrieves the current status of the user's shopping cart. Parameter: userId (string)")
	public String getStatus(@P("userId") String userId) {
		return cartSessionManager.getCartStatus(userId);
	}

	public static class BookNotFoundException extends RuntimeException {
		public BookNotFoundException(String message) {
			super(message);
		}
	}

	public static class InsufficientStockException extends RuntimeException {
		public InsufficientStockException(String message) {
			super(message);
		}
	}
}