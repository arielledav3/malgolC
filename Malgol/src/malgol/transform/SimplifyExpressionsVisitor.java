/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package malgol.transform;

import malgol.ast.*;

import malgol.common.Operator;
import malgol.type.*;

import java.util.*;

//Arielle Davenport and Yovana Gomez

//public class SimplifyExpressionsVisitor extends RemoveStructuredControlVisitor {}


public class SimplifyExpressionsVisitor implements ASTVisitor {
    private Expression expressionResult = null;
    private LinkedList<Declaration> newDeclarations = null;
    private LinkedList<Statement> newStatements = null;
    private Program programResult = null;
    private FunctionDefinition func_def = null;
    private BlockStatement newBlock = null;
    
    public Program getResult() {
    	return programResult;
    }
    
    private void freshResultVariables() {
    	expressionResult = null;
    	newDeclarations = new LinkedList<Declaration>();
    	newStatements = new LinkedList<Statement>();
    }

    @Override
    public void visit(BlockStatement b) {
        LinkedList<Statement> tempS = new LinkedList<Statement>();
        LinkedList<Declaration> tempD = new LinkedList<Declaration>(b.getDeclarationList());
        for(Statement s : b.getStatementList()) {
            s.accept(this);
            tempS.addAll(newStatements);
            tempD.addAll(newDeclarations);
        }
        BlockStatement newBlock = new BlockStatement(null, tempD, tempS);
        newBlock.addLabels(b.getLabels());
        freshResultVariables();
        newStatements.add(newBlock);
    }

    @Override
    public void visit(AssignmentStatement s) {
        s.getLeft().accept(this);
        LinkedList<Statement> leftStatements = newStatements;
        LinkedList<Declaration> leftDeclarations = newDeclarations;
        Expression leftExpression = expressionResult;
        s.getRight().accept(this);
        leftStatements.addAll(newStatements);
        leftDeclarations.addAll(newDeclarations);
        newStatements = leftStatements;
        newDeclarations = leftDeclarations;
        Statement newAssignment = new AssignmentStatement(null, leftExpression, expressionResult);
        newStatements.add(newAssignment);
        convertToSingleStatement();
        newStatements.get(0).addLabels(s.getLabels());
    }

    
    @Override
    public void visit(SelectionStatement s) { 
        assert(s.getTrueBranch() instanceof GotoStatement)
                : "True branch is not a goto during expression simplification.";
        assert(s.getFalseBranch() instanceof SkipStatement)
                : "False branch is not a skip during expression simplification.";        
        // NOTE: The true branch should be a GotoStatement and
        //       the false branch should be a SkipStatement.
        s.getTest().accept(this);
        SelectionStatement newIf =
                new SelectionStatement(null, expressionResult, s.getTrueBranch(), s.getFalseBranch());
        newStatements.add(newIf);
        convertToSingleStatement();
        newStatements.get(0).addLabels(s.getLabels());
    }

    @Override
    public void visit(WhileStatement s) {
        // NOTE: There should be no WhileStatements at this point!!!
        
        assert(false) : "While statement encountered during expression simplificaiton.";
        //throw new RuntimeException("There should be no while loop at this point!");
    }

    @Override
    public void visit(PrintStatement s) {
        s.getExpression().accept(this);
        Statement newPrint = new PrintStatement(null, expressionResult);
        newStatements.add(newPrint);
        convertToSingleStatement();
        newStatements.get(0).addLabels(s.getLabels());    
    }

    @Override
    public void visit(SkipStatement s) {
        freshResultVariables();
        newStatements.add(s);
    }

    @Override
    public void visit(GotoStatement s) {
    	freshResultVariables();
        newStatements.add(s);
    }

    @Override
    public void visit(ArrayExpression e) {
        VariableExpression location = VariableExpression.freshTemporary("LOCATION");
        location.setType(LocationType.singleton());
        VariableExpression index = VariableExpression.freshTemporary("INDEX");
        index.setType(IntType.singleton());
        LinkedList<Declaration> tempDeclarations = new LinkedList<Declaration>();
        LinkedList<Statement> tempStatements = new LinkedList<Statement>();
        Expression pointer = e;
        while (pointer instanceof ArrayExpression) {
            ArrayExpression ptr = (ArrayExpression) pointer;
            ptr.getIndex().accept(this);
            Expression scaleIndex = new BinaryExpression(null, Operator.TIMES, expressionResult,
            							new ConstantExpression(null, ptr.getType().getByteSize()));
            scaleIndex.setType(IntType.singleton());
            Statement scaleStatement = new AssignmentStatement(null, index, scaleIndex);
            //Expression offset = new BinaryExpression(Operator.PLUS, location, index);
            Expression offset = new OffsetExpression(location, index);
            offset.setType(LocationType.singleton());
            Statement offsetStatement = new AssignmentStatement(null, location, offset);
            tempStatements.add(0, offsetStatement);
            tempStatements.add(0, scaleStatement);
            tempDeclarations.addAll(0, newDeclarations);
            tempStatements.addAll(0, newStatements);
            pointer = ptr.getArray();
        }
        newStatements = tempStatements;
        newStatements.add(0, new AssignmentStatement(null, location, pointer));
        newDeclarations = tempDeclarations;
        newDeclarations.add(0, new Declaration(null, index.getName(), IntType.singleton()));
        newDeclarations.add(0, new Declaration(null, location.getName(), LocationType.singleton()));
        expressionResult = new DereferenceExpression(location);
        expressionResult.setType(e.getType());
    }

    @Override
    public void visit(BinaryExpression e) {
        e.getLeft().accept(this);
        LinkedList<Statement> leftStatements = newStatements;
        LinkedList<Declaration> leftDeclarations = newDeclarations;
        Expression leftExpression = expressionResult;
        e.getRight().accept(this);
        newStatements.addAll(0, leftStatements);
        newDeclarations.addAll(0, leftDeclarations);
        VariableExpression newVar = VariableExpression.freshTemporary("BinaryOp");
        newVar.setType(e.getType());
        Expression newExpression = new BinaryExpression(null, e.getOperator(), leftExpression, expressionResult);
        newStatements.add(new AssignmentStatement(null, newVar, newExpression));
        newDeclarations.add(new Declaration(null, newVar.getName(), e.getType()));
        expressionResult = newVar;
    }

    @Override
    public void visit(UnaryExpression e) {
        e.getExpression().accept(this);
        VariableExpression newVar = VariableExpression.freshTemporary("UnaryOp");
        newVar.setType(e.getType());
        Expression newExpression = new UnaryExpression(null, e.getOperator(), expressionResult);
        newStatements.add(new AssignmentStatement(null, newVar, newExpression));
        newDeclarations.add(new Declaration(null, newVar.getName(), e.getType()));
        expressionResult = newVar;
    }

    @Override
    public void visit(VariableExpression e) {
    	freshResultVariables();
        expressionResult = e;
    }

    @Override
    public void visit(ConstantExpression e) {
    	freshResultVariables();
        expressionResult = e;
    }

    @Override
    public void visit(Declaration d) {
        // This should never be called!!!
        assert(false) : "Attempting to simplify declaration during expression simplification";
    }
    
    private void convertToSingleStatement() {
        if (newStatements.size() > 1 || newDeclarations.size() > 0) {
        		LinkedList<Statement> temp = new LinkedList<Statement>();
        		temp.add(new BlockStatement(null, newDeclarations, newStatements));
        		newStatements = temp;
            newDeclarations = new LinkedList<Declaration>();
        }
        expressionResult = null;
    }

    @Override
    public void visit(DereferenceExpression e) {
        // There should not be any pointers yet!
        assert(false) : "Dereference found DURING expression simplification";
    }
    
    @Override
    public void visit(OffsetExpression e) {
        // There should not be any pointers yet!
        assert(false) : "OffsetExpression found DURING expression simplification";
    }
    
    //STAGE 4
    
    @Override
    public void visit(FunctionDefinition f) {
    	// TODO
    	//throw new RuntimeException("You need to implement this.");
    	//GET BODY of function
    		f.getBody().accept(this);
    		//BlockStatement newBlock = 
    		
    		func_def =  new FunctionDefinition(f.getFirstToken(),f.getReturnType(),f.getName(),f.getParameters(),newBlock);
    		
    		//declare statements , declarations , and expressionResult null
    		//newStatements = null;
    		//newDeclarations = null;
    		//expressionResult = null;
    	
    	
    }

    @Override
    public void visit(FunctionCallExpression e) {
    	// TODO
    	freshResultVariables();
    //	throw new RuntimeException("You need to implement this.");
    	//declare linked list for all expressions, statements, and decls in function call
    	LinkedList<Expression> expr_arg = new LinkedList<>();
    	LinkedList<Statement> state_arg = new LinkedList<>();
    	LinkedList<Declaration> decl_arg = new LinkedList<>();
    	
    	//load each argment with expressions from loop
    	for(Expression exp_arg : e.getArguments()){
    		exp_arg.accept(this);
    		expr_arg.add(expressionResult);
    		state_arg.addAll(newStatements);
    		decl_arg.addAll(newDeclarations);
    	}//end loop
    		
    		freshResultVariables();
    		
    		//set values to global variables
    		newStatements = state_arg;
    		newDeclarations = decl_arg;

    		VariableExpression var = VariableExpression.freshTemporary("functionCall");
    		
    		var.setType(e.getType());
    		// add to assigment and decls types 
    		FunctionCallExpression func_call = new FunctionCallExpression(null, e.getName(), expr_arg);
    		newStatements.add(new AssignmentStatement(null,var, func_call));
    		newDeclarations.add(new Declaration(null, var.getName(), e.getType()));
    		expressionResult = var;
    		
    	
    	
    }

    @Override
    public void visit(ReturnStatement s) {
    	// TODO
    //	throw new RuntimeException("You need to implement this.");
    	//grab return statement
    	s.getExpression().accept(this);
    	Statement new_return_stat = new ReturnStatement(null, expressionResult);
    	newStatements.add(new_return_stat);
    	convertToSingleStatement();
    	newStatements.get(0).addLabels(s.getLabels());
    	
    }

    @Override
	public void visit(Program p) {
    	// TODO
    	//throw new RuntimeException("You need to implement this.");
    	 LinkedList<FunctionDefinition> r_list = new LinkedList<>();
    	 
    	 for (FunctionDefinition func : p.getFunctionList()){
    		 func.accept(this);
    		 r_list.add(func_def);
    	 }
    	 
    	 programResult = new Program(r_list);
    	
    	/* OLD DEFINITION BELOW
    	p.getBlockStatement().accept(this);
		assert(newDeclarations.size() == 0);
		assert(newStatements.size() == 1);
		BlockStatement temp = (BlockStatement) newStatements.get(0);
		programResult = new Program(temp);
		*/
	}
}
