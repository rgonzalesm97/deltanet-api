package com.delta.deltanet.models.entity;

import java.io.Serializable;
import java.math.BigInteger;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.PrePersist;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import com.sun.istack.NotNull;

@Entity
@Table(name="area")
public class Area implements Serializable{
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "id")
	private Long id;
	
	@Column(name = "nombre", length = 100, unique = true, nullable = false)
	private String nombre;
	
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
	
	@Column(name = "estado_registro", nullable = false)
	private char estadoRegistro;
	
	@PrePersist
	public void prePersist() {
		//fechaCreado = new Date();
		estadoRegistro = 'A';
	}
	
	
	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getNombre() {
		return nombre;
	}

	public void setNombre(String nombre) {
		this.nombre = nombre;
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

	public char getEstadoRegistro() {
		return estadoRegistro;
	}

	public void setEstadoRegistro(char estadoRegistro) {
		this.estadoRegistro = estadoRegistro;
	}

 
	@Override
	public String toString() {
		return "Area [id=" + id + ", nombre=" + nombre + ", usuCreado=" + usuCreado + ", fechaCreado="
				+ fechaCreado + ", usuEditado=" + usuEditado + ", fechaEditado=" + fechaEditado + ", estadoRegistro="
				+ estadoRegistro + "]";
	}


	private static final long serialVersionUID = 1L;

}
