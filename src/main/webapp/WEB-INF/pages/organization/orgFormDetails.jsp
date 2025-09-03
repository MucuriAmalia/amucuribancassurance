<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<div class="x_panel">
<form:form class="form-horizontal" role="form" modelAttribute="organization"
                       method="post" action="createOrganization" id="orgForm"
                       enctype="multipart/form-data">
                  
				        <a href="<c:url value='/protected/organization/editOrganization'/> " class="btn btn-info">Edit</a>
				        <hr>
				         <c:if test="${not empty message}">
				          <div class="alert alert-success alert-dismissible">
				              <button class="close" aria-hidden="true" data-dismiss="alert" type="button">x</button>
				                ${message}
				          </div>
				          </c:if>
				        <div class="item form-group">
				            <div class="col-md-6 col-xs-12">
		                    <label for="orgName" class="label-align col-md-5">Organization Name</label>
		                     <div class="col-md-7 col-xs-12">
		                         <p class="form-control-static"> <c:out value="${organization.orgName}"></c:out></p>
		                     </div>               
		                    </div>
		                    <div class="col-md-6 col-xs-12">
		                    <label for="orgShtDesc" class="label-align col-md-5">Alias</label>
		                    <div class="col-md-7 col-xs-12">
		                      <p class="form-control-static"> <c:out value="${organization.orgShtDesc}"></c:out></p>
		                    </div>    
		                    </div>   
				        </div>
						<div class="item form-group">
							<div class="col-md-6 col-xs-12">
								<label for="orgName" class="label-align col-md-5">Mobile</label>
								<div class="col-md-7 col-xs-12">
									<p class="form-control-static"> <c:out value="${organization.orgMobile}"></c:out></p>
								</div>
							</div>
							<div class="col-md-6 col-xs-12">
								<label for="orgShtDesc" class="label-align col-md-5">Phone</label>
								<div class="col-md-7 col-xs-12">
									<p class="form-control-static"> <c:out value="${organization.orgPhone}"></c:out></p>
								</div>
							</div>
						</div>
				          <div class="item form-group">
				           <div class="col-md-6 col-xs-12">
		                    <label for="orgFax" class="label-align col-md-5">Fax Number</label>
		                    <div class="col-md-7 col-xs-12">
		                     <p class="form-control-static"> <c:out value="${organization.orgFax}"></c:out></p>                  
		                 </div>
		                 </div>
		                  <div class="col-md-6 col-xs-12">
		                 <label for="orgWebsite" class="label-align col-md-5">WebSite</label>
		                    <div class="col-md-7 col-xs-12">
		                      <p class="form-control-static"> <c:out value="${organization.orgWebsite}"></c:out></p>                   
		                    </div>
		                 </div>
				        </div>
				        <div class="item form-group">
				          
				           <div class="col-md-6 col-xs-12">
		                 <label for="currencyName" class="label-align col-md-5">Date of Incorporation</label>
		                    <div class="col-md-7 col-xs-12">
		                      <p class="form-control-static"> <c:out value="${organization.dateIncorp}"></c:out></p> 
		                                    
		                 </div>
		                 </div>
		                  <div class="col-md-6 col-xs-12">
		                 <label for="currencyName" class="label-align col-md-5">Certificate No.</label>
		                    <div class="col-md-7 col-xs-12">
		                      <p class="form-control-static"> <c:out value="${organization.certNumber}"></c:out></p> 
		                                    
		                 </div>
		                 </div>
				           
		                
				        </div>
				        
				        <div class="item form-group">
				          
				           <div class="col-md-6 col-xs-12">
		                 <label for="currencyName" class="label-align col-md-5">Currency</label>
		                    <div class="col-md-7 col-xs-12">
		                      <p class="form-control-static"> <c:out value="${organization.curName}"></c:out></p>
		                                    
		                 </div>
		                 </div>
				            <div class="col-md-6 col-xs-12">
		                 <label for="countryName" class="label-align col-md-5">Country</label>
		                    <div class="col-md-7 col-xs-12">
		                        <p class="form-control-static"> <c:out value="${organization.couName}"></c:out></p>
		                                 
		                    </div>
		                 </div>
		                
				        </div>
						<div class="item form-group">

							<div class="col-md-6 col-xs-12">
								<label class="label-align col-md-5">Pin Number</label>
								<div class="col-md-7 col-xs-12">
									<p class="form-control-static"> <c:out value="${organization.pinNo}"></c:out></p>

								</div>
							</div>
							<div class="col-md-6 col-xs-12">

							</div>

						</div>
				        
				          <div class="x_title">
				        <h4>Address Information</h4>
				        </div>
				        
				        <div class="item form-group">
				           <div class="col-md-6 col-xs-12">
		                    <label for="zipCode" class="label-align col-md-5">Postal Address</label>
		                    <div class="col-md-7 col-xs-12">
		                    <p class="form-control-static"> <c:out value="${organization.addAddress}"></c:out></p>
		                                    
		                 </div>
		                 </div>
		                 
				        
				        <div class="col-md-6 col-xs-12">
		                  <label for="zipCode" class="label-align col-md-5">Email Address</label>
		                    <div class="col-md-7 col-xs-12">
		                    <p class="form-control-static"> <c:out value="${organization.emailAddress}"></c:out></p>
		                 </div>
				        </div>
				        </div>
				        <div class="item form-group">
				           <div class="col-md-6 col-xs-12">
		                    <label for="zipCode" class="label-align col-md-5">Physical Address</label>
		                    <div class="col-md-7 col-xs-12">
		                    <p class="form-control-static"> <c:out value="${organization.phyAddress}"></c:out></p>
		                                    
		                 </div>
		                 </div>
		                 
				        
				        <div class="col-md-6 col-xs-12">
		                  
				        </div>
				        </div>
				   
				        
				
				        
				        <div class="item form-group">
				           
				             <div class="col-md-6 col-xs-12">
		                    <label for="logo" class="label-align col-md-5">Logo</label>
		                    <div class="col-md-7 col-xs-12">
		                     <div class="kv-avatar center-block" style="width:200px">
						        <img  src="<c:url value='/protected/organization/logo'/> " style="width:200px">
						    </div>
						    </div>
		                 </div>
				          
				          </div>
				        
				      
				     <form:hidden path="orgCode" id="orgCodepk" /> 
                      </form:form>
                      </div>