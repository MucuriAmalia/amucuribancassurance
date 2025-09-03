var UTILITIES = UTILITIES || {};
$(function() {
    $(document).ready(function() {
        $(".datepicker-input").each(function() {
            $(this).datetimepicker({
                format: 'DD/MM/YYYY'
            });
        });

        $("#total-val").val(0);
        createAccountsForSel();

        // Disable print button initially
        $("#print-report").prop("disabled", true);

        $("#btn-search-trans").on('click', function(){
            var table = policyUpdateStatus();
            $("#total-val").val(0);
            $(".total").val(0);

            // Check table data and enable print button
            setTimeout(function() {
                if (table && table.rows().count() > 0) {
                    $("#print-report").prop("disabled", false);
                } else {
                    $("#print-report").prop("disabled", true);
                }
            }, 500);
        });

        statusReport();
    });
});

function createAccountsForSel(){
    if($("#acc-frm").filter("div").html() != undefined)
    {
        Select2Builder.initAjaxSelect2({
            containerId : "acc-frm",
            sort : 'name',
            change: function(e, a, v){
                $("#agent-search-number").val(e.added.acctId);
            },
            formatResult : function(a)
            {
                return a.name
            },
            formatSelection : function(a)
            {
                return a.name
            },
            initSelection: function (element, callback) {

            },
            id: "acctId",
            placeholder:"Select Insurance Company",
        });
    }

    $("#acc-frm").on("select2-removed", function(e) {
        $("#agent-search-number").val('');
    })
}

function policyUpdateStatus() {

    if(!$("#from-date").val()){
        bootbox.alert("Select Date from....");
        return;
    }
    if(!$("#wet-date").val()){
        bootbox.alert("Select Date To....");
        return;
    }
    if(!$("#agent-search-number").val()){
        bootbox.alert("Select Insurance Company....");
        return;
    }
    if(!$("#status").val()){
        bootbox.alert("Select Status....");
        return;
    }

    var url = "viewupdated";
    var currTable = $('#update-tbl').DataTable({
        "processing": true,
        "serverSide": true,
        "autoWidth": true,
        "ajax": {
            'url': url,
            'type': 'GET',
            'dataType': 'json',
            'data': {'status': $("#status").val(),
                'agentCode': $("#agent-search-number").val(),
                'wefDate': $("#from-date").val(),
                'wetDate':  $("#wet-date").val(),
            },
            "dataSrc": function (json) {
                console.log("Received data: ", json);
                return json.data || json;
            }
        },
        "lengthMenu": [[10, 15, 20, 1000, 100000, 1000000], [10, 15, 20, 1000, 100000, 1000000 ]],
        "pageLength": 10,
        "destroy": true,
        "columns": [
            {"data": "clientName"},
            {"data": "riskNote"},
            {"data": "polNo"},
            {"data": "clientPol"},
            {"data": "productName"},
            {
                "data": "polauthDate",
                "render": function (data, type, full, meta) {
                    if (full.polauthDate) {
                        return moment(full.polauthDate).format('DD/MM/YYYY');
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
            }
        ],
    });

    return currTable;
}

function statusReport() {
    $("#print-report").on("click", function() {
        var reportUrl = "rpt_updated_policies.pdf";
        window.open(reportUrl, "_blank");
    });
}
