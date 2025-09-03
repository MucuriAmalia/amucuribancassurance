<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<script type="text/javascript" src="<c:url value="/js/modules/setups/utilitysetups.js"/>"></script>

  <div class="x_panel">
     <button class="btn btn-success btn btn-info float-right" id="btn-add-modes">New</button>
       <div class="x_title">
		   <h2><i class="fa fa-bars"></i> Payment Modes List</h2>
		   <div class="clearfix"></div>
	</div>
	  <div class="table-responsive">
	<table id="paymodetbl" class="table table-striped" style="width: 100%">
		<thead>
			<tr class="headings">

				<th>Sht Desc</th>
				<th>Payment Mode Desc</th>
				<th>Min Value</th>
				<th>Max Value</th>
				<th>Supports Check</th>
				<th width="5%"></th>
				<th width="5%"></th>
			</tr>
		</thead>
	</table>
		  </div>
	</div>
  <div class="modal fade" id="paymentModesModal" tabindex="-1" role="dialog"
		aria-labelledby="paymodesModalLabel" aria-hidden="true">
		<div class="modal-dialog">
			<div class="modal-content">
				<div class="modal-header">
				    <h4 class="modal-title" id="paymodesModalLabel">
                        Edit/Add Payment Modes
                    </h4>
					<button type="button" class="close" data-dismiss="modal"
						aria-label="Close">
						<span aria-hidden="true">&times;</span>
					</button>

				</div>
				<div class="modal-body" id="branch_model">
					<form id="pay-modes-form" class="form-horizontal">
						<input type="hidden" class="form-control" id="pm-id" name="pmId">
						<div class="item form-group">
							<label for="sht-desc" class="col-md-3 label-align">Sht Desc</label>

							<div class="col-md-8">
								<input type="text" class="form-control" id="sht-desc"
									name="pmShtDesc"  required>
							</div>
						</div>
						<div class="item form-group">
							<label for="description" class="col-md-3 label-align">Description</label>

							<div class="col-md-8">
							    <input type="text" class="form-control" id="description"
									name="pmDesc"  required>
							</div>
						</div>
						<div class="item form-group">
							<label for="min-val" class="col-md-3 label-align">Min Value</label>

							<div class="col-md-8">
							    <input type="text" class="form-control number" id="min-val"
									name="pmMinValue" required>
							</div>
						</div>
						<div class="item form-group">
							<label for="max-val" class="col-md-3 label-align">Max Value</label>

							<div class="col-md-8">
							    <input type="text" class="form-control" id="max-val"
									name="pmMaxValue"  required>
							</div>
						</div>
						<div class="item form-group">
							<label for="py-support-check" class="col-md-3 label-align">Supports Check</label>
							<div class="col-md-8">
								<select class="form-control" id="py-support-check" name="supportsCheque">
									<option value="">Select Value</option>
									<option value="Y">Yes</option>
									<option value="N">No</option>
								</select>
							</div>
						</div>

					</form>
				</div>
				<div class="modal-footer">
					<button data-loading-text="Saving..." id="savePaymentModes"
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
	