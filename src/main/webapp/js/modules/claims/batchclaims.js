/**
 * Created by HP on 7/13/2017.
 */

var UTILITIES = UTILITIES || {};
$(function() {

    $(document).ready(function () {
        parenquiry(-2000);
        providerLov();
        processBatchClaims();
    })
});

function processBatchClaims(){
    $("#btn-process").on('click', function(){
        var arrss = [];
        var currTable = $('#med_clm_enquiry_tbl').dataTable();
        currTable.$('input[type="checkbox"]').each(function(){
            if(this.checked){
                arrss.push(this.value);
            }

        });
        if(arrss.length==0){
            bootbox.alert("No Transaction Selected to Process..");
            return;
        }
        // $('#myPleaseWait').modal({
   //     backdrop: 'static',
     //   keyboard: true
    // });

        var $currForm = $('#batch-process-form');
        var currValidator = $currForm.validate();
        if (!$currForm.valid()) {
            return;
        }

        var data = {};
        $currForm.serializeArray().map(function(x) {
            data[x.name] = x.value;
        });
        var url = "processBatchClaims";
        data.trans = arrss;


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
                $('#med_clm_enquiry_tbl').DataTable().ajax.reload(null,false);
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

function parenquiry(contractId){
    var url = "allbatchTrans";
    var currTable = $('#med_clm_enquiry_tbl').DataTable( {
        "processing": true,
        "serverSide": true,
        autoWidth: true,
        "order": [[ 1, "desc" ]],
        searching: false,
        "bLengthChange": false,
        "deferRender": true,
        "ajax": {
            'url': url,
            'data':{
                'contractId': contractId,
            },
        },
        lengthMenu: [ [10,15,20], [10,15,20] ],
        pageLength: 10,
        destroy: true,
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
            { "data": "parId" },
            { "data": "parNo" },
            { "data": "parRevisionNo" },
            { "data": "eventDate",
                "render": function ( data, type, full, meta ) {
                    return moment(full.eventDate).format('DD/MM/YYYY');
                }
            },
            { "data": "clientDef",
                "render": function ( data, type, full, meta ) {

                    return full.clientDef.fname+" "+full.clientDef.otherNames;
                }
            },
            { "data": "notDate" ,
                "render": function ( data, type, full, meta ) {
                    return moment(full.notDate).format('DD/MM/YYYY');
                }
            },
            { "data": "transUser",
                "render": function ( data, type, full, meta ) {

                    return full.transUser.username;
                }
            },
            { "data": "totalApprAmount",
                "render": function ( data, type, full, meta ) {
                    return UTILITIES.currencyFormat(full.totalApprAmount);
                }
            },
        ]
    } );

    $('#example-select-all').on('click', function(){
        // Check/uncheck all checkboxes in the table
        var rows = currTable.rows({ 'search': 'applied' }).nodes();
        $('input[type="checkbox"]', rows).prop('checked', this.checked);
    });

    $('#med_clm_enquiry_tbl tbody').on('change', 'input[type="checkbox"]', function(){
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


function providerLov(){
    if($("#provider-frm").filter("div").html() != undefined)
    {
        Select2Builder.initAjaxSelect2({
            containerId : "provider-frm",
            sort : 'contractNo',
            change: function(e, a, v){
                $("#provider-no").text(e.added.serviceProviders.name);
                $("#provider-id").val(e.added.spcId);
                parenquiry(e.added.spcId);
            },
            formatResult : function(a)
            {
                return a.serviceProviders.name;
            },
            formatSelection : function(a)
            {
                return a.serviceProviders.name;
            },
            initSelection: function (element, callback) {

            },
            id: "spcId",
            width:"250px",
            placeholder:"Select Service Provider"

        });
    }
}