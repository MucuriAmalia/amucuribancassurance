<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<div class="modal fade" id="convertProspectModal" tabindex="-1" role="dialog"
     aria-labelledby="convertProspectModalLabel" aria-hidden="true">
    <div class="modal-dialog modal-lg">
        <div class="modal-content">
            <div class="modal-header">
                <h4 class="modal-title" id="convertProspectModalLabel">
                    Convert Prospect To Client
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

                            <form id="cnvt-prospect-form" class="form-horizontal">
                                <input type="hidden" class="form-control" id="prospect-cnvt-id-pk" name="tenId">
                                <input type="hidden" id="prosp-cnvt-quot-prd-pk" name="prodId">
                                <input type="hidden" id="prosp-cnvt-quot-pk" name="quotationId">
                                <input type="hidden" name="phoneNo" id="cnvt-phone-no">
                                <div class="item form-group form-required">
                                    <div class="col-md-6 col-xs-12">
                                        <label for="prs-cnvt-sht-desc" class="label-align col-md-5">Prospect ID</label>
                                        <div class="col-md-7 col-xs-12">
                                            <input type="text" name="prospShtDesc" id="prs-cnvt-sht-desc"
                                                   class="form-control"
                                                   placeholder="" readonly>
                                        </div>
                                    </div>
                                    <div class="col-md-6 col-xs-12">
                                        <label for="clnt-cnvt-client-type" class="label-align col-md-5">Prospect
                                            Type</label>
                                        <div class="col-md-7 col-xs-12">
                                            <input type="hidden" id="clnt-cnvt-type-id" name="clientTypeId"/>
                                            <input type="hidden" id="clnt-cnvt-type-name">
                                            <div id="clnt-cnvt-client-type" class="form-control"
                                                 select2-url="<c:url value="/protected/clients/setups/selClientTypes"/>">

                                            </div>

                                        </div>
                                    </div>
                                </div>
                                <div class="item form-group form-required">
                                    <div class="col-md-6 col-xs-12">
                                        <label for="resident-status" class="label-align col-md-5">Resident Status<span
                                                class="required">*</span></label>
                                        <div class="col-md-7 col-xs-12">
                                            <select id="resident-status" name="residentStatus" class="form-control"
                                                    required>
                                                <option value="" disabled selected>Select Resident Status</option>
                                                <option value="resident">Individual-Kenyan Citizen</option>
                                                <option value="non-resident">Individual-Non-Kenyan Resident</option>
                                                <option value="non-citizen">Individual-Non-Kenyan Non-Resident</option>
                                                <option value="company">Non-Individual Company</option>

                                            </select>
                                        </div>
                                    </div>
                                    <div class="col-md-6 col-xs-12">
                                        <label for="cnvt-id-number" class="label-align col-md-5">Id
                                            No <span class="required">*</span></label>
                                        <div class="col-md-7 col-xs-12">
                                            <input type="text" name="idNo" id="cnvt-id-number" class="form-control"
                                                   placeholder="ID Number" required>

                                            <div id="loading-spinner" class="spinner-border text-primary" role="status"
                                                 style="display:none; position:absolute; right:-30px; top:50%;">
                                                <span class="sr-only">Loading...</span>
                                            </div>
                                        </div>
                                    </div>

                                </div>
                                <div class="item form-group form-required">
                                    <div class="col-md-6 col-xs-12">
                                        <label for="cnvt-passport-no" class="label-align col-md-5">Passport
                                            No</label>
                                        <div class="col-md-7 col-xs-12">
                                            <input type="text" name="passportNo" id="cnvt-passport-no"
                                                   class="form-control"
                                                   placeholder="Passport No">
                                        </div>
                                    </div>
                                    <div class="col-md-6 col-xs-12">
                                        <label for="cnvt-pin-number" class="label-align col-md-5">Pin
                                            No <span class="required">*</span> </label>
                                        <div class="col-md-7 col-xs-12">
                                            <input type="text" name="pinNo" id="cnvt-pin-number"
                                                   class="form-control"
                                                   placeholder="Pin Number" required>
                                            <div id="pin-validation-msg" class="mt-2"></div>

                                        </div>
                                    </div>

                                </div>
                                <div class="item form-group form-required">
                                    <div class="col-md-6 col-xs-12">
                                        <label for="cnvt-fname" class="label-align col-md-5">First
                                            Name</label>
                                        <div class="col-md-7 col-xs-12">
                                            <input type="text" name="fname" id="cnvt-fname" class="form-control"
                                                   placeholder="First Name" required>
                                        </div>
                                    </div>
                                    <div class="col-md-6 col-xs-12 additionalName">
                                        <label for="cnvt-lname" class="label-align col-md-5">Last Name
                                            <span class="required">*</span></label>
                                        <div class="col-md-7 col-xs-12">
                                            <input type="text" name="lname" id="cnvt-lname" class="form-control"
                                                   placeholder="Last Name">
                                        </div>
                                    </div>
                                </div>
                                <div class="item form-group">
                                    <div class="col-md-6 col-xs-12">
                                        <label for="cnvt-other-names" class="label-align col-md-5">Other
                                            Names</label>
                                        <div class="col-md-7 col-xs-12">
                                            <input type="text" name="otherNames" id="cnvt-other-names"
                                                   class="form-control"
                                                   placeholder="Other Names" required>
                                        </div>
                                    </div>
                                    <div class="col-md-6 col-xs-12">
                                        <label for="cnvt-gender" class="label-align col-md-5"
                                               id="cnvt-lblGender">Gender</label>
                                        <div class="col-md-7 col-xs-12 gender">
                                            <select class="form-control" id="cnvt-gender" name="gender" required>
                                                <option value="">Select Gender</option>
                                                <option value="M">Male</option>
                                                <option value="F">Female</option>
                                            </select>

                                        </div>
                                    </div>
                                </div>
                                <div class="item form-group">

                                    <div class="col-md-6 col-xs-12">
                                        <label for="cnvt-clnt-title" class="label-align col-md-5">Title
                                        </label>
                                        <div class="col-md-7 col-xs-12">
                                            <input type="hidden" id="cnvt-clnt-title-id" name="clientTitle"/>
                                            <input type="hidden" id="cnvt-clnt-title-name">
                                            <div id="cnvt-clnt-title" class="form-control"
                                                 select2-url="<c:url value="/protected/clients/setups/selClientTitles"/>">

                                            </div>
                                        </div>
                                    </div>

                                    <div class="col-md-6 col-xs-12">
                                        <label for="cnvt-prosp-email" class="label-align col-md-5">Email</label>
                                        <div class="col-md-7 col-xs-12">
                                            <input type="email" name="emailAddress" id="cnvt-prosp-email"
                                                   class="form-control"
                                                   placeholder="Email Address">
                                        </div>
                                    </div>
                                </div>

                                <div class="item form-group form-required">
                                    <div class="col-md-6 col-xs-12">
                                        <label for="cnvt-address" class="label-align col-md-5">Address</label>
                                        <div class="col-md-7 col-xs-12">
                                            <textarea rows="1.2" cols=30 class="form-control" name="address"
                                                      id="cnvt-address"></textarea>
                                        </div>

                                    </div>

                                    <div class="col-md-6 col-xs-12">
                                        <label for="cnvt-town-code-lov" class="label-align col-md-5">Town</label>
                                        <div class="col-md-7 col-xs-12">
                                            <input type="hidden" id="cnvt-clnt-town-id" name="town"/>
                                            <input type="hidden" id="cnvt-clnt-town-name">
                                            <div id="cnvt-town-code-lov" class="form-control"
                                                 select2-url="<c:url value="/protected/clients/setups/selClientTown"/>">

                                            </div>
                                        </div>

                                    </div>

                                </div>
                                <div class="item form-group form-required">
                                    <div class="col-md-6 col-xs-12">
                                        <label for="cnvt-postal-code" class="label-align col-md-5">Postal
                                            Code</label>
                                        <div class="col-md-7 col-xs-12">
                                            <input type="hidden" id="cnvt-postal-code-id" name="postalCodesDef"/>
                                            <input type="hidden" id="cnvt-postal-name">
                                            <div id="cnvt-postal-code" class="form-control"
                                                 select2-url="<c:url value="/protected/clients/setups/selClientPostalCode"/>">

                                            </div>
                                        </div>

                                    </div>
                                    <div class="col-md-6 col-xs-12">
                                        <label for="cnvt-sel3" class="label-align col-md-5">Status</label>
                                        <div class="col-md-7 col-xs-12">
                                            <select class="form-control" id="cnvt-sel3" name="status" required>
                                                <option value="">Select Status</option>
                                                <option value="A">Active</option>
                                                <option value="T">Terminated</option>
                                            </select>
                                        </div>
                                    </div>
                                </div>

                                <div class="item form-group form-required">
                                    <div class="col-md-6 col-xs-12">
                                        <label for="cnvt-clnt-country" class="label-align col-md-5">Domicile
                                            Country</label>
                                        <div class="col-md-7 col-xs-12">
                                            <input type="hidden" id="cnvt-cou-id" name="country"/>
                                            <input type="hidden" id="cnvt-cou-name">
                                            <div id="cnvt-clnt-country" class="form-control"
                                                 select2-url="<c:url value="/protected/organization/countries"/>">

                                            </div>

                                        </div>
                                    </div>
                                    <div class="col-md-6 col-xs-12">
                                        <label for="cnvt-office-tel-no" class="label-align col-md-5">Tel No</label>
                                        <div class="col-md-7 col-xs-12">
                                            <input type="text" name="officeTel" id="cnvt-office-tel-no"
                                                   class="form-control"
                                                   placeholder="Tel No">
                                        </div>
                                    </div>

                                </div>
                                <div class="item form-group form-required">
                                    <%--                                    <div class="col-md-6 col-xs-12">--%>
                                    <%--                                        <label for="cnvt-sms-pref" class="label-align col-md-5">SMS Number</label>--%>
                                    <%--                                        <div class="col-md-3 col-xs-12">--%>
                                    <%--                                            <input type="hidden" id="cnvt-pref-sms-id" name="prefixId"/>--%>
                                    <%--                                            <input type="hidden" id="cnvt-pref-sms-name">--%>
                                    <%--                                            <div id="cnvt-sms-pref" class="form-control"--%>
                                    <%--                                                 select2-url="<c:url value="/protected/clients/setups/selMobilePrefix"/>">--%>

                                    <%--                                            </div>--%>
                                    <%--                                        </div>--%>
                                    <%--                                        <div class="col-md-4 col-xs-12">--%>
                                    <%--                                            <input type="text" name="smsNumber" id="cnvt-sms-no"--%>
                                    <%--                                                   class="form-control"--%>
                                    <%--                                                   placeholder="SMS No" required>--%>
                                    <%--                                        </div>--%>
                                    <%--                                    </div>--%>
                                    <div class="col-md-6 col-xs-12">
                                        <label for="cnvt-phone-pref" class="label-align col-md-5">Phone
                                            Number</label>
                                        <div class="col-md-3 col-xs-12">
                                            <input type="hidden" id="cnvt-pref-phone-id" name="prefix"/>
                                            <input type="hidden" id="cnvt-pref-phone-name">
                                            <div id="cnvt-phone-pref" class="form-control"
                                                 select2-url="<c:url value="/protected/clients/setups/selMobilePrefix"/>">

                                            </div>
                                        </div>
                                        <div class="col-md-4 col-xs-12">
                                            <input type="text" name="phoneNo" id="cnvt-clnt-phone-no"
                                                   class="form-control"
                                                   placeholder="Phone No" required>
                                        </div>
                                    </div>

                                    <div class="col-md-6 col-xs-12">
                                        <label for="segment-code" class="label-align col-md-5">Segment<span
                                                class="required">*</span></label>
                                        <div class="col-md-7 col-xs-12">
                                            <input type="hidden" id="segmentId" name="segmentId">
                                            <input type="hidden" id="segmentName" name="segmentName">
                                            <div id="segment-code" class="form-control"
                                                 select2-url="<c:url value='/protected/clients/setups/selSegment'/>"></div>
                                        </div>
                                    </div>
                                </div>
                                <div class="item form-group form-required">
                                    <div class="col-md-6 col-xs-12">
                                        <label for="cnvt-ten-branch" class="label-align col-md-5">Branch
                                            Registered</label>
                                        <div class="col-md-7">
                                            <input type="hidden" id="cnvt-obId" name="branchId"/>
                                            <input type="hidden" id="cnvt-reg-brn-name">
                                            <input type="hidden" id="cnvt-ob-name">
                                            <div id="cnvt-ten-branch" class="form-control"
                                                 select2-url="<c:url value="/protected/setups/branches"/>">

                                            </div>
                                        </div>
                                    </div>

                                </div>
                                <div class="employee-info">
                                    <div class="item form-group form-required">
                                        <div class="col-md-6 col-xs-12">
                                            <label for="cnvt-sect-def" class="col-md-5 label-align"
                                                   id="cnvt-lbl-sector">Sector</label>

                                            <div class="col-md-7 col-xs-12">
                                                <input type="hidden" id="cnvt-sect-id" name="clientSector"/>
                                                <input type="hidden" id="cnvt-sect-name">
                                                <div id="cnvt-sect-def" class="form-control"
                                                     select2-url="<c:url value="/protected/setups/selSectors"/>">

                                                </div>
                                            </div>
                                        </div>
                                        <div class="col-md-6 col-xs-12">
                                            <label for="cnvt-occ-def"
                                                   class="col-md-5 label-align">Occupation</label>

                                            <div class="col-md-7 col-xs-12">
                                                <input type="hidden" id="cnvt-occ-id" name="occupation"/>
                                                <input type="hidden" id="cnvt-occ-name">
                                                <div id="cnvt-occ-def" class="form-control"
                                                     select2-url="<c:url value="/protected/setups/selOccupations"/>">

                                                </div>
                                            </div>
                                        </div>
                                    </div>
                                </div>
                                <div class="item form-group form-required">
                                    <div class="col-md-6 col-xs-12">
                                        <label for="cnvt-dob" class="col-md-5 label-align" id="cnvt-lbl-dob">Date of
                                            Birth</label>

                                        <div class="col-md-7 col-xs-12">
                                            <div class='input-group date datepicker-input'>
                                                <input type='text' class="form-control float-right" name="dob"
                                                       id="cnvt-dob"/>
                                                <div class="input-group-addon">
                                                    <span class="fa fa-calendar"></span>
                                                </div>
                                            </div>
                                        </div>
                                    </div>
                                    <div class="col-md-6 col-xs-12">
                                        <label for="cnvt-date-reg" class="col-md-5 label-align">Date
                                            Registered</label>

                                        <div class="col-md-7 col-xs-12">
                                            <div class='input-group date datepicker-input'>

                                                <input type='text' class="form-control float-right"
                                                       name="dateregistered" id="cnvt-date-reg"/>
                                                <div class="input-group-addon">
                                                    <span class="fa fa-calendar"></span>
                                                </div>

                                            </div>
                                        </div>
                                    </div>
                                </div>
                                <div class="item form-group form-required">
                                    <div class="col-md-6 col-xs-12">
                                        <label for="cnvt-pol_start-date" class="col-md-5 label-align">Policy Start
                                            Date</label>

                                        <div class="col-md-7 col-xs-12">
                                            <div class='input-group date datepicker-input'>

                                                <input type='text' class="form-control float-right"
                                                       name="polStartDate"
                                                       id="cnvt-pol_start-date"/>
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
                <input type="hidden" id="polId_field" name="polId">
                <button data-loading-text="Saving..." id="covertPrspctClnt"
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

<div class="modal fade" id="convertCombQuot" tabindex="-1" role="dialog"
     aria-labelledby="convertCombQuotLabel" aria-hidden="true">
    <div class="modal-dialog">
        <div class="modal-content">
            <div class="modal-header">
                <h4 class="modal-title" id="convertCombQuotLabel">
                    Select Quote Product to Convert
                </h4>
                <button type="button" class="close" data-dismiss="modal"
                        aria-label="Close">
                    <span aria-hidden="true">&times;</span>
                </button>

            </div>
            <div class="modal-body">

                <div class="table-responsive" style="height: 300px !important; overflow: scroll;">
                    <table class="table table-striped table-hover table-bordered table-fixed" id="convertCombQuotTable">
                        <thead>
                        <tr>
                            <th width="1%"></th>
                            <th>Insurer</th>
                            <th>Cover Type</th>
                            <th>Risk Id</th>
                            <th>Premium</th>
                        </tr>
                        </thead>
                        <tbody>

                        </tbody>
                    </table>
                </div>

                <form id="convert-quote-form">
                    <input type="hidden" id="quote-product-id" name="quoteProductId"/>
                </form>

            </div>
            <div class="modal-footer">
                <button data-loading-text="Saving..." id="saveSelectedProduct"
                        type="button" class="btn btn-primary">
                    Submit
                </button>
                <button type="button" class="btn btn-default" data-dismiss="modal">
                    Cancel
                </button>
            </div>
        </div>
    </div>
</div>