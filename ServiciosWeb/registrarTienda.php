<?php

include 'conexion.php';

$nombre = $_REQUEST['nombre'];
$direccion = $_REQUEST['direccion'];
$telefono = $_REQUEST['telefono'];
$admin = $_REQUEST['administrador'];

$sql = "INSERT INTO tienda (id_tienda, nombre, direccion, telefono, administrador) VALUES (NULL, '$nombre', '$direccion', '$telefono', '$admin');";
$resultado_insert=mysqli_query($conexion,$sql);

//VALIDAR CUANDO EL ARTICULO Y EL MODELO YA EXISTEN EN LA BD
if($resultado_insert){
	$consulta="SELECT * FROM tienda ORDER BY id_tienda DESC LIMIT 1";
	$resultado=mysqli_query($conexion,$consulta);

	$datos = array();
	if($registro=mysqli_fetch_array($resultado)){
		$json[]=$registro;
		echo json_encode($json);
	}
	mysqli_close($conexion);
	echo json_encode($json);
}

?>