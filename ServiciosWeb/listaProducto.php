<?php 

	include 'pdf.php';
	include 'conexion.php';
	
	$conexion=mysqli_connect($hostname_localhost,$username_localhost,$password_localhost,$database_localhost);
	$consulta = "SELECT id_producto, articulo,modelo,cantidad FROM producto where articulo = 'Accesorios' order by cantidad";
	$resultado= mysqli_query($conexion,$consulta);	
	
	//$resultado = $mysqli->query($query);
	$pdf = new PDF();
	$pdf->AliasNbPages();
	$pdf->AddPage('portrait','LETTER');

	$pdf->SetFont('times','B',16);
	$pdf->SetY(20);
	$pdf->text(95,25,'WIFIX',0,0,'C');
	$pdf->SetFont('times','B',12);
	$pdf->text(82,30,'Av. 4 Entrada 7 Local 1',0,0,'C');

	$pdf->SetFont('times','B',12);
	$pdf->SetY(40);
	$pdf->text(81,40,'LISTA DE PRODUCTO',0,0,'C');

	//========== LISTA DE LOS PRODUCTOS ACCESORIOS
	$pdf->SetFillColor(232,232,232);
	$pdf->SetFont('Arial','B',8);
	$pdf->SetY(45);
	$pdf->SetX(20);
	$pdf->Cell(170,6,'ACCESORIOS',1,1,'C',1);
	$pdf->SetX(20);
	$pdf->Cell(20,6,'ID Producto',1,0,'C',1);
	$pdf->Cell(55,6,'Articulo',1,0,'C',1);
	$pdf->Cell(68,6,'Modelo',1,0,'C',1);
	$pdf->Cell(27,6,'Cantidad',1,1,'C',1);
	
	while($row = mysqli_fetch_array($resultado)){
		$pdf->Cell(10);
		$pdf->Cell(20,6,$row['id_producto'],1,0,'C');
		$pdf->Cell(55,6,$row['articulo'],1,0,'C');
		$pdf->Cell(68,6,$row['modelo'],1,0,'C');
		$pdf->Cell(27,6,$row['cantidad'],1,1,'C');
	}

	//========== LISTA DE LOS PRODUCTOS ACUARIOS
	$con1 = "SELECT id_producto,articulo,modelo,cantidad FROM producto where articulo = 'Acuario' order by cantidad";
	$resul1= mysqli_query($conexion,$con1);	

	$pdf->AliasNbPages();
	$pdf->AddPage('portrait','LETTER');

	$pdf->SetFillColor(232,232,232);
	$pdf->SetFont('Arial','B',8);
	$pdf->SetY(20);
	$pdf->SetX(20);
	$pdf->Cell(170,6,'ACUARIO',1,1,'C',1);
	$pdf->SetX(20);
	$pdf->Cell(20,6,'ID Producto',1,0,'C',1);
	$pdf->Cell(55,6,'Articulo',1,0,'C',1);
	$pdf->Cell(68,6,'Modelo',1,0,'C',1);
	$pdf->Cell(27,6,'Cantidad',1,1,'C',1);
	
	while($row = mysqli_fetch_array($resul1)){
		$pdf->Cell(10);
		$pdf->Cell(20,6,$row['id_producto'],1,0,'C');
		$pdf->Cell(55,6,$row['articulo'],1,0,'C');
		$pdf->Cell(68,6,$row['modelo'],1,0,'C');
		$pdf->Cell(27,6,$row['cantidad'],1,1,'C');
	}

	//========== LISTA DE LOS PRODUCTOS AGENDA
	$con2 = "SELECT id_producto,articulo,modelo,cantidad FROM producto where articulo = 'Agenda' order by cantidad";
	$resul2= mysqli_query($conexion,$con2);

	$pdf->AliasNbPages();
	$pdf->AddPage('portrait','LETTER');

	$pdf->SetFillColor(232,232,232);
	$pdf->SetFont('Arial','B',8);
	$pdf->SetY(20);
	$pdf->SetX(20);
	$pdf->Cell(170,6,'AGENDA',1,1,'C',1);
	$pdf->SetX(20);
	$pdf->Cell(20,6,'ID Producto',1,0,'C',1);
	$pdf->Cell(55,6,'Articulo',1,0,'C',1);
	$pdf->Cell(68,6,'Modelo',1,0,'C',1);
	$pdf->Cell(27,6,'Cantidad',1,1,'C',1);
	
	while($row = mysqli_fetch_array($resul2)){
		$pdf->Cell(10);
		$pdf->Cell(20,6,$row['id_producto'],1,0,'C');
		$pdf->Cell(55,6,$row['articulo'],1,0,'C');
		$pdf->Cell(68,6,$row['modelo'],1,0,'C');
		$pdf->Cell(27,6,$row['cantidad'],1,1,'C');
	}

	//========== LISTA DE LOS PRODUCTOS AUDIFONOS
	$con3 = "SELECT id_producto,articulo,modelo,cantidad FROM producto where articulo = 'Audifonos' order by cantidad";
	$resul3= mysqli_query($conexion,$con3);	

	$pdf->AliasNbPages();
	$pdf->AddPage('portrait','LETTER');

	$pdf->SetFillColor(232,232,232);
	$pdf->SetFont('Arial','B',8);
	$pdf->SetY(20);
	$pdf->SetX(20);
	$pdf->Cell(170,6,'AUDIFONOS',1,1,'C',1);
	$pdf->SetX(20);
	$pdf->Cell(20,6,'ID Producto',1,0,'C',1);
	$pdf->Cell(55,6,'Articulo',1,0,'C',1);
	$pdf->Cell(68,6,'Modelo',1,0,'C',1);
	$pdf->Cell(27,6,'Cantidad',1,1,'C',1);
	
	while($row = mysqli_fetch_array($resul3)){
		$pdf->Cell(10);
		$pdf->Cell(20,6,$row['id_producto'],1,0,'C');
		$pdf->Cell(55,6,$row['articulo'],1,0,'C');
		$pdf->Cell(68,6,$row['modelo'],1,0,'C');
		$pdf->Cell(27,6,$row['cantidad'],1,1,'C');
	}

	//========== LISTA DE LOS PRODUCTOS BATERIAS
	$con4 = "SELECT id_producto,articulo,modelo,cantidad FROM producto where articulo = 'Baterias' order by cantidad";
	$resul4= mysqli_query($conexion,$con4);	

	$pdf->AliasNbPages();
	$pdf->AddPage('portrait','LETTER');

	$pdf->SetFillColor(232,232,232);
	$pdf->SetFont('Arial','B',8);
	$pdf->SetY(20);
	$pdf->SetX(20);
	$pdf->Cell(170,6,'BATERIAS',1,1,'C',1);
	$pdf->SetX(20);
	$pdf->Cell(20,6,'ID Producto',1,0,'C',1);
	$pdf->Cell(55,6,'Articulo',1,0,'C',1);
	$pdf->Cell(68,6,'Modelo',1,0,'C',1);
	$pdf->Cell(27,6,'Cantidad',1,1,'C',1);
	
	while($row = mysqli_fetch_array($resul4)){
		$pdf->Cell(10);
		$pdf->Cell(20,6,$row['id_producto'],1,0,'C');
		$pdf->Cell(55,6,$row['articulo'],1,0,'C');
		$pdf->Cell(68,6,$row['modelo'],1,0,'C');
		$pdf->Cell(27,6,$row['cantidad'],1,1,'C');
	}

	//========== LISTA DE LOS PRODUCTOS BLOQUES
	$con5 = "SELECT id_producto,articulo,modelo,cantidad FROM producto where articulo = 'Bloques' order by cantidad";
	$resul5 = mysqli_query($conexion,$con5);	

	$pdf->AliasNbPages();
	$pdf->AddPage('portrait','LETTER');

	$pdf->SetFillColor(232,232,232);
	$pdf->SetFont('Arial','B',8);
	$pdf->SetY(20);
	$pdf->SetX(20);
	$pdf->Cell(170,6,'BLOQUES',1,1,'C',1);
	$pdf->SetX(20);
	$pdf->Cell(20,6,'ID Producto',1,0,'C',1);
	$pdf->Cell(55,6,'Articulo',1,0,'C',1);
	$pdf->Cell(68,6,'Modelo',1,0,'C',1);
	$pdf->Cell(27,6,'Cantidad',1,1,'C',1);
	
	while($row = mysqli_fetch_array($resul5)){
		$pdf->Cell(10);
		$pdf->Cell(20,6,$row['id_producto'],1,0,'C');
		$pdf->Cell(55,6,$row['articulo'],1,0,'C');
		$pdf->Cell(68,6,$row['modelo'],1,0,'C');
		$pdf->Cell(27,6,$row['cantidad'],1,1,'C');
	}

	//========== LISTA DE LOS PRODUCTOS CABLES
	$con6 = "SELECT id_producto,articulo,modelo,cantidad FROM producto where articulo = 'Cables' order by cantidad";
	$resul6 = mysqli_query($conexion,$con6);

	$pdf->AliasNbPages();
	$pdf->AddPage('portrait','LETTER');	

	$pdf->SetFillColor(232,232,232);
	$pdf->SetFont('Arial','B',8);
	$pdf->SetY(20);
	$pdf->SetX(20);
	$pdf->Cell(170,6,'CABLES',1,1,'C',1);
	$pdf->SetX(20);
	$pdf->Cell(20,6,'ID Producto',1,0,'C',1);
	$pdf->Cell(55,6,'Articulo',1,0,'C',1);
	$pdf->Cell(68,6,'Modelo',1,0,'C',1);
	$pdf->Cell(27,6,'Cantidad',1,1,'C',1);
	
	while($row = mysqli_fetch_array($resul6)){
		$pdf->Cell(10);
		$pdf->Cell(20,6,$row['id_producto'],1,0,'C');
		$pdf->Cell(55,6,$row['articulo'],1,0,'C');
		$pdf->Cell(68,6,$row['modelo'],1,0,'C');
		$pdf->Cell(27,6,$row['cantidad'],1,1,'C');
	}

	//========== LISTA DE LOS PRODUCTOS CARGADOR
	$con7 = "SELECT id_producto,articulo,modelo,cantidad FROM producto where articulo = 'Cargador' order by cantidad";
	$resul7 = mysqli_query($conexion,$con7);

	$pdf->AliasNbPages();
	$pdf->AddPage('portrait','LETTER');	

	$pdf->SetFillColor(232,232,232);
	$pdf->SetFont('Arial','B',8);
	$pdf->SetY(20);
	$pdf->SetX(20);
	$pdf->Cell(170,6,'CARGADOR',1,1,'C',1);
	$pdf->SetX(20);
	$pdf->Cell(20,6,'ID Producto',1,0,'C',1);
	$pdf->Cell(55,6,'Articulo',1,0,'C',1);
	$pdf->Cell(68,6,'Modelo',1,0,'C',1);
	$pdf->Cell(27,6,'Cantidad',1,1,'C',1);
	
	while($row = mysqli_fetch_array($resul7)){
		$pdf->Cell(10);
		$pdf->Cell(20,6,$row['id_producto'],1,0,'C');
		$pdf->Cell(55,6,$row['articulo'],1,0,'C');
		$pdf->Cell(68,6,$row['modelo'],1,0,'C');
		$pdf->Cell(27,6,$row['cantidad'],1,1,'C');
	}

	//========== LISTA DE LOS PRODUCTOS FIBRAS 3D
	$con8 = "SELECT id_producto,articulo,modelo,cantidad FROM producto where articulo = 'Fibras 3D' order by cantidad";
	$resul8 = mysqli_query($conexion,$con8);

	$pdf->AliasNbPages();
	$pdf->AddPage('portrait','LETTER');	

	$pdf->SetFillColor(232,232,232);
	$pdf->SetFont('Arial','B',8);
	$pdf->SetY(20);
	$pdf->SetX(20);
	$pdf->Cell(170,6,'FIBRAS 3D',1,1,'C',1);
	$pdf->SetX(20);
	$pdf->Cell(20,6,'ID Producto',1,0,'C',1);
	$pdf->Cell(55,6,'Articulo',1,0,'C',1);
	$pdf->Cell(68,6,'Modelo',1,0,'C',1);
	$pdf->Cell(27,6,'Cantidad',1,1,'C',1);
	
	while($row = mysqli_fetch_array($resul8)){
		$pdf->Cell(10);
		$pdf->Cell(20,6,$row['id_producto'],1,0,'C');
		$pdf->Cell(55,6,$row['articulo'],1,0,'C');
		$pdf->Cell(68,6,$row['modelo'],1,0,'C');
		$pdf->Cell(27,6,$row['cantidad'],1,1,'C');
	}

	//========== LISTA DE LOS PRODUCTOS FIBRAS 4D
	$con9 = "SELECT id_producto,articulo,modelo,cantidad FROM producto where articulo = 'Fibras 4D' order by cantidad";
	$resul9 = mysqli_query($conexion,$con9);

	$pdf->AliasNbPages();
	$pdf->AddPage('portrait','LETTER');	

	$pdf->SetFillColor(232,232,232);
	$pdf->SetFont('Arial','B',8);
	$pdf->SetY(20);
	$pdf->SetX(20);
	$pdf->Cell(170,6,'FIBRAS 4D',1,1,'C',1);
	$pdf->SetX(20);
	$pdf->Cell(20,6,'ID Producto',1,0,'C',1);
	$pdf->Cell(55,6,'Articulo',1,0,'C',1);
	$pdf->Cell(68,6,'Modelo',1,0,'C',1);
	$pdf->Cell(27,6,'Cantidad',1,1,'C',1);
	
	while($row = mysqli_fetch_array($resul9)){
		$pdf->Cell(10);
		$pdf->Cell(20,6,$row['id_producto'],1,0,'C');
		$pdf->Cell(55,6,$row['articulo'],1,0,'C');
		$pdf->Cell(68,6,$row['modelo'],1,0,'C');
		$pdf->Cell(27,6,$row['cantidad'],1,1,'C');
	}

	//========== LISTA DE LOS PRODUCTOS FIBRAS 5D
	$con10 = "SELECT id_producto,articulo,modelo,cantidad FROM producto where articulo = 'Fibras 5D' order by cantidad";
	$resul10 = mysqli_query($conexion,$con10);

	$pdf->AliasNbPages();
	$pdf->AddPage('portrait','LETTER');	

	$pdf->SetFillColor(232,232,232);
	$pdf->SetFont('Arial','B',8);
	$pdf->SetY(20);
	$pdf->SetX(20);
	$pdf->Cell(170,6,'FIBRAS 5D',1,1,'C',1);
	$pdf->SetX(20);
	$pdf->Cell(20,6,'ID Producto',1,0,'C',1);
	$pdf->Cell(55,6,'Articulo',1,0,'C',1);
	$pdf->Cell(68,6,'Modelo',1,0,'C',1);
	$pdf->Cell(27,6,'Cantidad',1,1,'C',1);
	
	while($row = mysqli_fetch_array($resul10)){
		$pdf->Cell(10);
		$pdf->Cell(20,6,$row['id_producto'],1,0,'C');
		$pdf->Cell(55,6,$row['articulo'],1,0,'C');
		$pdf->Cell(68,6,$row['modelo'],1,0,'C');
		$pdf->Cell(27,6,$row['cantidad'],1,1,'C');
	}

	//========== LISTA DE LOS PRODUCTOS FIBRAS BASICAS
	$con11 = "SELECT id_producto,articulo,modelo,cantidad FROM producto where articulo = 'Fibras Basicas' order by cantidad";
	$resul11 = mysqli_query($conexion,$con11);	

	$pdf->AliasNbPages();
	$pdf->AddPage('portrait','LETTER');

	$pdf->SetFillColor(232,232,232);
	$pdf->SetFont('Arial','B',8);
	$pdf->SetY(20);
	$pdf->SetX(20);
	$pdf->Cell(170,6,'FIBRAS BASICAS',1,1,'C',1);
	$pdf->SetX(20);
	$pdf->Cell(20,6,'ID Producto',1,0,'C',1);
	$pdf->Cell(55,6,'Articulo',1,0,'C',1);
	$pdf->Cell(68,6,'Modelo',1,0,'C',1);
	$pdf->Cell(27,6,'Cantidad',1,1,'C',1);
	
	while($row = mysqli_fetch_array($resul11)){
		$pdf->Cell(10);
		$pdf->Cell(20,6,$row['id_producto'],1,0,'C');
		$pdf->Cell(55,6,$row['articulo'],1,0,'C');
		$pdf->Cell(68,6,$row['modelo'],1,0,'C');
		$pdf->Cell(27,6,$row['cantidad'],1,1,'C');
	}

	//========== LISTA DE LOS PRODUCTOS FIBRAS BASICOS CHINOS
	$con12 = "SELECT id_producto,articulo,modelo,cantidad FROM producto where articulo = 'Fibras Basicos Chinos' order by cantidad";
	$resul12 = mysqli_query($conexion,$con12);	

	$pdf->AliasNbPages();
	$pdf->AddPage('portrait','LETTER');

	$pdf->SetFillColor(232,232,232);
	$pdf->SetFont('Arial','B',8);
	$pdf->SetY(20);
	$pdf->SetX(20);
	$pdf->Cell(170,6,'FIBRAS BASICOS CHINOS',1,1,'C',1);
	$pdf->SetX(20);
	$pdf->Cell(20,6,'ID Producto',1,0,'C',1);
	$pdf->Cell(55,6,'Articulo',1,0,'C',1);
	$pdf->Cell(68,6,'Modelo',1,0,'C',1);
	$pdf->Cell(27,6,'Cantidad',1,1,'C',1);
	
	while($row = mysqli_fetch_array($resul12)){
		$pdf->Cell(10);
		$pdf->Cell(20,6,$row['id_producto'],1,0,'C');
		$pdf->Cell(55,6,$row['articulo'],1,0,'C');
		$pdf->Cell(68,6,$row['modelo'],1,0,'C');
		$pdf->Cell(27,6,$row['cantidad'],1,1,'C');
	}

	//========== LISTA DE LOS PRODUCTOS FIBRAS BISELADOS
	$con13 = "SELECT id_producto,articulo,modelo,cantidad FROM producto where articulo = 'Fibras Biselados' order by cantidad";
	$resul13 = mysqli_query($conexion,$con13);

	$pdf->AliasNbPages();
	$pdf->AddPage('portrait','LETTER');	

	$pdf->SetFillColor(232,232,232);
	$pdf->SetFont('Arial','B',8);
	$pdf->SetY(20);
	$pdf->SetX(20);
	$pdf->Cell(170,6,'FIBRAS BISELADOS',1,1,'C',1);
	$pdf->SetX(20);
	$pdf->Cell(20,6,'ID Producto',1,0,'C',1);
	$pdf->Cell(55,6,'Articulo',1,0,'C',1);
	$pdf->Cell(68,6,'Modelo',1,0,'C',1);
	$pdf->Cell(27,6,'Cantidad',1,1,'C',1);
	
	while($row = mysqli_fetch_array($resul13)){
		$pdf->Cell(10);
		$pdf->Cell(20,6,$row['id_producto'],1,0,'C');
		$pdf->Cell(55,6,$row['articulo'],1,0,'C');
		$pdf->Cell(68,6,$row['modelo'],1,0,'C');
		$pdf->Cell(27,6,$row['cantidad'],1,1,'C');
	}

	//========== LISTA DE LOS PRODUCTOS FORROS 360
	$con14 = "SELECT id_producto,articulo,modelo,cantidad FROM producto where articulo = 'Forros 360' order by cantidad";
	$resul14 = mysqli_query($conexion,$con14);	

	$pdf->AliasNbPages();
	$pdf->AddPage('portrait','LETTER');

	$pdf->SetFillColor(232,232,232);
	$pdf->SetFont('Arial','B',8);
	$pdf->SetY(20);
	$pdf->SetX(20);
	$pdf->Cell(170,6,'FORROS 360',1,1,'C',1);
	$pdf->SetX(20);
	$pdf->Cell(20,6,'ID Producto',1,0,'C',1);
	$pdf->Cell(55,6,'Articulo',1,0,'C',1);
	$pdf->Cell(68,6,'Modelo',1,0,'C',1);
	$pdf->Cell(27,6,'Cantidad',1,1,'C',1);
	
	while($row = mysqli_fetch_array($resul14)){
		$pdf->Cell(10);
		$pdf->Cell(20,6,$row['id_producto'],1,0,'C');
		$pdf->Cell(55,6,$row['articulo'],1,0,'C');
		$pdf->Cell(68,6,$row['modelo'],1,0,'C');
		$pdf->Cell(27,6,$row['cantidad'],1,1,'C');
	}

	//========== LISTA DE LOS PRODUCTOS FORROS 360 MAGNETICOS
	$con15 = "SELECT id_producto,articulo,modelo,cantidad FROM producto where articulo = 'Forros 360 Magnetico' order by cantidad";
	$resul15 = mysqli_query($conexion,$con15);

	$pdf->AliasNbPages();
	$pdf->AddPage('portrait','LETTER');	

	$pdf->SetFillColor(232,232,232);
	$pdf->SetFont('Arial','B',8);
	$pdf->SetY(20);
	$pdf->SetX(20);
	$pdf->Cell(170,6,'FORROS 360 MAGNETICOS',1,1,'C',1);
	$pdf->SetX(20);
	$pdf->Cell(20,6,'ID Producto',1,0,'C',1);
	$pdf->Cell(55,6,'Articulo',1,0,'C',1);
	$pdf->Cell(68,6,'Modelo',1,0,'C',1);
	$pdf->Cell(27,6,'Cantidad',1,1,'C',1);
	
	while($row = mysqli_fetch_array($resul15)){
		$pdf->Cell(10);
		$pdf->Cell(20,6,$row['id_producto'],1,0,'C');
		$pdf->Cell(55,6,$row['articulo'],1,0,'C');
		$pdf->Cell(68,6,$row['modelo'],1,0,'C');
		$pdf->Cell(27,6,$row['cantidad'],1,1,'C');
	}
	//========== LISTA DE LOS PRODUCTOS FORROS ANTISHOCK BASICOS
	$con16 = "SELECT id_producto,articulo,modelo,cantidad FROM producto where articulo = 'Forros Antishock Basicos' order by cantidad";
	$resul16 = mysqli_query($conexion,$con16);	

	$pdf->AliasNbPages();
	$pdf->AddPage('portrait','LETTER');

	$pdf->SetFillColor(232,232,232);
	$pdf->SetFont('Arial','B',8);
	$pdf->SetY(20);
	$pdf->SetX(20);
	$pdf->Cell(170,6,'FORROS ANTISHOCK BASICOS',1,1,'C',1);
	$pdf->SetX(20);
	$pdf->Cell(20,6,'ID Producto',1,0,'C',1);
	$pdf->Cell(55,6,'Articulo',1,0,'C',1);
	$pdf->Cell(68,6,'Modelo',1,0,'C',1);
	$pdf->Cell(27,6,'Cantidad',1,1,'C',1);
	
	while($row = mysqli_fetch_array($resul16)){
		$pdf->Cell(10);
		$pdf->Cell(20,6,$row['id_producto'],1,0,'C');
		$pdf->Cell(55,6,$row['articulo'],1,0,'C');
		$pdf->Cell(68,6,$row['modelo'],1,0,'C');
		$pdf->Cell(27,6,$row['cantidad'],1,1,'C');
	}

	//========== LISTA DE LOS PRODUCTOS FORROS ANTISHOCK BOOMPER
	$con17 = "SELECT id_producto,articulo,modelo,cantidad FROM producto where articulo = 'Forros Antishock Boomper' order by cantidad";
	$resul17 = mysqli_query($conexion,$con17);	

	$pdf->AliasNbPages();
	$pdf->AddPage('portrait','LETTER');

	$pdf->SetFillColor(232,232,232);
	$pdf->SetFont('Arial','B',8);
	$pdf->SetY(20);
	$pdf->SetX(20);
	$pdf->Cell(170,6,'FORROS ANTISHOCK BOOMPER',1,1,'C',1);
	$pdf->SetX(20);
	$pdf->Cell(20,6,'ID Producto',1,0,'C',1);
	$pdf->Cell(55,6,'Articulo',1,0,'C',1);
	$pdf->Cell(68,6,'Modelo',1,0,'C',1);
	$pdf->Cell(27,6,'Cantidad',1,1,'C',1);
	
	while($row = mysqli_fetch_array($resul17)){
		$pdf->Cell(10);
		$pdf->Cell(20,6,$row['id_producto'],1,0,'C');
		$pdf->Cell(55,6,$row['articulo'],1,0,'C');
		$pdf->Cell(68,6,$row['modelo'],1,0,'C');
		$pdf->Cell(27,6,$row['cantidad'],1,1,'C');
	}

	//========== LISTA DE LOS PRODUCTOS FORROS ANTISHOCK CHINOS
	$con18 = "SELECT id_producto,articulo,modelo,cantidad FROM producto where articulo = 'Forros Antishock Chinos' order by cantidad";
	$resul18 = mysqli_query($conexion,$con18);	

	$pdf->AliasNbPages();
	$pdf->AddPage('portrait','LETTER');

	$pdf->SetFillColor(232,232,232);
	$pdf->SetFont('Arial','B',8);
	$pdf->SetY(20);
	$pdf->SetX(20);
	$pdf->Cell(170,6,'FORROS ANTISHOCK CHINOS',1,1,'C',1);
	$pdf->SetX(20);
	$pdf->Cell(20,6,'ID Producto',1,0,'C',1);
	$pdf->Cell(55,6,'Articulo',1,0,'C',1);
	$pdf->Cell(68,6,'Modelo',1,0,'C',1);
	$pdf->Cell(27,6,'Cantidad',1,1,'C',1);
	
	while($row = mysqli_fetch_array($resul18)){
		$pdf->Cell(10);
		$pdf->Cell(20,6,$row['id_producto'],1,0,'C');
		$pdf->Cell(55,6,$row['articulo'],1,0,'C');
		$pdf->Cell(68,6,$row['modelo'],1,0,'C');
		$pdf->Cell(27,6,$row['cantidad'],1,1,'C');
	}

	//========== LISTA DE LOS PRODUCTOS FORROS SILICONE CASE
	$con19 = "SELECT id_producto,articulo,modelo,cantidad FROM producto where articulo = 'Forros Silicone Case' order by cantidad";
	$resul19 = mysqli_query($conexion,$con19);	

	$pdf->AliasNbPages();
	$pdf->AddPage('portrait','LETTER');

	$pdf->SetFillColor(232,232,232);
	$pdf->SetFont('Arial','B',8);
	$pdf->SetY(20);
	$pdf->SetX(20);
	$pdf->Cell(170,6,'FORROS SILICONE CASE',1,1,'C',1);
	$pdf->SetX(20);
	$pdf->Cell(20,6,'ID Producto',1,0,'C',1);
	$pdf->Cell(55,6,'Articulo',1,0,'C',1);
	$pdf->Cell(68,6,'Modelo',1,0,'C',1);
	$pdf->Cell(27,6,'Cantidad',1,1,'C',1);
	
	while($row = mysqli_fetch_array($resul19)){
		$pdf->Cell(10);
		$pdf->Cell(20,6,$row['id_producto'],1,0,'C');
		$pdf->Cell(55,6,$row['articulo'],1,0,'C');
		$pdf->Cell(68,6,$row['modelo'],1,0,'C');
		$pdf->Cell(27,6,$row['cantidad'],1,1,'C');
	}

	//========== LISTA DE LOS PRODUCTOS GOMAS BASICAS
	$con20 = "SELECT id_producto,articulo,modelo,cantidad FROM producto where articulo = 'Gomas basicas' order by cantidad";
	$resul20 = mysqli_query($conexion,$con20);

	$pdf->AliasNbPages();
	$pdf->AddPage('portrait','LETTER');	

	$pdf->SetFillColor(232,232,232);
	$pdf->SetFont('Arial','B',8);
	$pdf->SetY(20);
	$pdf->SetX(20);
	$pdf->Cell(170,6,'GOMAS BASICAS',1,1,'C',1);
	$pdf->SetX(20);
	$pdf->Cell(20,6,'ID Producto',1,0,'C',1);
	$pdf->Cell(55,6,'Articulo',1,0,'C',1);
	$pdf->Cell(68,6,'Modelo',1,0,'C',1);
	$pdf->Cell(27,6,'Cantidad',1,1,'C',1);
	
	while($row = mysqli_fetch_array($resul20)){
		$pdf->Cell(10);
		$pdf->Cell(20,6,$row['id_producto'],1,0,'C');
		$pdf->Cell(55,6,$row['articulo'],1,0,'C');
		$pdf->Cell(68,6,$row['modelo'],1,0,'C');
		$pdf->Cell(27,6,$row['cantidad'],1,1,'C');
	}

	//========== LISTA DE LOS PRODUCTOS GOMAS DISEÑO
	$con21 = "SELECT id_producto,articulo,modelo,cantidad FROM producto where articulo = 'Gomas diseno' order by cantidad";
	$resul21 = mysqli_query($conexion,$con21);	

	$pdf->AliasNbPages();
	$pdf->AddPage('portrait','LETTER');

	$pdf->SetFillColor(232,232,232);
	$pdf->SetFont('Arial','B',8);
	$pdf->SetY(20);
	$pdf->SetX(20);
	$pdf->Cell(170,6,'GOMAS DISENO',1,1,'C',1);
	$pdf->SetX(20);
	$pdf->Cell(20,6,'ID Producto',1,0,'C',1);
	$pdf->Cell(55,6,'Articulo',1,0,'C',1);
	$pdf->Cell(68,6,'Modelo',1,0,'C',1);
	$pdf->Cell(27,6,'Cantidad',1,1,'C',1);
	
	while($row = mysqli_fetch_array($resul21)){
		$pdf->Cell(10);
		$pdf->Cell(20,6,$row['id_producto'],1,0,'C');
		$pdf->Cell(55,6,$row['articulo'],1,0,'C');
		$pdf->Cell(68,6,$row['modelo'],1,0,'C');
		$pdf->Cell(27,6,$row['cantidad'],1,1,'C');
	}

	//========== LISTA DE LOS PRODUCTOS MEMORIAS
	$con22 = "SELECT id_producto,articulo,modelo,cantidad FROM producto where articulo = 'Memorias' order by cantidad";
	$resul22 = mysqli_query($conexion,$con22);	

	$pdf->AliasNbPages();
	$pdf->AddPage('portrait','LETTER');

	$pdf->SetFillColor(232,232,232);
	$pdf->SetFont('Arial','B',8);
	$pdf->SetY(20);
	$pdf->SetX(20);
	$pdf->Cell(170,6,'MEMORIAS',1,1,'C',1);
	$pdf->SetX(20);
	$pdf->Cell(20,6,'ID Producto',1,0,'C',1);
	$pdf->Cell(55,6,'Articulo',1,0,'C',1);
	$pdf->Cell(68,6,'Modelo',1,0,'C',1);
	$pdf->Cell(27,6,'Cantidad',1,1,'C',1);
	
	while($row = mysqli_fetch_array($resul22)){
		$pdf->Cell(10);
		$pdf->Cell(20,6,$row['id_producto'],1,0,'C');
		$pdf->Cell(55,6,$row['articulo'],1,0,'C');
		$pdf->Cell(68,6,$row['modelo'],1,0,'C');
		$pdf->Cell(27,6,$row['cantidad'],1,1,'C');
	}

	//========== LISTA DE LOS PRODUCTOS TABLET
	$con23 = "SELECT id_producto,articulo,modelo,cantidad FROM producto where articulo = 'Tablet' order by cantidad";
	$resul23 = mysqli_query($conexion,$con23);

	$pdf->AliasNbPages();
	$pdf->AddPage('portrait','LETTER');	

	$pdf->SetFillColor(232,232,232);
	$pdf->SetFont('Arial','B',8);
	$pdf->SetY(20);
	$pdf->SetX(20);
	$pdf->Cell(170,6,'TABLET',1,1,'C',1);
	$pdf->SetX(20);
	$pdf->Cell(20,6,'ID Producto',1,0,'C',1);
	$pdf->Cell(55,6,'Articulo',1,0,'C',1);
	$pdf->Cell(68,6,'Modelo',1,0,'C',1);
	$pdf->Cell(27,6,'Cantidad',1,1,'C',1);
	
	while($row = mysqli_fetch_array($resul23)){
		$pdf->Cell(10);
		$pdf->Cell(20,6,$row['id_producto'],1,0,'C');
		$pdf->Cell(55,6,$row['articulo'],1,0,'C');
		$pdf->Cell(68,6,$row['modelo'],1,0,'C');
		$pdf->Cell(27,6,$row['cantidad'],1,1,'C');
	}

	//========== LISTA DE LOS PRODUCTOS TELEFONO
	$con24 = "SELECT id_producto,articulo,modelo,cantidad FROM producto where articulo = 'Telefono' order by cantidad";
	$resul24 = mysqli_query($conexion,$con24);	

	$pdf->AliasNbPages();
	$pdf->AddPage('portrait','LETTER');

	$pdf->SetFillColor(232,232,232);
	$pdf->SetFont('Arial','B',8);
	$pdf->SetY(20);
	$pdf->SetX(20);
	$pdf->Cell(170,6,'TELEFONO',1,1,'C',1);
	$pdf->SetX(20);
	$pdf->Cell(20,6,'ID Producto',1,0,'C',1);
	$pdf->Cell(55,6,'Articulo',1,0,'C',1);
	$pdf->Cell(68,6,'Modelo',1,0,'C',1);
	$pdf->Cell(27,6,'Cantidad',1,1,'C',1);
	
	while($row = mysqli_fetch_array($resul24)){
		$pdf->Cell(10);
		$pdf->Cell(20,6,$row['id_producto'],1,0,'C');
		$pdf->Cell(55,6,$row['articulo'],1,0,'C');
		$pdf->Cell(68,6,$row['modelo'],1,0,'C');
		$pdf->Cell(27,6,$row['cantidad'],1,1,'C');
	}
	
	$pdf->Output();  // con d se descarga automaticamente 
	// con f se guarda directamente en el disco pero se tiene q poner nombre
	  //$pdf->Output('F','REPORTE.PDF');
		
?>