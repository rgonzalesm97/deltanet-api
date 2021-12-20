package com.delta.deltanet.models.service;

import java.util.List;

import com.delta.deltanet.models.entity.Estado;

public interface IEstadoService {
	
	public List<Estado> findAll();
	public Estado findById(Long id);
	public Estado save(Estado estado);
	public void delte(Long id);
	
}
