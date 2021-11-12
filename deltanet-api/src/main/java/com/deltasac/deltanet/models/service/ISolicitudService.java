package com.deltasac.deltanet.models.service;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.deltasac.deltanet.models.entity.Solicitud;

public interface ISolicitudService {
	
	public List<Solicitud> findAll();
	public Page<Solicitud> findAll(Pageable pageable);
	public Solicitud findById(Long id);
	public Solicitud save(Solicitud solicitud);
	public void delete(Long id);

}
