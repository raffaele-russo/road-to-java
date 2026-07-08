import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;

/**
 * Iterator: expose a way to traverse a collection's elements sequentially
 * without exposing its underlying representation.
 * Java-specific angle: the enhanced for-loop is sugar over exactly this —
 * `for (Book b : bookshelf)` compiles to calling `iterator()` and then
 * `hasNext()`/`next()` in a loop. Implement Iterable yourself and you get
 * that syntax for free. Streams/Spliterator (module 05) are the modern
 * successor for bulk/aggregate operations over a sequence.
 * Note: this file's top-level class is named Iterator, so java.util.Iterator
 * is referenced fully-qualified below rather than imported (same simple name).
 * Run:  java 11-design-patterns/Iterator.java
 */
public class Iterator {

    record Book(String title) {
    }

    static class Bookshelf implements Iterable<Book> {
        private final List<Book> books = new ArrayList<>();

        void add(Book book) { books.add(book); }

        @Override
        public java.util.Iterator<Book> iterator() {
            return new java.util.Iterator<Book>() {
                private int index = 0;

                @Override
                public boolean hasNext() { return index < books.size(); }

                @Override
                public Book next() {
                    if (!hasNext()) throw new NoSuchElementException();
                    return books.get(index++);
                }
            };
        }
    }

    public static void main(String[] args) {
        System.out.println("== iterator ==");
        Bookshelf shelf = new Bookshelf();
        shelf.add(new Book("Effective Java"));
        shelf.add(new Book("Java Concurrency in Practice"));

        for (Book book : shelf) {          // enhanced for-loop, powered by our iterator() above
            System.out.println("  " + book.title());
        }

        java.util.Iterator<Book> it = shelf.iterator();
        System.out.println("  manual walk: hasNext=" + it.hasNext() + ", next=" + it.next().title());
    }
}
