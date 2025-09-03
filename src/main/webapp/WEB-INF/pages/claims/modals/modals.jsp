<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<div class="modal fade" id="memberModal" tabindex="-1" role="dialog"
     aria-labelledby="memberModalLabel" aria-hidden="true">
    <div class="modal-dialog modal-lg">
        <div class="modal-content">
            <div class="modal-header">
                <h4 class="modal-title" id="memberModalLabel">Select A
                                    Member</h4>
                <button type="button" class="close" data-dismiss="modal"
                        aria-label="Close">
                    <span aria-hidden="true">&times;</span>
                </button>

            </div>
            <div class="modal-body">
                <div class="box-body">
                    <form id="search-form" class="form-horizontal">
                        <div class="item form-group">
                            <div class="col-md-6">
                                <label for="brn-id" class="col-md-4 label-align">Client/Scheme
                                </label>

                                <div class="col-md-8">
                                    <input type="hidden" id="client-id"/>
                                    <div id="client-frm" class="form-control"
                                         select2-url="<c:url value="/protected/uw/policies/uwClients"/>" >

                                    </div>
                                </div>
                            </div>
                            <div class="col-md-6">
                                <label for="brn-id" class="col-md-4 label-align">Policy
                                    </label>

                                <div class="col-md-8">
                                    <input type="hidden" id="client-pol-id"/>
                                    <div id="med-pol-frm" class="form-control"
                                         select2-url="<c:url value="/protected/medical/claims/selpolicies"/>" >

                                    </div>
                                </div>
                            </div>

                        </div>
                        <div class="item form-group">
                            <div class="col-md-6">
                                <label for="brn-id" class="col-md-4 label-align">Member
                                    Name</label>

                                <div class="col-md-8">
                                    <input type='text' class="form-control float-right" style="text-transform: lowercase"
                                           id="member-search-name" />
                                </div>
                            </div>
                            <div class="col-md-6">
                                <label for="brn-id" class="col-md-4 label-align">Gender
                                </label>

                                <div class="col-md-8">
                                    <select class="form-control" id="gender-search-number"
                                            required>
                                        <option value="">Select Gender</option>
                                        <option value="M">Male</option>
                                        <option value="F">Female</option>
                                    </select>
                                </div>
                            </div>

                        </div>
                        <div class="item form-group">
                            <div class="col-md-6">
                                <label for="brn-id" class="col-md-4 label-align">Card
                                    No.</label>

                                <div class="col-md-8">
                                    <input type='text' class="form-control float-right"
                                           id="card-search-number" />
                                </div>
                            </div>
                            <div class="col-md-6">


                            </div>

                        </div>
                        <div class="item form-group">
                            <input type="button" class="btn btn-info float-right"
                                   style="margin-right: 10px;" value="Search"
                                   id="btn-search-members">
                        </div>


                    </form>
                </div>

                <table id="searchMembersTbl" class="table table-striped" style="width: 100%">
                    <thead>
                    <tr class="headings">

                        <th>Card No.</th>
                        <th>Member No.</th>
                        <th>Member Name</th>
                        <th>Gender</th>
                        <th>DOB</th>
                        <th>Age</th>
                    </tr>
                    </thead>
                </table>
            </div>
        </div>
    </div>
</div>

<div class="modal fade" id="requestModal" tabindex="-1" role="dialog"
     aria-labelledby="requestModalLabel" aria-hidden="true">
    <div class="modal-dialog">
        <div class="modal-content">
            <div class="modal-header">
                <h4 class="modal-title" id="requestModalLabel">
                    Edit/Add Medical Requests
                </h4>
                <button type="button" class="close" data-dismiss="modal"
                        aria-label="Close">
                    <span aria-hidden="true">&times;</span>
                </button>

            </div>
            <div class="modal-body">

                <form id="medical-request-form" class="form-horizontal">
                    <input type="hidden" class="form-control" id="req-code" name="reqId">
                    <div class="item form-group">
                        <label for="brn-id" class="col-md-3 label-align">Request Date</label>

                        <div class="col-md-8 col-xs-12">
                            <div class='input-group date datepicker-input' id="txt-req-date">
                                <input type='text' class="form-control float-right" name="requestDate"
                                        id="txt-request-date" />
                                <div class="input-group-addon">
                                    <span class="fa fa-calendar"></span>
                                </div>
                            </div>
                        </div>
                    </div>
                    <div class="item form-group">
                        <label for="noOfUnits" class="label-align col-md-3">Request
                            Type</label>
                        <div class="col-md-8 col-xs-12">

                                <select class="form-control" id="txt-req-type" name="requestType"
                                        required>
                                    <option value="">Select Request Type</option>
                                    <option value="D">Direct Billing</option>
                                    <option value="R">Reimbursement</option>
                                </select>

                        </div>
                    </div>
                    <div class="item form-group">
                    <label for="noOfUnits" class="label-align col-md-3">Diagnosis
                    </label>
                    <div class="col-md-8 col-xs-12">

                        <input type="hidden" name="ailments" id="txt-diagnosis-id">
                        <input type="hidden"  id="txt-diagnosis-desc">
                        <div id="txt-diagnosis-frm" class="form-control"
                             select2-url="<c:url value="/protected/medical/claims/ailmentssel"/>" >

                        </div>

                    </div>
                   </div>
                    <div class="item form-group">
                        <label for="noOfUnits" class="label-align col-md-3">Service Place
                        </label>
                        <div class="col-md-8 col-xs-12">
                            <select class="form-control" id="ben-service-place" name="servicePlace"
                                    required>
                                <option value="">Select Service Place</option>
                                <option value="IN">INPATIENT</option>
                                <option value="OT">OUTPATIENT</option>
                                <option value="DC">DAY CARE</option>
                            </select>

                        </div>
                    </div>
                    <div class="item form-group">
                        <label for="noOfUnits" class="label-align col-md-3">Request Description
                        </label>
                        <div class="col-md-8 col-xs-12">

                            <textarea class="form-control" rows="5" id="req-description" name="description"></textarea>

                        </div>
                    </div>



                </form>
            </div>
            <div class="modal-footer">
                <button data-loading-text="Saving..." id="saveRequestsBtn"
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


<div class="modal fade" id="serviceModal" tabindex="-1" role="dialog"
     aria-labelledby="serviceModalLabel" aria-hidden="true">
    <div class="modal-dialog">
        <div class="modal-content">
            <div class="modal-header">
                <h4 class="modal-title" id="serviceModalLabel">
                    Edit/Add Medical Request Services
                </h4>
                <button type="button" class="close" data-dismiss="modal"
                        aria-label="Close">
                    <span aria-hidden="true">&times;</span>
                </button>

            </div>
            <div class="modal-body">

                <form id="medical-service-form" class="form-horizontal">
                    <input type="hidden" class="form-control" id="req-service-code" name="reqServiceId">
                    <input type="hidden" class="form-control" id="req-service-reg-code" name="request">
                    <div class="item form-group">
                        <label for="brn-id" class="col-md-3 label-align">Manual
                            Control</label>

                        <div class="col-md-8 col-xs-12">
                            <div class="col-md-9 checkbox">
                                <label>
                                    <input type="checkbox" name="manualControl" id="chk-manual-ctrl">
                                </label>
                            </div>
                        </div>
                    </div>
                    <div class="item form-group">
                        <label for="brn-id" class="col-md-3 label-align">Service Date</label>

                        <div class="col-md-8 col-xs-12">
                            <div class='input-group date datepicker-input' id="txt-service-date">
                                <input  type='text' class="form-control float-right" name="serviceDate"
                                        id="txt-request-serv-date" />
                                <div class="input-group-addon">
                                    <span class="fa fa-calendar"></span>
                                </div>
                            </div>
                        </div>
                    </div>
                    <div class="item form-group">
                        <label for="noOfUnits" class="label-align col-md-3">Medical Service
                        </label>
                        <div class="col-md-8 col-xs-12">

                            <input type="hidden" name="medicalservice" id="serv-id">
                            <input type="hidden"  id="serv-desc">
                            <div id="medicalservice" class="form-control"
                                 select2-url="<c:url value="/protected/medical/claims/medicalServices"/>" >

                            </div>

                        </div>
                    </div>
                    <div class="item form-group">
                        <label for="noOfUnits" class="label-align col-md-3">Activity
                        </label>
                        <div class="col-md-8 col-xs-12">

                            <input type="hidden" name="medActivities" id="serv-activity-id">
                            <input type="hidden"  id="serv-activity-desc">
                            <div id="serv-activity-frm" class="form-control"
                                 select2-url="<c:url value="/protected/medical/claims/activitiessel"/>" >

                            </div>

                        </div>
                    </div>
                    <div class="item form-group">
                        <label for="noOfUnits" class="label-align col-md-3">Quantity
                        </label>
                        <div class="col-md-8 col-xs-12">
                            <input type="number"  class="form-control" name="reqQuantity"
                                   id="req-service-qty" />
                        </div>
                    </div>

                    <div class="item form-group">
                        <label for="noOfUnits" class="label-align col-md-3">Requested Price
                        </label>
                        <div class="col-md-8 col-xs-12">
                            <input type="text"  class="form-control currency" name="reqPrice"
                                   id="req-service-price" />
                        </div>
                    </div>
                    <div class="item form-group">
                        <label for="noOfUnits" class="label-align col-md-3">Approved Quantity
                        </label>
                        <div class="col-md-8 col-xs-12">
                            <input type="number"  class="form-control" name="appQuantity"
                                   id="req-approved-qty" disabled/>
                        </div>
                    </div>

                    <div class="item form-group">
                        <label for="noOfUnits" class="label-align col-md-3">Approved Price
                        </label>
                        <div class="col-md-8 col-xs-12">
                            <input type="text"  class="form-control currency" name="appPrice"
                                   id="req-approved-price" disabled />
                        </div>
                    </div>

                    <div class="item form-group">
                        <label for="noOfUnits" class="label-align col-md-3">Affected Benefit
                        </label>
                        <div class="col-md-8 col-xs-12">
                            <p class="form-control-static" id="par-affected-benefit"> </p>
                        </div>
                    </div>
                    <div class="item form-group">
                            <label for="brn-id" class="col-md-3 label-align">Exgratia
                            </label>

                            <div class="col-md-8 col-xs-12">
                                <div class="col-md-9 checkbox">
                                    <label>
                                        <input type="checkbox" name="exgratiaService" id="req-exgratia-ctrl" disabled>
                                    </label>
                                </div>
                            </div>
                        </div>
                    <div class="item form-group">
                            <label for="noOfUnits" class="label-align col-md-3">Amount</label>
                            <div class="col-md-8 col-xs-12">
                                <input type="text"  class="form-control currency" name="exgratiaAmount"
                                       id="req-exgratia-amount" disabled />
                            </div>

                        </div>

                </form>
            </div>
            <div class="modal-footer">
                <button data-loading-text="Saving..." id="saveRequestServiceBtn"
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

<div class="modal fade" id="serviceLogModal" tabindex="-1" role="dialog"
     aria-labelledby="serviceLogModalLabel" aria-hidden="true">
    <div class="modal-dialog">
        <div class="modal-content">
            <div class="modal-header">
                <h4 class="modal-title" id="serviceLogModalLabel">Service Log</h4>
                <button type="button" class="close" data-dismiss="modal"
                        aria-label="Close">
                    <span aria-hidden="true">&times;</span>
                </button>

            </div>
            <div class="modal-body">
                <table id="med_service_log_tbl" class="table table-striped" style="width: 100%">
                    <thead>
                    <tr class="headings">
                        <th width="10%">ID</th>
                        <th width="90%">Log Description</th>
                    </tr>
                    </thead>
                </table>
            </div>
            <div class="modal-footer">

                <button type="button" class="btn btn-default" data-dismiss="modal">
                    Close</button>
            </div>
        </div>
    </div>
</div>


<div class="modal fade" id="reportsModal" tabindex="-1" role="dialog"
     aria-labelledby="reportsModalLabel" aria-hidden="true">
    <div class="modal-dialog">
        <div class="modal-content">
            <div class="modal-header">
                <h4 class="modal-title" id="reportsModalLabel">Reports</h4>
                <button type="button" class="close" data-dismiss="modal"
                        aria-label="Close">
                    <span aria-hidden="true">&times;</span>
                </button>

            </div>
            <div class="modal-body">
                <div class="row">
                    <div class="col-md-6">

                        <ul style="list-style-type: none;" class="reports-links">
                            <li><a
                                    href="<c:url value='/protected/medical/claims/rpt_inp_letter'/> "
                                    target="_blank">InPatient Undertaking Letter</a></li>
                            <li><a
                                    href="<c:url value='/protected/medical/claims/rpt_outp_letter'/> "
                                    target="_blank">OutPatient Undertaking Letter</a></li>
                            <li class="rejection_letter"><a
                                    href="<c:url value='/protected/medical/claims/rpt_decline_letter'/> "
                                    target="_blank">Preauth Decline Letter</a></li>
                            <li ><a href="javascript:void(0);"  onclick="UTILITIES.sendEmail();">Send Email</a></li>
                        </ul>
                    </div>
                    <div class="col-md-6">

                        <ul style="list-style-type: none;">
                            <!--<li><a href="#">Invoice Note</a></li> -->
                        </ul>

                    </div>
                </div>


            </div>
            <div class="modal-footer">

                <button type="button" class="btn btn-default" data-dismiss="modal">
                    Close</button>
            </div>
        </div>
    </div>
</div>

<div class="modal fade" id="emailModal" tabindex="-1" role="dialog"
     aria-labelledby="emailModalLabel" aria-hidden="true">
    <div class="modal-dialog modal-lg">
        <div class="modal-content">
            <div class="modal-header">
                <h4 class="modal-title" id="emailModalLabel">Send Email</h4>
                <button type="button" class="close" data-dismiss="modal"
                        aria-label="Close">
                    <span aria-hidden="true">&times;</span>
                </button>

            </div>
            <div class="modal-body">
                <form id="email-form"  class="form-horizontal">
                    <div class="item form-group">
                        <label for="email-to" class="col-md-2 label-align">Email To</label>

                        <div class="col-md-10">
                            <select class="form-control" id="email-to" name="receiverType">
                                <option value="">Select Email To</option>
                                <option value="S">Service Provider</option>

                            </select>
                        </div>
                    </div>
                    <div class="item form-group">
                        <label for="email-send-to" class="col-md-2 label-align">Send To</label>

                        <div class="col-md-10">
                            <input type="text" class="editUserCntrls form-control"
                                   id="email-send-to" name="sendTo"
                                   readonly>
                        </div>
                    </div>
                    <div class="item form-group">
                        <label for="email-cc" class="col-md-2 label-align">CC</label>

                        <div class="col-md-10">
                            <input type="text" class="editUserCntrls form-control"
                                   id="email-cc" name="sendCC"
                            >
                        </div>
                    </div>
                    <div class="item form-group">
                        <label for="email-bcc" class="col-md-2 label-align">BCC</label>

                        <div class="col-md-10">
                            <input type="text" class="editUserCntrls form-control"
                                   id="email-bcc" name="sendBcc"
                            >
                        </div>
                    </div>
                    <div class="item form-group">
                        <label for="cou-name" class="col-md-2 label-align">Reports</label>

                        <div class="col-md-10">
                            <label class="checkbox-inline"><input type="checkbox" value="IL">InPatient Undertaking Letter</label>
                            <label class="checkbox-inline"><input type="checkbox" value="DN">OutPatient Undertaking Letter</label>
                            <label class="checkbox-inline"><input type="checkbox" value="DL">Preauth Decline Letter</label>
                        </div>
                    </div>
                    <div class="item form-group">
                        <label for="email-subject" class="col-md-2 label-align">Subject</label>

                        <div class="col-md-10">
                            <input type="text" class="editUserCntrls form-control"
                                   id="email-subject" name="subject"
                                   required>
                        </div>
                    </div>
                    <div class="item form-group">
                        <label for="unit-id" class="col-md-2 label-align">Email Template</label>

                        <div class="col-md-10">
                            <textarea class="form-control" rows="10" cols="20" id="email-template" name="message"></textarea>
                        </div>
                    </div>

                </form>

            </div>
            <div class="modal-footer">
                <button data-loading-text="Saving..." id="sendEmailForm"
                        type="button" class="btn btn-success">Send</button>
                <button type="button" class="btn btn-default" data-dismiss="modal">
                    Close</button>
            </div>
        </div>
    </div>
</div>

<div class="modal fade" id="memberDtlsModal" tabindex="-1" role="dialog"
     aria-labelledby="memberDtlsModalLabel" aria-hidden="true">
    <div class="modal-dialog modal-sm">
        <div class="modal-content">
            <div class="modal-header">
                <h4 class="modal-title" id="memberDtlsModalLabel">
                    Member Details
                </h4>
                <button type="button" class="close" data-dismiss="modal"
                        aria-label="Close">
                    <span aria-hidden="true">&times;</span>
                </button>

            </div>
            <div class="modal-body">
                <form id="member-dtls-form" class="form-horizontal form-label-left">
                    <div class="item form-group form-required">
                        <div class="col-md-12 col-xs-12">
                            <label for="clm-member-info" class="label-align col-md-4">
                                Member:</label>
                            <div class="col-md-8 col-xs-12">
                                <p class="form-control-static" id="clm-member-info"></p>
                            </div>
                        </div>
                    </div>
                    <div class="item form-group form-required">
                        <div class="col-md-12 col-xs-12">
                            <label for="clm-member-client-id" class="label-align col-md-4">
                                Client ID:</label>
                            <div class="col-md-8 col-xs-12">
                                <p class="form-control-static" id="clm-member-client-id"></p>
                            </div>
                        </div>
                    </div>
                    <div class="item form-group form-required">
                        <div class="col-md-12 col-xs-12">
                            <label for="clm-member-gender" class="label-align col-md-4">
                                Gender:</label>
                            <div class="col-md-8 col-xs-12">
                                <p class="form-control-static" id="clm-member-gender"></p>
                            </div>
                        </div>
                    </div>
                    <div class="item form-group form-required">
                        <div class="col-md-12 col-xs-12">
                            <label for="clm-member-dob" class="label-align col-md-4">
                                DOB:</label>
                            <div class="col-md-8 col-xs-12">
                                <p class="form-control-static" id="clm-member-dob"></p>
                            </div>
                        </div>
                    </div>
                    <div class="item form-group form-required">
                        <div class="col-md-12 col-xs-12">
                            <label for="clm-member-age" class="label-align col-md-4">
                                Age:</label>
                            <div class="col-md-8 col-xs-12">
                                <p class="form-control-static" id="clm-member-age"></p>
                            </div>
                        </div>
                    </div>
                    <div class="item form-group form-required">
                        <div class="col-md-12 col-xs-12">
                            <label for="clm-member-card" class="label-align col-md-4">
                                Card No:</label>
                            <div class="col-md-8 col-xs-12">
                                <p class="form-control-static" id="clm-member-card"></p>
                            </div>
                        </div>
                    </div>
                    <div class="item form-group form-required">
                        <div class="col-md-12 col-xs-12">
                            <label for="clm-member-wef" class="label-align col-md-4">
                                WEF:</label>
                            <div class="col-md-8 col-xs-12">
                                <p class="form-control-static" id="clm-member-wef"></p>
                            </div>
                        </div>
                    </div>
                    <div class="item form-group form-required">
                        <div class="col-md-12 col-xs-12">
                            <label for="clm-member-wet" class="label-align col-md-4">
                                WET:</label>
                            <div class="col-md-8 col-xs-12">
                                <p class="form-control-static" id="clm-member-wet"></p>
                            </div>
                        </div>
                    </div>
                    <div class="item form-group form-required">
                        <div class="col-md-12 col-xs-12">
                            <label for="clm-member-category" class="label-align col-md-4">
                                Category:</label>
                            <div class="col-md-8 col-xs-12">
                                <p class="form-control-static" id="clm-member-category"></p>
                            </div>
                        </div>
                    </div>
                    <div class="item form-group form-required">
                        <div class="col-md-12 col-xs-12">
                            <label for="clm-member-family" class="label-align col-md-4">
                                Family:</label>
                            <div class="col-md-8 col-xs-12">
                                <p class="form-control-static" id="clm-member-family"></p>
                            </div>
                        </div>
                    </div>
                </form>

            </div>
            <div class="modal-footer">
                <button type="button" class="btn btn-default" data-dismiss="modal">
                    OK
                </button>
            </div>
        </div>
    </div>
</div>



<div class="modal fade" id="clmdocModal" tabindex="-1" role="dialog"
     aria-labelledby="clmdocModalLabel" aria-hidden="true">
    <div class="modal-dialog">
        <form id="clm-doc-form" class="form-horizontal" enctype="multipart/form-data">
            <div class="modal-content">
                <div class="modal-header">
                    <h4 class="modal-title" id="clmdocModalLabel">Upload Claim Document</h4>
                    <button type="button" class="close" data-dismiss="modal"
                            aria-label="Close">
                        <span aria-hidden="true">&times;</span>
                    </button>

                </div>
                <div class="modal-body">

                    <input type="hidden" id="risk-doc-id" name="prdId"/>
                    <input type="hidden" id="risk-sub-doc-id" name="reqdDocs"/>
                    <div class="item form-group">
                        <label for="risk-doc-name" class="col-md-3 label-align">Document Type</label>

                        <div class="col-md-8">
                            <p class="form-control-static" id="risk-doc-name"></p>
                        </div>
                    </div>
                    <div class="item form-group">
                        <label for="risk-upload-name" class="col-md-3 label-align">File Name</label>

                        <div class="col-md-8">
                            <p class="form-control-static" id="risk-upload-name"></p>
                        </div>
                    </div>
                    <div class="item form-group">
                        <label for="brn-id" class="col-md-4 label-align">Document</label>

                        <div class="col-md-8">
                            <div class="input-group col-xs-12">
                                <input name="file" type="file" id="avatar" required>
                            </div>
                        </div>
                    </div>



                </div>
                <div class="modal-footer">
                    <input  value="Upload"
                            type="submit" class="btn btn-success">

                    </input>
                    <button type="button" class="btn btn-default" data-dismiss="modal">
                        Close</button>
                </div>
            </div>
        </form>
    </div>
</div>