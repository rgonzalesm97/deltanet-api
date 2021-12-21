package com.delta.deltanet.models.dao;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.delta.deltanet.models.entity.Prioridad;

public interface IPrioridadDao extends JpaRepository<Prioridad, Long> {

	@Query("from Prioridad where estadoRegistro = 'A'")
	public List<Prioridad> findAll();
}
