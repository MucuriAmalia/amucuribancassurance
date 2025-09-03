<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="sec" uri="http://www.springframework.org/security/tags" %>
<script type="text/javascript" src="<c:url value="/js/modules/utils/select2builder.js"/>"></script>
<script type="text/javascript" src="<c:url value="/js/modules/utils/select2builder.js"/>"></script>
<script type="text/javascript" src="<c:url value="/libs/rivets/rivets.js"/>"></script>
<%--<script  src="<c:url value='/libs/moment/min/moment.min.js' /> "></script>--%>
<script type="text/javascript" src="<c:url value="/js/modules/claims/claimtrans.js"/>"></script>
 <script type="text/javascript">
    var clmId = ${clmId};
    var mckClaimNo = ${taskId != null ? taskId : clmId};
    var approvalStatus =${approvalStatus != null ? approvalStatus : "N"};
</script>

<div class="x_content">

    <div class="x_panel">
    <div class="x_title">
        <h2><i class="fa fa-bars"></i> Claim Transaction Details</h2>
        <div class="clearfix"></div>
    </div>
        <div class="col-xs-2">
        <ul id="myTab" class="nav nav-tabs tabs-left" role="tablist">
            <li role="presentation" class="active"><a href="#tab_content1"
                                                      id="home-tab" role="tab" data-toggle="tab" aria-expanded="true">Overview</a>
            </li>
            <li role="presentation" class="" id="show-taxes"><a href="#tab_content2"
                                                                role="tab" id="profile-tab" data-toggle="tab" aria-expanded="false">Claimant Details</a>
            </li>
            <li role="presentation" class="" id="show-clauses"><a href="#tab_content3"
                                                                  role="tab" id="comm-rates-tab" data-toggle="tab" aria-expanded="false">Documents</a>
            </li>
            <li role="presentation" class="" id="show-activity"><a href="#tab_content4"
                                                                  role="tab" id="activity-tab" data-toggle="tab" aria-expanded="false">Tracking Activities</a>
            </li>
            <li role="presentation" class="" id="show-logs"><a href="#tab_content5"
                                                                   role="tab" id="logs-tab" data-toggle="tab" aria-expanded="false">Audit Logs</a>
            </li>
        </ul>
        </div>
        <div id="myTabContent" class="tab-content">
            <li>
                <input type="button" class="btn btn-primary float-right"
                       value="" id="btn-clm-status" style="display: none;">
                <input type="button" class="btn btn-primary float-right"
                       value="Reports" id="btn-clm-reports" data-toggle="modal" data-target="#reportsModal">

            </li>

            <div role="tabpanel" class="tab-pane active"
                 id="tab_content1" aria-labelledby="home-tab">
              <div class="col-md-12">
                  <div class="row">
                <div class="col-md-6 col-sm-6 col-xs-12">
                    <div class="x_panel">
                        <div class="x_title">
                            <h2>Claim Information</h2>
                            <input type="hidden" id="binder-code">
                            <input type="hidden" id="clm-status1">
                        </div>
                        <div class="x_content">
                            <div class="col-md-12 col-xs-12">
                                <div class="form-group row">
                                <label for="claim-no" class="col-md-5 col-form-label">
                                    Claim No</label>
                                <div class="col-md-7 col-xs-12">
                                    <p class="form-control-static" id="claim-no"> </p>
                                </div>
                                </div>
                            </div>
                            <div class="col-md-12 col-xs-12">
                                <div class="form-group row">
                                <label for="client-name" class="col-md-5 col-form-label">
                                    Client</label>
                                <div class="col-md-7 col-xs-12">
                                    <p class="form-control-static" id="client-name"> </p>
                                </div>
                                </div>
                            </div>
                            <div class="col-md-12 col-xs-12">
                                <div class="form-group row">
                                <label for="loss-desc" class="col-md-5 col-form-label">
                                    Loss Description</label>
                                <div class="col-md-7 col-xs-12">
                                    <p class="form-control-static" id="loss-desc"> </p>
                                </div>
                                </div>
                            </div>
                            <div class="col-md-12 col-xs-12">
                                <div class="form-group row">
                                <label for="causation-desc" class="col-md-5 col-form-label">
                                    Claim Causation</label>
                                <div class="col-md-7 col-xs-12">
                                    <p class="form-control-static" id="causation-desc"> </p>
                                </div>
                                </div>
                            </div>
                            <div class="col-md-12 col-xs-12">
                                <div class="form-group row">
                                <label for="loss-date" class="col-md-5 col-form-label">
                                    Loss Date</label>
                                <div class="col-md-7 col-xs-12">
                                    <p class="form-control-static" id="loss-date"> </p>
                                </div>
                                </div>
                            </div>
                            <div class="col-md-12 col-xs-12">
                                <div class="form-group row">
                                    <label for="risk-identifier" class="col-md-5 col-form-label">
                                        Risk Identifier</label>
                                    <div class="col-md-7 col-xs-12">
                                        <p class="form-control-static" id="risk-identifier"> </p>
                                    </div>
                                </div>
                            </div>
                            <div class="col-md-12 col-xs-12">
                                <div class="form-group row">
                                <label for="notific-date" class="col-md-5 col-form-label">
                                    Notification Date</label>
                                <div class="col-md-7 col-xs-12">
                                    <p class="form-control-static" id="notific-date"> </p>
                                </div>
                                </div>
                            </div>
                            <div class="col-md-12 col-xs-12">
                                <div class="form-group row">
                                <label for="bkd-date" class="col-md-5 col-form-label">
                                    Booked Date</label>
                                <div class="col-md-7 col-xs-12">
                                    <p class="form-control-static" id="bkd-date"> </p>
                                </div>
                                </div>
                            </div>
                            <div class="col-md-12 col-xs-12">
                                <div class="form-group row">
                                <label for="next-rev-date" class="col-md-5 col-form-label">
                                    Next Review Date</label>
                                <div class="col-md-7 col-xs-12">
                                    <p class="form-control-static" id="next-rev-date"> </p>
                                </div>
                                </div>
                            </div>
                            <div class="col-md-12 col-xs-12">
                                <div class="form-group row">
                                <label for="liab-admission" class="col-md-5 col-form-label">
                                    Liability Admission</label>
                                <div class="col-md-7 col-xs-12">
                                    <p class="form-control-static" id="liab-admission"> </p>
                                </div>
                                </div>
                            </div>
                            <div id="balance-section" style="display: none;">
                            <div class="col-md-12 col-xs-12">
                                <div class="form-group row">
                                    <label for="client-balance" class="col-md-5 col-form-label">
                                       Client Balance</label>
                                    <div class="col-md-7 col-xs-12">
                                        <p class="form-control-static" id="client-balance"> </p>
                                    </div>
                                </div>
                            </div>
<%--                            <div class="col-md-12 col-xs-12">--%>
<%--                                <div class="form-group row">--%>
<%--                                    <label for="insurer-balance" class="col-md-5 col-form-label">--%>
<%--                                        Insurer Balance</label>--%>
<%--                                    <div class="col-md-7 col-xs-12">--%>
<%--                                        <p class="form-control-static" id="insurer-balance"> </p>--%>
<%--                                    </div>--%>
<%--                                </div>--%>
<%--                            </div>--%>
                            <div class="col-md-12 col-xs-12">
                                <div class="form-group row">
                                    <label for="balance-approver" class="col-md-5 col-form-label">
                                        Balance Approved By</label>
                                    <div class="col-md-7 col-xs-12">
                                        <p class="form-control-static" id="balance-approver"> </p>
                                    </div>
                                </div>
                            </div>
                            </div>

                        </div>
                    </div>
                </div>
                <div class="col-md-6 col-sm-6 col-xs-12">
                    <div class="x_panel">
                        <div class="x_title">
                            <h2>Policy Information</h2>
                            <div class="clearfix"></div>
                        </div>
                        <div class="x_content">
                            <div class="col-md-12 col-xs-12">
                                <div class="form-group row">
                            <label for="pol-no" class="col-md-5 col-form-label">
                                Policy Number</label>
                            <div class="col-md-7 col-xs-12">
                                <p class="form-control-static" id="pol-no"> </p>
                            </div>
                                </div>
                                <div class="form-group row">
                                    <label for="insurer" class="col-md-5 col-form-label">
                                        Insurer</label>
                                    <div class="col-md-7 col-xs-12">
                                        <p class="form-control-static" id="insurer"> </p>
                                    </div>
                                </div>
                                <div class="form-group row">
                                    <label for="insurer-ref-no" class="col-md-5 col-form-label">
                                        Insurer Ref No.</label>
                                    <div class="col-md-7 col-xs-12">
                                        <p class="form-control-static" id="insurer-ref-no"> </p>
                                    </div>
                                </div>
                            </div>





                            <div class="col-md-12 col-xs-12">
                                <div class="form-group row">
                                    <label for="insured" class="col-md-5 col-form-label">
                                        Insured</label>
                                    <div class="col-md-7 col-xs-12">
                                        <p class="form-control-static" id="insured"> </p>
                                    </div>
                                </div>
                            </div>
                            <div class="col-md-12 col-xs-12">
                                <div class="form-group row">
                                <label for="product" class="col-md-5 col-form-label">
                                    Product</label>
                                <div class="col-md-7 col-xs-12">
                                    <p class="form-control-static" id="product"> </p>
                                </div>
                                </div>
                            </div>
                            <div class="col-md-12 col-xs-12">
                                <div class="form-group row">
                                <label for="risk-id" class="col-md-5 col-form-label">
                                    Insured Property</label>
                                <div class="col-md-7 col-xs-12">
                                    <p class="form-control-static" id="risk-id"> </p>
                                    <input type="hidden" id="curr-risk-id">

                                </div>
                                </div>
                            </div>
                            <div class="col-md-12 col-xs-12">
                                <div class="form-group row">
                                <label for="risk-value" class="col-md-5 col-form-label">
                                    Value</label>
                                <div class="col-md-7 col-xs-12">
                                    <p class="form-control-static" id="risk-value"> </p>
                                </div>
                                </div>
                            </div>
                            <div class="col-md-12 col-xs-12">
                                <div class="form-group row">
                                <label for="risk-wef" class="col-md-5 col-form-label">
                                   Period From</label>
                                <div class="col-md-7 col-xs-12">
                                    <p class="form-control-static" id="risk-wef"> </p>
                                </div>
                                </div>
                            </div>
                            <div class="col-md-12 col-xs-12">
                                <div class="form-group row">
                                <label for="risk-wet" class="col-md-5 col-form-label">
                                    Period To</label>
                                <div class="col-md-7 col-xs-12">
                                    <p class="form-control-static" id="risk-wet"> </p>
                                </div>
                                </div>
                            </div>
                        </div>
                    </div>
                </div>
                  </div>
              </div>
            </div>
            <div role="tabpanel" class="tab-pane fade"
                id="tab_content2" aria-labelledby="profile-tab">
                <div class="x_panel">
                    <div class="x_title">
                        <h4>Claimant Details</h4>
                    </div>
                    <button type="button" class="btn btn-info"
                            id="add-peril-btn">New</button>
                    <div class="table-responsive">
                    <table id="clm_claimant_tbl" class="table table-striped" style="width: 100%">
                        <thead>
                        <tr class="headings">
                            <th>Name</th>
                            <th>Type</th>
                            <th>Peril Description</th>
                            <th>Claim Estimate</th>
                            <th>Created Date</th>
                            <th>Created By</th>
                            <th width="5%"></th>
                        </tr>
                        </thead>
                    </table>
                        </div>
                </div>
                <div class="x_panel">
                    <div class="x_title">
                        <h4>Payments Details</h4>
                    </div>
                    <div class="table-responsive">
                        <table id="clm_perils_pymnt_tbl" class="table table-striped" style="width: 100%">
                            <thead>
                                <tr class="headings">
                                    <th>Payee</th>
                                    <th>Reference</th>
                                    <th>Pay Method</th>
                                    <th>Trans Type</th>
                                    <th>Currency</th>
                                    <th>Amount</th>
                                    <th>Status</th>
                                    <th>Created By</th>
                                    <th>Created Date</th>
                                    <th>Auth Date</th>
                                    <th>Auth By</th>
                                </tr>
                            </thead>
                        </table>
                    </div>
                </div>
            </div>
            <div role="tabpanel" class="tab-pane fade"
                       id="tab_content3" aria-labelledby="comm-rates-tab">

            <div class="x_panel">
                <div class="x_title">
                    <h4>Required Documents</h4>
                </div>
                <div class="table-responsive"  aria-labelledby="profile-tab">
                    <button class="btn btn-info" id="btn-add-clmdocs">New</button>
                    <table id="clm_required_docs_tbl" class="table table-striped" style="width: 100%">
                        <thead>
                        <tr class="headings">
                            <th>Document Desc</th>
<%--                            <th>Ref</th>--%>
                            <th>File Name</th>
                            <th>Uploaded By</th>
                            <th>Uploaded Date</th>
                            <th>Remarks</th>
                            <th ></th>
                            <th></th>
                            <th></th>
<%--                            <th></th>--%>
                        </tr>
                        </thead>
                    </table>
                </div>
            </div>
<%--            <div class="x_panel">--%>
<%--                <div class="x_title">--%>
<%--                    <h4>Other Claim Documents</h4>--%>
<%--                </div>--%>
<%--                <button type="button" class="btn btn-info" id="btn-add-upload">Upload Document</button>--%>
<%--                <div class="table-responsive">--%>
<%--                    <table id="clm_req_docs_tbl" class="table table-striped" style="width: 100%">--%>
<%--                        <thead>--%>
<%--                        <tr class="headings">--%>
<%--                            <th>File ID</th>--%>
<%--                            <th>File Name</th>--%>
<%--                            <th>Uploaded By</th>--%>
<%--                            <th>Uploaded Date</th>--%>
<%--                            <th>Comment</th>--%>
<%--                            <th width="5%"></th>--%>
<%--                            <th width="5%"></th>--%>
<%--                        </tr>--%>
<%--                        </thead>--%>
<%--                    </table>--%>
<%--                </div>--%>
<%--            </div>--%>
        </div>
            <div role="tabpanel" class="tab-pane fade"
                 id="tab_content4" aria-labelledby="activity-tab">

                                <div class="x_panel">
                                    <div class="x_title">
                                        <h4>Claim Progress</h4>
                                    </div>
                                        <button type="button" class="btn btn-info"
                                                id="btn-add-activity">New</button>
                                        <table id="clm_activities_tbl" class="table table-striped" style="width: 100%">
                                            <thead>
                                            <tr class="headings">
                                                <th>Activity</th>
                                                <th>Action By</th>
                                                <th>Date</th>
                                                <th>Current Activity</th>
                                                <th>Activity Notes</th>
                                                <th>Reminder Date</th>
                                            </tr>
                                            </thead>
                                        </table>
                                </div>

            </div>
            <div role="tabpanel" class="tab-pane fade"
                 id="tab_content5" aria-labelledby="logs-tab">

                <div class="x_panel">
                    <div class="x_title">
                        <h4>Audit Logs</h4>
                    </div>

                    <table id="clm_logs_tbl" class="table table-striped" style="width: 100%">
                        <thead>
                        <tr class="headings">
                            <th>Activity</th>
                            <th>Action By</th>
                            <th>Date</th>
                            <th>Current Activity</th>
                            <th>Rejected Reason</th>
                            <th>Re-submission Comments</th>
                            <th></th>
                        </tr>
                        </thead>
                    </table>
                </div>
            </div>

            </div>
    </div>
    </div>
<%--        <hr>--%>
<%--        <div class="col-md-12 col-sm-12 col-xs-12">--%>
<%--            <div class="col-md-6 col-xs-12">--%>
<%--                <label for="reserve-total" class="label-align col-md-7">--%>
<%--                    Total Pending Payments</label>--%>
<%--                <div class="col-md-5 col-xs-5">--%>
<%--                    <p class="form-control-static" id="reserve-total"> </p>--%>
<%--                </div>--%>
<%--            </div>--%>
<%--            <div class="col-md-6 col-xs-12">--%>
<%--                <label for="payments-total" class="label-align col-md-7">--%>
<%--                    Total Payments</label>--%>
<%--                <div class="col-md-5 col-xs-5">--%>
<%--                    <p class="form-control-static" id="payments-total"> </p>--%>
<%--                </div>--%>
<%--            </div>--%>
<%--        </div>--%>
    </div>


<div class="modal fade" id="perilTransModal" tabindex="-1" role="dialog"
     aria-labelledby="perilTransModalLabel" aria-hidden="true">
    <div class="modal-dialog">
        <div class="modal-content">
            <div class="modal-header">
                <button type="button" class="close" data-dismiss="modal"
                        aria-label="Close">
                    <span aria-hidden="true">&times;</span>
                </button>
                <h4 class="modal-title" id="perilTransModalLabel">
                    Add A new Peril
                </h4>
            </div>
            <div class="modal-body">
                <form id="new-peril-form" class="form-horizontal">
                    <div class="item form-group">
                        <input type="hidden" name="riskId"  id="myRiskId"/>
                        <label for="brn-id" class="col-md-4 label-align">Self as
                            Claimant</label>

                        <div class="col-md-8 checkbox">
                            <label>
                                <input type="hidden" name="selfAsClaimant" id="self-as-clmnt2"/>
                                <input type="checkbox" id="self-as-clmnt"/>
                            </label>
                        </div>
                    </div>
                    <div class="item form-group">
                        <label for="brn-id" class="col-md-4 label-align">Claimant</label>
                        <div class="col-md-8 d-flex align-items-center">
                            <input type="hidden" name="claimantCode" id="clmt-code"/>
                            <input type="hidden" id="clmt-name">
                            <div id="clmant-def" class="form-control flex-grow-1 mr-2"
                                 select2-url="<c:url value='/protected/claims/selClaimants'/>">
                            </div>
                            <input type="button" class="btn btn-sm btn-success" id="btn-add-claimant" value="New">
                        </div>

                    </div>
                    <div class="item form-group">
                        <label for="brn-id" class="col-md-4 label-align">Peril</label>

                        <div class="col-md-8">
                            <input type="hidden" name="perilCode"  id="peril-code"/>
                            <input type="hidden" id="peril-name"/>
                            <div id="peril-def" class="form-control"
                                 select2-url="<c:url value="/protected/claims/selSubclassPerils"/>" >

                            </div>
                        </div>
                    </div>
                    <div class="item form-group">
                        <label for="brn-id" class="col-md-4 label-align">Claim Estimate</label>

                        <div class="col-md-8">
                            <input type='text' class="form-control float-right"
                                 name="perilEstimate"  id="clm-estimate" />
                        </div>
                    </div>
                    <input type="hidden" name="expireSectionId" id="expire-section-id"/>
                    <input type="hidden" name="expireSection" id="expire-section"/>
                </form>
            </div>
            <div class="modal-footer">
                <button data-loading-text="Saving..." id="btn-add-peril-selected"
                        type="button" class="btn btn-primary">
                    Save
                </button>
                <button type="button" class="btn btn-default" data-dismiss="modal">
                    Cancel
                </button>
            </div>
        </div>
    </div>
</div>

<div class="modal fade" id="claimantsModal" tabindex="-1" role="dialog"
     aria-labelledby="claimantsModalLabel" aria-hidden="true">
    <div class="modal-dialog">
        <div class="modal-content">
            <div class="modal-header">
                <button type="button" class="close" data-dismiss="modal"
                        aria-label="Close">
                    <span aria-hidden="true">&times;</span>
                </button>
                <h4 class="modal-title" id="claimantsModalLabel">
                    Edit/Add Claimant
                </h4>
            </div>
            <div class="modal-body" id="branch_model">
                <form id="claimants-form" class="form-horizontal">
                    <input type="hidden" class="form-control" id="clmnt-id" name="claimantId">
                    <div class="item form-group">
                        <label for="brn-id" class="col-md-3 label-align">Surname</label>

                        <div class="col-md-8">
                            <input type="text" class="form-control" id="clmnt-surname"
                                   name="surname"  required>
                        </div>
                    </div>
                    <div class="item form-group">
                        <label for="unit-id" class="col-md-3 label-align">Other Names</label>

                        <div class="col-md-8">
                            <input type="text" class="form-control" id="clmnt-othernames"
                                   name="otherNames"  required>
                        </div>
                    </div>
                    <div class="item form-group">
                        <label for="unit-id" class="col-md-3 label-align">Occupation</label>

                        <div class="col-md-8">
                            <input type="hidden" id="clmt-occupt" name="occupation"/>
                            <input type="hidden" id="clmt-occupt-names"/>
                            <div id="occup-def" class="form-control"
                                 select2-url="<c:url value="/protected/claims/selOccupations"/>" >

                            </div>
                        </div>
                    </div>
                    <div class="item form-group">
                        <label for="unit-id" class="col-md-3 label-align">ID Number</label>
                        <div class="col-md-8">
                            <input type="text" class="form-control" id="clmnt-idnumber"
                                   name="idNumber" required>
                        </div>
                    </div>

                    <div class="item form-group">
                        <label for="unit-id" class="col-md-3 label-align">Email Address</label>
                        <div class="col-md-8">
                            <input type="email" class="form-control" id="clmnt-email" name="email" required>
                        </div>
                    </div>

                    <div class="item form-group">
                        <label for="unit-id" class="col-md-3 label-align">Address</label>

                        <div class="col-md-8">
                            <textarea class="form-control" rows="2" id="clmnt-address" name="address"></textarea>
                        </div>
                    </div>
                    <div class="item form-group">
                        <label for="unit-id" class="col-md-3 label-align">Mobile Number</label>

                        <div class="col-md-8">
                            <input type="text" class="form-control" id="clmnt-mobnumber"
                                   name="mobileNo"  required>
                        </div>
                    </div>

                </form>
            </div>
            <div class="modal-footer">
                <button data-loading-text="Saving..." id="saveClaimantDef"
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

<div class="modal fade" id="uploadClmModal" tabindex="-1" role="dialog"
     aria-labelledby="uploadClmModalLabel" aria-hidden="true">
    <div class="modal-dialog">
        <div class="modal-content">
            <form id="clm-doc-form" class="form-horizontal">
            <div class="modal-header">
                <h4 class="modal-title" id="uploadClmModalLabel">
                    Upload Claim Document
                </h4>
                <button type="button" class="close" data-dismiss="modal"
                        aria-label="Close">
                    <span aria-hidden="true">&times;</span>
                </button>

            </div>
            <div class="modal-body">


                    <input type="hidden" class="form-control" id="upload-code" name="uploadId">
                    <input type="hidden" class="form-control" id="upload-clm-id" name="claimBookings">
                    <div class="item form-group">
                        <label for="brn-id" class="col-md-3 label-align">File ID</label>

                        <div class="col-md-8">
                            <input type="text" class="form-control" id="upload-sht-id"
                                   name="fileId">
                        </div>
                    </div>
                    <div class="item form-group">
                        <label for="file" class="col-md-3 label-align">File</label>

                        <div class="col-md-8">
                            <label class="custom-file">
                                <input type="file" id="file" class="custom-file-input" name="file">
                                <span class="custom-file-control"></span>
                            </label>
                        </div>
                    </div>
                    <div class="item form-group">
                        <label for="rate-taxable" class="col-md-3 label-align">Comment</label>
                        <div class="col-md-8">
                            <label>
                                <textarea class="form-control" rows="3" id="comment-desc" name="uploadedComment"></textarea>
                            </label>
                        </div>
                    </div>
            </div>
            <div class="modal-footer">
                <input type="submit" class="btn btn-success" value="Save">
                <button type="button" class="btn btn-default" data-dismiss="modal">
                    Cancel
                </button>
            </div>
            </form>
        </div>
    </div>
</div>


<div class="modal fade" id="clmReqDocsModal" tabindex="-1" role="dialog"
     aria-labelledby="clmReqDocsModalLabel" aria-hidden="true">
    <div class="modal-dialog">
        <div class="modal-content">
            <div class="modal-header">
                <h4 class="modal-title" id="clmReqDocsModalLabel">Select Required Docs</h4>
                <button type="button" class="close" data-dismiss="modal"  aria-label="Close">
                    <span aria-hidden="true">&times;</span>
                </button>

            </div>
            <div class="modal-body">
                <form class="form-horizontal">
                    <div class="item form-group">
                        <label for="doc-name-search" class="col-md-3 label-align">Document Name</label>
                        <div class="col-md-6">
                            <input type="text" class="form-control" id="doc-name-search" name="req-doc">
                        </div>
                        <div class="col-md-1">
                            <button  id="searchDocuments" type="button" class="btn btn-primary"> Search </button>
                        </div>
                    </div>
                </form>
                <div style="height: 300px !important; overflow: scroll;">
                    <table class="table table-striped table-hover table-bordered table-fixed" id="risksReqDocsTbl">
                        <thead>
                        <tr>
                            <th width="1%"></th>
                            <th width="4%">Document Id</th>
                            <th width="12%">Document Name</th>
                        </tr>
                        </thead>
                        <tbody> </tbody>
                    </table>
                </div>
                <form id="req-clm-docs-form">
                    <input type="hidden" id="req-risk-code" name="subCode"/>
                </form>
            </div>
            <div class="modal-footer">
                <button data-loading-text="Saving..." id="saveReqClmDocsBtn"
                        type="button" class="btn btn-success">Save</button>
                <button type="button" class="btn btn-default" data-dismiss="modal">Cancel</button>
            </div>
        </div>
    </div>
</div>

<div class="modal fade" id="uploadClmReqModal" tabindex="-1" role="dialog"
     aria-labelledby="uploadClmReqModalLabel" aria-hidden="true">
    <div class="modal-dialog">
        <div class="modal-content">
            <form id="clm-req-form" class="form-horizontal">
                <div class="modal-header">
                    <h4 class="modal-title" id="uploadClmReqModalLabel">
                        Upload Claim Document
                    </h4>
                    <button type="button" class="close" data-dismiss="modal"
                            aria-label="Close">
                        <span aria-hidden="true">&times;</span>
                    </button>

                </div>
                <div class="modal-body">


                    <input type="hidden" class="form-control" id="uploadreq-code" name="clmRequiredId">
                    <input type="hidden" class="form-control" id="trans-type" name="transType">
                    <div class="item form-group">
                        <label for="brn-id" class="col-md-3 label-align">Doc Ref No</label>

                        <div class="col-md-8">
                            <input type="text" class="form-control" id="doc-ref-no"
                                   name="docRefNo">
                        </div>
                    </div>
                    <div class="item form-group">
                        <label for="rate-taxable" class="col-md-3 label-align">Remarks</label>
                        <div class="col-md-8">
                            <label>
                                <textarea class="form-control" rows="3" id="req-remarks" name="remarks"></textarea>
                            </label>
                        </div>
                    </div>
                    <div class="item form-group uploadfile">
                        <label for="req-doc-file" class="col-md-3 label-align">File</label>

                        <div class="col-md-8">
                            <label class="input-group col-xs-12">
                                <input type="file" id="req-doc-file" name="file">
                                <span class="custom-file-control"></span>
                            </label>
                        </div>
                    </div>








                </div>
                <div class="modal-footer">
                   <%-- <input type="submit" class="btn btn-success" value="Save"> --%>
                   <input type="submit" class="btn btn-success" value="Upload" id="upload-claimDoc-btn">
                    <button type="button" class="btn btn-default" data-dismiss="modal">
                        Cancel
                    </button>
                </div>
                <div id="upload-spinner" class="text-center mt-3" style="display: none;">
                    <!-- Spinner only (no text inside) -->
                    <div class="spinner-border text-primary" role="status" style="width: 3rem; height: 3rem;">

                    </div>
                    <!-- Status text placed separately to avoid rotation -->
                    <div class="mt-2" style="margin-bottom: 30px;">Uploading document, please wait...
                    </div>
                </div>
            </form>
        </div>
    </div>
</div>




<div class="modal fade" id="activityModal" tabindex="-1" role="dialog"
     aria-labelledby="activityModalLabel" aria-hidden="true">
    <div class="modal-dialog">
        <div class="modal-content">
            <div class="modal-header">
                <h4 class="modal-title" id="activityModalLabel">
                    Add new Activity
                </h4>
                <button type="button" class="close" data-dismiss="modal"
                        aria-label="Close">
                    <span aria-hidden="true">&times;</span>
                </button>

            </div>
            <div class="modal-body">
                <form id="clm-act-form" class="form-horizontal">
                    <div class="item form-group">
                        <label for="clm-activity" class="label-align col-md-5">
                            Select Activity<span class="required">*</span></label>
                        <div class="col-md-7 col-xs-12">
                            <input type="hidden"  id="activity-code" name="activity" />
                            <input type="hidden" id="activity-desc" />
                            <div id="clm-activity" class="form-control"
                                 select2-url="<c:url value="/protected/claims/selclmActivity"/>" >

                            </div>
                        </div>
                    </div>
                    <div class="item form-group">
                        <label for="activity-notes" class="label-align col-md-5">
                            Activity Note<span class="required">*</span></label>
                        <div class="col-md-7 col-xs-12">
                                <input type="text"  class="form-control float-right text-area"
                                       id="activity-notes" required name="activityNotes" />
                        </div>
                    </div>
                    <div class="item form-group">
                        <label for="net-review-date" class="label-align col-md-5">
                            Reminder Date<span class="required">*</span></label>
                        <div class="col-md-7 col-xs-12">
                            <div class='input-group date datepicker-input' id="act-reminder-date">
                                <input type="text"  class="form-control float-right"
                                       id="net-review-date" required name="remDate" />
                                <div class="input-group-addon">
                                    <span class="fa fa-calendar"></span>
                                </div>
                            </div>
                        </div>
                    </div>
                    <div class="item form-group" id="claim-ref-section">
                        <label for="insurer-ref" class="label-align col-md-5">
                            Insurer Reference No<span class="required">*</span></label>
                        <div class="col-md-7 col-xs-12">
                            <input type="text" class="form-control float-right"
                                   id="insurer-ref" name="insurerRef" required/>
                        </div>
                    </div>
                </form>
            </div>
            <div class="modal-footer">
                <button data-loading-text="Saving..." id="saveClmActivity"
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


<div class="modal fade" id="paymentModal" tabindex="-1" role="dialog"
     aria-labelledby="paymentModalLabel" aria-hidden="true">
    <div class="modal-dialog">
        <div class="modal-content">
            <div class="modal-header">
                <h4 class="modal-title" id="paymentModalLabel">
                    Add/Edit Payment
                </h4>
                <button type="button" class="close" data-dismiss="modal"
                        aria-label="Close">
                    <span aria-hidden="true">&times;</span>
                </button>

            </div>
            <div class="modal-body">
                <form id="clm-pay-form" class="form-horizontal">
                    <input type="hidden" class="form-control" id="peril-pk" name="claimPerils">
                    <input type="hidden" class="form-control" id="payment-id" name="clmPymntId">
                    <div class="item form-group">
                        <label for="brn-id" class="col-md-4 label-align">Payee<span class="required">*</span></label>
                        <div class="col-md-7 col-xs-12">
                            <input type='text' class="form-control float-right"
                                   name="payee"  id="payee-name" required/>
                        </div>
                    </div>
                    <div class="item form-group">
                        <label for="brn-id" class="col-md-4 label-align">Payment Reference<span class="required">*</span></label>
                        <div class="col-md-7 col-xs-12">
                            <input type='text' class="form-control float-right"
                                   name="pymntRef"  id="pay-ref" required/>
                        </div>
                    </div>
                    <div class="item form-group">
                        <label for="payment-type" class="col-md-4 label-align">Payment Type</label>

                        <div class="col-md-7 col-xs-12">
                            <select class="form-control" id="payment-type" name="pymntType" required>
                                <option value="">Select Payment Type</option>
                                <option value="N">Normal</option>
                                <option value="X">Exgratia</option>
                            </select>
                        </div>
                    </div>
                    <div class="item form-group">
                        <label for="pay-date" class="label-align col-md-4">
                            Date Paid<span class="required">*</span></label>
                        <div class="col-md-7 col-xs-12">
                            <div class='input-group date datepicker-input' id="payment-date">
                                <input type="text"  class="form-control float-right"
                                       id="pay-date" required  name="pymntDate" />
                                <div class="input-group-addon">
                                    <span class="fa fa-calendar"></span>
                                </div>
                            </div>
                        </div>
                    </div>
                    <div class="item form-group">
                        <label for="brn-id" class="col-md-4 label-align">Paid Amount<span class="required">*</span></label>
                        <div class="col-md-7 col-xs-12">
                            <input type='text' class="form-control float-right"
                                   name="clmPymntAmount"  id="paid-amount" required/>
                        </div>
                    </div>
                </form>
            </div>
            <div class="modal-footer">
                <button data-loading-text="Saving..." id="saveClmPayment"
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


<div class="modal fade" id="claimPerilModal" tabindex="-1" role="dialog"
     aria-labelledby="claimPerilModalLabel" aria-hidden="true">
    <div class="modal-dialog">
        <div class="modal-content">
            <div class="modal-header">
                <h4 class="modal-title" id="claimPerilModalLabel">
                    Edit Peril
                </h4>
                <button type="button" class="close" data-dismiss="modal"
                        aria-label="Close">
                    <span aria-hidden="true">&times;</span>
                </button>

            </div>
            <div class="modal-body">
                <form id="clm-peril-form" class="form-horizontal">
                    <input type="hidden" class="form-control" id="peril-id" name="clmPerilId">
                    <div class="item form-group">
                        <label for="brn-id" class="col-md-4 label-align">Peril</label>
                        <div class="col-md-7 col-xs-12">
                            <input type='text' class="form-control float-right"
                                   id="peril-desc" readonly/>
                        </div>
                    </div>

                    <div class="item form-group">
                        <label for="brn-id" class="col-md-4 label-align">Claim Estimate <span class="required">*</span></label>
                        <div class="col-md-7 col-xs-12">
                            <input type='text' class="form-control float-right"
                                   name="reserve"  id="reserve-amount" required/>
                        </div>
                    </div>
                    <div class="item form-group">
                        <label for="brn-id" class="col-md-4 label-align">Remarks</label>
                        <div class="col-md-7 col-xs-12">
                            <input type='text' class="form-control float-right"
                                   name="remarks"  id="remarks-desc"/>
                        </div>
                    </div>
                </form>
            </div>
            <div class="modal-footer">
                <button data-loading-text="Saving..." id="saveClmPeril"
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
                            <li class="clm-synopsis-dip"><a
                                    href="<c:url value='/protected/claims/rpt_claims_synopsis'/> "
                                    target="_blank">Claim Synopsis</a></li>
                            <li>
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

<div class="modal fade" id="claimStatusModal" tabindex="-1" role="dialog" aria-labelledby="claimStatusModalLabel" aria-hidden="true">
    <div class="modal-dialog" role="document">
        <div class="modal-content">
            <div class="modal-header">
                <h3 class="modal-title" id="claimStatusModalLabel">Close/Re-open Claim</h3>
                <button type="button" class="close" data-dismiss="modal" aria-label="Close">
                    <span aria-hidden="true">×</span>
                </button>
            </div>
            <div class="modal-body">
                <form id="clm-status-form">
                    <input type="hidden" class="form-control" id="new-status" name="newStatus">

                    <!-- Closure Type Dropdown -->
                    <div id="closedStatus" class="item form-group">
                        <label for="clsStatus" class="col-form-label">Closed As: <span class="required">*</span></label>
                        <select class="form-control" id="clsStatus" name="closeReason" required>
                            <option value="">Select Closure Type</option>
                            <option class="dropdown-item" value="ST">Closed As Settled</option>
                            <option class="dropdown-item" value="RJ">Closed As Rejected</option>
                            <option class="dropdown-item" value="NC">Closed As No Claim</option>
                            <option class="dropdown-item" value="RC">Repairs completed/Release letter issued</option>
                            <option class="dropdown-item" value="DV">CIL DV Issued</option>
                            <option class="dropdown-item" value="CD">Claim repudiated/Declined</option>
                        </select>
                    </div>

                    <!-- Activity Date -->
                    <div class="item form-group" id="activity-date-picker">
                        <label for="activity-date" class="col-form-label">Activity Date:</label>
                        <div class='input-group date datepicker-input' id="activity-date-picker">
                            <input type="text" class="form-control" id="activity-date" name="activityDate"/>
                            <div class="input-group-addon">
                                <span class="fa fa-calendar"></span>
                            </div>
                        </div>
                    </div>

                    <div class="item form-group" id="clm-settlement-frm">
                        <label for="settlementType" class="col-form-label col-md-5 label-align">Type of Settlement: <span class="required">*</span></label>
                        <div class="col-md-7 col-xs-12">
                            <textarea class="form-control" rows="5" cols="20" id="settlementType" name="settlementType" required=></textarea>
                        </div>
                    </div>

                    <!-- DV Fields Section -->
                    <div id="dv-fields-section" style="display: none;">
                        <!-- DV Issuance Date -->
                        <div class="item form-group">
                            <label for="dv-issuance-date" class="col-form-label col-md-5 label-align">DV Issuance Date: <span class="required">*</span></label>
                            <div class="col-md-7 col-xs-12">
                                <div class='input-group date datepicker-input' id="dv-issuance-date-picker">
                                    <input type="text" class="form-control" id="dv-issuance-date" name="dvIssuanceDate" required/>
                                    <div class="input-group-addon">
                                        <span class="fa fa-calendar"></span>
                                    </div>
                                </div>
                            </div>
                        </div>

                        <!-- Offer Amount -->
                        <div class="item form-group">
                            <label for="dv-offer-amount" class="col-form-label col-md-5 label-align">Offer/Settlement Amount: <span class="required">*</span></label>
                            <div class="col-md-7 col-xs-12">
                                <input type="text" class="form-control currency-input" id="dv-offer-amount" name="dvOfferAmount" placeholder="0.00" required/>
                            </div>
                        </div>

                        <!-- Client Decision -->
                        <div class="item form-group">
                            <label for="client-decision" class="col-form-label col-md-5 label-align">Client Decision: <span class="required">*</span></label>
                            <div class="col-md-7 col-xs-12">
                                <select class="form-control" id="client-decision" name="clientDecision" required>
                                    <option value="">Select Decision</option>
                                    <option value="ACCEPTED">Accepted</option>
                                    <option value="DECLINED">Declined</option>
                                </select>
                            </div>
                        </div>

                        <!-- Decline Reason -->
                        <div class="item form-group" id="decline-reason-section" style="display: none;">
                            <label for="decline-reason" class="col-form-label col-md-5 label-align">Decline Reason: <span class="required">*</span></label>
                            <div class="col-md-7 col-xs-12">
                                <textarea class="form-control" rows="3" id="decline-reason" name="declineReason"></textarea>
                            </div>
                        </div>

                        <!-- DV Execution Date -->
                        <div class="item form-group" id="dv-acceptance-date">
                            <label for="dv-acceptance-date" class="col-form-label col-md-5 label-align">DV Execution: <span class="required">*</span></label>
                            <div class="col-md-7 col-xs-12">
                                <div class='input-group date datepicker-input' id="dv-acceptance-date-picker">
                                    <input type="text" class="form-control" id="dv-acceptance-date" name="dvAcceptanceDate" required/>
                                    <div class="input-group-addon">
                                        <span class="fa fa-calendar"></span>
                                    </div>
                                </div>
                            </div>
                        </div>

                        <!-- Decline Date -->
                        <div class="item form-group" id="dv-execution-date">
                            <label for="dv-execution-date" class="col-form-label col-md-5 label-align">Decline Date: <span class="required">*</span></label>
                            <div class="col-md-7 col-xs-12">
                                <div class='input-group date datepicker-input' id="dv-execution-date-picker">
                                    <input type="text" class="form-control" id="dv-execution-date" name="dvExecutionDate" required/>
                                    <div class="input-group-addon">
                                        <span class="fa fa-calendar"></span>
                                    </div>
                                </div>
                            </div>
                        </div>

                        <!-- Payment Fields -->
                        <div class="item form-group form-required" id="edit-payment-mode">
                            <label class="col-md-5 col-form-label label-align" for="pm-mode-frm">Payment Mode: <span class="required">*</span></label>
                            <div class="col-md-7 col-xs-12">
                                <input type="hidden" id="pm-id" name="paymentId"/>
                                <div id="pm-mode-frm" class="form-control" select2-url="<c:url value='/protected/uw/policies/uwpaymentmodes'/>" required></div>
                            </div>
                        </div>

                        <!-- Final Settlement Date -->
                        <div class="item form-group">
                            <label for="final-settlement-date" class="col-form-label col-md-5 label-align">Date of Settlement/Closure: <span class="required">*</span></label>
                            <div class="col-md-7 col-xs-12">
                                <div class='input-group date datepicker-input' id="final-settlement-date-picker">
                                    <input type="text" class="form-control" id="final-settlement-date" name="finalSettlementDate" required/>
                                    <div class="input-group-addon">
                                        <span class="fa fa-calendar"></span>
                                    </div>
                                </div>
                            </div>
                        </div>
                    </div>

                    <!-- Remarks -->
                    <div class="item form-group" id="remarks">
                        <label for="clm-remarks" class="col-form-label">Remarks: <span class="required">*</span></label>
                        <textarea class="form-control" rows="5" cols="20" id="clm-remarks" name="remarks" required=></textarea>
                    </div>
                </form>
            </div>

            <div class="modal-footer">
                <button id="saveDvPartial" type="button" class="btn btn-info" style="display: none;">
                    Save Progress
                </button>
                <button data-loading-text="Saving..." id="saveClmStatus" type="button" class="btn btn-primary">Save</button>
                <button type="button" class="btn btn-secondary" data-dismiss="modal">Cancel</button>
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
                                <option value="C">Client</option>
                                <option value="A">Intermediary</option>
                                <option value="IN">Inhouse</option>
                                <option value="B">Both</option>

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
                            <label class="checkbox-inline clm-synop-dip"><input type="checkbox" value="SY">Claim Synopsis</label>

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


<div class="modal fade" id="claimStatusHistModal" tabindex="-1" role="dialog"
     aria-labelledby="claimStatusHistModalLabel" aria-hidden="true">
    <div class="modal-dialog">
        <div class="modal-content">
            <div class="modal-header">
                <h4 class="modal-title" id="claimStatusHistModalLabel">
                    Claim Status History
                </h4>
                <button type="button" class="close" data-dismiss="modal"
                        aria-label="Close">
                    <span aria-hidden="true">&times;</span>
                </button>

            </div>
            <div class="modal-body table-responsive">
                <table id="clm_status_tbl" class="table table-striped" style="width: 100%">
                    <thead>
                    <tr class="headings">
                        <th>Status</th>
                        <th>Captured By</th>
                        <th>Status Date</th>
                        <th>Current?</th>
                        <th>Remarks</th>
                    </tr>
                    </thead>
                    <tbody>

                    </tbody>
                </table>
            </div>
            <div class="modal-footer">
                <button type="button" class="btn btn-default" data-dismiss="modal">
                    OK
                </button>
            </div>
        </div>
    </div>
</div>
    <div id="approvalModal" class="modal fade" tabindex="-1" role="dialog">
        <div class="modal-dialog" role="document">
            <div class="modal-content">
                <div class="modal-header">
                    <h5 class="modal-title">Approve Task</h5>
                    <button type="button" style="display: none;" class="close" data-dismiss="modal" aria-label="Close">
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
                    <button type="button" style="display: none;" class="close" data-dismiss="modal" aria-label="Close">
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
    <div class="float-right" id="hidden-buttons">
    <sec:authorize access="hasAnyAuthority('AUTHORIZE_POLICY')">
        <input type="button" class="btn btn-primary btn btn-danger float-right"
               value="Reject Task" id="btn-reject-claim">
    </sec:authorize>

    <sec:authorize access="hasAnyAuthority('AUTHORIZE_POLICY')">
        <input type="button" class="btn btn-primary float-right"
               value="Approve Task" id="btn-approve-claim">
    </sec:authorize>
    </div>
</div>
<script>
    $(document).ready(function() {

        // Initially hide payment mode and settlement type fields
        $('#edit-payment-mode').hide();
        $('#clm-settlement-frm').hide();

        // Listen for changes in the "Closed As" dropdown
        $('#clsStatus').on('change', function() {
            var selectedValue = $(this).val();

            if (selectedValue === 'ST') { // Closed As Settled
                // Show payment mode and settlement type fields with animation
                $('#edit-payment-mode').slideDown(300);
                $('#clm-settlement-frm').slideDown(300);

                // Make fields required
                $('#pm-id').attr('required', true);
                $('#settlementType').attr('required', true);
            } else {
                // Hide payment mode and settlement type fields with animation
                $('#edit-payment-mode').slideUp(300);
                $('#clm-settlement-frm').slideUp(300);

                // Remove required attribute and clear values
                $('#pm-id').removeAttr('required').val('');
                $('#settlementType').removeAttr('required').val('');

                // Clear Select2 dropdown if it's initialized
                if ($('#pm-mode-frm').hasClass('select2-hidden-accessible')) {
                    $('#pm-mode-frm').val(null).trigger('change');
                }
            }
        });

        // Trigger on modal open to ensure correct initial state
        $(document).on('shown.bs.modal', '[data-toggle="modal"]', function () {
            $('#clsStatus').trigger('change');
        });
    });
</script>