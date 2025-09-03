<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib uri="http://www.springframework.org/security/tags" prefix="sec" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>

    
<head>
    <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
    <!-- Meta, title, CSS, favicons, etc. -->
    <meta charset="utf-8">
    <meta http-equiv="X-UA-Compatible" content="IE=edge">
    <meta name="viewport" content="width=device-width, initial-scale=1">
    <meta http-equiv='cache-control' content='no-cache'>
    <meta http-equiv='expires' content='0'>
    <meta http-equiv='pragma' content='no-cache'>
    <link rel="icon" type="image/x-icon" href="<c:url value='/libs/favicon.ico' /> ">
   <title><spring:message  code="project.title" /></title>

    <!-- Bootstrap -->
    <link  href="<c:url value='/libs/bootstrap/dist/css/bootstrap.min.css' /> " rel="stylesheet">
    <!-- Font Awesome -->
    <link href="<c:url value='/libs/font-awesome/css/font-awesome.min.css' /> " rel="stylesheet">
    <!-- NProgress -->
    <link  href="<c:url value='/libs/nprogress/nprogress.css' /> " rel="stylesheet">
    <!-- iCheck -->
    <link href="<c:url value='/libs/iCheck/skins/flat/green.css' /> " rel="stylesheet">
    <!-- bootstrap-progressbar -->
    <link  href="<c:url value='/libs/bootstrap-progressbar/css/bootstrap-progressbar-3.3.4.min.css' /> " rel="stylesheet">
    <link  href="<c:url value='/libs/css/trumbowyg.min.css' /> " rel="stylesheet">
   <!-- bootstrap-daterangepicker -->
    <link  href="<c:url value='/libs/bootstrap-daterangepicker/daterangepicker.css' /> " rel="stylesheet">
    <link  href="<c:url value='/libs/css/bootstrap-datetimepicker.css' /> " rel="stylesheet">
    <link href="<c:url value='/libs/fileinput.min.css' /> " rel="stylesheet"/>
    <link rel="stylesheet" href="<c:url value='/libs/css/validation.css' /> ">
    
    <link href="<c:url value='/libs/pnotify/dist/sweetalert2.min.css' /> " rel="stylesheet">

    <link href="<c:url value='/libs/fullcalendar/dist/fullcalendar.min.css' /> "  rel="stylesheet">
    <link href="<c:url value='/libs/fullcalendar/dist/fullcalendar.print.css' /> " rel="stylesheet" media="print">
    
    <link href="<c:url value="/libs/select2/select2.css"/>" rel="stylesheet">
		<link href="<c:url value="/libs/select2/select2-bootstrap.css"/>" rel="stylesheet">
    <!-- Custom Theme Style -->
 <link rel="stylesheet" type="text/css" href="<c:url value='/libs/DataTables/media/css/dataTables.bootstrap.css' />">
   <link href="<c:url value='/libs/css/bootstrap-datetimepicker.css' /> " rel="stylesheet"/>
   <link href="<c:url value='/libs/css/select.dataTables.min.css' /> " rel="stylesheet"/>
    <link href="<c:url value='/libs/css/jquery.dataTables.multiselect.css' /> " rel="stylesheet"/>

    <link href="<c:url value='/libs/malihu-custom-scrollbar-plugin/jquery.mCustomScrollbar.min.css' />" rel="stylesheet"/>
 <link href="<c:url value='/libs/build/css/custom.min.css' /> " rel="stylesheet">
    <!-- jQuery -->
    <script src="<c:url value='/libs/jquery/dist/jquery.min.js' /> "></script>
     <script src="<c:url value="/libs/select2/select2.js"/>"></script>
		<script src="<c:url value='/js/fileinput.min.js' /> "></script>




	  <script type="text/javascript" src="<c:url value='/libs/DataTables/media/js/jquery.dataTables.min.js' /> "></script>

	 <script type="text/javascript" src="<c:url value='/libs/DataTables/media/js/dataTables.bootstrap.js' /> "></script>

	 <script type="text/javascript" src="<c:url value="/libs/js/jquery.number.min.js"/>"></script>

	 <script type="text/javascript" src="<c:url value="/js/modules/head.js"/>"></script>
	 <script type="text/javascript" src="<c:url value="/libs/js/dataTables.select.min.js"/>"></script>
    <script type="text/javascript" src="<c:url value="/js/jquery.dataTables.multiselect.js"/>"></script>
    <link rel="stylesheet" type="text/css" href="<c:url value='/libs/custom-broker.css' />">

</head>
