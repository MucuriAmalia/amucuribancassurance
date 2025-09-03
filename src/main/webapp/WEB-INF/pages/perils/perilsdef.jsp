<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<script type="text/javascript"
	src="<c:url value="/js/modules/utils/select2builder.js"/>"></script>
<script type="text/javascript"
	src="<c:url value="/js/modules/perils/perils.js"/>"></script>
<div class="x_panel">
	<div class="x_title">
	    <h2><i class="fa fa-bars"></i> Perils and Class Perils</h2>
		<ul class="nav navbar-right panel_toolbox">
			<li><a class="collapse-link"><i class="fa fa-chevron-up"></i></a>
			</li>
		</ul>
		<div class="clearfix"></div>
	</div>
	<div class="x_content">
		<div class="" role="tabpanel" data-example-id="togglable-tabs">
			<ul id="myTab" class="nav nav-tabs bar_tabs" role="tablist">
				<li role="presentation" class="active"><a href="#tab_content1"
					id="home-tab" role="tab" data-toggle="tab" aria-expanded="true">Perils</a>
				</li>
				<li role="presentation" class=""><a href="#tab_content2"
					role="tab" id="profile-tab" data-toggle="tab" aria-expanded="false">Class Perils</a>
				</li>
			</ul>
			<div id="myTabContent" class="tab-content">
				<div role="tabpanel" class="tab-pane active"
					id="tab_content1" aria-labelledby="home-tab">
					<button type="button" class="btn btn-info float-right"
						id="btn-add-perils">New</button>
					<div class="table-responsive">
					<table id="perilList" class="table table-striped" style="width: 100%">
						<thead>
							<tr class="headings">
			
								<th>Peril Id</th>
								<th>Peril Desc</th>
								<th>Peril Type</th>
								<th width="5%"></th>
								<th width="5%"></th>
							</tr>
						</thead>
					</table>
						</div>
					
				</div>
				<div role="tabpanel" class="tab-pane fade" id="tab_content2"
					aria-labelledby="profile-tab" >
					<form class="form-horizontal">
			<div class="item form-group">
				<label for="brn-id" class="col-md-3 label-align">Select Sub Class</label>

				<div class="col-md-4">
					<input type="hidden" id="sub-code"/>
					<div id="sub-class-def" class="form-control"
						select2-url="<c:url value="/protected/setups/clauses/subclassSelect"/>">

					</div>
					<input type="hidden" id="class-pk">

				</div>
			</div>

		</form>
					<button type="button" class="btn btn-info float-right"
						id="btn-add-sub-perils">New</button>
					<div class="table-responsive">
					<table id="subperilsTbl" class="table table-striped" style="width: 100%">
						<thead>
							<tr class="headings">
			
								<th>Peril</th>
								<th>Type</th>
								<th>SI/Limit</th>
								<th>Limit Description</th>
								<th>Person Limit</th>
								<th>Claim Limit</th>
								<th>Excess Type</th>
								<th>Excess</th>
								<th>Expire on Claim</th>
								<th width="5%"></th>
								<th width="5%"></th>
							</tr>
						</thead>
					</table>
						</div>
				</div>
			</div>
		</div>
	</div>
</div>



<div class="modal fade" id="perilsModal" tabindex="-1" role="dialog"
		aria-labelledby="perilsModalLabel" aria-hidden="true">
		<div class="modal-dialog">
			<div class="modal-content">
				<div class="modal-header">
				    <h4 class="modal-title" id="perilsModalLabel">
                        Edit/Add Peril
                    </h4>
					<button type="button" class="close" data-dismiss="modal"
						aria-label="Close">
						<span aria-hidden="true">&times;</span>
					</button>

				</div>
				<div class="modal-body">
				   
					<form id="perils-form" class="form-horizontal">
						<input type="hidden" class="form-control" id="peril-code" name="perilCode">
						<div class="item form-group">
							<label for="brn-id" class="col-md-3 label-align">Peril ID</label>

							<div class="col-md-8">
								<input type="text" class="form-control" id="peril-id"
									name="perilShtDesc"  required>
							</div>
						</div>
						<div class="item form-group">
							<label for="cou-name" class="col-md-3 label-align">Peril Description</label>

							<div class="col-md-8">
								<input type="text" class="editUserCntrls form-control"
									id="peril-name" name="perilDesc" 
									required>
							</div>
						</div>	
						<div class="item form-group">
							<label for="rate-taxable" class="col-md-3 label-align">Peril Type</label>
							<div class="col-md-9">
							     <select class="form-control" id="peril-type" name="perilType" required>
							        <option value="">Select Peril Type</option>
							        <option value="C">Claim Payment</option>
							        <option value="F">Fee Payment</option>
							        <option value="B">Both</option>
								  </select>
							</div>
						</div>
						
						
						
					</form>
				</div>
				<div class="modal-footer">
					<button data-loading-text="Saving..." id="savePerilsBtn"
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


<div class="modal fade" id="subperilsModal" tabindex="-1" role="dialog"
	 aria-labelledby="subperilsModalLabel" aria-hidden="true">
	<div class="modal-dialog modal-lg">
		<div class="modal-content">
		    <h4 class="modal-title" id="subperilsModalLabel">
                Edit/Add Sub Class Peril
            </h4>
			<div class="modal-header">
				<button type="button" class="close" data-dismiss="modal"
						aria-label="Close">
					<span aria-hidden="true">&times;</span>
				</button>

			</div>
			<div class="modal-body">

				<form id="sub-perils-form" class="form-horizontal">
					<input type="hidden" class="form-control" id="lbl-sper-id" name="sperId">
					<input type="hidden" class="form-control" id="txt-sub-id" name="subclass">
					<div class="item form-group">
						<div class="col-md-6 col-xs-12">
						<label for="brn-id" class="col-md-5 label-align">Peril ID</label>

						<div class="col-md-7">
							<input type="hidden" id="sub-peril-code" name="peril"/>
							<input type="hidden" id="sub-peril-name"/>
							<div id="peril-def" class="form-control"
								 select2-url="<c:url value="/protected/setups/perils/selectperils"/>">

							</div>
						</div>
						</div>
						<div class="col-md-6 col-xs-12">
						<label for="cou-name" class="col-md-5 label-align">Type</label>

						<div class="col-md-7">
							<select class="form-control" id="sper-type" name="perilType" required>
								<option value="">Select Peril Type</option>
								<option value="S">Self</option>
								<option value="T">Third Party</option>
								<option value="B">Both</option>
							</select>
						</div>
							</div>
					</div>
					<div class="item form-group">
						<div class="col-md-6 col-xs-12">
						<label for="rate-taxable" class="col-md-5 label-align">SI/Limit</label>
						<div class="col-md-7">
							<select class="form-control" id="si-limit" name="siOrLimit" required>
								<option value="">Select SI/Limit</option>
								<option value="RS">Risk Sum Insured</option>
								<option value="SS">Section SI/Limit</option>
								<option value="PL">Peril Limit</option>
								<option value="UL">Unlimited</option>
								<option value="EX">Extension</option>
								<option value="GT">GPA Total/Temp Disability</option>
								<option value="PD">Permanent Disability</option>
								<option value="WT">Workmen Total/Temp Disability</option>
							</select>
						</div>
							</div>
						<div class="col-md-6 col-xs-12">
							<label for="cou-name" class="col-md-5 label-align">Dep. on Loss Type</label>

							<div class="col-md-7 checkbox">
								<label>
									<input type="checkbox" name="dependLossType" id="depend-loss-type">
								</label>
							</div>
							</div>
					</div>
					<div class="item form-group">
						<div class="col-md-6 col-xs-12">
							<label for="cou-name" class="col-md-5 label-align">Expire on Claim</label>

							<div class="col-md-7 checkbox">
								<label>
									<input type="checkbox" name="expireOnClaim" id="expire-clm">
								</label>
							</div>
							</div>
						<div class="col-md-6 col-xs-12">
							<label for="brn-id" class="col-md-5 label-align">Limit Description</label>

							<div class="col-md-7">
								<input type="text" class="form-control" id="limit-desc"
									   name="description"  required>
							</div>

						</div>
					</div>
					<div class="item form-group">
						<div class="col-md-6 col-xs-12">
							<label for="cou-name" class="col-md-5 label-align">Person Limit</label>

							<div class="col-md-7">
								<input type="number" class="form-control" id="person-limit"
									   name="limit"  required>
							</div>
						</div>
						<div class="col-md-6 col-xs-12">
							<label for="cou-name" class="col-md-5 label-align">Deprecit. Percent.</label>

							<div class="col-md-7">
								<input type="number" class="form-control" id="dep-perce"
									   name="deprecPercent"  min="0" max="100">
							</div>

						</div>
					</div>
					<div class="item form-group">
						<div class="col-md-6 col-xs-12">
							<label for="cou-name" class="col-md-5 label-align">Limit Amount</label>

							<div class="col-md-7">
								<input type="number" class="form-control" id="limit-amount"
									   name="claimLimit"  required>
							</div>
						</div>
						<div class="col-md-6 col-xs-12">
							<label for="cou-name" class="col-md-5 label-align">Mandatory</label>

							<div class="col-md-7 checkbox">
								<label>
									<input type="checkbox" name="mandatory" id="mandatory">
								</label>
							</div>
							</div>
						</div>
					<div class="item form-group">
						<div class="col-md-6 col-xs-12">
							<label for="cou-name" class="col-md-5 label-align">Excess Type</label>

							<div class="col-md-7">
								<select class="form-control" id="excess-type" name="excessType" required>
									<option value="">Select Excess Type</option>
									<option value="P">Percentage</option>
									<option value="A">Amount</option>
								</select>
							</div>
						</div>
						<div class="col-md-6 col-xs-12">
							<label for="cou-name" class="col-md-5 label-align">Excess</label>

							<div class="col-md-7">
								<input type="number" class="form-control" id="excess-amount"
									   name="excess"  required>
							</div>

						</div>
					</div>
					<div class="item form-group">
						<div class="col-md-6 col-xs-12">
							<label for="cou-name" class="col-md-5 label-align">Excess Min</label>

							<div class="col-md-7">
								<input type="number" class="form-control" id="excess-min"
									   name="excessMin">
							</div>

						</div>
						<div class="col-md-6 col-xs-12">
							<label for="cou-name" class="col-md-5 label-align">Excess Max</label>

							<div class="col-md-7">
								<input type="number" class="form-control" id="excess-max"
									   name="excessMax">
							</div>

						</div>
					</div>
					<div class="item form-group">
						<div class="col-md-6 col-xs-12">
							<label for="cou-name" class="col-md-5 label-align">Claim Excess Type</label>

							<div class="col-md-7">
								<select class="form-control" id="clm-excess-type" name="claimExcessType">
									<option value="">Select Claim Excess Type</option>
									<option value="P">Percentage</option>
									<option value="A">Amount</option>
								</select>
							</div>
						</div>
						<div class="col-md-6 col-xs-12">
							<label for="cou-name" class="col-md-5 label-align">Claim Excess</label>

							<div class="col-md-7">
								<input type="number" class="form-control" id="clm-excess-amount"
									   name="claimExcess">
							</div>

						</div>
					</div>
					<div class="item form-group">
						<div class="col-md-6 col-xs-12">
							<label for="cou-name" class="col-md-5 label-align">Claim Excess Min</label>

							<div class="col-md-7">
								<input type="number" class="form-control" id="clm-excess-min"
									   name="claimExcessMin" >
							</div>

						</div>
						<div class="col-md-6 col-xs-12">
							<label for="cou-name" class="col-md-5 label-align">Claim Excess Max</label>

							<div class="col-md-7">
								<input type="number" class="form-control" id="clm-excess-max"
									   name="claimExcessMax">
							</div>

						</div>
					</div>
				</form>
			</div>
			<div class="modal-footer">
				<button data-loading-text="Saving..." id="saveSubPerilsBtn"
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
