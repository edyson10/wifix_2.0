<?php

include 'conexion.php';

$diagnostico = $_REQUEST['servicio'];

$sql = "DELETE FROM diagnostico WHERE id_servicio = '$diagnostico'";
$resultado_insert=mysqli_query($conexion,$sql);
if($resultado_insert){
	echo "Eliminado";
}else{
	echo "No eliminado";
}

?>