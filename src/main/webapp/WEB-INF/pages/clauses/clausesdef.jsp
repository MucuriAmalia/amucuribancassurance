<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<script type="text/javascript"
		src="<c:url value="/js/modules/utils/select2builder.js"/>"></script>
<script type="text/javascript"
		src="<c:url value="/js/modules/clauses/clauses.js"/>"></script>
<div class="x_panel">
	<div class="x_title">
	    <h2><i class="fa fa-bars"></i> Clauses and Sub Class Clauses</h2>
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
														  id="home-tab" role="tab" data-toggle="tab" aria-expanded="true">Clauses Definition</a>
				</li>
				<li role="presentation" class=""><a href="#tab_content2"
													role="tab" id="profile-tab" data-toggle="tab" aria-expanded="false">Sub Class Clauses</a>
				</li>

			</ul>
			<div id="myTabContent" class="tab-content">
				<div role="tabpanel" class="tab-pane active"
					 id="tab_content1" aria-labelledby="home-tab">
					<div class="item form-group form-required">
						<div class="col-md-6 col-xs-12">
							<label for="txt-clause-type" class="label-align col-md-3">
								Clause Type</label>
							<div class="col-md-4 col-xs-12">
								<select class="form-control" id="txt-clause-type"
								>
									<option value="">Select Type</option>
									<option value="C">Clause</option>
									<option value="L">Limits</option>
									<option value="E">Excess</option>
									<option value="X">Exclusions</option>
									<option value="W">Warranty</option>
								</select>
							</div>
						</div>
						<div class="col-md-6 col-xs-12">
							<button type="button" class="btn btn-info float-right"
									id="btn-add-clauses">New</button>
							<a
									class="btn btn-info float-right"	href="<c:url value='/protected/setups/clauses/clauses_rpt.pdf'/> "
									target="_blank">Report</a>
						</div>
					</div>
					<div class="table-responsive">
						<table id="clauseList" class="table table-striped" style="width: 100%">
							<thead>
							<tr class="headings">

								<th>Clause Id</th>
								<th>Clause Heading</th>
								<th>Clause Type</th>
								<th>Editable?</th>
								<th>Clause Wording</th>
								<th width="5%"></th>
								<th width="5%"></th>
							</tr>
							</thead>
						</table>
					</div>

				</div>
				<div role="tabpanel" class="tab-pane fade" id="tab_content2"
					 aria-labelledby="profile-tab">
					<form class="form-horizontal">
						<div class="item form-group">
							<label for="brn-id" class="col-md-3 label-align">Select
								Sub Class</label>

							<div class="col-md-4">
								<input type="hidden" id="sub-code"/>
								<div id="sub-class-def" class="form-control"
									 select2-url="<c:url value="/protected/setups/clauses/subclassSelect"/>">

								</div>
								<input type="hidden" id="class-pk">

							</div>
						</div>

					</form>
					<div>
						<div class="item form-group">
							<label for="brn-id" class="col-md-1 label-align">Clause Heading</label>

							<div class="col-md-2">
								<input type="text" class="form-control" id="clauses-name-search"
								>
							</div>
							<div class="col-md-1">
								<button  id="searchclause"
										 type="button" class="btn btn-primary">
									Search
								</button>
							</div>
						</div>
						<button type="button" class="btn btn-info float-right"
								id="btn-add-sub-clauses">New</button>
					</div>
					<div class="table-responsive">
						<table id="subclausesList" class="table table-striped" style="width: 100%">
							<thead>
							<tr class="headings">

								<th>Clause Id</th>
								<th>Clause Heading</th>
								<th>Clause Type</th>
								<th>Editable?</th>
								<th>Mandatory?</th>
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


<div class="modal fade" id="clauseModal" tabindex="-1" role="dialog"
	 aria-labelledby="clauseModalLabel" aria-hidden="true">
	<div class="modal-dialog">
		<div class="modal-content">
			<div class="modal-header">
			    <h4 class="modal-title" id="clauseModalLabel">
                    Edit/Add Clause
                </h4>
				<button type="button" class="close" data-dismiss="modal"
						aria-label="Close">
					<span aria-hidden="true">&times;</span>
				</button>

			</div>
			<div class="modal-body">

				<form id="clause-form" class="form-horizontal">
					<input type="hidden" class="form-control" id="clause-code" name="clauId">
					<div class="item form-group">
						<label for="brn-id" class="col-md-3 label-align">Clause ID</label>

						<div class="col-md-8">
							<input type="text" class="form-control" id="clau-id"
								   name="clauShtDesc"  required>
						</div>
					</div>
					<div class="item form-group">
						<label for="clause-name" class="col-md-3 label-align">Clause Heading</label>

						<div class="col-md-8">
							<input type="text" class="editUserCntrls form-control"
								   id="clause-name" name="clauHeading"
								   required>
						</div>
					</div>
					<div class="item form-group">
						<label for="clause-type" class="label-align col-md-3">
							Clause Type</label>
						<div class="col-md-8 col-xs-12">
							<select class="form-control" id="clause-type" name="clauseType"
									required>
								<option value="">Select Clause Type</option>
								<option value="C">Clause</option>
								<option value="L">Limits</option>
								<option value="E">Excess</option>
								<option value="X">Exclusions</option>
								<option value="W">Warranty</option>
							</select>
						</div>
					</div>
					<div class="item form-group">
						<label for="rate-taxable" class="col-md-3 label-align">Editable</label>
						<div class="col-md-9 checkbox">
							<label>
								<input type="checkbox" name="editable" id="chk-cl-editable">
							</label>
						</div>
					</div>
					<div class="item form-group">
						<label for="cla-wording" class="col-md-3 label-align">Clause Wording</label>

						<div class="col-md-8">
							<textarea rows="7" cols="20" class="form-control" id="cla-wording" name="clauWording"></textarea>
						</div>
					</div>




				</form>
			</div>
			<div class="modal-footer">
				<button data-loading-text="Saving..." id="saveClauseBtn"
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

<div class="modal fade" id="subclauseModal" tabindex="-1" role="dialog"
	 aria-labelledby="subclauseModalLabel" aria-hidden="true">
	<div class="modal-dialog">
		<div class="modal-content">
			<div class="modal-header">
			    <h4 class="modal-title" id="subclauseModalLabel">Select Clause</h4>
				<button type="button" class="close" data-dismiss="modal"
						aria-label="Close">
					<span aria-hidden="true">&times;</span>
				</button>

			</div>
			<div class="modal-body">
				<form class="form-horizontal">
					<div class="item form-group">
						<label for="brn-id" class="col-md-3 label-align">Clause</label>

						<div class="col-md-6">
							<input type="text" class="form-control" id="clause-name-search"
							>
						</div>
						<div class="col-md-1">
							<button  id="searchClauses"
									 type="button" class="btn btn-primary">
								Search
							</button>
						</div>
					</div>
				</form>
				<div style="height: 300px !important; overflow: scroll;">
					<table class="table table-striped table-hover table-bordered table-fixed" id="subclausestbl">
						<thead>
						<tr>
							<th width="1%"></th>
							<th width="4%">Clause Id</th>
							<th width="12%">Clause Heading</th>
							<th width="12%">Editable?</th>
						</tr>
						</thead>
						<tbody>

						</tbody>
					</table>
				</div>
				<form id="subcl-clauses-form">
					<input type="hidden" id="cla-sub-code" name="subCode"/>
				</form>
			</div>
			<div class="modal-footer">
				<button data-loading-text="Saving..." id="saveSubclausesBtn"
						type="button" class="btn btn-success">Save</button>
				<button type="button" class="btn btn-default" data-dismiss="modal">
					Cancel</button>
			</div>
		</div>
	</div>
</div>



<div class="modal fade" id="editSubclauseModal" tabindex="-1" role="dialog"
	 aria-labelledby="editSubclauseModalLabel" aria-hidden="true">
	<div class="modal-dialog">
		<div class="modal-content">

			<div class="modal-header">
			    <h4 class="modal-title" id="editSubclauseModalLabel">
                    Edit Clause
                </h4>
				<button type="button" class="close" data-dismiss="modal"
						aria-label="Close">
					<span aria-hidden="true">&times;</span>
				</button>

			</div>
			<div class="modal-body">

				<form id="sub-clause-form" class="form-horizontal">
					<input type="hidden" class="form-control" id="sub-clause-code" name="clauId">
					<input type="hidden" class="form-control" id="sub-sub-code" name="subclass">
					<input type="hidden" class="form-control" id="sub-clause-pk" name="clause">
					<div class="item form-group">
						<label for="brn-id" class="col-md-3 label-align">Clause ID</label>

						<div class="col-md-8">
							<input type="text" class="form-control" id="sub-clau-id"
								   name="clauShtDesc"  disabled>
						</div>
					</div>
					<div class="item form-group">
						<label for="sub-clause-name" class="col-md-3 label-align">Clause Heading</label>

						<div class="col-md-8">
							<input type="text" class="editUserCntrls form-control"
								   id="sub-clause-name"
								   disabled>
						</div>
					</div>
					<div class="item form-group">
						<label for="rate-taxable" class="col-md-3 label-align">Mandatory</label>
						<div class="col-md-9 checkbox">
							<label>
								<input type="checkbox" name="mandatory" id="chk-cl-mandatory">
							</label>
						</div>
					</div>




				</form>
			</div>
			<div class="modal-footer">
				<button data-loading-text="Saving..." id="saveSubCluseBtn"
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