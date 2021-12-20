package com.delta.deltanet.models.service;

import java.util.List;

import com.delta.deltanet.models.entity.Area;

public interface IAreaService {
	
	public List<Area> findAll();
	public Area findById(Long id);
	public Area save(Area area);
	public void delte(Long id);
	
}
