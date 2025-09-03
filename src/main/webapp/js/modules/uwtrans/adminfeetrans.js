/**
 * Created by peter on 6/7/2017.
 */
$(function() {

    $(document).ready(function () {
        $(".datepicker-input").each(function() {
            $(this).datetimepicker({
                format: 'DD/MM/YYYY'
            });

        });
        populateClientLov();
        populateCurrencyLov();
        populateUserBranches();

    });
});


function populateUserBranches(){
    if($("#brn-frm").filter("div").html() != undefined)
    {
        Select2Builder.initAjaxSelect2({
            containerId : "brn-frm",
            sort : 'obName',
            change: function(e, a, v){
                $("#branch-id").val(e.added.obId);
            },
            formatResult : function(a)
            {
                return a.obName;
            },
            formatSelection : function(a)
            {
                return a.obName;
            },
            initSelection: function (element, callback) {
                //var code  = $('#brn-id').val();
                //var name = $("#brn-name").val();
                //var data = {obName:name,obId:code};
                //callback(data);
            },
            id: "obId",
            width:"250px",
            placeholder:"Select Branch"

        });
    }
}


function populateClientLov(){
    if($("#client-frm").filter("div").html() != undefined)
    {
        Select2Builder.initAjaxSelect2({
            containerId : "client-frm",
            sort : 'fname',
            change: function(e, a, v){
                $("#client-id").val(e.added.tenId);
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
            placeholder:"Select Client"

        });
    }
}

function populateCurrencyLov(){
    if($("#curr-frm").filter("div").html() != undefined)
    {
        Select2Builder.initAjaxSelect2({
            containerId : "curr-frm",
            sort : 'curName',
            change: function(e, a, v){
                $("#currency-id").val(e.added.curCode);

            },
            formatResult : function(a)
            {
                return a.curName;
            },
            formatSelection : function(a)
            {
                return a.curName;
            },
            initSelection: function (element, callback) {
                var code  = $('#cur-id').val();
                var name = $("#cur-name").val();
                var data = {curName:name,curCode:code};
                callback(data);
            },
            id: "curCode",
            width:"250px",
            placeholder:"Select Currency"

        });
    }
}
