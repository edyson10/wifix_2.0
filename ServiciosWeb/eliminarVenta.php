<?php

$conexion = new PDO("mysql:host=localhost;dbname=wifix", "root","");
//$conexion = new PDO("mysql:host=18.228.235.94;dbname=proyecto", "edynson","Edynson123@");

$venta = $_REQUEST['venta'];
$cantidad = $_REQUEST['cantidad'];
$articulo = $_REQUEST['articulo'];
$modelo = $_REQUEST['modelo'];

$consulta = $conexion->query("UPDATE producto SET cantidad = cantidad + '$cantidad' WHERE articulo = '$articulo' AND modelo = '$modelo'");
$sql = $conexion->query("DELETE FROM venta WHERE id_venta = '$venta'");

$con=$conexion->query("SELECT * FROM producto WHERE articulo = '$articulo' AND modelo = '$modelo'");
$datos = array();

foreach($con as $row){
    $datos[]=$row;
}

echo json_encode($datos);

?>