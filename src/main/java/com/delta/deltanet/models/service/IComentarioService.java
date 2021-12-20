package com.delta.deltanet.models.service;

import java.util.List;

import com.delta.deltanet.models.entity.Comentario;

public interface IComentarioService {
	
	public List<Comentario> findAll();
	public Comentario findById(Long id);
	public Comentario save(Comentario comentario);
	public void delte(Long id);
	
}
