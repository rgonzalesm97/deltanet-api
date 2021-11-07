package com.deltasac.deltanet.models.service;

import java.util.List;

import org.springframework.transaction.annotation.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.deltasac.deltanet.models.dao.ISolicitudDao;
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

}
