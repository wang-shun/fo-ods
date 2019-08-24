package com.foods.pubsub.dlt;

import java.util.Map;
import java.util.Set;

public class CSAMapWriter {

	public static String separator=",";
	
	public static String writeCSV(Map<String,String> map){
		StringBuffer sb=new StringBuffer();
		
		//Write header
		Set<String> s=map.keySet();
		
		int i=0;
		for (String col:s ){
			sb.append(col);
			if(++i<s.size()){
				sb.append(separator);
			}
		}
		sb.append("\n");
		
		//Write Content
		i=0;
		for (String col:s ){
			if(map.get(col)!=null){
				//System.out.println(String.valueOf(map.get(col)));
				sb.append(String.valueOf(map.get(col)) );
			}else{
				continue;
			}
			if(++i<s.size()){
				sb.append(separator);
			}
		}
		sb.append("\n");
		
		
		return sb.toString();
	}
	
}
