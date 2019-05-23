<?php

include 'conexion.php';

$cedula = $_REQUEST['cedula'];
$contrasena = $_REQUEST['pass'];
$nuevaContrasena = $_REQUEST['password'];
$repiContrasena = $_REQUEST['repass'];

$sql="UPDATE empleado SET password = '$nuevaContrasena' WHERE cedula = '$cedula' AND password = '$contrasena'";
$resultado_insert=mysqli_query($conexion,$sql);

if($resultado_insert){
		$consulta="SELECT * FROM empleado WHERE cedula = '{$cedula}' AND password = '$nuevaContrasena'";
		$resultado=mysqli_query($conexion,$consulta);

		$datos = array();
		if($registro=mysqli_fetch_array($resultado)){
			$json['empleado']=$registro;
			echo json_encode($json);
		}
		mysqli_close($conexion);
		//echo "-->";
		//echo json_encode($datos);
	}
	echo json_encode($datos);
?>