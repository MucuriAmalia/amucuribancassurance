$(function () {

    $(document).ready(function () {
        createClassesList();
        deleteClasses();
        addClasses();
        addSubClasses();
        createorUpdateSubClasses();
        addSubclassCovTypes();
        crudSubCovTypes();
        addSubclassSection();
        crudSubclassSection();
        getCovtSectionsModal();
        saveCoverSections();
        crudSubCoverTypeSections();
        addEditSectionForm();

        $("#sub-tb-class-pk").val("");
        $('#min-prem,#min-si').number(true, 2);
        //addClauses();
        $(document).on('click', '#btn-delete-selected-subsec', function () {
            var selected = [];
            $('#subsecList .premium-checkbox-subsec:checked').each(function () {
                selected.push($(this).val());
            });
            if (selected.length === 0) {
                Swal.fire({ title: 'Error', text: 'No items selected.', icon: 'error' });
                return;
            }
            bootbox.confirm("Are you sure you want to delete the selected items?", function (result) {
                if (result) {
                    $.ajax({
                        type: 'POST',
                        url: 'deleteSubSectionsBatch',
                        data: JSON.stringify(selected),
                        contentType: 'application/json',
                        dataType: 'json',
                        success: function (result) {
                            Swal.fire({ title: 'Success', text: 'Selected items deleted.', icon: 'success' });
                            $('#subsecList').DataTable().ajax.reload();
                        },
                        error: function (jqXHR) {
                            Swal.fire({ title: 'Error', text: jqXHR.responseText, icon: 'error' });
                        }
                    });
                }
            });
        });
        // Batch delete handler for premium items
        $(document).on('click', '#btn-delete-selected', function () {
            var selected = [];
            $('#covttypesections .premium-checkbox:checked').each(function () {
                selected.push($(this).val());
            });
            if (selected.length === 0) {
                Swal.fire({ title: 'Error', text: 'No items selected.', icon: 'error' });
                return;
            }
            bootbox.confirm("Are you sure you want to delete the selected items?", function (result) {
                if (result) {
                    $.ajax({
                        type: 'POST',
                        url: 'deleteCoverSectionsBatch',
                        data: JSON.stringify(selected),
                        contentType: 'application/json',
                        dataType: 'json',
                        success: function (result) {
                            Swal.fire({ title: 'Success', text: 'Selected items deleted.', icon: 'success' });
                            $('#covttypesections').DataTable().ajax.reload();
                        },
                        error: function (jqXHR) {
                            Swal.fire({ title: 'Error', text: jqXHR.responseText, icon: 'error' });
                        }
                    });
                }
            });
        });

    });
});


function makeCoverTypesSelection() {
    var table = $('#subcovList').DataTable();
    $('#subcovList tbody').on('click', 'tr', function () {
        $(this).addClass('table-primary').siblings().removeClass('table-primary');
        var aData = table.rows('.table-primary').data();
        if (aData[0] === undefined || aData[0] === null) {

        } else {
            $("#sub-cov-attach-pk").val(aData[0].scId);
            createCoverSections();
        }
    });
}

function makeSubClassSelection() {
    var table = $('#subclassList').DataTable();
    $('#subclassList tbody').on('click', 'tr', function () {
        $(this).addClass('table-primary').siblings().removeClass('table-primary');
        var aData = table.rows('.table-primary').data();

        if (aData[0] === undefined || aData[0] === null) {
            console.log("Passed null " + aData);
        } else {
            $("#sub-tb-class-pk").val(aData[0].subId);
            createCoverTypes();
            createSections();
            $("#sub-cov-attach-pk").val("");
            createCoverSections();
            makeCoverTypesSelection();
            //createClausesList();
        }
    });
}


function addClauses() {
    $("#btn-add-clauses").click(function () {
        $('#clause-form').find("input[type=text],input[type=number],input[type=mobileNumber],input[type=emailFull],input[type=password],input[type=hidden], textarea,select").val("");
        $("#clause-sub-pk").val($("#sub-tb-class-pk").val());
        $("#chk-cl-editable").prop("checked", false);
        $('#clauseModal').modal({
            backdrop: 'static',
            keyboard: true
        })
    });

    var $classForm = $('#clause-form');
    var validator = $classForm.validate();
    $('#saveClauseBtn').click(function () {
        if (!$classForm.valid()) {
            return;
        }
        var $btn = $(this).button('Saving');
        var data = {};
        $classForm.serializeArray().map(function (x) {
            data[x.name] = x.value;
        });
        var url = "createClause";
        var request = $.post(url, data);
        request.success(function () {
            bootbox.alert("Record created/updated Successfully");
            createClausesList();
            validator.resetForm();
            $('#clause-form').find("input[type=text],input[type=number],input[type=mobileNumber],input[type=emailFull],input[type=password],input[type=hidden], textarea,select").val("");
            $("#chk-cl-editable").prop("checked", false);
            $('#clauseModal').modal('hide');
        });

        request.error(function (jqXHR, textStatus, errorThrown) {
            console.log(jqXHR);
            bootbox.alert(jqXHR.responseText);
        });
        request.always(function () {
            $btn.button('reset');
        });
    });
}

function addCoverSections() {
    $("#btn-add-section").click(function () {
        $('#sect-form').find("input[type=text],input[type=number],input[type=mobileNumber],input[type=emailFull],input[type=password],input[type=hidden], textarea,select").val("");
        $("#cover-sec-code-pk").val($("#covt-sec-pk").val());
        $('#sectionsModal').modal({
            backdrop: 'static',
            keyboard: true
        })
    });

    var $classForm = $('#sect-form');
    var validator = $classForm.validate();
    $('#saveSectBtn').click(function () {
        if (!$classForm.valid()) {
            return;
        }
        var $btn = $(this).button('Saving');
        var data = {};
        $classForm.serializeArray().map(function (x) {
            data[x.name] = x.value;
        });
        var url = "createSection";
        var request = $.post(url, data);
        request.success(function () {
            bootbox.alert("Record created/updated Successfully");
            createSectionsList();
            validator.resetForm();
            $('#sect-form').find("input[type=text],input[type=number],input[type=mobileNumber],input[type=emailFull],input[type=password],input[type=hidden], textarea,select").val("");
            $('#sectionsModal').modal('hide');
        });

        request.error(function (jqXHR, textStatus, errorThrown) {
            console.log(jqXHR);
            bootbox.alert(jqXHR.responseText);
        });
        request.always(function () {
            $btn.button('reset');
        });
    });

}


function addCoverTypes() {
    $("#btn-add-covertypes").click(function () {
        $('#cover-form').find("input[type=text],input[type=number],input[type=mobileNumber],input[type=emailFull],input[type=password],input[type=hidden], textarea,select").val("");
        $("#chk-active").prop("checked", false);
        $("#chk-unique-risk").prop("checked", false);
        $("#sub-code-pk").val($("#sub-tb-class-pk").val());
        $('#coverTypeModal').modal({
            backdrop: 'static',
            keyboard: true
        })
    });

    var $classForm = $('#cover-form');
    var validator = $classForm.validate();
    $('#saveCoverTypeBtn').click(function () {
        if (!$classForm.valid()) {
            return;
        }
        var $btn = $(this).button('Saving');
        var data = {};
        $classForm.serializeArray().map(function (x) {
            data[x.name] = x.value;
        });
        var url = "createCoverType";
        var request = $.post(url, data);
        request.success(function () {
            bootbox.alert("Record created/updated Successfully");
            //createCoverTypes();
            $('#subcovList').DataTable().ajax.reload();
            validator.resetForm();
            $('#cover-form').find("input[type=text],input[type=number],input[type=mobileNumber],input[type=emailFull],input[type=password],input[type=hidden], textarea,select").val("");
            $("#chk-cov-active").prop("checked", false);
            $('#coverTypeModal').modal('hide');
        });

        request.error(function (jqXHR, textStatus, errorThrown) {
            console.log(jqXHR);
            bootbox.alert(jqXHR.responseText);
        });
        request.always(function () {
            $btn.button('reset');
        });
    });
}

function addSubClasses() {
    $("#btn-add-subclass").click(function () {
        $('#sub-class-form').find("input[type=text],input[type=number],input[type=mobileNumber],input[type=emailFull],input[type=password],input[type=hidden], textarea,select").val("");
        $("#chk-sub-active").prop("checked", false);
        $("#chk-unique-risk").prop("checked", false);
        $("#clasdef-pk").val($("#class-pk").val());
        $('#subclassModal').modal({
            backdrop: 'static',
            keyboard: true
        })
    });


}

function createorUpdateSubClasses() {
    var $classForm = $('#sub-class-form');
    var validator = $classForm.validate();
    $('#saveSubClassBtn').click(function () {
        if (!$classForm.valid()) {
            return;
        }
        var $btn = $(this).button('Saving');
        var data = {};
        $classForm.serializeArray().map(function (x) {
            data[x.name] = x.value;
        });
        var url = "createSubClass";
        var request = $.post(url, data);
        request.success(function () {
            Swal.fire({
                title: 'Success',
                text: 'Record created/updated Successfully',
                icon: 'success'
            });
            $('#subclassList').DataTable().ajax.reload();
            validator.resetForm();
            $('#sub-class-form').find("input[type=text],input[type=number],input[type=mobileNumber],input[type=emailFull],input[type=password],input[type=hidden], textarea,select").val("");
            $("#chk-active").prop("checked", false);
            $("#chk-unique-risk").prop("checked", false);
            $('#subclassModal').modal('hide');
        });

        request.error(function (jqXHR, textStatus, errorThrown) {
            Swal.fire({
                title: 'Error',
                text: jqXHR.responseText,
                icon: 'error'
            });
        });
        request.always(function () {
            $btn.button('reset');
        });
    });
}

function deleteClasses() {
    $("#delClassBtn").click(function () {
        bootbox.confirm("Are you sure want to delete the record?", function (result) {
            if (result) {
                if ($("#cl-code").val() != '') {
                    $.ajax({
                        type: 'GET',
                        url: 'deleteClass/' + $("#cl-code").val(),
                        dataType: 'json',
                        async: true,
                        success: function (result) {
                            Swal.fire({
                                title: 'Success',
                                text: 'Record Deleted Successfully',
                                icon: 'success'
                            });

                            $('#class-def').select2('val', null);
                            createClassesList();
                            $('#class-form').find("input[type=text],input[type=number],input[type=mobileNumber],input[type=emailFull],input[type=password],input[type=hidden], textarea,select").val("");
                            $("#chk-active").prop("checked", false);
                            $('#classModal').modal('hide');
                        },
                        error: function (jqXHR, textStatus, errorThrown) {
                            Swal.fire({
                                title: 'Error',
                                text: jqXHR.responseText,
                                icon: 'error'
                            });
                        }
                    });
                } else {
                    bootbox.alert("No Class to delete");
                }
            }
        });
    });
}

function addClasses() {
    $("#btn-add-classes").click(function () {
        $('#class-form').find("input[type=text],input[type=number],input[type=mobileNumber],input[type=emailFull],input[type=password],input[type=hidden], textarea,select").val("");
        $("#chk-active").prop("checked", true);
        $("#delClassBtn").hide();
        $('#classModal').modal({
            backdrop: 'static',
            keyboard: true
        })

    });

    $("#btn-edit-classes").click(function () {
        if ($("#cl-code").val() != '') {
            $("#delClassBtn").show();
            $('#classModal').modal({
                backdrop: 'static',
                keyboard: true
            })
        } else {
            Swal.fire({
                title: 'Error',
                text: 'Select Class to Edit',
                icon: 'error'
            })
        }
    });

    var $classForm = $('#class-form');
    var validator = $classForm.validate();
    $('#saveClassBtn').click(function () {
        if (!$classForm.valid()) {
            return;
        }
        var $btn = $(this).button('Saving');
        var data = {};
        $classForm.serializeArray().map(function (x) {
            data[x.name] = x.value;
        });
        var url = "createClassDef";
        var request = $.post(url, data);
        request.success(function () {
            Swal.fire({
                title: 'Success',
                text: 'Record created/updated Successfully',
                icon: 'success'
            });
            $('#class-def').select2('val', null);
            createClassesList();
            validator.resetForm();
            $('#class-form').find("input[type=text],input[type=number],input[type=mobileNumber],input[type=emailFull],input[type=password],input[type=hidden], textarea,select").val("");
            $("#chk-active").prop("checked", false);
            $('#classModal').modal('hide');
        });

        request.error(function (jqXHR, textStatus, errorThrown) {
            Swal.fire({
                title: 'Error',
                text: jqXHR.responseText,
                icon: 'error'
            });
        });
        request.always(function () {
            $btn.button('reset');
        });
    });
}


function createCoverTypes() {
    var url = "subCovertypes/0";
    if ($("#sub-tb-class-pk").val() != '') {
        url = "subCovertypes/" + $("#sub-tb-class-pk").val();
    }
    var currTable = $('#subcovList').DataTable({
        "processing": true,
        "serverSide": true,
        "searching": true,  // Explicitly enable searching
        autoWidth: true,
        "ajax": {
            'url': url,
            'data': function(d) {
                // Convert search value to uppercase before sending to server
                if (d.search && d.search.value) {
                    d.search.value = d.search.value.toUpperCase();
                }
                return d;
            }
        },
        lengthMenu: [5,10,20,25,50,100],
        pageLength: 10,
        destroy: true,
        "columns": [
            {
                "data": "scId",
                "render": function (data, type, full, meta) {
                    return full.coverTypes.covShtDesc;
                }
            },
            {
                "data": "scId",
                "render": function (data, type, full, meta) {
                    return full.coverTypes.covName;
                }
            },
            {
                "data": "defaultCoverType",
                "render": function (data, type, full, meta) {
                    if (full.defaultCoverType) {
                        return 'Yes';
                    } else {
                        return 'No'
                    }
                }
            },
            {"data": "minPrem"},
            {
                "data": "scId",
                "searchable": false,  // Don't include action buttons in search
                "render": function (data, type, full, meta) {
                    return '<button type="button" class="btn btn-success btn btn-info btn-sm" type="button" data-subcovtypes=' + encodeURI(JSON.stringify(full)) + ' onclick="editSubCovType(this);"><i class="fa fa-pencil-square-o"></button>';
                }
            },
            {
                "data": "scId",
                "searchable": false,  // Don't include action buttons in search
                "render": function (data, type, full, meta) {
                    return '<button type="button" class="btn btn-danger btn btn-danger btn-sm" data-subcovtypes=' + encodeURI(JSON.stringify(full)) + ' onclick="confirmSubCovTypeDel(this);"><i class="fa fa-trash-o"></button>';
                }
            },
        ]
    });
    return currTable;
}


function createSubClassesList() {
    var url = "subclassList/0";
    if ($("#class-pk").val() != '') {
        url = "subclassList/" + $("#class-pk").val();
    }
    return $('#subclassList').DataTable({
        "processing": true,
        "serverSide": true,
        "searching": true,
        autoWidth: true,
        "ajax": {
            'url': url,
            'data': function(d) {
                if (d.search && d.search.value) {
                    d.search.value = d.search.value.toUpperCase();
                }
                return d;
            }
        },
        lengthMenu: [5,10,20,25,50,100],
        pageLength: 10,
        destroy: true,
        "columns": [
            {"data": "subShtDesc"},
            {"data": "subDesc"},
            {"data": "riskFormat"},
            {"data": "riskUnique"},
            {"data": "active"},
            {
                "data": "subId",
                "searchable": false,
                "render": function (data, type, full, meta) {
                    return '<button type="button" class="btn btn-success btn btn-info btn-sm" type="button" data-subclass=' + encodeURI(JSON.stringify(full)) + ' onclick="editSubclass(this);"><i class="fa fa-pencil-square-o"></button>';
                }
            },
            {
                "data": "subId",
                "searchable": false,
                "render": function (data, type, full, meta) {
                    return '<button type="button" class="btn btn-danger btn btn-danger btn-sm" data-subclass=' + encodeURI(JSON.stringify(full)) + ' onclick="deleteSubclass(this);"><i class="fa fa-trash-o"></button>';
                }
            },
        ]
    });
}


function createSectionsList() {
    var url = "sections/0";
    if ($("#covt-sec-pk").val() != '') {
        url = "sections/" + $("#covt-sec-pk").val();
    }
    var currTable = $('#sectionsList').DataTable({
        "processing": true,
        "serverSide": true,
        autoWidth: true,
        "ajax": {
            'url': url,
        },
        lengthMenu: [5,10,20,25,50,100],
        pageLength: 10,
        destroy: true,
        "columns": [
            {"data": "shtDesc"},
            {"data": "desc"},
            {"data": "type"},
            {
                "data": "Id",
                "render": function (data, type, full, meta) {
                    return '<input type="button" class="hyperlink-btn" data-acctype=' + encodeURI(JSON.stringify(full)) + ' value="Edit" onclick="editAcctTypes(this);"/>';
                }

            },
            {
                "data": "Id",
                "render": function (data, type, full, meta) {
                    return '<input type="button" class="hyperlink-btn" data-acctype=' + encodeURI(JSON.stringify(full)) + ' value="Delete" onclick="confirmAcctypeDel(this);"/>';
                }

            },
        ]
    });
    return currTable;
}


function createClausesList() {
    var url = "clauses/0";
    if ($("#sub-tb-class-pk").val() != '') {
        url = "clauses/" + $("#sub-tb-class-pk").val();
    }
    var currTable = $('#clausesList').DataTable({
        "processing": true,
        "serverSide": true,
        "ajax": {
            'url': url,
        },
        lengthMenu: [5,10,20,25,50,100],
        pageLength: 10,
        destroy: true,
        "columns": [
            {"data": "clauShtDesc"},
            {"data": "clauHeading"},
            {"data": "editable"},
            {"data": "clauWording"},
            {
                "data": "clauId",
                "render": function (data, type, full, meta) {
                    return '<input type="button" class="hyperlink-btn" data-acctype=' + encodeURI(JSON.stringify(full)) + ' value="Edit" onclick="editAcctTypes(this);"/>';
                }

            },
            {
                "data": "clauId",
                "render": function (data, type, full, meta) {
                    return '<input type="button" class="hyperlink-btn" data-acctype=' + encodeURI(JSON.stringify(full)) + ' value="Delete" onclick="confirmAcctypeDel(this);"/>';
                }

            },
        ]
    });
    return currTable;
}


function createClassesList() {
    if ($("#class-def").filter("div").html() != undefined) {
        Select2Builder.initAjaxSelect2({
            containerId: "class-def",
            sort: 'clDesc',
            change: function (e, a, v) {
                $("#class-pk").val(e.added.clId);
                $("#cl-code").val(e.added.clId);
                model.class.classId = e.added.clId;
                $("#class-id").val(e.added.clShtDesc);
                $("#class-name").val(e.added.clDesc);
                if (e.added.clactive)
                    $("#chk-active").prop("checked", e.added.clactive);
                else
                    $("#chk-active").prop("checked", false);
                createSubClassesList();
                makeSubClassSelection();
            },
            formatResult: function (a) {
                return a.clDesc
            },
            formatSelection: function (a) {
                return a.clDesc
            },
            initSelection: function (element, callback) {
//	            	var code = $("#obId").val();
//	                var name = $("#ob-name").val();
//	                model.accounts.branch.brnCode = code;
//	                var data = {obName:name,obId:code};
//	                callback(data);
            },
            id: "clId",
            placeholder: "Select Classes"
        });
    }
}

function editSubclass(button) {
    var subclass = JSON.parse(decodeURI($(button).data("subclass")));
    $("#scl-pk").val(subclass["subId"]);
    $("#sub-class-id").val(subclass["subShtDesc"]);
    $("#sub-class-name").val(subclass["subDesc"]);
    $("#risk-id-format").val(subclass["riskFormat"]);
    $("#clm-pool-percent").val(subclass["claimPoolPercentage"]);
    $("#clasdef-pk").val(subclass["classDef"].clId);
    if (subclass["active"])
        $("#chk-sub-active").prop("checked", subclass["active"]);
    else
        $("#chk-sub-active").prop("checked", false);
    if (subclass["riskUnique"])
        $("#chk-unique-risk").prop("checked", subclass["active"]);
    else
        $("#chk-unique-risk").prop("checked", false);
    $('#subclassModal').modal({
        backdrop: 'static',
        keyboard: true
    })

}

function confirmSubCovTypeDel(button) {
    var covtype = JSON.parse(decodeURI($(button).data("subcovtypes")));
    bootbox.confirm("Are you sure want to delete " + covtype["coverTypes"].covName + "?", function (result) {
        if (result) {
            $.ajax({
                type: 'GET',
                url: 'deleteSubCovType/' + covtype["scId"],
                dataType: 'json',
                async: true,
                success: function (result) {
                    Swal.fire({
                        title: 'Success',
                        text: 'Record Deleted Successfully',
                        icon: 'success'
                    });
                    $('#subcovList').DataTable().ajax.reload();
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
    });
}

function editSubCovType(button) {
    var covtype = JSON.parse(decodeURI($(button).data("subcovtypes")));
    $("#cov-code").val(covtype["scId"]);
    $("#sub-code-pk").val(covtype["subclass"].subId);
    $("#sub-cov-pk").val(covtype["coverTypes"].covId);
    $("#sub-cov-name").val(covtype["coverTypes"].covName);
    $("#new-cover-id").val(covtype["coverTypes"].covShtDesc);
    $("#new-cov-active").prop("checked", covtype["coverTypes"].active || false);
    $("#min-prem").val(covtype["minPrem"]);
    createCoverTypeLov();
    if (covtype["defaultCoverType"])
        $("#chk-sub-cov-def").prop("checked", covtype["defaultCoverType"]);
    else
        $("#chk-sub-cov-def").prop("checked", false);
    $('#coverTypeModal').modal({
        backdrop: 'static',
        keyboard: true
    });

}

function deleteSubclass(button) {
    var subclass = JSON.parse(decodeURI($(button).data("subclass")));
    bootbox.confirm("Are you sure want to delete " + subclass["subDesc"] + "?", function (result) {
        if (result) {
            $.ajax({
                type: 'GET',
                url: 'deleteSubClass/' + subclass["subId"],
                dataType: 'json',
                async: true,
                success: function (result) {
                    Swal.fire({
                        title: 'Success',
                        text: 'Record Deleted Successfully',
                        icon: 'success'
                    });
                    $('#subclassList').DataTable().ajax.reload();
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
    });
}

function crudSubCovTypes() {
    var $classForm = $('#cover-form');
    var validator = $classForm.validate();
    $('#saveCoverTypeBtn').click(function () {

        if (!$classForm.valid()) {
            return;
        }

        if ($("#sub-cov-pk").val() == '') {
            Swal.fire({
                title: 'Error',
                text: 'Select Cover Type to continue',
                icon: 'error'
            });
            return;
        }

        var $btn = $(this).button('Saving');
        var data = {};
        $classForm.serializeArray().map(function (x) {
            data[x.name] = x.value;
        });
        var url = "createSubCoverType";
        var request = $.post(url, data);
        request.success(function () {
            Swal.fire({
                title: 'Success',
                text: 'Record created/updated Successfully',
                icon: 'success'
            });
            $('#subcovList').DataTable().ajax.reload();
            validator.resetForm();
            $('#cover-form').find("input[type=text],input[type=number],input[type=mobileNumber],input[type=emailFull],input[type=password],input[type=hidden], textarea,select").val("");
            $("#chk-sub-cov-def").prop("checked", false);
            $('#coverTypeModal').modal('hide');
        });

        request.error(function (jqXHR, textStatus, errorThrown) {
            Swal.fire({
                title: 'Error',
                text: jqXHR.responseText,
                icon: 'error'
            })
        });
        request.always(function () {
            $btn.button('reset');
        });
    });
}

function addSubclassCovTypes() {
    $("#btn-add-sub-cov").click(function () {
        if ($("#sub-tb-class-pk").val() != '') {
            model.subclass.subcode = $("#sub-tb-class-pk").val();
            $("#sub-cov-pk").val("");
            $("#sub-cov-name").val("");
            createCoverTypeLov();
            $('#cover-form').find("input[type=text],input[type=number],input[type=mobileNumber],input[type=emailFull],input[type=password],input[type=hidden], textarea,select").val("");
            $("#chk-sub-cov-def").prop("checked", false);
            $("#sub-code-pk").val($("#sub-tb-class-pk").val());
            $('#coverTypeModal').modal({
                backdrop: 'static',
                keyboard: true
            })
        } else {
            bootbox.alert("Select Sub class to Add Cover Type");
        }

    });
    $("#btn-add-new-cov").click(function () {
        $('#new-cover-form').find("input[type=text],input[type=number],input[type=mobileNumber],input[type=emailFull],input[type=password],input[type=hidden], textarea,select").val("");
        $("#new-cov-active").prop("checked", false);
        $("#delnewCoverTypeBtn").hide();
        $('#newcoverTypeModal').modal({
            backdrop: 'static',
            keyboard: true
        })
    });

    $("#delnewCoverTypeBtn").click(function () {
        if ($("#new-cov-code").val() != '') {
            bootbox.confirm("Are you sure want to delete CoverType?", function (result) {
                if (result) {
                    $.ajax({
                        type: 'GET',
                        url: 'deleteCovType/' + $("#new-cov-code").val(),
                        dataType: 'json',
                        async: true,
                        success: function (result) {
                            Swal.fire({
                                title: 'Success',
                                text: 'Record Deleted Successfully',
                                icon: 'success'
                            });
                            $('#newcoverTypeModal').modal('hide');
                            createCoverTypeLov();
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
            });
        } else {
            bootbox.alert("Select Cover Type to Delete");
        }
    })

    $("#btn-edit-old-cov").click(function () {
        $("#new-cov-code").val($('#sub-cov-pk').val());
        if ($("#new-cov-code").val() !== '') {
            $('#new-cov-name').val($("#sub-cov-name").val());
            $("#min-si").val($('#min-prem').val())
            $("#delnewCoverTypeBtn").show();
            $('#newcoverTypeModal').modal({
                backdrop: 'static',
                keyboard: true
            })
        } else {
            bootbox.alert("Select Cover Type to Edit");
        }

    });

    var $classForm = $('#new-cover-form');
    var validator = $classForm.validate();
    $('#savenewCoverTypeBtn').click(function () {
        if (!$classForm.valid()) {
            return;
        }
        var $btn = $(this).button('Saving');
        var data = {};
        $classForm.serializeArray().map(function (x) {
            data[x.name] = x.value;
        });
        data["classId"] = $("#class-pk").val();
        var url = "createCoverType";
        var request = $.post(url, data);
        request.success(function () {
            Swal.fire({
                title: 'Success',
                text: 'Record created/updated Successfully',
                icon: 'success'
            })
            createCoverTypeLov();
            validator.resetForm();
            $('#new-cover-form').find("input[type=text],input[type=number],input[type=mobileNumber],input[type=emailFull],input[type=password],input[type=hidden], textarea,select").val("");
            $("#new-cov-active").prop("checked", false);
            $('#newcoverTypeModal').modal('hide');
        });

        request.error(function (jqXHR, textStatus, errorThrown) {
            Swal.fire({
                title: 'Error',
                text: jqXHR.responseText,
                icon: 'error'
            });
        });
        request.always(function () {
            $btn.button('reset');
        });
    });

}


function createCoverTypeLov() {
    if ($("#sub-cov-def").filter("div").html() != undefined) {
        Select2Builder.initAjaxSelect2({
            containerId: "sub-cov-def",
            sort: 'covName',
            change: function (e, a, v) {
                $("#sub-cov-pk").val(e.added.covId);
                $("#new-cov-code").val(e.added.covId);
                $("#new-cover-id").val(e.added.covShtDesc);
                $("#new-cov-name").val(e.added.covName);
                $("#min-si").val(e.added.minSi);
                if (e.added.active)
                    $("#new-cov-active").prop("checked", e.added.active);
                else
                    $("#new-cov-active").prop("checked", false);
            },
            formatResult: function (a) {
                return a.covName
            },
            formatSelection: function (a) {
                return a.covName
            },
            initSelection: function (element, callback) {
                var coverCode = $("#sub-cov-pk").val();
                var coverName = $("#sub-cov-name").val();
//	          	 model.organization.country.county.countyId = countyCode;
                var data = {covName: coverName, covId: coverCode};
                callback(data);
            },
            id: "covId",
            placeholder: "Select Cover Type",
            params: {
                subId: function () {
                    return model.subclass.subcode;
                }
                ,
                classId: function () {
                    return model.class.classId;
                }
            }
        });

    }
}


function createSections() {
    var url = "subSections/0";
    if ($("#sub-tb-class-pk").val() != '') {
        url = "subSections/" + $("#sub-tb-class-pk").val();
    }
    var currTable = $('#subsecList').DataTable({
        "processing": true,
        "serverSide": true,
        "searching": true,
        autoWidth: true,
        "ajax": {
            'url': url,
            'data': function(d) {

                if (d.search && d.search.value) {
                    d.search.value = d.search.value.toUpperCase();
                }
                return d;
            }
        },
        lengthMenu: [5,10,20,25,50,100],
        pageLength: 10,
        destroy: true,
        "columns": [
            {
                "data": "ssId",
                "orderable": false,
                "searchable": false,
                "render": function (data, type, full, meta) {
                    return '<input type="checkbox" class="premium-checkbox-subsec" value="' + data + '">';
                }
            },
            {
                "data": "ssId",
                "render": function (data, type, full, meta) {
                    return full.section.shtDesc;
                }
            },
            {
                "data": "ssId",
                "render": function (data, type, full, meta) {
                    return full.section.desc;
                }
            },
            {
                "data": "ssId",
                "render": function (data, type, full, meta) {
                    return full.section.type;
                }
            },
            {
                "data": "ssId",
                "render": function (data, type, full, meta) {
                    if (full.computeCommission && full.computeCommission==='Y') {
                        return 'Yes'
                    } else {
                        return 'No';
                    }
                }
            },
            {
                "data": "ssId",
                "render": function (data, type, full, meta) {
                    if (full.active) {
                        return 'Yes'
                    } else {
                        return 'No';
                    }
                }
            },
            {
                "data": "ssId",
                "orderable": false,
                "searchable": false,
                "render": function (data, type, full, meta) {
                    return '<button type="button" class="btn btn-success btn btn-info btn-sm" type="button" data-sections=' + encodeURI(JSON.stringify(full)) + ' onclick="editSubSection(this);"><i class="fa fa-pencil-square-o"></button>';
                }
            },
            {
                "data": "ssId",
                "orderable": false,
                "searchable": false,
                "render": function (data, type, full, meta) {
                    return '<button type="button" class="btn btn-danger btn btn-danger btn-sm" data-sections=' + encodeURI(JSON.stringify(full)) + ' onclick="confirmSubSectionDel(this);"><i class="fa fa-trash-o"></button>';
                }
            },
        ]
    });


    $('#subsecList').off('change', '#select-all-premium-subsec').on('change', '#select-all-premium-subsec', function () {
        var checked = $(this).is(':checked');
        $('#subsecList .premium-checkbox-subsec').prop('checked', checked);
    });

    $('#subsecList').off('change', '.premium-checkbox-subsec').on('change', '.premium-checkbox-subsec', function () {
        if (!$(this).is(':checked')) {
            $('#select-all-premium-subsec').prop('checked', false);
        }
    });

    return currTable;
}
function crudSubclassSection() {
    var $classForm = $('#sect-form');
    var validator = $classForm.validate();
    $('#saveSectBtn').click(function () {
        if (!$classForm.valid()) {
            return;
        }
        var $btn = $(this).button('Saving');
        var data = {};
        $classForm.serializeArray().map(function (x) {
            data[x.name] = x.value;
        });
        var url = "createSubclassSection";
        var request = $.post(url, data);
        request.success(function () {
            Swal.fire({
                title: 'Success',
                text: 'Record created/updated Successfully',
                icon: 'success'
            });
            $('#subsecList').DataTable().ajax.reload();
            validator.resetForm();
            $('#sect-form').find("input[type=text],input[type=number],input[type=mobileNumber],input[type=emailFull],input[type=password],input[type=hidden], textarea,select").val("");
            $("#decl-sect").prop("checked", false);
            $("#sect-active").prop("checked", false);
            $('#sectionsModal').modal('hide');
        });

        request.error(function (jqXHR, textStatus, errorThrown) {
            Swal.fire({
                title: 'Error',
                text: jqXHR.responseText,
                icon: 'error'
            });
        });
        request.always(function () {
            $btn.button('reset');
        });
    });

    $("#btn-edit-old-sect").click(function () {
        if ($("#new-sec-code").val() != '') {
            $("#newdelSectBtn").show();
            $('#newsectionsModal').modal({
                backdrop: 'static',
                keyboard: true
            })
        } else {
            bootbox.alert("Select Section to Edit")
        }
    });

    $("#newdelSectBtn").click(function () {
        if ($("#new-sec-code").val() != '') {
            bootbox.confirm("Are you sure want to delete Section?", function (result) {
                if (result) {
                    $.ajax({
                        type: 'GET',
                        url: 'deleteSection/' + $("#new-sec-code").val(),
                        dataType: 'json',
                        async: true,
                        success: function (result) {
                            Swal.fire({
                                title: 'Success',
                                text: 'Record Deleted Successfully',
                                icon: 'success'
                            });
                            $('#newsectionsModal').modal('hide');
                            createSectionsLov();
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
            });
        } else {
            bootbox.alert("Select Section to Delete")
        }
    });
}

function addSubclassSection() {
    $("#btn-add-sub-sec").click(function () {
        if ($("#sub-tb-class-pk").val() != '') {

            $('#sect-form').find("input[type=text],input[type=number],input[type=mobileNumber],input[type=emailFull],input[type=password],input[type=hidden], textarea,select").val("");
            $("#decl-sect").prop("checked", false);
            $("#sect-active").prop("checked", false);
            $("#sec-sub-code-pk").val($("#sub-tb-class-pk").val());
            model.subclass.subcode = $("#sub-tb-class-pk").val();
            createSectionsLov();
            $('#sectionsModal').modal({
                backdrop: 'static',
                keyboard: true
            })
        } else {
            bootbox.alert("Select Sub class to Add Section");
        }

    });

    $('#btn-add-new-sect').click(function () {
        $('#new-sect-form').find("input[type=text],input[type=number],input[type=mobileNumber],input[type=emailFull],input[type=password],input[type=hidden], textarea,select").val("");
        $("#newdelSectBtn").hide();
        $('#newsectionsModal').modal({
            backdrop: 'static',
            keyboard: true
        })
    });


}

function addEditSectionForm() {
    var $classForm = $('#new-sect-form');
    var validator = $classForm.validate();
    $('#newsaveSectBtn').click(function () {
        if (!$classForm.valid()) {
            return;
        }
        var $btn = $(this).button('Saving');
        var data = {};
        $classForm.serializeArray().map(function (x) {
            data[x.name] = x.value;
        });
        var url = "createSection";
        var request = $.post(url, data);
        request.success(function () {
            Swal.fire({
                title: 'Success',
                text: 'Record created/updated Successfully',
                icon: 'success'
            });

            validator.resetForm();
            $('#new-sect-form').find("input[type=text],input[type=number],input[type=mobileNumber],input[type=emailFull],input[type=password],input[type=hidden], textarea,select").val("");
            //$("#new-cov-active").prop("checked", false);
            $("#sect-pk-code").val("");
            $("#sect-pk-name").val("");
            createSectionsLov();
            $('#newsectionsModal').modal('hide');
        });

        request.error(function (jqXHR, textStatus, errorThrown) {
            Swal.fire({
                title: 'Error',
                text: jqXHR.responseText,
                icon: 'error'
            });
        });
        request.always(function () {
            $btn.button('reset');
        });
    });


    $('#newsavenewSectBtn').click(function () {
        if (!$classForm.valid()) {
            return;
        }
        var $btn = $(this).button('Saving');
        var data = {};
        $classForm.serializeArray().map(function (x) {
            data[x.name] = x.value;
        });
        var url = "createSection";
        var request = $.post(url, data);
        request.success(function () {
            Swal.fire({
                title: 'Success',
                text: 'Record created/updated Successfully',
                icon: 'success'
            });

            validator.resetForm();
            $('#new-sect-form').find("input[type=text],input[type=number],input[type=mobileNumber],input[type=emailFull],input[type=password],input[type=hidden], textarea,select").val("");
            $("#sect-pk-code").val("");
            $("#sect-pk-name").val("");
            createSectionsLov();
        });

        request.error(function (jqXHR, textStatus, errorThrown) {
            Swal.fire({
                title: 'Error',
                text: jqXHR.responseText,
                icon: 'error'
            });
        });
        request.always(function () {
            $btn.button('reset');
        });
    });
}

function confirmSubSectionDel(button) {
    var section = JSON.parse(decodeURI($(button).data("sections")));
    bootbox.confirm("Are you sure want to delete " + section["section"].desc, function (result) {
        if (result) {
            $.ajax({
                type: 'GET',
                url: 'deleteSubSection/' + section["ssId"],
                dataType: 'json',
                async: true,
                success: function (result) {
                    Swal.fire({
                        title: 'Success',
                        text: 'Record Deleted Successfully',
                        icon: 'success'
                    });

                    $('#subsecList').DataTable().ajax.reload();
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
    });
}

function editSubSection(button) {
    var section = JSON.parse(decodeURI($(button).data("sections")));
    $("#sec-code").val(section["ssId"]);
    $("#sec-sub-code-pk").val(section["subclass"].subId);
    $("#sect-pk-name").val(section['section'].desc);
    $("#sect-pk-code").val(section['section'].id);


    $("#sect-pk-code").val(section['section'].id);
    $("#new-sec-code").val(section['section'].id);
    $("#new-sec-id").val(section['section'].shtDesc);
    $("#new-sect-name").val(section['section'].desc);
    $("#new-sect-type").val(section['section'].type);

    createSectionsLov();
    if (section["declaration"])
        $("#decl-sect").prop("checked", section["declaration"]);
    else
        $("#decl-sect").prop("checked", false);

    if (section["active"])
        $("#sect-active").prop("checked", section["active"]);
    else
        $("#sect-active").prop("checked", false);
    if (section["computeCommission"] && section["computeCommission"]==='Y')
        $("#compute-comm").prop("checked", true);
    else
        $("#compute-comm").prop("checked", false);
    $('#sectionsModal').modal({
        backdrop: 'static',
        keyboard: true
    })
}

function createSectionsLov() {
    if ($("#sect-def").filter("div").html() != undefined) {
        Select2Builder.initAjaxSelect2({
            containerId: "sect-def",
            sort: 'desc',
            change: function (e, a, v) {
                // console.log(e.added);
                $("#sect-pk-code").val(e.added.id);
                $("#new-sec-code").val(e.added.id);
                $("#new-sec-id").val(e.added.shtDesc);
                $("#new-sect-name").val(e.added.desc);

                $("#new-sect-type").val(e.added.type);
//	        	  if(e.added.active)
//	        			$("#new-cov-active").prop("checked",e.added.active);
//	        		else
//	        			$("#new-cov-active").prop("checked", false);
            },
            formatResult: function (a) {
                return a.desc
            },
            formatSelection: function (a) {
                return a.desc
            },
            initSelection: function (element, callback) {
                var sectCode = $("#sect-pk-code").val();
                var sectName = $("#sect-pk-name").val();
////	          	 model.organization.country.county.countyId = countyCode;
                var data = {desc: sectName, id: sectCode};
                callback(data);
            },
            id: "id",
            width: "200px",
            placeholder: "Select Section",
            params: {
                subId: function () {
                    return model.subclass.subcode;
                }
            }
        });

    }
}

function getCovtSectionsModal() {

    $("#btn-add-covt_sections").click(function () {
        if ($("#sub-cov-attach-pk").val() != '') {
            $.ajax({
                type: 'GET',
                url: 'subclasscovtSections',
                dataType: 'json',
                data: {"secId": $("#sub-cov-attach-pk").val(), "subId": $("#sub-tb-class-pk").val()},
                async: true,
                success: function (result) {
                    $("#covtSectTbl tbody").each(function () {
                        $(this).remove();
                    });
                    for (var res in result) {
                        var markup = "<tr><td><input type='checkbox' name='record' id='" + result[res].ssId + "'></td><td>" + result[res].section.shtDesc + "</td><td>" + result[res].section.desc + "</td></tr>";
                        $("#covtSectTbl").append(markup);
                    }
                    $("#sect-cover-code-pk").val($("#sub-cov-attach-pk").val());
                    $('#covSectModal').modal({
                        backdrop: 'static',
                        keyboard: true
                    })
                },
                error: function (jqXHR, textStatus, errorThrown) {
                    Swal.fire({
                        title: 'Error',
                        text: jqXHR.responseText,
                        icon: 'error'
                    });
                }
            });
        } else {
            bootbox.alert("Select Cover Type to add A section");
        }
    })


}

function createCoverSections() {
    var url = "covtypeSections/0";
    if ($("#sub-cov-attach-pk").val() != '') {
        url = "covtypeSections/" + $("#sub-cov-attach-pk").val();
    }
    var currTable = $('#covttypesections').DataTable({
        "processing": true,
        "serverSide": true,
        "searching": true,
        autoWidth: true,
        "ajax": {
            'url': url,
            'data': function(d) {

                if (d.search && d.search.value) {
                    d.search.value = d.search.value.toUpperCase();
                }
                return d;
            }
        },
        lengthMenu: [5,10,20,25,50,100],
        pageLength: 10,
        destroy: true,
        "columns": [
            {
                "data": "scsId",
                "orderable": false,
                "searchable": false,
                "render": function (data, type, full, meta) {
                    return '<input type="checkbox" class="premium-checkbox" value="' + data + '">';
                }
            },
            {
                "data": "scsId",
                "render": function (data, type, full, meta) {
                    return full.subSections.section.shtDesc;
                }
            },
            {
                "data": "ssId",
                "render": function (data, type, full, meta) {
                    return full.subSections.section.desc;
                }
            },
            {
                "data": "ssId",
                "render": function (data, type, full, meta) {
                    return full.mandatory ? 'Yes' : 'No';
                }
            },
            {
                "data": "scsId",
                "orderable": false,
                "searchable": false,
                "render": function (data, type, full, meta) {
                    return '<button type="button" class="btn btn-success btn btn-info btn-sm" data-sections=' + encodeURI(JSON.stringify(full)) + ' onclick="editSubCoverSection(this);"><i class="fa fa-pencil-square-o"></button>';
                }
            },
            {
                "data": "scsId",
                "orderable": false,
                "searchable": false,
                "render": function (data, type, full, meta) {
                    return '<button type="button" class="btn btn-danger btn btn-danger btn-sm" data-sections=' + encodeURI(JSON.stringify(full)) + ' onclick="confirmSubCoverSectionDel(this);"><i class="fa fa-trash-o"></button>';
                }
            },
        ]
    });


    $('#covttypesections').off('change', '#select-all-premium').on('change', '#select-all-premium', function () {
        var checked = $(this).is(':checked');
        $('#covttypesections .premium-checkbox').prop('checked', checked);
    });

    $('#covttypesections').off('change', '.premium-checkbox').on('change', '.premium-checkbox', function () {
        if (!$(this).is(':checked')) {
            $('#select-all-premium').prop('checked', false);
        }
    });

    return currTable;
}

function saveCoverSections() {
    var arr = [];
    $("#savecoverSectionBtn").click(function () {
        $("#covtSectTbl tbody").find('input[name="record"]').each(function () {
            if ($(this).is(":checked")) {
                arr.push($(this).attr("id"));
            }
        });
        if (arr.length == 0) {
            bootbox.alert("No Section Selected to attach..");
            return;
        }

        var $currForm = $('#cover-section-form');
        var currValidator = $currForm.validate();
        if (!$currForm.valid()) {
            return;
        }

        var data = {};
        $currForm.serializeArray().map(function (x) {
            data[x.name] = x.value;
        });
        var url = "createCoverTypeSections";
        data.sections = arr;
        $.ajax({
            url: url,
            type: "POST",
            data: JSON.stringify(data),
            success: function (s) {
                arr = [];
                Swal.fire({
                    title: 'Success',
                    text: 'Record created/updated Successfully',
                    icon: 'success'
                });
                $('#covttypesections').DataTable().ajax.reload();
                $('#covSectModal').modal('hide');
            },
            error: function (jqXHR, textStatus, errorThrown) {
                console.log(jqXHR);
                Swal.fire({
                    title: 'Error',
                    text: jqXHR.responseText,
                    icon: 'error'
                });
            },
            dataType: "json",
            contentType: "application/json"
        });
    })
}

function crudSubCoverTypeSections() {
    var $classForm = $('#edit-cover-section-form');
    var validator = $classForm.validate();
    $('#saveEditcoverSectionBtn').click(function () {
        if (!$classForm.valid()) {
            return;
        }
        var $btn = $(this).button('Saving');
        var data = {};
        $classForm.serializeArray().map(function (x) {
            data[x.name] = x.value;
        });
        var url = "createCoverTypeSection";
        var request = $.post(url, data);
        request.success(function () {
            Swal.fire({
                title: 'Success',
                text: 'Record created/updated Successfully',
                icon: 'success'
            })
            $('#covttypesections').DataTable().ajax.reload();
            validator.resetForm();
            $('#sect-form').find("input[type=text],input[type=number],input[type=mobileNumber],input[type=emailFull],input[type=password],input[type=hidden], textarea,select").val("");
            $("#edit-cov-sect-mand").prop("checked", false);
            $("#edit-cov-sect-integ").prop("checked", false);
            $('#editcovSectModal').modal('hide');
        });

        request.error(function (jqXHR, textStatus, errorThrown) {
            Swal.fire({
                title: 'Error',
                text: jqXHR.responseText,
                icon: 'error'
            });
        });
        request.always(function () {
            $btn.button('reset');
        });
    });
}


function confirmSubCoverSectionDel(button) {
    var section = JSON.parse(decodeURI($(button).data("sections")));
    bootbox.confirm("Are you sure want to delete " + section["subSections"].section.desc, function (result) {
        if (result) {
            $.ajax({
                type: 'GET',
                url: 'deleteCoverSection/' + section["scsId"],
                dataType: 'json',
                async: true,
                success: function (result) {
                    Swal.fire({
                        title: 'Success',
                        text: 'Record Deleted Successfully',
                        icon: 'success'
                    })

                    $('#covttypesections').DataTable().ajax.reload();
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
    });
}

function editSubCoverSection(button) {
    var section = JSON.parse(decodeURI($(button).data("sections")));
    $("#edit-scs-code-pk").val(section["scsId"]);
    $("#edit-scs-cov-pk").val(section["subcoverType"].scId);
    $("#edit-scs-sec-pk").val(section["subSections"].ssId);
    $("#edit-scs-sec-name").val(section["subSections"].section.desc);
    $("#edit-cov-sect-order").val(section["order"]);
    if (section["mandatory"])
        $("#edit-cov-sect-mand").prop("checked", section["mandatory"]);
    else
        $("#edit-cov-sect-mand").prop("checked", false);
    if (section["integration"] === "Y") {
        $("#edit-cov-sect-integ").prop("checked", true);
    } else $("#edit-cov-sect-integ").prop("checked", false);
    if (section["supportsEarnings"] === "Y") {
        $("#supports-earning").prop("checked", true);
    } else $("#supports-earning").prop("checked", false);
    $("#earning-type").val(section["earningType"]);
    $('#editcovSectModal').modal({
        backdrop: 'static',
        keyboard: true
    });


}


var model = {
    subclass: {
        subcode: ""
    },
    class: {
        classId: ""
    }
};