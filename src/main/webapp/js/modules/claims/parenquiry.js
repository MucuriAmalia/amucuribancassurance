/**
 * Created by peter on 3/10/2017.
 */
$(function() {

    $(document).ready(function () {
        parenquiry();
        smartCardsEnquiry();
    })
});

function parenquiry(){
    var url = "partrans";
    var currTable = $('#med_clm_enquiry_tbl').DataTable( {
        "processing": true,
        "serverSide": true,
        autoWidth: true,
        "order": [[ 1, "desc" ]],
        searching: false,
        "deferRender": true,
        "ajax": {
            'url': url,
        },
        lengthMenu: [ [10,15,20], [10,15,20] ],
        pageLength: 10,
        destroy: true,
        "columns": [
            { "data": "parNo" },
            { "data": "parRevisionNo" },
            { "data": "eventDate",
                "render": function ( data, type, full, meta ) {
                    return moment(full.eventDate).format('DD/MM/YYYY');
                }
            },
            { "data": "clientDef",
                "render": function ( data, type, full, meta ) {

                    return full.clientDef.fname+" "+full.clientDef.otherNames;
                }
            },
            { "data": "notDate" ,
                "render": function ( data, type, full, meta ) {
                    return moment(full.notDate).format('DD/MM/YYYY');
                }
            },
            { "data": "parStatus" },
            { "data": "transUser",
                "render": function ( data, type, full, meta ) {

                    return full.transUser.username;
                }
            },
            { "data": "transType",
                "render": function ( data, type, full, meta ) {
                       if(full.transType){
                           if(full.transType ==="C") return "Claims";
                           else  if(full.transType ==="P") return "Pre-Auth Trans";
                       }
                }
            },
            {
                "data": "parId",
                "render": function ( data, type, full, meta ) {
                    return '<form action="parclaimtrans" method="post"><input type="hidden" name="id" value='+full.parId+'><input type="submit"  class="btn btn-success btn btn-info btn-sm" value="View" ></form>';
                }

            },
        ]
    } );
    return currTable;
}


function smartCardsEnquiry(){
    var url = "smartclaims";
    var currTable = $('#smart_clm_enquiry_tbl').DataTable( {
        "processing": true,
        "serverSide": true,
        autoWidth: true,
        searching: false,
        "deferRender": true,
        "ajax": {
            'url': url,
        },
        lengthMenu: [ [10,15,20], [10,15,20] ],
        pageLength: 10,
        destroy: true,
        "columns": [
            { "data": "parNo" },
            { "data": "parRevisionNo" },
            { "data": "eventDate",
                "render": function ( data, type, full, meta ) {
                    return moment(full.eventDate).format('DD/MM/YYYY');
                }
            },
            { "data": "clientDef",
                "render": function ( data, type, full, meta ) {

                    return full.clientDef.fname+" "+full.clientDef.otherNames;
                }
            },
            { "data": "transUser",
                "render": function ( data, type, full, meta ) {

                    return full.transUser.username;
                }
            },
            { "data": "parStatus" },
            {
                "data": "parId",
                "render": function ( data, type, full, meta ) {
                    return '<form action="parclaimtrans" method="post"><input type="hidden" name="id" value='+full.parId+'><input type="submit"  class="btn btn-success btn btn-info btn-sm" value="View" ></form>';
                }

            },
        ]
    } );
    return currTable;
}