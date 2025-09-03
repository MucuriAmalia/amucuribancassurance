<!DOCTYPE html>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib uri="http://www.springframework.org/security/tags" prefix="sec" %>
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>

<html>
<head>
    <title><spring:message code="project.title" /></title>
    <link rel="icon" type="image/x-icon" href="<c:url value='/libs/favicon.ico' />">
    <link href="<c:url value='/libs/bootstrap/dist/css/bootstrap.min.css' />" rel="stylesheet">
    <!-- Font Awesome -->
    <link href="<c:url value='/libs/font-awesome/css/font-awesome.min.css' />" rel="stylesheet">
    <!-- NProgress -->
    <link href="<c:url value='/libs/nprogress/nprogress.css' />" rel="stylesheet">
    <!-- iCheck -->
    <link href="<c:url value='/libs/pnotify/dist/sweetalert2.min.css' />" rel="stylesheet">
    <link href="<c:url value='/libs/iCheck/skins/flat/green.css' />" rel="stylesheet">
    <link href="<c:url value='/libs/build/css/custom.min.css' />" rel="stylesheet">
    <script src="<c:url value='/libs/jquery/dist/jquery.min.js' />"></script>
    <script src="<c:url value='/libs/bootstrap/dist/js/bootstrap.min.js' />"></script>
    <script src="<c:url value='/libs/jquery-validation/jquery.validate.min.js'/>"></script>
    <script src="<c:url value='/libs/jquery-validation/additional-methods.min.js'/>"></script>
    <script src="<c:url value='/libs/pnotify/dist/sweetalert2.all.min.js'/>"></script>
    <script type="text/javascript" src="<c:url value='/js/modules/login/login.js'/>"></script>
    <style>
        #login-error { display: none; }
    </style>
</head>

<body class="login">
<div>
    <div class="image-area"></div>
    <div class="form-area">
        <form class="fom-inputs" action="j_spring_security_check" method="post" novalidate="novalidate" autocomplete="off" id="login_form">
            <img src="<c:url value='/logo'/>" width="250px" class="imgfom"/>
            <h3>Login to <spring:message code='project.name'/></h3>
            <!-- Holiday Notification Banner -->
            <div class="alert alert-info alert-dismissible fade show" role="alert" id="holiday-notification" style="display: none;">
                <strong>Holiday Notice:</strong> You are logging in on a non-working day. All transactions will be pushed to the next working day.
            </div>
            <c:if test="${isHoliday}">
                <script>
                    document.addEventListener("DOMContentLoaded", function () {
                        document.getElementById("holiday-notification").style.display = "block";
                    });
                </script>
            </c:if>
            <div class="alert alert-danger alert-dismissible fade show" role="alert" id="login-error" aria-live="assertive">
                <c:choose>
                    <c:when test="${param.locked == 'true'}">
                        Your account is locked due to too many failed login attempts. Please contact support.
                    </c:when>
                    <c:otherwise>
                        <c:out value="${fn:replace(SPRING_SECURITY_LAST_EXCEPTION.message, 'Bad credentials', 'Username/Password are incorrect')}" />
                    </c:otherwise>
                </c:choose>
            </div>
            <input type="text" class="form-control" name="j_username" placeholder="Username" required="" />
            <input type="password" class="form-control" name="j_password" placeholder="Password" required="" />
            <button class="submit" type="submit" id="login-btn" onclick="this.disabled=true;this.value='Sending, please wait...';this.form.submit();"><spring:message code="login.signIn" /></button>
            <a class="reset_pass" href="javascript:void(0);" id="btn-forgot-password" onclick="forgotPassword()">Forgot Password?</a>
            <input type="hidden" name="${_csrf.parameterName}" value="${_csrf.token}"/>
        </form>
    </div>
</div>

<script>
    $(function(){
        $(document).ready(function() {
            var url = $(location).attr('href');
            var error = url.indexOf("j_spring_security_check") >= 0;
            if (error) {
                $('#login-error').show();
            } else {
                $('#login-error').hide();
            }
            // Holiday Check
            $.get("/api/holidays/isTodayHoliday", function(data) {
                if (data.holiday) {
                    $('#holiday-notification').show();
                }
            });
            // Form Validation
            $("#login_form").validate({
                rules: {
                    j_username: "required",
                    j_password: "required"
                },
                messages: {
                    j_username: "Please enter your username",
                    j_password: "Please enter your password"
                },
                errorPlacement: function(error, element) {
                    error.insertAfter(element);
                }
            });
        });
        $(document).ajaxStart(function () {
            $("#btn-forgot-password").attr("disabled", true);
        });
        $(document).ajaxComplete(function () {
            $("#btn-forgot-password").attr("disabled", false);
        });
    });

    function forgotPassword() {
        var $form = $('#login_form');
        var data = {};
        $form.serializeArray().map(function(x){data[x.name] = x.value;});
        var url = "resetPassword";
        var request = $.post(url, data);
        request.success(function(){
            Swal.fire({
                title: 'Success',
                text: 'Credentials sent to your email address. Please check your email',
                icon: 'success'
            });
        });
        request.error(function(jqXHR, textStatus, errorThrown){
            Swal.fire({
                title: 'Error',
                text: jqXHR.responseText,
                icon: 'error'
            });
        });
    }
</script>
</body>
</html>