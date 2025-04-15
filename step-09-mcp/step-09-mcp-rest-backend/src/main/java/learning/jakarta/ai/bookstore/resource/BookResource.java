package learning.jakarta.ai.bookstore.resource;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import learning.jakarta.ai.bookstore.domain.Book;
import learning.jakarta.ai.bookstore.repository.BookRepository;

import java.util.List;
import java.util.Optional;

@ApplicationScoped
@Path("/books")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class BookResource {

    @Inject
    private BookRepository bookRepository;

    @GET
    public Response getAllBooks() {
        List<Book> books = bookRepository.findAll();
        return Response.ok(books).build();
    }

    @GET
    @Path("/{isbn}")
    public Response getBookByIsbn(@PathParam("isbn") String isbn) {
        Optional<Book> book = bookRepository.findByIsbn(isbn);
        return book.map(value -> Response.ok(value).build())
                .orElse(Response.status(Response.Status.NOT_FOUND).build());
    }

    @GET
    @Path("/categories")
    public Response getAllCategories() {
        List<String> categories = bookRepository.findAllCategories();
        return Response.ok(categories).build();
    }

    @GET
    @Path("/category/{category}")
    public Response getBooksByCategory(@PathParam("category") String category) {
        List<Book> books = bookRepository.findByCategory(category);
        return Response.ok(books).build();
    }

    @GET
    @Path("/search")
    public Response searchBooks(@QueryParam("query") String query) {
        List<Book> books = bookRepository.findByTitleContainingIgnoreCaseOrAuthorContainingIgnoreCase(query, query);
        return Response.ok(books).build();
    }

    @POST
    @Transactional
    public Response createBook(Book book) {
        bookRepository.save(book);
        return Response.status(Response.Status.CREATED).entity(book).build();
    }

    @PUT
    @Path("/{isbn}")
    @Transactional
    public Response updateBook(@PathParam("isbn") String isbn, Book book) {
        if (!isbn.equals(book.getIsbn())) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity("ISBN in path does not match ISBN in book data").build();
        }
        
        Optional<Book> existingBook = bookRepository.findByIsbn(isbn);
        if (existingBook.isEmpty()) {
            return Response.status(Response.Status.NOT_FOUND).build();
        }
        
        bookRepository.save(book);
        return Response.ok(book).build();
    }

    @PATCH
    @Path("/{isbn}/stock/{quantity}")
    @Transactional
    public Response updateStockQuantity(@PathParam("isbn") String isbn, @PathParam("quantity") int quantity) {
        Optional<Book> existingBook = bookRepository.findByIsbn(isbn);
        if (existingBook.isEmpty()) {
            return Response.status(Response.Status.NOT_FOUND).build();
        }
        
        bookRepository.updateStockQuantity(isbn, quantity);
        return Response.ok().build();
    }

    @POST
    @Path("/batch")
    @Transactional
    public Response createBooks(List<Book> books) {
        bookRepository.saveAll(books);
        return Response.status(Response.Status.CREATED).build();
    }
}