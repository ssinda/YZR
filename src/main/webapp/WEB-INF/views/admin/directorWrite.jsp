<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<%@page language="java" contentType="text/html; charset=utf-8"
	pageEncoding="utf-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@include file="../include/header.jsp"%>
<%@include file="./sidebar.jsp"%>
<c:if test="${ member_id == null }">
	<script>
		<%response.sendRedirect("/login");%>
	</script>
</c:if>
<div class="content" style="margin-left: 250px;">
	<h2>감독 등록</h2><br><br>
	<form id="directorWritefrm" role="form" action="/admin/director/new" method="post" enctype="multipart/form-data">
		<label class="col-sm-1">이름</label>
		<div class="col-md-3">
			<input type="text" id="director_name" name="director_name" class="form-control" placeholder="Enter Name" />
		</div>
		<label class="col-sm-1">나이</label>
		<div class="col-md-3">
			<input type="text" id="director_age" name="director_age" class="form-control" placeholder="Enter Age" />
		</div><br><br><br>
		<label class="col-sm-1">데뷔작</label>
		<div class="col-md-3">
			<input type="text" id="director_debut" name="director_debut" class="form-control" placeholder="Enter Debut" />
		</div>
		<label class="col-sm-1">소속사</label>
		<div class="col-md-3">
			<input type="text" id="director_company" name="director_company" class="form-control" placeholder="Enter Company" />
		</div><br><br><br>
		<label class="col-sm-1">영화ID</label>
		<div class="col-md-3">
			<select id="movie_id" name="movie_id">
				<c:forEach items="${ moviename_list }" var="list">
					<option value="${ list.movie_id }">${ list.title }</option>
				</c:forEach>
			</select>
		</div>
		<label class="col-sm-1">사진</label>
			<input type="file" id="file" name="file" size="60" />
		<div style="clear: both; margin-left: 300px;">
			<button type="submit" class="btn btn-primary">등록</button>
		</div>
	</form>
</div>
<%@include file="../include/footer.jsp"%>