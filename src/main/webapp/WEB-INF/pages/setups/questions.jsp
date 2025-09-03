<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<div class="x_panel">
    <div class="x_title">
        <h4>Questions Definition</h4>
    </div>

    <button type="button" class="btn btn-success btn btn-info float-right" data-toggle="modal"
            data-target="#quizModal">New</button>
    <table id="questionList" class="table" style="width:100%">
        <thead>
        <tr>

            <th width="30%">Question Name</th>
            <th width="30%">Question Type</th>
            <th width="5%"></th>
            <th width="5%"></th>
        </tr>
        </thead>
    </table>
    <div class="x_title">
        <h4>Question Choices</h4>
    </div>

    <button type="button" class="btn btn-success btn btn-info float-right" id="btn-add-question-choice">New</button>
    <input type="hidden" id="choice-quiz-pk">
    <table id="questionChoiceList" class="table" style="width:100%">
        <thead>
        <tr>

            <th width="80%">Choice</th>
            <th width="5%"></th>
            <th width="5%"></th>
        </tr>
        </thead>
    </table>
</div>

<div class="modal fade" id="quizModal" tabindex="-1" role="dialog"
     aria-labelledby="quizModalLabel" aria-hidden="true">
    <div class="modal-dialog">
        <div class="modal-content">
            <div class="modal-header">
                <button type="button" class="close" data-dismiss="modal"
                        aria-label="Close">
                    <span aria-hidden="true">&times;</span>
                </button>
                <h4 class="modal-title" id="quizModalLabel">
                    Edit/Add Questions
                </h4>
            </div>
            <div class="modal-body" id="branch_model">
                <form id="quiz-form" class="form-horizontal">
                    <input type="hidden" class="form-control" id="pk-code" name="bqdid">
                    <div class="item form-group">
                        <label for="brn-id" class="col-md-3 label-align">Question Name</label>

                        <div class="col-md-8">
                            <textarea  rows="4" class="form-control" id="quiz-name"
                                      name="questionname"  required></textarea>
                        </div>
                    </div>
                    <div class="item form-group">
                        <label for="brn-id" class="col-md-3 label-align">Choice Type</label>

                        <div class="col-md-8">
                            <select class="form-control" id="quiz-type" name="questiontype" required>
                                <option value="">Select Choice Type</option>
                                <option value="text">Text</option>
                                <option value="radiogroup">Radio Group</option>
                                <option value="checkbox">Checkbox </option>
                                <option value="dropdown">Drop Down </option>
                            </select>
                        </div>
                    </div>


                </form>
            </div>
            <div class="modal-footer">
                <button data-loading-text="Saving..." id="saveQuestion"
                        type="button" class="btn btn-success">
                    Save
                </button>
                <button type="button" class="btn btn-default" data-dismiss="modal">
                    Cancel
                </button>
            </div>
        </div>
    </div>
</div>

<div class="modal fade" id="quizChoiceModal" tabindex="-1" role="dialog"
     aria-labelledby="quizChoiceModalLabel" aria-hidden="true">
    <div class="modal-dialog">
        <div class="modal-content">
            <div class="modal-header">
                <button type="button" class="close" data-dismiss="modal"
                        aria-label="Close">
                    <span aria-hidden="true">&times;</span>
                </button>
                <h4 class="modal-title" id="quizChoiceModalLabel">
                    Edit/Add Question Choices
                </h4>
            </div>
            <div class="modal-body">
                <form id="quiz-choice-form" class="form-horizontal">
                    <input type="hidden" class="form-control" id="choice-pk-code" name="qcId">
                    <input type="hidden" class="form-control" id="choice-quiz-code" name="questions">
                    <div class="item form-group">
                        <label for="brn-id" class="col-md-3 label-align">Choice </label>

                        <div class="col-md-8">
                            <textarea  rows="4" class="form-control" id="choice-name"
                                       name="choice"  required></textarea>
                        </div>
                    </div>
                </form>
            </div>
            <div class="modal-footer">
                <button data-loading-text="Saving..." id="saveQuestionChoice"
                        type="button" class="btn btn-success">
                    Save
                </button>
                <button type="button" class="btn btn-default" data-dismiss="modal">
                    Cancel
                </button>
            </div>
        </div>
    </div>
</div>

<script type="application/javascript">
    $(document).ready(function () {

        $('#questionList').DataTable( {
            "processing": true,
            "serverSide": true,
            "ajax": 'questions',
            lengthMenu: [ [10, 15], [10, 15] ],
            pageLength: 10,
            destroy: true,
            "columns": [
                { "data": "questionname" },
                { "data": "questiontype" },
                {
                    "data": "bqdid",
                    "render": function ( data, type, full, meta ) {
                        return '<button type="button" class="btn btn-success btn btn-info btn-sm" data-clienttitle='+encodeURI(JSON.stringify(full)) + ' onclick="editQuestion(this);"><i class="fa fa-pencil-square-o"></button>';
                    }

                },
                {
                    "data": "bqdid",
                    "render": function ( data, type, full, meta ) {
                        return '<button type="button" class="btn btn-danger btn btn-danger btn-sm" data-clienttitle='+encodeURI(JSON.stringify(full)) + ' onclick="confirmClientTitleDel(this);"><i class="fa fa-trash-o"></button>';
                    }

                },
            ]
        } );

        makeQuizSelection();

        function showChoices(quizId){
            $('#questionChoiceList').DataTable( {
                "processing": true,
                "serverSide": true,
                "ajax": 'questions/'+quizId,
                lengthMenu: [ [10, 15], [10, 15] ],
                pageLength: 10,
                destroy: true,
                "columns": [
                    { "data": "choice" },
                    {
                        "data": "qcId",
                        "render": function ( data, type, full, meta ) {
                            return '<button type="button" class="btn btn-success btn btn-info btn-sm" data-clienttitle='+encodeURI(JSON.stringify(full)) + ' onclick="editQuestionChoice(this);"><i class="fa fa-pencil-square-o"></button>';
                        }

                    },
                    {
                        "data": "qcId",
                        "render": function ( data, type, full, meta ) {
                            return '<button type="button" class="btn btn-danger btn btn-danger btn-sm" data-clienttitle='+encodeURI(JSON.stringify(full)) + ' onclick="confirmClientTitleDel(this);"><i class="fa fa-trash-o"></button>';
                        }

                    },
                ]
            } );
        }

        function makeQuizSelection(){
            var table = $('#questionList').DataTable();
            $('#questionList tbody').on( 'click', 'tr', function () {
                $(this).addClass('table-primary').siblings().removeClass('table-primary');
                var aData = table.rows('.table-primary').data();
                if(aData) {
                    showChoices(aData[0].bqdid);
                    $("#choice-quiz-pk").val(aData[0].bqdid);
                }
            } );
        }

        function editQuestion(data){
            console.log(data);
        }

        var $quizform = $('#quiz-form');
        $('#saveQuestion').click(function(){
            if (!$quizform.valid()) {
                return;
            }
            var validator = $quizform.validate();
           // var $btn = $(this).button('Saving');
            var data = {};
            $quizform.serializeArray().map(function(x){data[x.name] = x.value;});
            var url = "createQuestions";
            var request = $.post(url, data );
            request.success(function(){
                Swal.fire({
                title: 'Success',
                text: 'Record created/updated Successfully',
                icon: 'success'
             });
                $('#questionList').DataTable().ajax.reload();
                validator.resetForm();
                $('#quiz-form').find("input[type=text],input[type=mobileNumber],input[type=emailFull],input[type=password],input[type=hidden], textarea,select").val("");
                $('#quizModal').modal('hide');
            });
            request.error(function(jqXHR, textStatus, errorThrown){
                Swal.fire({
                title: 'Error',
                text: jqXHR.responseText,
                icon: 'error'
            });
            });
            request.always(function(){
              //  $btn.button('reset');
            });
        });

        $("#btn-add-question-choice").on('click', function () {
            $("#choice-quiz-code").val($("#choice-quiz-pk").val());
            $('#quizChoiceModal').modal('show');
        });


        var $quizchoiceform = $('#quiz-choice-form');
        $('#saveQuestionChoice').click(function(){
            if (!$quizchoiceform.valid()) {
                return;
            }
            var validator = $quizchoiceform.validate();
           // var $btn = $(this).button('Saving');
            var data = {};
            $quizchoiceform.serializeArray().map(function(x){data[x.name] = x.value;});
            var url = "createQuestionsChoices";
            var request = $.post(url, data );
            request.success(function(){
                Swal.fire({
                title: 'Success',
                text: 'Record created/updated Successfully',
                icon: 'success'
            });
                $('#questionChoiceList').DataTable().ajax.reload();
                validator.resetForm();
                $('#quiz-choice-form').find("input[type=text],input[type=mobileNumber],input[type=emailFull],input[type=password],input[type=hidden], textarea,select").val("");
                $('#quizChoiceModal').modal('hide');
            });
            request.error(function(jqXHR, textStatus, errorThrown){
                Swal.fire({
                title: 'Error',
                text: jqXHR.responseText,
                icon: 'error'
            });
            });
            request.always(function(){
               // $btn.button('reset');
            });
        });

    })
</script>