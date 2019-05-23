<?php

include 'conexion.php';

$correo= $_REQUEST['correo'];
$nuevaContrasena = $_REQUEST['contrasena'];
$repiContrasena = $_REQUEST['repiContrasena'];

$sql="UPDATE empleado SET contrasena = '$nuevaContrasena' WHERE correo = '$correo'";
$resultado_insert=mysqli_query($conexion,$sql);

if($resultado_insert){
		$consulta="SELECT * FROM empleado WHERE correo = '{$correo}'";
		$resultado=mysqli_query($conexion,$consulta);

		$datos = array();
		if($registro=mysqli_fetch_array($resultado)){
			$json['empleado']=$registro;
			echo json_encode($json);
		}
		mysqli_close($conexion);
		echo json_encode($datos);
	}

?>