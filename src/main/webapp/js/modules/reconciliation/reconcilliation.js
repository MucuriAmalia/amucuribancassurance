$(document).ready(function() {
    // Initialize the reconciliation table with server-side processing
    var table = $('#reconciliation_child').DataTable({
        "processing": true,
        "serverSide": true,
        "ajax": {
            "url": "<c:url value='/protected/reconciliation/fetchReconciliationData'/>",
            "dataSrc": "",
            "data": function (d) {
                d.periodFrom = $('#periodFrom').val();
                d.periodTo = $('#periodTo').val();
            }
        },
        "columns": [
            {
                "data": null,
                "className": 'dt-body-center',
                "orderable": false,
                "render": function (data, type, row) {
                    return '<input type="checkbox" class="row-checkbox" />';
                }
            },
            {"data": "policy"},
            {"data": "client"},
            {"data": "payment"},
            {"data": "premium"},
            {"data": "commission"},
            {"data": "settlement"},
            {"data": "allocatedAmount"},
            {"data": "payableAmount"}
        ]
    });

    // Initialize the reconciliation child table with server-side processing
    var childTable = $('#reconciliation_child').DataTable({
        "processing": true,
        "serverSide": true,
        "ajax": {
            "url": "<c:url value='/protected/reconciliation/fetchReconciliationChildData'/>",
            "dataSrc": ""
        },
        "columns": [
            {"data": "policy"},
            {"data": "client"},
            {"data": "payment"},
            {"data": "premium"},
            {"data": "commission"},
            {"data": "settlement"},
            {"data": "allocatedAmount"},
            {"data": "payableAmount"},
            {  // Column definition for "status" with optional rendering
                "data": "status",
                "render": function (data, type, row) {
                    if (data === "matched") {
                        return '<span class="badge badge-success">Matched</span>';
                    } else if (data === "unmatched") {
                        return '<span class="badge badge-danger">Unmatched</span>';
                    } else {
                        return data;
                    }
                }
            }
        ]
    });

    $('#filterButton').click(function () {
        table.ajax.reload();
    });

    $('#reconcileButton').click(function () {
        var selectedIds = [];
        $('#reconciliationTable input.row-checkbox:checked').each(function () {
            var row = $(this).closest('tr');
            var rowData = table.row(row).data();

            if (rowData && rowData.id) {
                selectedIds.push(rowData.id);
            }
        });

        if (selectedIds.length === 0) {
            alert('Please select rows to reconcile.');
            return;
        }

        $.ajax({
            url: "<c:url value='/protected/reconciliation/reconcileSelected'/>",
            type: 'POST',
            contentType: 'application/json',
            data: JSON.stringify(selectedIds),
            success: function (response) {
                alert('Reconciliation completed successfully.');

                // Fetch child data based on reconciled IDs
                fetchChildData(selectedIds);
                console.log("Child data response:", response);
                $('#reconciliation_child').ajax.reload();
            },
            error: function (xhr, status, error) {
                alert('Error occurred during reconciliation.');
                console.error(error);
            }
        });
    });

    // Function to fetch child data based on reconciled parent IDs
    function fetchChildData(parentIds) {
        $.ajax({
            url: "<c:url value='/protected/reconciliation/fetchReconciliationChildData'/>",
            type: 'GET',
            contentType: 'application/json',
            data: JSON.stringify(parentIds),
            success: function (response) {
                // Update the child table with the fetched data
                $('#reconciliation_child').clear().rows.add(response).draw();
                $('#reconciliation_child').ajax.reload();
            },
            error: function (xhr, status, error) {
                alert('Failed to fetch child data.');

            },
        })
    }
})