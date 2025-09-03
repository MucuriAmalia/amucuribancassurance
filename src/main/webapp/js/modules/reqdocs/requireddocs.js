/**
 * Created by peter on 3/5/2017.
 */
var SETUPS = SETUPS || {};
$(function() {

    $(document).ready(function () {

        SETUPS.createRequiredDocs();
        SETUPS.addReqdDocs();
        SETUPS.saveRequiredDocs();
        SETUPS.createSubclassLov();
        SETUPS.getSubClassReqdDocsModal();
        SETUPS.saveSubclassReqDocs();
        SETUPS.saveSubRequiredDocs();
    });
});


SETUPS.createSubclassLov = function(){
    if($("#sub-class-def").filter("div").html() != undefined)
    {
        Select2Builder.initAjaxSelect2({
            containerId : "sub-class-def",
            sort : 'subDesc',
            change: function(e, a, v){
                $("#sub-code").val(e.added.subId);
                SETUPS.createSclReqDocs(e.added.subId);
            },
            formatResult : function(a)
            {
                return a.subDesc
            },
            formatSelection : function(a)
            {
                return a.subDesc
            },
            initSelection: function (element, callback) {

            },
            id: "subId",
            width:"200px",
            placeholder:"Select Sub Class"
        });

    }
}

SETUPS.addReqdDocs = function() {
    $("#btn-add-req-doc").click(function () {
        $('#reqd-doc-form').find("input[type=text],input[type=number],input[type=mobileNumber],input[type=emailFull],input[type=password],input[type=hidden], textarea,select").val("");
        $("#reqd-doc-form input:checkbox").prop("checked", false);
        $(".validity").hide();
        $('#reqdocsModal').modal({
            backdrop: 'static',
            keyboard: true
        })
    });

    $("#chk-lop").change(function() {
        if(this.checked) {
            $(".validity").show();
        }
        else{
            $(".validity").hide();
        }
    });
}


SETUPS.saveSubRequiredDocs= function(){
    var $classForm = $('#sub-reqd-doc-form');
    var validator = $classForm.validate();
    $('#saveSubReqdDocs').click(function(){
        if (!$classForm.valid()) {
            return;
        }
        var $btn = $(this).button('Saving');
        var data = {};
        $classForm.serializeArray().map(function(x){data[x.name] = x.value;});
        var url = "createSubReqdDocs";
        var request = $.post(url, data );
        request.success(function(){
            Swal.fire({
                title: 'Success',
                text: 'Record created/updated Successfully',
                icon: 'success'
            })
            $('#subrequireddoctbl').DataTable().ajax.reload();
            validator.resetForm();
            $('#sub-reqd-doc-form').find("input[type=text],input[type=number],input[type=mobileNumber],input[type=emailFull],input[type=password],input[type=hidden], textarea,select").val("");
            $("#sub-reqd-doc-form input:checkbox").prop("checked", false);
            $('#editsubReqdocsModal').modal('hide');
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

SETUPS.saveRequiredDocs= function(){
    var $classForm = $('#reqd-doc-form');
    var validator = $classForm.validate();
    $('#saveReqdDocs').click(function(){
        if (!$classForm.valid()) {
            return;
        }
        var $btn = $(this).button('Saving');
        var data = {};
        $classForm.serializeArray().map(function(x){data[x.name] = x.value;});
        var url = "createReqdDocs";
        var request = $.post(url, data );
        request.success(function(){
            Swal.fire({
                title: 'Success',
                text: 'Record created/updated Successfully',
                icon: 'success'
            })
            $('#requireddoctbl').DataTable().ajax.reload();
            validator.resetForm();
            $('#reqd-doc-form').find("input[type=text],input[type=number],input[type=mobileNumber],input[type=emailFull],input[type=password],input[type=hidden], textarea,select").val("");
            $("#reqd-doc-form input:checkbox").prop("checked", false);
            $('#reqdocsModal').modal('hide');
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

SETUPS.createRequiredDocs = function(){
    var url = "reqdocslist";
    var currTable = $('#requireddoctbl').DataTable( {
        "processing": true,
        "serverSide": true,
        autoWidth: true,
        "ajax": {
            'url': url,
        },
        lengthMenu: [ [5], [5] ],
        pageLength: 5,
        destroy: true,
        "columns": [
            { "data": "reqShtDesc" },
            { "data": "reqDesc" },
            { "data": "appliesNewBusiness" },
            { "data": "appliesEndorsement" },
            { "data": "appliesRenewal" },
            { "data": "appliesCertificate" },
            { "data": "appliesLossOpening" },
            { "data": "appliesAccount",
                "render": function ( data, type, full, meta ) {
                    if(full.appliesAccount){
                        return (full.appliesAccount==="Y")?"Yes":"No";
                    }
                    else
                        return "No";
                }
            },
            { "data": "appliesSubAgent",
                "render": function ( data, type, full, meta ) {
                    if(full.appliesSubAgent){
                        return (full.appliesSubAgent==="Y")?"Yes":"No";
                    }
                    else
                        return "No";
                }
            },
            { "data": "appliesClient",
                "render": function ( data, type, full, meta ) {
                    if(full.appliesClient){
                        return (full.appliesClient==="Y")?"Yes":"No";
                    }
                    else
                        return "No";
                }
            },
            { "data": "appliesCorpClient",
                "render": function ( data, type, full, meta ) {
                    if(full.appliesCorpClient){
                        return (full.appliesCorpClient==="Y")?"Yes":"No";
                    }
                    else
                        return "No";
                }
            },
            { "data": "accrualDoc",
                "render": function ( data, type, full, meta ) {
                    if(full.accrualDoc){
                        return (full.accrualDoc==="Y")?"Yes":"No";
                    }
                    else
                        return "No";
                }
            },
            { "data": "period" },
            { "data": "periodType" },
            {
                "data": "reqId",
                "render": function ( data, type, full, meta ) {
                    return '<button type="button" class="btn btn-success btn btn-info btn-sm" data-reqdocs='+encodeURI(JSON.stringify(full)) + ' value="Edit" onclick="SETUPS.editReqdDocs(this);"><i class="fa fa-pencil-square-o"></button>';
                }

            },
            {
                "data": "reqId",
                "render": function ( data, type, full, meta ) {
                    return '<button type="button" class="btn btn-danger btn btn-danger btn-sm" data-reqdocs='+encodeURI(JSON.stringify(full)) + ' value="Delete" onclick="SETUPS.deleteRqdDocs(this);"><i class="fa fa-trash-o"></button>';
                }

            },
        ]
    } );
    return currTable;
}


SETUPS.deleteRqdDocs = function(button){
    var docs = JSON.parse(decodeURI($(button).data("reqdocs")));
    bootbox.confirm("Are you sure want to delete "+docs["reqDesc"]+"?", function(result) {
        if(result){
            $.ajax({
                type: 'GET',
                url:  'deleteReqdDoc/' + docs["reqId"],
                dataType: 'json',
                async: true,
                success: function(result) {
                   Swal.fire({
                title: 'Success',
                text: 'Record created/updated Successfully',
                icon: 'success'
            })
                    $('#requireddoctbl').DataTable().ajax.reload();
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

SETUPS.editReqdDocs = function(button){
    var docs = JSON.parse(decodeURI($(button).data("reqdocs")));
    $("#reqd-code").val(docs["reqId"]);
    $("#reqd-doc-id").val(docs["reqShtDesc"]);
    $("#reqd-doc-desc").val(docs["reqDesc"]);
    $("#req-period").val(docs["period"]);
    $("#req-period-type").val(docs["periodType"]);
    $("#chk-nb").prop("checked", docs["appliesNewBusiness"]);
    $("#chk-en").prop("checked", docs["appliesEndorsement"]);
    $("#chk-rn").prop("checked", docs["appliesRenewal"]);
    $("#chk-cert").prop("checked", docs["appliesCertificate"]);
    $("#chk-lop").prop("checked", docs["appliesLossOpening"]);
    if(docs["appliesLossOpening"]) {
        $(".validity").show();
    }
    else{
        $(".validity").hide();
    }
    $("#chk-clm-pymt").prop("checked", docs["appliesClmPymt"]);
    if(docs["appliesAccount"]){
        if(docs["appliesAccount"]==="Y"){
            $("#chk-acct").prop("checked", true);
        }
        else $("#chk-acct").prop("checked", false);
    }else{
        $("#chk-acct").prop("checked", false);
    }
    if(docs["appliesClient"]){
        if(docs["appliesClient"]==="Y"){
            $("#chk-clnt").prop("checked", true);
        }
        else $("#chk-clnt").prop("checked", false);
    }else{
        $("#chk-clnt").prop("checked", false);
    }
    if(docs["appliesCorpClient"]){
        if(docs["appliesCorpClient"]==="Y"){
            $("#chk-cop-clnt").prop("checked", true);
        }
        else $("#chk-cop-clnt").prop("checked", false);
    }else{
        $("#chk-cop-clnt").prop("checked", false);
    }
    if(docs["appliesSubAgent"]){
        if(docs["appliesSubAgent"]==="Y"){
            $("#chk-sub-acct").prop("checked", true);
        }
        else $("#chk-sub-acct").prop("checked", false);
    }else{
        $("#chk-sub-acct").prop("checked", false);
    }
    if(docs["accrualDoc"]){
        if(docs["accrualDoc"]==="Y"){
            $("#chk-accrual-doc").prop("checked", true);
        }
        else $("#chk-accrual-doc").prop("checked", false);
    }else{
        $("#chk-accrual-doc").prop("checked", false);
    }
    $('#reqdocsModal').modal({
        backdrop: 'static',
        keyboard: true
    })

}

SETUPS.editSubReqdDocs = function(button){
    var docs = JSON.parse(decodeURI($(button).data("reqdocs")));
    $("#sub-reqd-code").val(docs["sclReqrdId"]);
    $("#sub-reqd-sub-code").val(docs["subclass"].subId);
    $("#sub-reqd-doc-code").val(docs["requiredDoc"].reqId);
    $("#sub-reqd-doc-id").val(docs["requiredDoc"].reqShtDesc);
    $("#sub-reqd-doc-desc").val(docs["requiredDoc"].reqDesc);
    $("#sub-req-mandatory").prop("checked", docs["mandatory"]);
    $('#editsubReqdocsModal').modal({
        backdrop: 'static',
        keyboard: true
    })
}

SETUPS.deleteSubReqDocs= function(button){
    var docs = JSON.parse(decodeURI($(button).data("reqdocs")));
    bootbox.confirm("Are you sure want to delete "+docs["requiredDoc"].reqDesc+"?", function(result) {
        if(result){
            $.ajax({
                type: 'GET',
                url:  'deleteSubReqdDoc/' + docs["sclReqrdId"],
                dataType: 'json',
                async: true,
                success: function(result) {
                   Swal.fire({
                title: 'Success',
                text: 'Record created/updated Successfully',
                icon: 'success'
            })
                    $('#subrequireddoctbl').DataTable().ajax.reload();
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


SETUPS.createSclReqDocs = function(subCode){
    var url = "subrequireddocs/"+subCode;
    var currTable = $('#subrequireddoctbl').DataTable( {
        "processing": true,
        "serverSide": true,
        autoWidth: true,
        "ajax": {
            'url': url,
        },
        lengthMenu: [ [5], [5] ],
        pageLength: 5,
        destroy: true,
        "columns": [
            { "data": "requiredDoc",
                "render": function ( data, type, full, meta ) {
                    return full.requiredDoc.reqShtDesc;
                }
            },
            { "data": "requiredDoc",
                "render": function ( data, type, full, meta ) {
                    return full.requiredDoc.reqDesc;
                }
            },
            { "data": "mandatory",
                "render": function ( data, type, full, meta ) {
                    return full.mandatory;
                }
            },
            {
                "data": "sclReqrdId",
                "render": function ( data, type, full, meta ) {
                    return '<button type="button" class="btn btn-success btn btn-info btn-sm" data-reqdocs='+encodeURI(JSON.stringify(full)) + ' value="Edit" onclick="SETUPS.editSubReqdDocs(this);"><i class="fa fa-pencil-square-o"></button>';
                }

            },
            {
                "data": "sclReqrdId",
                "render": function ( data, type, full, meta ) {
                    return '<button type="button" class="btn btn-danger btn btn-danger btn-sm" data-reqdocs='+encodeURI(JSON.stringify(full)) + ' onclick="SETUPS.deleteSubReqDocs(this);"><i class="fa fa-trash-o"></button>';
                }

            },
        ]
    } );
    return currTable;
}

SETUPS.getSubClassReqdDocsModal = function(){

    $("#btn-add-sub-req-docs").click(function(){
        SETUPS.searchReqdDocs("");
    });
}

SETUPS.searchReqdDocs = function(search){
    if($("#sub-code").val() != ''){
        $.ajax({
            type: 'GET',
            url:  'subclassreqdocs',
            dataType: 'json',
            data: {"subCode": $("#sub-code").val(),"searchName":search},
            async: true,
            success: function(result) {
                $("#subrequiredDocsTbl tbody").each(function(){
                    $(this).remove();
                });
                for(var res in result){
                    var markup = "<tr><td><input type='checkbox' name='record' id='"+result[res].reqId+"'></td><td>" + result[res].reqShtDesc + "</td><td>" + result[res].reqDesc + "</td></tr>";
                    $("#subrequiredDocsTbl").append(markup);
                }
                $("#req-doc-sub-code").val($("#sub-code").val());
                $('#subreqDocsModal').modal({
                    backdrop: 'static',
                    keyboard: true
                })
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
        bootbox.alert("Select Sub Class to attach Required Documents")
    }
}



SETUPS.saveSubclassReqDocs = function(){
    var arr = [];
    $("#saveSubclassReqdDoc").click(function(){
        $("#subrequiredDocsTbl tbody").find('input[name="record"]').each(function(){
            if($(this).is(":checked")){
                arr.push($(this).attr("id"));
            }
        });
        if(arr.length==0){
            bootbox.alert("No Required Docs Selected to attach..");
            return;
        }

        var $currForm = $('#sub-req-docs-form');
        var currValidator = $currForm.validate();
        if (!$currForm.valid()) {
            return;
        }

        var data = {};
        $currForm.serializeArray().map(function(x) {
            data[x.name] = x.value;
        });
        var url = "createSubclassReqDocs";
        data.requiredDocs = arr;


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
                $('#subrequireddoctbl').DataTable().ajax.reload();
                $('#subreqDocsModal').modal('hide');
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