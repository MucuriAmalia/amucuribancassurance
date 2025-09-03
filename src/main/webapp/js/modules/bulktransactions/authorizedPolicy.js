$(function () {
    $(document).ready(function () {
        getUnAuthorizedPolicy();
    });
});

function getUnAuthorizedPolicy() {
    var url = 'unAuthorizedPolicy';
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
                "data": "policyId",
                "render": function (data, type, full, meta) {
                    var checked = checkedRows[data] ? 'checked' : '';
                    return `<input type="checkbox" class="rowCheckbox" data-id="${data}" ${checked}>`;
                }
            },
            {"data": "loadedCoverType"},
            {"data": "polNo"},
            {
                "data": "sumInsured",
                "render": function (data, type, full, meta) {
                    return UTILITIES.currencyFormat(full.sumInsured);
                }
            },
            {
                "data": "basicPrem",
                "render": function (data, type, full, meta) {
                    return UTILITIES.currencyFormat(full.basicPrem);
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
                "data": "dateProcessed",
                "render": function (data, type, full, meta) {
                    if (full.dateProcessed) {
                        return moment(full.dateProcessed).format('DD/MM/YYYY');
                    } else return "No Date";
                }
            },
            {
                "data": "policyId",
                "render": function(data, type, full, meta) {
                    return '<form action="edituwtrans" method="post"><input type="hidden" name="id" value=' + full.policyId + '><input type="submit" class="btn btn-success btn btn-info btn-sm" value="View"></form>';
                }
            },
            {
                "data": "policyId",
                "render": function (data, type, full, meta) {
                    return `<button type="button" class="btn btn-primary btn-sm" data-trans=${encodeURI(JSON.stringify(full))} value="Process" onclick="processTrans(this);">Authorize</button>`;
                }
            }
        ],
        "rowCallback": function (row, data) {
            var checkbox = $(row).find('.rowCheckbox');
            checkbox.prop('checked', checkedRows[data.policyId] || false);
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
                bulkPolicyAuthorization(checkedPolicy, currTable);
            });
        }
    });
    return currTable;
}


function processTrans(button) {
    var trans = JSON.parse(decodeURI($(button).data("trans")));
    bootbox.confirm("Are you sure want to authorize this policy?", function (result) {
        console.log(result);
        if (result) {
            $.ajax({
                type: 'POST',
                url: 'approveSinglePolicy/' + trans['policyId'],
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

function bulkPolicyAuthorization(policyId, tableInstance) {
    if (policyId.length === 0){
        Swal.fire({
            title: 'Error',
            text: 'Select At least One Policy To Authorize',
            icon: 'error'
        });
        return;
    }
    bootbox.confirm("Are you sure want to bulk authorize selected policies?", function (result) {
        $('#bulk-authorize-policy').prop('disabled', true);
        $.ajax({
            url: 'approveBulkpolicies',
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
            }
        });
    });
}

var checkedRows = {};

