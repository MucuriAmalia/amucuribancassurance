/**
 * Created by peter on 6/8/2017.
 */
var UTILITIES = UTILITIES || {};
$(function() {

    $(document).ready(function () {
        populateAdminFeeDetails();
        addAdminFeePolicies();
        saveCategoryRules();

        $("#btn-auth-admin").click(function(){
            authorizeAdminFee();
        });
    });
});



function populateAdminFeeDetails(){
    if(typeof adminFeeId!== 'undefined'){
        if(adminFeeId!==-2000){
            $.ajax( {
                url: 'getAdminFeeDetails/'+adminFeeId,
                type: 'GET',
                processData: false,
                contentType: false,
                success: function (s ) {
                    $("#txt-client-no").text(s.clientDef.fname+" "+ s.clientDef.otherNames);
                    $("#txt-client-id").val(s.clientDef.tenId);
                    $("#txt-ref-no").text(s.refNo);
                    if(s.branch)
                    $("#txt-branch").text(s.branch.obName);
                    $("#txt-currency").text(s.currencies.curName);
                    $("#txt-trans-date").text(moment(s.preparedDate).format('DD/MM/YYYY'));
                    $("#txt-gross-amt").text(UTILITIES.currencyFormat(s.adminFeeAmt));
                    $("#txt-vat-amt").text(UTILITIES.currencyFormat(s.vatAmt));
                    $("#txt-excise-duty").text(UTILITIES.currencyFormat(s.exciseDuty));
                    $("#txt-vat-excise-duty").text(UTILITIES.currencyFormat(s.vatExciseDuty));
                    $("#txt-net-amt").text(UTILITIES.currencyFormat(s.adminNetAmt));
                    $("#txt-authorised").text(s.authorised);
                    if(s.authorisedBy)
                    $("#txt-authorised-by").text(s.authorisedBy.username);
                    $("#txt-remarks").text(s.remarks);
                    if(s.authorised==="Y"){
                        $("#btn-auth-admin").hide();
                        $("#btn-add-policies").hide();
                    }
                    else{
                        $("#btn-auth-admin").show();
                        $("#btn-add-policies").show();
                    }
                    getAdminFeePolicies(adminFeeId);
                },
                error: function(xhr, error){
                    bootbox.alert(xhr.responseText);
                }
            });
        }
        else{

        }
    }
}


function authorizeAdminFee(){
    var stack_bottomleft = {"dir1": "up", "dir2": "left", "firstpos1": 25, "firstpos2": 25};
    // $('#myPleaseWait').modal({
   //     backdrop: 'static',
     //   keyboard: true
    // });
    $.ajax({
        type: 'GET',
        url:  'authoriseAdminFee',
        dataType: 'json',
        async: true,
        success: function(result) {
            // $('#myPleaseWait').modal('hide');
            Swal.fire({
                title: 'Success',
                text: 'Admin Fee Authorised Successfully',
                icon: 'success'
            });
            populateAdminFeeDetails();
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


function getAdminFeePolicies(adminFeeCode){
    var url = "adminFeePolicies";
    var currTable = $('#admin_fee_policies').DataTable( {
        "processing": true,
        "serverSide": true,
        autoWidth: true,
        "deferRender": true,
        "ajax": {
            'url': url,
        },
        lengthMenu: [ [10,15,20], [10,15,20] ],
        pageLength: 10,
        destroy: true,
        "columns": [
            { "data": "policy",
                "render": function ( data, type, full, meta ) {

                    return full.policy.polNo;
                }
            },
            { "data": "policy",
                "render": function ( data, type, full, meta ) {

                    return full.policy.polRevNo;
                }
            },
            { "data": "policy",
                "render": function ( data, type, full, meta ) {

                    return full.policy.product.proDesc;
                }
            },
            { "data": "policy",
                "render": function ( data, type, full, meta ) {
                    return moment(full.policy.wefDate).format('DD/MM/YYYY');
                }
            },
            { "data": "policy" ,
                "render": function ( data, type, full, meta ) {
                    return moment(full.policy.wetDate).format('DD/MM/YYYY');
                }
            },
            { "data": "policy",
                "render": function ( data, type, full, meta ) {

                    return full.policy.refNo;
                }
            },
            {
                "data": "policy",
                "render": function ( data, type, full, meta ) {

                    return "";
                }

            },
        ]
    } );
    return currTable;
}



function addAdminFeePolicies() {
    $("#btn-add-policies").click(function () {
            $("#admin-fee-code").val(adminFeeId);
            $.ajax({
                type: 'GET',
                url: 'getAdminFeePolicies',
                dataType: 'json',
                data: {"clientId": $("#txt-client-id").val(),"adminFeeId": adminFeeId},
                async: true,
                success: function (result) {
                    $("#adminPoliciesTbl tbody").each(function () {
                        $(this).remove();
                    });
                    for (var res in result) {
                        var markup = "<tr><td><input type='checkbox' name='record' id='" + result[res][0] + "'></td><td>" + result[res][1] + "</td><td>" + result[res][2] + "</td><td>" + result[res][3] + "</td></tr>";
                        $("#adminPoliciesTbl").append(markup);
                    }
                    $('#adminFeePolModal').modal({
                        backdrop: 'static',
                        keyboard: true
                    })
                },
                error: function (jqXHR, textStatus, errorThrown) {
                    Swal.fire({
                title: 'Error',
                text: jqXHR.responseText,
                icon: 'error'
            });
                }
            });

    });
}

function saveCategoryRules(){
    var arr = [];
    $("#saveAdminFeePolBtn").click(function(){
        $("#adminPoliciesTbl tbody").find('input[name="record"]').each(function(){
            if($(this).is(":checked")){
                arr.push($(this).attr("id"));
            }
        });
        if(arr.length==0){
            bootbox.alert("No Policies Selected to attach..");
            return;
        }

        // $('#myPleaseWait').modal({
   //     backdrop: 'static',
     //   keyboard: true
    // });

        var $currForm = $('#admin-pol-form');
        var currValidator = $currForm.validate();
        if (!$currForm.valid()) {
            return;
        }

        var data = {};
        $currForm.serializeArray().map(function(x) {
            data[x.name] = x.value;
        });
        var url = "createAdminFeePolicies";
        data.policies = arr;


        $.ajax({
            url : url,
            type : "POST",
            data : JSON.stringify(data),
            success : function(s) {
                // $('#myPleaseWait').modal('hide');
                Swal.fire({
                title: 'Success',
                text: 'Record created/updated Successfully',
                icon: 'success'
            });
                populateAdminFeeDetails();
                $('#admin_fee_policies').DataTable().ajax.reload();
                $('#adminFeePolModal').modal('hide');
                arr=[];
            },
            error : function(jqXHR, textStatus, errorThrown) {
                // $('#myPleaseWait').modal('hide');
                Swal.fire({
                title: 'Error',
                text: jqXHR.responseText,
                icon: 'error'
            });
            },
            dataType : "json",
            contentType : "application/json"
        });
    })
}