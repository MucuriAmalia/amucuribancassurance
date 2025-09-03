$(function () {
    $(document).ready(function () {
        importData();
        getUnProcessedTransactions();
    });
});

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

        $("#btn-import-data").prop('disabled', true);
        var data = new FormData(this);
        data.append('file', $('#file-avatar')[0].files[0]);

        $.ajax({
            url: 'transUploadExcel',
            type: 'POST',
            data: data,
            processData: false,
            contentType: false,
            success: function (response) {
                console.log("Response msg:", JSON.stringify(response)); // Debugging
                console.log("Invalid Records: ", response.invalidRecords); // Debugging

                const successfulPolicies = response.successfulPolicies || 0;
                const successfulRisks = response.successfulRisks || 0;
                const successfulPremiumItems = response.successfulPremiumItems || 0;
                const failedCount = response.failedRecords || 0;
                const invalidRecords = response.invalidRecords || [];

                let title = '';
                let message = '';

                if (failedCount === 0 && successfulPolicies > 0 && successfulRisks > 0 && successfulPremiumItems > 0) {
                    title = 'Success';
                    message = 'Data uploaded successfully.';
                }
                else if (failedCount > 0 && successfulPolicies === 0 && successfulRisks === 0 && successfulPremiumItems === 0) {
                    title = 'Error';
                    message = '<p>No record has been saved.</p>';

                    // Check if there are invalid records
                    if (invalidRecords.length > 0) {
                        message += '<p>Errors encountered:</p><ul>';
                        invalidRecords.forEach(record => {
                            message += `<li>${record}</li>`;
                        });
                        message += '</ul>';
                    } else {
                        message += '<p>No detailed error information available.</p>';
                    }
                }
                else if (failedCount > 0 && (successfulPolicies > 0 || successfulRisks > 0 || successfulPremiumItems > 0)) {
                    title = 'Partial Success';
                    message = `<p>Data uploaded successfully with some errors.</p>
                   <p>Uploaded Policy Details: ${successfulPolicies}</p>
                   <p>Uploaded Risk Details: ${successfulRisks}</p>
                   <p>Uploaded Premium Items: ${successfulPremiumItems}</p>
                   <p>This number of records were not saved: ${failedCount}</p>`;

                    if (invalidRecords.length > 0) {
                        message += '<p>Errors encountered:</p><ul>';
                        invalidRecords.forEach(record => {
                            message += `<li>${record}</li>`;
                        });
                        message += '</ul>';
                    }
                }

                console.log("Final Message: ", message); // Debugging

                $('#importDataModal').modal('hide');
                $('#data-upload-form')[0].reset();
                $('#file-avatar').val('');
                validator.resetForm();

                Swal.fire({
                    title: title,
                    html: message,
                    icon: failedCount === 0 ? 'success' : 'warning',
                    customClass: {
                        popup: 'swal-wide'
                    }
                });

                if (successfulPolicies > 0 || successfulRisks > 0 || successfulPremiumItems > 0) {
                    $('#uploadedTransTable').DataTable().ajax.reload();
                }

                $("#btn-import-data").prop('disabled', false);
            },

            error: function (jqXHR) {
                Swal.fire({
                    title: 'Error',
                    text: jqXHR.responseText,
                    icon: 'error'
                });
                $("#btn-import-data").prop('disabled', false);
            }
        });
    });
}

function getUnProcessedTransactions() {
    var url = "unProcessedTrans";
    var currTable = $('#uploadedTransTable').DataTable({
        "processing": true,
        "serverSide": true,
        "autoWidth": true,
        "ajax": {
            'url': url,
            'type': 'GET'
        },
        "lengthMenu": [[10, 15, 20, 1000, 100000, 1000000], [10, 15, 20, 1000, 100000, 1000000 ]],
        "pageLength": 10,
        "destroy": true,
        "columns": [
            {
                "data": "transProcessingId",
                "render": function (data, type, full, meta) {
                    var checked = checkedRows[data] ? 'checked' : '';
                    return `<input type="checkbox" class="rowCheckbox" data-id="${data}" ${checked}>`;
                }
            },
            {"data": "serialNo"},
            {"data": "polNumber"},
            {"data": "polCode"},
            {
                "data": "coverDateFrom",
                "render": function (data, type, full, meta) {
                    if (full.coverDateFrom) {
                        return moment(full.coverDateFrom).format('DD/MM/YYYY');
                    } else return "No Date";
                }
            },
            {
                "data": "coverDateTo",
                "render": function (data, type, full, meta) {
                    if (full.coverDateTo) {
                        return moment(full.coverDateTo).format('DD/MM/YYYY');
                    } else return "No Date";
                }
            },
            {"data": "currency"},
            {"data": "inceptionDate",
                "render": function(data) {
                    return data ? moment(data).format('DD/MM/YYYY') : "No Date";
                }
            },
            {"data": "proposerCode"},
            {"data": "agentCode"},
            {"data": "transProcessed"},
            {"data": "isRenewable"},
            {"data": "renewDate",
                "render": function(data) {
                    return data ? moment(data).format('DD/MM/YYYY') : "No Date";
                }
            },
            {"data": "clientFname"},
            {"data": "clientOtherNames"},
            {"data": "riskId"},
            {"data": "subclassCode"},
            {"data": "coverTypeCode"},
            {"data": "riskSumAssured"},
            {"data": "authorisedBy" },
            {"data": "authorisedDate",
                "render": function(data) {
                    return data ? moment(data).format('DD/MM/YYYY') : "No Date";
                }
            },
            {"data": "coinsuranceFlag" },
            {"data": "coinsurancePercentage"},
            {"data": "coinsuranceLeaderFlag"},
            {"data": "sectionCode"},
            {"data": "policyInsuredCode"},
            {"data": "riskCode"},
            {
                "data": "transProcessingId",
                "render": function (data, type, full, meta) {
                    return `<button type="button" class="btn btn-primary btn-sm" data-trans=${encodeURI(JSON.stringify(full))} value="Process" onclick="processTrans(this);">Process</button>`;
                }
            },
            // {
            //     "data": "sumInsured",
            //     "render": function (data, type, full, meta) {
            //         return UTILITIES.currencyFormat(full.sumInsured);
            //     }
            // },
            // {
            //     "data": "grossPremium",
            //     "render": function (data, type, full, meta) {
            //         return UTILITIES.currencyFormat(full.grossPremium);
            //     }
            // },
            // {
            //     "data": "coverDateFrom",
            //     "render": function (data, type, full, meta) {
            //         if (full.coverDateFrom) {
            //             return moment(full.coverDateFrom).format('DD/MM/YYYY');
            //         } else return "No Date";
            //     }
            // },
            // {
            //     "data": "coverDateTo",
            //     "render": function (data, type, full, meta) {
            //         if (full.coverDateTo) {
            //             return moment(full.coverDateTo).format('DD/MM/YYYY');
            //         } else return "No Date";
            //     }
            // },
            // {
            //     "data": "dateUploaded",
            //     "render": function (data, type, full, meta) {
            //         if (full.dateUploaded) {
            //             return moment(full.dateUploaded).format('DD/MM/YYYY');
            //         } else return "No Date";
            //     }
            // },
        ],
        "rowCallback": function (row, data) {
            var checkbox = $(row).find('.rowCheckbox');
            checkbox.prop('checked', checkedRows[data.transProcessingId] || false);
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
        }
    });
    return currTable;
}


function processTrans(button) {
    var trans = JSON.parse(decodeURI($(button).data("trans")));
    bootbox.confirm("Are you sure want to process this transaction?", function (result) {
        console.log(result);
        if (result) {
            $.ajax({
                type: 'POST',
                url: 'processSingleTrans/' + trans['transProcessingId'],
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
    bootbox.confirm("Are you sure want to bulk process the transactions?", function (result) {
        $('#bulk-process-trans').prop('disabled', true);
        $.ajax({
            url: 'processBulkTransactions',
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
                $('#bulk-process-trans').prop('disabled', false);
            }
        });
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
    bootbox.confirm("Are you sure want to delete the transactions?", function (result) {
        if(result) {
            $('#bulk-delete-trans').prop('disabled', true);
            $.ajax({
                url: 'deleteBulkTransactions',
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
                    $('#bulk-delete-trans').prop('disabled', false);
                }
            });
        }
    });
}


var checkedRows = {};

