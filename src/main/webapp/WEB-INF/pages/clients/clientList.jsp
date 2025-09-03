<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<script type="text/javascript" src="<c:url value="/js/modules/clients/clients.js"/>"></script>
<div class="x_panel">
    <div class="x_title">
        <h2><i class="fa fa-bars"></i> Clients List</h2>
        <div class="clearfix"></div>
    </div>

    <form id="search-form" class="form-horizontal">
        <div class="item form-group">
            <div class="col-md-6 col-xs-12">
                <label for="pass-search-type" class="col-md-5 label-align">Client Type
                </label>

                <div class="col-md-7 col-xs-12">
                    <select id="pass-search-type"  class="form-control">
                        <option value="" disabled selected>Select Client Type</option>
                        <option value="IND">Individual</option>
                        <option value="CORP">Corporate</option>
                    </select>
                </div>
            </div>
        </div>
        <div class="item form-group" id="ind">
            <div class="col-md-6 col-xs-12">
                <label for="pass-search-name" class="col-md-5 label-align">ID Number
                </label>

                <div class="col-md-7 col-xs-12">
                    <input type='text' class="form-control float-right"
                           id="pass-search-name"/>
                </div>
            </div>
        </div>
        <div class="item form-group" id="corp">
            <div class="col-md-6 col-xs-12">
                <label for="corp-reg-no" class="col-md-5 label-align">Registration Number
                </label>

                <div class="col-md-7 col-xs-12">
                    <input type='text' class="form-control float-right"
                           id="corp-reg-no"/>
                </div>
            </div>
        </div>
        <div class="item form-group">
            <input type="button" class="btn btn-primary float-right"
                   style="margin-right: 10px;" value="Search"
                   id="btn-search-client">
        </div>


    </form>
</div>

<div class="x_panel">
    <a href="<c:url value='/protected/clients/setups/clientsform'/> " class="btn btn-primary float-right">New</a>
    <div class="table-responsive">
        <table id="tenTbl" class="table table-striped" style="width: 100%">
            <thead>
            <tr class="headings">
                <th>Client ID</th>
                <th>Name</th>
                <th>Id/Passport/Reg No</th>
                <th>Email</th>
                <th>Phone</th>
                <th>Client Type</th>
                <th>Status</th>
                <th>CIF No</th>
                <th>Created On</th>
                <th>Created By</th>
                <th>Approved?</th>
                <th width="5%"></th>
                <th width="5%"></th>
            </tr>
            </thead>
        </table>
    </div>
</div>

<script>
    $(document).ready(function () {
        $("#ind").hide();
        $("#corp").hide();
        $('#pass-search-type').on('change', function () {
            if ($(this).val() === "CORP") {
                $("#corp").show();
                $("#ind").hide();
            } else if ($(this).val() === "IND") {
                $("#ind").show();
                $("#corp").hide();
            }
        });

        $("#btn-search-client").on('click', function () {
            var searchType = $("#pass-search-type").val();
            var searchValue;
            if (searchType === 'IND') {
                if (!$("#pass-search-name").val()) {
                    bootbox.alert('Please Enter Client ID Number');
                    return;
                } else {
                    searchValue = $("#pass-search-name").val();
                }
            } else if (searchType === 'CORP') {
                if (!$("#corp-reg-no").val()) {
                    bootbox.alert('Please Enter Company Registration No');
                } else {
                    searchValue = $("#corp-reg-no").val();
                }
            }

            createTenantsList(searchType, searchValue);
        });
    })


    function createTenantsList(searchType, searchValue) {
        var url = "tenants";
        return $('#tenTbl').DataTable({
            "processing": true,
            "serverSide": true,
            autoWidth: true,
            "searching": false,
            "ajax": {
                'url': url,
                'data': {searchType:searchType, searchValue:searchValue},
                'error': function(jqXHR, textStatus, errorThrown){
                    Swal.fire({
                        title: 'Error',
                        text: jqXHR.responseText,
                        icon: 'error'
                    })
                }
            },
            lengthMenu: [[10, 15], [10, 15]],
            pageLength: 10,
            destroy: true,
            "columns": [
                {"data": "tenantNumber"},
                {"data": "clientName"},
                {"data": "idNo"},
                {"data": "emailAddress"},
                {"data": "phoneNo"},
                {"data": "clientType"},
                {
                    "data": "status",
                    "render": function (data, type, full, meta) {
                        if (!full.status || full.status === "T") {
                            return "Terminated";
                        } else if (full.status === "A")
                            return "Active";
                    }
                },
                {"data": "clientCIF"},
                {
                    "data": "dateCreated",
                    "render": function (data, type, full, meta) {
                        if (full.dateCreated)
                            return moment(full.dateCreated).format('DD/MM/YYYY');
                        else return "";
                    }
                },
                {
                    "data": "tenId",
                    "render": function (data, type, full, meta) {
                        if (full.username)
                            return full.username;
                        else return "";
                    }

                },
                {
                    "data": "tenId",
                    "render": function (data, type, full, meta) {
                        if (full.authStatus)
                            return full.authStatus;
                        else return "";
                    }

                },
                {
                    "data": "tenId",
                    "render": function (data, type, full, meta) {
                        var id = (full.hashCode) ? full.hashCode : full.tenId;
                        return "<a href=" + SERVLET_CONTEXT + "/protected/clients/setups/editClients/" + id + " class='btn btn-primary btn btn-primary btn-sm'>View</a>";
                    }

                },
                {
                    "data": "tenId",
                    "render": function (data, type, full, meta) {
                        return '<button type="button" class="btn btn-danger btn btn-danger btn-sm" data-clients=' + encodeURI(JSON.stringify(full)) + ' value="Delete" onclick="deleteClient(this);"><i class="fa fa-trash-o"></button>';
                    }

                },
            ]
        });
    }


    function deleteClient(button) {
        var clients = JSON.parse(decodeURI($(button).data("clients")));
        bootbox.confirm("Are you sure want to delete the client?", function (result) {
            if (result) {
                // // $('#myPleaseWait').modal({
//			backdrop: 'static',
//			keyboard: true
//		})
                $.ajax({
                    type: 'GET',
                    url: 'deleteClient/' + clients['tenId'],
                    dataType: 'json',
                    async: true,
                    success: function (result) {
                        // $('#myPleaseWait').modal('hide');
                        Swal.fire({
                            title: 'Success',
                            text: 'Client Deleted Successfully',
                            icon: 'success'
                        });
                        $('#tenTbl').DataTable().ajax.reload();
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
            }

        });
    }
</script>