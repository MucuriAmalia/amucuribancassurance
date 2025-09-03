/**
 * Created by peter on 2/20/2017.
 */
$(function() {

    $(document).ready(function () {

        createSubclassLov();
        addScheduleMapping();
        saveScheduleMapping();

    })
});




function createSectionsLov(){
    if($("#sect-def").filter("div").html() != undefined)
    {
        Select2Builder.initAjaxSelect2({
            containerId : "sect-def",
            sort : 'desc',
            change: function(e, a, v){
                $("#sect-pk-code").val(e.added.id);
            },
            formatResult : function(a)
            {
                return a.desc
            },
            formatSelection : function(a)
            {
                return a.desc
            },
            initSelection: function (element, callback) {
                var sectCode = $("#sect-pk-code").val();
                var sectName = $("#sect-pk-name").val();
                var data = {desc:sectName,id:sectCode};
                callback(data);
            },
            id: "id",
            width:"200px",
            placeholder:"Select Section",
            params: {subId:$("#sub-code").val()}
        });

    }
    if($("#map-sect-def").filter("div").html() != undefined)
    {
        Select2Builder.initAjaxSelect2({
            containerId : "map-sect-def",
            sort : 'desc',
            change: function(e, a, v){
                $("#map-sect-pk-code").val(e.added.id);
            },
            formatResult : function(a)
            {
                return a.desc
            },
            formatSelection : function(a)
            {
                return a.desc
            },
            initSelection: function (element, callback) {
                var sectCode = $("#map-sect-pk-code").val();
                var sectName = $("#map-sect-pk-name").val();
                var data = {desc:sectName,id:sectCode};
                callback(data);
            },
            id: "id",
            width:"200px",
            placeholder:"Select Section",
            params: {subId:$("#sub-code").val()}
        });


        $("#map-sect-def").on("select2-removed", function(e) {
            $("#map-sect-pk-code").val('');
        })

    }
}

    function saveScheduleMapping(){
        var $classForm = $('#sched-map-form');
        var validator = $classForm.validate();
        $('#saveschedMapsBtn').click(function(){
            if (!$classForm.valid()) {
                return;
            }

            var $btn = $(this).button('Saving');
            var data = {};
            $classForm.serializeArray().map(function(x){data[x.name] = x.value;});
            var url = "createSchedMapping";
            var request = $.post(url, data );
            request.success(function(){
                Swal.fire({
                title: 'Success',
                text: 'Record created/updated Successfully',
                icon: 'success'
               });
                $('#sched-mappings-tbl').DataTable().ajax.reload(null,false);;
                validator.resetForm();
                $('#sched-map-form').find("input[type=text],input[type=number],input[type=mobileNumber],input[type=emailFull],input[type=password],input[type=hidden], textarea,select").val("");
                $('#schedMappingModal').modal('hide');
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

    function addScheduleMapping(){
        $("#btn-add-sched-map").click(function(){
            if($("#sub-code").val() != ''){
                $('#sched-map-form').find("input[type=text],input[type=number],input[type=mobileNumber],input[type=emailFull],input[type=password],input[type=hidden], textarea,select").val("");
                $("#sched-sub-det").val($("#sub-code").val());
                $("#lov-name").attr("disabled",true);
                $("#option-name").attr("disabled",true);
                $("#chk-prem-item").prop("checked", false);
                createSectionsLov();
                $('#schedMappingModal').modal({
                    backdrop: 'static',
                    keyboard: true
                })
            }
            else{
                bootbox.alert("Select Sub Class to Add Schedule Mapping");
            }

        });

        $("#column-type").on('change', function(){
            if($("#column-type").val()==="L"){
                $("#lov-name").attr("disabled",false);
                $("#option-name").attr("disabled",true);
            }
            else  if($("#column-type").val()==="O"){
                $("#lov-name").attr("disabled",true);
                $("#option-name").attr("disabled",false);
            }
            else{
                $("#lov-name").val('');
                $("#lov-name").attr("disabled",true);
                $("#option-name").attr("disabled",true);
            }
        })

    }


function deleteScheduleMapping(button){
    var schedules = JSON.parse(decodeURI($(button).data("schedules")));
    bootbox.confirm("Are you sure want to delete "+ schedules["columnName"]+"?", function(result) {
        if(result){
            $.ajax({
                type: 'GET',
                url:  'deleteSchedMapping/' + schedules["smId"],
                dataType: 'json',
                async: true,
                success: function(result) {
                   Swal.fire({
                title: 'Success',
                text: 'Record created/updated Successfully',
                icon: 'success'
            })
                    $('#sched-mappings-tbl').DataTable().ajax.reload(null,false);;
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

    function editSchedulesMapping(button){
        var schedules = JSON.parse(decodeURI($(button).data("schedules")));
        $("#sched-code").val(schedules["smId"]);
        $("#sched-sub-det").val(schedules["subclass"].subId);
        $("#sched-col-index").val(schedules["columnIndex"]);
        $("#sched-col-name").val(schedules["columnName"]);
        $("#column-type").val(schedules["columnType"]);
        $("#option-name").val(schedules["options"]);
        $("#lov-name").val(schedules["lovName"]);
        if(schedules["premItem"]){
            if(schedules["premItem"]==="Y"){
                $("#chk-prem-item").prop("checked", true);
            }
            else $("#chk-prem-item").prop("checked", false);
        }else{
            $("#chk-prem-item").prop("checked", false);
        }
        if(schedules["sections"]){
            $("#sect-pk-code").val(schedules["sections"].id);
            $("#sect-pk-name").val(schedules["sections"].desc);
        }else{
            $("#sect-pk-code").val("");
            $("#sect-pk-name").val("");
        }
        if(schedules["mappedSections"]){
            $("#map-sect-pk-code").val(schedules["mappedSections"].id);
            $("#map-sect-pk-name").val(schedules["mappedSections"].desc);
        }
        createSectionsLov();
        $("#risk-col-name").val(schedules["mappedRiskColumn"]);
        if($("#column-type").val()==="L"){
            $("#lov-name").attr("disabled",false);
            $("#option-name").attr("disabled",true);
        }
        else  if($("#column-type").val()==="O"){
            $("#lov-name").attr("disabled",true);
            $("#option-name").attr("disabled",false);
        }
        else{
            $("#lov-name").val('');
            $("#lov-name").attr("disabled",true);
            $("#option-name").attr("disabled",true);
        }
        $('#schedMappingModal').modal({
            backdrop: 'static',
            keyboard: true
        })
    }

    function createScheduleMapTbl(subCode){
        var url = "schedmapping/"+subCode;
        var currTable = $('#sched-mappings-tbl').DataTable( {
            "processing": true,
            "serverSide": true,
            autoWidth: true,
            "ajax": {
                'url': url,
            },
            lengthMenu: [ [5,10], [5,10] ],
            pageLength: 5,
            destroy: true,
            "columns": [
                { "data": "columnName" },
                { "data": "columnType" },
                { "data": "lovName" },
                { "data": "columnIndex" },
                { "data": "premItem" },
                {
                    "data": "smId",
                    "render": function ( data, type, full, meta ) {
                       if(full.sections){
                           return full.sections.desc;
                       }
                        else return "";
                    }

                },
                { "data": "mappedRiskColumn" },
                {
                    "data": "smId",
                    "render": function ( data, type, full, meta ) {
                        if(full.mappedSections){
                            return full.mappedSections.desc;
                        }
                        else return "";
                    }

                },
                {
                    "data": "smId",
                    "render": function ( data, type, full, meta ) {
                        return '<button type="button" class="btn btn-success btn btn-info btn-sm" type="button" data-schedules='+encodeURI(JSON.stringify(full)) + ' onclick="editSchedulesMapping(this);"><i class="fa fa-pencil-square-o"></button>';
                    }

                },
                {
                    "data": "smId",
                    "render": function ( data, type, full, meta ) {
                        return '<button type="button" class="btn btn-danger btn btn-danger btn-sm" data-schedules='+encodeURI(JSON.stringify(full)) + ' onclick="deleteScheduleMapping(this);"><i class="fa fa-trash-o"></button>';
                    }

                },
            ]
        } );
        return currTable;
    }



    function createSubclassLov(){
        if($("#sub-class-def").filter("div").html() != undefined)
        {
            Select2Builder.initAjaxSelect2({
                containerId : "sub-class-def",
                sort : 'subDesc',
                change: function(e, a, v){
                    $("#sub-code").val(e.added.subId);
                    createScheduleMapTbl(e.added.subId);
                },
                formatResult : function(a)
                {
                    return a.subDesc
                },
                formatSelection : function(a)
                {
                    return a.subDesc
                },
                initSelection: function (element, callback) {

                },
                id: "subId",
                width:"200px",
                placeholder:"Select Sub Class"
            });

        }
    }
