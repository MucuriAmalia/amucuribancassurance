<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<script type="text/javascript" src="<c:url value="/js/modules/utils/select2builder.js"/>"></script>
<script type="text/javascript" src="<c:url value="/libs/rivets/rivets.js"/>"></script>
<div class="x_panel">
    <div class="card-box table-responsive">
        <div class="x_title">
            <h4>Ticketing Screen</h4>
        </div>
        <form id="search-form" class="form-horizontal">
            <div class="item form-group">

                <div class="col-md-6 col-xs-12">
                    <label for="brn-id" class="col-md-4 label-align">Policy
                        No.</label>

                    <div class="col-md-8 col-xs-12">
                        <input type='text' class="form-control float-right"
                               id="pol-search-number" placeholder="Policy No" />
                    </div>
                </div>
                <div class="item form-group riskIdgroup">
                    <div class="col-md-6 col-xs-12" >
                        <label for="brn-id" class="col-md-4 label-align">Quote
                            No.</label>

                        <div class="col-md-8 col-xs-12">
                            <input type='text' class="form-control float-right"
                                   id="rev-search-number" placeholder="Quote No"/>
                        </div>
                    </div>
                </div>
            </div>
            <div class="item form-group">

                <div class="col-md-6 col-xs-12">
                    <label for="brn-id" class="col-md-4 label-align">Prepared By
                    </label>

                    <div class="col-md-8 col-xs-12">
                        <input type='hidden' class="form-control float-right"
                               id="rev-search-name" />
                        <input type='text' class="form-control float-right"
                               id="dr-search-user" placeholder="Prepared By"/>
                    </div>
                </div>
                <div class="col-md-6 col-xs-12">

                </div>

            </div>
            <div class="item form-group">
                <input type="button" class="btn btn-info float-right"
                       style="margin-right: 10px;" value="Search"
                       id="btn-search-tickets">
            </div>


        </form>
        <table id="tckt_enquiry_tbl" class="table" style="width:100%">
            <thead>
            <tr>
                <th>Task ID</th>
                <th>Task Name</th>
                <th>Ref No.</th>
                <th>Client Name</th>
                <th>Prepared By</th>
                <th>Task Date</th>
                <th>Task Assignee</th>
                <th width="5%"></th>
            </tr>
            </thead>
        </table>
    </div>
</div>

<div class="modal fade" id="assignModal" tabindex="-1" role="dialog"
     aria-labelledby="assignModalLabel" aria-hidden="true">
    <div class="modal-dialog">
        <div class="modal-content">
            <div class="modal-header">
                <button type="button" class="close" data-dismiss="modal"
                        aria-label="Close">
                    <span aria-hidden="true">&times;</span>
                </button>
                <h4 class="modal-title" id="assignModalLabel">Assign Ticket to User</h4>
            </div>
            <div class="modal-body">

                <form id="assign-user-form" class="form-horizontal">
                    <div class="item form-group">
                        <input type="hidden" name="businessKey" id="business-key">
                        <label for="brn-id" class="col-md-4 label-align">Select User</label>

                        <div class="col-md-8">
                            <input type="hidden" id="assign-user-name" name="username"/>
                            <div id="user-assignee-div" class="form-control"
                                 select2-url="<c:url value="/protected/organization/managers"/>" >

                            </div>
                        </div>
                    </div>

                </form>

            </div>
            <div class="modal-footer">
                <button data-loading-text="Saving..." id="updateTicket"
                        type="button" class="btn btn-success">
                    OK
                </button>
                <button type="button" class="btn btn-default" data-dismiss="modal">
                    Close</button>
            </div>
        </div>
    </div>
</div>

<script type="application/javascript">
    $(document).ready(function () {
        getTickets();

        $("#btn-search-tickets").on('click', function(){
            getTickets();

        });

        $("#pol-search-number").on("change paste keyup", function() {
              $("#rev-search-number").val('');
              $("#dr-search-user").val('');
        });
        $("#rev-search-number").on("change paste keyup", function() {
            $("#pol-search-number").val('');
            $("#dr-search-user").val('');
        });
        $("#dr-search-user").on("change paste keyup", function() {
            $("#pol-search-number").val('');
            $("#rev-search-number").val('');
        });

        updateTicket();


    });

     function updateTicket() {
         var $classForm = $('#assign-user-form');
         var validator = $classForm.validate();
         $('#updateTicket').click(function () {
             if (!$classForm.valid()) {
                 return;
             }
             if ($("#assign-user-name").val() === '') {
                 bootbox.alert('Select User to assign!');
                 return;
             }
             // $('#myPleaseWait').modal({
                 backdrop: 'static',
                 keyboard: true
             });
            // var $btn = $(this).button('Saving');
             var data = {};
             $classForm.serializeArray().map(function (x) {
                 data[x.name] = x.value;
             });
             var url = SERVLET_CONTEXT + "/protected/workflow/reaassigneeTicket";
             var request = $.post(url, data);
             request.success(function () {
                 // $('#myPleaseWait').modal('hide');
                 $('#assignModal').modal('hide');
                 Swal.fire({
                     title: 'Success',
                     text: 'Ticket Reassigned Successfully',
                     icon: 'success'
                 });
                 $('#tckt_enquiry_tbl').DataTable().ajax.reload();
             });

             request.error(function (jqXHR, textStatus, errorThrown) {
                 // $('#myPleaseWait').modal('hide');
                 Swal.fire({
                     title: 'Error',
                     text: jqXHR.responseText,
                     icon: 'error'
                 })
             });
             request.always(function () {
              //   $btn.button('reset');
             });
         });
     }

      function getTickets(){
        var url = SERVLET_CONTEXT + '/protected/workflow/tickets';
        var currTable = $('#tckt_enquiry_tbl').DataTable(UTILITIES.extendsOpts({
            "ajax": {
                'url': url,
                'data': {
                    'policyNo': $("#pol-search-number").val(),
                    'quoteNo': $("#rev-search-number").val(),
                    'preparedBy': $("#dr-search-user").val(),
                },
            },
            "order": [[ 5, "desc" ]],
            "columns": [
                { "data": "taskId" },
                { "data": "activeProcess" },
                { "data": "docId",
                    "render": function ( data, type, full, meta ) {
                        if(full.policyTrans) {
                            if (full.policyTrans.polNo)
                                return full.policyTrans.polNo;
                            else if (full.policyTrans.proposalNo)
                                return full.policyTrans.proposalNo;
                        }
                        else if(full.quoteTrans) {
                            return full.quoteTrans.quotNo;
                        }
                        else return "";
                    }
                },
                { "data": "docId",
                    "render": function ( data, type, full, meta ) {
                        if(full.policyTrans)
                            return full.policyTrans.client.fname+" "+full.policyTrans.client.otherNames;
                        else if(full.quoteTrans){
                            if(full.quoteTrans.clientType=="C")
                                return full.quoteTrans.client.fname+" "+full.quoteTrans.client.otherNames;
                            else
                                return full.quoteTrans.prospect.fname+" "+full.quoteTrans.prospect.otherNames;
                        }


                        else return "";
                    }
                },
                { "data": "docId",
                    "render": function ( data, type, full, meta ) {
                        if(full.policyTrans)
                            return full.policyTrans.createdUser.username;
                        else if(full.quoteTrans)
                            return full.quoteTrans.preparedBy.username;
                        else return "";
                    }
                },
                { "data": "docId",
                    "render": function ( data, type, full, meta ) {
                        if(full.createdDate)
                        return moment(full.createdDate).format('DD/MM/YYYY');
                        else return "";
                    }
                },
                { "data": "docId",
                    "render": function ( data, type, full, meta ) {
                        if(full.userId)
                            return full.userId.username;
                        else return "";
                    }
                },
                {
                    "data": "docId",
                    "render": function ( data, type, full, meta ) {
                        return '<button type="button" class="btn btn-success btn btn-info btn-sm" data-tickets=' + encodeURI(JSON.stringify(full)) + ' onclick="reassign(this);">Reassign</button>';
                    }

                },
            ]
        } ));
        return currTable;
    };

      function reassign(button){
          var wfdocs = JSON.parse(decodeURI($(button).data("tickets")));
              UTILITIES.createAssigneeLov();
              if(wfdocs['policyTrans'])
              $("#business-key").val(wfdocs['policyTrans'].policyId);
              else if(wfdocs['quoteTrans'])
                  $("#business-key").val(wfdocs['quoteTrans'].quoteId);
              $('#assignModal').modal({
                  backdrop: 'static',
                  keyboard: true
              })
      }
</script>