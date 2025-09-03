<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="sec" uri="http://www.springframework.org/security/tags" %>
<script type="text/javascript" src="<c:url value="/js/modules/utils/select2builder.js"/>"></script>
<script type="text/javascript" src="<c:url value="/libs/rivets/rivets.js"/>"></script>
<script type="text/javascript" src="<c:url value="/js/modules/clients/prospectsHelper.js"/>"></script>
<script type="text/javascript" src="<c:url value="/js/modules/quotes/quottrans.js"/>"></script>
<style>
    /* Professional Button Intelligence Styles */
    .btn-processing {
        opacity: 0.6;
        cursor: not-allowed !important;
        pointer-events: none;
        position: relative;
    }

    .btn-processing::after {
        content: '';
        position: absolute;
        width: 16px;
        height: 16px;
        margin: auto;
        border: 2px solid transparent;
        border-top-color: #ffffff;
        border-radius: 50%;
        animation: button-loading-spinner 1s ease infinite;
        top: 50%;
        left: 50%;
        transform: translate(-50%, -50%);
    }

    @keyframes button-loading-spinner {
        from { transform: translate(-50%, -50%) rotate(0turn); }
        to { transform: translate(-50%, -50%) rotate(1turn); }
    }

    .btn-success-flash {
        background-color: #5cb85c !important;
        border-color: #4cae4c !important;
        color: white !important;
        transition: all 0.3s ease;
    }

    .btn-error-flash {
        background-color: #d9534f !important;
        border-color: #d43f3a !important;
        color: white !important;
        transition: all 0.3s ease;
    }
</style>
<script type="text/javascript">
    var quotId = ${quotId};

    function unlockForm() {
        if (quotId !== -2000 && quotId !== 'undefined') {
            navigator.sendBeacon(SERVLET_CONTEXT + '/protected/home/unlockForm',
                new URLSearchParams({ "formName": "QUOTE", "formId": quotId})
            );
            console.log("Form unlock request sent.");
        }
    }

    window.addEventListener("beforeunload", unlockForm);
</script>
<div class="x_panel">
    <div class="x_title">
        <h2><i class="fa fa-bars"></i> Quotation Details</h2>
        <div class="clearfix"></div>
    </div>
    <div class="x_panel">
        <h4 class="float-left blue" style="font-weight: bolder" id="h4pol"></h4>
        <div class="float-right">

            <div class="quot-panel">

                <sec:authorize access="hasAnyAuthority('ACCESS_QUOT_REPORTS')">
                    <button type="button" class="btn btn-primary float-right"
                            id="btn-quot-reports" data-toggle="modal" data-target="#reportsModal"
                            style="display:none">
                        <i class="fa fa-print"></i>
                    </button>
                </sec:authorize>

                <sec:authorize access="hasAnyAuthority('MAKE_READY_QUOTE')">
                    <input type="button" class="btn btn-primary float-right"
                           value="Unsubmit" id="btn-undo-make-ready-policy"
                           style="display:none">
                </sec:authorize>



            </div>
        </div>
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
                            <input type="hidden" id="client-national-id">
                            <div id="client-div">
                                <div id="client-frm" class="form-control" style="display: none"
                                     select2-url="<c:url value="/protected/uw/policies/uwClients"/>">

                                </div>

                                <a
                                        href="<c:url value="/protected/clients/setups/clientsform?type=quot"/>"
                                        id="btn-add-client" class="btn-sm btn-success btn-info"
                                        style="display: none">New</a>
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

            <div class="item form-group">
                <div class="col-md-6 col-xs-12">
                    <label for="sourcegroup-frm" class="label-align col-md-5">
                        Source Classification</label>
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
                        Source</label>
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
            <div class="item form-group" id="act-display">
                <div class="col-md-6 col-xs-12" id="activation-code-display" style="display: none;">
                    <label for="activation-code" class="label-align col-md-5">
                        Activation Code</label>
                    <div class="col-md-7 col-xs-12">
                        <input type="text" name="activationCode" id="activation-code"
                               class="form-control"
                               placeholder="Enter Campaign Activation Code">
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
                    <div id="edit-branch" >
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
            <div class="item form-group form-required">
                <div class="col-md-6 col-xs-12 introducer-agent-type">
                    <div class="item form-group form-required">
                        <label for="houseId" class="label-align col-md-5">
                            Introducer </label>
                        <div class="col-md-7 col-xs-12">
                            <input type="hidden" id="introducer-id" name="introducerAgentId"/>
                            <input type="hidden" id="introducer-name" name="introducerAgent">
                            <div id="introducer-frm" class="form-control"
                                 select2-url="<c:url value="/protected/uw/policies/introducergents"/>">

                        </div>
                    </div>
                </div>
            </div>
            <div class="col-md-6 col-xs-12">
                <label for="introducer-absaNo-name" class="label-align col-md-5">Introducer AB No</label>
                <div class="col-md-7 col-xs-12">
                    <input type="hidden" id="introducer-absa-No-id" name="absaNoIntroducer"/>
                    <input type="text" id="introducer-absaNo-name" class="form-control" readonly>
                </div>
            </div>
        </div>
        <div class="item form-group form-required">
            <div class="col-md-6 col-xs-12 leads-man-type">
                <div class="item form-group form-required">
                    <label for="leadsId" class="label-align col-md-5">Leads Man</label>
                    <div class="col-md-7 col-xs-12">
                        <input type="hidden" id="leads-man-id" name="leadsManId"/>
                        <input type="hidden" id="leads-man-name">
                        <div id="leads-man-frm" class="form-control"
                             select2-url="<c:url value='/protected/uw/policies/leadsMan'/>">
                        </div>
                    </div>
                </div>
            </div>
            <div class="col-md-6 col-xs-12">
                <label for="leads-absaNo-name" class="label-align col-md-5">Leads Man AB No</label>
                <div class="col-md-7 col-xs-12">
                    <input type="hidden" id="leads-absaNo-id" name="absaNoLeadsMan"/>
                    <input type="text" id="leads-absaNo-name" class="form-control" readonly>
                </div>
            </div>
        </div>
        <div class="item form-group">
            <div class="col-md-6 col-xs-12" >
                <div class="item form-group">
                    <label for="agent-type" class="label-align col-md-5"> Sub Agent/ Marketer</label>
                    <div class="col-md-7 col-xs-12">
                        <select class="form-control" id="agent-type" name="accountTypes">
                            <option value="">Select Type</option>
                            <option value="SUB">Sub Agent</option>
                            <option value="MRK">Marketer</option>
                        </select>
                    </div>
                    <div class="col-md-3">
                    </div>
                </div>
            </div>
            <div class="col-md-6 col-xs-12 sub-agent-type" style="display: none;">
                <div class="item form-group form-required">
                    <label for="houseId" class="label-align col-md-5">Sub Agent</label>
                    <div class="col-md-7 col-xs-12">
                        <input type="hidden" id="sub-agent-id" name="subAgentId"/>
                        <input type="hidden" id="sub-agent-name">
                        <div id="sub-agent-frm" class="form-control"
                             select2-url="<c:url value='/protected/uw/policies/inhouseagents'/>">
                        </div>
                    </div>
                </div>
            </div>
            <div class="col-md-6 col-xs-12 marketer-agent-type" style="display: none;">
                <div class="item form-group form-required">
                    <label for="houseId" class="label-align col-md-5">Marketer</label>
                    <div class="col-md-7 col-xs-12">
                        <input type="hidden" id="marketer-id" name="marketerAgentId"/>
                        <input type="hidden" id="marketer-name">
                        <div id="marketer-frm" class="form-control"
                             select2-url="<c:url value='/protected/uw/policies/marketeragents'/>">
                        </div>
                    </div>
                </div>
            </div>
        </div>
        <div class="item form-group form-required">
            <div class="col-md-6 col-xs-12 sub-agent-absNo" style="display: none;">
                <label for="sub-agent-absaNo-name" class="label-align col-md-5">Sub Agent AB No</label>
                <div class="col-md-7 col-xs-12">
                    <input type="hidden" id="sub-agent-absaNo-id" name="absaNoSubAgent"/>
                    <input type="text" id="sub-agent-absaNo-name" class="form-control" readonly>
                </div>
            </div>
            <div class="col-md-6 col-xs-12 marketer-agent-absNo" style="display: none;">
                <label for="marketer-absaNo-name" class="label-align col-md-5">Marketer AB No</label>
                <div class="col-md-7 col-xs-12">
                    <input type="hidden" id="marketer-absaNo-id" name="absaNoMarketer"/>
                    <input type="text" id="marketer-absaNo-name" class="form-control" readonly>
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
            <div class="item form-group form-required" id="display-suminsured">
                <div class="col-md-6 col-xs-12">
                    <label for="pol-sum-insured" class="label-align col-md-5">
                        Sum Insured</label>
                    <div class="col-md-7 col-xs-12">
                        <p class="form-control-static" id="pol-sum-insured"></p>
                    </div>
                </div>
                <div class="col-md-6 col-xs-12">
                    <label for="pol-basic-prem" class="label-align col-md-5">
                        Gross Prem</label>
                    <div class="col-md-7 col-xs-12">
                        <p class="form-control-static" id="pol-basic-prem"></p>
                    </div>
                </div>
<%--                <div class="col-md-6 col-xs-12">--%>
<%--                    <label for="pol-premium" class="label-align col-md-5">--%>
<%--                        Premium</label>--%>
<%--                    <div class="col-md-7 col-xs-12">--%>

                    <%--                        <p class="form-control-static" id="pol-premium"></p>--%>
                    <%--                    </div>--%>
                    <%--                </div>--%>
                </div>
                <%--            <div class="item form-group form-required">--%>
                <%--                <div class="col-md-6 col-xs-12">--%>
                <%--                    <label for="pol-net-prem" class="label-align col-md-5">--%>
                <%--                        Net Prem</label>--%>
                <%--                    <div class="col-md-7 col-xs-12">--%>
                <%--                        <p class="form-control-static" id="pol-net-prem"></p>--%>
                <%--                    </div>--%>
                <%--                </div>--%>
                <%--            </div>--%>
                <div class="item form-group form-required" id="display-tl">
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
                <div class="item form-group form-required" id="display-whtx">
                    <div class="col-md-6 col-xs-12">
                        <div class="item form-group form-required">
                            <label for="pol-whtx" class="label-align col-md-5">
                                WHTX Tax</label>
                            <div class="col-md-7 col-xs-12">
                                <p class="form-control-static" id="pol-whtx"></p>
                            </div>
                        </div>
                    </div>
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
                </div>
                <div class="item form-group form-required">
                    <div class="col-md-6 col-xs-12" id="display-comm">
                        <label for="pol-comm-amt" class="label-align col-md-5">
                            Commission Amt</label>
                        <div class="col-md-7 col-xs-12">
                            <p class="form-control-static" id="pol-comm-amt"></p>
                        </div>
                    </div>
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
                                    <th>Status</th>
                                    <th width="5%"></th>
                                    <th width="5%"></th>
                                </tr>
                                </thead>
                            </table>
                        </div>
                    </div>
                    <div id="quot-prod-div">
                        <button class="btn btn-primary btn btn-primary" id="btn-save-quot-prod">Save</button>
                        <button class="btn btn-primary btn btn-primary" id="btn-cancel-quot-prod">Cancel</button>
                        <form id="quot-pro-form" class="form-horizontal form-label-left">
                            <input type="hidden" id="risk-bind-code"/>
                            <input type="hidden" id="quot-prod-pk" name="quoteProductId"/>
                            <div class="item form-group form-required">
                                <div class="col-md-6 col-xs-12">
                                    <label for="prd-code" class="label-align col-md-5">Product<span
                                            class="required">*</span></label>
                                    <div class="col-md-7 col-xs-12">
                                        <input type="hidden" id="prd-id" rv-value="product.proCode" name="product"/>
                                        <input type="hidden" id="pr-name">
                                        <div id="prd-code" class="form-control"
                                             select2-url="<c:url value="/protected/setups/binders/selgenproducts"/>">

                                        </div>
                                    </div>
                                </div>
                                <div class="col-md-6 col-xs-12">
                                    <label for="pol-bin-type" class="label-align col-md-5">
                                        Contract Type<span class="required">*</span></label>
                                    <div class="col-md-7 col-xs-12">
                                        <select class="form-control" id="pol-bin-type"
                                                required>
                                            <option value="">Contract Type</option>
                                            <option value="B">Contract</option>
                                            <option value="M">Negotiable</option>
                                        </select>
                                    </div>
                                </div>
                            </div>
                            <div class="item form-group form-required">
                                <div class="col-md-6 col-xs-12">
                                    <label for="binder-frm" class="label-align col-md-5">
                                        Contract<span class="required">*</span></label>
                                    <div class="col-md-7 col-xs-12">
                                        <div id="edit-binder">
                                            <input type="hidden" id="binder-id" name="bindCode"/>
                                            <input type="hidden" id="product-id" name="prodId"/>
                                            <input type="hidden" id="pol-agent-id" name="agentId"/>
                                            <input type="hidden" id="bind-name">
                                            <div id="binder-frm" class="form-control"
                                                 select2-url="<c:url value="/protected/uw/policies/uwBinders"/>">

                                            </div>
                                        </div>
                                        <div id="display-binder">
                                            <p class="form-control-static" id="binder-info"></p>
                                        </div>
                                    </div>
                                </div>
                                <div class="col-md-6 col-xs-12">
                                    <label for="pol-ins-comp" class="label-align col-md-5">
                                        Insurance Company</label>
                                    <div class="col-md-7 col-xs-12">
                                        <p class="form-control-static" id="pol-ins-comp"></p>
                                    </div>
                                </div>
                            </div>
                        </form>
                    </div>


                    <div class="x_content">
                        <div class="" role="tabpanel" data-example-id="togglable-tabs">
                            <ul id="myTab" class="nav nav-tabs bar_tabs" role="tablist">
                                <li role="presentation" class="active"><a href="#tab_content1"
                                                                          id="home-tab" role="tab" data-toggle="tab"
                                                                          aria-expanded="true">Risk Details</a>
                                </li>
                                <li role="presentation" class="" id="show-taxes"><a href="#tab_content2"
                                                                                    role="tab" id="profile-tab"
                                                                                    data-toggle="tab"
                                                                                    aria-expanded="false">Levies</a>
                                </li>
                                <li role="presentation" class="" id="show-clauses"><a href="#tab_content3"
                                                                                      role="tab" id="comm-rates-tab"
                                                                                      data-toggle="tab"
                                                                                      aria-expanded="false">Clauses</a>
                                </li>
                            </ul>
                            <div id="myTabContent" class="tab-content">
                                <div role="tabpanel" class="tab-pane active"
                                     id="tab_content1" aria-labelledby="home-tab">
                                    <input type="hidden" id="risk-det-id-pk"/>
                                    <button class="btn btn-primary btn btn-primary" id="btn-add-risk">New</button>
                                    <button class="btn btn-primary btn btn-primary" id="btn-save-risk">Save</button>
                                    <button class="btn btn-primary btn btn-primary" id="btn-save-cancel">Cancel</button>
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
                                    <form id="risk-form" class="form-horizontal form-label-left">
                                        <input type="hidden" id="risk-code-pk" name="riskId"/>
                                        <input type="hidden" id="risk-pro-code-pk" name="productCode"/>
                                        <div class="item form-group form-required">
                                            <div class="col-md-6 col-xs-12">
                                                <label for="insured-type" class="label-align col-md-5">
                                                    Insured Type<span class="required">*</span></label>
                                                <div class="col-md-7 col-xs-12">
                                                    <select class="form-control" id="insured-type" name="insuredType"
                                                            required>
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
                                                <label for="insured-frm" class="label-align col-md-5">
                                                    Insured<span class="required">*</span></label>
                                                <div class="col-md-7 col-xs-12">
                                                    <input type="hidden" id="insured-code" name="insuredCode"/>
                                                    <input type="hidden" id="insured-name">
                                                    <input type="hidden" name="riskIdentifier"/>
                                                    <input type="hidden" id="insured-other-name">
                                                    <input type="hidden" id="insured-id">
                                                    <div id="insured-clnt-div">
                                                        <div id="insured-frm" class="form-control"
                                                             select2-url="<c:url value="/protected/uw/policies/uwClients"/>">

                                                        </div>
                                                    </div>
                                                    <div id="insured-prs-div">
                                                        <div id="insured-prs-frm" class="form-control"
                                                             select2-url="<c:url value="/protected/quotes/selprospects"/>">

                                                        </div>
                                                    </div>
                                                </div>
                                            </div>
                                            <div class="col-md-6 col-xs-12">
                                                <label for="risk-binder" class="label-align col-md-5">
                                                    Contract<span class="required">*</span></label>
                                                <div class="col-md-7 col-xs-12">
                                                    <input type="hidden" id="risk-binder-code" name="bindCode"/>
                                                    <p class="form-control-static" id="risk-binder"></p>
                                                </div>
                                            </div>
                                        </div>
                                        <div class="item form-group form-required">
                                            <div class="col-md-6 col-xs-12">
                                                <label for="subclass-frm" class="label-align col-md-5">
                                                    Classification<span class="required">*</span></label>
                                                <div class="col-md-7 col-xs-12">
                                                    <input type="hidden" id="risk-sub-code" name="sclCode"/>
                                                    <input type="hidden" id="sub-name">
                                                    <div id="subclass-frm" class="form-control"
                                                         select2-url="<c:url value="/protected/uw/policies/uwsubclasses"/>">

                                                    </div>
                                                </div>
                                            </div>
                                            <div class="col-md-6 col-xs-12">
                                                <label for="covertypes-frm" class="label-align col-md-5">
                                                    Cover Type<span class="required">*</span></label>
                                                <div class="col-md-7 col-xs-12">
                                                    <input type="hidden" id="risk-cov-code" name="coverCode"/>
                                                    <input type="hidden" id="binder-det-id" name="binderDet"/>
                                                    <input type="hidden" id="cover-name">
                                                    <div id="covertypes-frm" class="form-control"
                                                         select2-url="<c:url value="/protected/uw/policies/riskCoverTypes"/>">

                                                    </div>
                                                </div>
                                            </div>
                                        </div>
                                        <div class="item form-group form-required">
                                            <div class="col-md-6 col-xs-12">
                                                <label for="risk-id" class="label-align col-md-5" id="risk-label">
                                                    Risk Identifier<span class="required">*</span></label>
                                                <div class="col-md-7 col-xs-12">
                                                    <input type="text" name="riskShtDesc" id="risk-id"
                                                           class="form-control"
                                                           placeholder="" required>
                                                </div>
                                            </div>
                                            <div class="col-md-6 col-xs-12">
                                                <label for="risk-desc" class="label-align col-md-5"
                                                       id="risk-description">
                                                    Risk Description<span class="required">*</span></label>
                                                <div class="col-md-7 col-xs-12 checkbox">
                                                    <input type="text" name="riskDesc" id="risk-desc"
                                                           class="form-control"
                                                           placeholder="" required>
                                                </div>
                                            </div>
                                        </div>
                                        <div class="item form-group form-required">
                                            <div class="col-md-6 col-xs-12 motor-disp" style="display: none;">
                                                <label for="overrid-prem" class="label-align col-md-5">
                                                    Negotiated Premium</label>
                                                <div class="col-md-7 col-xs-12 checkbox">
                                                    <input type="text" name="butchargePrem" id="overrid-prem"
                                                           class="form-control"
                                                           placeholder="Negotiated Premium">
                                                </div>
                                            </div>
                                            <div class="col-md-6 col-xs-12" style="opacity: 0;">
                                                <label for="comm-rate" class="label-align col-md-5">
                                                    Commission Rate<span class="required">*</span></label>
                                                <div class="col-md-7 col-xs-12">
                                                    <input type="number" name="commRate" id="comm-rate"
                                                           class="form-control"
                                                           placeholder="Comm Rate" required>
                                                </div>
                                            </div>
                                        </div>
                                    </form>
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
                                                        <button class="btn btn-primary btn btn-primary"
                                                                id="btn-add-new-section">New
                                                        </button>
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
                                    <div class="x_content" id="prem-rates-div">
                                        <div class="cutom-container">
                                            <table id="section_form_tbl" class="table" style="width:100%">
                                                <thead>
                                                <tr>
                                                    <th>Premium Item</th>
                                                    <th>Limit Amount</th>
                                                    <th>Rate</th>
                                                    <th>Div Factor</th>
                                                    <th>Free Limit</th>
                                                </tr>
                                                </thead>
                                            </table>
                                        </div>
                                    </div>
                                </div>
                                <div role="tabpanel" class="tab-pane fade"
                                     id="tab_content2" aria-labelledby="profile-tab">
                                    <button class="btn btn-success btn btn-primary" id="btn-add-new-tax">New</button>
                                    <div class="cutom-container">
                                        <table id="polTaxesList" class="table" style="width:100%">
                                            <thead>
                                            <tr>
                                                <th>Trans Code</th>
                                                <th>Rate</th>
                                                <th>Div Factor</th>
                                                <th>Rate Type</th>
                                                <th>Levy Amount</th>
                                                <th>Levy Level</th>
                                                <th width="5%"></th>
                                                <th width="5%"></th>
                                            </tr>
                                            </thead>
                                        </table>
                                    </div>
                                </div>
                                <div role="tabpanel" class="tab-pane fade"
                                     id="tab_content3" aria-labelledby="comm-rates-tab">
                                    <button class="btn btn-success btn btn-primary" id="btn-add-new-clause">New</button>
                                    <div class="cutom-container">
                                        <table id="polclausesList" class="table" style="width:100%">
                                            <thead>
                                            <tr>

                                                <th>Clause Heading</th>
                                                <th>Clause Type</th>
                                                <th>Editable?</th>
                                                <th>Clause Wording</th>
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
                <%--                    <div role="tabpanel" class="tab-pane fade"--%>
                <%--                         id="tab_content5" aria-labelledby="workflow-tab">--%>
                <%--                        <button class="btn btn-success btn btn-info" id="btn-show-task-history">History</button>--%>
                <%--                        <img id="proc-main-diagram" class="img-responsive img-rounded proc-diagram" src=""--%>
                <%--                             alt="Workflow Process Diagram">--%>
                <%--                    </div> <div role="tabpanel" class="tab-pane fade"--%>
                <%--                         id="tab_content5" aria-labelledby="workflow-tab">--%>
                <%--                        <button class="btn btn-success btn btn-info" id="btn-show-task-history">History</button>--%>
                <%--                        <img id="proc-main-diagram" class="img-responsive img-rounded proc-diagram" src=""--%>
                <%--                             alt="Workflow Process Diagram">--%>
                <%--                    </div>--%>
            </div>
        </div>
        <div class="float-right">
            <input action="action" type="button" onclick="history.go(-1);" class="btn btn-primary float-right"
                   value="Back"
                   id="renbtn" style="display:none"/>

            <div class="quot-panel">

                <sec:authorize access="hasAnyAuthority('ACCESS_QUOT_REPORTS')">
                    <button type="button" class="btn btn-primary float-right"
                            id="btn-quot-reports" data-toggle="modal" data-target="#reportsModal"
                            style="display:none">
                        <i class="fa fa-print"></i>
                    </button>
                </sec:authorize>
<%--                <input type="button" class="btn btn-primary float-right"--%>
<%--                       value="Assign" id="btn-assign-trans">--%>
                <sec:authorize access="hasAnyAuthority('SAVE_QUOTE')">
                    <input type="button" class="btn btn-primary float-right"
                           value="Save" id="btn-add-quote"
                           style="display:none">
                </sec:authorize>
                <sec:authorize access="hasAnyAuthority('MAKE_READY_QUOTE')">
                    <input type="button" class="btn btn-primary float-right"
                           value="Submit" id="btn-make-ready-policy"
                           style="display:none">
                </sec:authorize>

                <sec:authorize access="hasAnyAuthority('AUTHORIZE_QUOTE')">
                    <input type="button" class="btn btn-primary float-right"
                           value="Approve Quote" id="btn-auth-quote">
                </sec:authorize>

                <sec:authorize access="hasAnyAuthority('CONFIRM_QUOTE')">
                    <input type="button" class="btn btn-primary float-right"
                           value="Confirm Quote" id="btn-confirm-quote"
                           style="display:none">
                </sec:authorize>
                <sec:authorize access="hasAnyAuthority('CONVERT_QUOT_PRODUCT')">
                    <input type="button" class="btn btn-primary float-right"
                           value="Convert" id="btn-convert-quote"
                           style="display:none">
                </sec:authorize>

            </div>
        </div>
    </div>
</div>

<script>
    // Convert the value of the Risk Identifier to uppercase
    $('#risk-id').on('input', function () {
        $(this).val(function (_, val) {
            return val.toUpperCase();
        });
    });
</script>
<jsp:include page="modals/riskmodals.jsp"></jsp:include>
<jsp:include page="modals/comparequote.jsp"></jsp:include>
<jsp:include page="../modals/prospectmodals.jsp"></jsp:include>
<jsp:include page="modals/convertquot.jsp"></jsp:include>