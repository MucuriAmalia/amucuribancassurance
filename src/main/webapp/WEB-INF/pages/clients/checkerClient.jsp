<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="sec" uri="http://www.springframework.org/security/tags" %>
<script type="text/javascript" src="<c:url value="/js/modules/utils/select2builder.js"/>"></script>
<script type="text/javascript" src="<c:url value="/libs/rivets/rivets.js"/>"></script>

<% Long mckIdNo = (Long) session.getAttribute("mck_id_no"); %>
<script type="text/javascript">
    var mck_id_no = "<%= mckIdNo %>";
</script>

<div class="x_panel">
  <div class="" role="tabpanel" data-example-id="togglable-tabs">
    <ul class="breadcrumb bg-white">
      <li><a href="<c:url value="/protected/clients/setups/clientslist"/>">Client:&nbsp;&nbsp</a></li>
      <li class="active" id="client_det" style="font-weight: bolder"></li>
    </ul>
    <ul id="myTab" class="nav nav-tabs bar_tabs" role="tablist">
      <li role="presentation" class="active"><a href="#tab_content1"
                                                id="home-tab" role="tab" data-toggle="tab"
                                                aria-expanded="true">Client Details</a>
      </li>
      <li role="presentation" id="show-docs"><a href="#tab_content2"
                                                                     role="tab" id="profile-tab" data-toggle="tab"
                                                                     aria-expanded="true">Client Documents</a>
      </li>
      <li role="presentation" id="show-trans"><a href="#tab_content3"
                                                role="tab" id="trans-tab" data-toggle="tab"
                                                aria-expanded="true">Transactions</a>
      </li>
    </ul>

  </div>
    <div id="myTabContent" class="tab-content">
      <div role="tabpanel" class="tab-pane active"
           id="tab_content1" aria-labelledby="home-tab">

        <form id="tenant-form" data-parsley-validate class="form-horizontal form-label-left" enctype="multipart/form-data">

          <div class="x_panel">
            <input type="hidden" name="tenId" id="tenId-pk">
            <div class="item form-group">
              <div class="col-md-6 col-xs-12">
                  <label for="ob-name" class="label-align col-md-5">Branch Registered</label>
                  <div class="col-md-7">
                    <p class="form-control-static" id="ob-name"></p>
                  </div>
                </div>
              <div class="col-md-6 col-xs-12 form-required">
                <label for="clnt-type-name" class="label-align col-md-5">Client Type</label>
                <div class="col-md-5 col-xs-12">
                  <p class="form-control-static" id="clnt-type-name"></p>
                </div>
              </div>
            </div>
            <div class="item form-group form-required">
              <div class="col-md-6 col-xs-12">
                <label for="clnt-title-name" class="label-align col-md-5">Title</label>
                <div class="col-md-6 col-xs-12">
                  <p class="form-control-static" id="clnt-title-name"></p>
                </div>
              </div>
              <div class="col-md-6 col-xs-12">
                <label for="client-ref-no" class="label-align col-md-5">Client Segment</label>
                <div class="col-md-7 col-xs-12">
                  <p class="form-control-static" id="client-ref-no"></p>
                </div>
              </div>
            </div>
            <div class="item form-group form-required">
              <div class="col-md-6 col-xs-12">
                <label for="fname" class="label-align col-md-5">First
                  Name</label>
                <div class="col-md-7 col-xs-12">
                  <p class="form-control-static" id="fname"></p>
                </div>
              </div>
              <div class="col-md-6 col-xs-12">
                <label for="other-names" class="label-align col-md-5">Other
                  Names</label>
                <div class="col-md-7 col-xs-12">
                  <p class="form-control-static" id="other-names"></p>
                </div>
              </div>
            </div>

            <div class="item form-group form-required">
              <div class="col-md-6 col-xs-12">
                <label for="pin-no" class="label-align col-md-5">Pin No</label>
                <div class="col-md-7 col-xs-12">
                  <p class="form-control-static" id="pin-no"></p>
                </div>
              </div>
              <div class="col-md-6 col-xs-12">
                <label for="clnt-authorised" class="label-align col-md-5">Approved?</label>
                <div class="col-md-7 col-xs-12">
                  <p class="form-control-static" id="clnt-authorised" style="color: green"></p>
                </div>
              </div>

            </div>
            <div class="item form-group form-required">
              <div class="col-md-6 col-xs-12">
                <label for="address" class="label-align col-md-5">Address</label>
                <div class="col-md-7 col-xs-12">
                  <p class="form-control-static" id="address"></p>
                </div>

              </div>

              <div class="col-md-6 col-xs-12">
                <label for="clnt-town-name" class="label-align col-md-5">Town</label>
                <div class="col-md-7 col-xs-12">
                  <p class="form-control-static" id="clnt-town-name"></p>
                </div>

              </div>

            </div>
            <div class="item form-group form-required">
              <div class="col-md-6 col-xs-12">
                <label for="postal-name" class="label-align col-md-5">Postal Code</label>
                <div class="col-md-7 col-xs-12">
                  <p class="form-control-static" id="postal-name"></p>
                </div>

              </div>

              <div class="col-md-6 col-xs-12">
                <label for="id-no" class="label-align col-md-5" id="lbl-id-no">ID No</label>
                <div class="col-md-7 col-xs-12">
                  <p class="form-control-static" id="id-no"></p>
                </div>

              </div>

            </div>
            <div class="item form-group">
              <div class="col-md-6 col-xs-12">
                <label for="passport-no" class="label-align col-md-5">Passport No</label>
                <div class="col-md-7 col-xs-12">
                  <p class="form-control-static" id="passport-no"></p>
                </div>

              </div>

              <div class="col-md-6 col-xs-12">
                <label for="gender" class="label-align col-md-5" id="lblGender">Gender</label>
                <div class="col-md-7 col-xs-12 gender">
                  <p class="form-control-static" id="gender"></p>
                </div>
              </div>
            </div>

            <div class="item form-group form-required">
              <div class="col-md-6 col-xs-12">
                <label for="cou-name" class="label-align col-md-5">Domicile Country</label>
                <div class="col-md-7 col-xs-12">
                  <p class="form-control-static" id="cou-name"></p>
                </div>
              </div>
              <div class="col-md-6 col-xs-12">
                <label for="office-tel-no" class="label-align col-md-5">Tel No</label>
                <div class="col-md-7 col-xs-12">
                  <p class="form-control-static" id="office-tel-no"></p>
                </div>
              </div>
            </div>
            <div class="item form-group form-required">
              <div class="col-md-6 col-xs-12">
                <label for="pref-sms-name" class="label-align col-md-5">SMS Number</label>
                <div class="col-md-7 col-xs-12">
                  <p class="form-control-static" id="pref-sms-name"></p>
                </div>
              </div>
              <div class="col-md-6 col-xs-12">
                <label for="pref-phone-name" class="label-align col-md-5">Phone Number</label>
                <div class="col-md-7 col-xs-12">
                  <p class="form-control-static" id="pref-phone-name"></p>
                </div>
              </div>
            </div>
            <div class="item form-group form-required">
                <div class="col-md-6 col-xs-12">
                    <label for="resident-status" class="label-align col-md-5">Resident Status</label>
                    <div class="col-md-7 col-xs-12">
                        <p class="form-control-static" id="resident-status"></p>
                    </div>
                </div>
              <div class="col-md-6 col-xs-12">
                <label for="email-address" class="label-align col-md-5">Email</label>
                <div class="col-md-7 col-xs-12">
                  <p class="form-control-static" id="email-address"></p>
                </div>
              </div>
            </div>
            <div class="item form-group form-required">
              <div class="col-md-6 col-xs-12">
                <label for="sel3" class="label-align col-md-5">Status</label>
                <div class="col-md-7 col-xs-12">
                  <p class="form-control-static" id="sel3"></p>
                </div>
              </div>
              <div class="col-md-6 col-xs-12">
                <label for="dob" class="col-md-5 label-align" id="lbl-dob">Date of Birth</label>

                <div class="col-md-7 col-xs-12">
                  <p class="form-control-static" id="dob"></p>
                </div>
              </div>
            </div>
            <div class="item form-group form-required">

              <div class="col-md-6 col-xs-12">
                <label for="occ-name" class="label-align col-md-5" id="lbl-occ">Occupation</label>
                <div class="col-md-7 col-xs-12">
                  <p class="form-control-static" id="occ-name"></p>
                </div>
              </div>
            <div class="col-md-6 col-xs-12">
              <label for="sect-name" class="col-md-5 label-align" id="lbl-sector">Sector</label>
              <div class="col-md-7 col-xs-12">
                <p class="form-control-static" id="sect-name"></p>
              </div>
            </div>
          </div>
            <div class="item form-group form-required">

              <div class="col-md-6 col-xs-12" id="myComments">
                <label for="comment" class="label-align col-md-5">Blacklisting Comment</label>
                <div class="col-md-7 col-xs-12">
                  <p class="form-control-static" id="comment"></p>
                </div>
              </div>
            </div>
          <h4>Other Information</h4>
          <hr>
          <div class="item form-group">
            <label for="date-reg" class="col-md-3 label-align">Date Registered</label>

            <div class="col-md-3 col-xs-12">
              <p class="form-control-static" id="date-reg"></p>
            </div>
          </div>
          <div class="item form-group">
            <label for="dt-terminated" class="col-md-3 label-align">Date Terminated</label>

            <div class="col-md-3 col-xs-12">
              <p class="form-control-static" id="dt-terminated"></p>
            </div>
          </div>
          <div class='spacer'></div>

          <div class="item form-group">
            <div class="col-md-6 col-xs-12 form-required">
              <label for="file" class="label-align col-md-5" id="client-photo">
                Photo</label>
              <div class="col-md-7 col-xs-12">
                <div class="kv-avatar center-block" style="width: 200px">
                  <img  src="<c:url value='/protected/clients/setups/tenantImage/${tenId}'/> ">

                </div>
              </div>
            </div>
          </div>
      </div>
        </form>
      </div>
      <div role="tabpanel" class="tab-pane fade"
           id="tab_content2" aria-labelledby="profile-tab">
        <button class="btn btn-primary btn btn-info" id="btn-add-docs">New</button>
        <div class="card-box table-responsive">
          <table id="clientDocsList" class="table" style="width:100%">
            <thead>
            <tr>
              <th>Document ID</th>
              <th>Document Desc</th>
              <th>File Name</th>
              <th>File Ref. No.</th>
              <th width="5%"></th>
              <th width="5%"></th>
              <th width="5%"></th>
            </tr>
            </thead>
          </table>
        </div>
      </div>
      <div role="tabpanel" class="tab-pane fade"
           id="tab_content3" aria-labelledby="profile-tab">
          <table id="clientTransList" class="table" style="width:100%">
            <thead>
            <tr>
              <th>Policy No</th>
              <th>Product</th>
              <th>Start Date</th>
              <th>End Date</th>
              <th> Date</th>
              <th> Ref</th>
              <th> Type</th>
              <th>Gross</th>
              <th>Net</th>
              <th>Balance</th>
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
          <button type="button" class="close" data-dismiss="modal"
                  aria-label="Close">
            <span aria-hidden="true">&times;</span>
          </button>
          <h4 class="modal-title" id="clientReqDocsModalLabel">Select Required Docs</h4>
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
                <button  id="searchDocuments"
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
                  type="button" class="btn btn-success">Save</button>
          <button type="button" class="btn btn-default" data-dismiss="modal">
            Cancel</button>
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
            <button type="button" class="close" data-dismiss="modal"
                    aria-label="Close">
              <span aria-hidden="true">&times;</span>
            </button>
            <h4 class="modal-title" id="clientdocModalLabel">Upload Client Document</h4>
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

<div id="quot-panel">
    <sec:authorize access="hasAnyAuthority('ACCESS_QUOTES')">
        <input type="button" class="btn btn-primary btn btn-danger float-right"
               value="Reject Task" id="btn-reject-quot">
    </sec:authorize>

    <sec:authorize access="hasAnyAuthority('ACCESS_QUOTES')">
        <input type="button" class="btn btn-primary float-right"
               value="Approve Task" id="btn-approve-quote">
    </sec:authorize>
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
    </div></div>
</div>
<div id="approvalModal" class="modal fade" tabindex="-1" role="dialog">
    <div class="modal-dialog" role="document">
        <div class="modal-content">
            <div class="modal-header">
                <h5 class="modal-title">Approve Task</h5>
                <button type="button" class="close" data-dismiss="modal" aria-label="Close">
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
                <button type="button" class="close" data-dismiss="modal" aria-label="Close">
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

<script type="text/javascript">
    $(document).ready(function() {
        $(".datepicker-input").each(function() {
          $(this).datetimepicker({
            format: 'DD/MM/YYYY'
          });
        });
        $("#btn-approve-quote").on('click', function (){
            approveTask();
        });

        $("#btn-reject-quot").on('click', function (){
            rejectTask();
        });
        var mckIdNo = "${mck_id_no}";  // Make sure mck_id_no is set
        var url = SERVLET_CONTEXT + "/protected/getCheckerData";
        var taskType = "client";


        $.ajax({
            url: url,
            method: 'GET',
            data: {
            mckIdNo: mckIdNo,
            taskType: taskType
             },
            success: function(data) {
                var clientData = (typeof data === "string") ? JSON.parse(data) : data;
                populateTenantDetails(clientData)
            },
            error: function(xhr, status, error) {
                console.error('Error fetching client data:', error);
            }
        });
        function populateTenantDetails(data){
            $("#fname").text(data.fname);
            $("#other-names").text(data.otherNames);
            $("#client_det").html(data.fname+" "+data.otherNames);
            $("#comment").text(data.comment);
            $("#id-no").text(data.idNo);
            $("#passport-no").text(data.passportNo);
            $("#pin-no").text(data.pinNo);
            //$("#client-ref-no").text(data.clientRef);
            $("#client-ref-no").text(data.segmentName);
            $("#email-address").text(data.emailAddress);
            $("#address").text(data.address);
            if(data.dob)
                $("#dob").text(moment(data.dob).format('DD/MM/YYYY'));
            if(data.status && data.status==='A'){
              $("#sel3").text('Active').css('color','green').css('font-weight','bold');
            }
            else  if(data.status && data.status==='I'){
              $("#sel3").text('Inactive').css('color','red').css('font-weight','bold');
            }
            else  if(data.status && data.status==='T'){
              $("#sel3").text('Terminated').css('color','red').css('font-weight','bold');
            }
            else{
              $("#sel3").text('Pending Verification').css('color','red').css('font-weight','bold');
            }
            if(data.authStatus && data.authStatus==='Y'){
              $("#btn-auth-client").val('Unapprove');
              $("#clnt-authorised").text('Yes').css('color','green').css('font-weight','bold');
            }
            else{
              $("#btn-auth-client").val('Approve');
              $("#clnt-authorised").text('No').css('color','red').css('font-weight','bold');

            }

            if(data.authStatus && data.authStatus==='Y'){
              $('#btn-add-docs').css('display', 'none');
            }
            else {
              $('#btn-add-docs').css('display', 'block');

            }

            if(data.clientType==='C' || data.clientType===''){
              $("#gender,.gender,#lblGender").hide();
              $("#lbl-dob").html("Date of Incorporation");
              $("#lbl-id-no").html("Registration No.");
              $(".employee-info").hide();

            }
            else if(data.clientType==='I'){
              $("#gender,.gender,#lblGender").show();
              $("#lbl-dob").html("Date of Birth");
              $("#lbl-id-no").html("ID No.");
              $(".employee-info").show();
            }
            if(data.gender && data.gender==='M'){
              $("#gender").text('Male');
            }
            else if(data.gender && data.gender==='F'){
              $("#gender").text('Female');
            }
            else{
                $("#gender").text('Other');
            }
            $("#ten-id").text(data.tenantNumber);
            $("#office-tel-no").text(data.officeTel);
            $("#date-reg").text(moment(data.dateregistered).format('DD/MM/YYYY'));
            $("#ob-name").text(data.obName);
            if(data.country) {
              $("#cou-name").text(data.couName);
            }
            if(data.smsPrefix) {
              $("#pref-sms-name").text(data.smsPrefixName + data.smsNumber);
            }
            else{
              $("#pref-sms-name").text(data.smsNumber);
            }
            if(data.phonePrefix) {
              $("#pref-phone-name").text(data.phonePrefixName + data.phoneNo);
            }
            else{
              $("#pref-phone-name").text(data.phoneNo);
            }
            if(data.clientTypeId) {
              $("#clnt-type-name").text(data.clientTypeDesc);
            }
            if(data.titleId) {
              $("#clnt-title-name").text(data.titleName);
            }
            if(data.residentStatus) {
              $("#resident-status").text(data.residentStatus);
            }
            if(data.sectCode) {
              $("#sect-name").text(data.sectName);

            }
            if(data.occCode) {
              $("#occ-name").text(data.occName);
            }

            if(data.ctCode) {
              $("#clnt-town-name").text(data.ctName);
            }
            if(data.pcode) {
              $("#postal-name").text(data.postalName);
            }
            if(data.dateterminated)
              $("#dt-terminated").text(moment(data.dateterminated).format('DD/MM/YYYY'));
            getClientDocs(mckIdNo);
        }
        function rejectTask(){
            // Display the modal
            $('#rejectionModal').modal('show');

            $('#rejectConfirmButton').off('click').on('click', function() {
                var reason = $('#rejectionReason').val();

                if (reason) {
                    $.ajax({
                        url: SERVLET_CONTEXT + '/protected/users/rejectTask',
                        type: 'POST',
                        contentType: 'application/json',
                        data: JSON.stringify({
                            taskId: mck_id_no,
                            reason: reason
                        }),
                        success: function(response) {
                            window.location.href = SERVLET_CONTEXT + "/protected/home";

                        },
                        error: function(xhr, status, error) {
                            console.error("Error rejecting task: ", error);
                        }
                    });
                } else {
                    console.error("No reason provided for rejection");
                }
                // Hide the modal after submission
                $('#rejectionModal').modal('hide');
            });
        }

        function approveTask(){
            // Display the modal
            $('#approvalModal').modal('show');
            $('#approveConfirmButton').off('click').on('click', function() {

                $.ajax({
                    url: SERVLET_CONTEXT + '/protected/users/approveTask',
                    type: 'POST',
                    contentType: 'application/json',
                    data: JSON.stringify(mck_id_no),
                    success: function(response) {
                        window.location.href = SERVLET_CONTEXT + "/protected/home";
                    },
                    error: function(xhr, status, error) {
                        console.error("Error approving task: ", error);
                    }
                });

                // Hide the modal after submission
                $('#approvalModal').modal('hide');
            });
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
    });
</script>


