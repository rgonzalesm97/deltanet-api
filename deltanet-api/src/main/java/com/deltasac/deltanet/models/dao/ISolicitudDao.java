package com.deltasac.deltanet.models.dao;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.deltasac.deltanet.models.entity.Area;
import com.deltasac.deltanet.models.entity.EstadoSolic;
import com.deltasac.deltanet.models.entity.Solicitud;

public interface ISolicitudDao extends JpaRepository<Solicitud, Long> {
	
	@Query("from Area")
	public List<Area> findAllAreas();
	
	@Query("from Area where id=?1")
	public Area findAreaId(Long id);
	
	@Query("select e from EstadoSolic e where e.id=?1")
	public EstadoSolic findEstado(Long id);
	
	public Page<Solicitud> findByidcrea(Integer id, Pageable pageable);
	
	public List<Solicitud> findByidcrea(Integer id);

}
