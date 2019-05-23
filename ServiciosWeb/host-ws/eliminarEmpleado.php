<?php

include 'conexion.php';

$cedula = $_REQUEST['cedula'];

//MODIFICAR EL DELETE SE DEBE DE ELIMINAR EL EMPLEADO, PERO MANTENER LAS VENTAS QUE REALIZO
$sql="UPDATE empleado SET estado = 0 WHERE cedula = '$cedula'";
$resultado_insert=mysqli_query($conexion,$sql);

if($resultado_insert){
		$consulta="SELECT * FROM empleado WHERE estado = 1 GROUP BY nombre ";
		$resultado=mysqli_query($conexion,$consulta);

		$datos = array();
		if($registro=mysqli_fetch_array($resultado)){
			$json['empleado']=$registro;
			echo json_encode($json);
		}
		mysqli_close($conexion);
	}
?>