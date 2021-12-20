package com.delta.deltanet.models.dao;

import org.springframework.data.jpa.repository.JpaRepository;

import com.delta.deltanet.models.entity.Categoria;

public interface ICategoriaDao extends JpaRepository<Categoria, Long> {

}
