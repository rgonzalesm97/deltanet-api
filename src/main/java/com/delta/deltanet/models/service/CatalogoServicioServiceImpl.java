package com.delta.deltanet.models.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.delta.deltanet.models.dao.ICatalogoServicioDao;
import com.delta.deltanet.models.entity.CatalogoServicio;

@Service
public class CatalogoServicioServiceImpl implements ICatalogoServicioService {
	
	@Autowired
	private ICatalogoServicioDao catalogoServicioDao;

	@Override
	public List<CatalogoServicio> findAll() {
		return catalogoServicioDao.findAll();
	}

	@Override
	public CatalogoServicio findById(Long id) {
		return catalogoServicioDao.findById(id).get();
	}

	@Override
	public CatalogoServicio save(CatalogoServicio CatalogoServicio) {
		return catalogoServicioDao.save(CatalogoServicio);
	}

	@Override
	public void delte(Long id) {
		catalogoServicioDao.deleteById(id);
	}
	
}
