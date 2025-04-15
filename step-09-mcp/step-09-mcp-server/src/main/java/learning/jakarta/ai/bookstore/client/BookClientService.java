package learning.jakarta.ai.bookstore.client;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import org.eclipse.microprofile.rest.client.inject.RestClient;

import java.util.List;

@ApplicationScoped
public class BookClientService {

    @Inject
    @RestClient
    BookClient bookClient;
    
    public List<Book> getAllBooks() {
        return bookClient.getAllBooks();
    }
    
    public Book findBookByIsbn(String isbn) {
        return bookClient.getBookByIsbn(isbn);
    }
    
    public void addNewBook(Book book) {
        bookClient.createBook(book);
    }

}