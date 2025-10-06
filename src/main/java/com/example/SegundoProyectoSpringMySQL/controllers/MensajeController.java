package com.example.SegundoProyectoSpringMySQL.controllers;

import com.example.SegundoProyectoSpringMySQL.entities.Mensaje;
import com.example.SegundoProyectoSpringMySQL.repositories.CategoriaRepository;
import com.example.SegundoProyectoSpringMySQL.repositories.MensajeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.List;

@RestController
@RequiredArgsConstructor
public class MensajeController  {

    @Autowired
    MensajeRepository mensajeRepository;
    @Autowired
    CategoriaRepository categoriaRepository;


    @GetMapping("/mensajes")
    public List<Mensaje> findAllMensajes() {
        return mensajeRepository.findAll();
    }

    @GetMapping("/mensajes/{id}")
    public ResponseEntity<Mensaje> findMensajes(@PathVariable Long id){
        return mensajeRepository.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/mensajes/{id}")
    public ResponseEntity<Void> removeMensajes(@PathVariable Long id) {
        if (!mensajeRepository.existsById(id)) {
            return ResponseEntity.notFound().build();
        }
        mensajeRepository.deleteById(id);
        return ResponseEntity.noContent().build();
    }

    @PutMapping("/mensajes/{id}")
    public ResponseEntity<Mensaje>  editMensajes(@RequestBody Mensaje mensaje, @PathVariable Long id){
        return mensajeRepository.findById(id)
                .map(m -> {
                    m.setCategoria(mensaje.getCategoria());
                    m.setId(mensaje.getId());
                    m.setTitulo(mensaje.getTitulo());
                    m.setTexto(mensaje.getTexto());
                    m.setFechaCreacion(mensaje.getFechaCreacion());
                    Mensaje mensajeActualizado = mensajeRepository.save(m);
                    return ResponseEntity.ok(mensajeActualizado);
                })
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping("/mensajes")
    public ResponseEntity<Mensaje> insertMensajes(@RequestBody Mensaje mensaje) {
        Mensaje saved = mensajeRepository.save(mensaje);

        URI uriMensaje = ServletUriComponentsBuilder
                .fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(saved.getId())
                .toUri();

        return ResponseEntity.created(uriMensaje).body(saved);
    }

}
