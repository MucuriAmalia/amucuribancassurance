<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<script type="text/javascript" src="<c:url value="/js/modules/setups/params.js"/>"></script>

  <div class="x_panel">
     <button class="btn btn-success btn btn-info float-right" id="btn-add-parameter">New</button>
       <div class="x_title">
		   <h2><i class="fa fa-bars"></i> System Parameters List</h2>
		   <div class="clearfix"></div>
	</div>
	  <div class="table-responsive">
	<table id="paramtbl" class="table table-striped" style="width: 100%">
		<thead>
			<tr class="headings">

				<th>Parameter Name</th>
				<th>Parameter Value</th>
				<th>Active</th>
				<th>Parameter Desc</th>
				<th width="5%"></th>
			</tr>
		</thead>
	</table>
		  </div>
	</div>
  <div class="modal fade" id="parametersModal" tabindex="-1" role="dialog"
		aria-labelledby="parametersModalLabel" aria-hidden="true">
		<div class="modal-dialog">
			<div class="modal-content">
				<div class="modal-header">
				    <h4 class="modal-title" id="parametersModalLabel">
                        Define System Parameters
                    </h4>
					<button type="button" class="close" data-dismiss="modal"
						aria-label="Close">
						<span aria-hidden="true">&times;</span>
					</button>

				</div>
				<div class="modal-body" id="branch_model">
					<form id="param-form" class="form-horizontal">
						<input type="hidden" class="form-control" id="param-id" name="paramId">
						
						<div class="item form-group">
							<label for="brn-id" class="col-md-3 label-align">Parameter Name</label>

							<div class="col-md-8">
								<input type="text" class="form-control" id="param-name"
									name="paramName"  required>
								<input type="hidden" class="form-control" id="param-hidden-id" name="paramName">
							</div>
						</div>
						<div class="item form-group">
							<label for="unit-id" class="col-md-3 label-align">Parameter Value</label>

							<div class="col-md-8">
							    <input type="text" class="form-control" id="param-value"
									name="paramValue"  required>
							</div>
						</div>
						<div class="item form-group">
							<label for="unit-id" class="col-md-3 label-align">Parameter Desc</label>

							<div class="col-md-8">
							   <textarea rows="3" cols=30 class="form-control" name="paramDesc" id="param-desc"></textarea>
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
					<button data-loading-text="Saving..." id="saveParam"
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
	