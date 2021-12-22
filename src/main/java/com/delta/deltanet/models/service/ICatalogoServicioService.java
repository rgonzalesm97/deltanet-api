package com.delta.deltanet.models.service;

import java.util.List;

import com.delta.deltanet.models.entity.CatalogoServicio;

public interface ICatalogoServicioService {
	
	public List<CatalogoServicio> findAll();
	public CatalogoServicio findById(Long id);
	public CatalogoServicio save(CatalogoServicio catalogoServicio);
	public void delete(Long id);
	
}
