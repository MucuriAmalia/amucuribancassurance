<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="sec" uri="http://www.springframework.org/security/tags" %>

<script type="text/javascript" src="<c:url value="/js/modules/utils/select2builder.js"/>"></script>
<script type="text/javascript" src="<c:url value="/libs/rivets/rivets.js"/>"></script>
<script type="text/javascript" src="<c:url value="/js/modules/uwtrans/PrintCerts.js"/>"></script>
<script type="text/javascript" src="<c:url value="/js/modules/uwtrans/uwtrans.js"/>"></script>
<script type="text/javascript" src="<c:url value="/libs/js/survey.jquery.js"/>"></script>
<script type="text/javascript">
    var polCode = ${policyId};
    var polStatus = ${policyStatus}+"";
    var userObjAcctId = '${sessionScope.SPRING_SECURITY_CONTEXT.authentication.principal.accountDef.acctId}';
    var userObjAcctName = '${sessionScope.SPRING_SECURITY_CONTEXT.authentication.principal.accountDef.name}';
</script>
<script type="text/javascript">
    var requestContextPath = '${pageContext.request.contextPath}';
</script>
<div class="x_panel">
    <%--    <c:choose>--%>
    <%--    <c:when test="${policyId != -2000}">--%>

    <%--        <a href="<c:url value="/protected/uw/policies/masterenq?polId=${policyId}"/>" id="backMaster" class="btn btn-default float-right">360&#xb0; View</a>--%>

    <%--    </c:when>--%>
    <%--        <c:when test="${policyId == -2000}">--%>
    <%--            <a href="<c:url value="/protected/uw/policies/masterenq?polId=${policyId}"/>" id="backMaster" class="btn btn-default float-right" style="pointer-events: none">360&#xb0; View</a>--%>

    <%--        </c:when>--%>
    <%--    </c:choose>--%>
    <div class="row no-print">
        <div class="col-xs-12">
            <input type="button" class="btn btn-info float-left"
                   value="Dispatch" id="btn-dispatch-trans">
            <input type="button" class="btn btn-info float-left"
                   value="Assign" id="btn-assign-trans">
            <sec:authorize access="hasAnyAuthority('AUTHORIZE_POLICY')">
                <input type="button" class="btn btn-info float-left"
                       value="Authorise" id="btn-auth-policy">
            </sec:authorize>
            <sec:authorize access="hasAnyAuthority('SAVE_POLICY')">
                <input type="button" class="btn btn-info float-left"
                       value="Save" id="btn-add-policy"
                       style="display:none">
            </sec:authorize>
            <sec:authorize access="hasAnyAuthority('MAKE_POLICY_READY')">
                <input type="button" class="btn btn-info float-left"
                       value="Submit" id="btn-make-ready-policy"
                       style="display:none">
            </sec:authorize>


            <sec:authorize access="hasAnyAuthority('MAKE_POLICY_READY')">
                <input type="button" class="btn btn-info float-left"
                       value="Unsubmit" id="btn-undo-make-ready"
                       style="display:none">
            </sec:authorize>
            <sec:authorize access="hasAnyAuthority('ACCESS_POLICY_REPORTS')">
                <button type="button" class="btn btn-info float-right"
                        id="btn-uw-reports" data-toggle="modal" data-target="#reportsModal"
                        style="display:none">
                    <i class="fa fa-print"></i>
                </button>

            </sec:authorize>
        </div>
    </div>
    <hr>
    <form id="policy-form" class="form-horizontal form-label-left">
        <input action="action" type="button" onclick="history.go(-1);" class="btn btn-info float-right" value="Back"
               id="renbtn" style="display:none"/>
</div>
<input type="hidden" id="policy-id" name="policyId"/>
<input type="hidden" id="pol-trans-type" name="transType"/>
<input type="hidden" id="pol-rev-trans-type" name="polRevStatus"/>
<input type="hidden" id="pol-prev-policy" name="prevPolicy"/>
<input type="hidden" id="pol-reuse-contra-policy" name="reusecontraPolicy"/>
<input type="hidden" id="hasrefund"/>
<input type="hidden" id="refund-amount1"/>
<div class="col-md-6 col-xs-12">
    <div class="item form-group form-required">
        <label for="client-frm" class="label-align col-md-5">
            Client <span class="required">*</span></label>
        <div id="client-input" class="col-md-7 col-xs-9">
            <div class="edit-client">
                <input type="hidden" id="client-id" name="clientId"/>
                <input type="hidden" id="client-f-name">
                <input type="hidden" id="client-other-name">

                <div id="client-frm" class="form-control"
                     select2-url="<c:url value="/protected/uw/policies/uwClients"/>">
                </div>
            </div>
        </div>
        <%--                <div id ="new-client" class="col-md-1 col-xs-3">--%>
        <%--                    &lt;%&ndash;<div id="display-client">&ndash;%&gt;--%>
        <%--                    &lt;%&ndash;<p class="form-control-static" id="client-info"></p>&ndash;%&gt;--%>
        <%--                    &lt;%&ndash;</div>&ndash;%&gt;--%>
        <%--                    <div class="edit-client">--%>
        <%--                        <a href="<c:url value="/protected/clients/setups/clientsform?type=pol"/>"--%>
        <%--                           id="btn-add-client"--%>
        <%--                           class="btn btn-sm btn-info">New</a>--%>
        <%--                    </div>--%>
        <%--                </div>--%>
    </div>
</div>
<div class="col-md-6 col-xs-12">
    <div class="item form-group form-required">
        <label for="pol-buss-type" class="label-align col-md-5">
            Business Type<span class="required">*</span></label>
        <div class="col-md-7 col-xs-12">
            <select class="form-control" id="pol-buss-type" name="businessType"
                    required>
                <option value="">Select Business Type</option>
                <option value="N">Annual</option>
                <option value="S">Short Period</option>
            </select>
        </div>
    </div>
</div>
<div class="col-md-6 col-xs-12 bind-info">
    <div class="item form-group form-required ">
        <label for="binder-frm" class="label-align col-md-5">
            Contract<span class="required">*</span></label>
        <div class="col-md-7 col-xs-12">
            <div id="edit-binder">
                <input type="hidden" id="binder-id" name="bindCode"/>
                <input type="hidden" id="product-id" name="prodId"/>
                <input type="hidden" id="pol-agent-id" name="agentId"/>
                <input type="hidden" id="pol-bind-age-appli"/>
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
</div>

<%--        <div class="col-md-6 col-xs-12 multi-product">--%>
<%--            <div class="item form-group form-required">--%>
<%--                <label for="product-frm" class="label-align col-md-5 ">--%>
<%--                    Product</label>--%>
<%--                <div class="col-md-7 col-xs-12">--%>
<%--                    <input type="hidden" id="bind-product-desc">--%>
<%--                    <div id="product-frm" class="form-control"--%>
<%--                         select2-url="<c:url value="/protected/uw/policies/uwMultiProducts"/>">--%>
<%--                    </div>--%>
<%--                </div>--%>

<%--            </div>--%>
<%--            &lt;%&ndash;<div class="item form-group">&ndash;%&gt;--%>
<%--            &lt;%&ndash;<div class="col-md-6 col-xs-12">&ndash;%&gt;--%>
<%--            &lt;%&ndash;</div>&ndash;%&gt;--%>
<%--            &lt;%&ndash;<div class="col-md-6 col-xs-12">&ndash;%&gt;--%>
<%--            &lt;%&ndash;</div>&ndash;%&gt;--%>

<%--        </div>--%>


<%--<div class="col-md-6 col-xs-12">--%>

<%--</div>--%>

<div class="col-md-6 col-xs-12  bind-insurance">
    <div class="item form-group form-required ">
        <label for="pol-prod-name" class="label-align col-md-5">
            Product</label>
        <div class="col-md-7 col-xs-12">
            <p class="form-control-static" id="pol-prod-name"></p>
        </div>
    </div>
</div>
<div class="col-md-6 col-xs-12 bind-insurance">
    <div class="item form-group form-required ">
        <label for="pol-ins-comp" class="label-align col-md-5">
            Intermediary</label>
        <div class="col-md-7 col-xs-12">
            <p class="form-control-static" id="pol-ins-comp"></p>
        </div>
    </div>

</div>
<div class="col-md-6 col-xs-12">
    <div class="item form-group form-required">
        <label for="pol-interface-type" class="label-align col-md-5">
            Policy Type<span class="required">*</span></label>
        <div class="col-md-7 col-xs-12">
            <select class="form-control" id="pol-interface-type" name="interfaceType"
                    required>
                <option value="">Select Policy Type</option>
                <option value="A">Accrual</option>
                <option value="C">Cash</option>
            </select>
        </div>
    </div>
</div>
<div class="col-md-6 col-xs-12  form-required">
    <div class="item form-group form-required">
        <label for="brn-id" class="col-md-5 label-align">Date
            From</label>

        <div class="col-md-7 col-xs-12">
            <div class='input-group date datepicker-input' id="wef-date">
                <input type='text' class="form-control float-right" name="wefDate"
                       id="from-date" required/>
                <div class="input-group-addon">
                    <span class="fa fa-calendar"></span>
                </div>
            </div>
        </div>
    </div>
</div>
<div class="col-md-6 col-xs-12">
    <div class="item form-group form-required">
        <label for="noOfUnits" class="label-align col-md-5">Date
            To</label>
        <div class="col-md-7 col-xs-12">
            <div class='input-group date datepicker-input' id="cover-to-date">
                <input type='text' class="form-control float-right" name="wetDate"
                       id="wet-date" required/>
                <div class="input-group-addon">
                    <span class="fa fa-calendar"></span>
                </div>
            </div>
        </div>

    </div>


</div>

<div class="col-md-6 col-xs-12">
    <div class="item form-group form-required">
        <label for="edit-branch" class="label-align col-md-5">
            Branch<span class="required">*</span></label>
        <div class="col-md-7 col-xs-12">
            <div id="edit-branch">
                <input type="hidden" id="brn-id" name="branchId"/>
                <input type="hidden" id="brn-name">
                <div id="brn-frm" class="form-control"
                     select2-url="<c:url value="/protected/uw/policies/uwbranches"/>">

                </div>
            </div>
            <div id="display-branch">
                <p class="form-control-static" id="branch-info"></p>
            </div>
        </div>
    </div>
</div>
<div class="col-md-6 col-xs-12">
    <div class="item form-group form-required">
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
</div>

<div id="other-pol-details">
    <div class="item form-group form-required">
        <div class="col-md-6 col-xs-12">
            <label for="pol-no" class="label-align col-md-5">
                Policy No</label>
            <div class="col-md-7 col-xs-12">
                <input type="hidden" id="div-pol-no" name="polNo"/>
                <p class="form-control-static" id="pol-no"></p>
            </div>
        </div>
        <div class="col-md-6 col-xs-12">
            <label for="pol-rev-no" class="label-align col-md-5">
                Endorsement Number</label>
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
                Basic Premium</label>
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
    <%--                <div class="item form-group form-required">--%>
    <%--                    <div class="col-md-6 col-xs-12">--%>
    <%--                        <label for="pol-tl" class="label-align col-md-5">--%>
    <%--                            Training Levy</label>--%>
    <%--                        <div class="col-md-7 col-xs-12">--%>
    <%--                            <p class="form-control-static" id="pol-tl"></p>--%>
    <%--                        </div>--%>
    <%--                    </div>--%>
    <%--                    <div class="col-md-6 col-xs-12">--%>
    <%--                        <label for="pol-phcf" class="label-align col-md-5">--%>
    <%--                            PHCF Fund</label>--%>
    <%--                        <div class="col-md-7 col-xs-12">--%>
    <%--                            <p class="form-control-static" id="pol-phcf"></p>--%>
    <%--                        </div>--%>
    <%--                    </div>--%>
    <%--                </div>--%>
    <%--                <div class="item form-group form-required">--%>
    <%--                    <div class="col-md-6 col-xs-12">--%>
    <%--                        <label for="pol-sd" class="label-align col-md-5">--%>
    <%--                            Stamp Duty</label>--%>
    <%--                        <div class="col-md-7 col-xs-12">--%>
    <%--                            <p class="form-control-static" id="pol-sd"></p>--%>
    <%--                        </div>--%>
    <%--                    </div>--%>
    <%--                    <div class="col-md-6 col-xs-12">--%>
    <%--                        <label for="pol-whtx" class="label-align col-md-5">--%>
    <%--                            WHTX Tax</label>--%>
    <%--                        <div class="col-md-7 col-xs-12">--%>
    <%--                            <p class="form-control-static" id="pol-whtx"></p>--%>
    <%--                        </div>--%>
    <%--                    </div>--%>
    <%--                </div>--%>
    <div class="item form-group form-required">
        <div class="col-md-6 col-xs-12">
            <label for="pol-phcf" class="label-align col-md-5">
                PHF Fund</label>
            <div class="col-md-7 col-xs-12">
                <p class="form-control-static" id="pol-phcf"></p>
            </div>
        </div>
        <div class="col-md-6 col-xs-12">
            <label for="pol-tran-type-disp" class="label-align col-md-5">
                Transaction Type</label>
            <div class="col-md-7 col-xs-12">
                <p class="form-control-static" id="pol-tran-type-disp"></p>
            </div>
        </div>
    </div>
    <div class="item form-group form-required">
        <div class="col-md-6 col-xs-12">
            <label for="pol-comm-amt" class="label-align col-md-5">
                Commission Amt</label>
            <div class="col-md-7 col-xs-12">
                <p class="form-control-static" id="pol-comm-amt"></p>
            </div>
        </div>
        <div class="col-md-6 col-xs-12">
            <label for="pol-ren-date" class="label-align col-md-5">
                Renewal Date</label>
            <div class="col-md-7 col-xs-12">
                <p class="form-control-static" id="pol-ren-date"></p>
            </div>
        </div>
    </div>
    <div class="item form-group form-required">
        <div class="col-md-6 col-xs-12">
            <label for="pol-status" class="label-align col-md-5">
                Policy Status</label>
            <div class="col-md-7 col-xs-12">
                <p class="form-control-static" id="pol-status"></p>
            </div>
        </div>
        <div class="col-md-6 col-xs-12">
            <label for="pol-fap" class="label-align col-md-5">
                Future Annual Premium</label>
            <div class="col-md-7 col-xs-12">
                <p class="form-control-static" id="pol-fap"></p>
            </div>
        </div>
    </div>
    <div class="item form-group form-required">
        <div class="col-md-6 col-xs-12">
            <label for="pol-sub-agent-comm" class="label-align col-md-5">
                Sub Agent Commission</label>
            <div class="col-md-7 col-xs-12">
                <p class="form-control-static" id="pol-sub-agent-comm"></p>
            </div>
        </div>
        <div class="col-md-6 col-xs-12">
            <label for="pol-endos-gross-premium" class="label-align col-md-5">
                Installment Total Policy Premium</label>
            <div class="col-md-7 col-xs-12">
                <p class="form-control-static" id="pol-endos-gross-premium"></p>
            </div>
        </div>
    </div>
    <div class="item form-group form-required">

        <div class="col-md-6 col-xs-12">
            <label for="pol-paid-premium" class="label-align col-md-5">
                Paid Premium</label>
            <div class="col-md-7 col-xs-12">
                <p class="form-control-static" id="pol-paid-premium"></p>
            </div>
        </div>
        <div class="col-md-6 col-xs-12">
            <label for="pol-refundable-amount" class="label-align col-md-5">
                Refundable Premium</label>
            <div class="col-md-7 col-xs-12">
                <p class="form-control-static" id="pol-refundable-amount"></p>
            </div>
        </div>
    </div>
</div>
<div class="col-md-6 col-xs-12 chkimport-risks">
    <div class="item form-group form-required">
        <label for="rate-taxable" class="col-md-5 label-align import-risks">Import Risks</label>
        <div class="col-md-7 checkbox import-risks">
            <label>
                <input type="checkbox" id="chk-import-risks" name="importRisks">
            </label>
        </div>
    </div>
</div>
</form>
</div>
<div class="x_panel multi-product-uw">
    <div class="x_title">
        <h4>Policy Binders</h4>
        <ul class="nav navbar-right panel_toolbox">
            <li><a class="collapse-link"><i class="fa fa-chevron-up"></i></a>
            </li>
        </ul>
    </div>
    <div class="x_content">
        <div class="card-box table-responsive">
            <input type="hidden" id="pol-binder-pk">
            <table id="policy-binders-tbl" class="table table-striped" style="width:100%">
                <thead>
                <tr>
                    <th>Intermediary</th>
                    <th>Binder ID</th>
                    <th>Binder Desc</th>
                    <th>Basic Prem</th>
                    <th width="5%"></th>
                    <th width="5%"></th>
                </tr>
                </thead>
            </table>
        </div>
    </div>
</div>

<div class="x_panel risk-detail-tab">
    <div class="x_title">
        <h4>Coverage Details</h4>
        <ul class="nav navbar-right panel_toolbox">
            <li><a class="collapse-link"><i class="fa fa-chevron-up"></i></a>
            </li>
        </ul>
    </div>
    <div class="x_content">

        <ul id="myTab" class="nav nav-tabs bar_tabs" role="tablist">
            <li role="presentation" class="active"><a href="#tab_content1"
                                                      id="home-tab" role="tab" data-toggle="tab"
                                                      aria-expanded="true">Coverage Details</a>
            </li>
            <li role="presentation" class="" id="show-taxes"><a href="#tab_content2"
                                                                role="tab" id="profile-tab" data-toggle="tab"
                                                                aria-expanded="false">Taxes</a>
            </li>
            <%--                <li role="presentation" class=""><a href="#tab_content29" role="tab"--%>
            <%--                                                    id="questionnaire-tabb" data-toggle="tab"--%>
            <%--                                                    aria-controls="profile"--%>
            <%--                                                    aria-expanded="false">Questionnaire</a>--%>
            <%--                </li>--%>
            <li role="presentation" class="" id="show-clauses"><a href="#tab_content3"
                                                                  role="tab" id="comm-rates-tab" data-toggle="tab"
                                                                  aria-expanded="false">Clauses</a>
            </li>
            <li role="presentation" class="" id="end-remarks"><a href="#tab_content4"
                                                                 role="tab" id="end-remarks-tab" data-toggle="tab"
                                                                 aria-expanded="false">Remarks</a>
            </li>

            <li role="presentation" class="" id="checks-tab"><a href="#tab_pol_checks"
                                                                role="tab" id="pol-checks-tab" data-toggle="tab"
                                                                aria-expanded="false">Checks</a>
            </li>
            <li role="presentation" class="" id="cert-tab"><a href="#tab_pol_certs"
                                                              role="tab" id="pol-cets-tab"
                                                              data-toggle="tab"
                                                              aria-expanded="false">
                Certificates</a>
            </li>
            <li role="presentation" class="" id="receipts-tab"><a href="#tab_pol_receipts"
                                                                  role="tab" id="pol-rec-tab" data-toggle="tab"
                                                                  aria-expanded="false">Receipts</a>
            </li>
            <li role="presentation" class="" id="workflow"><a href="#tab_content5"
                                                              role="tab" id="workflow-tab" data-toggle="tab"
                                                              aria-expanded="false">Process Diagram</a>
            </li>
        </ul>


        <div id="myTabContent" class="tab-content">
            <div role="tabpanel" class="tab-pane active"
                 id="tab_content1" aria-labelledby="home-tab">
                <input type="hidden" id="risk-bind-code"/>
                <input type="hidden" id="risk-det-id-pk"/>
                <button class="btn btn-success btn btn-info" id="btn-endors-risk" style="display:none">Endorse
                </button>
                <button class="btn btn-success btn btn-info" id="btn-add-risk">New</button>
                <button class="btn btn-success btn btn-info" id="btn-save-risk">Save</button>
                <button class="btn btn-info btn btn-info" id="btn-save-cancel">Cancel</button>
                <button class="btn btn-info btn btn-info" id="btn-import-risk">Import Risk</button>
                <button class="btn btn-info btn btn-info" id="btn-import-logs" style="display:none">Logs</button>
                <div id="risk-div">

                    <table id="risk_tbl" class="table table-striped" style="width:100%">
                        <thead>
                        <tr>
                            <th class="motor-disp">Plate Number</th>
                            <th class="non-motor-disp">Risk ID</th>
                            <th class="motor-disp">Model</th>
                            <th class="non-motor-disp">Risk Desc</th>
                            <th>WEF</th>
                            <th>WET</th>
                            <th>Binder</th>
                            <th>Sub Class</th>
                            <th>Cover Type</th>
                            <th>Sum Insured</th>
                            <th>Premium</th>
                            <th width="5%"></th>
                            <th width="5%"></th>
                        </tr>
                        </thead>
                    </table>
                </div>
                <form id="risk-form" class="form-horizontal form-label-left">
                    <input type="hidden" id="risk-code-pk" name="riskId"/>
                    <input type="hidden" id="risk-binder-code-pk" name="polBindCode"/>
                    <input type="hidden" id="risk-trans-type" name="transType"/>
                    <input type="hidden" id="risk-ident-code" name="riskIdentifier"/>
                    <div class="item form-group form-required">
                        <div class="col-md-6 col-xs-12">
                            <label for="insured-frm" class="label-align col-md-5">
                                Insured<span class="required">*</span></label>
                            <div class="col-md-7 col-xs-12">
                                <input type="hidden" id="insured-code" name="insuredCode"/>
                                <input type="hidden" id="insured-name">
                                <input type="hidden" name="riskIdentifier"/>
                                <input type="hidden" id="risk-binder-code" name="bindCode"/>
                                <input type="hidden" id="insured-other-name">
                                <div id="insured-frm" class="form-control"
                                     select2-url="<c:url value="/protected/uw/policies/uwClients"/>">

                                </div>
                            </div>
                        </div>
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
                    </div>
                    <div class="item form-group form-required">

                        <div class="col-md-6 col-xs-12">
                            <label for="covertypes-frm" class="label-align col-md-5">
                                Coverage<span class="required">*</span></label>
                            <div class="col-md-7 col-xs-12">
                                <input type="hidden" id="risk-cov-code" name="coverCode"/>
                                <input type="hidden" id="binder-det-id" name="binderDet"/>
                                <input type="hidden" id="cover-name">
                                <div id="covertypes-frm" class="form-control"
                                     select2-url="<c:url value="/protected/uw/policies/riskCoverTypes"/>">

                                </div>
                            </div>
                        </div>
                        <div class="col-md-6 col-xs-12">
                            <label for="overrid-prem" class="label-align col-md-5">
                                Negotiated Premium</label>
                            <div class="col-md-7 col-xs-12 checkbox">
                                <input type="text" name="butchargePrem" id="overrid-prem" class="form-control"
                                       placeholder="Negotiated Premium">
                            </div>
                        </div>
                    </div>
                    <div class="item form-group form-required">
                        <div class="col-md-6 col-xs-12">
                            <label for="risk-id" class="label-align col-md-5" id="risk-label">
                                Risk Identifier<span class="required">*</span></label>
                            <div class="col-md-7 col-xs-12">
                                <input type="text" name="riskShtDesc" id="risk-id" class="form-control"
                                       required>
                            </div>
                        </div>
                        <div class="col-md-6 col-xs-12">
                            <label for="risk-desc" class="label-align col-md-5" id="risk-description">
                                Risk Details<span class="required">*</span></label>
                            <div class="col-md-7 col-xs-12 checkbox">
                                <input type="text" name="riskDesc" id="risk-desc" class="form-control"
                                       required>
                            </div>
                        </div>
                    </div>
                    <div class="item form-group">
                        <div class="col-md-6 col-xs-12  form-required">
                            <label for="brn-id" class="col-md-5 label-align">Date
                                From</label>

                            <div class="col-md-7 col-xs-12">
                                <div class='input-group date datepicker-input' id="risk-cover-from">
                                    <input type='text' class="form-control float-right" name="wefDate"
                                           id="risk-wef-date" required/>
                                    <div class="input-group-addon">
                                        <span class="fa fa-calendar"></span>
                                    </div>
                                </div>
                            </div>
                        </div>
                        <div class="col-md-6 col-xs-12">
                            <label for="noOfUnits" class="label-align col-md-5">Date
                                To</label>
                            <div class="col-md-7 col-xs-12">
                                <div class='input-group date datepicker-input' id="risk-cover-to">
                                    <input type='text' class="form-control float-right" name="wetDate"
                                           id="risk-wet-date" required/>
                                    <div class="input-group-addon">
                                        <span class="fa fa-calendar"></span>
                                    </div>
                                </div>
                            </div>

                        </div>


                    </div>
                    <div class="item form-group">
                        <div class="col-md-6 col-xs-12">
                            <label for="comm-rate" class="label-align col-md-5">
                                Commission Rate<span class="required">*</span></label>
                            <div class="col-md-7 col-xs-12">
                                <input type="number" name="commRate" id="comm-rate" class="form-control"
                                       placeholder="Comm Rate" required>
                            </div>
                        </div>
                        <div class="col-md-6 col-xs-12 motor-disp installment">
                            <label for="install-perc" class="label-align col-md-5">
                                Installment Percentage</label>
                            <div class="col-md-7 col-xs-12 ">
                                <input type="text" name="installmentPerc" id="install-perc" class="form-control"
                                       placeholder="Installment Percentage" readonly>
                            </div>
                        </div>
                    </div>
                    <div class="item form-group installment">
                        <div class="col-md-6 col-xs-12 motor-disp">
                            <label for="install-amt" class="col-md-5 label-align">Installment Amount</label>

                            <div class="col-md-7">
                                <input type="text" class="editUserCntrls form-control"
                                       id="install-amt" name="installAmount"
                                >
                            </div>
                        </div>
                        <div class="col-md-6 col-xs-12">
                            <label for="rate-taxable" class="col-md-5 label-align">Installment No</label>
                            <div class="col-md-7 col-xs-12 checkbox">
                                <input type="text" name="installmentNo" id="install-no" class="form-control"
                                       placeholder="Installment No" readonly>
                            </div>
                        </div>

                    </div>
                </form>


                <div class="x_panel">
                    <div class="x_title">
                        <h4>Premium Items, Certificates and Schedules</h4>
                        <ul class="nav navbar-right panel_toolbox">
                            <li><a class="collapse-link"><i class="fa fa-chevron-up"></i></a>
                            </li>
                        </ul>
                    </div>
                    <div class="x_content">


                        <div id="sect-div">
                            <div class="" role="tabpanel" data-example-id="togglable-tabs">
                                <ul id="myTab1" class="nav nav-tabs bar_tabs" role="tablist">
                                    <li role="presentation" class="active"><a href="#tab_content11" id="home-tabb"
                                                                              role="tab" data-toggle="tab"
                                                                              aria-controls="home"
                                                                              aria-expanded="true">Premium Items</a>
                                    </li>
                                    <li role="presentation" class=""><a href="#tab_content22" role="tab"
                                                                        id="profile-tabb"
                                                                        data-toggle="tab"
                                                                        aria-controls="profile"
                                                                        aria-expanded="false">Certificates</a>
                                    </li>
                                    <li role="presentation" class=""><a href="#tab_content23" role="tab"
                                                                        id="schedules-tabb" data-toggle="tab"
                                                                        aria-controls="profile"
                                                                        aria-expanded="false">Vehicle Details</a>
                                    </li>
                                    <li role="presentation" class=""><a href="#tab_content24" role="tab"
                                                                        id="docs-tabb" data-toggle="tab"
                                                                        aria-controls="profile"
                                                                        aria-expanded="false">Risk Documents</a>
                                    </li>
                                    <li role="presentation" class=""><a href="#tab_content26" role="tab"
                                                                        id="insured-docs-tabb" data-toggle="tab"
                                                                        aria-controls="profile"
                                                                        aria-expanded="false">Insured Documents</a>
                                    </li>
                                    <li role="presentation" class=""><a href="#tab_content25" role="tab"
                                                                        id="int-parties-tabb" data-toggle="tab"
                                                                        aria-controls="profile"
                                                                        aria-expanded="false">Interested Parties</a>
                                    </li>
                                    <li role="presentation" class="wiba-disp"><a href="#tab_content30" role="tab"
                                                                                 id="int-beneficiaries-tabb"
                                                                                 data-toggle="tab"
                                                                                 aria-controls="profile"
                                                                                 aria-expanded="false">Beneficiaries</a>
                                    </li>
                                </ul>
                                <div id="myTabContent2" class="tab-content">
                                    <div role="tabpanel" class="tab-pane active" id="tab_content11"
                                         aria-labelledby="home-tab">
                                        <button class="btn btn-success btn btn-info" id="btn-add-new-section">New
                                        </button>
                                        <input type="hidden" id="insured-client-age">
                                        <div class="card-box table-responsive">
                                            <table id="section_tbl" class="table table-striped" style="width:100%">
                                                <thead>
                                                <tr>
                                                    <th>Premium Item</th>
                                                    <th>Value</th>
                                                    <th>Rate</th>
                                                    <th>Calc Prem</th>
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
                                    <div role="tabpanel" class="tab-pane fade"
                                         id="tab_content26" aria-labelledby="profile-tab">
                                        <div class="card-box table-responsive">
                                            <table id="clientDocsList" class="table table-striped" style="width:100%">
                                                <thead>
                                                <tr>
                                                    <th>Document ID</th>
                                                    <th>Document Desc</th>
                                                    <th>File Name</th>
                                                    <th>File Verifier</th>
                                                    <th width="5%"></th>
                                                </tr>
                                                </thead>
                                            </table>
                                        </div>
                                    </div>

                                    <div role="tabpanel" class="tab-pane fade" id="tab_content23"
                                         aria-labelledby="profile-tab">
                                        <button class="btn btn-success btn btn-info" id="btn-add-new-sched">New
                                        </button>
                                        <button class="btn btn-success btn btn-info" id="btn-add-edit-sched">Edit
                                        </button>
                                        <button class="btn btn-success btn btn-info" id="btn-add-del-sched">Delete
                                        </button>
                                        <%--                                            <iframe id="downloadFrame" style="display:none"></iframe>--%>
                                        <%--                                            <table id="risk-sched_tbl" class="table" style="width:100%">--%>

                                        <%--                                            </table>--%>
                                        <table id="schedule_details_tbl"
                                               class="table table-striped" style="width:100%">
                                            <thead>
                                            <tr>
                                                <th>Color</th>
                                                <th>Body Type</th>
                                                <th>Make</th>
                                                <th>Model</th>
                                                <th>Carry Capacity</th>
                                                <th>Chassis No</th>
                                                <th>CC</th>
                                                <th>Engine No</th>
                                                <th>Log Book.</th>
                                                <th>YOM</th>
                                            </tr>
                                            </thead>
                                        </table>
                                    </div>
                                    <div role="tabpanel" class="tab-pane fade" id="tab_content25"
                                         aria-labelledby="home-tab">
                                        <button class="btn btn-success btn btn-info" id="btn-add-new-ips">New
                                        </button>
                                        <div class="card-box table-responsive">
                                            <table id="interested_parties_tbl"
                                                   class="table table-striped" style="width:100%">
                                                <thead>
                                                <tr>
                                                    <th>Name</th>
                                                    <th>Type</th>
                                                    <th>Pin</th>
                                                    <th>ID No/Reg No</th>
                                                    <th width="5%"></th>
                                                </tr>
                                                </thead>
                                            </table>
                                        </div>
                                    </div>
                                    <div role="tabpanel" class="tab-pane fade" id="tab_content24"
                                         aria-labelledby="profile-tab">
                                        <button class="btn btn-success btn btn-info" id="btn-add-docs">New</button>
                                        <table id="risk_docs_tbl" class="table table-striped" style="width:100%">
                                            <thead>
                                            <tr>
                                                <th>Revision No</th>
                                                <th>Document ID</th>
                                                <th>Document Desc</th>
                                                <th>File Name</th>
                                                <th>File Verifier</th>
                                                <th width="5%"></th>
                                                <th width="5%"></th>
                                                <th width="5%"></th>
                                            </tr>
                                            </thead>
                                        </table>

                                    </div>
                                    <div role="tabpanel" class="tab-pane fade" id="tab_content22"
                                         aria-labelledby="profile-tab">
                                        <button class="btn btn-success btn btn-info" id="btn-add-new-cert">New
                                        </button>
                                        <div class="card-box table-responsive">
                                            <table id="cert_tbl" class="table table-striped" style="width:100%">
                                                <thead>
                                                <tr>
                                                    <th>Cert Type</th>
                                                    <th>Status</th>
                                                    <th>Print Status</th>
                                                    <th>WEF</th>
                                                    <th>WET</th>
                                                    <th>Cert No</th>
                                                    <th>Reason cancelled</th>
                                                    <%--<th width="5%"></th>--%>
                                                    <%--<th width="5%"></th>--%>
                                                    <th width="5%"></th>
                                                    <th width="5%"></th>
                                                </tr>
                                                </thead>
                                            </table>
                                        </div>

                                    </div>
                                    <div role="tabpanel" class="tab-pane fade" id="tab_content30"
                                         aria-labelledby="profile-tab">
                                        <button class="btn btn-success btn btn-info" id="btn-add-beneficiary">New
                                        </button>
                                        <form id="benefits-upload-form" class="form-horizontal"
                                              enctype="multipart/form-data">
                                            <input type="hidden" class="form-control" id="ben-import-risk-id"
                                                   name="riskId">
                                            <div class="col-md-10 col-xs-12 form-required">

                                                <div class="col-md-5 col-xs-12">
                                                    <div class="input-group col-xs-12">
                                                        <input name="file" type="file" id="avatar" required>
                                                    </div>
                                                </div>
                                                <div class="col-md-3 col-xs-12">
                                                    <input type="submit" class="btn btn-success btn-sm float-left"
                                                           style="margin-right: 10px;" value="Upload Beneficiaries">
                                                </div>
                                            </div>
                                        </form>
                                        <table id="risk_beneficiaries_tbl" class="table table-striped"
                                               style="width:100%">
                                            <thead>
                                            <tr>
                                                <th>Name</th>
                                                <th>Occupation</th>
                                                <th>Salary</th>
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
                    <div class="x_content" id="prem-rates-div">
                        <div class="card-box table-responsive">
                            <table id="section_form_tbl" class="table table-striped" style="width:100%">
                                <thead>
                                <tr>
                                    <th>Premium Item</th>
                                    <th>Value</th>
                                    <th>Rate</th>
                                    <th>Div Factor</th>
                                    <th>Free Limit</th>
                                    <th></th>
                                    <th></th>
                                </tr>
                                </thead>
                            </table>
                        </div>
                    </div>
                </div>
            </div>
            <div role="tabpanel" class="tab-pane fade"
                 id="tab_content2" aria-labelledby="profile-tab">
                <button class="btn btn-success btn btn-info" id="btn-add-new-tax">New</button>
                <div class="card-box table-responsive">
                    <table id="polTaxesList" class="table table-striped" style="width:100%">
                        <thead>
                        <tr>
                            <th>Trans Code</th>
                            <th>Tax Rate</th>
                            <th>Div Factor</th>
                            <th>Rate Type</th>
                            <th>Tax Amount</th>
                            <th>Tax Level</th>
                            <th width="5%"></th>
                            <th width="5%"></th>
                        </tr>
                        </thead>
                    </table>
                </div>
            </div>
            <%--                <div role="tabpanel" class="tab-pane fade" id="tab_content29"--%>
            <%--                     aria-labelledby="home-tab">--%>
            <%--                    <button class="btn btn-success btn btn-info" id="btn-questionnaire">Fill Questionnaire--%>
            <%--                    </button>--%>
            <%--                    <button class="btn btn-danger btn btn-info" id="btn-del-questionnaire">Remove Questionnaire--%>
            <%--                    </button>--%>

            <%--                    <button class="btn btn-success btn btn-info" id="btn-edit-questionnaire">Edit Questionnaire--%>
            <%--                    </button>--%>
            <%--                    <div class="card-box table-responsive">--%>
            <%--                        <table id="questionnaire_tbl" class="table" style="width:100%">--%>
            <%--                            <thead>--%>
            <%--                            <tr>--%>
            <%--                                <th>Question</th>--%>
            <%--                                <th>Answer</th>--%>
            <%--                            </tr>--%>
            <%--                            </thead>--%>
            <%--                        </table>--%>
            <%--                    </div>--%>
            <%--                </div>--%>
            <div role="tabpanel" class="tab-pane fade"
                 id="tab_pol_checks" aria-labelledby="profile-tab">

                <table id="polChecksList" class="table table-striped" style="width:100%">
                    <thead>
                    <tr>
                        <th>Check Name</th>
                        <th>Approved</th>
                        <th>Approved By</th>
                        <th>Approved On</th>
                        <th width="5%"></th>
                    </tr>
                    </thead>
                </table>
            </div>
            <div role="tabpanel" class="tab-pane fade"
                 id="tab_pol_certs" aria-labelledby="profile-tab">
                <div class="col-md-6 col-xs-12">
                    <input type="button" class="btn btn-info float-left"
                           value="Print Digital Certificate" id="btn-reprint-cert-policy"
                    >
                    <%--                        <input type="button" value="Allocate" class="btn btn-success" id="btn-allocate">--%>
                    <%--                        <input type="button" value="Deallocate" class="btn btn-success" id="btn-deallocate">--%>
                </div>
                <div class="x_panel">
                    <form id="print-cert-form">
                        <input type="hidden" id="cert-type-id" name="branchCert"/>
                    </form>
                    <div class="cutom-container">
                        <table id="polCertsList" class="table table-striped" style="width:100%">
                            <thead>
                            <tr>
                                <th>Risk</th>
                                <th>WEF</th>
                                <th>WET</th>
                                <th>Start Time</th>
                                <th>Status</th>
                                <th>Alloc By</th>
                                <th>Cert No</th>
                                <th>Good For Print</th>
                            </tr>
                            </thead>
                        </table>
                    </div>
                    <form class="form-horizontal" id="print-form">


                    </form>
                    <input type="button" value="Print" class="btn btn-success" id="btn-print">

                </div>
            </div>
            <div role="tabpanel" class="tab-pane fade"
                 id="tab_pol_receipts" aria-labelledby="profile-tab">

                <table id="polReceipts" class="table table-striped" style="width:100%">
                    <thead>
                    <tr>
                        <th>Receipt No</th>
                        <th>Receipt Date</th>
                        <th>Amount</th>
                    </tr>
                    </thead>
                </table>
            </div>


            <div role="tabpanel" class="tab-pane fade"
                 id="tab_content3" aria-labelledby="comm-rates-tab">
                <button class="btn btn-success btn btn-info" id="btn-add-new-clause">New</button>
                <table id="polclausesList" class="table table-striped" style="width:100%">
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
            <div role="tabpanel" class="tab-pane fade"
                 id="tab_content4" aria-labelledby="end-remarks-tab">
                <button class="btn btn-success btn btn-info" id="btn-add-new-remark">Select Remark</button>

                <button class="btn btn-success btn btn-info" id="btn-save-remark">Save</button>
                <form id="frm-pol-remarks" class="form-horizontal">
                    <input type="hidden" id="pol-remark-pk" name="remarksId">
                    <input type="hidden" id="remark-pk" name="endRemarks.remarkId">
                    <input type="hidden" id="remark-pol-id" name="policy">
                    <div class="item form-group">
                        <textarea class="form-control" rows="5" id="poli-remarks" name="remarks"></textarea>
                    </div>
                </form>
            </div>
            <div role="tabpanel" class="tab-pane fade"
                 id="tab_content5" aria-labelledby="workflow-tab">
                <button class="btn btn-success btn btn-info" id="btn-show-task-history">History</button>
                <img id="proc-main-diagram" class="img-responsive img-rounded proc-diagram" src=""
                     alt="Workflow Process Diagram">
            </div>
        </div>

    </div>
</div>

<jsp:include page="modals/riskmodals.jsp"></jsp:include>
<jsp:include page="modals/endorseriskmodals.jsp"></jsp:include>
<jsp:include page="../modals/prospectmodals.jsp"></jsp:include>