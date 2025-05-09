package com.example.tpgestionlivre.driving.web

import com.example.tpgestionlivre.domain.model.Book
import com.example.tpgestionlivre.domain.usecase.BookUseCase
import com.example.tpgestionlivre.infrastructure.driving.web.dto.BookDto
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.registerKotlinModule
import com.ninjasquad.springmockk.MockkBean
import io.kotest.core.spec.style.FunSpec
import io.kotest.extensions.spring.SpringExtension
import io.mockk.every
import io.mockk.justRun
import io.mockk.verify
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest
import org.springframework.http.MediaType.APPLICATION_JSON
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.get
import org.springframework.test.web.servlet.post
import org.springframework.test.web.servlet.put

@WebMvcTest
class BookControllerIT(
    @MockkBean private val bookUseCase: BookUseCase,
    @Autowired private val mockMvc: MockMvc
) : FunSpec({
    extension(SpringExtension)

    val objectMapper = ObjectMapper().registerKotlinModule()

    test("rest route get book by id") {
        // GIVEN
        val bookId = 1L
        val expectedBook = Book(id = bookId, title = "1984", author = "Georges Orwell", isReserved = false)
        every { bookUseCase.findById(bookId) } returns expectedBook

        // WHEN
        mockMvc.get("/books/$bookId")
            // THEN
            .andExpect {
                status { isOk() }
                content {
                    contentType(APPLICATION_JSON)
                    json(
                        """
                        {
                          "id": 1,
                          "name": "1984",
                          "author": "Georges Orwell",
                          "isReserved": false
                        }
                        """.trimIndent()
                    )
                }
            }

        verify(exactly = 1) { bookUseCase.findById(bookId) }
    }

    test("rest route post book with id") {
        val bookDto = BookDto(1L, "Les Misérables", "Victor Hugo", false)
        val json = objectMapper.writeValueAsString(bookDto)

        // GIVEN
        val bookWithId = Book(id = 1L, title = "Les Misérables", author = "Victor Hugo", isReserved = false)
        justRun { bookUseCase.addBook(bookWithId) }

        // WHEN
        mockMvc.post("/books") {
            content = json
            contentType = APPLICATION_JSON
            accept = APPLICATION_JSON
        }.andExpect {
            status { isCreated() }
        }

        verify(exactly = 1) { bookUseCase.addBook(bookWithId) }
    }

    test("rest route put book with id") {
        val bookId = 1L
        val updatedBookDto = BookDto(0L, "Les Misérables", "Victor Hugo", false)
        val updatedBook = Book(id = bookId, title = "Les Misérables", author = "Victor Hugo", isReserved = false)
        val json = objectMapper.writeValueAsString(updatedBookDto)

        // GIVEN
        justRun { bookUseCase.saveBook(updatedBook) }

        // WHEN
        mockMvc.put("/books/$bookId") {
            content = json
            contentType = APPLICATION_JSON
        }.andExpect {
            status { isOk() }
        }

        verify(exactly = 1) { bookUseCase.saveBook(updatedBook) }
    }

    test("rest route get book by id should return 404 if not found") {
        val bookId = 999L
        every { bookUseCase.findById(bookId) } returns null

        mockMvc.get("/books/$bookId")
            .andExpect {
                status { isNotFound() }
            }

        verify(exactly = 1) { bookUseCase.findById(bookId) }
    }

})