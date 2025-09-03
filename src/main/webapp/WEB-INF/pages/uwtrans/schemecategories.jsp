<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="sec" uri="http://www.springframework.org/security/tags" %>
<script type="text/javascript" src="<c:url value="/js/modules/utils/select2builder.js"/>"></script>
<script type="text/javascript" src="<c:url value="/libs/rivets/rivets.js"/>"></script>
<script type="text/javascript" src="<c:url value="/js/modules/uwtrans/categorytrans.js"/>"></script>
<script type="text/javascript">
    var polCode = ${policyId};
    var polStatus = ${policyStatus}+"";
</script>
<div class="x_panel">
    <sec:authorize access="hasAnyAuthority('ACCESS_POLICY_REPORTS')">
        <input type="button" class="btn btn-info float-right"
               value="Reports" id="btn-uw-reports" data-toggle="modal" data-target="#reportsModal"
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
    <sec:authorize access="hasAnyAuthority('MAKE_POLICY_READY')">
        <input type="button" class="btn btn-info float-right"
               value="Make Trans Ready" id="btn-make-ready-policy"
               style="display:none">
    </sec:authorize>
    <sec:authorize access="hasAnyAuthority('MAKE_POLICY_READY')">
        <input type="button" class="btn btn-info float-right"
               value="Unsubmit" id="btn-undo-make-ready"
               style="display:none">
    </sec:authorize>

    <div class="x_title">
        <h4 id="wkflow-task-name"><i class="fa fa-bars"></i> Medical Policy Details</h4>
        <div class="clearfix"></div>

        <ul class="nav navbar-right panel_toolbox">
            <li><a class="collapse-link"><i class="fa fa-chevron-up"></i></a>
            </li>
        </ul>
    </div>
    <div class="col-md-1 col-xs-12 edit-client">
        <button class="btn btn-success btn btn-info float-right" id="btn-edit-policy">Edit</button>
    </div>
    <div class="x_content">

        <form id="policy-form" class="form-horizontal form-label-left">
            <input type="hidden" id="med-pol-bin-code">
            <input type="hidden" id="med-pol-binder-det-pk">
            <input type="hidden" id="pol-buss-type">
            <input type="hidden" id="pol-bin-type">
            <input type="hidden" id="policy-card-id">
            <input type="hidden" id="client-id">
            <input type="hidden" id="client-f-name">
            <input type="hidden" id="client-other-name">
            <input type="hidden" id="pol-ins-comp">
            <input type="hidden" id="pol-prod-name">
            <input type="hidden" id="pol-interface-type">
            <input type="hidden" id="pol-medicalCover-Type">
            <input type="hidden" id="pm-id">
            <input type="hidden" id="pm-name">
            <input type="hidden" id="cur-id">
            <input type="hidden" id="cur-name">
            <input type="hidden" id="brn-id">
            <input type="hidden" id="brn-name">
            <input type="hidden" id="bin-code">
            <input type="hidden" id="pol-agent-id">
            <input type="hidden" id="product-id">
            <input type="hidden" id="pol-create-date">
            <div class="item form-group form-required">
               
            <div class="col-md-6 col-xs-12">
                <label for="pol-no" class="label-align col-md-5">
                    Policy No</label>
                <div class="col-md-7 col-xs-12">
                    <p class="form-control-static" id="pol-no"> </p>
                </div>
            </div>
            <div class="col-md-6 col-xs-12">
                    <label for="pol-rev-no" class="label-align col-md-5">
                        Endorsement Number</label>
                    <div class="col-md-7 col-xs-12">
                        <p class="form-control-static" id="pol-rev-no"> </p>
                    </div>

            </div>
        </div>
            <div class="item form-group form-required">
                <div class="col-md-6 col-xs-12">
                    <label for="client-info" class="label-align col-md-5">
                        Client</label>
                    <div class="col-md-7 col-xs-12">
                        <p class="form-control-static" id="client-info"> </p>
                    </div>
                </div>
                <div class="col-md-6 col-xs-12">
                    <label for="binder-info" class="label-align col-md-5">
                       Binder</label>
                    <div class="col-md-7 col-xs-12">
                        <p class="form-control-static" id="binder-info"> </p>
                    </div>
                </div>
            </div>
            <div class="item form-group form-required">
                <div class="col-md-6 col-xs-12">
                    <label for="from-date" class="label-align col-md-5">
                        WEF</label>
                    <div class="col-md-7 col-xs-12">
                        <p class="form-control-static" id="from-date"> </p>
                    </div>
                </div>
                <div class="col-md-6 col-xs-12">
                    <label for="wet-date" class="label-align col-md-5">
                        WET</label>
                    <div class="col-md-7 col-xs-12">
                        <p class="form-control-static" id="wet-date"> </p>
                    </div>
                </div>
            </div>
            <div class="item form-group form-required">
                <div class="col-md-6 col-xs-12">
                    <label for="pol-sum-insured" class="label-align col-md-5">
                        Sum Insured</label>
                    <div class="col-md-7 col-xs-12">
                        <p class="form-control-static" id="pol-sum-insured"> </p>
                    </div>
                </div>
                <div class="col-md-6 col-xs-12">
                    <label for="pol-premium" class="label-align col-md-5">
                        Basic Premium</label>
                    <div class="col-md-7 col-xs-12">

                        <p class="form-control-static" id="pol-premium"> </p>
                    </div>
                </div>
            </div>
            <div class="item form-group form-required">
                <div class="col-md-6 col-xs-12">
                    <label for="pol-basic-prem" class="label-align col-md-5">
                        Gross Prem</label>
                    <div class="col-md-7 col-xs-12">
                        <p class="form-control-static" id="pol-basic-prem"> </p>
                    </div>
                </div>
                <div class="col-md-6 col-xs-12">
                    <label for="pol-net-prem" class="label-align col-md-5">
                        Net Prem</label>
                    <div class="col-md-7 col-xs-12">
                        <p class="form-control-static" id="pol-net-prem"> </p>
                    </div>
                </div>
            </div>
            <div class="item form-group form-required">
                <div class="col-md-6 col-xs-12">
                    <label for="pol-tl" class="label-align col-md-5">
                        Training Levy</label>
                    <div class="col-md-7 col-xs-12">
                        <p class="form-control-static" id="pol-tl"> </p>
                    </div>
                </div>
                <div class="col-md-6 col-xs-12">
                    <label for="pol-phcf" class="label-align col-md-5">
                        PHCF Fund</label>
                    <div class="col-md-7 col-xs-12">
                        <p class="form-control-static" id="pol-phcf"> </p>
                    </div>
                </div>
            </div>
            <div class="item form-group form-required">
                <div class="col-md-6 col-xs-12">
                    <label for="pol-sd" class="label-align col-md-5">
                        Stamp Duty</label>
                    <div class="col-md-7 col-xs-12">
                        <p class="form-control-static" id="pol-sd"> </p>
                    </div>
                </div>
                <div class="col-md-6 col-xs-12">
                    <label for="pol-whtx" class="label-align col-md-5">
                        WHTX Tax</label>
                    <div class="col-md-7 col-xs-12">
                        <p class="form-control-static" id="pol-whtx"> </p>
                    </div>
                </div>
            </div>
            <div class="item form-group form-required">
                <div class="col-md-6 col-xs-12">
                    <label for="pol-extras" class="label-align col-md-5">
                        Extras</label>
                    <div class="col-md-7 col-xs-12">
                        <p class="form-control-static" id="pol-extras"> </p>
                    </div>
                </div>
                <div class="col-md-6 col-xs-12">
                    <label for="pol-tran-type-disp" class="label-align col-md-5">
                        Transaction Type</label>
                    <div class="col-md-7 col-xs-12">
                        <p class="form-control-static" id="pol-tran-type-disp"> </p>
                    </div>
                </div>
            </div>
            <div class="item form-group form-required">
                <div class="col-md-6 col-xs-12">
                    <label for="pol-comm-amt" class="label-align col-md-5">
                        Commission Amt</label>
                    <div class="col-md-7 col-xs-12">
                        <p class="form-control-static" id="pol-comm-amt"> </p>
                    </div>
                </div>
                <div class="col-md-6 col-xs-12">
                    <label for="client-policyno" class="label-align col-md-5">
                        Insurer Policy number</label>
                    <div class="col-md-7 col-xs-12">
                        <p class="form-control-static" id="client-policyno"> </p>
                    </div>
                </div>
            </div>
            <div class="item form-group form-required">
                <div class="col-md-6 col-xs-12">
                    <label for="pol-status" class="label-align col-md-5">
                        Policy Status</label>
                    <div class="col-md-7 col-xs-12">
                        <p class="form-control-static" id="pol-status"> </p>
                    </div>
                </div>
                <div class="col-md-6 col-xs-12">
                    <label for="pol-fap" class="label-align col-md-5">
                        Future Annual Premium</label>
                    <div class="col-md-7 col-xs-12">
                        <p class="form-control-static" id="pol-fap"> </p>
                    </div>
                </div>
            </div>
            <div class="item form-group form-required">
                <div class="col-md-6 col-xs-12">
                    <label for="pol-freq-pay" class="label-align col-md-5">
                        Payment Frequency</label>
                    <div class="col-md-7 col-xs-12">
                        <p class="form-control-static" id="pol-freq-pay"> </p>
                    </div>
                </div>
                <div class="col-md-6 col-xs-12">
                    <label for="pol-ren-date" class="label-align col-md-5">
                        Renewal Date</label>
                    <div class="col-md-7 col-xs-12">
                        <p class="form-control-static" id="pol-ren-date"> </p>
                    </div>
                </div>
            </div>
            <div class="item form-group form-required">

                <div class="item form-group pol-cardtype">
                    <div class="col-md-6 col-xs-12">
                        <label for="pol-card-type" class="label-align col-md-5">
                            Card Type</label>
                        <div class="col-md-7 col-xs-12">
                            <p class="form-control-static" id="pol-card-type"> </p>
                        </div>
                    </div>
                    <div class="col-md-6 col-xs-12">
                        <label for="medicalCoverTyp-info" class="label-align col-md-5">
                            Type</label>
                        <div class="col-md-7 col-xs-12">
                            <div id="display-medicalCoverTyp">
                                <p class="form-control-static" id="medicalCoverTyp-info"> </p>
                            </div>
                        </div>
                    </div>
                </div>

            </div>
            <div class="item form-group form-required pol-cardtype">
                <div class="col-md-6 col-xs-12">
                    <label for="pol-issue-card-fee" class="label-align col-md-5">
                        Issue Card Fee</label>
                    <div class="col-md-7 col-xs-12">
                        <p class="form-control-static" id="pol-issue-card-fee"> </p>
                    </div>
                </div>
                <div class="item form-group pol-cardtype">
                    <div class="col-md-6 col-xs-12">
                        <label for="pol-service-charge" class="label-align col-md-5">
                            Service Charge</label>
                        <div class="col-md-7 col-xs-12">
                            <p class="form-control-static" id="pol-service-charge"> </p>
                        </div>
                    </div>
                </div>
            </div>
            <div class="item form-group form-required pol-cardtype">
                <div class="col-md-6 col-xs-12">
                    <label for="pol-vat-amount" class="label-align col-md-5">
                        VAT Amount</label>
                    <div class="col-md-7 col-xs-12">
                        <p class="form-control-static" id="pol-vat-amount"> </p>
                    </div>
                </div>
                <div class="item form-group pol-cardtype">
                    <div class="col-md-6 col-xs-12">
                        <label for="pol-reissue-card-fee" class="label-align col-md-5">
                            Re-Issue Card Fee</label>
                        <div class="col-md-7 col-xs-12">
                            <p class="form-control-static" id="pol-reissue-card-fee"> </p>
                        </div>
                    </div>
                </div>
            </div>
        </form>
        <hr>
        <div class="x_content">
                <div class="" role="tabpanel" data-example-id="togglable-tabs" id="med-cards-tabs">
                    <ul id="myTab-med" class="nav nav-tabs bar_tabs" role="tablist">
                        <li role="presentation" class="active"><a href="#tab_content1-med"
                                                                  id="home-tab-med" role="tab" data-toggle="tab" aria-expanded="true">Card Management</a>
                        </li>
                        <li role="presentation" class="" id="workflow-dg"><a href="#tab_content5-med"
                                                                          role="tab" id="workflow-tab-med" data-toggle="tab" aria-expanded="false">Process Diagram</a>
                        </li>
                        </ul>
                    <div id="myTabContent-med" class="tab-content">
                        <div role="tabpanel" class="tab-pane active"
                             id="tab_content1-med" aria-labelledby="home-tab-med">
                <input type="hidden" id="replaceCardId">
                <div class="card-box table-responsive">
                <table id="searchMembersTbl" class="table table-striped" style="width:100%">
                    <thead>
                    <tr>
                        <th>Card No.</th>
                        <th>Member No.</th>
                        <th>Member</th>
                        <th>WEF</th>
                        <th>WET</th>
                        <th>DOB</th>
                        <th>Status</th>
                    </tr>
                    </thead>
                </table>
                    </div>
                <input type="button" class="btn btn-info float-right"
                       value="Replace" id="btn-replace-cards">
                      </div>
                        <div role="tabpanel" class="tab-pane fade"
                             id="tab_content5-med" aria-labelledby="workflow-tab">
                            <button class="btn btn-success btn btn-info btn-show-task-history">History</button>
                            <img  class="img-responsive img-rounded proc-diagram proc-main-diagram" src=""
                                 alt="Workflow Process Diagram">
                        </div>
                    </div>
                </div>
            <div class="" role="tabpanel" data-example-id="togglable-tabs" id="uw_tabs">
                <ul id="myTab" class="nav nav-tabs bar_tabs" role="tablist">
                    <li role="presentation" class="active"><a href="#tab_content1"
                                                              id="home-tab" role="tab" data-toggle="tab" aria-expanded="true">Category Details</a>
                    </li>
                    <li role="presentation" class="" id="show-taxes"><a href="#tab_content2"
                                                                        role="tab" id="profile-tab" data-toggle="tab" aria-expanded="false">Taxes</a>
                    </li>
                    <li role="presentation" class=""><a href="#tab_content2-4"
                                                        role="tab" id="profile-tab-4" data-toggle="tab" aria-expanded="false">Clauses</a>
                    </li>
                    <li role="presentation" class="" id="show-policy-members"><a href="#tab_pol-members"
                                                                        role="tab" id="pol-members-tab" data-toggle="tab" aria-expanded="false">Members</a>
                    </li>
                    <li role="presentation" class="" id="show-self-fund"><a href="#tab_content-selffund"
                                                                        role="tab" id="self-fund-tab" data-toggle="tab" aria-expanded="false">Self Fund</a>
                    </li>
                    <li role="presentation" class="" id="show-self-funds"><a href="#tab_content-receipts"
                                                                            role="tab" id="self-fund-tabs" data-toggle="tab" aria-expanded="false">Receipts</a>
                    </li>
                    <li role="presentation" class="" id="workflow"><a href="#tab_content5"
                                                                      role="tab" id="workflow-tab" data-toggle="tab" aria-expanded="false">Process Diagram</a>
                    </li>

                    <li role="presentation" class="" id="checks-tab"><a href="#tab_pol_checks"
                                                                        role="tab" id="pol-checks-tab" data-toggle="tab"
                                                                        aria-expanded="false">Checks</a>
                    </li>
                </ul>
       <div id="myTabContent" class="tab-content">
                    <div role="tabpanel" class="tab-pane active"
                         id="tab_content1" aria-labelledby="home-tab">
                        <div class="card-box table-responsive">
        <button class="btn btn-success btn btn-info float-left" id="btn-add-category">New</button>
        <input type="hidden" id="fam-category-pk">
        <input type="hidden" id="fam-category-bin-pk">
                        <div class="cutom-container">
        <table id="med-categories" class="table table-striped" style="width:100%">
            <thead>
            <tr>
                <th>Category</th>
                <th>Sht Desc</th>
                <th>Description</th>
                <th>Premium</th>
                <th>Status</th>
                <th width="5%"></th>
                <th width="5%"></th>
            </tr>
            </thead>
        </table>
                            </div>
</div>

<div class="x_panel">
    <div class="x_title">
        <h4>Famiy Details</h4>

        <ul class="nav navbar-right panel_toolbox">
            <li><a class="collapse-link"><i class="fa fa-chevron-up"></i></a>
            </li>
        </ul>
    </div>

    <div class="x_content">
        <div class="" role="tabpanel" data-example-id="togglable-tabs">
            <ul id="myTab-2" class="nav nav-tabs bar_tabs" role="tablist">
                <li role="presentation" class="active"><a href="#tab_content2-6"
                                                    role="tab" id="profile-tab-6" data-toggle="tab" aria-expanded="false">Rules</a>
                </li>
                <li role="presentation" class=""><a href="#tab_content2-2"
                                                    role="tab" id="profile-tab-2" data-toggle="tab" aria-expanded="false">Members</a>
                </li>
                <li role="presentation" class=""><a href="#tab_content2-3"
                                                    role="tab" id="profile-tab-3" data-toggle="tab" aria-expanded="false">Benefits</a>
                </li>

                <li role="presentation" class=""><a href="#tab_content2-5"
                                                    role="tab" id="profile-tab-5" data-toggle="tab" aria-expanded="false">Exclusions</a>
                </li>
                <li role="presentation" class=""><a href="#tab_content2-7"
                                                    role="tab" id="profile-tab-7" data-toggle="tab" aria-expanded="false">Loadings</a>
                </li>
                <li role="presentation" class=""><a href="#tab_content2-8"
                                                    role="tab" id="profile-tab-8" data-toggle="tab" aria-expanded="false">Panel Providers</a>
                </li>
                <li role="presentation" class=""><a href="#tab_content2-9"
                                                    role="tab" id="profile-tab-9" data-toggle="tab" aria-expanded="false">Bed Types</a>
                </li>

            </ul>
            <div id="myTabContent-1" class="tab-content">
                <div role="tabpanel" class="tab-pane active"
                     id="tab_content2-6" aria-labelledby="profile-tab-6">
                    <div class="card-box table-responsive">
                    <button class="btn btn-success btn btn-info float-left" id="btn-add-new-rule">New</button>
                    <table id="med-rules-tbl" class="table table-striped" style="width:100%">
                        <thead>
                        <tr>
                            <th>Rule ID</th>
                            <th>Rule Value</th>
                            <th>Rule Description</th>
                            <th width="5%"></th>
                            <th width="5%"></th>
                        </tr>
                        </thead>
                    </table>
                        </div>
                </div>
                <div role="tabpanel" class="tab-pane fade"
                     id="tab_content2-2" aria-labelledby="profile-tab-2">
                    <div class="card-box table-responsive">
                    <button class="btn btn-success btn btn-info float-left" id="btn-add-member">New</button>
                    <table id="med-members" class="table table-striped" style="width:100%">
                        <thead>
                        <tr>
                            <th>Member Name</th>
                            <th>Member No</th>
                            <th>Type</th>
                            <th>Gender</th>
                            <th>DOB</th>
                            <th>Principal</th>
                            <th>Age</th>
                            <th>Card No</th>
                            <th>Status</th>
                            <th>Premium</th>
                            <th  width="5%"></th>
                            <th  width="5%"></th>
                            <th width="5%"></th>
                            <th width="5%"></th>
                            <th width="5%"></th>
                        </tr>
                        </thead>
                    </table>
                        </div>
                    </div>
                <div role="tabpanel" class="tab-pane fade"
                     id="tab_content2-3" aria-labelledby="profile-tab-3">
                    <div class="card-box table-responsive">
                    <button class="btn btn-success btn btn-info" id="btn-add-new-benefit">New</button>
                    <input type="hidden" id="fund-Binder">

                    <table id="medCoversList" class="table table-striped" style="width:100%">
                        <thead>
                        <tr>
                            <th>Cover</th>
                            <th>Main Cover</th>
                            <th>Parent Cover</th>
                            <th>Prorated Full</th>
                            <th>Min Prem.</th>
                            <th>Limit Amt</th>
                            <th>Appl. At</th>
                            <th>Premium</th>
                            <th>Waiting Prd</th>
                            <th>Status</th>
                            <th width="5%"></th>
                            <th width="5%"></th>
                        </tr>
                        </thead>
                    </table>
                        </div>
                </div>
                <div role="tabpanel" class="tab-pane fade"
                     id="tab_content2-5" aria-labelledby="profile-tab-5">
                    <div class="card-box table-responsive">
                    <table id="exclusionsList" class="table table-striped" style="width:100%">
                        <thead>
                        <tr>
                            <th>Exclusion ID</th>
                            <th>Exclusion Desc</th>
                            <th>Cost Per Claim</th>
                            <th>Upper Limit</th>
                            <th>Waiting Days</th>
                            <th>Chronic</th>
                            <th width="5%"></th>
                            <th width="5%"></th>
                        </tr>
                        </thead>
                    </table>
                        </div>
                </div>
                <div role="tabpanel" class="tab-pane fade"
                     id="tab_content2-7" aria-labelledby="profile-tab-7">
                    <div class="card-box table-responsive">
                    <table id="loadingsList" class="table table-striped" style="width:100%">
                        <thead>
                        <tr>
                            <th>Ailment ID</th>
                            <th>Ailment Desc</th>
                            <th>Cost Per Claim</th>
                            <th>Upper Limit</th>
                            <th>Waiting Days</th>
                            <th>Chronic</th>
                            <th>Rate Type</th>
                            <th>Rate</th>
                            <th>Amount</th>
                            <th width="5%"></th>
                            <th width="5%"></th>
                        </tr>
                        </thead>
                    </table>
                        </div>
                    </div>

                <div role="tabpanel" class="tab-pane fade"
                     id="tab_content2-8" aria-labelledby="profile-tab-8">
                    <div class="card-box table-responsive">
                    <button class="btn btn-success btn btn-info" id="btn-add-new-panel">New</button>

                    <table id="providersList" class="table table-striped" style="width:100%">
                        <thead>
                        <tr>
                            <th>Name</th>
                            <th>Contact Person</th>
                            <th>Pin No</th>
                            <th>Tel No</th>
                            <th>Bank Branch</th>
                            <th>Account No</th>
                            <th>Payment Mode</th>
                            <th width="5%"></th>
                        </tr>
                        </thead>
                    </table>
                        </div>
                </div>
                <div role="tabpanel" class="tab-pane fade"
                     id="tab_content2-9" aria-labelledby="profile-tab-8">

                        <table id="med-bedtypes-tbl" class="table table-striped" style="width:100%" width="70%">
                            <thead>
                            <tr>
                                <th width="5%">Category</th>
                                <th width="5%">Bed Type</th>
                                <th width="5%">Override Bed Cost</th>
                                <th width="1%"></th>
                            </tr>
                            </thead>
                        </table>

                </div>

                </div>



        </div>
    </div>
        </div>
    </div>


                <div role="tabpanel" class="tab-pane fade"
                     id="tab_content2" aria-labelledby="profile-tab">
                    <div class="card-box table-responsive">
                    <button class="btn btn-success btn btn-info" id="btn-add-new-tax">New</button>

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
           <div role="tabpanel" class="tab-pane fade"
                id="tab_content5" aria-labelledby="workflow-tab">
               <button class="btn btn-success btn btn-info" id="btn-show-task-history">History</button>
               <img id="proc-main-diagram" class="img-responsive img-rounded proc-diagram" src=""
                    alt="Workflow Process Diagram">
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
                id="tab_content-receipts" aria-labelledby="profile-tab">
               <div class="card-box table-responsive">
               <table id="self_fund_receipts" class="table table-striped" style="width:100%">
                   <thead>
                   <tr>
                       <th>Receipt No</th>
                       <th>Receipt Date</th>
                       <th>Receipt Amount</th>
                       <th>Narration</th>
                       <th>Trans DC</th>
                       <th>Receipted By</th>
                       <th>Trans Type</th>
                   </tr>
                   </thead>
               </table>
                   </div>

           </div>
           <div role="tabpanel" class="tab-pane fade"
                id="tab_content2-4" aria-labelledby="profile-tab-4">
               <div class="card-box table-responsive">
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
           </div>
           <div role="tabpanel" class="tab-pane fade"
                id="tab_pol-members" aria-labelledby="profile-tab">
               <form id="member-upload-form" class="form-horizontal" enctype="multipart/form-data">
                   <input type="hidden" class="form-control" id="rates-bind-code" name="binder">
                   <%--<div class="col-md-10 col-xs-12 form-required">--%>

                       <%--<div class="col-md-5 col-xs-12">--%>
                           <%--<div class="input-group col-xs-12">--%>
                               <%--<input name="file" type="file" id="avatar" required>--%>
                           <%--</div>--%>
                       <%--</div>--%>


                       <%--<div class="col-md-3 col-xs-12">--%>
                           <%--<input type="submit" class="btn btn-success btn-sm float-left" id="btn-upload-members" style="margin-right: 10px;" value="Upload Excel">--%>

                       <%--</div>--%>

                       <%--<div class="col-md-2 col-xs-12">--%>
                           <%--<a href="file" id="memberfile" class="btn btn-danger" ></a>--%>
                       <%--</div>--%>
                   <%--</div>--%>
                   <div class="col-md-12 col-xs-12 form-required">

                       <div class="col-md-3 col-xs-12">
                           <div style="position:relative;">
                               <a class='btn btn-primary' href='javascript:;'>
                                   Choose Members Upload File...
                                   <input type="file" id="avatar" required
                                          style='position:absolute;z-index:2;top:0;left:0;filter: alpha(opacity=0);-ms-filter:"progid:DXImageTransform.Microsoft.Alpha(Opacity=0)";opacity:0;background-color:transparent;color:transparent;'
                                          name="file" size="40" onchange='$("#upload-file-info").html($(this).val());'>
                               </a>
                               &nbsp;
                               <span class='label label-info' id="upload-file-info"></span>
                           </div>
                       </div>
                       <div class="col-md-3 col-xs-12">
                           <input id="btn-upload-members"  type="submit" class="btn btn-success float-left" style="margin-right: 10px;"
                                  value="Upload Members">
                       </div>
                       <div class="btn-group col-md-2 col-xs-12">
                           <a id="downloadFile"  class="btn btn-primary active float-right">
                               <i class="glyphicon glyphicon-download-alt" aria-hidden="true"></i> Download Members Template
                           </a>
                       </div>
                       <div class="col-md-2 col-xs-12">
                       <a href="file" id="memberfile" class="btn btn-danger" ></a>
                       </div>
                   </div>
               </form>
               <div class="card-box table-responsive">
               <table id="pol-med-members" class="table table-striped" style="width:100%">
                   <thead>
                   <tr>
                       <th>Category</th>
                       <th>Member Name</th>
                       <th>Member No</th>
                       <th>Type</th>
                       <th>Gender</th>
                       <th>DOB</th>
                       <th>Age</th>
                       <th>Card No</th>
                       <th>Status</th>
                       <th>Premium</th>
                       <th  width="5%"></th>
                       <th  width="5%"></th>
                       <th width="5%"></th>
                   </tr>
                   </thead>
               </table>
                   </div>
           </div>
           <div role="tabpanel" class="tab-pane fade"
                id="tab_content-selffund" aria-labelledby="profile-tab">
               <div class="card-box table-responsive">
               <button class="btn btn-success btn btn-info" id="btn-add-self-fund">New</button>
               <input type="hidden" id="pol-freqpay">

               <table id="pol_self_fund_tbl" class="table table-striped" style="width:100%">
                   <thead>
                   <tr>
                       <th>Level</th>
                       <th>Value</th>
                       <th>Pay When Fund Exh.</th>
                       <th>Pay When Benefit Exh.</th>
                       <th>Min. Deposit</th>
                       <th>Reset Amt</th>
                       <th>Deposit Amt</th>
                       <th>Receipted Amt</th>
                       <th>Billing Freq.</th>
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




<jsp:include page="modals/medicalmodals.jsp"></jsp:include>
