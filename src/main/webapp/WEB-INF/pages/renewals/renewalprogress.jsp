<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<script type="text/javascript" src="<c:url value="/js/modules/utils/select2builder.js"/>"></script>
<script type="text/javascript" src="<c:url value="/libs/rivets/rivets.js"/>"></script>
<script type="text/javascript" src="<c:url value="/js/modules/renewals/renewalprogress.js"/>"></script>
<div class="x_content">

  <div class="x_title">
	<h4>Renewal in Progress Transactions</h4>
	</div>
	 <div class="x_panel">
	<form id="search-form" class="form-horizontal form-label-left">
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
				<label for="brn-id" class="col-md-5 label-align">Select Binder</label>

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
				<label for="brn-id" class="col-md-5 label-align">Risk ID</label>

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
	</div>
	<div class="x_panel">
		<div class="card-box table-responsive">
	<form id="renewal-frm">	

</form>	
	
	 <input type="button" class="btn btn-info float-right"
			 value="Authorize" id="btn-auth-renewal" 
			 >
	
	 <input type="button" class="btn btn-info float-right"
			 value="Submit" id="btn-make-ready"
			 >
					
	    <input type="button" class="btn btn-info float-right"
			 value="Renewal Notice" id="btn-uw-reports" data-toggle="modal" data-target="#reportsModal"
			 >
	<input type="button" class="btn btn-info float-right"
			 value="Send Email" id="btn-send-email">
			 
			 <input type="button" class="btn btn-info float-right"
			 value="Send SMS" id="btn-send-sms">


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
				<th>Policy Status</th>
				<th width="5%"></th>
			</tr>
		</thead>
	</table>
			</div>
	

</div>
	
	
	
	
	