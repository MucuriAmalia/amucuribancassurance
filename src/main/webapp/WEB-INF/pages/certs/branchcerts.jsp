<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib prefix="sec" uri="http://www.springframework.org/security/tags" %>
<script type="text/javascript" src="<c:url value="/js/modules/utils/select2builder.js"/>"></script>
<script type="text/javascript" src="<c:url value="/libs/rivets/rivets.js"/>"></script>
<script type="text/javascript" src="<c:url value="/js/modules/certs/branchcerts.js"/>"></script>
<%--<sec:authorize access="hasAnyAuthority('ALLOC_BRANCH_CERTS')" var="hasCertAllocRole" ></sec:authorize>--%>
<sec:authorize access="hasAnyAuthority('ALLOC_BRANCH_CERTS')">
	<input id="hasCertAllocRole" access="hasAnyAuthority('ALLOC_BRANCH_CERTS')" type="text" class="hide" value="true"/>
</sec:authorize>
<%--<input id="hasCertAllocRole" access="hasAnyAuthority('ALLOC_BRANCH_CERTS')" type="text" class="hide" value="true"/>--%>
<%--<input id="hasCertAllocRole" access="!hasAnyAuthority('ALLOC_BRANCH_CERTS')" type="text" class="hide" value="false"/>--%>

<div class="x_panel">
	<sec:authorize access="hasAnyAuthority('ACCESS_CERT_LOTS')">
		<button class="btn btn-success btn btn-info float-right" id="btn-add-cert-lots">New</button>
	</sec:authorize>
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
<div class="x_panel">
	<sec:authorize access="hasAnyAuthority('ALLOC_BRANCH_CERTS')">
		<button class="btn btn-success btn btn-info float-right" id="btn-add-brn-certs">New</button>
	</sec:authorize>

       <div class="x_title">
	<h4>Branch and User Certificates</h4>
	</div>
	<div class="table-responsive">
	<table id="brncerts" class="table table-striped" style="width: 100%">
		<thead>
			<tr class="headings">
				<th>Certificate Type</th>
				<th>User</th>
				<th>Branch</th>
				<th>From</th>
				<th>To</th>
				<th>Total</th>
				<th>Last Printed</th>
				<th>Allocate for Print</th>
				<th>Status</th>
				<th width="5%"></th>
				<th width="5%"></th>
				<th width="5%"></th>
				<th width="5%"></th>
			</tr>
		</thead>
	</table>
		  </div>
	</div>
  <div class="modal fade" id="brncertlotsModal" tabindex="-1" role="dialog"
		aria-labelledby="brncertlotsModalLabel" aria-hidden="true">
		<div class="modal-dialog">
			<div class="modal-content">
				<div class="modal-header">
					<button type="button" class="close" data-dismiss="modal"
						aria-label="Close">
						<span aria-hidden="true">&times;</span>
					</button>
					<h4 class="modal-title" id="brncertlotsModalLabel">
						Allocate Certificates to Branch and User
					</h4>
				</div>
				<input type="hidden" id="selected-cert-pk">
				<input type="hidden" id="cert-available-from">
				<input type="hidden" id="reassigning">

				<div class="modal-body">
					<form id="brn-cert-lots-form" class="form-horizontal">
						<input type="hidden" class="form-control" id="cert-pk" name="brnCertId">
						<input type="hidden" class="form-control" id="cert-pk-from" name="reassignedBrnCert">
						<input type="hidden" id="cert-type-pk" name="certLots"/>

						<%--<div class="item form-group">--%>
							<%--<label for="brn-id" class="col-md-3 label-align">Certificate Lot</label>--%>

							<%--<div class="col-md-8">--%>
								<%--<input type="hidden" id="cert-type-pk" name="certLots"/>--%>
						        <%--<input type="hidden" id="cert-type-name">--%>
		                        <%--<div id="cert-lots" class="form-control"--%>
				                                 <%--select2-url="<c:url value="/protected/certs/selCertLots"/>" >--%>

				               <%--</div>--%>
							<%--</div>--%>
						<%--</div>--%>

						<div class="item form-group">
							<label for="brn-id" class="col-md-3 label-align">Branch</label>

							<div class="col-md-8">
								<input type="hidden" id="cert-brn-code" name="branch"/>
						        <input type="hidden" id="cert-brn-name">
		                        <div id="branch-def" class="form-control"
									select2-url="<c:url value="/protected/setups/branches"/>">

								</div>
							</div>
						</div>
						<div class="item form-group">
							<label for="brn-id" class="col-md-3 label-align">User</label>

							<div class="col-md-8">
								<input type="hidden" id="username"/>
		                     <input type="hidden" id="userCod" name="user.id"/>
		                        <div id="user-def" class="form-control"
				                                 select2-url="<c:url value="/protected/organization/managers"/>"  >

				               </div>
							</div>
						</div>
						<div class="item form-group">
							<label for="unit-id" class="col-md-3 label-align">No From</label>

							<div class="col-md-8">
							 <input type="number" class="form-control" id="cert-no-from"
									name="noFrom" readonly>
							</div>
						</div>
						<div class="item form-group">
                            <label for="unit-id" class="col-md-3 label-align">No of Certificates</label>

                            <div class="col-md-8">
                             <input type="number" class="form-control" id="cert-no-of-certs"
                                    >
                            </div>
                        </div>
						<div class="item form-group">
							<label for="unit-id" class="col-md-3 label-align">No To</label>

							<div class="col-md-8">
							 <input type="number" class="form-control" id="cert-no-to"
									name="noTo" readonly>
							</div>
						</div>

					</form>
				</div>
				<div class="modal-footer">
					<button data-loading-text="Saving..." id="saveBrnCertLots"
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
					<input type="hidden" class="form-control" id="certpk" name="cerId">

					<%--<div class="item form-group">--%>
						<%--<label for="brn-id" class="col-md-3 label-align">Certificate Type</label>--%>

						<%--<div class="col-md-8">--%>
							<%--<input type="hidden" id="cert-typepk" name="certTypes"/>--%>
							<%--<input type="hidden" id="cert-typename">--%>
							<%--<div id="cert-type" class="form-control"--%>
								 <%--select2-url="<c:url value="/protected/certs/selCertTypes"/>" >--%>

							<%--</div>--%>
						<%--</div>--%>
					<%--</div>--%>
					<div class="item form-group">
						<label for="brn-id" class="col-md-3 label-align">Sub Class</label>

						<div class="col-md-8">
							<input type="hidden" id="cert-sub-code" name="subclass"/>
							<input type="hidden" id="cert-sub-name">
							<input type="hidden" id="cert-typepk" name="certTypes"/>
							<input type="hidden" id="subcls-cert-typepk" name="subclassCertType"/>
							<div id="sub-class-def" class="form-control"
								 select2-url="<c:url value="/protected/certs/selectSubclassCertTypes"/>">

							</div>
						</div>
					</div>
					<div class="item form-group">
						<label for="brn-id" class="col-md-3 label-align">Underwriter</label>

						<div class="col-md-8">
							<input type="hidden" id="user-name"/>
							<input type="hidden" id="userCode" name="underwriter.acctId"/>
							<div id="underwriter-def" class="form-control"
								 select2-url="<c:url value="/protected/setups/binders/selAccounts"/>"  >

							</div>
						</div>
					</div>
					<div class="item form-group">
						<label for="unit-id" class="col-md-3 label-align">No From</label>

						<div class="col-md-8">
							<input type="number" class="form-control" id="cert-nofrom"
								   name="noFrom">
						</div>
					</div>
					<div class="item form-group">
						<label for="unit-id" class="col-md-3 label-align">No To</label>

						<div class="col-md-8">
							<input type="number" class="form-control" id="cert-noto"
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
<div class="modal fade" id="dealloclotsModal" tabindex="-1" role="dialog"
		aria-labelledby="dealloclotsModalLabel" aria-hidden="true">
		<div class="modal-dialog">
			<div class="modal-content">
				<div class="modal-header">
					<button type="button" class="close" data-dismiss="modal"
						aria-label="Close">
						<span aria-hidden="true">&times;</span>
					</button>
					<h4 class="modal-title" id="dealloclotsModalLabel">
						Deallocate Selected Lot
					</h4>
				</div>
				<div class="modal-body">
				<form id="frm-pol-remarks" class="form-horizontal">
				         <input type="hidden" id="dealloc-cert-pk" >
						<div class="item form-group">
							<label for="unit-id" class="col-md-3 label-align">Remarks</label>

							<div class="col-md-8">
							<textarea class="form-control" rows="5" id="cert-remarks"></textarea>
							</div>
						</div>
						</form>
				</div>
				<div class="modal-footer">
					<button data-loading-text="Saving..." id="deallocatecert"
						type="button" class="btn btn-success">
						Deallocate
					</button>
					<button type="button" class="btn btn-default" data-dismiss="modal">
						Cancel
					</button>
				</div>
			</div>
		</div>
	</div>


	<div class="modal fade" id="allocprintModal" tabindex="-1" role="dialog"
    		aria-labelledby="allocprintModalLabel" aria-hidden="true">
    		<div class="modal-dialog">
    			<div class="modal-content">
    				<div class="modal-header">
    					<button type="button" class="close" data-dismiss="modal"
    						aria-label="Close">
    						<span aria-hidden="true">&times;</span>
    					</button>
    					<h4 class="modal-title" id="allocprintModalLabel">
    						Allocate Certificate for Print
    					</h4>
    				</div>
    				<div class="modal-body">
    				<form id="frm-cert-alloc" class="form-horizontal">
    				         <input type="hidden" id="alloc-cert-pk" >
    						<div class="item form-group">
    							<label for="unit-id" class="col-md-4 label-align">Last Printed Cert No</label>

    							<div class="col-md-8">
    							   <input type="number" class="form-control" id="last-printed-cert">
    							</div>
    						</div>
    						<div class="item form-group">
                                <label for="unit-id" class="col-md-4 label-align">Allocate for Printing</label>

                                <div class="col-md-8">
                                  <select class="form-control" id="alloc-for-print" required>
                                    <option value=""></option>
                                    <option value="Y">Yes</option>
                                    <option value="N">No</option>
                                  </select>
                                </div>
                            </div>
    						</form>
    				</div>
    				<div class="modal-footer">
    					<button data-loading-text="Saving..." id="allocforprint"
    						type="button" class="btn btn-success">
    						OK
    					</button>
    					<button type="button" class="btn btn-default" data-dismiss="modal">
    						Cancel
    					</button>
    				</div>
    			</div>
    		</div>
    	</div>