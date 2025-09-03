<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib uri="http://www.springframework.org/security/tags"
	prefix="security"%>

<div class="top_nav">
	<div class="nav_menu">
		<div class="nav toggle">
			<a id="menu_toggle"><i class="fa fa-bars"></i></a>
		</div>
		<nav class="nav navbar-nav">
			<ul class=" navbar-right">
				<li class="nav-item dropdown open" style="padding-left: 15px;">
					<a href="javascript:;" class="user-profile dropdown-toggle" aria-haspopup="true" id="navbarDropdown" data-toggle="dropdown" aria-expanded="false"> <security:authorize
							access="isAuthenticated()">
						<security:authentication property="principal.username" />
					</security:authorize> <span class=" fa fa-angle-down"></span>
					</a>
					<div class="dropdown-menu dropdown-usermenu float-right" aria-labelledby="navbarDropdown">
						<c:url var="logoutUrl" value="/logout" />
						<form action="${logoutUrl}" method="post" id="logoutForm">
							<input type="hidden" name="${_csrf.parameterName}"
								   value="${_csrf.token}" />
						</form>
						<a class="dropdown-item" href="#" onclick="javascript:logoutForm.submit();"><spring:message
								code="header.logout" /></a>
					</div>
				</li>
				<c:if test="${not empty sessionScope.userBranches}">
					<li class="nav-item" style="color: white;">
						<span style="padding-right: 5px;">Branch:</span>
						<span style="padding-left: 0">
						${sessionScope.userBranches[0].branchName}</span>
					</li>
				</c:if>
			</ul>
		</nav>
	</div>
</div>


