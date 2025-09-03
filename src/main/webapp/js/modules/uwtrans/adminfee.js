/**
 * Created by peter on 6/6/2017.
 */
var UTILITIES = UTILITIES || {};
$(function() {

    $(document).ready(function () {

        getAdminFeeTrans("adminfeeTrans");
        $('input[type=radio][name=optradio]').change(function() {
            if(this.value==="U"){
                getAdminFeeTrans("adminfeeTrans");
            }
            else if(this.value==="A"){
                getAdminFeeTrans("authadminfeeTrans");
            }
        });

    });
});


function getAdminFeeTrans(url){
    var currTable = $('#admin_fee_tbl').DataTable( {
        "processing": true,
        "serverSide": true,
        autoWidth: true,
        "deferRender": true,
        "order": [[ 11, "desc" ]],
        "ajax": {
            'url': url,
        },
        lengthMenu: [ [10,15,20], [10,15,20] ],
        pageLength: 10,
        destroy: true,
        "columns": [
            { "data": "refNo" },
            { "data": "clientDef",
                "render": function ( data, type, full, meta ) {

                    return full.clientDef.fname+" "+full.clientDef.otherNames;
                }
            },
            { "data": "currencies",
                "render": function ( data, type, full, meta ) {

                    return full.currencies.curIsoCode;
                }
            },
            {
                "data" : "adminFeeAmt",
                "render" : function(data, type, full, meta) {
                    return UTILITIES.currencyFormat(full.adminFeeAmt);
                }
            },
            {
                "data" : "vatAmt",
                "render" : function(data, type, full, meta) {
                    return UTILITIES.currencyFormat(full.vatAmt);
                }
            },
            {
                "data" : "exciseDuty",
                "render" : function(data, type, full, meta) {
                    return UTILITIES.currencyFormat(full.exciseDuty);
                }
            },
            {
                "data" : "adminNetAmt",
                "render" : function(data, type, full, meta) {
                    return UTILITIES.currencyFormat(full.adminNetAmt);
                }
            },
            { "data": "preparedBy",
                "render": function ( data, type, full, meta ) {
                    return full.preparedBy.username;
                }
            },
            { "data": "preparedDate" ,
                "render": function ( data, type, full, meta ) {
                    return moment(full.preparedDate).format('DD/MM/YYYY');
                }
            },
            { "data": "authorised" },
            { "data": "paid" },
            {
                "data": "adminFeeId",
                "render": function ( data, type, full, meta ) {
                    if(canAccess) {
                        return '<form action="editadminfee" method="post"><input type="hidden" name="id" value=' + full.adminFeeId + '><input type="submit"  class="btn btn-success btn btn-info btn-sm" value="Edit" ></form>';
                    }
                    else return "";
                }

            },
        ]
    } );
    return currTable;
}