package com.delta.deltanet.models.entity;

import java.io.Serializable;
import java.sql.Timestamp;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.Lob;
import javax.persistence.ManyToOne;
import javax.persistence.PrePersist;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.sun.istack.NotNull;

@Entity
@Table(name="comentario")
public class Comentario implements Serializable{
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "id")
	private Long id;
	
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "ticket_id", referencedColumnName = "id", nullable = false)
	@JsonIgnoreProperties({ "hibernateLazyInitializer", "handler" })
	private Ticket ticket;
	
	@Column(name = "usuario", length = 50, nullable = false)
	private String usuario;
	
	@Column(name = "descripcion", nullable = false, columnDefinition = "text")
	@Lob
	private String descripcion;
	
	@Column(name = "visibilidad", nullable = false)
	private char visibilidad;
	
	@Column(name = "usu_creado", length = 50, nullable = false)
	private String usuCreado;
	
	@Column(name = "fecha_creado", nullable = false)
	@Temporal(TemporalType.TIMESTAMP)
	private Date fechaCreado;
	
	@Column(name = "usu_editado", length = 50)
	private String usuEditado;
	
	@Column(name = "fecha_editado")
	@Temporal(TemporalType.TIMESTAMP)
	private Date fechaEditado;
	
	@PrePersist
	public void prePersist() {
		//fechaCreado = new Date();
		visibilidad = 'S';
	}

	
	public Long getId() {
		return id;
	}


	public void setId(Long id) {
		this.id = id;
	}


	public Ticket getTicket() {
		return ticket;
	}


	public void setTicket(Ticket ticket) {
		this.ticket = ticket;
	}


	public String getUsuario() {
		return usuario;
	}


	public void setUsuario(String usuario) {
		this.usuario = usuario;
	}


	public String getDescripcion() {
		return descripcion;
	}


	public void setDescripcion(String descripcion) {
		this.descripcion = descripcion;
	}


	public char getVisibilidad() {
		return visibilidad;
	}


	public void setVisibilidad(char visibilidad) {
		this.visibilidad = visibilidad;
	}


	public String getUsuCreado() {
		return usuCreado;
	}


	public void setUsuCreado(String usuCreado) {
		this.usuCreado = usuCreado;
	}


	public Date getFechaCreado() {
		return fechaCreado;
	}


	public void setFechaCreado(Date fechaCreado) {
		this.fechaCreado = fechaCreado;
	}


	public String getUsuEditado() {
		return usuEditado;
	}


	public void setUsuEditado(String usuEditado) {
		this.usuEditado = usuEditado;
	}


	public Date getFechaEditado() {
		return fechaEditado;
	}


	public void setFechaEditado(Date fechaEditado) {
		this.fechaEditado = fechaEditado;
	}


	@Override
	public String toString() {
		return "Comentario [id=" + id + ", ticket=" + ticket + ", usuario=" + usuario + ", descripcion=" + descripcion
				+ ", visibilidad=" + visibilidad + ", usuCreado=" + usuCreado + ", fechaCreado=" + fechaCreado
				+ ", usuEditado=" + usuEditado + ", fechaEditado=" + fechaEditado + "]";
	}


	private static final long serialVersionUID = 1L;

}
