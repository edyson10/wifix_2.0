<?php 
include 'pdf.php';
include 'conexion.php';
$conexion=mysqli_connect($hostname_localhost,$username_localhost,$password_localhost,$database_localhost);

$fechaActual = $_REQUEST['fecha'];

$consulta = "SELECT venta.id_venta,empleado.nombre,venta.marca, venta.modelo,venta.precio,venta.cantidad 
from venta inner join empleado on empleado.cedula = venta.cedulaEmp  
where fecha_hora_venta LIKE '%$fechaActual%'";
$resultado= mysqli_query($conexion,$consulta);	
	
	//$resultado = $mysqli->query($query);
$pdf = new PDF();
$pdf->AliasNbPages();
$pdf->AddPage('portrait','LETTER');

$pdf->SetFont('times','B',12);
$pdf->SetY(0);
$pdf->text(35,20, 'Fecha: '.$fechaActual,0,0,'C');

$pdf->SetFont('times','B',16);
$pdf->SetY(35);
$pdf->text(95,30,'WIFIX',0,0,'C');
$pdf->SetFont('times','B',12);
$pdf->text(82,35,'Av. 4 Entrada 7 Local 1',0,0,'C');
$pdf->text(80,45,'REPORTE DE VENTAS',0,0,'C');
	
$pdf->SetFillColor(232,232,232);
$pdf->SetFont('Arial','B',8);
$pdf->SetY(50);
$pdf->SetX(11);
$pdf->Cell(15,6,'ID Venta',1,0,'C',1);
$pdf->Cell(23,6,'Empleado',1,0,'C',1);
$pdf->Cell(45,6,'Producto',1,0,'C',1);
$pdf->Cell(50,6,'Modelo',1,0,'C',1);
$pdf->Cell(15,6,'Cantidad',1,0,'C',1);
$pdf->Cell(25,6,'Precio unitario',1,0,'C',1);
$pdf->Cell(25,6,'Precio venta',1,1,'C',1);
//**cuantos datos valla a pedir**
//$row = mysqli_fetch_array($resultado);
//print_r($row);
 //die();
while($row = mysqli_fetch_array($resultado)){
	//print_r($row);
	$pdf->Cell(1);
	$pdf->Cell(15,6,$row['id_venta'],1,0,'C',1);
	$pdf->Cell(23,6,$row['nombre'],1,0,'C',1);
	$pdf->Cell(45,6,$row['marca'],1,0,'C');
	$pdf->Cell(50,6,$row['modelo'],1,0,'C');
	$pdf->Cell(15,6,$row['cantidad'] ,1,0,'C');
	$pdf->Cell(25,6,$row['precio'] ,1,0,'C');
	$pdf->Cell(25,6,$row['precio'],1,1,'C');
}
$consulta = "SELECT sum(venta.cantidad) as cantidad, sum(venta.precio) precioVenta FROM venta  where venta.fecha_hora_venta = '$fechaActual'";
$resultado= mysqli_query($conexion,$consulta);	
while($row = mysqli_fetch_array($resultado)){
	//print_r($row);
	$pdf->Cell(10);
	$pdf->Cell(10,6,'',0,0,'C');
    $pdf->Cell(54,6,'',0,0,'C');
	$pdf->Cell(60,6,'TOTAL',0,0,'C');
	$pdf->Cell(15,6,$row['cantidad'] ,1,0,'C');
	$pdf->Cell(25,6,$row['precioVenta'],1,0,'C');
	$pdf->Cell(25,6,$row['precioVenta'],1,1,'C');
}

/*
** ================ INCIO CODIGO =====================
** ================ SERVICIOS TECNICOS ======================
*/
// ================ DIAGNOSTICO ===============
$con = "SELECT * FROM diagnostico WHERE fecha_diagnostico LIKE '%$fechaActual%'";
$res= mysqli_query($conexion,$con); 

$pdf->AliasNbPages();
$pdf->AddPage('portrait','LETTER');

$pdf->SetFont('times','B',12);
$pdf->SetY(0);
$pdf->text(35,20, 'Fecha: '.$fechaActual,0,0,'C');

$pdf->SetFont('times','B',12);
$pdf->SetY(25);
$pdf->text(72,30,'REPORTES DE SERVICIOS TECNICOS',0,0,'C');
$pdf->text(80,40,'REPORTES DIAGNOSTICO',0,0,'C');

$pdf->SetFillColor(232,232,232);
$pdf->SetFont('Arial','B',8);
$pdf->SetY(50);
$pdf->SetX(15);
$pdf->Cell(17,6,'ID Servicio',1,0,'C',1);
$pdf->Cell(15,6,'Estado',1,0,'C',1);
$pdf->Cell(20,6,'Empleado',1,0,'C',1);
$pdf->Cell(25,6,'Marca',1,0,'C',1);
$pdf->Cell(25,6,'Modelo ',1,0,'C',1);
$pdf->Cell(60,6,'Falla',1,0,'C',1);
$pdf->Cell(20,6,'Precio',1,1,'C',1);
//**cuantos datos valla a pedir**
while($row = $res->fetch_assoc()){
	$pdf->Cell(5);
	$pdf->Cell(17,6,$row['id_servicio'],1,0,'C',1);
	$pdf->Cell(15,6,$row['estado'],1,0,'C');
	$pdf->Cell(20,6,$row['cedulaEmp'],1,0,'C');
	$pdf->Cell(25,6,$row['marca'],1,0,'C');
	$pdf->Cell(25,6,$row['modelo'] ,1,0,'C');
	$pdf->Cell(60,6,$row['falla'],1,0,'C');
	$pdf->Cell(20,6,$row['precio'],1,1,'C');
}
$consulta = "SELECT sum(precio) precioVenta FROM diagnostico WHERE fecha_diagnostico LIKE '%$fechaActual%'";
$resultado= mysqli_query($conexion,$consulta);	
while($row = mysqli_fetch_array($resultado)){
	//print_r($row);
	$pdf->Cell(20);
	$pdf->Cell(25,6,'',0,0,'C');
	$pdf->Cell(25,6,'',0,0,'C');
	$pdf->Cell(47,6,'',0,0,'C');
	$pdf->Cell(50,6,'TOTAL',0,0,'C');
	$pdf->Cell(20,6,$row['precioVenta'],1,1,'C');
}

// ================ REPARACIÓN ===============
$con = "SELECT * FROM reparacion WHERE fecha_reparacion LIKE '%$fechaActual%'";
$res= mysqli_query($conexion,$con); 

$pdf->AliasNbPages();
$pdf->AddPage('portrait','LETTER');

$pdf->SetFont('times','B',12);
$pdf->SetY(0);
$pdf->text(35,20, 'Fecha: '.$fechaActual,0,0,'C');

$pdf->SetFont('times','B',12);
$pdf->SetY(25);
$pdf->text(80,30,'REPORTES REPARACION',0,0,'C');

$pdf->SetFillColor(232,232,232);
$pdf->SetFont('Arial','B',8);
$pdf->SetY(40);
$pdf->SetX(15);
$pdf->Cell(20,6,'ID Reparacion',1,0,'C',1);
$pdf->Cell(17,6,'ID Servicio',1,0,'C',1);
$pdf->Cell(20,6,'Estado',1,0,'C',1);
$pdf->Cell(20,6,'Bodega',1,0,'C',1);
$pdf->Cell(25,6,'Repuesto',1,0,'C',1);
$pdf->Cell(40,6,'Detalle',1,0,'C',1);
$pdf->Cell(20,6,'Costo',1,0,'C',1);
$pdf->Cell(20,6,'Precio',1,1,'C',1);


//**cuantos datos valla a pedir**
while($row = $res->fetch_assoc()){
	$pdf->Cell(5);
	$pdf->Cell(20,6,$row['id_reparacion'],1,0,'C',1);
	$pdf->Cell(17,6,$row['id_servicio'],1,0,'C');
	$pdf->Cell(20,6,$row['estado'],1,0,'C');
	$pdf->Cell(20,6,$row['bodega'],1,0,'C');
	$pdf->Cell(25,6,$row['repuesto'],1,0,'C');
	$pdf->Cell(40,6,$row['detalle_reparacion'] ,1,0,'C');
	$pdf->Cell(20,6,$row['costo_reparacion'],1,0,'C');
	$pdf->Cell(20,6,$row['precio_reparacion'],1,1,'C');
}

//SUMA DE LOS COSTOS Y PRECIOS DE RE LOS REPUESTOS
$consulta = "SELECT sum(costo_reparacion) costoReparacion, sum(precio_reparacion) precioReparacion  
			FROM reparacion WHERE fecha_reparacion LIKE '%$fechaActual%'";
$resultado= mysqli_query($conexion,$consulta);	
while($row = mysqli_fetch_array($resultado)){
	//print_r($row);
	$pdf->Cell(20);
	$pdf->Cell(25,6,'',0,0,'C');
	$pdf->Cell(45,6,'',0,0,'C');
	$pdf->Cell(57,6,'TOTAL',0,0,'C');
	$pdf->Cell(20,6,$row['costoReparacion'],1,0,'C');
	$pdf->Cell(20,6,$row['precioReparacion'],1,1,'C');
}

//CONSULTA PARA CALCULAR LA UTILIDAD DE LAS REPARACIONES
$consulta = "SELECT (sum(precio_reparacion - costo_reparacion) div 2) AS Utilidad FROM reparacion WHERE fecha_reparacion LIKE '%$fechaActual%'";
$resultado= mysqli_query($conexion,$consulta);	
while($row = mysqli_fetch_array($resultado)){
	//print_r($row);
	$pdf->Cell(20);
	$pdf->Cell(25,6,'',0,0,'C');
	$pdf->Cell(55,6,'',0,0,'C');
	$pdf->Cell(67,6,'UTILIDAD',0,0,'C');
	$pdf->Cell(20,6,$row['Utilidad'],1,1,'C');
}

/*
** ================ FIN CODIGO ===================== 
*/

// GASTOS----------------------------------------------------------------
//_-----------------------------------------------------------------------------------_
$con = "SELECT id_baja ,cedulaEmp,tipo_baja,descripcion,precio,fecha_baja FROM baja where tipo_baja='Gasto' and fecha_baja='$fechaActual'";
$res= mysqli_query($conexion,$con); 
$pdf->AliasNbPages();
$pdf->AddPage('portrait','LETTER');

$pdf->SetFont('times','B',12);
$pdf->SetY(0);
$pdf->text(35,20, 'Fecha: '.$fechaActual,0,0,'C');

$pdf->SetFont('times','B',12);
$pdf->SetY(25);
$pdf->text(72,30,'REPORTE DE GASTOS',0,0,'C');

$pdf->SetFillColor(232,232,232);
$pdf->SetFont('Arial','B',8);
$pdf->SetY(40);
$pdf->SetX(0);
$pdf->Cell(15,6,'',0,0,'C');
$pdf->Cell(15,6,'ID Baja',1,0,'C',1);
$pdf->Cell(25,6,'Empleado',1,0,'C',1);
$pdf->Cell(50,6,'Tipo Baja',1,0,'C',1);
$pdf->Cell(45,6,'Descripcion ',1,0,'C',1);
$pdf->Cell(25,6,'Fecha',1,0,'C',1);
$pdf->Cell(25,6,'Precio',1,1,'C',1);
//**cuantos datos valla a pedir**
while($row = $res->fetch_assoc()){
	$pdf->Cell(-9);
	$pdf->Cell(14,6,'',0,0,'C');
	$pdf->Cell(15,6,$row['id_baja'],1,0,'C',1);
	$pdf->Cell(25,6,$row['cedulaEmp'],1,0,'C');
	$pdf->Cell(50,6,$row['tipo_baja'],1,0,'C');
	$pdf->Cell(45,6,$row['descripcion'] ,1,0,'C');
	$pdf->Cell(25,6,$row['fecha_baja'],1,0,'C');
	$pdf->Cell(25,6,$row['precio'],1,1,'C');
}

$consulta = "SELECT sum(baja.precio) as precio FROM baja where tipo_baja='Gasto' and fecha_baja='$fechaActual'";
$resultado= mysqli_query($conexion,$consulta);	
while($row = mysqli_fetch_array($resultado)){
	//print_r($row);
	$pdf->Cell(20);
	$pdf->Cell(25,6,'',0,0,'C');
	$pdf->Cell(25,6,'',0,0,'C');
	$pdf->Cell(25,6,'',0,0,'C');
	$pdf->Cell(35,6,'',0,0,'C');
	$pdf->Cell(35,6,'TOTAL',0,0,'C');
	$pdf->Cell(25,6,$row['precio'],1,1,'C');
}	

// COSTOSSSS-------------------------------------------------------------------
//_-----------------------------------------------------------------------------------_
$con = "SELECT id_baja ,cedulaEmp,tipo_baja,descripcion,precio,fecha_baja FROM baja where tipo_baja='Costo' and fecha_baja='$fechaActual'";
$res= mysqli_query($conexion,$con); 
$pdf->AliasNbPages();
$pdf->AddPage('portrait','LETTER');

$pdf->SetFont('times','B',12);
$pdf->SetY(0);
$pdf->text(35,20, 'Fecha: '.$fechaActual,0,0,'C');

$pdf->SetFont('times','B',12);
$pdf->SetY(25);
$pdf->text(70,30,'REPORTE DE COSTOS',0,0,'C');

$pdf->SetFillColor(232,232,232);
$pdf->SetFont('Arial','B',8);
$pdf->SetY(40);
$pdf->SetX(0);
$pdf->Cell(15,6,'',0,0,'C');
$pdf->Cell(15,6,'ID Baja',1,0,'C',1);
$pdf->Cell(25,6,'Empleado',1,0,'C',1);
$pdf->Cell(50,6,'Tipo Baja',1,0,'C',1);
$pdf->Cell(45,6,'Descripcion ',1,0,'C',1);
$pdf->Cell(25,6,'Fecha',1,0,'C',1);
$pdf->Cell(25,6,'Precio',1,1,'C',1);
//**cuantos datos valla a pedir**
while($row = $res->fetch_assoc()){
	$pdf->Cell(-9);
	$pdf->Cell(14,6,'',0,0,'C');
	$pdf->Cell(15,6,$row['id_baja'],1,0,'C',1);
	$pdf->Cell(25,6,$row['cedulaEmp'],1,0,'C');
	$pdf->Cell(50,6,$row['tipo_baja'],1,0,'C');
	$pdf->Cell(45,6,$row['descripcion'] ,1,0,'C');
	$pdf->Cell(25,6,$row['fecha_baja'],1,0,'C');
	$pdf->Cell(25,6,$row['precio'],1,1,'C');
}

$consulta = "SELECT sum(baja.precio) as precio FROM baja where tipo_baja='Costo' and fecha_baja='$fechaActual'";
$resultado= mysqli_query($conexion,$consulta);	
while($row = mysqli_fetch_array($resultado)){
	//print_r($row);	
	$pdf->Cell(5);
	$pdf->Cell(25,6,'',0,0,'C');
	$pdf->Cell(25,6,'',0,0,'C');
	$pdf->Cell(25,6,'',0,0,'C');
	$pdf->Cell(45,6,'',0,0,'C');
	$pdf->Cell(40,6,'TOTAL',0,0,'C');
	$pdf->Cell(25,6,$row['precio'],1,1,'C');
}
	
/*
$con = "SELECT sum('cantidad') FROM detalleventa";
$resp= mysqli_query($conexion,$con);
$pdf->SetFillColor(232,232,232);
$pdf->SetFont('Arial','B',12);
$pdf->Cell(120);
$pdf->Cell(50,6,$row['cantidad'],1,0,'C');
*/
$pdf->Output();  // con d se descarga automaticamente 
	// con f se guarda directamente en el disco pero se tiene q poner nombre
	  //$pdf->Output('F','REPORTE.PDF');
		
?>