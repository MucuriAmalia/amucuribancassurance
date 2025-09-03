<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<div class="modal fade" id="createProspectModal" tabindex="-1" role="dialog"
aria-labelledby="createProspectModalLabel" aria-hidden="true">
<div class="modal-dialog">
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

            <form id="prospect-form" class="form-horizontal">
                <input type="hidden" class="form-control" id="prospect-id-pk" name="tenId">
                <input type="hidden" id="prosp-quot-prd-pk" name="prodId">
                <input type="hidden" id="prs-sht-desc" name="prospShtDesc">

                <div class="item form-group">
                    <label for="clnt-client-type" class="label-align col-md-3">Prospect Type</label>
                    <div class="col-md-9 col-xs-12">
                        <input type="hidden" id="clnt-type-id" name="clientTypeId"/>
                        <input type="hidden" id="clnt-type-name">
                        <div id="clnt-client-type" class="form-control"
                             select2-url="<c:url value="/protected/clients/setups/selClientTypes"/>" >

                        </div>

                    </div>
                </div>
                <div class="item form-group firstNameDiv">
                    <label for="fname" class="label-align col-md-3">First
                        Name<span class="required">*</span></label>
                    <div class="col-md-9 col-xs-12">
                        <input type="text" name="fname" id="fname" class="form-control"
                               placeholder="First Name" required>
                    </div>
                </div>
                <div class="item form-group additionalName">
                    <label for="other-names" class="label-align col-md-3">Other
                        Names<span class="required">*</span></label>
                    <div class="col-md-9 col-xs-12">
                        <input type="text" name="otherNames" id="other-names" class="form-control"
                               placeholder="Other Names" required>
                    </div>
                </div>
                <div class="item form-group">
                    <label for="phone-no" class="label-align col-md-3">Phone
                        Number</label>
                    <div class="col-md-9 col-xs-12">
                        <input type="text" name="phoneNo" id="phone-no" class="form-control"
                               placeholder="Phone Number">
                    </div>
                </div>
                <div class="item form-group">
                    <label for="prosp-email" class="label-align col-md-3">Email</label>
                    <div class="col-md-9 col-xs-12">
                        <input type="email" name="emailAddress" id="prosp-email" class="form-control"
                               placeholder="Email Address">
                    </div>
                </div>
                <div class="item form-group">
                    <label for="gender" class="label-align col-md-3" id="lblGender">Gender</label>
                    <div class="col-md-9 col-xs-12 gender">

                        <select class="form-control" id="gender" name="gender">
                            <option value="">Select Gender</option>
                            <option value="M">Male</option>
                            <option value="F">Female</option>
                        </select>

                    </div>
                </div>
                <div class="item form-group dobDiv">
                    <label for="dob" class="col-md-3 label-align" id="lbl-dob">Date of Birth</label>

                    <div class="col-md-9 col-xs-12">
                        <div class='input-group date datepicker-input'>
                            <input type='text' class="form-control" name="dob" id="prs-dob"/>
                            <div class="input-group-addon">
                                <span class="fa fa-calendar"></span>
                            </div>
                        </div>
                    </div>
                </div>
                <div class="item form-group">
                    <label for="selCategory" class="label-align col-md-3">Category</label>
                    <div class="col-md-9 col-xs-12">

                        <select class="form-control" id="selCategory" name="category" required>
                            <option value="">Select Category</option>
                            <option value="H">Hot</option>
                            <option value="W">Warm</option>
                            <option value="C">Cold</option>
                        </select>

                    </div>
                </div>
                <div class="item form-group prop-status">
                    <label for="sel3" class="label-align col-md-3">Status</label>
                    <div class="col-md-9 col-xs-12">

                        <select class="form-control" id="sel3" name="status" required>
                            <option value="">Select Status</option>
                            <option value="A">Active</option>
                            <option value="T">Terminated</option>
                        </select>

                    </div>
                </div>
                <div class="item form-group">
                    <label for="txt-comment" class="label-align col-md-3">Comment</label>
                    <div class="col-md-9 col-xs-12">
                        <textarea  name="comment" id="txt-comment" class="form-control" rows="2"
                                   placeholder="Comment"></textarea>
                    </div>
                </div>
<%--                <div class="item form-group">--%>
<%--                    <label for="sub-agent-frm" class="label-align col-md-3">--%>
<%--                        Introducer</label>--%>
<%--                    <div class="col-md-9 col-xs-12">--%>
<%--                        <input type="hidden" id="sub-agent-id" name="acctId"/>--%>
<%--                        <input type="hidden" id="sub-agent-name">--%>
<%--                        <div id="sub-agent-frm" class="form-control"--%>
<%--                             select2-url="<c:url value="/protected/uw/policies/inhouseagents"/>">--%>

<%--                        </div>--%>
<%--                    </div>--%>
<%--                </div>--%>
                <div class="item form-group">
                    <label for="branch-frm" class="label-align col-md-3">
                        Branch</label>
                    <div class="col-md-9 col-xs-12">
                        <div id="edit-branch">
                            <input type="hidden" id="prsbrn-id" name="branchId"/>
                            <input type="hidden" id="prsbrn-name">
                            <div id="branch-frm" class="form-control"
                                 select2-url="<c:url value="/protected/uw/policies/uwbranches"/>">

                            </div>
                        </div>
                    </div>
                </div>


            </form>
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

<div class="modal fade" id="createClientModal" tabindex="-1" role="dialog"
     aria-labelledby="createClientModalLabel" aria-hidden="true">
    <div class="modal-dialog modal-lg">
        <div class="modal-content">
            <div class="modal-header">
                <h4 class="modal-title" id="createClientModalLabel">
                    Create/Edit Client
                </h4>
                <button type="button" class="close" data-dismiss="modal"
                        aria-label="Close">
                    <span aria-hidden="true">&times;</span>
                </button>

            </div>
           
            <div class="modal-body">
            
                <form id="tenant-form" data-parsley-validate class="form-horizontal form-label-left">
 <div class="item form-group form-required">
                        <div class="col-md-8 col-xs-12">
                            <label for="search-fname" class="label-align col-md-5">
                               Search Value<span class="required">*</span></label>
                            <div class="col-md-5 col-xs-12">
                                <input type="text" name="fname" id="search-fname" class="form-control"
                                       placeholder="Search Value" >
                                      
                            </div>
                            <div class="col-md-2 col-xs-12">
                             <button data-loading-text="Saving..." id="saveClientBtnsss"
                        type="button" class="btn btn-success">
                    Populate Client
                </button>
                </div>
                            
                        </div>
                    </div>
                    <input type="hidden" name="tenId" id="tenId-pk">
                    <div class="item form-group form-required">
                        <div class="col-md-6 col-xs-12">
                            <label for="clnt-title" class="label-align col-md-5">Title
                                <span class="required">*</span></label>
                            <div class="col-md-7 col-xs-12">
                                <input type="hidden" id="clnt-title-id" name="clientTitle"/>
                                <input type="hidden" id="clnt-title-name">
                                <div id="clnt-title" class="form-control"
                                     select2-url="<c:url value="/protected/clients/setups/selClientTitles"/>" >

                                </div>
                            </div>
                        </div>
                        <div class="col-md-6 col-xs-12">

                        </div>
                    </div>
                    <div class="item form-group form-required">
                        <div class="col-md-6 col-xs-12">
                            <label for="clnt-fname" class="label-align col-md-5">First
                                Name<span class="required">*</span></label>
                            <div class="col-md-7 col-xs-12">
                                <input type="text" name="fname" id="clnt-fname" class="form-control"
                                       placeholder="First Name" required>
                            </div>
                        </div>
                        <div class="col-md-6 col-xs-12">
                            <label for="clnt-other-names" class="label-align col-md-5">Other
                                Names<span class="required">*</span></label>
                            <div class="col-md-7 col-xs-12">
                                <input type="text" name="otherNames" id="clnt-other-names" class="form-control"
                                       placeholder="Other Names" required>
                            </div>
                        </div>
                    </div>
                    <div class="item form-group">
                        <div class="col-md-6 col-xs-12">
                            <label for="ten-id" class="label-align col-md-5">Client ID</label>
                            <div class="col-md-7 col-xs-12">
                                <input type="text" name="tenantNumber" id="ten-id" class="form-control"
                                       placeholder="" readonly>
                            </div>
                        </div>
                        <div class="col-md-6 col-xs-12 form-required">
                            <label for="clnt-client-type-1" class="label-align col-md-5">Client Type<span class="required">*</span></label>
                            <div class="col-md-7 col-xs-12">
                                <input type="hidden" id="clnt-type-id-1" name="tenantType"/>
                                <input type="hidden" id="clnt-type-name-1">
                                <div id="clnt-client-type-1" class="form-control"
                                     select2-url="<c:url value="/protected/clients/setups/selClientTypes"/>" >

                                </div>

                            </div>
                        </div>
                    </div>
                    <div class="item form-group form-required">
                        <div class="col-md-6 col-xs-12">
                            <label for="client-pin-no" class="label-align col-md-5">Pin No<span class="required">*</span></label>
                            <div class="col-md-7 col-xs-12">
                                <input type="text" name="pinNo" id="client-pin-no" class="form-control"
                                       placeholder="Pin No" required>
                            </div>
                        </div>
                        <div class="col-md-6 col-xs-12">
                            <label for="email-address" class="label-align col-md-5">Email<span class="required">*</span></label>
                            <div class="col-md-7 col-xs-12">
                                <input type="email" name="emailAddress" id="email-address" class="form-control"
                                       placeholder="Email" required>
                            </div>
                        </div>
                    </div>
                    <div class="item form-group form-required">
                        <div class="col-md-6 col-xs-12">
                            <label for="address" class="label-align col-md-5">Address</label>
                            <div class="col-md-7 col-xs-12">
                                <textarea rows="1" cols=30 class="form-control" name="address" id="address"></textarea>
                            </div>

                        </div>

                        <div class="col-md-6 col-xs-12">
                            <label for="client-id-no" class="label-align col-md-5">ID No<span class="required">*</span></label>
                            <div class="col-md-7 col-xs-12">
                                <input type="text" name="idNo" id="client-id-no" class="form-control"
                                       placeholder="ID No" required>
                            </div>

                        </div>

                    </div>
                    <div class="item form-group form-required">
                        <div class="col-md-6 col-xs-12">
                            <label for="passport-no" class="label-align col-md-5">Passport No</label>
                            <div class="col-md-7 col-xs-12">
                                <input type="text" name="passportNo" id="passport-no" class="form-control"
                                       placeholder="Passport No">
                            </div>

                        </div>

                        <div class="col-md-6 col-xs-12">
                            <label for="clnt-gender" class="label-align col-md-5" id="clnt-lblGender">Gender<span class="required">*</span></label>
                            <div class="col-md-7 col-xs-12 gender">

                                <select class="form-control" id="clnt-gender" name="gender" required>
                                    <option value="">Select Gender</option>
                                    <option value="M">Male</option>
                                    <option value="F">Female</option>
                                </select>

                            </div>
                        </div>
                    </div>
                    <div class="item form-group form-required">
                        <div class="col-md-6 col-xs-12">
                            <label for="clnt-country" class="label-align col-md-5">Domicile Country<span class="required">*</span></label>
                            <div class="col-md-7 col-xs-12">
                                <input type="hidden" id="cou-id" name="country"/>
                                <input type="hidden" id="cou-name">
                                <div id="clnt-country" class="form-control"
                                     select2-url="<c:url value="/protected/organization/countries"/>" >

                                </div>

                            </div>
                        </div>
                        <div class="col-md-6 col-xs-12">

                        </div>
                    </div>
                    <div class="item form-group form-required">
                        <div class="col-md-6 col-xs-12">
                            <label for="sms-pref" class="label-align col-md-5">SMS Number<span class="required">*</span></label>
                            <div class="col-md-3 col-xs-12">
                                <input type="hidden" id="pref-sms-id" name="smsPrefix"/>
                                <input type="hidden" id="pref-sms-name">
                                <div id="sms-pref" class="form-control"
                                     select2-url="<c:url value="/protected/clients/setups/selMobilePrefix"/>" >

                                </div>
                            </div>
                            <div class="col-md-4 col-xs-12">
                                <input type="text" name="smsNumber" id="sms-no" class="form-control"
                                       placeholder="SMS No" required>
                            </div>
                        </div>
                        <div class="col-md-6 col-xs-12">
                            <label for="phone-pref" class="label-align col-md-5">Phone Number<span class="required">*</span></label>
                            <div class="col-md-3 col-xs-12">
                                <input type="hidden" id="pref-phone-id" name="phonePrefix"/>
                                <input type="hidden" id="pref-phone-name">
                                <div id="phone-pref" class="form-control"
                                     select2-url="<c:url value="/protected/clients/setups/selMobilePrefix"/>" >

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
                            <label for="ten-branch" class="label-align col-md-5">Branch Registered<span class="required">*</span></label>
                            <div class="col-md-7">
                                <input type="hidden" id="obId" name="registeredbrn"/>
                                <input type="hidden" id="reg-brn-name">
                                <input type="hidden" id="ob-name">
                                <div id="ten-branch" class="form-control"
                                     select2-url="<c:url value="/protected/setups/branches"/>" >

                                </div>
                            </div>
                        </div>
                        <div class="col-md-6 col-xs-12">
                            <div class="item form-group">
                                <label for="dob" class="col-md-5 label-align">Date of Birth<span class="required">*</span></label>

                                <div class="col-md-7 col-xs-12">
                                    <div class='input-group date datepicker-input'>
                                        <input type='text' class="form-control float-right" name="dob" id="dob" required/>
                                        <div class="input-group-addon">
                                            <span class="fa fa-calendar"></span>
                                        </div>
                                    </div>
                                </div>
                            </div>
                        </div>
                    </div>
                    <div class="item form-group form-required">
                        <div class="col-md-6 col-xs-12">
                            <label for="sect-def" class="col-md-5 label-align">Sector</label>

                            <div class="col-md-7 col-xs-12">
                                <input type="hidden" id="sect-id" name="clientSector"/>
                                <input type="hidden" id="sect-name">
                                <div id="sect-def" class="form-control"
                                     select2-url="<c:url value="/protected/setups/selSectors"/>" >

                                </div>
                            </div>
                            </div>
                        <div class="col-md-6 col-xs-12">
                            <label for="occ-def" class="col-md-5 label-align" >Occupation</label>

                            <div class="col-md-7 col-xs-12">
                                <input type="hidden" id="occ-id" name="occupation"/>
                                <input type="hidden" id="occ-name">
                                <div id="occ-def" class="form-control"
                                     select2-url="<c:url value="/protected/setups/selOccupations"/>" >

                                </div>
                            </div>
                        </div>
                    </div>
                    <div class="item form-group form-required clnt-status">
                        <div class="col-md-6 col-xs-12">
                            <label for="selstatus" class="label-align col-md-5">Status<span class="required">*</span></label>
                            <div class="col-md-7 col-xs-12">

                                <select class="form-control" id="selstatus" name="status">
                                    <option value="">Select Status</option>
                                    <option value="A">Active</option>
                                    <option value="T">Terminated</option>
                                </select>

                            </div>
                        </div>
                        <div class="col-md-6 col-xs-12">
                        </div>
                    </div>
                </form>
            </div>
            <div class="modal-footer">
                <button data-loading-text="Saving..." id="saveClientBtn"
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