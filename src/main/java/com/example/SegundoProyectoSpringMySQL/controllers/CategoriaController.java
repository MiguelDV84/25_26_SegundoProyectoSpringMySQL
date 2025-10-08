package com.example.SegundoProyectoSpringMySQL.controllers;

import com.example.SegundoProyectoSpringMySQL.entities.Categoria;
import com.example.SegundoProyectoSpringMySQL.repositories.CategoriaRepository;
import com.example.SegundoProyectoSpringMySQL.repositories.MensajeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class CategoriaController {

    @Autowired
    CategoriaRepository categoriaRepository;

    @GetMapping("/categorias")
    public ResponseEntity<List<Categoria>> getCategorias() {
        return ResponseEntity.ok(categoriaRepository.findAll());
    }

    @GetMapping("/categorias/{id}")
    public ResponseEntity<Categoria> getCategoria(@PathVariable Long id) {
        return categoriaRepository.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping("/categorias")
    public ResponseEntity<Categoria> createCategoria(@RequestBody Categoria categoria) {
        Categoria nuevaCategoria = categoriaRepository.save(categoria);
        return ResponseEntity.status(201).body(nuevaCategoria);
    }

    @PutMapping("/categorias/{id}")
    public ResponseEntity<Categoria> editCategoria(@RequestBody Categoria categoria, @PathVariable Long id) {
        return categoriaRepository.findById(id)
                .map(c -> {
                    c.setNombreCategoria(categoria.getNombreCategoria());
                    Categoria categoriaActualizada = categoriaRepository.save(c);
                    return ResponseEntity.ok(categoriaActualizada);
                })
                .orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/categorias/{id}")
    public ResponseEntity<Void> deleteCategoria(@PathVariable Long id) {
        if (!categoriaRepository.existsById(id)) {
            return ResponseEntity.notFound().build();
        }
        categoriaRepository.deleteById(id);
        return ResponseEntity.noContent().build();
    }
}
