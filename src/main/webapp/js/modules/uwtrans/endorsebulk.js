$(function () {

    $(document).ready(function () {

        $.ajaxSetup({
            cache: false
        });


        $('#rev-type').val("");

        loadrevisionModal();

        confirmSelectedRevision();

        selectEndorseType();

        getPolicyWet();

        populateClientLov();

        createAccountsForSel();

        $(".datepicker-input").each(function () {
            $(this).datetimepicker({
                format: 'DD/MM/YYYY'
            });

        });

        if ($("#rev-type").val() === "EX") {

            $("#eff-date-to").show();
            $("#rein-amt").hide();
        }
        if ($(this).val() === "CN") {
            $(".cancel-Type").show();
            $("#eff-date-to").hide();
            $("#rein-amt").hide();
        }
        if ($(this).val() === "RS") {
            $("#eff-date-to").hide();
            $(".cancel-Type").hide();
            $("#rein-amt").show();
        } else {
            $("#eff-date-to").hide();
            $(".cancel-Type").hide();
            $("#rein-amt").hide();
        }

        $("#rev-type").on('change', function () {
            if ($(this).val() === "CN") {
                $(".cancel-Type").show();
                $("#rein-amt").hide();
            }
            if ($(this).val() === "RS") {
                $("#rein-amt").show();
            } else {
                $(".cancel-Type").hide();
                $("#rein-amt").hide();
            }
        })

    })
});

function checkExistingTrans() {
    $.ajax({
        type: 'GET',
        url: 'countUnauthPolicies',
        dataType: 'json',
        data: {"policyNumber": $("#pol-number").val()},
        async: true,
        success: function (result) {
            console.log("One");
            if (result > 0) {
                createUnauthPolicies();
                $("#existing-trans").show();
            } else {
                $("#existing-trans").hide();
            }
        },
        error: function (jqXHR, textStatus, errorThrown) {

        }
    });
}


function getPolicyWet() {
    $('#eff-date-from').on('dp.change', function (ev) {
        var curDate = ev.date;
        var dt = moment(curDate).format('DD/MM/YYYY');
        $.ajax({
            type: 'GET',
            url: 'getWetDate',
            dataType: 'json',
            data: {"wefDate": dt},
            async: true,
            success: function (result) {
                $("#eff-to-date").val(moment(result).format('DD/MM/YYYY'));


            },
            error: function (jqXHR, textStatus, errorThrown) {

            }
        });
    });
}

function selectEndorseType() {
    $("#rev-type").on('change', function () {
        console.log($("#rev-type").val());
        if ($("#rev-type").val() === "EX") {
            $("#rein-amt").hide();
            $("#eff-date-to").show();
            $("#eff-to-date").val("");
        }
        if ($("#rev-type").val() === "RS") {
            $("#rein-amt").show();
            $("#eff-date-to").hide();
        }
        if ($(this).val() === "CN") {
            $(".cancel-Type").show();
            $("#rein-amt").hide();
        } else {
            $("#rein-amt").hide();
            $("#eff-date-to").hide();
        }
    })
}

function loadrevisionModal() {
    $("#btn-show-search-pol").on('click', function () {

        // Set all search parameters to empty

        $("#rev-search-name").val("");
        $("#pol-search-number").val("");
        $("#agent-search-number").val("");
        $("#rev-search-number").val("");
        $("#dr-search-number").val("");

        // createRevisionTrans();

        $("#btn-search-policies").show();
        $("#btn-search-med-policies").hide();
        $("#btn-search-life-policies").hide();
        $('#revPoliciesModal').modal({
            backdrop: 'static',
            keyboard: true
        })
    });

    $("#btn-show-search-med").on('click', function () {
        createMedRevisionTrans();
        $("#btn-search-policies").hide();
        $("#btn-search-life-policies").hide();
        $("#btn-search-med-policies").show();
        $('#revPoliciesModal').modal({
            backdrop: 'static',
            keyboard: true
        })
    });
    $("#btn-show-search-life").on('click', function () {
        createLifeRevisionTrans();
        $('.drNo').hide();
        $('.plateNo').hide();
        $("#btn-search-policies").hide();
        $("#btn-search-med-policies").hide();
        $("#btn-search-life-policies").show();
        $('#revPoliciesModal').modal({
            backdrop: 'static',
            keyboard: true
        })
    });

    $("#btn-search-policies").on('click', function () {
        var clientCode = $("#rev-search-name").val();
        var policyNo = $("#pol-search-number").val();
        var agent = $("#agent-search-number").val();
        var riskCode = $("#rev-search-number").val();
        var refNo = $("#dr-search-number").val();
        if (clientCode === "" && policyNo === "" && agent === "" && riskCode === "" && refNo === "") {
            bootbox.alert("Provide At least one Search Parameter");
            return;
        }
        console.log("clientCode === " + clientCode + "policyNo === " + policyNo + "agent === " + agent + "riskCode === " + riskCode + "refNo === " + refNo);
        createRevisionTrans();

    });

    $("#btn-search-med-policies").on('click', function () {
        createMedRevisionTrans();

    });


    $("#btn-search-life-policies").on('click', function () {
        createLifeRevisionTrans();
    })


}

function confirmSelectedRevision() {
    $("#selectauthtrans").on('click', function () {
        if ($("#pol-number").val() != '') {
            $('#revPoliciesModal').modal('hide');
        } else {
            bootbox.alert('Select a Policy to continue');
        }
        checkExistingTrans();
    });
}


function createMedRevisionTrans() {
    var url = "revisionMedPolicies";
    var currTable = $('#revtranstbl').DataTable({
        "processing": true,
        "serverSide": true,
        "ajax": {
            'url': url,
            'data': {
                'clientName': $("#rev-search-name").val(),
                'policyNo': $("#pol-search-number").val(),
                'agent': $("#agent-search-number").val(),
                'endorseNumber': $("#rev-search-number").val(),
                'refno': $("#dr-search-number").val(),
            },
        },
        autoWidth: true,
        lengthMenu: [[10], [10]],
        pageLength: 10,
        destroy: true,
        searching: false,
        "columns": [
            {"data": "policyNo"},
            {"data": "clientPolNo"},
            {"data": "polRevNo"},
            {
                "data": "policyId",
                "render": function (data, type, full, meta) {
                    console.log(full);
                    return full.clientName
                }
            },
            {
                "data": "policyId",
                "render": function (data, type, full, meta) {
                    return "";
                    return full.agentName;
                }
            },
            {"data": "polUwYr"},
            {
                "data": "policyId",
                "render": function (data, type, full, meta) {
                    return full.binderName;
                }
            },

        ]
    });


    $('#revtranstbl tbody').on('click', 'tr', function () {

        $(this).addClass('table-primary').siblings().removeClass('table-primary');

        var d = currTable.row(this).data();
        if (d) {
            $("#rev-pol-id").val(d.policyId);
            $("#pol-number").val(d.policyNo);
            $.ajax({
                type: 'GET',
                url: 'countUnauthPolicies',
                dataType: 'json',
                data: {"policyNumber": $("#pol-number").val()},
                async: true,
                success: function (result) {
                    console.log("Two");
                    if (result > 0) {
                        createUnauthPolicies();
                        $("#existing-trans").show();
                    } else {
                        $("#existing-trans").hide();
                    }
                },
                error: function (jqXHR, textStatus, errorThrown) {

                }
            });


        }


    });

    return currTable;
}


function createLifeRevisionTrans() {
    var url = "revisionLifePolicies";
    var currTable = $('#revtranstbl').DataTable({
        "processing": true,
        "serverSide": true,
        "ajax": {
            'url': url,
            'data': {
                'clientName': $("#rev-search-name").val(),
                'policyNo': $("#pol-search-number").val(),
                'agent': $("#agent-search-name").val()
            },
        },
        autoWidth: true,
        lengthMenu: [[10], [10]],
        pageLength: 10,
        destroy: true,
        searching: false,
        "columns": [
            {"data": "polNo"},
            {"data": "clientPolNo"},
            {"data": "polRevNo"},
            {
                "data": "policyId",
                "render": function (data, type, full, meta) {
                    return full.client.fname + " " + full.client.otherNames;
                }
            },
            {
                "data": "policyId",
                "render": function (data, type, full, meta) {
                    return full.agent.name;
                }
            },
            {"data": "uwYear"},
            {
                "data": "policyId",
                "render": function (data, type, full, meta) {
                    return full.binder.binName;
                }
            },

        ]
    });


    $('#revtranstbl tbody').on('click', 'tr', function () {

        $(this).addClass('table-primary').siblings().removeClass('table-primary');

        var d = currTable.row(this).data();
        if (d) {
            $("#rev-pol-id").val(d.policyId);
            $("#pol-number").val(d.polNo);
            $.ajax({
                type: 'GET',
                url: 'countUnauthPolicies',
                dataType: 'json',
                data: {"policyNumber": $("#pol-number").val()},
                async: true,
                success: function (result) {
                    console.log("Three=" + $("#pol-number").val());
                    if (result > 0) {
                        createUnauthPolicies();
                        $("#existing-trans").show();
                    } else {
                        $("#existing-trans").hide();
                    }
                },
                error: function (jqXHR, textStatus, errorThrown) {

                }
            });


        }


    });

    return currTable;
}

function createRevisionTrans() {
    var url = "bulkPoliciesRevision";
    var currTable = $('#revtranstbl').DataTable({
        "processing": true,
        "serverSide": true,
        "ajax": {
            'url': url,
            'data': {
                'client': $("#client-id").val(),
                'policyNo': $("#pol-search-number").val(),
                'agent': $("#agent-search-number").val(),
                'endorseNumber': $("#rev-search-number").val(),
                'refno': $("#dr-search-number").val(),
                'endorsetype': $("#rev-type").val(),
            },
        },
        autoWidth: true,
        lengthMenu: [[10], [10]],
        pageLength: 10,
        destroy: true,
        searching: false,
        "columns": [
            {"data": "policyNo"},
            {"data": "clientPolNo"},
            {"data": "polRevNo"},
            {"data": "clientName"},
            {"data": "agentName"},
            {"data": "polUwYr"},
            {"data": "binderName"}
        ]
    });

    $('#revtranstbl tbody').on('click', 'tr', function () {

        $(this).addClass('table-primary').siblings().removeClass('table-primary');

        var d = currTable.row(this).data();
        if (d) {
            $("#rev-pol-id").val(d.policyId);
            $("#pol-number").val(d.policyNo);
            $.ajax({
                type: 'GET',
                url: 'countUnauthPolicies',
                dataType: 'json',
                data: {"policyNumber": $("#pol-number").val()},
                async: true,
                success: function (result) {
                    console.log("Four");
                    if (result > 0) {
                        createUnauthPolicies();
                        $("#existing-trans").show();
                    } else {
                        $("#existing-trans").hide();
                    }
                },
                error: function (jqXHR, textStatus, errorThrown) {

                }
            });


        }


    });

    return currTable;
}

function createUnauthPolicies() {
    var url = "unauthPolicies";
    var currTable = $('#poltrans').DataTable({
        "processing": true,
        "serverSide": true,
        "ajax": {
            'url': url,
            'data': {
                'policyNumber': $("#pol-number").val(),
            },
        },
        autoWidth: true,
        lengthMenu: [[10], [10]],
        pageLength: 10,
        destroy: true,
        searching: false,
        "columns": [
            {"data": "proposalNo"},
            {"data": "polNo"},
            {
                "data": "polCreateddt",
                "render": function (data, type, full, meta) {
                    return moment(full.polCreateddt).format('DD/MM/YYYY');
                }
            },
            {
                "data": "wefDate",
                "render": function (data, type, full, meta) {
                    return moment(full.wefDate).format('DD/MM/YYYY');
                }
            },
            {
                "data": "wetDate",
                "render": function (data, type, full, meta) {
                    return moment(full.wetDate).format('DD/MM/YYYY');
                }
            },
            {
                "data": "policyId",
                "render": function (data, type, full, meta) {
                    return full.createdUser.username;
                }
            },

            {
                "data": "currentStatus",
                "render": function (data, type, full, meta) {
                    if (full.currentStatus === 'D') return "Draft";
                    else if (full.currentStatus === 'NT') return "New Transaction";
                    else if (full.currentStatus === 'RV') return "Revision Invoice";
                }
            },
            {
                "data": "policyId",
                "render": function (data, type, full, meta) {
                    if (full.status === "A") {
                        return '<form action="edituwtrans" method="post"><input type="hidden" name="id" value=' + full.policyId + '><input type="submit"  class="btn btn-success" value="View" ></form>';

                    } else
                        return '<form action="edituwtrans" method="post"><input type="hidden" name="id" value=' + full.policyId + '><input type="submit"  class="btn btn-success" value="Edit" ></form>';

                }

            },
            {
                "data": "policyId",
                "render": function (data, type, full, meta) {
                    return '<input type="button" class="btn btn-success" data-policy=' + encodeURI(JSON.stringify(full)) + ' value="Delete" onclick="confirmPolicyDelete(this);"/>';
                }

            },


        ]
    });
    return currTable;
}

function confirmPolicyDelete(button) {
    var policy = JSON.parse(decodeURI($(button).data("policy")));
    bootbox.confirm("Are you sure want to delete " + policy["polNo"] + "?", function (result) {
        if (result) {
            // // $('#myPleaseWait').modal({
//			backdrop: 'static',
//			keyboard: true
//		})
            $.ajax({
                type: 'GET',
                url: 'deletePolRecord',
                data: {"policyId": policy["policyId"]},
                dataType: 'json',
                async: true,
                success: function (result) {
                    // $('#myPleaseWait').modal('hide');
                    Swal.fire({
                        title: 'Success',
                        text: 'Transaction Deleted Successfully',
                        icon: 'success'
                    });
                    $('#poltrans').DataTable().ajax.reload();
                    $("#existing-trans").hide();
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

function populateClientLov() {
    if ($("#client-frm").filter("div").html() != undefined) {
        Select2Builder.initAjaxSelect2({
            containerId: "client-frm",
            sort: 'fname',
            change: function (e, a, v) {
                $("#rev-search-name").val(e.added.fname.concat(e.added.otherNames));
                $('#client-id').val(e.added.tenId);
            },
            formatResult: function (a) {
                if (a.idNo !== null) {
                    return a.fname + " " + a.otherNames + " - " + a.idNo;
                } else {
                    return a.fname + " " + a.otherNames
                }
            },
            formatSelection: function (a) {
                if (a.idNo !== null) {
                    return a.fname + " " + a.otherNames + " - " + a.idNo;
                } else {
                    return a.fname + " " + a.otherNames
                }
            },
            initSelection: function (element, callback) {

            },
            id: "tenId",
            placeholder: "Select Client"

        });
    }

    $("#client-frm").on("select2-removed", function (e) {
        $("#rev-search-name").val('');
        $("#client-id").val('');
    })
}

function createAccountsForSel() {
    if ($("#acc-frm").filter("div").html() != undefined) {
        Select2Builder.initAjaxSelect2({
            containerId: "acc-frm",
            sort: 'name',
            change: function (e, a, v) {
                $("#agent-search-number").val(e.added.acctId);
                $("#agent-search-name").val(e.added.name);
            },
            formatResult: function (a) {
                return a.name
            },
            formatSelection: function (a) {
                return a.name
            },
            initSelection: function (element, callback) {

            },
            id: "acctId",
            placeholder: "Select Account",
        });
    }

    $("#acc-frm").on("select2-removed", function (e) {
        $("#agent-search-number").val('');
    })
}