package com.delta.deltanet.models.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.delta.deltanet.models.dao.ICategoriaDao;
import com.delta.deltanet.models.entity.Categoria;

@Service
public class CategoriaServiceImpl implements ICategoriaService {
	
	@Autowired
	private ICategoriaDao categoriaDao;

	@Override
	public List<Categoria> findAll() {
		return categoriaDao.findAll();
	}

	@Override
	public Categoria findById(Long id) {
		return categoriaDao.findById(id).get();
	}

	@Override
	public Categoria save(Categoria Categoria) {
		return categoriaDao.save(Categoria);
	}

	@Override
	public void delte(Long id) {
		categoriaDao.deleteById(id);
	}
	
}
