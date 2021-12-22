package com.delta.deltanet.models.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.delta.deltanet.models.dao.IEstadoDao;
import com.delta.deltanet.models.entity.Estado;

@Service
public class EstadoServiceImpl implements IEstadoService {
	
	@Autowired
	private IEstadoDao estadoDao;

	@Override
	public List<Estado> findAll() {
		return estadoDao.findAll();
	}

	@Override
	public Estado findById(Long id) {
		return estadoDao.findById(id).orElse(null);
	}

	@Override
	public Estado save(Estado Estado) {
		return estadoDao.save(Estado);
	}

	@Override
	public void delete(Long id) {
		estadoDao.deleteById(id);
	}
	
}
