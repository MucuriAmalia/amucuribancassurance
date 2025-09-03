<%--
  Created by IntelliJ IDEA.
  User: orito
  Date: 27/01/2025
  Time: 11:29
  To change this template use File | Settings | File Templates.
--%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<script type="text/javascript" src="<c:url value="/js/modules/utils/select2builder.js"/>"></script>
<script type="text/javascript" src="<c:url value="/libs/rivets/rivets.js"/>"></script>
<div class="x_panel">
    <button class="btn btn-success btn btn-info float-right" id="btn-add-seg">New</button>
    <div class="x_title">
        <h2><i class="fa fa-bars"></i> Segments</h2>
        <div class="clearfix"></div>
    </div>
    <div class="x_content">
        <div class="table-responsive">
            <table id="segtbl" class="table table-striped" style="width:100%">
                <thead>
                <tr>
                    <th>Seg Name</th>
                    <th>Description</th>
                    <th>Segment </th>
                    <th>SAP Segment</th>
                    <th>Company Code</th>
                    <th>Profit Center</th>
                    <th>Main Bank segment</th>
                    <th style="width: 5px"></th>
                </tr>
                </thead>
            </table>
        </div>
    </div>
    </div>
</div>
        <div class="modal fade" id="segModal" tabindex="-1" role="dialog"
             aria-labelledby="segModalLabel" aria-hidden="true">
            <div class="modal-dialog">
                <div class="modal-content">
                    <div class="modal-header">
                        <h4 class="modal-title" id="segModalLabel">
                            Edit/Add Segments
                        </h4>
                        <button type="button" class="close" data-dismiss="modal"
                                aria-label="Close">
                            <span aria-hidden="true">&times;</span>
                        </button>
                    </div>
                    <div class="modal-body" id="seg_model">
                        <form id="segmentForm" class="form-horizontal" method="post" action="<c:url value='/protected/setups/segments'/>">
                            <input type="hidden" id="seg-id" name="segId">
                            <div class="item form-group">
                                <label for="seg-name" class="col-md-3 label-align">Segment Name</label>
                                <div class="col-md-8">
                                    <input type="text" class="form-control" id="seg-name"
                                           name="segName" required>
                                </div>
                            </div>
                            <div class="item form-group">
                                <label for="seg-description" class="col-md-3 label-align">Description</label>
                                <div class="col-md-8">
                                    <input type="text" class="form-control" id="seg-description"
                                           name="segDescription" required>
                                </div>
                            </div>
                            <div class="item form-group">
                                <label for="seg-segment" class="col-md-3 label-align">Segment</label>
                                <div class="col-md-8">
                                    <input type="text" class="form-control" id="seg-segment"
                                           name="segSegment" required>
                                </div>
                            </div>
                            <div class="item form-group">
                                <label for="sap-seg-segment" class="col-md-3 label-align">Sap Segment</label>
                                <div class="col-md-8">
                                    <input type="text" class="form-control" id="sap-seg-segment"
                                           name="sapSegment" required>
                                </div>
                            </div>
                            <div class="item form-group">
                                <label for="seg-code" class="col-md-3 label-align">Company Code</label>
                                <div class="col-md-8">
                                    <input type="text" class="form-control" id="seg-code"
                                           name="segCode" required>
                                </div>
                            </div>
                            <div class="item form-group">
                                <label for="seg-comp-code" class="col-md-3 label-align">Profit Center</label>
                                <div class="col-md-8">
                                    <input type="text" class="form-control" id="seg-comp-code" name="segCompCode" required>
                                </div>
                            </div>
                            <div class="item form-group">
                                <label for="seg-main-bank_segment" class="col-md-3 label-align">Main Bank Segment</label>
                                <div class="col-md-8 col-xs-12">
                                    <input type="hidden" id="seg-main-bank-segment-id" name="segMainBankSegmentId"/>
                                    <input type="hidden" id="seg-main-bank-segment-name" name="segMainBankSegment">
                                    <div id="seg-main-bank_segment" class="form-control"
                                         select2-url="<c:url value="/protected/setups/selMainBankSegments"/>" >
                                    </div>
                                </div>
                            </div>
                        </form>
                    </div>
                    <div class="modal-footer">
                        <button data-loading-text="Saving..." id="savesegments"
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

    function createMainBankSegmentSelect() {
        if($("#seg-main-bank_segment").filter("div").html() != undefined) {
            Select2Builder.initAjaxSelect2({
                containerId : "seg-main-bank_segment",
                sort : 'mainBankSegName',
                change:  function(e, a, v){
                    $("#seg-main-bank-segment-id").val(e.added.mainBankSegId);
                    $("#seg-main-bank-segment-name").val(e.added.mainBankSegName);
                },
                formatResult : function(a) {
                    return a.mainBankSegName
                },
                formatSelection : function(a) {
                    return a.mainBankSegName
                },
                initSelection: function (element, callback) {
                    var id = $("#seg-main-bank-segment-id").val();
                    var name = $("#seg-main-bank-segment-name").val();
                    var data = {mainBankSegName: name, mainBankSegId: id};
                    callback(data);
                },
                id: "mainBankSegId",
                placeholder: "Select Main Bank Segment",
            });
        }
    }

    // Function to set the main bank segment dropdown
    function setSegmentMainBankSegment(segmentId, segmentName) {
        $("#seg-main-bank-segment-id").val(segmentId);
        $("#seg-main-bank-segment-name").val(segmentName);

        // Trigger the initSelection to update the display
        $("#seg-main-bank_segment").select2("val", segmentId);
    }

    // Function to clear the main bank segment selection
    function clearSegmentMainBankSegment() {
        $("#seg-main-bank-segment-id").val('');
        $("#seg-main-bank-segment-name").val('');
        $("#seg-main-bank_segment").select2("val", "");
    }

    function newSegment() {
        $('#segmentForm')[0].reset(); // Clear the form
        $("#seg-id").val(''); // Clear the hidden field for segment ID

        // Clear the main bank segment dropdown
        clearSegmentMainBankSegment();

        // Initialize the main bank segment select2
        createMainBankSegmentSelect();

        $('#segModal').modal('show');
    }

    $(document).ready(function() {
        createSegmentsTable();

        $('#btn-add-seg').click(function() {
            newSegment();
        });

        $('#savesegments').click(function() {
            var $btn = $(this).button('loading');
            var form = $('#segmentForm')[0];

            // Check if the form is valid
            if (!form.checkValidity()) {
                // Trigger HTML5 validation
                form.reportValidity();
                $btn.button('reset');
                return;
            }

            var formData = $('#segmentForm').serialize();
            $.ajax({
                type: 'POST',
                url: $('#segmentForm').attr('action'),
                data: formData,
                success: function(response) {
                    console.log("Save successful:", response);
                    Swal.fire({
                        title: 'Success',
                        text: 'Segment saved successfully',
                        icon: 'success'
                    });
                    $('#segModal').modal('hide');
                    $('#segtbl').DataTable().ajax.reload();
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

    function editSegment(button) {
        var segment = JSON.parse(decodeURI($(button).data("segment")));
        $("#seg-id").val(segment["segId"]);
        $("#seg-name").val(segment["segName"]);
        $("#seg-description").val(segment["segDescription"]);
        $("#seg-segment").val(segment["segSegment"]);
        $("#sap-seg-segment").val(segment["sapSegment"]);
        $("#seg-code").val(segment["segCode"]);
        $("#seg-comp-code").val(segment["segCompCode"]);

        // Set the main bank segment dropdown - handle different possible field names
        if (segment["segMainBankSegmentId"] && segment["segMainBankSegment"]) {
            setSegmentMainBankSegment(segment["segMainBankSegmentId"], segment["segMainBankSegment"]);
        } else if (segment["segMainBankSegment"]) {
            // If you're storing just the name in segMainBankSegment field
            $("#seg-main-bank-segment-name").val(segment["segMainBankSegment"]);
        } else {
            clearSegmentMainBankSegment();
        }

        // Initialize the main bank segment select2
        createMainBankSegmentSelect();

        $('#segModal').modal('show');
    }

    function createSegmentsTable() {
        var url = "segmentsList";
        var table = $('#segtbl').DataTable({
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
                { "data": "segName" },
                { "data": "segDescription" },
                { "data": "segSegment" },
                { "data": "sapSegment" },
                { "data": "segCode" },
                { "data": "segCompCode" },
                {
                    "data": "segMainBankSegment",  // Display the name
                    "defaultContent": "" // Show empty if no data
                },
                {
                    "data": "segId",
                    "render": function(data, type, full, meta) {
                        return '<button type="button" class="btn btn-success btn-sm" data-segment=' + encodeURI(JSON.stringify(full)) + ' onclick="editSegment(this);"><i class="fa fa-pencil-square-o"></i></button>';
                    }
                }
            ]
        });
        return table;
    }
</script>