<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="sec" uri="http://www.springframework.org/security/tags" %>
<script type="text/javascript" src="<c:url value="/js/modules/utils/select2builder.js"/>"></script>
<script type="text/javascript" src="<c:url value="/libs/rivets/rivets.js"/>"></script>
<script type="text/javascript">
    var polCode = ${policyId};
    var polStatus = '${policyStatus}';
    <c:if test="${not empty mckPolId}">
    var mckPolId = "<c:out value='${mckPolId}'/>";
    </c:if>
    <c:if test="${not empty mckPolCheckerCode}">
        var mckPolCheckerCode = "<c:out value='${mckPolCheckerCode}'/>";
    </c:if>
    console.log("polCode:", polCode);
    console.log("mckPolId:", mckPolId);
    console.log("mckPolCheckerCode:", mckPolCheckerCode);;
</script>
<script type="text/javascript" src="<c:url value="/js/modules/uwtrans/checkerlife.js"/>"></script>


<div class="x_panel">
    <div class="x_title">
        <h2 ><i class="fa fa-bars"></i>View Proposal Details</h2>

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
                    <p  id="pol-bin-type"></p>
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
                <label for="brn-id" class="col-md-5 label-align">Date
                    From</label>

                <div class="col-md-7 col-xs-12">
                    <div >
                        <p  name="wefDate"
                               id="from-date"></p>

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
                    <p id="pol-frequency">
                    </p>
                </div>
            </div>

            <div class="item form-group form-required">
                <label for="pm-mode-frm" class="label-align col-md-5">
                    Payment Mode<span class="required">*</span></label>
                <div class="col-md-7 col-xs-12">
                    <div id="edit-payment-mode">
                        <input type="hidden" id="pm-id" name="paymentId"/>
                        <input type="hidden" id="pm-name">

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
                    <div id="cover-to-date">
                        <p name="wetDate"
                               id="wet-date"></p>

                    </div>
                </div>
            </div>


            <div class="item form-group form-required">
                <label for="client-pol-no" class="label-align col-md-5">
                    Insurer Policy number<span class="required">*</span></label>
                <div class="col-md-7 col-xs-12">
                    <p name="clientPolNo" id="client-pol-no"
                           ></p>
                </div>
            </div>
            <div class="item form-group form-required">
                <label for="curr-frm" class="label-align col-md-5">
                    Currency<span class="required">*</span></label>
                <div class="col-md-7 col-xs-12">
                    <div id="edit-currency">
                        <input type="hidden" id="cur-id" name="currencyId"/>
                        <input type="hidden" id="cur-name">

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
                                                                                role="tab" id="profile-tab"
                                                                                data-toggle="tab"
                                                                                aria-expanded="false">Taxes</a>
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
                <li role="presentation" class="hide-details" id="est-ben"><a href="#tab_content27" role="tab"
                                                                             id="benefits-tabb" data-toggle="tab"
                                                                             aria-controls="profile"
                                                                             aria-expanded="false">Estimated
                    Benefits</a>
                </li>
                <li role="presentation" class="hide-details" id="instal-schedule"><a href="#tab_content207" role="tab"
                                                                                     id="installments-tabb"
                                                                                     data-toggle="tab"
                                                                                     aria-controls="profile"
                                                                                     aria-expanded="false">Installments
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
                <li role="presentation" class="hide-details" id="checks-tab"><a href="#tab_pol_checks"
                                                                                role="tab" id="pol-checks-tab"
                                                                                data-toggle="tab"
                                                                                aria-expanded="false">Checks</a>
                </li>
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
                    <button class="btn btn-success btn btn-info" id="btn-endors-risk" style="display:none">Endorse
                    </button>

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

                            <div class="item form-group form-required premium-disp">
                                <label for="premium-amt" class="label-align col-md-5">
                                    Premium</label>
                                <div class="col-md-7 col-xs-12">
                                    <input type="number" class="form-control float-right" align="right" id="premium-amt"
                                           name="premium">
                                </div>
                            </div>
                            <div class="item form-group form-required sumassured-disp">
                                <label for="sumassured-amt" class="label-align col-md-5">
                                    Sum Assured</label>
                                <div class="col-md-7 col-xs-12">
                                    <input type="number" class="form-control float-right" id="sumassured-amt"
                                           name="sumInsured">
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
                                        <%--<li role="presentation" class=""><a href="#tab_content23" role="tab"--%>
                                        <%--id="schedules-tabb" data-toggle="tab"--%>
                                        <%--aria-controls="profile"--%>
                                        <%--aria-expanded="false" disabled="true">Schedules</a>--%>
                                        <%--</li>--%>
                                        <li role="presentation" class=""><a href="#tab_content24" role="tab"
                                                                            id="docs-tabb" data-toggle="tab"
                                                                            aria-controls="profile"
                                                                            aria-expanded="false">Risk Documents</a>
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

                                            <input type="hidden" id="insured-client-age">
                                            <div class="card-box table-responsive">
                                                <table id="section_tbl" class="table table-striped" style="width:100%">
                                                    <thead>
                                                    <tr>
                                                        <th>Premium Item</th>
                                                        <th>Rate</th>
                                                        <th>Calc Prem</th>
                                                        <th>Premium</th>
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
                                                        <th>File Verifier</th>
                                                        <th width="5%"></th>
                                                    </tr>
                                                    </thead>
                                                </table>
                                            </div>

                                        </div>

                                        <div role="tabpanel" class="tab-pane fade" id="tab_content23"
                                             aria-labelledby="profile-tab">

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
                                            <table id="risk_docs_tbl" class="table table-striped" style="width:100%">
                                                <thead>
                                                <tr>
                                                    <th>Revision No</th>
                                                    <th>Document ID</th>
                                                    <th>Document Desc</th>
                                                    <th>File Name</th>
                                                    <th>File Verifier</th>
                                                </tr>
                                                </thead>
                                            </table>

                                        </div>
                                        <div role="tabpanel" class="tab-pane fade" id="tab_content22"
                                             aria-labelledby="profile-tab">

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
                                <table id="section_form_tbl" class="table table-striped" style="width:100%">
                                    <thead>
                                    <tr>
                                        <th>Premium Item</th>
                                        <th>Age</th>
                                        <th>Rate</th>
                                        <th>Div Factor</th>
                                        <th>Free Limit</th>
                                    </tr>
                                    </thead>
                                </table>
                            </div>
                        </div>
                    </div>
                </div>
                <div role="tabpanel" class="tab-pane fade"
                     id="tab_content2" aria-labelledby="profile-tab">
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
                            </tr>
                            </thead>
                        </table>
                    </div>
                </div>
                <div role="tabpanel" class="tab-pane fade" id="tab_content25"
                     aria-labelledby="home-tab">
                    <div class="card-box table-responsive">
                        <table id="benefeciary_tbl" class="table table-striped" style="width:100%">
                            <thead>
                            <tr>
                                <th>Name</th>
                                <th>Relationship</th>
                                <th>Share(%)</th>
                                <th>ID No/Reg No</th>
                            </tr>
                            </thead>
                        </table>
                    </div>
                </div>

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
                                    <th>Allocation Amount</th>
                                    <th>Comm Amount</th>
                                    <th>Paid To Date</th>
                                </tr>
                                </thead>
                            </table>
                        </div>
                    </div>
                </div>
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

                    <table id="polChecksList" class="table table-striped" style="width:100%">
                        <thead>
                        <tr>
                            <th>Check Name</th>
                            <th>Approved</th>
                            <th>Approved By</th>
                            <th>Approved On</th>
                        </tr>
                        </thead>
                    </table>
                </div>
                <div role="tabpanel" class="tab-pane fade"
                     id="tab_content3" aria-labelledby="comm-rates-tab">
                    <table id="polclausesList" class="table table-striped" style="width:100%">
                        <thead>
                        <tr>

                            <th>Clause Heading</th>
                            <th>Clause Type</th>
                            <th>Editable?</th>
                            <th>Clause Wording</th>

                        </tr>
                        </thead>
                    </table>
                </div>
                <div role="tabpanel" class="tab-pane fade"
                     id="tab_content4" aria-labelledby="end-remarks-tab">

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
<div id="quot-panel">
    <sec:authorize access="hasAnyAuthority('AUTHORIZE_POLICY')">
        <input type="button" class="btn btn-primary btn btn-danger float-right"
               value="Reject Task" id="btn-reject-quot">
    </sec:authorize>

    <sec:authorize access="hasAnyAuthority('AUTHORIZE_POLICY')">
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

<jsp:include page="modals/riskmodals.jsp"></jsp:include>
<jsp:include page="modals/endorseriskmodals.jsp"></jsp:include>
<jsp:include page="../modals/prospectmodals.jsp"></jsp:include>