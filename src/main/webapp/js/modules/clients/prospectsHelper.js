var PROSPECTS_UTILITIES = PROSPECTS_UTILITIES || {};




PROSPECTS_UTILITIES.populateProspectInsured = function (){
    if($("#insured-prs-frm").filter("div").html() != undefined)
    {
        Select2Builder.initAjaxSelect2({
            containerId : "insured-prs-frm",
            sort : 'fname',
            change: function(e, a, v){
                //$("#client-id").val(e.added.tenId);
                $("#insured-code").val(e.added.tenId);
                //$("#insured-id").val(e.added.tenId);
            },
            formatResult : function(a)
            {
                return a.fname+" "+a.otherNames;
            },
            formatSelection : function(a)
            {
                return a.fname+" "+a.otherNames;
            },
            initSelection: function (element, callback) {
                var code = $("#insured-code").val();
                var name = $("#insured-name").val();
                var othernames = $("#insured-other-name").val();
                var data = {fname:name,otherNames:othernames,tenId:code};
                callback(data);
            },
            id: "tenId",
            width:"250px",
            placeholder:"Select Insured"

        });
    }
}


PROSPECTS_UTILITIES.populateProspectLov = function (){
    if($("#prospects-frm").filter("div").html() != undefined)
    {
        Select2Builder.initAjaxSelect2({
            containerId : "prospects-frm",
            sort : 'fname',
            change: function(e, a, v){
                $("#client-id").val(e.added.tenId);
                $("#insured-name").val(e.added.fname);
                $("#insured-code").val(e.added.tenId);
                $("#insured-other-name").val(e.added.otherNames);
                $("#insured-type").val("P");
                PROSPECTS_UTILITIES.populateProspectInsured();
                $("#btn-add-prs").val("Edit");

            },
            formatResult : function(a)
            {
                return a.fname+" "+a.otherNames;
            },
            formatSelection : function(a)
            {
                return a.fname+" "+a.otherNames;
            },
            initSelection: function (element, callback) {
                var code = $("#client-id").val();
                var name = $("#client-f-name").val();
                var othernames = $("#client-other-name").val();
                var data = {fname:name,otherNames:othernames,tenId:code};
                callback(data);
            },
            id: "tenId",
            placeholder:"Select Prospect"

        });

        $("#prospects-frm").on("select2-removed", function(e) {
            $("#client-id").val("");
            $("#insured-name").val("");
            $("#insured-code").val("");
            $("#insured-other-name").val("");
            $("#insured-type").val("");
            this.populateProspectInsured();
            $("#btn-add-prs").val("New");
        });
    }
}

PROSPECTS_UTILITIES.populateClientTypeLov = function(){
    if($("#clnt-client-type").filter("div").html() != undefined)
    {
        Select2Builder.initAjaxSelect2({
            containerId : "clnt-client-type",
            sort : 'clientType',
            change: function(e, a, v){
                $("#clnt-type-id").val(e.added.typeId);
                $("#clnt-type-name").val(e.added.typeDesc);
                $("#clnt-type-code").val(e.added.clientType);
                if (e.added.clientType === 'C' || e.added.clientType === '') {
                    $("#gender,.gender,#lblGender").hide();
                    $("#lbl-dob").html("Date of Incorporation");
                    $("#lbl-id-no").html("Registration No.");
                    $("#clnt-title").select2("enable", false);
                    $('#clnt-title').select2('val', null);
                    $("#clnt-title-id").val("");
                    $("span.othernames").hide();
                    $("#client-photo").html("Logo");
                    $(".employee-info").hide();
                    $(".clientTitleDiv").hide();
                    $(".clientRefDiv").hide();
                    $("#id-no").attr("placeholder", "Company Reg. No");
                    $(".passportDiv").hide();
                    $(".firstNameDiv label").html("Company Name");
                    $(".firstNameDiv div input").attr("placeholder", "Company name");
                    $(".additionalName").hide();
                    $(".dobDiv div input").attr("placeholder", "Date of Incorporation");
                    $(".statusDiv").hide();
                    $(".clientAuthorizedDiv").hide();
                } else if (e.added.clientType === 'I') {
                    $(".employee-info h4").html("Employment Information");
                    $(".clientAuthorizedDiv").hide();
                    $(".statusDiv").hide();
                    $(".dobDiv div input").attr("placeholder", "Date of Birth");
                    $(".additionalName").show();
                    $(".firstNameDiv div input").attr("placeholder", "First Name");
                    $(".firstNameDiv label").html("First Name");
                    $(".passportDiv").show();
                    $("#id-no").attr("placeholder", "Client ID No");
                    $(".clientTitleDiv").show();
                    $(".clientRefDiv").hide();
                    $("#gender,.gender,#lblGender").show();
                    $("#lbl-dob").html("Date of Birth");
                    $("#lbl-id-no").html("ID No.");
                    $("#clnt-title").select2("enable", true);
                    $('#clnt-title').select2('val', null);
                    $("#clnt-title-id").val("");
                    $("span.othernames").show();
                    $("#client-photo").html("Photo");
                    $(".employee-info").show();
                }
            },
            formatResult : function(a)
            {
                return  a.typeDesc
            },
            formatSelection : function(a)
            {
                return  a.typeDesc
            },
            initSelection: function (element, callback) {
                var code = $("#clnt-type-id").val();
                var name = $("#clnt-type-name").val();
                var data = {typeDesc:name,typeId:code};
                callback(data);
            },
            id: "typeId",
            placeholder:"Select Client Type",

        });
    }
}

PROSPECTS_UTILITIES.populateQuotProspects =function (prospectId){
    $.ajax({
        type: 'GET',
        url: 'getProspectDetails/'+prospectId,
        dataType: 'json',
        async: true,
        success: function (result) {
            $("#prospect-id-pk").val(result.tenId);
            $("#prs-sht-desc").val(result.prospShtDesc);
            $("#fname").val(result.fname);
            $("#other-names").val(result.otherNames);
            $("#prosp-email").val(result.emailAddress);
            $("#phone-no").val(result.phoneNo);
            $("#clnt-type-id").val(result.clientTypeId);
            $("#clnt-type-name").val(result.clientType);
            $("#other-names").val(result.otherNames);
            $('#sub-agent-id').val(result.acctId);
            $('#sub-agent-name').val(result.acctName);
            $('#prsbrn-id').val(result.branchId);
            $('#prsbrn-name').val(result.branchName);
            PROSPECTS_UTILITIES.populateClientTypeLov();
            PROSPECTS_UTILITIES.populateSubAgentsLov();
            PROSPECTS_UTILITIES.populateUserBranches();
            $("#pin-no").val(result.pinNo);
            $("#txt-comment").val(result.comment);
            if(result.dob){
                $("#prs-dob").val(moment(result.dob).format('DD/MM/YYYY'));
            }
            $("#id-no").val(result.idNo);
            $("#selCategory").val(result.category);
            $("#gender").val(result.gender);
            $("#sel3").val(result.status);
            $("#sel3").attr("readonly",false);
            $("#prs-pref-sms-id").val(result.prefixId);
            $("#prs-pref-sms-name").val(result.prefix);
            $('#createProspectModal').modal({
                backdrop: 'static',
                keyboard: true
            })
        },
        error: function (jqXHR, textStatus, errorThrown) {
             Swal.fire({
                title: 'Error',
                text: jqXHR.responseText,
                icon: 'error'
            })
        }
    });

}

PROSPECTS_UTILITIES.addProspects = function (updateType) {
    $("#btn-add-prs").click(function () {
        if(typeof $("#client-id").val() !== "undefined" && $("#client-id").val().length > 0){
                PROSPECTS_UTILITIES.populateQuotProspects($("#client-id").val());
        }
        else {
            $('#prospect-form').find("input[type=text],input[type=number],input[type=mobileNumber],input[type=emailFull],input[type=password],input[type=hidden], textarea,select").val("");
            $("#sel3").val("A");
            $("#sel3").attr("readonly", true);
            $('#clnt-client-type').select2('val', null);
            $('#sub-agent-frm').select2('val', null);
            $('#branch-frm').select2('val', null);
            $('#clnt-client-type').attr('value', '');
            $('#sub-agent-frm').attr('value', '');
            $('#branch-frm').attr('value', '');
            $("#prs-pref-sms-id").val("");
            $("#prs-pref-sms-name").val("");
            if (updateType && updateType === 'P') {
                PROSPECTS_UTILITIES.defaultCountry();
                PROSPECTS_UTILITIES.populateSubAgentsLov();
                PROSPECTS_UTILITIES.populateUserBranches();
            }
            $('#createProspectModal').modal({
                backdrop: 'static',
                keyboard: true
            });
        }
    });

    $('#saveProspectsBtn').click(function(){
        var $classForm = $('#prospect-form');
        var validator = $classForm.validate();
        if (!$classForm.valid()) {
            return;
        }
        var $btn = $(this).button('Saving');
        var data = {};
        $classForm.serializeArray().map(function(x){data[x.name] = x.value;});
        var url = SERVLET_CONTEXT+"/protected/clients/setups/createProspect";
        var request = $.post(url, data );
        request.success(function(result){
            Swal.fire({
                title: 'Success',
                text: 'Record created/updated Successfully',
                icon: 'success'
            })
            if(updateType && updateType==='P'){
                $('#tenTbl').DataTable().ajax.reload();

            }
            else if(updateType==='Q'){
                $("#client-id").val(result.tenId);
                $("#client-f-name").val(result.fname);
                $("#client-other-name").val(result.otherNames);
                PROSPECTS_UTILITIES.populateProspectInsured();
                $("#insured-name").val(result.fname);
                $("#insured-code").val(result.tenId);
                $("#insured-other-name").val(result.otherNames);
                PROSPECTS_UTILITIES.populateProspectLov();
            }
            validator.resetForm();
            $('#prospect-form').find("input[type=text],input[type=number],input[type=mobileNumber],input[type=emailFull],input[type=password],input[type=hidden], textarea,select").val("");
            $('#createProspectModal').modal('hide');
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


PROSPECTS_UTILITIES.createProspectList = function (){
    var url = SERVLET_CONTEXT+ "/protected/clients/setups/prospects";
    return $('#tenTbl').DataTable({
        "processing": true,
        "serverSide": true,
        autoWidth: true,
        "searching": true,
        "ajax": {
            'url': url
        },
        lengthMenu: [[10, 15], [10, 15]],
        pageLength: 10,
        destroy: true,
        "columns": [
            {"data": "prospShtDesc"},
            {
                "data": "fname",
                "render": function (data, type, full, meta) {
                    return full.fname + " " + full.otherNames;
                }
            },
            {
                "data": "phoneNo",
                "render": function (data, type, full, meta) {
                    return full.phoneNo;
                }
            },
            {
                "data": "tenantType",
                "render": function (data, type, full, meta) {
                    return full.clientType;
                }

            },
            {
                "data": "dateofBirth",
                "render": function (data, type, full, meta) {
                    return full.dateofBirth;
                }
            },
            {
                "data": "status",
                "render": function (data, type, full, meta) {
                    if (!full.status || full.status === "T") {
                        return "Terminated";
                    } else if (full.status === "A")
                        return "Active";
                }
            },
            {
                "data": "category",
                "render": function (data, type, full, meta) {
                    if (!full.category || full.category === "H") {
                        return "Hot";
                    } else if (full.status === "W")
                        return "Warm";
                    else if (full.status === "C")
                        return "Cold";
                }
            },
            {"data": "branchName"},
            // {"data": "acctName"},
            {"data": "comment"},
            {"data": "createdBy",
                "render": function ( data, type, full, meta ) {

                    return full.username;
                }
            },
            {
                "data": "tenId",
                "render": function (data, type, full, meta) {
                    return '<button type="button" class="btn btn-success btn btn-info btn-sm" type="button" data-prospects=' + encodeURI(JSON.stringify(full)) + ' onclick="PROSPECTS_UTILITIES.editProspects(this);"><i class="fa fa-pencil-square-o"></button>';
                }

            },
            {
                "data": "tenId",
                "render": function (data, type, full, meta) {
                    return '<button type="button" class="btn btn-danger btn btn-danger btn-sm" data-prospects=' + encodeURI(JSON.stringify(full)) + ' onclick="PROSPECTS_UTILITIES.deleteProspects(this);"><i class="fa fa-trash-o"></button>';
                }

            },
        ]
    });
}

PROSPECTS_UTILITIES.editProspects = function (button){
    var prospects = JSON.parse(decodeURI($(button).data("prospects")));
    if(prospects["status"]){
        if(prospects["status"]==="T"){
            bootbox.alert("Cannot make changes to terminated Prospect");
            return;
        }
    }
    $("#prospect-id-pk").val(prospects["tenId"]);
    $("#prs-sht-desc").val(prospects["prospShtDesc"]);
    $("#fname").val(prospects["fname"]);
    $("#other-names").val(prospects["otherNames"]);
    $("#phone-no").val(prospects["phoneNo"]);
    $("#clnt-type-id").val(prospects["clientTypeId"]);
    $("#clnt-type-name").val(prospects["clientType"]);
    $("#other-names").val(prospects["otherNames"]);
    $("#prosp-email").val(prospects["emailAddress"]);
    $('#sub-agent-id').val(prospects["acctId"]);
    $('#sub-agent-name').val(prospects["acctName"]);
    $('#prsbrn-id').val(prospects["branchId"]);
    $('#prsbrn-name').val(prospects["branchName"]);
    PROSPECTS_UTILITIES.populateClientTypeLov();
    PROSPECTS_UTILITIES.populateSubAgentsLov();
    PROSPECTS_UTILITIES.populateUserBranches();
    $("#pin-no").val(prospects["pinNo"]);
    $("#txt-comment").val(prospects["comment"]);
    if(prospects["dateofBirth"]){
        $("#prs-dob").val(prospects["dateofBirth"]);
    }
    $("#id-no").val(prospects["idNo"]);
    $("#selCategory").val(prospects["category"]);
    $("#gender").val(prospects["gender"]);
    $("#sel3").val(prospects["status"]);
    $("#sel3").attr("readonly",false);
    $("#prs-pref-sms-id").val(prospects["prefixId"]);
    $("#prs-pref-sms-name").val(prospects["prefix"]);
    this.defaultCountry();
    $('#createProspectModal').modal({
        backdrop: 'static',
        keyboard: true
    })

}

PROSPECTS_UTILITIES.populateUserBranches = function(){
    if($("#branch-frm").filter("div").html() != undefined)
    {
        Select2Builder.initAjaxSelect2({
            containerId : "branch-frm",
            sort : 'obName',
            change: function(e, a, v){
                $("#prsbrn-id").val(e.added.obId);
            },
            formatResult : function(a)
            {
                return a.obName;
            },
            formatSelection : function(a)
            {
                return a.obName;
            },
            initSelection: function (element, callback) {
                var code  = $('#prsbrn-id').val();
                var name = $("#prsbrn-name").val();
                var data = {obName:name,obId:code};
                callback(data);
            },
            id: "obId",
            width:"250px",
            placeholder:"Select Branch"

        });
    }
};

    $(document).ready(function() {
    // Initialize when modal opens
    $('#createProspectModal').on('shown.bs.modal', function() {
        PROSPECTS_UTILITIES.populateUserBranches();
    });

    // Also initialize if modal is already open (during page reload)
    if ($('#createProspectModal').hasClass('show')) {
        PROSPECTS_UTILITIES.populateUserBranches();
    }
});

PROSPECTS_UTILITIES.populateSubAgentsLov = function(){
    if($("#sub-agent-frm").filter("div").html() != undefined)
    {
        Select2Builder.initAjaxSelect2({
            containerId : "sub-agent-frm",
            sort : 'name',
            change: function(e, a, v){
                $("#sub-agent-id").val(e.added.acctId);

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
                var code  = $('#sub-agent-id').val();
                var name = $("#sub-agent-name").val();
                var data = {name:name,acctId:code};
                callback(data);
            },
            id: "acctId",
            width:"250px",
            placeholder:"Select Sub Agent"

        });

        $("#sub-agent-frm").on("select2-removed", function(e) {
            $("#sub-agent-id").val('');
        })
    }
};



PROSPECTS_UTILITIES.deleteProspects = function (button){
    var prospects = JSON.parse(decodeURI($(button).data("prospects")));
    bootbox.confirm("Are you sure want to delete "+prospects["fname"]+"?", function(result) {
        if(result){
            $.ajax({
                type: 'GET',
                url:  'deleteProspect/' + prospects["tenId"],
                dataType: 'json',
                async: true,
                success: function(result) {
                   Swal.fire({
                title: 'Success',
                text: 'Record created/updated Successfully',
                icon: 'success'
            })
                    $('#tenTbl').DataTable().ajax.reload();
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
    });
}

PROSPECTS_UTILITIES.defaultCountry = function(){
    $.ajax( {
        url: SERVLET_CONTEXT+'/protected/clients/setups/getDefaultCountry',
        type: 'GET',
        processData: false,
        contentType: false,
        success: function (s ) {
            if(s){
                $("#cou-id").val(s.couCode);
                $("#cou-prefix").val(s.prefix);
                $("#cou-name").val(s.couName);
                PROSPECTS_UTILITIES.populateSmsPrefix(s.couCode);
            }
        },
        error: function(xhr, error){

        }
    });

}

PROSPECTS_UTILITIES.populateSmsPrefix =function (couCode) {
    if ($("#prs-sms-pref").filter("div").html() != undefined) {
        Select2Builder.initAjaxSelect2({
            containerId: "prs-sms-pref",
            sort: 'prefixName',
            change: function (e, a, v) {
                $("#prs-pref-sms-id").val(e.added.prefixId);
                $("#prs-pref-sms-name").val(e.added.prefixName);
            },
            formatResult: function (a) {
                return a.prefixName
            },
            formatSelection: function (a) {
                return a.prefixName
            },
            initSelection: function (element, callback) {
                var code = $("#prs-pref-sms-id").val();
                var name = $("#prs-pref-sms-name").val();
                var data = {prefixName:name,prefixId:code};
                callback(data);
            },
            id: "prefixId",
            placeholder: "Prefix",
            params: {couCode: couCode}

        });
    }
}