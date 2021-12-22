package com.delta.deltanet.models.dao;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.delta.deltanet.models.entity.Archivo;

public interface IArchivoDao extends JpaRepository<Archivo, Long> {
	public List<Archivo> findByTablaAndTablaId(String tabla, Long tablaId);
}
