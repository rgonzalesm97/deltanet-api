package com.delta.deltanet.models.service;

import java.util.List;

import com.delta.deltanet.models.entity.Ticket;

public interface ITicketService {
	
	public List<Ticket> findAll();
	public Ticket findById(Long id);
	public Ticket save(Ticket ticket);
	public void delete(Long id);
	public List<Ticket> findAllByUsuarioServicio(Long id);
	public List<Ticket> findAllByUsuarioServicioNull();
	public List<Ticket> findAllByUsuarioCreador(String usuario);
	public List<Ticket> findAllResueltos(String usuario, Long usuarioServicioId, Long areaId);
	public List<Ticket> findAllResueltosByUsuarioCreador(String usuario);
	public List<Ticket> findAllResueltosAdmin(Long areaId);
	public List<Ticket> findAllModificados();
	public List<Ticket> findAllFiltro(Long idAreaOrigen,
										Long idAreaDestino,
										String tipoUsuario,
										Long idPrioridad,
										Long idCategoria,
										Long idCatalogoServicio,
										String usuarioCrea);
	
	
}
