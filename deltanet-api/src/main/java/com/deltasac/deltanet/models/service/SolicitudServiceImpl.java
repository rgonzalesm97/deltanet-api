package com.deltasac.deltanet.models.service;

import java.util.List;

import org.springframework.transaction.annotation.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import com.deltasac.deltanet.models.dao.ISolicitudDao;
import com.deltasac.deltanet.models.entity.Area;
import com.deltasac.deltanet.models.entity.EstadoSolic;
import com.deltasac.deltanet.models.entity.Solicitud;

@Service
public class SolicitudServiceImpl implements ISolicitudService {
	
	@Autowired
	private ISolicitudDao solicitudDao;

	@Override
	@Transactional(readOnly=true)
	public List<Solicitud> findAll() {
		return (List<Solicitud>) solicitudDao.findAll();
	}

	@Override
	@Transactional(readOnly=true)
	public Solicitud findById(Long id) {
		return solicitudDao.findById(id).orElse(null);
	}

	@Override
	@Transactional
	public Solicitud save(Solicitud solicitud) {
		return solicitudDao.save(solicitud);
	}

	@Override
	@Transactional
	public void delete(Long id) {
		solicitudDao.deleteById(id);		
	}

	@Override
	public Page<Solicitud> findAll(Integer id, Pageable pageable) {
		/*------------------------------------------------------
		 Si queremos listar todos sin filtro paginado:
		 
		 --> return solicitudDao.findAll(pageable);
		 ------------------------------------------------------*/
		return solicitudDao.findByidcrea(id, pageable);
	}

	@Override
	@Transactional(readOnly=true)
	public List<Area> findAllAreas() {
		return solicitudDao.findAllAreas();
	}
	
	@Override
	@Transactional(readOnly=true)
	public EstadoSolic cargaEstado(Long id) {
		System.out.println("ingresa al servicio");
		return solicitudDao.findEstado(id);
	}

	@Override
	@Transactional(readOnly=true)
	public List<Solicitud> findAllIdCrea(Integer id) {
		return solicitudDao.findByidcrea(id);
	}

	@Override
	@Transactional(readOnly=true)
	public Area findAreaId(Long id) {
		return solicitudDao.findAreaId(id);
	}

}
