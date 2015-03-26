<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">

<html>
<head>

<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">


<meta charset="utf-8">
<meta http-equiv="X-UA-Compatible" content="IE=edge">
<meta name="viewport" content="width=device-width, initial-scale=1">

<title>Welcome to EasyTaxi</title>

<script src="http://ajax.googleapis.com/ajax/libs/jquery/2.0.2/jquery.min.js"></script>
<script type="text/javascript" src="js/bootstrap.min.js"></script>
<link rel="stylesheet" href="css/bootstrap.css" />

<style>
body {
	padding-top: 70px;
	padding-bottom: 30px;
}

.theme-dropdown .dropdown-menu {
	position: static;
	display: block;
	margin-bottom: 20px;
}

.theme-showcase>p>.btn {
	margin: 5px 0;
}
</style>

	<script>
		$(':button')
				.click(
						function() {
							var file = $("#file")[0].files[0];
							url = "processCSV";
							var xhr = new XMLHttpRequest(), typeValue = "post", urlValue = "processCSV";

							var formData = new FormData();
							formData.append("thefile", file);

							xhr.onreadystatechange = function() {
								if (xhr.readyState == 4 && xhr.status == 200) {
									var result = JSON.parse(xhr.responseText);
									alert("complete");
									//callback(result);
								}
							}

							xhr.open("post", url, true);
							xhr.setRequestHeader("Content-Type","multipart/form-data");
							xhr.send(formData);

						});
	</script>
	

</head>

<body>
	<%@include file="header.jsp"%>

	<div class="container theme-showcase" role="main">
		Please select your desired file for auto-generating of receipt. </br> </br>
		Please note that: </br> <span style="margin-left: 2em"> 1. All
			complete row data in the CSV will be sent out </br> <span
			style="margin-left: 2em"> 2. Incomplete row data will result
				in no email being sent </br> <span style="margin-left: 2em"> 3.
					All incomplete row data will be reflected in a text file upon
					completion.<br> <span style="margin-left: 2em">4. The
						maximum memory size has been set to 3mb<br>
				</span><br>
					<form method="POST" action="processCSV" enctype="multipart/form-data" >
						<input name="file" id="file" accept=".csv" type="file" /><br>
						<input type="submit" value="Upload" />

					</form>
	</div>
</body>
</html>