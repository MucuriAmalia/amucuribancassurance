
var UTILITIES = UTILITIES || {};
$(function() {

    $(document).ready(function () {
        populateClientLov();
        getPolicyWet();
        populateUserBranches();
        populatePaymentModes();
        populateCurrencyLov();
        populateMultiProduct();
        populateBinderLov();
        populateTitlesLov();
        populateClientTypeLov2();
        populateCountryLov();
        populateBranchLov1();
        createSectorSelect();
        addNewClient();
        saveClientDetails();

        $("#pol-buss-type").val("N");
        $("#pol-ins-comp").text($("#bind-ins-name").val());
        $("#pol-prod-name").text($("#bind-pro-name").val());
        if($("#pol-bin-type").val()==="S") {
            $("#binder-frm").select2("enable", true);
            $("#pol-ins-comp").text("Fund Transaction");
            $("#pol-prod-name").text("Fund Product");
        }
        else{
            $("#binder-frm").select2("enable", true);
            $("#pol-ins-comp").text("");
            $("#pol-prod-name").text("");
            $("#medicalCoverTyp-info").text("");

        }
        $(".datepicker-input").each(function() {
            $(this).datetimepicker({
                format: 'DD/MM/YYYY'
            });

        });

        $(".policy-beneficiary").hide();
        $("#pol-bin-type").on('change', function(){
            if($(this).val()==="S") {
                $("#binder-frm").select2("enable", true);
                $("#pol-ins-comp").text("Fund Transaction");
                $("#pol-prod-name").text("Fund Product");
                $("#pol-medicalCoverType").val("G");
                populateBinderLov();
            }
            else{
                $("#binder-frm").select2("enable", true);
                $("#pol-ins-comp").text("");
                $("#pol-prod-name").text("");
                $("#medicalCoverTyp-info").text("");
                populateBinderLov();
            }

        });
        //$(".policy-card-type").hide();
        //$("#pol-card-issue").on('change',function(){
        //    if($(this).val()==="Y"){
        //        $(".policy-card-type").show();
        //    } else{
        //        $("pol-card-type").text("");
        //        $(".policy-card-type").hide();
        //
        //    }
        //});
    });
});




function saveClientDetails(){
    $('#saveClientBtn').click(function(){
        var $classForm = $('#tenant-form');
        var validator = $classForm.validate();
        if (!$classForm.valid()) {
            return;
        }
        var $btn = $(this).button('Saving');
        var data = {};
        $classForm.serializeArray().map(function(x){data[x.name] = x.value;});
        var url = "createClient";
        var request = $.post(url, data );
        request.success(function(result){
            $("#client-id").val(result.tenId);
            $("#client-f-name").val(result.fname);
            $("#client-other-name").val(result.otherNames);
            populateClientLov();
            Swal.fire({
                title: 'Success',
                text: 'Record created/updated Successfully',
                icon: 'success'
            })
            validator.resetForm();
            $('#tenant-form').find("input[type=text],input[type=number],input[type=mobileNumber],input[type=emailFull],input[type=password],input[type=hidden], textarea,select").val("");
            $('#createClientModal').modal('hide');
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


function populateBinderCardTypes(binCode,wetdate){
    if($("#card-frm").filter("div").html() != undefined)
    {
        console.log("testing refresh "+wetdate)
        Select2Builder.initAjaxSelect2({
            containerId : "card-frm",
            sort : 'binCardDesc',
            change: function(e, a, v){
                $("#card-id").val(e.added.binCardId);
            },
            formatResult : function(a)
            {
                return a.binCardDesc;
            },
            formatSelection : function(a)
            {
                return a.binCardDesc;
            },
            initSelection: function (element, callback) {

                var code  = $("#card-id").val();
                var name = $("#card-name").val();
                var data = {binCardDesc:name,binCardId:code};
                callback(data);
            },
            id: "binCardId",
            width:"250px",
            params: {bindCode: binCode,wefDate:wetdate},
            placeholder:"Select Card Type"

        });
    }
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
            },
            id: "couCode",
            placeholder:"Select Country",

        });
    }
}


function populateSmsPrefix(couCode){
    if($("#sms-pref").filter("div").html() != undefined)
    {
        Select2Builder.initAjaxSelect2({
            containerId : "sms-pref",
            sort : 'prefixName',
            change: function(e, a, v){
                console.log(e.added.prefixId);
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
                console.log(e.added.prefixId);
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
            },
            id: "prefixId",
            placeholder:"Prefix",
            params: {couCode: couCode}

        });
    }
}


function populateClientTypeLov2(){
    if($("#clnt-client-type-1").filter("div").html() != undefined)
    {
        Select2Builder.initAjaxSelect2({
            containerId : "clnt-client-type-1",
            sort : 'clientType',
            change: function(e, a, v){
                $("#clnt-type-id-1").val(e.added.typeId);
                $("#clnt-type-name-1").val(e.added.typeDesc);
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
            },
            id: "typeId",
            placeholder:"Select Client Type",

        });
    }
    if($("#clnt-client-type").filter("div").html() != undefined)
    {
        Select2Builder.initAjaxSelect2({
            containerId : "clnt-client-type",
            sort : 'clientType',
            change: function(e, a, v){
                $("#clnt-type-id").val(e.added.typeId);
                $("#clnt-type-name").val(e.added.typeDesc);
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
            },
            id: "typeId",
            placeholder:"Select Client Type",

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
                $("#pm-name").val(e.added.pmDesc);
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

function populateUserBranches(){
    if($("#brn-frm").filter("div").html() != undefined)
    {
        Select2Builder.initAjaxSelect2({
            containerId : "brn-frm",
            sort : 'obName',
            change: function(e, a, v){
                $("#brn-id").val(e.added.obId);
                $("#brn-name").val(e.added.obName);
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
                var code  = $('#brn-id').val();
                var name = $("#brn-name").val();
                var data = {obName:name,obId:code};
                callback(data);
            },
            id: "obId",
            width:"250px",
            placeholder:"Select Branch"

        });
    }
}
function getPolicyWet(){
    $('#wef-date').on('dp.change', function (ev) {
        var curDate = ev.date;
        var dt = moment(curDate).format('DD/MM/YYYY');
        $("#risk-wef-date").val(dt);
        $.ajax({
            type: 'GET',
            url:  'getWetDate',
            dataType: 'json',
            data: {"wefDate":dt},
            async: true,
            success: function(result) {
                $("#wet-date").val(moment(result).format('DD/MM/YYYY'));
                $("#risk-wet-date").val(moment(result).format('DD/MM/YYYY'));


            },
            error: function(jqXHR, textStatus, errorThrown) {

            }
        });
        populateBinderCardTypes($("#binder-id").val(),dt);
    });
}



function populateCurrencyLov(){
    if($("#curr-frm").filter("div").html() != undefined)
    {
        Select2Builder.initAjaxSelect2({
            containerId : "curr-frm",
            sort : 'curName',
            change: function(e, a, v){
                $("#cur-id").val(e.added.curCode);
                $("#cur-name").val(e.added.curName);
            },
            formatResult : function(a)
            {
                return a.curName;
            },
            formatSelection : function(a)
            {
                return a.curName;
            },
            initSelection: function (element, callback) {
                var code  = $('#cur-id').val();
                var name = $("#cur-name").val();
                var data = {curName:name,curCode:code};
                callback(data);
            },
            id: "curCode",
            width:"250px",
            placeholder:"Select Currency"

        });
    }
}


function populateMultiProduct() {
    if ($("#product-frm").filter("div").html() != undefined) {
        Select2Builder.initAjaxSelect2({
            containerId: "product-frm",
            sort: 'proDesc',
            change: function (e, a, v) {
                // $("#sect-id").val(e.added.code);
                // populateOccupations(e.added.code);
                $("#product-id").val(e.added.proCode);
                populateBinderLov();
            },
            formatResult: function (a) {
                return a.proDesc
            },
            formatSelection: function (a) {
                return a.proDesc
            },
            initSelection: function (element, callback) {
                var code = $("#product-id").val();
                var name = $("#bind-product-desc").val();
                var data = {proDesc: name, proCode: code};
                callback(data);
            },
            id: "proCode",
            placeholder: "Select Product",
        });
    }
}

function populateBinderLov(data) {
    var selectedProductId = $("#prd-id").val();
    if ($("#binder-frm").filter("div").html() != undefined) {
        Select2Builder.initAjaxSelect2({
            containerId: "binder-frm",
            sort: 'binName',
            change: function (e, a, v) {
                populateImportSubCovers(e.added.binId);
                $("#binder-id").val(e.added.binId);
                $("#risk-binder-code").val(e.added.binId);
                $("#risk-bind-code").val(e.added.binId);
                $("#pol-ins-comp").text(e.added.name);
                $("#pol-prod-name").text(e.added.proDesc);
                // $("#product-id").val(e.added.proCode);
                $("#pol-agent-id").val(e.added.acctId);
                $("#client-pol-no").val(e.added.binPolNo);
                $("#risk-binder").text(e.added.binName);
                $("#pol-bind-age-appli").val(e.added.ageApplicable);
              //  populateSubclassLov();
                populateMultiProduct();

            },
            data: (data) ? data : [],
            formatResult: function (a) {
                return a.binName;
            },
            formatSelection: function (a) {
                return a.binName;
            },
            initSelection: function (element, callback) {
                var code = $('#binder-id').val();
                var name = $("#bind-name").val();
                var data = {binName: name, binId: code};
                callback(data);
            },
            id: "binId",
            width: "250px",
            multiple: ($("#pol-bin-type").val() && $("#pol-bin-type").val() === 'MP'),
            params: {
                bindType: $("#pol-bin-type").val(),
                productId: selectedProductId
            },
            placeholder: "Select Contract"

        });
    }
};

// function populateBinderLov(){
//     if($("#binder-frm").filter("div").html() != undefined)
//     {
//         Select2Builder.initAjaxSelect2({
//             containerId : "binder-frm",
//             sort : 'binName',
//             change: function(e, a, v){
//                 $("#binder-id").val(e.added.binId);
//                 $("#bind-pro-name").val(e.added.product.proDesc);
//                 $("#bind-ins-name").val(e.added.account.name);
//                 $("#pol-ins-comp").text(e.added.account.name);
//                 $("#pol-prod-name").text(e.added.product.proDesc);
//                 $("#product-id").val(e.added.product.proCode);
//                 $("#pol-agent-id").val(e.added.account.acctId);
//                 $("#client-pol-no").val(e.added.binPolNo);
//                 $("#bind-name").val(e.added.binName);
//                 $("#pol-medicalCoverType").val(e.added.medicalCoverType);
//                 if (e.added.medicalCoverType==='G')
//                     $("#medicalCoverTyp-info").text('Group');
//                 if (e.added.medicalCoverType==='I')
//                     $("#medicalCoverTyp-info").text('Individual');
//                 $("#comm-rate").val(e.added.account.accountType.commRate);
//                 //populateSubclassLov();
//             },
//             formatResult : function(a)
//             {
//                 return a.binName;
//             },
//             formatSelection : function(a)
//             {
//                 return a.binName;
//             },
//             initSelection: function (element, callback) {
//                 var code  = $('#binder-id').val();
//                 var name = $("#bind-name").val();
//                 var data = {binName:name,binId:code};
//                 callback(data);
//             },
//             id: "binId",
//             width:"250px",
//             params: {bindType: $("#pol-bin-type").val()},
//             placeholder:"Select Contract"
//
//         });
//     }
// }

function populateClientLov(){
    if($("#client-frm").filter("div").html() != undefined)
    {
        Select2Builder.initAjaxSelect2({
            containerId : "client-frm",
            sort : 'fname',
            change: function(e, a, v){
                $("#client-id").val(e.added.tenId);
                $("#client-f-name").val(e.added.fname);
                $("#client-other-name").val(e.added.otherNames);
               // populateInsuredLov();

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
            placeholder:"Select Client"

        });
    }
}

function addNewClient(){

    //$("#btn-add-client").click(function(){
    //    $(".clnt-status").hide();
    //    $('#tenant-form').find("input[type=text],input[type=number],input[type=mobileNumber],input[type=emailFull],input[type=password],input[type=hidden], textarea,select").val("");
    //    // console.log("waititu 3 testing")
    //    $('#createClientModal').modal({
    //        backdrop: 'static',
    //        keyboard: true
    //    })
    //})
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

function createPolicyCategories(){
    var url = "getPolicyCategories";
    var currTable = $('#polTaxesList').DataTable( {
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
            { "data": "revenueItems",
                "render": function ( data, type, full, meta ) {
                    return UTILITIES.getRevDesc(full.revenueItems.item);
                }
            },
            { "data": "taxRate" },
            { "data": "divFactor" },
            { "data": "rateType" },
            { "data": "taxAmount",
                "render": function ( data, type, full, meta ) {

                    return UTILITIES.currencyFormat(full.taxAmount);
                }
            },
            { "data": "taxLevel",
                "render": function ( data, type, full, meta ) {
                    if(full.taxLevel){
                        if(full.taxLevel==="R")
                            return "Risk";
                        else if(full.taxLevel==="P")
                            return "Policy";
                    }
                    else
                        return "Policy";

                }
            },
            {
                "data": "polTaxId",
                "render": function ( data, type, full, meta ) {
                    if(full.policy.authStatus){
                        if(full.policy.authStatus==="D")
                            return '<button type="button" class="btn btn-success btn btn-info btn-sm" type="button" data-poltaxes='+encodeURI(JSON.stringify(full)) + ' onclick="editPolTaxes(this);"><i class="fa fa-pencil-square-o"></button>';
                        else
                            return '<button type="button" class="btn btn-success btn btn-info btn-sm" type="button" data-poltaxes='+encodeURI(JSON.stringify(full)) + ' onclick="editPolTaxes(this);" disabled><i class="fa fa-pencil-square-o"></button>';

                    }
                    else
                        return '<button type="button" class="btn btn-success btn btn-info btn-sm" type="button" data-poltaxes='+encodeURI(JSON.stringify(full)) + ' onclick="editPolTaxes(this);" disabled><i class="fa fa-pencil-square-o"></button>';

                }

            },
            {
                "data": "polTaxId",
                "render": function ( data, type, full, meta ) {
                    if(full.policy.authStatus){
                        if(full.policy.authStatus==="D")
                            return '<button type="button" class="btn btn-danger btn btn-danger btn-sm" data-poltaxes='+encodeURI(JSON.stringify(full)) + ' onclick="deletePolTaxes(this);"><i class="fa fa-trash-o"></button>';
                        else
                            return '<button type="button" class="btn btn-danger btn btn-danger btn-sm" data-poltaxes='+encodeURI(JSON.stringify(full)) + ' onclick="deletePolTaxes(this);" disabled><i class="fa fa-trash-o"></button>';

                    }
                    else
                        return '<button type="button" class="btn btn-danger btn btn-danger btn-sm" data-poltaxes='+encodeURI(JSON.stringify(full)) + ' onclick="deletePolTaxes(this);" disabled><i class="fa fa-trash-o"></button>';


                }

            },
        ]
    } );
    return currTable;
}
