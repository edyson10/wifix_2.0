<?php

require_once('conexion.php');

$conexion=mysqli_connect($hostname_localhost,$username_localhost,$password_localhost,$database_localhost);

$cedulaEmp = $_POST['empleado'];
$marca = $_POST['marca'];
$modelo = $_POST['modelo'];
$fallaExpresada = $_POST['fallaExpresada'];
$diagnostico = $_POST['diagnostico'];
$observacion = $_POST['observacion'];
$clave = $_POST['clave'];
$costoReparacion = $_POST['costoReparacion'];
$fechaEntrega = $_POST['fechaEntrega'];
$cedulaCliente = $_POST['cedulaCli'];

$imagen = $_POST['imagen'];
$path = "imagenes/$marca.jpg";
$url = "http://$hostname_localhost/ServiciosWeb/$path";

file_put_contents($path, base64_encode($imagen));
$bytesArchivo = file_get_contents($path);

//$url_i = "imagenes/$cedulaCliente.jpg";

//$im = file_get_contents('filename.jpg');
//$imdata = base64_encode($im);

$sql = "INSERT INTO servicio (fechaServicio, cedulaEmp, marca, modelo, falla, descripcion, observacion, precio, cedulaCli, fechaEntrega, clave, estado, foto) VALUES (CURDATE(), '$cedulaEmp', '$marca', '$modelo', '$fallaExpresada', '$diagnostico', '$observacion','$costoReparacion', '$cedulaCliente','$fechaEntrega','$clave', 1, $bytesArchivo)";
$resultado_insert=mysqli_query($conexion,$sql);
if($resultado_insert){
	echo "Registra";
}else{
	echo "No registra";
}
?>