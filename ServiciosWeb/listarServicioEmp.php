<?php

$hostname_localhost="localhost";
$database_localhost="wifix";
$username_localhost="root";
$password_localhost="";

$json = array();

$empleado = $_REQUEST['empleado'];

$conexion=mysqli_connect($hostname_localhost,$username_localhost,$password_localhost,$database_localhost);
$consulta = "SELECT * from servicio where cedulaEmp = '$empleado' OR cedulaCli = '$empleado'";
$resultado = mysqli_query($conexion,$consulta);

	while($registro = mysqli_fetch_array($resultado)) {
		//$result["id_servicio"] = $registro['id_servicio'];
		$result["fechaServicio"] = $registro['fechaServicio'];
		//$result["cedulaEmp"] = $registro['cedulaEmp'];
		$result["marca"] = $registro['marca'];
		$result["modelo"] = $registro['modelo'];
		$result["falla"] = $registro['falla'];
		//$result["descripcion"] = $registro['descripcion'];
		//$result["observacion"] = $registro['observacion'];
		//$result["precio"] = $registro['precio'];
		$result["cedulaCli"] = $registro['cedulaCli'];
		//$result["clave"] = $registro['clave'];
		//$result["estado"] = $registro['estado'];
		//$result["fechaEntrega"] = $registro['fechaEntrega'];
		//$result["foto"] = base64_encode($registro['foto']);
		$json[]=$result;
	}
	mysqli_close($conexion);
	echo json_encode($json);

?>