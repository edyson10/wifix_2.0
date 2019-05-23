<?php

include 'conexion.php';

$num_servicio = $_REQUEST['servicio'];

$conexion=mysqli_connect($hostname_localhost,$username_localhost,$password_localhost,$database_localhost);
$sql = "SELECT * FROM servicio WHERE id_servicio = '$num_servicio'";

$resultado_insert=mysqli_query($conexion,$sql);

if($resultado_insert){
		$resultado=mysqli_query($conexion,$sql);

		if($registro=mysqli_fetch_array($resultado)){
			$json['servicio']=$registro;
		}
		mysqli_close($conexion);
		echo json_encode($json);
	}
?>