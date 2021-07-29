<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<title>Insert title here</title>
<link rel="stylesheet" href="https://maxcdn.bootstrapcdn.com/bootstrap/4.4.1/css/bootstrap.min.css">
<script src="https://ajax.googleapis.com/ajax/libs/jquery/3.4.1/jquery.min.js"></script>
<script src="https://maxcdn.bootstrapcdn.com/bootstrap/4.4.1/js/bootstrap.min.js"></script>
<style>
        .content{
          background-color:#FCBE34;
          margin:auto;
          width: 1200px;
      }
      .innerOuter{
          border:1px solid white;
          width:80%;
          margin:auto;
          padding:5% 15%;
          background:white;
      }
 
</style>
</head>
<body>
  
          <!--독서모임게시판상세불러오기-->
          <jsp:include page=""/>
              
            <div class="content">
  
            <div class="innerOuter"  style="padding:5% 5%;" align="center">
                  <br><br><br><br><br><br><br><br>
                <div align="left" style="padding-left:19px">
                    <ul class="nav nav-tabs">
                        <li class="nav-item">
                          <a class="nav-link" href="#">모임정보</a>
                        </li>
                        <li class="nav-item">
                          <a class="nav-link active" href="#">소게시판</a>
                        </li>
                </div>

                    <div align="center" style="width: 800px; border-radius: 10px; height: auto; border: 1px solid; padding-right: 20px; padding-left: 20px;"> 
                            <br><br>
                            <h6 align="left"><b>소게시판<b></h6>
                                
                            <button type="submit" style="float: right; margin: auto; border-radius: 10px; background: white;">글쓰기</button>
                        
                    <br><br>
                    <c:forEach var="g" items="${ groupBoardList }">
                    <div style="border: 1px solid; width: 750; height: 800; margin: auto; border-radius: 10px;">
                        <table align="center" >
                            <tr>
                                <td width="200">작성자아이콘 </td>
                                <td>작성자닉네임</td>
                            </tr>
                            <tr>
                                <td>작성날짜</td>
                                <td>작성시간</td>
                            </tr>
                            <tr>
                                <td colspan="2" width="650px" height="150"> 작성내용 미리보기 </td>
                            </tr>
                        </table>
                    </div>


                    <div style="border: 1px solid; width: 750; margin: auto; border-radius: 10px;">
                        <table  align="center">
                            <tr>
                                <td width="550" onclick="">댓글(1)</td>
                                <td width="100"><button type="submit" style="float: left; border-radius: 10px; background: white;">댓글달기</button></td>
                            </tr>
                        </table>
                        <br>
                    </div>
                    </c:forEach>
                    <br><br><br>
              


                    


                    <br><br>
                    <button type="button" id="more" style="border-radius: 10px; background: white; ">더보기▼</button>
             
                    <script>
                        var more = -1;
                         $(function(){$('#more').on('click',function(){
                             
                                 more = more +1;
                                
                                $.ajax({
                                    url : "groupBoardList.do",
                                    type: 'POST',
                                    data: { more : more,		    
                                         
                                        },
                                    dataType: "json",
                                    success: function(data){
                        
                                            var addListHtml ="";
                                            addListHtml += "<div class='fixed_img_col' style='height:200px'>";
                                            for(var i in data.list){
                                            
                                            addListHtml += "<table>"
                                    	    addListHtml += "<tr>"
                                            addListHtml += "<td>"+data.list[i].작성자아이콘+"</td>";
                                            addListHtml += "<td>"+data.list[i].작성자이름+"</td>";
                                            addListHtml += "</tr>"
                                     	    addListHtml += "<tr>"
                                           	addListHtml += "<td>"+data.list[i].작성날짜+"</td>";
                                            addListHtml += "<td>"+data.list[i].작성시간+"</td>";
                                            addListHtml += "<tr>"
                                            addListHtml += "<td>"+data.list[i].작성내용+"</td>";
                                            addListHtml += "</tr>"
                                            addListHtml += "<tr>"
                                            addListHtml += "</table>";
                                            addListHtml += "<table>"
                                            addListHtml += "<tr>"
                                            addListHtml += "<td>"+data.list[i].댓글+data.list[i].댓글수+"</td>";
 				                            addListHtml += "<td>"+"<button>댓글달기</button>"+"</td>";
                                            addListHtml += "</tr>"
                                            addListHtml += "</table>";	
                                        }
                                            addListHtml += "</div>";
                                        if(data.list.length>=1){
                                            $("#morePage").append(addListHtml);
                                        }else{
                                            alert("다음페이지가 없습니다.");
                                        }						
                                    },
                                    error: function (request,status,errorData){   
                                        alert('error code: '+request.status+"\n"
                                                +'message:' +request.reponseText+'\n'
                                                + 'error :'+  errorData);
                                    }
                                });
                            }); 
                         }); 
                        </script>
                        <br><br><br>
                </div>            
           </div>   
      </div>
         <br><br>

         <!--푸터바 입력-->
         <jsp:include page=""/>  


    </body>

</html>
  