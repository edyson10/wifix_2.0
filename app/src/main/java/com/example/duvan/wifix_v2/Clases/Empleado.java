package com.example.duvan.wifix_v2.Clases;

public class Empleado {

    private String nonmbre;
    private String apellido;
    private String cedula;
    private String sexo;
    private String telefono;
    private String direccion;
    private String dorreo;
    private String password;
    private int tienda;
    private String rutaImagen;

    public Empleado(){

    }

    public Empleado(String nonmbre, String apellido, String cedula, String sexo, String telefono,
                    String direccion, String dorreo, String password, int tienda, String rutaImagen) {
        this.nonmbre = nonmbre;
        this.apellido = apellido;
        this.cedula = cedula;
        this.sexo = sexo;
        this.telefono = telefono;
        this.direccion = direccion;
        this.dorreo = dorreo;
        this.password = password;
        this.tienda = tienda;
        this.rutaImagen = rutaImagen;
    }

    public String getNonmbre() {
        return nonmbre;
    }

    public void setNonmbre(String nonmbre) {
        this.nonmbre = nonmbre;
    }

    public String getApellido() {
        return apellido;
    }

    public void setApellido(String apellido) {
        this.apellido = apellido;
    }

    public String getCedula() {
        return cedula;
    }

    public void setCedula(String cedula) {
        this.cedula = cedula;
    }

    public String getSexo() {
        return sexo;
    }

    public void setSexo(String sexo) {
        this.sexo = sexo;
    }

    public String getTelefono() {
        return telefono;
    }

    public void setTelefono(String telefono) {
        this.telefono = telefono;
    }

    public String getDireccion() {
        return direccion;
    }

    public void setDireccion(String direccion) {
        this.direccion = direccion;
    }

    public String getDorreo() {
        return dorreo;
    }

    public void setDorreo(String dorreo) {
        this.dorreo = dorreo;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public int getTienda() {
        return tienda;
    }

    public void setTienda(int tienda) {
        this.tienda = tienda;
    }

    public String getRutaImagen() {
        return rutaImagen;
    }

    public void setRutaImagen(String rutaImagen) {
        this.rutaImagen = rutaImagen;
    }
}
