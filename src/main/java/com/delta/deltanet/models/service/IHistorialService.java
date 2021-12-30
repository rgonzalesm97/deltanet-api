package com.delta.deltanet.models.service;

import java.util.List;

import com.delta.deltanet.models.entity.Historial;

public interface IHistorialService {
	
	public List<Historial> findAll();
	public Historial findById(Long id);
	public List<Historial> findAllByTabla(String tabla);
	public List<Historial> findAllByItem(String tabla, Long idTabla);
	public Historial save(Historial historial);
	public void delete(Long id);
	public List<Historial> findHistorialTicket(Long idTabla);
	
}
