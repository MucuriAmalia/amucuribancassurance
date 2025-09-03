<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<script type="text/javascript" src="<c:url value="/js/modules/utils/select2builder.js"/>"></script>
<script type="text/javascript" src="<c:url value="/libs/rivets/rivets.js"/>"></script>
<meta name="viewport" content="width=device-width, initial-scale=1">
<style>
    .switch {
        position: relative;
        display: inline-block;
        width: 60px;
        height: 34px;
    }

    .switch input {
        opacity: 0;
        width: 0;
        height: 0;
    }

    .slider {
        position: absolute;
        cursor: pointer;
        top: 0;
        left: 0;
        right: 0;
        bottom: 0;
        background-color: #ccc;
        -webkit-transition: .4s;
        transition: .4s;
    }

    .slider:before {
        position: absolute;
        content: "";
        height: 26px;
        width: 26px;
        left: 4px;
        bottom: 4px;
        background-color: white;
        -webkit-transition: .4s;
        transition: .4s;
    }

    input:checked + .slider {
        background-color: #2196F3;
    }

    input:focus + .slider {
        box-shadow: 0 0 1px #2196F3;
    }

    input:checked + .slider:before {
        -webkit-transform: translateX(26px);
        -ms-transform: translateX(26px);
        transform: translateX(26px);
    }

    /* Rounded sliders */
    .slider.round {
        border-radius: 34px;
    }

    .slider.round:before {
        border-radius: 50%;
    }

    input[type="checkbox"]{
        cursor: pointer;
    }
</style>
<div class="x_panel">
    <div class="card-box table-responsive">
        <div class="x_title">
            <h4>Data Loading Screen</h4>
        </div>
        <form id="upload-form" class="form-horizontal" enctype="multipart/form-data">
            <div class="col-xs-12 form-required">

                <div class="col-md-3 col-xs-12">
                    <div class="input-group col-xs-12">

                        <input name="file" type="file" id="avatar"  required>
                    </div>
                </div>
                <div>
                    <div class="col-md-3 col-xs-12">
                        <input type="submit" class="btn btn-success btn-sm float-left" id="btn-upload-policies" style="margin-right: 10px;" value="Upload Excel">
                    </div>
                    <div class="col-md-5 col-xs-12">
                        <div class="col-md-3 col-xs-12">

                            <input type="button" class="btn btn-info btn-sm "
                                   style="margin-right: 10px;" value="Load Policies"
                                   id="btn-load-pols">
                        </div>
                        <%--<div class="col-md-5 col-xs-12">--%>
                            <input type="button" class="btn btn-danger btn-sm float-right "
                                   style="margin-right: 10px;" value="Remove unloaded Policies"
                                   id="btn-remove-unloaded-pols">
                        <%--</div>--%>

                    </div>
                </div>

            </div>
            <div class="col-xs-12 form-required">
                <div class="col-md-3 col-xs-12">
                    <label>Show Loaded</label>
                    <label class="switch">
                        <input type="checkbox">
                        <span class="slider round"></span>
                    </label>

                </div>
                <div class="item form-group">

                </div>
            </div>
        </form>
        <form id="search-form" class="form-horizontal">

            <%--<div class="item form-group">--%>
                <%--<input type="button" class="btn btn-info float-right"--%>
                       <%--style="margin-right: 10px;" value="Load Policies"--%>
                       <%--id="btn-load-pols">--%>
            <%--</div>--%>


        </form>

        <table id="data_load_tbl" class="table" style="width:100%">
            <thead>
            <tr>
                <th>Binder</th>
                <th>Branch</th>
                <th>Eff Date</th>
                <th>Exp Date</th>
                <th>Premium</th>
                <th>Value</th>
                <th>Account Name</th>
                <th>Comments</th>
                <th>Policy No</th>
            </tr>
            </thead>
        </table>
    </div>
</div>
<script type="application/javascript">
    $(document).ready(function () {
        getPoliciesToLoad("N");
        loadPolicies();
        uploadPolicies();
        removeUnloadedpol();




    });

    document.addEventListener('DOMContentLoaded', function () {
        var checkbox = document.querySelector('input[type="checkbox"]');

        checkbox.addEventListener('change', function () {
            if (checkbox.checked) {
                // do this
                console.log('Checked');
                getPoliciesToLoad("Y");
            } else {
                // do that
                console.log('Not checked');
                getPoliciesToLoad("N");
            }
        });
    });

    function loadPolicies(){
        $("#btn-load-pols").click(function(){
            bootbox.confirm("Are you sure want to load the policy data? This is not reversible ", function(result) {
                if(result){
                    // $('#myPleaseWait').modal({
                        backdrop: 'static',
                        keyboard: true
                    });
                    var url = SERVLET_CONTEXT+"/protected/dataloading/loadPolicyRecord";
                    $.ajax({
                        type: 'GET',
                        url:  url,
                        dataType: 'json',
                        async: true,
                        success: function(result) {
                            // $('#myPleaseWait').modal('hide');
                            Swal.fire({
                                title: 'Success',
                                text: 'Transaction completed. Check the highlighted records which have not been loaded',
                                icon: 'success'
                            });
                            $('#data_load_tbl').DataTable().ajax.reload();
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
        });
    }


    function removeUnloadedpol(){
        $("#btn-remove-unloaded-pols").click(function(){
            bootbox.confirm("Are you sure want to remove unloaded policy data that you imported? This is not reversible ", function(result) {
                if(result){
                    // $('#myPleaseWait').modal({
                        backdrop: 'static',
                        keyboard: true
                    });
                    var url = SERVLET_CONTEXT+"/protected/dataloading/removeUnloadedpol";
                    $.ajax({
                        type: 'GET',
                        url:  url,
                        dataType: 'json',
                        async: true,
                        success: function(result) {
                            // $('#myPleaseWait').modal('hide');
                            Swal.fire({
                                title: 'Success',
                                text: 'Unloaded Policies removed successfully.',
                                icon: 'success'
                            });
                            $('#data_load_tbl').DataTable().ajax.reload();
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
        });
    }


    function getPoliciesToLoad(loaded){
        var url = SERVLET_CONTEXT + '/protected/dataloading/policyToload';
        if (loaded==="Y"){
            url = SERVLET_CONTEXT + '/protected/dataloading/loadedPolicies';
        }

        var currTable = $('#data_load_tbl').DataTable(UTILITIES.extendsOpts({
            "ajax": {
                'url': url
            },
            "columns": [
                { "data": "binder" },
                { "data": "branch" },
                { "data": "loadId",
                    "render": function ( data, type, full, meta ) {
                        if(full.effdate)
                            return moment(full.effdate).format('DD/MM/YYYY');
                        else return "";
                    }
                },
                { "data": "loadId",
                    "render": function ( data, type, full, meta ) {
                        if(full.expdate)
                            return moment(full.expdate).format('DD/MM/YYYY');
                        else return "";
                    }
                },
                { "data": "loadId",
                    "render": function ( data, type, full, meta ) {
                        if(full.premium)
                            return UTILITIES.currencyFormat(full.premium);
                        else return "";
                    }
                },
                { "data": "loadId",
                    "render": function ( data, type, full, meta ) {
                        if(full.benefitvalue)
                            return UTILITIES.currencyFormat(full.benefitvalue);
                        else return "";
                    }
                },
                { "data": "accountName" },
                { "data": "comments"  ,
                    "render": function ( data, type, full, meta ) {
                        if(full.polid)
                            return "Loaded Successfully";
                        else return full.comments;
                    }},
                { "data": "polid" ,
                    "render": function ( data, type, full, meta ) {
                        if(full.polid)
                       //     return '<button type="button" class="btn btn-link"  onclick="UWScreen.viewPremLimits("+result[res].pk+");">'+full.polid.polNo+'</button>';
                        return '<form action="edituwtrans" method="post"><input type="hidden" name="id" value=' + full.polid.policyId + '><input type="submit"  class="btn btn-link" value='+full.polid.polNo+' ></form>';
                        else return "";
                    }
                },
            ],
            "rowCallback": function( row, data, index ) {
                if (!data.polid & !data.comments=="" )
                {
                    $('td', row).css('background-color', '#ffe6e6');
                }
            }
        } ));
        return currTable;


    };

    function uploadPolicies(){
        var $form = $("#upload-form");
        var validator = $form.validate();
        $('form#upload-form')
            .submit( function( e ) {
                e.preventDefault();
                if (!$form.valid()) {
                    return;
                }
                var data = new FormData( this );
                data.append( 'file', $( '#avatar' )[0].files[0] );
                $.ajax( {
                    url: 'importPolicies',
                    type: 'POST',
                    data: data,
                    processData: false,
                    contentType: false,
                    success: function (s ) {
                        Swal.fire({
                            title: 'Success',
                            text: 'File Uploaded Successfully',
                            icon: 'success'
                        });
                        $('#data_load_tbl').DataTable().ajax.reload();
                    },
                    error: function(jqXHR, textStatus, errorThrown){
                        Swal.fire({
                title: 'Error',
                text: jqXHR.responseText,
                icon: 'error'
            });
                    }
                });
            });
    }
</script>