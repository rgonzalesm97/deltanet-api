package com.delta.deltanet.models.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.delta.deltanet.models.dao.ICatalogoServicioDao;
import com.delta.deltanet.models.entity.Area;
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
		return catalogoServicioDao.findById(id).orElse(null);
	}
	
	@Override
	public CatalogoServicio save(CatalogoServicio CatalogoServicio) {
		return catalogoServicioDao.save(CatalogoServicio);
	}

	@Override
	public void delete(Long id) {
		catalogoServicioDao.deleteById(id);
	}

	@Override
	public List<CatalogoServicio> findByArea(Area area) {
		return catalogoServicioDao.findByArea(area);
	}

	@Override
	public CatalogoServicio findByIdAndEstado(Long id, String estado) {
		return catalogoServicioDao.findByIdAndEstadoRegistro(id, estado);
	}

	@Override
	public List<CatalogoServicio> findByAreaAndEstado(Area area, String estado) {
		return catalogoServicioDao.findByAreaAndEstadoRegstro(area, estado);
	}

	

	
	
}
