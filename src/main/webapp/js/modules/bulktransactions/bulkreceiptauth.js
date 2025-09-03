$(function () {
    $(document).ready(function () {
        getProcessedUpdates();
    });
});

function getProcessedUpdates() {
    var url = "processedbulkreceipt";
    var currTable = $('#uploadedTransTable').DataTable({
        "processing": true,
        "serverSide": true,
        "autoWidth": true,
        "ajax": {
            'url': url,
            'type': 'GET'
        },
        "lengthMenu": [[10, 15, 20, 100, 1000, 100000, 1000000], [10, 15, 20, 100, 1000, 100000, 1000000 ]],
        "pageLength": 10,
        "destroy": true,
        "columns": [
            {
                "data": "taskId",
                "render": function (data, type, full, meta) {
                    var checked = checkedRows[data] ? 'checked' : '';
                    return `<input type="checkbox" class="rowCheckbox" data-id="${data}" ${checked}>`;
                }
            },
            {"data": "taskName"},
            {"data": "policyNumber"},
            {
                "data": "taskId",
                "render": function (data, type, full, meta) {

                    if (full.madeOnDate) {
                        return timeago().format(full.madeOnDate);
                    } else {
                        return "";
                    }
                }
            },
            {"data": "madeBy"},
            // {
            //     "data": "taskId",
            //     "render": function (data, type, full, meta) {
            //         console.log(full)
            //         if (full.taskType === 'QT')
            //             return '<form action="checkerGetQuoteForm" method="get"><input type="hidden" name="id" value=' + full.taskId + '><input type="submit"  class="btn btn-success btn btn-info btn-sm" value="View" ></form>';
            //         else if (full.taskType === 'CL')
            //             return '<form action="checkerGetClientForm" method="get"><input type="hidden" name="id" value=' + full.taskId + '><input type="submit"  class="btn btn-success btn btn-info btn-sm" value="View" ></form>';
            //         else if (full.taskType === 'RC')
            //             return '<form action="<%= request.getContextPath() %>/checkerGetReceiptForm" method="get"><input type="hidden" name="id" value=' + full.taskId + '><input type="submit"  class="btn btn-success btn btn-info btn-sm" value="View" ></form>';
            //         else if (full.taskType === 'LP')
            //             return '<form action="checkerViewPolicy" method="post"><input type="hidden" name="id" value=' + full.taskId + '><input type="submit"  class="btn btn-success btn btn-info btn-sm" value="View" ></form>';
            //         else if (full.taskType === 'ANP' || full.taskType === 'ALP')
            //             return '<form action="edituwtrans" method="post"><input type="hidden" name="id" value=' + full.policyId + '><input type="submit"  class="btn btn-success btn btn-info btn-sm" value="View" ></form>';
            //         else if (full.taskType === 'IM')
            //             return '<form action="editAcctForm" method="post"><input type="hidden" name="id" value=' + full.acctId + '><input type="submit"  class="btn btn-success btn btn-info btn-sm" value="View" ></form>';
            //     }
            //
            // },
        ],
        "rowCallback": function (row, data) {
            var checkbox = $(row).find('.rowCheckbox');
            checkbox.prop('checked', checkedRows[data.taskId] || false);
        },
        "initComplete": function () {

            $('#selectAll').on('change', function () {
                var isChecked = $(this).is(':checked');
                $('.rowCheckbox').each(function () {
                    var id = $(this).data('id');
                    checkedRows[id] = isChecked;
                    $(this).prop('checked', isChecked);
                });
            });

            $('#uploadedTransTable tbody').on('change', '.rowCheckbox', function () {
                var id = $(this).data('id');
                checkedRows[id] = $(this).is(':checked');

                var allChecked = $('.rowCheckbox').length === $('.rowCheckbox:checked').length;
                $('#selectAll').prop('checked', allChecked);
            });

            $('#bulk-auth-reject-receipt-trans').on('click', function () {
                var checkedTransactions = Object.keys(checkedRows).filter(function (id) {
                    return checkedRows[id] === true;
                });
                bulkProcessUpdates(checkedTransactions, currTable);
            });

            $('#bulk-auth-receipt-trans').on('click', function () {
                var checkedTransactions = Object.keys(checkedRows).filter(function (id) {
                    return checkedRows[id] === true;
                });
                bulkProcessReceiptAuths(checkedTransactions, currTable);
            });
            $('#bulk-auth-reject-trans').on('click', function () {
                var checkedTransactions = Object.keys(checkedRows).filter(function (id) {
                    return checkedRows[id] === true;
                });
                bulkRejectTask(checkedTransactions, currTable);
            });

        }
    });
    return currTable;
}

function bulkRejectTask(transactionIds, tableInstance) {
    if (transactionIds.length === 0){
        Swal.fire({
            title: 'Error',
            text: 'Select At least One Transaction To Process',
            icon: 'error'
        });
        return;
    }
    $('#rejectionModal').modal('show');
    $('#rejectConfirmButton').off('click').on('click', function() {
        var reason = $('#rejectionReason').val();
        if (reason) {
            $('#bulk-auth-receipt-trans').prop('disabled', true);
            $('#bulk-auth-reject-trans').prop('disabled', true);
            $('#rejectionModal').modal('hide');

            Swal.fire({
                title: 'Processing...',
                text: 'Please wait.',
                allowOutsideClick: false,
                allowEscapeKey: false,
                didOpen: () => {
                    Swal.showLoading();
                }
            });

            $.ajax({
                url: 'rejectbulkreceipttasks',
                type: 'POST',
                contentType: 'application/json',
                data: JSON.stringify({
                    taskId: transactionIds.map(Number),
                    reason: reason
                }),
                success: function (response) {
                    Swal.fire({
                        title: 'Success',
                        text: 'Receipts Processed Successfully',
                        icon: 'success'
                    });
                    checkedRows = {};
                    $('#selectAll').prop('checked', false);
                    $('.rowCheckbox').prop('checked', false);
                    if (tableInstance) {
                        tableInstance.ajax.reload();
                    }
                },
                error: function (jqXHR, textStatus, errorThrown) {
                    Swal.fire({
                        title: 'Error',
                        text: jqXHR.responseText,
                        icon: 'error'
                    });
                },
                complete: function () {
                    $('#bulk-auth-receipt-trans').prop('disabled', false);
                    $('#bulk-auth-reject-trans').prop('disabled', false);
                }
            });
        }
    });
}


function bulkProcessReceiptAuths(transactionIds, tableInstance) {
    if (transactionIds.length === 0){
        Swal.fire({
            title: 'Error',
            text: 'Select At least One Transaction To Process',
            icon: 'error'
        });
        return;
    }
    bootbox.confirm("Are you sure you want to bulk Authorize selected receipt transactions?", function (result) {
        if(result) {
            $('#bulk-auth-receipt-trans').prop('disabled', true);
            $('#bulk-auth-reject-trans').prop('disabled', true);

            Swal.fire({
                title: 'Processing...',
                text: 'Please wait.',
                allowOutsideClick: false,
                allowEscapeKey: false,
                didOpen: () => {
                    Swal.showLoading();
                }
            });

            $.ajax({
                url: 'approvebulkreceipttasks',
                type: 'POST',
                contentType: 'application/json',
                data: JSON.stringify(transactionIds.map(Number)),
                success: function (response) {
                    Swal.fire({
                        title: 'Success',
                        text: 'Receipts Processed Successfully',
                        icon: 'success'
                    });
                    checkedRows = {};
                    $('#selectAll').prop('checked', false);
                    $('.rowCheckbox').prop('checked', false);
                    if (tableInstance) {
                        tableInstance.ajax.reload();
                    }
                },
                error: function (jqXHR, textStatus, errorThrown) {
                    Swal.fire({
                        title: 'Error',
                        text: jqXHR.responseText,
                        icon: 'error'
                    });
                },
                complete: function () {
                    $('#bulk-auth-receipt-trans').prop('disabled', false);
                    $('#bulk-auth-reject-trans').prop('disabled', false);
                }
            });
        }
    });
}

var checkedRows = {};

