<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>


<div class="x_panel">
    <div class="x_title">
        <h4>Questionnaire</h4>
    </div>
    <div id="surveyElement">
    </div>

    <div id="surveyResult">

    </div>
</div>


<script type="text/javascript" src="https://cdnjs.cloudflare.com/ajax/libs/survey-jquery/1.0.81/survey.jquery.js"></script>

<script type="application/javascript">

    function init() {

        $.ajax({
            type: 'GET',
            url: 'allBinderQuestions',
            dataType: 'json',
            async: true,
            success: function (result) {
                getObject(result);
            },
            error: function (jqXHR, textStatus, errorThrown) {
                console.log(jqXHR);
                Swal.fire({
                title: 'Error',
                text: jqXHR.responseText,
                icon: 'error'
            });
            }
        });

         function getObject(quiz){
             var json = {
                 title: "Questionnaire",
                 showProgressBar: "top",
                 pages: [
                     {
                         questions: quiz
                     }
                 ]
             };
             console.log(json);
             window.survey = new Survey.Model(json);
             survey.onComplete.add(function(result) {
                 document.querySelector("#surveyResult").innerHTML =
                     "result: " + JSON.stringify(result.data);
             });

             $("#surveyElement").Survey({
                 model: survey
             });
         }



    }

    if (!window["%hammerhead%"]) {
        init();
    }

</script>