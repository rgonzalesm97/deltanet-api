package com.delta.deltanet.models.entity;

import java.io.Serializable;
import java.sql.Timestamp;
import java.util.Date;
import java.util.Set;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToMany;
import javax.persistence.PrePersist;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import org.springframework.web.bind.annotation.Mapping;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.sun.istack.NotNull;

@Entity
@Table(name="usuario_servicio")
public class UsuarioServicio implements Serializable{
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "id")
	private Long id;
	
	//many to many con catalogoSerivicio
	@ManyToMany(mappedBy = "usuarioServicios")
	@JsonIgnore
	private Set<CatalogoServicio> catalogoServicios;
	
	@Column(name = "usuario", length = 50, nullable = false)
	private String usuario;
	
	@Column(name = "nombre", length = 100, nullable = false)
	private String nombre;
	
	@Column(name = "apellidos", length = 100, nullable = false)
	private String apellidos;
	
	@Column(name = "rol", nullable = false)
	private char rol;
	
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
	
	@Column(name = "estado_registro")
	private char estadoRegistro;
	
	@PrePersist
	public void prePersist() {
		//fechaCreado = new Date();
		rol = 2;
		estadoRegistro = 'A';
	}
	
	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public Set<CatalogoServicio> getCatalogoServicios() {
		return catalogoServicios;
	}

	public void setCatalogoServicios(Set<CatalogoServicio> catalogoServicios) {
		this.catalogoServicios = catalogoServicios;
	}

	public String getUsuario() {
		return usuario;
	}

	public void setUsuario(String usuario) {
		this.usuario = usuario;
	}

	public String getNombre() {
		return nombre;
	}

	public void setNombre(String nombre) {
		this.nombre = nombre;
	}

	public String getApellidos() {
		return apellidos;
	}

	public void setApellidos(String apellidos) {
		this.apellidos = apellidos;
	}

	public char getRol() {
		return rol;
	}

	public void setRol(char rol) {
		this.rol = rol;
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

	private static final long serialVersionUID = 1L;

}
