$(function () {
    $(document).ready(function () {
        createAccountsForSel();
        createProductForSel();
        populateClientLov();

        $("#btn-search-policies").on('click', function () {
            var policyNo = $("#pol-search-number").val();
            var riskId = $("#rev-search-number").val();
            var drNo = $("#dr-search-number").val();
            var clientCode = $("#rev-search-name").val();
            var agentCode = $("#agent-search-number").val();
            var prodCode = $("#product-search-number").val();
            if (policyNo === '' && riskId === '' && drNo === '' && clientCode === '' && agentCode === '' && prodCode === '') {
                bootbox.alert("Provide At least one Search Parameter");
                return;
            }
            policyEnquiry();
        })
    });
});

function createAccountsForSel() {
    if ($("#acc-frm").filter("div").html() != undefined) {
        Select2Builder.initAjaxSelect2({
            containerId: "acc-frm",
            sort: 'name',
            change: function (e, a, v) {
                $("#agent-search-number").val(e.added.acctId);
            },
            formatResult: function (a) {
                return a.name
            },
            formatSelection: function (a) {
                return a.name
            },
            initSelection: function (element, callback) {

            },
            id: "acctId",
            placeholder: "Select Insurance Company",
        });
    }
}

function createProductForSel() {
    if ($("#prd-code").filter("div").html() != undefined) {
        Select2Builder.initAjaxSelect2({
            containerId: "prd-code",
            sort: 'proDesc',
            change: function (e, a, v) {
                $("#product-search-number").val(e.added.proCode);
            },
            formatResult: function (a) {
                return a.proDesc
            },
            formatSelection: function (a) {
                return a.proDesc
            },
            initSelection: function (element, callback) {

            },
            id: "proCode",
            placeholder: "Select Product",
        });
    }
}

function populateClientLov(){
    if($("#client-frm").filter("div").html() != undefined)
    {
        Select2Builder.initAjaxSelect2({
            containerId : "client-frm",
            sort : 'fname',
            change: function(e, a, v){
                $("#rev-search-name").val(e.added.tenId);

            },
            formatResult : function(a)
            {
                if (a.idNo !== null) {
                    return a.fname + " " + a.otherNames + " - " + a.idNo;
                } else {
                    return a.fname + " " + a.otherNames
                }
            },
            formatSelection : function(a)
            {
                if (a.idNo !== null) {
                    return a.fname + " " + a.otherNames + " - " + a.idNo;
                } else {
                    return a.fname + " " + a.otherNames
                }
            },
            initSelection: function (element, callback) {

            },
            id: "tenId",
            placeholder:"Select Client"

        });
    }
}

function policyEnquiry() {
    var url = "enquiryActiveMedPolicies";
    return $('#pol_enquiry_tbl').DataTable({
        "processing": true,
        "serverSide": true,
        autoWidth: true,
        searching: false,
        scrollX: true,
        columnDefs: [
            {width: 150, targets: 3},
            {width: 150, targets: 6},
            {width: 150, targets: 7},
        ],
        "deferRender": true,
        "ajax": {
            'url': url,
            'data': {
                "policyNo": $("#pol-search-number").val(),
                "riskShtDesc": $("#rev-search-number").val(),
                "drNumber": $("#dr-search-number").val(),
                "clientCode": $("#rev-search-name").val(),
                "agentCode": $("#agent-search-number").val(),
                "prodCode": $("#product-search-number").val()
            },
        },
        lengthMenu: [[10, 15, 20], [10, 15, 20]],
        pageLength: 10,
        destroy: true,
        "columns": [
            {
                "data": "policyId",
                "render": function (data, type, full, meta) {
                    return '<form action="edituwtrans" method="post"><input type="hidden" name="id" value=' + full.policyId + '><input type="submit"  class="btn btn-success btn btn-info btn-sm" value="More Details" ></form>';
                }
            },
            {"data": "policyNo"},
            {"data": "polRevNo"},
            {"data": "product"},
            {
                "data": "wefDate",
                "render": function (data, type, full, meta) {
                    return moment(full.wefDate).format('DD/MM/YYYY');
                }
            },
            {
                "data": "wetDate",
                "render": function (data, type, full, meta) {
                    return moment(full.wetDate).format('DD/MM/YYYY');
                }
            },
            {"data": "clientName"},
            {"data": "agentName"},
            {"data": "currency"},
            {"data": "username"},
            {"data": "currentStatus"},
            {
                "data": "policyId",
                "render":function (data,type,full,meta) {
                    if(full.currentStatus === "Active") {
                        return '<button type="button" class="btn btn-danger btn btn-info btn-sm" data-policy=' + encodeURI(JSON.stringify(full)) + ' onclick="lapsePolicy(this);">Lapse</button>';
                    } else if (full.currentStatus === "Lapsed") {
                        return '<button type="button" class="btn btn-success btn btn-info btn-sm" data-policy=' + encodeURI(JSON.stringify(full)) + ' onclick="unlapsePolicy(this);">Unlapse</button>';
                    }
                }}
        ]
    });

}

function lapsePolicy(button) {
    var policy = JSON.parse(decodeURI($(button).data("policy")));
    $("#lapse-pol-id").val(policy['policyId']);
    bootbox.confirm("Are you sure you want to lapse policy " + policy["policyNo"] + "?", function (result) {
        if (result) {
            $.ajax({
                type: 'GET',
                url: 'lapsePolicy/' + policy["policyId"],
                dataType: 'json',
                async: true,
                success: function (result) {
                    bootbox.alert("Policy Lapsed Successfully");
                    $('#pol_enquiry_tbl').DataTable().ajax.reload();
                },
                error: function (jqXHR, textStatus, errorThrown) {
                    bootbox.alert(jqXHR.responseText);
                }
            });
        }
    });
}

function unlapsePolicy(button) {
    var policy = JSON.parse(decodeURI($(button).data("policy")));
    $("#lapse-pol-id").val(policy['policyId']);
    bootbox.confirm("Are you sure you want to unlapse policy " + policy["policyNo"] + "?", function (result) {
        if (result) {
            $.ajax({
                type: 'GET',
                url: 'unlapsePolicy/' + policy["policyId"],
                dataType: 'json',
                async: true,
                success: function (result) {
                    bootbox.alert("Policy Unlapsed Successfully");
                    $('#pol_enquiry_tbl').DataTable().ajax.reload();
                },
                error: function (jqXHR, textStatus, errorThrown) {
                    bootbox.alert(jqXHR.responseText);
                }
            });
        }
    });
}