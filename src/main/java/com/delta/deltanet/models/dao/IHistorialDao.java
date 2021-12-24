package com.delta.deltanet.models.dao;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.delta.deltanet.models.entity.Historial;

public interface IHistorialDao extends JpaRepository<Historial, Long> {

	@Query("from Historial where tabla = ?1")
	public List<Historial> findByTabla(String tabla);
	
	@Query("from Historial where tabla = ?1 and tablaId = ?2")
	public List<Historial> findByItem(String tabla, Long idTabla);
}
