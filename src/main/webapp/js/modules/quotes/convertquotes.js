/**
 * Created by peter on 3/19/2017.
 */


$(function() {

    $(document).ready(function () {
        populateClientLov();
        createAccountsForSel();
        createProductForSel( "G");
        addProspects();
        createSectorSelect();
        populateBranchLov1();
        populateCountryLov();
        populateTitlesLov();
        createClientTownLov();
        populateSmsPrefix(-2000);
        populatePostalCode(-2000);
        saveClientDocsList();
        uploadClientDocument();
        addSMSPrefix();
        convertQuoteProcess();

        $(document).ajaxStart(function () {
            $("#convertQuotBtn").attr("disabled", true);
        });
        $(document).ajaxComplete(function () {
            $("#convertQuotBtn").attr("disabled", false);
        });

        $("#btn-add-docs").click(function(){
            searchReqDocs("");
        });

        $("#searchDocuments").click(function(){
            searchReqDocs($("#doc-name-search").val());
        })
        //  tenantImage(-2000);


        $(".datepicker-input").each(function() {
            $(this).datetimepicker({
                format: 'DD/MM/YYYY'
            });

        });

        $("#closeUpload").click(function(){

            $('#clientdocModal').modal('hide');
            $('#createProspectModal').modal('show');
        })

        $("#closeclientReqDocsModal").click(function(){
            $('#clientReqDocsModal').modal('hide');
            $('#createProspectModal').modal('show');

        })




        $("#btn-search-quotes").on('click', function(){
            var quote = $("#quote-search-number").val();
            var clientCode = $("#rev-search-name").val();
            var agentCode = $("#agent-search-number").val();
            var prodCode = $("#product-search-number").val();
            var prscode = $("#prs-search-name").val();
            if(quote===''  && clientCode==='' && agentCode==='' && prodCode==='' &&prscode==='' ){
                bootbox.alert("Provide At least one Search Parameter");
                return;
            }

            quoteEnquiryConvert();

        });
    });
});


function populatePostalCode(townCode){
    if($("#postal-code").filter("div").html() != undefined)
    {
        Select2Builder.initAjaxSelect2({
            containerId : "postal-code",
            sort : 'zipCode',
            change: function(e, a, v){
                $("#postal-code-id").val(e.added.pcode);
            },
            formatResult : function(a)
            {
                return a.zipCode
            },
            formatSelection : function(a)
            {
                return a.zipCode
            },
            initSelection: function (element, callback) {
                var code = $("#postal-code-id").val();
                var name = $("#postal-name").val();
                var data = {zipCode:name,pcode:code};
                callback(data);
            },
            id: "pcode",
            placeholder:"Select Postal Code",
            params: {townCode: townCode}

        });
    }
}
function createClientTownLov(){
    if($("#town-code-lov").filter("div").html() != undefined)
    {
        Select2Builder.initAjaxSelect2({
            containerId : "town-code-lov",
            sort : 'ctName',
            change: function(e, a, v){
                $("#clnt-town-id").val(e.added.ctCode);
                populatePostalCode(e.added.ctCode);
            },
            formatResult : function(a)
            {
                return a.ctName
            },
            formatSelection : function(a)
            {
                return a.ctName
            },
            initSelection: function (element, callback) {
                var code = $("#clnt-town-id").val();
                var name = $("#clnt-town-name").val();
                var data = {ctName:name,ctCode:code};
                callback(data);
            },
            id: "ctCode",
            placeholder:"Select Town",
        });
    }
}
function populateTitlesLov(){
    if($("#clnt-title").filter("div").html() != undefined)
    {
        Select2Builder.initAjaxSelect2({
            containerId : "clnt-title",
            sort : 'titleName',
            change: function(e, a, v){
                $("#clnt-title-id").val(e.added.titleId);
                $("#clnt-title-name").val(e.added.titleName);
            },
            formatResult : function(a)
            {
                return a.titleName
            },
            formatSelection : function(a)
            {
                return a.titleName
            },
            initSelection: function (element, callback) {
            },
            id: "titleId",
            placeholder:"Select Title",

        });
    }
}

function defaultCountry(){
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
                populateCountryLov();
                populateSmsPrefix(s.couCode);
            }
        },
        error: function(xhr, error){

        }
    });

}

function populateSmsPrefix(couCode){
    if($("#sms-pref").filter("div").html() != undefined)
    {
        Select2Builder.initAjaxSelect2({
            containerId : "sms-pref",
            sort : 'prefixName',
            change: function(e, a, v){
                $("#pref-sms-id").val(e.added.prefixId);
            },
            formatResult : function(a)
            {
                return a.prefixName
            },
            formatSelection : function(a)
            {
                return a.prefixName
            },
            initSelection: function (element, callback) {
                var code = $("#pref-sms-id").val();
                var name = $("#pref-sms-name").val();
                var data = {prefixName:name,prefixId:code};
                callback(data);
            },
            id: "prefixId",
            placeholder:"Prefix",
            params: {couCode: couCode}

        });
    }

    if($("#phone-pref").filter("div").html() != undefined)
    {
        Select2Builder.initAjaxSelect2({
            containerId : "phone-pref",
            sort : 'prefixName',
            change: function(e, a, v){
                $("#pref-phone-id").val(e.added.prefixId);
            },
            formatResult : function(a)
            {
                return a.prefixName
            },
            formatSelection : function(a)
            {
                return a.prefixName
            },
            initSelection: function (element, callback) {
                var code = $("#pref-phone-id").val();
                var name = $("#pref-phone-name").val();
                var data = {prefixName:name,prefixId:code};
                callback(data);
            },
            id: "prefixId",
            placeholder:"Prefix",
            params: {couCode: couCode}

        });
    }
}


function populateCountryLov(){
    if($("#clnt-country").filter("div").html() != undefined)
    {
        Select2Builder.initAjaxSelect2({
            containerId : "clnt-country",
            sort : 'couName',
            change: function(e, a, v){
                $("#cou-id").val(e.added.couCode);
                $("#cou-prefix").val(e.added.prefix);
                populateSmsPrefix(e.added.couCode);
            },
            formatResult : function(a)
            {
                return a.couName
            },
            formatSelection : function(a)
            {
                return a.couName
            },
            initSelection: function (element, callback) {
                var code = $("#cou-id").val();
                var name = $("#cou-name").val();
                var data = {couName:name,couCode:code};
                callback(data);
            },
            id: "couCode",
            placeholder:"Select Country",

        });
    }
}
function populateBranchLov1(){
    if($("#ten-branch").filter("div").html() != undefined)
    {
        Select2Builder.initAjaxSelect2({
            containerId : "ten-branch",
            sort : 'obName',
            change: function(e, a, v){
                $("#obId").val(e.added.obId);
            },
            formatResult : function(a)
            {
                return a.obName
            },
            formatSelection : function(a)
            {
                return a.obName
            },
            initSelection: function (element, callback) {
            },
            id: "obId",
            placeholder:"Select Branch",

        });
    }
}

function populateClientLov(){
    if($("#prospects-frm").filter("div").html() != undefined)
    {
        Select2Builder.initAjaxSelect2({
            containerId : "prospects-frm",
            sort : 'fname',
            change: function(e, a, v){
                $("#prs-search-name").val(e.added.tenId);

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

            },
            id: "tenId",
            placeholder:"Select Prospect"

        });
    }

    $("#prospects-frm").on("select2-removed", function(e) {
        $("#prs-search-name").val('');
    })
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
            placeholder:"Select Account",
        });
    }

    $("#acc-frm").on("select2-removed", function(e) {
        $("#agent-search-number").val('');
    })
}


function createProductForSel(grpType){

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
            params: {grpType: grpType},
            placeholder:"Select Product",
        });
    }

    $("#prd-code").on("select2-removed", function(e) {
        $("#product-search-number").val('');
    })
}


function quoteEnquiryConvert(){


    var url = "convertedquotes";
    return $('#convert_quot_tbl').DataTable({
        "processing": true,
        "serverSide": true,
        autoWidth: true,
        "deferRender": true,
        "ajax": {
            'url': url,
            'data': {
                'clientCode': $("#rev-search-name").val(),
                'quoteNo': $("#quote-search-number").val(),
                'proCode': $("#product-search-number").val(),
                'agentCode': $("#agent-search-number").val(),
                'prsCode': $("#prs-search-name").val(),
            },
        },
        lengthMenu: [[10, 15, 20], [10, 15, 20]],
        pageLength: 10,
        destroy: true,
        "columns": [
            {
                "data": "quoteNo",
                "render": function (data, type, full, meta) {
                    return full.quoteNo;
                }
            },
            {
                "data": "product",
                "render": function (data, type, full, meta) {
                    return full.product;
                }
            },
            {
                "data": "insuranceCompany",
                "render": function (data, type, full, meta) {
                    return full.insuranceCompany;
                }
            },
            {
                "data": "wef",
                "render": function (data, type, full, meta) {
                    return moment(full.wef).format('DD/MM/YYYY');
                }
            },
            {
                "data": "wet",
                "render": function (data, type, full, meta) {
                    return moment(full.wet).format('DD/MM/YYYY');
                }
            },
            {
                "data": "quoteNo",
                "render": function (data, type, full, meta) {
                    return full.fname+" "+full.otherNames;
                }
            },
            {
                "data": "currency",
                "render": function (data, type, full, meta) {
                    return full.currency;
                }
            },
            {
                "data": "converted",
                "render": function (data, type, full, meta) {
                    if (full.converted) {
                        if (full.converted === 'Y') {
                            return "Yes";
                        } else if (full.converted === 'R') {
                            return "No";
                        }

                    } else {
                        return "No";
                    }
                }
            },
            {
                "data": "polId",
                "render": function (data, type, full, meta) {

                    if (full.polId) {
                        return '<form action="edituwtrans" method="post"><input type="hidden" name="id" value=' + full.polId + '><input type="submit"  class="btn btn-info btn-sm" value="' + full.policyNo + '" ></form>';


                    } else
                        return "";
                }
            },
            // {
            //     "data": "quoteProductId",
            //     "render": function (data, type, full, meta) {
            //         console.log(full)
            //         // if (canAccess) {
            //         return '<button type="button" class="btn btn-success btn btn-info btn-sm" data-quotprod=' + encodeURI(JSON.stringify(full)) + ' onclick="convertQuote(this);">Convert</button>';
            //         // }
            //         // else return "";
            //     }
            //
            // },
            {
                "data": "quoteId",
                "render": function (data, type, full, meta) {
                    return '<form action="editquottrans" method="post"><input type="hidden" name="id" value=' + full.quoteId + '><input type="submit"  class="btn btn-success btn btn-info btn-sm" value="View" ></form>';
                }

            },
        ]
    });

}



function populateClientTypeLov(){
    if($("#clnt-client-type").filter("div").html() != undefined)
    {
        Select2Builder.initAjaxSelect2({
            containerId : "clnt-client-type",
            sort : 'clientType',
            change: function(e, a, v){
                $("#clnt-type-id").val(e.added.typeId);
                $("#clnt-type-name").val(e.added.typeDesc);
                if(e.added.clientType==='C' || e.added.clientType===''){
                    $("#gender,.gender,#lblGender").hide();
                    $("#lbl-dob").html("Date of Incorporation");
                    $("#lbl-id-no").html("Registration No.");
                    $("#clnt-title").select2("enable", false);
                    $('#clnt-title').select2('val', null);
                    $("#clnt-title-id").val("");
                    $("span.othernames").hide();
                    $("#client-photo").html("Logo");
                    $(".employee-info").hide();
                }
                else if(e.added.clientType==='I'){
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


function tenantImage(id){
    $("#avatar").fileinput({
        overwriteInitial: true,
        maxFileSize: 1500,
        showClose: false,
        showCaption: false,
        browseLabel: '',
        removeLabel: '',
        browseIcon: '<i class="fa fa-folder-open"></i>',
        removeIcon: '<i class="fa fa-times"></i>',
        removeTitle: 'Cancel or reset changes',
        elErrorContainer: '#kv-avatar-errors',
        msgErrorClass: 'alert alert-block alert-danger',
        defaultPreviewContent: '<img src="'+getContextPath()+'/protected/clients/setups/tenantImage/'+id+'"  style="width:180px">',
        layoutTemplates: {main2: '{preview} ' + ' {remove} {browse}'},
        allowedFileExtensions: ["jpg", "png", "gif"]
    });
}
function addProspects() {
    $("#btn-add-prs").click(function () {
        $('#prospect-form').find("input[type=text],input[type=number],input[type=mobileNumber],input[type=emailFull],input[type=password],input[type=hidden], textarea,select").val("");
        $("#sel3").val("A");
        $("#sel3").attr("readonly",true);
        $('#clnt-client-type').select2('val', null);
        $('#clnt-client-type').attr('value', '');
        $('#createProspectModal').modal({
            backdrop: 'static',
            keyboard: true
        })
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
        var url = "createProspectConvert";
        var request = $.post(url, data );
        request.success(function(result){
            Swal.fire({
                title: 'Success',
                text: 'Quote Product converted Successfully',
                icon: 'success'
            });
            // $('#myPleaseWait').modal('hide');
            $('#convert_quot_tbl').DataTable().ajax.reload();
            validator.resetForm();
            $('#prospect-form').find("input[type=text],input[type=number],input[type=mobileNumber],input[type=emailFull],input[type=password],input[type=hidden], textarea,select").val("");
            $('#createProspectModal').modal('hide');
            $('#converQuotModal').modal('hide');
        });

        request.error(function(jqXHR, textStatus, errorThrown){
            // $('#myPleaseWait').modal('hide');
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



function createSectorSelect(){
    if($("#sect-def").filter("div").html() != undefined)
    {
        Select2Builder.initAjaxSelect2({
            containerId : "sect-def",
            sort : 'name',
            change: function(e, a, v){
                $("#sect-id").val(e.added.code);
                populateOccupations(e.added.code);
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
            id: "code",
            placeholder:"Select Sector",
        });
    }
}

function populateOccupations(sectCode){
    if($("#occ-def").filter("div").html() != undefined)
    {
        Select2Builder.initAjaxSelect2({
            containerId : "occ-def",
            sort : 'name',
            change: function(e, a, v){
                $("#occ-id").val(e.added.code);
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
            id: "code",
            placeholder:"Select Occupation",
            params: {sectCode: sectCode}

        });
    }
}

function populateQuotProspects(tenId,quoteProductId){
    $(".prop-status").hide();
    $.ajax({
        type: 'GET',
        url: 'getProspectDetails/'+tenId,
        dataType: 'json',
        async: true,
        success: function (result) {
            $("#prospect-id-pk").val(tenId);
            $("#prs-sht-desc").val(result.prospShtDesc);
            $("#fname").val(result.fname);
            $("#dob").val(result.dob);
            $("#gender").val(result.gender);
            $("#prosp-email").val(result.emailAddress);
            $("#other-names").val(result.otherNames);
            $("#phone-no").val(result.phoneNo);
            var phoneNo = result.phoneNo;
            $("#pref-sms-id").val(result.prefixId);
            $("#pref-sms-name").val(result.prefix);
            $("#pref-phone-id").val(result.prefixId);
            $("#pref-phone-name").val(result.prefix);
            $("#pol_start-date").val($("#from-date").val());
            defaultCountry();
            if(phoneNo){
                $("#sms-no").val(phoneNo.substring(phoneNo.length - 9, phoneNo.length));
                $("#clnt-phone-no").val(phoneNo.substring(phoneNo.length - 9, phoneNo.length));
            }

            $("#clnt-type-id").val(result.clientTypeId);
            $("#clnt-type-name").val(result.clientType);
            populateClientTypeLov();
            // $("#pin-no").val(data["quoteTrans"].prospect.pinNo);
            // $("#id-no").val(data["quoteTrans"].prospect.idNo);
            $("#sel3").val(result.status);
            $("#prosp-quot-prd-pk").val(quoteProductId);
            $("#prosp-quot-pk").val("");
            getClientDocs(tenId);

            $("#address").val("P.O. Box");
            $("#sel3").attr("readonly",false);
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


function populateMedQuotProspects(data){
    $(".prop-status").hide();
    $("#prospect-id-pk").val(data["prospect"].tenId);
    $("#prs-sht-desc").val(data["prospect"].prospShtDesc);
    $("#fname").val(data["prospect"].fname);
    $("#gender").val(data["prospect"].gender);
    $("#prosp-email").val(data["prospect"].emailAddress);
    $("#other-names").val(data["prospect"].otherNames);
    $("#phone-no").val(data["prospect"].phoneNo);
    var phoneNo = data["prospect"].phoneNo;
    if(phoneNo){
        $("#sms-no").val(phoneNo.substring(phoneNo.length - 9, phoneNo.length));
        $("#clnt-phone-no").val(phoneNo.substring(phoneNo.length - 9, phoneNo.length));
    }

    $("#clnt-type-id").val(data["prospect"].tenantType.typeId);
    $("#clnt-type-name").val(data["prospect"].tenantType.typeDesc);
    $("#other-names").val(data["prospect"].otherNames);
    populateClientTypeLov();
    $("#pin-no").val(data["prospect"].pinNo);
    $("#id-no").val(data["prospect"].idNo);
    $("#gender").val(data["prospect"].gender);
    $("#sel3").val(data["prospect"].status);
    $("#prosp-quot-prd-pk").val("");
    $("#prosp-quot-pk").val(data['quoteId']);
    $("#date-reg").val(moment(new Date()).format('DD/MM/YYYY'));
    $("#sel3").attr("readonly",false);
    $('#createProspectModal').modal({
        backdrop: 'static',
        keyboard: true
    })
}

function convertMedQuote(button){
    var quote = JSON.parse(decodeURI($(button).data("medquot")));
    $.ajax({
        type: 'GET',
        url:  'convertMedQuote/'+quote['quoteId'],
        dataType: 'json',
        async: true,
        success: function(result) {
            if(result!=="Y"){
                // $('#myPleaseWait').modal('hide');
                populateMedQuotProspects(quote);
                return;
            }
            // $('#myPleaseWait').modal('hide');
            Swal.fire({
                title: 'Success',
                text: 'Quote converted Successfully',
                icon: 'success'
            });
            $('#convert_quot_tbl').DataTable().ajax.reload();
        },
        error: function(jqXHR, textStatus, errorThrown) {
            // $('#myPleaseWait').modal('hide');
             Swal.fire({
                title: 'Error',
                text: jqXHR.responseText,
                icon: 'error'
            })
        }
    });
}



function addSMSPrefix(){
    $("#btn-add-sms-prefix,#btn-add-phone-prefix").click(function(){
        if($("#cou-id").val() != ''){
            $('#prefix-form').find("input[type=text],input[type=number],input[type=mobileNumber],input[type=emailFull],input[type=password],input[type=hidden], textarea,select").val("");
            $("#prefix-country").val($("#cou-id").val());
            createMobProviderSel();
            $('#prefixModal').modal({
                backdrop: 'static',
                keyboard: true
            })
        }
        else{
            bootbox.alert("Select Country to continue");
        }
    });

    var $classForm = $('#prefix-form');
    var validator = $classForm.validate();
    $('#savePrefixBtn').click(function(){
        if (!$classForm.valid()) {
            return;
        }

        var $btn = $(this).button('Saving');
        var data = {};
        $classForm.serializeArray().map(function(x){data[x.name] = x.value;});
        var url = "createPrefix";
        var request = $.post(url, data );
        request.success(function(){
            Swal.fire({
                title: 'Success',
                text: 'Record created/updated Successfully',
                icon: 'success'
            })
            populateSmsPrefix($("#cou-id").val());
            $('#prefix-form').find("input[type=text],input[type=number],input[type=mobileNumber],input[type=emailFull],input[type=password],input[type=hidden], textarea,select").val("");
            $('#prefixModal').modal('hide');
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

function createMobProviderSel(){
    if($("#mob-provider").filter("div").html() != undefined)
    {
        Select2Builder.initAjaxSelect2({
            containerId : "mob-provider",
            sort : 'providerName',
            change: function(e, a, v){
                $("#mob-prefix-provd-id").val(e.added.providerId);
            },
            formatResult : function(a)
            {
                return a.providerName
            },
            formatSelection : function(a)
            {
                return a.providerName
            },
            initSelection: function (element, callback) {

            },
            id: "providerId",
            placeholder:"Select Provider",
        });
    }
}
function convertQuoteProcess(){
    $("#convertQuotBtn").click(function(){
        var $currForm = $('#convert-quot-form');
        var currValidator = $currForm.validate();
        if (!$currForm.valid()) {
            return;
        }
        var data = {};
        $currForm.serializeArray().map(function(x) {
            data[x.name] = x.value;
        });
        var url = "convertQuote";
        $.ajax({
            url : url,
            type : "POST",
            data : JSON.stringify(data),
            success : function(result) {
                console.log(result);
                if(result!=="Y" && result.prospectId){
                    // $('#myPleaseWait').modal('hide');
                    populateQuotProspects(result.prospectId,$("#conver-quot-pro-id").val());
                    return;
                }
                // $('#myPleaseWait').modal('hide');
                $('#converQuotModal').modal('hide');
                Swal.fire({
                    title: 'Success',
                    text: 'Quote Product converted Successfully',
                    icon: 'success'
                });
                $('#convert_quot_tbl').DataTable().ajax.reload();
            },
            error : function(jqXHR, textStatus, errorThrown) {
                console.log(jqXHR);
                Swal.fire({
                title: 'Error',
                text: jqXHR.responseText,
                icon: 'error'
            });
                // $('#myPleaseWait').modal('hide');
            },
            dataType : "json",
            contentType : "application/json"
        });
    });
}

function convertQuote(button){
    var product = JSON.parse(decodeURI($(button).data("quotprod")));
    $("#conver-quot-pro-id").val(product['quoteProductId']);
    $('#converQuotModal').modal({
        backdrop: 'static',
        keyboard: true
    });
}
//
// function convertQuote(button){
//     var product = JSON.parse(decodeURI($(button).data("quotprod")));
//     $("#conver-quot-pro-id").val(product['quoteProductId']);
//     $('#converQuotModal').modal({
//         backdrop: 'static',
//         keyboard: true
//     });
//
//     $.ajax({
//         type: 'GET',
//         url:  'convertQuote/'+product['quoteProductId'],
//         dataType: 'json',
//         async: true,
//         success: function(result) {
//             console.log(result);
//             if(result!=="Y"){
//                 // $('#myPleaseWait').modal('hide');
//
//                 populateQuotProspects(product);
//                 return;
//             }
//             // $('#myPleaseWait').modal('hide');
//             new PNotify({
//                 title: 'Success',
//                 text: 'Quote Product Converted Successfully',
//                 type: 'success',
//                 styling: 'bootstrap3'
//             });
//             $('#convert_quot_tbl').DataTable().ajax.reload();
//         },
//         error: function(jqXHR, textStatus, errorThrown) {
//             // $('#myPleaseWait').modal('hide');
//             new PNotify({
//                 title: 'Error',
//                 text: jqXHR.responseText,
//                 type: 'error',
//                 styling: 'bootstrap3'
//             });
//         }
//     });
// }




function getClientDocs(clientId){
    var url = "prospectDocs/"+clientId;
    var currTable = $('#clientDocsList').DataTable( {
        "processing": true,
        "serverSide": true,
        autoWidth: true,
        "ajax": {
            'url': url,
        },
        lengthMenu: [ [10,15,20], [10,15,20] ],
        pageLength: 10,
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
            { "data": "uploadedFileName" },
            { "data": "checkSum" },
            {
                "data": "cdId",
                "render": function ( data, type, full, meta ) {
                    return '<button type="button" class="btn btn-success btn btn-info btn-sm" data-docs='+encodeURI(JSON.stringify(full)) + ' onclick="editClientDocs(this);"><i class="fa fa-pencil-square-o"></button>';
                }

            },
            {
                "data": "cdId",
                "render": function ( data, type, full, meta ) {
                    return '<button type="button" class="btn btn-success btn btn-info btn-sm" type="button" data-docs=' + encodeURI(JSON.stringify(full)) + ' onclick="downloadClientDoc(this);"><i class="fa fa-file-archive-o"></button>';

                }

            },
            {
                "data": "cdId",
                "render": function ( data, type, full, meta ) {
                    return '<button type="button" class="btn btn-danger btn btn-info btn-sm" data-docs='+encodeURI(JSON.stringify(full)) + ' onclick="deleteClientDoc(this);"><i class="fa fa-trash-o"></button>';
                }

            },
        ]
    } );
    return currTable;
}

function downloadClientDoc(button){
    var docs = JSON.parse(decodeURI($(button).data("docs")));
    window.open(SERVLET_CONTEXT+"/protected/clients/setups/prospectDocument/"+docs['cdId'],
        '_blank' // <- This is what makes it open in a new window.
    );
}

function editClientDocs(button){
    var docs = JSON.parse(decodeURI($(button).data("docs")));

    $("#clnt-doc-name").text(docs["requiredDoc"].reqDesc);
    $("#clnt-upload-name").text(docs['uploadedFileName']);

    $("#clnt-doc-id").val(docs['cdId']);

    console.log("testing again");
    $('#createProspectModal').modal('hide');
    $('#clientdocModal').modal({
        backdrop: 'static',
        keyboard: true
    });
}

function deleteClientDoc(button){
    var docs = JSON.parse(decodeURI($(button).data("docs")));
    bootbox.confirm("Are you sure want to delete "+docs['requiredDoc'].reqDesc+"?", function(result) {
        if(result){
            // // $('#myPleaseWait').modal({
//			backdrop: 'static',
//			keyboard: true
//		})
            $.ajax({
                type: 'GET',
                url:  'deleteProspectDoc/' + docs['cdId'],
                dataType: 'json',
                async: true,
                success: function(result) {
                    // $('#myPleaseWait').modal('hide');
                   Swal.fire({
                title: 'Success',
                text: 'Record created/updated Successfully',
                icon: 'success'
            })
                    $('#clientDocsList').DataTable().ajax.reload();
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



function uploadClientDocument(){
    var $form = $("#clnt-doc-form");
    var validator = $form.validate();
    $('form#clnt-doc-form')
        .submit( function( e ) {
            e.preventDefault();

            if (!$form.valid()) {
                return;
            }
            // // $('#myPleaseWait').modal({
//			backdrop: 'static',
//			keyboard: true
//		})
            var data = new FormData( this );
            data.append( 'file', $( '#clnt-avatar' )[0].files[0] );
            $.ajax( {
                url: 'uploadProspectDocs',
                type: 'POST',
                data: data,
                processData: false,
                contentType: false,
                success: function (s ) {
                    // $('#myPleaseWait').modal('hide');
                    Swal.fire({
                        title: 'Success',
                        text: 'File Uploaded Successfully',
                        icon: 'success'
                    });
                    $('#clientdocModal').modal('hide');
                    $('#createProspectModal').modal('show');
                    var $el = $('#clnt-avatar');
                    $el.wrap('<form>').closest('form').get(0).reset();
                    $el.unwrap();
                    $('#clientDocsList').DataTable().ajax.reload();

                },
                error: function(xhr, error){
                    // $('#myPleaseWait').modal('hide');
                   Swal.fire({
                title: 'Error',
                text: xhr.responseText,
                icon: 'error'
            });
                }
            });
        });
}


function searchReqDocs(search){
    if($("#acctId-pk").val() != ''){
        $.ajax({
            type: 'GET',
            url:  'prospectreqdocs',
            dataType: 'json',
            data: {"clientCode": $("#prospect-id-pk").val(),"docName":search},
            async: true,
            success: function(result) {
                $("#clientDocsTbl tbody").each(function(){
                    $(this).remove();
                });
                for(var res in result){
                    var markup = "<tr><td><input type='checkbox' name='record' id='"+result[res].reqId+"'></td><td>" + result[res].reqShtDesc + "</td><td>" + result[res].reqDesc + "</td></tr>";
                    $("#clientDocsTbl").append(markup);
                }
                $("#req-client-code").val($("#prospect-id-pk").val());
                $('#createProspectModal').modal('hide');
                $('#clientReqDocsModal').modal({
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
        bootbox.alert("No Account to attach Documents")
    }
}


function saveClientDocsList(){
    var arr = [];
    $("#saveClientDocsBtn").click(function(){
        $("#clientDocsTbl tbody").find('input[name="record"]').each(function(){
            if($(this).is(":checked")){
                arr.push($(this).attr("id"));
            }
        });
        if(arr.length==0){
            bootbox.alert("No Documents Selected to attach..");
            return;
        }

        var $currForm = $('#client-doc-form');
        var currValidator = $currForm.validate();
        if (!$currForm.valid()) {
            return;
        }

        var data = {};
        $currForm.serializeArray().map(function(x) {
            data[x.name] = x.value;
        });
        var url = "createProspectDocs";
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
                $('#clientDocsList').DataTable().ajax.reload();
                $('#clientReqDocsModal').modal('hide');
                $('#createProspectModal').modal('show');
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


