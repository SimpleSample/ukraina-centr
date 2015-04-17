package com.nagornyi.uc.common.captcha.http;

import java.io.InputStream;

public interface HttpLoader {

	public String httpPost(String url, String postdata);
	
	public String httpGet(String url);

    public InputStream httpGetAsIN(String url);
}
