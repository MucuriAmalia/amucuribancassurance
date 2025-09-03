/**
 * Created by HP on 1/25/2018.
 */


var PrintCerts = function(){


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
    var printBatchCerts = function() {
        $('#btn-print-certs').on('click', function (e) {
            // // $('#myPleaseWait').modal({
//			backdrop: 'static',
//			keyboard: true
//		})
            var url = "printCertificate";
            $.ajax({
                type: 'GET',
                url:  'printCertificates',
                dataType: 'json',
                async: true,
                success: function (s) {
                    // $('#myPleaseWait').modal('hide');
                    printPdf(SERVLET_CONTEXT + "/protected/certs/printcert");
                    bootbox.confirm({
                        message: "Certificate Printed Successfully?",
                        buttons: {
                            confirm: {
                                label: 'Yes',
                                className: 'btn-success'
                            },
                            cancel: {
                                label: 'No',
                                className: 'btn-danger'
                            }
                        },
                        callback: function (result) {
                            if (result) {
                                $.ajax({
                                    type: 'GET',
                                    url: SERVLET_CONTEXT + '/protected/certs/markPrintedCerts',
                                    dataType: 'json',
                                    async: true,
                                    success: function (result) {
                                        bootbox.alert("Receipt Printing operation complete");
                                        $('#cert_tbl').DataTable().ajax.reload();

                                    },
                                    error: function (jqXHR, textStatus, errorThrown) {
                                        Swal.fire({
                title: 'Error',
                text: jqXHR.responseText,
                icon: 'error'
            });
                                    }
                                });

                            }
                        }
                    });

                    arr = [];
                },
                error: function (jqXHR, textStatus, errorThrown) {
                    // $('#myPleaseWait').modal('hide');
                    Swal.fire({
                title: 'Error',
                text: jqXHR.responseText,
                icon: 'error'
            });
                }
            });

        });
    }

    var  createCertTable=function(policycode) {
        var rows_selected = [];
        var table = $('#polCertsList').DataTable({
            "processing": true,
            "serverSide": true,
            autoWidth: true,
            "searching": false,
            "ajax": {
                'url': "getpolicyprintcerts",
                'data': {
                    'polId': policycode
                },
            },
            lengthMenu: [[20, 40, 60], [20, 40, 60]],
            pageLength: 20,
            destroy: true,
            'columnDefs': [{
                'targets': 7,
                'searchable': false,
                'orderable': false,
                'className': 'dt-body-center',
                'render': function (data, type, full, meta) {
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
            'rowCallback': function (row, data, dataIndex) {
                var rowId = data.cqId;
                if ($.inArray(rowId, rows_selected) !== -1) {
                    $(row).find('input[type="checkbox"]').prop('checked', true);
                    $(row).addClass('selected');
                }
            },
            "columns": [

                {
                    "data": "risk",
                    "render": function (data, type, full, meta) {
                        return full.riskId;
                    }
                },
                {
                    "data": "policyCerts",
                    "render": function (data, type, full, meta) {
                        return moment(full.policyWef).format('DD/MM/YYYY');
                    }
                },
                {
                    "data": "policyCerts",
                    "render": function (data, type, full, meta) {
                        return moment(full.policyWet).format('DD/MM/YYYY');
                    }
                },
                {
                    "data": "policyCerts",
                    "render": function (data, type, full, meta) {
                        return moment(full.certWef).format('DD/MM/YYYY hh:mm:ss');
                    }
                },
                {
                    "data": "status",
                    "render": function (data, type, full, meta) {
                        if (full.status === "P") {
                            return "Printed";
                        } else if (full.status === "N") {
                            return "Not Printed";
                        }
                    }
                },
                {
                    "data": "allocBy",
                    "render": function (data, type, full, meta) {
                        return full.username;
                    }
                },
                {
                    "data": "certNo",
                    "render": function (data, type, full, meta) {

                        return full.certNo;
                    }
                },
                {"data": "cqId"},
            ]
        });

        // Handle click on checkbox
        $('#polCertsList tbody').on('click', 'input[type="checkbox"]', function (e) {
            var $row = $(this).closest('tr');

            // Get row data
            var data = table.row($row).data();

            // Get row ID
            var rowId = data.cqId;

            // Determine whether row ID is in the list of selected row IDs
            var index = $.inArray(rowId, rows_selected);

            // If checkbox is checked and row ID is not in list of selected row IDs
            if (this.checked && index === -1) {
                rows_selected.push(rowId);

                // Otherwise, if checkbox is not checked and row ID is in list of selected row IDs
            } else if (!this.checked && index !== -1) {
                rows_selected.splice(index, 1);
            }

            if (this.checked) {
                $row.addClass('selected');
            } else {
                $row.removeClass('selected');
            }
            e.stopPropagation();
        });
        $('#polCertsList').on('click', 'tbody td, thead th:first-child', function (e) {
            $(this).parent().find('input[type="checkbox"]').trigger('click');
        });

        //table.on('draw', function(){
        //    // Update state of "Select all" control
        //    updateDataTableSelectAllCtrl(table);
        //});

        $('#btn-print').on('click', function (e) {
            var arr = [];
            $.each(rows_selected, function (index, rowId) {
                arr.push(rowId);
            });

            if (arr.length !== 1) {
                bootbox.alert("Select at least once certificate to print..");
                return;
            }
            //
            // var $currForm = $('#print-form');
            // var currValidator = $currForm.validate();
            // if (!$currForm.valid()) {
            //     return;
            // }
            // var data = {};
            // $currForm.serializeArray().map(function (x) {
            //     data[x.name] = x.value;
            // });
            // data.certCodes = arr[0];
            // var url = "printPolCertificate";
           var code = arr[0];
            // // $('#myPleaseWait').modal({
//			backdrop: 'static',
//			keyboard: true
//		})
            jQuery.ajax({
                url: SERVLET_CONTEXT + '/protected/uw/policies/printDigitalCertificate/'+ code,
                type: 'GET',
                dataType: "json",
                async: true,
                contentType: "application/json; charset=utf-8",
                success: function (result) {
                    // $('body').pleaseWait('stop');
                    // $('#myPleaseWait').modal('hide');
                    var link = $('<a href="' + result.certurl + '" />');
                    link.attr('target', '_blank');
                    window.open(link.attr('href'));

                },
                error: function (jqXHR, textStatus, errorThrown) {
                    // $('#myPleaseWait').modal('hide');
                    Swal.fire({
                title: 'Error',
                text: jqXHR.responseText,
                icon: 'error'
                   });

                },
                // beforeSend: function(){
                //     $('body').pleaseWait();
                // }

            });
        });

            // $.ajax({
            //     url : url,
            //     type : "POST",
            //     data : JSON.stringify(data),
            //     success : function(s) {
            //
            //         printPdf(SERVLET_CONTEXT +"/protected/certs/printcert");
            //         bootbox.confirm({
            //             message: "Certificate Printed Successfully?",
            //             buttons: {
            //                 confirm: {
            //                     label: 'Yes',
            //                     className: 'btn-success'
            //                 },
            //                 cancel: {
            //                     label: 'No',
            //                     className: 'btn-danger'
            //                 }
            //             },
            //             callback: function (result) {
            //                 if(result){
            //                     $.ajax({
            //                         type: 'GET',
            //                         url:  'markPrintedPolCerts',
            //                         dataType: 'json',
            //                         async: true,
            //                         success: function(result) {
            //                             bootbox.alert("Receipt Printing operation complete");
            //                             $('#polCertsList').DataTable().ajax.reload();
            //                             $('#cert_tbl').DataTable().ajax.reload();
            //
            //                         },
            //                         error: function(jqXHR, textStatus, errorThrown) {
            //                             new PNotify({
            //                                 title: 'Error',
            //                                 text: jqXHR.responseText,
            //                                 type: 'error',
            //                                 styling: 'bootstrap3'
            //                             });
            //                         }
            //                     });
            //
            //                 }
            //             }
            //         });
            //
            //         arr = [];
            //     },
            //     error : function(jqXHR, textStatus, errorThrown) {
            //         new PNotify({
            //             title: 'Error',
            //             text: jqXHR.responseText,
            //             type: 'error',
            //             styling: 'bootstrap3'
            //         });
            //     },
            //     dataType : "json",
            //     contentType : "application/json"
            // });

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

            //var $currForm = $('#print-cert-form');
            //var currValidator = $currForm.validate();
            //if (!$currForm.valid()) {
            //	return;
            //}
            var data = {};
            //$currForm.serializeArray().map(function(x) {
            //	data[x.name] = x.value;
            //});
            var url = "deallocatePolCerts";
            data.certs = arr;
            $.ajax({
                url : url,
                type : "POST",
                data : JSON.stringify(data),
                success : function(s) {
                    Swal.fire({
                        title: 'Success',
                        text: 'Certificate Deallocation Successfully',
                        icon: 'success'
                    })
                    $('#polCertsList').DataTable().ajax.reload();
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

            //var $currForm = $('#print-cert-form');
            //var currValidator = $currForm.validate();
            //if (!$currForm.valid()) {
            //	return;
            //}
            var data = {};
            //$currForm.serializeArray().map(function(x) {
            //	data[x.name] = x.value;
            //});
            var url = "allocatePolCerts";
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
                    })
                    $('#polCertsList').DataTable().ajax.reload();
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

    return{
        printCerts:printBatchCerts,
        createCertTable:createCertTable
    }

}();