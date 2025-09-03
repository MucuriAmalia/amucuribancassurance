$(function () {
    $(document).ready(function () {
        importUpdateData();
        getUnProcessedUpdates();
    });
});

// function importUpdateData() {
//     $("#btn-import-data").on('click', function () {
//         $('#importDataModal').modal({
//             backdrop: 'static',
//             keyboard: true
//         });
//     });
//
//     var $form = $("#data-upload-form");
//     var validator = $form.validate();
//
//     $('form#data-upload-form').submit(function (e) {
//         e.preventDefault();
//         if (!$form.valid()) {
//             return;
//         }
//
//         $("#btn-import-data").prop('disabled', true);
//         var data = new FormData(this);
//         data.append('file', $('#file-avatar')[0].files[0]);
//
//         $.ajax({
//             url: 'uploadbulkreceipt',
//             type: 'POST',
//             data: data,
//             processData: false,
//             contentType: false,
//             success: function (response) {
//                 console.log("Response msg" +JSON.stringify(response));
//                 const successfulReceipts = response.successfulReceipts || 0;
//                 const invalidReceipts = response.invalidReceipts || 0;
//                 // const invalidRecords = response.invalidRecords || [];
//                 const invalidRecords = response.invalidRecordDetails || [];
//                 let title = '';
//                 let message = '';
//
//                 if (invalidReceipts === 0 && successfulReceipts > 0 ) {
//                     title = 'Success';
//                     message = 'Data uploaded successfully.';
//                 }
//                 else if (invalidReceipts > 0 && successfulReceipts === 0) {
//                     title = 'Error';
//                     message = 'No record has been saved.\n\n';
//                     // message += invalidRecords.join('\n');
//                     message += invalidRecords.join('\n');
//                 }
//                 else if (invalidReceipts > 0 && successfulReceipts > 0) {
//                     title = 'Partial Success';
//                     message = 'Data uploaded successfully with some errors.\n\n';
//                     message += `Uploaded Policy Details: ${successfulReceipts}\n\n`;
//                     message += `This number of records were not saved (${invalidReceipts}):\n\n`;
//                     // message += invalidRecords.join('\n');
//                     message += invalidRecords.join('\n');
//                 }
//
//                 $('#importDataModal').modal('hide');
//                 $form[0].reset();
//                 $('#file-avatar').val('');
//                 validator.resetForm();
//
//                 Swal.fire({
//                     title: title,
//                     text: message,
//                     icon: invalidReceipts === 0 ? 'success' : 'warning',
//                     customClass: {
//                         popup: 'swal-wide'
//                     }
//                 });
//
//                 if (successfulReceipts > 0 ) {
//                     $('#uploadedTransTable').DataTable().ajax.reload();
//                 }
//                 $("#btn-import-data").prop('disabled', false);
//             },
//             error: function (jqXHR) {
//                 Swal.fire({
//                     title: 'Error',
//                     text: jqXHR.responseText,
//                     icon: 'error'
//                 });
//                 $("#btn-import-data").prop('disabled', false);
//             }
//         });
//     });
// }

function importUpdateData() {
    $("#btn-import-data").on('click', function () {
        $('#importDataModal').modal({
            backdrop: 'static',
            keyboard: true
        });
    });

    var $form = $("#data-upload-form");
    var validator = $form.validate();

    $('form#data-upload-form').submit(function (e) {
        e.preventDefault();
        if (!$form.valid()) {
            return;
        }

        // Disable buttons
        $("#upload-btn, #close-btn, #btn-import-data").prop('disabled', true);
        // Show progress bar
        $("#progress-container").show();
        $("#upload-progress").css('width', '0%').text('0%');

        var data = new FormData(this);
        data.append('file', $('#file-avatar')[0].files[0]);

        $.ajax({
            url: 'uploadbulkreceipt',
            type: 'POST',
            data: data,
            processData: false,
            contentType: false,
            xhr: function() {
                var xhr = new window.XMLHttpRequest();
                xhr.upload.addEventListener('progress', function(evt) {
                    if (evt.lengthComputable) {
                        var percentComplete = Math.round((evt.loaded / evt.total) * 100);
                        $("#upload-progress").css('width', percentComplete + '%').text(percentComplete + '%');
                    }
                }, false);
                return xhr;
            },
            success: function (response) {
                console.log("Response msg" + JSON.stringify(response));
                const successfulReceipts = response.successfulReceipts || 0;
                const invalidReceipts = response.invalidReceipts || 0;
                const invalidRecords = response.invalidRecordDetails || [];
                let title = '';
                let message = '';

                if (invalidReceipts === 0 && successfulReceipts > 0) {
                    title = 'Success';
                    message = 'Data uploaded successfully.';
                } else if (invalidReceipts > 0 && successfulReceipts === 0) {
                    title = 'Error';
                    message = 'No record has been saved.\n\n';
                    message += invalidRecords.join('\n');
                } else if (invalidReceipts > 0 && successfulReceipts > 0) {
                    title = 'Partial Success';
                    message = 'Data uploaded successfully with some errors.\n\n';
                    message += `Uploaded Policy Details: ${successfulReceipts}\n\n`;
                    message += `This number of records were not saved (${invalidReceipts}):\n\n`;
                    message += invalidRecords.join('\n');
                }

                $('#importDataModal').modal('hide');
                $form[0].reset();
                $('#file-avatar').val('');
                validator.resetForm();
                $("#progress-container").hide();

                Swal.fire({
                    title: title,
                    text: message,
                    icon: invalidReceipts === 0 ? 'success' : 'warning',
                    customClass: {
                        popup: 'swal-wide'
                    }
                });

                if (successfulReceipts > 0) {
                    $('#uploadedTransTable').DataTable().ajax.reload();
                }
                // Re-enable buttons
                $("#upload-btn, #close-btn, #btn-import-data").prop('disabled', false);
            },
            error: function (jqXHR) {
                $("#progress-container").hide();
                Swal.fire({
                    title: 'Error',
                    text: jqXHR.responseText,
                    icon: 'error'
                });
                // Re-enable buttons
                $("#upload-btn, #close-btn, #btn-import-data").prop('disabled', false);
            }
        });
    });
}

function getUnProcessedUpdates() {
    var url = "unprocessbulkreceipt";
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
                "data": "receiptBulkId",
                "render": function (data, type, full, meta) {
                    var checked = checkedRows[data] ? 'checked' : '';
                    return `<input type="checkbox" class="rowCheckbox" data-id="${data}" ${checked}>`;
                }
            },
            {"data": "receiptType"},
            {"data": "insurerName"},
            {"data": "branchName"},
            {"data": "receiptAmount"},
            {"data": "paidBy"},
            {"data": "documentDate"},
            {"data": "paymentRef"},
            {"data": "manualRef"},
            {"data": "narration"}
        ],
        "rowCallback": function (row, data) {
            var checkbox = $(row).find('.rowCheckbox');
            checkbox.prop('checked', checkedRows[data.receiptBulkId] || false);
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

            $('#bulk-process-trans').on('click', function () {
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

            $('#bulk-delete-trans').on('click', function () {
                var checkedTransactions = Object.keys(checkedRows).filter(function (id) {
                    return checkedRows[id] === true;
                });
                bulkDeleteUpdates(checkedTransactions, currTable);
            });
            $('#btn-template-download').on('click', function () {
                var checkedTransactions = Object.keys(checkedRows).filter(function (id) {
                    return checkedRows[id] === true;
                });
                bulkTemplateDownload();
            });
        }
    });
    return currTable;
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
            $('#bulk-process-trans').prop('disabled', true);
            $('#bulk-delete-trans').prop('disabled', true);

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
                    $('#bulk-process-trans').prop('disabled', false);
                    $('#bulk-delete-trans').prop('disabled', false);
                }
            });
        }
    });
}

function bulkProcessUpdates(transactionIds, tableInstance) {
    if (transactionIds.length === 0){
        Swal.fire({
            title: 'Error',
            text: 'Select At least One Transaction To Process',
            icon: 'error'
        });
        return;
    }
    bootbox.confirm("Are you sure you want to bulk process selected transactions?", function (result) {
        if(result) {
            $('#bulk-process-trans').prop('disabled', true);
            $('#bulk-delete-trans').prop('disabled', true);
            $('#bulk-auth-receipt-trans').prop('disabled', true);

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
                url: 'approvebulkreceipt',
                type: 'POST',
                contentType: 'application/json',
                data: JSON.stringify(transactionIds.map(Number)),
                success: function (response) {
                    Swal.fire({
                        title: 'Success',
                        text: 'Transactions Processed Successfully',
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
                    $('#bulk-process-trans').prop('disabled', false);
                    $('#bulk-delete-trans').prop('disabled', false);
                }
            });
        }
    });
}


function bulkDeleteUpdates(transactionIds, tableInstance) {
    if (transactionIds.length === 0){
        Swal.fire({
            title: 'Error',
            text: 'Select At least One Transaction To Process',
            icon: 'error'
        });
        return;
    }
    bootbox.confirm("Are you sure you want to bulk process selected transactions?", function (result) {
        if(result) {
            $('#bulk-auth-receipt-trans').prop('disabled', true);
            $('#bulk-process-trans').prop('disabled', true);
            $('#bulk-delete-trans').prop('disabled', true);

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
                url: 'deletebulkreceipt',
                type: 'POST',
                contentType: 'application/json',
                data: JSON.stringify(transactionIds.map(Number)),
                success: function (response) {
                    Swal.fire({
                        title: 'Success',
                        text: 'Transactions Deleted Successfully',
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
                    $('#bulk-process-trans').prop('disabled', false);
                    $('#bulk-delete-trans').prop('disabled', false);
                }
            });
        }
    });
}

var checkedRows = {};

