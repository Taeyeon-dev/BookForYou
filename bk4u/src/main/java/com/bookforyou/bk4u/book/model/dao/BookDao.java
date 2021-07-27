package com.bookforyou.bk4u.book.model.dao;

import java.util.ArrayList;
import java.util.HashMap;

import org.apache.ibatis.session.RowBounds;
import org.mybatis.spring.SqlSessionTemplate;
import org.springframework.stereotype.Repository;

import com.bookforyou.bk4u.book.model.vo.Book;
import com.bookforyou.bk4u.common.model.vo.PageInfo;

@Repository
public class BookDao {
	
	public int selectAdminListCount(SqlSessionTemplate sqlSession) {
		return sqlSession.selectOne("bookMapper.selectAdminListCount");
	}
	
	public ArrayList<Book> selectAdminBookList(SqlSessionTemplate sqlSession, PageInfo pi) {
		int offset = (pi.getCurrentPage() - 1) * pi.getBoardLimit();
		RowBounds rowBounds = new RowBounds(offset, pi.getBoardLimit());
		return (ArrayList)sqlSession.selectList("bookMapper.selectAdminBookList", null, rowBounds);
	}

	public int selectSearchBookCount(SqlSessionTemplate sqlSession, String condition, String keyword) {
		return sqlSession.selectOne("bookMapper.selectSearchBookCount");
	}

	public ArrayList<Book> selectSearchBook(SqlSessionTemplate sqlSession, PageInfo pi, HashMap<String, String> map) {
		int offset = (pi.getCurrentPage() - 1) * pi.getBoardLimit();
		RowBounds rowBounds = new RowBounds(offset, pi.getBoardLimit());
		return (ArrayList)sqlSession.selectList("bookMapper.selectSearchBook", map, rowBounds);
	}
}
