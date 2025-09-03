<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="sec" uri="http://www.springframework.org/security/tags" %>
<div class="x_panel">
    <div class="x_title">
        <h2><i class="fa fa-bars"></i> Finance Setups</h2>
        <div class="clearfix"></div>
    </div>
    <div class="x_content">
<div class="panel panel-default">
    <div class="panel-body">
        <div class="row">
            <div class="col-md-6 col-sm-6 col-xs-12">
                <ul style="list-style-type: none;">
                    <li><h5 style="font-weight: bolder;">Definitions</h5></li>
                        <li><a href="<c:url value="/protected/medical/setups/bedtypes"/>">Bed Types
                        </a></li>
                        <li><a href="<c:url value="/protected/medical/setups/labtests"/>">Services
                        </a></li>
                    <li><a href="<c:url value="/protected/medical/setups/bensections"/>">Claim Networks
                    </a></li>
                    <li><a href="<c:url value="/protected/accounts/banksdef"/>">Health Maintanance Organizations
                    </a></li>
                    <li><a href="<c:url value="/protected/medical/setups/cardtypes"/>">Card Types
                    </a></li>
               <!--     <li><a href="<c:url value="/protected/medical/setups/familysizes"/>">Family Size
                    </a></li>
                    <li><a href="<c:url value="/protected/medical/setups/benefits"/>">Benefits
                    </a></li>

                    <li><a href="<c:url value="/protected/medical/setups/specfees"/>">Specialist Fees
                    </a></li> -->

                </ul>
            </div>
            <div class="col-md-6 col-sm-6 col-xs-12">
                <ul style="list-style-type: none;">
                    <li><h5 style="font-weight: bolder;">Ailments/Departments/Drugs</h5></li>
                    <li><a href="<c:url value="/protected/medical/setups/ailment"/>">Ailments
                    </a></li>
                    <li><a href="<c:url value="/protected/medical/setups/servproviders"/>">Service Providers
                    </a></li>
                    <li><a href="<c:url value="/protected/medical/setups/providercontract"/>">Service Providers Contracts
                    </a></li>
                    <li><a href="<c:url value="/protected/medical/setups/events"/>">Event Types
                    </a></li>


                </ul>
                </ul>
            </div>
        </div>
    </div>
</div>
    </div>