<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="sec" uri="http://www.springframework.org/security/tags" %>
<script type="text/javascript" src="<c:url value="/js/modules/utils/select2builder.js"/>"></script>
<script type="text/javascript" src="<c:url value="/libs/rivets/rivets.js"/>"></script>
<script type="text/javascript" src="<c:url value="/js/modules/quotes/quotechecker.js"/>"></script>
<script type="text/javascript">
    var mck_id_quote = "${mckIdQuote}";
</script>
<div class="x_title">
    <h2><i class="fa fa-bars"></i> Quotation Details</h2>
    <div class="clearfix"></div>
</div>
<div class="x_panel">
    <div class="col-md-9 col-sm-9  offset-md-3">

        <h4 class="float-left blue" style="font-weight: bolder" id="h4pol"></h4>
        <input action="action" type="button" onclick="history.go(-1);" class="btn btn-primary float-right" value="Back"
               id="renbtn" style="display:none"/>


    </div>

    <%--    <div class="x_title">--%>
    <%--        <h4 id="wkflow-task-name">Quotation Details</h4>--%>

    <%--        <ul class="nav navbar-right panel_toolbox">--%>
    <%--            <li><a class="collapse-link"><i class="fa fa-chevron-up"></i></a>--%>
    <%--            </li>--%>
    <%--        </ul>--%>
    <%--    </div>--%>

    <div class="x_content">

        <form id="quot-form" class="form-horizontal form-label-left">
            <input type="hidden" id="quot-id" name="quoteId"/>
            <div class="item form-group form-required">
                <div class="col-md-6 col-xs-12">
                    <label for="quote-type" class="label-align col-md-5" id="quote-type">
                        Quote Type<span class="required">*</span></label>
                    <div class="col-md-7 col-xs-12 radio">
                        <label id="combined-quote">
                            <input type="radio" checked="" value="combined" name="quoteType">
                            Combined Quote
                        </label>
                        <label id="comparison-quote">
                            <input type="radio" value="comparison" name="quoteType">
                            Comparison Quote
                        </label>
                        <p class="form-control-static" id="quote-info"></p>
                    </div>
                </div>
            </div>
            <div class="item form-group form-required">
                <div class="col-md-6 col-xs-12">
                    <label for="clnt-type" class="label-align col-md-5">
                        Client Type<span class="required">*</span></label>
                    <div class="col-md-7 col-xs-12">
                        <select class="form-control" id="clnt-type" name="clientType" required>
                            <option value="">Select Client Type</option>
                            <option value="P">Prospect</option>
                            <option value="C">Client</option>
                        </select>
                    </div>
                </div>
                <div class="col-md-6 col-xs-12">

                </div>

            </div>
            <div class="item form-group form-required">
                <div class="col-md-6 col-xs-12">
                    <label for="curr-frm" class="label-align col-md-5">
                        Currency<span class="required">*</span></label>
                    <div class="col-md-7 col-xs-12">
                        <div id="edit-currency">
                            <input type="hidden" id="cur-id" name="currencyId"/>
                            <input type="hidden" id="cur-name">
                            <div id="curr-frm" class="form-control"
                                 select2-url="<c:url value="/protected/uw/policies/uwcurrencies"/>">

                            </div>
                        </div>
                        <div id="display-currency">
                            <p class="form-control-static" id="currency-info"></p>
                        </div>
                    </div>
                </div>
                <div class="col-md-6 col-xs-12">
                    <label for="client-frm" class="label-align col-md-5 quot-client">
                        Client<span class="required">*</span></label>
                    <div class="col-md-6">
                        <div id="edit-client">
                            <input type="hidden" id="client-id" name="clientId"/>
                            <input type="hidden" id="client-f-name">
                            <input type="hidden" id="client-other-name">
                            <div id="client-div">
                                <div id="client-frm" class="form-control" style="display: none"
                                     select2-url="<c:url value="/protected/uw/policies/uwClients"/>">

                                </div>

                                <a
                                        href="<c:url value="/protected/clients/setups/clientsform?type=quot"/>"
                                        id="btn-add-client" class="btn-sm btn-success btn-info" style="display: none">New</a>
                            </div>
                            <div id="prs-div">
                                <div id="prospects-frm" class="form-control" style="display: none"
                                     select2-url="<c:url value="/protected/quotes/selprospects"/>">

                                </div>

                            </div>
                        </div>
                        <div id="display-client">
                            <p class="form-control-static" id="client-info"></p>
                        </div>
                    </div>
                    <div class="col-md-1">
                        <input type="button" class="btn-sm btn-success btn-info" id="btn-add-prs" value="New"
                               style="display: none">
                    </div>
                </div>

            </div>

            <div class="item form-group form-required">
                <div class="col-md-6 col-xs-12">
                    <label for="sourcegroup-frm" class="label-align col-md-5">
                        Source Classification<span class="required">*</span></label>
                    <div class="col-md-7 col-xs-12">
                        <div id="edit-sourcegroup">
                            <input type="hidden" id="sourcegroup-id"/>
                            <input type="hidden" id="sourcegroup-name">
                            <div id="sourcegroup-frm" class="form-control"
                                 select2-url="<c:url value="/protected/quotes/sourcesgroups"/>">
                            </div>
                        </div>
                        <div id="display-sourcegroup">
                            <p class="form-control-static" id="sourcegroup-info"></p>
                        </div>
                    </div>
                </div>
                <div class="col-md-6 col-xs-12">
                    <label for="source-frm" class="label-align col-md-5">
                        Source<span class="required">*</span></label>
                    <div class="col-md-7 col-xs-12">
                        <div id="edit-source">
                            <input type="hidden" id="source-id" name="sourceId"/>
                            <input type="hidden" id="source-name">
                            <div id="source-frm" class="form-control"
                                 select2-url="<c:url value="/protected/quotes/businesssources"/>">
                            </div>
                        </div>
                        <div id="display-source">
                            <p class="form-control-static" id="source-info"></p>
                        </div>
                    </div>
                </div>
            </div>
            <div class="item form-group form-required">
                <div class="col-md-6 col-xs-12">
                    <label for="pm-mode-frm" class="label-align col-md-5">
                        Payment Mode<span class="required">*</span></label>
                    <div class="col-md-7 col-xs-12">
                        <div id="edit-payment-mode">
                            <input type="hidden" id="pm-id" name="paymentId"/>
                            <input type="hidden" id="pm-name">
                            <div id="pm-mode-frm" class="form-control"
                                 select2-url="<c:url value="/protected/uw/policies/uwpaymentmodes"/>">

                            </div>
                        </div>
                        <div id="display-payment-mode">
                            <p class="form-control-static" id="pay-mode-info"></p>
                        </div>
                    </div>
                </div>
                <div class="col-md-6 col-xs-12">
                    <label for="brn-frm" class="label-align col-md-5">
                        Branch<span class="required">*</span></label>
                    <div class="col-md-7 col-xs-12">
                        <div id="edit-branch" class="col-md-12">
                            <input type="hidden" id="brn-id" name="branchId"/>
                            <input type="hidden" id="brn-name">
                            <div id="brn-frm" class="form-control"
                                 select2-url="<c:url value="/protected/uw/policies/uwbranches"/>">

                            </div>
                        </div>
                        <div id="display-branch" class="col-md-12">
                            <p class="form-control-static" id="branch-info"></p>
                        </div>
                    </div>
                </div>
            </div>
            <div id="other-pol-details">
                <div class="item form-group form-required">
                    <div class="col-md-6 col-xs-12">
                        <label for="pol-no" class="label-align col-md-5">
                            Quotation No</label>
                        <div class="col-md-7 col-xs-12">
                            <input type="hidden" id="div-pol-no" name="polNo"/>
                            <p class="form-control-static" id="pol-no"></p>
                        </div>
                    </div>
                    <div class="col-md-6 col-xs-12">
                        <label for="pol-rev-no" class="label-align col-md-5">
                            Revision Number</label>
                        <div class="col-md-7 col-xs-12">
                            <input type="hidden" id="div-endos-no" name="polRevNo"/>
                            <p class="form-control-static" id="pol-rev-no"></p>
                        </div>
                    </div>
                </div>
                <div class="item form-group form-required">
                    <div class="col-md-6 col-xs-12">
                        <label for="pol-sum-insured" class="label-align col-md-5">
                            Sum Insured</label>
                        <div class="col-md-7 col-xs-12">
                            <p class="form-control-static" id="pol-sum-insured"></p>
                        </div>
                    </div>
                    <div class="col-md-6 col-xs-12">
                        <label for="pol-premium" class="label-align col-md-5">
                            Premium</label>
                        <div class="col-md-7 col-xs-12">

                            <p class="form-control-static" id="pol-premium"></p>
                        </div>
                    </div>
                </div>
                <div class="item form-group form-required">
                    <div class="col-md-6 col-xs-12">
                        <label for="pol-basic-prem" class="label-align col-md-5">
                            Gross Prem</label>
                        <div class="col-md-7 col-xs-12">
                            <p class="form-control-static" id="pol-basic-prem"></p>
                        </div>
                    </div>
                    <div class="col-md-6 col-xs-12">
                        <label for="pol-net-prem" class="label-align col-md-5">
                            Net Prem</label>
                        <div class="col-md-7 col-xs-12">
                            <p class="form-control-static" id="pol-net-prem"></p>
                        </div>
                    </div>
                </div>
                <div class="item form-group form-required">
                    <div class="col-md-6 col-xs-12">
                        <label for="pol-tl" class="label-align col-md-5">
                            Training Levy</label>
                        <div class="col-md-7 col-xs-12">
                            <p class="form-control-static" id="pol-tl"></p>
                        </div>
                    </div>
                    <div class="col-md-6 col-xs-12">
                        <label for="pol-sd" class="label-align col-md-5">
                            Stamp Duty</label>
                        <div class="col-md-7 col-xs-12">
                            <p class="form-control-static" id="pol-sd"></p>
                        </div>
                    </div>

                </div>
                <div class="item form-group form-required">
                    <div class="col-md-6 col-xs-12">
                        <label for="pol-phcf" class="label-align col-md-5">
                            PHF Fund</label>
                        <div class="col-md-7 col-xs-12">
                            <p class="form-control-static" id="pol-phcf"></p>
                        </div>
                    </div>
                    <%--                    <div class="col-md-6 col-xs-12">--%>
                    <%--                        <label for="pol-extras" class="label-align col-md-5">--%>
                    <%--                            Extras</label>--%>
                    <%--                        <div class="col-md-7 col-xs-12">--%>
                    <%--                            <p class="form-control-static" id="pol-extras"> </p>--%>
                    <%--                        </div>--%>
                    <%--                    </div>--%>
                    <div class="col-md-6 col-xs-12">
                        <label for="pol-comm-amt" class="label-align col-md-5">
                            Commission Amt</label>
                        <div class="col-md-7 col-xs-12">
                            <p class="form-control-static" id="pol-comm-amt"></p>
                        </div>
                    </div>
                </div>
                <div class="item form-group form-required">
                    <div class="col-md-6 col-xs-12">
                        <label for="pol-status" class="label-align col-md-5">
                            Quotation Status</label>
                        <div class="col-md-7 col-xs-12">
                            <p class="form-control-static" id="pol-status"></p>
                        </div>
                    </div>
                    <div class="col-md-6 col-xs-12">
                        <label for="pol-exp-date" class="label-align col-md-5">
                            Quotation Expiry Date</label>
                        <div class="col-md-7 col-xs-12">
                            <p class="form-control-static" id="pol-exp-date"></p>
                        </div>
                    </div>
                </div>
            </div>

        </form>
    </div>

    <div class="x_title">
        <h4>Quotation Product Details</h4>
        <ul class="nav navbar-right panel_toolbox">
            <li><a class="collapse-link"><i class="fa fa-chevron-up"></i></a>
            </li>
        </ul>
    </div>
    <div class="x_content">
        <div class="" role="tabpanel" data-example-id="togglable-tabs">
            <ul id="myProductTab" class="nav nav-tabs bar_tabs" role="tablist">
                <li role="presentation" class="active"><a href="#prod_content1"
                                                          id="prod-tab" role="tab" data-toggle="tab"
                                                          aria-expanded="true">Product Details</a>
                </li>
                <%--                    <li role="presentation" class="" id="workflow"><a href="#tab_content5"--%>
                <%--                                                                      role="tab" id="workflow-tab" data-toggle="tab" aria-expanded="false">Process Diagram</a>--%>
                <%--                    </li>--%>
            </ul>
            <div id="myProdTabContent" class="tab-content">
                <div role="tabpanel" class="tab-pane active"
                     id="prod_content1" aria-labelledby="prod-tab">
                    <div id="quot-prod-tbl">
                        <input type="hidden" id="quot-prod-id-pk"/>
                        <button class="btn btn-primary btn btn-primary" id="btn-add-quot-prod">New</button>
                        <button class="btn btn-primary btn btn-primary" id="btn-compare-quot">Compare</button>
                        <div class="cutom-container">
                            <input type="hidden" id="prod-binder">
                            <input type="hidden" id="comm-rate1">
                            <table id="prod_tbl" class="table" style="width:100%">
                                <thead>
                                <tr>
                                    <th>Contract</th>
                                    <th>Insurance Company</th>
                                    <th>Product</th>
                                    <th>Sum Insured</th>
                                    <th>Premium</th>
                                    <th>Commission</th>
                                    <th width="5%"></th>
                                    <th width="5%"></th>
                                </tr>
                                </thead>
                            </table>
                        </div>
                    </div>

                    <div class="x_content">
                        <div class="" role="tabpanel" data-example-id="togglable-tabs">
                            <ul id="myTab" class="nav nav-tabs bar_tabs" role="tablist">
                                <li role="presentation" class="active"><a href="#tab_content1"
                                                                          id="home-tab" role="tab" data-toggle="tab"
                                                                          aria-expanded="true">Risk Details</a>
                                </li>
                            </ul>
                            <div id="myTabContent" class="tab-content">
                                <div role="tabpanel" class="tab-pane active"
                                     id="tab_content1" aria-labelledby="home-tab">
                                    <input type="hidden" id="risk-det-id-pk"/>
                                    <div id="risk-div">
                                        <div class="cutom-container">
                                            <table id="risk_tbl" class="table" style="width:100%">
                                                <thead>
                                                <tr>
                                                    <th>Risk ID</th>
                                                    <th>Risk Desc</th>
                                                    <th>Classification</th>
                                                    <th>Cover Type</th>
                                                    <th>Sum Insured</th>
                                                    <th>Premium</th>
                                                    <th width="5%"></th>
                                                    <th width="5%"></th>
                                                </tr>
                                                </thead>
                                            </table>
                                        </div>
                                    </div>

                                    <div class="x_title">
                                        <h4>Premium Items</h4>
                                        <ul class="nav navbar-right panel_toolbox">
                                            <li><a class="collapse-link"><i class="fa fa-chevron-up"></i></a>
                                            </li>
                                        </ul>
                                    </div>
                                    <div class="x_content">
                                        <div id="sect-div">
                                            <div class="" role="tabpanel" data-example-id="togglable-tabs">
                                                <ul id="myTab1" class="nav nav-tabs bar_tabs" role="tablist">
                                                    <li role="presentation" class="active"><a href="#tab_content11"
                                                                                              id="home-tabb" role="tab"
                                                                                              data-toggle="tab"
                                                                                              aria-controls="home"
                                                                                              aria-expanded="true">Premium
                                                        Items</a>
                                                    </li>
                                                </ul>
                                                <div id="myTabContent2" class="tab-content">
                                                    <div role="tabpanel" class="tab-pane active" id="tab_content11"
                                                         aria-labelledby="home-tab">
                                                        <div class="cutom-container">
                                                            <table id="section_tbl" class="table" style="width:100%">
                                                                <thead>
                                                                <tr>
                                                                    <th>Premium Item</th>
                                                                    <th>Limit Amount</th>
                                                                    <th>Rate</th>
                                                                    <th>Premium</th>
                                                                    <th>Div Factor</th>
                                                                    <th>Free Limit</th>
                                                                    <th width="5%"></th>
                                                                    <th width="5%"></th>
                                                                </tr>
                                                                </thead>
                                                            </table>
                                                        </div>
                                                    </div>
                                                </div>

                                            </div>
                                        </div>

                                    </div>

                                </div>
                            </div>


                        </div>


                    </div>
                </div>
            </div>
        </div>
    </div>
    <div id="quot-panel">
        <sec:authorize access="hasAnyAuthority('AUTHORIZE_QUOTE')">
            <input type="button" class="btn btn-primary btn btn-danger float-right"
                   value="Reject Task" id="btn-reject-quot">
        </sec:authorize>

        <sec:authorize access="hasAnyAuthority('AUTHORIZE_QUOTE')">
            <input type="button" class="btn btn-primary float-right"
                   value="Approve Task" id="btn-approve-quote">
        </sec:authorize>
    </div>
    <div id="approvalModal" class="modal fade" tabindex="-1" role="dialog">
        <div class="modal-dialog" role="document">
            <div class="modal-content">
                <div class="modal-header">
                    <h5 class="modal-title">Approve Task</h5>
                    <button type="button" class="close" data-dismiss="modal" aria-label="Close">
                        <span aria-hidden="true">&times;</span>
                    </button>
                </div>
                <div class="modal-body">
                    <p>Are you sure you want to approve this task?</p>
                    <input type="hidden" id="approvalTaskId">
                </div>
                <div class="modal-footer">
                    <button type="button" class="btn btn-secondary" data-dismiss="modal">Cancel</button>
                    <button type="button" id="approveConfirmButton" class="btn btn-primary">Approve</button>
                </div>
            </div>
        </div>
    </div>

    <!-- Rejection Modal -->
    <div id="rejectionModal" class="modal fade" tabindex="-1" role="dialog">
        <div class="modal-dialog" role="document">
            <div class="modal-content">
                <div class="modal-header">
                    <h5 class="modal-title">Reject Task</h5>
                    <button type="button" class="close" data-dismiss="modal" aria-label="Close">
                        <span aria-hidden="true">&times;</span>
                    </button>
                </div>
                <div class="modal-body">
                    <textarea id="rejectionReason" class="form-control" placeholder="Enter the reason for rejection"></textarea>
                </div>
                <div class="modal-footer">
                    <button type="button" class="btn btn-secondary" data-dismiss="modal">Close</button>
                    <button type="button" id="rejectConfirmButton" class="btn btn-danger">Reject</button>
                </div>
            </div>
        </div>
    </div>