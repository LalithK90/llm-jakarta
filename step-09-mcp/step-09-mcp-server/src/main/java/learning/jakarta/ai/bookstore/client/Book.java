package learning.jakarta.ai.bookstore.client;

import java.math.BigDecimal;

public record Book(
    Long id,
    String isbn,
    String title,
    String author,
    String description,
    BigDecimal price,
    int stockQuantity,
    String category,
    String imageUrl
){}
