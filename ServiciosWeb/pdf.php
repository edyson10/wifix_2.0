<?php

require 'fpdf/fpdf.php';

class pdf extends FPDF{

  public function header(){

    date_default_timezone_set('America/Bogota');
    $fecha= date('d-m-Y');
    $this->SetFont('Arial','B',12);
    //$this->Cell(0,5,date('25/m/Y'),0,1); 
    //$this->Cell(0,5,date("d-m-Y",strtotime($fecha."- 1 days")),0,1,'L');
    //$this->Image('imagen\logo.png',165,10,40,30,'png');
    $this->SetY(20);
  }

  public function footer(){

    $this->SetFont('Arial','B',12);
    $this->SetY(-15);
    $this->Write(5,'Cucuta,Colombia');
    $this->SetX(-25);
    $this->AliasNbPages();
    $this->Write(5,$this->PageNo().'/{nb}');
  }

}
?>