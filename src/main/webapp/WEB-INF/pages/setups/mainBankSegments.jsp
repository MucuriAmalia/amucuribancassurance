<%--
  Created by IntelliJ IDEA.
  User: orito
  Date: 17/07/2025
  Time: 13:07
  To change this template use File | Settings | File Templates.
--%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<script type="text/javascript" src="<c:url value="/js/modules/utils/select2builder.js"/>"></script>
<script type="text/javascript" src="<c:url value="/libs/rivets/rivets.js"/>"></script>
<div class="x_panel">
    <button class="btn btn-success btn btn-info float-right" id="btn-add-main-bank-seg">New</button>
    <div class="x_title">
        <h2><i class="fa fa-bank"></i> Main Bank Segments</h2>
        <div class="clearfix"></div>
    </div>
    <div class="x_content">
        <div class="table-responsive">
            <table id="mainBankSegtbl" class="table table-striped" style="width:100%">
                <thead>
                <tr>
                    <th>Main Bank Segment</th>
                    <th style="width: 5px"></th>
                </tr>
                </thead>
            </table>
        </div>
    </div>
</div>

<div class="modal fade" id="mainBankSegModal" tabindex="-1" role="dialog"
     aria-labelledby="mainBankSegModalLabel" aria-hidden="true">
    <div class="modal-dialog">
        <div class="modal-content">
            <div class="modal-header">
                <h4 class="modal-title" id="mainBankSegModalLabel">
                    Edit/Add Main Bank Segment
                </h4>
                <button type="button" class="close" data-dismiss="modal"
                        aria-label="Close">
                    <span aria-hidden="true">&times;</span>
                </button>
            </div>
            <div class="modal-body" id="main_bank_seg_model">
                <form id="mainBankSegmentForm" class="form-horizontal" method="post" action="<c:url value='/protected/setups/mainBankSegments'/>">
                    <input type="hidden" id="main-bank-seg-id" name="mainBankSegId">
                    <div class="item form-group">
                        <label for="main-bank-segment-name" class="col-md-3 label-align">Main Bank Segment</label>
                        <div class="col-md-8">
                            <input type="text" class="form-control" id="main-bank-segment-name"
                                   name="mainBankSegName" required>
                        </div>
                    </div>
                </form>
            </div>
            <div class="modal-footer">
                <button data-loading-text="Saving..." id="saveMainBankSegments"
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

<script type="text/javascript">

    function newMainBankSegment() {
        $('#mainBankSegmentForm')[0].reset(); // Clear the form
        $("#main-bank-seg-id").val(''); // Clear the hidden field for main bank segment ID
        $('#mainBankSegModal').modal('show');
    }

    $(document).ready(function() {
        createMainBankSegmentsTable();

        $('#btn-add-main-bank-seg').click(function() {
            newMainBankSegment();
        });

        $('#saveMainBankSegments').click(function() {
            var $btn = $(this).button('loading');
            var form = $('#mainBankSegmentForm')[0];

            // Check if the form is valid
            if (!form.checkValidity()) {
                // Trigger HTML5 validation
                form.reportValidity();
                $btn.button('reset');
                return;
            }

            var formData = $('#mainBankSegmentForm').serialize();
            $.ajax({
                type: 'POST',
                url: $('#mainBankSegmentForm').attr('action'),
                data: formData,
                success: function(response) {
                    console.log("Save successful:", response);
                    Swal.fire({
                        title: 'Success',
                        text: 'Main Bank Segment saved successfully',
                        icon: 'success'
                    });
                    $('#mainBankSegModal').modal('hide');
                    $('#mainBankSegtbl').DataTable().ajax.reload();
                },
                error: function(jqXHR, textStatus, errorThrown) {
                    console.error("Save failed:", jqXHR.responseText);
                    Swal.fire({
                        title: 'Error',
                        text: jqXHR.responseText,
                        icon: 'error'
                    });
                },
                complete: function() {
                    $btn.button('reset');
                }
            });
        });
    });

    function editMainBankSegment(button) {
        var mainBankSegment = JSON.parse(decodeURI($(button).data("mainbanksegment")));
        $("#main-bank-seg-id").val(mainBankSegment["mainBankSegId"]);
        $("#main-bank-segment-name").val(mainBankSegment["mainBankSegName"]);
        $('#mainBankSegModal').modal('show');
    }

    function createMainBankSegmentsTable() {
        var url = "mainBankSegmentsList"; // Endpoint should filter for bank segments only
        var table = $('#mainBankSegtbl').DataTable({
            "processing": true,
            "serverSide": true,
            "ajax": {
                "url": url,
                "type": "GET",
                "dataSrc": "data"
            },
            lengthMenu: [[10, 20, 30], [10, 20, 30]],
            pageLength: 10,
            destroy: true,
            "columns": [
                { "data": "mainBankSegName" },
                {
                    "data": "mainBankSegId",
                    "render": function(data, type, full, meta) {
                        return '<button type="button" class="btn btn-success btn-sm" data-mainbanksegment=' + encodeURI(JSON.stringify(full)) + ' onclick="editMainBankSegment(this);"><i class="fa fa-pencil-square-o"></i></button>';
                    }
                }
            ]
        });
        return table;
    }
</script>