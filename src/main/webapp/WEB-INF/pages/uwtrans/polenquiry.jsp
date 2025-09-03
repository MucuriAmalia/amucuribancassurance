<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<script type="text/javascript" src="<c:url value="/js/modules/utils/select2builder.js"/>"></script>
<script type="text/javascript" src="<c:url value="/libs/rivets/rivets.js"/>"></script>
<script type="text/javascript" src="<c:url value="/js/modules/uwtrans/policies.js"/>"></script>
<div class="x_panel">
	<div class="card-box table-responsive">
  <div class="x_title">
	<h4>Policy Enquiry</h4>
	</div>
	 <form id="search-form" class="form-horizontal">
		 <div class="item form-group">
			 <div class="col-md-12 col-xs-12">
				 <label for="unified-search" class="col-md-2 control-label">Search Parameter</label>
				 <div class="col-md-10 col-xs-12">
					 <input type='text' class="form-control pull-right" id="unified-search"
							placeholder="Enter Policy No, Risk ID, DR No, Client, Client ID Number, KRA Pin, Client Cif, Insurance Company or Product" />
				 </div>
			 </div>
		 </div>
		 <div class="form-group">
			 <a class="btn btn-info pull-right" style="margin-right: 10px; "  id="btn-clear-search">Clear Search </a>
			 <input type="button" class="btn btn-info pull-right" style="margin-right: 10px; float: right;" value="Search"
					id="btn-search-policies">
		 </div>

	 </form>

		<table id="pol_enquiry_tbl" class="table table-striped" style="width:100%">
		<thead>
			<tr>
				<th width="5%"></th>
                <th>Policy No</th>
				<th>Endors. No</th>
				<th>Product</th>
				<th>Cover From</th>
				<th>Cover To</th>
				<th>Client</th>
				<th>Intermediary</th>
				<th>Transaction Type</th>
				<th>Currency</th>
				<th>Prep. By</th>
				<th>Authorizer Comments</th>
			</tr>
		</thead>
	</table>
		</div>
</div>
<script>
	document.addEventListener("DOMContentLoaded", function() {
		document.getElementById("btn-clear-search").addEventListener("click", function() {
			// Clear input
			document.getElementById("unified-search").value = "";

			if ($.fn.DataTable.isDataTable('#pol_enquiry_tbl')) {
				var table = $('#pol_enquiry_tbl').DataTable();
				location.reload();
			}
		});
	});
</script>
