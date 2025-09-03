$(function () {
    $(document).ready(function () {
        importData();
        getUnProcessedTransactions();
    });
});

// function importData() {
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
//             url: 'uploadembedretrench',
//             type: 'POST',
//             data: data,
//             processData: false,
//             contentType: false,
//             success: function (response) {
//                 console.log("Response msg" +JSON.stringify(response));
//                 const successfulPolicies = response.successfulPolicies || 0;
//                 const successfulRisks = response.successfulRisks || 0;
//                 const failedRecords = response.failedRecords || 0;
//                 const invalidRecords = response.invalidRecords || [];
//                 let title = '';
//                 let message = '';
//
//                 if (failedRecords === 0 && successfulPolicies > 0 && successfulRisks > 0) {
//                     title = 'Success';
//                     message = 'Data uploaded successfully.';
//                 }
//                 else if (failedRecords > 0 && successfulPolicies === 0 && successfulRisks === 0) {
//                     title = 'Error';
//                     message = 'No record has been saved.\n\n';
//                     message += invalidRecords.join('\n');
//                 }
//                 else if (failedRecords > 0 && successfulPolicies > 0 && successfulRisks > 0) {
//                     title = 'Partial Success';
//                     message = 'Data uploaded successfully with some errors.\n\n';
//                     message += `Uploaded Policy Details: ${successfulPolicies}\n\n`;
//                     message += `Uploaded Premium Items: ${successfulRisks}\n\n`;
//                     message += `This number of records were not saved (${failedRecords}):\n\n`;
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
//                     icon: failedRecords === 0 ? 'success' : 'warning',
//                     customClass: {
//                         popup: 'swal-wide'
//                     }
//                 });
//
//                 if (successfulPolicies > 0 && successfulRisks > 0) {
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

function importData() {
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
            url: 'uploadembedretrench',
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
                const successfulPolicies = response.successfulPolicies || 0;
                const successfulRisks = response.successfulRisks || 0;
                const failedRecords = response.failedRecords || 0;
                const invalidRecords = response.invalidRecords || [];
                let title = '';
                let message = '';

                if (failedRecords === 0 && successfulPolicies > 0 && successfulRisks > 0) {
                    title = 'Success';
                    message = 'Data uploaded successfully.';
                } else if (failedRecords > 0 && successfulPolicies === 0 && successfulRisks === 0) {
                    title = 'Error';
                    message = 'No record has been saved.\n\n';
                    message += invalidRecords.join('\n');
                } else if (failedRecords > 0 && successfulPolicies > 0 && successfulRisks > 0) {
                    title = 'Partial Success';
                    message = 'Data uploaded successfully with some errors.\n\n';
                    message += `Uploaded Policy Details: ${successfulPolicies}\n\n`;
                    message += `Uploaded Premium Items: ${successfulRisks}\n\n`;
                    message += `This number of records were not saved (${failedRecords}):\n\n`;
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
                    icon: failedRecords === 0 ? 'success' : 'warning',
                    customClass: {
                        popup: 'swal-wide'
                    }
                });

                if (successfulPolicies > 0 && successfulRisks > 0) {
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

function getUnProcessedTransactions() {
    var url = "unprocessembedretrench";
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
                "data": "embedPackageId",
                "render": function (data, type, full, meta) {
                    var checked = checkedRows[data] ? 'checked' : '';
                    return `<input type="checkbox" class="rowCheckbox" data-id="${data}" ${checked}>`;
                }
            },
            {"data": "coverType"},
            {"data": "productName"},
            {"data": "insurerName"},
            {
                "data": null,
                "render": function (data, type, full, meta) {
                    return (full.clientFName || '') + ' ' + (full.clientOtherNames || '');
                }
            },
            {
                "data": "coverFrom",
                "render": function (data, type, full, meta) {
                    if (full.coverFrom) {
                        return moment(full.coverFrom).format('DD/MM/YYYY');
                    } else return "No Date";
                }
            },
            {
                "data": "coverTo",
                "render": function (data, type, full, meta) {
                    if (full.coverTo) {
                        return moment(full.coverTo).format('DD/MM/YYYY');
                    } else return "No Date";
                }
            },
            {
                "data": "uploadedDate",
                "render": function (data, type, full, meta) {
                    if (full.uploadedDate) {
                        return moment(full.uploadedDate).format('DD/MM/YYYY');
                    } else return "No Date";
                }
            },
            {
                "data": "embedPackageId",
                "render": function (data, type, full, meta) {
                    return `<button type="button" class="btn btn-primary btn-sm" data-trans=${encodeURI(JSON.stringify(full))} value="Process" onclick="processTrans(this);">Process</button>`;
                }
            }
        ],
        "rowCallback": function (row, data) {
            var checkbox = $(row).find('.rowCheckbox');
            checkbox.prop('checked', checkedRows[data.embedPackageId] || false);
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
                bulkProcessTransactions(checkedTransactions, currTable);
            });
            $('#bulk-delete-trans').on('click', function () {
                var checkedTransactions = Object.keys(checkedRows).filter(function (id) {
                    return checkedRows[id] === true;
                });
                bulkDeleteTransactions(checkedTransactions, currTable);
            });

            $('#btn-download-template').on('click', function () {
                var checkedTransactions = Object.keys(checkedRows).filter(function (id) {
                    return checkedRows[id] === true;
                });
                bulkDownloadTemplate();
            });
        }
    });
    return currTable;
}


function processTrans(button) {
    var trans = JSON.parse(decodeURI($(button).data("trans")));
    bootbox.confirm("Are you sure want to process this transaction?", function (result) {
        console.log("Results "+result);
        if (result) {
            $('#uploadedTransTable').find('button').prop('disabled', true);

            $('#bulk-process-trans').prop('disabled', true);
            $('#bulk-delete-trans').prop('disabled', true);

            Swal.fire({
                title: 'Processing...',
                text: 'Please wait while we process the transactions.',
                allowOutsideClick: false,
                allowEscapeKey: false,
                didOpen: () => {
                    Swal.showLoading();
                }
            });

            $.ajax({
                type: 'POST',
                url: 'processsinglretrench/' + trans['embedPackageId'],
                dataType: 'json',
                async: true,
                success: function (result) {
                    Swal.fire({
                        title: 'Success',
                        text: 'Transaction Processed Successfully',
                        icon: 'success'
                    });
                    $('#uploadedTransTable').DataTable().ajax.reload();
                },
                error: function (jqXHR, textStatus, errorThrown) {
                    Swal.fire({
                        title: 'Error',
                        text: jqXHR.responseText,
                        icon: 'error'
                    });
                },
                complete: function () {
                    $('#uploadedTransTable').find('button').prop('disabled', false);
                    $('#bulk-process-trans').prop('disabled', false);
                    $('#bulk-delete-trans').prop('disabled', false);
                }
            });
        }

    });
}

function bulkProcessTransactions(transactionIds, tableInstance) {
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
            $('#uploadedTransTable').find('button').prop('disabled', true);
            $('#bulk-delete-trans').prop('disabled', true);
            $('#bulk-process-trans').prop('disabled', true);

            Swal.fire({
                title: 'Processing...',
                text: 'Please wait while we process the transactions.',
                allowOutsideClick: false,
                allowEscapeKey: false,
                didOpen: () => {
                    Swal.showLoading();
                }
            });

            $.ajax({
                url: 'processbulkretrench',
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
                    $('#uploadedTransTable').find('button').prop('disabled', false);
                    $('#bulk-process-trans').prop('disabled', false);
                    $('#bulk-delete-trans').prop('disabled', false);
                }
            });
        }
    });
}

function bulkDeleteTransactions(transactionIds, tableInstance) {
    if (transactionIds.length === 0){
        Swal.fire({
            title: 'Error',
            text: 'Select At least One Transaction To Process',
            icon: 'error'
        });
        return;
    }
    bootbox.confirm("Are you sure you want to delete selected transactions?", function (result) {
        if(result) {
            $('#uploadedTransTable').find('button').prop('disabled', true);
            $('#bulk-delete-trans').prop('disabled', true);
            $('#bulk-process-trans').prop('disabled', true);
            Swal.fire({
                title: 'Processing...',
                text: 'Please wait while we delete the transactions.',
                allowOutsideClick: false,
                allowEscapeKey: false,
                didOpen: () => {
                    Swal.showLoading();
                }
            });

            $.ajax({
                url: 'deletebulkretrench',
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
                    $('#uploadedTransTable').find('button').prop('disabled', false);
                    $('#bulk-delete-trans').prop('disabled', false);
                    $('#bulk-process-trans').prop('disabled', false);
                }
            });
        }
    });
}

var checkedRows = {};

