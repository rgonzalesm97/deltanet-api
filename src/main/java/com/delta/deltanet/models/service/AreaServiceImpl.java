package com.delta.deltanet.models.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.delta.deltanet.models.dao.IAreaDao;
import com.delta.deltanet.models.entity.Area;

@Service
public class AreaServiceImpl implements IAreaService {
	
	@Autowired
	private IAreaDao areaDao;

	@Override
	public List<Area> findAll() {
		return areaDao.findAll();
	}

	@Override
	public Area findById(Long id) {
		return areaDao.findById(id).orElse(null);
	}

	@Override
	public Area save(Area Area) {
		return areaDao.save(Area);
	}

	@Override
	public void delete(Long id) {
		areaDao.deleteById(id);
	}
	
}
