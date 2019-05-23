<?php

$modelo = $_REQUEST['modelo'];
$conexion = new PDO("mysql:host=localhost;dbname=id7938581_dbwifix", "id7938581_admin","appwifix");
$res= $conexion->query("SELECT modelo FROM producto where articulo = '$modelo'");

$datos = array();

foreach($res as $row){
    $datos[]=$row;
}

echo json_encode($datos);

?>