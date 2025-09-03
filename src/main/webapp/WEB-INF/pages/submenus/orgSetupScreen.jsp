<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="sec" uri="http://www.springframework.org/security/tags" %>
<div class="x_panel">
	<div class="x_title">
		<h2><i class="fa fa-bars"></i> Organization Setups</h2>
		<div class="clearfix"></div>
	</div>
	<div class="x_content">
<div class="panel panel-default">
<div class="panel-body">
<div class="row">
	<sec:authorize access="hasAnyAuthority('SETUP_ORGANISATIONS')">
	<div class="col-md-6 col-sm-6 col-xs-12">
		<ul style="list-style-type: none;">
			<li><h5 style="font-weight: bolder;">Organizations Setups</h5></li>
			<sec:authorize access="hasAnyAuthority('ACCESS_ORG')">
			<li><a href="<c:url value="/protected/organization/"/>">Organization
					Definition</a></li>
			</sec:authorize>
			<sec:authorize access="hasAnyAuthority('ACCESS_CURRENCY')">
			<li><a href="<c:url value="/protected/setups/currency"/>">Currency
					Definition</a></li>
			</sec:authorize>
			<sec:authorize access="hasAnyAuthority('ACCESS_COUNTRY')">
			<li><a href="<c:url value="/protected/setups/countries"/>">Countries,Counties
					and Towns</a></li>
			</sec:authorize>
			<sec:authorize access="hasAnyAuthority('ACCESS_PAYMENT_MODES')">
			<li><a href="<c:url value="/protected/setups/paymentmodes"/>">Payment
					Modes</a></li>
			</sec:authorize>
			<sec:authorize access="hasAnyAuthority('ACCESS_SEQUENCE')">
			<li><a href="<c:url value="/protected/setups/syssequences"/>">System
					Sequences</a></li>
			</sec:authorize>
			<sec:authorize access="hasAnyAuthority('ACCESS_PARAMETERS')">
			<li><a href="<c:url value="/protected/setups/parameterlist"/>">System
					Parameters</a></li>
			</sec:authorize>
			<li><a href="<c:url value="/protected/setups/mapping"/>">Transaction
				Types Mapping</a></li>
			<li><a href="<c:url value="/protected/setups/businesssources"/>">Business Sources</a></li>
			<sec:authorize access="hasAnyAuthority('ACCESS_ESCALATION_MATRIX')">
			<li><a href="<c:url value="/protected/setups/escalation"/>">Task Escalation Setups</a></li>
			</sec:authorize>
			<sec:authorize access="hasAnyAuthority('ACCESS_ORG')">
			<li><a href="<c:url value="/protected/setups/rejectedreasons"/>">Rejected Reasons</a></li>
			</sec:authorize>
			<sec:authorize access="hasAnyAuthority('ACCESS_ORG')">
				<li><a href="<c:url value='/protected/setups/publicholidays'/>">Public Holidays</a></li>

			</sec:authorize>
		</ul>
	</div>
	</sec:authorize>
	<sec:authorize access="hasAnyAuthority('SETUP_ACCOUNTS')">
	<div class="col-md-6 col-sm-6 col-xs-12">
		<ul style="list-style-type: none;">
			<li><h5 style="font-weight: bolder;">Account Setups</h5></li>
			<sec:authorize access="hasAnyAuthority('ACCESS_ACCOUNT_TYPES')">
			<li><a href="<c:url value="/protected/setups/accttypes"/>">Account
					Types</a></li>
			</sec:authorize>



		</ul>
	</div>
	</sec:authorize>
</div>
	<div class="row">
	<sec:authorize access="hasAnyAuthority('SETUP_CLIENTS')">
	<div class="col-md-6 col-sm-6 col-xs-12">
		<ul style="list-style-type: none;">
			<li><h5 style="font-weight: bolder;">Client Setups</h5></li>
			<sec:authorize access="hasAnyAuthority('ACCESS_ACCOUNT_TYPES')">
				<li><a href="<c:url value="/protected/workflow/deployhome"/>"></a></li>
			</sec:authorize>


			<sec:authorize access="hasAnyAuthority('ACCESS_CLIENT_TYPES')">
				<li><a
						href="<c:url value="/protected/setups/clienttypeshome"/>">Client Types</a></li>
			</sec:authorize>
			<sec:authorize access="hasAnyAuthority('ACCESS_CLIENT_TITLE')">
				<li><a
						href="<c:url value="/protected/setups/clienttitleshome"/>">Client Titles</a></li>
			</sec:authorize>
			<sec:authorize access="hasAnyAuthority('ACCESS_CLIENTS')">
				<li><a
						href="<c:url value="/protected/setups/intparties"/>">Interested Parties</a></li>
			</sec:authorize>
			<sec:authorize access="hasAnyAuthority('ACCESS_PREFIXES')">
				<li><a
						href="<c:url value="/protected/setups/mobprefixlist"/>">Mobile Prefixes</a></li>
			</sec:authorize>
			<sec:authorize access="hasAnyAuthority('ACCESS_SECTORS')">
				<li><a
						href="<c:url value="/protected/setups/occupations"/>">Sectors/Occupations</a></li>
			</sec:authorize>
			<sec:authorize access="hasAnyAuthority('ACCESS_CLIENT_TYPES')">
				<li><a
						href="<c:url value="/protected/setups/segments"/>">Segments</a></li>
			</sec:authorize>
			<sec:authorize access="hasAnyAuthority('ACCESS_CLIENT_TYPES')">
				<li><a
						href="<c:url value="/protected/setups/mainBankSegments"/>">Main Bank Segments</a></li>
			</sec:authorize>
		</ul>
	</div>
	</sec:authorize>
	<sec:authorize access="hasAnyAuthority('SETUP_WORKFLOWS_CHECKS')">
	<div class="col-md-6 col-sm-6 col-xs-12">
		<ul style="list-style-type: none;">
			<li><h5 style="font-weight: bolder;">Workflow and Checks Setups</h5></li>
			<sec:authorize access="hasAnyAuthority('ACCESS_ACCOUNT_TYPES')">
			<li><a href="<c:url value="/protected/workflow/deployhome"/>">Deployments</a></li>
			</sec:authorize>
			<sec:authorize access="hasAnyAuthority('ACCESS_ACCOUNT_TYPES')">
				<li><a href="<c:url value="/protected/setups/checks/checksHome"/>">Checks</a></li>
			</sec:authorize>
			</ul>
		</div>
	</sec:authorize>
	</div>
</div>
</div>
	</div>
</div>