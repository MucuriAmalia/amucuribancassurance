/**
 * Created by peter on 3/28/2017.
 */
var UTILITIES = UTILITIES || {};
$(function(){

    $(document).ready(function() {

        getAuthTrans();

        $(document).ajaxStart(function () {
            $("#accounts-tbl button").prop("disabled", true);
        });

        $(document).ajaxComplete(function () {
            $("#accounts-tbl button").prop("disabled", false);
        });

    })
});


function getAuthTrans() {
    var url = "accounttrans";
    var currTable = $('#accounts-tbl')
        .DataTable(
            {
                "processing" : true,
                "serverSide" : true,
                "ajax" : url,
                lengthMenu : [ [ 20, 30, 40, 50 ], [ 20, 30, 40, 50 ] ],
                pageLength : 20,
                "scrollX": true,
                destroy : true,
                "columns" : [
                    {
                        "data": "transno",
                        "className": 'dt-body-center',
                        "orderable": false,
                        "render": function(data, type, row) {
                            return '<input type="checkbox" class="row-checkbox" value="' + row.transno + '">';
                        }
                    },
                    {
                        "data" : "transDate",
                        "render" : function(data, type, full, meta) {
                            return moment(full.transDate).format('DD/MM/YYYY');
                        }

                    },
                    {
                        "data" : "controlAcc"
                    },
                    {
                        "data" : "refNo"
                    },
                    {
                        "data" : "transType"
                    },
                    {
                        "data" : "transdc",
                        "render" : function(data, type, full, meta) {
                            if(full.transdc==="C")
                            return "Credit";
                            else if(full.transdc==="D")
                                return "Debit";
                        }
                    },
                    {
                        "data" : "amount",
                        "render" : function(data, type, full, meta) {
                            return UTILITIES.currencyFormat(full.amount);
                        }
                    },
                    {
                        "data" : "netAmount",
                        "render" : function(data, type, full, meta) {
                            return UTILITIES.currencyFormat(full.netAmount);
                        }
                    },
                    {
                        "data" : "payeeName"
                    },
                    {
                        "data" : "transno",
                        "render" : function(data, type, full, meta) {
                            return '<button type="button" class="btn btn-success btn btn-info btn-sm" type="button" data-audits='+encodeURI(JSON.stringify(full)) + '  onclick="authTrans(this);">Authorize</button>';

                        }
                    },
                    {
                        "data" : "transno",
                        "render" : function(data, type, full, meta) {
                            return '<button type="button" class="btn btn-danger btn btn-info btn-sm" type="button" data-audits='+encodeURI(JSON.stringify(full)) + '  onclick="rejectTrans(this);">Reject</button>';

                        }
                    },

                ]
            });
    $('#selectAll').click(function() {
        var isChecked = $(this).prop('checked');
        $('.row-checkbox').prop('checked', isChecked);
    });

    // Bulk Authorize button
    $('#btn-approve-all').click(function() {
        bulkAuthAction('authorize');
    });

    // Bulk Reject button
    $('#btn-reject-all').click(function() {
        bulkAuthAction('reject');
    });

    return currTable;
}


function authTrans(button){
    var audits = JSON.parse(decodeURI($(button).data("audits")));
    bootbox.confirm("Authorize Selected Transaction?", function(result) {
        if (result) {
            $.ajax({
                type: 'GET',
                url:  'authAccountTrans/' + audits['transno'],
                dataType: 'json',
                async: true,
                success: function(result) {
                    Swal.fire({
                        title: 'Success',
                        text: 'Transaction Authorized Successfully',
                        icon: 'success'
                    });

                    $('#accounts-tbl').DataTable().ajax.reload();
                },
                error: function(jqXHR, textStatus, errorThrown) {
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

function rejectTrans(button){
    var audits = JSON.parse(decodeURI($(button).data("audits")));
    bootbox.confirm("Reject Selected Commission Transaction?", function(result) {
        if (result) {
            $.ajax({
                type: 'GET',
                url:  'deleteAuthTrans/' + audits['transno'],
                dataType: 'json',
                async: true,
                success: function(result) {
                    Swal.fire({
                        title: 'Success',
                        text: 'Transaction Rejected Successfully',
                        icon: 'success'
                    });
                    $('#accounts-tbl').DataTable().ajax.reload();
                },
                error: function(jqXHR, textStatus, errorThrown) {
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

// Bulk action function
function bulkAuthAction(action, specificIds) {
    var selectedTransNos = specificIds || [];
    var totalAmount = 0;

    // Get the current DataTable instance
    var table = $('#accounts-tbl').DataTable();

    if (!specificIds) {
        $('.row-checkbox:checked').each(function() {
            var transNo = $(this).val();
            selectedTransNos.push(transNo);

            // Find the corresponding row data and add to total amount
            var rowData = table.row($(this).closest('tr')).data();
            if (rowData && rowData.netAmount) {
                totalAmount += parseFloat(rowData.netAmount);
            }
        });
    } else {
        // If specific IDs are provided, calculate total for those
        specificIds.forEach(function(transNo) {
            var rowData = table.rows().data().toArray().find(function(row) {
                return row.transno == transNo;
            });
            if (rowData && rowData.netAmount) {
                totalAmount += parseFloat(rowData.netAmount);
            }
        });
    }

    if (selectedTransNos.length === 0) {
        Swal.fire({
            title: 'Error',
            text: 'Please select at least one transaction',
            icon: 'error'
        });
        return;
    }

    var endpoint, successMessage;

    if (action === 'authorize') {
        endpoint = 'authBulkAccountTrans';
        successMessage = 'authorized';
    } else {
        endpoint = 'deleteBulkAuthTrans';
        successMessage = 'rejected';
    }

    // Format the total amount using your utility function
    var formattedAmount = UTILITIES.currencyFormat(totalAmount);

    Swal.fire({
        title: 'Confirm ' + action,
        text: 'Are you sure you want to ' + action + ' ' + selectedTransNos.length +
            ' transaction(s) with a total amount of ' + formattedAmount + '?',
        icon: 'warning',
        showCancelButton: true,
        confirmButtonColor: '#3085d6',
        cancelButtonColor: '#d33',
        confirmButtonText: 'Yes, ' + action + ' them!'
    }).then((result) => {
        if (result.isConfirmed) {
            // Convert to numbers if needed
            var numericIds = selectedTransNos.map(id => Number(id));

            $.ajax({
                url: endpoint,
                type: 'POST',
                contentType: 'application/json',
                data: JSON.stringify(numericIds),
                success: function(response) {
                    Swal.fire({
                        title: 'Success',
                        text: selectedTransNos.length + ' transactions ' + successMessage + ' successfully',
                        icon: 'success'
                    });
                    $('#accounts-tbl').DataTable().ajax.reload();
                    $('#selectAll').prop('checked', false);
                },
                error: function(jqXHR) {
                    Swal.fire({
                        title: 'Error',
                        text: jqXHR.responseText || 'An error occurred',
                        icon: 'error'
                    });
                }
            });
        }
    });
}