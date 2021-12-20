package com.delta.deltanet.models.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.delta.deltanet.models.dao.IArchivoDao;
import com.delta.deltanet.models.entity.Archivo;

@Service
public class ArchivoServiceImpl implements IArchivoService {
	
	@Autowired
	private IArchivoDao archivoDao;

	@Override
	public List<Archivo> findAll() {
		return archivoDao.findAll();
	}

	@Override
	public Archivo findById(Long id) {
		return archivoDao.findById(id).get();
	}

	@Override
	public Archivo save(Archivo archivo) {
		return archivoDao.save(archivo);
	}

	@Override
	public void delte(Long id) {
		archivoDao.deleteById(id);
	}
	
}
