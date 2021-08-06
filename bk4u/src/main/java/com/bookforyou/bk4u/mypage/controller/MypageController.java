package com.bookforyou.bk4u.mypage.controller;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Random;

import javax.servlet.http.HttpSession;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.ModelAndView;

import com.bookforyou.bk4u.book.model.service.BookService;
import com.bookforyou.bk4u.book.model.vo.Book;
import com.bookforyou.bk4u.common.model.service.MailSendService;
import com.bookforyou.bk4u.common.model.vo.PageInfo;
import com.bookforyou.bk4u.common.template.Pagination;
import com.bookforyou.bk4u.member.model.service.MemberService;
import com.bookforyou.bk4u.member.model.vo.Member;
import com.bookforyou.bk4u.member.model.vo.MemberCategory;
import com.bookforyou.bk4u.member.model.vo.MemberInterest;
import com.bookforyou.bk4u.mypage.model.service.MypageService;
import com.google.gson.Gson;

@Controller
public class MypageController {
	
	@Autowired
	private MypageService mypageService;
	
	@Autowired
	private MemberService memberService;
	
	@Autowired
	private MailSendService mss;

	@Autowired
	private BCryptPasswordEncoder bcryptPasswordEncoder;
	
	MemberInterest memberInterest;
	
	MemberCategory memberCategory;
	
	private Logger log = LoggerFactory.getLogger(MypageController.class);
	

	
	Member member;
	
	/**
	 * 내 정보 페이지로 이동하는 메서드
	 * @author 안세아
	 * @return
	 */
	@RequestMapping("info.mp")
	public String myInfo() {
		return "mypage/myInfo";
	}
	
	/**
	 * 탈퇴 정보 페이지로 이동하는 메서드
	 * @author 안세아
	 * @return
	 */
	@RequestMapping("delete-account-form.mp")
	public String deleteAccountForm() {
		return "mypage/deleteAccount";
	}
	
	/**
	 * 추천 정보 페이지로 이동하는 메서드
	 * @author 안세아
	 * @return
	 */
	@RequestMapping("my-recommend.mp")
	public String updateMyRecommend(HttpSession session, Model model) {
		Member loginUser = (Member) session.getAttribute("loginUser");
		
		return "mypage/myRecommend";
	}
	
	/**
	 * 추천정보 수정하는 메서드
	 * @author 안세아
	 */
	@RequestMapping("update-recommend.mp")
	public String recommedEnroll(Member member,HttpSession session, @RequestParam(value="interestArray")List<String> interestArray,@RequestParam(value="subCategoryArray")List<String> subCategoryArray, Model model ) {
		
		// 기존의 관심사, subCategory삭제
		int delInterestResult = mypageService.deleteAllMemInterest(member);
		int delSubcategoryResult = mypageService.deleteAllMemCategory(member);
		
		// 기존의 관심사와 subCategory가 삭제되면
		//interestArray와 subCategoryArray에서 하나씩 꺼내서 테이블에 넣어주기
		memberInterest = new MemberInterest();
		memberInterest.setMemNo(member.getMemNo());
		for(String interest : interestArray) {
			int interestNo = Integer.parseInt(interest);
			memberInterest.setInterestNo(interestNo);
			int interestResult = memberService.insertMemberInterest(memberInterest);
		}
		
		memberCategory = new MemberCategory();
		memberCategory.setMemNo(member.getMemNo());
		for(String subcategory : subCategoryArray) {
			int subCategoryNo = Integer.parseInt(subcategory);
			memberCategory.setSubCategoryNo(subCategoryNo);
			int categoryResult = memberService.insertMemberCategory(memberCategory);
		}
		// 멤버 객체 직업, 선호난이도 수정 반영해주기
		int updateMemberResult = memberService.updateMemberWorkAndLevel(member);
		if(updateMemberResult > 0) {
			// 다 성공하면 session에 새로운 loginUser반영
			Member loginUser = (Member) session.getAttribute("loginUser");
			Member updateMem = memberService.loginMember(loginUser);
			session.setAttribute("loginUser", updateMem);
			session.setAttribute("alertMsg", "성공적으로 반영되었습니다.");
			return "redirect:my-recommend.mp";
		}else {
			session.setAttribute("alertMsg", "반영에 실패하였습니다.");
			return "redirect:my-recommend.mp";
		}
		
		
		
	}
	
	/**
	 * 멤버의 추천 관심사 리스트를 가져오는 메서드
	 * @author 안세아
	 * @param memNo
	 * @return
	 */
	@ResponseBody
	@RequestMapping(value="interest.mp",produces="application/json; charset=utf-8")
	public String selectMyInterestList(String memNum) {
		
		int memNo = Integer.parseInt(memNum);
		ArrayList<MemberInterest> memberInterestList = mypageService.getMemberInterestList(memNo);
		
		return new Gson().toJson(memberInterestList);
	} 
	
	/**
	 * 멤버의 추천 서브 카테고리를 가져오는 메서드
	 * @author 안세아
	 * @return
	 */
	@ResponseBody
	@RequestMapping(value="subcategory.mp",produces="application/json; charset=utf-8")
	public String selectMySubCategoryList(String memNum) {
		
		int memNo = Integer.parseInt(memNum);
		ArrayList<MemberCategory> mySubCategoryList = mypageService.getSubCategoryList(memNo);
	
		return new Gson().toJson(mySubCategoryList);
	}
	
	/**
	 * 멤버의 직업과 선호 난이도 가져오는 메서드
	 * @author 안세아
	 */
	@ResponseBody
	@RequestMapping(value="my-work-level.mp",produces="application/json; charset=utf-8")
	public String selectMyWorkAndLevel(HttpSession session) {
		
		Member loginUser = (Member) session.getAttribute("loginUser");
	
		return new Gson().toJson(loginUser);
	}
	
	/**
	 * 프로필 이미지 수정하는 메서드
	 * @author 안세아
	 * @param file
	 * @param session
	 * @return
	 * @throws Exception
	 */
	@ResponseBody
	@RequestMapping(value="update-my-profile-img.mp")
	public String uploadProfileImage(MultipartFile file, HttpSession session) throws Exception{
		Member loginUser = null;
		if(!file.getOriginalFilename().equals("")) {
			String changeName = saveFile(session,file);
			loginUser = (Member) session.getAttribute("loginUser");
			loginUser.setOriginImgName(file.getOriginalFilename());
			loginUser.setChangeImgName("resources/member/uploadFiles/"+changeName);
		}
		
		int result = mypageService.updateProfileImg(loginUser);
		if(result > 0) {
			loginUser = memberService.loginMember(loginUser);
			session.setAttribute("loginUser", loginUser);
			
			return "success";
		}else {
			
			return "fail";
			
		}
		
	}
	
	public String saveFile(HttpSession session, MultipartFile file) {
		String savePath = session.getServletContext().getRealPath("/resources/member/uploadFiles/");
		
		String originName = file.getOriginalFilename();
		// 20210702170130(년월일시분초) + 23152(랜덤값) + .jpg(원본파일확장자)
		String currentTime = new SimpleDateFormat("yyyyMMddHHMMSS").format(new Date());
		int ranNum = (int)(Math.random() * + 90000 + 10000);
		String ext = originName.substring(originName.lastIndexOf("."));
		
		String changeName = currentTime + ranNum + ext;
		
		try {
			file.transferTo(new File(savePath + changeName));
		} catch (IllegalStateException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return changeName;
	}
	
	/**
	 * 1번부터 12번 샘플 데이터 비암호화 회원의 패스워드 가져오는 메서드
	 * @author 안세아
	 * @param memNum
	 * @return
	 */
	@ResponseBody
	@RequestMapping(value="get-pwd.mp")
	public String getDecodePassword(String memNum){
		// 1. 1번부터 12번 회원의 패스워드 가져오는 버전
		int memNo = Integer.parseInt(memNum);
		String pwd = memberService.selectMemberPassword(memNo);
		return pwd;
		
	}
	
	/**
	 * 패스워드 암호화해서 업데이트 하는 메서드
	 * @author 안세아
	 * @param memNum
	 * @return
	 */
	@ResponseBody
	@RequestMapping(value="update-pwd.mp")
	public String updatePassword(String memNum, String memPwd,HttpSession session) {
		Member loginUser = (Member) session.getAttribute("loginUser");
		int memNo = Integer.parseInt(memNum);
		String encPwd = bcryptPasswordEncoder.encode(memPwd);
		loginUser.setMemNo(memNo);
		loginUser.setMemPwd(encPwd);
		int result = mypageService.updateMemPassword(loginUser);
		
		if(result > 0) {
			loginUser = memberService.loginMember(loginUser);
			session.setAttribute("loginUser", loginUser);
			return "success";
		}else {
			return "fail";
		}
	}
	
	/**
	 * 기존 패스워드와 사용자가 입력한 패스워드 확인하는 메서드
	 * @param memNum
	 * @param inputLastPwd
	 * @return
	 */
	@ResponseBody
	@RequestMapping(value="match-last-pwd.mp")
	public String matchLastPassword(String memNum, String inputLastPwd) {
		int memNo = Integer.parseInt(memNum);
		String encLastPwd = memberService.selectMemberPassword(memNo);
		if(bcryptPasswordEncoder.matches(inputLastPwd, encLastPwd)) {
			return "success";
		}else {
			return "fail";
		}
	}
	
	/**
	 * 마이페이지 닉네임 업데이트하는 메서드
	 * @author 안세아
	 * @param memNum
	 * @param inputNick
	 * @return
	 */
	@ResponseBody
	@RequestMapping(value="nick-change.mp")
	public String updateMemNickname(String memNum, String inputNick,HttpSession session) {
		member = new Member();
		int memNo =  Integer.parseInt(memNum);
		member.setMemNo(memNo);
		member.setMemNickname(inputNick);
		int result = mypageService.updateMemberNickname(member);
		if(result > 0) {
			Member loginUser = (Member) session.getAttribute("loginUser");
			member = memberService.loginMember(loginUser);
			session.setAttribute("loginUser", member);
			return "success";
		}else {
			return "fail";
		}
	}
	
	/**
	 * 마이페이지 이메일 인증번호 보내기
	 * @author 안세아
	 * @return
	 */
	@ResponseBody
	@RequestMapping(value="certificate-send.mp")
	public String sendCertificateNumberByEmail(String email) {
		Random random = new Random();
		String certificateNumber = Integer.toString(random.nextInt(888888) + 111111);
		
		mss.sendCertificateNumber(certificateNumber, email);
		log.info("certificateNum: " + certificateNumber);
		
		return certificateNumber;
		
		
	}
	
	/**
	 * 마이페이지 이메일 수정하는 메서드
	 * @author 안세아
	 * @param email
	 * @param session
	 * @return
	 */
	@ResponseBody
	@RequestMapping(value="update-email.mp")
	public String updateMemberEmail(String email,HttpSession session) {
		log.info("email: " + email);
		Member member = (Member) session.getAttribute("loginUser");
		member.setMemEmail(email);
		int result = mypageService.updateMemberEmail(member);
		log.info("result: " + result);
		if(result>0) {
			Member loginUser = memberService.loginMember(member);
			session.setAttribute("loginUser", loginUser);
			return "success";
		}else {
			return "fail";
		}
		
	}
	
	/**
	 * 마이페이지 중복확인 필요없는 부가 정보 수정해주는 메서드
	 * @author 안세아
	 * @param memNum
	 * @param memPostInput
	 * @param memBasicAddressInput
	 * @param memDetailAddressInput
	 * @param memAddressReferInput
	 * @param memNameInput
	 * @param memPhoneInput
	 * @param memGenderInput
	 * @param memAgeInput
	 * @return
	 */
	@ResponseBody
	@RequestMapping(value="update-detail-info.mp")
	public String updateMemberDetail(String memNum, String memPostInput, String memBasicAddressInput, String memDetailAddressInput, String memAddressReferInput, String memNameInput, String memPhoneInput, String memGenderInput, String memAgeInput, HttpSession session) {
		Member member = (Member) session.getAttribute("loginUser");
		int memNo = Integer.parseInt(memNum);
		member.setMemNo(memNo);
		member.setMemPost(memPostInput);
		member.setMemBasicAddress(memBasicAddressInput);
		member.setMemDetailAddress(memDetailAddressInput);
		member.setMemAddressRefer(memAddressReferInput);
		member.setMemName(memNameInput);
		member.setMemPhone(memPhoneInput);
		member.setMemGender(memGenderInput);
		member.setMemAge(memAgeInput);
		log.info("수정된 멤버: " + member);
		
		int result = mypageService.updateMemberDetail(member);
		log.info("result값: " +result);
		if(result > 0) {
			Member loginUser = memberService.loginMember(member);
			session.setAttribute("loginUser", loginUser);
			return "success";
		}else {
			return "fail";
		}
	}
	
	/**
	 * 회원 탈퇴 처리하는 메서드
	 * @author 안세아 
	 * @param member
	 * @param session
	 * @param model
	 * @return
	 */
	@RequestMapping("disable-member.mp")
	public String disableMember(Member member, HttpSession session, Model model) {
		int result = mypageService.updateMemberStatusDisable(member);
		if(result > 0) {
			session.removeAttribute("loginUser");
			session.setAttribute("byeMsg", "탈퇴가 처리되었습니다. 그동안 이용해주셔서 감사합니다.");
			return "redirect:/";
		}else {
			model.addAttribute("errorMsg","유효하지 않은 접근입니다.");
			return "common/errorPage";
		}
	}
	
	/**
	 *  보관함 리스트 조회하는 메서드
	 * @param currentPage
	 * @param model
	 * @param session
	 * @return
	 */
	@RequestMapping("my-list.mp")
	public String selectMyList(@RequestParam(value="currentPage", defaultValue="1") int currentPage, Model model,HttpSession session) {
		Member loginUser = (Member) session.getAttribute("loginUser");
		
		int listCount = mypageService.selectMyListCount(loginUser);
		
		PageInfo pi = Pagination.getPageInfo(listCount, currentPage, 10, 5);
		
		HashMap<String,Object> listParam = new HashMap<String,Object>();
		listParam.put("pi", pi);
		listParam.put("member", loginUser);
		
		ArrayList<Book> myList = mypageService.selectMyList(listParam);
		log.info("myList : "+ myList);
		model.addAttribute("pi", pi);
		model.addAttribute("list",myList);
		
		return "mypage/myList";
	}
	
	
	
	
	
	
	
	
}
