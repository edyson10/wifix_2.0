<?php

include 'conexion.php';

$articulo = $_REQUEST['articulo'];
$modelo = $_REQUEST['modelo'];
$cantidad = $_REQUEST['cantidad'];

$sql = "UPDATE producto SET cantidad = cantidad + '$cantidad' WHERE articulo = '$articulo' AND modelo = '$modelo'";

$resultado_update = mysqli_query($conexion, $sql);
if ($resultado_update) {
	$consulta = "SELECT * FROM producto WHERE articulo = '$articulo' AND modelo = '$modelo'";
	$resultado = mysqli_query($conexion, $consulta);

	$datos = array();

	if($registro=mysqli_fetch_array($resultado)){
		$json['producto']=$registro;
		echo json_encode($json);
	}
	mysqli_close($conexion);
	echo json_encode($datos);
}
?>