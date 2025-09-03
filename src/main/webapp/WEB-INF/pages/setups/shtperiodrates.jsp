<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<script type="text/javascript" src="<c:url value="/js/modules/utils/select2builder.js"/>"></script>
<script type="text/javascript" src="<c:url value="/libs/rivets/rivets.js"/>"></script>
<script type="text/javascript" src="<c:url value="/js/modules/setups/shtperiod.js"/>"></script>

  <div class="x_panel">
  <div class="x_title">
  	<h2><i class="fa fa-bars"></i>  Short Period Rates List</h2>
    <div class="clearfix"></div>
  </div>
  <div class="" role="tabpanel" data-example-id="togglable-tabs">
			<ul id="myTab" class="nav nav-tabs bar_tabs" role="tablist">
				<li role="presentation" class="active"><a href="#tab_content1"
					id="home-tab" role="tab" data-toggle="tab" aria-expanded="true">Short Periods</a>
				</li>
				<li role="presentation" class=""><a href="#tab_content2"
					role="tab" id="profile-tab" data-toggle="tab" aria-expanded="false">Section Short Periods</a>
				</li>
			</ul>
	<div id="myTabContent" class="tab-content">
				<div role="tabpanel" class="tab-pane active"
					id="tab_content1" aria-labelledby="home-tab">
     <button class="btn btn-success btn btn-info float-right" id="btn-add-sht-period">New</button>
       <div class="x_title">
	<h4>Short Period Rates List</h4>
	</div>
					<div class="table-responsive">
	<table id="sht-period-tbl" class="table table-striped" style="width: 100%">
		<thead>
			<tr class="headings">

				<th>Period From</th>
				<th>Period To</th>
				<th>Rate</th>
				<th>Div Factor</th>
				<th>Active</th>
				<th width="5%"></th>
				<th width="5%"></th>
			</tr>
		</thead>
	</table>
						</div>
	</div>
	
		<div role="tabpanel" class="tab-pane fade"
					id="tab_content2" aria-labelledby="profile-tab">
					  <button class="btn btn-success btn btn-info float-right" id="btn-add-sec-sht-period">New</button>
       <div class="x_title">
	<h4>Section Short Period Rates List</h4>
	</div>
	
	<form id="sec-shtprd-form" class="form-horizontal">
	    <div class="item form-group form-required">
				<div class="col-md-6">
				   <label for="brn-id" class="col-md-5 label-align">
					Select Sub Class Section</label>

				<div class="col-md-7">
		                     <input type="hidden" id="sub-sec-id"/>
		                     <input type="hidden" id="sub-sec-name">
		                        <div id="sub-sec-frm" class="form-control" 
				                                 select2-url="<c:url value="/protected/setups/selSubclassSections"/>" >
				                                 
				               </div> 
				               
				</div>
				</div>
				
				
				</div>
			</form>
			<div class="table-responsive">
			<table id="sec-sht-period-tbl" class="table table-striped" style="width: 100%">
		<thead>
			<tr class="headings">

				<th>Period From</th>
				<th>Period To</th>
				<th>Rate</th>
				<th>Div Factor</th>
				<th>Active</th>
				<th width="5%"></th>
				<th width="5%"></th>
			</tr>
		</thead>
				</table>
	</table>		
					
		</div>
	</div>
	</div>
	</div>
  <div class="modal fade" id="shortPeriodsModal" tabindex="-1" role="dialog"
		aria-labelledby="shortPeriodsModalLabel" aria-hidden="true">
		<div class="modal-dialog">
			<div class="modal-content">
				<div class="modal-header">
				    <h4 class="modal-title" id="shortPeriodsModalLabel">
                        Define Short Period Rates
                    </h4>
					<button type="button" class="close" data-dismiss="modal"
						aria-label="Close">
						<span aria-hidden="true">&times;</span>
					</button>

				</div>
				<div class="modal-body">
					<form id="short-period-form" class="form-horizontal">
						<input type="hidden" class="form-control" id="period-id" name="spCode">
						
						<div class="item form-group">
							<label for="brn-id" class="col-md-3 label-align">Period From</label>

							<div class="col-md-8">
								<input type="number" class="form-control" id="period-from"
									name="periodFrom"  required>
							</div>
						</div>
						<div class="item form-group">
							<label for="unit-id" class="col-md-3 label-align">Period To</label>

							<div class="col-md-8">
							    <input type="number" class="form-control" id="period-to"
									name="periodTo"  required>
							</div>
						</div>
						<div class="item form-group">
							<label for="unit-id" class="col-md-3 label-align">Rate</label>

							<div class="col-md-8">
							   <input type="text" class="form-control" id="period-rate"
									name="rate"  required>
							</div>
						</div>
						<div class="item form-group">
							<label for="unit-id" class="col-md-3 label-align">Division Factor</label>

							<div class="col-md-8">
							   <input type="text" class="form-control" id="period-div-factor"
									name="divFactor"  required>
							</div>
						</div>
						<div class="item form-group">
							<label for="unit-id" class="col-md-3 label-align">Active Indicator</label>

							<div class="col-md-9 checkbox">
							<label>
								 <input type="checkbox" name="active" id="chk-active">
								 </label>
								 
							</div>
						</div>
						
					</form>
				</div>
				<div class="modal-footer">
					<button data-loading-text="Saving..." id="saveShtPeriod"
						type="button" class="btn btn-success">
						Save
					</button>
					<button type="button" class="btn btn-default" data-dismiss="modal">
						Cancel
					</button>
				</div>
			</div>
		</div>
	</div>
	
	
	
	<div class="modal fade" id="secShortPrdsModal" tabindex="-1" role="dialog"
		aria-labelledby="secShortPrdsModalLabel" aria-hidden="true">
		<div class="modal-dialog">
			<div class="modal-content">
				<div class="modal-header">
				    <h4 class="modal-title" id="secShortPrdsModalLabel">
                        Define Section Short Period Rates
                    </h4>
					<button type="button" class="close" data-dismiss="modal"
						aria-label="Close">
						<span aria-hidden="true">&times;</span>
					</button>

				</div>
				<div class="modal-body" id="branch_model">
					<form id="sec-short-period-form" class="form-horizontal">
						<input type="hidden" class="form-control" id="sec-period-id" name="spCode">
						<input type="hidden" class="form-control" id="sub-section-id" name="section">
						<div class="item form-group">
							<label for="brn-id" class="col-md-3 label-align">Period From</label>

							<div class="col-md-8">
								<input type="number" class="form-control" id="sec-period-from"
									name="periodFrom"  required>
							</div>
						</div>
						<div class="item form-group">
							<label for="unit-id" class="col-md-3 label-align">Period To</label>

							<div class="col-md-8">
							    <input type="number" class="form-control" id="sec-period-to"
									name="periodTo"  required>
							</div>
						</div>
						<div class="item form-group">
							<label for="unit-id" class="col-md-3 label-align">Rate</label>

							<div class="col-md-8">
							   <input type="text" class="form-control" id="sec-period-rate"
									name="rate"  required>
							</div>
						</div>
						<div class="item form-group">
							<label for="unit-id" class="col-md-3 label-align">Division Factor</label>

							<div class="col-md-8">
							   <input type="text" class="form-control" id="sec-prd-div-factor"
									name="divFactor"  required>
							</div>
						</div>
						<div class="item form-group">
							<label for="unit-id" class="col-md-3 label-align">Active Indicator</label>

							<div class="col-md-9 checkbox">
							<label>
								 <input type="checkbox" name="active" id="sec-chk-active">
								 </label>
								 
							</div>
						</div>
						
					</form>
				</div>
				<div class="modal-footer">
					<button data-loading-text="Saving..." id="saveSecShtPeriod"
						type="button" class="btn btn-success">
						Save
					</button>
					<button type="button" class="btn btn-default" data-dismiss="modal">
						Cancel
					</button>
				</div>
			</div>
		</div>
	</div>
	