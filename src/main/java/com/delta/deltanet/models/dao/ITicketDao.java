package com.delta.deltanet.models.dao;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.delta.deltanet.models.entity.Ticket;

public interface ITicketDao extends JpaRepository<Ticket, Long> {
	
	@Query("from Ticket where areaOrigen.id=?1 or (areaDestino.id=?2 or tipoUsuarioCreador=?3 or prioridad.id=?4 or categoria.id=?5 or catalogoServicio.id=?6 or usuarioCreador=?7)")
	public List<Ticket> findAllFiltro(Long idAreaOrigen,
										Long idAreaDestino,
										String tipoUsuario,
										Long idPrioridad,
										Long idCategoria,
										Long idCatalogoServicio,
										String usuarioCrea);

	@Query("from Ticket where usuarioServicio.id = ?1")
	public List<Ticket> findAllByUsuarioServicio(Long idUsuarioServicio);
	
	@Query("from Ticket where usuarioServicio.id = null")
	public List<Ticket> findAllByUsuarioServicioNull();
	
	@Query("from Ticket where usuarioCreador = ?1")
	public List<Ticket> findAllByUsuarioCreador(String usuario);
	
	@Query("from Ticket where estado.id = 4 and (usuarioCreador = ?1 or usuarioServicio.id = ?2 or areaDestino.id = ?3)")
	public List<Ticket> findAllResueltos(String usuario, Long usuarioServicioId, Long areaId);
	
	@Query("from Ticket where estado.id = 4 and usuarioCreador = ?1")
	public List<Ticket> findAllResueltosByUsuarioCreador(String usuario);
	
	@Query("from Ticket where estado.id = 4 and areaDestino.id = ?1")
	public List<Ticket> findAllResueltosAdmin(Long areaId);
	
	@Query("from Ticket where fechaEditado <> null and fechaEditado <= CURRENT_TIMESTAMP")
	public List<Ticket> findAllModificados();
	
}
