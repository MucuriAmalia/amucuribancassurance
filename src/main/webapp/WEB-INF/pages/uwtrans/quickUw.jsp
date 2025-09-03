<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<script type="text/javascript"
        src="<c:url value="/js/modules/utils/select2builder.js"/>"></script>
<script type="text/javascript"
        src="<c:url value="/libs/rivets/rivets.js"/>"></script>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@ taglib prefix="sec" uri="http://www.springframework.org/security/tags" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<div class="x_panel">
    <div class="x_title">
        <h2><i class="fa fa-bars"></i> Quick Motor Underwriting</h2>
        <div class="clearfix"></div>
    </div>
    <div class="x_content">
    </div>
</div>
<script>

    $(document).ready(function () {

        UW.quickUW.init();
    });

    if(!window.UW)
        window.UW = {};

    UW.quickUW = {
        createValidatorForm : function($form_container) {
            $form_container.children("div").steps({
                headerTag: "h3",
                bodyTag: "section",
                transitionEffect: "fade",
                stepsOrientation: "vertical",
                onStepChanging: function (event, currentIndex, newIndex) {
                    if(currentIndex===0){
                        var form = $("#policy-form");
                        form.validate({
                            ignore: []
                        });
                        if (!form.valid()) {
                            return false;
                        }
                        if(!UW.quickUW.validatePin()){
                            $.showAlert({title: "Agent Portal", body: 'Pin is Invalid....Please put in a valid pin to continue...'});
                            return false;
                        }
                    }
                    if(currentIndex===1) {
                        var riskform = $("#risk-form");
                        var riskFrom = $("#risk-from-date").val();
                        var riskTo = $("#risk-wet-date").val();
                        var riskId = $("#riskId").val();
                        var chassisNo = $("#chassisNumber").val();

                        riskform.validate({
                            ignore: []
                        });
                        if (!riskform.valid()) {
                            return false;
                        }
                        UW.quickUW.validateInsurance(riskFrom, riskTo, riskId,chassisNo);
                    }
                    return true;
                },
                onFinishing: function (event, currentIndex) {
                    var vehicleform = $("#vehicle-form");
                    vehicleform.validate({
                        ignore: []
                    });
                    if (!vehicleform.valid()) {
                        return false;
                    }
                    return true;
                },
                onFinished: function (event, currentIndex) {
                    UW.quickUW.createRecord();

                }
            });
            return $form_container;
        },
        stringEmpty: function (str){
            if (typeof str == 'undefined' || !str || str.length === 0 || str === "" || !/[^\s]/.test(str) || /^\s*$/.test(str) || str.replace(/\s/g,"") === "")
                return true;
            else
                return false;
        },
        validatePin: function (){
            var elem = document.getElementById("pinNo");
            var re = /^[A,P]{1}[0-9]{9}[a-zA-Z]{1}$/;
            return re.test(elem.value);
        },
        getPolicyWet : function(){
            $('#wef-date').on('dp.change', function (ev) {
                var curDate = ev.date;
                var dt = moment(curDate).format('DD/MM/YYYY');
                var cdate = moment(curDate);

                if($('#val_1').is(':checked')){
                    var futureMonth = moment(cdate).add(1, 'M').subtract(1, 'd');
                    $("#risk-wet-date").val(moment(futureMonth).format('DD/MM/YYYY'));
                }
                else if($('#val_6').is(':checked')){
                    var futureMonth = moment(cdate).add(6, 'M').subtract(1, 'd');
                    $("#risk-wet-date").val(moment(futureMonth).format('DD/MM/YYYY'));
                }
                else if($('#val_12').is(':checked')){
                    var futureMonth = moment(cdate).add(12, 'M').subtract(1, 'd');
                    $("#risk-wet-date").val(moment(futureMonth).format('DD/MM/YYYY'));
                }
            });
        },
        createRecord: function () {
            var htmlerror = $("#double-ins-error").val();
            if(!UW.quickUW.stringEmpty(htmlerror)) {
                return;
            }
            var form = $('#risk-form')[0];
            var data = new FormData(form);
            data.append("pinNumber", $("#pinNo").val());
            data.append("product", $("#products").val());
            data.append("policyHolderEmail", $("#policyHolderEmail").val());
            data.append("policyHolderPhone", $("#policyHolderPhone").val());
            if($("#torPolicy").is(':checked')) {
                data.append("torPolicy", 'on');
            }
            else{
                data.append("torPolicy", 'off');
            }
            data.append("policyIdNumber", $("#policyIdNumber").val());
            data.append("policyHolder", $("#policyHolder").val());
            data.append("make", $("#make").val());
            if (typeof $("#model").val() !== 'undefined'){
                data.append("model", $("#model").val());
            }
            else{
                data.append("model", '');
            }
            if (typeof $("#bodyType").val() !== 'undefined'){
                data.append("bodyType", $("#bodyType").val());
            }
            else{
                data.append("bodyType", '');
            }
            data.append("tonnage", $("#tonnage").val());
            data.append("yom", $("#yom").val());
            data.append("carryCapacity", $("#carryCapacity").val());
            data.append("chassisNumber", $("#chassisNumber").val());
            data.append("butchargePrem", $("#butchargePrem").val());
            console.log(data);
            $.ajax({
                type: "POST",
                enctype: 'multipart/form-data',
                url: SERVLET_CONTEXT + '/api/v1/uwtrans/quickpolicy',
                data: data,
                processData: false,
                contentType: false,
                cache: false,
                timeout: 600000,
                success: function (data) {
                    $('body').pleaseWait('stop');
                    window.location.href = SERVLET_CONTEXT+"/protected/home/policies/update/"+data.policyHashCode;
                },
                error: function(jqXHR, textStatus, errorThrown){
                    $('body').pleaseWait('stop');
                    var obj = $.parseJSON(jqXHR.responseText);
                    console.log(obj['message']);
                    $("#error-msg").css('display','block');
                    $("#error-msg").html(obj['message']);
                    setTimeout(function() {
                        $("#error-msg").css('display','none');
                    }, 30000);

                },
                beforeSend: function(){
                    $('body').pleaseWait();
                }
            });
        },
        validateInsurance: function (coverFrom,coverTo,regno,chassisNo){
            $("#error-msg").css('display','block');
            $("#error-msg").html('Checking Double Insurance. Please Wait.....');
            $("#double-ins-error").val("Checking error...");
            $.ajax({
                type: 'GET',
                url:  SERVLET_CONTEXT+'/api/v1/integration/doubleInsurance?regNo='+regno+'&coverFrom='+coverFrom+'&coverTo='+coverTo+'&chasis='+chassisNo,
                dataType: 'json',
                async: true,
                success: function(result) {
                    if(result.doubleInsurance && result.doubleInsurance.length > 0){
                        var doubleInsurance = result.doubleInsurance;
                        $("#error-msg").css('display','block');
                        $("#error-msg").html("<span>The Vehicle Reg No: "+doubleInsurance[0].registrationNumber+" with Chassis No "+doubleInsurance[0].chassisNumber+" is already insured at "+doubleInsurance[0].memberCompanyName+" Certificate Number "+doubleInsurance[0].insuranceCertificateNo+" valid to "+doubleInsurance[0].coverEndDate+". Please go back to previous screen and make changes to dates</span>");
                        $("#double-ins-error").val('Invalid Insurance.....');
                    }
                    else{
                        $("#error-msg").css('display','none');
                        $("#error-msg").html("");
                        $("#double-ins-error").val("");
                    }

                },
                error: function(jqXHR, textStatus, errorThrown) {
                    $("#double-ins-error").val("");
                }
            });
        },
        init: function (){
            UW.quickUW.createValidatorForm($("#policy-wizard"));
            UW.quickUW.loadLovs();
            UW.quickUW.getPolicyWet();

            $('input[type=radio][name=covertype]').change(function() {
                if (this.value === '97') {
                    $("#sumInsured").val('').prop('disabled', false).prop('required',true);
                    $(".docs").css('display','block');
                }
                else if (this.value === '86') {
                    $("#sumInsured").val('').prop('disabled', true).prop('required',false);
                    $(".docs").css('display','none');
                }
            });

            $('input[type=radio][name=period]').change(function() {
                var dtFromString= $("#risk-from-date").val();
                var dtFrom = moment(dtFromString, 'DD/MM/YYYY').toDate();
                if (this.value === '1') {
                    var futureMonth = moment(dtFrom).add(1, 'M').subtract(1, 'd');
                    $("#risk-wet-date").val(moment(futureMonth).format('DD/MM/YYYY'));
                }
                else if (this.value === '12') {
                    var futureMonth = moment(dtFrom).add(12, 'M').subtract(1, 'd');
                    $("#risk-wet-date").val(moment(futureMonth).format('DD/MM/YYYY'));
                }
                else if (this.value === '6') {
                    var futureMonth = moment(dtFrom).add(6, 'M').subtract(1, 'd');
                    $("#risk-wet-date").val(moment(futureMonth).format('DD/MM/YYYY'));
                }
            });

            $('#torPolicy').on('change', function(){
                if(this.checked)
                {
                    $("#comp").attr("disabled",true);
                    $("#val_1").attr('checked', true);
                    $("#sumInsured").val('').prop('disabled', true).prop('required',false);
                    $(".docs").css('display','none');
                    $("#val_6").attr("disabled",true);
                    $("#val_12").attr("disabled",true);
                    $("#tpt").attr('checked', true);
                    var dtFromString= $("#risk-from-date").val();
                    var dtFrom = moment(dtFromString, 'DD/MM/YYYY').toDate();
                    var futureMonth = moment(dtFrom).add(1, 'M').subtract(1, 'd');
                    $("#risk-wet-date").val(moment(futureMonth).format('DD/MM/YYYY'));
                }
                else{
                    $("#comp").attr("disabled",false);
                    $("#tpt").attr('checked', false);
                    $("#val_6").attr("disabled",false);
                    $("#val_12").attr("disabled",false);
                    $("#val_1").attr('checked', false);
                }
            })

            var currentYear = new Date().getFullYear();
            var yearRange = Array(50).fill(0);
            var years = yearRange.map(
                (item, index) => (item[index] = currentYear - index)
            );

            years.forEach(function(item){
                let option=$('<option></option>').html(item);
                $('#yom').append(option);
            });
            $('#yom').select2();

            $(".datepicker-input").each(function() {
                $(this).datetimepicker({
                    format: 'DD/MM/YYYY'
                });

            });

            $("#risk-from-date").val(moment().format('DD/MM/YYYY'));

            $("#pinNo").bind("focusout", function (e) {
                $.ajax({
                    url: SERVLET_CONTEXT + '/api/v1/uwtrans/getClientDetails?pinNo='+$("#pinNo").val(),
                    type: 'GET',
                    processData: false,
                    contentType: false,
                    async: true,
                    success: function (s) {
                        if(s && s.clients) {
                            // $(".idcopy").css('display','none');
                            // $(".pincopy").css('display','none');
                            // $("#idCopy").prop('required',false);
                            // $("#pinCopy").prop('required',false);
                            if (s.clients.fname) {
                                $("#policyHolder").val(s.clients.fname + " " + s.clients.otherNames).prop('readonly', true);
                            } else {
                                $("#policyHolder").prop('readonly', false);
                            }
                            if (s.clients.phoneNo) {
                                $("#policyHolderPhone").val(s.clients.phoneNo).prop('readonly', true);
                            } else {
                                $("#policyHolderPhone").prop('readonly', false);
                            }
                            if (s.clients.emailAddress) {
                                $("#policyHolderEmail").val(s.clients.emailAddress).prop('readonly', true);
                            } else {
                                $("#policyHolderEmail").prop('readonly', false);
                            }
                            if (s.clients.idNo) {
                                $("#policyIdNumber").val(s.clients.idNo).prop('readonly', true);
                            } else {
                                $("#policyIdNumber").prop('readonly', false);
                            }
                        }
                        else{
                            // $(".idcopy").css('display','block');
                            // $(".pincopy").css('display','block');
                            // $("#idCopy").prop('required',true);
                            // $("#pinCopy").prop('required',true);
                            $("#policyIdNumber").prop('readonly', false);
                            $("#policyHolderEmail").prop('readonly', false);
                            $("#policyHolderPhone").prop('readonly', false);
                            $("#policyHolder").prop('readonly', false);
                        }
                        $('body').pleaseWait('stop');
                    },
                    error: function (xhr, error) {
                        $('body').pleaseWait('stop');
                    },
                    beforeSend: function () {
                        $('body').pleaseWait();
                    }
                });
            });

            $("#pinNo").keydown(function (event) {
                if (event.which == 13) {
                    $.ajax({
                        url: SERVLET_CONTEXT + '/api/v1/uwtrans/getClientDetails?pinNo='+$("#pinNo").val(),
                        type: 'GET',
                        processData: false,
                        contentType: false,
                        async: true,
                        success: function (s) {
                            if(s && s.clients) {
                                $(".idcopy").css('display','none');
                                $(".pincopy").css('display','none');
                                if (s.clients.fname) {
                                    $("#policyHolder").val(s.clients.fname + " " + s.clients.otherNames).prop('readonly', true);
                                } else {
                                    $("#policyHolder").prop('readonly', false);
                                }
                                if (s.clients.phoneNo) {
                                    $("#policyHolderPhone").val(s.clients.phoneNo).prop('readonly', true);
                                } else {
                                    $("#policyHolderPhone").prop('readonly', false);
                                }
                                if (s.clients.emailAddress) {
                                    $("#policyHolderEmail").val(s.clients.emailAddress).prop('readonly', true);
                                } else {
                                    $("#policyHolderEmail").prop('readonly', false);
                                }
                                if (s.clients.idNo) {
                                    $("#policyIdNumber").val(s.clients.idNo).prop('readonly', true);
                                } else {
                                    $("#policyIdNumber").prop('readonly', false);
                                }
                            }
                            else{
                                $(".idcopy").css('display','block');
                                $(".pincopy").css('display','block');
                                $("#policyIdNumber").prop('readonly', false);
                                $("#policyHolderEmail").prop('readonly', false);
                                $("#policyHolderPhone").prop('readonly', false);
                                $("#policyHolder").prop('readonly', false);
                            }
                            $('body').pleaseWait('stop');
                        },
                        error: function (xhr, error) {
                            $('body').pleaseWait('stop');
                        },
                        beforeSend: function () {
                            $('body').pleaseWait();
                        }
                    });

                }
            });
        },
    }

</script>


