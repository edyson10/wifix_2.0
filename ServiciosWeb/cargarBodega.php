<?php

$conexion = new PDO("mysql:host=localhost;dbname=wifix", "root","");
//$conexion = new PDO("mysql:host=18.228.235.94;dbname=proyecto", "edynson","Edynson123@");

$con=$conexion->query("SELECT * FROM bodega");
$datos = array();

foreach($con as $row){
    $datos[]=$row;
}

echo json_encode($datos);
?>