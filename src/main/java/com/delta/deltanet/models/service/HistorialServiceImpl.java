package com.delta.deltanet.models.service;

import java.util.List;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.delta.deltanet.models.dao.IHistorialDao;
import com.delta.deltanet.models.entity.Historial;

@Service
public class HistorialServiceImpl implements IHistorialService {
	
	@Autowired
	private IHistorialDao historialDao;

	@Override
	public List<Historial> findAll() {
		return historialDao.findAll();
	}

	@Override
	public List<Historial> findAllByTabla(String tabla) {
		return historialDao.findByTabla(tabla);
	}
	
	@Override
	public List<Historial> findAllByItem(String tabla, Long idTabla) {
		return historialDao.findByItem(tabla, idTabla);
	}
	
	@Override
	public Historial findById(Long id) {
		return historialDao.findById(id).get();
	}

	@Override
	public Historial save(Historial Historial) {
		return historialDao.save(Historial);
	}

	@Override
	public void delete(Long id) {
		historialDao.deleteById(id);
	}

	@Override
	public List<Historial> findHistorialTicket(Long idTabla) {
		return historialDao.findHistorialTicket(idTabla);
	}

	
}
