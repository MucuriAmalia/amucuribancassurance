<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<div class="x_panel">
   <div class="x_title">
		<h2>Claims Reports</h2>
		<ul class="nav navbar-right panel_toolbox">
			<li><a class="collapse-link"><i class="fa fa-chevron-up"></i></a>
			</li>
		</ul>
		<div class="clearfix"></div>
	</div>
	<div class="x_content">
	 <div class="row">
					<div class="col-md-6">

						<ul style="list-style-type: none;" class="reports-links">
								<li><a
								href="<c:url value='/protected/uw/policies/rpt_risk_note'/> "
								target="_blank">Claim Statuses General Report</a></li>
								<li><a
								href="<c:url value='/protected/uw/policies/rpt_prem_working'/> "
								target="_blank">Claims Statuses Declined Claims Report</a></li>
								<li class="endorse-disp"><a
								href="<c:url value='/protected/uw/policies/rpt_endorse'/> "
								target="_blank">Claims Statuses Settled Claims Report</a></li>
						 		</ul>
					</div>
					
					<div class="col-md-6">

							<ul style="list-style-type: none;" class="reports-links">
								<li><a
								href="<c:url value='/protected/uw/policies/rpt_risk_note'/> "
								target="_blank">Claim Statuses Outstanding Claims Report</a></li>
								<li><a
								href="<c:url value='/protected/uw/policies/rpt_prem_working'/> "
								target="_blank">Claim Statuses Comparative Based On Settled Amount Report</a></li>
								<li class="endorse-disp"><a
								href="<c:url value='/protected/uw/policies/rpt_endorse'/> "
								target="_blank">Loss Ratio Summary Per Binder Report</a></li>
						 		</ul>

					</div>
				</div>
	</div>
	</div>