<?php

//$conexion = new PDO("mysql:host=localhost;dbname=wifix", "root","");
$conexion = new PDO("mysql:host=18.228.235.94;dbname=proyecto", "edynson","Edynson123@");


//$res= $conexion->query("SELECT * FROM empleado WHERE estado=1");
$res = $conexion->query("SELECT e.nombre, e.apellido, e.cedula, ((sum(precio+cantidad) div 2)div 2) as Utilidad FROM empleado e 
	INNER JOIN venta v ON e.cedula = v.cedulaEmp  WHERE estado = 1 and fecha_hora_venta >= '2019-03-17' and fecha_hora_venta <= '2019-03-24' 
	GROUP BY e.nombre, e.apellido, e.cedula  ORDER BY Utilidad DESC");
$datos = array();

foreach($res as $row){
    $datos[]=$row;
}

echo json_encode($datos);

?>