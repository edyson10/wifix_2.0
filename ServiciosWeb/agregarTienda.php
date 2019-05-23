<?php

include 'conexion.php';

$nombre = $_REQUEST['nombre'];
$direccion = $_REQUEST['direccion'];
$telefono = $_REQUEST['telefono'];
$correo = $_REQUEST['correo'];

$sql="INSERT INTO tienda (nombre, direccion, telefono, correo) VALUES ('$nombre', '$direccion', '$telefono', '$correo');";
$resultado_insert=mysqli_query($conexion,$sql);

if($resultado_insert){
		$consulta="SELECT * FROM tienda ORDER by id_tienda DESC LIMIT 1";
		$resultado=mysqli_query($conexion,$consulta);

		$datos = array();
		if($registro=mysqli_fetch_array($resultado)){
			$json['tienda']=$registro;
			echo json_encode($json);
		}
		mysqli_close($conexion);
		echo json_encode($datos);
	}
?>