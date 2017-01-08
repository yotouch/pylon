<#macro page title = ""><!DOCTYPE HTML>
<html lang="en-US">
<head>
    <title>${title!""}</title>

    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0, user-scalable=0, minimum-scale=1.0, maximum-scale=1.0">
    <meta name="apple-mobile-web-app-capable" content="yes" />

    <link rel="stylesheet" type="text/css" href="/static/libs/bootstrap/css/bootstrap.min.css">
    <link rel="stylesheet" type="text/css" href="/static/libs/datetimepicker/css/bootstrap-datetimepicker.min.css">
    <script src="/static/libs/jquery/jquery-1.11.3.min.js"></script>
    <script src="/static/libs/bootstrap/js/bootstrap.min.js"></script>
    <script src="/static/libs/datetimepicker/js/bootstrap-datetimepicker.min.js"></script>
    <script src="/static/libs/datetimepicker/js/locales/bootstrap-datetimepicker.zh-CN.js"></script>


</head>
<body><#nested></body>
</html>
</#macro>
