package learning.jakarta.ai.bookstore.repository;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.TypedQuery;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import jakarta.transaction.Transactional;
import learning.jakarta.ai.bookstore.domain.Book;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

@ApplicationScoped
public class BookRepository implements Serializable {

    @PersistenceContext
    private EntityManager entityManager;

    public List<Book> findAll() {
        return entityManager.createQuery("SELECT b FROM Book b ORDER BY b.id", Book.class)
                .getResultList();
    }

    public List<String> findAllCategories() {
        return entityManager.createQuery("select DISTINCT b.category from Book b", String.class)
                .getResultList();
    }

    public Optional<Book> findByIsbn(String isbn) {
        Book book = entityManager.createQuery("SELECT b FROM Book b WHERE b.isbn = :isbn", Book.class)
                .setParameter("isbn", isbn)
                .getSingleResult();
        return Optional.ofNullable(book);
    }

    public List<Book> findByCategory(String category) {
        return entityManager.createQuery(
                        "SELECT b FROM Book b WHERE LOWER(b.category) = LOWER(:category)",
                        Book.class)
                .setParameter("category", category)
                .getResultList();
    }

    public List<Book> findByTitleOrAuthor(String titleQuery, String authorQuery) {
        String title = (titleQuery != null) ? titleQuery.trim() : "";
        String author = (authorQuery != null) ? authorQuery.trim() : "";

        boolean hasTitle = !title.isEmpty();
        boolean hasAuthor = !author.isEmpty();

        if (!hasTitle && !hasAuthor) {
            return List.of(); // Or Collections.emptyList() if not on Java 9+
        }

        CriteriaBuilder cb = entityManager.getCriteriaBuilder();
        CriteriaQuery<Book> cq = cb.createQuery(Book.class);
        Root<Book> book = cq.from(Book.class);
        List<Predicate> predicates = new ArrayList<>();

        if (hasTitle) {
            predicates.add(cb.like(cb.lower(book.get("title")), "%" + title.toLowerCase() + "%"));
        }

        if (hasAuthor) {
            predicates.add(cb.like(cb.lower(book.get("author")), "%" + author.toLowerCase() + "%"));
        }

        // Combine predicates with OR if both title and author were provided
        // If only one was provided, predicates list will have only one element
        cq.where(cb.or(predicates.toArray(new Predicate[0])));

        // Add ordering for consistent results
        cq.orderBy(cb.asc(book.get("id")));

        TypedQuery<Book> query = entityManager.createQuery(cq);
        return query.getResultList();
    }

    public void save(Book book) {
        TypedQuery<Book> query = entityManager.createQuery(
                "SELECT b FROM Book b WHERE b.isbn = :isbn", Book.class);
        query.setParameter("isbn", book.getIsbn());
        List<Book> existingBooks = query.getResultList();

        if (existingBooks.isEmpty()) {
            entityManager.persist(book);
        } else {
            Book existingBook = existingBooks.get(0);
            existingBook.setTitle(book.getTitle());
            existingBook.setAuthor(book.getAuthor());
            existingBook.setCategory(book.getCategory());
            existingBook.setDescription(book.getDescription());
            existingBook.setPrice(book.getPrice());
            existingBook.setStockQuantity(book.getStockQuantity());
            existingBook.setImageUrl(book.getImageUrl());
            entityManager.merge(existingBook);
        }
    }

    @Transactional
    public void saveAll(List<Book> books) {
        for (Book book : books) {
            save(book);
        }
    }

    @Transactional
    public void updateStockQuantity(String isbn, int newStock) {
        Book book = findByIsbn(isbn).orElse(null);
        if (book != null) {
            book.setStockQuantity(newStock);
            entityManager.merge(book);
        }
    }

    public List<Book> findTop3ByCategory(String trimmedCategory) {
       return findTop3ByCategory(trimmedCategory, 3);
    }

    public List<Book> findTop3ByCategory(String categorySearchTerm, int limit) {
        if (categorySearchTerm == null || categorySearchTerm.trim().isEmpty() || limit <= 0) {
            return Collections.emptyList();
        }

        String trimmedSearchTerm = categorySearchTerm.trim();
        String likePattern = "%" + trimmedSearchTerm + "%";

        return entityManager.createQuery(
                        "SELECT b FROM Book b WHERE lower(b.category) LIKE lower(:pattern) ORDER BY b.title ASC", Book.class)
                .setParameter("pattern", likePattern)
                .setMaxResults(limit)
                .getResultList();
    }
}