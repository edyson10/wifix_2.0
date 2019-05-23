<?php

include 'conexion.php';
	
$articulo = $_REQUEST['articulo'];
$modelo = $_REQUEST['modelo'];
$precioUni = $_REQUEST['precioUni'];
$precioVen = $_REQUEST['precioVen'];
$cantidad = $_REQUEST['cantidad'];
$descripcion = $_REQUEST['descripcion'];

$sql="INSERT INTO producto (articulo, modelo, precioUnitario, precioVenta, cantidad, descripcion) 
		VALUES ('$articulo', '$modelo', '$precioUni', '$precioVen', '$cantidad', '$descripcion')";

$resultado_insert=mysqli_query($conexion,$sql);
//VALIDAR CUANDO EL ARTICULO Y EL MODELO YA EXISTEN EN LA BD
if($resultado_insert){
	$consulta="SELECT * FROM producto WHERE articulo = '{$articulo}' AND modelo = '{$modelo}'";
	$resultado=mysqli_query($conexion,$consulta);

	$datos = array();
	if($registro=mysqli_fetch_array($resultado)){
		$json['producto']=$registro;
		echo json_encode($json);
	}
	mysqli_close($conexion);
	echo json_encode($datos);
}
?>