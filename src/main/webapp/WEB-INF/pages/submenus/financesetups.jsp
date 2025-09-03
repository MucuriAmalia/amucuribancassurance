<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="sec" uri="http://www.springframework.org/security/tags" %>
<div class="x_panel">
    <div class="x_title">
        <h2><i class="fa fa-bars"></i> Finance Setups</h2>
        <div class="clearfix"></div>
    </div>
    <div class="x_content">
<div class="panel panel-default">
    <div class="panel-body">
        <div class="row">
            <div class="col-md-6 col-sm-6 col-xs-12">
                <ul style="list-style-type: none;">
                    <li><h5 style="font-weight: bolder;">Finance Setups</h5></li>
                    <sec:authorize access="hasAnyAuthority('ACCESS_COA')">
                    <li><a href="<c:url value="/protected/accounts/chartsofaccounts"/>">Chart of Accounts
                    </a></li>
                    </sec:authorize>
                    <sec:authorize access="hasAnyAuthority('ACCESS_BANKS')">
                    <li><a href="<c:url value="/protected/accounts/banksdef"/>">Banks and Branches
                    </a></li>
                    </sec:authorize>
                    <li><a href="<c:url value="/protected/accounts/collectaccts"/>">Collection Accounts
                    </a></li>
                    <li><a href="<c:url value="/protected/setups/revitems/bankAccounts"/>">Bank Accounts
                    </a></li>
                    <li><a href="<c:url value="/protected/accounts/reportformats"/>">Accounting Reports Formats
                    </a></li>
                    <li><a href="<c:url value="/protected/accounts/openingbal"/>">Opening Balances
                    </a></li>
                    <li><a href="<c:url value="/protected/accounts/accountingPeriods"/>">Accounting Periods
                    </a></li>
<%--                    <li><a href="<c:url value="/protected/accounts/gltrans"/>">GL Listings</a></li>--%>
<%--                    <li><a href="<c:url value="/protected/accounts/sap"/>">Sap File--%>
<%--                    </a></li>--%>
                </ul>
            </div>
            <div class="col-md-6 col-sm-6 col-xs-12">

                </ul>
            </div>
        </div>
    </div>
</div>
</div>