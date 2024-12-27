package org.example.library.controller;

import org.example.library.dto.CourzinaDto;
import org.example.library.model.Courzina;
import org.example.library.service.LibraryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@RestController
@RequestMapping("/api/library")
public class LibraryController {

    private final LibraryService libraryService;

    @Autowired
    public LibraryController(LibraryService libraryService) {
        this.libraryService = libraryService;
    }

    // Эндпоинт для создания записи о книге
    @PostMapping("/borrow")
    public ResponseEntity<Courzina> borrowBook(@RequestBody CourzinaDto courzinaDto) {
        try {
            Courzina courzina = libraryService.create(courzinaDto);
            return ResponseEntity.ok(courzina);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(null);
        }
    }

    // Эндпоинт для получения информации о книге по ID
    @GetMapping("/{id}")
    public ResponseEntity<Courzina> getBookById(@PathVariable Long id) {
        Optional<Courzina> courzina = libraryService.get(id);
        return courzina.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());
    }
}
