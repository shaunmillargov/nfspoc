<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>  
<%@ taglib prefix='c' uri='http://java.sun.com/jsp/jstl/core' %>
<html>
	<head>
		<title>NFS POC</title>
		
		<!-- Bootstrap -->
		<link href="/css/bootstrap.min.css" rel="stylesheet">
		<script src="/js/bootstrap.min.js"></script>	
		
		<!-- jquery -->
		<script src="/js/jquery.min.js"></script>	

</head>
	<style>
	body {
		border: 10px solid white;
	}
	.row{
  		text-align:center;
  		/*the same margin which is every button have, it is for small screen, and if you have many buttons.*/
  		margin-left:0px;
  		margin-right:20px;
	}
	.wrapper {
    	overflow: hidden;
	}
	.progress {
    	float:left; 
	}
	.linkdiv {
    	overflow: hidden; 
    	visibility: hidden;
	}
	</style>
	<script>
		$(document).ready(function() {
		
			//var urlEndPoint = 'http://localhost:8090/subscribe';
			var urlEndPoint = '<c:out value="${basePath}"/>/subscribe';
			var eventSource = new EventSource(urlEndPoint);
			
			eventSource.addEventListener("updates", function(event) {
				var transmission = JSON.parse(event.data);
				console.log(transmission);
				
				if (transmission.error == true) {
				  $("#bar" + transmission.threadId).removeClass("bg-success");
				  $("#bar" + transmission.threadId).addClass("bg-danger");
				}
				
				$("#bar" + transmission.threadId).css("width", transmission.progress+"%");
				$("#bar" + transmission.threadId).text(transmission.duration);
				
				if (transmission.fileUrl) {
					$("#dldiv" + transmission.threadId).css("visibility", "visible");
					$("#dllink" + transmission.threadId).attr("href", transmission.fileUrl);
					// Next line triggers download (note this is using native JS as JQ method isn't reliable). 
					// Line may also be removed to allow user to download file manually.  
					document.getElementById('dllink' + transmission.threadId).click();
				}
			})
			
			eventSource.addEventListener('error', function(event) {
				console.log("Error: " + event.currentTarget.readyState )
				
				if (event.currentTarget.readyState == EventSource.CLOSED) {
					
				} else {
					eventSource.close();
				}
			})			
			
		})
		window.onbeforeunload = function() {
			eventSource.close();
		}
		
	</script>
		
	<body>
	<h2>NFS Download POC</h2>
	<br/>
	<c:if test="${not empty fbo.errors}"> 
		<div class="alert alert-danger" role="alert" style="width: 55%">
	 		<c:out value="${fbo.errors}"/>
		</div> 	
	</c:if>
	<label for="documentIds">Choose one or more documents (use shift to select more than one)</label>
		<form:form action="/demo/commenceDemo" method="post" modelAttribute="fbo">
         	<form:select path="documentGuids" class="form-select" size="5" multiple="true" items="${documentOptions}" disabled="${fbo.testing == true ? 'true' : 'false'}" /><br/></br/>
         		<c:if test="${fbo.testing == false}"> 
         			<button type="submit" name="submit" class="btn btn-primary">Submit</button>
         		</c:if>
         		<c:if test="${fbo.testing == true}"> 
         			<button type="submit" name="reset" class="btn btn-danger">Reset</button>
         		</c:if>
    	</form:form>
	<br/>
	<br/>
	
	<c:if test="${fbo.testing == true}"> 
	<h4> Progress </h4>
	</c:if>
	
	<c:forEach var="item" items="${fbo.jobs}">         
    	<p>${item.label}, threadId: ${item.threadId}</p>
    	<div class="wrapper">
			<div class="progress" style="width: 55%">
  				<div class="progress-bar bg-success" id="bar${item.threadId}" role="progressbar" style="width: 0%;" aria-valuemin="0" aria-valuemax="100"></div>
			</div>
			<div class="linkdiv" id="dldiv${item.threadId}">&nbsp;<a id="dllink${item.threadId}" href="Http://www.google.com">Download file</a></div>
		</div>	
		<br/>
    </c:forEach>
    
	</body>
</html>
