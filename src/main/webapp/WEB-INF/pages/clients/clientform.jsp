<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="sec" uri="http://www.springframework.org/security/tags" %>
<script type="text/javascript" src="<c:url value="/js/modules/utils/select2builder.js"/>"></script>
<script type="text/javascript" src="<c:url value="/libs/rivets/rivets.js"/>"></script>

<script type="text/javascript">
    var tenidpkey = ${tenId};
    function unlockForm() {
        if (tenidpkey !== -2000 && tenidpkey !== 'undefined') {
            navigator.sendBeacon(SERVLET_CONTEXT + '/protected/home/unlockForm',
                new URLSearchParams({ "formName": "CLIENT", "formId": tenidpkey })
            );
            console.log("Form unlock request sent.");
        }
    }

    window.addEventListener("beforeunload", unlockForm);
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
                      enctype="multipart/form-data" autocomplete="off">
                    <%--                    <div class="x_panel">--%>
                    <div class="item form-group">
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
                        <%--<input type="submit" class="btn btn-primary float-right" id="btn-save-tenant" value="Save"> --%>
                        <%-- <input type="button" class="btn btn-primary float-right" id="btn-search-tenant"
                                value="Pull from Core System"> --%>
                    </div>
                    <%--                    </div>--%>
                    <div class="x_panel">
                        <input type="hidden" name="tenId" id="tenId-pk">
                        <div class="item form-group">
                            <div class="col-md-6 col-xs-12 form-required">
                                <label for="clnt-client-type" class="label-align col-md-5">Client Type<span
                                        class="required">*</span></label>
                                <div class="col-md-5 col-xs-12">
                                    <input type="hidden" id="clnt-type-id" name="clientTypeId" autocomplete="new-password"/>
                                    <input type="hidden" id="clnt-type-name" autocomplete="new-password">
                                    <input type="hidden" id="clnt-type-code" autocomplete="new-password">
                                    <div id="clnt-client-type" class="form-control"
                                         select2-url="<c:url value="/protected/clients/setups/selClientTypes"/>"
                                         data-autocomplete="new-password">

                                    </div>
                                </div>
                                <sec:authorize access="hasAnyAuthority('ADD_CLIENT_TYPE')">
                                    <div class="col-md-2 col-xs-12">
                                        <input type="button" value="New" class="btn btn-primary btn-sm"
                                               id="btn-add-client-type" style="display:none">
                                    </div>
                                </sec:authorize>
                            </div>

                            <div class="col-md-6 col-xs-12">
                                <label for="ten-id" class="label-align col-md-5">Client CIF</label>
                                <div class="col-md-7 col-xs-12">
                                    <input type="text" name="clientCIF" id="ten-id" class="form-control"
                                           placeholder="" autocomplete="new-password" readonly>
                                </div>
                            </div>
                        </div>
                        <div class="item form-group form-required">
                            <div class="col-md-6 col-xs-12 clientTitleDiv">
                                <label for="clnt-title" class="label-align col-md-5">Title
                                </label>
                                <div class="col-md-6 col-xs-12 clnt-title">
                                    <input type="hidden" id="clnt-title-id" name="titleId" autocomplete="new-password"/>
                                    <input type="hidden" id="clnt-title-name" autocomplete="new-password">
                                    <div id="clnt-title" class="form-control"
                                         select2-url="<c:url value="/protected/clients/setups/selClientTitles"/>"
                                         data-autocomplete="new-password">

                                    </div>
                                </div>
                            </div>
                            <div class="col-md-6 col-xs-12 clientRefDiv">
                                <label for="client-ref-no" class="label-align col-md-5">Client Reference</label>
                                <div class="col-md-7 col-xs-12">
                                    <input type="text" name="tenantNumber" id="client-ref-no" class="form-control"
                                           readonly autocomplete="new-password">
                                </div>
                            </div>
                        </div>
                        <div class="item form-group form-required">
                            <div class="col-md-6 col-xs-12">
                                <label for="resident-status" class="label-align col-md-5">Resident Status<span
                                        class="required">*</span></label>
                                <div class="col-md-7 col-xs-12">
                                    <select id="resident-status" name="residentStatus" class="form-control" autocomplete="new-password" required>
                                        <option value="" disabled selected>Select Resident Status</option>
                                        <option value="resident">Individual-Kenyan Citizen</option>
                                        <option value="non-resident">Individual-Non-Kenyan Resident</option>
                                        <option value="non-citizen">Individual-Non-Kenyan Non-Resident</option>
                                        <option value="company">Non-Individual Company</option>
                                    </select>
                                </div>
                            </div>

                            <div class="col-md-6 col-xs-12">
                                <label for="id-no" class="label-align col-md-5" id="lbl-id-no">ID No <span
                                        class="required">*</span></label>
                                <div class="col-md-7 col-xs-12 position-relative">
                                    <input type="text" name="idNo" id="id-no" class="form-control"
                                           placeholder="ID No" autocomplete="new-password">
                                    <!-- Add this spinner somewhere in your HTML -->
                                    <div id="loading-spinner" class="spinner-border text-primary" role="status"
                                         style="display:none; position:absolute; right:-30px; top:50%;">
                                        <span class="sr-only">Loading...</span>
                                    </div>
                                </div>
                            </div>
                        </div>

                        <div class="item form-group">
                            <div class="col-md-6 col-xs-12 passportDiv">
                                <label for="passport-no" class="label-align col-md-5">Passport No</label>
                                <div class="col-md-7 col-xs-12">
                                    <input type="text" name="passportNo" id="passport-no" class="form-control"
                                           placeholder="Passport No" autocomplete="new-password">
                                </div>
                            </div>

                            <div class="col-md-6 col-xs-12">
                                <label for="pin-no" class="label-align col-md-5">Pin No <span class="required">*</span></label>
                                <div class="col-md-7 col-xs-12">
                                    <input type="text" name="pinNo" id="pin-no" class="form-control"
                                           placeholder="Pin No" autocomplete="new-password">
                                    <!-- Validation message goes here -->
                                    <div id="pin-validation-msg" class="mt-2"></div>
                                </div>
                            </div>
                        </div>

                        <div class="item form-group form-required">
                            <div class="col-md-6 col-xs-12 firstNameDiv">
                                <label for="fname" class="label-align col-md-5">First
                                    Name<span class="required">*</span></label>
                                <div class="col-md-7 col-xs-12">
                                    <input type="text" name="fname" id="fname" class="form-control"
                                           placeholder="First Name" autocomplete="new-password">
                                </div>
                            </div>


                            <div class="col-md-6 col-xs-12 additionalName">
                                <label for="lname" class="label-align col-md-5">Last Name
                                    <span class="required">*</span></label>
                                <div class="col-md-7 col-xs-12">
                                    <input type="text" name="lname" id="lname" class="form-control"
                                           placeholder="Last Name" autocomplete="new-password">
                                </div>
                            </div>
                        </div>

                        <div class="item form-group form-required">
                            <div class="col-md-6 col-xs-12 additionalName">
                                <label for="other-names" class="label-align col-md-5">Other
                                    Names</label>
                                <div class="col-md-7 col-xs-12">
                                    <input type="text" name="otherNames" id="other-names" class="form-control"
                                           placeholder="Other Names" autocomplete="new-password">
                                </div>
                            </div>
                            <!-- Date of Birth Field -->
                            <div class="col-md-6 col-xs-12 dobDiv">
                                <label for="dob" class="col-md-5 label-align" id="lbl-dob">Date of Birth<span
                                        class="required">*</span></label>
                                <div class="col-md-7 col-xs-12">
                                    <input type="text" name="dob" id="dob" class="form-control"
                                           placeholder="Date of Birth" autocomplete="new-password">
                                </div>
                            </div>
                        </div>

                        <div class="item form-group form-required">
                            <div class="col-md-6 col-xs-12">
                                <label for="gender" class="label-align col-md-5" id="lblGender">Gender</label>
                                <div class="col-md-7 col-xs-12 gender">
                                    <div class="input-group field-width">
									 <span class="input-group-addon">
									  <i class="fa fa-venus-mars"></i></span>
                                        <select class="form-control" id="gender" name="gender" autocomplete="new-password">
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
                                    <input type="hidden" id="cou-id" name="couCode" autocomplete="new-password" required/>
                                    <input type="hidden" id="cou-name" autocomplete="new-password">
                                    <div id="clnt-country" class="form-control"
                                         select2-url="<c:url value='/protected/organization/countries' />"></div>
                                </div>
                            </div>
                        </div>

                        <div class="item form-group form-required">
                            <div class="col-md-6 col-xs-12">
                                <label for="pep-type" class="label-align col-md-5">PEP Type</label>
                                <div class="col-md-7 col-xs-12">
                                    <input type="text" id="pep-type" name="pepType" class="form-control" readonly>
                                </div>
                            </div>

                            <div class="col-md-6 col-xs-12">
                                <label for="screening-status" class="label-align col-md-5">Screening Status</label>
                                <div class="col-md-7 col-xs-12">
                                    <input type="text" id="screening-status" name="screeningStatus" class="form-control" readonly>
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
                                <label for="postal-name" class="label-align col-md-5">Postal Code</label>
                                <div class="col-md-7 col-xs-12">
                                    <input type="hidden" id="postal-code-id" name="pcode"/>
                                    <input type="text" id="postal-name" class="form-control" readonly>
                                </div>
                            </div>

                            <div class="col-md-6 col-xs-12">
                                <label for="phone-pref" class="label-align col-md-5">Phone Number<span
                                        class="required">*</span></label>
                                <div class="col-md-3 col-xs-12">
                                    <input type="hidden" id="pref-phone-id" autocomplete="new-password" name="phonePrefixId"/>
                                    <input type="hidden" id="pref-phone-name" autocomplete="new-password">
                                    <div id="phone-pref" class="form-control"
                                         select2-url="<c:url value="/protected/clients/setups/selMobilePrefix"/>"
                                         data-autocomplete="new-password">

                                    </div>
                                    <input type="button" value="New" class="btn btn-primary btn-sm"
                                           id="btn-add-phone-prefix" style="display:none">
                                </div>
                                <div class="col-md-4 col-xs-12">
                                    <input type="text" name="phoneNo" id="phone-no" class="form-control"
                                           placeholder="Phone No" autocomplete="new-password">
                                </div>
                            </div>
                        </div>

                        <%--                        <div class="item form-group form-required">--%>
                        <%--                            <div class="col-md-6 col-xs-12">--%>
                        <%--                                <label for="noOfUnits" class="label-align col-md-5">SMS Number</label>--%>
                        <%--                                <div class="col-md-3 col-xs-12">--%>
                        <%--                                    <input type="hidden" id="pref-sms-id" name="smsPrefixId"/>--%>
                        <%--                                    <input type="hidden" id="pref-sms-name">--%>
                        <%--                                    <div id="sms-pref" class="form-control"--%>
                        <%--                                         select2-url="<c:url value="/protected/clients/setups/selMobilePrefix"/>">--%>

                        <%--                                    </div>--%>
                        <%--                                    <input type="button" value="New" class="btn btn-primary btn-sm"--%>
                        <%--                                           id="btn-add-sms-prefix" style="display:none">--%>
                        <%--                                </div>--%>
                        <%--                                <div class="col-md-4 col-xs-12">--%>
                        <%--                                    <input type="text" name="smsNumber" id="sms-no" class="form-control"--%>
                        <%--                                           placeholder="SMS No">--%>
                        <%--                                </div>--%>
                        <%--                            </div>--%>
                        <%--                            <div class="col-md-6 col-xs-12">--%>
                        <%--                                <label for="phone-pref" class="label-align col-md-5">Phone Number</label>--%>
                        <%--                                <div class="col-md-3 col-xs-12">--%>
                        <%--                                    <input type="hidden" id="pref-phone-id" name="phonePrefixId"/>--%>
                        <%--                                    <input type="hidden" id="pref-phone-name">--%>
                        <%--                                    <div id="phone-pref" class="form-control"--%>
                        <%--                                         select2-url="<c:url value="/protected/clients/setups/selMobilePrefix"/>">--%>

                        <%--                                    </div>--%>
                        <%--                                    <input type="button" value="New" class="btn btn-primary btn-sm"--%>
                        <%--                                           id="btn-add-phone-prefix" style="display:none">--%>
                        <%--                                </div>--%>
                        <%--                                <div class="col-md-4 col-xs-12">--%>
                        <%--                                    <input type="text" name="phoneNo" id="phone-no" class="form-control"--%>
                        <%--                                           placeholder="Phone No">--%>
                        <%--                                </div>--%>
                        <%--                            </div>--%>
                        <%--                        </div>--%>
                        <div class="item form-group form-required">
                            <div class="col-md-6 col-xs-12">
                                <label for="ten-branch" class="label-align col-md-5">Branch Registered</label>
                                <div class="col-md-7">
                                    <input type="hidden" id="obId" name="obId" autocomplete="new-password">
                                    <input type="hidden" id="reg-brn-name" autocomplete="new-password">
                                    <input type="hidden" id="ob-name" autocomplete="new-password">
                                    <div id="ten-branch" class="form-control"
                                         select2-url="<c:url value="/protected/uw/policies/allbranches"/>"
                                         data-autocomplete="new-password">

                                    </div>
                                </div>
                            </div>
                            <div class="col-md-6 col-xs-12">
                                <label for="email-address" class="label-align col-md-5">Email<span
                                        class="required">*</span></label>
                                <div class="col-md-7 col-xs-12">
                                    <input type="email" name="emailAddress" id="email-address" class="form-control"
                                           placeholder="Email" autocomplete="new-password" required>
                                </div>
                            </div>
                        </div>
                        <div class="item form-group form-required">
                            <div class="col-md-6 col-xs-12 statusDiv">
                                <label for="sel3" class="label-align col-md-5">Status</label>
                                <div class="col-md-7 col-xs-12">

                                    <select class="form-control" id="sel3" name="status" autocomplete="new-password">
                                        <option value="">Select Status</option>
                                        <option value="A">Active</option>
                                        <option value="T">Terminated</option>
                                        <option value="B">Blacklisted</option>
                                    </select>

                                </div>
                            </div>

                            <div class="col-md-6 col-xs-12">
                                <label for="segment-code" class="label-align col-md-5">Segments<span
                                        class="required">*</span></label>
                                <div class="col-md-7 col-xs-12">
                                    <input type="hidden" id="segmentId" name="segmentId" autocomplete="new-password">
                                    <input type="hidden" id="segmentName" name="segmentName" autocomplete="new-password">
                                    <div id="segment-code" class="form-control"
                                         select2-url="<c:url value='/protected/clients/setups/selSegment'/>"
                                         data-autocomplete="new-password"></div>
                                </div>
                            </div>

                            <div class="col-md-6 col-xs-12" id="myComments" style="display: none">
                                <label for="myComments" class="label-align col-md-5">Blacklisting Comment</label>
                                <div class="col-md-7 col-xs-12">

                                    <textarea class="form-control" rows="2" placeholder="Comments" name="comment"
                                              title="A reason for blacklisting or termination must be provided must be provided"
                                              autocomplete="new-password" required></textarea>

                                </div>
                            </div>
                        </div>

                        <div class="item form-group">
                            <div class="col-md-6 col-xs-12">
                                <label for="marketing-material" class="label-align col-md-5">Receive Marketing
                                    Material?</label>
                                <div class="col-md-7 col-xs-12">
                                    <input type="checkbox" id="marketing-material" name="receiveMarketingMaterial">
                                </div>
                            </div>
                            <div class="col-md-6 col-xs-12" id="communication-mode-container" style="display: none;">
                                <label for="preferred-communication" class="label-align col-md-5">Preferred Mode of
                                    Communication</label>
                                <div class="col-md-7 col-xs-12">
                                    <select id="preferred-communication" name="preferredModeOfCommunication"
                                            class="form-control" autocomplete="new-password">
                                        <option value="" disabled selected>Select Communication Mode</option>
                                        <option value="email">Email</option>
                                        <option value="sms">SMS</option>
                                        <option value="phone">Phone</option>
                                    </select>
                                </div>
                            </div>
                        </div>


                        <%--                        <div class="col-md-6 col-xs-12 clientAuthorizedDiv">--%>
                        <%--                            <label for="clnt-authorised" class="label-align col-md-5">--%>
                        <%--                                Authorised?</label>--%>
                        <%--                            <div class="col-md-7 col-xs-12">--%>
                        <%--                                <p class="form-control-static" id="clnt-authorised"></p>--%>
                        <%--                            </div>--%>
                        <%--                        </div>--%>
                    </div>

                    <div class="employee-info">
                        <h4>Employment Information</h4>
                        <div class="item form-group">
                            <label for="sect-def" class="col-md-3 label-align" id="lbl-sector">Sector</label>

                            <div class="col-md-4 col-xs-12">
                                <input type="hidden" id="sect-id" autocomplete="new-password" name="sectCode"/>
                                <input type="hidden" id="sect-name" autocomplete="new-password">
                                <div id="sect-def" class="form-control"
                                     select2-url="<c:url value="/protected/setups/selSectors"/>"
                                     data-autocomplete="new-password">

                                </div>
                            </div>
                        </div>
                        <div class="item form-group">
                            <label for="occ-def" class="col-md-3 label-align" id="lbl-occ">Occupation</label>

                            <div class="col-md-4 col-xs-12">
                                <input type="hidden" id="occ-id" autocomplete="new-password" name="occCode"/>
                                <input type="hidden" id="occ-name" autocomplete="new-password">
                                <div id="occ-def" class="form-control"
                                     select2-url="<c:url value="/protected/setups/selOccupations"/>"
                                     data-autocomplete="new-password">

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
                    <input type="submit" class="btn btn-primary float-right" id="btn-save-tenant" value="Save">
                    <c:if test="${empty param.type}">
                        <a href="<c:url value='/protected/clients/setups/clientslist'/> " id="back"
                           class="btn btn-primary float-right">Back</a>
                    </c:if>
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
<script>
    $(document).ready(function () {
        $(".datepicker-input").each(function () {
            $(this).datetimepicker({
                format: 'DD/MM/YYYY'
            });


        });

        $(document).ajaxStart(function () {
            $("#btn-save-tenant").attr("disabled", true);
        });
        $(document).ajaxComplete(function () {
            $("#btn-save-tenant").attr("disabled", false);
        });
        getClientDetails();
        getTenantDetails();


        $("#sel2").change(function () {
            $("#clnt-type-code").val($(this).val());
            if ($(this).val() == 'C' || $(this).val() == '') {
                $("#gender,.gender,#lblGender").hide();
                $("#lbl-dob").html("Date of Incorporation");
                $("#lbl-id-no").html("Registration No.");
                $("#clnt-title").select2("enable", false);
                $('#clnt-title').select2('val', null);
                $("#clnt-title-id").val("");
                $("span.othernames").hide();
                $("#client-photo").html("Logo");

                // Hide PEP type and screening status for corporate clients
                // $("#pep-type").closest('.form-group').hide();
                // $("#screening-status").closest('.form-group').hide();
                // $("#pep-type").val("");
                // $("#screening-status").val("");

            } else if ($(this).val() == 'I') {
                $("#gender,.gender,#lblGender").show();
                $("#lbl-dob").html("Date of Birth");
                $("#lbl-id-no").html("ID No.");
                $("#clnt-title").select2("enable", true);
                $('#clnt-title').select2('val', null);
                $("#clnt-title-id").val("");
                $("span.othernames").show();
                $("#client-photo").html("Photo");

                // Show PEP type and screening status for individual clients
                // $("#pep-type").closest('.form-group').show();
                // $("#screening-status").closest('.form-group').show();
            }
        });

        $('#clientSearch').delegate('tr', 'click', function () {
            var tds = $(this).addClass('selected').find('td');
            tds.each(function (index, item) {
                switch (index) {
                    case 0:
                        var arr = $(item).html().split(",");
                        if (arr.length > 1) {
                            $("#fname").val(arr[1]);
                            $("#other-names").val(arr[0]);
                            $("#fname").attr("readonly", "true");
                            $("#other-names").attr("readonly", "true");
                        } else if (arr.length == 1) {
                            $("#fname").val(arr[0]);
                            $("#fname").attr("readonly", "true");
                        }
                        break;
                    case 1:
                        if ($(item).html() !== '') {
                            $("#pin-no").val($(item).html());
                            $("#pin-no").attr("readonly", "true");
                        }
                        break;
                    case 2:
                        if ($(item).html() !== '') {
                            $(".clnt-title").css("display", "none");
                            $(".display-clnt-title").css("display", "block");
                            $("#client-title").val($(item).html());
                            $("#client-title").attr("readonly", "true");
                        }
                        break;
                    case 3:
                        if ($(item).html() !== '') {
                            $("#office-tel-no").val($(item).html());
                            $("#office-tel-no").attr("readonly", "true");
                        }
                        break;
                    case 4:
                        if ($(item).html() !== '') {
                            $("#email-address").val($(item).html());
                            $("#email-address").attr("readonly", "true");
                        }
                        break;
                    case 5:
                        if ($(item).html() !== '') {
                            $("#address").val(+'P.O. BOX ' + $(item).html());
                        }
                        break;
                    case 6:
                        if ($(item).html() !== '') {
                            $("#client-ref-no").val($(item).html());
                        }
                        break;
                    case 7:
                        if ($(item).html() !== '') {
                            $("#id-no").val($(item).html());
                            $("#id-no").attr("readonly", "true");
                        }
                        break;
                    case 8:
                        if ($(item).html() !== '') {
                            var dateOnly = moment($(item).html(), "YYYYMMDD");
                            var ndate = dateOnly.toDate();
                            $("#dob").val(moment(ndate).format('DD/MM/YYYY'));
                        }
                        break;

                }

            });
            $('#clientSearchModal').modal('hide');
        });

        const phoneInput = document.getElementById('phone-no');
        phoneInput.addEventListener('input', function () {
            let phoneNumber = phoneInput.value.replace(/\D/g, '');

            if (phoneNumber.startsWith('0')) {
                phoneNumber = phoneNumber.substring(1);
            }

            if (phoneNumber.length > 3 && phoneNumber.length <= 6) {
                phoneNumber = phoneNumber.slice(0, 3) + ' ' + phoneNumber.slice(3);
            } else if (phoneNumber.length > 6) {
                phoneNumber = phoneNumber.slice(0, 3) + ' ' + phoneNumber.slice(3, 6) + ' ' + phoneNumber.slice(6);
            }

            phoneInput.value = phoneNumber;
        });

        const form = document.querySelector('form');
        form.addEventListener('submit', function (e) {
            phoneInput.value = phoneInput.value.replace(/\s/g, '');
        });

        populateCountryLov();
        populateTitlesLov();
        createSectorSelect();
        createClientTownLov();
        createClient();
        addSMSPrefix();
        populateClientTypeLov();
        newClientTypes();
        saveClientTypes();
        validateDetails();
        newClientTitles();
        saveClientTitle();
        saveMobProviders();
        showClients();
        populateSmsPrefix(-2000);
        populateOccupations(-2000);
        populatePostalCode(-2000);
        populateBranchA();
        SegmentCode();


    });


    function getTenantDetails() {
        if (typeof tenidpkey !== 'undefined') {
            if (tenidpkey !== -2000) {
                $.ajax({
                    url: 'tenants/' + tenidpkey,
                    type: 'GET',
                    processData: false,
                    contentType: false,
                    success: function (s) {
                        populateTenantDetails(s);
                        $("#show-docs").css("display", "block");
                        $("#tab_content2").css("display", "block");
                        $(".clnt-title").css("display", "block");
                        $(".display-clnt-title").css("display", "none");
                        $("#btn-search-tenant").css("display", "none");

                    },
                    error: function (xhr, error) {
                        bootbox.alert(xhr.responseText);
                    }
                });
            } else {
                tenantImage(-2000);
                getSystemDate();
                defaultCountry();
                $("#address").val("P.O. Box");
                $("#show-docs").css("display", "none");
                $("#tab_content2").css("display", "none");
                $("#btn-auth-client").css("display", "none");
                $("#btn-save-tenant").css("display", "block");
                $("#btn-add-client-type").css("display", "block");
                $("#btn-add-sms-prefix").css("display", "block");
                $("#btn-add-phone-prefix").css("display", "block");
                $("#btn-search-tenant").css("display", "block");
                $(".clnt-title").css("display", "block");
                $(".display-clnt-title").css("display", "none");
            }

        }
    }

    function getSystemDate() {
        $.ajax({
            url: 'getTodaysDate',
            type: 'GET',
            processData: false,
            contentType: false,
            success: function (s) {
                $("#date-reg").val(moment(s).format('DD/MM/YYYY'));
            },
            error: function (xhr, error) {

            }
        });

    }

    function populateTenantDetails(data) {
        console.log(data);
        $("#tenId-pk").val(data.tenId);
        $("#fname").val(data.fname);
        $("#other-names").val(data.otherNames);
        $("#id-no").val(data.idNo);
        $("#passport-no").val(data.passportNo);
        $("#pin-no").val(data.pinNo);
        $("#client-ref-no").val(data.tenantNumber);
        $("#email-address").val(data.emailAddress);
        $("#phone-no").val(data.phoneNo);
        $("#sms-no").val(data.smsNumber);
        $("#address").val(data.address);
        $("#sel2").val(data.clientType);
        $("#dob").val(moment(data.dob).format('DD/MM/YYYY'));
        if (data.status != "T") {
            $("#sel3").val(data.status);
            $("#sel3").prop("disabled", false);
            $("#btn-save-tenant").prop("disabled", false);
        } else {
            $("#sel3").val(data.status);
            $("#sel3").prop("disabled", true);
            $("#btn-save-tenant").prop("disabled", true);
        }

        $("#clnt-type-code").val(data.clientType);
        if (data.clientType === 'C' || data.clientType === '') {
            $("#gender,.gender,#lblGender").hide();
            $("#lbl-dob").html("Date of Incorporation");
            $("#lbl-id-no").html("Registration No.");
            $(".employee-info").hide();

        } else if (data.clientType === 'I') {
            $("#gender,.gender,#lblGender").show();
            $("#lbl-dob").html("Date of Birth");
            $("#lbl-id-no").html("ID No.");
            $(".employee-info").show();
        }

        if (data.authStatus && data.authStatus === "Y") {
            $("#clnt-authorised").text("Yes");
            $("#btn-save-tenant").css("display", "none");
            $("#btn-auth-client").css("display", "block");
            $("#btn-auth-client").val("Un-Approve");
            $("#btn-add-client-type").css("display", "none");
            $("#clnt-title").select2("enable", false);
            $("#fname").attr("disabled", "disabled");
            $("#other-names").attr("disabled", "disabled");
            $("#pin-no").attr("disabled", "disabled");
            $("#email-address").attr("disabled", "disabled");
            $("#address").attr("disabled", "disabled");
            $("#id-no").attr("disabled", "disabled");
            $("#passport-no").attr("disabled", "disabled");
            $("#gender").attr("disabled", "disabled");
            $("#clnt-country,#sms-pref,#town-code-lov,#postal-code,#clnt-client-type,#clnt-title,#sect-def,#ten-branch,#occ-def,#phone-pref").select2("readonly", true);
            $("#office-tel-no").attr("disabled", "disabled");
            $("#sms-no").attr("disabled", "disabled");
            $("#phone-no").attr("disabled", "disabled");
            $("#btn-add-sms-prefix").css("display", "none");
            $("#btn-add-phone-prefix").css("display", "none");
            $("#sel3").attr("disabled", "disabled");
            $("#dob").attr("disabled", "disabled");
            $("#btn-add-docs").hide();

        } else {
            $("#clnt-authorised").text("No");
            $("#btn-auth-client").css("display", "block");
            $("#btn-save-tenant").css("display", "block");
            $("#btn-auth-client").val("Approve");
            $("#btn-add-client-type").css("display", "block");
            $("#btn-add-sms-prefix").css("display", "block");
            $("#btn-add-phone-prefix").css("display", "block");
            $("#fname").removeAttr('disabled');
            $("#other-names").removeAttr('disabled');
            $("#pin-no").removeAttr('disabled');
            $("#email-address").removeAttr('disabled');
            $("#address").removeAttr('disabled');
            $("#id-no").removeAttr('disabled');
            $("#passport-no").removeAttr('disabled');
            $("#gender").removeAttr('disabled');
            $("#clnt-country,#sms-pref,#town-code-lov,#postal-code,#clnt-client-type,#clnt-title,#sect-def,#ten-branch,#occ-def,#phone-pref").select2("readonly", false);
            $("#office-tel-no").removeAttr('disabled');
            $("#sms-no").removeAttr('disabled');
            $("#phone-no").removeAttr('disabled');

            $("#sel3").removeAttr('disabled');
            $("#dob").removeAttr('disabled');
            $("#btn-add-docs").show();
        }
        $("#gender").val(data.gender);
        $("#ten-id").val(data.clientCIF);
        $("#office-tel-no").val(data.officeTel);
        $("#date-reg").val(moment(data.dateregistered).format('DD/MM/YYYY'));

        $("#ob-name").val(data.obName);
        $("#obId").val(data.obId);
        populateBranchA();

        if (data.country) {
            $("#cou-name").val(data.couName);
            $("#cou-id").val(data.couCode);
            populateCountryLov();
        }
        if (data.smsPrefix) {
            $("#pref-sms-id").val(data.smsPrefixId);
            $("#pref-sms-name").val(data.smsPrefixName);

        }
        if (data.phonePrefix) {
            $("#pref-phone-id").val(data.phonePrefixId);
            $("#pref-phone-name").val(data.phonePrefixName);
        }
        populateSmsPrefix(data.couCode);
        if (data.clientTypeId) {
            $("#clnt-type-id").val(data.clientTypeId);
            $("#clnt-type-name").val(data.clientTypeDesc);
        }
        populateClientTypeLov();
        if (data.titleId) {
            $("#clnt-title-id").val(data.titleId);
            $("#clnt-title-name").val(data.titleName);
        }
        populateTitlesLov();
        if (data.sectCode) {
            $("#sect-id").val(data.sectCode);
            $("#sect-name").val(data.sectName);

        }
        createSectorSelect();
        if (data.occCode) {
            $("#occ-id").val(data.occCode);
            $("#occ-name").val(data.occName);
            populateOccupations(data.sectCode);
        }

        if (data.ctCode) {
            $("#clnt-town-id").val(data.ctCode);
            $("#clnt-town-name").val(data.ctName);
            createClientTownLov();
        }
        if (data.pcode) {
            $("#postal-code-id").val(data.pcode);
            $("#postal-name").val(data.postalName);
            populatePostalCode(data.pcode);
        }
        if (data.dateterminated) {
            $("#dt-terminated").val(moment(data.dateterminated).format('DD/MM/YYYY'));
            tenantImage(data.tenId);
        }
        if (data.segmentId) {
            $("#segmentId").val(data.segmentId);
            $("#segmentName").val(data.segmentName);
            SegmentCode();
        }
    }


    function tenantImage(id) {
        $("#avatar").fileinput({
            overwriteInitial: true,
            maxFileSize: 1500,
            showClose: false,
            showCaption: false,
            browseLabel: '',
            removeLabel: '',
            browseIcon: '<i class="fa fa-folder-open"></i>',
            removeIcon: '<i class="fa fa-times"></i>',
            removeTitle: 'Cancel or reset changes',
            elErrorContainer: '#kv-avatar-errors',
            msgErrorClass: 'alert alert-block alert-danger',
            defaultPreviewContent: '<img src="' + getContextPath() + '/protected/clients/setups/tenantImage/' + id + '"  style="width:180px">',
            layoutTemplates: {main2: '{preview} ' + ' {remove} {browse}'},
            allowedFileExtensions: ["jpg", "png", "gif"]
        });
    }

    function showClients() {
        $("#btn-search-tenant").click(function () {
            $('#clientSearchModal').modal({
                backdrop: 'static',
                keyboard: true
            });
        });

        $("#searchClients").click(function () {
            $("#tenId-pk").val('');
            $("#clnt-type-id").val('');
            $("#clnt-type-name").val('');
            $("#clnt-type-code").val('');
            $('#tenant-form')[0].reset();
            jQuery('.select2-offscreen').select2('val', '');

            searchClients();
        });
    }


    function searchClients() {
        // // $('#myPleaseWait').modal({
//			backdrop: 'static',
//			keyboard: true
//		});
        $.ajax({
            type: 'GET',
            url: 'integrationClients',
            dataType: 'json',
            data: {"custId": $("#cust-id-search").val(), "legalId": $("#legal-id-search").val()},
            async: true,
            success: function (result) {
                // $('#myPleaseWait').modal('hide');
                if (result) {
                    $("#clientSearch tbody").each(function () {
                        $(this).remove();
                    });
                    for (var res in result) {
                        var markup = "<tr><td>" + result[res].custname + "</td>" +
                            "<td>" + result[res].clientpin + "</td>" +
                            "<td>" + result[res].title + "</td>" +
                            "<td>" + result[res].sms1 + "</td>" +
                            "<td>" + result[res].email1 + "</td>" +
                            "<td>" + result[res].address + "</td>" +
                            "<td>" + result[res].customercode + "</td>" +
                            "<td>" + result[res].legalid + "</td>" +
                            "<td>" + result[res].birthincdate + "</td>" +
                            "</tr>";
                        $("#clientSearch").append(markup);
                    }
                } else {
                    Swal.fire({
                        title: 'Error',
                        text: 'Unable To get Client Details from Core Banking System',
                        icon: 'error'
                    })
                }


            },
            error: function (jqXHR, textStatus, errorThrown) {
                // $('#myPleaseWait').modal('hide');
                Swal.fire({
                    title: 'Error',
                    text: jqXHR.responseText,
                    icon: 'error'
                });
            }
        });
        // }
        // else{
        // 	bootbox.alert("Select Product to attach sub classes")
        // }
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

    function SegmentCode() {
        if ($("#segment-code").filter("div").html() != undefined) {
            Select2Builder.initAjaxSelect2({
                containerId: "segment-code",
                sort: 'segName',
                change: function (e, a, v) {
                    $("#segmentName").val(e.added.segName);
                    $("#segmentId").val(e.added.segId);
                    // var segmentId = e.added.segId;
                    // fetchSegmentDetails(segmentId);
                },
                formatResult: function (a) {
                    return a.segName;
                },
                formatSelection: function (a) {
                    return a.segName;
                },
                initSelection: function (element, callback) {
                    var name = $("#segmentName").val();
                    var id = $("#segmentId").val();
                    var data = {segName: name, segId: id};
                    callback(data);
                },
                id: "segId",
                placeholder: "Select Segment",
            });
        }
    }

    // function fetchSegmentDetails(segmentId) {
    //     $.ajax({
    //         url: 'segmentsList/' + segmentId,
    //         type: 'GET',
    //         success: function(segment) {
    //             $('#segmentId').val(segment.id);
    //             $('#segmentName').val(segment.segName);
    //             $('#segmentDescription').val(segment.segDescription);
    //             $('#segCompCode').val(segment.segCompCode);
    //             $('#segSegment').val(segment.segSegment);
    //         },
    //         error: function(xhr, error) {
    //             console.error('Error fetching segment details:', error);
    //         }
    //     });
    // }

    function populatePostalCode(townCode) {
        // Use AJAX to fetch the postal code for the selected town
        $.ajax({
            url: SERVLET_CONTEXT + '/protected/clients/setups/selClientPostalCode', // Ensure this endpoint supports filtering by townCode
            type: 'GET',
            data: {townCode: townCode},
            success: function (response) {
                console.log(response);
                if (response && response.numberOfElements > 0) {
                    console.log(response.content[0]);
                    const postal = response.content[0];
                    const postalCode = postal.zipCode;
                    const postalCodeId = postal.pcode;

                    $("#postal-code-id").val(postalCodeId);
                    $("#postal-name").val(postalCode);
                }
            },
            error: function (xhr, status, error) {
                console.error("Error fetching postal code: ", error);
            }
        });
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

    function getContextPath() {
        return window.location.pathname.substring(0, window.location.pathname
            .indexOf("/", 2));
    }


    function newClientTypes() {
        $("#btn-add-client-type").on("click", function () {
            $('#client-type-form').find("input[type=text],input[type=mobileNumber],input[type=emailFull],input[type=password],input[type=hidden],input[type=number], textarea,select").val("");
            $('#clntTypeModesModal').modal('show');
        });
    }


    function saveClientTypes() {
        var $paymodesForm = $('#client-type-form');
        var validator = $paymodesForm.validate();
        $('#clntTypeModesModal').on('hidden.bs.modal', function () {
            validator.resetForm();
            $('#client-type-form').find("input[type=text],input[type=mobileNumber],input[type=emailFull],input[type=password],input[type=hidden], textarea,select").val("");
        });

        $('#saveClientType').click(function () {
            if (!$paymodesForm.valid()) {
                return;
            }
            var $btn = $(this).button('Saving');
            var data = {};
            $paymodesForm.serializeArray().map(function (x) {
                data[x.name] = x.value;
            });
            var url = "creatClientType";
            var request = $.post(url, data);
            request.success(function () {
                Swal.fire({
                    title: 'Success',
                    text: 'Record created/updated Successfully',
                    icon: 'success'
                });
                validator.resetForm();
                $('#clntTypeModesModal').modal('hide');
            });
            request.error(function (jqXHR, textStatus, errorThrown) {
                Swal.fire({
                    title: 'Error',
                    text: jqXHR.responseText,
                    icon: 'error'
                });
            });
            request.always(function () {
                $btn.button('reset');
            });
        });
    }


    function saveMobProviders() {
        $("#btn-add-mob-provider").on("click", function () {
            $('#mob-provider-form').find("input[type=text],input[type=number],input[type=mobileNumber],input[type=emailFull],input[type=password],input[type=hidden], textarea,select").val("");
            $("#mob-provider").select2("val", "");
            $('#mobProviderModal').modal({
                backdrop: 'static',
                keyboard: true
            })
        });

        $('#saveMobProvider').click(function () {
            var $frm = $('#mob-provider-form');
            if (!$frm.valid()) {
                return;
            }
            var $btn = $(this).button('Saving');
            var data = {};
            $frm.serializeArray().map(function (x) {
                data[x.name] = x.value;
            });
            var url = "createMobProvider";
            var request = $.post(url, data);
            request.success(function () {
                Swal.fire({
                    title: 'Success',
                    text: 'Record created/updated Successfully',
                    icon: 'success'
                });
                $('#mobProviderModal').modal('hide');
            });
            request.error(function (jqXHR, textStatus, errorThrown) {
                Swal.fire({
                    title: 'Error',
                    text: jqXHR.responseText,
                    icon: 'error'
                });
            });
            request.always(function () {
                $btn.button('reset');
            });
        });
    }

    function validateDetails() {
        $('#pin-no').on('change', function () {
            var pinNo = $("#pin-no").val();
            if (pinNo !== '') {
                $.ajax({
                    type: 'GET',
                    url: 'validatePin',
                    dataType: 'json',
                    data: {"pinNo": pinNo},
                    async: true,
                    success: function (result) {

                    },
                    error: function (jqXHR, textStatus, errorThrown) {
                        Swal.fire({
                            title: 'Error',
                            text: jqXHR.responseText,
                            icon: 'error'
                        });
                        $("#pin-no").val("");
                    }
                });
            }
        });

        $('#id-no').on('change', function () {
            console.log($("#clnt-type-code").val());
            if ($("#clnt-type-code").val() === "I") {
                var idno = $("#id-no").val();
                $.ajax({
                    type: 'GET',
                    url: 'validateIDNo',
                    dataType: 'json',
                    data: {"idNo": idno},
                    async: true,
                    success: function (result) {

                    },
                    error: function (jqXHR, textStatus, errorThrown) {
                        Swal.fire({
                            title: 'Error',
                            text: jqXHR.responseText,
                            icon: 'error'
                        });
                        $("#id-no").val("");
                    }
                });
            }
        });


        $('#passport-no').on('change', function () {
            var passport = $("#passport-no").val();
            $.ajax({
                type: 'GET',
                url: 'validatePassport',
                dataType: 'json',
                data: {"passportNo": passport},
                async: true,
                success: function (result) {

                },
                error: function (jqXHR, textStatus, errorThrown) {
                    Swal.fire({
                        title: 'Error',
                        text: jqXHR.responseText,
                        icon: 'error'
                    });
                    $("#passport-no").val("");
                }
            });
        });
    }


    function newClientTitles() {
        $("#btn-add-clnt-title").on("click", function () {
            $('#client-title-form').find("input[type=text],input[type=mobileNumber],input[type=emailFull],input[type=password],input[type=hidden],input[type=number], textarea,select").val("");
            $('#clntTitleModesModal').modal('show');
        });
    }

    function saveClientTitle() {
        var $paymodesForm = $('#client-title-form');
        var validator = $paymodesForm.validate();
        $('#clntTitleModesModal').on('hidden.bs.modal', function () {
            validator.resetForm();
            $('#client-title-form').find("input[type=text],input[type=mobileNumber],input[type=emailFull],input[type=password],input[type=hidden], textarea,select").val("");
        });

        $('#saveClientTitle').click(function () {
            if (!$paymodesForm.valid()) {
                return;
            }
            var $btn = $(this).button('Saving');
            var data = {};
            $paymodesForm.serializeArray().map(function (x) {
                data[x.name] = x.value;
            });
            var url = "creatClientTitle";
            var request = $.post(url, data);
            request.success(function () {
                Swal.fire({
                    title: 'Success',
                    text: 'Record created/updated Successfully',
                    icon: 'success'
                });
                validator.resetForm();
                $('#clntTitleModesModal').modal('hide');
            });
            request.error(function (jqXHR, textStatus, errorThrown) {
                Swal.fire({
                    title: 'Error',
                    text: jqXHR.responseText,
                    icon: 'error'
                });
            });
            request.always(function () {
                $btn.button('reset');
            });
        });
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

    function addSMSPrefix() {
        $("#btn-add-sms-prefix,#btn-add-phone-prefix").click(function () {
            if ($("#cou-id").val() != '') {
                $('#prefix-form').find("input[type=text],input[type=number],input[type=mobileNumber],input[type=emailFull],input[type=password],input[type=hidden], textarea,select").val("");
                $("#prefix-country").val($("#cou-id").val());
                createMobProviderSel();
                $('#prefixModal').modal({
                    backdrop: 'static',
                    keyboard: true
                })
            } else {
                bootbox.alert("Select Country to continue");
            }
        });

        var $classForm = $('#prefix-form');
        var validator = $classForm.validate();
        $('#savePrefixBtn').click(function () {
            if (!$classForm.valid()) {
                return;
            }

            var $btn = $(this).button('Saving');
            var data = {};
            $classForm.serializeArray().map(function (x) {
                data[x.name] = x.value;
            });
            var url = "createPrefix";
            var request = $.post(url, data);
            request.success(function () {
                Swal.fire({
                    title: 'Success',
                    text: 'Record created/updated Successfully',
                    icon: 'success'
                });
                populateSmsPrefix($("#cou-id").val());
                $('#prefix-form').find("input[type=text],input[type=number],input[type=mobileNumber],input[type=emailFull],input[type=password],input[type=hidden], textarea,select").val("");
                $('#prefixModal').modal('hide');
            });

            request.error(function (jqXHR, textStatus, errorThrown) {
                Swal.fire({
                    title: 'Error',
                    text: jqXHR.responseText,
                    icon: 'error'
                });
            });
            request.always(function () {
                $btn.button('reset');
            });
        });
    }


    function createClient() {
        var $form = $("#tenant-form");
        var validator = $form.validate();
        $('form#tenant-form').submit(function (e) {
            e.preventDefault();

            // var clientType = $('#clnt-type-name').val().trim();
            //
            // // ONLY PIN FIX: Skip PIN validation for corporate clients
            // if (clientType.toUpperCase() === 'CORPORATE') {
            //     // Temporarily remove required attribute from PIN during validation
            //     var pinRequired = $("#pin-no").attr('required');
            //     $("#pin-no").removeAttr('required');
            //
            //     // Run normal form validation (will skip PIN now)
            //     var isValid = $form.valid();
            //
            //     // Restore PIN required state (but keep it not required for corporate)
            //     // Don't restore it for corporate clients
            //
            //     if (!isValid) {
            //         return;
            //     }
            // } else {
            //     // For individuals, use normal validation (including PIN)
            //     if (!$form.valid()) {
            //         return;
            //     }
            if (!$form.valid()) {
                return;
            }

            var data = new FormData(this);
            data.append('file', $('#avatar')[0].files[0]);
            var params = new URLSearchParams(window.location.search);
            var clientId = params.get('clientId');
            if (clientId) {
                data.append('clientId', clientId);
            }

            $.ajax({
                url: 'createClient',
                type: 'POST',
                data: data,
                processData: false,
                contentType: false,
                success: function (response) {
                    if (response === 'M') {
                        Swal.fire({
                            title: 'Success',
                            text: 'Client has been created pending Approval...',
                            icon: 'success'
                        });
                        window.location.href = SERVLET_CONTEXT + "/protected/clients/setups/clientslist";
                    } else {
                        Swal.fire({
                            title: 'Success',
                            text: 'Record created/updated Successfully',
                            icon: 'success'
                        });
                        window.location.href = SERVLET_CONTEXT + "/protected/clients/setups/editClients/" + response.clientId;
                    }
                },
                error: function (xhr, error) {
                    Swal.fire({
                        title: 'Error',
                        text: xhr.responseText,
                        icon: 'error'
                    });
                }
            });
        });
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
                        // Corporate client - hide PEP and screening fields
                        $("#gender,.gender,#lblGender").hide();
                        $("#lbl-dob").html("Date of Incorporation");
                        $("#lbl-id-no").html("Registration No.");
                        $("#clnt-title").select2("enable", false);
                        $('#clnt-title').select2('val', null);
                        $("#clnt-title-id").val("");
                        $("span.othernames").hide();
                        $("#client-photo").html("Logo");
                        $(".employee-info").hide();
                        $(".clientTitleDiv").hide();
                        $(".clientRefDiv").hide();
                        $("#id-no").attr("placeholder", "Company Reg. No");
                        $(".passportDiv").hide();
                        $(".firstNameDiv label").html("Company Name");
                        $(".firstNameDiv div input").attr("placeholder", "Company name");
                        $(".additionalName").hide();
                        $(".dobDiv div input").attr("placeholder", "Date of Incorporation");
                        $(".statusDiv").hide();
                        $(".clientAuthorizedDiv").hide();

                        // Hide PEP and screening fields (existing code)
                        $("#pep-type").closest('.form-group').hide();
                        $("#screening-status").closest('.form-group').hide();
                        $("#pep-type").val("");
                        $("#screening-status").val("");

                        // ONLY PIN FIX: Remove PIN requirement for corporate
                        $("#pin-no").removeAttr('required');
                        $("#pin-no").removeClass('required');
                        $('label[for="pin-no"]').html('Pin No'); // Remove asterisk

                    } else if (e.added.clientType === 'I') {
                        // Individual client - show PEP and screening fields
                        $(".employee-info h4").html("Employment Information");
                        $(".clientAuthorizedDiv").hide();
                        $(".statusDiv").hide();
                        $(".dobDiv div input").attr("placeholder", "Date of Birth");
                        $(".additionalName").show();
                        $(".firstNameDiv div input").attr("placeholder", "First Name");
                        $(".firstNameDiv label").html("First Name");
                        $(".passportDiv").show();
                        $("#id-no").attr("placeholder", "Client ID No");
                        $(".clientTitleDiv").show();
                        $(".clientRefDiv").hide();
                        $("#gender,.gender,#lblGender").show();
                        $("#lbl-dob").html("Date of Birth");
                        $("#lbl-id-no").html("ID No.");
                        $("#clnt-title").select2("enable", true);
                        $('#clnt-title').select2('val', null);
                        $("#clnt-title-id").val("");
                        $("span.othernames").show();
                        $("#client-photo").html("Photo");
                        $(".employee-info").show();

                        // Show PEP and screening fields (existing code)
                        $("#pep-type").closest('.form-group').show();
                        $("#screening-status").closest('.form-group').show();

                        // RESTORE PIN requirement for individuals
                        // $("#pin-no").attr('required', true);
                        // $("#pin-no").addClass('required');
                        // $('label[for="pin-no"]').html('Pin No <span class="required">*</span>');
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

    function getClientDetails() {
        var url = window.location.href;
        var params = new URLSearchParams(window.location.search);
        var clientId = params.get('clientId');
        if (typeof clientId !== 'undefined') {
            if (clientId !== -2000) {
                $.ajax({
                    url: SERVLET_CONTEXT + '/protected/clients/setups/getClientDetails/' + clientId,
                    type: 'GET',
                    success: function (client) {
                        console.log(client)
                        $("#btn-search-tenant").hide();
                        $("#ten-id").val(client.clientCIF);
                        $("#clnt-type-id").val(client.clientTypeId);
                        $("#clnt-type-name").val(client.clientTypeDesc);
                        populateClientTypeLov();
                        $("#clnt-title-id").val(client.titleId);
                        $("#clnt-title-name").val(client.titleName);
                        populateTitlesLov();
                        $("#client-ref-no").val(client.tenantNumber);
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
                        $("#segmentName").val(client.segmentName);
                        $("#segmentId").val(client.segmentId);
                        SegmentCode();
                        $("#occ-id").val(client.occCode);
                        $("#occ-name").val(client.occName);
                        populateOccupations(client.sectCode);
                        $("#avatar").val(client.file);
                        $("#back").hide();
                        $("#back-view-client").show();
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
                });
            }
        } else {
            window.location.href = SERVLET_CONTEXT + '/protected/clients/setups/clientsform';
        }
    }

    $(document).ready(function () {
        let userDetailsFetched = false;
        function fetchUserDetails() {
            if (userDetailsFetched) return;
            if ($('#fname').val() && $('#lname').val() && $('#dob').val()) {
                return;
            }
            var idNo = $('#id-no').val().trim();
            var passportNo = $('#passport-no').val().trim();
            var residentStatus = $('#resident-status').val();
            var clientType = $('#clnt-type-name').val().trim();

            if (!clientType) {
                Swal.fire({
                    icon: 'warning',
                    title: 'Please select the Client type before validation',
                    showConfirmButton: true
                });

            } else if (clientType.toUpperCase() !== 'CORPORATE') {

                // Ensure both Resident Status and ID/Passport are provided
                if (!residentStatus) {
                    Swal.fire({
                        icon: 'warning',
                        title: 'Please select a Resident Status before validation',
                        showConfirmButton: true
                    });
                    return;
                }

                if (!idNo && !passportNo) {
                    Swal.fire({
                        icon: 'warning',
                        title: 'Please enter either your ID number or Passport number',
                        showConfirmButton: true
                    });
                    return;
                }

                $('#loading-spinner').show();
                var documentType = idNo ? 'ssnit' : 'passport';
                var documentId = idNo || passportNo;

                // Fetch IPRS details
                $.ajax({
                    url: SERVLET_CONTEXT + '/protected/clients/setups/validateID',
                    type: 'GET',
                    data: {documentType: documentType, documentId: documentId},
                    success: function (iprsResponse) {
                        $('#loading-spinner').hide();

                        if (iprsResponse.status === 'Success') {
                            $('#id-no').val(documentType === 'ssnit' ? documentId : '').prop('readonly', true);
                            $('#passport-no').val(iprsResponse.passportNumber).prop('readonly', true);
                            $('#fname').val(iprsResponse.firstName).prop('readonly', true);
                            $('#lname').val(iprsResponse.otherName).prop('readonly', true);
                            $('#other-names').val(iprsResponse.lastName).prop('readonly', true);
                            $('#dob').val(moment(iprsResponse.birthDate).format('DD/MM/YYYY')).prop('readonly', true);

                            if (iprsResponse.gender) {
                                $('#gender').val(iprsResponse.gender === 'M' ? 'M' : 'F').prop('disabled', false);
                            }

                            Swal.fire({
                                icon: 'success',
                                text: 'IPRS data retrieved successfully!',
                                title: 'Success'
                            });
                            userDetailsFetched = true;
                            validateKRA(clientType);

                        } else {
                            Swal.fire({
                                icon: 'error',
                                title: 'Error',
                                text: iprsResponse.message,
                            });
                        }
                    },
                    error: function (jqXHR, textStatus, errorThrown) {
                        $('#loading-spinner').hide();
                        Swal.fire({
                            icon: 'error',
                            text: jqXHR.responseText,
                            title: 'Error'

                        });
                    }
                });
            }
        }

        function validateKRA(clientType) {
            var clientType1 = clientType || $('#clnt-type-name').val().trim();
            var idNumber = $("#id-no").val();
            var residentStatus = $("#resident-status").val();
            if (residentStatus === 'resident') {
                residentStatus = 'KE';
            } else if (residentStatus === 'non-resident') {
                residentStatus = 'NKE';
            } else if (residentStatus === 'non-citizen') {
                residentStatus = 'NKENR';
            } else if (residentStatus === 'company') {
                residentStatus = 'COMP';
            }
            var data = {};
            var authentication = {};
            var authenticationTaskWorkProducts = {};
            authenticationTaskWorkProducts.taxationDetails = {taxPayerId: idNumber, taxPayerType: residentStatus};
            authentication.authenticationTaskWorkProducts = authenticationTaskWorkProducts;
            console.log(authentication)
            data.authentication = authentication;
            console.log(data);
            $('#loading-spinner').show();
            $.ajax({
                url: SERVLET_CONTEXT + '/protected/clients/setups/validateKRA',
                contentType: 'application/json',
                type: 'POST',
                data: JSON.stringify(data),
                success: function (kraResponse) {
                    $('#loading-spinner').hide();
                    $('#pin-no').val(kraResponse).prop('readonly', true);
                    Swal.fire({
                        title: 'Success',
                        text: 'PIN Validated Successfully',
                        icon: 'success'
                    });
                    netrevealScreening(clientType1);

                },
                error: function (jqXHR, textStatus, errorThrown) {
                    $('#loading-spinner').hide();
                    Swal.fire({
                        title: 'Error',
                        text: jqXHR.responseText,
                        icon: 'error'
                    });
                }
            });
        }

        function netrevealScreening(clientType) {
            // Skip screening for corporate clients
            if (clientType.toUpperCase() === 'CORPORATE') {
                Swal.fire({
                    title: 'Info',
                    text: 'Screening skipped for corporate clients',
                    icon: 'info'
                });
                return;
            }
            // Proceed with screening for individual clients
            var identificationType;
            if ($("#id-no").val()) {
                identificationType = "905";
            } else {
                identificationType = "906";
            }
            var data = {};
            var regulatoryAssessmentWorkProducts = {};
            var commonDetails = {};
            if (clientType.toUpperCase() === 'CORPORATE') {
                commonDetails = {
                    customerType: "E_PRIMARY",
                    customerId: "custid",
                    controlId: "cntrlid",
                    sbu: "RBB",
                    teller: "ABO31TA",
                    branch: "632005",
                    orgUnit: "KE"
                };
                if ($("#fname").val() || $("#dob").val() || $('#id-no').val()) {
                    Swal.fire({
                        icon: 'warning',
                        title: 'Please input the company name and date of incorporation',
                        showConfirmButton: true
                    });
                    return;
                }
                regulatoryAssessmentWorkProducts.entityDetails = {
                    companyName: $("#fname").val(),
                    companyTradingAsName: $("#fname").val(),
                    dateOfRegistration: moment($("#dob").val(), "DD/MM/YYYY", true).format('YYYY-MM-DD'),
                    companyRegistrationNumber: $('#id-no').val(),
                    countryOfOperation: ["KE"],
                    countriesTradedWith: ["KE"],
                    incorporationCountryCode: "KE"
                };
            } else {
                commonDetails = {
                    customerType: "I_PRIMARY",
                    identificationType: identificationType,
                    sbu: "RBB",
                    teller: "AB031TA",
                    branch: "632005",
                    orgUnit: "KE"
                };
                regulatoryAssessmentWorkProducts.individualDetails = {
                    firstName: $("#fname").val(),
                    middleName: $("#lname").val(),
                    surName: $("#other-names").val(),
                    identificationNumber: $('#id-no').val(),
                    dateOfBirth: moment($("#dob").val(), "DD/MM/YYYY", true).format('YYYY-MM-DD'),
                    nationality: "KE",
                    countryOfResidence: "KE"
                };
            }
            regulatoryAssessmentWorkProducts.commonDetails = commonDetails;
            data.regulatoryAssessmentWorkProducts = regulatoryAssessmentWorkProducts;
            var payload = {};
            payload.request = data;
            payload.clientType = clientType;
            $('#loading-spinner').show();
            $.ajax({
                url: SERVLET_CONTEXT + '/protected/clients/setups/netreveal',
                contentType: 'application/json',
                type: 'POST',
                data: JSON.stringify(payload),
                success: function (response) {
                    $('#loading-spinner').hide();
                    $('#pep-type').val(response.pepType).prop('readonly', true);
                    $('#screening-status').val(response.screeningStatus).prop('readonly', true);
                    Swal.fire({
                        title: 'Success',
                        text: 'Client Screened Successfully',
                        icon: 'success'
                    });
                },
                error: function (jqXHR, textStatus, errorThrown) {
                    $('#loading-spinner').hide();
                    Swal.fire({
                        title: 'Error',
                        text: jqXHR.responseText,
                        icon: 'error'
                    });
                }
            });
        }

        // Trigger re-fetch when Resident Status changes
        $('#resident-status').on('change', function () {
            userDetailsFetched = false;
            if ($(this).val() !== 'resident') {
                $('#passport-no').prop('required', true);
                $('#id-no').prop('required', false);
                $('#pin-no').prop('required', false);
                $('label[for="id-no"]').find('.required').hide();
                $('label[for="pin-no"]').find('.required').hide();
                $('label[for="passport-no"]').append(' <span class="required">*</span>');
            } else {
                $('#id-no').prop('required', true);
                $('#pin-no').prop('required', true);
                $('label[for="id-no"]').find('.required').show();
                $('label[for="pin-no"]').find('.required').show();
                $('#passport-no').prop('required', false);
                $('label[for="passport-no"]').find('.required').remove();
            }
            // Clear previous data when resident status changes
            $('#fname, #lname, #dob, #pin-no, #passport-no, #id-no').val('').prop('readonly', false);
            $('#gender').val('').prop('disabled', false);

            console.log('Resident Status changed, refreshing user information...');

        });

        // Trigger fetchUserDetails when ID/Passport is provided
        $('#id-no, #passport-no').on('blur', function () {
            var clientType = $('#clnt-type-name').val().trim();
            if (!clientType) {
                Swal.fire({
                    icon: 'warning',
                    title: 'Please select the Client type before validation',
                    showConfirmButton: true
                });
                // return;
            }
            if (clientType.toUpperCase() === 'CORPORATE') {
                $('#resident-status').val('company');
                // Skip KRA validation for corporate entities
                // Swal.fire({
                //     title: 'Info',
                //     text: 'KRA validation skipped for corporate clients',
                //     icon: 'info'
                // });
                return;
            } else if (!$('#resident-status').val()) {
                Swal.fire({
                    icon: 'warning',
                    title: 'Please select the Resident Status before validation',
                    showConfirmButton: true
                });
            } else {
                fetchUserDetails(); // This will call KRA validation for individuals
            }
        });
    });

    // preffered mode of communication
    $(document).ready(function () {
        // Listen for changes to the marketing material checkbox
        $('#marketing-material').on('change', function () {
            if ($(this).is(':checked')) {
                // Show the dropdown if the checkbox is checked
                $('#communication-mode-container').slideDown();
            } else {
                // Hide the dropdown and reset its value if the checkbox is unchecked
                $('#communication-mode-container').slideUp();
                $('#preferred-communication').val('');
            }
        });
    });
</script>