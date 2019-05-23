<?php 
include 'pdf.php';
include 'conexion.php';
$conexion=mysqli_connect($hostname_localhost,$username_localhost,$password_localhost,$database_localhost);
$mes = 02;
$anio = 2019;
$mesActual = 'Marzo';
//$consulta = "SELECT venta.id_venta,venta.marca,venta.modelo,sum(venta.cantidad) as cantidad,
//sum(venta.precio) as precioVenta 
//FROM venta where MONTH(venta.fecha_hora_venta) = '$mes' AND YEAR(venta.fecha_hora_venta) = '$anio'
//group by venta.id_venta,venta.marca,venta.modelo";
//$consulta = "SELECT venta.id_venta,venta.marca,venta.modelo,sum(venta.cantidad) as cantidad,
//sum(venta.precio) as precioVenta 
//FROM venta group by venta.id_venta,venta.marca,venta.modelo order by venta.marca";

$consulta = "SELECT marca, modelo, sum(cantidad) AS cantidad, sum(precio) AS precioVen FROM venta 
GROUP BY marca, modelo, cantidad ORDER BY marca, modelo";
$resultado = mysqli_query($conexion,$consulta);	
	
//$resultado = $mysqli->query($query);
$pdf = new PDF();
$pdf->AliasNbPages();
$pdf->AddPage('portrait','LETTER');

$pdf->SetFont('times','B',12);
$pdf->SetY(0);
$pdf->text(35,20, 'Mes: '.$mesActual,0,0,'C');

$pdf->SetFont('times','B',16);
$pdf->SetY(25);
$pdf->text(95,25,'WIFIX',0,0,'C');
$pdf->SetFont('times','B',13);
$pdf->text(82,30,'Av. 4 Entrada 7 Local 1',0,0,'C');

$pdf->text(80,40,'REPORTE DE VENTAS',0,0,'C');
$pdf->SetFillColor(232,232,232);
$pdf->SetFont('Arial','B',8);
$pdf->SetY(50);
$pdf->SetX(6);
$pdf->Cell(25,6,'',0,0,'C');
//$pdf->Cell(15,6,'ID Venta',1,0,'C',1);
$pdf->Cell(45,6,'Producto',1,0,'C',1);
$pdf->Cell(60,6,'Modelo',1,0,'C',1);
$pdf->Cell(15,6,'Cantidad',1,0,'C',1);
$pdf->Cell(25,6,'Precio venta',1,1,'C',1);
//**cuantos datos valla a pedir**
//$row = mysqli_fetch_array($resultado);
//print_r($row);
 //die();
while($row = mysqli_fetch_array($resultado)){
	//print_r($row);
	$pdf->Cell(20);
	$pdf->Cell(1,6,'',0,0,'C');
	//$pdf->Cell(15,6,$row['id_venta'],1,0,'C',1);
	$pdf->Cell(45,6,$row['marca'],1,0,'C');
	$pdf->Cell(60,6,$row['modelo'],1,0,'C');
	$pdf->Cell(15,6,$row['cantidad'] ,1,0,'C');
	$pdf->Cell(25,6,$row['precioVen'],1,1,'C');
}
//$consulta="SELECT sum(venta.cantidad) as cantidad, sum(venta.precio) precioVenta FROM venta where MONTH(venta.fecha_hora_venta) = '$mes' AND YEAR(venta.fecha_hora_venta) = '$anio' ";
$consulta = "SELECT sum(cantidad) as cantidad, sum(precio) as precio FROM venta ";
$resultado= mysqli_query($conexion,$consulta);	
while($row = mysqli_fetch_array($resultado)){
	//print_r($row);
	$pdf->Cell(15);
	$pdf->Cell(10,6,'',0,0,'C');
    $pdf->Cell(50,6,'',0,0,'C');
	$pdf->Cell(51,6,'TOTAL',0,0,'C');
	$pdf->Cell(15,6,$row['cantidad'] ,1,0,'C');
	$pdf->Cell(25,6,$row['precio'],1,1,'C');
}
	
//_------------------------------------------------------------SERVICIOS-----------------------
$con = "SELECT id_servicio,estado,cedulaEmp,marca,modelo,falla,precio FROM diagnostico where MONTH(fecha_diagnostico) = '$mes' AND YEAR(fecha_diagnostico) = '$anio'";
$res= mysqli_query($conexion,$con); 
$pdf->AliasNbPages();
$pdf->AddPage('portrait','LETTER');

$pdf->SetFont('times','B',12);
$pdf->SetY(0);
$pdf->text(35,20, 'Mes: '.$mesActual,0,0,'C');

$pdf->SetFont('times','B',12);
$pdf->SetY(25);
$pdf->text(72,30,'REPORTES DE SERVICIOS',0,0,'C');

$pdf->SetFillColor(232,232,232);
$pdf->SetFont('Arial','B',8);
$pdf->SetY(40);
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
$consulta = "SELECT sum(precio) precioVenta FROM diagnostico where MONTH(fecha_diagnostico) = '$mes' AND YEAR(fecha_diagnostico) = '$anio'";
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
// GASTOS----------------------------------------------------------------
//_-----------------------------------------------------------------------------------_
$con = "SELECT id_baja ,cedulaEmp,tipo_baja,descripcion,precio,fecha_baja FROM baja where tipo_baja='Gasto' and MONTH(fecha_baja) = '$mes' AND YEAR(fecha_baja) = '$anio'";
$res= mysqli_query($conexion,$con); 
$pdf->AliasNbPages();
$pdf->AddPage('portrait','LETTER');

$pdf->SetFont('times','B',12);
$pdf->SetY(0);
$pdf->text(35,20, 'Mes: '.$mesActual,0,0,'C');
$pdf->SetFont('times','B',12);
$pdf->SetY(25);
$pdf->text(72,30,'REPORTES DE GASTOS',0,0,'C');

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
$consulta = "SELECT sum(baja.precio) as precio FROM baja where tipo_baja='Gasto'  and MONTH(fecha_baja) = '$mes' AND YEAR(fecha_baja) = '$anio'";
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
$con = "SELECT id_baja ,cedulaEmp,tipo_baja,descripcion,precio,fecha_baja FROM baja where tipo_baja='Costo' and MONTH(fecha_baja) = '$mes' AND YEAR(fecha_baja) = '$anio'";
$res= mysqli_query($conexion,$con); 
$pdf->AliasNbPages();
$pdf->AddPage('portrait','LETTER');

$pdf->SetFont('times','B',12);
$pdf->SetY(0);
$pdf->text(35,20, 'Mes: '.$mesActual,0,0,'C');
$pdf->SetFont('times','B',12);
$pdf->SetY(25);
$pdf->text(72,30,'REPORTES DE COSTOS',0,0,'C');

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
$consulta = "SELECT sum(baja.precio) as precio FROM baja where tipo_baja='Costo' and MONTH(fecha_baja) = '$mes' AND YEAR(fecha_baja) = '$anio'";
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
//$pdf->Output('d','REPORTE_MES.PDF');
?>