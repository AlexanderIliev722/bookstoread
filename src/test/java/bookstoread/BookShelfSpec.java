package bookstoread;

import org.junit.jupiter.api.*;
import org.junit.platform.runner.JUnitPlatform;
import org.junit.runner.RunWith;

import java.time.Duration;
import java.time.LocalDate;
import java.time.Month;
import java.time.Year;
import java.time.temporal.ChronoUnit;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.Map;

import static java.util.Arrays.asList;
import static java.util.Collections.singletonList;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

@RunWith(JUnitPlatform.class)
@DisplayName("A bookshelf")
public class BookShelfSpec {

    private static final String AUTHOR_JOSHUA_BLOCH = "Joshua Bloch";
    private static final String AUTHOR_STEVE_MCCONNEL = "Steve McConnel";
    private static final String AUTHOR_STEVE_MCCONNELL_ALT = "Steve McConnell"; // Handling the typo in Zadacha 14 c
    private static final String AUTHOR_FREDERICK_BROOKS = "Frederick Phillips Brooks";
    private static final String AUTHOR_ROBERT_MARTIN = "Robert C. Martin";
    private static final String AUTHOR_GEORGE_MARTIN = "George R.R. Martin";
    private static final String AUTHOR_ALICE = "Alice The Fan";

    private BookShelf shelf;
    private Book effectiveJava;
    private Book codeComplete;
    private Book mythicalManMonth;
    private Book cleanCode;

    @BeforeEach
    void init() {
        shelf = new BookShelf();
        // Using constants here
        effectiveJava = new Book("Effective Java", AUTHOR_JOSHUA_BLOCH, LocalDate.of(2008, Month.MAY, 8));
        codeComplete = new Book("Code Complete", AUTHOR_STEVE_MCCONNEL, LocalDate.of(2004, Month.JUNE, 9));
        mythicalManMonth = new Book("The Mythical Man-Month", AUTHOR_FREDERICK_BROOKS, LocalDate.of(1975, Month.JANUARY, 1));
        cleanCode = new Book("Clean Code", AUTHOR_ROBERT_MARTIN, LocalDate.of(2008, Month.AUGUST, 1));
    }

    @Nested
    @DisplayName("is empty")
    class IsEmpty {

        @Test
        @DisplayName("when no book is added to it")
        public void emptyBookShelfWhenNoBookAdded() {
            List<Book> books = shelf.books();
            assertTrue(books.isEmpty(), () -> "BookShelf should be empty");
        }

        @Test
        @DisplayName("when add is called without books")
        void emptyBookShelfWhenAddIsCalledWithoutBooks() {
            shelf.add();
            List<Book> books = shelf.books();
            assertTrue(books.isEmpty(), () -> "BookShelf should be empty.");
        }

        //Zadacha 14 a)
        @Test
        @DisplayName("when arrange is called on empty shelf")
        void emptyBookShelfWhenArrangedIsCalled() {
            List<Book> books = shelf.arrange();
            assertTrue(books.isEmpty(), () -> "Arranged list should be empty when shelf is empty.");
        }
    }

    @Nested
    @DisplayName("after adding books")
    class BooksAreAdded {

        @Test
        @DisplayName("contains two books")
        void bookshelfContainsTwoBooksWhenTwoBooksAdded() {
            shelf.add(effectiveJava, codeComplete);
            List<Book> books = shelf.books();
            assertEquals(2, books.size(), () -> "BookShelf should have two books");
        }

        @Test
        @DisplayName("returns an immutable books collection to client")
        void bookshelfIsImmutableForClient() {
            shelf.add(effectiveJava, codeComplete);
            List<Book> books = shelf.books();
            try {
                books.add(mythicalManMonth);
                fail(() -> "Should not be able to add book to books");
            } catch (Exception e) {
                assertTrue(e instanceof UnsupportedOperationException, () -> "BookShelf should throw UnsupportedOperationException");
            }
        }
    }

    @Test
    void throwsExceptionWhenBooksAreAddedAfterCapacityIsReached() {
        BookShelf bookShelf = new BookShelf(2);
        bookShelf.add(effectiveJava, codeComplete);
        BookShelfCapacityReached throwException = assertThrows(BookShelfCapacityReached.class, () -> bookShelf.add(mythicalManMonth));
        assertEquals("BookShelf capacity of 2 is reached. You can't add more books.", throwException.getMessage());
    }

    @Test
    void test_should_complete_in_one_second() {
        assertTimeoutPreemptively(Duration.of(1, ChronoUnit.SECONDS), () -> Thread.sleep(500));
    }

    @RepeatedTest(value = 10, name = "i_am_a_repeated_test__{currentRepetition}/{totalRepetitions}")
    void i_am_a_repeated_test() {
        assertTrue(true);
    }

    @Nested
    @DisplayName("is arranged")
    class WhenArranged {

        @Test
        @DisplayName("lexicographically by book title")
        void bookshelfArrangedByBookTitle() {
            shelf.add(effectiveJava, codeComplete, mythicalManMonth);
            List<Book> books = shelf.arrange();
            assertEquals(asList(codeComplete, effectiveJava, mythicalManMonth), books, () -> "Books in a bookshelf should be arranged lexicographically by book title");
        }

        @Test
        @DisplayName("by user provided criteria (by book title lexicographically descending)")
        void bookshelfArrangedByUserProvidedCriteria() {
            shelf.add(effectiveJava, codeComplete, mythicalManMonth);
            List<Book> books = shelf.arrange(Comparator.<Book>naturalOrder().reversed());
            assertEquals(
                    asList(mythicalManMonth, effectiveJava, codeComplete),
                    books,
                    () -> "Books in a bookshelf are arranged in descending order of book title");
        }

        @Test
        @DisplayName("by book publication date in ascending order")
        void bookshelfArrangedByAnotherUserProvidedCriteria() {
            shelf.add(effectiveJava, codeComplete, mythicalManMonth);
            List<Book> books = shelf.arrange((b1, b2) -> b1.getPublishedOn().compareTo(b2.getPublishedOn()));
            assertEquals(
                    asList(mythicalManMonth, codeComplete, effectiveJava),
                    books,
                    () -> "Books in a bookshelf are arranged by book publication date in ascending order");
        }

        //Zadacha 14 a)
        @Test
        @DisplayName("by publication date (newest first)")
        void bookshelfArrangedByPublicationDateReversed() {
            shelf.add(effectiveJava, codeComplete, mythicalManMonth);
            List<Book> books = shelf.arrange(Comparator.comparing(Book::getPublishedOn).reversed());
            assertEquals(
                    asList(effectiveJava, codeComplete, mythicalManMonth),
                    books,
                    () -> "Books should be arranged by date: Newest to Oldest(descending order)");
        }

        //Zadacha 14 c)
        @Test
        @DisplayName("books are arranged by author and then by title")
        void booksAreArrangedByAuthorThenTitle() {
            BookShelf shelf = new BookShelf();
            // Using constants here
            Book effectiveJava = new Book("Effective Java", AUTHOR_JOSHUA_BLOCH, LocalDate.of(2008, 5, 8));
            Book codeComplete = new Book("Code Complete", AUTHOR_STEVE_MCCONNELL_ALT, LocalDate.of(2004, 6, 9));
            Book javaConcurrency = new Book("Java Concurrency in Practice", AUTHOR_JOSHUA_BLOCH, LocalDate.of(2006, 5, 19));

            shelf.add(codeComplete, effectiveJava, javaConcurrency);
            List<Book> arrangedBooks = shelf.arrangeByAuthorThenTitle();

            assertEquals(Arrays.asList(effectiveJava, javaConcurrency, codeComplete), arrangedBooks, "Books should be sorted by Author then Title");
        }

        //Zadacha 14 e) TDD
        @Test
        @DisplayName("books are arranged by title and then by author")
        void booksAreArrangedByTitleThenAuthor() {
            // Using constants here
            Book windsOfWinter = new Book("The Winds of Winter", AUTHOR_GEORGE_MARTIN, LocalDate.of(2025, Month.DECEMBER, 1));
            Book windsOfWinterFanFic = new Book("The Winds of Winter", AUTHOR_ALICE, LocalDate.of(2024, Month.JANUARY, 1));
            Book gameOfThrones = new Book("A Game of Thrones", AUTHOR_GEORGE_MARTIN, LocalDate.of(1996, Month.AUGUST, 1));

            shelf.add(windsOfWinter, windsOfWinterFanFic, gameOfThrones);
            List<Book> arranged = shelf.arrangeByTitleThenAuthor();

            assertEquals(Arrays.asList(gameOfThrones, windsOfWinterFanFic, windsOfWinter),
                    arranged, "Should sort by Title first then by Author t");
        }
    }

    @Nested
    @DisplayName("books are grouped by")
    class GroupBy {

        @Test
        @DisplayName("publication year")
        void groupBooksInBookShelfByPublicationYear() {
            shelf.add(effectiveJava, codeComplete, mythicalManMonth, cleanCode);

            Map<Year, List<Book>> booksByPublicationYear = shelf.groupByPublicationYear();
            assertThat(booksByPublicationYear)
                    .containsKey(Year.of(2008))
                    .containsValues(Arrays.asList(effectiveJava, cleanCode));

            assertThat(booksByPublicationYear)
                    .containsKey(Year.of(2004))
                    .containsValues(singletonList(codeComplete));

            assertThat(booksByPublicationYear)
                    .containsKey(Year.of(1975))
                    .containsValues(singletonList(mythicalManMonth));
        }

        @Test
        @DisplayName("user provided criteria(group by author name)")
        void groupBooksInBookShelfByUserProvidedCriteria() {
            shelf.add(effectiveJava, codeComplete, mythicalManMonth, cleanCode);
            Map<String, List<Book>> booksByAuthor = shelf.groupBy(Book::getAuthor);

            assertThat(booksByAuthor)
                    .containsKey(AUTHOR_JOSHUA_BLOCH)
                    .containsValues(singletonList(effectiveJava));

            assertThat(booksByAuthor)
                    .containsKey(AUTHOR_STEVE_MCCONNEL)
                    .containsValues(singletonList(codeComplete));

            assertThat(booksByAuthor)
                    .containsKey(AUTHOR_FREDERICK_BROOKS)
                    .containsValues(singletonList(mythicalManMonth));

            assertThat(booksByAuthor)
                    .containsKey(AUTHOR_ROBERT_MARTIN)
                    .containsValues(singletonList(cleanCode));
        }

        //Zadacha 14 d)
        @Test
        @DisplayName("author name")
        void groupBooksInBookShelfByAuthor() {
            shelf.add(effectiveJava, codeComplete, mythicalManMonth, cleanCode);
            Map<String, List<Book>> booksByAuthor = shelf.groupByAuthor();

            assertThat(booksByAuthor).containsKey(AUTHOR_JOSHUA_BLOCH)
                    .containsValues(singletonList(effectiveJava));

            assertThat(booksByAuthor).containsKey(AUTHOR_STEVE_MCCONNEL)
                    .containsValues(singletonList(codeComplete));

            assertThat(booksByAuthor).containsKey(AUTHOR_ROBERT_MARTIN)
                    .containsValues(singletonList(cleanCode));
        }
        @Test
        @DisplayName("author name with multiple books by the same author")
        void groupBooksInBookShelfByAuthor_MultipleBooks() {
            Book javaConcurrency = new Book("Java Concurrency in Practice", AUTHOR_JOSHUA_BLOCH, LocalDate.of(2006, Month.MAY, 19));
            shelf.add(effectiveJava, javaConcurrency, codeComplete);

            Map<String, List<Book>> booksByAuthor = shelf.groupByAuthor();

            assertThat(booksByAuthor)
                    .containsKey(AUTHOR_JOSHUA_BLOCH)
                    .containsEntry(AUTHOR_JOSHUA_BLOCH, Arrays.asList(effectiveJava, javaConcurrency));

            assertThat(booksByAuthor)
                    .containsKey(AUTHOR_STEVE_MCCONNEL)
                    .containsEntry(AUTHOR_STEVE_MCCONNEL, singletonList(codeComplete));
        }
    }
}