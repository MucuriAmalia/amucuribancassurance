<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<script type="text/javascript" src="<c:url value="/js/modules/reconciliation/sfi/sfi.js"/>"></script>

<div class="x_panel">
    <div role="tabpanel" data-example-id="togglable-tabs">
        <ul id="myTab" class="nav nav-tabs bar_tabs" role="tablist">
            <li role="presentation" class="active">
                <a href="#tab_content1" id="debit-tab" role="tab" data-toggle="tab" aria-expanded="true">PCI-Debit Card</a>
            </li>
            <li role="presentation">
                <a href="#tab_content2" role="tab" id="credit-tab" data-toggle="tab" aria-expanded="false">PCI-Credit Card</a>
            </li>
            <li role="presentation">
                <a href="#tab_content3" role="tab" id="macra-tab" data-toggle="tab" aria-expanded="false">PCI-Macra</a>
            </li>
            <li role="presentation">
                <a href="#tab_content4" role="tab" id="mortgages-tab" data-toggle="tab" aria-expanded="false">Mortgages</a>
            </li>
            <li role="presentation">
                <a href="#tab_content5" role="tab" id="raw-file-tab" data-toggle="tab" aria-expanded="false">SFI-Raw File</a>
            </li>
        </ul>
        <div id="myTabContent" class="tab-content">
            <!-- PCI-Debit Card tab -->
            <div role="tabpanel" class="tab-pane fade active in" id="tab_content1" aria-labelledby="debit-tab">
                <div class="x_title">
                    <h2><i class="fa fa-bars"></i> PCI-Debit Card Details</h2>
                    <div class="clearfix"></div>
                </div>
                <div class="table-responsive">
                    <table id="debitTable" class="table table-striped table-bordered">
                        <thead>
                        <tr id="debitTableHead">
                            <!-- Dynamic headers -->
                        </tr>
                        </thead>
                        <tbody id="debitTableBody">
                            <!-- Dynamic data rows -->
                        </tbody>
                    </table>
                </div>
            </div>

            <!-- PCI-Credit Card tab -->
            <div role="tabpanel" class="tab-pane fade" id="tab_content2" aria-labelledby="credit-tab">
                <div class="x_title">
                    <h2><i class="fa fa-bars"></i> PCI-Credit Card Details</h2>
                    <div class="clearfix"></div>
                </div>
                <div class="table-responsive">
                    <table id="creditTable" class="table table-striped table-bordered">
                        <thead>
                        <tr id="creditTableHead">
                            <!-- Dynamic headers -->
                        </tr>
                        </thead>
                        <tbody id="creditTableBody">
                            <!-- Dynamic data rows -->
                        </tbody>
                    </table>
                </div>
            </div>

            <!-- PCI-Macra tab -->
            <div role="tabpanel" class="tab-pane fade" id="tab_content3" aria-labelledby="macra-tab">
                <div class="x_title">
                    <h2><i class="fa fa-bars"></i> PCI-Macra Details</h2>
                    <div class="clearfix"></div>
                </div>
                <div class="table-responsive">
                    <table id="macraTable" class="table table-striped table-bordered">
                        <thead>
                        <tr id="macraTableHead">
                            <!-- Dynamic headers -->
                        </tr>
                        </thead>
                        <tbody id="macraTableBody">
                            <!-- Dynamic data rows -->
                        </tbody>
                    </table>
                </div>
            </div>

            <!-- Mortgages tab -->
            <div role="tabpanel" class="tab-pane fade" id="tab_content4" aria-labelledby="mortgages-tab">
                <div class="x_title">
                    <h2><i class="fa fa-bars"></i> Mortgages Details</h2>
                    <div class="clearfix"></div>
                </div>
                <div class="table-responsive">
                    <table id="mortgagesTable" class="table table-striped table-bordered">
                        <thead>
                        <tr id="mortgagesTableHead">
                            <!-- Dynamic headers -->
                        </tr>
                        </thead>
                        <tbody id="mortgagesTableBody">
                            <!-- Dynamic data rows -->
                        </tbody>
                    </table>
                </div>
            </div>

            <!-- SFI-Raw File tab -->
            <div role="tabpanel" class="tab-pane fade" id="tab_content5" aria-labelledby="raw-file-tab">
                <div class="x_title">
                    <h2><i class="fa fa-bars"></i> SFI-Raw File Details</h2>
                    <div class="clearfix"></div>
                </div>
                <div class="table-responsive">
                    <table id="rawFileTable" class="table table-striped table-bordered">
                        <thead>
                        <tr id="rawFileTableHead">
                            <!-- Dynamic headers -->
                        </tr>
                        </thead>
                        <tbody id="rawFileTableBody">
                            <!-- Dynamic data rows -->
                        </tbody>
                    </table>
                </div>
            </div>
        </div>
    </div>
</div>
