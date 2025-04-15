package learning.jakarta.ai.bookstore.client;

import jakarta.ws.rs.core.Response;
import org.eclipse.microprofile.rest.client.ext.ResponseExceptionMapper;

public class BookClientExceptionMapper implements ResponseExceptionMapper<RuntimeException> {

    @Override
    public RuntimeException toThrowable(Response response) {
        if (response.getStatus() == 404) {
            return new BookNotFoundException("Book not found");
        } else if (response.getStatus() >= 400 && response.getStatus() < 500) {
            return new RuntimeException("Client error: " + response.getStatus());
        } else if (response.getStatus() >= 500) {
            return new RuntimeException("Server error: " + response.getStatus());
        }
        return null;
    }
}