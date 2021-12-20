package com.delta.deltanet.models.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.delta.deltanet.models.dao.IUsuarioServicioDao;
import com.delta.deltanet.models.entity.UsuarioServicio;

@Service
public class UsuarioServicioServiceImpl implements IUsuarioServicioService {
	
	@Autowired
	private IUsuarioServicioDao usuarioServicioDao;

	@Override
	public List<UsuarioServicio> findAll() {
		return usuarioServicioDao.findAll();
	}

	@Override
	public UsuarioServicio findById(Long id) {
		return usuarioServicioDao.findById(id).get();
	}

	@Override
	public UsuarioServicio save(UsuarioServicio UsuarioServicio) {
		return usuarioServicioDao.save(UsuarioServicio);
	}

	@Override
	public void delte(Long id) {
		usuarioServicioDao.deleteById(id);
	}
	
}
