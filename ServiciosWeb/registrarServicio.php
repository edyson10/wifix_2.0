<?php
 
require_once('conexion.php');

$cedulaEmp = $_REQUEST['empleado'];
$marca = $_REQUEST['marca'];
$modelo = $_REQUEST['modelo'];
$fallaExpresada = $_REQUEST['fallaExpresada'];
$diagnostico = $_REQUEST['diagnostico'];
$observacion = $_REQUEST['observacion'];
$precioReparacion = $_REQUEST['precioReparacion'];
$costoReparacion = $_REQUEST['costoReparacion'];
$cedulaCliente = $_REQUEST['cedulaCli'];
$fechaEntrega = $_REQUEST['fechaEntrega'];
$clave = $_REQUEST['clave'];

/*$imagen = $_POST['imagen'];
$path = "imagenes/$cedulaCliente.png";
	 
$actualpath = "https://192.168.1.6/ServiciosWeb/$path";
*/	 
$sql = "INSERT INTO servicio (id_servicio, fechaServicio, cedulaEmp, marca, modelo, falla, descripcion, 
observacion, precio, costo, cedulaCli, fechaEntrega, clave, estado) 
VALUES (NULL, CURDATE(), '$cedulaEmp', '$marca', '$modelo', '$fallaExpresada', '$diagnostico', '$observacion', '$precioReparacion',
 '$costoReparacion', '$cedulaCliente','$fechaEntrega', '$clave', 1)";
$resultado_insert=mysqli_query($conexion,$sql);

//VALIDAR CUANDO EL ARTICULO Y EL MODELO YA EXISTEN EN LA BD
if($resultado_insert){
	$consulta="SELECT * FROM servicio ORDER BY id_servicio DESC LIMIT 1";
	$resultado=mysqli_query($conexion,$consulta);

	$datos = array();
	if($registro=mysqli_fetch_array($resultado)){
		$json[]=$registro;
		echo json_encode($json);
	}
	mysqli_close($conexion);
	echo json_encode($json);
}
