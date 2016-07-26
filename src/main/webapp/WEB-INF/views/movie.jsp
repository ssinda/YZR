<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<%@page language="java" contentType="text/html; charset=utf-8" pageEncoding="utf-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn" %>
<%@include file="./include/header.jsp" %>
<div id="wrap_content" style="min-height: 100%; position: relative;">
	<div id="content" style="padding-bottom:400px;">
		<div style="float:left;">
			<span style="font-weight:bold; font-size:36px;">무비차트</span>
		</div>
		<div style="text-align:right; float:right; width:200px; height:50px; padding-top:10px;">
			<ul class="nav nav-tabs" style="display:inline-block;">
				<li><a href="javascript:getMovieByCategory('chart')"><span class="cat" style="font-weight:bold;">무비차트</span></a></li>
				<li><a href="javascript:getMovieByCategory('schedule')"><span class="cat" style="font-weight:bold;">상영예정작</span></a></li>
			</ul>
		</div>
		
		<br/>
		
		<div style="border:2px solid #000; margin-top:40px;"></div>
		
		<div id="menu" style="display:none;">
			<div id="sel_option" style="margin-top:10px;">
				<input type="checkbox" id="checkbox" name="checkbox" style="margin-top:8px; float:left;" onclick="play()"/> 
				<label style="font-size:12px; margin-top:6px; float:left;">현재 상영작만 보기</label>
				<div style="margin-top:8px; width:205px; float:right;">
					<select id="order" class="form-control" style="width:150px; float:left;">
						<option selected="selected" value="예매율순">예매율순</option>
						<option value="평점순">평점순</option>
						<option value="관람객순">관람객순</option>
					</select>
					<button type="button" id="sel_btn" class="btn btn-primary" name="sel_btn" onclick="play()" style="margin-left:5px;">
						GO
					</button>
				</div>
			</div>
		</div>
		
		<div id="ajax_movie" style="margin-top:25px;">
			<!-- 무비차트 ajax 처리 내용뿌려줄 곳 -->
		</div>
		
	</div>	
</div>

<script>
	/* 
	function setMenu(){
		var menu = "";
		menu += '<div id="sel_option" style="margin-top:10px;">'
			+ '<input type="checkbox" id="checkbox" name="checkbox" checked="checked" style="margin-top:8px; float:left;"/>' 
			+ '<label style="font-size:12px; margin-top:6px; float:left;">현재 상영작만 보기</label>'
			+ '<div style="margin-top:8px; width:205px; float:right;">'
			+ '<select id="order" class="form-control" style="width:150px; float:left;">'
			+ '<option selected="selected" value="예매율순">예매율순</option>'
			+ '<option value="평점순">평점순</option>'
			+ '<option value="관람객순">관람객순</option>'
			+ '</select>'
			+ '<button type="button" id="sel_btn" class="btn btn-primary" name="sel_btn" onclick="view()" style="margin-left:5px;">'
			+ 'GO'
			+ '</button>'
			+ '</div>'
			+ '</div>';
		
			return menu;
	}
	*/
	function more(){
		$(".moreline").show();
		$(".moregroup").show();
		$("#more_btn").hide();
	}
	
	function setMovieByCategory(movie, category){
		var result = "";
		var menu = $("#menu");
		if(category == "chart"){
			menu.show();
			for(var i=0; i<movie.length; i++){
				if(i>3 && i<8){
					if(i%4 == 0){
						result += '<br/><div style="border:1px solid #000; margin-top:430px;"></div>'
					}
					result += '<div id="movie_chart'+i+'" style="margin-top:35px;">'
						+ '<div id="chart" style="width:285px; height:380px; text-align:left; float:left;">'
						+ '<div style="margin-left:18px;">'
						+ '<img src="/resources/poster/'+movie[i].poster+'" style="width:249px; height:300px;"/> <br/>'
						+ '제목 : '+ movie[i].title +' <br/>'
						+ '관람등급 : '+ movie[i].rating +' <br/>'
						+ '예매율 : '+ movie[i].reservation_rate +'% <br/>'
						+ '개봉날짜 : '+ movie[i].open_date +' <br/>'
						+ '<input type="button" id="res_btn" name="res_btn" class="btn btn-danger btn-sm" onclick="" value="예매" style="width:250px; display:inline-block; margin-top:7px;"/>'
						+ '</div>'
						+ '</div>'
						+ '</div>';
					if(i == 7){
						result += '<button type="button" id="more_btn" onclick="more()" style="float:right; margin-top:55px; margin-right:18px; border:0; background-color:white;">'
							+ '<i class="fa fa-plus-square fa-1x" aria-hidden="true" style="color:gray;">'
							+ '</i>'
							+ '<span style="font-weight:bold; font-size:15px;">'
							+ '더보기'
							+ '</span>'
							+ '</button>';
					}
				}else if(i>7){
					if(i%4 == 0){
						result += '<br/><div class="moreline" style="border:1px solid #000; margin-top:430px; display:none;"></div>'
					}
					result += '<div class="moregroup" style="display:none;">' 
						+ '<div id="movie_chart'+i+'" style="margin-top:35px;">'
						+ '<div id="chart" style="width:285px; height:380px; text-align:left; float:left;">'
						+ '<div style="margin-left:18px;">'
						+ '<img src="/resources/poster/'+movie[i].poster+'" style="width:249px; height:300px;"/> <br/>'
						+ '제목 : '+ movie[i].title +' <br/>'
						+ '관람등급 : '+ movie[i].rating +' <br/>'
						+ '예매율 : '+ movie[i].reservation_rate +'% <br/>'
						+ '개봉날짜 : '+ movie[i].open_date +' <br/>'
						+ '<input type="button" id="res_btn" name="res_btn" class="btn btn-danger btn-sm" onclick="" value="예매" style="width:250px; display:inline-block; margin-top:7px;"/>'
						+ '</div>'
						+ '</div>'
						+ '</div>'
						+ '</div>';
					
				}else{
					result += '<div id="movie_chart'+i+'" style="margin-top:100px;">'
						+ '<div id="chart" style="width:285px; height:380px; text-align:left; float:left;">'
						+ '<div style="margin-left:18px;">'
						+ '<img src="/resources/poster/'+movie[i].poster+'" style="width:249px; height:300px;"/> <br/>'
						+ '제목 : '+ movie[i].title +' <br/>'
						+ '관람등급 : '+ movie[i].rating +' <br/>'
						+ '예매율 : '+ movie[i].reservation_rate +'% <br/>'
						+ '개봉날짜 : '+ movie[i].open_date +' <br/>'
						+ '<input type="button" id="res_btn" name="res_btn" class="btn btn-danger btn-sm" onclick="" value="예매" style="width:250px; display:inline-block; margin-top:7px;"/>'
						+ '</div>'
						+ '</div>'
						+ '</div>';
				}
				
				
			}
		}else if(category == "schedule"){
			menu.hide();
			document.getElementById("checkbox").checked = false;
			result += '<h3 style="margin-left:18px; margin-bottom:10px;">'+ movie[0].open_date +'</h3>'
			for(var j=0; j<movie.length; j++){
				if(j>0){
					if(movie[j].open_date != movie[j-1].open_date){
						result += '<br/> <div style="border:1px solid #000; margin-top:420px; clear:both;"></div>'
							+ '<h3 style="margin-left:18px;">'+ movie[j].open_date +'</h3>'
							+ '<div id="chart" style="width:285px; height:380px; text-align:left; float:left;">'
							+ '<div style="margin-left:18px;">'
							+ '<img src="/resources/poster/'+movie[j].poster+'" style="width:249px; height:300px;"/> <br/>'
							+ '제목 : '+ movie[j].title +' <br/>'
							+ '관람등급 : '+ movie[j].rating +' <br/>'
							+ '예매율 : '+ movie[j].reservation_rate +'% <br/>'
							+ '개봉날짜 : '+ movie[j].open_date +' <br/>'
							+ '<input type="button" id="res_btn" name="res_btn" class="btn btn-danger btn-sm" onclick="" value="예매" style="width:250px; display:inline-block; margin-top:7px;"/>'
							+ '</div>'
							+ '</div>'
							+ '</div>';
					}
					if(movie[j].open_date == movie[j-1].open_date){
						result += '<div id="chart" style="width:285px; height:380px; text-align:left; float:left; margin-bottom:70px;">'
							+ '<div style="margin-left:18px;">'
							+ '<img src="/resources/poster/'+movie[j].poster+'" style="width:249px; height:300px;"/> <br/>'
							+ '제목 : '+ movie[j].title +' <br/>'
							+ '관람등급 : '+ movie[j].rating +' <br/>'
							+ '예매율 : '+ movie[j].reservation_rate +'% <br/>'
							+ '개봉날짜 : '+ movie[j].open_date +' <br/>'
							+ '<input type="button" id="res_btn" name="res_btn" class="btn btn-danger btn-sm" onclick="" value="예매" style="width:250px; display:inline-block; margin-top:7px;"/>'
							+ '</div>'
							+ '</div>';
					}
				}else{
					result += '<div id="chart" style="width:285px; height:380px; text-align:left; float:left;">'
						+ '<div style="margin-left:18px;">'
						+ '<img src="/resources/poster/'+movie[j].poster+'" style="width:249px; height:300px;"/> <br/>'
						+ '제목 : '+ movie[j].title +' <br/>'
						+ '관람등급 : '+ movie[j].rating +' <br/>'
						+ '예매율 : '+ movie[j].reservation_rate +'% <br/>'
						+ '개봉날짜 : '+ movie[j].open_date +' <br/>'
						+ '<input type="button" id="res_btn" name="res_btn" class="btn btn-danger btn-sm" onclick="" value="예매" style="width:250px; display:inline-block; margin-top:7px;"/>'
						+ '</div>'
						+ '</div>';
				}
			}
		}
		document.getElementById("ajax_movie").innerHTML = result;
	}
	
	function getMovieByCategory(category){
		if(category == "chart"){
			$.ajax({
				type : 'get',
				url : '/movie/' + category,
				headers : {
				"Content-Type" : "application/json",
				//"X-HTTP-Method-Override" : "GET",  ----  POST 이거나 GET인경우는 생략가능
				},
				dataType : 'json',
				data : '',
				success : function(movie){
					setMovieByCategory(movie, "chart");
				}
			});
		}else if(category == "schedule"){
			$.ajax({
				type : 'get',
				url : '/movie/' + category,
				headers : {
				"Content-Type" : "application/json",
				//"X-HTTP-Method-Override" : "GET",  ----  POST 이거나 GET인경우는 생략가능
				},
				dataType : 'json',
				data : '',
				success : function(movie){
					setMovieByCategory(movie, "schedule");
				}
			});
		}
	}
	
	getMovieByCategory("chart");	
	
	function play(){
		var play = document.getElementById("checkbox");
		var order = document.getElementById("order");
		if(play.checked){
			play.value = "on";
		}else if(!play.checked){
			play.value = "off";
		}
		alert(play+"+"+order);
		$.ajax({
			type : 'get',
			url : '/movie/chart/' + play.value + "/" + order.value,
			headers : {
			"Content-Type" : "application/json",
			//"X-HTTP-Method-Override" : "GET",  ----  POST 이거나 GET인경우는 생략가능
			},
			dataType : 'json',
			data : '',
			success : function(movie){
				setMovieByCategory(movie, "chart");
			}
		});
	}
</script>

<style>
	.nav-tabs{
		border:0px;
	}
	
	.nav-tabs > li > a:hover,
	.nav-tabs > li > a:focus{
		border:0px;
		background-color:#fff;
		list-style-type: none;
		color:red;
	}
	
	.nav-tabs > li > a:active{
		color:red;
	}
</style>
<% session.setAttribute("menu", null);%>
<%@include file="./include/footer.jsp" %>