package com.delta.deltanet.models.service;

import java.util.List;

import com.delta.deltanet.models.entity.Area;
import com.delta.deltanet.models.entity.CatalogoServicio;

public interface ICatalogoServicioService {
	
	public List<CatalogoServicio> findAll();
	public List<CatalogoServicio> findByArea(Area area);
	public List<CatalogoServicio> findByAreaAndEstado(Area area, String estado);
	public CatalogoServicio findById(Long id);
	public CatalogoServicio findByIdAndEstado(Long id, String estado);
	public CatalogoServicio save(CatalogoServicio catalogoServicio);
	public void delete(Long id);
	
}
