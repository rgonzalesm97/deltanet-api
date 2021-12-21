package com.delta.deltanet.models.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.delta.deltanet.models.dao.IPrioridadDao;
import com.delta.deltanet.models.entity.Prioridad;

@Service
public class PrioridadServiceImpl implements IPrioridadService {
	
	@Autowired
	private IPrioridadDao prioridadDao;

	@Override
	public List<Prioridad> findAll() {
		return prioridadDao.findAll();
	}

	@Override
	public Prioridad findById(Long id) {
		return prioridadDao.findById(id).orElse(null);
	}

	@Override
	public Prioridad save(Prioridad Prioridad) {
		return prioridadDao.save(Prioridad);
	}

	@Override
	public void delete(Long id) {
		prioridadDao.deleteById(id);
	}
	
}
