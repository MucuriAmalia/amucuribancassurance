<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="sec" uri="http://www.springframework.org/security/tags" %>
<div class="x_panel">
    <div class="x_title">
        <h2><i class="fa fa-bars"></i> Data Migration Upload</h2>
        <div class="clearfix"></div>
    </div>
    <div class="x_content">
        <div class="panel panel-default">
            <div class="panel-body">
                <div class="row">
                    <sec:authorize access="hasAnyAuthority('ACCESS_UW')">
                        <div class="col-md-6 col-sm-6 col-xs-12">
                            <ul style="list-style-type: none;">
                                <li><h5 style="font-weight: bolder;">Load General Insurance Data</h5></li>
                                <sec:authorize access="hasAnyAuthority('ACCESS_NEW_BUSINESS')">
                                    <li><a href="<c:url value="/protected/transprocessing/transHome"/>">Loaded Policy
                                        Creation</a></li>
                                </sec:authorize>
                            </ul>
                        </div>
                    </sec:authorize>
                    <sec:authorize access="hasAnyAuthority('SETUP_ACCOUNTS')">
                        <div class="col-md-6 col-sm-6 col-xs-12">
                            <ul style="list-style-type: none;">
                                <li><h5 style="font-weight: bolder;">Load Clients Data</h5></li>
                                <sec:authorize access="hasAnyAuthority('ACCESS_NEW_BUSINESS')">
                                    <li><a href="<c:url value="/protected/clientmigration/clientHomeMigration"/>">Loaded Client
                                        Data For Migration</a></li>
                                </sec:authorize>
                            </ul>
                        </div>
                    </sec:authorize>
                    <sec:authorize access="hasAnyAuthority('SETUP_ACCOUNTS')">
                        <div class="col-md-6 col-sm-6 col-xs-12">
                            <ul style="list-style-type: none;">
                                <li><h5 style="font-weight: bolder;">Authorize General Insurance Data</h5></li>
                                <sec:authorize access="hasAnyAuthority('ACCESS_NEW_BUSINESS')">
                                    <li><a href="<c:url value="/protected/transprocessing/authorizedBulkPolicy"/>">Loaded
                                        Policy Authorization</a></li>
                                </sec:authorize>
                            </ul>
                        </div>
                    </sec:authorize>
                </div>
                <div class="row">
                    <sec:authorize access="hasAnyAuthority('ACCESS_UW')">
                        <div class="col-md-6 col-sm-6 col-xs-12">
                            <ul style="list-style-type: none;">
                                <li><h5 style="font-weight: bolder;">Load Life Insurance Data</h5></li>
                            </ul>
                        </div>
                    </sec:authorize>
                    <sec:authorize access="hasAnyAuthority('SETUP_ACCOUNTS')">
                        <div class="col-md-6 col-sm-6 col-xs-12">
                            <ul style="list-style-type: none;">
                                <li><h5 style="font-weight: bolder;">Authorize Life Insurance Data</h5></li>
                            </ul>
                        </div>
                    </sec:authorize>
                </div>
            </div>
        </div>
    </div>
</div>