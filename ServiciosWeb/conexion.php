<?php


$hostname_localhost="localhost";
$database_localhost="wifix";
$username_localhost="root";
$password_localhost="";

/*
$hostname_localhost="18.228.235.94";
$database_localhost="proyecto";
$username_localhost="edynson";
$password_localhost="Edynson123@";
*/

$conexion=mysqli_connect($hostname_localhost,$username_localhost,$password_localhost,$database_localhost)
or die("error al conectar".mysqli_error());

?>