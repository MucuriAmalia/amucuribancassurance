/**
 * Created by HP on 6/16/2018.
 */
var MobMoneyApp = (function($){

    function editMpesaDtls(button){
        var mpesa = JSON.parse(decodeURI($(button).data("mpesa")));
        $("#mpesa-code-pk").val(mpesa["bidId"]);
        $("#mpesa-ref-no").val(mpesa["billNewRfNumber"]);
        $('#mpesaRefModal').modal({
            backdrop: 'static',
            keyboard: true
        })
    }

    function processMpesaTrans(button){
        var mpesa = JSON.parse(decodeURI($(button).data("mpesa")));
        bootbox.confirm("Are you sure want to process transaction ref no "+mpesa["transId"]+"?", function(result) {
            if(result){
                $.ajax({
                    type: 'GET',
                    url:  'processMpesaTrans/' + mpesa["transId"],
                    dataType: 'json',
                    async: true,
                    success: function(result) {
                        Swal.fire({
						title: 'Success',
						text: 'Process Successfully',
						icon: 'success'
					 });
                        $('#mpesa_trans_tbl').DataTable().ajax.reload();
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
        });
    }

    function saveMpesaDtls() {
        $('#saveMpesaRef').click(function(){
            var $classForm = $('#mpesa-form');
            var validator = $classForm.validate();
            if (!$classForm.valid()) {
                return;
            }
            var $btn = $(this).button('Saving');
            var data = {};
            $classForm.serializeArray().map(function(x){data[x.name] = x.value;});
            var url = "updateIntegrationDtls";
            var request = $.post(url, data );
            request.success(function(){
                Swal.fire({
                title: 'Success',
                text: 'Record created/updated Successfully',
                icon: 'success'
            });
                $('#mpesa_trans_tbl').DataTable().ajax.reload();
                validator.resetForm();
                $classForm.find("input[type=text],input[type=number],input[type=mobileNumber],input[type=emailFull],input[type=password],input[type=hidden], textarea,select").val("");
                $('#mpesaRefModal').modal('hide');
            });

            request.error(function(jqXHR, textStatus, errorThrown){
                Swal.fire({
                title: 'Error',
                text: jqXHR.responseText,
                icon: 'error'
            });
            });
            request.always(function(){
                $btn.button('reset');
            });
        });
    }

    function createMoneyTbl(selected){
        var url = "integrationDtls";
        var table = $('#mpesa_trans_tbl').DataTable( {
            "processing": true,
            "serverSide": true,
            "ajax": {
                'url': url,
                'data': {
                    'receipted': selected,
                },
            },
            lengthMenu: [ [10, 15], [10, 15] ],
            pageLength: 10,
            destroy: true,
            "columns": [
                { "data": "clientFname" },
                { "data": "middleName" },
                { "data": "lname" },
                { "data": "transId" },
                { "data": "phoneNumber" },
                { "data": "billRfNumber" },
                { "data": "billNewRfNumber" },
                {
                    "data": "bidId",
                    "render": function ( data, type, full, meta ) {
                        return UTILITIES.currencyFormat(full.transAmount);
                     }

                },
                {
                    "data": "receipted",
                    "render": function ( data, type, full, meta ) {
                        if(full.receipted ==="Y")
                            return "Yes";
                        else return "No";
                    }

                },
                {
                    "data": "bidId",
                    "render": function ( data, type, full, meta ) {
                        if(full.receipted ==="Y")
                        return '<button type="button" class="btn btn-info btn btn-info btn-sm" data-mpesa='+encodeURI(JSON.stringify(full)) + ' onclick="MobMoneyApp.editMpesaDtls(this);" disabled>Edit</button>';
                        else
                            return '<button type="button" class="btn btn-info btn btn-info btn-sm" data-mpesa='+encodeURI(JSON.stringify(full)) + ' onclick="MobMoneyApp.editMpesaDtls(this);">Edit</button>';
                    }

                },
                {
                    "data": "bidId",
                    "render": function ( data, type, full, meta ) {
                        if(full.receipted ==="Y")
                        return '<button type="button" class="btn btn-info btn btn-info btn-sm" data-mpesa='+encodeURI(JSON.stringify(full)) + ' onclick="MobMoneyApp.processMpesaTrans(this);" disabled>Process</button>';
                        else return '<button type="button" class="btn btn-info btn btn-info btn-sm" data-mpesa='+encodeURI(JSON.stringify(full)) + ' onclick="MobMoneyApp.processMpesaTrans(this);">Process</button>';
                    }

                },

            ]
        } );

        return table;
    }

    function init(){

        $('#receipt-type').on('change', function (e) {
            var valueSelected = this.value;
            console.log(valueSelected);
            createMoneyTbl(valueSelected);
            $('#mpesa_trans_tbl').DataTable().ajax.reload();
        });


        createMoneyTbl("N");
        saveMpesaDtls();
    }

    return {
        init:init,
        editMpesaDtls:editMpesaDtls,
        processMpesaTrans:processMpesaTrans
    }

})(jQuery);

jQuery(MobMoneyApp.init);