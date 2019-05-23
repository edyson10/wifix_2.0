<?php

$conexion = new PDO("mysql:host=localhost;dbname=wifix", "root","");

$marca = $_REQUEST['marca'];
$modelo = $_REQUEST['modelo'];
$precio = $_REQUEST['precio'];
$cantidad = $_REQUEST['cantidad'];
$cedulaEmp = $_REQUEST['empleado'];

//$res= $conexion->query("SELECT * FROM empleado WHERE estado=1");
$res = $conexion->query("INSERT INTO venta (id_venta, fecha_hora_venta, cedulaEmp, marca, modelo, precio, cantidad) 
		VALUES (NULL, DATE_SUB(current_timestamp(), INTERVAL 5 HOUR), '$cedulaEmp', '$marca' , '$modelo', '$precio', '$cantidad')");
$con= $conexion->query("SELECT * FROM venta ORDER by id_venta DESC LIMIT 1");
$datos = array();

foreach($con as $row){
	$resul=$conexion->query("UPDATE producto SET cantidad = cantidad - '$cantidad' where articulo = '$marca' and modelo = '$modelo'");
	$datos[]=$row;
}

echo json_encode($datos);
?>