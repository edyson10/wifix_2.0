<?php

$conexion = new PDO("mysql:host=localhost;dbname=id7938581_dbwifix", "id7938581_admin","appwifix");
//$res= $conexion->query("SELECT empleado.nombre, venta.total_producto,venta.totalventa FROM venta JOIN empleado ON v.id_empleado = e.id_empleado ");

$res = $conexion->query("SELECT SUM(total_venta) FROM venta AS subtabla");
$datos = array();

foreach($res as $row){
    $datos[]=$row;
}

echo json_encode($datos);

?>