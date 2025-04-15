package learning.jakarta.ai.bookstore;

import jakarta.ws.rs.core.Application;
import jakarta.ws.rs.ApplicationPath;

@ApplicationPath("rest")
public class BookApplication extends Application {
    // Needed to enable Jakarta REST and specify path.
}
