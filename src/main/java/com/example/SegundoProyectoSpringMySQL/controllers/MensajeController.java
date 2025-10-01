package com.example.SegundoProyectoSpringMySQL.controllers;

import com.example.SegundoProyectoSpringMySQL.entities.Categoria;
import com.example.SegundoProyectoSpringMySQL.entities.Mensaje;
import com.example.SegundoProyectoSpringMySQL.repositories.CategoriaRepository;
import com.example.SegundoProyectoSpringMySQL.repositories.MensajeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequiredArgsConstructor
public class MensajeController {

    //@Autowired no es necesario indicar aquí esta anotación poque los hemos puesto como parámetros del constructor
    MensajeRepository mensajeRepository;
    CategoriaRepository categoriaRepository;

    //Inyectamos los repositorios en el constructor, así Spring los crea y gestiona automáticamente).
    public MensajeController(CategoriaRepository categoriaRepository, MensajeRepository mensajeRepository){

        //Para poder acceder desde otros métodos lo asigno a la propiedad de la clase
        this.mensajeRepository = mensajeRepository;
        this.categoriaRepository = categoriaRepository;

        Categoria categoria = Categoria.builder()
                .nombreCategoria("Móviles")
                .mensajes(List.of(
                        Mensaje.builder()
                                .titulo("Primer post sobre móviles")
                                .texto("Este es el texto del primer post")
                                .fechaCreacion(LocalDateTime.now())
                                .build(),
                        Mensaje.builder()
                                .titulo("Segundo post sobre móviles")
                                .texto("Este es el texto del segundo post")
                                .fechaCreacion(LocalDateTime.now())
                                .build()
                ))
                .build();

        categoria.getMensajes().forEach(
                mensaje -> mensaje.setCategoria(categoria)
        );

        categoriaRepository.save(categoria);
    }


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
    public Mensaje insertMensajes(@RequestBody Mensaje mensaje){
        return new Mensaje();
    }
}
