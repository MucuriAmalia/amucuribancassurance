
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
    <!-- iCheck -->
    <link href="<c:url value='/libs/iCheck/skins/flat/green.css' /> " rel="stylesheet">
    <link href="<c:url value='/libs/css/login.css' /> " rel="stylesheet">
    <script src="<c:url value='/libs/jquery/dist/jquery.min.js' /> "></script>
    <script  src="<c:url value='/libs/bootstrap/dist/js/bootstrap.min.js' /> "></script>
    <script src="<c:url value="/libs/jquery-validation/jquery.validate.min.js"/>"></script>
    <script src="<c:url value="/libs/jquery-validation/additional-methods.min.js"/>"></script>
    <script type="text/javascript" src="<c:url value="/js/modules/login/login.js"/>"></script>
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
            <h3>Change Password</h3>

            <!--Form with header-->
            <div class="form">

                <form:form action="changePassword" modelAttribute="changePasswordDTO" method="post">
                    <input type="hidden" value="${username}" name="username" />
                    <c:if test="${error != null}">
                    <div class="alert alert-danger alert-dismissible fade show" role="alert" id="login-error">
                        <button type="button" class="close" data-dismiss="alert" aria-label="Close"><span aria-hidden="true">×</span>
                        </button>
                            ${error}
                    </div>
                    </c:if>
                    <div>
                        <div class="item form-group">
                            <label for="form2">One Time Password</label>
                            <input type="password" id="form2" class="form-control input-lg" name="currentPassword">

                        </div>

                        <div class="item form-group">
                            <label for="form4">New Password</label>
                            <input type="password" id="form4" class="form-control input-lg" name="newPass">

                        </div>
                        <div class="item form-group">
                            <label for="form4">Confirm Password</label>
                            <input type="password" id="form5" class="form-control input-lg" name="confirmPass">

                        </div>

                        <div class="text-xs-center">
                            <button class="btn btn-deep-purple" type="submit">Change Password</button>
                        </div>
                    </div>
                </form:form>

            </div>
        </div>
    </div>
</body>
<script>
    $(function(){

        $(document).ready(function() {

        });

    });

</script>
</html>