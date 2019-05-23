<?php

$conexion = new PDO("mysql:host=localhost;dbname=wifix", "root","");
//$conexion = new PDO("mysql:host=18.228.235.94;dbname=proyecto", "edynson","Edynson123@");

$marca = $_REQUEST['marca'];
$modelo = $_REQUEST['modelo'];

$con=$conexion->query("SELECT producto.articulo, producto.modelo, producto.cantidad, bodega.nombre AS bodega  
FROM producto INNER JOIN bodega ON bodega.id_bodega = producto.bodega WHERE articulo = '$marca' AND modelo = '$modelo' AND estado = 1");
$datos = array();

foreach($con as $row){
    $datos[]=$row;
}

echo json_encode($datos);
?>