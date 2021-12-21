package com.delta.deltanet.models.dao;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.delta.deltanet.models.entity.TipoAccion;

public interface ITipoAccionDao extends JpaRepository<TipoAccion, Long> {

	@Query("from TipoAccion where estadoRegistro = 'A'")
	public List<TipoAccion> findAll();
}
