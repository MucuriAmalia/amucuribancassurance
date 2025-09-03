<%--
  Created by IntelliJ IDEA.
  User: Envy
  Date: 7/21/2024
  Time: 12:37 PM
  To change this template use File | Settings | File Templates.
--%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<script type="text/javascript" src="<c:url value="/js/modules/utils/select2builder.js"/>"></script>
<script type="text/javascript" src="<c:url value="/libs/rivets/rivets.js"/>"></script>
<div class="x_panel">
    <div class="x_title">
        <h2><i class="fa fa-bars"></i> Schedules Set Up</h2>
        <ul class="nav navbar-right panel_toolbox">
            <li><a class="collapse-link"><i class="fa fa-chevron-up"></i></a>
            </li>
        </ul>
        <div class="clearfix"></div>
    </div>
    <div class="x_content">
        <form id="prg-grp-form" class="form-horizontal">
            <div class="item form-group form-required">
                <div class="col-md-6">
                    <label for="sub-code" class="col-md-5 label-align">Select
                        Class</label>

                    <div class="col-md-7">
                        <input type="hidden" id="sub-code" name="subclassId"/>
                        <input type="hidden" id="sub-name">
                        <div id="subclass-frm" class="form-control"
                             select2-url="<c:url value="/protected/setups/selSubclasses"/>">

                        </div>

                    </div>
                </div>
                <div class="col-md-2">

                </div>

                <div class="col-md-2">

                </div>

            </div>
        </form>
    </div>
</div>
<div class="x_panel">
    <div class="x_title">
        <h2>Schedules</h2>
        <ul class="nav navbar-right panel_toolbox">
            <li><a class="collapse-link"><i class="fa fa-chevron-up"></i></a>
            </li>
        </ul>
        <div class="clearfix"></div>
    </div>
    <div class="x_content">
        <input type="hidden" id="prg-pk">
        <button type="button" class="btn btn-info" id="btn-add-sched">New</button>
        <table id="sched-table" class="table table-striped" style="width: 100%">
            <thead>
            <tr class="headings">
                <th>Schedule Name</th>
                <th>Category</th>
                <th>Schedule Key Column</th>
                <th width="5%"></th>
                <th width="5%"></th>
            </tr>
            </thead>
        </table>
    </div>
</div>


<div class="modal fade" id="newScheduleModal" tabindex="-1" role="dialog"
     aria-labelledby="sectorModalLabel" aria-hidden="true">
    <div class="modal-dialog modal-lg">
        <div class="modal-content">
            <div class="modal-header">
                <h4 class="modal-title" id="sectorModalLabel">
                    New Schedule Definitions
                </h4>
                <button type="button" class="close" data-dismiss="modal"
                        aria-label="Close">
                    <span aria-hidden="true">&times;</span>
                </button>

            </div>
            <div class="modal-body">

                <form id="schedule-form" class="form-horizontal">
                    <input type="hidden" class="form-control" id="sector-code" name="code">
                    <div class="item form-group">
                        <label for="datatableName-id" class="col-md-3 label-align">Table Name<span class="required">*</span></label>

                        <div class="col-md-8">
                            <input type="text" class="form-control" id="datatableName-id"
                                   name="datatableName"  required>
                        </div>
                    </div>
                    <div class="item form-group">
                        <label for="apptableName" class="col-md-3 label-align">System Table Name<span class="required">*</span></label>

                        <div class="col-md-8">
                            <select class="form-control" id="apptableName" name="apptableName"
                                    required>
                                <option value="">Select System Table Name</option>
                                <option value="sys_brk_risks">Risks</option>
                            </select>
                        </div>
                    </div>
                    <div class="item form-group">
                        <label for="appTableNameKey-id" class="col-md-3 label-align">Sub Class Code</label>

                        <div class="col-md-8">
                            <input type="text" class="form-control" id="appTableNameKey-id"
                                   name="appTableNameKey"  readonly>
                        </div>
                    </div>
                    <div class="item form-group">
                        <div class="col-sm-3">
                            <button type="button" id="btnaddcolumn" class="btn btn-primary"><i class="fa fa-plus "></i></button>
                        </div>
                        <div class="col-sm-9">
                        </div>
                    </div>
                    <div class="item form-group">
                        <label class="col-md-3 label-align"><h4>Columns</h4></label>
                    </div>
                    <table id="custom-tbl" class="table table-striped" style="width: 100%">
                        <thead>
                        <tr class="headings">

                            <th>Name</th>
                            <th>Type</th>
                            <th>Options</th>
                            <th>Mandatory</th>
                            <th>Length</th>
                            <th width="5%"></th>
                            <th width="5%"></th>
                        </tr>
                        </thead>
                    </table>


                </form>
            </div>
            <div class="modal-footer">
                <button data-loading-text="Saving..." id="saveSectorBtn"
                        type="button" class="btn btn-success" onclick="SCHEDULES.createSchedule()">
                    Save
                </button>
                <button data-loading-text="Saving..." id="updateSectorBtn"
                        type="button" class="btn btn-success" onclick="SCHEDULES.updateSchedule()">
                    Update
                </button>
                <button type="button" class="btn btn-default" data-dismiss="modal">
                    Cancel
                </button>
            </div>
        </div>
    </div>
</div>

<div class="modal fade bs-example-modal-sm" id="myPleaseWait" tabindex="-1"
     role="dialog" aria-hidden="true" data-backdrop="static">
    <div class="modal-dialog modal-sm">
        <div class="modal-content">
            <div class="modal-header">
                <h4 class="modal-title">
                    <span class="glyphicon glyphicon-time">
                    </span>Please Wait
                </h4>
            </div>
            <div class="modal-body">
                <div class="progress">
                    <div class="progress-bar progress-bar-info
                    progress-bar-striped active"
                         style="width: 100%">
                    </div>
                </div>
            </div>
        </div>
    </div>
</div>

<script>
    $(document).ready(function() {
        // Reset form when modal is closed
        $('#newScheduleModal').on('hidden.bs.modal', function() {
            // Reset the form fields
            $('#schedule-form')[0].reset();

            // Optionally, clear the custom table (if applicable)
            $('#custom-tbl').DataTable().clear().draw(); // If using DataTables

            // Clear any specific values or state if needed
            $('#saveSectorBtn').show();

        });

    });

    function selectColumnType(event){
        var id  = event.id;
        var index = id.substr(id.indexOf("-"),id.length);
        var optId = 'column-options'+index;
        console.log($("#"+optId));
        if(event.value==='dropdown'){
            $("#"+optId).css('display','block');
        }
        else{
            $("#"+optId).css('display','none');
        }
    }

    $(function(){
        SCHEDULES.populateSubclassLov();

        $("#btnaddcolumn").click(function () {
            var row_index = $('#custom-tbl tr').length;
            console.log(row_index);
            var markup = "<tr><td><input id='column-code-" + row_index + "' name='name' type='text' placeholder='Column Name' class='form-control'></td>" +
                "<td> <select name='type' id='columnType-" + row_index + "' class='form-control' onchange='selectColumnType(this);'>&nbsp;&nbsp; "+
                "<option class='displaynone' value=''>Select Column Type</option>"+
                "<option value='string'>String</option> "+
                "  <option value='number'>Number</option> "+
                "  <option value='decimal'>Decimal</option>"+
                "    <option value='boolean'>Boolean</option>"+
                "  <option value='date'>Date</option>"+
                "   <option value='datetime'>Datetime</option>"+
                "   <option value='text'>Text</option>"+
                "    <option value='dropdown'>Dropdown</option>"+
                "    </select></td>" +
                "<td><textarea style='display: none' rows='1' id='column-options-" + row_index + "' name='options' placeholder='Options separated by commas' class='form-control'/></td> "+
                "<td><input id='column-mandatory-" + row_index + "' name='mandatory' type='checkbox'></td>" +
                "<td><input id='column-code-" + row_index + "' name='length' type='number' placeholder='Column Length' class='form-control'></td>" +
                "<td><button type='button' class='btn btn-danger hyperlink-btn'><i class='fa fa-trash-o'></button></td></tr>";
            $("#custom-tbl").append(markup);
        });

        $("#custom-tbl").on('click','.hyperlink-btn',function() {
            $(this).closest('tr').remove();
        });

        $("#btn-add-sched").click(function(){
            $('#updateSectorBtn').hide();
            if($("#sub-code").val()!=''){
                $('#schedule-form').find("input[type=text],input[type=number],input[type=mobileNumber],input[type=emailFull],input[type=password], textarea,select").val("");
                $("#appTableNameKey-id").val( $("#sub-code").val());
                $('#newScheduleModal').modal({
                    backdrop: 'static',
                    keyboard: true
                });
            }
            else{
                Swal.fire({
                    title: 'Error',
                    text: 'Select Class to add Schedule',
                    icon: 'error'
                });

            }


        });
    });

    var SCHEDULES = {
        populateSubclassLov : function(){
            if($("#subclass-frm").filter("div").html() != undefined)
            {
                Select2Builder.initAjaxSelect2({
                    containerId : "subclass-frm",
                    sort : 'subId',
                    change: function(e, a, v){
                        $("#sub-code").val(e.added.subId);
                        SCHEDULES.initSchedule(e.added.subId);

                    },
                    formatResult : function(a)
                    {
                        return a.subDesc;
                    },
                    formatSelection : function(a)
                    {
                        return a.subDesc;
                    },
                    initSelection: function (element, callback) {
                        var code  = $('#sub-code').val();
                        var name = $("#sub-name").val();
                        var data = {subDesc:name,subId:code};
                        callback(data);
                    },
                    id: "subDesc",
                    width:"250px",
                    placeholder:"Select Classification"

                });
            }
        },
        initSchedule: function (subId) {
            $('#sched-table').DataTable({
                "processing": true,
                "serverSide": true,
                autoWidth: true,
                "ajax": {
                    'url': SERVLET_CONTEXT + '/protected/schedules/'+subId+'/schedules',
                },
                lengthMenu: [[5], [5]],
                pageLength: 5,
                destroy: true,
                dom: 'Bfrtip',
                "columns": [
                    {'data': 'tableName'},
                    {'data': 'category'},
                    {'data': 'keyValue'},
                    {
                        "data": "keyValue",
                        "render": function ( keyValue, type, full, meta ) {
                            return '<button type="button"  class="btn btn-success btn btn-info btn-sm" data-schedules='+encodeURI(JSON.stringify(full))  + '  onclick="SCHEDULES.editSchedule(this);"><i class="fa fa-pencil-square-o"></i></button>';
                        }
                    },
                    {
                        "data": "keyValue",
                        "render": function ( keyValue, type, full, meta ) {
                            console.log(full)
                            return '<button type="button"  class="btn btn-danger btn btn-danger btn-sm" data-schedules='+encodeURI(JSON.stringify(full))  + '  onclick="SCHEDULES.deleteSchedule(this);"><i class="fa fa-trash-o"></i></button>';
                        }

                    },
                ]
            });
        },
        createSchedule:function () {
            var arr = [];
            $('#custom-tbl > tbody  > tr').each(
                function() {
                    var data = {};
                    $(this).find(":input[type='text'],:input[type='number'],:input[type='checkbox'],select,textarea").serializeArray()
                        .map(function(x) {
                            data[x.name] = x.value;
                        });
                    arr.push(data);
                });
            var $form = $('#schedule-form');
            var data = {};
            $form.serializeArray().map(function(x){data[x.name] = x.value;});
            data.columnFormList = arr;
            jQuery.ajax ({
                url:  SERVLET_CONTEXT + '/protected/schedules/createSchedule',
                type: "POST",
                data: JSON.stringify(data),
                dataType: "json",
                contentType: "application/json; charset=utf-8",
                success: function(data){
                    // $('#myPleaseWait').modal('hide');
                    $('#sched-table').DataTable().ajax.reload();
                    $('#newScheduleModal').modal('hide');
                },
                error: function(jqXHR, textStatus, errorThrown){
                    // $('#myPleaseWait').modal('hide');
                    Swal.fire({
                title: 'Error',
                text: jqXHR.responseText,
                icon: 'error'
            });

                },
                beforeSend: function(){
                    // // $('#myPleaseWait').modal({
                    //     backdrop: 'static',
                    //     keyboard: true
                    // });
                }

            });
        },

       editSchedule: function (button) {
            $('#saveSectorBtn').hide();
            $('#updateSectorBtn').show();
            var schedules = JSON.parse(decodeURI($(button).data("schedules")));
            $("#datatableName-id").val(schedules["tableName"]);
            $("#appTableNameKey-id").val(schedules["keyValue"]);

            // Call your backend to get the existing columns
            $.ajax({
                url: SERVLET_CONTEXT + '/protected/schedules/getTableColumns', // Adjust the URL according to your API
                type: 'GET',
                data: { tableName: schedules["tableName"] },
                success: function(columns) {
                    $("#custom-tbl").find("tr:gt(0)").remove(); // Clear existing rows
                    // Populate the table with existing columns
                    columns.forEach(function(column, index) {
                        var isDisabled = (column.name === 's_brk_risks_id' || column.name === 'pri_code') ? 'disabled' : '';
                        var isReadOnly = (column.name === 's_brk_risks_id' || column.name === 'pri_code') ? 'readonly' : '';
                        var deleteButton = (column.name === 's_brk_risks_id' || column.name === 'pri_code') ? '' : "<button type='button' class='btn btn-danger hyperlink-btn'><i class='fa fa-trash-o'></button>";

                        var markup = "<tr><td><input id='column-code-" + index + "' name='name' type='text' value='" + column.name + "' placeholder='Column Name' class='form-control' " + isReadOnly + "></td>" +
                            "<td><select name='type' id='columnType-" + index + "' class='form-control' " + isDisabled + ">" +
                            "<option value='String'" + (column.type === 'String' ? ' selected' : '') + ">String</option>" +
                            "<option value='Number'" + (column.type === 'Number' ? ' selected' : '') + ">Number</option>" +
                            "<option value='Decimal'" + (column.type === 'Decimal' ? ' selected' : '') + ">Decimal</option>" +
                            "<option value='Boolean'" + (column.type === 'Boolean' ? ' selected' : '') + ">Boolean</option>" +
                            "<option value='Date'" + (column.type === 'Date' ? ' selected' : '') + ">Date</option>" +
                            "<option value='Text'" + (column.type === 'Text' ? ' selected' : '') + ">Text</option>" +
                            "<option value='dropdown'>Dropdown</option>" + // Add logic for dropdown handling
                            "</select></td>" +
                            "<td><textarea style='display: none' rows='1' id='column-options-" + index + "' name='options' placeholder='Options separated by commas' class='form-control'/></td> " +
                            "<td><input id='column-mandatory-" + index + "' name='mandatory' type='checkbox'" + (column.mandatory === "true" ? ' checked' : '') + " " + isDisabled + "></td>" +
                            "<td><input id='column-length-" + index + "' name='length' type='number' value='" + (column.length || '') + "' placeholder='Column Length' class='form-control' " + isDisabled + "></td>" +
                            "<td>" + deleteButton + "</td></tr>";

                        $("#custom-tbl").append(markup);
                    });
                }
            });
            $('#newScheduleModal').modal('show');
        },


        updateSchedule: function() {
            var tableForm = {
                datatableName: $('#datatableName-id').val(),
                apptableName: $('#apptableName').val(),
                columnFormList: []
            };

            $('#custom-tbl tbody tr').each(function() {
                var column = {
                    name: $(this).find('input[name="name"]').val(), // Get the column name
                    type: $(this).find('select[name="type"]').val(), // Get the column type
                    options: $(this).find('textarea[name="options"]').val(), // Get the options for dropdown if any
                    mandatory: $(this).find('input[name="mandatory"]').is(':checked'), // Check if column is mandatory
                    length: $(this).find('input[name="length"]').val(), // Get the column length
                    action: 'modify' // Assuming the action is always modify for the update
                };

                tableForm.columnFormList.push(column);
                console.log(column); // Debugging log
            });

            $.ajax({
                type: "POST",
                url: SERVLET_CONTEXT + '/protected/schedules/updateSchedule',
                contentType: "application/json",
                data: JSON.stringify(tableForm),
                success: function(response) {
                    $('#newScheduleModal').modal('hide');
                },
                error: function(xhr) {
                    alert("Error: " + xhr.responseText);
                }
            });
        }

    }

</script>