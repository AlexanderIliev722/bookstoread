//Zadacha 14 b)
package bookstoread;

public class BookAuthorFilter implements BookFilter {
    private final String author;

    private BookAuthorFilter(String author) {
        this.author = author;
    }

    public static BookAuthorFilter By(String author) {
        return new BookAuthorFilter(author);
    }

    @Override
    public boolean apply(Book b) {
        // Check if the book is null first!
        if (b == null) {
            return false;
        }
        return b.getAuthor().equals(author);
    }
}
