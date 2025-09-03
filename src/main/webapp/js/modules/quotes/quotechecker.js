$(function() {


    $(document).ready(function () {

        showCheckerQuoteData();

        $("#btn-approve-quote").on('click', function (){
            approveTask();
        });

        $("#btn-reject-quot").on('click', function (){
            rejectTask();
        });
    });

    function rejectTask(){
        // Display the modal
        $('#rejectionModal').modal('show');

        $('#rejectConfirmButton').off('click').on('click', function() {
            var reason = $('#rejectionReason').val();

            if (reason) {
                $.ajax({
                    url: SERVLET_CONTEXT + '/protected/users/rejectTask',
                    type: 'POST',
                    contentType: 'application/json',
                    data: JSON.stringify({
                        taskId: mck_id_quote,
                        reason: reason
                    }),
                    success: function(response) {
                        window.location.href = SERVLET_CONTEXT + "/protected/home";

                    },
                    error: function(xhr, status, error) {
                        console.error("Error rejecting task: ", error);
                    }
                });
            } else {
                console.error("No reason provided for rejection");
            }
            // Hide the modal after submission
            $('#rejectionModal').modal('hide');
        });
    }

    function approveTask(){
        // Display the modal
        $('#approvalModal').modal('show');
        $('#approveConfirmButton').off('click').on('click', function() {

            $.ajax({
                url: SERVLET_CONTEXT + '/protected/users/approveTask',
                type: 'POST',
                contentType: 'application/json',
                data: JSON.stringify(mck_id_quote),
                success: function(response) {
                    window.location.href = SERVLET_CONTEXT + "/protected/home";
                },
                error: function(xhr, status, error) {
                    console.error("Error approving task: ", error);
                }
            });

            // Hide the modal after submission
            $('#approvalModal').modal('hide');
        });
    }

    function showCheckerQuoteData(){
        var mckIdNo = mck_id_quote;  // Make sure mck_id_no is set
        var url = SERVLET_CONTEXT + "/protected/getCheckerData";
        var taskType = "quote";

        $.ajax({
            url: url,
            method: 'GET',
            data: {
                mckIdNo: mckIdNo,
                taskType: taskType
            },
            success: function(data) {
                var clientData = (typeof data === "string") ? JSON.parse(data) : data;
                displayUnverifiedQuoteDetails(clientData);

            },
            error: function(xhr, status, error) {
                console.error('Error fetching client data:', error);
            }
        });
    }
    function displayUnverifiedQuoteDetails(s) {
        $("#btn-reject-quot").css("display","block");
        $("#btn-approve-quote").css("display","block");
        $("#pol-status").text("Pending Verification").css('color','red').css('font-weight','bold');
        $("#edit-currency").hide();
        $("#display-currency").show();
        $("#edit-branch").hide();
        $("#display-branch").show();
        $("#edit-payment-mode").hide();
        $("#display-payment-mode").show();
        $("#edit-binder").hide();
        $("#display-binder").show();
        $("#edit-client").hide();
        $("#display-client").show();
        $("#poli-remarks").attr("disabled", "disabled");
        $("#pol-buss-type").prop("disabled", true);
        $("#pol-bin-type").prop("disabled", true);
        $("#from-date").prop("disabled", true);
        $("#wet-date").prop("disabled", true);
        $('#clnt-type').prop("disabled",true);
        $("#btn-save-quot-prod").hide();
        $("#btn-cancel-quot-prod").hide();
        $("#btn-add-quot-prod").hide();
        $("#btn-add-risk").hide();
        //$("#btn-add-new-clause").show();
        //$("#btn-add-new-tax").show();
        $("#btn-add-new-clause").hide();
        $("#btn-add-new-tax").hide();
        $("#btn-add-new-section").hide();
        $("#btn-add-new-remark").hide();
        $("#btn-save-remark").hide();
        $("#display-source").show();
        $("#display-sourcegroup").show();
        $("#edit-sourcegroup").hide();
        $("#edit-source").hide();
        $("#combined-quote").hide();
        $("#comparison-quote").hide();
        $("#comparison-type").hide();
        $("#btn-compare-quot").hide();
        if (s.quoteType === "combined"){
            $("#quote-info").text("Combined Quote");
        } else if (s.quoteType === "comparison") {
            $("#quote-info").text("Comparison Quote");
        } else {
            $("#quote-info").text("Not Defined");
        }
        if (s.quoteType === "combined") {
            $("#btn-compare-quot").hide();
        }
        if (s.quoteType === "comparison") {
            $("#btn-add-quot-prod").hide();
        }
        $("#client-info").text(s.fname+" "+s.otherNames);
        $("#pay-mode-info").text(s.paymentMode);
        $("#source-info").text(s.sourceName);
        $("#source-name").val(s.sourceName);
        $("#source-id").val(s.sourceId);
        $("#sourcegroup-id").val(s.sourceGroupId);
        $("#sourcegroup-name").val(s.sourceGroupName);
        $("#sourcegroup-info").text(s.sourceGroupName);
        populateSourceGroupLov();

        $("#branch-info").text(s.branch);
        $("#currency-info").text(s.currency);
        $("#pol-no").text(s.quotNo);
        $("#pol-rev-no").text(s.quotRevNo);
        $("#pol-sum-insured").text(UTILITIES.currencyFormat(s.sumInsured));
        $("#pol-premium").text(UTILITIES.currencyFormat(s.premium));
        $("#pol-basic-prem").text(UTILITIES.currencyFormat(s.basicPrem));
        $("#pol-net-prem").text(UTILITIES.currencyFormat(s.netPrem));
        $("#from-date").val(moment(s.quoteWef).format('DD/MM/YYYY'));
        $("#wet-date").val(moment(s.quoteWet).format('DD/MM/YYYY'));
        $('#clnt-type').val((s.clientType)?s.clientType:"C");
        if(!(s.clientType) || s.clientType==='' || s.clientType==='C'){
            $(".quot-client").text("Client *");
            $("#client-div").show();
            $("#insured-clnt-div").show();
            $("#prs-div").hide();
            $("#insured-prs-div").hide();
            $("#client-id").val("");
            $("#insured-name").val("");
            $("#insured-code").val("");
            $("#insured-other-name").val("");
            populateInsuredLov();
        }
        else if(s.clientType==='P'){
            $(".quot-client").text("Prospect *");
            $("#client-div").hide();
            $("#insured-clnt-div").hide();
            $("#insured-prs-div").show();
            $("#prs-div").show();
            $('#insured-frm').select2('val', null);
            $("#client-id").val("");
            $("#insured-name").val("");
            $("#insured-code").val("");
            $("#insured-other-name").val("");
            $("#btn-add-prs").val("Edit");
            $("#btn-add-prs").show();
            populateInsuredLov();
        }
        $("#cur-id").val(s.curCode);
        $("#cur-name").val(s.currency);
        populateCurrencyLov();
        $("#client-id").val(s.tenId);
        $("#client-f-name").val(s.fname);
        $("#client-other-name").val(s.otherNames);
        populateClientLov();
        $("#pm-id").val(s.pmId);
        $("#pm-name").val(s.paymentMode);
        populatePaymentModes();
        $("#brn-id").val(s.obId);
        $("#brn-name").val(s.branch);
        $('#pol-comm-amt').text(UTILITIES.currencyFormat(s.commAmt));
        $('#pol-tl').text(UTILITIES.currencyFormat(s.trainingLevy));
        $('#pol-phcf').text(UTILITIES.currencyFormat(s.phcf));
        $('#pol-sd').text(UTILITIES.currencyFormat(s.stampDuty));
        $('#pol-whtx').text(UTILITIES.currencyFormat(s.whtx));
        $('#pol-extras').text(UTILITIES.currencyFormat(s.extras));
        if(s.expiryDate)
            $("#pol-exp-date").text(moment(s.expiryDate).format('DD/MM/YYYY'));

        populateUserBranches();
        $('#risk_tbl').DataTable().clear().destroy();
        $('#risk_tbl').DataTable({
            data: [s],

            destroy: true,

            "columns": [
                {
                    data: 'riskShtDesc',
                },
                {
                    data: 'riskDesc',
                },
                {
                    data: 'classification',
                },
                {
                    data: 'covType',
                },
                {
                    data: 'sumInsured',
                },
                {
                    data: 'premium',
                }
            ]

        })
        $('#prod_tbl').DataTable().clear().destroy();
        $('#prod_tbl').DataTable({
            data: [s],

            destroy: true,

            "columns": [
                {
                    data: 'contract',
                },
                {
                    data: 'insuranceCompany',
                },
                {
                    data: 'product',
                },
                {
                    data: 'sumInsured',
                },
                {
                    data: 'premium',
                },
                {
                    data: 'commAmt',
                }
            ]

        })
        var sections = s.quotRiskLimitsList;


        // Initialize or refresh the DataTable
        if ($.fn.DataTable.isDataTable('#section_tbl')) {
            // If DataTable already exists, destroy it and reinitialize with new data
            $('#section_tbl').DataTable().clear().destroy();
        }

        $('#section_tbl').DataTable({
            data: sections,  // Use the 'sections' array as the data source
            columns: [
                {
                    data: 'section.desc',
                    title: 'Premium Item'
                },
                {
                    data: 'amount', // Limit Amount
                    title: 'Limit Amount',
                    render: function(data, type, row) {
                        return data ? data.toFixed(2) : 'N/A'; // Format amount to 2 decimal places
                    }
                },
                {
                    data: 'rate', // Rate
                    title: 'Rate',
                    render: function(data, type, row) {
                        return data ? data.toFixed(2) : 'N/A'; // Format rate to 2 decimal places
                    }
                },
                {
                    // Premium calculation (amount * rate / divFactor)
                    data: null,
                    title: 'Premium',
                    render: function(data, type, row) {
                        var premium = (row.amount * row.rate) / row.divFactor;
                        return premium ? premium.toFixed(2) : '0.00'; // Calculate and format premium
                    }
                },
                {
                    data: 'divFactor', // Div Factor
                    title: 'Div Factor'
                },
                {
                    data: 'freeLimit', // Free Limit
                    title: 'Free Limit',
                    render: function(data, type, row) {
                        return data ? data.toFixed(2) : '0.00'; // Format free limit to 2 decimal places
                    }
                },

            ],
            destroy: true,  // Ensure the DataTable can be reinitialized with new data
            paging: true,
            searching: false,
            info: false
        });
    }

    function populateSourcesLov(srcGroupId){


        if($("#source-frm").filter("div").html() != undefined)
        {
            Select2Builder.initAjaxSelect2({
                containerId : "source-frm",
                sort : 'desc',
                change: function(e, a, v){
                    $("#source-id").val(e.added.srcId);
                },
                formatResult : function(a)
                {
                    return a.desc;
                },
                formatSelection : function(a)
                {
                    return a.desc;
                },
                initSelection: function (element, callback) {
                    var code  = $('#source-id').val();
                    var name = $("#source-name").val();
                    var data = {desc:name,srcId:code};
                    callback(data);
                },
                id: "srcId",
                width:"250px",
                params: {srcGroupId: srcGroupId},
                placeholder:"Select Source"

            });
        }


    }

    function populateCurrencyLov(){
        if($("#curr-frm").filter("div").html() != undefined)
        {
            Select2Builder.initAjaxSelect2({
                containerId : "curr-frm",
                sort : 'curName',
                change: function(e, a, v){
                    $("#cur-id").val(e.added.curCode);

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


    function populateClientLov(){
        if($("#client-frm").filter("div").html() != undefined)
        {
            Select2Builder.initAjaxSelect2({
                containerId : "client-frm",
                sort : 'fname',
                change: function(e, a, v){
                    $("#client-id").val(e.added.tenId);
                    $("#insured-name").val(e.added.fname);
                    $("#insured-code").val(e.added.tenId);
                    $("#insured-other-name").val(e.added.otherNames);
                    $("#insured-type").val("C");
                    populateInsuredLov();

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


    function populateSourceGroupLov(){
        if($("#sourcegroup-frm").filter("div").html() != undefined)
        {
            Select2Builder.initAjaxSelect2({
                containerId : "sourcegroup-frm",
                sort : 'desc',
                change: function(e, a, v){
                    $("#sourcegroup-id").val(e.added.srcGroupId);
                    populateSourcesLov(e.added.srcGroupId);

                },
                formatResult : function(a)
                {
                    return a.desc;
                },
                formatSelection : function(a)
                {
                    return a.desc;
                },
                initSelection: function (element, callback) {
                    var code  = $('#sourcegroup-id').val();
                    var name = $("#sourcegroup-name").val();
                    var data = {desc:name,srcGroupId:code};
                    callback(data);
                    populateSourcesLov($('#sourcegroup-id').val());
                },
                id: "srcGroupId",
                width:"250px",
                placeholder:"Select Source Group"

            });

        }

    }

    function populateInsuredLov(){
        if($("#insured-frm").filter("div").html() != undefined)
        {
            Select2Builder.initAjaxSelect2({
                containerId : "insured-frm",
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

    function populateUserBranches(){
        if($("#brn-frm").filter("div").html() != undefined)
        {
            Select2Builder.initAjaxSelect2({
                containerId : "brn-frm",
                sort : 'obName',
                change: function(e, a, v){
                    $("#brn-id").val(e.added.obId);
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
});