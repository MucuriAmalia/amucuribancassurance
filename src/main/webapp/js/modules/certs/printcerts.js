$(function(){

    $(document).ready(function() {

        printModule.certTypeLov();
        printModule.accLov();
        printModule.branchLov();

        $("#btn-search-certs").on('click', function(){
            printModule.certLov();
        });


    });
});


var printModule = (function(){

    function printPdf(url){
        var iframe = this._printIframe;
        if (!this._printIframe) {
            iframe = this._printIframe = document.createElement('iframe');
            document.body.appendChild(iframe);

            iframe.style.display = 'none';
            iframe.onload = function() {
                setTimeout(function() {
                    iframe.focus();
                    iframe.contentWindow.print();
                }, 1);
            };
        }

        iframe.src = url;
    }


    function createCertTable(cert,branch,acct,certStatus,polNo,riskId){
        var rows_selected = [];
        var table = $('#cert_tbl').DataTable({
            "processing": true,
            "serverSide": true,
            autoWidth: true,
            "searching": false,
            "ajax": {
                'url': "getprintcerts",
                'data':{
                    'certCode': cert,
                    'brnCode':  branch,
                    'acctCode':  acct,
                    'certStatus':  certStatus,
                    'polNo':  polNo,
                    'riskId':  riskId,
                },
            },
            lengthMenu: [ [20, 40,60], [20, 40,60] ],
            pageLength: 20,
            destroy: true,
            'columnDefs': [{
                'targets': 9,
                'searchable':false,
                'orderable':false,
                'className': 'dt-body-center',
                'render': function (data, type, full, meta){
                    //if(full.goodForPrint){
                    //    if(full.goodForPrint ==='Y'){
                    //        return '<input type="checkbox" checked>';
                    //    }
                    //    else
                    //        return '<input type="checkbox">';
                    //}else
                    return '<input type="checkbox">';
                }
            }],
            'rowCallback': function(row, data, dataIndex){
                var rowId = data.cqId;
                if($.inArray(rowId, rows_selected) !== -1){
                    $(row).find('input[type="checkbox"]').prop('checked', true);
                    $(row).addClass('selected');
                }
            },
            "columns": [
                { "data": "risk",
                    "render": function ( data, type, full, meta ) {
                        return full.risk.policy.polNo;
                    }
                },
                { "data": "risk",
                    "render": function ( data, type, full, meta ) {
                        return full.risk.insured.fname+" "+full.risk.insured.otherNames;
                    }
                },
                { "data": "risk",
                    "render": function ( data, type, full, meta ) {
                        return full.risk.riskShtDesc;
                    }
                },
                { "data": "policyCerts",
                    "render": function ( data, type, full, meta ) {
                        return moment(full.policyCerts.certWef).format('DD/MM/YYYY');
                    }
                },
                { "data": "policyCerts" ,
                    "render": function ( data, type, full, meta ) {
                        return moment(full.policyCerts.certWet).format('DD/MM/YYYY');
                    }
                },
                { "data": "policyCerts" ,
                    "render": function ( data, type, full, meta ) {
                        return moment(full.certWef).format('DD/MM/YYYY hh:mm:ss');
                    }
                },
                { "data": "status",
                    "render": function ( data, type, full, meta ) {

                        return full.status;
                    }
                },
                { "data": "allocBy",
                    "render": function ( data, type, full, meta ) {
                         if(full.allocBy)
                        return full.allocBy.username;
                        else return "";
                    }
                },
                { "data": "certNo",
                    "render": function ( data, type, full, meta ) {

                        return full.certNo;
                    }
                },
                { "data": "cqId" },
            ]
        } );

        // Handle click on checkbox
        $('#cert_tbl tbody').on('click', 'input[type="checkbox"]', function(e){
            var $row = $(this).closest('tr');

            // Get row data
            var data = table.row($row).data();

            // Get row ID
            var rowId = data.cqId;

            // Determine whether row ID is in the list of selected row IDs
            var index = $.inArray(rowId, rows_selected);

            // If checkbox is checked and row ID is not in list of selected row IDs
            if(this.checked && index === -1){
                rows_selected.push(rowId);

                // Otherwise, if checkbox is not checked and row ID is in list of selected row IDs
            } else if (!this.checked && index !== -1){
                rows_selected.splice(index, 1);
            }

            if(this.checked){
                $row.addClass('selected');
            } else {
                $row.removeClass('selected');
            }

            // Update state of "Select all" control
            //updateDataTableSelectAllCtrl(table);

            // Prevent click event from propagating to parent
            e.stopPropagation();
        });
        $('#cert_tbl').on('click', 'tbody td, thead th:first-child', function(e){
            $(this).parent().find('input[type="checkbox"]').trigger('click');
        });

        //table.on('draw', function(){
        //    // Update state of "Select all" control
        //    updateDataTableSelectAllCtrl(table);
        //});

        // $('#btn-print').click(function(){
        //     var ipuCode = ($(this).data("val"));
        //     jQuery.ajax ({
        //         url: SERVLET_CONTEXT + '/api/v1/integration/riskdigitalcert?ipuCode='+ipuCode,
        //         type: 'GET',
        //         dataType: "json",
        //         async: true,
        //         contentType: "application/json; charset=utf-8",
        //         success: function(result){
        //             $('body').pleaseWait('stop');
        //             var link = $('<a href="' + result.certurl + '" />');
        //             link.attr('target', '_blank');
        //             window.open(link.attr('href'));
        //
        //         },
        //         error: function(jqXHR, textStatus, errorThrown){
        //             $('body').pleaseWait('stop');
        //             var obj = $.parseJSON(jqXHR.responseText);
        //             $.showAlert({title: "Agency Portal", body: 'Error getting digital certificate.. Please contact us for more details'});
        //
        //         },
        //         beforeSend: function(){
        //             $('body').pleaseWait();
        //         }
        //
        //     });
        // });

        // $('#btn-print').on('click', function(e){
        //     var arr = [];
        //     $.each(rows_selected, function(index, rowId){
        //         arr.push(rowId);
        //     });
        //
        //     if(arr.length==0){
        //         bootbox.alert("No Certificates Selected to Print..");
        //         return;
        //     }
        //
        //     var $currForm = $('#print-form');
        //     var currValidator = $currForm.validate();
        //     if (!$currForm.valid()) {
        //         return;
        //     }
        //     var data = {};
        //     $currForm.serializeArray().map(function(x) {
        //         data[x.name] = x.value;
        //     });
        //     data.certCodes = arr;
        //     var url = "printCertificate";
        //     $.ajax({
        //         url : url,
        //         type : "POST",
        //         data : JSON.stringify(data),
        //         success : function(s) {
        //
        //             printPdf(requestContextPath+"/protected/certs/printcert");
        //             bootbox.confirm({
        //                 message: "Certificate Printed Successfully?",
        //                 buttons: {
        //                     confirm: {
        //                         label: 'Yes',
        //                         className: 'btn-success'
        //                     },
        //                     cancel: {
        //                         label: 'No',
        //                         className: 'btn-danger'
        //                     }
        //                 },
        //                 callback: function (result) {
        //                     if(result){
        //                         $.ajax({
        //                             type: 'GET',
        //                             url:  'markPrintedCerts',
        //                             dataType: 'json',
        //                             async: true,
        //                             success: function(result) {
        //                                 bootbox.alert("Receipt Printing operation complete");
        //                                 $('#cert_tbl').DataTable().ajax.reload();
        //
        //                             },
        //                             error: function(jqXHR, textStatus, errorThrown) {
        //                                 new PNotify({
        //                                     title: 'Error',
        //                                     text: jqXHR.responseText,
        //                                     type: 'error',
        //                                     styling: 'bootstrap3'
        //                                 });
        //                             }
        //                         });
        //
        //                     }
        //                 }
        //             });
        //
        //             arr = [];
        //         },
        //         error : function(jqXHR, textStatus, errorThrown) {
        //             new PNotify({
        //                 title: 'Error',
        //                 text: jqXHR.responseText,
        //                 type: 'error',
        //                 styling: 'bootstrap3'
        //             });
        //         },
        //         dataType : "json",
        //         contentType : "application/json"
        //     });
        //
        // });

        $('#btn-deallocate').on('click', function(e){

            var arr = [];
            $.each(rows_selected, function(index, rowId){
                arr.push(rowId);
            });

            if(arr.length==0){
                bootbox.alert("No Certificates Selected to Deallocate..");
                return;
            }

            var $currForm = $('#print-cert-form');
            var currValidator = $currForm.validate();
            if (!$currForm.valid()) {
                return;
            }
            var data = {};
            $currForm.serializeArray().map(function(x) {
                data[x.name] = x.value;
            });
            var url = "deallocateCerts";
            data.certs = arr;
            $.ajax({
                url : url,
                type : "POST",
                data : JSON.stringify(data),
                success : function(s) {
                    Swal.fire({
                        title: 'Success',
                        text: 'Certificate Allocation Successfully',
                        icon: 'success'
                    });
                    $('#cert_tbl').DataTable().ajax.reload();
                    arr = [];
                },
                error : function(jqXHR, textStatus, errorThrown) {
                    Swal.fire({
                title: 'Error',
                text: jqXHR.responseText,
                icon: 'error'
            });
                },
                dataType : "json",
                contentType : "application/json"
            });

        });

        $('#btn-allocate').on('click', function(e){

            var arr = [];
            $.each(rows_selected, function(index, rowId){
                arr.push(rowId);
            });

            if(arr.length==0){
                bootbox.alert("No Certificates Selected to Allocate..");
                return;
            }

            var $currForm = $('#print-cert-form');
            var currValidator = $currForm.validate();
            if (!$currForm.valid()) {
                return;
            }
            var data = {};
            $currForm.serializeArray().map(function(x) {
                data[x.name] = x.value;
            });
            var url = "allocateCerts";
            data.certs = arr;
            $.ajax({
                url : url,
                type : "POST",
                data : JSON.stringify(data),
                success : function(s) {
                    Swal.fire({
                        title: 'Success',
                        text: 'Certificate Allocation Successfully',
                        icon: 'success'
                    });
                    $('#cert_tbl').DataTable().ajax.reload();
                    arr = [];
                },
                error : function(jqXHR, textStatus, errorThrown) {
                     Swal.fire({
                title: 'Error',
                text: jqXHR.responseText,
                icon: 'error'
            });
                },
                dataType : "json",
                contentType : "application/json"
            });

        });
        rows_selected = table.column(0).data();
        table.draw(false);
        return table;
    }

    function updateDataTableSelectAllCtrl(table){
        var $table             = table.table().node();
        var $chkbox_all        = $('tbody input[type="checkbox"]', $table);
        var $chkbox_checked    = $('tbody input[type="checkbox"]:checked', $table);
        var chkbox_select_all  = $('thead input[name="select_all"]', $table).get(0);

        // If none of the checkboxes are checked
        if($chkbox_checked.length === 0){
            chkbox_select_all.checked = false;
            if('indeterminate' in chkbox_select_all){
                chkbox_select_all.indeterminate = false;
            }

            // If all of the checkboxes are checked
        } else if ($chkbox_checked.length === $chkbox_all.length){
            chkbox_select_all.checked = true;
            if('indeterminate' in chkbox_select_all){
                chkbox_select_all.indeterminate = false;
            }

            // If some of the checkboxes are checked
        } else {
            chkbox_select_all.checked = true;
            if('indeterminate' in chkbox_select_all){
                chkbox_select_all.indeterminate = true;
            }
        }
    }



    function createBranchLov(){
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
                    return a.obName
                },
                formatSelection : function(a)
                {
                    return a.obName
                },
                initSelection: function (element, callback) {

                },
                id: "obId",
                width:"200px",
                placeholder:"Select Branch"
            });

        }
    }

    function createUnderwriterLov(){
        if($("#underwriter-def").filter("div").html() != undefined)
        {
            Select2Builder.initAjaxSelect2({
                containerId : "underwriter-def",
                sort : 'name',
                change: function (e, a, v) {
                    $("#userCod").val(e.added.acctId);
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
                placeholder:"Select Underwriter"
            });
        }
    }

    //function createCertTypeLov(){
    //    if($("#cert-type").filter("div").html() != undefined)
    //    {
    //        Select2Builder.initAjaxSelect2({
    //            containerId : "cert-type",
    //            sort : 'certDesc',
    //            change: function (e, a, v) {
    //                $("#cert-type-pk").val(e.added.certId);
    //            },
    //            formatResult : function(a)
    //            {
    //                return a.certDesc
    //            },
    //            formatSelection : function(a)
    //            {
    //                return a.certDesc
    //            },
    //            initSelection: function (element, callback) {
    //
    //            },
    //            id: "certId",
    //            placeholder:"Select Cert Type"
    //        });
    //    }
    //}

    function createCertTypeLov(){
        if($("#cert-type").filter("div").html() != undefined)
        {
            Select2Builder.initAjaxSelect2({
                containerId : "cert-type",
                sort : 'subclasscertId',
                change: function (e, a, v) {
                    $("#cert-type-pk").val(e.added.subclasscertId);
                },
                formatResult : function(a)
                {
                    return a.subclassDesc
                },
                formatSelection : function(a)
                {
                    return a.subclassDesc
                },
                initSelection: function (element, callback) {

                },
                id: "subclasscertId",
                placeholder:"Select Sub Class"
            });
        }
    }

    function createCertificate(cert,branch,acct){
        if($("#cert-type-frm").filter("div").html() != undefined)
        {
            Select2Builder.initAjaxSelect2({
                containerId : "cert-type-frm",
                sort : 'noFrom',
                change: function (e, a, v) {
                    $("#cert-type-id").val(e.added.brnCertId);
                    $("#no-from").text(e.added.noFrom);
                    $("#no-to").text(e.added.noTo);
                    $("#cert-no").val(e.added.lastPrintedNo);
                    $("#avail-certs").text(e.added.countCerts);
                },
                formatResult : function(a)
                {
                    return "From "+a.noFrom+" To "+ a.noTo
                },
                formatSelection : function(a)
                {
                    return "From "+a.noFrom+" To "+ a.noTo
                },
                initSelection: function (element, callback) {

                },
                id: "brnCertId",
                placeholder:"Select Certificate",
                params:{certId:cert,brnCode:branch,acctCode:acct}
            });
        }
    }

    function createCertificateLov(){
        var acct ,branch,certcode,certStatus,polNo,riskId;
        if($("#userCod").val() != ''){
            acct = $("#userCod").val();
        }else{
            bootbox.alert('Select Underwriter to proceed');
            return;
        }
        if($("#brn-id").val() != ''){
            branch = $("#brn-id").val();
        }else{
            bootbox.alert('Select Branch to proceed');
            return;
        }
        if($("#cert-type-pk").val() != ''){
            certcode = $("#cert-type-pk").val();
        }else{
            bootbox.alert('Select Sub class to proceed');
            return;
        }

        if($("#cert-status").val() != ''){
            certStatus = $("#cert-status").val();
        }
        if($("#rev-search-number").val() != ''){
            riskId = $("#rev-search-number").val();
        }
        if($("#pol-search-number").val() != ''){
            polNo = $("#pol-search-number").val();
        }

        console.log("certcode="+certcode)
        createCertificate(certcode,branch,acct);
        createCertTable(certcode,branch,acct,certStatus,polNo,riskId)
    }

    return{
        certTypeLov:createCertTypeLov,
        accLov:createUnderwriterLov,
        branchLov:createBranchLov,
        certLov:createCertificateLov
    }

})();