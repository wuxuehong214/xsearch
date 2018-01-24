package com.snp.bd.xsearch.service.impl;


import org.apache.log4j.Logger;

import com.snp.bd.xsearch.service.IResourceService;

public class ResourceServiceImpl implements IResourceService{

	Logger logger =Logger.getLogger("hehehe");
	@Override
	public void search() {
		
		logger.info("请求智能检索接口");
		System.out.println("正在检索");
		
	}

}
