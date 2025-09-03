<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<script type="text/javascript"
	src="<c:url value="/js/modules/utils/select2builder.js"/>"></script>
<script type="text/javascript"
	src="<c:url value="/libs/rivets/rivets.js"/>"></script>
<script type="text/javascript"
	src="<c:url value="/js/modules/uwtrans/contras.js"/>"></script>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<div class="x_panel">
	<div class="x_title">
		<h2><i class="fa fa-bars"></i> Renewals Processing</h2>
        <div class="clearfix"></div>
	</div>
	<div class="x_content">
		<c:if test="${error != null}">
			<div class="alert alert-error alert-dismissible">
				<a href="#" class="close" data-dismiss="alert" aria-label="close">&times;</a>
				${error}
			</div>
		</c:if>
		

		<form:form class="form-horizontal" action="startRenewal"
			modelAttribute="startRenForm">
			<div class="item form-group form-required">
				<div class="col-md-6 col-xs-12">
					<label for="brn-id" class="col-md-6 label-align">Type of
						Renewal</label>

					<div class="col-md-6 col-xs-12">
						<div class="radio">
							  <label>
							  <form:radiobutton path="renewalType" value="S"/>Single Policy Renewal
							</div>
							<div class="radio">
							  <label><form:radiobutton path="renewalType" value="B"/>Batch Renewals
							</div>
							<div class="radio">
							  <label><form:radiobutton path="renewalType" value="P"/>Renewal in Progress Trans
							</div>
					</div>
				</div>
				<div class="col-md-6 col-xs-12"></div>


			</div>


			<div class="box-footer">
				<input type="submit" class="btn btn-info float-left"
					style="margin-right: 10px;" value="Next">
			</div>

		</form:form>
		
	</div>
</div>
