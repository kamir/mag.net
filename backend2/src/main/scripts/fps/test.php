<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Strict//EN"
"http://www.w3.org/TR/xhtml1/DTD/xhtml1-strict.dtd">

<html xmlns="http://www.w3.org/1999/xhtml" xml:lang="en" lang="en">
<head>
<title>OCR Processor Module</title>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8" />
<link rel="stylesheet" type="text/css" href="../../css/style.css" />
</head>
<body class="test-data">
Debugging:
<table cellspacing="0" cellpadding="0" border="0">
<tr class="subhead" align="Left"><th>Name</th><th>Value</th></tr>
<?php $class = 'normal'; ?>
<tr class="<?php echo htmlspecialchars($class) ?>"><td>PHP_VERSION</td><td><?php echo htmlspecialchars(PHP_VERSION) ?></td></tr>
<?php $VARS = isset($_SERVER)? $_SERVER: (isset($HTTP_SERVER_VARS)? $HTTP_SERVER_VARS: array()); ?>
<?php foreach ($VARS as $name => $value) { ?>
<?php
	if (strpos($name, 'HTTP_') !== 0 && strpos($name, 'REQUEST_') !== 0)
		continue;
	$class = $class === 'alt'? 'normal': 'alt'
?>
<tr class="<?php echo htmlspecialchars($class) ?>"><td><?php echo htmlspecialchars($name) ?></td><td><?php echo htmlspecialchars($value) ?></td></tr>
<?php } ?>
</table>


<hr>
We use Tesseract 3.02 in the background ...
<hr>


<?php

phpinfo();

$uploaddir = 'uploads/';
$uploadfile = $uploaddir . basename($_FILES['userfile']['name']);

echo '<p> \n * ARRAY name: '.$_FILES['userfile']['name'].'<br/><br/><hr>\n';
echo ' * UPLOADFILE : '.$uploadfile.'<br/><br/><hr>\n';
echo ' * ARRAY tmp_name: '.$_FILES['userfile']['tmp_name'].'<br/><br/><hr>\n';

if (move_uploaded_file($_FILES['userfile']['tmp_name'], $uploadfile)) {
    echo "Datei ist valide und wurde erfolgreich hochgeladen.\n";
} 
else {
    echo "Möglicherweise eine Dateiupload-Attacke!\n";
    echo '<hr><b>Weitere Debugging Informationen:</b>\n';
    print_r($_FILES);
}

print "</p>";


?>

</body>
</html>