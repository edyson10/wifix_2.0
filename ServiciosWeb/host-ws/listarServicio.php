<?php

include 'conexion.php';

$conexion=mysqli_connect($hostname_localhost,$username_localhost,$password_localhost,$database_localhost);
$sql = "SELECT * FROM servicio";
$resultado_insert=mysqli_query($conexion,$sql);

if($resultado_insert){
		$consulta="SELECT * FROM servicio";
		$resultado=mysqli_query($conexion,$consulta);
		
		if($registro=mysqli_fetch_array($resultado)){
			$json['servicio']=$registro;
		}
		mysqli_close($conexion);
		echo json_encode($json);
	}
?>