<?php

$conexion = new PDO("mysql:host=localhost;dbname=wifix", "root","");

$id = $_REQUEST["servicio"];

$res = $conexion->query("UPDATE diagnostico SET estado = 0 WHERE id_servicio = $id");
$con = $conexion->query("SELECT * FROM diagnostico WHERE id_servicio = $id");
$datos = array();
foreach($con as $row){
    $datos[]=$row;
}
echo json_encode($datos);

?>