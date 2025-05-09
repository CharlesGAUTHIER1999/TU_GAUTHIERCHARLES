package com.example.tpgestionlivre.domain.usecase

import com.example.tpgestionlivre.domain.model.Book
import com.example.tpgestionlivre.domain.port.BookPort

class BookUseCase(private val bookPort: BookPort) {

    fun getAllBooks(): List<Book> {
        return bookPort.getAllBooks().sortedBy {
            it.title.lowercase()
        }
    }

    fun addBook(book: Book) {
        bookPort.createBook(book)
    }

    fun reserveBook(bookId: Long): Book {
        val book = bookPort.findById(bookId)
            ?: throw IllegalArgumentException("Book not found")
        book.reserve()
        return bookPort.saveBook(book)

    }

    fun saveBook(book: Book): Book {
        return bookPort.saveBook(book)
    }

    fun findById(bookId: Long): Book? {
        return bookPort.findById(bookId)
    }
}