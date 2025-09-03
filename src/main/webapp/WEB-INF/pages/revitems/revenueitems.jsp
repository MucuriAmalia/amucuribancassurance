<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<script type="text/javascript" src="<c:url value="/js/modules/utils/select2builder.js"/>"></script>
<script type="text/javascript" src="<c:url value="/libs/rivets/rivets.js"/>"></script>
<script type="text/javascript"
	src="<c:url value="/js/modules/revitems/revitems.js"/>"></script>
<div class="x_panel">
   <div class="x_title">
		<h2><i class="fa fa-bars"></i> Revenue Items</h2>
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
														  id="home-tab" role="tab" data-toggle="tab" aria-expanded="true">Global level</a>
				</li>
				<li role="presentation" class=""><a href="#tab_content2"
													role="tab" id="profile-tab" data-toggle="tab" aria-expanded="false">Sub Class Level</a>
				</li>
			</ul>
			<div id="myTabContent" class="tab-content">
				<div role="tabpanel" class="tab-pane active"
					 id="tab_content1" aria-labelledby="home-tab">
					<button type="button" class="btn btn-info float-left" id="btn-add-bulk-rev-item">Bulk Edit</button>
					<div class="table-responsive">
						<table id="revenueItemsGlobalList" class="table table-striped" style="width:100%">
							<thead>
							<tr>

								<th>Revenue Item ID</th>
								<th>Revenue Item Desc</th>
								<th>Debit Account</th>
								<th>Credit Account</th>
								<th width="5%"></th>
								<th width="5%"></th>
							</tr>
							</thead>
						</table>
					</div>
				</div>
				<div role="tabpanel" class="tab-pane fade" id="tab_content2"
					 aria-labelledby="profile-tab" >

					<form id="prg-grp-form" class="form-horizontal">
						<div class="row">
				      <div class="col-md-6 col-xs-12">
						<div class="item form-group form-required">
								<label for="brn-id" class="col-md-5 label-align">Select
									Sub Class</label>

								<div class="col-md-7">
									<input type="hidden" id="prg-id"/>
									<div id="prd-group" class="form-control"
										 select2-url="<c:url value="/protected/setups/revitems/selSubclass"/>" >

									</div>

								</div>
							</div>
						</div>
							<div class="x_panel">
								<div class="x_title">
									<h2>Revenue Items</h2>
									<ul class="nav navbar-right panel_toolbox">
										<li><a class="collapse-link"><i class="fa fa-chevron-up"></i></a>
										</li>
									</ul>
									<div class="clearfix"></div>
								</div>
								<div class="x_content">
									<input type="hidden" id="prg-pk">
									<button type="button" class="btn btn-info float-left" id="btn-add-rev-item">Bulk Edit</button>
										<br>
										<table id="revenueItemsList" class="table table-striped" style="width:100%">
											<thead>
											<tr>

												<th>Revenue Item ID</th>
												<th>Revenue Item Desc</th>
												<th>Debit Account</th>
												<th>Credit Account</th>
												<th width="5%"></th>
												<th width="5%"></th>
											</tr>
											</thead>
										</table>
								</div>
							</div>

						</div>
					</form>
				</div>
			</div>

			</div>
		</div>

	
	
	
	<div class="modal fade" id="bulkRevItemsModal" tabindex="-1" role="dialog"
	aria-labelledby="bulkRevItemsModalLabel" aria-hidden="true">
	<div class="modal-dialog modal-lg">
		<div class="modal-content">
			<div class="modal-header">
			    <h4 class="modal-title" id="bulkRevItemsModalLabel">Add Revenue Items</h4>
				<button type="button" class="close" data-dismiss="modal"
					aria-label="Close">
					<span aria-hidden="true">&times;</span>
				</button>

			</div>
			<div class="modal-body">
			 
          <div style="height: 300px !important; overflow: scroll;">
		  <table class="table table-striped table-hover table-bordered table-fixed" id="revItemsTbl">
			<thead>
				<tr>
					<th width="5%"></th>
				   <th width="5%">Code</th>
					<th width="12%">Rev. Item Desc</th>
					<th width="12%">Dr Account</th>
					<th width="12%">Cr Account</th>
				</tr>
			</thead>
			<tbody>
			
			</tbody>
			</table>
			</div>
			  <form id="bulk-rev-items-form">
			     <input type="hidden" id="rev-prg-code" name="prgCode"/>
			  </form>
			</div>
			<div class="modal-footer">
				<button data-loading-text="Saving..." id="saverevItemsBtn" style="display: none"
					type="button" class="btn btn-success">Save</button>
				<button data-loading-text="Saving..." id="saverevItemsBtn2" style="display: none"
						type="button" class="btn btn-success">Save</button>
				<button type="button" class="btn btn-default" data-dismiss="modal">
					Cancel</button>
			</div>
		</div>
	</div>
</div>



<div class="modal fade" id="revitemsModal" tabindex="-1" role="dialog"
	aria-labelledby="revitemsModalLabel" aria-hidden="true">
	<div class="modal-dialog">
		<div class="modal-content">
			<div class="modal-header">
			    <h4 class="modal-title" id="revitemsModalLabel">Edit Revenue Item</h4>
				<button type="button" class="close" data-dismiss="modal"
					aria-label="Close">
					<span aria-hidden="true">&times;</span>
				</button>

			</div>
			<div class="modal-body" id="branch_model">

				<form id="update-rev-items-form" class="form-horizontal">
					<input type="hidden" class="form-control" id="rev-code" name="revenueId">
					<input type="hidden" class="form-control" id="rev-prgcode-code" name="prodGroup">
					<div class="item form-group">
						<label for="brn-id" class="col-md-3 label-align">
							Code</label>

						<div class="col-md-8">
							<input type="text" class="form-control" id="rev-item-id"
								name="item" readonly>
						</div>
					</div>
					<div class="item form-group">
						<label for="cou-name" class="col-md-3 label-align">
							Description</label>

						<div class="col-md-8">
							<input type="text" class="editUserCntrls form-control"
								id="rev-item-desc"  readonly>
						</div>
					</div>
					<div class="item form-group">
						<label for="cou-name" class="col-md-3 label-align">
							Debit Account</label>

						<div class="col-md-8">
							<input type="hidden" name="drAccount" id="dr-account-code">
							<input type="hidden" id="dr-account-name">
							<input type="hidden" id="dr-account-cd">
							<div id="dr-account-frm" class="form-control"
								 select2-url="<c:url value="/protected/setups/revitems/selGlAccount"/>" >

							</div>
						</div>
					</div>
					<div>
						<label for="cou-name" class="col-md-3 label-align">
							Credit Account</label>

						<div class="col-md-8">
							<input type="hidden" name="crAccount" id="cr-account-code">
							<input type="hidden" id="cr-account-name">
							<input type="hidden" id="cr-account-cd">
							<div id="cr-account-frm" class="form-control"
								 select2-url="<c:url value="/protected/setups/revitems/selGlAccount"/>" >

							</div>
						</div>
					</div>

					

				</form>
			</div>
			<div class="modal-footer">
				
				<button data-loading-text="Saving..." id="updateRevItemsBtn"
					type="button" class="btn btn-success">Save</button>
				<button type="button" class="btn btn-default" data-dismiss="modal">
					Cancel</button>
			</div>
		</div>
	</div>
</div>
