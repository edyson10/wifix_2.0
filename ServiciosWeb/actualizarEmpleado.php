<?php

$conexion = new PDO("mysql:host=localhost;dbname=wifix", "root","");

$cedula = $_REQUEST['cedula'];
$nombre = $_REQUEST['nombre'];
$apellido = $_REQUEST['apellido'];
$telefono = $_REQUEST['telefono'];
$direccion = $_REQUEST['direccion'];
$correo = $_REQUEST['correo'];
	
$resul= $conexion->query("UPDATE empleado SET nombre='$nombre',apellido='$apellido',telefono='$telefono', direccion='$direccion' ,correo='$correo' WHERE cedula = '$cedula'");
$res= $conexion->query("SELECT * FROM empleado WHERE cedula = '{$cedula}'");
$datos = array();

foreach($res as $row){
    $datos[]=$row;
}

echo json_encode($datos);
?>