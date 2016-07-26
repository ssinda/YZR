package net.nigne.yzrproject.persistence;

import java.sql.Array;
import java.sql.Date;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.Locale;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.AbstractQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Expression;
import javax.persistence.criteria.Join;
import javax.persistence.criteria.Order;
import javax.persistence.criteria.Path;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import javax.persistence.criteria.Subquery;

import org.hibernate.jpa.criteria.expression.function.AggregationFunction.COUNT;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import net.nigne.yzrproject.domain.ActorVO;
import net.nigne.yzrproject.domain.DirectorVO;
import net.nigne.yzrproject.domain.GenreVO;
import net.nigne.yzrproject.domain.GpaVO;
import net.nigne.yzrproject.domain.MovieVO;
import net.nigne.yzrproject.domain.ReservationVO;

@Repository
public class MovieDAOImpl implements MovieDAO {
			
	@PersistenceContext
	private EntityManager entityManager;
	
	@Override
	public MovieVO getMovie(String movie_id) {
		MovieVO vo = new MovieVO();
		CriteriaBuilder cb = entityManager.getCriteriaBuilder();
		CriteriaQuery<MovieVO> cq = cb.createQuery(MovieVO.class);
		Root<MovieVO> root = cq.from(MovieVO.class);
		Predicate p = cb.equal(root.get("movie_id"), movie_id);
		cq.where(p);
		
		TypedQuery<MovieVO> tq = entityManager.createQuery(cq);
		vo = tq.getSingleResult();
		
		return vo;
	}

	@Override
	public List<MovieVO> genreMovie(String member_id) {
		
		// 회원 아이디로 예매내역에 있는 영화들의 장르중 가장 많이 본 장르 가져오기
		
		CriteriaBuilder cb = entityManager.getCriteriaBuilder();
		CriteriaQuery<GenreVO> genreQuery = cb.createQuery(GenreVO.class);
		Root<GenreVO> genreRoot = genreQuery.from(GenreVO.class);
		
		Subquery<ReservationVO> subQuery = genreQuery.subquery(ReservationVO.class);
		Root<ReservationVO> subQueryRoot = subQuery.from(ReservationVO.class);
		subQuery.select(subQueryRoot.get("movie_id"));
		subQuery.where(cb.equal(subQueryRoot.get("member_id"), member_id));
		
		// 예외처리구간
		
		CriteriaQuery<String> resQuery = cb.createQuery(String.class);
		Root<ReservationVO> resRootQuery = resQuery.from(ReservationVO.class);
		resQuery.select(resRootQuery.get("movie_id"));
		resQuery.where(cb.equal(resRootQuery.get("member_id"), member_id));
		
		TypedQuery<String> restq = entityManager.createQuery(resQuery);
		List<String> res_movie_id = restq.getResultList();
		
		List<MovieVO> genre_movie = null;
		if(res_movie_id.isEmpty()){
			CriteriaQuery<MovieVO> Query = cb.createQuery(MovieVO.class);
			Root<MovieVO> Root = Query.from(MovieVO.class);
			
			Query.where(cb.equal(Root.get("status"), "play"));
			Query.orderBy(cb.desc(Root.get("open_date")));
			
			TypedQuery<MovieVO> movie_tq = entityManager.createQuery(Query).setFirstResult(0).setMaxResults(1);
			genre_movie = movie_tq.getResultList();
		// 예외처리 끝
		}else{
		
			genreQuery.select(genreRoot.get("movie_genre"));
			genreQuery.where(genreRoot.get("movie_id").in(subQuery));
			genreQuery.groupBy(genreRoot.get("movie_genre"));
			genreQuery.orderBy(cb.desc(cb.count(genreRoot.get("movie_genre"))));
			
			TypedQuery<GenreVO> tq = entityManager.createQuery(genreQuery);
			
			List<GenreVO> genre_list = tq.getResultList();
			
			// 가장 많이본 장르에 해당하는 영화들의 개봉날짜를 기준으로 최신 영화를 뽑아낸다
			
			CriteriaQuery<MovieVO> movieQuery = cb.createQuery(MovieVO.class);
			Root<MovieVO> movieRoot = movieQuery.from(MovieVO.class);
			
			Subquery<GenreVO> genreSubQuery = movieQuery.subquery(GenreVO.class);
			Root<GenreVO> genreSubQueryRoot = genreSubQuery.from(GenreVO.class);
			
			genreSubQuery.select(genreSubQueryRoot.get("movie_id"));
			genreSubQuery.where(cb.equal(genreSubQueryRoot.get("movie_genre"), genre_list.get(0)));
			
			movieQuery.where(cb.and(movieRoot.get("movie_id").in(genreSubQuery), cb.equal(movieRoot.get("status"), "play")));
			movieQuery.orderBy(cb.desc(movieRoot.get("open_date")));
			
			TypedQuery<MovieVO> mtq = entityManager.createQuery(movieQuery);
			genre_movie = mtq.getResultList();
		
			if(genre_movie.isEmpty()){
				for(int g=0; g<genre_list.size(); g++){
					genreSubQuery.select(genreSubQueryRoot.get("movie_id"));
					genreSubQuery.where(cb.equal(genreSubQueryRoot.get("movie_genre"), genre_list.get(g)));
					
					movieQuery.where(cb.and(movieRoot.get("movie_id").in(genreSubQuery), cb.equal(movieRoot.get("status"), "play")));
					movieQuery.orderBy(cb.desc(movieRoot.get("open_date")));
					
					mtq = entityManager.createQuery(movieQuery);
					genre_movie = mtq.getResultList();
					if(!genre_movie.isEmpty()){
						break;
					}
				}
			}
		}
		return genre_movie;
	}

	@Override
	public List<MovieVO> actorMovie(String member_id) {
		
		// 회원 아이디로 예매내역에 있는 영화중 가장 많이 본 배우 가져오기
		
		CriteriaBuilder cb = entityManager.getCriteriaBuilder();
		CriteriaQuery<ActorVO> actorQuery = cb.createQuery(ActorVO.class);
		Root<ActorVO> actorRoot = actorQuery.from(ActorVO.class);
		
		Subquery<ReservationVO> subQuery = actorQuery.subquery(ReservationVO.class);
		Root<ReservationVO> subQueryRoot = subQuery.from(ReservationVO.class);
		subQuery.select(subQueryRoot.get("movie_id"));
		subQuery.where(cb.equal(subQueryRoot.get("member_id"), member_id));
		
		CriteriaQuery<String> resQuery = cb.createQuery(String.class);
		Root<ReservationVO> resRootQuery = resQuery.from(ReservationVO.class);
		resQuery.select(resRootQuery.get("movie_id"));
		resQuery.where(cb.equal(resRootQuery.get("member_id"), member_id));
		
		TypedQuery<String> restq = entityManager.createQuery(resQuery);
		List<String> res_movie_id = restq.getResultList();
		
		// 예외처리 구간
		
		List<MovieVO> actor_movie = null;
		if(res_movie_id.isEmpty()){
			CriteriaQuery<MovieVO> Query = cb.createQuery(MovieVO.class);
			Root<MovieVO> Root = Query.from(MovieVO.class);
			
			Query.where(cb.equal(Root.get("status"), "play"));
			Query.orderBy(cb.desc(Root.get("open_date")));
			
			TypedQuery<MovieVO> movie_tq = entityManager.createQuery(Query).setFirstResult(1).setMaxResults(1);
			actor_movie = movie_tq.getResultList();
		
			// 예외처리 끝
			
		}else{
			actorQuery.select(actorRoot.get("actor_name"));
			actorQuery.where(actorRoot.get("movie_id").in(subQuery));
			actorQuery.groupBy(actorRoot.get("actor_name"));
			actorQuery.orderBy(cb.desc(cb.count(actorRoot.get("actor_name"))));
			
			TypedQuery<ActorVO> tq = entityManager.createQuery(actorQuery);
			
			List<ActorVO> actor_list = tq.getResultList();
			
			// 가장 많이본 배우에 해당하는 영화들의 개봉날짜를 기준으로 최신 영화를 뽑아낸다
			
			CriteriaQuery<MovieVO> movieQuery = cb.createQuery(MovieVO.class);
			Root<MovieVO> movieRoot = movieQuery.from(MovieVO.class);
			
			Subquery<ActorVO> actorSubQuery = movieQuery.subquery(ActorVO.class);
			Root<ActorVO> actorSubQueryRoot = actorSubQuery.from(ActorVO.class);
			
			actorSubQuery.select(actorSubQueryRoot.get("movie_id"));
			actorSubQuery.where(cb.equal(actorSubQueryRoot.get("actor_name"), actor_list.get(0)));
			
			movieQuery.where(cb.and(movieRoot.get("movie_id").in(actorSubQuery), cb.equal(movieRoot.get("status"), "play")));
			movieQuery.orderBy(cb.desc(movieRoot.get("open_date")));
			
			TypedQuery<MovieVO> mtq = entityManager.createQuery(movieQuery);
			actor_movie = mtq.getResultList();
			
			if(actor_movie.isEmpty()){
				for(int a=0; a<actor_list.size(); a++){
					actorSubQuery.select(actorSubQueryRoot.get("movie_id"));
					actorSubQuery.where(cb.equal(actorSubQueryRoot.get("movie_genre"), actor_list.get(a)));
					
					movieQuery.where(cb.and(movieRoot.get("movie_id").in(actorSubQuery), cb.equal(movieRoot.get("status"), "play")));
					movieQuery.orderBy(cb.desc(movieRoot.get("open_date")));
					
					mtq = entityManager.createQuery(movieQuery);
					actor_movie = mtq.getResultList();
					if(!actor_movie.isEmpty()){
						break;
					}
				}
			}
		}
		return actor_movie;
	}

	@Override
	public List<MovieVO> directorMovie(String member_id) {
		
		// 회원 아이디로 예매내역에 있는 영화들의 감독중 가장 많이 본 장르 가져오기
		
		CriteriaBuilder cb = entityManager.getCriteriaBuilder();
		CriteriaQuery<DirectorVO> directorQuery = cb.createQuery(DirectorVO.class);
		Root<DirectorVO> directorRoot = directorQuery.from(DirectorVO.class);
		
		Subquery<ReservationVO> subQuery = directorQuery.subquery(ReservationVO.class);
		Root<ReservationVO> subQueryRoot = subQuery.from(ReservationVO.class);
		subQuery.select(subQueryRoot.get("movie_id"));
		subQuery.where(cb.equal(subQueryRoot.get("member_id"), member_id));
		
		CriteriaQuery<String> resQuery = cb.createQuery(String.class);
		Root<ReservationVO> resRootQuery = resQuery.from(ReservationVO.class);
		resQuery.select(resRootQuery.get("movie_id"));
		resQuery.where(cb.equal(resRootQuery.get("member_id"), member_id));
		
		TypedQuery<String> restq = entityManager.createQuery(resQuery);
		List<String> res_movie_id = restq.getResultList();
		
		// 예외처리 구간
		
		List<MovieVO> director_movie = null;
		if(res_movie_id.isEmpty()){
			CriteriaQuery<MovieVO> Query = cb.createQuery(MovieVO.class);
			Root<MovieVO> Root = Query.from(MovieVO.class);
			
			Query.where(cb.equal(Root.get("status"), "play"));
			Query.orderBy(cb.desc(Root.get("open_date")));
			
			TypedQuery<MovieVO> movie_tq = entityManager.createQuery(Query).setFirstResult(2).setMaxResults(1);
			director_movie = movie_tq.getResultList();
		
			// 예외처리 끝
			
		}else{
			
			directorQuery.select(directorRoot.get("director_name"));
			directorQuery.where(directorRoot.get("movie_id").in(subQuery));
			directorQuery.groupBy(directorRoot.get("director_name"));
			directorQuery.orderBy(cb.desc(cb.count(directorRoot.get("director_name"))));
			
			TypedQuery<DirectorVO> tq = entityManager.createQuery(directorQuery);
			
			List<DirectorVO> director_list = tq.getResultList();
			
			// 가장 많이본 감독에 해당하는 영화들의 개봉날짜를 기준으로 최신 영화를 뽑아낸다
			
			CriteriaQuery<MovieVO> movieQuery = cb.createQuery(MovieVO.class);
			Root<MovieVO> movieRoot = movieQuery.from(MovieVO.class);
			
			Subquery<DirectorVO> directorSubQuery = movieQuery.subquery(DirectorVO.class);
			Root<DirectorVO> directorSubQueryRoot = directorSubQuery.from(DirectorVO.class);
			
			directorSubQuery.select(directorSubQueryRoot.get("movie_id"));
			directorSubQuery.where(cb.equal(directorSubQueryRoot.get("director_name"), director_list.get(0)));
			
			movieQuery.where(cb.and(movieRoot.get("movie_id").in(directorSubQuery), cb.equal(movieRoot.get("status"), "play")));
			movieQuery.orderBy(cb.desc(movieRoot.get("open_date")));
			
			TypedQuery<MovieVO> mtq = entityManager.createQuery(movieQuery);
			director_movie = mtq.getResultList();
		
			if(director_movie.isEmpty()){
				for(int d=0; d<director_list.size(); d++){
					directorSubQuery.select(directorSubQueryRoot.get("movie_id"));
					directorSubQuery.where(cb.equal(directorSubQueryRoot.get("movie_genre"), director_list.get(d)));
					
					movieQuery.where(cb.and(movieRoot.get("movie_id").in(directorSubQuery), cb.equal(movieRoot.get("status"), "play")));
					movieQuery.orderBy(cb.desc(movieRoot.get("open_date")));
					
					mtq = entityManager.createQuery(movieQuery);
					director_movie = mtq.getResultList();
					if(!director_movie.isEmpty()){
						break;
					}
				}
			}
		}
		return director_movie;
	}

	@Override
	public List<MovieVO> basicMovie() {
		CriteriaBuilder cb = entityManager.getCriteriaBuilder();
		try{
			CriteriaQuery<MovieVO> cq = cb.createQuery(MovieVO.class);
			Root<MovieVO> root = cq.from(MovieVO.class);
			cq.where(cb.equal(root.get("status"), "play"));
			cq.orderBy(cb.desc(root.get("open_date")));
			
			TypedQuery<MovieVO> tq = entityManager.createQuery(cq).setFirstResult(0).setMaxResults(3);
			List<MovieVO> basic_movie = tq.getResultList();
			return basic_movie;
			
		}catch(Exception e){
			return null;
		}
		
	}

	@Override
	public List<MovieVO> getMovieChart(String play, String order) {
		CriteriaBuilder cb = entityManager.getCriteriaBuilder();
		CriteriaQuery<MovieVO> cq = cb.createQuery(MovieVO.class);
		Root<MovieVO> root = cq.from(MovieVO.class);
		
		CriteriaQuery<String> gpaQuery = cb.createQuery(String.class);
		Root<GpaVO> gpaRoot = gpaQuery.from(GpaVO.class);

		gpaQuery.select(gpaRoot.get("movie_id"));
		gpaQuery.orderBy(cb.desc(gpaRoot.get("avg")));
		
		TypedQuery<String> gpatq = entityManager.createQuery(gpaQuery);
		List<String> gpa_list = gpatq.getResultList();
		
		Predicate p = cb.equal(root.get("status"), "play");
		Predicate p2 = cb.notEqual(root.get("status"), "end");
		List<Predicate> pl = new ArrayList<Predicate>();
		
		if("on".equals(play)){
			cq.where(p);
			if("예매율순".equals(order)){
				cq.orderBy(cb.desc(root.get("reservation_rate")));
			}else if("평점순".equals(order)){
				for(int a=0; a<gpa_list.size(); a++){
					pl.add(cb.or(cb.equal(root.get("movie_id"), gpa_list.get(a))));
				}
				cq.where(cb.and(p, cb.or(pl.toArray(new Predicate[pl.size()]))));
			}else if("관람객순".equals(order)){
				cq.orderBy(cb.desc(root.get("moviegoers_cnt")));
			}
			
		}else if("off".equals(play)){
			cq.where(p2);
			if("예매율순".equals(order)){
				cq.orderBy(cb.desc(root.get("reservation_rate")));
			}else if("평점순".equals(order)){
				for(int b=0; b<gpa_list.size(); b++){
					pl.add(cb.or(cb.equal(root.get("movie_id"), gpa_list.get(b))));
				}
				cq.where(cb.and(p2, cb.or(pl.toArray(new Predicate[pl.size()]))));
			}else if("관람객순".equals(order)){
				cq.orderBy(cb.desc(root.get("moviegoers_cnt")));
			}
		}
		
		TypedQuery<MovieVO> tq = entityManager.createQuery(cq);
		List<MovieVO> movie_chart = tq.getResultList();
		
		return movie_chart;
	}

	@Override
	public List<MovieVO> getMovieSchedule() {
		CriteriaBuilder cb = entityManager.getCriteriaBuilder();
		CriteriaQuery<MovieVO> cq = cb.createQuery(MovieVO.class);
		Root<MovieVO> root = cq.from(MovieVO.class);
		
		cq.where(cb.equal(root.get("status"), "schedule"));
		cq.orderBy(cb.asc(root.get("open_date")));
		
		TypedQuery<MovieVO> tq = entityManager.createQuery(cq);
		List<MovieVO> movie_schedule = tq.getResultList();
	
		return movie_schedule;
	}

}
