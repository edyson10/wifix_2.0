<?php

include 'conexion.php';

$conexion=mysqli_connect($hostname_localhost,$username_localhost,$password_localhost,$database_localhost);

$nombre = $_REQUEST['nombre'];
$apellido = $_REQUEST['apellido'];
$cedula = $_REQUEST['cedula'];
$sexo = $_REQUEST['sexo'];
$telefono = $_REQUEST['telefono'];
$direccion = $_REQUEST['direccion'];
$correo = $_REQUEST['correo'];
$password = $_REQUEST['password'];
$repass = $_REQUEST['repass'];

$sql="INSERT INTO empleado (nombre,correo,contrasena) VALUES ('$nombre','$correo','$password')";
$resultado_insert=mysqli_query($conexion,$sql);

//NO SE LE OLVIDE VALIDAR SI LA CONTRASEÑA SE REPITE O NO 

if($resultado_insert){
		$consulta="SELECT * FROM usuario WHERE nombre = '{$nombre}'";
		$resultado=mysqli_query($conexion,$consulta);
		
		if($registro=mysqli_fetch_array($resultado)){
			$json['empleado']=$registro;
		}
		mysqli_close($conexion);
		echo json_encode($json);
	}
?>