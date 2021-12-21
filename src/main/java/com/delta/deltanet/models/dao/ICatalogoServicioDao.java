package com.delta.deltanet.models.dao;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.delta.deltanet.models.entity.CatalogoServicio;

public interface ICatalogoServicioDao extends JpaRepository<CatalogoServicio, Long> {

	@Query("from CatalogoServicio where estadoRegistro = 'A'")
	public List<CatalogoServicio> findAll();
}
