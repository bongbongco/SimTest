<?php
define('HOST', '127.0.0.1');
define('USER', 'root');
define('PASS', 'springst2016!');
define('DB', 'usim');

$con = mysqli_connect(HOST, USER, PASS, DB);

$id = $_POST['id'];
$pw = $_POST['pw'];
$imei = $_POST['imei'];
$ssn = $_POST['ssn'];

$sql = "select * from users where id='$id' and pw='$pw' and imei='$imei' and ssn='$ssn'";

$res = mysqli_query($con, $sql);

$check = mysqli_fetch_array($res, MYSQL_ASSOC);

if(isset($check)){
$result = 'success.';
}else{
$result = 'failure.';
}

$paramsArray = array();

array_push($paramsArray, array('ID'=>$id, 'PASSWORD'=>$pw, 'IMEI'=>$imei, 'SSN'=>$ssn, 'RESULT'=>$result));

echo json_encode(array("USER"=>$paramsArray));

mysqli_close($con);
?>
