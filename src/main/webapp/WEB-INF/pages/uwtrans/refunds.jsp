<%--
  Created by IntelliJ IDEA.
  User: joanrunyiri
  Date: 29/05/2025
  Time: 12:13
  To change this template use File | Settings | File Templates.
--%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="sec" uri="http://www.springframework.org/security/tags" %>

<!-- JavaScript dependencies -->
<script type="text/javascript" src="<c:url value='/js/modules/utils/select2builder.js'/>"></script>
<script type="text/javascript" src="<c:url value='/libs/rivets/rivets.js'/>"></script>
<%--<script type="text/javascript" src="<c:url value='/js/modules/uwtrans/refunds.js'/>"></script>--%>

<!-- Main panel -->
<div class="x_panel">
    <div class="x_title">
        <h2><i class="fa fa-bars"></i> Refunds</h2>
        <ul class="nav navbar-right panel_toolbox">
            <li><a class="collapse-link"><i class="fa fa-chevron-up"></i></a></li>
        </ul>
        <div class="clearfix"></div>
    </div>

    <!-- Search Form -->
    <form id="search-form" class="form-horizontal">
        <div class="form-group row">
        <!-- Policy Number Field -->
                    <div class="col-md-6">
                        <label for="policy-search-number" class="col-form-label">Policy Number</label>
                        <input type="text" class="form-control" id="policy-search-number" placeholder="Enter Policy Number" />
                    </div>

            <!-- Client Field -->
            <div class="col-md-6">
                <label for="rev-search-name" class="col-form-label">Client</label>
                <input type="hidden" class="form-control" id="rev-search-name" />
                <div id="client-frm" class="form-control"
                     select2-url="<c:url value='/protected/uw/policies/uwClients'/>">
                </div>
            </div>

            <!-- Product Field -->
            <div class="col-md-6">
                <label for="product-search-number" class="col-form-label">Product</label>
                <input type="hidden" class="form-control" id="product-search-number" />
                <div id="prd-code" class="form-control"
                     select2-url="<c:url value='/protected/setups/binders/selproducts'/>">
                </div>
            </div>
        </div>
    </form>

    <div class="item form-group">
        <input type="button" class="btn btn-info float-right"
               style="margin-right: 10px;" value="Search"
               id="btn-search-refunds">
    </div>
</div>
</form>

<hr>

<!-- Table of Refunds -->
<div class="table-responsive">
    <table id="refunds-tbl" class="table table-striped" style="width: 100%">
        <thead>
        <tr class="headings">
            <th><input type="checkbox" id="select-all-refunds"> Select All</th>
            <th>Policy No</th>
            <th>Trans No</th>
            <th>Trans Date</th>
            <th>Client Name</th>
            <th>Product</th>
            <th>Trans Type</th>
            <th>Balance</th>
            <th width="10%">Actions</th>
        </tr>
        </thead>
    </table>
</div>

<!-- Bulk Process Section -->
<div class="row mt-3">
    <div class="col-md-12">
        <button type="button" class="btn btn-success" id="btn-process-selected-refunds" disabled>
            <i class="fa fa-check"></i> Process Selected Refunds
        </button>
        <span id="selected-count" class="ml-2 text-muted">0 refunds selected</span>
    </div>
</div>
<div class="modal fade" id="riskdocModal" tabindex="-1" role="dialog"
     aria-labelledby="riskdocModalLabel" aria-hidden="true">
    <div class="modal-dialog">
        <form id="risk-doc-form" class="form-horizontal" enctype="multipart/form-data">
            <div class="modal-content">
                <div class="modal-header">
                    <h4 class="modal-title" id="riskdocModalLabel">Upload Risk Document</h4>
                    <button type="button" class="close" data-dismiss="modal"
                            aria-label="Close">
                        <span aria-hidden="true">&times;</span>
                    </button>

                </div>
                <div class="modal-body">

                    <input type="hidden" id="risk-doc-id" name="docId"/>
                    <div class="item form-group">
                        <label for="risk-doc-name" class="col-md-3 label-align">Document Type</label>
                        <div class="col-md-8">
                            <p class="form-control-static" id="risk-doc-name"></p>
                        </div>
                    </div>
                    <div class="item form-group">
                        <label for="risk-upload-name" class="col-md-3 label-align">Uploaded File Name</label>

                        <div class="col-md-8">
                            <p class="form-control-static" id="risk-upload-name"></p>
                        </div>
                    </div>
                    <div class="item form-group">
                        <label for="avatar" class="col-md-4 label-align">Document</label>

                        <div class="col-md-8">
                            <div class="input-group col-xs-12">
                                <input name="file" type="file" id="avatar" required>
                            </div>
                        </div>
                    </div>



                </div>
                <div class="modal-footer">
                    <input  value="Upload"
                            type="submit" class="btn btn-success"
                            id="upload-doc-btn">

                    </input>
                    <button type="button" class="btn btn-default" data-dismiss="modal">
                        Close</button>
                </div>
                <div id="upload-spinner" class="text-center mt-3" style="display: none;">
                    <!-- Spinner only (no text inside) -->
                    <div class="spinner-border text-primary" role="status" style="width: 3rem; height: 3rem;">

                    </div>
                    <!-- Status text placed separately to avoid rotation -->
                    <div class="mt-2" style="margin-bottom: 30px;">Uploading document, please wait...</div>
                </div>



            </div>
        </form>
    </div>
</div>

<!-- Print form (empty for now) -->
<form id="print-form" class="form-horizontal"></form>

<script>
    var UWScreen = (function () {
        var downloadRefundDoc = function (button) {
            var docs = JSON.parse(decodeURI($(button).data("docs")));
            window.open(SERVLET_CONTEXT + '/protected/uw/policies/riskdocument/' + docs['rdId'], '_blank');

        };

        var deleteRiskDoc = function (button) {

            var docs = JSON.parse(decodeURI($(button).data("docs")));
            console.log(docs);


            bootbox.confirm("Are you sure want to delete " + docs['docShtDesc'] + "?", function (result) {
                if (result) {
                    $.ajax({
                        type: 'GET',
                        // Use same URL pattern with full path
                        url: SERVLET_CONTEXT + '/protected/uw/policies/deleteRiskDoc/' + docs['rdId'],
                        dataType: 'json',
                        async: true,
                        success: function (result) {
                            Swal.fire({
                                title: 'Success',
                                text: 'Record Deleted Successfully',
                                icon: 'success'
                            });
                            // Only change: reload refund table instead of risk table
                            if (window.refreshRefundRequiredDocsTable) {
                                window.refreshRefundRequiredDocsTable();
                            } else {
                                $('#refund_required_docs_tbl').DataTable().ajax.reload();
                            }
                        },
                        error: function (jqXHR, textStatus, errorThrown) {
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
        var editRiskDocs = function (button) {
            var docs = JSON.parse(decodeURI($(button).data("docs")));
            $("#risk-doc-name").text(docs["docDesc"]);
            $("#risk-upload-name").text(docs['uploadedFileName']);
            $("#risk-doc-id").val(docs['rdId']);
            $('#riskdocModal').modal({
                backdrop: 'static',
                keyboard: true
            });
        }



        return {
            downloadRefundDoc: downloadRefundDoc,
            deleteRiskDoc: deleteRiskDoc,
            editRiskDocs: editRiskDocs,
        };
    })();

    function uploadRefundDoc(button) {
        var requiredDocs = JSON.parse(decodeURI($(button).data("reqdocs")));
        $("#upload-refund-req-code").val(requiredDocs["rfdRequiredId"]);
        $("#refund-trans-type").val("RF");
        $(".uploadfile").show();
        $('#uploadRefundReqModal').modal({
            backdrop: 'static',
            keyboard: true
        });
    }
    $(document).ready(function() {


        $(document).on('show.bs.modal', '.modal', function () {
            var zIndex = 1040 + (10 * $('.modal:visible').length);
            $(this).css('z-index', zIndex);
            setTimeout(function () {
                $('.modal-backdrop').not('.modal-stack').css('z-index', zIndex - 1).addClass('modal-stack');
            }, 0);
        });

        $(document).on('hidden.bs.modal', '.modal', function () {
            $('.modal:visible').length && $('body').addClass('modal-open');
        });


        // Initialize table variable in wider scope
        var refundsTable;
        var selectedRefunds = [];

        // Initialize DataTable
        function initRefundsTable() {
            refundsTable = $('#refunds-tbl').DataTable({
                "processing": true,
                "serverSide": true,
                autoWidth: true,
                searching: false,
                "deferRender": true,
                "ajax": {
                    "url": SERVLET_CONTEXT + "/protected/uw/refunds/getClientRefunds",
                    "type": "GET",
                    "data": function(d) {
                        d.clientCode = $("#rev-search-name").val();
                        d.prodCode = $("#product-search-number").val();
                        d.policyNo = $("#policy-search-number").val();
                        console.log("Search params - Client:", d.clientCode, "Product:", d.prodCode, "Policy No:", d.policyNo);
                    },
                    "error": function(xhr, error, thrown) {
                        console.error("DataTable AJAX error:", error, thrown);
                    }
                },
                lengthMenu: [[10,15,20], [10,15,20]],
                pageLength: 10,
                destroy: true,
                "columns": [
                    {
                        "data": null,
                        "orderable": false,
                        "render": function(data, type, full) {
                            return '<input type="checkbox" class="refund-checkbox" data-trans-no="' + full.transno + '" data-refund-data="' + encodeURIComponent(JSON.stringify(full)) + '">';
                        }
                    },
                    {
                        "data": "policyNo",
                        "defaultContent": "-"
                    },
                    {
                        "data": "transno",
                        "defaultContent": "-"
                    },
                    {
                        "data": "transDate",
                        "render": function(data) {
                            return data ? moment(data).format('DD/MM/YYYY') : '-';
                        }
                    },
                    {
                        "data": null,
                        "render": function(data) {
                            var fname = data.clientFname || '';
                            var onames = data.clientOtherNames || '';
                            return fname + ' ' + onames;
                        }
                    },
                    {
                        "data": "productDesc",
                        "defaultContent": "-"
                    },
                    {
                        "data": "transType",
                        "defaultContent": "-"
                    },
                    {
                        "data": "balance",
                        "render": function(data) {
                            return data ? UTILITIES.currencyFormat(data) : '-';
                        }
                    },
                    {
                        "data": "transno",
                        "render": function(data, type, full) {
                            return '<button class="btn btn-success btn btn-info btn-sm process-single-refund" ' +
                                'data-trans-no="' + data + '" ' +
                                'data-refund-data="' + encodeURIComponent(JSON.stringify(full)) + '">' +
                                'Process</button>';
                        }

                    }
                ],
                "order": [[3, "desc"]], // Sort by date column
                "drawCallback": function() {
                    updateSelectedCount();
                    updateSelectAllCheckbox();
                }
            });

            return refundsTable;
        }

        // Handle select all checkbox
        $('#select-all-refunds').on('change', function() {
            var isChecked = $(this).is(':checked');
            $('.refund-checkbox:visible').prop('checked', isChecked);
            updateSelectedRefunds();
        });

        // Handle individual checkbox changes
        $(document).on('change', '.refund-checkbox', function() {
            updateSelectedRefunds();
            updateSelectAllCheckbox();
        });

        // Update selected refunds array
        function updateSelectedRefunds() {
            selectedRefunds = [];
            $('.refund-checkbox:checked').each(function() {
                var refundData = JSON.parse(decodeURIComponent($(this).data('refund-data')));
                selectedRefunds.push(refundData);
            });
            updateSelectedCount();
            $('#btn-process-selected-refunds').prop('disabled', selectedRefunds.length === 0);
        }

        // Update selected count display
        function updateSelectedCount() {
            var count = $('.refund-checkbox:checked').length;
            $('#selected-count').text(count + ' refund' + (count !== 1 ? 's' : '') + ' selected');
        }

        // Update select all checkbox state
        function updateSelectAllCheckbox() {
            var totalVisible = $('.refund-checkbox:visible').length;
            var totalChecked = $('.refund-checkbox:visible:checked').length;

            if (totalChecked === 0) {
                $('#select-all-refunds').prop('indeterminate', false).prop('checked', false);
            } else if (totalChecked === totalVisible) {
                $('#select-all-refunds').prop('indeterminate', false).prop('checked', true);
            } else {
                $('#select-all-refunds').prop('indeterminate', true);
            }
        }

        // Handle bulk process button
        $('#btn-process-selected-refunds').on('click', function() {
            if (selectedRefunds.length === 0) {
                bootbox.alert("Please select at least one refund to process.");
                return;
            }

            var message = "Are you sure you want to process " + selectedRefunds.length + " refund" +
                (selectedRefunds.length !== 1 ? "s" : "") + "?<br><br>" +
                "This will create reverse transactions and submit them for approval.";

            bootbox.confirm({
                title: "Confirm Refund Processing",
                message: message,
                buttons: {
                    confirm: {
                        label: 'Yes, Process Refunds',
                        className: 'btn-success'
                    },
                    cancel: {
                        label: 'Cancel',
                        className: 'btn-secondary'
                    }
                },
                callback: function(result) {
                    if (result) {
                        processRefunds(selectedRefunds);
                    }
                }
            });
        });

        // Handle single refund process
        // $(document).on('click', '.process-single-refund', function() {
        //     var refundData = JSON.parse(decodeURIComponent($(this).data('refund-data')));
        //
        //     bootbox.confirm({
        //         title: "Confirm Refund Processing",
        //         message: "Are you sure you want to process this refund?<br><br>" +
        //             "Transaction: " + refundData.transno + "<br>" +
        //             "Amount: " + UTILITIES.currencyFormat(refundData.balance),
        //         buttons: {
        //             confirm: {
        //                 label: 'Yes, Process Refund',
        //                 className: 'btn-success'
        //             },
        //             cancel: {
        //                 label: 'Cancel',
        //                 className: 'btn-secondary'
        //             }
        //         },
        //         callback: function(result) {
        //             if (result) {
        //                 processRefunds([refundData]);
        //             }
        //         }
        //     });
        // });
        // Handle single refund process
        $(document).on('click', '.process-single-refund', function() {
            var refundData = JSON.parse(decodeURIComponent($(this).data('refund-data')));
            var refundTransNo = refundData.transno;
            var riskId = refundData.riskId;
            console.log("refund data", refundData);

            var dialog = bootbox.dialog({
                title: "Confirm Refund Processing",
                message: '<div class="refund-confirmation">' +
                    '<p><strong>Are you sure you want to process this refund?</strong></p>' +
                    '<p><strong>Transaction:</strong> ' + refundData.transno + '</p>' +
                    '<p><strong>Amount:</strong> ' + UTILITIES.currencyFormat(refundData.balance) + '</p>' +
                    '</div>' +
                    '<hr>' +
                    '<div class="x_panel">' +
                    '<div class="x_title">' +
                    '<h4>Required Documents</h4>' +
                    '</div>' +
                    '<div class="table-responsive" aria-labelledby="profile-tab">' +
                    '<button type="button" class="btn btn-info btn-sm" id="btn-add-refund-docs">New</button>' +
                    '<table id="refund_required_docs_tbl" class="table table-striped table-sm" style="width: 100%; margin-top: 10px;">' +
                    '<thead>' +
                    '<tr class="headings">' +
                    '<th>Document ID</th>' +
                    '<th>File Name</th>' +
                    '<th>Uploaded By</th>' +
                    '<th>Uploaded Date</th>' +
                    '<th>Remarks</th>' +
                    '<th></th>' +
                    '<th></th>' +
                    '<th></th>' +
                    '</tr>' +
                    '</thead>' +
                    '</table>' +
                    '</div>' +
                    '</div>'+
                    '<div class="x_panel">' +
                    '<div class="x_title">' +
                    '<h4>Refund Comments</h4>' +
                    '</div>' +
                    '<div class="x_content">' +
                    '<form id="refundCommentsForm">' +
                    '<div class="form-group">' +
                    '<label for="refundCommentsText">Comments</label>' +
                    '<textarea class="form-control" id="refundCommentsText" rows="4" placeholder="Enter general comments about this refund..."></textarea>' +
                    '</div>' +
                    '</form>' +
                    '</div>' +
                    '</div>',

                size: 'large',
                buttons: {
                    cancel: {
                        label: 'Cancel',
                        className: 'btn-secondary'
                    },
                    confirm: {
                        label: 'Process Refund',
                        className: 'btn-success',
                        callback: function() {
                            var refundComments = $('#refundCommentsText').val();
                            processRefunds([refundData], refundComments);
                            return true;
                        }
                    }
                }
            });


            dialog.on('shown.bs.modal', function() {

                getRefundRequiredDocs(riskId);


                $(document).off('click', '#btn-add-refund-docs').on('click', '#btn-add-refund-docs', function() {
                    showRefundDocSelectionModal(riskId);
                });
            });
        });
        function showRefundDocSelectionModal(riskId) {
            var dialog = bootbox.dialog({
                title: "Select Required Documents",
                message: '<form class="form-horizontal" id="refund-docs-form">' +
                    // Copy the exact same hidden field pattern that works
                    '<input type="hidden" name="subCode" value="' + riskId + '">' +

                    '<div class="item form-group">' +
                    '<label for="refund-doc-name-search" class="col-md-3 label-align">Document Name</label>' +
                    '<div class="col-md-6">' +
                    '<input type="text" class="form-control" id="refund-doc-name-search" name="req-doc">' +
                    '</div>' +
                    '<div class="col-md-3">' +
                    '<button id="searchRefundDocuments" type="button" class="btn btn-primary">Search</button>' +
                    '</div>' +
                    '</div>' +
                    '</form>' +
                    '<div style="height: 300px !important; overflow: scroll;">' +
                    '<table class="table table-striped table-hover table-bordered table-fixed" id="refundReqDocsTbl">' +
                    '<thead>' +
                    '<tr>' +
                    '<th width="1%"></th>' +
                    '<th width="4%">Document Id</th>' +
                    '<th width="12%">Document Name</th>' +
                    '</tr>' +
                    '</thead>' +
                    '<tbody></tbody>' +
                    '</table>' +
                    '</div>',
                size: 'large',
                buttons: {
                    cancel: {
                        label: 'Cancel',
                        className: 'btn-default'
                    },
                    save: {
                        label: 'Save',
                        className: 'btn-success',
                        callback: function() {

                            var arr = [];

                            $("#refundReqDocsTbl tbody").find('input[name="record"]').each(function () {
                                if ($(this).is(":checked")) {
                                    arr.push($(this).attr("id"));
                                }
                            });

                            if (arr.length == 0) {
                                Swal.fire({
                                    title: 'Error',
                                    text: 'No Documents Selected to attach..',
                                    icon: 'error'
                                });
                                return false;
                            }


                            var $currForm = $('#refund-docs-form');
                            var currValidator = $currForm.validate();
                            if (!$currForm.valid()) {
                                return false;
                            }


                            var data = {};
                            $currForm.serializeArray().map(function (x) {
                                data[x.name] = x.value;
                            });

                            var url = SERVLET_CONTEXT + "/protected/uw/refunds/createRefundRiskDocs";
                            data.requiredDocs = arr;

                            $.ajax({
                                url: url,
                                type: "POST",
                                data: JSON.stringify(data),
                                success: function (s) {
                                    Swal.fire({
                                        title: 'Success',
                                        text: 'Records Created Successfully',
                                        icon: 'success'
                                    });

                                    $('#refund_required_docs_tbl').DataTable().ajax.reload();
                                    dialog.modal('hide');
                                    arr = [];
                                },
                                error: function (jqXHR, textStatus, errorThrown) {
                                    console.log(jqXHR);
                                    Swal.fire({
                                        title: 'Error',
                                        text: jqXHR.responseText,
                                        icon: 'error'
                                    });
                                },
                                dataType: "json",
                                contentType: "application/json"
                            });

                            return true;
                        }
                    }
                }
            });
            dialog.on('shown.bs.modal', function() {
                loadAvailableRefundDocuments(riskId);
                $(document).off('click', '#searchRefundDocuments').on('click', '#searchRefundDocuments', function() {
                    var searchTerm = $('#refund-doc-name-search').val();
                    loadAvailableRefundDocuments(riskId, searchTerm);
                });
            });
        }

        function getRefundRequiredDocs(riskId) {

            var url = SERVLET_CONTEXT +"/protected/uw/policies/riskRefundDocs/" + riskId;

            var currTable = $('#refund_required_docs_tbl').DataTable(UTILITIES.extendsOpts({
                "ajaxUrl": url,
                "columns": [
                    {
                        "data": "rfdRequiredId",
                        "render": function (data, type, full, meta) {
                            return full.docShtDesc
                        }
                    },
                    {
                        "data": null,
                        "render": function(data, type, full, meta) {
                            return full.uploadedFileName || full.docDesc || "-";
                        }
                    },

                    {
                        "data": "userReceived",
                        "render": function (data, type, full, meta) {
                            if (full.username)
                                return full.username;
                            else return "";
                        }
                    },
                    {
                        "data": "dateReceived",
                        "render": function (data, type, full, meta) {
                            if (full.uploadedDate) {
                                return moment(full.uploadedDate).format('YYYY-MM-DD HH:mm:ss');
                            } else return "";
                        }
                    },
                    {
                        "data": "remarks"
                    },
                    {
                        "data": "rfdRequiredId",
                        "render": function (data, type, full, meta) {
                            if (full.dateReceived)
                                return '<button type="button" class="btn btn-success btn-sm" disabled>Upload</button>';
                            else

                                return '<button type="button" class="btn btn-success btn-sm" data-docs=' + encodeURI(JSON.stringify(full)) + ' onclick="UWScreen.editRiskDocs(this);" data-toggle="tooltip" data-placement="top" title="Upload Document">Upload</button>';
                        }
                    },
                    {
                        "data": "rfdRequiredId",
                        "render": function (data, type, full, meta) {
                            return '<button type="button" class="btn btn-success btn btn-info btn-sm" type="button" data-docs=' + encodeURI(JSON.stringify(full)) + ' onclick="UWScreen.downloadRefundDoc(this);">View</button>';
                        }
                    },
                    {
                        "data": "rdId",
                        "render": function (data, type, full, meta) {
                            return '<button type="button" class="btn btn-danger btn-sm" data-docs="' +
                                encodeURI(JSON.stringify(full)) +
                                '" onclick="UWScreen.deleteRiskDoc(this);"><i class="fa fa-trash-o"></i></button>';
                        }
                    }
                ]
            }));

            window.refreshRefundRequiredDocsTable = function() {
                currTable.ajax.reload(null, false);
            };

            return currTable;
        }




        function loadAvailableRefundDocuments(riskId, searchTerm) {
            console.log("Loading docs for riskId:", riskId);
            $.ajax({
                type: 'GET',
                url: SERVLET_CONTEXT + "/protected/uw/policies/getriskreqdocs",
                dataType: 'json',
                data: {
                    riskId: riskId,
                    docName: searchTerm || ""
                },
                success: function(result) {

                    $("#refundReqDocsTbl tbody").empty();

                    if (result.length === 0) {
                        $('#refundReqDocsTbl tbody').append('<tr><td colspan="3" class="text-center">No documents found</td></tr>');
                        return;
                    }

                    $.each(result, function(index, doc) {

                        let sclReqrdId = doc.sclReqrdId;
                        let reqShtDesc = doc.reqShtDesc;
                        let reqDesc = doc.reqDesc;

                        let rowWithConcat = '<tr>' +
                            '<td><input type="checkbox" name="record" id="' + sclReqrdId + '" /></td>' +
                            '<td>' + reqShtDesc + '</td>' +
                            '<td>' + reqDesc + '</td>' +
                            '</tr>';



                        // TEST: Template literal
                        let rowWithTemplate = `<tr>
                    <td><input type="checkbox" name="record" id="${sclReqrdId}" /></td>
                    <td>${reqShtDesc}</td>
                    <td>${reqDesc}</td>
                </tr>`;

                        $('#refundReqDocsTbl tbody').append(rowWithConcat);
                    });

                },
                error: function(xhr) {
                    Swal.fire({
                        title: 'Error loading documents',
                        text: xhr.responseText,
                        icon: 'error'
                    });
                }
            });
        }


        function processRefunds(refundsToProcess, refundComments) {
            var transactionNumbers = refundsToProcess.map(function(refund) {
                return refund.transno;
            });

            $.ajax({
                url: SERVLET_CONTEXT + "/protected/uw/refunds/processRefunds",
                type: "POST",
                contentType: "application/json",
                data: JSON.stringify({
                    transactionNumbers: transactionNumbers,
                    refundComments: refundComments
                }),
                success: function(response) {
                    if (response.success) {
                        bootbox.alert({
                            title: "Success",
                            message: "Refunds processed successfully!<br><br>" +
                                response.message + "<br><br>" +
                                "The refunds have been submitted for approval.",
                            callback: function() {
                                // Clear selections and refresh table
                                selectedRefunds = [];
                                $('#select-all-refunds').prop('checked', false);
                                if (refundsTable) {
                                    refundsTable.ajax.reload();
                                }
                            }
                        });
                    } else {
                        var errorMessage = "Failed to process refunds:<br>" + (response.message || "Unknown error occurred.");


                        if (response.errors && response.errors.length > 0) {
                            errorMessage += "<br><br><strong>Details:</strong><br>";
                            response.errors.forEach(function(error) {
                                errorMessage +=  error + "<br>";
                            });
                        }

                        bootbox.alert({
                            title: "Error",
                            message: errorMessage
                        });
                    }
                },
                error: function(xhr, status, error) {
                    var errorMessage = "Failed to process refunds.";

                    if (xhr.responseJSON && xhr.responseJSON.message) {
                        errorMessage += "<br>" + xhr.responseJSON.message;
                    } else if (xhr.responseText) {
                        errorMessage += "<br>" + xhr.responseText;
                    }

                    bootbox.alert({
                        title: "Error",
                        message: errorMessage
                    });
                }
            });
        }
        var uploadRiskDocument = function () {
            var $form = $("#risk-doc-form");
            var validator = $form.validate();

            $('form#risk-doc-form').submit(function (e) {
                e.preventDefault();
                if (!$form.valid()) {
                    return;
                }

                var $submitButton = $('#upload-doc-btn');
                var originalText = $submitButton.text();
                $submitButton.prop('disabled', true).text('Uploading...');

                var data = new FormData(this);
                data.append('file', $('#avatar')[0].files[0]);

                // Show spinner
                $('#upload-spinner').show();

                $.ajax({
                    url: SERVLET_CONTEXT +'/protected/uw/refunds/uploadRefundDocs',
                    type: 'POST',
                    data: data,
                    processData: false,
                    contentType: false,
                    xhr: function () {
                        return new window.XMLHttpRequest(); // No progress tracking needed
                    },
                    success: function (s) {
                        Swal.fire({
                            title: 'Success',
                            text: 'File Uploaded Successfully',
                            icon: 'success'
                        });

                        $('#riskdocModal').modal('hide');
                        var $el = $('#avatar');
                        $el.wrap('<form>').closest('form').get(0).reset();
                        $el.unwrap();
                        $('#refund_required_docs_tbl').DataTable().ajax.reload();
                    },
                    error: function (xhr, error) {
                        Swal.fire({
                            title: 'Error',
                            text: xhr.responseText,
                            icon: 'error'
                        });
                    },
                    complete: function () {
                        // Reset UI
                        $submitButton.prop('disabled', false).text(originalText);
                        $('#upload-spinner').hide();
                    }
                });
            });
        };

        $(document).on('shown.bs.modal', '#riskdocModal', function() {
            uploadRiskDocument();
        });


        $(document).on('hidden.bs.modal', '#riskdocModal', function() {
            $('#risk-doc-form').off('submit').validate().destroy();
        });
        function submitRefundUpload() {
            var fileInput = $('#refund-doc-file')[0];
            var file = fileInput.files[0];

            if (!file) {
                alert("Please select a file to upload.");
                return;
            }

            var formData = new FormData();
            formData.append('file', file);
            formData.append('docRefNo', $('#refund-doc-ref-no').val());
            formData.append('remarks', $('#refund-doc-remarks').val());
            formData.append('rfdRequiredId', $('#upload-refund-req-code').val());

            $.ajax({
                url: 'uploadRefundDocument',
                type: 'POST',
                data: formData,
                processData: false,
                contentType: false,
                success: function(response) {
                    $('#uploadRefundReqModal').modal('hide');
                    Swal.fire({
                        title: 'Success',
                        text: 'Document uploaded successfully',
                        icon: 'success'
                    });
                    $('#refund_required_docs_tbl').DataTable().ajax.reload();
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

        // Initialize Select2 for Product
        if($("#prd-code").filter("div").html() != undefined) {
            Select2Builder.initAjaxSelect2({
                containerId: "prd-code",
                sort: 'proDesc',
                change: function(e, a, v) {
                    $("#product-search-number").val(e.added ? e.added.proCode : '');
                },
                formatResult: function(a) {
                    return a.proDesc;
                },
                formatSelection: function(a) {
                    return a.proDesc;
                },
                initSelection: function(element, callback) {},
                id: "proCode",
                placeholder: "Select Product"
            });
        }

        // Initialize Select2 for Client
        if($("#client-frm").filter("div").html() != undefined) {
            Select2Builder.initAjaxSelect2({
                containerId: "client-frm",
                sort: 'fname',
                change: function(e, a, v) {
                    $("#rev-search-name").val(e.added ? e.added.tenId : '');
                    console.log("Selected Client ID:", e.added ? e.added.tenId : '');
                },
                formatResult: function(a) {
                    return a.fname + " " + a.otherNames;
                },
                formatSelection: function(a) {
                    return a.fname + " " + a.otherNames;
                },
                initSelection: function(element, callback) {},
                id: "tenId",
                placeholder: "Select Client"
            });
        }

        // Handle search button click
        $("#btn-search-refunds").on('click', function() {
            var clientCode = $("#rev-search-name").val();
            var prodCode = $("#product-search-number").val();

            // Allow search with no parameters (will show all refunds)
            if(!refundsTable) {
                refundsTable = initRefundsTable();
            } else {
                refundsTable.ajax.reload();
            }
        });

        // Handle view refund button clicks
        $(document).on('click', '.view-refund', function() {
            var transNo = $(this).data('trans-no');
            console.log('Viewing refund transaction:', transNo);
            // Add your view logic here
            bootbox.alert("View functionality for transaction " + transNo + " - implement as needed");
        });

        // Initialize table on page load
        initRefundsTable();
    });
</script>