<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="sec" uri="http://www.springframework.org/security/tags" %>
<script type="text/javascript" src="<c:url value="/js/modules/utils/select2builder.js"/>"></script>
<script type="text/javascript" src="<c:url value="/libs/rivets/rivets.js"/>"></script>

<% Long mckIdNo = (Long) session.getAttribute("mck_id_no"); %>
<script type="text/javascript">
    var mck_id_no = <%= mckIdNo %>;
</script>

<div class="x_panel">
    <div class="" role="tabpanel" data-example-id="togglable-tabs">
        <ul id="myTab" class="nav nav-tabs bar_tabs" role="tablist">
            <li role="presentation" class="active"><a href="#tab_content1"
                                                      id="home-tab" role="tab" data-toggle="tab"
                                                      aria-expanded="true">Client Details</a>
            </li>
            <li role="presentation" id="show-docs" style="display:none"><a href="#tab_content2"
                                                                           role="tab" id="profile-tab" data-toggle="tab"
                                                                           aria-expanded="false">Client Documents</a>
            </li>
        </ul>
        <div id="myTabContent" class="tab-content">
            <div role="tabpanel" class="tab-pane active"
                 id="tab_content1" aria-labelledby="home-tab">

                <form id="tenant-form" data-parsley-validate class="form-horizontal form-label-left"
                      enctype="multipart/form-data">
                    <div class="x_panel">
                        <div class="item form-group">
                            <c:if test="${empty param.type}">
                                <a href="<c:url value='/protected/clients/setups/clientslist'/> " id="back"
                                   class="btn btn-primary float-right">Back</a>
                            </c:if>
                            <c:if test="${param.type.equalsIgnoreCase('pol')}">
                                <a href="<c:url value="/protected/uw/policies/editpolicy"/>"
                                   class="btn btn-default float-right">Back to Policy</a>
                            </c:if>
                            <c:if test="${param.type.equalsIgnoreCase('med')}">
                                <a href="<c:url value="/protected/medical/policies/uwform"/>"
                                   class="btn btn-default float-right">Back to Policy</a>
                            </c:if>
                            <c:if test="${param.type.equalsIgnoreCase('life')}">
                                <a href="<c:url value="/protected/life/policies/lifeuwform"/>"
                                   class="btn btn-default float-right">Back to Policy</a>
                            </c:if>
                            <c:if test="${param.type.equalsIgnoreCase('quot')}">
                                <a href="<c:url value="/protected/quotes/editClientQuote"/>"
                                   class="btn btn-default float-right">Back to Quote</a>
                            </c:if>
                            <c:if test="${param.type.equalsIgnoreCase('medquot')}">
                                <a href="<c:url value="/protected/quotes/editMedQuote"/>"
                                   class="btn btn-default float-right">Back to Quote</a>
                            </c:if>
                            <c:if test="${param.type.equalsIgnoreCase('medmember')}">
                                <a href="<c:url value="/protected/medical/policies/editpolicy"/>"
                                   class="btn btn-default float-right">Back to Policy</a>
                            </c:if>
                            <input action="action" type="button" style="display: none" onclick="history.go(-1);"
                                   class="btn btn-primary float-left"
                                   value="Back"
                                   id="back-view-client">
                            <input type="submit" class="btn btn-primary float-right" id="btn-save-tenant" value="Save">
                            <%--  <input type="button" class="btn btn-primary float-right" id="btn-search-tenant" --%>
                            <%--         value="Pull from Core System"> --%>
                        </div>
                    </div>
                    <div class="x_panel">
                        <input type="hidden" name="tenId" id="tenId-pk">
                        <div class="item form-group">
                            <div class="col-md-6 col-xs-12 form-required">
                                <label for="clnt-client-type" class="label-align col-md-5">Client Type<span
                                        class="required">*</span></label>
                                <div class="col-md-5 col-xs-12">
                                    <input type="hidden" id="clnt-type-id" name="clientTypeId"/>
                                    <input type="hidden" id="clnt-type-name">
                                    <input type="hidden" id="clnt-type-code">
                                    <div id="clnt-client-type" class="form-control"
                                         select2-url="<c:url value="/protected/clients/setups/selClientTypes"/>">

                                    </div>
                                </div>
                                <sec:authorize access="hasAnyAuthority('ADD_CLIENT_TYPE')">
                                    <div class="col-md-2 col-xs-12">
                                        <input type="button" value="New" class="btn btn-primary btn-sm"
                                               id="btn-add-client-type" style="display:none">
                                    </div>
                                </sec:authorize>
                            </div>
                            <div class="col-md-6 col-xs-12 clientIdReference">
                                <label for="ten-id" class="label-align col-md-5">Client ID</label>
                                <div class="col-md-7 col-xs-12">
                                    <input type="text" name="tenantNumber" id="ten-id" class="form-control"
                                           placeholder="" readonly>
                                </div>
                            </div>
                        </div>
                        <div class="item form-group form-required">
                            <div class="col-md-6 col-xs-12 titleDiv">
                                <label for="clnt-title" class="label-align col-md-5">Title
                                </label>
                                <div class="col-md-6 col-xs-12 clnt-title">
                                    <input type="hidden" id="clnt-title-id" name="titleId"/>
                                    <input type="hidden" id="clnt-title-name">
                                    <div id="clnt-title" class="form-control"
                                         select2-url="<c:url value="/protected/clients/setups/selClientTitles"/>">

                                    </div>
                                </div>
                            </div>
                            <div class="col-md-6 col-xs-12 clientIdReference">
                                <label for="client-ref-no" class="label-align col-md-5">Client Reference</label>
                                <div class="col-md-7 col-xs-12">
                                    <input type="text" name="clientRef" id="client-ref-no" class="form-control"
                                           readonly>
                                </div>
                            </div>
                        </div>
                        <div class="item form-group form-required">
                            <div class="col-md-6 col-xs-12">
                                <label for="fname" class="label-align col-md-5" id="lblFirstName">First
                                    Name<span class="required">*</span></label>
                                <div class="col-md-7 col-xs-12">
                                    <input type="text" name="fname" id="fname" class="form-control"
                                           placeholder="First Name">
                                </div>
                            </div>
                            <div class="col-md-6 col-xs-12" id="otherNamesDiv">
                                <label for="other-names" class="label-align col-md-5">Other
                                    Names<span class="required othernames">*</span></label>
                                <div class="col-md-7 col-xs-12">
                                    <input type="text" name="otherNames" id="other-names" class="form-control"
                                           placeholder="Other Names">
                                </div>
                            </div>
                            <!-- Date of Birth Field -->
                            <div class="col-md-6 col-xs-12">
                                <label for="dob" class="col-md-5 label-align" id="lbl-dob">Date of Birth<span
                                        class="required">*</span></label>
                                <div class="col-md-7 col-xs-12">
                                    <input type="text" name="dob" id="dob" class="form-control"
                                           placeholder="Date of Birth">
                                </div>
                            </div>
                        </div>

                        <div class="item form-group form-required">
                            <div class="col-md-6 col-xs-12">
                                <label for="postal-code" class="label-align col-md-5">Postal Code<span class="required">*</span></label>
                                <div class="col-md-7 col-xs-12">
                                    <input type="hidden" id="postal-code-id" name="pcode"/>
                                    <input type="hidden" id="postal-name" required>
                                    <div id="postal-code" class="form-control"
                                         select2-url="<c:url value="/protected/clients/setups/selClientPostalCode"/>">

                                    </div>
                                </div>

                            </div>

                            <div class="col-md-6 col-xs-12">
                                <label for="id-no" class="label-align col-md-5" id="lbl-id-no">ID No</label>
                                <div class="col-md-7 col-xs-12">
                                    <input type="text" name="idNo" id="id-no" class="form-control"
                                           placeholder="ID No">
                                </div>

                            </div>

                        </div>
                        <div class="item form-group" id="passAndGender">
                            <div class="col-md-6 col-xs-12">
                                <label for="passport-no" class="label-align col-md-5" id="lblPassport">Passport No</label>
                                <div class="col-md-7 col-xs-12 divPassportNo">
                                    <input type="text" name="passportNo" id="passport-no" class="form-control"
                                           placeholder="Passport No">
                                </div>

                            </div>

                            <div class="col-md-6 col-xs-12">
                                <label for="gender" class="label-align col-md-5" id="lblGender">Gender</label>
                                <div class="col-md-7 col-xs-12 gender">
                                    <div class="input-group field-width">
									 <span class="input-group-addon">
									  <i class="fa fa-venus-mars"></i></span>
                                        <select class="form-control" id="gender" name="gender">
                                            <option value="">Select Gender</option>
                                            <option value="M">Male</option>
                                            <option value="F">Female</option>
                                        </select>
                                    </div>
                                </div>
                            </div>

                            <div class="col-md-6 col-xs-12">
                                <label for="clnt-country" class="label-align col-md-5">Domicile Country<span
                                        class="required">*</span></label>
                                <div class="col-md-7 col-xs-12">
                                    <input type="hidden" id="cou-id" name="couCode" required/>
                                    <input type="hidden" id="cou-name">
                                    <div id="clnt-country" class="form-control"
                                         select2-url="<c:url value='/protected/organization/countries' />"></div>
                                </div>
                            </div>
                        </div>


                        <div class="item form-group form-required">
                            <div class="col-md-6 col-xs-12">
                                <label for="address" class="label-align col-md-5">Address</label>
                                <div class="col-md-7 col-xs-12">
                                    <textarea rows="2" cols=30 class="form-control" name="address"
                                              id="address"></textarea>
                                </div>
                            </div>

                            <div class="col-md-6 col-xs-12">
                                <label for="town-code-lov" class="label-align col-md-5">Town<span
                                        class="required">*</span></label>
                                <div class="col-md-7 col-xs-12">
                                    <input type="hidden" id="clnt-town-id" name="ctCode"/>
                                    <input type="hidden" id="clnt-town-name">
                                    <div id="town-code-lov" class="form-control"
                                         select2-url="<c:url value="/protected/clients/setups/selClientTown"/>">
                                    </div>
                                </div>
                            </div>
                        </div>
                        <div class="item form-group form-required">
                            <div class="col-md-6 col-xs-12">
                                <label for="postal-code" class="label-align col-md-5">Postal Code<span
                                        class="required">*</span></label>
                                <div class="col-md-7 col-xs-12">
                                    <input type="hidden" id="postal-code-id" name="pcode"/>
                                    <input type="hidden" id="postal-name" required>
                                    <div id="postal-code" class="form-control"
                                         select2-url="<c:url value="/protected/clients/setups/selClientPostalCode"/>">
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
                                <label for="noOfUnits" class="label-align col-md-5">SMS Number</label>
                                <div class="col-md-3 col-xs-12">
                                    <input type="hidden" id="pref-sms-id" name="smsPrefixId"/>
                                    <input type="hidden" id="pref-sms-name">
                                    <div id="sms-pref" class="form-control"
                                         select2-url="<c:url value="/protected/clients/setups/selMobilePrefix"/>">

                                    </div>
                                    <input type="button" value="New" class="btn btn-primary btn-sm"
                                           id="btn-add-sms-prefix" style="display:none">
                                </div>
                                <div class="col-md-4 col-xs-12">
                                    <input type="text" name="smsNumber" id="sms-no" class="form-control"
                                           placeholder="SMS No">
                                </div>
                            </div>
                            <div class="col-md-6 col-xs-12 form-required">
                                <label for="phone-pref" class="label-align col-md-5">Phone Number</label>
                                <div class="col-md-3 col-xs-12">
                                    <input type="hidden" id="pref-phone-id" name="phonePrefixId"/>
                                    <input type="hidden" id="pref-phone-name">
                                    <div id="phone-pref" class="form-control"
                                         select2-url="<c:url value="/protected/clients/setups/selMobilePrefix"/>">

                                    </div>
                                    <input type="button" value="New" class="btn btn-primary btn-sm"
                                           id="btn-add-phone-prefix" style="display:none">
                                </div>
                                <div class="col-md-4 col-xs-12">
                                    <input type="text" name="phoneNo" id="phone-no" class="form-control"
                                           placeholder="Phone No">
                                </div>
                            </div>
                        </div>
                        <div class="item form-group form-required">
                            <div class="col-md-6 col-xs-12">
                                <label for="ten-branch" class="label-align col-md-5">Branch Registered</label>
                                <div class="col-md-7">
                                    <input type="hidden" id="obId" name="obId" rv-value="tenant.branch.brnCode"/>
                                    <input type="hidden" id="reg-brn-name">
                                    <input type="hidden" id="ob-name">
                                    <div id="ten-branch" class="form-control"
                                         select2-url="<c:url value="/protected/uw/policies/allbranches"/>">

                                    </div>
                                </div>
                            </div>
                            <div class="col-md-6 col-xs-12">
                                <label for="email-address" class="label-align col-md-5">Email</label>
                                <div class="col-md-7 col-xs-12">
                                    <input type="email" name="emailAddress" id="email-address" class="form-control"
                                           placeholder="Email">
                                </div>
                            </div>
                        </div>
                        <div class="item form-group form-required">
                            <div class="col-md-6 col-xs-12 statusDiv">
                                <label for="sel3" class="label-align col-md-5">Status</label>
                                <div class="col-md-7 col-xs-12">

                                    <select class="form-control" id="sel3" name="status">
                                        <option value="">Select Status</option>
                                        <option value="A">Active</option>
                                        <option value="T">Terminated</option>
                                        <option value="B">Blacklisted</option>
                                    </select>

                                </div>
                            </div>
                            <div class="col-md-6 col-xs-12" id="myComments" style="display: none">
                                <label for="myComments" class="label-align col-md-5">Blacklisting Comment</label>
                                <div class="col-md-7 col-xs-12">

                                    <textarea class="form-control" rows="2" placeholder="Comments" name="comment"
                                              title="A reason for blacklisting or termination must be provided must be provided"
                                              required></textarea>

                                </div>
                            </div>
                        </div>

                        <div class="col-md-6 col-xs-12 authorizedLbl">
                            <label for="clnt-authorised" class="label-align col-md-5">
                                Authorised?</label>
                            <div class="col-md-7 col-xs-12">
                                <p class="form-control-static" id="clnt-authorised"></p>
                            </div>
                        </div>
                    </div>

                    <div class="employee-info">
                        <h4>Employment Information</h4>
                        <hr>
                        <div class="item form-group">
                            <label for="sect-def" class="col-md-3 label-align" id="lbl-sector">Sector</label>

                            <div class="col-md-4 col-xs-12">
                                <input type="hidden" id="sect-id" name="sectCode"/>
                                <input type="hidden" id="sect-name">
                                <div id="sect-def" class="form-control"
                                     select2-url="<c:url value="/protected/setups/selSectors"/>">

                                </div>
                            </div>
                        </div>
                        <div class="item form-group">
                            <label for="occ-def" class="col-md-3 label-align" id="lbl-occ">Occupation</label>

                            <div class="col-md-4 col-xs-12">
                                <input type="hidden" id="occ-id" name="occCode"/>
                                <input type="hidden" id="occ-name">
                                <div id="occ-def" class="form-control"
                                     select2-url="<c:url value="/protected/setups/selOccupations"/>">

                                </div>
                            </div>
                        </div>
                    </div>
                    <hr>
                    <h4>Other Information</h4>
                    <hr>
                    <%--            <div class="item form-group">--%>
                    <%--                <label for="dob" class="col-md-3 label-align" id="lbl-dob">Date of Birth<span--%>
                    <%--                        class="required">*</span></label>--%>

                    <%--                <div class="col-md-3 col-xs-12">--%>
                    <%--                    <div class='input-group date datepicker-input'>--%>
                    <%--                        <input type='text' class="form-control float-right" name="dob" id="dob"/>--%>
                    <%--                        <div class="input-group-addon">--%>
                    <%--                            <span class="fa fa-calendar"></span>--%>
                    <%--                        </div>--%>
                    <%--                    </div>--%>
                    <%--                </div>--%>
                    <%--            </div>--%>
                    <div class="item form-group">
                        <label for="date-reg" class="col-md-3 label-align">Date Registered</label>

                        <div class="col-md-3 col-xs-12">
                            <div class='input-group date datepicker-input'>
                                <c:choose>
                                    <c:when test="${tenId ==-2000}">
                                        <input type='text' class="form-control float-right" name="dateregistered"
                                               id="date-reg"/>
                                        <div class="input-group-addon">
                                            <span class="fa fa-calendar"></span>
                                        </div>
                                    </c:when>
                                    <c:otherwise>
                                        <input type='text' class="form-control float-right" name="dateregistered"
                                               id="date-reg" readonly/>
                                        <div class="input-group-addon">
                                            <span class="fa fa-calendar"></span>
                                        </div>
                                    </c:otherwise>
                                </c:choose>

                            </div>
                        </div>
                    </div>
                    <div class="item form-group">
                        <label for="dt-terminated" class="col-md-3 label-align">Date Terminated</label>

                        <div class="col-md-3 col-xs-12">
                            <div class='input-group date datepicker-input'>
                                <input type='text' class="form-control float-right" name="dateterminated"
                                       id="dt-terminated" disabled/>
                                <div class="input-group-addon">
                                    <span class="fa fa-calendar"></span>
                                </div>
                            </div>
                        </div>
                    </div>
                    <div class='spacer'></div>

                    <div class="item form-group">
                        <div class="col-md-6 col-xs-12 form-required">
                            <label for="avatar" class="label-align col-md-5" id="client-photo">
                                Photo</label>
                            <div class="col-md-7 col-xs-12">
                                <div class="kv-avatar center-block" style="width: 200px">
                                    <input name="file" type="file" id="avatar" class="file-loading">

                                </div>
                            </div>
                        </div>
                    </div>
                    <div role="tabpanel" class="tab-pane fade"
                         id="tab_content2" aria-labelledby="profile-tab" style="display:none">
                        <button class="btn btn-primary btn btn-info" id="btn-add-docs">New</button>
                        <div class="card-box table-responsive">
                            <table id="clientDocsList" class="table table-striped" style="width: 100%">
                                <thead>
                                <tr class="headings">
                                    <th>Document ID</th>
                                    <th>Document Desc</th>
                                    <th>File Name</th>
                                    <th>File Verifier</th>
                                    <th>File Ref. No.</th>
                                    <th width="5%"></th>
                                    <th width="5%"></th>
                                    <th width="5%"></th>
                                </tr>
                                </thead>
                            </table>
                        </div>
                    </div>
                </form>
            </div>
        </div>

    </div>

    <div class="modal fade" id="prefixModal" tabindex="-1" role="dialog"
         aria-labelledby="prefixModalLabel" aria-hidden="true">
        <div class="modal-dialog">
            <div class="modal-content">
                <div class="modal-header">
                    <h4 class="modal-title" id="prefixModalLabel">
                        Add Mobile Prefix
                    </h4>
                    <button type="button" class="close" data-dismiss="modal"
                            aria-label="Close">
                        <span aria-hidden="true">&times;</span>
                    </button>

                </div>
                <div class="modal-body">
                    <input type="hidden" class="form-control" id="prefix-cou-pk">
                    <form id="prefix-form" class="form-horizontal">
                        <input type="hidden" class="form-control" id="prefix-id" name="prefixId">
                        <input type="hidden" class="form-control" id="prefix-country" name="country">

                        <div class="item form-group">
                            <label for="prefix-name" class="col-md-3 label-align">Mobile Prefix</label>

                            <div class="col-md-8">
                                <input type="text" class="editUserCntrls form-control"
                                       id="prefix-name" name="prefixName"
                                       required>
                            </div>
                        </div>
                        <div class="item form-group">
                            <label for="mob-provider" class="col-md-3 label-align">Mobile Provider</label>

                            <div class="col-md-8">
                                <div class="col-md-7 col-xs-12">
                                    <input type="hidden" id="mob-prefix-provd-id" name="providers"/>
                                    <div id="mob-provider" class="form-control"
                                         select2-url="<c:url value="/protected/clients/setups/selMobProviders"/>">

                                    </div>
                                </div>
                                <div class="col-md-1 col-xs-12">
                                    <input type="button" value="New" class="btn btn-success btn-sm"
                                           id="btn-add-mob-provider">
                                </div>
                            </div>
                        </div>
                    </form>
                </div>
                <div class="modal-footer">
                    <button data-loading-text="Saving..." id="savePrefixBtn"
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

    <div class="modal fade" id="clntTypeModesModal" tabindex="-1" role="dialog"
         aria-labelledby="clntTypeModesModalLabel" aria-hidden="true">
        <div class="modal-dialog">
            <div class="modal-content">
                <div class="modal-header">
                    <h4 class="modal-title" id="clntTypeModesModalLabel">
                        Edit/Add Client Type
                    </h4>
                    <button type="button" class="close" data-dismiss="modal"
                            aria-label="Close">
                        <span aria-hidden="true">&times;</span>
                    </button>

                </div>
                <div class="modal-body">
                    <form id="client-type-form" class="form-horizontal">
                        <input type="hidden" class="form-control" id="type-id" name="typeId">
                        <div class="item form-group">
                            <label for="brn-id" class="col-md-3 label-align">Client Type</label>

                            <div class="col-md-8">
                                <select class="form-control" id="clnt-type" name="clientType" required>
                                    <option value="">Select Client Type</option>
                                    <option value="I">Individual</option>
                                    <option value="C">Corporate</option>
                                </select>
                            </div>
                        </div>
                        <div class="item form-group">
                            <label for="unit-id" class="col-md-3 label-align">Client Type Desc</label>

                            <div class="col-md-8">
                                <input type="text" class="form-control" id="type-desc"
                                       name="typeDesc" required>
                            </div>
                        </div>
                    </form>
                </div>
                <div class="modal-footer">
                    <button data-loading-text="Saving..." id="saveClientType"
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

    <div class="modal fade" id="clntTitleModesModal" tabindex="-1" role="dialog"
         aria-labelledby="clntTitleModesModalLabel" aria-hidden="true">
        <div class="modal-dialog">
            <div class="modal-content">
                <div class="modal-header">
                    <h4 class="modal-title" id="clntTitleModesModalLabel">
                        Edit/Add Client Title
                    </h4>
                    <button type="button" class="close" data-dismiss="modal"
                            aria-label="Close">
                        <span aria-hidden="true">&times;</span>
                    </button>

                </div>
                <div class="modal-body" id="branch_model">
                    <form id="client-title-form" class="form-horizontal">
                        <input type="hidden" class="form-control" id="title-id" name="titleId">

                        <div class="item form-group">
                            <label for="unit-id" class="col-md-3 label-align">Client Title</label>

                            <div class="col-md-8">
                                <input type="text" class="form-control" id="title-desc"
                                       name="titleName" required>
                            </div>
                        </div>

                    </form>
                </div>
                <div class="modal-footer">
                    <button data-loading-text="Saving..." id="saveClientTitle"
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


    <div class="modal fade" id="mobProviderModal" tabindex="-1" role="dialog"
         aria-labelledby="mobProviderModalLabel" aria-hidden="true">
        <div class="modal-dialog">
            <div class="modal-content">
                <div class="modal-header">
                    <h4 class="modal-title" id="mobProviderModalLabel">
                        Add Mobile Provider
                    </h4>
                    <button type="button" class="close" data-dismiss="modal"
                            aria-label="Close">
                        <span aria-hidden="true">&times;</span>
                    </button>

                </div>
                <div class="modal-body">
                    <form id="mob-provider-form" class="form-horizontal">
                        <input type="hidden" class="form-control" id="provider-id" name="providerId">

                        <div class="item form-group">
                            <label for="unit-id" class="col-md-3 label-align">Mobile Provider</label>

                            <div class="col-md-8">
                                <input type="text" class="form-control" id="provider-name"
                                       name="providerName" required>
                            </div>
                        </div>

                    </form>
                </div>
                <div class="modal-footer">
                    <button data-loading-text="Saving..." id="saveMobProvider"
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
                    <button type="button" class="btn btn-default" data-dismiss="modal">
                        Cancel
                    </button>
                </div>
            </div>
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
                            <label for="clnt-doc-name" class="col-md-3 label-align">Document Type</label>

                            <div class="col-md-8">
                                <p class="form-control-static" id="clnt-doc-name"></p>
                            </div>
                        </div>
                        <div class="item form-group">
                            <label for="upload-sht-id" class="col-md-3 label-align">File Ref. No</label>

                            <div class="col-md-8">
                                <input type="text" class="form-control" id="upload-sht-id"
                                       name="fileId">
                            </div>
                        </div>
                        <div class="item form-group">
                            <label for="clnt-upload-name" class="col-md-3 label-align">Uploaded File Name</label>

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
                        <button type="button" class="btn btn-default" data-dismiss="modal">
                            Close
                        </button>
                    </div>
                </div>
            </form>
        </div>
    </div>


    <div class="modal fade" id="clientSearchModal" tabindex="-1" role="dialog"
         aria-labelledby="clientSearchModalLabel" aria-hidden="true">
        <div class="modal-dialog modal-lg">
            <div class="modal-content">
                <div class="modal-header">
                    <h4 class="modal-title" id="clientSearchModalLabel">Search Client from T24</h4>
                    <button type="button" class="close" data-dismiss="modal"
                            aria-label="Close">
                        <span aria-hidden="true">&times;</span>
                    </button>

                </div>
                <div class="modal-body">
                    <form class="form-horizontal">
                        <div class="item form-group">
                            <label for="brn-id" class="col-md-2 label-align">Legal ID</label>

                            <div class="col-md-3">
                                <input type="text" class="form-control" id="legal-id-search"
                                >
                            </div>
                            <label for="brn-id" class="col-md-2 label-align">Cust ID</label>

                            <div class="col-md-3">
                                <input type="text" class="form-control" id="cust-id-search"
                                >
                            </div>
                            <div class="col-md-1">
                                <button id="searchClients"
                                        type="button" class="btn btn-primary">
                                    Search
                                </button>
                            </div>
                        </div>
                    </form>
                    <div style="height: 300px !important; overflow: scroll;">
                        <table class="table table-striped table-hover table-bordered table-fixed" id="clientSearch">
                            <thead>
                            <tr>
                                <th width="12%">Name</th>
                                <th width="4%">Pin</th>
                                <th width="4%">Title</th>
                                <th width="4%">Phone</th>
                                <th width="4%">Email</th>
                                <th width="4%">Address</th>
                                <th width="4%">Cust Code</th>
                                <th width="4%">Legal ID</th>
                                <th width="4%">Birth Date</th>
                            </tr>
                            </thead>
                            <tbody>

                            </tbody>
                        </table>
                    </div>
                </div>
                <div class="modal-footer">
                    <button type="button" class="btn btn-default" data-dismiss="modal">
                        Cancel
                    </button>
                </div>
            </div>
        </div>
    </div>

    <div class="modal fade bs-example-modal-sm" id="myPleaseWait" tabindex="-1"
         role="dialog" aria-hidden="true" data-backdrop="static">
        <div class="modal-dialog modal-sm">
            <div class="modal-content">
                <div class="modal-header">
                    <h4 class="modal-title">
                    <span class="glyphicon glyphicon-time">
                    </span>Please Wait
                    </h4>
                </div>
                <div class="modal-body">
                    <div class="progress">
                        <div class="progress-bar progress-bar-info
                    progress-bar-striped active"
                             style="width: 100%">
                        </div>
                    </div>
                </div>
            </div>
        </div>
    </div>
</div>

<script type="text/javascript">
    $(document).ready(function() {
        $(".datepicker-input").each(function() {
            $(this).datetimepicker({
                format: 'DD/MM/YYYY'
            });
        });

        var mckIdNo = "${mck_id_no}";  // Make sure mck_id_no is set
        var url = SERVLET_CONTEXT + "/protected/viewMakerTask";


        $.ajax({
            url: url,
            method: 'GET',
            data: {
                taskId: mckIdNo,
                // taskType: taskType
            },
            success: function(client) {
                console.log(client);
                populateClientDetails(client);
            },
            error: function(xhr, status, error) {
                console.error('Error fetching client data:', error);
            }
        });

        function populateClientDetails(client) {
            $("#ten-id").val(client.tenantNumber);
            $("#clnt-type-id").val(client.clientTypeId);
            $("#clnt-type-name").val(client.clientTypeDesc);
            populateClientTypeLov();
            $("#clnt-title-id").val(client.titleId);
            $("#clnt-title-name").val(client.titleName);
            populateTitlesLov();
            $("#client-ref-no").val(client.clientRef);
            $("#fname").val(client.fname);
            $("#other-names").val(client.otherNames);
            $("#pin-no").val(client.pinNo);
            $("#address").val(client.address);
            $("#clnt-town-id").val(client.ctCode);
            $("#clnt-town-name").val(client.ctName);
            createClientTownLov();
            $("#postal-code-id").val(client.pcode);
            $("#postal-name").val(client.postalName);
            populatePostalCode(client.ctCode);
            $("#cou-id").val(client.couCode);
            $("#cou-name").val(client.couName);
            populateCountryLov();
            $("#id-no").val(client.idNo);
            $("#passport-no").val(client.passportNo);
            $("#gender").val(client.gender);
            $("#office-tel-no").val(client.officeTel);
            $("#resident-status").val(client.residentStatus);
            $("#pref-sms-id").val(client.smsPrefixId);
            $("#pref-sms-name").val(client.smsPrefixName);
            populateSmsPrefix(client.couCode);
            $("#sms-no").val(client.smsNumber);
            $("#pref-phone-id").val(client.phonePrefixId);
            $("#pref-phone-name").val(client.phonePrefixName);
            populateSmsPrefix(client.couCode);
            $("#phone-no").val(client.phoneNo);
            $("#email-address").val(client.emailAddress);
            $("#obId").val(client.obId);
            $("#ob-name").val(client.obName);
            populateBranchA();
            $("#sel3").val(client.status);
            $("#dob").val(moment(client.dob).format('DD/MM/YYYY'));
            $("#date-reg").val(moment(client.dateregistered).format('DD/MM/YYYY'));
            if (client.dateterminated !== null) {
                $("#dt-terminated").val(moment(client.dateterminated).format('DD/MM/YYYY'));
            }
            $("#sect-id").val(client.sectCode);
            $("#sect-name").val(client.sectName);
            createSectorSelect();
            $("#occ-id").val(client.occCode);
            $("#occ-name").val(client.occName);
            populateOccupations(client.sectCode);
            $("#avatar").val(client.file);
            $("#back").hide();
            $("#back-view-client").show();
            // getClientDocs();
            if (client.clientType === 'C' || client.clientType === '') {
                $("#gender,.gender,#lblGender").hide();
                $("#lbl-dob").html("Date of Incorporation");
                $("#lbl-id-no").html("Registration No.");
                $(".employee-info").hide();
            } else if (client.clientType === 'I') {
                $("#gender,.gender,#lblGender").show();
                $("#lbl-dob").html("Date of Birth");
                $("#lbl-id-no").html("ID No.");
                $(".employee-info").show();
            }
        }


        function getClientDocs(clientId){
            var url = SERVLET_CONTEXT+"/protected/clients/setups/clientDocs/"+clientId;
            var currTable = $('#clientDocsList').DataTable( {
                "processing": true,
                "serverSide": true,
                autoWidth: true,
                "ajax": {
                    'url': url,
                },
                lengthMenu: [ [10,15,20], [10,15,20] ],
                pageLength: 10,
                destroy: true,
                "columns": [
                    { "data": "cdId",
                        "render": function ( data, type, full, meta ) {

                            return full.reqShtDesc;
                        }
                    },
                    { "data": "cdId",
                        "render": function ( data, type, full, meta ) {

                            return full.reqDesc;
                        }
                    },
                    { "data": "uploadedFileName" },
                    // { "data": "checkSum" },
                    { "data": "fileId" },
                    {
                        "data": "cdId",
                        "render": function ( data, type, full, meta ) {
                            if(full.authStatus && full.authStatus==="A"){
                                // return '<button type="button" class="btn btn-success btn btn-info btn-sm" data-docs='+encodeURI(JSON.stringify(full)) + ' onclick="editClientDocs(this);" disabled><i class="fa fa-pencil-square-o"></button>';
                            }else
                                return '<button type="button" class="btn btn-success btn btn-info btn-sm" data-docs='+encodeURI(JSON.stringify(full)) + ' onclick="editClientDocs(this);"><i class="fa fa-pencil-square-o"></button>';
                        }

                    },
                    {
                        "data": "cdId",
                        "render": function ( data, type, full, meta ) {
                            return '<button type="button" class="btn btn-success btn btn-info btn-sm" type="button" data-docs=' + encodeURI(JSON.stringify(full)) + ' onclick="downloadClientDoc(this);"><i class="fa fa-file-archive-o"></button>';

                        }

                    },
                    {
                        "data": "cdId",
                        "render": function ( data, type, full, meta ) {
                            if(full.authStatus && full.authStatus==="A"){
                                // return '<button type="button" class="btn btn-danger btn btn-info btn-sm" data-docs='+encodeURI(JSON.stringify(full)) + ' onclick="deleteClientDoc(this);" disabled><i class="fa fa-trash-o"></button>';
                            }else
                                return '<button type="button" class="btn btn-danger btn btn-info btn-sm" data-docs='+encodeURI(JSON.stringify(full)) + ' onclick="deleteClientDoc(this);"><i class="fa fa-trash-o"></button>';
                        }

                    },
                ]
            } );
            return currTable;
        }

        function createMobProviderSel() {
            if ($("#mob-provider").filter("div").html() != undefined) {
                Select2Builder.initAjaxSelect2({
                    containerId: "mob-provider",
                    sort: 'providerName',
                    change: function (e, a, v) {
                        $("#mob-prefix-provd-id").val(e.added.providerId);
                    },
                    formatResult: function (a) {
                        return a.providerName
                    },
                    formatSelection: function (a) {
                        return a.providerName
                    },
                    initSelection: function (element, callback) {

                    },
                    id: "providerId",
                    placeholder: "Select Provider",
                });
            }
        }


        function populateSmsPrefix(couCode) {
            if ($("#sms-pref").filter("div").html() != undefined) {
                Select2Builder.initAjaxSelect2({
                    containerId: "sms-pref",
                    sort: 'prefixName',
                    change: function (e, a, v) {
                        console.log(e.added.prefixId);
                        $("#pref-sms-id").val(e.added.prefixId);
                    },
                    formatResult: function (a) {
                        return a.prefixName
                    },
                    formatSelection: function (a) {
                        return a.prefixName
                    },
                    initSelection: function (element, callback) {
                        var code = $("#pref-sms-id").val();
                        var name = $("#pref-sms-name").val();
                        var data = {prefixName: name, prefixId: code};
                        callback(data);
                    },
                    id: "prefixId",
                    placeholder: "Prefix",
                    params: {couCode: couCode}

                });
            }

            if ($("#phone-pref").filter("div").html() != undefined) {
                Select2Builder.initAjaxSelect2({
                    containerId: "phone-pref",
                    sort: 'prefixName',
                    change: function (e, a, v) {
                        console.log(e.added.prefixId);
                        $("#pref-phone-id").val(e.added.prefixId);
                    },
                    formatResult: function (a) {
                        return a.prefixName
                    },
                    formatSelection: function (a) {
                        return a.prefixName
                    },
                    initSelection: function (element, callback) {
                        var code = $("#pref-phone-id").val();
                        var name = $("#pref-phone-name").val();
                        var data = {prefixName: name, prefixId: code};
                        callback(data);
                    },
                    id: "prefixId",
                    placeholder: "Prefix",
                    params: {couCode: couCode}

                });
            }
        }

        function populatePostalCode(townCode) {
            if ($("#postal-code").filter("div").html() != undefined) {
                Select2Builder.initAjaxSelect2({
                    containerId: "postal-code",
                    sort: 'zipCode',
                    change: function (e, a, v) {
                        $("#postal-code-id").val(e.added.pcode);
                    },
                    formatResult: function (a) {
                        return a.zipCode
                    },
                    formatSelection: function (a) {
                        return a.zipCode
                    },
                    initSelection: function (element, callback) {
                        var code = $("#postal-code-id").val();
                        var name = $("#postal-name").val();
                        var data = {zipCode: name, pcode: code};
                        callback(data);
                    },
                    id: "pcode",
                    placeholder: "Select Postal Code",
                    params: {townCode: townCode}

                });
            }
        }

        function createSectorSelect() {
            if ($("#sect-def").filter("div").html() != undefined) {
                Select2Builder.initAjaxSelect2({
                    containerId: "sect-def",
                    sort: 'name',
                    change: function (e, a, v) {
                        $("#sect-id").val(e.added.code);
                        populateOccupations(e.added.code);
                    },
                    formatResult: function (a) {
                        return a.name
                    },
                    formatSelection: function (a) {
                        return a.name
                    },
                    initSelection: function (element, callback) {
                        var code = $("#sect-id").val();
                        var name = $("#sect-name").val();
                        var data = {name: name, code: code};
                        callback(data);
                    },
                    id: "code",
                    placeholder: "Select Sector",
                });
            }
        }

        function populateOccupations(sectCode) {
            if ($("#occ-def").filter("div").html() != undefined) {
                Select2Builder.initAjaxSelect2({
                    containerId: "occ-def",
                    sort: 'name',
                    change: function (e, a, v) {
                        $("#occ-id").val(e.added.code);
                    },
                    formatResult: function (a) {
                        return a.name
                    },
                    formatSelection: function (a) {
                        return a.name
                    },
                    initSelection: function (element, callback) {
                        var code = $("#occ-id").val();
                        var name = $("#occ-name").val();
                        var data = {name: name, code: code};
                        callback(data);
                    },
                    id: "code",
                    placeholder: "Select Occupation",
                    params: {sectCode: sectCode}

                });
            }
        }

        function populateTitlesLov() {
            if ($("#clnt-title").filter("div").html() != undefined) {
                Select2Builder.initAjaxSelect2({
                    containerId: "clnt-title",
                    sort: 'titleName',
                    change: function (e, a, v) {
                        $("#clnt-title-id").val(e.added.titleId);
                        $("#clnt-title-name").val(e.added.titleName);
                    },
                    formatResult: function (a) {
                        return a.titleName
                    },
                    formatSelection: function (a) {
                        return a.titleName
                    },
                    initSelection: function (element, callback) {
                        var code = $("#clnt-title-id").val();
                        var name = $("#clnt-title-name").val();
                        var data = {titleName: name, titleId: code};
                        callback(data);
                    },
                    id: "titleId",
                    placeholder: "Select Title",

                });
            }
        }

        function populateCountryLov() {
            if ($("#clnt-country").filter("div").html() != undefined) {
                Select2Builder.initAjaxSelect2({
                    containerId: "clnt-country",
                    sort: 'couName',
                    change: function (e, a, v) {
                        $("#cou-id").val(e.added.couCode);
                        $("#cou-prefix").val(e.added.prefix);
                        populateSmsPrefix(e.added.couCode);
                    },
                    formatResult: function (a) {
                        return a.couName
                    },
                    formatSelection: function (a) {
                        return a.couName
                    },
                    initSelection: function (element, callback) {
                        var code = $("#cou-id").val();
                        var name = $("#cou-name").val();
                        var data = {couName: name, couCode: code};
                        callback(data);
                    },
                    id: "couCode",
                    placeholder: "Select Country",

                });
            }
        }

        function populateBranchA() {
            if ($("#ten-branch").filter("div").html() != undefined) {
                Select2Builder.initAjaxSelect2({
                    containerId: "ten-branch",
                    sort: 'obName',
                    change: function (e, a, v) {
                        $("#obId").val(e.added.obId);
                        $("#ob-name").val(e.added.obName)
                    },
                    formatResult: function (a) {
                        return a.obName
                    },
                    formatSelection: function (a) {
                        return a.obName
                    },
                    initSelection: function (element, callback) {
                        var code = $("#obId").val();
                        var name = $("#ob-name").val();
                        //model.tenant.branch.brnCode = code;
                        var data = {obName: name, obId: code};
                        callback(data);
                    },
                    id: "obId",
                    placeholder: "Select Branch",

                });
            }

        }

        function populateClientTypeLov() {
            if ($("#clnt-client-type").filter("div").html() != undefined) {
                Select2Builder.initAjaxSelect2({
                    containerId: "clnt-client-type",
                    sort: 'clientType',
                    change: function (e, a, v) {
                        $("#clnt-type-id").val(e.added.typeId);
                        $("#clnt-type-name").val(e.added.typeDesc);
                        $("#clnt-type-code").val(e.added.clientType);
                        if (e.added.clientType === 'C' || e.added.clientType === '') {
                            $("#gender,.gender,#lblGender").hide();
                            $("#lbl-dob").html("Date of Incorporation");
                            $("#lbl-id-no").html("Registration No.");
                            $("#clnt-title").select2("enable", false);
                            $('#clnt-title').select2('val', null);
                            $("#clnt-title-id").val("");
                            $("#otherNamesDiv").hide();
                            $("#btn-search-tenant").hide();
                            $(".clientIdReference").hide();
                            $("#lblFirstName").html("Company Name");
                            $(".titleDiv").hide();
                            $("span.othernames").hide();
                            $(".statusDiv").hide();
                            $("#lblPassport,.divPassportNo, #passport-no").hide();
                            $("#client-photo").html("Logo");
                            $(".employee-info").hide();
                            $(".authorizedLbl").hide();
                        } else if (e.added.clientType === 'I') {
                            $(".titleDiv").show();
                            $("#gender,.gender,#lblGender").show();
                            $("#btn-search-tenant").hide();
                            $("#lbl-dob").html("Date of Birth");
                            $("#lbl-id-no").html("ID No.");
                            $("#clnt-title").select2("enable", true);
                            $('#clnt-title').select2('val', null);
                            $("#clnt-title-id").val("");
                            $("#otherNamesDiv").show();
                            $("span.othernames").show();
                            $("#lblPassport,.divPassportNo, #passport-no").show();
                            $("#client-photo").html("Photo");
                            $(".employee-info").show();
                            $(".statusDiv").hide();
                            $(".authorizedLbl").hide();
                            $(".clientIdReference").hide();
                        }
                    },
                    formatResult: function (a) {
                        return a.typeDesc
                    },
                    formatSelection: function (a) {
                        return a.typeDesc
                    },
                    initSelection: function (element, callback) {
                        var code = $("#clnt-type-id").val();
                        var name = $("#clnt-type-name").val();
                        var data = {typeDesc: name, typeId: code};
                        callback(data);
                    },
                    id: "typeId",
                    placeholder: "Select Client Type",

                });
            }
        }

        function createClientTownLov() {
            if ($("#town-code-lov").filter("div").html() != undefined) {
                Select2Builder.initAjaxSelect2({
                    containerId: "town-code-lov",
                    sort: 'ctName',
                    change: function (e, a, v) {
                        $("#clnt-town-id").val(e.added.ctCode);
                        populatePostalCode(e.added.ctCode);
                    },
                    formatResult: function (a) {
                        return a.ctName
                    },
                    formatSelection: function (a) {
                        return a.ctName
                    },
                    initSelection: function (element, callback) {
                        var code = $("#clnt-town-id").val();
                        var name = $("#clnt-town-name").val();
                        var data = {ctName: name, ctCode: code};
                        callback(data);
                    },
                    id: "ctCode",
                    placeholder: "Select Town",
                });
            }
        }


        function defaultCountry() {
            $.ajax({
                url: 'getDefaultCountry',
                type: 'GET',
                processData: false,
                contentType: false,
                success: function (s) {
                    if (s) {
                        $("#cou-id").val(s.couCode);
                        $("#cou-prefix").val(s.prefix);
                        $("#cou-name").val(s.couName);
                        populateCountryLov();
                        populateSmsPrefix(s.couCode);
                    }
                },
                error: function (xhr, error) {

                }
            });

        }
    });
</script>


