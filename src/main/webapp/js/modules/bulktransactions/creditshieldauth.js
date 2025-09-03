$(function () {
    $(document).ready(function () {
        getBulkCreditShieldPolicy();
    });
});

function getBulkCreditShieldPolicy() {
    var url = 'viewunprocesscreditshield';
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
                "data": "shieldId",
                "render": function (data, type, full, meta) {
                    var checked = checkedRows[data] ? 'checked' : '';
                    return `<input type="checkbox" class="rowCheckbox" data-id="${data}" ${checked}>`;
                }
            },
            {"data": "coverType"},
            {"data": "polNo"},
            {
                "data": "cardPremium",
                "render": function (data, type, full, meta) {
                    return UTILITIES.currencyFormat(full.cardPremium);
                }
            },
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
                "data": "polWet",
                "render": function (data, type, full, meta) {
                    if (full.coverTo) {
                        return moment(full.coverTo).format('DD/MM/YYYY');
                    } else return "No Date";
                }
            },
            {
                "data": "processedDate",
                "render": function (data, type, full, meta) {
                    if (full.processedDate) {
                        return moment(full.processedDate).format('DD/MM/YYYY');
                    } else return "No Date";
                }
            },
            {"data": "transStatus"},
            // {
            //     "data": "polId",
            //     "render": function(data, type, full, meta) {
            //         return '<form action="edituwtrans" method="post"><input type="hidden" name="id" value=' + full.polId + '><input type="submit" class="btn btn-primary btn-sm" value="view"></form>';
            //     }
            // },
            {
                "data": "shieldId",
                "render": function (data, type, full, meta) {
                    return `<button type="button" class="btn btn-primary btn-sm" data-trans=${encodeURI(JSON.stringify(full))} value="Process" onclick="processCreditShield(this);">Authorize</button>`;
                }
            }
        ],
        "rowCallback": function (row, data) {
            var checkbox = $(row).find('.rowCheckbox');
            checkbox.prop('checked', checkedRows[data.shieldId] || false);
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
                bulkDeleteProcessedCreditShieldAuth(checkedPolicy, currTable);
            });
        }
    });
    return currTable;
}

function processCreditShield(button) {
    var trans = JSON.parse(decodeURI($(button).data("trans")));
    console.log(trans);
    bootbox.confirm("Are you sure want to authorize this policy?", function (result) {
        console.log(result);
        if (result) {
            Swal.fire({
                title: 'Processing...',
                text: 'Please wait while we authorize the transaction.',
                allowOutsideClick: false,
                allowEscapeKey: false,
                didOpen: () => {
                    Swal.showLoading();
                }
            });
            $.ajax({
                type: 'POST',
                url: 'approvesinglercreditshield/' + trans['shieldId'],
                dataType: 'json',
                async: true,
                success: function (result) {
                    Swal.fire({
                        title: 'Success',
                        text: 'Policy Authorized Successfully',
                        icon: 'success'
                    });
                    $('#loadedPolicyTable').DataTable().ajax.reload();
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
        if(result){
            $('#bulk-authorize-policy').prop('disabled', true);
            $('#bulk-delete-policy').prop('disabled', true);
            Swal.fire({
                title: 'Processing...',
                text: 'Please wait while we authorize the transactions.',
                allowOutsideClick: false,
                allowEscapeKey: false,
                didOpen: () => {
                    Swal.showLoading();
                }
            });

            $.ajax({
                url: 'approvebulkcreditshield',
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
                    $('#bulk-authorize-policy').prop('disabled', false);
                    $('#bulk-delete-policy').prop('disabled', false);
                }
            });
        }
    });
}


function bulkDeleteProcessedCreditShieldAuth(policyId, tableInstance) {
    if (policyId.length === 0){
        Swal.fire({
            title: 'Error',
            text: 'Select At least One Policy To Delete',
            icon: 'error'
        });
        return;
    }
    bootbox.confirm("Are you sure want to bulk delete selected policies?", function (result) {
        if (result){
            $('#bulk-delete-policy').prop('disabled', true);
            $('#bulk-authorize-policy').prop('disabled', true);
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
                url: 'deleteprocessedbulkcredShield',
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

