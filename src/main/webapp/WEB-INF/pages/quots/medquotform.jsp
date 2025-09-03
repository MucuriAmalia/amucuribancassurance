<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="sec" uri="http://www.springframework.org/security/tags" %>
<script type="text/javascript" src="<c:url value="/js/modules/utils/select2builder.js"/>"></script>
<script type="text/javascript" src="<c:url value="/libs/rivets/rivets.js"/>"></script>
<script type="text/javascript" src="<c:url value="/js/modules/quotes/medquottrans.js"/>"></script>
<script type="text/javascript">
    var quotId = ${quotId};
</script>

<div class="x_panel">
    <div class="x_title">
        <h2 id="wkflow-task-name"><i class="fa fa-bars"></i> Quotation Details</h2>
        <ul class="nav navbar-right panel_toolbox">
            <li><a class="collapse-link"><i class="fa fa-chevron-up"></i></a>
            </li>
        </ul>
        <div class="clearfix"></div>
    </div>
    <input action="action" type="button" onclick="history.go(-1);"  class="btn btn-info float-right" value="Back" id="renbtn" style="display:none"/>
<sec:authorize access="hasAnyAuthority('ACCESS_QUOT_REPORTS')">
    <input type="button" class="btn btn-info float-right"
           value="Reports" id="btn-quot-reports" data-toggle="modal" data-target="#medQuotereportsModal"
           style="display:none">
    </sec:authorize>
    <input type="button" class="btn btn-info float-right"
           value="Assign" id="btn-assign-trans">
<sec:authorize access="hasAnyAuthority('SAVE_QUOTE')">
    <input type="button" class="btn btn-info float-right"
           value="Save" id="btn-add-quote"
           style="display:none">
    </sec:authorize>
<sec:authorize access="hasAnyAuthority('MAKE_READY_QUOTE')">
    <input type="button" class="btn btn-info float-right"
           value="Submit" id="btn-make-ready-policy"
           style="display:none">
    </sec:authorize>

<sec:authorize access="hasAnyAuthority('AUTHORIZE_QUOTE')">
    <input type="button" class="btn btn-info float-right"
           value="Authorise Quote" id="btn-auth-quote">
    </sec:authorize>
<sec:authorize access="hasAnyAuthority('CONFIRM_QUOTE')">
    <input type="button" class="btn btn-info float-right"
           value="Confirm Quote" id="btn-confirm-quote"
           style="display:none">
    </sec:authorize>
    <sec:authorize access="hasAnyAuthority('MAKE_READY_QUOTE')">
        <input type="button" class="btn btn-info float-right"
               value="Undo Make Trans Ready" id="btn-undo-make-ready-policy"
               style="display:none">
    </sec:authorize>
<sec:authorize access="hasAnyAuthority('CLOSE_QUOTE')">
    <input type="button" class="btn btn-info float-right"
           value="Close Quote" id="btn-cancel-quote"
           style="display:none">
    </sec:authorize>




    <div class="x_content">

        <form id="quot-form" class="form-horizontal form-label-left">
            <input type="hidden" id="quot-id" name="quoteId"/>
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
                    <label for="client-frm" class="label-align col-md-5 quot-client">
                        Client<span class="required">*</span></label>
                    <div class="col-md-7 col-xs-12">
                        <div id="edit-client">
                            <input type="hidden" id="client-id" name="clientId"/>
                            <input type="hidden" id="client-f-name">
                            <input type="hidden" id="client-other-name">
                            <div id="client-div">
                                <div id="client-frm" class="form-control" style="display: none"
                                     select2-url="<c:url value="/protected/uw/policies/uwClients"/>" >

                                </div>
                                <a
                                        href="<c:url value="/protected/clients/setups/clientsform?type=medquot"/>" id="btn-add-client"  class="btn-sm btn-success btn-info">New</a>
                            </div>
                            <div id="prs-div">
                                <div id="prospects-frm" class="form-control" style="display: none"
                                     select2-url="<c:url value="/protected/quotes/selprospects"/>" >

                                </div>
                                <input type="button" class="btn-sm btn-success btn-info" id="btn-add-prs" value="New" style="display: none">

                            </div>
                        </div>
                        <div id="display-client">
                            <p class="form-control-static" id="client-info"> </p>
                        </div>
                    </div>
                </div>

            </div>
            <div class="item form-group form-required">
                <div class="col-md-6 col-xs-12">
                    <label for="pol-buss-type" class="label-align col-md-5">
                        Period<span class="required">*</span></label>
                    <div class="col-md-7 col-xs-12">
                        <select class="form-control" id="pol-buss-type" name="businessType"
                        required>
                            <option value="">Select Business Type</option>
                            <option value="N">Annual</option>
                            <option value="S">Short Period</option>
                        </select>
                    </div>
                </div>
                <div class="col-md-6 col-xs-12">
                    <label for="pol-bin-type" class="label-align col-md-5">
                        Scheme Type<span class="required">*</span></label>
                    <div class="col-md-7 col-xs-12">
                        <select class="form-control" id="pol-bin-type" required
                        >
                            <option value="">Select Scheme Type</option>
                            <option value="B">Insured Binder</option>
                            <option value="BN">Insured Non-Binder</option>
                            <option value="S">Self Fund</option>
                        </select>
                    </div>
                </div>

            </div>
            <div class = "item form-group">

                <div class="col-md-6 col-xs-12">
                    <label for="binder-frm" class="label-align col-md-5">
                        Binder/Mask<span class="required">*</span></label>
                    <div class="col-md-7 col-xs-12">
                        <div id="edit-binder">
                            <input type="hidden" id="binder-id" name="bindCode"/>
                            <input type="hidden"  id="product-id" name="prodId"/>
                            <input type="hidden" id="bind-name" name="bindName"/>
                            <input type="hidden"  id="bind-pro-name"/>
                            <input type="hidden"  id="bind-ins-name"/>
                            <div id="binder-frm" class="form-control"
                                 select2-url="<c:url value="/protected/medical/policies/uwBinders"/>" >

                            </div>
                        </div>
                        <div id="display-binder">
                            <p class="form-control-static" id="binder-info"> </p>
                        </div>
                    </div>
                </div>
                <div class="col-md-6 col-xs-12">
                    <label for="medicalCoverTyp-info" class="label-align col-md-5">
                        Type</label>
                    <div class="col-md-7 col-xs-12">
                        <input type="hidden" id="pol-medicalCoverType" name="medicalCoverType"/>
                        <div id="display-medicalCoverTyp">
                            <p class="form-control-static" id="medicalCoverTyp-info"> </p>
                        </div>
                    </div>
                </div>
            </div>
            <div class="item form-group form-required">
                <div class="col-md-6 col-xs-12">
                <label for="pol-ins-comp" class="label-align col-md-5">
                    Intermediary</label>
                <div class="col-md-7 col-xs-12">
                    <p class="form-control-static" id="pol-ins-comp"></p>
                </div>
                </div>
                <div class="col-md-6 col-xs-12">
                    <label for="pol-prod-name" class="label-align col-md-5">
                        Product</label>
                    <div class="col-md-7 col-xs-12">
                        <p class="form-control-static" id="pol-prod-name"></p>
                    </div>
                </div>

            </div>
            <div class="item form-group">
                <div class="col-md-6 col-xs-12  form-required">
                    <label for="brn-id" class="col-md-5 label-align">Date
                        From</label>

                    <div class="col-md-7 col-xs-12">
                        <div class='input-group date datepicker-input' id="wef-date">
                            <input type='text' class="form-control float-right" name="wefDate"
                                   id="from-date" required />
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
                        <div class='input-group date datepicker-input' id="cover-to-date">
                            <input type='text' class="form-control float-right" name="wetDate"
                                   id="wet-date" required />
                            <div class="input-group-addon">
                                <span class="fa fa-calendar"></span>
                            </div>
                        </div>
                    </div>

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
                                 select2-url="<c:url value="/protected/uw/policies/uwcurrencies"/>" >
                            </div>
                        </div>
                        <div id="display-currency">
                            <p class="form-control-static" id="currency-info"> </p>
                        </div>
                    </div>
                </div>
                <div class="col-md-6 col-xs-12">
                    <label for="brn-frm" class="label-align col-md-5">
                        Branch<span class="required">*</span></label>
                    <div class="col-md-7 col-xs-12">
                        <div id="edit-branch">
                            <input type="hidden" id="brn-id" name="branchId"/>
                            <input type="hidden" id="brn-name">
                            <div id="brn-frm" class="form-control"
                                 select2-url="<c:url value="/protected/uw/policies/uwbranches"/>" >

                            </div>
                        </div>
                        <div id="display-branch">
                            <p class="form-control-static" id="branch-info"> </p>
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
                                 select2-url="<c:url value="/protected/uw/policies/uwpaymentmodes"/>" >

                            </div>
                        </div>
                        <div id="display-payment-mode">
                            <p class="form-control-static" id="pay-mode-info"> </p>
                        </div>
                    </div>


                    </div>
                    <div class="item form-group policy-card-type">
                        <div class="col-md-6 col-xs-12" >
                            <label for="card-frm" class="label-align col-md-5">
                                Card Type</label>
                            <div class="col-md-7 col-xs-12">
                                <input type="hidden" id="card-id" name="cardId"/>
                                <input type="hidden" id="card-name">
                                <div id="card-frm" class="form-control"
                                     select2-url="<c:url value="/protected/medical/policies/binderCardTypes"/>" >

                                </div>
                            </div>
                        </div>
                    </div>
            </div>
            <div class="item form-group form-required">
                <div class="col-md-6 col-xs-12">
                    <label for="sourcegroup-frm" class="label-align col-md-5">
                        Source Group<span class="required">*</span></label>
                    <div class="col-md-7 col-xs-12">
                        <div id="edit-sourcegroup">
                            <input type="hidden" id="sourcegroup-id"/>
                            <input type="hidden" id="sourcegroup-name">
                            <div id="sourcegroup-frm" class="form-control"
                                 select2-url="<c:url value="/protected/quotes/sourcesgroups"/>" >
                            </div>
                        </div>
                        <div id="display-sourcegroup">
                            <p class="form-control-static" id="sourcegroup-info"> </p>
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
                                 select2-url="<c:url value="/protected/quotes/businesssources"/>" >
                            </div>
                        </div>
                        <div id="display-source">
                            <p class="form-control-static" id="source-info"> </p>
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
                            <input type="hidden" id="div-pol-no" name="quotNo"/>
                            <p class="form-control-static" id="pol-no"> </p>
                        </div>
                    </div>
                    <div class="col-md-6 col-xs-12">
                        <label for="pol-rev-no" class="label-align col-md-5">
                            Revision Number</label>
                        <div class="col-md-7 col-xs-12">
                            <input type="hidden" id="div-endos-no" name="quotRevNo"/>
                            <p class="form-control-static" id="pol-rev-no"> </p>
                        </div>
                    </div>
                </div>
                <div class="item form-group form-required">
                    <div class="col-md-6 col-xs-12">
                        <label for="pol-status" class="label-align col-md-5">
                            Quotation Status</label>
                        <div class="col-md-7 col-xs-12">
                            <p class="form-control-static" id="pol-status"> </p>
                        </div>
                    </div>
                    <div class="col-md-6 col-xs-12">
                        <label for="pol-premium" class="label-align col-md-5">
                             Premium</label>
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
                        <label for="issue-card-fee" class="label-align col-md-5">
                            Issue Card Fee</label>
                        <div class="col-md-7 col-xs-12">
                            <p class="form-control-static" id="issue-card-fee"> </p>
                        </div>
                    </div>
                </div>
                <div class="item form-group form-required">
                    <div class="col-md-6 col-xs-12">
                        <label for="service-Charge" class="label-align col-md-5">
                            Service Fee</label>
                        <div class="col-md-7 col-xs-12">
                            <p class="form-control-static" id="service-Charge"> </p>
                        </div>
                    </div>
                    <div class="col-md-6 col-xs-12">
                        <label for="pol-comm-amt" class="label-align col-md-5">
                            Commission Amt</label>
                        <div class="col-md-7 col-xs-12">
                            <p class="form-control-static" id="pol-comm-amt"> </p>
                        </div>
                    </div>
                </div>
                <div class="item form-group form-required">
                    <div class="col-md-6 col-xs-12">
                        <label for="pol-vat-amt" class="label-align col-md-5">
                            V.A.T</label>
                        <div class="col-md-7 col-xs-12">
                            <p class="form-control-static" id="pol-vat-amt"> </p>
                        </div>
                    </div>
                </div>


            </div>

        </form>
    </div>
    <div class="x_title" id="uw_tabs_title">
        <h4>Other Quotation Details</h4>
        <ul class="nav navbar-right panel_toolbox">
            <li><a class="collapse-link"><i class="fa fa-chevron-up"></i></a>
            </li>
        </ul>
    </div>
    <div class="x_content">
        <div class="" role="tabpanel" data-example-id="togglable-tabs" id="uw_tabs">
            <ul id="myTab" class="nav nav-tabs bar_tabs" role="tablist">
                <li role="presentation" class="active"><a href="#tab_content1"
                                                          id="home-tab" role="tab" data-toggle="tab" aria-expanded="true">Category Details</a>
                </li>
                <li role="presentation" class="" ><a href="#tab_content2"
                                                                    role="tab" id="profile-tab" data-toggle="tab" aria-expanded="false">Taxes</a>
                </li>
                <li role="presentation" class=""><a href="#tab_content2-4"
                                                    role="tab" id="profile-tab-4" data-toggle="tab" aria-expanded="false">Clauses</a>
                </li>
            </ul>
            <div id="myTabContent" class="tab-content">
                <div role="tabpanel" class="tab-pane active"
                     id="tab_content1" aria-labelledby="home-tab">
                    <div class="card-box table-responsive">
                        <button class="btn btn-success btn btn-info float-left" id="btn-add-category">New</button>
                        <input type="hidden" id="fam-category-pk">
                        <input type="hidden" id="fam-category-bin-pk">
                        <div class="table-responsive">
                            <table id="med-categories" class="table table-striped" style="width: 100%">
                                <thead>
                                <tr class="headings">
                                    <th>Category</th>
                                    <th>Sht Desc</th>
                                    <th>Description</th>
                                    <th>Principal Age</th>
                                    <th>Principal Gender</th>
                                    <th>Spouse Age</th>
                                    <th>Number of Children</th>
                                    <th>Premium</th>
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
                                                                        role="tab" id="profile-tab-2" data-toggle="tab" aria-expanded="false">Families</a>
                                    </li>
                                    <li role="presentation" class=""><a href="#tab_content2-3"
                                                                        role="tab" id="profile-tab-3" data-toggle="tab" aria-expanded="false">Benefits</a>
                                    </li>

                                </ul>


                                <div id="myTabContent-1" class="tab-content">
                                    <div role="tabpanel" class="tab-pane active"
                                         id="tab_content2-6" aria-labelledby="profile-tab-6">
                                        <div class="card-box table-responsive">
                                            <button class="btn btn-success btn btn-info float-left" id="btn-add-new-rule">New</button>
                                            <table id="med-rules-tbl" class="table table-striped" style="width: 100%">
                                                <thead>
                                                <tr class="headings">
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
                                        <div class="card-box table-responsive" class="col-md-6 col-xs-12">
                                            <button class="btn btn-success btn btn-info" id="btn-add-new-fam">New</button>
                                            <table id="familyDetailsList" class="table table-striped" >
                                                <thead>
                                                <tr class="headings">
                                                    <th width="15%">Family Size</th>
                                                    <th width="15%">Number of Families</th>
                                                    <th width="15%">Age Bracket</th>
                                                    <th width="15%">Required Cards</th>
                                                    <th width="15%">Premium</th>
                                                    <th width="5%"></th>
                                                    <th width="5%"></th>
                                                    <th ></th>
                                                </tr>
                                                </thead>
                                            </table>
                                        </div>
                                        <div class="col-md-6 col-xs-12">

                                        </div>
                                    </div>
                                    <div role="tabpanel" class="tab-pane fade"
                                         id="tab_content2-3" aria-labelledby="profile-tab-3">
                                        <div class="card-box table-responsive">
                                            <button class="btn btn-success btn btn-info" id="btn-add-new-benefit">New</button>
                                            <input type="hidden" id="fund-Binder">

                                            <table id="medCoversList" class="table table-striped" style="width: 100%">
                                                <thead>
                                                <tr class="headings">
                                                    <th>Cover</th>
                                                    <th>Main Cover</th>
                                                    <th>Parent Cover</th>
                                                    <th>Prorated Full</th>
                                                    <th>Min Prem.</th>
                                                    <th>Limit Amt</th>
                                                    <th>Appl. At</th>
                                                    <th>Premium</th>
                                                    <th>Waiting Prd</th>
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
                <div role="tabpanel" class="tab-pane fade"
                     id="tab_content2" aria-labelledby="profile-tab">
                    <div class="card-box table-responsive">
                        <button class="btn btn-success btn btn-info" id="btn-add-new-tax">New</button>

                        <table id="polTaxesList" class="table table-striped" style="width: 100%">
                            <thead>
                            <tr class="headings">
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
                     id="tab_content2-4" aria-labelledby="profile-tab-4">
                    <div class="card-box table-responsive">
                        <button class="btn btn-success btn btn-info" id="btn-add-new-clause">New</button>

                        <table id="polclausesList" class="table table-striped" style="width: 100%">
                            <thead>
                            <tr class="headings">

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
<jsp:include page="../modals/prospectmodals.jsp"></jsp:include>
<jsp:include page="modals/riskmodals.jsp"></jsp:include>