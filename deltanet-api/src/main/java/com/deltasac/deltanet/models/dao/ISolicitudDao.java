package com.deltasac.deltanet.models.dao;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.deltasac.deltanet.models.entity.Area;
import com.deltasac.deltanet.models.entity.Solicitud;

public interface ISolicitudDao extends JpaRepository<Solicitud, Long> {
	
	@Query("from Area")
	public List<Area> findAllAreas();
	
	public Page<Solicitud> findByidcrea(Integer id, Pageable pageable);
	
	public List<Solicitud> findByidcrea(Integer id);

}
