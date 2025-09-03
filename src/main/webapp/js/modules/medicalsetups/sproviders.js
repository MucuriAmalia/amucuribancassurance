
$(function() {

    $(document).ready(function () {

        createProviderTypesList();
        addProviderTypes();
        deleteProviderTypes();
        createProviders();
        addProviders();
        populatePaymentModes();
        populateBankBranches();
        getNewServiceModal();
        saveProviderServices();
        savePrdrServiceActvty();
        getNewServiceActivityModal();

    });
});




function populateBankBranches(){
    if($("#bank-branch-frm").filter("div").html() != undefined)
    {
        Select2Builder.initAjaxSelect2({
            containerId : "bank-branch-frm",
            sort : 'branchName',
            change: function(e, a, v){
                $("#brn-id").val(e.added.pmId);
            },
            formatResult : function(a)
            {
                return a.branchName;
            },
            formatSelection : function(a)
            {
                return a.branchName;
            },
            initSelection: function (element, callback) {
                //var code  = $('#pm-id').val();
                //var name = $("#pm-name").val();
                //var data = {pmDesc:name,pmId:code};
                //callback(data);
            },
            id: "mspId",
            width:"250px",
            placeholder:"Select Bank Branch"

        });
    }
}

function populatePaymentModes(){
    if($("#pm-mode-frm").filter("div").html() != undefined)
    {
        Select2Builder.initAjaxSelect2({
            containerId : "pm-mode-frm",
            sort : 'pmDesc',
            change: function(e, a, v){
                $("#pm-id").val(e.added.pmId);
            },
            formatResult : function(a)
            {
                return a.pmDesc;
            },
            formatSelection : function(a)
            {
                return a.pmDesc;
            },
            initSelection: function (element, callback) {
                var code  = $('#pm-id').val();
                var name = $("#pm-name").val();
                var data = {pmDesc:name,pmId:code};
                callback(data);
            },
            id: "pmId",
            width:"250px",
            placeholder:"Select Payment Mode"

        });
    }
}


function addProviders(){
    $("#btn-add-providers").click(function() {
        if ($("#provd-types-pk").val() != '') {
            $("#prov-type-code").val($("#provd-types-pk").val());
            $('#provsModal').modal({
                backdrop: 'static',
                keyboard: true
            })
        }
        else {
            bootbox.alert("Select Service Provider Type to Add Service Provider");
        }
    });

    var $classForm = $('#providers-form');
    var validator = $classForm.validate();
    $('#saveProvdersBtn').click(function(){
        if (!$classForm.valid()) {
            return;
        }
        var $btn = $(this).button('Saving');
        var data = {};
        $classForm.serializeArray().map(function(x){data[x.name] = x.value;});
        var url = "createProviders";
        var request = $.post(url, data );
        request.success(function(){
            Swal.fire({
                title: 'Success',
                text: 'Record created/updated Successfully',
                icon: 'success'
            })
            $('#providerTbl').DataTable().ajax.reload();
            validator.resetForm();
            $('#providers-form').find("input[type=text],input[type=number],input[type=mobileNumber],input[type=emailFull],input[type=password],input[type=hidden], textarea,select").val("");
            $('#provsModal').modal('hide');
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


function deleteProviderTypes(){
    $("#delPrvdTypeBtn").click(function(){
        bootbox.confirm("Are you sure want to delete the record?", function(result) {
            if(result){
                if($("#provtype-code").val() != ''){
                    $.ajax({
                        type: 'GET',
                        url:  'deleteProviderTypes/' + $("#provtype-code").val(),
                        dataType: 'json',
                        async: true,
                        success: function(result) {
                            Swal.fire({
					title: 'Success',
					text: 'Record Deleted Successfully',
					icon: 'success'
				});
                            $('#sprovder-types').select2('val', null);
                            createProviderTypesList();
                            $('#prov-types-form').find("input[type=text],input[type=number],input[type=mobileNumber],input[type=emailFull],input[type=password],input[type=hidden], textarea,select").val("");
                             $('#provTypesModal').modal('hide');
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
                else{
                    bootbox.alert("No Provider Type to delete");
                }
            }
        });
    });
}


function addProviderTypes() {
    $("#btn-add-prov-types").click(function () {
        $('#prov-types-form').find("input[type=text],input[type=number],input[type=mobileNumber],input[type=emailFull],input[type=password],input[type=hidden], textarea,select").val("");
       $("#delPrvdTypeBtn").hide();
        $('#provTypesModal').modal({
            backdrop: 'static',
            keyboard: true
        })

    });

    $("#btn-edit-prov-types").click(function(){
        if($("#provtype-code").val() != ''){
            $("#delPrvdTypeBtn").show();
            $('#provTypesModal').modal({
                backdrop: 'static',
                keyboard: true
            })
        }
        else{
            Swal.fire({
                title: 'Error',
                text: 'Select Class to Edit',
                icon: 'error'
            });
        }
    });

    var $classForm = $('#prov-types-form');
    var validator = $classForm.validate();
    $('#saveProvderTypeBtn').click(function(){
        if (!$classForm.valid()) {
            return;
        }
        var $btn = $(this).button('Saving');
        var data = {};
        $classForm.serializeArray().map(function(x){data[x.name] = x.value;});
        var url = "createProviderTypes";
        var request = $.post(url, data );
        request.success(function(){
            Swal.fire({
                title: 'Success',
                text: 'Record created/updated Successfully',
                icon: 'success'
            })
            $('#sprovder-types').select2('val', null);
            createProviderTypesList();
            validator.resetForm();
            $('#prov-types-form').find("input[type=text],input[type=number],input[type=mobileNumber],input[type=emailFull],input[type=password],input[type=hidden], textarea,select").val("");
            $('#provTypesModal').modal('hide');
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

function createProviderTypesList(){
    if($("#sprovder-types").filter("div").html() != undefined)
    {
        Select2Builder.initAjaxSelect2({
            containerId : "sprovder-types",
            sort : 'shtDesc',
            change: function(e, a, v){
                $("#provd-types-pk").val(e.added.id);
                $("#provtype-code").val(e.added.id);
                $("#provtype-id").val(e.added.shtDesc);
                $("#provtype-name").val(e.added.desc);
                createProviders();
            },
            formatResult : function(a)
            {
                return a.shtDesc
            },
            formatSelection : function(a)
            {
                return a.shtDesc
            },
            initSelection: function (element, callback) {
//	            	var code = $("#obId").val();
//	                var name = $("#ob-name").val();
//	                model.accounts.branch.brnCode = code;
//	                var data = {obName:name,obId:code};
//	                callback(data);
            },
            id: "id",
            placeholder:"Select Service Provider Types"
        });
    }
}



function createProviders(){
    var url = "providerlist/0";
    if($("#class-pk").val() != ''){
        url = "providerlist/"+$("#provd-types-pk").val();
    }
    var currTable = $('#providerTbl').DataTable( {
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
            { "data": "name" },
            { "data": "contactPerson" },
            { "data": "pinNo" },
            { "data": "telNumber" },
            { "data": "status" },
            {
                "data": "bankBranches",
                "render": function ( data, type, full, meta ) {
                    if(full.bankBranches)
                    return full.bankBranches.branchName;
                    else return "";
                }

            },
            { "data": "accountNumber" },
            {
                "data": "paymentModes",
                "render": function ( data, type, full, meta ) {
                    if(full.paymentModes)
                        return full.paymentModes.pmDesc;
                    else return "";
                }

            },
            {
                "data": "mspId",
                "render": function ( data, type, full, meta ) {
                    return '<button type="button" class="btn btn-success btn btn-info btn-sm" type="button" data-subclass='+encodeURI(JSON.stringify(full)) + ' onclick="editSubclass(this);"><i class="fa fa-pencil-square-o"></button>';
                }

            },
            {
                "data": "mspId",
                "render": function ( data, type, full, meta ) {
                    return '<button type="button" class="btn btn-danger btn btn-danger btn-sm" data-subclass='+encodeURI(JSON.stringify(full)) + ' onclick="deleteSubclass(this);"><i class="fa fa-trash-o"></button>';
                }

            },
        ]
    } );
    $('#providerTbl tbody').on( 'click', 'tr', function () {

        $(this).addClass('table-primary').siblings().removeClass('table-primary');

        var d = currTable.row( this ).data();
       // console.log("providerCode=");
        if(d){
            $("#service-prvdr-code").val(d.mspId);
            //console.log("providerCode="+d.mspId);
            createSrvPrdServices(d.mspId);

        }
    } );
    return currTable;
}

function createSrvPrdServiceAct(serviceCode){

    var url = "servicesActvty/"+serviceCode;
    var table = $('#srvcActivityList').DataTable( {
        "processing": true,
        "serverSide": true,
        "ajax": url,
        lengthMenu: [ [10, 15], [10, 15] ],
        pageLength: 10,
        destroy: true,
        "columns": [
            { "data": "activities" ,
                "render": function ( data, type, full, meta ) {
                    if(full.activities){
                        return full.activities.benShtDesc;
                    }
                    else return "";
                }},
            { "data": "activities" ,
                "render": function ( data, type, full, meta ) {
                    if(full.activities){
                        return full.activities.benDesc;
                    }
                    else return "";
                }},
            { "data": "activities" ,
                "render": function ( data, type, full, meta ) {
                    if(full.activities){
                        return full.activities.section.desc;
                    }
                    else return "";
                }},
            { "data": "activityFee" ,
                "render": function ( data, type, full, meta ) {
                    if(full.activityFee){
                        return UTILITIES.currencyFormat(full.activityFee);
                    }
                    else return 0;
                }},
            {
                "data": "wefDate",
                "render": function (data, type, full, meta) {
                    return moment(full.wefDate).format('DD/MM/YYYY');
                }
            },
            {
                "data": "wetDate",
                "render": function (data, type, full, meta) {
                    if (full.wetDate)
                        return moment(full.wetDate).format('DD/MM/YYYY');
                    else "";
                }
            },
            {
                "data": "spaId",
                "render": function ( data, type, full, meta ) {
                    return '<button type="button" class="btn btn-success btn btn-info btn-sm" data-benefits='+encodeURI(JSON.stringify(full)) + ' onclick="editBenefits(this);"><i class="fa fa-pencil-square-o"></button>';
                }

            },
            {
                "data": "spaId",
                "render": function ( data, type, full, meta ) {
                    return '<button type="button" class="btn btn-danger btn btn-danger btn-sm" data-benefits='+encodeURI(JSON.stringify(full)) + ' onclick="confirmBenefits(this);"><i class="fa fa-trash-o"></button>';
                }

            },
        ]
    } );
    $('#srvcActivityList tbody').on( 'click', 'tr', function () {

        $(this).addClass('table-primary').siblings().removeClass('table-primary');

        var d = table.row( this ).data();
        // console.log("providerCode=");
        if(d){
            //$("#service-code").val(d.spsId);
            //console.log("Service="+d.spsId);
            //createSrvPrdServiceAct(d.spsId);

        }
    } );

    return table;
}
function createSrvPrdServices(providerCode){

    var url = "providerservices/"+providerCode;
    var table = $('#prvdrsrvcList').DataTable( {
        "processing": true,
        "serverSide": true,
        "ajax": url,
        lengthMenu: [ [10, 15], [10, 15] ],
        pageLength: 10,
        destroy: true,
        "columns": [
            { "data": "medicalServices" ,
                "render": function ( data, type, full, meta ) {
                    if(full.medicalServices){
                        return full.medicalServices.labShtDesc;
                    }
                    else return "";
                }},
            { "data": "medicalServices" ,
                "render": function ( data, type, full, meta ) {
                    if(full.medicalServices){
                        return full.medicalServices.labDesc;
                    }
                    else return "";
                }},
            { "data": "medicalServices" ,
                "render": function ( data, type, full, meta ) {
                    if(full.medicalServices){
                        return full.medicalServices.cptCode;
                    }
                    else return "";
                }},
            {
                "data": "wefDate",
                "render": function (data, type, full, meta) {
                    return moment(full.wefDate).format('DD/MM/YYYY');
                }
            },
            {
                "data": "wetDate",
                "render": function (data, type, full, meta) {
                    if (full.wetDate)
                    return moment(full.wetDate).format('DD/MM/YYYY');
                    else "";
                }
            },
            {
                "data": "spsId",
                "render": function ( data, type, full, meta ) {
                    return '<button type="button" class="btn btn-success btn btn-info btn-sm" data-benefits='+encodeURI(JSON.stringify(full)) + ' onclick="editBenefits(this);"><i class="fa fa-pencil-square-o"></button>';
                }

            },
            {
                "data": "spsId",
                "render": function ( data, type, full, meta ) {
                    return '<button type="button" class="btn btn-danger btn btn-danger btn-sm" data-benefits='+encodeURI(JSON.stringify(full)) + ' onclick="confirmBenefits(this);"><i class="fa fa-trash-o"></button>';
                }

            },
        ]
    } );
    $('#prvdrsrvcList tbody').on( 'click', 'tr', function () {

        $(this).addClass('table-primary').siblings().removeClass('table-primary');

        var d = table.row( this ).data();
        // console.log("providerCode=");
        if(d){
            $("#service-code").val(d.spsId);
            console.log("Service="+d.spsId);
            createSrvPrdServiceAct(d.spsId);

        }
    } );
    return table;
}

function getNewServiceModal() {
    $("#btn-add-prvdr-srvc").click(function () {
        if ($("#service-prvdr-code").val() != '') {
            $("#serv-provider-code").val($("#service-prvdr-code").val());
            getNewServices("");
            $('#providerServiceModal').modal({
                backdrop: 'static',
                keyboard: true
            });
        }
        else {
            bootbox.alert("Provider Details are not available to add A service")
        }
    });

    $("#searchService").click(function () {
            getNewServices($("#service-name-search").val());


    });
}


function getNewServiceActivityModal() {
    $("#btn-add-srvc-act").click(function () {
        if ($("#service-code").val() != '') {
            $("#serv-code").val($("#service-code").val());
            getNewServiceActivities("");
            $('#serviceActityModal').modal({
                backdrop: 'static',
                keyboard: true
            });
        }
        else {
            bootbox.alert("Service Details are not available to add activity")
        }
    });

    $("#searchActivity").click(function () {
        getNewServiceActivities($("#activity-name-search").val());


    });
}


function saveProviderServices(){
    var arr = [];
    $("#saveProviderServiceBtn").click(function(){
        $("#servicestbl tbody").find('input[name="record"]').each(function(){
            if($(this).is(":checked")){
                arr.push($(this).attr("id"));
            }
        });
        if(arr.length==0){
            bootbox.alert("No services Selected to attach..");
            return;
        }
        var $currForm = $('#provider-service-form');
        var currValidator = $currForm.validate();
        if (!$currForm.valid()) {
            return;
        }

        var data = {};
        $currForm.serializeArray().map(function(x) {
            data[x.name] = x.value;
        });
        var url = "createProviderServices";
        data.services = arr;


        $.ajax({
            url : url,
            type : "POST",
            data : JSON.stringify(data),
            success : function(s) {
                Swal.fire({
                title: 'Success',
                text: 'Record created/updated Successfully',
                icon: 'success'
            });
                $('#prvdrsrvcList').DataTable().ajax.reload();
                $('#providerServiceModal').modal('hide');
                arr = [];
            },
            error : function(jqXHR, textStatus, errorThrown) {
                console.log(jqXHR);
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



function savePrdrServiceActvty(){
    var arr = [];
    $("#saveServiceActivityBtn").click(function(){
        $("#activitytbl tbody").find('input[name="record"]').each(function(){
            if($(this).is(":checked")){
                arr.push($(this).attr("id"));
            }
        });
        if(arr.length==0){
            bootbox.alert("No activity Selected to attach..");
            return;
        }
        var $currForm = $('#service-activity-form');
        var currValidator = $currForm.validate();
        if (!$currForm.valid()) {
            return;
        }

        var data = {};
        $currForm.serializeArray().map(function(x) {
            data[x.name] = x.value;
        });
        var url = "createPrvdrSrvActivity";
        data.serviceactivities = arr;


        $.ajax({
            url : url,
            type : "POST",
            data : JSON.stringify(data),
            success : function(s) {
                Swal.fire({
                title: 'Success',
                text: 'Record created/updated Successfully',
                icon: 'success'
            });
                $('#srvcActivityList').DataTable().ajax.reload();
                $('#serviceActityModal').modal('hide');
                arr = [];
            },
            error : function(jqXHR, textStatus, errorThrown) {
                console.log(jqXHR);
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


function getNewServices(search){
    // $('#myPleaseWait').modal({
   //     backdrop: 'static',
     //   keyboard: true
    // });
    $.ajax({
        type: 'GET',
        url:  'getUnassignedServices',
        data: {"providerId": $("#service-prvdr-code").val(),"search":search},
        async: true,
        success: function(result) {
            // $('#myPleaseWait').modal('hide');
            $("#servicestbl tbody").each(function(){
                $(this).remove();
            });
            for(var res in result){
                var markup = "<tr><td><input type='checkbox' name='record' id='"+result[res].labId+
                    "'></td><td>"
                    + result[res].labShtDesc + "</td><td>"
                    + result[res].labDesc + "</td><td>"
                    + result[res].cptCode + "</td><td>"
                    + result[res].upperLimit + "</td></tr>";
                $("#servicestbl").append(markup);
            }

        },
        error: function(jqXHR, textStatus, errorThrown) {
             Swal.fire({
                title: 'Error',
                text: jqXHR.responseText,
                icon: 'error'
            })
        }
    });

}

function getNewServiceActivities(search){
    // $('#myPleaseWait').modal({
   //     backdrop: 'static',
     //   keyboard: true
    // });
    $.ajax({
        type: 'GET',
        url:  'getUnassignedServiceActvty',
        data: {"serviceId": $("#service-code").val(),"search":search},
        async: true,
        success: function(result) {
            // $('#myPleaseWait').modal('hide');
            $("#activitytbl tbody").each(function(){
                $(this).remove();
            });
            for(var res in result){
                var markup = "<tr><td><input type='checkbox' name='record' id='"+result[res].benId+
                    "'></td><td>"
                    + result[res].benShtDesc + "</td><td>"
                    + result[res].benDesc + "</td><td>"
                    + result[res].section.desc + "</td></tr>";
                $("#activitytbl").append(markup);
            }

        },
        error: function(jqXHR, textStatus, errorThrown) {
             Swal.fire({
                title: 'Error',
                text: jqXHR.responseText,
                icon: 'error'
            })
        }
    });

}


