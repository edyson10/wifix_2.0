<?php

$conexion = new PDO("mysql:host=localhost;dbname=wifix", "root","");

$marca = $_REQUEST['marca'];
$modelo = $_REQUEST['modelo'];
$cantidad = $_REQUEST['cantidad'];
	
$resul= $conexion->query("UPDATE producto SET cantidad = cantidad + '$cantidad' where articulo = '$marca' and modelo = '$modelo' ");
$res= $conexion->query("SELECT * FROM producto WHERE articulo = '{$marca}' AND modelo = '{$modelo}'");
$datos = array();

foreach($res as $row){
    $datos[]=$row;
}

echo json_encode($datos);
?>