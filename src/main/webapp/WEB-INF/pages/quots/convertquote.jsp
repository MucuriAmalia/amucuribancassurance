<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="sec" uri="http://www.springframework.org/security/tags" %>
<script type="text/javascript" src="<c:url value="/js/modules/utils/select2builder.js"/>"></script>
<script type="text/javascript" src="<c:url value="/libs/rivets/rivets.js"/>"></script>
<script type="text/javascript" src="<c:url value="/js/modules/quotes/convertquotes.js"/>"></script>
<sec:authorize access="hasAnyAuthority('CONVERT_QUOT_PRODUCT')" var="isConvertQuot"></sec:authorize>
<script type="text/javascript">
    var canAccess = ${isConvertQuot};

</script>
<div class="x_panel">
    <div class="x_title">
        <h2><i class="fa fa-bars"></i> Convert Quotation</h2>
        <div class="clearfix"></div>
    </div>
    <form id="search-form" class="form-horizontal">
        <div class="item form-group">
            <div class="col-md-6 col-xs-12">
                <label for="brn-id" class="col-md-4 label-align">Quotation Number
                    No.</label>

                <div class="col-md-8 col-xs-12">
                    <input type='text' class="form-control float-right"
                           id="quote-search-number"/>
                </div>
            </div>
            <div class="col-md-6 col-xs-12">
                <label for="brn-id" class="col-md-4 label-align">Client
                </label>

                <div class="col-md-8 col-xs-12">
                    <input type='hidden' class="form-control float-right"
                           id="rev-search-name"/>
                    <div id="client-frm" class="form-control"
                         select2-url="<c:url value="/protected/uw/policies/uwClients"/>">

                    </div>
                </div>
            </div>

        </div>
        <div class="item form-group">
            <div class="col-md-6 col-xs-12">
                <label for="brn-id" class="col-md-4 label-align">Insurance Company
                </label>

                <div class="col-md-8 col-xs-12">
                    <input type='hidden' class="form-control float-right"
                           id="agent-search-number"/>
                    <div id="acc-frm" class="form-control"
                         select2-url="<c:url value="/protected/setups/binders/selAccounts"/>">
                    </div>
                </div>
            </div>
            <div class="col-md-6 col-xs-12">
                <label for="brn-id" class="col-md-4 label-align">Product
                </label>

                <div class="col-md-8 col-xs-12">
                    <input type='hidden' class="form-control float-right"
                           id="product-search-number"/>
                    <div id="prd-code" class="form-control"
                         select2-url="<c:url value="/protected/setups/binders/selGrpProducts"/>">
                    </div>
                </div>
            </div>
        </div>
        <div class="item form-group">
            <div class="col-md-6 col-xs-12">
                <label for="brn-id" class="col-md-4 label-align">Prospect
                </label>

                <div class="col-md-8 col-xs-12">
                    <input type='hidden' class="form-control float-right"
                           id="prs-search-name"/>
                    <div id="prospects-frm" class="form-control"
                         select2-url="<c:url value="/protected/quotes/selprospects"/>">

                    </div>
                </div>
            </div>
            <div class="col-md-6 col-xs-12">

            </div>

        </div>
        <div class="item form-group">
            <input type="button" class="btn btn-info float-right"
                   style="margin-right: 10px;" value="Search"
                   id="btn-search-quotes">
        </div>


    </form>
    <div class="table-responsive">
        <table id="convert_quot_tbl" class="table table-striped" style="width: 100%">
            <thead>
            <tr class="headings">
                <th>Quot No</th>
                <th>Product</th>
                <th>Insurance Company</th>
                <th>Cover From</th>
                <th>Cover To</th>
                <th>Client</th>
                <th>Currency</th>
                <th>Converted?</th>
                <th>Policy No</th>
                <th></th>
                <th></th>
            </tr>
            </thead>
        </table>
    </div>
</div>


<div class="modal fade" id="clientReqDocsModal" tabindex="-1" role="dialog"
     aria-labelledby="clientReqDocsModalLabel" aria-hidden="true">
    <div class="modal-dialog">
        <div class="modal-content">
            <div class="modal-header">
                <h4 class="modal-title" id="clientReqDocsModalLabel">Select Required Docs</h4>
                <button type="button" class="close" data-dismiss="modal"
                        aria-label="Close">
                    <span aria-hidden="true">&times;</span>
                </button>

            </div>
            <div class="modal-body">
                <form class="form-horizontal">
                    <div class="item form-group">
                        <label for="brn-id" class="col-md-3 label-align">Document Name</label>

                        <div class="col-md-6">
                            <input type="text" class="form-control" id="doc-name-search"
                            >
                        </div>
                        <div class="col-md-1">
                            <button id="searchDocuments"
                                    type="button" class="btn btn-primary">
                                Search
                            </button>
                        </div>
                    </div>
                </form>
                <div style="height: 300px !important; overflow: scroll;">
                    <table class="table table-striped table-hover table-bordered table-fixed" id="clientDocsTbl">
                        <thead>
                        <tr>
                            <th width="1%"></th>
                            <th width="4%">Document Id</th>
                            <th width="12%">Document Name</th>
                        </tr>
                        </thead>
                        <tbody>

                        </tbody>
                    </table>
                </div>
                <form id="client-doc-form">
                    <input type="hidden" id="req-client-code" name="subCode"/>
                </form>
            </div>
            <div class="modal-footer">
                <button data-loading-text="Saving..." id="saveClientDocsBtn"
                        type="button" class="btn btn-success">Save
                </button>
                <button type="button" class="btn btn-default" id="closeclientReqDocsModal">
                    Cancel
                </button>
            </div>
        </div>
    </div>
</div>

<div class="modal fade" id="converQuotModal" tabindex="-1" role="dialog"
     aria-labelledby="converQuotModalLabel" aria-hidden="true">
    <div class="modal-dialog">
        <form id="convert-quot-form" class="form-horizontal">
            <div class="modal-content">
                <div class="modal-header">
                    <h4 class="modal-title" id="converQuotModalLabel">Convert Quote to Policy</h4>
                    <button type="button" class="close" data-dismiss="modal"
                            aria-label="Close">
                        <span aria-hidden="true">&times;</span>
                    </button>

                </div>
                <div class="modal-body">

                    <input type="hidden" id="conver-quot-pro-id" name="quotProdId"/>
                    <div class="item form-group">
                        <label for="cou-name" class="col-md-3 label-align">Policy Start Date</label>

                        <div class="col-md-8">
                            <div class='input-group date datepicker-input' id="wef-date">
                                <input type='text' class="form-control float-right" name="startDate"
                                       id="from-date" required/>
                                <div class="input-group-addon">
                                    <span class="fa fa-calendar"></span>
                                </div>
                            </div>
                        </div>
                    </div>
                </div>
                <div class="modal-footer">
                    <button type="button" class="btn btn-success" id="convertQuotBtn">
                        Convert
                    </button>
                    <button type="button" class="btn btn-default" data-dismiss="modal">
                        Close
                    </button>
                </div>
            </div>
        </form>
    </div>
</div>


<div class="modal fade" id="clientdocModal" tabindex="-1" role="dialog"
     aria-labelledby="clientdocModalLabel" aria-hidden="true">
    <div class="modal-dialog">
        <form id="clnt-doc-form" class="form-horizontal" enctype="multipart/form-data">
            <div class="modal-content">
                <div class="modal-header">
                    <h4 class="modal-title" id="clientdocModalLabel">Upload Client Document</h4>
                    <button type="button" class="close" data-dismiss="modal"
                            aria-label="Close">
                        <span aria-hidden="true">&times;</span>
                    </button>

                </div>
                <div class="modal-body">

                    <input type="hidden" id="clnt-doc-id" name="docId"/>
                    <input type="hidden" id="reqd-doc-id" name="requiredDoc"/>
                    <div class="item form-group">
                        <label for="cou-name" class="col-md-3 label-align">Document Type</label>

                        <div class="col-md-8">
                            <p class="form-control-static" id="clnt-doc-name"></p>
                        </div>
                    </div>
                    <div class="item form-group">
                        <label for="cou-name" class="col-md-3 label-align">Uploaded File Name</label>

                        <div class="col-md-8">
                            <p class="form-control-static" id="clnt-upload-name"></p>
                        </div>
                    </div>
                    <div class="item form-group">
                        <label for="brn-id" class="col-md-4 label-align">Document</label>

                        <div class="col-md-8">
                            <div class="input-group col-xs-12">
                                <input name="file" type="file" id="clnt-avatar" required>
                            </div>
                        </div>
                    </div>


                </div>
                <div class="modal-footer">
                    <input value="Upload"
                           type="submit" class="btn btn-success">

                    </input>
                    <button type="button" class="btn btn-default" id="closeUpload">
                        Close
                    </button>
                </div>
            </div>
        </form>
    </div>
</div>

<div class="modal fade" id="createProspectModal" tabindex="-1" role="dialog"
     aria-labelledby="createProspectModalLabel" aria-hidden="true">
    <div class="modal-dialog modal-lg">
        <div class="modal-content">
            <div class="modal-header">
                <h4 class="modal-title" id="createProspectModalLabel">
                    Create/Edit Prospect
                </h4>
                <button type="button" class="close" data-dismiss="modal"
                        aria-label="Close">
                    <span aria-hidden="true">&times;</span>
                </button>

            </div>
            <div class="modal-body">

                <div class="" role="tabpanel" data-example-id="togglable-tabs">
                    <ul id="myTab" class="nav nav-tabs bar_tabs" role="tablist">
                        <li role="presentation" class="active"><a href="#tab_content1"
                                                                  id="home-tab" role="tab" data-toggle="tab"
                                                                  aria-expanded="true">Client Details</a>
                        </li>
                        <li role="presentation" id="show-docs"><a href="#tab_content2"
                                                                  role="tab" id="profile-tab" data-toggle="tab"
                                                                  aria-expanded="false">Client Documents</a>
                        </li>
                    </ul>
                    <div id="myTabContent" class="tab-content">
                        <div role="tabpanel" class="tab-pane active"
                             id="tab_content1" aria-labelledby="home-tab">

                            <form id="prospect-form" class="form-horizontal">
                                <input type="hidden" class="form-control" id="prospect-id-pk" name="tenId">
                                <input type="hidden" id="prosp-quot-prd-pk" name="prodId">
                                <input type="hidden" id="prosp-quot-pk" name="quotationId">
                                <input type="hidden" name="phoneNo" id="phone-no">
                                <div class="item form-group form-required">
                                    <div class="col-md-6 col-xs-12">
                                        <label for="prs-sht-desc" class="label-align col-md-5">Prospect ID</label>
                                        <div class="col-md-7 col-xs-12">
                                            <input type="text" name="prospShtDesc" id="prs-sht-desc"
                                                   class="form-control"
                                                   placeholder="" readonly>
                                        </div>
                                    </div>
                                    <div class="col-md-6 col-xs-12">
                                        <label for="noOfUnits" class="label-align col-md-5">Prospect Type</label>
                                        <div class="col-md-7 col-xs-12">
                                            <input type="hidden" id="clnt-type-id" name="clientTypeId"/>
                                            <input type="hidden" id="clnt-type-name">
                                            <div id="clnt-client-type" class="form-control"
                                                 select2-url="<c:url value="/protected/clients/setups/selClientTypes"/>">

                                            </div>

                                        </div>
                                    </div>
                                </div>
                                <div class="item form-group form-required">
                                    <div class="col-md-6 col-xs-12">
                                        <label for="fname" class="label-align col-md-5">First
                                            Name</label>
                                        <div class="col-md-7 col-xs-12">
                                            <input type="text" name="fname" id="fname" class="form-control"
                                                   placeholder="First Name" required>
                                        </div>
                                    </div>
                                    <div class="col-md-6 col-xs-12">
                                        <label for="other-names" class="label-align col-md-5">Other
                                            Names</label>
                                        <div class="col-md-7 col-xs-12">
                                            <input type="text" name="otherNames" id="other-names" class="form-control"
                                                   placeholder="Other Names" required>
                                        </div>
                                    </div>
                                </div>

                                <div class="item form-group">

                                    <div class="col-md-6 col-xs-12">
                                        <label for="clnt-title" class="label-align col-md-5">Title
                                        </label>
                                        <div class="col-md-7 col-xs-12">
                                            <input type="hidden" id="clnt-title-id" name="clientTitle"/>
                                            <input type="hidden" id="clnt-title-name">
                                            <div id="clnt-title" class="form-control"
                                                 select2-url="<c:url value="/protected/clients/setups/selClientTitles"/>">

                                            </div>
                                        </div>
                                    </div>

                                    <div class="col-md-6 col-xs-12">
                                        <label for="prosp-email" class="label-align col-md-5">Email</label>
                                        <div class="col-md-7 col-xs-12">
                                            <input type="email" name="emailAddress" id="prosp-email"
                                                   class="form-control"
                                                   placeholder="Email Address">
                                        </div>
                                    </div>
                                </div>

                                <div class="item form-group form-required">
                                    <div class="col-md-6 col-xs-12">
                                        <label for="pin-number" class="label-align col-md-5">Pin
                                            No <span class="required">*</span> </label>
                                        <div class="col-md-7 col-xs-12">
                                            <input type="text" name="pinNo" id="pin-number" class="form-control"
                                                   placeholder="Pin Number" required>
                                        </div>
                                    </div>
                                    <div class="col-md-6 col-xs-12">
                                        <label for="id-number" class="label-align col-md-5">Id
                                            No <span class="required">*</span></label>
                                        <div class="col-md-7 col-xs-12">
                                            <input type="text" name="idNo" id="id-number" class="form-control"
                                                   placeholder="ID Number" required>
                                        </div>
                                    </div>
                                </div>

                                <div class="item form-group form-required">
                                    <div class="col-md-6 col-xs-12">
                                        <label for="address" class="label-align col-md-5">Address</label>
                                        <div class="col-md-7 col-xs-12">
                                            <textarea rows="1.2" cols=30 class="form-control" name="address"
                                                      id="address"></textarea>
                                        </div>

                                    </div>

                                    <div class="col-md-6 col-xs-12">
                                        <label for="noOfUnits" class="label-align col-md-5">Town</label>
                                        <div class="col-md-7 col-xs-12">
                                            <input type="hidden" id="clnt-town-id" name="town"/>
                                            <input type="hidden" id="clnt-town-name">
                                            <div id="town-code-lov" class="form-control"
                                                 select2-url="<c:url value="/protected/clients/setups/selClientTown"/>">

                                            </div>
                                        </div>

                                    </div>

                                </div>
                                <div class="item form-group form-required">
                                    <div class="col-md-6 col-xs-12">
                                        <label for="noOfUnits" class="label-align col-md-5">Postal Code</label>
                                        <div class="col-md-7 col-xs-12">
                                            <input type="hidden" id="postal-code-id" name="postalCodesDef"/>
                                            <input type="hidden" id="postal-name">
                                            <div id="postal-code" class="form-control"
                                                 select2-url="<c:url value="/protected/clients/setups/selClientPostalCode"/>">

                                            </div>
                                        </div>

                                    </div>
                                    <div class="col-md-6 col-xs-12">
                                        <label for="sel3" class="label-align col-md-5">Status</label>
                                        <div class="col-md-7 col-xs-12">
                                            <select class="form-control" id="sel3" name="status" required>
                                                <option value="">Select Status</option>
                                                <option value="A">Active</option>
                                                <option value="T">Terminated</option>
                                            </select>
                                        </div>
                                    </div>
                                </div>
                                <div class="item form-group">

                                    <div class="col-md-6 col-xs-12">
                                        <label for="passport-no" class="label-align col-md-5">Passport No</label>
                                        <div class="col-md-7 col-xs-12">
                                            <input type="text" name="passportNo" id="passport-no" class="form-control"
                                                   placeholder="Passport No">
                                        </div>
                                    </div>
                                    <div class="col-md-6 col-xs-12">
                                        <label for="gender" class="label-align col-md-5" id="lblGender">Gender</label>
                                        <div class="col-md-7 col-xs-12 gender">
                                            <select class="form-control" id="gender" name="gender" required>
                                                <option value="">Select Gender</option>
                                                <option value="M">Male</option>
                                                <option value="F">Female</option>
                                            </select>

                                        </div>
                                    </div>
                                </div>
                                <div class="item form-group form-required">
                                    <div class="col-md-6 col-xs-12">
                                        <label for="clnt-country" class="label-align col-md-5">Domicile Country</label>
                                        <div class="col-md-7 col-xs-12">
                                            <input type="hidden" id="cou-id" name="country"/>
                                            <input type="hidden" id="cou-name">
                                            <div id="clnt-country" class="form-control"
                                                 select2-url="<c:url value="/protected/organization/countries"/>">

                                            </div>

                                        </div>
                                    </div>
                                    <div class="col-md-6 col-xs-12">
                                        <label for="office-tel-no" class="label-align col-md-5">Tel No</label>
                                        <div class="col-md-7 col-xs-12">
                                            <input type="text" name="officeTel" id="office-tel-no" class="form-control"
                                                   placeholder="Tel No">
                                        </div>
                                    </div>

                                </div>
                                <div class="item form-group form-required">
                                    <div class="col-md-6 col-xs-12">
                                        <label for="sms-pref" class="label-align col-md-5">SMS Number</label>
                                        <div class="col-md-3 col-xs-12">
                                            <input type="hidden" id="pref-sms-id" name="prefixId"/>
                                            <input type="hidden" id="pref-sms-name">
                                            <div id="sms-pref" class="form-control"
                                                 select2-url="<c:url value="/protected/clients/setups/selMobilePrefix"/>">

                                            </div>
                                        </div>
                                        <div class="col-md-4 col-xs-12">
                                            <input type="text" name="smsNumber" id="sms-no" class="form-control"
                                                   placeholder="SMS No" required>
                                        </div>
                                    </div>
                                    <div class="col-md-6 col-xs-12">
                                        <label for="noOfUnits" class="label-align col-md-5">Phone Number</label>
                                        <div class="col-md-3 col-xs-12">
                                            <input type="hidden" id="pref-phone-id" name="prefix"/>
                                            <input type="hidden" id="pref-phone-name">
                                            <div id="phone-pref" class="form-control"
                                                 select2-url="<c:url value="/protected/clients/setups/selMobilePrefix"/>">

                                            </div>
                                        </div>
                                        <div class="col-md-4 col-xs-12">
                                            <input type="text" name="phoneNo" id="clnt-phone-no" class="form-control"
                                                   placeholder="Phone No" required>
                                        </div>
                                    </div>
                                </div>
                                <div class="item form-group form-required">
                                    <div class="col-md-6 col-xs-12">
                                        <label for="ten-branch" class="label-align col-md-5">Branch Registered</label>
                                        <div class="col-md-7">
                                            <input type="hidden" id="obId" name="branchId"/>
                                            <input type="hidden" id="reg-brn-name">
                                            <input type="hidden" id="ob-name">
                                            <div id="ten-branch" class="form-control"
                                                 select2-url="<c:url value="/protected/setups/branches"/>">

                                            </div>
                                        </div>
                                    </div>

                                </div>
                                <div class="employee-info">
                                    <div class="item form-group form-required">
                                        <div class="col-md-6 col-xs-12">
                                            <label for="sect-def" class="col-md-5 label-align"
                                                   id="lbl-sector">Sector</label>

                                            <div class="col-md-7 col-xs-12">
                                                <input type="hidden" id="sect-id" name="clientSector"/>
                                                <input type="hidden" id="sect-name">
                                                <div id="sect-def" class="form-control"
                                                     select2-url="<c:url value="/protected/setups/selSectors"/>">

                                                </div>
                                            </div>
                                        </div>
                                        <div class="col-md-6 col-xs-12">
                                            <label for="occ-def" class="col-md-5 label-align">Occupation</label>

                                            <div class="col-md-7 col-xs-12">
                                                <input type="hidden" id="occ-id" name="occupation"/>
                                                <input type="hidden" id="occ-name">
                                                <div id="occ-def" class="form-control"
                                                     select2-url="<c:url value="/protected/setups/selOccupations"/>">

                                                </div>
                                            </div>
                                        </div>
                                    </div>
                                </div>
                                <div class="item form-group form-required">
                                    <div class="col-md-6 col-xs-12">
                                        <label for="dob" class="col-md-5 label-align" id="lbl-dob">Date of
                                            Birth</label>

                                        <div class="col-md-7 col-xs-12">
                                            <div class='input-group date datepicker-input'>
                                                <input type='text' class="form-control float-right" name="dob"
                                                       id="dob"/>
                                                <div class="input-group-addon">
                                                    <span class="fa fa-calendar"></span>
                                                </div>
                                            </div>
                                        </div>
                                    </div>
                                    <div class="col-md-6 col-xs-12">
                                        <label for="reg-wet" class="col-md-5 label-align">Date Registered</label>

                                        <div class="col-md-7 col-xs-12">
                                            <div class='input-group date datepicker-input'>

                                                <input type='text' class="form-control float-right"
                                                       name="dateregistered" id="date-reg"/>
                                                <div class="input-group-addon">
                                                    <span class="fa fa-calendar"></span>
                                                </div>

                                            </div>
                                        </div>
                                    </div>
                                </div>
                                <div class="item form-group form-required">
                                    <div class="col-md-6 col-xs-12">
                                        <label for="pol_start-date" class="col-md-5 label-align">Policy Start
                                            Date</label>

                                        <div class="col-md-7 col-xs-12">
                                            <div class='input-group date datepicker-input'>

                                                <input type='text' class="form-control float-right" name="polStartDate"
                                                       id="pol_start-date"/>
                                                <div class="input-group-addon">
                                                    <span class="fa fa-calendar"></span>
                                                </div>

                                            </div>
                                        </div>
                                    </div>
                                </div>


                            </form>
                        </div>
                        <div role="tabpanel" class="tab-pane fade"
                             id="tab_content2" aria-labelledby="profile-tab">
                            <button class="btn btn-success btn btn-info" id="btn-add-docs">New</button>
                            <div class="card-box table-responsive">
                                <table id="clientDocsList" class="table table-striped" style="width: 100%">
                                    <thead>
                                    <tr class="headings">
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
                        </div>
                    </div>
                </div>
            </div>
            <div class="modal-footer">
                <button data-loading-text="Saving..." id="saveProspectsBtn"
                        type="button" class="btn btn-success">
                    Save
                </button>
                <button type="button" class="btn btn-default" data-dismiss="modal">
                    Cancel
                </button>
            </div>
        </div>
    </div>
</div>