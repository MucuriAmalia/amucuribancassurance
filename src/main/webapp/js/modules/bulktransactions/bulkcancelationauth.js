$(function () {
    $(document).ready(function () {
        getProcessedTransactions();
    });
});

function getProcessedTransactions() {
    var url = "viewBulkCNPolicies";
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
            {"data": "clientFname"},
            {"data": "policyStatus"},
            {
                "data": "sumInsured",
                "render": function (data, type, full, meta) {
                    // Check if data is null or undefined and return an empty value
                    if (data === null || data === undefined) {
                        return '';  // Return an empty string if value is null
                    }
                    // Otherwise, format the number
                    return data.toLocaleString();
                }
            },
            {
                "data": "premium",
                "render": function (data, type, full, meta) {
                    // Check if data is null or undefined and return an empty value
                    if (data === null || data === undefined) {
                        return '';  // Return an empty string if value is null
                    }
                    // Otherwise, format the number
                    return data.toLocaleString();
                }
            },
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
            // {
            //     "data": "bulkPolicyId",
            //     "render": function (data, type, full, meta) {
            //         console.log("Render Function Input:", { data, type, full, meta });
            //         return `<button type="button" class="btn btn-primary btn-sm" data-trans=${encodeURI(JSON.stringify(full))} value="Process" onclick="processTrans(this);">Process</button>`;
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
                var checkedTransactions = Object.keys(checkedRows).filter(function (id) {
                    return checkedRows[id] === true;
                });
                bulkProcessTransactions(checkedTransactions, currTable);
            });
            $('#bulk-delete-policy').on('click', function () {
                var checkedPolicy = Object.keys(checkedRows).filter(function (id) {
                    return checkedRows[id] === true;
                });
                bulkProcessDelTransactions(checkedPolicy, currTable);
            });

        }
    });
    return currTable;
}

function bulkProcessTransactions(policyId, tableInstance) {
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
            $('#bulk-authorize-policy').prop('disabled', true);
            $('#bulk-delete-policy').prop('disabled', true);

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
                url: 'authbulkcnreceipt',
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
                url: 'processbulkdelcnprocessedpolicies',
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

