<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<script type="text/javascript"
        src="<c:url value="/js/modules/utils/select2builder.js"/>"></script>
<div class="x_panel">
    <div class="x_title">
        <h2><i class="fa fa-bars"></i> Pending Requisitions</h2>
        <ul class="nav navbar-right panel_toolbox">
            <li><a class="collapse-link"><i class="fa fa-chevron-up"></i></a>
            </li>
        </ul>
        <div class="clearfix"></div>
    </div>
</div>
<div class="col-md-12 col-xs-12 table-responsive">
    <table id="req-tbl" class="table table-striped" style="width:100%">
        <thead>
        <tr>

            <th>Req. Type</th>
            <th>Req. Date</th>
            <th>Ref No</th>
            <th>Currency</th>
            <th>Amount</th>
            <th>Invoice No</th>
            <th>Payee</th>
            <th>Account No</th>
            <th>Raised By</th>
            <th></th>
            <th></th>
        </tr>
        </thead>
    </table>
</div>

<div  class="x_panel">
    <div class="x_title">
        <h2>GL Transactions</h2>
        <div class="clearfix"></div>
    </div>
<%--    <input type="button" id="add-det-btn"--%>
<%--           class="btn btn-info float-left" style="margin-right: 10px;"--%>
<%--           value="Add">--%>
    <div class="box-body">
        <div class="table-responsive">
            <table id="req-dtls-tbl" class="table table-striped" style="width:100%">
                <thead>
                <tr>

                    <th>Narration</th>
                    <th>Amount</th>
                    <th>GL Account</th>
                    <th>GL Account Name</th>
                    <th>Branch</th>
                </tr>
                </thead>

            </table>
        </div>
    </div>
</div>

<div class="modal fade" id="rejectReqModal" tabindex="-1" role="dialog"
     aria-labelledby="rejectReqModalLabel" aria-hidden="true">
    <div class="modal-dialog modal-lg">
        <div class="modal-content">
            <div class="modal-header">
                <h4 class="modal-title" id="rejectReqModalLabel">
                    Provide Reason for Rejecting the Requisition
                </h4>
                <button type="button" class="close" data-dismiss="modal"
                        aria-label="Close">
                    <span aria-hidden="true">&times;</span>
                </button>

            </div>

            <div class="modal-body">

                <form id="reject-form" data-parsley-validate class="form-horizontal form-label-left">
                    <input type="hidden" name="reqId" id="reqId-pk">

                    <div class="item form-group form-required">
                        <div class="col-md-12 col-xs-12">
                            <label for="reason" class="label-align col-md-3">Reason<span class="required">*</span></label>
                            <div class="col-md-9 col-xs-12">
                                <textarea class="form-control" name="reason" rows="5" id="reason"></textarea>
                            </div>
                        </div>
                    </div>
                </form>
            </div>
            <div class="modal-footer">
                <button data-loading-text="Saving..." id="saveRejectionReason"
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
<script>
    $(function(){

        $(document).ready(function() {

            getRequisitionTrans();
            getRequisitionDetails(-2000);
            rejectRequistions();

        });

    });


    function rejectRequistions(){
        $('#saveRejectionReason').click(function(){
            var $classForm = $('#reject-form');
            var validator = $classForm.validate();
            if (!$classForm.valid()) {
                return;
            }
            var $btn = $(this).text('Saving..');
            var data = {};
            $classForm.serializeArray().map(function(x){data[x.name] = x.value;});
            var url = "rejectRequistion";
            var request = $.post(url, data );
            request.success(function(result){
                Swal.fire({
                title: 'Success',
                text: 'Record created/updated Successfully',
                icon: 'success'
             });
                validator.resetForm();
                $('#reject-form').find("input[type=text],input[type=number],input[type=mobileNumber],input[type=emailFull],input[type=password],input[type=hidden], textarea,select").val("");
                $('#rejectReqModal').modal('hide');
                $('#req-tbl').DataTable().ajax.reload();
                $('#req-dtls-tbl').DataTable().ajax.url( "requistiondetails/"+-2000 ).load();
            });

            request.error(function(jqXHR, textStatus, errorThrown){
                Swal.fire({
                title: 'Error',
                text: jqXHR.responseText,
                icon: 'error'
            });
            });
            request.always(function(){
                $btn.text('Save');
            });
        });
    }

    function getRequisitionDetails(reqNo){

        var url = "requistiondetails/"+reqNo;
        return $('#req-dtls-tbl')
            .DataTable(
                {
                    "processing": true,
                    "serverSide": true,
                    "ajax": url,
                    lengthMenu: [[20, 30, 40, 50], [20, 30, 40, 50]],
                    pageLength: 20,
                    "scrollX": true,
                    "autoWidth": false,
                    destroy: true,
                    "columns": [

                        {
                            "data": "narrative"
                        },
                        {
                            "data": "transAmount",
                            "render": function (data, type, full, meta) {
                                return UTILITIES.currencyFormat(full.transAmount);
                            }
                        },
                        {
                            "data": "glAcctNo",
                        },
                        {
                            "data": "glAcctName",
                        },
                        {
                            "data": "branchName",
                        }
                    ]
                });
    }

    function getRequisitionTrans(){

        var url = "requistions";
        var currTable = $('#req-tbl')
            .DataTable(
                {
                    "processing": true,
                    "serverSide": true,
                    "ajax": url,
                    lengthMenu: [[20, 30, 40, 50], [20, 30, 40, 50]],
                    pageLength: 20,
                    "scrollX": true,
                    "autoWidth": false,
                    destroy: true,
                    "columns": [

                        {
                            "data": "paymentType"
                        },
                        {
                            "data": "requistionDate",
                            "render": function (data, type, full, meta) {
                                return moment(full.requistionDate).format('DD/MM/YYYY');
                            },
                            "width": "10%"

                        },
                        {"data": "refNo"},
                        {"data": "currency"},
                        {
                            "data": "amount",
                            "render": function (data, type, full, meta) {
                                return UTILITIES.currencyFormat(full.amount);
                            }
                        },
                        {
                            "data": "invoiceNo",
                            "width": "10%"
                        },
                        {
                            "data": "payeeName",
                            "width": "20%"
                        },
                        {
                            "data": "accountNo",
                            "width": "10%"
                        },
                        {
                            "data": "raisedUser",
                            "width": "10%"
                        },
                        {
                            "data": "ctNo",
                            "render": function (data, type, full, meta) {
                                return '<button type="button" class="btn btn-success btn btn-info btn-sm" type="button" data-trans=' + encodeURI(JSON.stringify(full)) + '  onclick="authTrans(this);">Approve</button>';

                            }
                        },
                        {
                            "data": "ctNo",
                            "render": function (data, type, full, meta) {
                                return '<button type="button" class="btn btn-danger btn btn-info btn-sm" type="button" data-trans=' + encodeURI(JSON.stringify(full)) + '  onclick="rejectRequistion(this);">Reject</button>';

                            }
                        },
                    ]
                });
        $('#req-tbl tbody').on( 'click', 'tr', function () {
            $(this).addClass('table-primary').siblings().removeClass('table-primary');
            var aData = currTable.rows('.table-primary').data();
            if (aData[0] === undefined || aData[0] === null){

            }
            else{
                $('#req-dtls-tbl').DataTable().ajax.url( "requistiondetails/"+aData[0].ctNo ).load();
            }
        } );

        return currTable;
    }

    function rejectRequistion(button) {
        var trans = JSON.parse(decodeURI($(button).data("trans")));
        $('#reject-form').find("input[type=text],input[type=number],input[type=mobileNumber],input[type=emailFull],input[type=password],input[type=hidden], textarea,select").val("");
        $("#reqId-pk").val(trans['ctNo']);
        $('#rejectReqModal').modal({
            backdrop: 'static',
            keyboard: true
        })
    }


    function authTrans(button){
        var trans = JSON.parse(decodeURI($(button).data("trans")));
        bootbox.confirm("Approve Selected Transaction?", function(result) {
            if (result) {
                $.ajax({
                    type: 'GET',
                    url:  'authoriseRequistion/' + trans['ctNo'],
                    dataType: 'json',
                    async: true,
                    success: function(result) {
                        Swal.fire({
                            title: 'Success',
                            text: 'Requisition Authorised Successfully',
                            icon: 'success'
                        })

                        $('#req-tbl').DataTable().ajax.reload();
                        $('#req-dtls-tbl').DataTable().ajax.url( "requistiondetails/"+-2000 ).load();
                    },
                    error: function(jqXHR, textStatus, errorThrown) {
                        Swal.fire({
                title: 'Error',
                text: jqXHR.responseText,
                icon: 'error'
            });
                    }
                });
            }
        });
    }

</script>