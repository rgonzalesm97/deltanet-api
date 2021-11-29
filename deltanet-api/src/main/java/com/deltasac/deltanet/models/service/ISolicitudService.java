package com.deltasac.deltanet.models.service;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.deltasac.deltanet.models.entity.Area;
import com.deltasac.deltanet.models.entity.EstadoSolic;
import com.deltasac.deltanet.models.entity.Solicitud;

public interface ISolicitudService {
	
	public List<Solicitud> findAll();
	public List<Solicitud> findAllIdCrea(Integer id);
	public List<Solicitud> findAllAreaIdUser(Long id);
	public Page<Solicitud> findAll(Integer id, Pageable pageable);
	public Solicitud findById(Long id);
	public Solicitud save(Solicitud solicitud);
	public void delete(Long id);
	public List<Area> findAllAreas();
	public Area findAreaId(Long id);
	public EstadoSolic cargaEstado(Long id);

}
