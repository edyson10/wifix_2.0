<?php

include 'conexion.php';

$venta = $_REQUEST['venta'];
$marca = $_REQUEST['marca'];
$modelo = $_REQUEST['modelo'];
$precio = $_REQUEST['precio'];
$cantidad = $_REQUEST['cantidad'];

$consulta = "UPDATE `venta` SET marca='$marca',modelo='$modelo',precio='$precio',cantidad='$cantidad' WHERE id_venta = '$venta'";
$resultado_insert=mysqli_query($conexion,$consulta);

if($resultado_insert){
	$consulta="SELECT * FROM venta WHERE id_venta = '{$venta}'";
	$resultado=mysqli_query($conexion,$consulta);

	$datos = array();
	if($registro=mysqli_fetch_array($resultado)){
		$json['venta']=$registro;
		echo json_encode($json);
	}
	mysqli_close($conexion);
}
?>