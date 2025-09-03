<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<script type="text/javascript" src="<c:url value="/js/modules/setups/utilitysetups.js"/>"></script>

  <div class="x_panel">
     <button class="btn btn-success btn btn-info float-right" id="btn-add-clnt-title">New</button>
       <div class="x_title">
		   <h2><i class="fa fa-bars"></i> Client Titles List</h2>
		   <div class="clearfix"></div>
	  </div>
	<table id="client-title-tbl" class="table table-striped" style="width: 100%">
		<thead>
			<tr class="headings">

				<th>Client Title</th>
				<th width="5%"></th>
				<th width="5%"></th>
			</tr>
		</thead>
	</table>
	</div>
  <div class="modal fade" id="clntTitleModesModal" tabindex="-1" role="dialog"
		aria-labelledby="clntTitleModesModalLabel" aria-hidden="true">
		<div class="modal-dialog">
			<div class="modal-content">
				<div class="modal-header">
				    <h4 class="modal-title" id="clntTitleModesModalLabel">
                        Edit/Add Client Title
                    </h4>
					<button type="button" class="close" data-dismiss="modal"
						aria-label="Close">
						<span aria-hidden="true">&times;</span>
					</button>

				</div>
				<div class="modal-body" id="branch_model">
					<form id="client-title-form" class="form-horizontal">
						<input type="hidden" class="form-control" id="title-id" name="titleId">
						<div class="item form-group">
							<label for="title-desc" class="col-md-3 label-align">Client Title</label>
							<div class="col-md-8">
							    <input type="text" class="form-control" id="title-desc"
									name="titleName"  required>
							</div>
						</div>
						
					</form>
				</div>
				<div class="modal-footer">
					<button data-loading-text="Saving..." id="saveClientTitle"
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
	