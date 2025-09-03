<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<script type="text/javascript" src="<c:url value="/js/modules/utils/select2builder.js"/>"></script>
<script type="text/javascript" src="<c:url value="/libs/rivets/rivets.js"/>"></script>
<script type="text/javascript"
	src="<c:url value="/js/modules/setups/occupations.js"/>"></script>
<div class="x_panel">
   <div class="x_title">
		<h2>Sectors</h2>
		<ul class="nav navbar-right panel_toolbox">
			<li><a class="collapse-link"><i class="fa fa-chevron-up"></i></a>
			</li>
		</ul>
		<div class="clearfix"></div>
	</div>
	<div class="x_content">
	<form id="prg-grp-form" class="form-horizontal">
	    <div class="item form-group form-required">
				<div class="col-md-6">
				   <label for="brn-id" class="col-md-5 label-align">Select
					Sector</label>

				<div class="col-md-7">
		                     <input type="hidden" id="sect-id"/>
		                     <input type="hidden" id="sect-name">
		                        <div id="sect-def" class="form-control" 
				                                 select2-url="<c:url value="/protected/setups/selSectors"/>" >
				                                 
				               </div> 
				               
				</div>
				</div>
				<div class="col-md-2">
				    <button type="button" class="btn btn-info" id="btn-add-sector">New</button>
			        <button type="button" class="btn btn-info" id="btn-edit-sector">Edit</button>
				    
				</div>
				
				<div class="col-md-2">
				    
				</div>
				
				</div>
			</form>
			</div>
		</div>
		<div class="x_panel">
		<div class="x_title">
		<h2>Occupations</h2>
		<ul class="nav navbar-right panel_toolbox">
			<li><a class="collapse-link"><i class="fa fa-chevron-up"></i></a>
			</li>
		</ul>
		<div class="clearfix"></div>
	</div>
	<div class="x_content">
         <input type="hidden" id="prg-pk">
		<button type="button" class="btn btn-info" id="btn-add-occup">New</button>
		<table id="occupList" class="table table-striped" style="width: 100%">
			<thead>
				<tr class="headings">

					<th>ID</th>
					<th>Description</th>
					<th width="5%"></th>
					<th width="5%"></th>
				</tr>
			</thead>
		</table>
	</div>
	</div>
	
	
	<div class="modal fade" id="sectorModal" tabindex="-1" role="dialog"
		aria-labelledby="sectorModalLabel" aria-hidden="true">
		<div class="modal-dialog">
			<div class="modal-content">
				<div class="modal-header">
				    <h4 class="modal-title" id="sectorModalLabel">
                        Add/Edit Sector
                    </h4>
					<button type="button" class="close" data-dismiss="modal"
						aria-label="Close">
						<span aria-hidden="true">&times;</span>
					</button>

				</div>
				<div class="modal-body">
				   
					<form id="sector-form" class="form-horizontal">
						<input type="hidden" class="form-control" id="sector-code" name="code">
						<div class="item form-group">
							<label for="brn-id" class="col-md-3 label-align">Sector Id<span class="required">*</span></label>

							<div class="col-md-8">
								<input type="text" class="form-control" id="sector-id"
									name="shortDesc"  required>
							</div>
						</div>
						<div class="item form-group">
							<label for="cou-name" class="col-md-3 label-align">Sector Name<span class="required">*</span></label>

							<div class="col-md-8">
								<input type="text" class="form-control" id="sector-name"
									name="name"  required>
							</div>
						</div>	
						
						
					</form>
				</div>
				<div class="modal-footer">
				    <button  id="delSectorBtn"
						type="button" class="btn btn-danger">
						Delete
					</button>
					<button data-loading-text="Saving..." id="saveSectorBtn"
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
	<div class="modal fade" id="occupModal" tabindex="-1" role="dialog"
		aria-labelledby="occupModalLabel" aria-hidden="true">
		<div class="modal-dialog">
			<div class="modal-content">
				<div class="modal-header">
				    <h4 class="modal-title" id="occupModalLabel">
                        Add/Edit Occupation
                    </h4>
					<button type="button" class="close" data-dismiss="modal"
						aria-label="Close">
						<span aria-hidden="true">&times;</span>
					</button>

				</div>
				<div class="modal-body">
				   
					<form id="occup-form" class="form-horizontal">
						<input type="hidden" class="form-control" id="occup-code" name="code">
						<input type="hidden" class="form-control" id="occup-sect-code" name="sector">
						<div class="item form-group">
							<label for="brn-id" class="col-md-3 label-align">Occupation Id<span class="required">*</span></label>

							<div class="col-md-8">
								<input type="text" class="form-control" id="occup-id"
									name="shortDesc"  required>
							</div>
						</div>
						<div class="item form-group">
							<label for="cou-name" class="col-md-3 label-align">Occupation<span class="required">*</span></label>

							<div class="col-md-8">
								<input type="text" class="form-control" id="occup-name"
									name="name"  required>
							</div>
						</div>	
						
						
					</form>
				</div>
				<div class="modal-footer">
					<button data-loading-text="Saving..." id="saveOccupBtn"
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