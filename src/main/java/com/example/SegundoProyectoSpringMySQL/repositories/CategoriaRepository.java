package com.example.SegundoProyectoSpringMySQL.repositories;

import com.example.SegundoProyectoSpringMySQL.entities.Categoria;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CategoriaRepository extends JpaRepository<Categoria,Long> {
}
