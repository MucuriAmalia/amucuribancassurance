
<!DOCTYPE html>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib uri="http://www.springframework.org/security/tags" prefix="sec" %>
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>

<html>
<head>
    <title><spring:message  code="project.title" /></title>
    <link  href="<c:url value='/libs/bootstrap/dist/css/bootstrap.min.css' /> " rel="stylesheet">
    <!-- Font Awesome -->
    <link href="<c:url value='/libs/font-awesome/css/font-awesome.min.css' /> " rel="stylesheet">
    <!-- NProgress -->
    <link  href="<c:url value='/libs/nprogress/nprogress.css' /> " rel="stylesheet">
    <link href="<c:url value='/libs/pnotify/dist/pnotify.css' /> " rel="stylesheet">
    <link href="<c:url value='/libs/pnotify/dist/pnotify.buttons.css' /> " rel="stylesheet">
    <link href="<c:url value='/libs/pnotify/dist/pnotify.nonblock.css' /> " rel="stylesheet">
    <!-- iCheck -->
    <link href="<c:url value='/libs/iCheck/skins/flat/green.css' /> " rel="stylesheet">
    <link href="<c:url value='/libs/css/login.css' /> " rel="stylesheet">
    <script src="<c:url value='/libs/jquery/dist/jquery.min.js' /> "></script>
    <script  src="<c:url value='/libs/bootstrap/dist/js/bootstrap.min.js' /> "></script>
    <script src="<c:url value="/libs/pnotify/dist/pnotify.js"/>"></script>
    <script src="<c:url value="/libs/pnotify/dist/pnotify.buttons.js"/>"></script>
    <script  src="<c:url value="/libs/pnotify/dist/pnotify.nonblock.js"/>"></script>
    <script src="<c:url value="/libs/jquery-validation/jquery.validate.min.js"/>"></script>
    <script src="<c:url value="/libs/jquery-validation/additional-methods.min.js"/>"></script>
</head>
<body>
<div class="container">
    <div class="col-md-10 col-md-offset-1 main" >
        <div class="col-md-6 left-side" >
            <h3><spring:message code='project.name'/></h3>
            <div class="kv-avatar center-block" style="width:400px">
                <img  src="<c:url value='/logo'/> " style="width:200px">
            </div>
            <br>


        </div><!--col-sm-6-->

        <div class="col-md-6 right-side">
                   <  <h3>Reset Password</h3>

            <!--Form with header-->
            <div class="form">


                            <form:form class="form-horizontal" action="reset-password"
                                       commandName="passwordResetForm">

                                <c:if test="${error != null}">
                                    <div class="alert alert-danger alert-dismissible fade show" role="alert" id="login-error">
                                        <button type="button" class="close" data-dismiss="alert" aria-label="Close"><span aria-hidden="true">×</span>
                                        </button>
                                            ${error}
                                    </div>
                                </c:if>
                                <input type="hidden" name="token" value="${token}"/>

                                <div class="item form-group">
                                    <label for="password">New Password</label>
                                      <form:password path="password" cssClass="form-control input-lg" required="required"/>
                                </div>
                                <div class="item form-group">
                                    <label for="confirmPassword">Confirm Password</label>
                                    <form:password path="confirmPassword" cssClass="form-control input-lg" required="required"/>
                                </div>
                                <div class="text-xs-center">
                                    <button type="submit" class="btn-deep-purple">Update password</button>
                                </div>
                            </form:form>

                        </div>

                </div>
            </div>
            <div class="row">
            </div>
        </div>

</body>
</html>