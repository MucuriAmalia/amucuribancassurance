<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="sec" uri="http://www.springframework.org/security/tags" %>

<script type="text/javascript" src="<c:url value="/js/modules/utils/select2builder.js"/>"></script>
<script type="text/javascript" src="<c:url value="/libs/rivets/rivets.js"/>"></script>

<div class="x_panel">
    <div class="x_title">
        <h2><i class="fa fa-bars"></i> Reconciliation</h2>
        <ul class="nav navbar-right panel_toolbox">
            <li><a class="collapse-link"><i class="fa fa-chevron-up"></i> </a></li>
        </ul>
        <div class="clearfix"></div>
    </div>
    <div class="x_content">
        <form id="search-form" class="form-horizontal">
            <div class="item form-group">

                <div class="col-md-6 col-xs-12">
                    <label for="from-date" class="col-md-5 label-align">Date
                        From</label>

                    <div class="col-md-7 col-xs-12">
                        <div class='input-group date datepicker-input' id="wef-date">
                            <input type='text' class="form-control float-right" name="wefDate"
                                   id="from-date" required/>
                            <div class="input-group-addon">
                                <span class="fa fa-calendar"></span>
                            </div>
                        </div>
                    </div>
                </div>
                <div class="col-md-6 col-xs-12">
                    <label for="wet-date" class="col-md-5 label-align">Date To
                    </label>

                    <div class="col-md-7 col-xs-12">
                        <div class='input-group date datepicker-input' id="cover-to-date">
                            <input type='text' class="form-control float-right" name="wetDate"
                                   id="wet-date" required/>
                            <div class="input-group-addon">
                                <span class="fa fa-calendar"></span>
                            </div>
                        </div>
                    </div>
                </div>
            </div>
            <div class="item form-group">
                <div class="col-md-6 col-xs-12">
                    <label for="acc-frm" class="col-md-5 label-align">Intermediary
                    </label>

                    <div class="col-md-7 col-xs-12">
                        <input type='hidden' class="form-control float-right"
                               id="agent-search-number" name="accountCode"/>
                        <div id="acc-frm" class="form-control"
                             select2-url="<c:url value="/protected/setups/binders/selAgentsAccounts"/>" >
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
        <div class="clearfix"></div>
    </div>
    <div class="x_panel">
        <div class="x_title">
            <h2>Reconciliation Data</h2>
            <ul class="nav navbar-right panel_toolbox">
                <li><a class="collapse-link"><i class="fa fa-chevron-up"></i> </a></li>
            </ul>
            <div class="clearfix"></div>
        </div>
        <div class="x_content">
            <div class="card-box table-responsive">
                <button class="btn btn-primary btn btn-primary" id="btn-import-data">Import Data</button>
                <a href="bulkreconciletemplate" class="btn btn-success">
                    <i class="fa fa-file-excel-o"></i> Download Template
                </a>
                <table id="reconciliationTable" class="table table-striped" style="width: 100%">
                    <thead>
                    <tr>
                        <th><input type="checkbox" id="selectAll"></th> <!-- Checkbox column -->
                        <th>Policy</th>
                        <th>Client</th>
                        <th>Risk Note Number</th>
                        <th>Underwriter Policy Number</th>
                        <th>Premium</th>
                        <th>Commission</th>
                        <th>Amount Payable</th>
                    </tr>
                    </thead>
                    <tbody>
                    </tbody>
                </table>
            </div>
            <button id="reconcileButton" class="btn btn-primary float-right">Reconcile</button>
        </div>
    </div>

    <div class="x_panel">
        <div class="x_title">
            <h2>Reconciled Data</h2>
            <ul class="nav navbar-right panel_toolbox">
                <li><a class="collapse-link"><i class="fa fa-chevron-up"></i> </a></li>
            </ul>
            <div class="clearfix"></div>
        </div>
        <button class="btn btn-primary float-right" id="btn-print-report">Print Report</button>

        <div class="x_content">
            <div class="card-box table-responsive">
                <table id="reconciliation_child" class="table table-striped" style="width: 100%">
                    <thead>
                    <tr>
                        <th>Policy</th>
                        <th>Client</th>
                        <th>Risk Note Number</th>
                        <th>Underwriter Policy Number</th>
                        <th>Premium</th>
                        <th>Commission</th>
                        <th>Amount Payable</th>
                        <th>Status</th>
                        <%--                        <th>Unmatched Entries</th>--%>
                    </tr>
                    </thead>
                    <tbody>
                    </tbody>
                </table>
            </div>
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


<script type="text/javascript">
    var uploadUrl = '<c:url value="/protected/reconciliation/uploadExcel"/>';
    var dataUrl = '<c:url value="/protected/reconciliation/reconciliationData"/>';

    $(document).ready(function () {
        $(".datepicker-input").each(function () {
            $(this).datetimepicker({
                format: 'DD/MM/YYYY'
            });

        });
        createAccountsForSel();
        importData();

        $('#reconcileButton').prop('disabled', true);
        $('#btn-print-report').prop('disabled', true);

        $('#selectAll').on('click', function () {
            var isChecked = $(this).prop('checked');
            $('.row-checkbox').prop('checked', isChecked);
        });

        $('#btn-search-trans').on('click', function () {
            if (!$("#from-date").val()) {
                bootbox.alert("Select Date From");
                return;
            }
            if (!$("#wet-date").val()) {
                bootbox.alert("Select Date To");
                return;
            }
            if (!$("#agent-search-number").val()) {
                bootbox.alert("Select Intermediary");
                return;
            }
            var reconDataTable = getReconciliationData($("#agent-search-number").val(), $("#from-date").val(), $("#wet-date").val());
            var reconTable = getReconciledData($("#agent-search-number").val(), $("#from-date").val(), $("#wet-date").val());

            reconDataTable.on('draw', function () {
                var data = reconDataTable.rows().data().length;
                $('#reconcileButton').prop('disabled', data === 0);
            });

            reconTable.on('draw', function () {
                var data = reconTable.rows().data().length;
                $('#btn-print-report').prop('disabled', data === 0);
            })

        });
    });

    function getReconciliationData(accountCode, dateFrom, dateTo) {
        if ($.fn.DataTable.isDataTable('#reconciliationTable')) {
            $('#reconciliationTable').DataTable().clear().destroy();
        }
        var table = $('#reconciliationTable').DataTable({
            processing: true,
            serverSide: true,
            searching: false,
            lengthMenu: [5, 10, 25, 50],
            pageLength: 10,
            "ajax": {
                "url": dataUrl,
                "data": {accountCode: accountCode, dateFrom: dateFrom, dateTo: dateTo}
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
                {"data": "clientName"},
                {"data": "riskNoteNumber"},
                {"data": "underwriterPolicyNumber"},
                {"data": "premium"},
                {"data": "commission"},
                {"data": "payableCommission"},
            ],
            "order": [[1, 'asc']]
        });
        reconcileData(table)
        return table;
    }


    function importData() {
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
                        $('#reconciliationTable').DataTable().ajax.reload();
                        $("#btn-import-data").prop('disabled', false);

                    },
                    error: function (jqXHR, textStatus, errorThrown) {
                        Swal.fire({
                            title: 'Error',
                            text: jqXHR.responseText,
                            icon: 'error'
                        });
                    }
                });
            });
    }


    function reconcileData(table) {
        $('#reconcileButton').click(function () {
            var selectedRows = [];
            // Collect selected rows (based on checkboxes)
            $('#reconciliationTable tbody input.row-checkbox:checked').each(function () {
                var rowData = table.row($(this).closest('tr')).data();
                selectedRows.push({
                    policyNumber: rowData.policyNumber,
                    clientName: rowData.clientName,
                    riskNoteNumber: rowData.riskNoteNumber,
                    underwriterPolicyNumber: rowData.underwriterPolicyNumber,
                    premium: rowData.premium,
                    payableCommission: rowData.payableCommission,
                    paidCommission: rowData.paidCommission,
                    commission: rowData.commission
                });
                console.log(selectedRows);

            });

            if (selectedRows.length === 0) {
                bootbox.alert('No Record Selected to Reconcile.');
                return;
            }


            // Disable the button and show loading state
            $('#reconcileButton').prop('disabled', true).text('Reconciling...');

            $.ajax({
                url: 'reconcile',
                type: 'POST',
                contentType: 'application/json',
                data: JSON.stringify(selectedRows),  // Send the selected rows as JSON
                success: function (response) {
                    Swal.fire({
                        title: 'Success',
                        text: 'Data Reconciled Successfully',
                        icon: 'success'
                    });
                    $('#reconcileButton').prop('disabled', false).text('Reconcile');
                    $('#reconciliationTable').DataTable().ajax.reload();
                    $('#reconciliation_child').DataTable().ajax.reload();


                },
                error: function (jqXHR, textStatus, errorThrown) {
                    Swal.fire({
                        title: 'Error',
                        text: jqXHR.responseText,
                        icon: 'error'
                    });
                    $('#reconcileButton').prop('disabled', false).text('Reconcile');
                }
            });
        });

    }

    // function getReconciledData(accountCode,dateFrom,dateTo) {
    //     if ($.fn.DataTable.isDataTable('#reconciliation_child')) {
    //         $('#reconciliation_child').DataTable().clear().destroy();
    //     }
    //     var table = $('#reconciliation_child').DataTable({
    //         processing: true,
    //         serverSide: true,
    //         searching: false,
    //         lengthMenu: [5, 10, 25, 50],
    //         pageLength: 10,
    //         "ajax": {
    //             "url": "reconciledData",
    //             "data":  { accountCode:accountCode, dateFrom:dateFrom,dateTo:dateTo}
    //         },
    //         "columns": [
    //             {"data": "policyNumber"},
    //             {"data": "clientName"},
    //             {"data": "premium"},
    //             {"data": "commission"},
    //             {"data": "payableCommission"},
    //             {"data": "status"}
    //             // {"data": "unmatchedEntries"}
    //         ],
    //         "order": [[1, 'asc']]
    //     });
    //     return table;
    // }


    var accountCodeStored, dateFromStored, dateToStored;

    function getReconciledData(accountCode, dateFrom, dateTo) {
        // Store the parameters globally for use in the print function
        accountCodeStored = accountCode;
        dateFromStored = dateFrom;
        dateToStored = dateTo;

        if ($.fn.DataTable.isDataTable('#reconciliation_child')) {
            $('#reconciliation_child').DataTable().clear().destroy();
        }

        var table = $('#reconciliation_child').DataTable({
            processing: true,
            serverSide: true,
            searching: false,
            lengthMenu: [5, 10, 25, 50],
            pageLength: 10,
            "ajax": {
                "url": "reconciledData",
                "data": {accountCode: accountCode, dateFrom: dateFrom, dateTo: dateTo}
            },
            "columns": [
                {"data": "policyNumber"},
                {"data": "clientName"},
                {"data": "riskNoteNumber"},
                {"data": "underwriterPolicyNumber"},
                {"data": "premium"},
                {"data": "commission"},
                {"data": "payableCommission"},
                {"data": "status"}
            ],
            "order": [[1, 'asc']]
        });

        return table;
    }

    $(document).ready(function () {
        $('#btn-print-report').click(function () {
            // Use the globally stored values to call the report
            window.open('rpt_reconciliation.pdf?accountCode=' + accountCodeStored +
                '&dateFrom=' + dateFromStored +
                '&dateTo=' + dateToStored, '_blank');
        });
    });


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
                placeholder: "Select Intermediary",
            });
        }

        $("#acc-frm").on("select2-removed", function (e) {
            $("#agent-search-number").val('');
        })
    }

</script>
