<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="sec" uri="http://www.springframework.org/security/tags" %>
<div class="x_panel">
	<div class="x_title">
		<h2><i class="fa fa-bars"></i> Underwriting Setups</h2>
		<div class="clearfix"></div>
	</div>
	<div class="x_content">
<div class="panel panel-default">
<div class="panel-body">
<div class="row">
	<div class="col-md-6 col-sm-6 col-xs-12">
		<ul style="list-style-type: none;">
			<li><h5 style="font-weight: bolder;">Product Setups</h5></li>
			<sec:authorize access="hasAnyAuthority('ACCESS_CLASSES')">
			<li><a href="<c:url value="/protected/setups/classes/classesHome"/>">Classes
					</a></li>
			</sec:authorize>
			<sec:authorize access="hasAnyAuthority('ACCESS_PRODUCTS')">
			<li><a href="<c:url value="/protected/setups/products/productsHome"/>">Products
					</a></li>
			</sec:authorize>
			<sec:authorize access="hasAnyAuthority('ACCESS_BINDERS')">
			<li><a href="<c:url value="/protected/setups/binders/bindersHome"/>">Product Contracts
					</a></li>
			</sec:authorize>
			<sec:authorize access="hasAnyAuthority('ACCESS_BINDERS')">
				<li><a href="<c:url value="/protected/sap/productcodes/productCodeHome"/>">Product Codes
				</a></li>
			</sec:authorize>
<%--			<sec:authorize access="hasAnyAuthority('ACCESS_BINDERS')">--%>
<%--				<li><a href="<c:url value="/protected/setups/reinsurance/treaties"/>">Reinsurance Treaties--%>
<%--				</a></li>--%>
<%--			</sec:authorize>--%>
		    
		</ul>
	</div>
	<div class="col-md-6 col-sm-6 col-xs-12">
		<ul style="list-style-type: none;">
			<li><h5 style="font-weight: bolder;">Clauses,Perils and Taxes Setups</h5></li>
			<sec:authorize access="hasAnyAuthority('ACCESS_CLAUSES')">
			<li><a href="<c:url value="/protected/setups/clauses/clausesHome"/>">Clauses
					</a></li>
			</sec:authorize>
			<sec:authorize access="hasAnyAuthority('ACCESS_TAXES')">
		    <li><a href="<c:url value="/protected/setups/taxes/taxesdefHome"/>">Levies
					</a></li>
			</sec:authorize>
			<sec:authorize access="hasAnyAuthority('ACCESS_PERILS')">
			<li><a href="<c:url value="/protected/setups/perils/perilsdefHome"/>">Perils
					</a></li>
			</sec:authorize>
			<sec:authorize access="hasAnyAuthority('ACCESS_REVENUE_ITEMS')">
			<li><a href="<c:url value="/protected/setups/revitems/revitemsHome"/>">Revenue Items
					</a></li>
			</sec:authorize>
			<sec:authorize access="hasAnyAuthority('ACCESS_REVENUE_ITEMS')">
				<li><a href="<c:url value="/protected/setups/revitems/uwrevitemsHome"/>">Uw Revenue Items
				</a></li>
			</sec:authorize>
			<sec:authorize access="hasAnyAuthority('ACCESS_PERIOD_RATES')">
			<li><a href="<c:url value="/protected/setups/shtprdrates"/>">Short Period Rates
					</a></li>
			</sec:authorize>
			<sec:authorize access="hasAnyAuthority('ACCESS_ENDORSEMENT_REMARKS')">
		    <li><a href="<c:url value="/protected/setups/endorseremarks"/>">Endorsement Remarks
					</a></li>
			</sec:authorize>
		    
		</ul>
	</div>
	<div class="col-md-6 col-sm-6 col-xs-12">
		<ul style="list-style-type: none;">
			<li><h5 style="font-weight: bolder;">Schedule, Required Docs and Claim Activities</h5></li>
			<sec:authorize access="hasAnyAuthority('ACCESS_SCHEDULE')">
			<li><a href="<c:url value="/protected/schedules/access"/>">Schedule Mappings
			</a></li>
			</sec:authorize>
			<sec:authorize access="hasAnyAuthority('ACCESS_REQUIRED_DOCS')">
			<li><a href="<c:url value="/protected/setups/requireddocs/reqdocs"/>">Required Documents
			</a></li>
			</sec:authorize>
			<sec:authorize access="hasAnyAuthority('ACCESS_CLAIM_ACTIVITIES')">
			<li><a href="<c:url value="/protected/setups/clmactivity"/>">Claim Activities
			</a></li>
			</sec:authorize>
<%--				<li><a href="<c:url value="/protected/setups/questionsdef"/>">Questionnare Definition--%>
<%--				</a></li>--%>

<%--			<li><a href="<c:url value="/protected/setups/testquestion"/>">Test Questions--%>
<%--			</a></li>--%>
		</ul>
	</div>
	<div class="col-md-6 col-sm-6 col-xs-12">
		<ul style="list-style-type: none;">
			<li><h5 style="font-weight: bolder;">Other Setups</h5></li>
			<sec:authorize access="hasAnyAuthority('ACCESS_BUDGET')">
				<li><a href="<c:url value="/protected/setups/budgetsdef"/>">Budgets Setups
				</a></li>
			</sec:authorize>
<%--			<li><a href="<c:url value="/protected/setups/budgetRep"/>">Report Setups--%>
<%--			</a></li>--%>

		</ul>
	</div>
</div>
</div>
</div>
</div>