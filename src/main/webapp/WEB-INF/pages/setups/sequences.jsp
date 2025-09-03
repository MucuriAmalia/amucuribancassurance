<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<script type="text/javascript"
	src="<c:url value="/js/modules/setups/sequences.js"/>"></script>
	
	<div class="x_panel">
	 <div class="x_title">
		 <h2><i class="fa fa-bars"></i> System Sequences Set Up</h2>
		 <div class="clearfix"></div>
	</div>
	<form id="seqform" class="form-horizontal form-label-left input_mask">
		
		    <input type="hidden" name="seqId" id="seq-code">
			<div class="item form-group">
				<label for="prefix-name" class="col-md-3 label-align">Prefix</label>
				<div class="col-md-4 col-xs-12">
					<input type="text" class="form-control" id="prefix-name"
						name="seqPrefix" required>
				</div>
			</div>
			<div class="item form-group">
				<label for="last-number" class="col-md-3 label-align">Last
					Value</label>

				<div class="col-md-4 col-xs-12">
					<input type="number" class="editUserCntrls form-control"
						id="last-number" name="lastNumber" disabled>
				</div>
			</div>
			
			<div class="item form-group">
				<label for="next-number" class="col-md-3 label-align">Next Value</label>

				<div class="col-md-4 col-xs-12">
					<input type="number" class="editUserCntrls form-control"
						id="next-number" name="nextNumber" required>
				</div>
			</div>
			

			<div class="item form-group">
				<label for="sel3" class="col-md-3 label-align">Sequence Type</label>

				<div class="col-md-4 col-xs-12">
					<select class="form-control" id="sel3" name="seqType" required>
							        <option value="">Select Sequence Type</option>
							        <option value="PBY">Per Branch Per Year</option>
								    <option value="PBA">Per Branch</option>
								    <option value="PAY">All Branches Per Year</option>
								     <option value="PAA">All Branches</option>
								     <option value="PPA">Per Product Per Branch</option>
								  </select>
				</div>
			</div>
			<div class="item form-group">
				<label for="sel2" class="col-md-3 label-align">Transaction Type</label>

				<div class="col-md-4 col-xs-12">
					<select class="form-control" id="sel2" name="transType" required>
							        <option value="">Select Transaction Type</option>
							        <option value="C">Clients Definition</option>
							        <option value="A">Agents</option>
								    <option value="P">Policies</option>
								    <option value="E">Endorsements</option>
								    <option value="R">Receipts</option>
								    <option value="D">Debit Note</option>
						            <option value="CL">Claim Trans</option>
						            <option value="CP">Claim Payments</option>
						            <option value="RQ">Requisition Payments</option>
						            <option value="M">Medical Membership</option>
						            <option value="Q">Quotation Trans</option>
						            <option value="AD">Admin Fee Trans</option>
									<option value="PR">Proposals</option>
						            <option value="CRD">Medical Cards</option>
									<option value="RFD">Refund</option>
									<option value="RM">Remittance Trans</option>
									<option value="CRCP">Creditor Commissions</option>
									<option value="SACP">Sub-Agent Commissions</option>
									<option value="BPU">Bulk Policy Upload</option>
									<option value="LD">Loaded Data</option>
									<option value="WPU">Wezesha Policy Upload</option>
									<option value="EPIU">Embedded Packaged Insurance Upload</option>
									<option value="ERIU">Embedded Retrenchment Insurance Upload</option>
									<option value="CLU"> Credit Life Upload</option>
									<option value="GLU"> Group Life Upload</option>
									<option value="TPU"> Timiza Policy Upload</option>
									<option value="CCU"> Credit Card Upload</option>
									<option value="MLU"> Mortgage Life Upload</option>
                                    <option value="CSU"> Credit Shield Upload</option>

								  </select>
				</div>
			</div>

		<div class="ln_solid"></div>
                      <div class="item form-group">
                      <div class="col-md-6 col-sm-6 col-xs-12 col-md-offset-3">
		<button type="button" class="btn btn-success" id="newSequency">New</button>
			<button data-loading-text="Saving..." id="saveSequencyBtn"
				type="button" class="btn btn-primary">Save</button>
				</div>
		</div>

	</form>
	 <div class="x_title">
	<h4>Sequences List</h4>
	</div>
		<div class="table-responsive">
	<table id="seqList" class="table table-striped" style="width:100%">
		<thead>
			<tr>

				<th>Prefix</th>
				<th>Last Value</th>
				<th>Next Value</th>
				<th>Sequence Type</th>
				<th>Transaction Type</th>
				<th width="5%"></th>
				<th width="5%"></th>
			</tr>
		</thead>
	</table>
			</div>
</div>