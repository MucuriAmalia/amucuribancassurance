/**
 * Created by peter on 3/12/2017.
 */

$(function() {

    $(document).ready(function () {

        getUserTrans();
        populateClientLov();

        $("#btn-search-quotes").on('click', function(){
            var quote = $("#quote-search-number").val();
            var clientCode = $("#rev-search-name").val();
            var prscode = $("#prs-search-name").val();
            if(quote==='' && clientCode==='' && prscode===''){
                bootbox.alert("Provide At least one Search Parameter");
                return;
            }

            quoteEnquiry();

        });

    });
});



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


function getUserTrans(){
    var url = "quotTrans";
    var currTable = $('#quot_tbl').DataTable( {
        "processing": true,
        "serverSide": true,
        autoWidth: true,
        "deferRender": true,
        "ajax": {
            'url': url,
        },
        lengthMenu: [ [10,15,20], [10,15,20] ],
        pageLength: 10,
        destroy: true,
        "order": [[ 7, "desc" ]],
        "columns": [
            { "data": "quotNo" },
            { "data": "quotRevNo" },
            { "data": "wefDate",
                "render": function ( data, type, full, meta ) {
                    return moment(full.wefDate).format('DD/MM/YYYY');
                }
            },
            { "data": "wetDate" ,
                "render": function ( data, type, full, meta ) {
                    return moment(full.wetDate).format('DD/MM/YYYY');
                }
            },
            { "data": "client",
                "render": function ( data, type, full, meta ) {

                    if(full.client)
                        return full.client.fname+" "+full.client.otherNames;
                    else if(full.prospect)
                        return  full.prospect.fname+" "+full.prospect.otherNames;
                    else return "";
                }
            },
            { "data": "transCurrency",
                "render": function ( data, type, full, meta ) {

                    return full.transCurrency.curIsoCode;
                }
            },
            { "data": "preparedBy",
                "render": function ( data, type, full, meta ) {

                    return full.preparedBy.username;
                }
            },
            {
                "data": "quoteId",
                "render": function ( data, type, full, meta ) {
                    return '<form action="editquottrans" method="post"><input type="hidden" name="id" value='+full.quoteId+'><input type="submit"  class="btn btn-primary btn btn-primary btn-sm" value="Edit" ></form>';
                }

            },
        ]
    } );
    return currTable;
}


function quoteEnquiry(){
    var url = "quotenquiry";
    var currTable = $('#quot_enquiry').DataTable( {
        "processing": true,
        "serverSide": true,
        autoWidth: true,
        "order": [[ 8, "desc" ]],
        "deferRender": true,
        "ajax": {
            'url': url,
            'data':{
                'clientCode': $("#rev-search-name").val(),
                'quoteNo':  $("#quote-search-number").val(),
                'prsCode': $("#prs-search-name").val(),
            },
        },
        lengthMenu: [ [10,15,20], [10,15,20] ],
        pageLength: 10,
        destroy: true,
        "columns": [
            {
                "data": "quoteId",
                "render": function ( data, type, full, meta ) {
                    return '<form action="editquottrans" method="post"><input type="hidden" name="id" value='+full.quoteId+'><input type="submit"  class="btn btn-primary btn btn-primary btn-sm" value="View" ></form>';
                }

            },
            { "data": "quotNo" },
            { "data": "quotRevNo" },
            { "data": "wefDate",
                "render": function ( data, type, full, meta ) {
                    return moment(full.wefDate).format('DD/MM/YYYY');
                }
            },
            { "data": "wetDate" ,
                "render": function ( data, type, full, meta ) {
                    return moment(full.wetDate).format('DD/MM/YYYY');
                }
            },
            { "data": "client",
                "render": function ( data, type, full, meta ) {
                    return full.client;
                }
            },
            { "data": "product",
                "render": function ( data, type, full, meta ) {
                    return full.product;
                }
            },
            { "data": "curIsoCode",
                "render": function ( data, type, full, meta ) {

                    return full.curIsoCode;
                }
            },
            { "data": "quotStatus",
                "render": function ( data, type, full, meta ) {
                    if(full.quotStatus){
                        if(full.quotStatus==='C'){
                            return "Confirmed";
                        }
                        else  if(full.quotStatus==='R'){
                            return "Ready";
                        }
                        else  if(full.quotStatus==='A'){
                            return "Authorized";
                        }
                        else  if(full.quotStatus==='CL'){
                            return "Cancelled";
                        }
                        else  if(full.quotStatus==='D'){
                            return "Draft";
                        }
                        else return  full.quotStatus;
                    }
                    else{
                        return full.quotStatus;
                    }
                }
            },
            { "data": "preparusernameedBy",
                "render": function ( data, type, full, meta ) {

                    return full.username;
                }
            },
            {
                "data": "convertedReference",
                "render": function (data, type, full, meta) {

                    if (!full.convertedReference && (full.quotStatus==='A' || full.quotStatus==='C')) {
                        return '<button type="button" class="btn btn-primary btn btn-primary btn-sm" data-quotprod=' + encodeURI(JSON.stringify(full)) + ' onclick="reviseQuote(this);">Revise</button>';
                    }
                    else return "";
                }
            },
            {
                "data": "convertedReference",
                "render": function (data, type, full, meta) {

                    return '<button type="button" class="btn btn-primary btn btn-primary btn-sm" data-quotprod=' + encodeURI(JSON.stringify(full)) + ' onclick="reuseQuote(this);">Reuse</button>';

                }
            },
            {
                "data": "quoteId",
                "render": function (data, type, full, meta) {
                    return '<button type="button" class="btn btn-danger btn btn-danger btn-sm" data-quotprod=' + encodeURI(JSON.stringify(full)) + ' onclick="deleteQuot(this);">Delete</button>';

                }
            },
        ]
    } );
    return currTable;
}

function deleteQuot(button){
    var product = JSON.parse(decodeURI($(button).data("quotprod")));
    bootbox.confirm("Are you sure want to delete "+product['quotNo']+"?", function(result) {
        if(result){
            // // $('#myPleaseWait').modal({
//			backdrop: 'static',
//			keyboard: true
//		})
            $.ajax({
                type: 'GET',
                url: SERVLET_CONTEXT+ '/protected/quotes/deleteQuote/' + product['quoteId'],
                dataType: 'json',
                async: true,
                success: function(result) {
                    // $('#myPleaseWait').modal('hide');
                   Swal.fire({
                        title: 'Success',
                        text: 'Record created/updated Successfully',
                        icon: 'success'
                    });
                    $('#quot_enquiry').DataTable().ajax.reload();
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



function reuseQuote(button){
    var product = JSON.parse(decodeURI($(button).data("quotprod")));
    $.ajax({
        type: 'GET',
        url:  'reuseQuote/'+product['quoteId'],
        dataType: 'json',
        async: true,
        success: function(result) {
            // $('#myPleaseWait').modal('hide');
            window.location.href = SERVLET_CONTEXT+"/protected/quotes/editquote/"+result;
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

function reviseQuote(button){
    var product = JSON.parse(decodeURI($(button).data("quotprod")));
    $.ajax({
        type: 'GET',
        url:  'reviseQuote/'+product['quoteId'],
        dataType: 'json',
        async: true,
        success: function(result) {
            // $('#myPleaseWait').modal('hide');
            window.location.href = SERVLET_CONTEXT+"/protected/quotes/editquote/"+result;
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
