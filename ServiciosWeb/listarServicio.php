<?php

//$conexion = new PDO("mysql:host=localhost;dbname=wifix", "root","");
$conexion = new PDO("mysql:host=18.228.235.94;dbname=proyecto", "edynson","Edynson123@");

$res = $conexion->query("SELECT * FROM servicio ORDER BY id_servicio DESC LIMIT 5");

$datos = array();

foreach($res as $row){
    $datos[]=$row;
}

echo json_encode($datos);

?>