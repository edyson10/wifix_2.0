package com.example.duvan.wifix_v2.Entidades;

public class ServicioVo {

    private String fechaServicio;
    private String cedulaEmp;
    private String marca;
    private String modelo;
    private String falla;
    private String diagnostico;
    private String observacion;
    private String costo;
    private String clave;
    private String estado;
    private String fechaEntrega;
    private int imagen;

    public ServicioVo() {
    }

    public ServicioVo(String fechaServicio, String cedulaEmp, String marca, String modelo, String falla,
                      String diagnostico, String observacion, String costo, String clave,
                      String estado, String fechaEntrega, int foto) {
        this.fechaServicio = fechaServicio;
        this.cedulaEmp = cedulaEmp;
        this.marca = marca;
        this.modelo = modelo;
        this.falla = falla;
        this.diagnostico = diagnostico;
        this.observacion = observacion;
        this.costo = costo;
        this.clave = clave;
        this.estado = estado;
        this.fechaEntrega = fechaEntrega;
        this.imagen = foto;
    }

    public String getFechaServicio() {
        return fechaServicio;
    }

    public void setFechaServicio(String fechaServicio) {
        this.fechaServicio = fechaServicio;
    }

    public String getCedulaEmp() {
        return cedulaEmp;
    }

    public void setCedulaEmp(String cedulaEmp) {
        this.cedulaEmp = cedulaEmp;
    }

    public String getMarca() {
        return marca;
    }

    public void setMarca(String marca) {
        this.marca = marca;
    }

    public String getModelo() {
        return modelo;
    }

    public void setModelo(String modelo) {
        this.modelo = modelo;
    }

    public String getFalla() {
        return falla;
    }

    public void setFalla(String falla) {
        this.falla = falla;
    }

    public String getDiagnostico() {
        return diagnostico;
    }

    public void setDiagnostico(String diagnostico) {
        this.diagnostico = diagnostico;
    }

    public String getObservacion() {
        return observacion;
    }

    public void setObservacion(String observacion) {
        this.observacion = observacion;
    }

    public String getCosto() {
        return costo;
    }

    public void setCosto(String costo) {
        this.costo = costo;
    }

    public String getClave() {
        return clave;
    }

    public void setClave(String clave) {
        this.clave = clave;
    }

    public String getEstado() {
        return estado;
    }

    public void setEstado(String estado) {
        this.estado = estado;
    }

    public String getFechaEntrega() {
        return fechaEntrega;
    }

    public void setFechaEntrega(String fechaEntrega) {
        this.fechaEntrega = fechaEntrega;
    }

    public int getImagen() {
        return imagen;
    }

    public void setImagen(int imagen) {
        this.imagen = imagen;
    }
}
