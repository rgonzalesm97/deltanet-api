package com.delta.deltanet.models.dao;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.delta.deltanet.models.entity.Area;

public interface IAreaDao extends JpaRepository<Area, Long> {
	
	@Query("from Area where estadoRegistro = 'A'")
	public List<Area> findAll();
}
