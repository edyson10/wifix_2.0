<?php
 
require_once('conexion.php');

$nombre = $_REQUEST['nombre'];
$tienda = $_REQUEST['tienda'];
	 
$sql = "INSERT INTO `bodega` (`id_bodega`, `nombre`, `id_tienda`) VALUES (NULL, '$nombre', '$tienda');";
$resultado_insert=mysqli_query($conexion,$sql);

//VALIDAR CUANDO EL ARTICULO Y EL MODELO YA EXISTEN EN LA BD
if($resultado_insert){
	$consulta="SELECT * FROM bodega ORDER BY id_bodega DESC LIMIT 1";
	$resultado=mysqli_query($conexion,$consulta);

	$datos = array();
	if($registro=mysqli_fetch_array($resultado)){
		$json[]=$registro;
		echo json_encode($json);
	}
	mysqli_close($conexion);
	echo json_encode($json);
}

?>