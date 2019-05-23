<?php

//include 'conexion.php';

//$conexion = new PDO("mysql:host=localhost;dbname=wifix", "root","");
$conexion = new PDO("mysql:host=18.228.235.94;dbname=proyecto", "edynson","Edynson123@");

$articulo = $_REQUEST['articulo'];
$modelo = $_REQUEST['modelo'];
$precioUni = $_REQUEST['precioUni'];
$precioVen = $_REQUEST['precioVen'];
$cantidad = $_REQUEST['cantidad'];
$descripcion = $_REQUEST['descripcion'];
$bodega = $_REQUEST['bodega'];

$res = $conexion->query("INSERT INTO producto (articulo, modelo, precioUnitario, precioVenta, cantidad, descripcion, estado, bodega) 
		VALUES ('$articulo', '$modelo', '$precioUni', '$precioVen', '$cantidad', '$descripcion', 1, '$bodega')");

$con=$conexion->query("SELECT * FROM producto ORDER by id_producto DESC LIMIT 1");
$datos = array();

foreach($con as $row){
    $datos[]=$row;
}

echo json_encode($datos);

/*
$sql="INSERT INTO producto (articulo, modelo, precioUnitario, precioVenta, cantidad, descripcion) 
		VALUES ('$articulo', '$modelo', '$precioUni', '$precioVen', '$cantidad', '$descripcion')";

if ($resultado_insert=mysqli_query($conexion,$sql)) {
	echo "Registro";
}else {
	echo "No registro";
}
*/

?>