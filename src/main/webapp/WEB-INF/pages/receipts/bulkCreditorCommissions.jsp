<%--
  Created by IntelliJ IDEA.
  User: joanrunyiri
  Date: 18/06/2025
  Time: 13:29
  To change this template use File | Settings | File Templates.
--%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<script type="text/javascript"
        src="<c:url value="/js/modules/utils/select2builder.js"/>"></script>
<div class="x_panel">
    <div class="x_title">
        <h2>Process Bulk Commissions</h2>
        <ul class="nav navbar-right panel_toolbox">
            <li><a class="collapse-link"><i class="fa fa-chevron-up"></i> </a></li>
        </ul>
        <div class="clearfix"></div>
    </div>
    <form id="credits-form" class="form-horizontal">

        <div class="item form-group">
            <div class="col-md-6 col-xs-12">
                <label for="brn-id" class="col-md-5 label-align">Insurance Company
                </label>

                <div class="col-md-7 col-xs-12">
                    <input type='hidden' class="form-control float-right"
                           id="agent-search-number" name="accountCode" />
                    <div id="acc-frm" class="form-control"
                         select2-url="<c:url value="/protected/setups/binders/selAgentsAccounts"/>" >
                    </div>
                </div>
            </div>
            <div class="col-md-6 col-xs-12">
                <label for="brn-id" class="col-md-5 label-align">Currency
                </label>

                <div class="col-md-7 col-xs-12">
                    <input type='hidden' class="form-control float-right"
                           id="cur-id" name="curCode" />
                    <div id="curr-frm" class="form-control"
                         select2-url="<c:url value="/protected/uw/policies/uwcurrencies"/>" >
                    </div>
                </div>
            </div>
        </div>
        <div class="item form-group">
            <input type="button" class="btn btn-primary float-right"
                   style="margin-right: 10px;" value="Search"
                   id="btn-search-trans">
        </div>
    </form>
    <hr>
    <div class="x_content">
        <div class="card-box table-responsive">
            <button class="btn btn-primary btn btn-primary" id="btn-import-data">Import Data</button>
            <a href="bulkreconciletemplate" class="btn btn-success">
                <i class="fa fa-file-excel-o"></i> Download Template
            </a>
            <table id="reconciliationTable" class="table table-striped" style="width: 100%">
                <thead>
                <tr>
                    <th><input type="checkbox" id="selectAll"></th>
                    <th>InsureMaster Policy No</th>
                    <th>Underwriter Policy No</th>
                    <th>Debit Ref No</th>
                    <th>Credit Ref No</th>
                    <th>Reference No</th>
                    <th>Underwriter Trans Code</th>
                    <th>Gross Premiums</th>
                    <th>Gross Commission</th>
                    <th>Gross Admin Fee</th>
                    <th>Gross Revenue</th>
                    <th>Comm WHTX</th>
                    <th>Admin WHTX</th>
                    <th>Net Revenue</th>
                    <th></th>
                </tr>
                </thead>
                <tbody>
                </tbody>
            </table>
        </div>

        <button id="reconcileButton" class="btn btn-primary float-right">Process</button>
        <button id="deleteButton" class="btn btn-danger float-right">Delete</button>
    </div>
</div>

<div class="x_panel">
    <div class="x_title">
        <h2>Transaction Details</h2>
        <ul class="nav navbar-right panel_toolbox">
            <li><a class="collapse-link"><i class="fa fa-chevron-up"></i> </a></li>
        </ul>
        <div class="clearfix"></div>
    </div>
    <div class="x_content">
        <ul class="nav nav-tabs" role="tablist">
            <li class="nav-item">
                <a class="nav-link active" data-toggle="tab" href="#transactions" role="tab">Transactions</a>
            </li>
            <li class="nav-item">
                <a class="nav-link" data-toggle="tab" href="#error-log" role="tab">Error Log</a>
            </li>
        </ul>

        <div class="tab-content">
            <div class="tab-pane" id="error-log" role="tabpanel">

                <div class="card-box table-responsive">
                    <table id="reconciliation_child" class="table table-striped" style="width: 100%">
                        <thead>
                        <tr>
                            <th><input type="checkbox" id="select-all"></th>
                            <th>InsureMaster Policy No</th>
                            <th>Client</th>
                            <th>DR. No.</th>
                            <th>CR. No.</th>
                            <th>Reference Number</th>
                            <th>Payment</th>
                            <%--                    <th>Premium</th>--%>
                            <th>Commission</th>
                            <th>WHTX</th>
                            <th>Payable Amt</th>
                            <th>Error Details</th>
                        </tr>
                        </thead>
                        <tbody>
                        </tbody>
                    </table>
                </div>
            </div>

            <div class="tab-pane active" id="transactions" role="tabpanel">
                <div class="card-box table-responsive">
                    <table id="instrans-tbl" class="table table-striped" style="width:100%">
                        <thead>
                        <tr>
                            <th><input type="checkbox" id="instrans-selectAll" data-select-all="false"></th>
                            <th>Trans Date</th>
                            <th>Insurance ID</th>
                            <th>Intermediary</th>
                            <th>Account</th>
                            <th>Amount</th>
                            <th>Remittance No</th>
                            <th></th>
                            <th></th>
                            <th></th>
                        </tr>
                        </thead>
                    </table>
                    <div class="row mb-3">
                        <div class="col-md-12">
                            <button id="btn-approve-all" class="btn btn-success">
                                <i class="fa fa-check"></i> Approve Selected
                            </button>
                            <button id="btn-reject-all" class="btn btn-danger">
                                <i class="fa fa-times"></i> Reject Selected
                            </button>
                        </div>
                    </div>
                </div>
            </div>
        </div>
    </div>
</div>

<div class="x_panel">
    <div class="x_title">
        <h2>Transaction Log</h2>
        <ul class="nav navbar-right panel_toolbox">
            <li><a class="collapse-link"><i class="fa fa-chevron-up"></i></a>
            </li>
        </ul>
        <div class="clearfix"></div>
    </div>
    <div class="x_content">
        <div class="card-box table-responsive">
            <table id="payment-trans-tbl" class="table table-striped" style="width:100%">
                <thead>
                <tr>
                    <th>Reference No</th>
                    <th>Policy No</th>
                    <th>Client Name</th>
                    <th>Client Account</th>
                    <th>Product</th>
                    <th>Amount</th>
                    <th>Commission</th>
                    <th>WHTX</th>
                    <th></th>
                </tr>
                </thead>
            </table>
        </div>
    </div>
</div>
<div class="modal fade" id="importDataModal" tabindex="-1" role="dialog"
     aria-labelledby="importDataModalLabel" aria-hidden="true">
    <form id="data-upload-form" class="form-horizontal" enctype="multipart/form-data">
        <div class="modal-dialog modal-md">
            <div class="modal-content">
                <div class="modal-header">
                    <h4 class="modal-title" id="importRisksModalLabel">Import Data</h4>
                    <button type="button" class="close" data-dismiss="modal"
                            aria-label="Close">
                        <span aria-hidden="true">&times;</span>
                    </button>

                </div>
                <div class="modal-body">

                    <div class="item form-group">
                        <label for="file-avatar" class="label-align col-md-5">
                            Excel File<span class="required">*</span></label>
                        <div class="col-md-7 col-xs-12">
                            <input name="file" type="file" id="file-avatar" required>
                        </div>
                    </div>
                    <div class="modal-footer">
                        <input type="submit" class="btn btn-success" style="margin-right: 10px;" value="Upload">
                        <button type="button" class="btn btn-default" data-dismiss="modal">
                            Close
                        </button>
                    </div>
                </div>
            </div>
        </div>
    </form>
</div>
<script>
    var uploadUrl = '<c:url value="/protected/accounts/uploadExcel"/>';
    var dataUrl = '<c:url value="/protected/accounts/reconciliationCommissionData"/>';

    $(document).ready(function () {


        $(".datepicker-input").each(function () {
            $(this).datetimepicker({
                format: 'DD/MM/YYYY'
            });
        });

        $("#btn-search-trans").on('click', function(){
            loadCommissionData($('#agent-search-number').val());
            insuranceTrans();
            $("#total-val").val(0);
            $(".total").val(0);

            loadErrorLogData($('#agent-search-number').val());
            // findCommissionsAudits(-2000);
        });


        // $('#reconcileButton').prop('disabled', true);

        // Import functionality
        $("#btn-import-data").on('click', function () {
            $('#importDataModal').modal({
                backdrop: 'static',
                keyboard: true
            });
        });

        var $form = $("#data-upload-form");
        var validator = $form.validate();
        $('form#data-upload-form')
            .submit(function (e) {
                e.preventDefault();
                if (!$form.valid()) {
                    return;
                }
                $("#btn-import-data").prop('disabled', true);

                var data = new FormData(this);
                data.append('file', $('#file-avatar')[0].files[0]);
                data.append('acctId', $('#agent-search-number').val());
                $.ajax({
                    url: 'uploadExcel',
                    type: 'POST',
                    data: data,
                    processData: false,
                    contentType: false,
                    success: function (s) {
                        $('#importDataModal').modal('hide');
                        Swal.fire({
                            title: 'Success',
                            text: 'Data Uploaded Successfully',
                            icon: 'success'
                        });
                        // Reload table if it exists
                        if ($.fn.DataTable.isDataTable('#reconciliationTable')) {
                            $('#reconciliationTable').DataTable().ajax.reload();
                        }
                        $("#btn-import-data").prop('disabled', false);

                        // Load commission data after successful import
                        loadCommissionData(
                            $("#agent-search-number").val(),
                            $("#from-date").val(),
                            $("#wet-date").val()
                        );
                    },
                    error: function (jqXHR, textStatus, errorThrown) {
                        Swal.fire({
                            title: 'Error',
                            text: jqXHR.responseText,
                            icon: 'error'
                        });
                        $("#btn-import-data").prop('disabled', false);
                    }
                });
            });

        loadCommissionData($('#agent-search-number').val(),  $("#from-date").val(),
            $("#wet-date").val());

        // // ADD THIS: Connect search button to load commission data
        // $("#btn-search-trans").on('click', function() {
        //     // Validate form fields
        //     if (!$("#from-date").val()) {
        //         bootbox.alert("Select Date From");
        //         return;
        //     }
        //     if (!$("#wet-date").val()) {
        //         bootbox.alert("Select Date To");
        //         return;
        //     }
        //     if (!$("#agent-search-number").val()) {
        //         bootbox.alert("Select Insurance Company");
        //         return;
        //     }
        //
        //     // Call function to populate commission reconciliation table
        //
        // });

        // Initialize account and currency dropdowns
        createAccountsForSel();
        populateCurrencyLov();
    });

    function loadCommissionData(accountCode, dateFrom, dateTo) {
        if ($.fn.DataTable.isDataTable('#reconciliationTable')) {
            $('#reconciliationTable').DataTable().clear().destroy();
        }

        // Build data object only with non-null values
        var ajaxData = {};
        if (accountCode !== null && accountCode !== undefined && accountCode !== '') {
            ajaxData.accountCode = accountCode;
        }
        if (dateFrom !== null && dateFrom !== undefined && dateFrom !== '') {
            ajaxData.dateFrom = dateFrom;
        }
        if (dateTo !== null && dateTo !== undefined && dateTo !== '') {
            ajaxData.dateTo = dateTo;
        }

        var table = $('#reconciliationTable').DataTable({
            processing: true,
            serverSide: true,
            searching: false,
            lengthMenu: [5, 10, 25, 50],
            pageLength: 10,
            "ajax": {
                "url": 'reconciliationCommissionData',
                "data": ajaxData  // Only send parameters that have actual values
            },
            "columns": [
                {
                    "data": null,
                    "className": 'dt-body-center',
                    "orderable": false,
                    "render": function (data, type, row) {
                        return '<input type="checkbox" class="row-checkbox" />';
                    }
                },
                {"data": "policyNumber"},
                {"data": "underWriterPolicyNo"},
                {"data": "debitRefNo"},
                {"data": "creditRefNo"},
                {"data": "revisionNo"},
                {"data": "transCode"},
                {
                    "data" : "payment",
                    "render" : function(data, type, full, meta) {
                        return UTILITIES.currencyFormat(full.payment);
                    }
                },
                {
                    "data" : "commission",
                    "render" : function(data, type, full, meta) {
                        return UTILITIES.currencyFormat(full.commission);
                    }
                },
                {
                    "data" : "adminFee",
                    "render" : function(data, type, full, meta) {
                        return UTILITIES.currencyFormat(full.adminFee);
                    }
                },
                {
                    "data" : "totalRevenue",
                    "render" : function(data, type, full, meta) {
                        return UTILITIES.currencyFormat(full.totalRevenue);
                    }
                },
                {
                    "data" : "withholdingTax",
                    "render" : function(data, type, full, meta) {
                        return UTILITIES.currencyFormat(full.withholdingTax);
                    }
                },
                {
                    "data" : "adminFeeWhtx",
                    "render" : function(data, type, full, meta) {
                        return UTILITIES.currencyFormat(full.adminFeeWhtx);
                    }
                },
                {
                    "data" : "payableCommission",
                    "render" : function(data, type, full, meta) {
                        return UTILITIES.currencyFormat(full.payableCommission);
                    }
                },
                {
                    "data": null,
                    "className": 'dt-body-center',
                    "orderable": false,
                    "render": function (data, type, row) {
                        return ''; // Empty column
                    }
                }
            ],
            "order": [[1, 'asc']]
        });

        $('#reconcileButton').prop('disabled', false);
        $('#selectAll').on('click', function(){
            var rows = table.rows({ 'page': 'current' }).nodes();
            $('input[type="checkbox"].row-checkbox', rows).prop('checked', this.checked);
        });
        reconcileCommissionData(table);
        return table;
    }
    function loadErrorLogData(agentCode) {
        if ($.fn.DataTable.isDataTable('#reconciliation_child')) {
            $('#reconciliation_child').DataTable().clear().destroy();
        }

        var ajaxData = {};
        if (agentCode !== null && agentCode !== undefined && agentCode !== '') {
            ajaxData.accountCode = agentCode;
        }
        console.log(ajaxData);
        var errorTable = $('#reconciliation_child').DataTable({
            processing: true,
            serverSide: true,
            searching: true,
            lengthMenu: [5, 10, 25, 50],
            pageLength: 10,
            "ajax": {
                "url": 'unreconciledCommissionData',
                "data": ajaxData
            },
            "columns": [
                {
                    "data": null,
                    "className": 'dt-body-center',
                    "orderable": false,
                    "render": function (data, type, row) {
                        console.log('Row Data:', row);
                        return '<input type="checkbox" class="error-row-checkbox" />';
                    }

                },
                {"data": "policyNumber"},
                {"data": "clientName"},
                {"data": "debitRefNo"},
                {"data": "creditRefNo"},
                {"data": "revisionNo"},
                {"data": "payment"},
                {"data": "commission"},
                {"data": "withholdingTax"},
                {"data": "payableCommission"},
                {
                    "data": "unreconciledReason",
                    "className": 'dt-body-left',
                    "orderable": false,
                    "render": function (data, type, row) {
                        return '<span class="text-danger" title="' + data + '">' +
                            (data.length > 50 ? data.substring(0, 50) + '...' : data) + '</span>';
                    }
                }
            ],
            "order": [[1, 'desc']],
            "rowCallback": function(row, data) {
                // Highlight error rows
                $(row).addClass('table-danger');
            }
        });

        return errorTable;
    }

    // Update the reconciliation success handler
    function reconcileCommissionData(table) {
        //$('#reconcileButton').click(function () {
        $('#reconcileButton').off('click').on('click', function () {
            console.log("Reconcile clicked!");
            var selectedRows = [];
            console.log(selectedRows);
            // Collect selected rows (based on checkboxes)
            $('#reconciliationTable tbody input.row-checkbox:checked').each(function () {
                var row = $(this).closest('tr');
                var rowData = $('#reconciliationTable').DataTable().row(row).data();
                console.log(rowData);
                selectedRows.push({
                    policyNumber: rowData.policyNumber,
                    clientName: rowData.clientName,
                    debitRefNo: rowData.debitRefNo,
                    creditRefNo: rowData.creditRefNo,
                    revisionNo: rowData.revisionNo,
                    payment: rowData.payment,
                    commission: rowData.commission,
                    withholdingTax: rowData.withholdingTax,
                    payableCommission: rowData.payableCommission,
                    adminFee: rowData.adminFee,
                    adminFeeWhtx: rowData.adminFeeWhtx,
                    underwriterCode:$("#agent-search-number").val(),
                    currencyCode: $("#cur-id").val()
                });
            });

            if (selectedRows.length === 0) {
                Swal.fire({
                    title: 'Error',
                    text: 'No Record Selected to Reconcile.',
                    icon: 'error'
                })
                return;
            }



            $('#reconcileButton').prop('disabled', true).text('Processing...');

            $.ajax({
                url: 'reconcileCommission',
                type: 'POST',
                contentType: 'application/json',
                data: JSON.stringify(selectedRows),
                success: function (response) {
                    Swal.fire({
                        title: 'Success',
                        text: 'Commission Data Reconciled Successfully',
                        icon: 'success'
                    });
                    $('#reconcileButton').prop('disabled', false).text('Process');

                    // Reload both tables
                    $('#reconciliationTable').DataTable().ajax.reload();

                    // Reload error log to show new unreconciled items
                    if ($.fn.DataTable.isDataTable('#reconciliation_child')) {
                        $('#reconciliation_child').DataTable().ajax.reload();
                    } else {
                        loadErrorLogData($('#agent-search-number').val());
                    }
                    insuranceTrans();

                    // Show the error log tab if there are new errors
                    $('a[href="#transactions"]').tab('show');
                },
                error: function (jqXHR, textStatus, errorThrown) {
                    Swal.fire({
                        title: 'Error',
                        text: jqXHR.responseText,
                        icon: 'error'
                    });
                    $('#reconcileButton').prop('disabled', false).text('Process');
                }
            });
        });
        $("#deleteButton").click(function (){
            Swal.fire({
                title: 'Are you sure to delete?',
                text: "You won't be able to revert this!",
                icon: 'warning',
                showCancelButton: true,
                confirmButtonColor: '#d33',
                cancelButtonColor: '#3085d6',
                confirmButtonText: 'Yes, delete it!',
                cancelButtonText: 'Cancel'
            }).then((result) => {
                if (result.isConfirmed) {
                    var selectedRws = [];

                    // Collect selected rows (based on checkboxes)
                    $('#reconciliationTable tbody input.row-checkbox:checked').each(function () {
                        var rowData = table.row($(this).closest('tr')).data();
                        selectedRws.push({
                            reconId: rowData.reconId
                        });
                    });

                    if (selectedRws.length === 0) {
                        Swal.fire({
                            title: 'Error',
                            text: 'No Record Selected to Delete.',
                            icon: 'error'
                        })
                        return;
                    }

                    $('#deleteButton').prop('disabled', true).text('Deleting...');

                    $.ajax({
                        url: 'deleteCommissions',
                        type: 'POST',
                        contentType: 'application/json',
                        data: JSON.stringify(selectedRws),
                        success: function (response) {
                            Swal.fire({
                                title: 'Success',
                                text: 'Record Deleted Successfully',
                                icon: 'success'
                            });
                            $('#deleteButton').prop('disabled', false).text('Delete');

                            // Reload both tables
                            $('#reconciliationTable').DataTable().ajax.reload();
                        },
                        error: function (jqXHR, textStatus, errorThrown) {
                            Swal.fire({
                                title: 'Error',
                                text: jqXHR.responseText,
                                icon: 'error'
                            });
                            $('#deleteButton').prop('disabled', false).text('Delete');
                        }
                    });

                }
            });
        });
    }

    function createAccountsForSel() {
        if ($("#acc-frm").filter("div").html() != undefined) {
            Select2Builder.initAjaxSelect2({
                containerId: "acc-frm",
                sort: 'name',
                change: function (e, a, v) {
                    $("#agent-search-number").val(e.added.acctId);
                },
                formatResult: function (a) {
                    return a.name
                },
                formatSelection: function (a) {
                    return a.name
                },
                initSelection: function (element, callback) {

                },
                id: "acctId",
                placeholder: "Select Insurance Company",
            });
        }

        $("#acc-frm").on("select2-removed", function (e) {
            $("#agent-search-number").val('');
        })
    }

    // Separate function to handle select all binding for instrans table
    function bindInstransSelectAll() {
        // Remove any existing handlers to prevent duplicates
        $('#instrans-selectAll').off('click.selectall');

        // Bind the select all functionality specifically to the header checkbox in instrans-tbl
        $('#instrans-selectAll').on('click.selectall', function() {
            var isChecked = $(this).prop('checked');
            console.log('Header checkbox clicked, checked:', isChecked);

            // Select/deselect all checkboxes in the table body
            $('#instrans-tbl tbody input.instrans-row-checkbox').prop('checked', isChecked);

            // Log for debugging
            var affectedCount = $('#instrans-tbl tbody input.instrans-row-checkbox').length;
            console.log('Affected checkboxes:', affectedCount);
        });

        // Handle individual checkbox changes
        $('#instrans-tbl tbody').off('change.instrans').on('change.instrans', 'input.instrans-row-checkbox', function() {
            var totalCheckboxes = $('#instrans-tbl tbody input.instrans-row-checkbox').length;
            var checkedCheckboxes = $('#instrans-tbl tbody input.instrans-row-checkbox:checked').length;

            var headerCheckbox = $('#instrans-selectAll');

            if (checkedCheckboxes === 0) {
                headerCheckbox.prop('checked', false).prop('indeterminate', false);
            } else if (checkedCheckboxes === totalCheckboxes) {
                headerCheckbox.prop('checked', true).prop('indeterminate', false);
            } else {
                headerCheckbox.prop('checked', false).prop('indeterminate', true);
            }
        });
    }

    function insuranceTrans() {
        $(document).ajaxStart(function () {
            $("#instrans-tbl").attr("disabled", true);
        });
        $(document).ajaxComplete(function () {
            $("#instrans-tbl").attr("disabled", false);
        });

        // Destroy existing table if it exists
        if ($.fn.DataTable.isDataTable('#instrans-tbl')) {
            $('#instrans-tbl').DataTable().destroy();
        }

        var url = "bulkCommTrans";
        var currTable = $('#instrans-tbl').DataTable({
            "processing" : true,
            "serverSide" : true,
            "ajax": {
                'url': url,
                'data': {
                    'currCode': $("#cur-id").val(),
                    'agentCode': $("#agent-search-number").val(),
                }
            },
            "order": [[1, "desc"]],
            lengthMenu : [ [ 20, 30, 40, 50 ], [ 20, 30, 40, 50 ] ],
            pageLength : 2000,
            "dom": "rt",
            "scrollY": "200px",
            scrollCollapse: true,
            destroy : true,
            "columnDefs": [
                { "width": 200, "targets":2 }
            ],
            "columns" : [
                {
                    "data": null,
                    "className": 'dt-body-center',
                    "orderable": false,
                    "render": function (data, type, row) {
                        return '<input type="checkbox" class="instrans-row-checkbox" value="' + row.transno + '">';
                    }
                },
                { "data": "transDate" ,
                    "render": function ( data, type, full, meta ) {
                        return moment(full.transDate).format('DD/MM/YYYY');
                    }
                },
                {
                    "data" : "agent",
                    "render" : function(data, type, full, meta) {
                        return full.agent.shtDesc;
                    }
                },
                {
                    "data" : "agent",
                    "render" : function(data, type, full, meta) {
                        return full.agent.name;
                    }
                },
                {
                    "data" : "controlAcc",
                    "render" : function(data, type, full, meta) {
                        return full.controlAcc;
                    }
                },
                {
                    "data" : "netAmount",
                    "render" : function(data, type, full, meta) {
                        return UTILITIES.currencyFormat(full.netAmount);
                    }
                },
                {
                    "data" : "refNo",
                    "render" : function(data, type, full, meta) {
                        return full.refNo;
                    }
                },
                {
                    "data": "transno",
                    "render": function(data, type, full, meta) {
                        const printUrl = `${SERVLET_CONTEXT}/protected/accounts/rpt_creditorCommissions.pdf?transno=${full.transno}`;
                        return `<a href="${printUrl}" class="btn btn-primary btn-sm" target="_blank">Print</a>`;
                    }
                },
                {
                    "data" : "transno",
                    "render" : function(data, type, full, meta) {
                        return '<button type="button" class="btn btn-success btn btn-info btn-sm" type="button" data-audits='+encodeURI(JSON.stringify(full)) + '  onclick="approveTrans(this);">Approve</button>';

                    }
                },
                {
                    "data" : "transno",
                    "render" : function(data, type, full, meta) {
                        return '<button type="button" class="btn btn-danger btn btn-info btn-sm" type="button" data-audits='+encodeURI(JSON.stringify(full)) + '  onclick="cancelTrans(this);">Reject</button>';

                    }
                },
            ],
            "drawCallback": function() {
                // Re-bind select all functionality after each draw/redraw
                bindInstransSelectAll();
            }
        });

        // Initial binding
        bindInstransSelectAll();

        // Bulk Approve button
        $('#btn-approve-all').off('click').on('click', function() {
            bulkAction('approve');
        });

        // Bulk Reject button
        $('#btn-reject-all').off('click').on('click', function() {
            bulkAction('reject');
        });

        $('#instrans-tbl tbody').off('click', 'tr').on('click', 'tr', function () {
            $(this).addClass('table-primary').siblings().removeClass('table-primary');
            var aData = currTable.rows('.table-primary').data();
            console.log(aData);
            if (aData[0] === undefined || aData[0] === null) {
            }
            else{

                findCommissionsAudits(aData[0].transno);
            }
        } );

        return currTable;
    }

    function findCommissionsAudits(transNo) {
        console.log("Sending transNo: " + transNo);
        var url = SERVLET_CONTEXT + "/protected/accounts/creditorcommaudits";
        var currTable = $('#payment-trans-tbl').DataTable({
            "processing": true,
            "serverSide": false,
            "ajax": {
                'url': url,
                'data': {
                    'transNo': transNo
                },
                'dataSrc': 'data',
                'dataType': 'json',
                'contentType': 'application/json'
            },
            "columns": [
                { "data": "refNo" },
                { "data": "clientPolNo" },
                {
                    "data": null,
                    "render": function(data, type, full) {
                        return full.fname + " " + full.otherNames;
                    }
                },
                { "data": "controlAcc" },
                {
                    "data": null,
                    "render": function(data, type, full) {
                        return full.proDesc;
                    }
                },
                {
                    "data": "paymentAmount",
                    "render": function(data, type, full) {
                        return UTILITIES.currencyFormat(data);
                    }
                },
                {
                    "data": "commAmount",
                    "render": function(data, type, full) {
                        return UTILITIES.currencyFormat(data);
                    }
                },
                {
                    "data": "whtxAmount",
                    "render": function(data, type, full) {
                        return UTILITIES.currencyFormat(data);
                    }
                },
                // {
                //     "data": null,
                //     "render": function(data, type, full) {
                //         return '<button type="button" class="btn btn-success btn btn-info btn-sm" onclick="undoAudits(this);">Undo</button>';
                //     }
                // }
            ]
        });

        return currTable;
    }

    function populateCurrencyLov() {
        if ($("#curr-frm").filter("div").html() != undefined) {
            Select2Builder.initAjaxSelect2({
                containerId: "curr-frm",
                sort: 'curName',
                change: function (e, a, v) {
                    $("#cur-id").val(e.added.curCode);
                },
                formatResult: function (a) {
                    return a.curName;
                },
                formatSelection: function (a) {
                    return a.curName;
                },
                initSelection: function (element, callback) {

                },
                id: "curCode",
                width: "250px",
                placeholder: "Select Currency"
            });
        }
    }


    function undoAudits(button){
        var audits = JSON.parse(decodeURI($(button).data("audits")));
        bootbox.confirm("Undo Selected Transaction?", function(result) {
            if (result) {
                $.ajax({
                    type: 'GET',
                    url:  'deletePaymentAudit/' + audits['paId'],
                    dataType: 'json',
                    async: true,
                    success: function(result) {
                        Swal.fire({
                            title: 'Success',
                            text: 'Operation Successfully',
                            icon: 'success'
                        });
                        creditorPaymentTrans();
                        insuranceTrans();
                        // $('#credits-tbl').DataTable().ajax.reload();
                        $("#total-val").val(0);
                        $(".total").val(0);
                        $('#payment-trans-tbl').DataTable().ajax.reload();
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


    function cancelTrans(button){
        var audits = JSON.parse(decodeURI($(button).data("audits")));
        bootbox.confirm("Reject Selected Transaction?", function(result) {
            if (result) {
                $.ajax({
                    type: 'GET',
                    url:  'deleteInsuranceTrans/' + audits['transno'],
                    dataType: 'json',
                    async: true,
                    success: function(result) {
                        Swal.fire({
                            title: 'Success',
                            text: 'Transaction Rejected Successfully',
                            icon: 'success'
                        });
                        // creditorPaymentTrans();
                        // insuranceTrans();
                        // $('#credits-tbl').DataTable().ajax.reload();
                        $("#total-val").val(0);
                        $(".total").val(0);
                        $('#payment-trans-tbl').DataTable().ajax.reload();
                        $('#instrans-tbl').DataTable().ajax.reload();
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

    function approveTrans(button) {
        var audits = JSON.parse(decodeURI($(button).data("audits")));
        bootbox.confirm("Approve Selected Transaction?", function(result) {
            if (result) {
                $.ajax({
                    type: 'GET',
                    url: 'approveCreditorComm/' + audits['transno'], // Assuming 'transno' is the transaction ID
                    dataType: 'json',
                    async: true,
                    success: function(result) {
                        Swal.fire({
                            title: 'Success',
                            text: 'Transaction Approved Successfully',
                            icon: 'success'
                        });

                        $('#instrans-tbl').DataTable().ajax.reload(); // Reload the table to reflect the changes
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


    function bulkAction(action) {
        var selectedTransNos = [];
        var totalAmount = 0;

        // Get the current DataTable instance
        var table = $('#instrans-tbl').DataTable();

        $('#instrans-tbl tbody input.instrans-row-checkbox:checked').each(function() {
            var transNo = $(this).val();
            selectedTransNos.push(transNo);

            // Find the corresponding row data and add to total amount
            var rowData = table.row($(this).closest('tr')).data();
            if (rowData && rowData.netAmount) {
                totalAmount += parseFloat(rowData.netAmount);
            }
        });

        if (selectedTransNos.length === 0) {
            Swal.fire({
                title: 'Error',
                text: 'Please select at least one transaction',
                icon: 'error'
            });
            return;
        }

        var endpoint, successMessage;

        if (action === 'approve') {
            endpoint = 'approvebULKCreditorComm';
            successMessage = 'approved';
        } else {
            endpoint = 'bulkDeleteInsuranceTrans';
            successMessage = 'rejected';
        }

        // Format the total amount using your utility function
        var formattedAmount = UTILITIES.currencyFormat(totalAmount);

        Swal.fire({
            title: 'Confirm ' + action,
            text: 'Are you sure you want to ' + action + ' ' + selectedTransNos.length +
                ' transaction(s) with a total amount of ' + formattedAmount + '?',
            icon: 'warning',
            showCancelButton: true,
            confirmButtonColor: '#3085d6',
            cancelButtonColor: '#d33',
            confirmButtonText: 'Yes, ' + action + ' them!'
        }).then((result) => {
            if (result.isConfirmed) {
                // Convert string IDs to numbers for your endpoint
                var numericIds = selectedTransNos.map(id => Number(id));

                $.ajax({
                    url: endpoint,
                    type: 'POST',
                    contentType: 'application/json',
                    data: JSON.stringify(numericIds),
                    dataType: 'json',
                    success: function(response) {
                        Swal.fire({
                            title: 'Success',
                            text: selectedTransNos.length + ' transactions ' + successMessage + ' successfully',
                            icon: 'success'
                        });
                        $('#instrans-tbl').DataTable().ajax.reload();
                        $('#payment-trans-tbl').DataTable().ajax.reload();
                        $('#instrans-selectAll').prop('checked', false);
                    },
                    error: function(jqXHR) {
                        Swal.fire({
                            title: 'Error',
                            text: jqXHR.responseText || 'An error occurred',
                            icon: 'error'
                        });
                    }
                });
            }
        });
    }

    // New helper function
    function processBulkAction(action, transNos) {
        var actionText = action === 'approve' ? 'approve' : 'reject';
        var endpoint = action === 'approve' ? 'approvebULKCreditorComm' : 'rejectbULKCreditorComm';

        Swal.fire({
            title: 'Confirm ' + actionText,
            text: 'Are you sure you want to ' + actionText + ' ' + transNos.length + ' transaction(s)?',
            icon: 'warning',
            showCancelButton: true,
            confirmButtonColor: '#3085d6',
            cancelButtonColor: '#d33',
            confirmButtonText: 'Yes, ' + actionText + ' them!'
        }).then((result) => {
            if (result.isConfirmed) {
                $.ajax({
                    url: endpoint,
                    type: 'POST',
                    contentType: 'application/json',
                    data: JSON.stringify(transNos),
                    success: function (response) {
                        Swal.fire({
                            title: 'Success',
                            text: transNos.length + ' transactions ' + actionText + 'ed successfully',
                            icon: 'success'
                        });
                        $('#instrans-tbl').DataTable().ajax.reload();
                        $('#payment-trans-tbl').DataTable().ajax.reload();
                        $('#instrans-selectAll').prop('checked', false);
                    },
                    error: function (jqXHR) {
                        Swal.fire({
                            title: 'Error',
                            text: jqXHR.responseText || 'An error occurred',
                            icon: 'error'
                        });
                    }
                });
            }
        });
    }

</script>