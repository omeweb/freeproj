package com.taobao.freeproj.dao;

import java.util.List;

import com.taobao.freeproj.domain.Catalog;

/**
 * ��Ŀdao
 * 
 * @author xialei.cg
 */
public interface CatalogDao {
	public long add(Catalog catalog);

	public int update(Catalog catalog);

	public int deleteAll(String code);

	public List<Catalog> getSibling(String code, int level);

	public List<Catalog> getAll();

	public List<Catalog> getAllEx(String code);

	public Catalog getOne(String code);

}
