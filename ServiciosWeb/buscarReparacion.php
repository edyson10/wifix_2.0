<?php
 
$conexion = new PDO("mysql:host=localhost;dbname=wifix", "root","");
//$conexion = new PDO("mysql:host=18.228.235.94;dbname=proyecto", "edynson","Edynson123@");

$id_reparacion = $_REQUEST['reparacion'];

$res = $conexion->query("SELECT r.id_reparacion, r.fecha_reparacion, d.cedulaEmp, d.marca, d.modelo,  d.falla, d.observacion, r.detalle_reparacion, d.cedulaCli, r.fecha_entrega from diagnostico d inner join reparacion r on r.id_servicio = d.id_servicio where r.id_reparacion = '$id_reparacion'");
$datos = array();

foreach($res as $row){
    $datos[]=$row;
}

echo json_encode($datos);

?>
