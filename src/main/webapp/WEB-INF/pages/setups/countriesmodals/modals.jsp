<div class="modal fade" id="countryModal" tabindex="-1" role="dialog"
		aria-labelledby="countryModalLabel" aria-hidden="true">
		<div class="modal-dialog">
			<div class="modal-content">
				<div class="modal-header">
				    <h4 class="modal-title" id="countryModalLabel">
                        Edit/Add Country
                    </h4>
					<button type="button" class="close" data-dismiss="modal"
						aria-label="Close">
						<span aria-hidden="true">&times;</span>
					</button>

				</div>
				<div class="modal-body" id="branch_model">
					<form id="country-form" class="form-horizontal">
						<input type="hidden" class="form-control" id="cou-code" name="couCode">
						<div class="item form-group">
							<label for="cou-code" class="col-md-3 label-align">Country Code</label>

							<div class="col-md-8">
								<input type="text" class="form-control" id="country-id"
									name="couShtDesc"  required>
							</div>
						</div>
						<div class="item form-group">
							<label for="cou-name" class="col-md-3 label-align">Country Name</label>

							<div class="col-md-8">
								<input type="text" class="editUserCntrls form-control"
									id="cou-name" name="couName" 
									required>
							</div>
						</div>
						<div class="item form-group">
							<label for="cou-name" class="col-md-3 label-align">Country Prefix</label>

							<div class="col-md-8">
								<input type="text" class="editUserCntrls form-control"
									id="cou-prefix" name="prefix" 
									required>
							</div>
						</div>
						
					</form>
				</div>
				<div class="modal-footer">
					<button data-loading-text="Saving..." id="saveCountryBtn"
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
	<div class="modal fade" id="countyModal" tabindex="-1" role="dialog"
		aria-labelledby="countyModalLabel" aria-hidden="true">
		<div class="modal-dialog">
			<div class="modal-content">
				<div class="modal-header">
				    <h4 class="modal-title" id="countyModalLabel">
                        Edit/Add County
                    </h4>
					<button type="button" class="close" data-dismiss="modal"
						aria-label="Close">
						<span aria-hidden="true">&times;</span>
					</button>

					<input type="hidden" class="form-control" id="country-pk-code" name="country">
				</div>
				<div class="modal-body" id="branch_model">
					<form id="county-form" class="form-horizontal">
						<input type="hidden" class="form-control" id="county-code" name="countyId">
						<input type="hidden" class="form-control" id="country-code" name="country">
						<div class="item form-group">
							<label for="brn-id" class="col-md-3 label-align">County Code</label>

							<div class="col-md-8">
								<input type="text" class="form-control" id="county-id"
									name="countyCode"  required>
							</div>
						</div>
						<div class="item form-group">
							<label for="cou-name" class="col-md-3 label-align">County Name</label>

							<div class="col-md-8">
								<input type="text" class="editUserCntrls form-control"
									id="county-name" name="countyName" 
									required>
							</div>
						</div>
						
					</form>
				</div>
				<div class="modal-footer">
					<button data-loading-text="Saving..." id="saveCountyBtn"
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
	<div class="modal fade" id="townModal" tabindex="-1" role="dialog"
		aria-labelledby="townModalLabel" aria-hidden="true">
		<div class="modal-dialog">
			<div class="modal-content">
				<div class="modal-header">
				    <h4 class="modal-title" id="townModalLabel">
                        Edit/Add Town
                    </h4>
					<button type="button" class="close" data-dismiss="modal"
						aria-label="Close">
						<span aria-hidden="true">&times;</span>
					</button>

					<input type="hidden" class="form-control" id="county-pk-code" name="county">
				</div>
				<div class="modal-body" id="branch_model">
					<form id="town-form" class="form-horizontal">
						<input type="hidden" class="form-control" id="town-code" name="ctCode">
						<input type="hidden" class="form-control" id="count-code" name="county">
						<div class="item form-group">
							<label for="town-id" class="col-md-3 label-align">Town Code</label>

							<div class="col-md-8">
								<input type="text" class="form-control" id="town-id"
									name="ctShtDesc"  required>
							</div>
						</div>
						<div class="item form-group">
							<label for="town-name" class="col-md-3 label-align">Town Name</label>

							<div class="col-md-8">
								<input type="text" class="editUserCntrls form-control"
									id="town-name" name="ctName" 
									required>
							</div>
						</div>
						
					</form>
				</div>
				<div class="modal-footer">
					<button data-loading-text="Saving..." id="saveTownBtn"
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
	
	
	
	<div class="modal fade" id="postalListModal" tabindex="-1" role="dialog"
		aria-labelledby="postalListModalLabel" aria-hidden="true">
		<div class="modal-dialog">
			<div class="modal-content">
				<div class="modal-header">
				    <h4 class="modal-title" id="postalListModalLabel">
                        Postal Codes
                    </h4>
					<button type="button" class="close" data-dismiss="modal"
						aria-label="Close">
						<span aria-hidden="true">&times;</span>
					</button>

					<input type="hidden" class="form-control" id="town-postal-pk-code">
				</div>
				<div class="modal-body" id="branch_model">
				<button type="button" class="btn btn-success btn btn-info float-right" id="btn-add-postal-code">New</button>
					<table id="postalCodesList" class="table table-striped" style="width: 100%">
					<thead>
						<tr class="headings">
			
							<th width="20%">Postal Name</th>
							<th width="20%">Postal Code</th>
							<th width="5%"></th>
							<th width="5%"></th>
						</tr>
					</thead>
				</table>
				</div>
				<div class="modal-footer">
					<button type="button" class="btn btn-default" data-dismiss="modal">
						Cancel
					</button>
				</div>
			</div>
		</div>
	</div>
	
	
	
	
	<div class="modal fade" id="savePostalModal" tabindex="-1" role="dialog"
		aria-labelledby="savePostalModalLabel" aria-hidden="true">
		<div class="modal-dialog">
			<div class="modal-content">
				<div class="modal-header">
					<h4 class="modal-title" id="savePostalModalLabel">
						Edit/Add Postal Code
					</h4>
					<button type="button" class="close" data-dismiss="modal"
							aria-label="Close">
						<span aria-hidden="true">&times;</span>
					</button>
				</div>
				<div class="modal-body" id="branch_model">
					<form id="postal-form" class="form-horizontal">
						<input type="hidden" class="form-control" id="ps-code" name="pcode">
						<input type="hidden" class="form-control" id="postal-town-code" name="town">
						<div class="item form-group">
							<label for="town-id" class="col-md-3 label-align">Postal Name</label>

							<div class="col-md-8">
								<input type="text" class="form-control" id="postal-name"
									name="postalName"  required>
							</div>
						</div>
						<div class="item form-group">
							<label for="town-name" class="col-md-3 label-align">Postal Code</label>

							<div class="col-md-8">
								<input type="text" class="editUserCntrls form-control"
									id="postal-code" name="zipCode" 
									required>
							</div>
						</div>
						
					</form>
				</div>
				<div class="modal-footer">
					<button data-loading-text="Saving..." id="savePostalCodeBtn"
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