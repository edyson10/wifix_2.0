<?php

$cedula = $_REQUEST['cedula'];
$pass = $_REQUEST['password'];
$tipo = $_REQUEST['tipo'];

$conexion = new PDO("mysql:host=localhost;dbname=wifix", "root","");
$res= $conexion->query("SELECT * FROM empleado WHERE cedula = '$cedula' AND password = '$pass' AND id_tipoempleado = '$tipo'");

$datos = array();

foreach($res as $row){
    $datos[]=$row;
}

echo json_encode($datos);

?>