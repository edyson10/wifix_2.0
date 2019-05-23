<?php
 
$conexion = new PDO("mysql:host=localhost;dbname=wifix", "root","");
//$conexion = new PDO("mysql:host=18.228.235.94;dbname=proyecto", "edynson","Edynson123@");

$id_baja = $_REQUEST['salida'];

$res = $conexion->query("SELECT id_baja, tipo_baja, descripcion, precio, fecha_baja FROM baja WHERE id_baja = '$id_baja'");
$datos = array();

foreach($res as $row){
    $datos[]=$row;
}

echo json_encode($datos);

?>
