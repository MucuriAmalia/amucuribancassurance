/**
 * Created by peter on 3/5/2017.
 */

$(function() {

    $(document).ready(function () {

        $(".datepicker-input").each(function() {
            $(this).datetimepicker({
                format: 'DD/MM/YYYY'
            });

        });

        if($("#loss-date").val() != ''){
            var dt = moment($("#loss-date").val()).format('DD/MM/YYYY');
            selectRiskLov(dt);
        }


        changeLossDate();
        selectNextReviewLov();
        selectClmActivity();
        addClmPeril();
        addSelectedPeril();
        addClaimants();
        saveClaimant();
        selectOccupationLov();
        createCurrencyLov();
        populatePaymentModes();
        createBankBranchLov();
        bankAccountsLov();

        $("#pay-type").on("change", function()
        {
            if(this.value && this.value==="CP"){
                selectClaimantLov();
                $(".tpd").css("display","block");
                $(".spr").css("display","none");
                $("#self-as-clmnt").removeAttr("disabled");
            }
            else if(this.value && this.value==="SP"){
                $("#self-as-clmnt").attr("disabled", true);
                selectServiceProviderLov();
                $(".tpd").css("display","none");
                $(".spr").css("display","block");
            }
            else{
                $(".tpd").css("display","none");
                $(".spr").css("display","none");
            }
        });


        $('#self-as-clmnt').change(function(){
            if(this.checked){
                $("#tpd-clmant-def").select2("readonly", true);
            }
            else{
                $("#tpd-clmant-def").select2("readonly", false);
            }
        });

    });
});


function bankAccountsLov (){
    if($("#acct-frm").filter("div").html() !== undefined)
    {
        Select2Builder.initAjaxSelect2({
            containerId : "acct-frm",
            sort : 'bankAcctName',
            change: function(e, a, v){
                $("#acct-id").val(e.added.baId);
            },
            formatResult : function(a)
            {
                return a.bankAcctName;
            },
            formatSelection : function(a)
            {
                return a.bankAcctName;
            },
            initSelection: function (element, callback) {

            },
            id: "baId",
            width:"250px",
            placeholder:"Select Bank Account"

        });
    }
}

function createCurrencyLov(){
    if($("#cur-def").filter("div").html() != undefined)
    {
        Select2Builder.initAjaxSelect2({
            containerId : "cur-def",
            sort : 'curName',
            change: function(e, a, v){
                $("#cur-id").val(e.added.curCode);
            },
            formatResult : function(a)
            {
                return a.curName
            },
            formatSelection : function(a)
            {
                return a.curName
            },
            initSelection: function (element, callback) {

            },
            id: "curCode",
            placeholder:"Select Currency",
        });
    }
}

function createBankBranchLov (){
    if($("#bank-branch-lov").filter("div").html() != undefined)
    {
        Select2Builder.initAjaxSelect2({
            containerId : "bank-branch-lov",
            sort : 'branchName',
            change: function(e, a, v){
                $("#bank-branch-id").val(e.added.bbId);
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

            },
            id: "bbId",
            placeholder:"Select Bank Branch",
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

            },
            id: "pmId",
            width:"250px",
            placeholder:"Select Payment Mode"

        });
    }
}


function addClmPeril(){
    $("#add-peril-btn").on('click',function(){
        $('#new-peril-form').find("input[type=text],input[type=mobileNumber],input[type=emailFull],input[type=password],input[type=hidden],input[type=number], textarea").val("");
        $('#new-peril-form').find("input[type=checkbox]").attr("checked", false);
        $("#clm-estimate").number( true, 2 );
        $('#perilTransModal').modal({
            backdrop : 'static',
            keyboard : true
        })
    });
}

function delRow(currElement) {
    var parentRowIndex = currElement.parentNode.parentNode.rowIndex;
    console.log("parentRowIndex="+parentRowIndex);
    $(currElement).closest('tr').remove();
    //document.getElementById($('#peril-table-id')).deleteRow(parentRowIndex);
}

function addSelectedPeril() {
    $("#btn-add-peril-selected").on('click', function () {
        if ($("#peril-code").val() === '') {
            bootbox.alert("Select Peril");
            return;
        }
        var count = $("#peril-table-id > tbody > tr").length;
        var data = "";
        var checked = "off";
        if ($("#self-as-clmnt").is(":checked")) {
            checked = "on";
        } else checked = "off";
        data += "<tr>" +
            "<td>" +
            "     <input type='hidden' name='perils[" + count + "].perilCode' value='" + $("#peril-code").val() + "'>" + $("#peril-name").val() +
            "</td>" +
            "<td>" +
            "     <input type='hidden' name='perils[" + count + "].perilEstimate' value='" + $("#clm-estimate").val() + "'>" + $("#clm-estimate").val() +
            "</td> " +
            "<td> " +
            "      <input type='button' class='hyperlink-btn btn btn-danger' value='Delete' onclick='delRow(this)'>" +
            "</td>" +
            "</tr>";
        console.log(data);
        $('#peril-table-id').append(data);
        $('#perilTransModal').modal('hide');
    });
}

function changeLossDate(){
    $('#los-date').on('dp.change', function (ev) {
        var curDate = ev.date;
        var dt = moment(curDate).format('DD/MM/YYYY');
        selectRiskLov(dt);
    });
}


function addClaimants(){
    $("#btn-add-claimant").click(function(){
        $('#claimants-form').find("input[type=text],input[type=number],input[type=mobileNumber],input[type=emailFull],input[type=password],input[type=hidden], textarea,select").val("");
        $('#claimants-form').select2('val', null);
        selectOccupationLov();
        $('#claimantsModal').modal({
            backdrop: 'static',
            keyboard: true
        })
    });
}


function selectOccupationLov(){
    if($("#occup-def").filter("div").html() != undefined)
    {
        Select2Builder.initAjaxSelect2({
            containerId : "occup-def",
            sort : 'name',
            change: function(e, a, v){
                $("#clmt-occupt").val(e.added.code);
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
                var code = $("#clmt-occupt").val();
                var name = $("#clmt-occupt-names").val();
//	                model.accounts.branch.brnCode = code;
                var data = {name:name,code:code};
                callback(data);
            },
            id: "code",
            placeholder:"Select Occupation"
        });
    }
}



function saveClaimant(){
    var $classForm = $('#claimants-form');
    var validator = $classForm.validate();
    $('#saveClaimantDef').click(function(){
        if (!$classForm.valid()) {
            return;
        }
        var $btn = $(this).button('Saving');
        var data = {};
        $classForm.serializeArray().map(function(x){data[x.name] = x.value;});
        var url = "createClaimant";
        var request = $.post(url, data );
        request.success(function(){
            // $('#myPleaseWait').modal('hide');
             Swal.fire({
                title: 'Success',
                text: 'Record created Successfully',
                icon: 'success'
            });
            $('#claimant-tbl').DataTable().ajax.reload();
            validator.resetForm();
            $('#claimants-form').find("input[type=text],input[type=number],input[type=mobileNumber],input[type=emailFull],input[type=password],input[type=hidden], textarea,select").val("");
            $('#claimants-form').select2('val', null);
            $('#claimantsModal').modal('hide');
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


function selectServiceProviderLov(){
    if($("#spr-clmant-def").filter("div").html() != undefined)
    {
        Select2Builder.initAjaxSelect2({
            containerId : "spr-clmant-def",
            sort : 'name',
            change: function(e,a,v){
                $("#spr-code").val(e.added.providerId);
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
            id: "providerId",
            placeholder:"Select Service Provider"
        });
    }
}



function selectClaimantLov(){
    if($("#tpd-clmant-def").filter("div").html() != undefined)
    {
        Select2Builder.initAjaxSelect2({
            containerId : "tpd-clmant-def",
            sort : 'surname',
            change: function(e,a,v){
                $("#clmt-code").val(e.added.claimantId);
                $("#clmt-name").val(e.added.surname+" "+ e.added.otherNames);

            },
            formatResult : function(a)
            {
                return a.surname+" "+ a.otherNames;
            },
            formatSelection : function(a)
            {
                return a.surname+" "+ a.otherNames;
            },
            initSelection: function (element, callback) {

            },
            id: "claimantId",
            placeholder:"Select Claimant"
        });
    }
}


function selectRiskPerils(riskId){
    if($("#peril-def").filter("div").html() != undefined)
    {
        Select2Builder.initAjaxSelect2({
            containerId : "peril-def",
            sort : 'bindPerilCode',
            change: function(e, a, v){
                $("#peril-code").val(e.added.bindPerilCode);
                $("#peril-name").val(e.added.perilDesc);
            },
            formatResult : function(a)
            {
                console.log(a);
                return a.perilDesc;
            },
            formatSelection : function(a)
            {
                return a.perilDesc;
            },
            initSelection: function (element, callback) {

            },
            id: "bindPerilCode",
            params: {riskId: riskId},
            placeholder:"Select Peril"
        });
    }
}


function selectNextReviewLov(){
    if($("#review-user").filter("div").html() != undefined)
    {
        Select2Builder.initAjaxSelect2({
            containerId : "review-user",
            sort : 'username',
            change: function(e,a,v){
                $("#next-rev-user").val(e.added.id);
                $("#next-rev-user-desc").val(e.added.username);
            },
            formatResult : function(a)
            {
                return a.username
            },
            formatSelection : function(a)
            {
                return a.username
            },
            initSelection: function (element, callback) {
                var code = $("#next-rev-user").val();
                var name = $("#next-rev-user-desc").val();
                var data = {username:name,id:code};
                callback(data);
            },
            id: "id",
            placeholder:"Select Next Review User"
        });
    }
}

function selectProvidersByTypeLov(){
    if($("#binder-provider-frm").filter("div").html() != undefined)
    {
        Select2Builder.initAjaxSelect2({
            containerId : "binder-provider-frm",
            sort : 'serviceProviders.name',
            change: function(e,a,v){
                $("#provider-code").val(e.added.spcId);
                $("#provider-desc").val(e.added.serviceProviders.name);
            },
            formatResult : function(a)
            {
                return a.serviceProviders.name
            },
            formatSelection : function(a)
            {
                return a.serviceProviders.name
            },
            initSelection: function (element, callback) {
            },
            id: "spcId",
            params: {bindCode: $("#binder-code").val(),typeCode: $("#provider-type").val()},
            placeholder:"Select Provider Type"
        });
    }
}
function selectProviderTypesLov(){
    if($("#providerType-frm").filter("div").html() != undefined)
    {
        Select2Builder.initAjaxSelect2({
            containerId : "providerType-frm",
            sort : 'shtDesc',
            change: function(e,a,v){
                $("#provider-type").val(e.added.id);
                $("#provider-type-desc").val(e.added.desc);
                selectProvidersByTypeLov();
            },
            formatResult : function(a)
            {
                return a.desc
            },
            formatSelection : function(a)
            {
                return a.desc
            },
            initSelection: function (element, callback) {
                var code = $("#provider-type").val();
                var name = $("#provider-type-desc").val();
                var data = {desc:name,id:code};
                callback(data);
            },
            id: "id",
            params: {bindCode: $("#binder-code").val()},
            placeholder:"Select Provider Type"
        });
    }
}





function getPolicyBalance(polId){

    $.ajax( {
        url: 'claimBalance/'+polId,
        type: 'GET',
        processData: false,
        contentType: false,
        success: function (s ) {
            $("#pol-client-balance").text(s.clientBalance);
            $("#pol-ins-balance").text(s.insBalance);

        },
        error: function(xhr, error){
            //bootbox.alert(xhr.responseText);
        }
    });

}

function getExpiringSection(peril,risk){
    $.ajax( {
        type: 'GET',
        url:  'getExpireRiskSection/'+peril+'/'+risk,
        processData: false,
        contentType: false,
        async:false,
        success: function (result ) {
            for (var res in result) {
                $("#expire-section-id").val(result[res].sectId);
                $("#expire-section").val(result[res].section);
            }
        },
        error: function(xhr, error){
            bootbox.alert(xhr.responseText);
        }
    });
}

function selectRiskLov(lossdate){
    if($("#risk-frm").filter("div").html() != undefined)
    {
        Select2Builder.initAjaxSelect2({
            containerId : "risk-frm",
            sort : 'riskShtDesc',
            change: function(e, a, v){
                $("#risk-id").val(e.added.riskId);
                $("#risk-desc").val(e.added.riskDesc);
                $("#risk-sht-desc").val(e.added.riskShtDesc);
                // if (e.added.binder){
                $("#binder-code").val(e.added.binderId);
                // }else {
                //     $("#binder-code").val(e.added.policy.binder.binId);
                // }

                $("#insured-name").val(e.added.clientname);

                selectRiskPerils(e.added.riskId);
                getPolicyBalance(e.added.polId);
                selectProviderTypesLov();
            },
            formatResult : function(a)
            {
                return "Risk: "+a.riskShtDesc +" Policy: "+ a.polno+" Insured: "+ a.clientname
            },
            formatSelection : function(a)
            {
                // return a.riskShtDesc+" "+a.riskDesc
                return "Risk: "+a.riskShtDesc +" Policy: "+ a.polno
            },
            initSelection: function (element, callback) {
                var code = $("#risk-id").val();
                var name = $("#risk-desc").val();
                var shtdesc = $("#risk-sht-desc").val();
                var data = {riskDesc:name,riskShtDesc:shtdesc,riskId:code};
                callback(data);
            },
            id: "riskId",
            params: {lossDate: lossdate},
            placeholder:"Select A Risk",
        });
    }
}

function selectClmActivity(){
    if($("#clm-activity").filter("div").html() != undefined)
    {
        Select2Builder.initAjaxSelect2({
            containerId : "clm-activity",
            sort : 'activityDesc',
            change: function(e,a,v){
                $("#activity-code").val(e.added.caId);
                $("#activity-desc").val(e.added.activityDesc);
            },
            formatResult : function(a)
            {
                return a.activityDesc
            },
            formatSelection : function(a)
            {
                return a.activityDesc
            },
            initSelection: function (element, callback) {
                var code = $("#activity-code").val();
                var name = $("#activity-desc").val();
                var data = {activityDesc:name,caId:code};
                callback(data);
            },
            id: "caId",
            placeholder:"Select Causation"
        });
    }
}
