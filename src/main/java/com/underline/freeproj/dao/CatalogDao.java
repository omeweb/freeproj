package com.underline.freeproj.dao;

import java.util.List;

import com.underline.freeproj.domain.Catalog;

/**
 * ÀàÄ¿dao
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
