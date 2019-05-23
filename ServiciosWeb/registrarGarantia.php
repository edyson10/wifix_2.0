<?php
 
$conexion = new PDO("mysql:host=localhost;dbname=wifix", "root","");
//$conexion = new PDO("mysql:host=18.228.235.94;dbname=proyecto", "edynson","Edynson123@");

$id_reparacion = $_REQUEST['reparacion'];
$observacion = $_REQUEST['observacion'];

$res = $conexion->query("INSERT INTO garantia (id_garantia, id_reparacion, observacion, fecha_garantia, estado) VALUES (NULL, '$id_reparacion', '$observacion', DATE_SUB(current_timestamp(), INTERVAL 5 HOUR), 1)");

$con=$conexion->query("SELECT * FROM garantia ORDER by id_garantia DESC LIMIT 1");
$datos = array();

foreach($con as $row){
    $datos[]=$row;
}

echo json_encode($datos);

?>