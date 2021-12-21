package com.delta.deltanet.models.dao;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.delta.deltanet.models.entity.Estado;

public interface IEstadoDao extends JpaRepository<Estado, Long> {

	@Query("from Estado where estadoRegistro = 'A'")
	public List<Estado> findAll();
}
