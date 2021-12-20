package com.delta.deltanet.models.service;

import java.util.List;

import com.delta.deltanet.models.entity.UsuarioServicio;

public interface IUsuarioServicioService {
	
	public List<UsuarioServicio> findAll();
	public UsuarioServicio findById(Long id);
	public UsuarioServicio save(UsuarioServicio usuarioServicio);
	public void delte(Long id);
	
}
