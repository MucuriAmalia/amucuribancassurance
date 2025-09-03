$(function(){

    $(document).ready(function(){
        getUserTrans();
        populateClientLov();
        createAccountsForSel();
        createProductForSel();

        $(document).ajaxStart(function () {
            $(".btn-danger").attr("disabled", true);
        });
        $(document).ajaxComplete(function () {
            $(".btn-danger").attr("disabled", false);
        });

        $("#btn-search-policies").on('click', function(){
            var policy = $("#pol-search-number").val();
            var endos = $("#rev-search-number").val();
            var drnumber = $("#dr-search-number").val();
            var clientCode = $("#rev-search-name").val();
            var agentCode = $("#agent-search-number").val();
            var prodCode = $("#product-search-number").val();
            if(policy==='' && endos==='' && drnumber==='' && clientCode==='' && agentCode==='' && prodCode===''){
                bootbox.alert("Provide At least one Search Parameter");
                return;
            }

            policyEnquiry();

        });
    });



});


function getUserTrans(){
    var url = "policyTrans";
    var currTable = $('#pol_tbl').DataTable( {
        "processing": true,
        "serverSide": true,
        autoWidth: true,
        "deferRender": true,
        "order": [[ 9, "desc" ]],
        "ajax": {
            'url': url,
        },
        lengthMenu: [ [10,15,20], [10,15,20] ],
        pageLength: 10,
        destroy: true,
        "columns": [
            { "data": "taskId" },
            { "data": "activeProcess" },
            { "data": "policyTrans",
                "render": function ( data, type, full, meta ) {

                    return full.policyTrans.polNo;
                }
            },
            { "data": "policyTrans",
                "render": function ( data, type, full, meta ) {

                    return full.policyTrans.polRevNo;
                }
            },
            { "data": "policyTrans",
                "render": function ( data, type, full, meta ) {

                    return full.policyTrans.product.proDesc;
                }
            },
            { "data": "policyTrans",
                "render": function ( data, type, full, meta ) {
                    return moment(full.policyTrans.wefDate).format('DD/MM/YYYY');
                }
            },
            { "data": "policyTrans" ,
                "render": function ( data, type, full, meta ) {
                    return moment(full.policyTrans.wetDate).format('DD/MM/YYYY');
                }
            },
            { "data": "policyTrans",
                "render": function ( data, type, full, meta ) {

                    return full.policyTrans.client.fname+" "+full.policyTrans.client.otherNames;
                }
            },
            { "data": "policyTrans",
                "render": function ( data, type, full, meta ) {

                    return full.policyTrans.createdUser.username;
                }
            },
            {
                "data": "policyTrans",
                "render": function ( data, type, full, meta ) {
                    if(canAccess) {
                        return '<form action="edituwtrans" method="post"><input type="hidden" name="id" value=' + full.policyTrans.policyId + '><input type="submit"  class="btn btn-success btn btn-info btn-sm" value="Edit" ></form>';
                    }
                    else return "";
                }

            },
        ]
    } );
    return currTable;
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
                }
                else {
                    return a.fname + " " + a.otherNames
                }
            },
            formatSelection : function(a)
            {
                if (a.idNo !== null) {
                    return a.fname + " " + a.otherNames + " - " + a.idNo;
                }
                else {
                    return a.fname + " " + a.otherNames
                };
            },
            initSelection: function (element, callback) {

            },
            id: "tenId",
            placeholder:"Select Client"

        });
    }

    $("#client-frm").on("select2-removed", function(e) {
        $("#rev-search-name").val('');
    })
}

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
            placeholder:"Select Underwriter",
        });
    }

    $("#acc-frm").on("select2-removed", function(e) {
        $("#agent-search-number").val('');
    })
}


function createProductForSel(){
    if($("#prd-code").filter("div").html() != undefined)
    {
        Select2Builder.initAjaxSelect2({
            containerId : "prd-code",
            sort : 'proDesc',
            change: function(e, a, v){
                $("#product-search-number").val(e.added.proCode);
            },
            formatResult : function(a)
            {
                return a.proDesc
            },
            formatSelection : function(a)
            {
                return a.proDesc
            },
            initSelection: function (element, callback) {

            },
            id: "proCode",
            placeholder:"Select Product",
        });
    }

    $("#prd-code").on("select2-removed", function(e) {
        $("#product-search-number").val('');
    })
}

function policyEnquiry(){
    var url = "pendingPolicies";
    var currTable = $('#pol_enquiry_tbl').DataTable( {
        "processing": true,
        "serverSide": true,
        autoWidth: true,
        searching: false,
        "deferRender": true,
        "ajax": {
            'url': url,
            'data':{
                'clientCode': $("#rev-search-name").val(),
                'policyNo':  $("#pol-search-number").val(),
                'agentCode':  $("#agent-search-number").val(),
                'endorseNumber':  $("#rev-search-number").val(),
                'refno': $("#dr-search-number").val(),
                'prodCode': $("#product-search-number").val(),
            },
        },
        lengthMenu: [ [10,15,20], [10,15,20] ],
        pageLength: 10,
        destroy: true,
        "columns": [
            { "data": "polNo" },
            { "data": "polRevNo" },
            { "data": "product",
                "render": function ( data, type, full, meta ) {

                    return full.product.proDesc;
                }
            },
            { "data": "wefDate",
                "render": function ( data, type, full, meta ) {
                    return moment(full.wefDate).format('DD/MM/YYYY');
                }
            },
            { "data": "wetDate" ,
                "render": function ( data, type, full, meta ) {
                    return moment(full.wetDate).format('DD/MM/YYYY');
                }
            },
            { "data": "client",
                "render": function ( data, type, full, meta ) {

                    return full.client.fname+" "+full.client.otherNames;
                }
            },
            { "data": "agent",
                "render": function ( data, type, full, meta ) {

                    return full.agent.name;
                }
            },
            { "data": "agent",
                "render": function ( data, type, full, meta ) {

                    return full.transType;
                }
            },

            { "data": "transCurrency",
                "render": function ( data, type, full, meta ) {

                    return full.transCurrency.curIsoCode;
                }
            },
            { "data": "createdUser",
                "render": function ( data, type, full, meta ) {

                    return full.createdUser.username;
                }
            },
            {
                "data": "policyId",
                "render": function ( data, type, full, meta ) {
                    return '<form action="edituwtrans" method="post"><input type="hidden" name="id" value='+full.policyId+'><input type="submit"  class="btn btn-success btn btn-info btn-sm" value="View" ></form>';
                }

            },
            {
                "data": "policyId",
                "render": function ( data, type, full, meta ) {
                    return '<input type="button" class="btn btn-danger btn-sm" data-policy='+encodeURI(JSON.stringify(full)) + ' value="Delete" onclick="confirmPolicyDelete(this);"/>';
                }

            },
        ]
    } );
    return currTable;
}


function confirmPolicyDelete(button){
    var policy = JSON.parse(decodeURI($(button).data("policy")));
    bootbox.confirm("Are you sure want to delete "+policy["polNo"]+"?", function(result) {
        if(result){
            // // $('#myPleaseWait').modal({
//			backdrop: 'static',
//			keyboard: true
//		})
            $.ajax({
                type: 'GET',
                url:  'deletePolRecord',
                data: {"policyId": policy["policyId"]},
                dataType: 'json',
                async: true,
                success: function(result) {
                    // $('#myPleaseWait').modal('hide');
                    Swal.fire({
						title: 'Success',
						text: 'Transaction Deleted Successfully',
						icon: 'success'
					 });
                    $('#pol_enquiry_tbl').DataTable().ajax.reload(null,false);
                },
                error: function(jqXHR, textStatus, errorThrown) {
                    // $('#myPleaseWait').modal('hide');
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

/**
 * Created by HP on 9/12/2017.
 */
