<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="sec" uri="http://www.springframework.org/security/tags" %>
<script type="text/javascript" src="<c:url value="/js/modules/utils/select2builder.js"/>"></script>
<script type="text/javascript" src="<c:url value="/libs/rivets/rivets.js"/>"></script>
<script type="text/javascript">
    var polCode = ${policyId};
    var polStatus = '${policyStatus}';
    console.log(polCode);
</script>
<script type="text/javascript" src="<c:url value="/libs/js/survey.jquery.js"/>"></script>
<script type="text/javascript" src="<c:url value="/js/modules/uwtrans/lifeuwtrans.js"/>"></script>

<div class="x_panel">
    <div class="x_title">
        <h2 id="wkflow-task-name"><i class="fa fa-bars"></i>Investment Details</h2>

        <ul class="nav navbar-right panel_toolbox">
            <li><a class="collapse-link"><i class="fa fa-chevron-up"></i></a>
            </li>
        </ul>
        <div class="clearfix"></div>
    </div>
    <div class="col-md-9 col-sm-9  offset-md-3">
        <input action="action" type="button" onclick="history.go(-1);" class="btn btn-info float-right" value="Back"
               id="renbtn" style="display:none"/>
        <sec:authorize access="hasAnyAuthority('ACCESS_POLICY_REPORTS')">
            <input type="button" class="btn btn-info float-right"
                   value="Reports" id="btn-uw-reports" data-toggle="modal" data-target="#lifereportsModal"
                   style="display:none">
        </sec:authorize>
        <input type="button" class="btn btn-info float-right"
               value="Dispatch" id="btn-dispatch-trans">
        <input type="button" class="btn btn-info float-right"
               value="Assign" id="btn-assign-trans">
        <sec:authorize access="hasAnyAuthority('AUTHORIZE_POLICY')">
            <input type="button" class="btn btn-info float-right"
                   value="Authorise" id="btn-auth-policy">
        </sec:authorize>
        <sec:authorize access="hasAnyAuthority('SAVE_POLICY')">
            <input type="button" class="btn btn-info float-right"
                   value="Save" id="btn-add-policy"
                   style="display:none">
        </sec:authorize>
        <sec:authorize access="hasAnyAuthority('MAKE_POLICY_READY')">
            <input type="button" class="btn btn-info float-right"
                   value="Submit" id="btn-make-ready-policy"
                   style="display:none">
        </sec:authorize>
        <sec:authorize access="hasAnyAuthority('MAKE_POLICY_READY')">
            <input type="button" class="btn btn-info float-right"
                   value="Convert" id="btn-convert-policy"
            >
        </sec:authorize>
        <sec:authorize access="hasAnyAuthority('MAKE_POLICY_READY')">
            <input type="button" class="btn btn-info float-right"
                   value="Unsubmit" id="btn-undo-make-ready"
                   style="display:none">
        </sec:authorize>
    </div>
    <div class="row">
        <br>
    </div>

    <form id="policy-form" class="form-horizontal form-label-left">
        <input type="hidden" id="policy-id" name="policyId"/>
        <input type="hidden" id="pol-trans-type" name="transType"/>
        <input type="hidden" id="pol-rev-trans-type" name="polRevStatus"/>
        <input type="hidden" id="pol-prev-policy" name="prevPolicy"/>
        <input type="hidden" id="pol-reuse-contra-policy" name="reusecontraPolicy"/>
        <input type="hidden" id="pol-buss-type" name="businessType">
        <div class="col-md-6 col-xs-12">

            <div class="item form-group form-required">
                <label for="pol-bin-type" class="label-align col-md-5">
                    Contract Type<span class="required">*</span></label>
                <div class="col-md-7 col-xs-12">
                    <select class="form-control" id="pol-bin-type" required>
                        <option value="">Select Contract Type</option>
                        <option value="B">Contract</option>
                        <option value="M">Negotiable Terms</option>
                    </select>
                </div>
            </div>

            <!-- Investment Frequency -->
            <div class="item form-group form-required">
                <label for="investment-frequency" class="label-align col-md-5">Investment Frequency<span class="required">*</span></label>
                <div class="col-md-7 col-xs-12">
                    <select class="form-control" id="investment-frequency" name="investmentFreq" required>
                        <option value="">Select Frequency</option>
                        <option value="A">Annual</option>
                        <option value="SG">Single</option>
                    </select>
                </div>
            </div>

            <!-- Investment Amount (Dynamic Label) -->
            <div class="item form-group form-required">
                <label id="investment-amount-label" for="investment-amount" class="label-align col-md-5">Investment Amount<span class="required">*</span></label>
                <div class="col-md-7 col-xs-12">
                    <input type="number" class="form-control" id="investment-amount" name="investment" required/>
                </div>
            </div>

            <!-- Allow Top-ups -->
            <div class="item form-group form-required">
                <label for="allow-top-ups" class="label-align col-md-5">Allow Top-ups<span class="required">*</span></label>
                <div class="col-md-7 col-xs-12">
                    <select class="form-control" id="allow-top-ups" name="allowTopUps" required>
                        <option value="">Select TopUps</option>
                        <option value=true>Yes</option>
                        <option value=false>No</option>
                    </select>
                </div>
            </div>

            <!-- Top-Up Amount (Dynamic Label) -->
            <div class="item form-group form-required">
                <label id="top-up-amount-label" for="top-up-amount" class="label-align col-md-5">Top-Up Amount<span class="required">*</span></label>
                <div class="col-md-7 col-xs-12">
                    <input type="number" class="form-control top-up-field" id="top-up-amount" name="topUp" required/>
                </div>
            </div>

            <!-- Top-up Frequency -->
            <div class="item form-group">
                <label for="top-up-frequency" class="label-align col-md-5">
                    Top-up Frequency</label>
                <div class="col-md-7 col-xs-12">
                    <select class="form-control" id="top-up-frequency" name="topUpFreq">
                        <option value="">Select Frequency</option>
                        <option value="SG">Single</option>
                        <option value="M">Monthly</option>
                        <option value="Q">Quarterly</option>
                        <option value="A">Annually</option>
                        <option value="S">Semi-Annually</option>
                    </select>
                </div>
            </div>

            <div class="item form-group form-required">
                <label for="client-frm" class="label-align col-md-5">
                    Assured<span class="required">*</span></label>
                <div class="col-md-7 col-xs-12">
                    <div class="edit-client">
                        <input type="hidden" id="client-id" name="clientId"/>
                        <input type="hidden" id="client-f-name">
                        <input type="hidden" id="client-other-name">
                        <div id="client-frm" class="form-control"
                             select2-url="<c:url value="/protected/uw/policies/uwClients"/>">

                        </div>

                    </div>
                    <div id="display-client">
                        <p class="form-control-static" id="client-info"></p>
                    </div>
                </div>
            </div>
            <div class="item form-group form-required">
                <label for="pol-ins-comp" class="label-align col-md-5">
                    Intermediary</label>
                <div class="col-md-7 col-xs-12">
                    <p class="form-control-static" id="pol-ins-comp"></p>
                </div>
            </div>

            <div class="item form-group form-required">
                <label for="brn-frm" class="label-align col-md-5">
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

<%--            <div class="item form-group form-required">--%>
<%--                <label for="pol-frequency" class="label-align col-md-5">--%>
<%--                    Payment Frequency<span class="required">*</span></label>--%>
<%--                <div class="col-md-7 col-xs-12">--%>
<%--                    <select class="form-control" id="pol-frequency" name="frequency"--%>
<%--                            required>--%>
<%--                        <option value="">Select Payment Frequency</option>--%>
<%--                        <option value="M">Monthly</option>--%>
<%--                        <option value="Q">Quartely</option>--%>
<%--                        <option value="S">Semi-Annually</option>--%>
<%--                        <option value="A">Annually</option>--%>
<%--                        <option value="SG">Single</option>--%>
<%--                    </select>--%>
<%--                </div>--%>
<%--            </div>--%>

            <div class="item form-group form-required">
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
            <div class="item form-group form-required show-output">
                <label for="pol-no" class="label-align col-md-5">
                    Policy No</label>
                <div class="col-md-7 col-xs-12">
                    <input type="hidden" id="div-pol-no" name="polNo"/>
                    <p class="form-control-static" id="pol-no"></p>
                </div>
            </div>
            <div class="item form-group form-required show-output">
                <label for="pol-rev-no" class="label-align col-md-5">
                    Endorsement Number</label>
                <div class="col-md-7 col-xs-12">
                    <input type="hidden" id="div-endos-no" name="polRevNo"/>
                    <p class="form-control-static" id="pol-rev-no"></p>
                </div>
            </div>
            <div class="item form-group form-required show-output">
                <label for="pol-sum-insured" class="label-align col-md-5">
                    Sum Assured</label>
                <div class="col-md-7 col-xs-12">
                    <p class="form-control-static" id="pol-sum-insured"></p>
                </div>
            </div>
            <div class="item form-group form-required show-output">
                <label for="pol-premium" class="label-align col-md-5">
                    Basic Premium</label>
                <div class="col-md-7 col-xs-12">

                    <p class="form-control-static" id="pol-premium"></p>
                </div>
            </div>
            <div class="item form-group form-required show-output">
                <label for="pol-tot-inst" class="label-align col-md-5">
                    Total instalments</label>
                <div class="col-md-7 col-xs-12">
                    <p class="form-control-static" id="pol-tot-inst"></p>
                </div>
            </div>
            <div class="item form-group form-required show-output">
                <label for="pol-phcf" class="label-align col-md-5">
                    PHCF</label>
                <div class="col-md-7 col-xs-12">
                    <p class="form-control-static" id="pol-phcf"></p>
                </div>
            </div>
            <div class="item form-group form-required show-output">
                <label for="pol-phcf" class="label-align col-md-5">
                    Reversionary Bonus</label>
                <div class="col-md-7 col-xs-12">
                    <p class="form-control-static" id="pol-rev-bonus"></p>
                </div>
            </div>
        </div>
        <div class="col-md-6 col-xs-12">
            <div class="item form-group form-required">
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
                             select2-url="<c:url value="/protected/life/policies/lifeBinders"/>">
                        </div>
                    </div>
                    <div id="display-binder">
                        <p class="form-control-static" id="binder-info"></p>
                    </div>
                </div>
            </div>
            <div class="item form-group form-required">
                <label for="pol-prod-name" class="label-align col-md-5">
                    Product</label>
                <div class="col-md-7 col-xs-12">
                    <p class="form-control-static" id="pol-prod-name"></p>
                </div>
            </div>

            <div class="item form-group form-required">
                <label for="pol-term-display" class="label-align col-md-5">
                    Term<span class="required">*</span></label>
                <div class="col-md-7 col-xs-12">
                    <input type="hidden" id="pol-term1">
                    <select class="form-control" id="pol-term" name="polTerm"
                            required>
                        <option value="">Select Term</option>
                    </select>
                    <p class="form-control-static" id="pol-term-display"></p>
                </div>
            </div>

            <!-- Expected Date of First Top-Up -->
            <div class="item form-group form-required">
                <label for="first-top-up-date" class="label-align col-md-5">Expected First Top-Up Date<span class="required">*</span></label>
                <div class="col-md-7 col-xs-12">
                    <div class='input-group date datepicker-input' id="first-top-up-date-picker">
                        <input type='text' class="form-control float-right top-up-field" id="first-top-up-date" name="firstTopUp" required/>
                        <div class="input-group-addon">
                            <span class="fa fa-calendar"></span>
                        </div>
                    </div>
                </div>
            </div>

            <!-- Expected Date of Last Top-Up -->
            <div class="item form-group form-required">
                <label for="last-top-up-date" class="label-align col-md-5">Expected Last Top-Up Date<span class="required">*</span></label>
                <div class="col-md-7 col-xs-12">
                    <div class='input-group date datepicker-input' id="last-top-up-date-picker">
                        <input type='text' class="form-control float-right top-up-field" id="last-top-up-date" name="lastTopUp" required/>
                        <div class="input-group-addon">
                            <span class="fa fa-calendar"></span>
                        </div>
                    </div>
                </div>
            </div>

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

            <div class="item form-group form-required">
                <label for="wet-date" class="label-align col-md-5">Date
                    To/Maturity Date</label>
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

            <div class="item form-group form-required">
                <label for="client-pol-no" class="label-align col-md-5">
                    Insurer Policy number<span class="required">*</span></label>
                <div class="col-md-7 col-xs-12">
                    <input type="text" name="clientPolNo" id="client-pol-no" class="form-control"
                           placeholder="Client Policy Number" required>
                </div>
            </div>
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
            <div class="item form-group form-required show-output">
                <label for="prop-no" class="label-align col-md-5">
                    Proposal No</label>
                <div class="col-md-7 col-xs-12">
                    <input type="hidden" id="div-prop-no" name="proposalNo"/>
                    <p class="form-control-static" id="prop-no"></p>
                </div>
            </div>
            <div class="item form-group form-required show-output">
                <label for="pol-tran-type-disp" class="label-align col-md-5">
                    Transaction Type</label>
                <div class="col-md-7 col-xs-12">
                    <p class="form-control-static" id="pol-tran-type-disp"></p>
                </div>
            </div>
            <div class="item form-group form-required show-output">
                <label for="pol-basic-prem" class="label-align col-md-5">
                    Instalment Prem</label>
                <div class="col-md-7 col-xs-12">
                    <p class="form-control-static" id="pol-basic-prem"></p>
                </div>
            </div>
            <div class="item form-group form-required show-output">
                <label for="pol-status" class="label-align col-md-5">
                    Policy Status</label>
                <div class="col-md-7 col-xs-12">
                    <p class="form-control-static" id="pol-status"></p>
                </div>
            </div>
            <div class="item form-group form-required show-output">
                <label for="pol-paid-to-date" class="label-align col-md-5">
                    Paid to Date</label>
                <div class="col-md-7 col-xs-12">
                    <p class="form-control-static" id="pol-paid-to-date"></p>
                </div>
            </div>
            <div class="item form-group form-required show-output">
                <label for="pol-paid-insts" class="label-align col-md-5">
                    Paid Instalments</label>
                <div class="col-md-7 col-xs-12">
                    <p class="form-control-static" id="pol-paid-insts"></p>
                </div>
            </div>
            <div class="item form-group form-required show-output">
                <label for="pol-paid-insts" class="label-align col-md-5">
                    Terminal Bonus</label>
                <div class="col-md-7 col-xs-12">
                    <p class="form-control-static" id="pol-term-bonus"></p>
                </div>
            </div>
            <div class="item form-group form-required show-output">
                <label for="pol-tax-relief" class="label-align col-md-5">
                    Tax Relief</label>
                <div class="col-md-7 col-xs-12">
                    <p class="form-control-static" id="pol-tax-relief"></p>
                </div>
            </div>
        </div>
    </form>


</div>

<div class="x_panel risk-detail-tab">
    <div class="x_title">
        <ul class="nav navbar-right panel_toolbox">
            <li><a class="collapse-link"><i class="fa fa-chevron-up"></i></a>
            </li>
        </ul>
    </div>
    <div class="x_content">
        <div class="" role="tabpanel" data-example-id="togglable-tabs">
            <ul id="myTab" class="nav nav-tabs bar_tabs" role="tablist">
                <li role="presentation" class="active"><a href="#tab_content1"
                                                          id="home-tab" role="tab" data-toggle="tab"
                                                          aria-expanded="true">Risk Details</a>
                </li>
                <li role="presentation" class="hide-details" id="show-taxes"><a href="#tab_content2"
                                                                                role="tab" id="profile-tab" data-toggle="tab"
                                                                                aria-expanded="false">Taxes</a>
                </li>
                </li>
                <li role="presentation" class="hide-details" id="interested-parties"><a href="#tab_content25" role="tab"
                                                                                        id="int-parties-tabb" data-toggle="tab"
                                                                                        aria-controls="profile"
                                                                                        aria-expanded="false">Beneficiaries</a>
                </li>
                <li role="presentation" class="hide-details" id="est-ben"><a href="#tab_content27" role="tab"
                                                                             id="benefits-tabb" data-toggle="tab"
                                                                             aria-controls="profile"
                                                                             aria-expanded="false">Estimated Benefits</a>
                </li>
                <li role="presentation" class="hide-details" id="instal-schedule"><a href="#tab_content207" role="tab"
                                                                                     id="installments-tabb" data-toggle="tab"
                                                                                     aria-controls="profile"
                                                                                     aria-expanded="false">Installments Schedule</a>
                </li>
                <li role="presentation" class="hide-details" id="show-clauses"><a href="#tab_content3"
                                                                                  role="tab" id="comm-rates-tab" data-toggle="tab"
                                                                                  aria-expanded="false">Clauses</a>
                </li>
                <li role="presentation" class="hide-details" id="show_recpts"><a href="#tab_content28" role="tab"
                                                                                 id="receipts-tab" data-toggle="tab"
                                                                                 aria-controls="profile"
                                                                                 aria-expanded="false">Receipts</a>
                </li>
                <li role="presentation" class="hide-details" id="end-remarks"><a href="#tab_content4"
                                                                                 role="tab" id="end-remarks-tab" data-toggle="tab"
                                                                                 aria-expanded="false">Remarks</a>
                </li>
                <li role="presentation" class="hide-details" id="checks-tab"><a href="#tab_pol_checks"
                                                                                role="tab" id="pol-checks-tab" data-toggle="tab"
                                                                                aria-expanded="false">Checks</a>
                </li>
            </ul>
            <div id="myTabContent" class="tab-content">
                <div role="tabpanel" class="tab-pane active"
                     id="tab_content1" aria-labelledby="home-tab">
                    <input type="hidden" id="risk-bind-code"/>
                    <input type="hidden" id="risk-det-id-pk"/>
                    <button class="btn btn-success btn btn-info" id="btn-endors-risk" style="display:none">Endorse
                    </button>

                    <button class="btn btn-success btn btn-info" id="btn-save-risk">Save</button>
                    <button class="btn btn-info btn btn-info" id="btn-save-cancel">Cancel</button>
                    <button class="btn btn-info btn btn-info" id="btn-import-logs" style="display:none">Logs</button>
                    <div id="risk-div">

                        <table id="risk_tbl" class="table" style="width:100%">
                            <thead>
                            <tr>
                                <th>Life Assured</th>
                                <th>Sub Class</th>
                                <th>Cover Type</th>
                                <th>Age</th>
                                <th width="5%"></th>
                                <th width="5%"></th>
                            </tr>
                            </thead>
                        </table>
                    </div>
                    <form id="risk-form" class="form-horizontal form-label-left">
                        <input type="hidden" id="risk-code-pk" name="riskId"/>
                        <input type="hidden" id="risk-trans-type" name="transType"/>
                        <input type="hidden" id="risk-ident-code" name="riskIdentifier"/>
                        <input type="hidden" id="risk-binder-code" name="bindCode"/>
                        <input type="hidden"  name="wefDate" id="risk-wef-date"/>
                        <input type="hidden"  name="wetDate" id="risk-wet-date"/>
                        <div class="col-md-6 col-xs-12">
                            <div class="item form-group form-required">
                                <label for="lifeassured-frm" class="label-align col-md-5">
                                    Life Assured<span class="required">*</span></label>
                                <div class="col-md-6 col-xs-12">
                                    <input type="hidden" id="lifeassured-code" name="insuredCode"/>
                                    <input type="hidden" id="lifeassured-name">
                                    <input type="hidden" name="riskIdentifier"/>
                                    <input type="hidden" id="lifeassured-other-name">
                                    <div id="lifeassured-frm" class="form-control"
                                         select2-url="<c:url value="/protected/uw/policies/uwClients"/>">

                                    </div>
                                </div>
                            </div>
                            <div class="item form-group form-required">
                                <label for="subclass-frm" class="label-align col-md-5">
                                    Sub Class<span class="required">*</span></label>
                                <div class="col-md-7 col-xs-12">
                                    <input type="hidden" id="risk-sub-code" name="sclCode"/>
                                    <input type="hidden" id="sub-name">
                                    <div id="subclass-frm" class="form-control"
                                         select2-url="<c:url value="/protected/uw/policies/uwsubclasses"/>">
                                    </div>
                                </div>
                            </div>
<%--                            <div class="item form-group form-required computetype-disp">--%>
<%--                                <label for ="compute" class="col-md-5 label-align">Compute using</label>--%>
<%--                                <div id="compute" class="col-md-7 col-xs-12">--%>
<%--                                    <input type="radio" name="computeType" id="pcompute" value="P"> Premium--%>
<%--                                    <input type="radio" name="computeType" id="scompute" value="S"> Sum Assured--%>
<%--                                </div>--%>
<%--                            </div>--%>
                        </div>

                        <div class="col-md-6 col-xs-12">
                            <div class="item form-group form-required">
                                <label for="lifeassured-age" class="label-align col-md-5">
                                    ANB</label>
                                <div class="col-md-7 col-xs-12">
                                    <input type="text" id="lifeassured-age" name="workingAge" readonly>
                                </div>
                            </div>
                            <div class="item form-group form-required">
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

                            <div class="item form-group form-required premium-disp">
                                <label for="premium-amt" class="label-align col-md-5">
                                    Premium</label>
                                <div class="col-md-7 col-xs-12">
                                    <input type="number" class="form-control float-right" align="right"  id="premium-amt" name="premium" >
                                </div>
                            </div>
                            <div class="item form-group form-required sumassured-disp">
                                <label for="sumassured-amt" class="label-align col-md-5">
                                    Sum Assured</label>
                                <div class="col-md-7 col-xs-12">
                                    <input type="number" class="form-control float-right" id="sumassured-amt" name="sumInsured" >
                                </div>
                            </div>
                        </div>
                    </form>


                    <div class="x_panel">
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
                                        <li role="presentation" class="active"><a href="#tab_content11" id="home-tabb"
                                                                                  role="tab" data-toggle="tab"
                                                                                  aria-controls="home"
                                                                                  aria-expanded="true">Premium Items</a>
                                        </li>
                                        <li role="presentation" class=""><a href="#tab_content24" role="tab"
                                                                            id="docs-tabb" data-toggle="tab"
                                                                            aria-controls="profile"
                                                                            aria-expanded="false">Risk Documents</a>
                                        </li>
                                        <li role="presentation" class=""><a href="#tab_content26" role="tab"
                                                                            id="insured-docs-tabb" data-toggle="tab"
                                                                            aria-controls="profile"
                                                                            aria-expanded="false">Assured/Life Assured Documents</a>
                                        </li>

                                    </ul>
                                    <div id="myTabContent2" class="tab-content">
                                        <div role="tabpanel" class="tab-pane active" id="tab_content11"
                                             aria-labelledby="home-tab">
                                            <button class="btn btn-success btn btn-info" id="btn-add-new-section">New
                                            </button>
                                            <input type="hidden" id="insured-client-age">
                                            <div class="card-box table-responsive">
                                                <table id="section_tbl" class="table" style="width:100%">
                                                    <thead>
                                                    <tr>
                                                        <th>Premium Item</th>
                                                        <th>Rate</th>
                                                        <th>Calc Prem</th>
                                                        <th>Premium</th>
                                                        <th width="5%"></th>
                                                        <th width="5%"></th>
                                                    </tr>
                                                    </thead>
                                                </table>
                                            </div>
                                        </div>
                                        <div role="tabpanel" class="tab-pane fade"
                                             id="tab_content26" aria-labelledby="profile-tab" >
                                            <div class="card-box table-responsive">
                                                <table id="clientDocsList" class="table" style="width:100%">
                                                    <caption><b>Assured</b></caption>
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
                                             aria-labelledby="profile-tab" >
                                            <button class="btn btn-success btn btn-info" id="btn-add-new-sched">New
                                            </button>
                                            <button class="btn btn-success btn btn-info" id="btn-add-edit-sched">Edit
                                            </button>
                                            <button class="btn btn-success btn btn-info" id="btn-add-del-sched">Delete
                                            </button>
                                            <iframe id="downloadFrame" style="display:none"></iframe>
                                            <table id="risk-sched_tbl" class="table" style="width:100%">

                                            </table>

                                        </div>

                                        <div role="tabpanel" class="tab-pane fade" id="tab_content24"
                                             aria-labelledby="profile-tab">
                                            <button class="btn btn-success btn btn-info" id="btn-add-docs">New</button>
                                            <table id="risk_docs_tbl" class="table" style="width:100%">
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
                                                <table id="cert_tbl" class="table" style="width:100%">
                                                    <thead>
                                                    <tr>
                                                        <th>Cert Type</th>
                                                        <th>Status</th>
                                                        <th>Print Status</th>
                                                        <th>WEF</th>
                                                        <th>WET</th>
                                                        <th>Cert No</th>
                                                        <th>Reason cancelled</th>
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
                            <div class="card-box table-responsive">
                                <table id="section_form_tbl" class="table" style="width:100%">
                                    <thead>
                                    <tr>
                                        <th>Premium Item</th>
                                        <th>Age</th>
                                        <th>Rate</th>
                                        <th>Div Factor</th>
                                        <th>Free Limit</th>
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
                        <table id="polTaxesList" class="table" style="width:100%">
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
                <div role="tabpanel" class="tab-pane fade" id="tab_content25"
                     aria-labelledby="home-tab">
                    <button class="btn btn-success btn btn-info" id="btn-add-new-ips">New
                    </button>
                    <div class="card-box table-responsive">
                        <table id="benefeciary_tbl" class="table" style="width:100%">
                            <thead>
                            <tr>
                                <th>Name</th>
                                <th>Relationship</th>
                                <th>Share(%)</th>
                                <th>ID No/Reg No</th>
                                <th width="5%"></th>
                                <th width="5%"></th>
                            </tr>
                            </thead>
                        </table>
                    </div>
                </div>

                <div role="tabpanel" class="tab-pane fade" id="tab_content28"
                     aria-labelledby="home-tab">
                    <div class="x_content">
                        <div class="card-box table-responsive">
                            <table id="receipts_tbl" class="table" style="width:100%">
                                <thead>
                                <tr>
                                    <th>No</th>
                                    <th>Date</th>
                                    <th>Dr/Cr</th>
                                    <th>Amount</th>
                                    <th>Allocate Amount</th>
                                    <th>Balance</th>
                                </tr>
                                </thead>
                            </table>
                        </div>
                        <div class="x_title">
                            <h4>Allocation Details</h4>
                        </div>
                        <div class="cutom-container">
                            <table id="receiptalloc_tbl" class="table" style="width:100%">
                                <thead>
                                <tr>

                                    <th>Instalment No</th>
                                    <th>Allocation Amount</th>
                                    <th>Comm Amount</th>
                                    <th>Paid To Date</th>
                                    <th width="5%"></th>
                                </tr>
                                </thead>
                            </table>
                        </div>
                    </div>
                </div>
                <div role="tabpanel" class="tab-pane fade" id="tab_content27"
                     aria-labelledby="home-tab">
                    <div class="card-box table-responsive">
                        <table id="benefit_tbl" class="table" style="width:100%">
                            <thead>
                            <tr>
                                <th>Expected Year</th>
                                <th>Est. Benefit</th>
                            </tr>
                            </thead>
                        </table>
                    </div>
                </div>
                <div role="tabpanel" class="tab-pane fade" id="tab_content207"
                     aria-labelledby="home-tab">
                    <div class="card-box table-responsive">
                        <table id="installments_tbl" class="table" style="width:100%">
                            <thead>
                            <tr>
                                <th>Installment No</th>
                                <th>Premium Amount</th>
                                <th>Expected Payment Date</th>
                                <th>Installment Paid</th>
                                <th>Paid Date</th>
                            </tr>
                            </thead>
                        </table>
                    </div>
                </div>
                <div role="tabpanel" class="tab-pane fade"
                     id="tab_pol_checks" aria-labelledby="profile-tab">

                    <table id="polChecksList" class="table" style="width:100%">
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
                     id="tab_content3" aria-labelledby="comm-rates-tab">
                    <button class="btn btn-success btn btn-info" id="btn-add-new-clause">New</button>
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
                <div role="tabpanel" class="tab-pane fade"
                     id="tab_content4" aria-labelledby="end-remarks-tab">
                    <button class="btn btn-success btn btn-info" id="btn-add-new-remark">Select Remark</button>

                    <button class="btn btn-success btn btn-info" id="btn-save-remark">Save</button>
                    <form id="frm-pol-remarks" class="form-horizontal">
                        <input type="hidden" id="pol-remark-pk" name="remarksId">
                        <input type="hidden" id="remark-pk" name="endRemarks.remarkId">
                        <input type="hidden" id="remark-pol-id" name="policy">
                        <div class="item form-group">
                            <textarea class="form-control" rows="5" id="poli-remarks" name="polRemarks"></textarea>
                        </div>
                    </form>
                </div>
                <%--                <div role="tabpanel" class="tab-pane fade"--%>
                <%--                     id="tab_content5" aria-labelledby="workflow-tab">--%>
                <%--                    <button class="btn btn-success btn btn-info" id="btn-show-task-history">History</button>--%>
                <%--                    <img id="proc-main-diagram" class="img-responsive img-rounded proc-diagram" src=""--%>
                <%--                         alt="Workflow Process Diagram">--%>
                <%--                </div>--%>
            </div>
        </div>
    </div>
</div>

<script type="text/javascript">
    // Handle dynamic label for Top-Up Amount based on the Top-Up Frequency
    document.getElementById('top-up-frequency').addEventListener('change', function() {
        var selectedFrequency = this.value;
        var topUpLabel = document.getElementById('top-up-amount-label');

        if (selectedFrequency === 'SG') {
            topUpLabel.innerHTML = 'Single Top-Up Amount<span class="required">*</span>';
        } else if (selectedFrequency === 'A') {
            topUpLabel.innerHTML = 'Annual Top-Up Amount<span class="required">*</span>';
        } else if (selectedFrequency === 'M') {
            topUpLabel.innerHTML = 'Monthly Top-Up Amount<span class="required">*</span>';
        } else if (selectedFrequency === 'Q') {
            topUpLabel.innerHTML = 'Quarterly Top-Up Amount<span class="required">*</span>';
        } else if (selectedFrequency === 'S') {
            topUpLabel.innerHTML = 'Semi-Annual Top-Up Amount<span class="required">*</span>';
        } else {
            topUpLabel.innerHTML = 'Top-Up Amount<span class="required">*</span>'; // Default label
        }
    });

    // Handle locking and unlocking of top-up fields based on Allow Top-ups selection
    document.getElementById('allow-top-ups').addEventListener('change', function() {
        var allowTopUps = this.value;
        var topUpFields = document.querySelectorAll('.top-up-field');

        // If "No" or empty is selected, disable all top-up-related fields
        if (allowTopUps === 'false' || allowTopUps === '') {
            topUpFields.forEach(function(field) {
                field.setAttribute('disabled', 'disabled');
            });
        } else if (allowTopUps === 'true') {
            // If "Yes" is selected, enable all top-up-related fields
            topUpFields.forEach(function(field) {
                field.removeAttribute('disabled');
            });
        }
    });

    // Initial state check in case the form loads with a specific value selected
    document.addEventListener('DOMContentLoaded', function() {
        var allowTopUps = document.getElementById('allow-top-ups').value;
        var topUpFields = document.querySelectorAll('.top-up-field');

        if (allowTopUps === 'no' || allowTopUps === '') {
            topUpFields.forEach(function(field) {
                field.setAttribute('disabled', 'disabled');
            });
        }
    });

    // Handle dynamic label for Investment Amount based on Investment Frequency
    document.getElementById('investment-frequency').addEventListener('change', function() {
        var selectedFrequency = this.value;
        var investmentLabel = document.getElementById('investment-amount-label');

        if (selectedFrequency === 'single') {
            investmentLabel.innerHTML = 'Single Investment Amount<span class="required">*</span>';
        } else if (selectedFrequency === 'annual') {
            investmentLabel.innerHTML = 'Annual Investment Amount<span class="required">*</span>';
        } else {
            investmentLabel.innerHTML = 'Investment Amount<span class="required">*</span>'; // Default label
        }
    });

</script>

<jsp:include page="modals/riskmodals.jsp"></jsp:include>
<jsp:include page="modals/endorseriskmodals.jsp"></jsp:include>
<jsp:include page="../modals/prospectmodals.jsp"></jsp:include>