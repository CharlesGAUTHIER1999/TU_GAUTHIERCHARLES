package com.example.tpgestionlivre.domain

import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.FunSpec
import com.example.tpgestionlivre.domain.model.Book

class BookTest : FunSpec({

    test("should throw when name is blank") {
        shouldThrow<IllegalArgumentException> {
            Book(id = 1L, title = "", author = "Jules Verne")
        }
    }

    test("should throw when author is blank") {
        shouldThrow<IllegalArgumentException> {
            Book(id = 1L, title = "Voyage au centre de la terre", author = "")
        }
    }

    test("should not throw when both name and author are valid") {
        Book(id = 1L, title = "Voyage au centre de la Terre", author = "Jules Verne")
    }
})