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
import javax.persistence.ManyToOne;
import javax.persistence.PrePersist;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.sun.istack.NotNull;

@Entity
@Table(name="historial")
public class Historial implements Serializable{
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "id")
	private Long id;
	
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "tipo_accion_id", referencedColumnName = "id", nullable = false)
	@JsonIgnoreProperties({ "hibernateLazyInitializer", "handler" })
	private TipoAccion tipoAccionId;
	
	@Column(name = "usuario_asignado", length = 50)
	private String usuAsignado;
	
	@Column(name = "tabla", length = 100, nullable = false)
	private String tabla;
	
	@Column(name = "tabla_id", nullable = false)
	private Long tablaId;
	
	@Column(name = "accion", length = 250, nullable = false)
	private String accion;
	
	@Column(name = "visibilidad", nullable = false)
	private char visibilidad;
	
	@Column(name = "usu_creado", length = 50, nullable = false)
	private String usuCreado;
	
	@Column(name = "fecha_creado", nullable = false)
	@Temporal(TemporalType.TIMESTAMP)
	private Date fechaCreado;

	
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



	public TipoAccion getTipoAccionId() {
		return tipoAccionId;
	}



	public void setTipoAccionId(TipoAccion tipoAccionId) {
		this.tipoAccionId = tipoAccionId;
	}



	public String getUsuAsignado() {
		return usuAsignado;
	}



	public void setUsuAsignado(String usuAsignado) {
		this.usuAsignado = usuAsignado;
	}



	public String getTabla() {
		return tabla;
	}



	public void setTabla(String tabla) {
		this.tabla = tabla;
	}



	public Long getTablaId() {
		return tablaId;
	}



	public void setTablaId(Long tablaId) {
		this.tablaId = tablaId;
	}



	public String getAccion() {
		return accion;
	}



	public void setAccion(String accion) {
		this.accion = accion;
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



	@Override
	public String toString() {
		return "Historial [id=" + id + ", tipoAccionId=" + tipoAccionId + ", usuAsignado=" + usuAsignado + ", tabla="
				+ tabla + ", tablaId=" + tablaId + ", accion=" + accion + ", visibilidad=" + visibilidad
				+ ", usuCreado=" + usuCreado + ", fechaCreado=" + fechaCreado + "]";
	}



	private static final long serialVersionUID = 1L;

}
