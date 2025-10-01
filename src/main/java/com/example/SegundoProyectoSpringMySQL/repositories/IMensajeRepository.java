package com.example.SegundoProyectoSpringMySQL.repositories;

import com.example.SegundoProyectoSpringMySQL.entities.Mensaje;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface IMensajeRepository extends JpaRepository<Mensaje, Long> {
    Mensaje findByTitulo(String titulo);
}
