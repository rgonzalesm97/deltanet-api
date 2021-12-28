package com.delta.deltanet.models.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.delta.deltanet.models.dao.ITicketDao;
import com.delta.deltanet.models.entity.Ticket;

@Service
public class TicketServiceImpl implements ITicketService {
	
	@Autowired
	private ITicketDao ticketDao;

	@Override
	public List<Ticket> findAll() {
		return ticketDao.findAll();
	}

	@Override
	public Ticket findById(Long id) {
		return ticketDao.findById(id).orElse(null);
	}

	@Override
	public Ticket save(Ticket Ticket) {
		return ticketDao.save(Ticket);
	}

	@Override
	public void delete(Long id) {
		ticketDao.deleteById(id);
	}

	@Override
	public List<Ticket> findAllByUsuarioServicio(Long id) {
		return ticketDao.findAllByUsuarioServicio(id);
	}
	
}
