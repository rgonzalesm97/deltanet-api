package com.delta.deltanet.models.service;

import java.util.List;

import com.delta.deltanet.models.entity.Prioridad;

public interface IPrioridadService {
	
	public List<Prioridad> findAll();
	public Prioridad findById(Long id);
	public Prioridad save(Prioridad prioridad);
	public void delete(Long id);
	
}
