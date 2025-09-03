<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<script type="text/javascript" src="<c:url value="/js/modules/utils/select2builder.js"/>"></script>
<script type="text/javascript" src="<c:url value="/libs/rivets/rivets.js"/>"></script>
<script type="text/javascript"
	src="<c:url value="/js/modules/binder/binder.js"/>"></script>
<div class="x_panel">
   <div class="x_title">
        <h2><i class="fa fa-bars"></i> Contract Definition</h2>
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
				   <label for="acc-id" class="col-md-5 label-align">
					Insurance Company</label>

				<div class="col-md-7">
		                     <input type="hidden" id="acc-id" rv-value="account.acctId"/>
		                     <input type="hidden" id="acc-name">
		                        <div id="acc-frm" class="form-control"
				                                 select2-url="<c:url value="/protected/setups/selParentAccts"/>" >

				               </div>

				</div>
				</div>

				</div>
			</form>
			</div>
		<div class="x_panel">
   <div class="x_title">
		<h2>Contracts</h2>
		<ul class="nav navbar-right panel_toolbox">
			<li><a class="collapse-link"><i class="fa fa-chevron-up"></i></a>
			</li>
		</ul>
		<div class="clearfix"></div>
	</div>
	<div class="x_content">
         <input type="hidden" id=binder-sel-pk>
		<button type="button" class="btn btn-info" id="btn-add-binder">New</button>
		<div class="spacer"></div>
		<div class="table-responsive">
		<table id="binderList" class="table table-striped" style="width: 100%">
			<thead>
				<tr class="headings">
                     <th>Type</th>
					<th>Contract</th>
					<th>Calculator Type</th>
					<th>Product</th>
					<th>Currency</th>
					<th>Active?</th>
					<th></th>
                    <th></th>
					<th></th>
					<th></th>
				</tr>
			</thead>
		</table>
			</div>
		</div>
		</div>
</div>