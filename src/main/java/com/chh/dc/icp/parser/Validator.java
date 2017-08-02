package com.chh.dc.icp.parser;

import java.util.List;
import java.util.Map;

public class Validator{

	private List<Condition> conditionList;

	public Validator(){

	}

	/**
	 * @return the conditionList
	 */
	public List<Condition> getConditionList(){
		return conditionList;
	}

	/**
	 * @param conditionList the conditionList to set
	 */
	public void setConditionList(List<Condition> conditionList){
		this.conditionList = conditionList;
	}

	public boolean validate(Map<String,String> oneData){
		//		List<ConditionResult> rsList = new ArrayList<ConditionResult>();
		boolean result = false;
		for(int i = 0; i < conditionList.size(); i++){
			Condition cd = conditionList.get(i);
			ConditionResult rs = calc(oneData, cd);
			if(i == 0){
				result = rs.result;
				continue;
			}
			if("or".equalsIgnoreCase(rs.logic)){
				result = result || rs.result;
			}else{
				result = result && rs.result;
			}
		}
		return result;
	}

	private ConditionResult calc(Map<String,String> oneData, Condition cd){
		ConditionResult rs = new ConditionResult();
		rs.group = cd.group;
		rs.logic = cd.logic;
		rs.result = false;
		if(cd.key == null){
			return rs;
		}
		String data = oneData.get(cd.key);
		if(data == null){
			return rs;
		}
		if("contain".equalsIgnoreCase(cd.symbol)){
			rs.result = data.contains(cd.expression);
		}else if("=".equalsIgnoreCase(cd.symbol)){
			rs.result = data.equals(cd.expression);
		}
		return rs;
	}

	class ConditionResult{

		public String group;

		public boolean result;

		public String logic;

	}

	static class Condition{

		public String group;

		public String key;

		public String expression;

		public String symbol;

		public String logic;

	}
}
