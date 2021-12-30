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

	@Override
	public List<Ticket> findAllByUsuarioServicioNull() {
		return ticketDao.findAllByUsuarioServicioNull();
	}

	@Override
	public List<Ticket> findAllResueltos(String usuario, Long usuarioServicioId, Long areaId) {
		return ticketDao.findAllResueltos(usuario, usuarioServicioId, areaId);
	}

	@Override
	public List<Ticket> findAllResueltosAdmin(Long areaId) {
		return ticketDao.findAllResueltosAdmin(areaId);
	}

	@Override
	public List<Ticket> findAllModificados() {
		return ticketDao.findAllModificados();
	}

	@Override
	public List<Ticket> findAllFiltro(Long idAreaOrigen, Long idAreaDestino, String tipoUsuario, Long idPrioridad,
			Long idCategoria, Long idCatalogoServicio, String usuarioCrea) {
		return ticketDao.findAllFiltro(idAreaOrigen, idAreaDestino, tipoUsuario, idPrioridad, idCategoria, idCatalogoServicio, usuarioCrea);	
	}

	@Override
	public List<Ticket> findAllByUsuarioCreador(String usuario) {
		return ticketDao.findAllByUsuarioCreador(usuario);
	}

	@Override
	public List<Ticket> findAllResueltosByUsuarioCreador(String usuario) {
		return ticketDao.findAllResueltosByUsuarioCreador(usuario);
	}
	
}
