<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<script type="text/javascript" src="<c:url value="/js/modules/utils/select2builder.js"/>"></script>
<script type="text/javascript" src="<c:url value="/libs/rivets/rivets.js"/>"></script>
<script type="text/javascript" src="<c:url value="/js/modules/certs/certlots.js"/>"></script>

  <div class="x_panel">
     <button class="btn btn-success btn btn-info float-right" id="btn-add-cert-lots">New</button>
       <div class="x_title">
	<h4>Underwriter Certificate Lots</h4>
	</div>
	  <div class="table-responsive">
	<table id="uwcertlots" class="table table-striped" style="width: 100%">
		<thead>
			<tr class="headings">

				<th>Underwriter</th>
				<th>Sub Class</th>
				<th>Certificate Type</th>
				<th>No From</th>
				<th>No To</th>
				<th>No of Certs</th>
				<th width="5%"></th>
				<th width="5%"></th>
			</tr>
		</thead>
	</table>
		  </div>
	</div>
  <div class="modal fade" id="certlotsModal" tabindex="-1" role="dialog"
		aria-labelledby="certlotsModalLabel" aria-hidden="true">
		<div class="modal-dialog">
			<div class="modal-content">
				<div class="modal-header">
					<button type="button" class="close" data-dismiss="modal"
						aria-label="Close">
						<span aria-hidden="true">&times;</span>
					</button>
					<h4 class="modal-title" id="certlotsModalLabel">
						Define Certificate Lots
					</h4>
				</div>
				<div class="modal-body">
					<form id="cert-lots-form" class="form-horizontal">
						<input type="hidden" class="form-control" id="cert-pk" name="cerId">
						
						<div class="item form-group">
							<label for="brn-id" class="col-md-3 label-align">Certificate Type</label>

							<div class="col-md-8">
								<input type="hidden" id="cert-type-pk" name="certTypes"/>
						        <input type="hidden" id="cert-type-name">
		                        <div id="cert-type" class="form-control" 
				                                 select2-url="<c:url value="/protected/certs/selCertTypes"/>" >
				                                 
				               </div> 
							</div>
						</div>
						<div class="item form-group">
							<label for="brn-id" class="col-md-3 label-align">Sub Class</label>

							<div class="col-md-8">
								<input type="hidden" id="cert-sub-code" name="subclass"/>
						        <input type="hidden" id="cert-sub-name">
		                        <div id="sub-class-def" class="form-control"
									select2-url="<c:url value="/protected/setups/clauses/subclassSelect"/>">
			
								</div>
							</div>
						</div>
						<div class="item form-group">
							<label for="brn-id" class="col-md-3 label-align">Underwriter</label>

							<div class="col-md-8">
								<input type="hidden" id="username"/>
		                     <input type="hidden" id="userCod" name="underwriter.acctId"/>
		                        <div id="underwriter-def" class="form-control" 
				                                 select2-url="<c:url value="/protected/setups/binders/selAccounts"/>"  >
				                                 
				               </div>    
							</div>
						</div>
						<div class="item form-group">
							<label for="unit-id" class="col-md-3 label-align">No From</label>

							<div class="col-md-8">
							 <input type="number" class="form-control" id="cert-no-from"
									name="noFrom">
							</div>
						</div>
						<div class="item form-group">
							<label for="unit-id" class="col-md-3 label-align">No To</label>

							<div class="col-md-8">
							 <input type="number" class="form-control" id="cert-no-to"
									name="noTo">
							</div>
						</div>
						
					</form>
				</div>
				<div class="modal-footer">
					<button data-loading-text="Saving..." id="saveCertLots"
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
	