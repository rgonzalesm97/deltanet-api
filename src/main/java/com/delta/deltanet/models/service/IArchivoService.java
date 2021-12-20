package com.delta.deltanet.models.service;

import java.util.List;

import com.delta.deltanet.models.entity.Archivo;

public interface IArchivoService {
	
	public List<Archivo> findAll();
	public Archivo findById(Long id);
	public Archivo save(Archivo archivo);
	public void delte(Long id);
	
}
