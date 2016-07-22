package malgol.transform;

import java.util.ArrayList;
import java.util.List;


import malgol.ast.*;
import malgol.common.*;
import malgol.type.*;
import malgol.util.Error;

/**
 * 
 * @author WMarrero
 */

//Arielle Davenport & Yovana Gomez
public class StaticAnalysisVisitor implements ASTVisitor {
	private SymbolTable symbolTable = null;
	private  Type  returnType;

	@Override
	public void visit(BlockStatement s) {
		symbolTable.createNewScope();
		for (Declaration d : s.getDeclarationList()) {
			d.accept(this);
		}
		for (Statement stat : s.getStatementList()) {
			stat.accept(this);
		}
		symbolTable.dropScope();
	}

	@Override
	public void visit(AssignmentStatement s) {
		s.getLeft().accept(this);
		if (s.getLeft().getType().isArray()) {
			Error.msg("Cannot assign into an array", s);
		}
		s.getRight().accept(this);
		if (!(s.getLeft().getType().equals(s.getRight().getType()))) {
			Error.msg("Type mismatch in", s);
		}
	}

	@Override
	public void visit(SelectionStatement s) {
		s.getTest().accept(this);
		if (s.getTest().getType() != BoolType.singleton()) {
			Error.msg("if test is not boolean:", s.getTest());
		}
		s.getTrueBranch().accept(this);
		s.getFalseBranch().accept(this);
	}

	@Override
	public void visit(WhileStatement s) {
		s.getTest().accept(this);
		if (s.getTest().getType() != BoolType.singleton()) {
			Error.msg("while test is not boolean:", s.getTest());
		}
		s.getBody().accept(this);
	}

	@Override
	public void visit(PrintStatement s) {
		s.getExpression().accept(this);
		if (s.getExpression().getType() != IntType.singleton()) {
			Error.msg("print expression must be integer type", s);
		}
	}

	@Override
	public void visit(SkipStatement s) {
	}

	@Override
	public void visit(ArrayExpression e) {
		e.getArray().accept(this);
		Type t = e.getArray().getType();
		if (!t.isArray()) {
			Error.msg("Not an array", e);
		}
		ArrayType aType = (ArrayType) t;
		e.setType(aType.getElementType());
		e.getIndex().accept(this);
		if (!e.getIndex().getType().isInt()) {
			Error.msg("Array index is not an int", e);
		}
	}

	@Override
	public void visit(BinaryExpression e) {
		e.getLeft().accept(this);
		e.getRight().accept(this);
		if (e.getOperator().isArithmetic()) {
			if (e.getLeft().getType() != IntType.singleton()) {
				Error.msg("Expected integer:", e.getLeft());
			}
			if (e.getRight().getType() != IntType.singleton()) {
				Error.msg("Expected integer:", e.getRight());
			}
			e.setType(IntType.singleton());
		} else if (e.getOperator().isRelational()) {
			if (e.getLeft().getType() != IntType.singleton()) {
				Error.msg("Expected integer:", e.getLeft());
			}
			if (e.getRight().getType() != IntType.singleton()) {
				Error.msg("Expected integer:", e.getRight());
			}
			e.setType(BoolType.singleton());
		} else if (e.getOperator().isBoolean()) {
			if (e.getLeft().getType() != BoolType.singleton()) {
				Error.msg("Expected Boolean:", e.getLeft());
			}
			if (e.getRight().getType() != BoolType.singleton()) {
				Error.msg("Expected Boolean:", e.getRight());
			}
			e.setType(BoolType.singleton());
		} else {
			Error.msg("unknown operator:", e);
		}
	}

	@Override
	public void visit(UnaryExpression e) {
		e.getExpression().accept(this);
		Type type = e.getExpression().getType();
		if (e.getOperator().isBoolean()) {
			if (type != BoolType.singleton()) {
				Error.msg("Expected Boolean:", e.getExpression());
			}
			e.setType(BoolType.singleton());
		} else if (e.getOperator().isArithmetic()) {
			if (type != IntType.singleton()) {
				Error.msg("Expected integer:", e.getExpression());
			}
			e.setType(IntType.singleton());
		}
	}

	@Override
	public void visit(VariableExpression e) {
		if (e.getType() == null) {
			Symbol sym = symbolTable.lookupInAllScopes(e.getName());
			if (sym == null) {
				Error.msg("Undeclared varaible: ", e);
			}
			Type t = sym.getType();
			e.setType(t);
		}
	}

	@Override
	public void visit(ConstantExpression e) {
		if (e.getType() == null) {
			Error.msg(
					"FATAL ERROR: Constant of unkown type.  Contact instructor",
					e);
		}
	}

	@Override
	public void visit(Declaration d) {
		String name = d.getName();
		if (symbolTable.lookupInCurrentScope(name) != null) {
			Error.msg(d.getName() + " already declared in this scope!!!", d);
		}
		Type type = d.getType();
		Symbol sym = Symbol.newVariableSymbol(name, type, false);
		symbolTable.insert(sym);
	}

	@Override
	public void visit(GotoStatement s) {
		throw new RuntimeException(
				"GotoStatement encountered during static analysis");
	}

	@Override
	public void visit(DereferenceExpression e) {
		throw new RuntimeException(
				"DereferenceExpression encountered during static analysis");
	}

	@Override
	public void visit(OffsetExpression e) {
		throw new RuntimeException(
				"OffsetExpression encountered during static analysis.");
	}

	//STart of code that needs to be changed
	@Override
	public void visit(FunctionDefinition f) {
		//get functiondefinition name and convert to string, look up in table to see if the name already exists
		String name = f.getName();
		if (symbolTable.lookupInCurrentScope(name) != null) {
			Error.msg("Sorry, this has already been declared" + name + ", " , f);
		}

		//get  array list for parameter types run loop through each parameter in function definition and add types to that array list
		List<Type> paramTypes = new ArrayList<>();
		for (Declaration param : f.getParameters()) {
			paramTypes.add(param.getType());
		}

		//set returntype, number of parameters, and parameter types
		Func_Type type = new Func_Type();
		type.setReturnType(f.getReturnType());
		type.set_paramnums(f.getParameters().size());
		type.set_paramtypes(paramTypes);

		//now get that return type
		returnType = f.getReturnType();

		//insert into table
		Symbol s2 = Symbol.newFunctionSymbol(name, type);
		symbolTable.insert(s2);

		//push into table
		symbolTable.createNewScope();
		for (Declaration d : f.getParameters()) {
			d.accept(this);
		}
		f.getBody().accept(this);
		symbolTable.dropScope();
	}

	@Override
	public void visit(FunctionCallExpression e) {
		String func_name = e.getName();
		Symbol s = symbolTable.lookupInAllScopes(func_name);
		//if name is null then  send error message
		if (s == null) {
			Error.msg("Unknown Function: "+ func_name + ", " , e);
		} else if (!s.isFunction()) {
			//if its  not a function
			Error.msg("This is not a function " , e);
		}

		//function get the function name and type
		Func_Type type = (Func_Type)symbolTable.lookupInAllScopes(e.getName()).getType();
		//set type of return type
		e.setType(type.getReturnType());
		//see if size of arguments in parameters
		if (e.getArguments().size() != type.get_paramnums()) {
			Error.msg("Argument count doesn't match", e);
		}

		// Accept each parameter & check its type
		// check arg expressions to see if argument types are matching
		List<Type> argTypes = type.get_paramtypes();
		List<Expression> args = e.getArguments();
		//create loop for all arguments to go through and see if argument types match
		for (int i=0;i<args.size(); i++) {
			args.get(i).accept(this);
			if (!args.get(i).getType().toString().equals(argTypes.get(i).toString())) {
				Error.msg("Argument type doesn't match in selection " + Integer.toString(i+1) + " of ", e);
			}
		}
	}
	

	@Override
	public void visit(ReturnStatement s) {
		// TODO
		s.getExpression().accept(this);
		//must have same type of return statement
		if(!s.getExpression().getType().toString().equals(returnType.toString())){
			Error.msg("Return Type Mismatch, ", s);
		}
		//throw new RuntimeException("You need to implement this.");
	}

	@Override
	public void visit(Program p) {
		// TODO
		symbolTable = new SymbolTable();
		symbolTable.createNewScope();
		for(FunctionDefinition f : p.getFunctionList()){
				f.accept(this);
		}
		//throw new RuntimeException("You need to implement this.");
		symbolTable.dropScope();
		
		/* OLD DEFINITION BELOW
		symbolTable = new SymbolTable();
		p.getBlockStatement().accept(this);
		*/
	}

	
}
