$(function () {
    $(document).ready(function () {
        getBulkCreditShieldPolicy();
    });
});

function getBulkCreditShieldPolicy() {
    var url = 'viewBulkRNPolicies';
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
            {"data": "policyCoverType"},
            {"data": "polNumber"},
            {
                "data": "polBasicPrem",
                "render": function (data, type, full, meta) {
                    return UTILITIES.currencyFormat(full.polBasicPrem);
                }
            },
            {
                "data": null,
                "render": function (data, type, full, meta) {
                    return (full.polClientFname || '') + ' ' + (full.polClientOtherNames || '');
                }
            },
            {
                "data": "polWef",
                "render": function (data, type, full, meta) {
                    if (full.polWef) {
                        return moment(full.polWef).format('DD/MM/YYYY');
                    } else return "No Date";
                }
            },
            {
                "data": "polWet",
                "render": function (data, type, full, meta) {
                    if (full.polWet) {
                        return moment(full.polWet).format('DD/MM/YYYY');
                    } else return "No Date";
                }
            },
            {
                "data": "polCreationDate",
                "render": function (data, type, full, meta) {
                    if (full.polCreationDate) {
                        return moment(full.polCreationDate).format('DD/MM/YYYY');
                    } else return "No Date";
                }
            },
            {"data": "policyStatus"},
            // {
            //     "data": "polId",
            //     "render": function(data, type, full, meta) {
            //         return '<form action="edituwtrans" method="post"><input type="hidden" name="id" value=' + full.polId + '><input type="submit" class="btn btn-primary btn-sm" value="view"></form>';
            //     }
            // },
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

            $('#bulk-authorize-policy').on('click', function () {
                var checkedPolicy = Object.keys(checkedRows).filter(function (id) {
                    return checkedRows[id] === true;
                });
                bulkCreditShieldAuth(checkedPolicy, currTable);
            });

            $('#bulk-delete-policy').on('click', function () {
                var checkedPolicy = Object.keys(checkedRows).filter(function (id) {
                    return checkedRows[id] === true;
                });
                //bulkCreditShieldAuth(checkedPolicy, currTable);
                bulkProcessDelTransactions(checkedPolicy, currTable);
            });
        }
    });
    return currTable;
}

function bulkrejectAuth(policyId, tableInstance) {
    if (policyId.length === 0){
        Swal.fire({
            title: 'Error',
            text: 'Select At least One Policy To Authorize',
            icon: 'error'
        });
        return;
    }
    bootbox.confirm("Are you sure want to bulk reject selected policies?", function (result) {
        if(result) {
            $('#bulk-reject-policy').prop('disabled', true);
            $('#bulk-authorize-policy').prop('disabled', true);
            Swal.fire({
                title: 'Processing...',
                text: 'Please wait',
                allowOutsideClick: false,
                allowEscapeKey: false,
                didOpen: () => {
                    Swal.showLoading();
                }
            });

            $.ajax({
                url: 'bulkauthreject',
                type: 'POST',
                contentType: 'application/json',
                data: JSON.stringify(policyId.map(Number)),
                success: function (response) {
                    Swal.fire({
                        title: 'Success',
                        text: 'Policies Authorized Successfully',
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
                    $('#bulk-reject-policy').prop('disabled', false);
                    $('#bulk-authorize-policy').prop('disabled', false);
                }
            });
        }
    });
}
var undoConvertToPolicy = function() {
    // Fetch rejection reasons when modal is opened
    $.ajax({
        type: 'GET',
        url: SERVLET_CONTEXT + '/protected/life/policies/rejection-reasons',
        dataType: 'json',
        success: function(response) {
            var reasonsHtml = '';
            response.data.forEach(function(reason) {
                reasonsHtml += `
                <div class="custom-control custom-radio mb-2">
                    <input type="radio" class="custom-control-input rejection-reason" 
                           id="reason-${reason.reasonId}" value="${reason.reasonId}" name="rejectionReason">
                    <label class="custom-control-label" for="reason-${reason.reasonId}">
                        ${reason.reasonDesc}
                    </label>
                </div>`;
            });
            $('#rejection-reasons-list').html(reasonsHtml);
            $('#rejectionModal').modal('show');
        },
        error: function(jqXHR) {
            Swal.fire({
                title: 'Error',
                text: 'Failed to load rejection reasons',
                icon: 'error'
            });
        }
    });

    $('#rejectConfirmButton').off('click').on('click', function() {
        var selectedReason = $('input[name="rejectionReason"]:checked').val();
        var comments = $('#rejectionReason').val().trim();

        if (!selectedReason) {
            Swal.fire({
                title: 'Error',
                text: 'Please select a rejection reason',
                icon: 'error'
            });
            return;
        }

        if (!comments) {
            Swal.fire({
                title: 'Error',
                text: 'Please provide additional comments',
                icon: 'error'
            });
            return;
        }

        $.ajax({
            type: 'GET',
            url: SERVLET_CONTEXT + '/protected/life/policies/undoProposalConversion',
            dataType: 'json',
            data: {
                reasonId: selectedReason,
                reason: comments
            },
            async: true,
            success: function(result) {
                Swal.fire({
                    title: 'Success',
                    text: 'Submitted Successfully',
                    icon: 'success'
                }).then(() => {
                    $('#rejectionModal').modal('hide');
                    populatePolicyDetails();
                    $('#polChecksList').DataTable().ajax.reload();
                    window.location.href = SERVLET_CONTEXT + "/protected/home";
                });
            },
            error: function(jqXHR) {
                Swal.fire({
                    title: 'Error',
                    text: jqXHR.responseText,
                    icon: 'error'
                });
            }
        });
    });
};


function bulkCreditShieldAuth(policyId, tableInstance) {
    if (policyId.length === 0){
        Swal.fire({
            title: 'Error',
            text: 'Select At least One Policy To Authorize',
            icon: 'error'
        });
        return;
    }
    bootbox.confirm("Are you sure want to bulk authorize selected policies?", function (result) {
        if(result) {
            $('#bulk-reject-policy').prop('disabled', true);
            $('#bulk-authorize-policy').prop('disabled', true);

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
                url: 'bulkauthrenewals',
                type: 'POST',
                contentType: 'application/json',
                data: JSON.stringify(policyId.map(Number)),
                success: function (response) {
                    Swal.fire({
                        title: 'Success',
                        text: 'Policies Authorized Successfully',
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
                    $('#bulk-reject-policy').prop('disabled', false);
                    $('#bulk-authorize-policy').prop('disabled', false);
                }
            });
        }
    });
}


function bulkProcessDelTransactions(policyId, tableInstance) {
    if (policyId.length === 0){
        Swal.fire({
            title: 'Error',
            text: 'Select At least One Transaction To Delete',
            icon: 'error'
        });
        return;
    }
    bootbox.confirm("Are you sure you want to bulk Delete selected transactions?", function (result) {
        if(result){
            $('#bulk-delete-policy').prop('disabled', true);
            $('#bulk-authorize-policy').prop('disabled', true);
            Swal.fire({
                title: 'Processing...',
                text: 'Please wait while we delete the policies.',
                allowOutsideClick: false,
                allowEscapeKey: false,
                didOpen: () => {
                    Swal.showLoading();
                }
            });
            $.ajax({
                url: 'processbulkdelrnprocessedpolicies',
                type: 'POST',
                contentType: 'application/json',
                data: JSON.stringify(policyId.map(Number)),
                success: function (response) {
                    Swal.fire({
                        title: 'Success',
                        text: 'Policies Deleted Successfully',
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
                    $('#bulk-delete-policy').prop('disabled', false);
                    $('#bulk-authorize-policy').prop('disabled', false);
                }
            });
        }
    });
}

var checkedRows = {};

