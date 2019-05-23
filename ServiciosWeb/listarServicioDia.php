<?php

//$conexion = new PDO("mysql:host=18.228.235.94;dbname=proyecto", "edynson","Edynson123@");
$conexion = new PDO("mysql:host=localhost;dbname=wifix", "root","");

//$res = $conexion->query("SELECT cedulaEmp, marca, modelo, falla, descripcion, observacion, precio, costo, cedulaCli, fechaEntrega, estado FROM servicio WHERE fechaServicio = CURDATE() order by id_servicio desc");

$res = $conexion->query("SELECT id_servicio, fecha_diagnostico, cedulaEmp, marca, modelo, falla, diagnostico, observacion, precio, cedulaCli, fechaEntrega, clave, estado 
FROM diagnostico WHERE DATE(fecha_diagnostico) = CURDATE() order by id_servicio DESC");

//$res = $conexion->query("SELECT cedulaEmp, marca, modelo, falla, descripcion, observacion, precio, costo, cedulaCli, fechaEntrega, estado FROM servicio ");

$datos = array();

foreach($res as $row){
    $datos[]=$row;
}

echo json_encode($datos);

?>