<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<script type="text/javascript" src="<c:url value="/js/modules/setups/utilitysetups.js"/>"></script>

  <div class="x_panel">
     <button class="btn btn-success btn btn-info float-right" id="btn-add-relation-types">New</button>
       <div class="x_title">
	<h4>Relationship Types List</h4>
	</div>
	<table id="relation-type-tbl" class="table table-striped" style="width: 100%">
		<thead>
			<tr class="headings">

				<th>RelationShip Type</th>
				<th>Relationship Type Desc</th>
				<th width="5%"></th>
				<th width="5%"></th>
			</tr>
		</thead>
	</table>
	</div>
  <div class="modal fade" id="rltshipTypeModal" tabindex="-1" role="dialog"
		aria-labelledby="rltshipTypeModalLabel" aria-hidden="true">
		<div class="modal-dialog">
			<div class="modal-content">
				<div class="modal-header">
					<button type="button" class="close" data-dismiss="modal"
						aria-label="Close">
						<span aria-hidden="true">&times;</span>
					</button>
					<h4 class="modal-title" id="rltshipTypeModalLabel">
						Edit/Add Relationship Type
					</h4>
				</div>
				<div class="modal-body" id="branch_model">
					<form id="relation-type-form" class="form-horizontal">
						<input type="hidden" class="form-control" id="type-id" name="typeId">
						<div class="item form-group">
							<label for="unit-id" class="col-md-3 label-align">Relationship Type</label>

							<div class="col-md-8">
								<input type="text" class="form-control" id="relation-type"
									   name="relationType"  required>
							</div>
						</div>
						<div class="item form-group">
							<label for="unit-id" class="col-md-3 label-align">Relationship Type Desc</label>

							<div class="col-md-8">
							    <input type="text" class="form-control" id="type-desc"
									name="relationDesc"  required>
							</div>
						</div>
						
					</form>
				</div>
				<div class="modal-footer">
					<button data-loading-text="Saving..." id="saveRelationType"
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
	