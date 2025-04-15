package learning.jakarta.ai.bookstore.client;

import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import org.eclipse.microprofile.rest.client.annotation.RegisterProvider;
import org.eclipse.microprofile.rest.client.inject.RegisterRestClient;

import java.util.List;


@Path("/rest/books")
@RegisterRestClient(configKey = "book-api")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
@RegisterProvider(learning.jakarta.ai.bookstore.client.BookClientExceptionMapper.class)
public interface BookClient {

    /**
     * Get all books
     * @return List of all books
     */
    @GET
    List<Book> getAllBooks();

    /**
     * Get a book by ISBN
     * @param isbn The ISBN of the book
     * @return The book with the given ISBN
     */
    @GET
    @Path("/{isbn}")
    Book getBookByIsbn(@PathParam("isbn") String isbn);

    /**
     * Get all book categories
     * @return List of all categories
     */
    @GET
    @Path("/categories")
    List<String> getAllCategories();

    /**
     * Get books by category
     * @param category The category to filter by
     * @return List of books in the given category
     */
    @GET
    @Path("/category/{category}")
    List<Book> getBooksByCategory(@PathParam("category") String category);

    /**
     * Search for books by title or author
     * @param query The search query
     * @return List of books matching the query
     */
    @GET
    @Path("/search")
    List<Book> searchBooks(@QueryParam("query") String query);

    /**
     * Create a new book
     * @param book The book to create
     * @return The created book
     */
    @POST
    Book createBook(Book book);

    /**
     * Update an existing book
     * @param isbn The ISBN of the book to update
     * @param book The updated book data
     * @return The updated book
     */
    @PUT
    @Path("/{isbn}")
    Book updateBook(@PathParam("isbn") String isbn, Book book);

    /**
     * Update the stock quantity of a book
     * @param isbn The ISBN of the book
     * @param quantity The new stock quantity
     */
    @PATCH
    @Path("/{isbn}/stock/{quantity}")
    void updateStockQuantity(@PathParam("isbn") String isbn, @PathParam("quantity") int quantity);

    /**
     * Create or update multiple books
     * @param books The list of books to create or update
     */
    @POST
    @Path("/batch")
    void createBooks(List<Book> books);
}