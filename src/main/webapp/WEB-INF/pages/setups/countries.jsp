<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<script>
    var requestContextPath = '${pageContext.request.contextPath}';
</script>
<script type="text/javascript" src="<c:url value="/js/modules/setups/setups.js"/>"></script>
 <div class="x_panel">
 <div class="x_title">
	 <h2><i class="fa fa-bars"></i> Country Details</h2>
	 <div class="clearfix"></div>
	</div>
	
	<button type="button" class="btn btn-success btn btn-info float-right" data-toggle="modal"
		data-target="#countryModal">New</button>
	<table id="countryList" class="table table-striped" style="width: 100%">
		<thead>
			<tr class="headings">

				<th width="30%">Country Code</th>
				<th width="30%">Country Name</th>
				<th width="30%">Country Prefix</th>
				<th width="5%"></th>
				<th width="5%"></th>
			</tr>
		</thead>
	</table>
	<div class="x_title">
	<h4>County Details</h4>
	</div>
	
	<button type="button" class="btn btn-success btn btn-info float-right" id="btn-add-county">New</button>
	
	<table id="countyList" class="table table-striped" style="width: 100%">
		<thead>
			<tr class="headings">

				<th width="30%">County Code</th>
				<th width="30%">County Name</th>
				<th width="5%"></th>
				<th width="5%"></th>
			</tr>
		</thead>
	</table>
	<div class="x_title">
	<h4>Town Details</h4>
	</div>
	
	<button type="button" class="btn btn-success btn btn-info float-right" id="btn-add-town">New</button>
	
	<table id="townList" class="table table-striped"">
		<thead>
			<tr class="headings">

				<th width="20%">Town Code</th>
				<th width="20%">Town Name</th>
				<th width="10%"></th>
				<th width="5%"></th>
				<th width="5%"></th>
			</tr>
		</thead>
	</table>
</div>
<jsp:include page="countriesmodals/modals.jsp"></jsp:include>