<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="sec" uri="http://www.springframework.org/security/tags" %>
<div class="x_panel">
    <div class="x_title">
        <h2><i class="fa fa-bars"></i> Upload Processing</h2>
        <div class="clearfix"></div>
    </div>
    <div class="x_content">
        <div class="panel panel-default">
            <div class="panel-body">
                <div class="row">
                    <sec:authorize access="hasAnyAuthority('ACCESS_UW')">
                        <div class="col-md-6 col-sm-6 col-xs-12">
                            <ul style="list-style-type: none;">
                                <li><h5 style="font-weight: bolder;">Upload Bulk General Insurance</h5></li>
                                <sec:authorize access="hasAnyAuthority('ACCESS_NEW_BUSINESS')">
                                    <li><a href="<c:url value="/protected/bulk/policycreation/bulkPolHome"/>">Bulk
                                        Policy Creation</a></li>
                                </sec:authorize>
                                <sec:authorize access="hasAnyAuthority('ACCESS_NEW_BUSINESS')">
                                    <li><a href="<c:url value="/protected/wezesha/polcreation/wezeshaCreation"/>">Wezesha
                                        Policy Creation</a></li>
                                </sec:authorize>
                                <sec:authorize access="hasAnyAuthority('ACCESS_NEW_BUSINESS')">
                                    <li><a href="<c:url value="/protected/embedpackaged/insurance/creatembedpackageInsur"/>">Embedded
                                            Packaged Insurance Creation</a></li>
                                </sec:authorize>
                                <sec:authorize access="hasAnyAuthority('ACCESS_NEW_BUSINESS')">
                                    <li><a href="<c:url value="/protected/bulk/staffmotor/bulkstaffmotor"/>">ABSA Staff Motor Creation </a></li>
                                </sec:authorize>
                                <sec:authorize access="hasAnyAuthority('ACCESS_NEW_BUSINESS')">
                                    <li>
                                        <a href="<c:url value="/protected/credit/card/createcreditcard"/>">Embedded
                                            Payment Card Creation</a></li>
                                </sec:authorize>

                            </ul>
                        </div>
                    </sec:authorize>
                    <sec:authorize access="hasAnyAuthority('SETUP_ACCOUNTS')">
                        <div class="col-md-6 col-sm-6 col-xs-12">
                            <ul style="list-style-type: none;">
                                <li><h5 style="font-weight: bolder;">Authorize Bulk General Insurance</h5></li>
                                <sec:authorize access="hasAnyAuthority('ACCESS_NEW_BUSINESS')">
                                    <li><a href="<c:url value="/protected/bulk/policycreation/viewbulkpolicyhome"/>">Bulk
                                      Policy Authorization</a></li>
                                </sec:authorize>
                                <sec:authorize access="hasAnyAuthority('ACCESS_NEW_BUSINESS')">
                                    <li><a href="<c:url value="/protected/wezesha/polcreation/viewWezesha"/>">Wezesha
                                        Policy Authorization</a></li>
                                </sec:authorize>
                                <sec:authorize access="hasAnyAuthority('ACCESS_NEW_BUSINESS')">
                                    <li><a href="<c:url value="/protected/embedpackaged/insurance/viewembedpackageInsur"/>">Embedded
                                            Packaged Insurance Authorization</a></li>
                                </sec:authorize>
                                <sec:authorize access="hasAnyAuthority('ACCESS_NEW_BUSINESS')">
                                    <li><a href="<c:url value="/protected/bulk/staffmotor/viewbulkstaffmotor"/>">ABSA Staff Motor Authorization</a></li>
                                </sec:authorize>
                                <sec:authorize access="hasAnyAuthority('ACCESS_NEW_BUSINESS')">
                                    <li>
                                        <a href="<c:url value="/protected/credit/card/viewcreditcard"/>">Embedded
                                            Payment Card Authorization</a></li>
                                </sec:authorize>
                            </ul>
                        </div>
                    </sec:authorize>
                </div>
                <div class="row">
                    <sec:authorize access="hasAnyAuthority('ACCESS_UW')">
                        <div class="col-md-6 col-sm-6 col-xs-12">
                            <ul style="list-style-type: none;">
                                <li><h5 style="font-weight: bolder;">Upload Bulk Life Insurance</h5></li>
                              <sec:authorize access="hasAnyAuthority('ACCESS_NEW_BUSINESS')">
                                  <li><a href="<c:url value="/protected/bulk/timiza/createtimiza"/>">Timiza
                                      Creation</a></li>
                              </sec:authorize>
                              <sec:authorize access="hasAnyAuthority('ACCESS_NEW_BUSINESS')">
                                <li>
                                  <a href="<c:url value="/protected/credit/life/createcreditlife"/>">Credit
                                    Life Creation</a></li>
                              </sec:authorize>
                                <sec:authorize access="hasAnyAuthority('ACCESS_NEW_BUSINESS')">
                                    <li> <a href="<c:url value="/protected/credit/shield/createcreditshield"/>">Credit
                                            Shield Creation</a></li>
                                </sec:authorize>
                                <sec:authorize access="hasAnyAuthority('ACCESS_NEW_BUSINESS')">
                                    <li>
                                        <a href="<c:url value="/protected/embedretrench/insurance/creatembedretrench"/>">Embedded
                                            Retrenchment Creation</a></li>
                                </sec:authorize>
                                <sec:authorize access="hasAnyAuthority('ACCESS_NEW_BUSINESS')">
                                    <li>
                                        <a href="<c:url value="/protected/group/life/creategrouplife"/>">Group
                                            Life Creation</a></li>
                                </sec:authorize>

                                <sec:authorize access="hasAnyAuthority('ACCESS_NEW_BUSINESS')">
                                    <li>
                                        <a href="<c:url value="/protected/mortgage/life/createmortgagelife"/>">Mortgage
                                            Life Creation</a></li>
                                </sec:authorize>
                            </ul>
                        </div>
                    </sec:authorize>
                    <sec:authorize access="hasAnyAuthority('SETUP_ACCOUNTS')">
                        <div class="col-md-6 col-sm-6 col-xs-12">
                            <ul style="list-style-type: none;">
                                <li><h5 style="font-weight: bolder;">Authorize Bulk Life Insurance</h5></li>
                                <sec:authorize access="hasAnyAuthority('ACCESS_NEW_BUSINESS')">
                                    <li><a href="<c:url value="/protected/bulk/timiza/viewtimiza"/>">Timiza
                                        Authorization</a></li>
                                </sec:authorize>
                              <sec:authorize access="hasAnyAuthority('ACCESS_NEW_BUSINESS')">
                                <li>
                                  <a href="<c:url value="/protected/credit/life/viewcreditlife"/>">Credit
                                    Life Authorization</a></li>
                              </sec:authorize>
                                <sec:authorize access="hasAnyAuthority('ACCESS_NEW_BUSINESS')">
                                    <li>
                                        <a href="<c:url value="/protected/credit/shield/viewcreditshield"/>">Credit
                                            Shield Authorization</a></li>
                                </sec:authorize>
                                <sec:authorize access="hasAnyAuthority('ACCESS_NEW_BUSINESS')">
                                    <li>
                                        <a href="<c:url value="/protected/embedretrench/insurance/viewembedretrench"/>">Embedded
                                            Retrenchment Authorization</a></li>
                                </sec:authorize>
                                <sec:authorize access="hasAnyAuthority('ACCESS_NEW_BUSINESS')">
                                    <li>
                                        <a href="<c:url value="/protected/group/life/viewgrouplife"/>">Group
                                            Life Authorization</a></li>
                                </sec:authorize>

                                <sec:authorize access="hasAnyAuthority('ACCESS_NEW_BUSINESS')">
                                    <li>
                                        <a href="<c:url value="/protected/mortgage/life/viewmortgagelife"/>">Mortgage
                                            Life Authorization</a></li>
                                </sec:authorize>
                            </ul>
                        </div>
                    </sec:authorize>
                </div>
<%--                *********************************************************--%>
<%--                 * SCYLLA 8434--%>
<%--                 *********************************************************--%>
                <div class="row">
                    <sec:authorize access="hasAnyAuthority('ACCESS_UW')">
                        <div class="col-md-6 col-sm-6 col-xs-12">
                            <ul style="list-style-type: none;">
                                <li><h5 style="font-weight: bolder;">Upload Non New Business Action</h5></li>
                                <sec:authorize access="hasAnyAuthority('ACCESS_NEW_BUSINESS')">
                                    <li><a href="<c:url value="/protected/endorsement/bulk/endorsementcreation"/>">Endorsements
                                        Creation</a></li>
                                </sec:authorize>
                                <sec:authorize access="hasAnyAuthority('ACCESS_NEW_BUSINESS')">
                                    <li>
                                        <a href="<c:url value="/protected/endorsement/bulk/createrenewals"/>">Renewals Creation</a></li>
                                </sec:authorize>
                                <sec:authorize access="hasAnyAuthority('ACCESS_NEW_BUSINESS')">
                                    <li> <a href="<c:url value="/protected/endorsement/bulk/cancellationscreation"/>">Cancellations Creation</a></li>
                                </sec:authorize>
                                    <%--
                                        <sec:authorize access="hasAnyAuthority('ACCESS_NEW_BUSINESS')">
                                            <li> <a href="<c:url value="/protected/endorsement/bulk/refundscreations"/>">Refund Creation</a></li>
                                        </sec:authorize>
                                        --%>
                            </ul>
                        </div>
                    </sec:authorize>
                    <sec:authorize access="hasAnyAuthority('SETUP_ACCOUNTS')">
                        <div class="col-md-6 col-sm-6 col-xs-12">
                            <ul style="list-style-type: none;">
                                <li><h5 style="font-weight: bolder;">Authorize Non New New Business</h5></li>
                                <sec:authorize access="hasAnyAuthority('ACCESS_NEW_BUSINESS')">
                                    <li><a href="<c:url value="/protected/endorsement/bulk/endorsementauthorization"/>">Endorsements
                                        Authorization</a></li>
                                </sec:authorize>
                                <sec:authorize access="hasAnyAuthority('ACCESS_NEW_BUSINESS')">
                                    <li>
                                        <a href="<c:url value="/protected/endorsement/bulk/authorizerenewals"/>">Renewals Authorization</a></li>
                                </sec:authorize>
                                <sec:authorize access="hasAnyAuthority('ACCESS_NEW_BUSINESS')">
                                    <li>
                                        <a href="<c:url value="/protected/endorsement/bulk/cancellationsauthorization"/>">Cancellations Authorization</a></li>
                                </sec:authorize>
                              <sec:authorize access="hasAnyAuthority('ACCESS_NEW_BUSINESS')">
                                 <li>
                                     <a href="<c:url value="/protected/endorsement/bulk/refundsauthorization"/>">Refund Authorization</a></li>
                             </sec:authorize>

                            </ul>
                        </div>
                    </sec:authorize>
                </div>
            </div>
        </div>
    </div>
</div>