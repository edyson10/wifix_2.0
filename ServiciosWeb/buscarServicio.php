<?php

include 'conexion.php';

$json = array();

if(isset($_GET["servicio"])){
	$cedula = $_GET["servicio"];
	$conexion=mysqli_connect($hostname_localhost,$username_localhost,$password_localhost,$database_localhost);

	$consulta = "SELECT * FROM diagnostico WHERE cedulaCli = '$cedula' OR cedulaEmp = '$cedula' OR id_servicio = '$cedula' ORDER BY id_servicio";
	$resultado = mysqli_query($conexion,$consulta);

	while($registro = mysqli_fetch_array($resultado)) {
		$result["id_servicio"] = $registro['id_servicio'];
		$result["fecha_diagnostico"] = $registro['fecha_diagnostico'];
		$result["cedulaEmp"] = $registro['cedulaEmp'];
		$result["marca"] = $registro['marca'];
		$result["modelo"] = $registro['modelo'];
		$result["falla"] = $registro['falla'];
		$result["diagnostico"] = $registro['diagnostico'];
		$result["observacion"] = $registro['observacion'];
		$result["precio"] = $registro['precio'];
		$result["cedulaCli"] = $registro['cedulaCli'];
		$result["fechaEntrega"] = $registro['fechaEntrega'];
		$result["clave"] = $registro['clave'];
		$result["estado"] = $registro['estado'];
		//$result["foto"] = base64_encode($registro['foto']);
		$json[]=$result;
	}
	mysqli_close($conexion);
	echo json_encode($json);
}
?>