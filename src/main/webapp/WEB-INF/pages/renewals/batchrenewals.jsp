<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<script type="text/javascript" src="<c:url value="/js/modules/utils/select2builder.js"/>"></script>
<script type="text/javascript" src="<c:url value="/libs/rivets/rivets.js"/>"></script>
<script type="text/javascript" src="<c:url value="/js/modules/renewals/batchrenewals.js"/>"></script>
<div class="x_panel">

  <div class="x_title">
	<h4>Batch Renewals</h4>
	</div>
	 
	<form id="search-form" class="form-horizontal">
						<div class="item form-group">

							<div class="col-md-6 col-xs-12">
								<label for="brn-id" class="col-md-5 label-align">Date
									From</label>

								<div class="col-md-7 col-xs-12">
									<div class='input-group date datepicker-input' id="wef-date">
										<input type='text' class="form-control float-right" name="wefDate"
											id="from-date" required />
										<div class="input-group-addon">
											<span class="fa fa-calendar"></span>
										</div>
						           </div>
								</div>
						</div>
						<div class="col-md-6 col-xs-12">
								<label for="brn-id" class="col-md-5 label-align">Date To
									</label>

								<div class="col-md-7 col-xs-12">
									<div class='input-group date datepicker-input' id="cover-to-date">
										<input type='text' class="form-control float-right" name="wetDate"
											id="wet-date" required />
										<div class="input-group-addon">
											<span class="fa fa-calendar"></span>
										</div>
						            </div>
								</div>
							</div>
						</div>
						<div class="item form-group">
							<div class="col-md-6 col-xs-12">
								<label for="brn-id" class="col-md-5 label-align">Select Product</label>

								<div class="col-md-7 col-xs-12">
									<input type='hidden' class="form-control float-right"
										   id="product-search-number" />
									<div id="prd-code" class="form-control"
										 select2-url="<c:url value="/protected/setups/binders/selproducts"/>" >
									</div>
								</div>
							</div>
							<div class="col-md-6 col-xs-12">
								<label for="brn-id" class="col-md-5 label-align">Select Branch</label>

								<div class="col-md-7 col-xs-12">
									<input type='hidden' class="form-control float-right"
										   id="brn-id" />
									<div id="brn-frm" class="form-control"
										 select2-url="<c:url value="/protected/uw/policies/uwbranches"/>" >
									</div>
								</div>
							</div>
						</div>
						<div class="item form-group">
							<div class="col-md-6 col-xs-12">
								<label for="brn-id" class="col-md-5 label-align">Underwriter</label>

								<div class="col-md-7 col-xs-12">
									<input type='hidden' class="form-control float-right"
										   id="agent-search-number" />
									<div id="acc-frm" class="form-control"
										 select2-url="<c:url value="/protected/setups/binders/selAccounts"/>" >
									</div>
								</div>
							</div>
							<div class="col-md-6 col-xs-12">
								<label for="brn-id" class="col-md-5 label-align">Select Contract</label>

								<div class="col-md-7 col-xs-12">
									<input type='hidden' class="form-control float-right"
										   id="risk-binder-code" />
									<div id="binder-frm" class="form-control"
										 select2-url="<c:url value="/protected/reports/uwBinders"/>" >
									</div>
								</div>
							</div>
						</div>
						<div class="item form-group">
							<div class="col-md-6 col-xs-12">
								<label for="brn-id" class="col-md-5 label-align">Client</label>

								<div class="col-md-7 col-xs-12">
									<input type='hidden' class="form-control float-right"
										   id="rev-search-name" />
									<div id="client-frm" class="form-control"
										 select2-url="<c:url value="/protected/uw/policies/uwClients"/>" >

									</div>

								</div>
							</div>
							<div class="col-md-6 col-xs-12">
								<label for="brn-id" class="col-md-5 label-align">Plate Number</label>

								<div class="col-md-7 col-xs-12">
									<input type='hidden' class="form-control float-right"
										   id="risk-search-id" />
									<div id="risk-frm" class="form-control"
										 select2-url="<c:url value="/protected/uw/renewals/selRisks"/>" >

									</div>
								</div>
							</div>

						</div>
						<div class="item form-group">
							<input type="button" class="btn btn-info float-right"
								style="margin-right: 10px;" value="Search"
								id="btn-search-renewals">
						</div>


					</form>
	</div>
	
	<div class="x_panel">

		<div class="card-box table-responsive">
<form id="renewal-frm">	

</form>	
<a href="<c:url value='/protected/uw/renewals/renprogress'/> " class="btn btn-info float-right">Next</a>
<input type="button" class="btn btn-info float-right" value="Transfer to Renewal in Progress" id="btn-transfer">
			<table id="example" class="table table-striped" style="width:100%">
		<thead>
			<tr>
			    <th width="5%"><input name="select_all" value="1" id="ren-select-all" type="checkbox"></th>
                <th>Policy No</th>
				<th>Endors. No</th>
				<th>Product</th>
				<th>Cover From</th>
				<th>Cover To</th>
				<th>Client</th>
				<th>Ins Company</th>
				<th>Currency</th>
				<th></th>
			</tr>
		</thead>
	</table>
	</div>
	

<div class="modal fade" id="errorModal" tabindex="-1" role="dialog"
	 aria-labelledby="errorModalLabel" aria-hidden="true">
	<div class="modal-dialog">
		<div class="modal-content">
			<div class="modal-header">
				<button type="button" class="close" data-dismiss="modal"
						aria-label="Close">
					<span aria-hidden="true">&times;</span>
				</button>
				<h4 class="modal-title" id="errorModalLabel">
					Error
				</h4>
			</div>
			<div class="modal-body text-danger" id="error-report">

			</div>
			</div>
		</div>
	</div>
</div>
