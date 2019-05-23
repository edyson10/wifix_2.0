<?PHP

include 'conexion.php';

//revisar si es obligatorio o ya la trae la clase conectada
$conexion=mysqli_connect($hostname_localhost,$username_localhost,$password_localhost,$database_localhost);

$nombre = $_REQUEST['nombre'];
$apellido = $_REQUEST['apellido'];
$cedula = $_REQUEST['cedula'];
$sexo = $_REQUEST['sexo'];
$telefono = $_REQUEST['telefono'];
$direccion = $_REQUEST['direccion'];
$correo = $_REQUEST['correo'];
$password = $_REQUEST['contrasena'];
$repass = $_REQUEST['repass'];

if($password == $repass ){
	
	$sql="INSERT INTO empleado (id_tipoempleado, nombre,apellido,cedula,sexo,telefono,direccion,correo,password,estado) 
						VALUES (2,'$nombre','$apellido','$cedula','$sexo','$telefono','$direccion','$correo','$password',1)";
	$resultado_insert=mysqli_query($conexion,$sql);

	$json = array();
	if($resultado_insert){
			$consulta="SELECT * FROM empleado WHERE cedula = '{$cedula}'";
			$resultado=mysqli_query($conexion,$consulta);
			
			if($registro=mysqli_fetch_array($resultado)){
				$json['empleado']=$registro;
			}
			mysqli_close($conexion);
			echo json_encode($json);
		}else{
		echo json_encode($json);
	}
}else {
	echo "contrasena incorrecta";
}
?>