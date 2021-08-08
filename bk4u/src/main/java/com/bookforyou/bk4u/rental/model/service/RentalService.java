package com.bookforyou.bk4u.rental.model.service;

import java.util.ArrayList;
import java.util.HashMap;

import com.bookforyou.bk4u.common.model.vo.PageInfo;
import com.bookforyou.bk4u.rental.model.vo.Rental;

public interface RentalService {

	/*
	 * [사용자] 대여 내역 개수 조회 (연지)
	 */
	int selectRentalCount(int memNo);
	
	/*
	 * [사용자] 대여 내역 조회 (연지)
	 */
	ArrayList<Rental> selectRentalList(PageInfo pi, int memNo);
	
	/*
	 * [사용자] 대여 내역 상세 조회 (연지)
	 */
	Rental selectRental(int rentalNo);

	/*
	 * [사용자] 도서 대여 신청 (연지)
	 */
	int insertRental(HashMap<String, Integer> map);

	/**
	 * [관리자] 전체 대여 목록 개수 조회 (한진)
	 */
	int selectAdminRentalListCount();

	/**
	 * [관리자] '예약중'인 목록 개수 조회 (한진)
	 */
	int selectAdminReserveListCount();
	
	/**
	 * [관리자] '대여중'인 목록 개수 조회 (한진)
	 */
	int selectAdminRentalIngListCount();
	
	/**
	 * [관리자] '반납완료'인 목록 개수 조회 (한진)
	 */
	int selectAdminReturnListCount();
	
	/**
	 * [관리자] '연체'인 목록 개수 조회
	 */
	int selectAdminOverdueListCount();
	
	/**
	 * [관리자] '예약취소'인 목록 개수 조회
	 */
	int selectAdminRentalCancelListCount();
	
	/**
	 * [관리자] 대여 목록 조회 (한진)
	 */
	ArrayList<Rental> selectAdminRentalList(PageInfo pi, HashMap<String, Integer> filter);


}
