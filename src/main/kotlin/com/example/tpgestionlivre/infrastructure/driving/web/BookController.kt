package com.example.tpgestionlivre.infrastructure.driving.web

import com.example.tpgestionlivre.domain.usecase.BookUseCase
import com.example.tpgestionlivre.infrastructure.driving.web.dto.BookDto
import com.example.tpgestionlivre.infrastructure.driving.web.dto.toDto
import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.CrossOrigin
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.ResponseStatus
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/books")
class BookController (
    private val bookUseCase: BookUseCase
) {
    @CrossOrigin
    @GetMapping
    fun getAllBooks(): List<BookDto> {
        return bookUseCase.getAllBooks()
            .map { it.toDto()}
    }

    @CrossOrigin
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    fun addBook(@RequestBody bookDto: BookDto) {
        bookUseCase.addBook(bookDto.toDomain())
    }

    @CrossOrigin
    @PostMapping("/{id}/reserve")
    fun reserveBook(@PathVariable id: Long) {
        bookUseCase.reserveBook(id)
    }
}