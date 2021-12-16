package com.delta.deltanet.models.entity;

import java.io.Serializable;
import java.sql.Timestamp;
import java.util.Date;
import java.util.Set;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.PrePersist;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.sun.istack.NotNull;

@Entity
@Table(name="catalogo_servicio")
public class CatalogoServicio implements Serializable{
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "id")
	private Long id;
	
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "area_id", referencedColumnName = "id", nullable = false)
	@JsonIgnoreProperties({ "hibernateLazyInitializer", "handler" })
	private Area area;
	
	//many to many con usuarioServicio
	@ManyToMany
	@JoinTable(
			name="usuario_catalogo",
			joinColumns= @JoinColumn(name="catalogo_servicio_id"),
			inverseJoinColumns = @JoinColumn(name="usuario_servicio_id")
	)
	private Set<UsuarioServicio> usuarioServicios;
	
	
	@Column(name = "nombre", length = 100, nullable = false)
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

	public Area getArea() {
		return area;
	}

	public void setArea(Area area) {
		this.area = area;
	}

	public Set<UsuarioServicio> getUsuarioServicios() {
		return usuarioServicios;
	}

	public void setUsuarioServicios(Set<UsuarioServicio> usuarioServicios) {
		this.usuarioServicios = usuarioServicios;
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
		return "CatalogoServicio [id=" + id + ", area=" + area + ", usuarioServicios=" + usuarioServicios + ", nombre="
				+ nombre + ", usuCreado=" + usuCreado + ", fechaCreado=" + fechaCreado + ", usuEditado=" + usuEditado
				+ ", fechaEditado=" + fechaEditado + ", estadoRegistro=" + estadoRegistro + "]";
	}


	private static final long serialVersionUID = 1L;

}
