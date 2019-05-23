<?php
 
require_once('conexion.php');

$id_servicio = $_REQUEST['id_servicio'];
$fallaExpresada = $_REQUEST['fallaExpresada'];
$diagnostico = $_REQUEST['diagnostico'];
$observacion = $_REQUEST['observacion'];
$precioReparacion = $_REQUEST['precioReparacion'];
$clave = $_REQUEST['clave'];
$fechaEntrega = $_REQUEST['fechaEntrega'];

/*$imagen = $_POST['imagen'];
$path = "imagenes/$cedulaCliente.png";
$actualpath = "https://192.168.1.6/ServiciosWeb/$path";
*/	 

$sql = "UPDATE diagnostico SET falla = '$fallaExpresada', diagnostico = '$diagnostico', observacion = '$observacion', precio = '$precioReparacion', fechaEntrega = '$fechaEntrega', clave = '$clave' WHERE id_servicio = '$id_servicio'";

$resultado_insert=mysqli_query($conexion,$sql);

//VALIDAR CUANDO EL ARTICULO Y EL MODELO YA EXISTEN EN LA BD
if($resultado_insert){
	$consulta = "SELECT * FROM diagnostico WHERE id_servicio = '$id_servicio'";
	$resultado = mysqli_query($conexion,$consulta);

	$datos = array();
	if($registro  =mysqli_fetch_array($resultado)){
		$json[] = $registro;
		echo json_encode($datos);
	}
	mysqli_close($conexion);
};
