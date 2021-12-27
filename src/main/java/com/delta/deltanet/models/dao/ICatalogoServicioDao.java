package com.delta.deltanet.models.dao;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.delta.deltanet.models.entity.Area;
import com.delta.deltanet.models.entity.CatalogoServicio;

public interface ICatalogoServicioDao extends JpaRepository<CatalogoServicio, Long> {

	@Query("from CatalogoServicio where estadoRegistro = 'A'")
	public List<CatalogoServicio> findAll();
	
	public List<CatalogoServicio> findByArea(Area area);

	public List<CatalogoServicio> findByAreaAndEstadoRegistro(Area area, String Estado);

	public CatalogoServicio findByIdAndEstadoRegistro(Long id, char estado);
	
}
