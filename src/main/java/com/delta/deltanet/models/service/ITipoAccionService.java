package com.delta.deltanet.models.service;

import java.util.List;

import com.delta.deltanet.models.entity.TipoAccion;

public interface ITipoAccionService {
	
	public List<TipoAccion> findAll();
	public TipoAccion findById(Long id);
	public TipoAccion save(TipoAccion tipoAccion);
	public void delete(Long id);
	
}
