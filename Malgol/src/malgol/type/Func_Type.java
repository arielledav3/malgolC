//Arielle Davenport Yovana Gomez
package malgol.type;

import java.util.LinkedList;
import java.util.List;

import malgol.ast.Declaration;
//import malgol.ast.Expression;

//creating new function type to handle all parameters, number of parameters, and comparing parameters
//for fuctioncallexpression and functiond defintion calls.

public class Func_Type extends Type {
	//declaring param numbers, param types, and a type for return type
	private int param_nums;
	private List<Type> params;
	private Type returnType;

	//function type constructor
	public Func_Type() {
		super(-1);
	}
	
	//make a list of all paramter types, doing A GET AND A SET FOR THE STATICANALYSISVISTOR
	public List<Type> get_paramtypes() {
		//RETURN parameter types
		return params;
	}
	
	public void set_paramtypes(List<Type> params) {
		//set current parameters to  parametertypes
		this.params = params;
	}
	
	//if statement comparing number of parameters to give correct amount of params with get and set
	public int get_paramnums() {
		return param_nums;
	}
	
	public void set_paramnums(int param_nums) {
		this.param_nums = param_nums;
	}
	
	//get and set return for fuctions making sure functions have called an int or boolean for return types
	public Type getReturnType() {
		return returnType;
	}
	public void setReturnType(Type returnType) {
		this.returnType = returnType;
	}

	private static final Func_Type unique = new Func_Type();

	public static Func_Type singleton() {
		return unique;
	}

	//seeing if the function types are the same from function and in main
	@Override
	public boolean equals(Type type2) {
		if (!(type2 instanceof Func_Type)) {
			return false;
		}
		// cast type to function type and see if all number of params, types of params, and returns types of function match 
		//to what is being called.
		Func_Type at2 = (Func_Type) type2;
		return 	   (param_nums == at2.param_nums)  && (returnType.equals(at2.returnType)) && (params.equals(at2.params));
	}

	//method boolean to see if it is a function
	@Override
	public boolean isFunction() {
		return true;
	}

	
	@Override
	public Type baseType() {
		return ((Func_Type) this).baseType();
	}

}
