<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"
    isELIgnored="false" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>    
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%
  request.setCharacterEncoding("UTF-8");
%> 
<c:set var="contextPath"  value="${pageContext.request.contextPath}"  />
<head>
   <meta charset="UTF-8">
   <title>글보기</title>
   <style>
     #tr_btn_modify{
       display:none;
     }
   
   </style>
   <script  src="http://code.jquery.com/jquery-latest.min.js"></script> 
   <script type="text/javascript" >
     
   
   
     // 리스트로 돌아가기
   	 function backToList(obj){
	    obj.action="${contextPath}/board/listArticles.do";
	    obj.submit();
     }
 	
     
     // form 수정하기
	 function fn_enable(obj){
		 document.getElementById("i_title").disabled=false;
		 document.getElementById("i_content").disabled=false;
		 document.getElementById("i_imageFileName").disabled=false;
		 document.getElementById("tr_btn_modify").style.display="block"; //  활성화 (보이기)
		 document.getElementById("tr_btn").style.display="none";		 // 비활성화 (가리기)
	 }
	 
     
     // 수정 반영하기 ( 컨트롤러 호출 )
     // parameter : form 태그의 name값 
	 function fn_modify_article(obj){
		 obj.action="${contextPath}/board/modArticle.do";	// action 설정
		 obj.submit();										// 해당 from , 컨트롤러 호출
	 }
	 
     // 글 삭제하기 
	 function fn_remove_article(url,articleNO){
		 var form = document.createElement("form");				// form 태그 생성
		 form.setAttribute("method", "post");					// form 태그 method 설정
		 form.setAttribute("action", url);						// form 태그 action 설정
		 
	     var articleNOInput = document.createElement("input");	// input 태그 생성
	     articleNOInput.setAttribute("type","hidden");			// input 태그 type 설정
	     articleNOInput.setAttribute("name","articleNO");		// input 태그 name 설정
	     articleNOInput.setAttribute("value", articleNO);		// input 태그 value 설정
		 
	     form.appendChild(articleNOInput);						// form태그 내부에 input 태그 삽입
	     document.body.appendChild(form);						// body 태그 내부에 form 태그 삽입
	     form.submit();											// 해당 from , 컨트롤러 호출
	 
	 }
	 
     // 답글 쓰기
	 function fn_reply_form(url, parentNO){
    	 
		 var form = document.createElement("form");				// form 태그 생성
		 form.setAttribute("method", "post");					// form 태그 method 설정
		 form.setAttribute("action", url);						// form 태그 action 설정
		 
	     var parentNOInput = document.createElement("input");	// input 태그 생성	
	     parentNOInput.setAttribute("type","hidden");			// input 태그 type 설정
	     parentNOInput.setAttribute("name","parentNO");			// input 태그 name 설정
	     parentNOInput.setAttribute("value", parentNO);			// input 태그 value 설정 = 부모글 번호
		 
	     form.appendChild(parentNOInput);						// form 태그 내부에 input 태그 삽입
	     document.body.appendChild(form);						// body 태그 내부에 form 태그 삽입
		 form.submit();											// 해당 from , 컨트롤러 호출		
	 }
	 
     // 미리보기 
	 function readURL(input) {									// this = input 태그
	     if (input.files && input.files[0]) {					// 파일이 존재하면
	    	 
	         var reader = new FileReader();
	         reader.onload = function (e) {
	             $('#preview').attr('src', e.target.result);
	         }
	         reader.readAsDataURL(input.files[0]);
	     }
	 }  
 </script>
</head>
<body>
  <form name="frmArticle" method="post"  action="${contextPath}"  enctype="multipart/form-data">
  <table  border=0  align="center">
  <tr>
   <td width=150 align="center" bgcolor=#FF9933>
      글번호
   </td>
   <td >
    <input type="text"  value="${article.articleNO }"  disabled />
    <input type="hidden" name="articleNO" value="${article.articleNO}"  />
   </td>
  </tr>
  <tr>
    <td width="150" align="center" bgcolor="#FF9933">
      작성자 아이디
   </td>
   <td >
    <input type=text value="${article.id }" name="writer"  disabled />
   </td>
  </tr>
  <tr>
    <td width="150" align="center" bgcolor="#FF9933">
      제목 
   </td>
   <td>
    <input type=text value="${article.title }"  name="title"  id="i_title" disabled />
   </td>   
  </tr>
  <tr>
    <td width="150" align="center" bgcolor="#FF9933">
      내용
   </td>
   <td>
    <textarea rows="20" cols="60"  name="content"  id="i_content"  disabled />${article.content }</textarea>
   </td>  
  </tr>
 
<c:if test="${not empty article.imageFileName && article.imageFileName!='null' }">  
<tr>
    <td width="150" align="center" bgcolor="#FF9933"  rowspan="2">
      이미지
   </td>
   <td>
     <input  type= "hidden"   name="originalFileName" value="${article.imageFileName }" />
    <img src="${contextPath}/download.do?articleNO=${article.articleNO}&imageFileName=${article.imageFileName}" id="preview"  /><br>
       
   </td>   
  </tr>  
  <tr>
    <td>
       <input  type="file"  name="imageFileName " id="i_imageFileName"   disabled   onchange="readURL(this);"   />
    </td>
  </tr>
 </c:if>
  <tr>
	   <td width="150" align="center" bgcolor="#FF9933">
	      등록일자
	   </td>
	   <td>
	    <input type=text value="<fmt:formatDate value="${article.writeDate}" />" disabled />
	   </td>   
  </tr>
  <tr   id="tr_btn_modify"  >
	   <td colspan="2"   align="center" >
	       <input type=button value="수정반영하기"   onClick="fn_modify_article(frmArticle)"  >
           <input type=button value="취소"  onClick="backToList(frmArticle)">
	   </td>   
  </tr>
    
  <tr  id="tr_btn"    >
   <td colspan="2" align="center">
	    <input type=button value="수정하기" onClick="fn_enable(this.form)">
	    <input type=button value="삭제하기" onClick="fn_remove_article('${contextPath}/board/removeArticle.do', ${article.articleNO})">
	    <input type=button value="리스트로 돌아가기"  onClick="backToList(this.form)">
	     <input type=button value="답글쓰기"  onClick="fn_reply_form('${contextPath}/board/replyForm.do', ${article.articleNO})">
   </td>
  </tr>
 </table>
 </form>
</body>
</html>