$(function() {

    $(document).ready(function () {
        createProvidersContracts();
        addProviderContracts();
        $(".datepicker-input").each(function() {
            $(this).datetimepicker({
                format: 'DD/MM/YYYY'
            });

        });
    });
});


function addProviderContracts() {
    $("#btn-add-contracts").on("click", function () {
         providerLov();
        $('#prov-contract-form').find("input[type=text],input[type=mobileNumber],input[type=emailFull],input[type=password],input[type=hidden],input[type=number], textarea,select").val("");
        $('#provContractModal').modal('show');
        return;
    });

    var $classForm = $('#prov-contract-form');
    var validator = $classForm.validate();
    $('#saveProvderContractsBtn').click(function(){
        if (!$classForm.valid()) {
            return;
        }
        var $btn = $(this).button('Saving');
        var data = {};
        $classForm.serializeArray().map(function(x){data[x.name] = x.value;});
        var url = "createProviderContract";
        var request = $.post(url, data );
        request.success(function(){
            Swal.fire({
                title: 'Success',
                text: 'Record created/updated Successfully',
                icon: 'success'
            })
            $('#providerContractTbl').DataTable().ajax.reload();
            validator.resetForm();
            $('#prov-contract-form').find("input[type=text],input[type=number],input[type=mobileNumber],input[type=emailFull],input[type=password],input[type=hidden], textarea,select").val("");
            $('#provContractModal').modal('hide');
        });

        request.error(function(jqXHR, textStatus, errorThrown){
             Swal.fire({
                title: 'Error',
                text: jqXHR.responseText,
                icon: 'error'
            })
        });
        request.always(function(){
            $btn.button('reset');
        });
    });
}


function providerLov(){
    if($("#provider-frm").filter("div").html() != undefined)
    {
        Select2Builder.initAjaxSelect2({
            containerId : "provider-frm",
            sort : 'name',
            change: function(e, a, v){
                 $("#provider-type").text(e.added.serviceProviderTypes.desc);
                 $("#provider-id").val(e.added.mspId);
            },
            formatResult : function(a)
            {
                return a.name;
            },
            formatSelection : function(a)
            {
                return a.name;
            },
            initSelection: function (element, callback) {

            },
            id: "mspId",
            width:"250px",
            placeholder:"Select Service Provider"

        });
    }
}



function createProvidersContracts(){
    var url = "providercontracts";
    var currTable = $('#providerContractTbl').DataTable( {
        "processing": true,
        "serverSide": true,
        autoWidth: true,
        "ajax": {
            'url': url,
        },
        lengthMenu: [ [10], [10] ],
        pageLength: 10,
        destroy: true,
        "columns": [
            { "data": "contractNo" },
            { "data": "contractType" },
            {
                "data": "serviceProviders",
                "render": function ( data, type, full, meta ) {
                    if(full.serviceProviders)
                        return full.serviceProviders.name;
                    else return "";
                }

            },
            {
                "data": "serviceProviders",
                "render": function ( data, type, full, meta ) {
                    if(full.serviceProviders)
                        return full.serviceProviders.serviceProviderTypes.desc;
                    else return "";
                }

            },
            { "data": "status" },
            { "data": "issueDate",
                "render": function ( data, type, full, meta ) {
                    return moment(full.issueDate).format('DD/MM/YYYY');
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
            {
                "data": "spcId",
                "render": function ( data, type, full, meta ) {
                    return '<button type="button" class="btn btn-success btn btn-info btn-sm" type="button" data-subclass='+encodeURI(JSON.stringify(full)) + ' onclick="editSubclass(this);"><i class="fa fa-pencil-square-o"></button>';
                }

            },
            {
                "data": "spcId",
                "render": function ( data, type, full, meta ) {
                    return '<button type="button" class="btn btn-danger btn btn-danger btn-sm" data-subclass='+encodeURI(JSON.stringify(full)) + ' onclick="deleteSubclass(this);"><i class="fa fa-trash-o"></button>';
                }

            },
        ]
    } );
    return currTable;
}