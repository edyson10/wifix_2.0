<?php

$empleado = $_REQUEST['empleado'];
$descripcion = $_REQUEST['descripcion'];
$precio = $_REQUEST['precio'];
$tipo = $_REQUEST['tipo'];

$conexion = new PDO("mysql:host=localhost;dbname=wifix", "root","");
//$res= $conexion->query("SELECT * FROM empleado WHERE estado=1")
$res = $conexion->query("INSERT INTO baja (id_baja, cedulaEmp, tipo_baja, descripcion, precio, fecha_baja) VALUES (NULL, '$empleado', '$tipo', '$descripcion', '$precio', CURDATE())");

//$res = $conexion->query("INSERT INTO baja (id_baja, cedulaEmp, tipo_baja, descripcion, precio, fecha_baja) VALUES (NULL, '$empleado', '$tipo', '$descripcion', '$precio', DATE_SUB(current_timestamp(), INTERVAL 5 HOUR))");

$con=$conexion->query("SELECT * FROM baja ORDER by id_baja DESC LIMIT 1");
$datos = array();

foreach($con as $row){
    $datos[]=$row;
}

echo json_encode($datos);

?>