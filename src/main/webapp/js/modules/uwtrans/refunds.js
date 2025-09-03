$(document).ready(function() {
    // Initialize table variable in wider scope
    var refundsTable;

    // Initialize DataTable
    function initRefundsTable() {
        refundsTable = $('#refunds-tbl').DataTable({
            "processing": true,
            "serverSide": true,
            autoWidth: true,
            searching: false,
            "deferRender": true,
            "ajax": {
                "url": SERVLET_CONTEXT + "/protected/uw/refunds/getClientRefunds",
                "type": "GET",
                "data": function(d) {
                    d.clientCode = $("#rev-search-name").val();
                    d.prodCode = $("#product-search-number").val();
                    d.policyNo = $("#policy-search-number").val();
                    console.log("Search params - Client:", d.clientCode, "Product:", d.prodCode, "Policy No:", d.policyNo);
                },
                "error": function(xhr, error, thrown) {
                    console.error("DataTable AJAX error:", error, thrown);
                }
            },
            lengthMenu: [[10,15,20], [10,15,20]],
            pageLength: 10,
            destroy: true,
            "columns": [
                {
                    "data": "policyNo",
                    "defaultContent": "-"
                },
                {
                    "data": "transno",
                    "defaultContent": "-"
                },
                {
                    "data": "transDate",
                    "render": function(data) {
                        return data ? moment(data).format('DD/MM/YYYY') : '-';
                    }
                },
                {
                    "data": null,
                    "render": function(data) {
                        var fname = data.clientFname || '';
                        var onames = data.clientOtherNames || '';
                        return fname + ' ' + onames;
                    }
                },
                {
                    "data": "productDesc",
                    "defaultContent": "-"
                },
                {
                    "data": "transType",
                    "defaultContent": "-"
                },
                {
                    "data": "balance",
                    "render": function(data) {
                        return data ? UTILITIES.currencyFormat(data) : '-';
                    }
                },
                {
                    "data": "transno",
                    "render": function(data, type, full) {
                        return '<div class="btn-group">' +
                            '<button class="btn btn-info btn-sm view-refund" data-trans-no="' + data + '">View</button>' +
                            '<button class="btn btn-success btn-sm process-refund" data-trans-no="' + data + '">Process</button>' +
                            '</div>';
                    }
                }
            ],
            "order": [[2, "desc"]]
        });

        return refundsTable;
    }

    // Initialize Select2 for Product
    if($("#prd-code").filter("div").html() != undefined) {
        Select2Builder.initAjaxSelect2({
            containerId: "prd-code",
            sort: 'proDesc',
            change: function(e, a, v) {
                $("#product-search-number").val(e.added ? e.added.proCode : '');
            },
            formatResult: function(a) {
                return a.proDesc;
            },
            formatSelection: function(a) {
                return a.proDesc;
            },
            initSelection: function(element, callback) {},
            id: "proCode",
            placeholder: "Select Product"
        });
    }

    // Initialize Select2 for Client
    if($("#client-frm").filter("div").html() != undefined) {
        Select2Builder.initAjaxSelect2({
            containerId: "client-frm",
            sort: 'fname',
            change: function(e, a, v) {
                $("#rev-search-name").val(e.added ? e.added.tenId : '');
                console.log("Selected Client ID:", e.added ? e.added.tenId : '');
            },
            formatResult: function(a) {
                return a.fname + " " + a.otherNames;
            },
            formatSelection: function(a) {
                return a.fname + " " + a.otherNames;
            },
            initSelection: function(element, callback) {},
            id: "tenId",
            placeholder: "Select Client"
        });
    }

    $("#btn-search-refunds").on('click', function() {
        var clientCode = $("#rev-search-name").val();
        var prodCode = $("#product-search-number").val();
        var policyNo = $("#policy-search-number").val();

        // Allow search with no parameters (will show all refunds)
        if(!refundsTable) {
            refundsTable = initRefundsTable();
        } else {
            refundsTable.ajax.reload();
        }
    });

    $(document).on('click', '.view-refund', function() {
        var transNo = $(this).data('trans-no');
        console.log('Viewing refund transaction:', transNo);
        // Add your view logic here
    });

    $(document).on('click', '.process-refund', function() {
        var transNo = $(this).data('trans-no');
        console.log('Processing refund transaction:', transNo);
    });

    // Initialize table on page load
    initRefundsTable();
});