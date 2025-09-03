<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="sec" uri="http://www.springframework.org/security/tags" %>
<script type="text/javascript" src="<c:url value="/js/modules/utils/select2builder.js"/>"></script>
<script type="text/javascript" src="<c:url value="/libs/rivets/rivets.js"/>"></script>
<script type="text/javascript" src="<c:url value="/js/modules/claims/preauthclaims.js"/>"></script>
<script type="text/javascript">
    var parCode = ${parCode};
</script>
<div class="x_content">
    <div class="x_panel">
        <c:if test="${error != null}">
            <div class="alert alert-error alert-dismissible">
                <a href="#" class="close" data-dismiss="alert" aria-label="close">&times;</a>
                    ${error}
            </div>
        </c:if>
        <input type="button" class="btn btn-info float-right"
               value="Reports" id="btn-claim-reports" data-toggle="modal" data-target="#reportsModal"
               style="display:none">

        <div class="float-right" id="div-convert-clm">
            <form:form action="convertpreauth" method="post" commandName="modelHelperForm"><form:hidden path="id"
                                                                                                        id="par-id-pk"/>
                <input type="submit" class="btn btn-info" value="Convert to Claim" id="btn-convert-claim">
            </form:form>
        </div>
        <input type="button" class="btn btn-info float-right"
               value="Save" id="btn-save-clm">
        <input type="button" class="btn btn-info float-right"
               value="Submit" id="btn-make-ready">
        <input type="button" class="btn btn-info float-right"
               value="Authorize" id="btn-auth-clm">


        <div class="x_title">
            <h2><i class="fa fa-bars"></i> Pre-Authorization Transaction</h2>
            <ul class="nav navbar-right panel_toolbox">
                <li><a class="collapse-link"><i class="fa fa-chevron-up"></i></a>
                </li>
            </ul>
            <div class="clearfix"></div>
        </div>
        <div class="x_content">
            <form id="clm-form" class="form-horizontal form-label-left">
                <div class="item form-group form-required">
                    <div class="col-md-6 col-xs-12">
                        <input type="hidden" name="parId" id="par-id">
                        <input type="hidden" name="category" id="med-par-category-pk">
                        <label for="pol-appr-type" class="label-align col-md-5">
                            Pre-Approval Type<span class="required">*</span></label>
                        <div class="col-md-7 col-xs-12">
                            <select class="form-control" id="pol-appr-type" name="approvalType"
                                    required>
                                <option value="">Select Pre-Approval Type</option>
                                <option value="EL">Elective</option>
                                <option value="EM">Emergency</option>
                            </select>
                        </div>
                    </div>
                    <div class="col-md-6 col-xs-12">
                        <label for="member-number" class="label-align col-md-5">
                            Member No.<span class="required">*</span></label>
                        <div class="col-md-7 col-xs-12">
                            <input type="hidden" name="clientDef" id="member-client-id">
                            <input type="hidden" name="categoryMember" id="member-cat-id-pk">
                            <div class='input-group'>
                                <input type="text" class="form-control float-right" id="member-number"
                                       required="required" readonly="true"/>
                                <div class="input-group-addon show-search-clm">
								<span class="glyphicon glyphicon-chevron-down"
                                      id="btn-show-search-clm" style="cursor: pointer"></span>
                                </div>
                            </div>
                            <input type="button" class="btn btn-info btn-sm" id="member-dtls-btn" value="Details">
                        </div>
                    </div>

                </div>
                <div class="item form-group form-required">
                    <div class="col-md-6 col-xs-12">
                        <label for="event-frm" class="label-align col-md-5">
                            Event Type<span class="required">*</span></label>
                        <div class="col-md-7 col-xs-12">
                            <input type="hidden" name="events" id="events-id">
                            <input type="hidden" id="events-name">
                            <div id="event-frm" class="form-control"
                                 select2-url="<c:url value="/protected/medical/claims/selevents"/>">

                            </div>
                        </div>
                    </div>
                    <div class="col-md-6 col-xs-12">
                        <label for="countryName" class="label-align col-md-5">Event Country</label>
                        <div class="col-md-7 col-xs-12">
                            <div class="item form-group">
                                <input type="hidden" name="country" id="country-id">
                                <input type="hidden" id="country-name">
                                <div id="country" class="form-control"
                                     select2-url="<c:url value="/protected/organization/countries"/>">

                                </div>
                            </div>
                        </div>
                    </div>

                </div>
                <div class="item form-group">
                    <div class="col-md-6 col-xs-12  form-required">
                        <label for="brn-id" class="col-md-5 label-align">Event
                            Date</label>

                        <div class="col-md-7 col-xs-12">
                            <div class='input-group date datepicker-input' id="wef-date">
                                <input class="form-control float-right" name="eventDate"
                                       id="from-date"/>
                                <div class="input-group-addon">
                                    <span class="fa fa-calendar"></span>
                                </div>
                            </div>
                        </div>
                    </div>
                    <div class="col-md-6 col-xs-12">
                        <label for="noOfUnits" class="label-align col-md-5">Notification
                            Date</label>
                        <div class="col-md-7 col-xs-12">
                            <div class='input-group date datepicker-input' id="cover-to-date">
                                <input type='text' class="form-control float-right" name="notDate"
                                       id="wet-date"/>
                                <div class="input-group-addon">
                                    <span class="fa fa-calendar"></span>
                                </div>
                            </div>
                        </div>

                    </div>
                </div>

                <div class="item form-group form-required">
                    <div class="col-md-6 col-xs-12">
                        <label for="pol-no" class="label-align col-md-5">
                            Policy Number</label>
                        <div class="col-md-7 col-xs-12">
                            <input type="hidden" name="policyTrans" id="policy-id">
                            <p class="form-control-static" id="pol-no"></p>
                        </div>
                    </div>
                    <div class="col-md-6 col-xs-12">
                        <label for="network-frm" class="label-align col-md-5">
                            Network Code<span class="required">*</span></label>
                        <div class="col-md-7 col-xs-12">
                            <input type="hidden" name="medicalNetworks" id="med-networks">
                            <input type="hidden" id="med-networks-name">
                            <div id="network-frm" class="form-control"
                                 select2-url="<c:url value="/protected/medical/claims/networkssel"/>">

                            </div>
                        </div>
                    </div>
                </div>

                <div class="item form-group form-required">
                    <div class="col-md-6 col-xs-12">
                        <label for="provider-frm" class="label-align col-md-5">
                            Contract No<span class="required">*</span></label>
                        <div class="col-md-7 col-xs-12">
                            <input type="hidden" name="providerContracts" id="provider-id">
                            <input type="hidden" id="provider-contract-no">
                            <div id="provider-frm" class="form-control"
                                 select2-url="<c:url value="/protected/medical/claims/providercontracts"/>">

                            </div>
                        </div>
                    </div>
                    <div class="col-md-6 col-xs-12">
                        <label for="countryName" class="label-align col-md-5">Service Provider</label>
                        <div class="col-md-7 col-xs-12">
                            <div class="item form-group">
                                <p class="form-control-static" id="provider-no"></p>
                            </div>
                        </div>
                    </div>

                </div>
                <div class="med_requests">
                    <div class="item form-group">
                        <div class="col-md-6 col-xs-12  form-required">
                            <label for="diagnosis-frm" class="label-align col-md-5">
                                Diagnosis<span class="required">*</span></label>
                            <div class="col-md-7 col-xs-12">
                                <input type="hidden" name="diagnosisId" id="diagnosis-id">
                                <div id="diagnosis-frm" class="form-control"
                                     select2-url="<c:url value="/protected/medical/claims/ailmentssel"/>">

                                </div>
                            </div>
                        </div>
                        <div class="col-md-6 col-xs-12  form-required">
                            <label for="clm-service-place" class="label-align col-md-5">
                                Service Place<span class="required">*</span></label>
                            <div class="col-md-7 col-xs-12">
                                <select class="form-control" id="clm-service-place" name="servicePlace"
                                        required>
                                    <option value="">Select Service Place</option>
                                    <option value="IN">INPATIENT</option>
                                    <option value="OT">OUTPATIENT</option>
                                    <option value="DC">DAY CARE</option>
                                </select>
                            </div>
                        </div>

                    </div>
                </div>
                <div class="item form-group">
                    <div class="col-md-6 col-xs-12  form-required">
                        <label for="brn-id" class="col-md-5 label-align">Admission
                            Date</label>

                        <div class="col-md-7 col-xs-12">
                            <div class='input-group date datepicker-input' id="adm-date">
                                <input class="form-control float-right" name="admissionDate"
                                       id="admission-date"/>
                                <div class="input-group-addon">
                                    <span class="fa fa-calendar"></span>
                                </div>
                            </div>
                        </div>
                    </div>

                    <div class="col-md-6 col-xs-12">
                        <label for="noOfUnits" class="label-align col-md-5">
                            Discharge Date</label>
                        <div class="col-md-7 col-xs-12">
                            <div class='input-group date datepicker-input' id="exp-adm-date">
                                <input class="form-control float-right" name="expectedAdmDate"
                                       id="exp-admission-date"/>
                                <div class="input-group-addon">
                                    <span class="fa fa-calendar"></span>
                                </div>
                            </div>
                        </div>

                    </div>
                </div>

                <div class="other-clm-details">
                    <div class="item form-group form-required">
                        <div class="col-md-6 col-xs-12">
                            <label for="par-trans-no" class="label-align col-md-5">
                                PAR No</label>
                            <div class="col-md-7 col-xs-12">
                                <input type="hidden" id="div-pol-no" name="polNo"/>
                                <p class="form-control-static" id="par-trans-no"></p>
                            </div>
                        </div>
                        <div class="col-md-6 col-xs-12">
                            <label for="par-rev-no" class="label-align col-md-5">
                                PAR Revision Number</label>
                            <div class="col-md-7 col-xs-12">
                                <input type="hidden" id="div-endos-no" name="polRevNo"/>
                                <p class="form-control-static" id="par-rev-no"></p>
                            </div>
                        </div>
                    </div>
                    <div class="item form-group form-required">
                        <div class="col-md-6 col-xs-12">
                            <label for="par-trans-status" class="label-align col-md-5">
                                Status</label>
                            <div class="col-md-7 col-xs-12">
                                <p class="form-control-static" id="par-trans-status"></p>
                            </div>
                        </div>
                        <div class="col-md-6 col-xs-12">
                            <label for="par-converted" class="label-align col-md-5">
                                Converted?</label>
                            <div class="col-md-7 col-xs-12">
                                <p class="form-control-static" id="par-converted"></p>
                            </div>


                        </div>
                    </div>
                    <div class="item form-group form-required">
                        <div class="col-md-6 col-xs-12">
                            <label for="par-request-amount" class="label-align col-md-5">
                                Total Request Amount</label>
                            <div class="col-md-7 col-xs-12">
                                <p class="form-control-static" id="par-request-amount"></p>
                            </div>
                        </div>
                        <div class="col-md-6 col-xs-12">
                            <label for="par-appr-amount" class="label-align col-md-5">
                                Total Approved Amount</label>
                            <div class="col-md-7 col-xs-12">
                                <p class="form-control-static" id="par-appr-amount"></p>
                            </div>


                        </div>
                    </div>
                    <div class="item form-group form-required">
                        <div class="col-md-6 col-xs-12">
                            <label for="par-rej-amount" class="label-align col-md-5">
                                Total Rejected Amount</label>
                            <div class="col-md-7 col-xs-12">
                                <p class="form-control-static" id="par-rej-amount"></p>
                            </div>
                        </div>
                        <div class="col-md-6 col-xs-12">

                        </div>
                    </div>
                </div>

                <div class="" role="tabpanel" data-example-id="togglable-tabs">
                    <ul id="myTab" class="nav nav-tabs bar_tabs" role="tablist">
                        <li role="presentation" class="active"><a href="#tab_content1"
                                                                  id="home-tab" role="tab" data-toggle="tab"
                                                                  aria-expanded="true">Medical Requests</a>
                        </li>
                        <li role="presentation" class="" id="show-taxes"><a href="#tab_content2"
                                                                            role="tab" id="profile-tab"
                                                                            data-toggle="tab" aria-expanded="false">Claim
                            Required Documents</a>
                        </li>
                    </ul>
                    <div id="myTabContent" class="tab-content">
                        <div role="tabpanel" class="tab-pane active"
                             id="tab_content1" aria-labelledby="home-tab">
                            <div id="med_requests_tbl">
                                <div class="card-box table-responsive">

                                    <input type="button" class="btn btn-success btn btn-info" id="btn-add-new-request"
                                           value="New">
                                    <input type="hidden" id="par-request-code-pk">

                                    <table id="med_request_tbl" class="table table-striped" style="width: 100%">
                                        <thead>
                                        <tr class="headings">
                                            <th>Request Date</th>
                                            <th>Request Type</th>
                                            <th>Diagnosis</th>
                                            <th>Service Place</th>
                                            <th>Description</th>
                                            <th>Request Amount</th>
                                            <th>Approved Amt</th>
                                            <th>Rej. Amt</th>
                                            <th width="5%"></th>
                                            <th width="5%"></th>
                                        </tr>
                                        </thead>
                                    </table>
                                </div>
                            </div>


                            <div class="med_requests">
                                <div class="item form-group">
                                    <div class="col-md-6 col-xs-12  form-required">
                                        <label for="brn-id" class="col-md-5 label-align">Request
                                            Date</label>

                                        <div class="col-md-7 col-xs-12">
                                            <div class='input-group date datepicker-input' id="req-date">
                                                <input class="form-control float-right" name="requestDate"
                                                       id="request-date"/>
                                                <div class="input-group-addon">
                                                    <span class="fa fa-calendar"></span>
                                                </div>
                                            </div>
                                        </div>
                                    </div>
                                    <div class="col-md-6 col-xs-12">
                                        <label for="noOfUnits" class="label-align col-md-5">Request
                                            Type</label>
                                        <div class="col-md-7 col-xs-12">
                                            <select class="form-control" id="req-type" name="requestType"
                                                    required>
                                                <option value="">Select Request Type</option>
                                                <option value="D">Direct Billing</option>
                                                <option value="R">Reimbursement</option>
                                            </select>
                                        </div>
                                    </div>
                                </div>

                                <div class="item form-group">
                                    <div class="col-md-6 col-xs-12  form-required">
                                        <label for="request-desc" class="label-align col-md-5">
                                            Description<span class="required">*</span></label>
                                        <div class="col-md-7 col-xs-12">
                                    <textarea class="form-control" rows="2" id="request-desc"
                                              name="requestDescription"></textarea>
                                        </div>
                                    </div>
                                    <div class="col-md-6 col-xs-12  form-required">
                                    </div>
                                </div>
                            </div>
                            <div class="x_title">
                                <h4>Medical Services</h4>
                            </div>

                            <div id="edit_services">
                                <div class="item form-group">
                                    <div class="col-md-6 col-xs-12  form-required">
                                        <label for="brn-id" class="col-md-5 label-align">Manual
                                            Control</label>

                                        <div class="col-md-7 col-xs-12">
                                            <div class="col-md-9 checkbox">
                                                <label>
                                                    <input type="checkbox" name="manualControl" id="manual-ctrl">
                                                </label>
                                            </div>
                                        </div>
                                    </div>
                                    <div class="col-md-6 col-xs-12">
                                        <label for="noOfUnits" class="label-align col-md-5">Medical Service</label>
                                        <div class="col-md-7 col-xs-12">
                                            <input type="hidden" name="activityId" id="serv-id2">
                                            <input type="hidden" id="serv-desc2">
                                            <div id="medicalservice2" class="form-control"
                                                 select2-url="<c:url value="/protected/medical/claims/medicalServices"/>">

                                            </div>
                                        </div>

                                    </div>


                                </div>
                                <div class="item form-group">
                                    <div class="col-md-6 col-xs-12  form-required">
                                        <label for="brn-id" class="col-md-5 label-align">Service Date
                                            Date</label>

                                        <div class="col-md-7 col-xs-12">
                                            <div class='input-group date datepicker-input' id="serv-date">
                                                <input class="form-control float-right" name="serviceDate"
                                                       id="service-date"/>
                                                <div class="input-group-addon">
                                                    <span class="fa fa-calendar"></span>
                                                </div>
                                            </div>
                                        </div>
                                    </div>
                                    <div class="col-md-6 col-xs-12">
                                        <label for="noOfUnits" class="label-align col-md-5">Activity</label>
                                        <div class="col-md-7 col-xs-12">
                                            <input type="hidden" name="activityId" id="activity-id">
                                            <input type="hidden" id="activity-desc">
                                            <div id="activity-frm" class="form-control"
                                                 select2-url="<c:url value="/protected/medical/claims/activitiessel"/>">

                                            </div>
                                        </div>

                                    </div>


                                </div>
                                <div class="item form-group">
                                    <div class="col-md-6 col-xs-12  form-required">
                                        <label for="brn-id" class="col-md-5 label-align">Requested Quantity</label>

                                        <div class="col-md-7 col-xs-12">
                                            <input type="number" class="form-control" name="qty"
                                                   id="service-qty"/>

                                        </div>
                                    </div>
                                    <div class="col-md-6 col-xs-12">
                                        <label for="noOfUnits" class="label-align col-md-5">Requested Price</label>
                                        <div class="col-md-7 col-xs-12">
                                            <input type="text" class="form-control currency" name="price"
                                                   id="service-price"/>
                                        </div>

                                    </div>


                                </div>
                                <div class="item form-group">
                                    <div class="col-md-6 col-xs-12  form-required">
                                        <label for="brn-id" class="col-md-5 label-align">Approved Quantity</label>

                                        <div class="col-md-7 col-xs-12">
                                            <input type="number" class="form-control" name="approvedQty"
                                                   id="approved-qty" disabled/>

                                        </div>
                                    </div>
                                    <div class="col-md-6 col-xs-12">
                                        <label for="noOfUnits" class="label-align col-md-5">Approved Price</label>
                                        <div class="col-md-7 col-xs-12">
                                            <input type="text" class="form-control currency" name="approvedPrice"
                                                   id="approved-price" disabled/>
                                        </div>

                                    </div>
                                </div>
                                <div class="item form-group">
                                    <div class="col-md-6 col-xs-12  form-required">
                                        <label for="brn-id" class="col-md-5 label-align">Benefit Affected</label>
                                        <div class="col-md-7 col-xs-12">
                                            <p class="form-control-static" id="txt-benefit-affected"></p>
                                        </div>
                                    </div>
                                    <div class="col-md-6 col-xs-12  form-required">
                                    </div>
                                </div>
                                <div class="item form-group">
                                    <div class="col-md-6 col-xs-12  form-required">
                                        <label for="brn-id" class="col-md-5 label-align">Exgratia
                                        </label>

                                        <div class="col-md-7 col-xs-12">
                                            <div class="col-md-9 checkbox">
                                                <label>
                                                    <input type="checkbox" name="exgratiaService" id="exgratia-ctrl"
                                                           disabled>
                                                </label>
                                            </div>
                                        </div>
                                    </div>
                                    <div class="col-md-6 col-xs-12">
                                        <label for="noOfUnits" class="label-align col-md-5">Amount</label>
                                        <div class="col-md-7 col-xs-12">
                                            <input type="text" class="form-control currency" name="exgratiaAmount"
                                                   id="exgratia-amount" disabled/>
                                        </div>

                                    </div>


                                </div>
                            </div>
                            <div id="med_services_tbl">
                                <div class="card-box table-responsive">
                                    <input type="button" class="btn btn-success btn btn-info" id="btn-add-new-service"
                                           value="New">
                                    <table id="med_service_tbl" class="table table-striped" style="width: 100%">
                                        <thead>
                                        <tr class="headings">
                                            <th>Service Date</th>
                                            <th>Activity ID</th>
                                            <th>Req. Quantity</th>
                                            <th>Req. Price</th>
                                            <th>Req. Amount</th>
                                            <th>Appr. Quantity</th>
                                            <th>Appr. Price</th>
                                            <th>Appr. Amount</th>
                                            <th>Rej. Amt</th>
                                            <th>Service Limit</th>
                                            <th width="5%"></th>
                                            <th width="5%"></th>
                                            <th width="5%"></th>
                                        </tr>
                                        </thead>
                                    </table>
                                </div>
                            </div>
                        </div>
                        <div role="tabpanel" class="tab-pane fade"
                             id="tab_content2" aria-labelledby="profile-tab">
                            <table id="par_docs_tbl" class="table table-striped" style="width: 100%">
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
            </form>
        </div>
    </div>
</div>


<jsp:include page="modals/modals.jsp"></jsp:include>