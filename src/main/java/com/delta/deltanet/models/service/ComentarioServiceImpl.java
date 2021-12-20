package com.delta.deltanet.models.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.delta.deltanet.models.dao.IComentarioDao;
import com.delta.deltanet.models.entity.Comentario;

@Service
public class ComentarioServiceImpl implements IComentarioService {
	
	@Autowired
	private IComentarioDao comentarioDao;

	@Override
	public List<Comentario> findAll() {
		return comentarioDao.findAll();
	}

	@Override
	public Comentario findById(Long id) {
		return comentarioDao.findById(id).get();
	}

	@Override
	public Comentario save(Comentario Comentario) {
		return comentarioDao.save(Comentario);
	}

	@Override
	public void delte(Long id) {
		comentarioDao.deleteById(id);
	}
	
}
