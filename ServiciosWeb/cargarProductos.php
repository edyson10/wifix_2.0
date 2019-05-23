<?php

$cedula = $_REQUEST['cedula'];

$sql = "SELECT producto.articulo, producto.modelo FROM producto inner join bodega on producto.bodega = bodega.id_bodega 
inner join tienda on tienda.id_tienda = bodega.id_tienda inner join empleado on empleado.id_tienda = tienda.id_tienda 
WHERE producto.estado = 1 and empleado.cedula = '$cedula' order by producto.articulo,producto.modelo" ;

function connectDB(){
    /*
    $server = "18.228.235.94";
    $user = "edynson";
    $pass = "Edynson123@";
    $bd = "proyecto";
    */
    
    $server = "localhost";
    $user = "root";
    $pass = "";
    $bd = "wifix";

    $conexion = mysqli_connect($server, $user, $pass,$bd);
    if($conexion){
        //echo 'La conexion de la base de datos se ha hecho satisfactoriamente';
    }else{
        echo 'Ha sucedido un error inexperado en la conexion de la base de datos';
    }
    return $conexion;
}

function disconnectDB($conexion){
    $close = mysqli_close($conexion);
    if($close){
        //echo 'La desconexion de la base de datos se ha hecho satisfactoriamente';
    }else{
        echo 'Ha sucedido un error inexperado en la desconexion de la base de datos';
    }   
    return $close;
}

function getArraySQL($sql){
    //Creamos la conexión con la función anterior
    $conexion = connectDB();
    //generamos la consulta
    mysqli_set_charset($conexion, "utf8"); //formato de datos utf8

    if(!$result = mysqli_query($conexion, $sql)) die(); //si la conexión cancelar programa

    $rawdata = array(); //creamos un array

    //guardamos en un array multidimensional todos los datos de la consulta
    $i=0;

    while($row = mysqli_fetch_array($result)){
        $rawdata[$i] = $row;
        $i++;
    }
    disconnectDB($conexion); //desconectamos la base de datos
    return $rawdata; //devolvemos el array
}
    $myArray = getArraySQL($sql);
    echo json_encode($myArray);
?>