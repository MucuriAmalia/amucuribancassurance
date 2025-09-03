$(function () {
    $(document).ready(function () {
        getBulkCreditShieldPolicy();
    });
});

function getBulkCreditShieldPolicy() {
    var url = 'viewBulkRefundsPolicies';
    var currTable = $('#loadedPolicyTable').DataTable({
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
                "data": "bulkPolicyId",
                "render": function (data, type, full, meta) {
                    var checked = checkedRows[data] ? 'checked' : '';
                    return `<input type="checkbox" class="rowCheckbox" data-id="${data}" ${checked}>`;
                }
            },
            {"data": "polNumber"},
            {"data": "polProposalNo"},
            {"data": "clientFname"},
            // {
            //     "data": "bulkPolicyId",
            //     "render": function (data, type, full, meta) {
            //         return `<button type="button" class="btn btn-primary btn-sm" data-trans=${encodeURI(JSON.stringify(full))} value="Process" onclick="processCreditShield(this);">Authorize</button>`;
            //     }
            // }
        ],
        "rowCallback": function (row, data) {
            var checkbox = $(row).find('.rowCheckbox');
            checkbox.prop('checked', checkedRows[data.bulkPolicyId] || false);
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

            $('#loadedPolicyTable tbody').on('change', '.rowCheckbox', function () {
                var id = $(this).data('id');
                checkedRows[id] = $(this).is(':checked');

                var allChecked = $('.rowCheckbox').length === $('.rowCheckbox:checked').length;
                $('#selectAll').prop('checked', allChecked);
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
                url: 'rejectbulkrefundsreceipt',
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
                url: 'authbulkrefundsreceipt',
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

