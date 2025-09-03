<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>

<!-- Required JS scripts -->
<script type="text/javascript" src="<c:url value='/js/modules/utils/select2builder.js'/>"></script>
<script type="text/javascript" src="<c:url value='/libs/rivets/rivets.js'/>"></script>
<script type="text/javascript" src="<c:url value="/js/modules/revitems/uwrevitems.js"/>"></script>

<div class="x_panel">
	<div class="x_title">
		<h2><i class="fa fa-bars"></i> Underwriter Revenue Items</h2>
		<ul class="nav navbar-right panel_toolbox">
			<li><a class="collapse-link"><i class="fa fa-chevron-up"></i></a></li>
		</ul>
		<div class="clearfix"></div>
	</div>

	<div class="x_content">

		<!-- Add/Edit Button -->
		<div class="mb-3">
			<button id="openAccountsModal" class="btn btn-primary">Add/Edit Underwriter Debit & Credit Accounts</button>
		</div>

		<!-- Accounts Modal -->
		<div class="modal fade" id="accountsModal" tabindex="-1" role="dialog" aria-hidden="true">
			<div class="modal-dialog">
				<div class="modal-content">
					<div class="modal-header">
						<h5 class="modal-title">Set Debit & Credit Accounts</h5>
						<button type="button" class="close" data-dismiss="modal">&times;</button>
					</div>
					<div class="modal-body">
						<form id="accountsForm">
							<div class="form-group">
								<label>Underwriter</label>
								<select id="accountSelect" class="form-control" style="width: 100%"></select>
							</div>
							<div class="form-group">
								<label>Sub Class Group</label>
								<select id="prd-group" class="form-control" style="width: 100%"></select>
							</div>
							<div class="form-group">
								<label>Debit Account</label>
								<select id="debitAccount" class="form-control" style="width: 100%"></select>
							</div>
							<div class="form-group">
								<label>Credit Account</label>
								<select id="creditAccount" class="form-control" style="width: 100%"></select>
							</div>
						</form>
					</div>
					<div class="modal-footer">
						<button type="button" id="saveAccountsBtn" class="btn btn-primary">Save</button>
						<button type="button" class="btn btn-secondary" data-dismiss="modal">Cancel</button>
					</div>
				</div>
			</div>
		</div>

		<!-- DataTable -->
		<h2>Commission Revenue Items</h2>
		<div class="card-box table-responsive">
			<table id="commRecvTable" class="table table-striped" style="width: 100%">
				<thead>
				<tr>
					<th><input type="checkbox" id="selectAll"></th>
					<th>Account Definition</th>
					<th>Sub Class</th>
					<th>Debit Account</th>
					<th>Credit Account</th>
				</tr>
				</thead>
				<tbody></tbody>
			</table>
		</div>
	</div>
</div>

<!-- Bulk Revenue Items Modal -->
<div class="modal fade" id="bulkRevItemsModal" tabindex="-1" role="dialog" aria-labelledby="bulkRevItemsModalLabel" aria-hidden="true">
	<div class="modal-dialog modal-lg">
		<div class="modal-content">
			<div class="modal-header">
				<h4 class="modal-title" id="bulkRevItemsModalLabel">Add Revenue Items</h4>
				<button type="button" class="close" data-dismiss="modal" aria-label="Close"><span>&times;</span></button>
			</div>
			<div class="modal-body">
				<div style="height: 300px; overflow-y: scroll;">
					<table class="table table-striped table-hover table-bordered" id="revItemsTbl">
						<thead>
						<tr>
							<th width="5%"></th>
							<th width="5%">Code</th>
							<th width="12%">Rev. Item Desc</th>
							<th width="12%">Dr Account</th>
							<th width="12%">Cr Account</th>
						</tr>
						</thead>
						<tbody></tbody>
					</table>
				</div>
				<form id="bulk-rev-items-form">
					<input type="hidden" id="rev-prg-code" name="prgCode" />
				</form>
			</div>
			<div class="modal-footer">
				<button id="saverevItemsBtn" type="button" class="btn btn-success" style="display: none">Save</button>
				<button id="saverevItemsBtn2" type="button" class="btn btn-success" style="display: none">Save</button>
				<button type="button" class="btn btn-secondary" data-dismiss="modal">Cancel</button>
			</div>
		</div>
	</div>
</div>

<!-- Single Revenue Item Edit Modal -->
<div class="modal fade" id="revitemsModal" tabindex="-1" role="dialog" aria-labelledby="revitemsModalLabel" aria-hidden="true">
	<div class="modal-dialog">
		<div class="modal-content">
			<div class="modal-header">
				<h4 class="modal-title" id="revitemsModalLabel">Edit Revenue Item</h4>
				<button type="button" class="close" data-dismiss="modal"><span>&times;</span></button>
			</div>
			<div class="modal-body">
				<form id="update-rev-items-form" class="form-horizontal">
					<input type="hidden" id="rev-code" name="revenueId" />
					<input type="hidden" id="rev-prgcode-code" name="prodGroup" />

					<div class="form-group row">
						<label class="col-md-3 col-form-label">Code</label>
						<div class="col-md-8">
							<input type="text" class="form-control" id="rev-item-id" name="item" readonly>
						</div>
					</div>

					<div class="form-group row">
						<label class="col-md-3 col-form-label">Description</label>
						<div class="col-md-8">
							<input type="text" class="form-control" id="rev-item-desc" readonly>
						</div>
					</div>

					<div class="form-group row">
						<label class="col-md-3 col-form-label">Debit Account</label>
						<div class="col-md-8">
							<input type="hidden" name="drAccount" id="dr-account-code" />
							<input type="hidden" id="dr-account-name" />
							<input type="hidden" id="dr-account-cd" />
							<div id="dr-account-frm" class="form-control"
								 select2-url="<c:url value='/protected/setups/revitems/selGlAccount'/>"></div>
						</div>
					</div>

					<div class="form-group row">
						<label class="col-md-3 col-form-label">Credit Account</label>
						<div class="col-md-8">
							<input type="hidden" name="crAccount" id="cr-account-code" />
							<input type="hidden" id="cr-account-name" />
							<input type="hidden" id="cr-account-cd" />
							<div id="cr-account-frm" class="form-control"
								 select2-url="<c:url value='/protected/setups/revitems/selGlAccount'/>"></div>
						</div>
					</div>
				</form>
			</div>
			<div class="modal-footer">
				<button id="updateRevItemsBtn" type="button" class="btn btn-success">Save</button>
				<button type="button" class="btn btn-secondary" data-dismiss="modal">Cancel</button>
			</div>
		</div>
	</div>
</div>


<script>
	$(document).ready(function () {
		// Initialize Commission Revenue Table
		$('#commRecvTable').DataTable({
			processing: true,
			serverSide: true,
			ajax: {
				url: SERVLET_CONTEXT + "/protected/setups/revitems/getAllInsurers",
				type: "GET"
			},
			columns: [
				{ data: "revenueId", defaultContent: "" },
				{ data: "accountDef.name", defaultContent: "" },
				{ data: "prodGroup.subDesc", defaultContent: "" },
				{
					data: null,
					render: function (data, type, row) {
						let name = row.drAccount?.name || "";
						let code = row.drAccount?.code || "";
						return name+" ("+code+")";
					}
				},
				{
					data: null,
					render: function (data, type, row) {
						let name = row.crAccount?.name || "";
						let code = row.crAccount?.code || "";
						return name+" ("+code+")";
					}
				}
			]
		});

		// Load Underwriters into dropdown
		$.ajax({
			url: SERVLET_CONTEXT +"/protected/setups/allaccounts/1?draw=1&columns%5B0%5D%5Bdata%5D=shtDesc&columns%5B0%5D%5Bname%5D=&columns%5B0%5D%5Bsearchable%5D=true&columns%5B0%5D%5Borderable%5D=true&columns%5B0%5D%5Bsearch%5D%5Bvalue%5D=&columns%5B0%5D%5Bsearch%5D%5Bregex%5D=false&columns%5B1%5D%5Bdata%5D=name&columns%5B1%5D%5Bname%5D=&columns%5B1%5D%5Bsearchable%5D=true&columns%5B1%5D%5Borderable%5D=true&columns%5B1%5D%5Bsearch%5D%5Bvalue%5D=&columns%5B1%5D%5Bsearch%5D%5Bregex%5D=false&columns%5B2%5D%5Bdata%5D=phoneNo&columns%5B2%5D%5Bname%5D=&columns%5B2%5D%5Bsearchable%5D=true&columns%5B2%5D%5Borderable%5D=true&columns%5B2%5D%5Bsearch%5D%5Bvalue%5D=&columns%5B2%5D%5Bsearch%5D%5Bregex%5D=false&columns%5B3%5D%5Bdata%5D=pinNo&columns%5B3%5D%5Bname%5D=&columns%5B3%5D%5Bsearchable%5D=true&columns%5B3%5D%5Borderable%5D=true&columns%5B3%5D%5Bsearch%5D%5Bvalue%5D=&columns%5B3%5D%5Bsearch%5D%5Bregex%5D=false&columns%5B4%5D%5Bdata%5D=status&columns%5B4%5D%5Bname%5D=&columns%5B4%5D%5Bsearchable%5D=true&columns%5B4%5D%5Borderable%5D=true&columns%5B4%5D%5Bsearch%5D%5Bvalue%5D=&columns%5B4%5D%5Bsearch%5D%5Bregex%5D=false&columns%5B5%5D%5Bdata%5D=updatedBy&columns%5B5%5D%5Bname%5D=&columns%5B5%5D%5Bsearchable%5D=true&columns%5B5%5D%5Borderable%5D=true&columns%5B5%5D%5Bsearch%5D%5Bvalue%5D=&columns%5B5%5D%5Bsearch%5D%5Bregex%5D=false&columns%5B6%5D%5Bdata%5D=accountTypeName&columns%5B6%5D%5Bname%5D=&columns%5B6%5D%5Bsearchable%5D=true&columns%5B6%5D%5Borderable%5D=true&columns%5B6%5D%5Bsearch%5D%5Bvalue%5D=&columns%5B6%5D%5Bsearch%5D%5Bregex%5D=false&columns%5B7%5D%5Bdata%5D=acctId&columns%5B7%5D%5Bname%5D=&columns%5B7%5D%5Bsearchable%5D=true&columns%5B7%5D%5Borderable%5D=true&columns%5B7%5D%5Bsearch%5D%5Bvalue%5D=&columns%5B7%5D%5Bsearch%5D%5Bregex%5D=false&columns%5B8%5D%5Bdata%5D=acctId&columns%5B8%5D%5Bname%5D=&columns%5B8%5D%5Bsearchable%5D=true&columns%5B8%5D%5Borderable%5D=true&columns%5B8%5D%5Bsearch%5D%5Bvalue%5D=&columns%5B8%5D%5Bsearch%5D%5Bregex%5D=false&order%5B0%5D%5Bcolumn%5D=0&order%5B0%5D%5Bdir%5D=asc&start=0&length=10&search%5Bvalue%5D=&search%5Bregex%5D=false&_=1748030051104",
			type: "GET",
			dataType: "json",
			success: function (response) {
				const select = $("#accountSelect");
				response.data.forEach(account => {
					select.append($("<option></option>").val(account.acctId).text(account.name));
				});
			},
			error: function (xhr, status, error) {
				console.error("Error loading underwriters:", error);
			}
		});

		// Initialize #prd-group as
		$.ajax({
			url: SERVLET_CONTEXT +"/protected/setups/revitems/selSubclass/?term=&page=0&size=500&sort=subDesc",
			type: 'GET',
			dataType: 'json',
			success: function (response) {
				const select = $('#prd-group');
				response.content.forEach(item => {
					select.append($("<option></option>").val(item.subId).text(item.subDesc));
				});
			}
		});


		// Open modal
		$("#openAccountsModal").click(function () {
			const underwriter = $("#accountSelect").val();
			const productGroup = $("#prd-group").val();

			$("#accountSelect").val(underwriter);
			$("#prd-group").val(productGroup);

			loadAccountsOptions("#debitAccount");
			loadAccountsOptions("#creditAccount");

			$("#accountsModal").modal("show");
		});

		// Load GL accounts into dropdowns
		function loadAccountsOptions(selector) {
			$.ajax({
				url: SERVLET_CONTEXT + "/protected/setups/revitems/selGlAccount?term=&page=0&size=500&sort=name",
				type: "GET",
				dataType: "json",
				success: function (response) {
					const select = $(selector);
					select.empty();
					select.append('<option value="">-- Select Account --</option>');
					response.content.forEach(account => {
						const label = account.name+' ('+account.code+')' ;
						select.append($("<option></option>").val(account.coId).text(label));
					});
				},
				error: function () {
					alert("Failed to load accounts");
				}
			});
		}
		$('#accountSelect').select2();
		$('#prd-group').select2();
		$('#debitAccount').select2();
		$('#creditAccount').select2();

		// Save accounts button handler
		$("#saveAccountsBtn").click(function () {
			const underwriter = $("#accountSelect").val();
			const debitAccount = $("#debitAccount").val();
			const creditAccount = $("#creditAccount").val();
			const prgId = $("#prd-group").val();

			if (!debitAccount || !creditAccount) {
				alert("Please select both Debit and Credit accounts");
				return;
			}

			const payload = {
				underwriterId: underwriter,
				debitAccount: debitAccount,
				creditAccount: creditAccount,
				prodGroup: prgId
			};

			$.ajax({
				url: SERVLET_CONTEXT + "/protected/setups/revitems/createUwRevenueItem",
				type: "POST",
				contentType: "application/json",
				data: JSON.stringify(payload),
				success: function () {
					Swal.fire({
						title: "Success!",
						text: "Accounts saved successfully.",
						icon: "success",
						confirmButtonText: "OK"
					}).then(() => location.reload());
					$("#accountsModal").modal("hide");
				},
				function(jqXHR, textStatus, errorThrown) {
					Swal.fire({
						title: "Error",
						icon: "error",
						text: "An error occurred. likely due to duplicate entries.",
						confirmButtonText: "OK"
					});
				}
			});
		});
	});
</script>
