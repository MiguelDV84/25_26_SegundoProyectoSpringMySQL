package com.example.SegundoProyectoSpringMySQL.entities;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;

import java.util.List;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table (name = "categorias")
@Entity
public class Categoria {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String nombreCategoria;

    @JsonIgnore
    @OneToMany(mappedBy = "categoria", cascade = {CascadeType.PERSIST,CascadeType.REMOVE})
    private List<Mensaje> mensajes;
}
