/**
 * Created by peter on 3/10/2017.
 */
$(function() {

    $(document).ready(function () {

        claimEnquiry();

        createMemberTrans();
        markCardsRequest();
        markCardsRececived();
        dispatchCards();
        populateClientLov();

        $("#btn-search-claims").on('click', function(){
            claimEnquiry();
        });

        $("#btn-search-cards").on('click', function(){
        	createMemberTrans();
        })

    })
});

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
                }
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

function markCardsRequest(){
    $("#btn-mark-request").on('click', function(){
        var arrs = [];
        var currTable = $('#searchMembersTbl').dataTable();
        currTable.$('input[type="checkbox"]').each(function(){
            if(this.checked){
                arrs.push(this.value);
            }

        });
        if(arrs.length==0){
            bootbox.alert("No Cards Selected to Receive..");
            return;
        }

        var $currForm = $('#cards-process-form');
        var currValidator = $currForm.validate();
        if (!$currForm.valid()) {
            return;
        }

        var data = {};
        $currForm.serializeArray().map(function(x) {
            data[x.name] = x.value;
        });
        var url = "requestCards";
        data.cards = arrs;


        $.ajax({
            url : url,
            type : "POST",
            data : JSON.stringify(data),
            success : function(s) {
                // $('#myPleaseWait').modal('hide');
                 Swal.fire({
                title: 'Success',
                text: 'Process Successfully',
                icon: 'success'
            });
                $('#searchMembersTbl').DataTable().ajax.reload(null,false);
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

function dispatchCards(){
    $("#btn-dispatch-cards").on('click', function(){
        var arr = [];
        var currTable = $('#searchMembersTbl').dataTable();
        currTable.$('input[type="checkbox"]').each(function(){
            if(this.checked){
                arr.push(this.value);
            }

        });
        if(arr.length==0){
            bootbox.alert("No Cards Selected to Dispatch..");
            return;
        }

        var $currForm = $('#cards-process-form');
        var currValidator = $currForm.validate();
        if (!$currForm.valid()) {
            return;
        }

        var data = {};
        $currForm.serializeArray().map(function(x) {
            data[x.name] = x.value;
        });
        var url = "dispatchCards";
        data.cards = arr;


        $.ajax({
            url : url,
            type : "POST",
            data : JSON.stringify(data),
            success : function(s) {
                // $('#myPleaseWait').modal('hide');
                 Swal.fire({
                title: 'Success',
                text: 'Process Successfully',
                icon: 'success'
            });
                $('#searchMembersTbl').DataTable().ajax.reload(null,false);
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

function markCardsRececived(){
    $("#btn-mark-received").on('click', function(){
        var arrss = [];
        var currTable = $('#searchMembersTbl').dataTable();
        currTable.$('input[type="checkbox"]').each(function(){
            if(this.checked){
                arrss.push(this.value);
            }

        });
        if(arrss.length==0){
            bootbox.alert("No Cards Selected to Receive..");
            return;
        }

        var $currForm = $('#cards-process-form');
        var currValidator = $currForm.validate();
        if (!$currForm.valid()) {
            return;
        }

        var data = {};
        $currForm.serializeArray().map(function(x) {
            data[x.name] = x.value;
        });
        var url = "receiveCards";
        data.cards = arrss;


        $.ajax({
            url : url,
            type : "POST",
            data : JSON.stringify(data),
            success : function(s) {
                // $('#myPleaseWait').modal('hide');
                 Swal.fire({
                title: 'Success',
                text: 'Process Successfully',
                icon: 'success'
            });
                $('#searchMembersTbl').DataTable().ajax.reload(null,false);
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



function createMemberTrans(){
    var url = "medicalCards";
    var currTable = $('#searchMembersTbl').DataTable( {
        "processing": true,
        "serverSide": true,
        "ajax": {
            'url': url,
            'data':{
                'clientName': $("#dr-scheme-name").val(),
                'policyNo':  $("#pol-search-number").val(),
                'memberNo':  $("#txt-member-no").val(),
                'memberName':  $("#txt-member-name").val(),
                'cardNo':  $("#card-search-number").val(),
            },
        },
        autoWidth: true,
        lengthMenu: [ [10], [10] ],
        pageLength: 10,
        destroy: true,
        searching: false,
        'columnDefs': [{
            'targets': 0,
            'searchable':false,
            'orderable':false,
            'render': function (data, type, full, meta){
                return '<input type="checkbox" name="id[]" value="'
                    + $('<div/>').text(data).html() + '">';
            }
        }],
        "columns": [
            { "data": "cardId" },
            { "data": "member",
                "render": function ( data, type, full, meta ) {
                    return full.member.memberShipNo;
                }
            },
            { "data": "cardNo" },
            { "data": "member",
                "render": function ( data, type, full, meta ) {
                    return full.member.client.tenantNumber;
                }
            },
            { "data": "member",
                "render": function ( data, type, full, meta ) {
                    return full.member.client.fname + " "+full.member.client.otherNames;
                }
            },
            { "data": "wefDate",
                "render": function ( data, type, full, meta ) {
                    return moment(full.wefDate).format('DD/MM/YYYY');
                }
            },
            { "data": "wetDate",
                "render": function ( data, type, full, meta ) {
                    return moment(full.wetDate).format('DD/MM/YYYY');
                }
            },
            { "data": "member",
                "render": function ( data, type, full, meta ) {
                    return moment(full.member.client.dob).format('DD/MM/YYYY');
                }
            },
            { "data": "member",
                "render": function ( data, type, full, meta ) {
                    return full.member.category.policy.polNo;
                }
            },
            { "data": "status",
                "render": function ( data, type, full, meta ) {
                    return full.status;
                }
            },

        ]
    } );

    $('#example-select-all').on('click', function(){
        // Check/uncheck all checkboxes in the table
        var rows = currTable.rows({ 'search': 'applied' }).nodes();
        $('input[type="checkbox"]', rows).prop('checked', this.checked);
    });

    $('#searchMembersTbl tbody').on('change', 'input[type="checkbox"]', function(){
        // If checkbox is not checked
        if(!this.checked){
            var el = $('#example-select-all').get(0);
            // If "Select all" control is checked and has 'indeterminate' property
            if(el && el.checked && ('indeterminate' in el)){
                // Set visual state of "Select all" control
                // as 'indeterminate'
                el.indeterminate = true;
            }
        }
    });




    return currTable;
}

function claimEnquiry(){
    var url = "enquireClaims";
    return $('#clm_enquiry_tbl').DataTable({
        "processing": true,
        "serverSide": true,
        autoWidth: true,
        searching: false,
        "order": [[8, "desc"]],
        "deferRender": true,
        "ajax": {
            'url': url,
            'data':{
                'policyNo':$('#pol-search-number').val(),
                'riskId':$('#rev-search-number').val(),
                'claimNo':$('#rev-claim-number').val(),
                'clientCode': $('#rev-search-name').val(),
            },
        },
        lengthMenu: [[10, 15, 20], [10, 15, 20]],
        pageLength: 10,
        destroy: true,
        "columns": [
            {"data": "claimNo"},
            {"data": "riskId"},
            // {"data": "policyNo"},
            {"data": "insuredName"},
            {"data": "insurerClaimRef"},
            {"data": "insurerName"},
            {"data": "productName"},
            {"data": "productType"},
            {"data": "clmStatus"},
            {
                "data": "clmId",
                "render": function (data, type, full, meta) {
                    return moment(full.lossDate).format('DD/MM/YYYY');
                }
            },

            {
                "data": "clmDate",
                "render": function (data, type, full, meta) {
                    return moment(full.clmDate).format('DD/MM/YYYY');
                }
            },
            {"data": "username"},
            {
                "data": "clmId",
                "render": function (data, type, full, meta) {
                    return '<form action="claimtrans" method="post"><input type="hidden" name="id" value=' + full.clmId + '><input type="submit"  class="btn btn-success btn btn-info btn-sm" value="View" ></form>';
                }

            },
        ]
    });
}

