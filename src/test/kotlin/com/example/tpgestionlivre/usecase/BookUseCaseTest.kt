package com.example.tpgestionlivre.usecase

import com.example.tpgestionlivre.domain.model.Book
import com.example.tpgestionlivre.domain.port.BookPort
import com.example.tpgestionlivre.domain.usecase.BookUseCase
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.assertThrows
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.kotlin.*
import org.mockito.junit.jupiter.MockitoExtension

@ExtendWith(MockitoExtension::class)
class BookUseCaseTest {

    private val repository: BookPort = mock()
    private val manager = BookUseCase(repository)

    @Test
    fun `should add book using repository`() {
        val book = Book(1L,"ZOLA", "Emile")
        manager.addBook(book)
        verify(repository).createBook(book)
    }

    @Test
    fun `should return books sorted by title`() {
        val book1 = Book(1L,"Zola", "Emile")
        val book2 = Book(1L,"Animal Farm", "George Orwell")

        whenever(repository.getAllBooks()).thenReturn(listOf(book1, book2))

        val books = manager.getAllBooks()

        assertEquals(2, books.size)
        assertEquals("Animal Farm", books[0].title)
        assertEquals("Zola", books[1].title)
    }

    @Test
    fun `should throw exception when creating a book with blank title`() {
        val exception = assertThrows<IllegalArgumentException> {
            Book(1L,"   ", "Orwell")
        }
        assertEquals("Name must not be blank", exception.message)
    }

    @Test
    fun `should throw exception when creating a book with blank author`() {
        val exception = assertThrows<IllegalArgumentException> {
            Book(1L,"1984", " ")
        }
        assertEquals("Author must not be blank", exception.message)
    }

        @Test
    fun `should sort books by title even if input list is unordered`() {
        val unorderedBooks = listOf(
            Book(1L,"Z", "Author Z"),
            Book(2L,"A", "Author A"),
            Book(3L,"M", "Author M")
        )

        whenever(repository.getAllBooks()).thenReturn(unorderedBooks)

        val result = manager.getAllBooks()

        assertEquals(listOf("A", "M", "Z"), result.map { it.title })
    }

    @Test
    fun `should call repository to retrieve all books`() {
        whenever(repository.getAllBooks()).thenReturn(emptyList())

        manager.getAllBooks()

        verify(repository).getAllBooks()
    }

    @Test
    fun `list of books is sorted in ascending order by title`() {
        val books = listOf(
            Book(1L,"Zebra", "A"),
            Book(2L,"Alpha", "B"),
            Book(3L,"Monkey", "C")
        )

        whenever(repository.getAllBooks()).thenReturn(books)

        val result = manager.getAllBooks()

        val sortedTitles = result.map { it.title }
        val expected = sortedTitles.sorted()

        assertEquals(expected, sortedTitles)
    }

    @Test
    fun `all returned books have non-blank title and author`() {
        val books = listOf(
            Book(1L,"1984", "Orwell"),
            Book(2L,"Brave New World", "Huxley")
        )

        whenever(repository.getAllBooks()).thenReturn(books)

        val result = manager.getAllBooks()

        assertTrue(result.all { it.title.isNotBlank() && it.author.isNotBlank() })
    }

    @Test
    fun `should handle duplicate titles correctly`() {
        val books = listOf(
            Book(1L,"Alpha", "Author1"),
            Book(2L,"Alpha", "Author2")
        )
        whenever(repository.getAllBooks()).thenReturn(books)

        val result = manager.getAllBooks()

        assertEquals(2, result.size)
        assertEquals("Alpha", result[0].title)
        assertEquals("Alpha", result[1].title)
    }

    @Test
    fun `should reserve a book when book is found`() {
        val book = Book(1L,"1984", "George Orwell")
        whenever(repository.findById(1L)).thenReturn(book)
        whenever(repository.saveBook(book)).thenReturn(book)

        val reservedBook = manager.reserveBook(1L)

        assertTrue(reservedBook.isReserved)
        verify(repository).saveBook(book)
    }

    @Test
    fun `should throw exception when book to reserve is not found`() {
        val bookId = 1L
        whenever(repository.findById(bookId)).thenReturn(null)

        val exception = assertThrows<IllegalArgumentException> {
            manager.reserveBook(bookId)
        }
        assertEquals("Book not found", exception.message)
    }

}