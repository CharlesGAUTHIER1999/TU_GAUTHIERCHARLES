package com.example.tpgestionlivre.usecase

import com.example.tpgestionlivre.domain.model.Book
import com.example.tpgestionlivre.domain.port.BookPort
import com.example.tpgestionlivre.domain.usecase.BookUseCase
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.collections.shouldContainExactly
import io.kotest.property.Arb
import io.kotest.property.arbitrary.int
import io.kotest.property.arbitrary.next
import io.kotest.property.arbitrary.string
import io.kotest.property.checkAll

class InMemoryBookPort : BookPort {
    private val books = mutableListOf<Book>()

    override fun getAllBooks(): List<Book> = books

    override fun createBook(book: Book) {
        books.add(book)
    }

    override fun findById(id: Long): Book? {
        return books.find { it.id == id }
    }

    override fun saveBook(book: Book): Book {
        if (!books.contains(book)) {
            books.add(book)
        }
        return book
    }

    fun clear() {
        books.clear()
    }
}

class BookUseCasePropertyTest : FunSpec({

    val bookPort = InMemoryBookPort()
    val bookUseCase = BookUseCase(bookPort)

    test("should return all elements in the alphabetical order") {
        checkAll(Arb.int(1..100)) { nbItems ->
            bookPort.clear()

            val titles = mutableListOf<String>()

            for (i in 1..nbItems) {
                val title = Arb.string(1..10).next()
                titles.add(title)
                bookUseCase.addBook(Book(id = i.toLong(), title = title, author = "Jules Verne"))
            }

            val res = bookUseCase.getAllBooks()

            res.map { it.title } shouldContainExactly titles.sorted()
        }
    }
})