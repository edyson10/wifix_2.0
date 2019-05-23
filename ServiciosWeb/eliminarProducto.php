<?php

//$conexion = new PDO("mysql:host=localhost;dbname=wifix", "root","");
//$conexion = new PDO("mysql:host=18.228.235.94;dbname=proyecto", "edynson","Edynson123@");

include 'conexion.php';

$articulo = $_REQUEST['articulo'];
$modelo = $_REQUEST['modelo'];
$cantidad = $_REQUEST['cantidad'];

$resul= $conexion->query("UPDATE producto SET cantidad = cantidad - '$cantidad' WHERE articulo = '$articulo' AND modelo = '$modelo'");
$res= $conexion->query("SELECT * FROM producto WHERE articulo = '$articulo' AND modelo = '$modelo'");
$datos = array();

foreach($res as $row){
    $datos[]=$row;
}

echo json_encode($datos);

?>