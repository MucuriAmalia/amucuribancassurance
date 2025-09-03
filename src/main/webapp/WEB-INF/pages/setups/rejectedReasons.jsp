<%--
  Created by IntelliJ IDEA.
  User: orito
  Date: 18/03/2025
  Time: 08:52
  To change this template use File | Settings | File Templates.
--%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>

<div class="x_panel">
    <button class="btn btn-success btn btn-info float-right" id="btn-add-rjct-reason">New</button>
    <div class="x_title">
        <h2><i class="fa fa-bars"></i> Rejected Draft Policy Reasons</h2>
        <div class="clearfix"></div>
    </div>
    <div class="table-responsive">
        <table id="rjct-reasons-tbl" class="table table-striped" style="width: 100%">
            <thead>
            <tr class="headings">

                <th>Reason Related</th>
                <th width="5%"></th>
                <th width="5%"></th>
            </tr>
            </thead>
        </table>
    </div>
</div>


<div class="modal fade" id="rjctReasonModal" tabindex="-1" role="dialog"
     aria-labelledby="rjctReasonModalLabel" aria-hidden="true">
    <div class="modal-dialog">
        <div class="modal-content">
            <h4 class="modal-title" id="rjctReasonModalLabel">
                Edit/Add Reject Reason
            </h4>
            <div class="modal-header">
                <button type="button" class="close" data-dismiss="modal"
                        aria-label="Close">
                    <span aria-hidden="true">&times;</span>
                </button>

            </div>
            <div class="modal-body" id="branch_model">
                <form id="reject-reason-form" class="form-horizontal">
                    <input type="hidden" class="form-control" id="rjct-id" name="reasonId">
                    <div class="item form-group">
                        <label for="unit-id" class="col-md-3 label-align">Reject Reason Desc</label>

                        <div class="col-md-8">
                            <input type="text" class="form-control" id="rjct-desc"
                                   name="reasonDesc"  required>
                        </div>
                    </div>

                </form>
            </div>
            <div class="modal-footer">
                <button data-loading-text="Saving..." id="saveRjctReason"
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

    $(function() {

        $(document).ready(function () {
            createRjctReasons();
            newRejectReason();
            saveRejectReason();
        });
    });

    function newRejectReason(){
        $("#btn-add-rjct-reason").on("click", function(){
            $('#reject-reason-form').find("input[type=text],input[type=mobileNumber],input[type=emailFull],input[type=password],input[type=hidden],input[type=number], textarea,select").val("");
            $('#rjctReasonModal').modal('show');
        });
    }

    function saveRejectReason(){
        var $form = $('#reject-reason-form');
        var validator = $form.validate();
        $('#rjctReasonModal').on('hidden.bs.modal', function () {
            validator.resetForm();
            $('#reject-reason-form').find("input[type=text],input[type=mobileNumber],input[type=emailFull],input[type=password],input[type=hidden], textarea,select").val("");
        });

        $('#saveRjctReason').click(function(){
            if (!$form.valid()) {
                return;
            }
            var $btn = $(this).button('Saving');
            var data = {};
            $form.serializeArray().map(function(x){data[x.name] = x.value;});
            var url = "createRejectedReasons";
            var request = $.post(url, data );
            request.success(function(){
                Swal.fire({
                    title: 'Success',
                    text: 'Record created/updated Successfully',
                    icon: 'success'
                })
                $('#rjct-reasons-tbl').DataTable().ajax.reload();
                validator.resetForm();
                $('#rjctReasonModal').modal('hide');
            });
            request.error(function(jqXHR, textStatus, errorThrown){
                Swal.fire({
                    title: 'Error',
                    text: jqXHR.responseText,
                    icon: 'error'
                })
            });
            request.always(function(){
                $btn.button('reset');
            });
        });
    }

    function editRjctReason(button){
        var activity = JSON.parse(decodeURI($(button).data("rejectreason")));
        $("#rjct-id").val(activity["reasonId"]);
        $("#rjct-desc").val(activity["reasonDesc"]);
        $('#rjctReasonModal').modal({
            backdrop: 'static',
            keyboard: true
        })
    }

    function confirmDelRjctReason(button){
        var reason = JSON.parse(decodeURI($(button).data("rejectreason")));
        bootbox.confirm("Are you sure want to delete "+reason["reasonDesc"]+"?", function(result) {
            if(result){
                $.ajax({
                    type: 'GET',
                    url:  'deleteRejectedReasons/' + reason["reasonId"],
                    dataType: 'json',
                    async: true,
                    success: function(result) {
                        Swal.fire({
                            title: 'Success',
                            text: 'Record created/updated Successfully',
                            icon: 'success'
                        })
                        $('#rjct-reasons-tbl').DataTable().ajax.reload();
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

    function createRjctReasons(){
        var url = "rjctReasons";
        var table = $('#rjct-reasons-tbl').DataTable( {
            "processing": true,
            "serverSide": true,
            "ajax": url,
            lengthMenu: [ [10, 15], [10, 15] ],
            pageLength: 10,
            destroy: true,
            "columns": [
                { "data": "reasonDesc" },
                {
                    "data": "reasonId",
                    "render": function ( data, type, full, meta ) {
                        return '<button type="button" class="btn btn-success btn btn-info btn-sm" data-rejectreason='+encodeURI(JSON.stringify(full)) + ' onclick="editRjctReason(this);"><i class="fa fa-pencil-square-o"></button>';
                    }

                },
                {
                    "data": "reasonId",
                    "render": function ( data, type, full, meta ) {
                        return '<button type="button" class="btn btn-danger btn btn-danger btn-sm" data-rejectreason='+encodeURI(JSON.stringify(full)) + ' onclick="confirmDelRjctReason(this);"><i class="fa fa-trash-o"></button>';
                    }

                },
            ]
        } );
        return table;
    }
</script>