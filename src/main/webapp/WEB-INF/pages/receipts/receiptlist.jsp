<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="sec" uri="http://www.springframework.org/security/tags" %>
<script type="text/javascript"
	src="<c:url value="/js/modules/utils/select2builder.js"/>"></script>
<script type="text/javascript"
	src="<c:url value="/libs/rivets/rivets.js"/>"></script>
<script type="text/javascript" src="<c:url value="/js/modules/receipts/receiptlist.js"/>"></script>
<div class="x_panel">
  <div class="x_title">
		<h2>Receipts</h2>
		<ul class="nav navbar-right panel_toolbox">
			<li><a class="collapse-link"><i class="fa fa-chevron-up"></i></a>
			</li>
		</ul>
		<div class="clearfix"></div>
	</div>
	<div class="card-box table-responsive">
	<sec:authorize access="hasAnyAuthority('CREATE_RECEIPT')">
	<a href="<c:url value='/protected/uw/receipts/receiptentry'/> " class="btn btn-primary float-right">New</a>
	</sec:authorize>

	<table id="rcts-tbl" class="table table-striped" style="width:100%">
		<thead>
			<tr>

				<th>Receipt No.</th>
				<th>Receipt Date</th>
				<th>Receipt By</th>
				<th>Paymt Mode</th>
				<th>Amount</th>
				<th>Paid By</th>
				<th>Printed?</th>
			</tr>
		</thead>
	</table>
		</div>
</div>