<?php

include 'conexion.php';

$conexion = mysqli_connect($hostname_localhost,$username_localhost,$password_localhost,$database_localhost);

$id_empleado = $_REQUEST['empleado'];
$fechaRevision = $_REQUEST['fechaServicio'];
$marca = $_REQUEST['marca'];
$modelo = $_REQUEST['modelo'];
$fallaExpresada = $_REQUEST['fallaExpresada'];
$diagnostico = $_REQUEST['diagnostico'];
$observacion = $_REQUEST['observacion'];
$clave = $_REQUEST['clave'];
$costoReparacion = $_REQUEST['costoReparacion'];
$fechaEntrega = $_REQUEST['fechaEntrega'];
$cedulaCliente = $_REQUEST['cedulaCli'];
$foto = $_REQUEST['foto'];

$sql="INSERT INTO servicio (id_empleado, marca, modelo, falla, descripcion, observacion,fecha_servicio, precio, foto, cedulaCli, fechaEntrega, clave, estado) VALUES ('$id_empleado', '$marca', '$modelo', '$fallaExpresada', '$diagnostico', 'observacion', '$fechaRevision', '$costoReparacion', '$foto', '$cedulaCliente','$fechaEntrega','$clave', 1)";
$resultado_insert=mysqli_query($conexion,$sql);

$json = array();
if($resultado_insert){
		$consulta="SELECT * FROM servicio ORDER by id_servicio DESC LIMIT 1";
		$resultado=mysqli_query($conexion,$consulta);
		
		if($registro=mysqli_fetch_array($resultado)){
			$json['servicio']=$registro;
		}
		mysqli_close($conexion);
		echo json_encode($json);
	}else{
		echo json_encode($json);
	}
?>