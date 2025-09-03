<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<script type="text/javascript" src="<c:url value="/js/modules/utils/select2builder.js"/>"></script>
<script type="text/javascript" src="<c:url value="/libs/rivets/rivets.js"/>"></script>
<script type="text/javascript"
	src="<c:url value="/js/modules/setups/prefixes.js"/>"></script>
<div class="x_panel">
   <div class="x_title">
		<h2>Mobile Prefix Definition</h2>
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
				   <label for="brn-id" class="col-md-5 label-align">
					Select Country</label>

				<div class="col-md-7">
		                     <input type="hidden" id="cou-id"/>
		                     <input type="hidden" id="cou-name">
		                        <div id="cou-frm" class="form-control" 
				                                select2-url="<c:url value="/protected/organization/countries"/>" >
				                                 
				               </div> 
				               
				</div>
				</div>
				
				
				</div>
			</form>
			</div>
			</div>
		<div class="x_panel">
   <div class="x_title">
		<h2>Mobile Prefix</h2>
		<ul class="nav navbar-right panel_toolbox">
			<li><a class="collapse-link"><i class="fa fa-chevron-up"></i></a>
			</li>
		</ul>
		<div class="clearfix"></div>
	</div>
	<div class="x_content">
		<button type="button" class="btn btn-info" id="btn-add-prefix">New</button>
		<table id="prefixList" class="table table-striped" style="width: 100%">
			<thead>
				<tr class="headings">

					<th>Prefix</th>
					<th>Mobile Provider</th>
					<th width="5%"></th>
					<th width="5%"></th>
				</tr>
			</thead>
		</table>
		</div>
		</div>
	
	
	
	<div class="modal fade" id="prefixModal" tabindex="-1" role="dialog"
		aria-labelledby="prefixModalLabel" aria-hidden="true">
		<div class="modal-dialog">
			<div class="modal-content">
				<div class="modal-header">
				    <h4 class="modal-title" id="prefixModalLabel">
                        Edit/Add Mobile Prefix
                    </h4>
					<button type="button" class="close" data-dismiss="modal"
						aria-label="Close">
						<span aria-hidden="true">&times;</span>
					</button>

				</div>
				<div class="modal-body">
				   <input type="hidden" class="form-control" id="prefix-cou-pk">
					<form id="prefix-form" class="form-horizontal">
						<input type="hidden" class="form-control" id="prefix-id" name="prefixId">
						<input type="hidden" class="form-control" id="prefix-country" name="country">
						
						<div class="item form-group">
							<label for="cou-name" class="col-md-3 label-align">Mobile Prefix</label>

							<div class="col-md-8">
								<input type="text" class="editUserCntrls form-control"
									id="prefix-name" name="prefixName" 
									required>
							</div>
						</div>	
						<div class="item form-group">
							<label for="cou-name" class="col-md-3 label-align">Mobile Provider</label>

							<div class="col-md-8">
								<input type="hidden" id="mob-prefix-provd-id" name="providers"/>
								<input type="hidden" id="mob-prefix-name"/>
								<div id="mob-provider" class="form-control"
									 select2-url="<c:url value="/protected/clients/setups/selMobProviders"/>" >

								</div>
							</div>
						</div>	
						
						
					</form>
				</div>
				<div class="modal-footer">
					<button data-loading-text="Saving..." id="savePrefixBtn"
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