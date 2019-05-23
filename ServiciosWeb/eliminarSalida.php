<?php

include 'conexion.php';

$salida = $_REQUEST['salida'];

$sql = "DELETE FROM baja WHERE id_baja = '$salida'";
$resultado_insert=mysqli_query($conexion,$sql);
if($resultado_insert){
	echo "Eliminado";
}else{
	echo "No eliminado";
}

?>