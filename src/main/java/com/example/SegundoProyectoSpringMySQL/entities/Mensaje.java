package com.example.SegundoProyectoSpringMySQL.entities;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "mensajes")
public class Mensaje {

    @Id //Clave primaria
    @GeneratedValue(strategy = GenerationType.IDENTITY) //Autonumérico
    private Long id;

    private String titulo;

    private String texto;

    @Column(name = "fecha_creacion")
    private LocalDateTime fechaCreacion;

    @ManyToOne
    private Categoria categoria;
}
