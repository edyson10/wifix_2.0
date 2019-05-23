<?php

include 'conexion.php';

$json = array();

if(isset($_REQUEST["venta"])){
	$venta = $_REQUEST["venta"];
	
	$consulta = "SELECT * FROM venta WHERE id_venta = '$venta'";
	$resultado = mysqli_query($conexion,$consulta);

	while($registro = mysqli_fetch_array($resultado)) {
		$result["marca"] = $registro['marca'];
		$result["modelo"] = $registro['modelo'];
		$result["precio"] = $registro['precio'];
		$result["cantidad"] = $registro['cantidad'];
		$json[]=$result;
	}
	mysqli_close($conexion);
	echo json_encode($json);

}

?>