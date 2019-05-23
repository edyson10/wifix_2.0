<?php
 
$conexion = new PDO("mysql:host=localhost;dbname=wifix", "root","");
//$conexion = new PDO("mysql:host=18.228.235.94;dbname=proyecto", "edynson","Edynson123@");

$id_servicio = $_REQUEST['servicio'];
$bodega = $_REQUEST['bodega'];
$repuesto = $_REQUEST['repuesto'];
$detalle_reparacion = $_REQUEST['detalle'];
$costo_reparacion = $_REQUEST['costo'];
$precio_reparacion = $_REQUEST['precio'];
$fecha_entrega = $_REQUEST['fechaEntrega'];

//$res = $conexion->query("INSERT INTO reparacion (id_reparacion, id_servicio, fecha_reparacion, bodega, detalle_reparacion, costo_reparacion, precio_reparacion, fecha_entrega, estado) VALUES (NULL, '$id_servicio', DATE_SUB(current_timestamp(), INTERVAL 5 HOUR), '$bodega', '$detalle_reparacion', '$costo_reparacion', '$precio_reparacion', '$fecha_entrega', 1)");

$res=$conexion->query("INSERT INTO reparacion (id_reparacion, id_servicio, fecha_reparacion, bodega, repuesto, detalle_reparacion, costo_reparacion, precio_reparacion, fecha_entrega, estado) VALUES (NULL, '$id_servicio', DATE_SUB(current_timestamp(), INTERVAL 5 HOUR), '$bodega', '$repuesto', '$detalle_reparacion', '$costo_reparacion', '$precio_reparacion', '$fecha_entrega', 1)");

$con=$conexion->query("SELECT * FROM reparacion ORDER by id_reparacion DESC LIMIT 1");
$datos = array();

foreach($con as $row){
    $datos[]=$row;
}

echo json_encode($datos);


?>