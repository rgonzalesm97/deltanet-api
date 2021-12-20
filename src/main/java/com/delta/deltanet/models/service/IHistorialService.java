package com.delta.deltanet.models.service;

import java.util.List;

import com.delta.deltanet.models.entity.Historial;

public interface IHistorialService {
	
	public List<Historial> findAll();
	public Historial findById(Long id);
	public Historial save(Historial historial);
	public void delte(Long id);
	
}
