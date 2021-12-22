package com.delta.deltanet.models.service;

import java.util.List;

import com.delta.deltanet.models.entity.Categoria;

public interface ICategoriaService {
	
	public List<Categoria> findAll();
	public Categoria findById(Long id);
	public Categoria save(Categoria categoria);
	public void delete(Long id);
	
}
