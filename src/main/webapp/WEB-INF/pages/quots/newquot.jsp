<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="sec" uri="http://www.springframework.org/security/tags" %>
<script type="text/javascript" src="<c:url value="/js/modules/utils/select2builder.js"/>"></script>
<script type="text/javascript" src="<c:url value="/libs/rivets/rivets.js"/>"></script>
<script type="text/javascript" src="<c:url value="/js/modules/clients/prospectsHelper.js"/>"></script>
<script type="text/javascript" src="<c:url value="/js/modules/quotes/quottrans.js"/>"></script>

<div class="x_panel">
    <div class="x_title">
        <h4 id="wkflow-task-name">New Quote Details</h4>

        <ul class="nav navbar-right panel_toolbox">
            <li><a class="collapse-link"><i class="fa fa-chevron-up"></i></a>
            </li>
        </ul>
    </div>

    <div class="x_content">
        <div id="smartwizard" dir="rtl-">
            <ul class="nav nav-progress">
                <li class="nav-item">
                    <a class="nav-link" href="#step-1">
                        <div class="num">1</div>
                        General Details
                    </a>
                </li>
                <li class="nav-item">
                    <a class="nav-link" href="#step-2">
                        <span class="num">2</span>
                        Products
                    </a>
                </li>
                <li class="nav-item">
                    <a class="nav-link" href="#step-3">
                        <span class="num">3</span>
                        Insured Items
                    </a>
                </li>
                <li class="nav-item">
                    <a class="nav-link " href="#step-4">
                        <span class="num">4</span>
                        Confirm Order
                    </a>
                </li>
            </ul>

            <div class="tab-content">
                <div id="step-1" class="tab-pane" role="tabpanel" aria-labelledby="step-1">

                    <form id="quot-form" class="form-horizontal form-label-left">
                        <div class="item form-group form-required">
                            <div class="col-md-6 col-xs-12">
                                <label for="clnt-type" class="label-align col-md-5">
                                    Client Type<span class="required">*</span></label>
                                <div class="col-md-7 col-xs-12">
                                    <select class="form-control" id="clnt-type" name="clientType" required>
                                        <option value="">Select Client Type</option>
                                        <option value="P">Prospect</option>
                                        <option value="C">Client</option>
                                    </select>
                                </div>
                            </div>
                            <div class="col-md-6 col-xs-12">

                            </div>

                        </div>
                        <div class="item form-group form-required">
                            <div class="col-md-6 col-xs-12">
                                <label for="curr-frm" class="label-align col-md-5">
                                    Currency<span class="required">*</span></label>
                                <div class="col-md-7 col-xs-12">
                                    <div id="edit-currency">
                                        <input type="hidden" id="cur-id" name="currencyId"/>
                                        <input type="hidden" id="cur-name">
                                        <div id="curr-frm" class="form-control"
                                             select2-url="<c:url value="/protected/uw/policies/uwcurrencies"/>" >

                                        </div>
                                    </div>
                                    <div id="display-currency">
                                        <p class="form-control-static" id="currency-info"> </p>
                                    </div>
                                </div>
                            </div>
                            <div class="col-md-6 col-xs-12">
                                <label for="client-frm" class="label-align col-md-5 quot-client">
                                    Client<span class="required">*</span></label>
                                <div class="col-md-6">
                                    <div id="edit-client">
                                        <input type="hidden" id="client-id" name="clientId"/>
                                        <input type="hidden" id="client-f-name">
                                        <input type="hidden" id="client-other-name">
                                        <div id="client-div">
                                            <div id="client-frm" class="form-control" style="display: none"
                                                 select2-url="<c:url value="/protected/uw/policies/uwClients"/>" >

                                            </div>

                                            <a
                                                    href="<c:url value="/protected/clients/setups/clientsform?type=quot"/>" id="btn-add-client"  class="btn-sm btn-success btn-info" style="display: none">New</a>
                                        </div>
                                        <div id="prs-div">
                                            <div id="prospects-frm" class="form-control" style="display: none"
                                                 select2-url="<c:url value="/protected/quotes/selprospects"/>" >

                                            </div>

                                        </div>
                                    </div>
                                    <div id="display-client">
                                        <p class="form-control-static" id="client-info"> </p>
                                    </div>
                                </div>
                                <div class="col-md-1">
                                    <input type="button" class="btn-sm btn-success btn-info" id="btn-add-prs" value="New" style="display: none">
                                </div>
                            </div>

                        </div>
                    </form>
                </div>
                <div id="step-2" class="tab-pane" role="tabpanel" aria-labelledby="step-2">
                    <form id="form-2" class="row row-cols-1 ms-5 me-5 needs-validation" novalidate>
                        <div class="col-md-6">
                            <label for="validationCustom04" class="form-label">Product</label>
                            <select class="form-select" id="sel-products" multiple required>
                                <option value="Apple iPhone 13">Apple iPhone 13</option>
                                <option value="Apple iPhone 12">Apple iPhone 12</option>
                                <option value="Samsung Galaxy S10">Samsung Galaxy S10</option>
                                <option value="Motorola G5">Motorola G5</option>
                            </select>
                            <div class="valid-feedback">
                                Looks good!
                            </div>
                            <div class="invalid-feedback">
                                Please select product.
                            </div>
                        </div>
                    </form>
                </div>
                <div id="step-3" class="tab-pane" role="tabpanel" aria-labelledby="step-3">
                    <form id="form-3" class="row row-cols-1 ms-5 me-5 needs-validation" novalidate>
                        <div class="col">
                            <label for="address" class="form-label">Address</label>
                            <input type="text" class="form-control" id="address" placeholder="1234 Main St" required="">
                            <div class="invalid-feedback">
                                Please enter your shipping address.
                            </div>
                        </div>
                        <div class="col">
                            <label for="validationCustom04" class="form-label">State</label>
                            <select class="form-select" id="state" required>
                                <option selected disabled value="">Choose...</option>
                                <option>State 1</option>
                                <option>State 2</option>
                                <option>State 3</option>
                            </select>
                            <div class="valid-feedback">
                                Looks good!
                            </div>
                            <div class="invalid-feedback">
                                Please select a valid state.
                            </div>
                        </div>
                        <div class="col">
                            <label for="validationCustom05" class="form-label">Zip</label>
                            <input type="text" class="form-control" id="zip" required>
                            <div class="invalid-feedback">
                                Please provide a valid zip.
                            </div>
                        </div>
                    </form>
                </div>
                <div id="step-4" class="tab-pane" role="tabpanel" aria-labelledby="step-4">

                    <form id="form-4" class="row row-cols-1 ms-5 me-5 needs-validation" novalidate>
                        <div class="col">
                            <div class="mb-3 text-muted">Please confirm your order details</div>

                            <div id="order-details"></div>

                            <h4 class="mt-3">Payment</h4>
                            <hr class="my-2">

                            <div class="row gy-3">
                                <div class="col-md-3">
                                    <label for="cc-name" class="form-label">Name on card</label>
                                    <input type="text" class="form-control" id="cc-name" value="My Name" placeholder="" required="">
                                    <small class="text-muted">Full name as displayed on card</small>
                                    <div class="invalid-feedback">
                                        Name on card is required
                                    </div>
                                </div>

                                <div class="col-md-3">
                                    <label for="cc-number" class="form-label">Credit card number</label>
                                    <input type="text" class="form-control" id="cc-number" value="54545454545454" placeholder="" required="">
                                    <div class="invalid-feedback">
                                        Credit card number is required
                                    </div>
                                </div>

                                <div class="col-md-3">
                                    <label for="cc-expiration" class="form-label">Expiration</label>
                                    <input type="text" class="form-control" id="cc-expiration" value="1/28" placeholder="" required="">
                                    <div class="invalid-feedback">
                                        Expiration date required
                                    </div>
                                </div>

                                <div class="col-md-3">
                                    <label for="cc-cvv" class="form-label">CVV</label>
                                    <input type="text" class="form-control" id="cc-cvv" value="123" placeholder="" required="">
                                    <div class="invalid-feedback">
                                        Security code required
                                    </div>
                                </div>

                                <div class="col">
                                    <input type="checkbox" class="form-check-input" id="save-info" required>
                                    <label class="form-check-label" for="save-info">I agree to the terms and conditions</label>
                                </div>

                                <small class="text-muted">This is an example page, do not enter any real data, even tho we don't submit this information!</small>

                            </div>
                        </div>
                    </form>



                </div>
            </div>

            <div class="progress">
                <div class="progress-bar" role="progressbar" style="width: 25%" aria-valuenow="25" aria-valuemin="0" aria-valuemax="100"></div>
            </div>
        </div>

    </div>
</div>
<script>
    //const myModal = new bootstrap.Modal(document.getElementById('confirmModal'));


    function onCancel() {
        // Reset wizard
        $('#smartwizard').smartWizard("reset");

        // Reset form
        document.getElementById("form-1").reset();
        document.getElementById("form-2").reset();
        document.getElementById("form-3").reset();
        document.getElementById("form-4").reset();
    }

    function onConfirm() {
        let form = document.getElementById('form-4');
        if (form) {
            if (!form.checkValidity()) {
                form.classList.add('was-validated');
                $('#smartwizard').smartWizard("setState", [3], 'error');
                $("#smartwizard").smartWizard('fixHeight');
                return false;
            }

           // myModal.show();
        }
    }

    function closeModal() {
        // Reset wizard
        $('#smartwizard').smartWizard("reset");

        // Reset form
        document.getElementById("form-1").reset();
        document.getElementById("form-2").reset();
        document.getElementById("form-3").reset();
        document.getElementById("form-4").reset();

        //myModal.hide();
    }
    function showConfirm() {
        const name = $('#first-name').val() + ' ' + $('#last-name').val();
        const products = $('#sel-products').val();
        const shipping = $('#address').val() + ' ' + $('#state').val() + ' ' + $('#zip').val();
        let html = `<h4 class="mb-3-">Customer Details</h4>
                  <hr class="my-2">
                  <div class="row g-3 align-items-center">
                    <div class="col-auto">
                      <label class="col-form-label">Name</label>
                    </div>
                    <div class="col-auto">
                      <span class="form-text-">${name}</span>
                    </div>
                  </div>

                  <h4 class="mt-3">Products</h4>
                  <hr class="my-2">
                  <div class="row g-3 align-items-center">
                    <div class="col-auto">
                      <span class="form-text-">${products}</span>
                    </div>
                  </div>

                  <h4 class="mt-3">Shipping</h4>
                  <hr class="my-2">
                  <div class="row g-3 align-items-center">
                    <div class="col-auto">
                      <span class="form-text-">${shipping}</span>
                    </div>
                  </div>`;
        $("#order-details").html(html);
        $('#smartwizard').smartWizard("fixHeight");
    }
    $(function() {
        // Leave step event is used for validating the forms
        $("#smartwizard").on("leaveStep", function(e, anchorObject, currentStepIdx, nextStepIdx, stepDirection) {
            // Validate only on forward movement
            if (stepDirection == 'forward') {
                let form = document.getElementById('form-' + (currentStepIdx + 1));
                if (form) {
                    if (!form.checkValidity()) {
                        form.classList.add('was-validated');
                        $('#smartwizard').smartWizard("setState", [currentStepIdx], 'error');
                        $("#smartwizard").smartWizard('fixHeight');
                        return false;
                    }
                    $('#smartwizard').smartWizard("unsetState", [currentStepIdx], 'error');
                }
            }
        });

        // Step show event
        $("#smartwizard").on("showStep", function(e, anchorObject, stepIndex, stepDirection, stepPosition) {
            $("#prev-btn").removeClass('disabled').prop('disabled', false);
            $("#next-btn").removeClass('disabled').prop('disabled', false);
            if(stepPosition === 'first') {
                $("#prev-btn").addClass('disabled').prop('disabled', true);
            } else if(stepPosition === 'last') {
                $("#next-btn").addClass('disabled').prop('disabled', true);
            } else {
                $("#prev-btn").removeClass('disabled').prop('disabled', false);
                $("#next-btn").removeClass('disabled').prop('disabled', false);
            }

            // Get step info from Smart Wizard
            let stepInfo = $('#smartwizard').smartWizard("getStepInfo");
            $("#sw-current-step").text(stepInfo.currentStep + 1);
            $("#sw-total-step").text(stepInfo.totalSteps);

            if (stepPosition == 'last') {
                showConfirm();
                $("#btnFinish").prop('disabled', false);
            } else {
                $("#btnFinish").prop('disabled', true);
            }

            // Focus first name
            if (stepIndex == 1) {
                setTimeout(() => {
                    $('#first-name').focus();
                }, 0);
            }
        });

        // Smart Wizard
        $('#smartwizard').smartWizard({
            selected: 0,
            // autoAdjustHeight: false,
            theme: 'arrows', // basic, arrows, square, round, dots
            transition: {
                animation:'css'
            },
            toolbar: {
                showNextButton: true, // show/hide a Next button
                showPreviousButton: true, // show/hide a Previous button
                position: 'bottom', // none/ top/ both bottom
                extraHtml: `<button class="btn btn-success" id="btnFinish" disabled onclick="onConfirm()">Complete Order</button>
                              <button class="btn btn-danger" id="btnCancel" onclick="onCancel()">Cancel</button>`
            },
            anchor: {
                enableNavigation: true, // Enable/Disable anchor navigation
                enableNavigationAlways: false, // Activates all anchors clickable always
                enableDoneState: true, // Add done state on visited steps
                markPreviousStepsAsDone: true, // When a step selected by url hash, all previous steps are marked done
                unDoneOnBackNavigation: true, // While navigate back, done state will be cleared
                enableDoneStateNavigation: true // Enable/Disable the done state navigation
            },
        });

        $("#state_selector").on("change", function() {
            $('#smartwizard').smartWizard("setState", [$('#step_to_style').val()], $(this).val(), !$('#is_reset').prop("checked"));
            return true;
        });

        $("#style_selector").on("change", function() {
            $('#smartwizard').smartWizard("setStyle", [$('#step_to_style').val()], $(this).val(), !$('#is_reset').prop("checked"));
            return true;
        });

    });
</script>