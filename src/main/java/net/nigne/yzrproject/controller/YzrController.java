package net.nigne.yzrproject.controller;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;

import net.nigne.yzrproject.domain.Criteria;
import net.nigne.yzrproject.domain.MovieVO;
import net.nigne.yzrproject.domain.NoticeVO;
import net.nigne.yzrproject.domain.ReservationVO;
import net.nigne.yzrproject.service.ActorService;
import net.nigne.yzrproject.service.DirectorService;
import net.nigne.yzrproject.service.GenreService;
import net.nigne.yzrproject.service.MovieService;
import net.nigne.yzrproject.service.NoticeService;
import net.nigne.yzrproject.service.ReservationService;
import net.nigne.yzrproject.service.SupportNoticeService;

@Controller
public class YzrController {
	
	@Autowired
	private MovieService movie_service;
	@Autowired
	private ReservationService reservation_service;
	@Autowired
	private NoticeService notice_service;
	@Autowired
	private GenreService genre_service;
	@Autowired
	private ActorService actor_service;
	@Autowired
	private DirectorService director_service;
	@Autowired
	private SupportNoticeService noticeService;

	@RequestMapping(value = "", method = RequestMethod.GET)
	public ModelAndView homeA(Locale locale, Model model) throws Exception {

		return new ModelAndView("redirect:/index");
	
	}
	
	
	@RequestMapping(value = "/user", method = RequestMethod.GET)
	public String userPage(Locale locale, Model model) throws Exception {

		return "user/index";
	}
	
	@RequestMapping(value = "/membership", method = RequestMethod.GET)
	public String membershipPage(Locale locale, Model model) throws Exception {

		return "membership";
	}
	
	@RequestMapping(value = "/support", method = RequestMethod.GET)
	public ModelAndView supportPage() throws Exception {
		
		Criteria cri=new Criteria();
		cri.setPage(1);
		cri.setArticlePerPage(5);
		ModelAndView view=new ModelAndView();
		view.addObject("notice_list", noticeService.getNoticeList(cri));
		view.setViewName("support/index");
		return view;
	}
	
	@RequestMapping(value = "/index", method = RequestMethod.GET)
	public String index(HttpServletRequest request, Model model) throws Exception {
		HttpSession session = request.getSession();
		String member_id = (String)session.getAttribute("member_id");
		
		MovieVO rec_genre_movie = new MovieVO();	// 추천장르영화
		MovieVO rec_actor_movie = new MovieVO();	// 추천배우영화
		MovieVO rec_director_movie = new MovieVO(); // 추천감독영화
		
		if(member_id == "" || member_id == null){
			// 기본추천영화
			List<MovieVO> rec_basic = movie_service.basicMovie();
			List<String> basic_title = new ArrayList();
			
			if(!rec_basic.isEmpty()){
				basic_title.add("No.1");
				basic_title.add("No.2");
				basic_title.add("No.3");
				
				model.addAttribute("basic_title", basic_title);
				model.addAttribute("rec_basic", rec_basic);
			}else{
				basic_title.add("등록된 영화가 없습니다.");
				basic_title.add("등록된 영화가 없습니다.");
				basic_title.add("등록된 영화가 없습니다.");
				
				model.addAttribute("basic_title", basic_title);
			}
		}else{
			List<ReservationVO> list = reservation_service.getReservation_list(member_id);	// 예매내역
			
			List<MovieVO> rec_genre = null;			// 추천장르목록
			List<MovieVO> rec_actor = null;			// 추천배우목록
			List<MovieVO> rec_director = null;		// 추천감독목록
			
			
			if(list.isEmpty()){
				
				rec_genre = movie_service.genreMovie(member_id);
				rec_actor = movie_service.actorMovie(member_id);
				rec_director = movie_service.directorMovie(member_id);
				
				List<MovieVO> rec_movie = new ArrayList();
				rec_movie.add(rec_genre.get(0));
				rec_movie.add(rec_actor.get(0));
				rec_movie.add(rec_director.get(0));
			
				model.addAttribute("rec_movie", rec_movie);
				
				List<String> rec_title = new ArrayList();
				
				rec_title.add("No.1");
				rec_title.add("No.2");
				rec_title.add("No.3");
		
				model.addAttribute("rec_title", rec_title);
				
			}else{
				// 추천영화가 예매 내역에 있는지 확인하기 위한 movie_id 배열
				List list_movieId = new ArrayList();
				
				for(int i=0; i<list.size(); i++){
					list_movieId.add(list.get(i).getMovie_id());
				}
				
				rec_genre = movie_service.genreMovie(member_id);			// 추천장르목록
				rec_actor = movie_service.actorMovie(member_id);			// 추천배우목록
				rec_director = movie_service.directorMovie(member_id);		// 추천감독목록
				
				
				// movie_id 배열에 추천장르영화 movie_id가 있는지 확인
				int mn = 0;
				
				for(int r=0; r<rec_genre.size(); r++){
					if(list_movieId.contains(rec_genre.get(r).getMovie_id())){
						continue;
					}
					mn = r;
				}
				rec_genre_movie = movie_service.getMovie(rec_genre.get(mn).getMovie_id());
				
				String genre = genre_service.getMovie_genre(rec_genre_movie.getMovie_id());
				
				// movie_id 배열에 추천배우영화 movie_id가 있는지 확인
				for(int a=0; a<rec_actor.size(); a++){
					if(list_movieId.contains(rec_actor.get(a).getMovie_id())){
						continue;
					}
					mn = a;
				}
				rec_actor_movie = movie_service.getMovie(rec_actor.get(mn).getMovie_id());
				
				String actor = actor_service.getMovie_actor(member_id);
				
				// movie_id 배열에 추천감독영화 movie_id가 있는지 확인
				for(int d=0; d<rec_director.size(); d++){
					if(list_movieId.contains(rec_director.get(d).getMovie_id())){
						continue;
					}
					mn = d;
				}
				rec_director_movie = movie_service.getMovie(rec_director.get(mn).getMovie_id());
				
				String director = director_service.getMovie_director(rec_director_movie.getMovie_id());
			
				// 카테고리별 추천영화 리스트 담기
				List<MovieVO> rec_movie = new ArrayList();
				rec_movie.add(rec_genre_movie);
				rec_movie.add(rec_actor_movie);
				rec_movie.add(rec_director_movie);
			
				model.addAttribute("rec_movie", rec_movie);
				
				// 추천영화별 타이틀
				List<String> rec_title = new ArrayList();
				
				rec_title.add("장르&nbsp<&nbsp" + genre + "&nbsp>");
				rec_title.add("배우&nbsp<&nbsp" + actor + "&nbsp>");
				rec_title.add("감독&nbsp<&nbsp" + director + "&nbsp>");
		
				model.addAttribute("rec_title", rec_title);
			}	
		}
		
		List<NoticeVO> notice_list = notice_service.getNotice();
		
		model.addAttribute("notice", notice_list);
		
		return "index";
	}
	
	@RequestMapping(value = "/index/{category}", method = RequestMethod.GET)
	public ResponseEntity<List<String>> event_img(@PathVariable("category") Integer category){
		ResponseEntity<List<String>> entity = null;
		
		try{
			List<String> list = new ArrayList();
			
			if(category.equals(1)){
				list.add("http://img.naver.net/static/www/u/2013/0731/nmms_224940510.gif");
				list.add("http://img.naver.net/static/www/u/2013/0731/nmms_224940510.gif");
				list.add("http://img.naver.net/static/www/u/2013/0731/nmms_224940510.gif");
				list.add("http://img.naver.net/static/www/u/2013/0731/nmms_224940510.gif");
			}else if(category.equals(2)){
				list.add("http://icon.daumcdn.net/w/icon/1606/30/105915014.png");
				list.add("http://icon.daumcdn.net/w/icon/1606/30/105915014.png");
				list.add("http://icon.daumcdn.net/w/icon/1606/30/105915014.png");
				list.add("http://icon.daumcdn.net/w/icon/1606/30/105915014.png");
			}else if(category.equals(3)){
				list.add("https://tv.pstatic.net/ugc?t=470x180&q=http://dbscthumb.phinf.naver.net/2315_000_2/20110926125556074_R61MRD5WL.jpg/n1464.jpg?type=m4500_4500_fst");
				list.add("https://tv.pstatic.net/ugc?t=470x180&q=http://dbscthumb.phinf.naver.net/2315_000_2/20110926125556074_R61MRD5WL.jpg/n1464.jpg?type=m4500_4500_fst");
				list.add("https://tv.pstatic.net/ugc?t=470x180&q=http://dbscthumb.phinf.naver.net/2315_000_2/20110926125556074_R61MRD5WL.jpg/n1464.jpg?type=m4500_4500_fst");
				list.add("https://tv.pstatic.net/ugc?t=470x180&q=http://dbscthumb.phinf.naver.net/2315_000_2/20110926125556074_R61MRD5WL.jpg/n1464.jpg?type=m4500_4500_fst");
			}else if(category.equals(4)){
				list.add("https://tv.pstatic.net/ugc?t=470x180&q=http://cafefiles.naver.net/20100528_160/credeliens_1275023106172pGQaS_png/yahoo_ai_credeliens.png");
				list.add("https://tv.pstatic.net/ugc?t=470x180&q=http://cafefiles.naver.net/20100528_160/credeliens_1275023106172pGQaS_png/yahoo_ai_credeliens.png");
				list.add("https://tv.pstatic.net/ugc?t=470x180&q=http://cafefiles.naver.net/20100528_160/credeliens_1275023106172pGQaS_png/yahoo_ai_credeliens.png");
				list.add("https://tv.pstatic.net/ugc?t=470x180&q=http://cafefiles.naver.net/20100528_160/credeliens_1275023106172pGQaS_png/yahoo_ai_credeliens.png");
			}
		
			entity = new ResponseEntity<List<String>>(list, HttpStatus.OK);
		
		}catch(Exception e){
			entity = new ResponseEntity<List<String>>(HttpStatus.BAD_REQUEST);
		}
		
		return entity;
	}
}