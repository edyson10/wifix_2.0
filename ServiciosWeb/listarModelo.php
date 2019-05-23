<?php

include 'conexion.php';

$conexion=mysqli_connect($hostname_localhost,$username_localhost,$password_localhost,$database_localhost);
$sql = "SELECT articulo FROM producto";
$resultado_insert=mysqli_query($conexion,$sql);

$json = array();
if($resultado_insert){
		$consulta="SELECT articulo FROM producto group by articulo";
		$resultado=mysqli_query($conexion,$sql);
		
		$datos = array();

		foreach($resultado as $row){
		    $datos[]=$row;
		}
		echo json_encode($datos);

	}

	?>