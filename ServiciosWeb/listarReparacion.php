<?php

//$conexion = new PDO("mysql:host=18.228.235.94;dbname=proyecto", "edynson","Edynson123@");
$conexion = new PDO("mysql:host=localhost;dbname=wifix", "root","");

$res = $conexion->query("SELECT * FROM reparacion order by id_reparacion DESC LIMIT 20");

$datos = array();

foreach($res as $row){
    $datos[]=$row;
}

echo json_encode($datos);

?>