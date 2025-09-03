<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="sec" uri="http://www.springframework.org/security/tags" %>
<script type="text/javascript" src="<c:url value="/js/modules/utils/select2builder.js"/>"></script>
<script type="text/javascript" src="<c:url value="/libs/rivets/rivets.js"/>"></script>
<script type="text/javascript">
    var polCode = ${policyId};
    var polStatus = '${policyStatus}';

    console.log("polCode:", polCode);
    console.log("polStatus:", polStatus);

    function unlockForm() {
        if (polCode !== -2000 && polStatus !== 'undefined') {
            navigator.sendBeacon(SERVLET_CONTEXT + '/protected/home/unlockForm',
                new URLSearchParams({ "formName": "LIFE", "formId": polCode })
            );
            console.log("Form unlock request sent.");
        }
    }

    window.addEventListener("beforeunload", unlockForm);
</script>

<script src="https://cdnjs.cloudflare.com/ajax/libs/jquery-validate/1.19.5/jquery.validate.min.js"></script>
<script type="text/javascript" src="<c:url value="/libs/js/survey.jquery.js"/>"></script>
<script type="text/javascript" src="<c:url value="/js/modules/uwtrans/lifeuwtrans.js"/>"></script>
<script type="text/javascript" src="<c:url value="/js/modules/uwtrans/endorse.js"/>"></script>
<style>
    .btn-processing {
        background-color: #6c757d !important;
        border-color: #6c757d !important;
        color: #fff !important;
        opacity: 0.6 !important;
        cursor: not-allowed !important;
        pointer-events: none !important;
    }

    .btn-processing:hover,
    .btn-processing:focus,
    .btn-processing:active {
        background-color: #6c757d !important;
        border-color: #6c757d !important;
        color: #fff !important;
        opacity: 0.6 !important;
    }
</style>

<div class="x_panel">
    <div class="x_title">
        <h2 id="wkflow-task-name"><i class="fa fa-bars"></i>Life Proposal Details</h2>

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


        <sec:authorize access="hasAnyAuthority('MAKE_POLICY_READY')">
            <input type="button" class="btn btn-info float-right"
                   value="Unsubmit" id="btn-undo-make-ready"
                   style="display:none">
        </sec:authorize>

        <sec:authorize access="hasAnyAuthority('MAKE_POLICY_READY')">
                <input type="button" class="btn btn-info float-right"
                       value="Negotiated Premium" id="btn-negotiate-premium"
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

            <div class="item form-group form-required">
                <label for="client-frm" class="label-align col-md-5">
                    Assured<span class="required">*</span></label>
                <div class="col-md-7 col-xs-12">
                    <div class="edit-client">
                        <input type="hidden" id="client-id" name="clientId"/>
                        <input type="hidden" id="client-f-name">
                        <input type="hidden" id="client-other-name">
                        <input type="hidden" id="ntln-id">
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
                    Insurer</label>
                <div class="col-md-7 col-xs-12">
                    <p class="form-control-static" id="pol-ins-comp"></p>
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

            <div class="item form-group form-required">
                <label for="pol-frequency" class="label-align col-md-5">
                    Payment Frequency<span class="required">*</span></label>
                <div class="col-md-7 col-xs-12">
                    <select class="form-control" id="pol-frequency" name="frequency"
                            required>
                        <option value="">Select Payment Frequency</option>
<%--                        <option value="D">Daily</option>--%>
<%--                        <option value="W">Weekly</option>--%>
                        <option value="M">Monthly</option>
                        <option value="Q">Quarterly</option>
                        <option value="S">Semi-Annually</option>
                        <option value="A">Annually</option>
                        <option value="SG">Single</option>
                    </select>
                </div>
            </div>

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
            <div class="introducer-agent-absaNo">
                <div class="item form-group">
                    <label for="introducer-absaNo-name" class="label-align col-md-5">Introducer AB No</label>
                    <div class="col-md-7 col-xs-12">
                        <input type="hidden" id="introducer-absa-No-id" name="absaNoIntroducer"/>
                        <input type="text" id="introducer-absaNo-name" class="form-control" readonly>
                    </div>
                </div>
            </div>
            <div class="leads-man-absaNo">
                <div class="item form-group">
                    <label for="leads-absaNo-name" class="label-align col-md-5">Leads Man AB No</label>
                    <div class="col-md-7 col-xs-12">
                        <input type="hidden" id="leads-absaNo-id" name="absaNoLeadsMan"/>
                        <input type="text" id="leads-absaNo-name" class="form-control" readonly>
                    </div>
                </div>
            </div>
            <div class="sub-agent-type" style="display: none;">
                <div class="item form-group form-required">
                    <label for="houseId" class="label-align col-md-5">Sub-Agent</label>
                    <div class="col-md-7 col-xs-12">
                        <input type="hidden" id="sub-agent-id" name="subAgentId"/>
                        <input type="hidden" id="sub-agent-name">
                        <div id="sub-agent-frm" class="form-control"
                             select2-url="<c:url value='/protected/uw/policies/inhouseagents'/>">
                        </div>
                    </div>
                </div>
            </div>

            <div class="marketer-agent-type" style="display: none;">
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
                <label for="negotiated-prem" class="label-align col-md-5">
                    Negotiated Premium</label>
                <div class="col-md-7 col-xs-12">
                    <input type="hidden" id="negotiated-prem" name="negotiatedPremium"/>
                    <p class="form-control-static" id="negotiated" ></p>
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
            <div class="item form-group form-required show-output">
                <label for="pol-sub-agent-comm" class="label-align col-md-5">
                    Sub Agent Commission</label>
                <div class="col-md-7 col-xs-12">
                    <p class="form-control-static" id="pol-sub-agent-comm"></p>
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
            <div class="item form-group form-required">
                <label for="pol-prod-name" class="label-align col-md-5">
                    Product</label>
                <div class="col-md-7 col-xs-12">
                    <p class="form-control-static" id="pol-prod-name"></p>
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


            <div class="item form-group">
                <label for="client-pol-no" class="label-align col-md-5">
                    Insurer Policy No</label>
                <div class="col-md-7 col-xs-12">
                    <input type="text" name="clientPolNo" id="client-pol-no" class="form-control"
                           placeholder="Client Policy Number" readonly>
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
            <div class="introducer-agent-type">
                <div class="item form-group form-required ">
                    <label for="houseId" class="label-align col-md-5">
                        Introducer </label>
                    <div class="col-md-7 col-xs-12">
                        <input type="hidden" id="introducer-id" name="introducerAgentId"/>
                        <input type="hidden" id="introducer-name">
                        <div id="introducer-frm" class="form-control"
                             select2-url="<c:url value="/protected/uw/policies/introducergents"/>">

                        </div>
                    </div>
                </div>
            </div>
            <div class="leads-man-type">
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
            <div class="sub-agent-absNo" style="display: none;">
                <div class="item form-group">
                    <label for="sub-agent-absaNo-name" class="label-align col-md-5">Sub Agent AB No</label>
                    <div class="col-md-7 col-xs-12">
                        <input type="hidden" id="sub-agent-absaNo-id" name="absaNoSubAgent"/>
                        <input type="text" id="sub-agent-absaNo-name" class="form-control" readonly>
                    </div>
                </div>
            </div>
            <div class="marketer-agent-absNo" style="display: none;">
                <div class="item form-group">
                    <label for="marketer-absaNo-name" class="label-align col-md-5">Marketer AB No</label>
                    <div class="col-md-7 col-xs-12">
                        <input type="hidden" id="marketer-absaNo-id" name="absaNoMarketer"/>
                        <input type="text" id="marketer-absaNo-name" class="form-control" readonly>
                    </div>
                </div>
            </div>
               <!-- <div class="item form-group">
                    <label for="chk-admin-fee" class="col-md-5 label-align">Admin Fee Applicable</label>
                    <div class="col-md-7 checkbox admin-fee">
                        <label>
                            <input type="checkbox" id="chk-admin-fee" name="adminFeePolicy">
                        </label>
                    </div>
                </div>
                -->
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
            <div class="item form-group form-required">
                <label for="pol-marketer-comm" class="label-align col-md-5">
                    Marketer Commission</label>
                <div class="col-md-7 col-xs-12">
                    <p class="form-control-static" id="pol-marketer-comm"></p>
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
                                                                                role="tab" id="profile-tab"
                                                                                data-toggle="tab"
                                                                                aria-expanded="false">Levies</a>
                </li>
                <%--                <li role="presentation" class="hide-details"><a href="#tab_content29" role="tab"--%>
                <%--                                                    id="questionnaire-tabb" data-toggle="tab"--%>
                <%--                                                    aria-controls="profile"--%>
                <%--                                                    aria-expanded="false">Questionnaire</a>--%>
                </li>
                <li role="presentation" class="hide-details" id="interested-parties"><a href="#tab_content25" role="tab"
                                                                                        id="int-parties-tabb"
                                                                                        data-toggle="tab"
                                                                                        aria-controls="profile"
                                                                                        aria-expanded="false">Beneficiaries</a>
                </li>
                <li role="presentation" class="hide-details" id="est-ben"><a href="#tab_content_27" role="tab"
                                                                             id="dependents-tabb" data-toggle="tab"
                                                                             aria-controls="profile"
                                                                             aria-expanded="false">Dependents </a>
                </li>
                <li role="presentation" class="hide-details" id="est-ben"><a href="#tab_content27" role="tab"
                                                                             id="benefits-tabb" data-toggle="tab"
                                                                             aria-controls="profile"
                                                                             aria-expanded="false">Surrender
                    Value</a>
                </li>
                <li role="presentation" class="hide-details" id="instal-schedule"><a href="#tab_content207" role="tab"
                                                                                     id="installments-tabb"
                                                                                     data-toggle="tab"
                                                                                     aria-controls="profile"
                                                                                     aria-expanded="false">Instalments
                    Schedule</a>
                </li>
                <li role="presentation" class="hide-details" id="show-clauses"><a href="#tab_content3"
                                                                                  role="tab" id="comm-rates-tab"
                                                                                  data-toggle="tab"
                                                                                  aria-expanded="false">Clauses</a>
                </li>
                <li role="presentation" class="hide-details" id="show_recpts"><a href="#tab_content28" role="tab"
                                                                                 id="receipts-tab" data-toggle="tab"
                                                                                 aria-controls="profile"
                                                                                 aria-expanded="false">Receipts</a>
                </li>
                <li role="presentation" class="hide-details" id="end-remarks"><a href="#tab_content4"
                                                                                 role="tab" id="end-remarks-tab"
                                                                                 data-toggle="tab"
                                                                                 aria-expanded="false">Remarks</a>
                </li>
                <li role="presentation" class="hide-details" id="refund-remarks"><a href="#tab_content6"
                                                                                 role="tab" id="refund-remarks-tab"
                                                                                 data-toggle="tab"
                                                                                 aria-expanded="false">Refund Remarks</a>
                </li>
                <li role="presentation" class="hide-details" id="checks-tab"><a href="#tab_pol_checks"
                                                                                role="tab" id="pol-checks-tab"
                                                                                data-toggle="tab"
                                                                                aria-expanded="false">Checks</a>
                <li role="presentation" class="hide-details" id="checks-tab"><a href="#tab_pol_more_info"
                                                                                role="tab" id="pol-checks-tab"
                                                                                data-toggle="tab"
                                                                                aria-expanded="false">Provide More Cover info</a>

                </li>
                <li role="presentation" class="hide-details" id="audit-trails"><a href="#tab_audit_trails" role="tab" data-toggle="tab" aria-expanded="false">Activity Trails</a></li>
                <%--                <li role="presentation" class="hide-details" id="workflow"><a href="#tab_content5"--%>
                <%--                                                                  role="tab" id="workflow-tab" data-toggle="tab"--%>
                <%--                                                                  aria-expanded="false">Process Diagram</a>--%>
                <%--                </li>--%>
            </ul>
            <div id="myTabContent" class="tab-content">
                <div role="tabpanel" class="tab-pane active"
                     id="tab_content1" aria-labelledby="home-tab">
                    <input type="hidden" id="risk-bind-code"/>
                    <input type="hidden" id="risk-det-id-pk"/>
<%--                    <button class="btn btn-success btn btn-info" id="btn-endors-risk" style="display:none">Endorse--%>
<%--                    </button>--%>

                    <button class="btn btn-success btn btn-info" id="btn-save-risk">Save</button>
                    <button class="btn btn-info btn btn-info" id="btn-save-cancel">Cancel</button>
                    <button class="btn btn-info btn btn-info" id="btn-import-logs" style="display:none">Logs</button>
                    <div id="risk-div">

                        <table id="risk_tbl" class="table table-striped" style="width:100%">
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
                        <input type="hidden" name="wefDate" id="risk-wef-date"/>
                        <input type="hidden" name="wetDate" id="risk-wet-date"/>
                        <div class="col-md-6 col-xs-12">
                            <div class="item form-group form-required">
                                <label for="lifeassured-frm" class="label-align col-md-5">
                                    Life Assured<span class="required">*</span></label>
                                <div class="col-md-6 col-xs-12">
                                    <input type="hidden" id="lifeassured-code" name="insuredCode"/>
                                    <input type="hidden" id="lifeassured-name">
                                    <input type="hidden" name="riskIdentifier"/>
                                    <input type="hidden" id="lifeassured-other-name">
                                    <input type="hidden" id="life-ntl-id">
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
                                <%--                                <div class="col-md-1 col-xs-12 edit-client">--%>
                                <%--                                    <a--%>
                                <%--                                            href="<c:url value="/protected/clients/setups/clientsform?type=life"/>"  id="btn-add-lifeassured" class="btn-sm btn-success btn-info">New</a>--%>
                                <%--                                </div>--%>
                            </div>
                            <div class="item form-group form-required computetype-disp">
                                <label for="compute" class="col-md-5 label-align">Compute using</label>
                                <div id="compute" class="col-md-7 col-xs-12">
                                    <input type="radio" name="computeType" id="pcompute" value="P"> Premium
                                    <input type="radio" name="computeType" id="scompute" value="S"> Sum Assured
                                </div>
                            </div>


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

<%--                            <div class="item form-group form-required premium-disp">--%>
<%--                                <label for="premium-amt" class="label-align col-md-5">--%>
<%--                                    Premium</label>--%>
<%--                                <div class="col-md-7 col-xs-12">--%>
<%--                                    <input type="number" class="form-control float-right" align="right" id="premium-amt"--%>
<%--                                           name="premium" step="0.01" min="0" value="0.00" oninput="this.value = parseFloat(this.value).toFixed(2)">--%>
<%--&lt;%&ndash;                                        <input type="text" class="form-control float-right" align="right"&ndash;%&gt;--%>
<%--&lt;%&ndash;                                                      id="premium-amt" name="premium" value="0.00" oninput="formatWithCommas(this)">&ndash;%&gt;--%>
<%--                                </div>--%>
<%--                            </div>--%>
<%--                            <div class="item form-group form-required sumassured-disp">--%>
<%--                                <label for="sumassured-amt" class="label-align col-md-5">--%>
<%--                                    Sum Assured</label>--%>
<%--                                <div class="col-md-7 col-xs-12">--%>
<%--                                    <input type="number" class="form-control float-right" id="sumassured-amt"--%>
<%--                                           name="sumInsured" step="0.01" min="0" value="0.00" oninput="this.value = parseFloat(this.value).toFixed(2)">--%>
<%--&lt;%&ndash;                                        <input type="text" class="form-control float-right" id="sumassured-amt"&ndash;%&gt;--%>
<%--&lt;%&ndash;                                                      name="sumInsured" value="0.00" oninput="formatWithCommas(this)">&ndash;%&gt;--%>
<%--                                </div>--%>
<%--                            </div>--%>

                            <div class="item form-group form-required premium-disp">
                                <label for="premium-amt-display" class="label-align col-md-5">Premium</label>
                                <div class="col-md-7 col-xs-12">
                                    <div class="input-wrapper">
                                        <!-- Visible input for display -->
                                        <input type="text" class="form-control amount-input float-right" id="premium-amt-display"
                                               value="0.00" placeholder="0.00" oninput="formatAmount(this)" onpaste="handlePaste(this)">
                                        <!-- Hidden input for submission -->
                                        <input type="hidden" id="premium-amt" name="premium" value="0.00">
                                    </div>
                                </div>
                            </div>

                            <div class="item form-group form-required sumassured-disp">
                                <label for="sumassured-amt-display" class="label-align col-md-5">Sum Assured</label>
                                <div class="col-md-7 col-xs-12">
                                    <!-- Visible input for display -->
                                    <input type="text" class="form-control amount-input float-right" id="sumassured-amt-display"
                                           value="0.00" placeholder="0.00" oninput="formatAmount(this)" onpaste="handlePaste(this)">
                                    <!-- Hidden input for submission -->
                                    <input type="hidden" id="sumassured-amt" name="sumInsured" value="0.00">
                                </div>
                            </div>

                        </div>


                        <script>
                            function formatAmount(input) {
                                // Store cursor position
                                const cursorPosition = input.selectionStart;
                                const oldLength = input.value.length;

                                // Remove all non-digit and non-decimal characters
                                let value = input.value.replace(/[^0-9.]/g, '');

                                // Handle multiple decimal points - keep only the first one
                                const parts = value.split('.');
                                if (parts.length > 2) {
                                    value = parts[0] + '.' + parts.slice(1).join('');
                                }

                                // Limit to 2 decimal places
                                if (parts.length === 2 && parts[1].length > 2) {
                                    value = parts[0] + '.' + parts[1].substring(0, 2);
                                }

                                // Convert to number and handle edge cases
                                let numValue = parseFloat(value || '0');
                                if (isNaN(numValue)) {
                                    numValue = 0;
                                }

                                // Format with commas
                                const formatted = formatNumberWithCommas(numValue);

                                // Update visible input value
                                input.value = formatted;

                                // Sync raw value to hidden input
                                const hiddenInputId = input.id.replace('-display', '');
                                const hiddenInput = document.getElementById(hiddenInputId);
                                if (hiddenInput) {
                                    hiddenInput.value = numValue.toFixed(2);
                                }

                                // Restore cursor position (approximate)
                                const newLength = formatted.length;
                                const lengthDiff = newLength - oldLength;
                                const newPosition = Math.max(0, Math.min(cursorPosition + lengthDiff, newLength));

                                setTimeout(() => {
                                    input.setSelectionRange(newPosition, newPosition);
                                }, 0);
                            }

                            function formatNumberWithCommas(num) {
                                if (num === 0) return '0.00';
                                return num.toLocaleString('en-US', {
                                    minimumFractionDigits: 2,
                                    maximumFractionDigits: 2
                                });
                            }

                            function handlePaste(input) {
                                setTimeout(() => {
                                    formatAmount(input);
                                }, 0);
                            }

                            function setDemoValue(displayInputId, value) {
                                const input = document.getElementById(displayInputId);
                                const numValue = parseFloat(value);
                                input.value = formatNumberWithCommas(numValue);
                                const hiddenInputId = displayInputId.replace('-display', '');
                                const hiddenInput = document.getElementById(hiddenInputId);
                                if (hiddenInput) {
                                    hiddenInput.value = numValue.toFixed(2);
                                }
                            }

                            function clearValues() {
                                const premiumDisplay = document.getElementById('premium-amt-display');
                                const sumAssuredDisplay = document.getElementById('sumassured-amt-display');
                                const premiumHidden = document.getElementById('premium-amt');
                                const sumAssuredHidden = document.getElementById('sumassured-amt');

                                premiumDisplay.value = '0.00';
                                sumAssuredDisplay.value = '0.00';
                                premiumHidden.value = '0.00';
                                sumAssuredHidden.value = '0.00';
                            }

                            // Test function
                            function testValues() {
                                setDemoValue('premium-amt-display', '1234.56');
                                setDemoValue('sumassured-amt-display', '500000');
                            }
                        </script>


                    </form>

                    <div class="x_panel" id="prem-item">
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
                                        <%--<li role="presentation" class=""><a href="#tab_content23" role="tab"--%>
                                        <%--id="schedules-tabb" data-toggle="tab"--%>
                                        <%--aria-controls="profile"--%>
                                        <%--aria-expanded="false" disabled="true">Schedules</a>--%>
                                        <%--</li>--%>
                                        <li role="presentation" class=""><a href="#tab_content24" role="tab"
                                                                            id="docs-tabb" data-toggle="tab"
                                                                            aria-controls="profile"
                                                                            aria-expanded="false">Risk/Supporting Documents</a>
                                        </li>
                                        <li role="presentation" class=""><a href="#tab_content30" role="tab"
                                                                            id="refund-docs-tabb" data-toggle="tab"
                                                                            aria-controls="profile"
                                                                            aria-expanded="false">Refund Supporting Documents</a>
                                        </li>
                                        <li role="presentation" class=""><a href="#tab_content26" role="tab"
                                                                            id="insured-docs-tabb" data-toggle="tab"
                                                                            aria-controls="profile"
                                                                            aria-expanded="false">KYC Documents</a>
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
                                             id="tab_content26" aria-labelledby="profile-tab">
                                            <div class="card-box table-responsive">
                                                <table id="clientDocsList" class="table table-striped"
                                                       style="width:100%">
                                                    <caption><b>Assured</b></caption>
                                                    <thead>
                                                    <tr>
                                                        <th>Document ID</th>
                                                        <th>Document Desc</th>
                                                        <th>File Name</th>
                                                        <th>Uploaded By</th>
                                                        <th>Uploaded Date</th>
                                                        <th>Verified By</th>
                                                        <th>Verified Date</th>
                                                        <th width="5%"></th>
                                                    </tr>
                                                    </thead>
                                                </table>
                                            </div>
                                            <%--                                            <div class="card-box table-responsive">--%>
                                            <%--                                                    <table id="clientDocsList1" class="table table-striped" style="width:100%">--%>
                                            <%--                                                        <caption > <b>Life Assured</b></caption>--%>
                                            <%--                                                        <thead>--%>

                                            <%--                                                        <tr>--%>
                                            <%--                                                            <th>Document ID</th>--%>
                                            <%--                                                            <th>Document Desc</th>--%>
                                            <%--                                                            <th>File Name</th>--%>
                                            <%--                                                            <th>File Verifier</th>--%>
                                            <%--                                                            <th width="5%"></th>--%>
                                            <%--                                                        </tr>--%>
                                            <%--                                                        </thead>--%>
                                            <%--                                                    </table>--%>
                                            <%--                                                </div>--%>
                                        </div>

                                        <div role="tabpanel" class="tab-pane fade" id="tab_content23"
                                             aria-labelledby="profile-tab">
                                            <button class="btn btn-success btn btn-info" id="btn-add-new-sched">New
                                            </button>
                                            <button class="btn btn-success btn btn-info" id="btn-add-edit-sched">Edit
                                            </button>
                                            <button class="btn btn-success btn btn-info" id="btn-add-del-sched">Delete
                                            </button>
                                            <iframe id="downloadFrame" style="display:none"></iframe>
                                            <table id="risk-sched_tbl" class="table table-striped" style="width:100%">

                                            </table>

                                        </div>

                                        <div role="tabpanel" class="tab-pane fade" id="tab_content24"
                                             aria-labelledby="profile-tab">
                                            <button class="btn btn-success btn btn-info" id="btn-add-docs">New</button>
                                            <div class="table-responsive">
                                            <table id="risk_docs_tbl" class="table table-striped" style="width:100%">
                                                <thead>
                                                <tr>
                                                    <th>Revision No</th>
                                                    <th>Document ID</th>
                                                    <th>Document Desc</th>
                                                    <th>File Name</th>
                                                    <th>Uploaded By</th>
                                                    <th>Uploaded Date</th>
                                                    <th>Verified By</th>
                                                    <th>Verified Date</th>
                                                    <th>Verify Document</th>
                                                    <th>Comment</th>
                                                    <th width="5%"></th>
                                                    <th width="5%"></th>
                                                    <th width="5%"></th>
                                                </tr>
                                                </thead>
                                            </table>
                                            </div>

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
                                                        <th width="5%"></th>
                                                        <th width="5%"></th>
                                                    </tr>
                                                    </thead>
                                                </table>
                                            </div>

                                        </div>
                                        <div role="tabpanel" class="tab-pane fade" id="tab_content31"
                                             aria-labelledby="profile-tab">
                                            <button class="btn btn-primary btn btn-primary" id="btn-add-docs">New</button>
                                            <table id="refund_docs_tbl" class="table table-striped" style="width:100%">
                                                <thead>
                                                <tr>
                                                    <th>Revision No</th>
                                                    <th>Document ID</th>
                                                    <th>File Name</th>
                                                    <th>Uploaded By</th>
                                                    <th>Uploaded Date</th>
                                                    <th>Verified By</th>
                                                    <th>Verified Date</th>
                                                    <th>Verify Document</th>
                                                    <th>Comment</th>
                                                    <th width="5%"></th>
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
                        <table id="polTaxesList" class="table table-striped" style="width:100%">
                            <thead>
                            <tr>
                                <th>Trans Code</th>
                                <th>Levy Rate</th>
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
                <div role="tabpanel" class="tab-pane fade" id="tab_content25"
                     aria-labelledby="home-tab">
                    <button class="btn btn-success btn btn-info" id="btn-add-new-ips">New
                    </button>
                    <div class="card-box table-responsive">
                        <table id="benefeciary_tbl" class="table table-striped" style="width:100%">
                            <thead>
                            <tr>
                                <th>Name</th>
                                <th>Relationship</th>
                                <th>Share(%)</th>
                                <th>Identification</th>
                                <th width="5%"></th>
                                <th width="5%"></th>
                            </tr>
                            </thead>
                        </table>
                    </div>
                </div>
                <div role="tabpanel" class="tab-pane fade" id="tab_dependents"
                     aria-labelledby="home-tab">
                    <button class="btn btn-success btn btn-info" id="btn-add-new-dps">New
                    </button>
                    <div class="card-box table-responsive">
                        <table id="dps_tbl" class="table table-striped" style="width:100%">
                            <thead>
                            <tr>
                                <th>Full Name</th>
                                <th>Relationship</th>
                                <th> </th>
                                <th>Identification</th>
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
                <%--                </button>--%>
                <%--                    <button class="btn btn-danger btn btn-info" id="btn-del-questionnaire" >Remove Questionnaire--%>
                <%--                    </button>--%>

                <%--                    <button class="btn btn-success btn btn-info" id="btn-edit-questionnaire">Edit Questionnaire--%>
                <%--                    </button>--%>
                <%--                    <div class="card-box table-responsive">--%>
                <%--                        <table id="questionnaire_tbl" class="table table-striped" style="width:100%">--%>
                <%--                            <thead>--%>
                <%--                            <tr>--%>
                <%--                                <th>Question</th>--%>
                <%--                                <th>Answer</th>--%>
                <%--                            </tr>--%>
                <%--                            </thead>--%>
                <%--                        </table>--%>
                <%--                    </div>--%>
                <%--                </div>--%>
                <div role="tabpanel" class="tab-pane fade" id="tab_content28"
                     aria-labelledby="home-tab">
                    <div class="x_content">
                        <div class="card-box table-responsive">
                            <table id="receipts_tbl" class="table table-striped" style="width:100%">
                                <thead>
                                <tr>
                                    <th>No</th>
                                    <th>Date</th>
                                    <th>Dr/Cr</th>
                                    <th>Amount</th>
                                    <th>Allocate Amount</th>
                                    <th>Balance</th>
                                    <th>Commission Amount</th>
                                    <th>Whtx Amount</th>
                                    <th>SubAgent/Marketer Comm Amount</th>
                                </tr>
                                </thead>
                            </table>
                        </div>
                        <div class="x_title">
                            <h4>Allocation Details</h4>
                        </div>
                        <div class="cutom-container">
                            <table id="receiptalloc_tbl" class="table table-striped" style="width:100%">
                                <thead>
                                <tr>

                                    <th>Instalment No</th>
                                    <th>Premium Item</th>
                                    <th>Allocation Amount</th>
                                    <th>Comm Amount</th>
                                    <th>SubAgent/Marketer Comm Amount</th>
                                    <th>Paid To Date</th>
                                    <th width="5%"></th>
                                </tr>
                                </thead>
                            </table>
                        </div>
                    </div>
                </div>

                <div role="tabpanel" class="tab-pane fade" id="tab_content_27"
                     aria-labelledby="home-tab">
                    <button type="button" class="btn btn-info" data-toggle="modal" data-target="#newDependentModal"  id="btn-add-new-dps">
                        New Dependent
                    </button>
                    <div class="card-box table-responsive">
                        <table id="dependents_tbl" class="table table-striped" style="width:100%">
                            <thead>
                            <tr>
                                <th>Full Name</th>
                                <th>Family Relation</th>
                                <th> </th>
                            </tr>
                            </thead>
                        </table>
                    </div>
                </div>

                <!-- Modal -->
                <div class="modal fade" id="newDependentModal" tabindex="-1" role="dialog" aria-labelledby="newDependentModalLabel" aria-hidden="true">
                    <div class="modal-dialog" role="document">
                        <form id="dependentForm" method="post" >
                            <div class="modal-content">
                                <div class="modal-header">
                                    <h5 class="modal-title">Add New Dependent</h5>
                                    <button type="button" class="close" data-dismiss="modal"><span>&times;</span></button>
                                </div>
                                <div class="modal-body">
                                    <!-- Common -->
                                    <div class="form-group">
                                        <label for="depType">Relationship Type</label>
                                        <select class="form-control" name="depType" id="depType" required>
                                            <option selected disabled>-- Select --</option>
                                            <option value="child">Child</option>
                                            <option value="spouse">Spouse</option>
                                            <option value="parent">Parent</option>
                                            <option value="efm">EFM</option>
                                            <option value="main_member">Main Member</option>
                                        </select>
                                    </div>

                                    <!-- Conditional Fields -->
                                    <div id="childSpouseFields" style="display:none;">
                                        <div class="form-group">
                                            <label>Initials</label>
                                            <input type="text" class="form-control" name="initials">
                                        </div>
                                        <div class="form-group">
                                            <label>First Name</label>
                                            <input type="text" class="form-control" name="firstName">
                                        </div>
                                        <div class="form-group">
                                            <label>Surname</label>
                                            <input type="text" class="form-control" name="surname">
                                        </div>
                                        <div class="form-group">
                                            <label>Date of Birth</label>
                                            <input type="date" class="form-control" name="dob">
                                        </div>
                                        <div class="form-group">
                                            <label>ID Type</label>
                                            <input type="number" class="form-control" name="idType">
                                        </div>
                                        <div class="form-group">
                                            <label>ID Number</label>
                                            <input type="text" class="form-control" name="idNumber">
                                        </div>
                                    </div>

                                    <div id="childOnlyFields" style="display:none;">
                                        <div class="form-group">
                                            <label>Gender</label>
                                            <select class="form-control" name="gender">
                                                <option value="">-- Select --</option>
                                                <option value="1">Male</option>
                                                <option value="2">Female</option>
                                            </select>
                                        </div>
                                        <div class="form-group">
                                            <label>Student</label>
                                            <select class="form-control" name="isStudent">
                                                <option value="">-- Select --</option>
                                                <option value="true">Yes</option>
                                                <option value="false">No</option>
                                            </select>
                                        </div>
                                        <div class="form-group">
                                            <label>Still Born</label>
                                            <select class="form-control" name="stillBorn">
                                                <option value="">-- Select --</option>
                                                <option value="true">Yes</option>
                                                <option value="false">No</option>
                                            </select>
                                        </div>
                                    </div>

                                </div>
                                <div class="modal-footer">
                                    <button type="submit" class="btn btn-primary" id="savepolicydepinfo">Save</button>
                                    <button type="button" class="btn btn-secondary" data-dismiss="modal">Cancel</button>
                                </div>
                            </div>
                        </form>
                    </div>
                </div>
                <script>
                    document.getElementById("depType").addEventListener("change", function () {
                        const depType = this.value;
                        const childSpouseFields = document.getElementById("childSpouseFields");
                        const childOnlyFields = document.getElementById("childOnlyFields");

                        // Hide all optional fields first
                        childSpouseFields.style.display = "none";
                        childOnlyFields.style.display = "none";

                        if (depType === "child") {
                            childSpouseFields.style.display = "block";
                            childOnlyFields.style.display = "block";
                            //} else if (depType === "spouse") {
                        }else {
                            childSpouseFields.style.display = "block";
                        }
                    });
                </script>
                <script>
                    $('#savepolicydepinfo').on('click', function () {
                        const formData = {
                            depType:$('select[name="depType"]').val(),
                            initials: $('input[name="initials"]').val(),
                            firstName: $('input[name="firstName"]').val(),
                            surname: $('input[name="surname"]').val(),
                            DOB: $('input[name="dob"]').val(),
                            identificationType: $('input[name="idType"]').val(),
                            identificationNumber: $('input[name="idNumber"]').val(),
                            isStudent: $('select[name="isStudent"]').val(),
                            stillBorn: $('select[name="stillBorn"]').val(),
                            gender: $('select[name="gender"]').val()


                        };

                        $.ajax({
                            url: '/createDependents',
                            method: 'POST',
                            contentType: 'application/json',
                            data: JSON.stringify(formData),
                            success: function (response) {
                                alert('Data saved successfully!');
                            },
                            error: function (xhr, status, error) {
                                alert('Error saving data');
                                console.error(error);
                            }
                        });
                    });
                </script>

                <div role="tabpanel" class="tab-pane fade" id="tab_content27"
                     aria-labelledby="home-tab">
                    <div class="card-box table-responsive">
                        <table id="benefit_tbl" class="table table-striped" style="width:100%">
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
                        <table id="installments_tbl" class="table table-striped" style="width:100%">
                            <thead>
                            <tr>
                                <th>Instalment No</th>
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
                     id="tab_pol_more_info" aria-labelledby="profile-tab">
                    <table id="policymiscinfolist" class="table table-striped" style="width:100%">
                        <thead>
                        <tr>
                            <th>Inflation Percentage</th>
                            <th>Bank branch </th>
                            <th>Account Number </th>
                            <th>Account Name</th>
                            <th>Strike Day</th>
                            <th>Bank ID</th>
                            <th>Bank Branch ID</th>
                            <th>Account Name</th>


                            <th width="5%"></th>
                        </tr>
                        </thead>
                    </table>
                    <div>
                        <table class="table table-striped" style="width:100%">
                            <tr>
                                <th colspan="2">Cover more info </th>
                            </tr>
                            <c:if test="${typeOfIntegration == 'ALAK_FPP'}">
                                <tr>
                                    <td>
                                        <label for="pol-cover_option_fpp" class="label-align col-md-5">
                                            Cover option <span class="required">*</span>
                                        </label>
                                    </td>
                                    <td>
                                        <select class="form-control" id="pol-cover_option_fpp" name="cover_option_fpp" required>
                                            <option selected disabled>Select Cover Option</option>
                                            <option value="1">Policyholder only</option>
                                            <option value="2">Policy holder + Spouse(1 max)</option>
                                            <option value="3">Policy holder plus Children(5 max)</option>
                                            <option value="4">Policy holder, Spouse(1 max) & Children(5 max)</option>
                                            <option value="5">Standalone Parent(4 max) or EFM(8 max)</option>
                                        </select>
                                    </td>
                                </tr>
                            </c:if>

                            <c:if test="${typeOfIntegration == 'ALAK_UP'}">
                                <tr>
                                    <th colspan="2"> Medical Questions </th>
                                </tr>
                                <tr>
                                    <td> Quiz 1 </td><td> </td>
                                    <td> Quiz 2</td>
                                    <td> Quiz 3 </td>
                                </tr>

                            </c:if>

                            <c:if test="${typeOfIntegration == 'ALAK_PA'}">
                                <tr>
                                    <td>
                                        <label for="pol-cover_option_pa" class="label-align col-md-5">
                                            Cover option <span class="required">*</span>
                                        </label>
                                    </td>
                                    <td>
                                        <select class="form-control" id="pol-cover_option_pa" name="cover_option_pa" required>
                                            <option selected disabled>Select Cover Option</option>
                                            <option value="1">Policyholder only</option>
                                            <option value="2">Policy holder + Spouse(1 max)</option>
                                            <option value="3">Policy holder plus Children(5 max) </option>
                                            <option value="4">Policy holder, Spouse(1 max) & Children(5 max) </option>
                                        </select>
                                    </td>
                                </tr>
                            </c:if>

                            <c:if test="${typeOfIntegration == 'ALAK_ENDOWMENT'}">
                                <tr><td colspan="2">Endowment plan integration active.</td></tr>
                            </c:if>

                            <thead>
                            <tr>
                                <th colspan="2">Policy Holder Banking Details</th>
                            </tr>
                            </thead>
                            <tbody>
                            <tr>
                                <th>Account Number</th>
                                <td><input type="text" class="form-control" name="accountNumber" placeholder="Enter account number"></td>
                            </tr>
                            <tr>
                                <th>Account Name</th>
                                <td><input type="text" class="form-control" name="accountName" placeholder="Enter account name"></td>
                            </tr>
                            <tr>
                                <th>Strike Day</th>
                                <td><input type="number" class="form-control" name="strikeDay" placeholder="Enter strike day"></td>
                            </tr>
                            <tr>
                                <th>Account Type</th>
                                <td><input type="text" class="form-control" name="accountType" placeholder="Enter account type"></td>
                            </tr>
                            <tr>
                                <th>Bank ID</th>
                                <td><input type="text" class="form-control" name="bankId" placeholder="Enter bank ID"></td>
                            </tr>
                            <tr>
                                <th>Bank Branch ID</th>
                                <td><input type="text" class="form-control" name="branchId" placeholder="Enter branch ID"></td>
                            </tr>
                            <tr>
                                <th colspan="2">Policy Additional Info</th>
                            </tr>
                            <tr>
                                <td>Inflation Percent</td>
                                <td><input type="number" class="form-control" name="inflationPercent" placeholder="Enter inflation %"></td>
                            </tr>
                            </tbody>
                        </table>
                        <button type="button" class="btn btn-primary" id="savepolicymiscinfo">Save</button>
                    </div>
                    <script>
                        $('#savepolicymiscinfo').on('click', function () {
                            const formData = {
                                accountNumber: $('input[name="accountNumber"]').val(),
                                accountName: $('input[name="accountName"]').val(),
                                strikeDay: $('input[name="strikeDay"]').val(),
                                accountType: $('input[name="accountType"]').val(),
                                bankId: $('input[name="bankId"]').val(),
                                branchId: $('input[name="branchId"]').val(),
                                inflationPercent: $('input[name="inflationPercent"]').val()
                            };

                            $.ajax({
                                url: 'savepolicymiscinfo',
                                method: 'POST',
                                contentType: 'application/json',
                                data: JSON.stringify(formData),
                                success: function (response) {
                                    alert('Data saved successfully!');
                                },
                                error: function (xhr, status, error) {
                                    alert('Error saving data');
                                    console.error(error);
                                }
                            });
                        });
                    </script>
                </div>
              <div role="tabpanel" class="tab-pane fade" id="tab_audit_trails" aria-labelledby="audit-trails">
                                  <table id="audit_trails_tbl" class="table table-striped table-bordered">
                                      <thead>
                                          <tr>
                                              <th>Checker Name</th>
                                              <th>Maker Name</th>
                                              <th>Time</th>
                                              <th>Maker Comments</th>
                                              <th>Checker Comments</th>
                                          </tr>
                                      </thead>
                                      <tbody></tbody>
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
                            <textarea class="form-control" rows="5" id="poli-remarks" name="polRemarks"></textarea>
                        </div>
                    </form>
                </div>
                <div role="tabpanel" class="tab-pane fade"
                     id="tab_content6" aria-labelledby="refund-remarks-tab">
                    <div class="x_panel">
                        <div class="x_title">
                            <h4>Refund Comments</h4>
                            <div class="clearfix"></div>
                        </div>
                        <div class="x_content">
                            <div class="item form-group">
                                <textarea class="form-control" rows="5" id="refund-comments-display" readonly></textarea>
                            </div>
                        </div>
                    </div>
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
<div class="col-md-9 col-sm-9  offset-md-3" id="hidden-buttons">
<%--       <input type="button" class="btn btn-info float-right"--%>
<%--              value="Dispatch" id="btn-dispatch-trans">--%>
       <input type="button" class="btn btn-info float-right"
              value="Assign" id="btn-assign-trans">
        <sec:authorize access="hasAnyAuthority('AUTHORIZE_POLICY')">
            <input type="button" class="btn btn-info float-right"
                   value="Authorise" id="btn-auth-policy">
        </sec:authorize>

    <sec:authorize access="hasAnyAuthority('PROPOSAL_CONVERSION')">
        <input type="button" class="btn btn-info float-right"
               value="Reject" id="btn-unconvert-policy"
        >
    </sec:authorize>
        <sec:authorize access="hasAnyAuthority('PROPOSAL_CONVERSION')">
            <input type="button" class="btn btn-info float-right"
                   value="Convert" id="btn-convert-policy"
            >
        </sec:authorize>
        <sec:authorize access="hasAnyAuthority('MAKE_POLICY_READY')">
            <input type="button" class="btn btn-info float-right"
                   value="Submit" id="btn-make-ready-policy"
                   style="display:none">
        </sec:authorize>

        <sec:authorize access="hasAnyAuthority('SAVE_POLICY')">
            <span class="spinner-border spinner-border-sm d-none ml-2" role="status"></span>
            <input type="button" class="btn btn-info float-right"
                   value="Save Draft" id="btn-add-policy"
                   style="display:none">
        </sec:authorize>

<%--    <sec:authorize access="hasAnyAuthority('AUTHORIZE_POLICY')">--%>
<%--        <c:if test="${!isMaker}">--%>
<%--            <input type="button" class="btn btn-info float-right"--%>
<%--                   value="Assign" id="btn-assign-trans2">--%>
<%--        </c:if>--%>
<%--    </sec:authorize>--%>
    <sec:authorize access="hasAnyAuthority('MAKE_POLICY_READY')">
        <input type="button" class="btn btn-info float-right"
               value="Submit" id="btn-cancel-policy"
               style="display:none">
    </sec:authorize>
<sec:authorize access="hasAnyAuthority('MAKE_POLICY_READY')">
        <button id="btn-comment-policy" class="btn btn-info float-right" style="display: none;" data-toggle="modal" data-target="#authcommentModal">Resubmission Comments</button>
    </sec:authorize>

    </div>
    <div class="row">
        <br>
    </div>
<!-- Document Comment Modal-->
<div class="modal fade" id="docCommentModal" tabindex="-1" role="dialog" aria-labelledby="docCommentModalLabel" aria-hidden="true">
    <div class="modal-dialog" role="document">
        <div class="modal-content">
            <div class="modal-header">
                <h5 class="modal-title" id="docCommentModalLabel">Document Comments</h5>
                <button type="button" class="close" data-dismiss="modal" aria-label="Close">
                    <span aria-hidden="true">&times;</span>
                </button>
            </div>
            <div class="modal-body">
                <form id="docCommentForm">
                    <input type="hidden" id="commentDocId">
                    <div class="form-group">
                        <label for="docCommentText">Comments</label>
                        <textarea class="form-control" id="docCommentText" rows="4"></textarea>
                    </div>
                </form>
            </div>
            <div class="modal-footer">
                <button type="button" class="btn btn-secondary" data-dismiss="modal">Close</button>
                <button type="button" class="btn btn-primary" id="saveDocCommentBtn">Save</button>
            </div>
        </div>
    </div>
</div>
<!-- Comment Modal -->
<div id="authcommentModal" class="modal fade" tabindex="-1" role="dialog">
    <div class="modal-dialog" role="document">
        <div class="modal-content">
            <div class="modal-header">
                <h5 class="modal-title">Add Comment</h5>
                <button type="button" class="close" data-dismiss="modal" aria-label="Close">
                    <span aria-hidden="true">&times;</span>
                </button>
            </div>
            <div class="modal-body">
                <form id="comment-form">
                    <div class="form-group">
                        <label for="policyComment">Comment <span class="text-danger">*</span></label>
                        <textarea id="policyComment" class="form-control" placeholder="Enter your comment"></textarea>
                    </div>
                </form>
            </div>
            <div class="modal-footer">
                <button type="button" class="btn btn-secondary" data-dismiss="modal">Cancel</button>
                <button type="button" id="saveCommentButton" class="btn btn-primary">Save Comment</button>
            </div>
        </div>
    </div>
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
                <form id="approval_form">
                    <div class="form-group">
                <p>Are you sure you want to approve this task?</p>
                <input type="hidden" id="approvalTaskId">
                    </div>
            </form>
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
    <<div class="modal-dialog" role="document">
    <div class="modal-content">
        <div class="modal-header">
            <h5 class="modal-title">Reject Task</h5>
            <button type="button" class="close" data-dismiss="modal" aria-label="Close">
                <span aria-hidden="true">&times;</span>
            </button>
        </div>
        <div class="modal-body">
            <form id="rejection-form">
                <div class="form-group">
                    <label>Select Rejection Reason(s) <span class="text-danger">*</span></label>
                    <div id="rejection-reasons-list">
                        <!-- Rejection reasons will be dynamically inserted here -->
                    </div>
                </div>
                <div class="form-group">
                    <label for="rejectionReason">Additional Comments <span class="text-danger">*</span></label>
                    <textarea id="rejectionReason" class="form-control" placeholder="Enter additional comments"></textarea>
                </div>
            </form>
        </div>
        <div class="modal-footer">
            <button type="button" class="btn btn-secondary" data-dismiss="modal">Close</button>
            <button type="button" id="rejectConfirmButton" class="btn btn-primary">Submit</button>
        </div>
    </div>
</div>
</div>
</div>
<script>

    var loadEligibleCheckers = function (searchParam) {
        $.ajax({
            url: SERVLET_CONTEXT + '/protected/home/getEligibleCheckers', // Endpoint for getting eligible checkers
            type: 'GET',
            data: {permissionName: 'AUTHORIZE_POLICY', searchParam: searchParam},
            success: function (response) {
                // Populate checkboxes with eligible checkers
                $("#checkersList tbody").each(function () {
                    $(this).remove();
                });
                for (var res in response) {
                    var absaNo = response[res].absaNo !== null ? response[res].absaNo : '';
                    var markup = "<tr>" +
                        // "<td><input type='hidden' id='checker-id'></td>" +
                        "<td><input type='checkbox'  class='checker-checkbox' value='" + response[res].id + "' id='" + response[res].id + "'></td><td>" + response[res].username + "</td><td>" + absaNo + "</td>" +
                        "</tr>";
                    $('#checkersList').append(markup)

                }
                $('#assignCheckerModal').modal({
                    backdrop: 'static',
                    keyboard: true
                });
            },
            error: function (jqXHR, textStatus, errorThrown) {
                Swal.fire({
                    title: 'Error',
                    text: jqXHR.responseText,
                    icon: 'error'
                });
            }
        });
    }
    $("#btn-assign-trans2").click(function () {
        loadEligibleCheckers('');

    });


    $('#saveDocCommentBtn').click(function() {
        const rdId = $('#commentDocId').val();
        const comment = $('#docCommentText').val().trim();

        $.ajax({
            url: 'saveRiskDocComment',
            type: 'GET',
            contentType: 'application/x-www-form-urlencoded; charset=UTF-8',
            data: {
                rdId: rdId,
                comments: comment
            },
            success: function() {
                $('#docCommentModal').modal('hide');
                Swal.fire({
                    title: 'Success',
                    text: 'Comment saved successfully',
                    icon: 'success',
                    timer: 2000,
                    showConfirmButton: false
                }).then(() => {
                    // Refresh the table
                    if (window.refreshRiskDocsTable) {
                        window.refreshRiskDocsTable();
                    }

                    // Populate policy details
                    saveRiskDocsList();
                });
            },
            error: function(jqXHR) {
                Swal.fire({
                    title: 'Error',
                    text: jqXHR.responseText || 'Failed to save comment',
                    icon: 'error'
                });
            }
        });
    });
    const currentUserId = "<%= request.getAttribute("currentUserId") %>";
    const isPolicyCreator = <%= request.getAttribute("isPolicyCreator") %>;
    const isMaker = full.createdBy === currentUserId || isPolicyCreator;

</script>

<jsp:include page="modals/riskmodals.jsp"></jsp:include>
<jsp:include page="modals/endorseriskmodals.jsp"></jsp:include>
<jsp:include page="../modals/prospectmodals.jsp"></jsp:include>