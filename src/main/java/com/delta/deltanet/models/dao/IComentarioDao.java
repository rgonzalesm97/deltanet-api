package com.delta.deltanet.models.dao;

import org.springframework.data.jpa.repository.JpaRepository;

import com.delta.deltanet.models.entity.Comentario;

public interface IComentarioDao extends JpaRepository<Comentario, Long> {

}
