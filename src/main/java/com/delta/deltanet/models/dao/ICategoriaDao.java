package com.delta.deltanet.models.dao;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.delta.deltanet.models.entity.Categoria;

public interface ICategoriaDao extends JpaRepository<Categoria, Long> {

	@Query("from Categoria where estadoRegistro = 'A'")
	public List<Categoria> findAll();
}
