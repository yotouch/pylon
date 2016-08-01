package weixin.popular.client;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.ResponseHandler;
import org.apache.http.util.EntityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import weixin.popular.util.XMLConverUtil;

public class XmlResponseHandler{

    private static final Logger logger = LoggerFactory.getLogger(XmlResponseHandler.class);
    
	private static Map<String, ResponseHandler<?>> map = new HashMap<String, ResponseHandler<?>>();

	@SuppressWarnings("unchecked")
	public static <T> ResponseHandler<T> createResponseHandler(final Class<T> clazz){
		if(map.containsKey(clazz.getName())){
			return (ResponseHandler<T>)map.get(clazz.getName());
		}else{
			ResponseHandler<T> responseHandler = new ResponseHandler<T>() {
				@Override
				public T handleResponse(HttpResponse response)
						throws ClientProtocolException, IOException {
					int status = response.getStatusLine().getStatusCode();
	                if (status >= 200 && status < 300) {
	                    HttpEntity entity = response.getEntity();
	                    String str = EntityUtils.toString(entity);
	                    logger.info("Pay response " + str);
						logger.info("Pay response " + (new String(str.getBytes("iso-8859-1"), "UTF8")));
	                    Header contentType = response.getEntity().getContentType();
	                    if(contentType!=null&&contentType.toString().matches(".*[uU][tT][fF]-8$")){
	                    	return XMLConverUtil.convertToObject(clazz,str);
	                    }else{
	                    	return XMLConverUtil.convertToObject(clazz,new String(str.getBytes("iso-8859-1"),"utf-8"));
	                    }
	                } else {
	                    throw new ClientProtocolException("Unexpected response status: " + status);
	                }

				}
			};
			map.put(clazz.getName(), responseHandler);
			return responseHandler;
		}
	}

}
