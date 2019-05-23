<?php

$conexion = new PDO("mysql:host=localhost;dbname=wifix", "root","");

$cedula = $_REQUEST['cedula'];
	
$res= $conexion->query("SELECT * FROM empleado WHERE cedula = '{$cedula}'");
$datos = array();

foreach($res as $row){
    $datos[]=$row;
}

echo json_encode($datos);
?>