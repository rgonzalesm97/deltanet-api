package com.delta.deltanet.models.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.delta.deltanet.models.dao.ITipoAccionDao;
import com.delta.deltanet.models.entity.TipoAccion;

@Service
public class TipoAccionServiceImpl implements ITipoAccionService {
	
	@Autowired
	private ITipoAccionDao tipoAccionDao;

	@Override
	public List<TipoAccion> findAll() {
		return tipoAccionDao.findAll();
	}

	@Override
	public TipoAccion findById(Long id) {
		return tipoAccionDao.findById(id).orElse(null);
	}

	@Override
	public TipoAccion save(TipoAccion TipoAccion) {
		return tipoAccionDao.save(TipoAccion);
	}

	@Override
	public void delete(Long id) {
		tipoAccionDao.deleteById(id);
	}
	
}
