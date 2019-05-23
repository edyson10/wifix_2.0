<?php

require_once ('conexion.php');
/*
$hostname_localhost="localhost";
$database_localhost="wifix";
$username_localhost="root";
$password_localhost="";
*/

$json = array();

$articulo = $_REQUEST['articulo'];

$conexion=mysqli_connect($hostname_localhost,$username_localhost,$password_localhost,$database_localhost);
$consulta = "SELECT modelo FROM producto WHERE articulo = '$articulo' ORDER BY modelo";
$resultado = mysqli_query($conexion,$consulta);

	while($registro = mysqli_fetch_array($resultado)) {
		//$result["id_servicio"] = $registro['id_servicio'];
		$result["modelo"] = $registro['modelo'];
		$json[]=$result;
	}
	mysqli_close($conexion);
	echo json_encode($json);

?>