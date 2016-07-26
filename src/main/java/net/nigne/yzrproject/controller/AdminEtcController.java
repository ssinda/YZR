package net.nigne.yzrproject.controller;

import java.io.File;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.util.FileCopyUtils;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.ModelAndView;
import net.nigne.yzrproject.domain.ActorVO;
import net.nigne.yzrproject.domain.DirectorVO;
import net.nigne.yzrproject.service.AdminEtcService;

@Controller
public class AdminEtcController {

	@Autowired
	private AdminEtcService service;
	
	@Autowired
	private String uploadPath;
	
	@RequestMapping(value = "/admin/actor/new", method = RequestMethod.GET)
	public ModelAndView adminActorPage() throws Exception {
		
		ModelAndView view=new ModelAndView();
		view.addObject("moviename_list", service.getMoviename());
		view.setViewName("admin/actorWrite");
		return view;
	}
	@RequestMapping(value = "/admin/director/new", method = RequestMethod.GET)
	public ModelAndView adminDirectorPage() throws Exception {
		ModelAndView view=new ModelAndView();
		view.addObject("moviename_list", service.getMoviename());
		view.setViewName("admin/directorWrite");
		return view;
	}
	@RequestMapping(value = "/admin/actor/new", method = RequestMethod.POST)
	public ModelAndView adminWriteActor(MultipartFile file, @RequestParam("movie_id") String movie_id, @RequestParam("actor_name") String actor_name,
			@RequestParam("actor_age") String actor_age, @RequestParam("actor_debut") String actor_debut, @RequestParam("actor_company") String actor_company) throws Exception{
		
		String fileName = actor_name + ".jpg";
		File target = new File("../resources/actor" , fileName);
		FileCopyUtils.copy(file.getBytes(), target);
		
		ActorVO vo=new ActorVO();
		vo.setMovie_id(movie_id);
		vo.setActor_name(actor_name);
		vo.setActor_age(actor_age);
		vo.setActor_photo(fileName);
		vo.setActor_debut(actor_debut);
		vo.setActor_company(actor_company);
		service.actorPersist(vo);
		
		return new ModelAndView("redirect:/admin/actor/new");
	}
	@RequestMapping(value = "/admin/director/new", method = RequestMethod.POST)
	public ModelAndView adminWriteDirector(MultipartFile file, @RequestParam("movie_id") String movie_id, @RequestParam("director_name") String director_name,
			@RequestParam("director_age") String director_age, @RequestParam("director_debut") String director_debut, @RequestParam("director_company") String director_company) throws Exception{
		
		String fileName = director_name + ".jpg";
		File target = new File(uploadPath + "\\director", fileName);
		FileCopyUtils.copy(file.getBytes(), target);
		
		DirectorVO vo= new DirectorVO();
		vo.setMovie_id(movie_id);
		vo.setDirector_name(director_name);
		vo.setDirector_age(director_age);
		vo.setDirector_photo(fileName);
		vo.setDirector_debut(director_debut);
		vo.setDirector_company(director_company);
		service.directorPersist(vo);
		
		return new ModelAndView("redirect:/admin/director/new");
	}
}