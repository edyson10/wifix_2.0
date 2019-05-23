<?php

$conexion = new PDO("mysql:host=localhost;dbname=wifix", "root","");
//$conexion = new PDO("mysql:host=18.228.235.94;dbname=proyecto", "edynson","Edynson123@");

$res = $conexion->query("SELECT id_venta, marca, modelo, precio FROM venta WHERE fecha_hora_venta <= (DATE_SUB(current_timestamp(), INTERVAL 5 HOUR)) 
AND DATE_FORMAT(fecha_hora_venta,'%Y-%m-%d') = DATE_FORMAT(DATE_SUB(current_timestamp(), INTERVAL 5 HOUR),'%Y-%m-%d') ORDER BY id_venta DESC");
$datos = array();

foreach($res as $row){
    $datos[]=$row;
}

echo json_encode($datos);

?>