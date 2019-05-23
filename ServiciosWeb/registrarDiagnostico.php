<?php
 
$conexion = new PDO("mysql:host=localhost;dbname=wifix", "root","");
//$conexion = new PDO("mysql:host=18.228.235.94;dbname=proyecto", "edynson","Edynson123@");

$cedulaEmp = $_GET['empleado'];
$marca = $_REQUEST['marca'];
$modelo = $_REQUEST['modelo'];
$fallaExpresada = $_REQUEST['fallaExpresada'];
$diagnostico = $_REQUEST['diagnostico'];
$observacion = $_REQUEST['observacion'];
$precioDiagnostico = $_REQUEST['precio'];
$cedulaCliente = $_REQUEST['cedulaCli'];
$fechaEntrega = $_REQUEST['fechaEntrega'];
$clave = $_REQUEST['clave'];

/*$imagen = $_POST['imagen'];
$path = "imagenes/$cedulaCliente.png";
	 
$actualpath = "https://192.168.1.6/ServiciosWeb/$path";
*/	 

$res = $conexion->query("INSERT INTO diagnostico (id_servicio, fecha_diagnostico, cedulaEmp, marca, modelo, falla, diagnostico, observacion, precio, cedulaCli, fechaEntrega, clave, estado) VALUES (NULL, DATE_SUB(current_timestamp(), INTERVAL 5 HOUR), '$cedulaEmp', '$marca', '$modelo', '$fallaExpresada', '$diagnostico', '$observacion', '$precioDiagnostico', '$cedulaCliente','$fechaEntrega', '$clave', 1)");

$con=$conexion->query("SELECT * FROM diagnostico ORDER by id_servicio DESC LIMIT 1");
$datos = array();

foreach($con as $row){
    $datos[]=$row;
}

echo json_encode($datos);

?>
