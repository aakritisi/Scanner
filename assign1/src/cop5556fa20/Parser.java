/**
 * Parser for the class project in COP5556 Programming Language Principles 
 * at the University of Florida, Fall 2020.
 * 
 * This software is solely for the educational benefit of students 
 * enrolled in the course during the Fall 2020 semester.  
 * 
 * This software, and any software derived from it,  may not be shared with others or posted to public web sites,
 * either during the course or afterwards.
 * 
 *  @Beverly A. Sanders, 2020
 *
 */


package cop5556fa20;

import static cop5556fa20.Scanner.Kind.*;

import java.util.ArrayList;
import java.util.List;

import cop5556fa20.Scanner.Kind;
import cop5556fa20.Scanner.LexicalException;
import cop5556fa20.Scanner.Token;
import cop5556fa20.SimpleParser.SyntaxException;
import cop5556fa20.AST.ASTNode;
import cop5556fa20.AST.Dec;
import cop5556fa20.AST.DecVar;
import cop5556fa20.AST.ExprIntLit;
import cop5556fa20.AST.ExprStringLit;
import cop5556fa20.AST.Expression;
import cop5556fa20.AST.Program;
import cop5556fa20.AST.Type;
import cop5556fa20.AST.Statement;
import cop5556fa20.AST.*;

public class Parser {

	@SuppressWarnings("serial")
	public static class SyntaxException extends Exception {
		final Token token;  //the token that caused an error to be discovered.

		public SyntaxException(Token token, String message) {
			super(message);
			this.token = token;
		}

		public Token token() {
			return token;
		}

	}


	Scanner scanner;
	Token t;

	Parser(Scanner scanner) {
		this.scanner = scanner;
		t = scanner.nextToken(); // establish invariant that t is always the next token to be processed
	}

	public Program parse() throws SyntaxException, LexicalException {
		Program p = program();
		matchEOF();
		return p;
	}

	private static final Kind[] firstProgram = {KW_int, KW_string, KW_image, IDENT}; //this is not the correct FIRST(Program...), but illustrates a handy programming technique

	private Program program() throws SyntaxException, LexicalException {
		Token first = t; //always save the current token.  
		List<ASTNode> decsAndStatements = new ArrayList<ASTNode>();
		while (isKind(firstProgram)) {
			switch (t.kind()) {
			case KW_int, KW_string, KW_image -> {
				Dec dec = declaration();
				decsAndStatements.add(dec);
				match(SEMI);
			}
			
			//Your finished parser should NEVER throw UnsupportedOperationException, but it is convenient as a placeholder for unimplemented features.
			default -> {
				Statement stmt = statement();
				decsAndStatements.add(stmt);
				match(SEMI);
			}
		}
		}
		return new Program(first, decsAndStatements);  //return a Program object
	}
	
	public Dec declaration() throws SyntaxException, LexicalException {
		Token first = t;
		Dec dec;
		if(t.kind() == KW_int ||t.kind() == KW_string)
			 dec = variableDeclaration();
		else if(t.kind() == KW_image)
			 dec = imageDeclaration();
		else
			throw new SyntaxException(t , "Syntax error");
		return dec;
	}
	
	public DecVar variableDeclaration() throws SyntaxException, LexicalException {
		Token first = t;
		Type type;
		Expression e;
		if (isKind(KW_int)) {
			consume();
			 type = Type.Int;
		}
		else {
			consume();
			 type = Type.String;
		}
		Token name = match(IDENT);
		if (isKind(ASSIGN)) {
			consume();
			 e = expression();
		}
		else
			 e = Expression.empty;
		
		return new DecVar(first, type, scanner.getText(name), e);
		
		
	}
	
	public DecImage imageDeclaration() throws SyntaxException, LexicalException {
		Token first = t;
		Expression e0;
		Expression e1;
		Expression e2 = Expression.empty;;
		Kind op = Kind.NOP;
		
			consume();
			if(t.kind() == LSQUARE)
			{
				
				consume();
				e0 = expression();
				if(t.kind() == COMMA)
				{
					consume();
					e1 = expression();
					if(t.kind() == RSQUARE)
					{
						consume();
						
						
					}
					else
						throw new SyntaxException(t , "Syntax error");
					
				}
				else
					throw new SyntaxException(t , "Syntax error");
				
				
			}
			else
			{
				e0 = Expression.empty;
				e1 = Expression.empty;
			}
			
			Token name = match(IDENT);
			
			if(t.kind() == LARROW || t.kind() == ASSIGN)
			{
					op = t.kind();
					consume();
					e2 = expression();
			}
			
		
		
		
		return new DecImage(first, Type.Image, scanner.getText(name), e0, e1, op, e2);
		
	}

	public Statement statement() throws SyntaxException, LexicalException {
		Token first = t;
		Token name = match(IDENT);
		Statement st;
		if(t.kind() == RARROW )
		{
			consume();
			if(isKind(KW_SCREEN))
			{
				consume();
				st = imageOutScreenStmt(first,name);
			}
			else
			{
				st = imageOutStatement(first,name);
			}
			
		} 
		else if(t.kind() == LARROW)
		 st = imageInStatement(first,name);
		else if(t.kind() == ASSIGN)
		{
			consume();
			if(isKind(STAR))
			{
				consume();
				st = loopStatement(first,name);
			}
			else
			{
				st = assingmentStatement(first,name);
			}
			
		}
		 
		else
			throw new SyntaxException(t , "Syntax error");
		
		return st;
		
	}
	
	public StatementOutFile imageOutStatement(Token first, Token name) throws SyntaxException, LexicalException {
		
		Expression e = expression();
	
		return new StatementOutFile(first, scanner.getText(name), e);
	}
	
	
	public StatementOutScreen imageOutScreenStmt(Token first, Token name) throws SyntaxException, LexicalException {
		
		Expression x = Expression.empty;
		Expression y = Expression.empty;
		
		if(isKind(LSQUARE))
		{
			consume();
			x = expression();
			if(isKind(COMMA))
			{
				consume();
				y = expression();
				match(RSQUARE);
			}
			else
				throw new SyntaxException(t , "Syntax error");	
		}
	
		return new StatementOutScreen(first, scanner.getText(name), x, y);
	}
	
	public StatementImageIn imageInStatement(Token first, Token name) throws SyntaxException, LexicalException {
		Expression src;
		if(t.kind() == LARROW)
		{
			consume();
			src = expression();
		}
		else
			throw new SyntaxException(t , "Syntax error");
		
		return new StatementImageIn(first, scanner.getText(name), src);
			
	}
	
	public StatementAssign assingmentStatement(Token first, Token name) throws SyntaxException, LexicalException {
		Expression e = expression();
		
		return new 	StatementAssign(first, scanner.getText(name), e);
		
	}
	
	public StatementLoop loopStatement(Token first, Token name) throws SyntaxException, LexicalException {
		Expression cond = Expression.empty;
		Expression e ;
		
			constXYSelector();
			if(t.kind() == COLON)
			{
				consume();
				if(t.kind() == PLUS || t.kind() == MINUS || t.kind() == EXCL || t.kind() == INTLIT || t.kind() == IDENT || t.kind() == LPAREN || t.kind() == STRINGLIT || t.kind() == KW_X || t.kind() == KW_Y || t.kind() == CONST || t.kind() == LPIXEL || t.kind() == AT)
				{
					cond = expression();
				}
				if(t.kind() == COLON)
				{
					consume();
					e = expression();
				}
				else
					throw new SyntaxException(t , "Syntax error");
			}
			else
				throw new SyntaxException(t , "Syntax error");
				
			
			return new 	StatementLoop(first, scanner.getText(name), cond, e);
		
	}
	
	
	//expression has package visibility (rather than private) to allow tests to call expression directly  
	protected Expression expression() throws SyntaxException, LexicalException {
		Token first = t;
		Expression e0 = orExpression();
		Expression e1;
		Expression e2;
		if(t.kind() == Q)
		{
			consume();
			e1 = expression();
			if(t.kind() == COLON)
			{
				consume();
				e2 = expression();
			}
			else
				throw new SyntaxException(t , "Syntax error");
			return new ExprConditional(first, e0, e1, e2);
		}
		else
		{
			return e0;
		}
		
	}

	public Expression orExpression() throws SyntaxException, LexicalException {
		Token first = t;
		Expression e0 = andExpression();
		Expression e1;
		while(t.kind() == OR)
		{
			consume();
			e1 = andExpression();
			e0 = new ExprBinary(first,e0,OR,e1);
			
		}
		return e0;
	}
	
	public Expression andExpression() throws SyntaxException, LexicalException {
		Token first = t;
		Expression e0 = eqExpression();
		Expression e1;
		while(t.kind() == AND)
		{
			consume();
			e1 = eqExpression();
			e0 = new ExprBinary(first,e0,AND,e1);
		}
		return e0;
	}
	
	
	public Expression eqExpression() throws SyntaxException, LexicalException {
		Token first = t;
		Expression e0 = relExpression();
		Expression e1;
		Kind op;
		while(t.kind() == EQ || t.kind() == NEQ)
		{
			op = t.kind();
			consume();
			e1 = relExpression();
			e0 = new ExprBinary(first,e0,op,e1);
		}
		return e0;
	}
	
	public Expression relExpression() throws SyntaxException, LexicalException {
		Token first = t;
		Expression e0 = addExpression();
		Expression e1;
		Kind op;
		while(t.kind() == LT || t.kind() == GT || t.kind() == LE || t.kind() == GE)
		{
			op = t.kind();
			consume();
			e1 = addExpression();
			e0 = new ExprBinary(first,e0,op,e1);
		}
		return e0;
	}
	
	public Expression addExpression() throws SyntaxException, LexicalException {
		Token first = t;
		Expression e0 = multExpression();
		Expression e1;
		Kind op;
		while(t.kind() == PLUS || t.kind() == MINUS)
		{
			op = t.kind();
			consume();
			e1 = multExpression();
			e0 = new ExprBinary(first,e0,op,e1);
		}
		return e0;
	}
	
	public Expression multExpression() throws SyntaxException, LexicalException {
		Token first = t;
		Expression e0 = unaryExpression();
		Expression e1;
		Kind op;
		while( t.kind() == STAR || t.kind() == DIV|| t.kind() == MOD)
		{
			op = t.kind();
			consume();
			e1 = unaryExpression();
			e0 = new ExprBinary(first,e0,op,e1);
		}
		return e0;
	}
	
	public Expression unaryExpression() throws SyntaxException, LexicalException {
		Token first = t;
		Expression e1;
		Kind op;
		if(t.kind() == PLUS || t.kind() == MINUS)
		{
			op = t.kind();
			consume();
			e1 = unaryExpression();
			return new ExprUnary(first, op, e1);
		}
		else
			return unaryExpressionNotPlusMinus();
	}
	
	public Expression unaryExpressionNotPlusMinus() throws SyntaxException, LexicalException {
		Token first = t;
		Expression e1;
		Kind op;
		if(t.kind() == EXCL)
		{
			op = t.kind();
			consume();
			e1 = unaryExpression();
			return new ExprUnary(first, op, e1);
		}
		else
			return hashExpression();
	}
	
	public Expression hashExpression() throws SyntaxException, LexicalException {
		Token first = t;
		Expression e = primary();
		String attr;
		while(t.kind() == HASH)
		{
			consume();
			attr = attribute();
			e = new ExprHash(first, e, attr);
		}
		return e;
	}
	
	public String attribute() throws SyntaxException, LexicalException {
		String att;
		if(t.kind() == KW_WIDTH || t.kind() == KW_HEIGHT|| t.kind() == KW_RED || t.kind() == KW_GREEN || t.kind() == KW_BLUE)
		{
			att = scanner.getText(t);
			consume();
		}
		else
			throw new SyntaxException(t , "Syntax error");
		return att;
	}
	
	private Expression primary() throws SyntaxException, LexicalException {
		Token first = t;
		Expression e;
		if(t.kind() == INTLIT) {
			int value = scanner.intVal(t);
			consume();
			e =  new ExprIntLit(first, value);
		}
		else if(t.kind() == STRINGLIT) {
			String text = scanner.getText(t);
			consume();
			e =  new ExprStringLit(first, text);
		}
		else if(t.kind() == IDENT || t.kind() == KW_X || t.kind() == KW_Y)
		{
			String text = scanner.getText(t);
			consume();
			e = new ExprVar(first, text);
		}
		else if(t.kind() == LPAREN){
			consume();
			e = expression();
			if(t.kind() == RPAREN)
			{
				consume();
			}
			else
				throw new SyntaxException(t , "Syntax error");
			
		}
		else if(t.kind() == CONST) {
			int value = scanner.intVal(t);
			String text = scanner.getText(t);
			consume();
			e = new ExprConst(first, text, value);
		}
		else if(t.kind() == LPIXEL) {
			
			e = pixelConstructor(first);
		}
		else if(t.kind() == AT) {
			e = argExpression(first);
		}
		else
			throw new SyntaxException(t , "Syntax error");
		
		if(t.kind() == LSQUARE)
			e = pixelSelector(first,e);
		
		return e;
	}
	
	public ExprPixelSelector pixelSelector(Token first, Expression e) throws SyntaxException, LexicalException {
		Expression e0;
		Expression e1;
		if(t.kind() == LSQUARE)
		{
			consume();
			e0 = expression();
			if(t.kind() == COMMA)
			{
				consume();
				e1 = expression();
				if(t.kind() == RSQUARE)
					consume();
				else
					throw new SyntaxException(t , "Syntax error");
			}
			else
				throw new SyntaxException(t , "Syntax error");
		}
		else
			throw new SyntaxException(t , "Syntax error");
		
		return new ExprPixelSelector(first, e, e0, e1);
		
	}
	
	public ExprArg argExpression(Token first) throws SyntaxException, LexicalException {
		Expression e;
		if(t.kind() == AT)
		{
			consume();
			e = primary();
		}
		else
			throw new SyntaxException(t , "Syntax error");
		return new ExprArg(first,e);
	}
	
	public ExprPixelConstructor pixelConstructor(Token first) throws SyntaxException, LexicalException {
		Expression e0;
		Expression e1;
		Expression e2;
		if(t.kind() == LPIXEL)
		{
			consume();
			e0 = expression();
			if(t.kind() == COMMA)
			{
				consume();
				e1 = expression();
				if(t.kind() == COMMA)
				{
					consume();
					e2 = expression();
					if(t.kind() == RPIXEL)
						consume();
					else
						throw new SyntaxException(t , "Syntax error");
				}
				else
					throw new SyntaxException(t , "Syntax error");
			}
			else
				throw new SyntaxException(t , "Syntax error");
			
		}
		else
			throw new SyntaxException(t , "Syntax error");
		
		return new ExprPixelConstructor(first, e0, e1, e2);
	}
	
	
	public void constXYSelector() throws SyntaxException, LexicalException {
		if(t.kind() == LSQUARE)
		{
			consume();
			if(t.kind() == KW_X)
			{
				consume();
				if(t.kind() == COMMA)
				{
					consume();
					if(t.kind() == KW_Y)
					{
						consume();
						if(t.kind() == RSQUARE)
						{
							consume();
						}
						else
							throw new SyntaxException(t , "Syntax error");
					}
					else
						throw new SyntaxException(t , "Syntax error");
				}
				else
					throw new SyntaxException(t , "Syntax error");
			}
			else
				throw new SyntaxException(t , "Syntax error");
		}
		else
			throw new SyntaxException(t , "Syntax error");
	}

	protected boolean isKind(Kind kind) {
		return t.kind() == kind;
	}

	protected boolean isKind(Kind... kinds) {
		for (Kind k : kinds) {
			if (k == t.kind())
				return true;
		}
		return false;
	}


	/**
	 * Precondition: kind != EOF
	 * 
	 * @param kind
	 * @return
	 * @throws SyntaxException
	 */
	private Token match(Kind kind) throws SyntaxException {
		Token tmp = t;
		if (isKind(kind)) {
			consume();
			return tmp;
		}
		error(t, kind.toString());
		return null; // unreachable
	}

	/**
	 * Precondition: kind != EOF
	 * 
	 * @param kind
	 * @return
	 * @throws SyntaxException
	 */
	private Token match(Kind... kinds) throws SyntaxException {
		Token tmp = t;
		if (isKind(kinds)) {
			consume();
			return tmp;
		}
		error(t, "expected one of " + kinds);
		return null; // unreachable
	}

	private Token consume() throws SyntaxException {
		Token tmp = t;
		if (isKind(EOF)) {
			error(t, "attempting to consume EOF");
		}
		t = scanner.nextToken();
		return tmp;
	}

	private void error(Token t, String m) throws SyntaxException {
		String message = m + " at " + t.line() + ":" + t.posInLine();
		throw new SyntaxException(t, message);
	}
	
	/**
	 * Only for check at end of program. Does not "consume" EOF so there is no
	 * attempt to get the nonexistent next Token.
	 * 
	 * @return
	 * @throws SyntaxException
	 */
	private Token matchEOF() throws SyntaxException {
		if (isKind(EOF)) {
			return t;
		}
		error(t, EOF.toString());
		return null; // unreachable
	}
}
