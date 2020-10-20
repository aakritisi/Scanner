/**
 * Class for  for the class project in COP5556 Programming Language Principles 
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

import cop5556fa20.Scanner.LexicalException;
import cop5556fa20.Scanner.Token;
import static cop5556fa20.Scanner.Kind.*;

public class SimpleParser {

	@SuppressWarnings("serial")
	public static class SyntaxException extends Exception {
		final Token token;

		public SyntaxException(Token token, String message) {
			super(message);
			this.token = token;
		}

		public Token token() {
			return token;
		}

	}


	final Scanner scanner;
	Token t;

	SimpleParser(Scanner scanner) {
		this.scanner = scanner;
		t = scanner.nextToken();
		//TODO ??
	}

	public void parse() throws SyntaxException, LexicalException {
		program();
		if (!consumedAll()) throw new SyntaxException(scanner.nextToken(), "tokens remain after parsing");
			//If consumedAll returns false, then there is at least one
		    //token left (the EOF token) so the call to nextToken is safe. 
	}
	
	public void consume() {
		t = scanner.nextToken();
	}

	public boolean consumedAll() {
		if (scanner.hasTokens()) { 
			Token t = scanner.nextToken();
			if (t.kind() != Scanner.Kind.EOF) return false;
		}
		return true;
	}


	private void program() throws SyntaxException, LexicalException {
		
		while(scanner.hasTokens())
		{
			if(t.kind() == KW_image || t.kind() == KW_int || t.kind() == KW_string)
			{
				declaration();
				if(t.kind() == SEMI)
				{
					consume();
				}
				else
				{
					System.out.println(t);
					throw new SyntaxException(t , "Syntax error");
				}
				
			}		
			else if(t.kind() == IDENT)
			{
				statement();
				if(t.kind() == SEMI)
				{
					consume();
				}
				else
					throw new SyntaxException(t , "Syntax error");
			}
			else
			{
				
				throw new SyntaxException(t , "Syntax error");
			}
			
		}
		
	}


	//make this public for convenience testing
	public void expression() throws SyntaxException, LexicalException {
		orExpression();
		if(t.kind() == Q)
		{
			consume();
			expression();
			if(t.kind() == COLON)
			{
				consume();
				expression();
			}
			else
				throw new SyntaxException(t , "Syntax error");
		}
	}
	
	public void declaration() throws SyntaxException, LexicalException {
		if(t.kind() == KW_int ||t.kind() == KW_string)
			variableDeclaration();
		else if(t.kind() == KW_image)
			imageDeclaration();
		else
			throw new SyntaxException(t , "Syntax error");
	}
	
	public void variableDeclaration() throws SyntaxException, LexicalException {
		varType();
		if(t.kind() == IDENT)
		{
			consume();
		}
		else
			throw new SyntaxException(t , "Syntax error");
		if(t.kind() == ASSIGN)
		{
			consume();
			expression();
		}
		
		
	}
	
	public void varType() throws SyntaxException, LexicalException {
		if(t.kind() == KW_int)
			consume();
		else if(t.kind() == KW_string)
			consume();
		else
			throw new SyntaxException(t , "Syntax error");
	}
	
	public void imageDeclaration() throws SyntaxException, LexicalException {
		if(t.kind() == KW_image)
		{
			consume();
			if(t.kind() == LSQUARE)
			{
				
				consume();
				expression();
				if(t.kind() == COMMA)
				{
					consume();
					expression();
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
			if(t.kind() == IDENT)
			{
				
				consume();
				if(t.kind() == LARROW || t.kind() == ASSIGN)
				{
					consume();
					expression();
				}
			}
			else
			{
				
				throw new SyntaxException(t , "Syntax error");
			}
			
		}
		else
			throw new SyntaxException(t , "Syntax error");
	}
	
	public void statement() throws SyntaxException, LexicalException {
	
		if(t.kind() == IDENT)
		{
			consume();
			if(t.kind() == RARROW )
				imageOutStatement();
			else if(t.kind() == LARROW)
				imageInStatement();
			else if(t.kind() == ASSIGN)
				assingmentStatement();
			else
				throw new SyntaxException(t , "Syntax error");
		}
		else
			throw new SyntaxException(t , "Syntax error");
	}
	
	public void imageOutStatement() throws SyntaxException, LexicalException {
		if(t.kind() == RARROW)
		{
			consume();
			if(t.kind() == KW_SCREEN)
			{
				consume();
				if(t.kind() == LSQUARE)
				{
					consume();
					expression();
					if(t.kind() == COMMA)
					{
						consume();
						expression();
						if(t.kind() == RSQUARE)
							consume();
						else
							throw new SyntaxException(t , "Syntax error");
					}
					else
						throw new SyntaxException(t , "Syntax error");
					
				}
			}
			else
				expression();
		}
		else
			throw new SyntaxException(t , "Syntax error");
	}
	
	public void imageInStatement() throws SyntaxException, LexicalException {
		if(t.kind() == LARROW)
		{
			consume();
			expression();
		}
		else
			throw new SyntaxException(t , "Syntax error");
			
	}
	
	public void assingmentStatement() throws SyntaxException, LexicalException {
		if(t.kind() == ASSIGN)
		{
			consume();
			if(t.kind() == STAR)
				loopStatement();
			else 
				expression();
		}
		else
			throw new SyntaxException(t , "Syntax error");
	}
	
	public void loopStatement() throws SyntaxException, LexicalException {
		if(t.kind() == STAR)
		{
			consume();
			constXYSelector();
			if(t.kind() == COLON)
			{
				consume();
				if(t.kind() == PLUS || t.kind() == MINUS || t.kind() == EXCL || t.kind() == INTLIT || t.kind() == IDENT || t.kind() == LPAREN || t.kind() == STRINGLIT || t.kind() == KW_X || t.kind() == KW_Y || t.kind() == CONST || t.kind() == LPIXEL || t.kind() == AT)
				{
					expression();
				}
				if(t.kind() == COLON)
				{
					consume();
					expression();
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
	
	public void andExpression() throws SyntaxException, LexicalException {
		eqExpression();
		while(t.kind() == AND)
		{
			consume();
			eqExpression();
		}
	}
	
	public void orExpression() throws SyntaxException, LexicalException {
		andExpression();
		while(t.kind() == OR)
		{
			consume();
			andExpression();
		}
	}
	
	public void eqExpression() throws SyntaxException, LexicalException {
		relExpression();
		while(t.kind() == EQ || t.kind() == NEQ)
		{
			consume();
			relExpression();
		}
	}
	
	public void relExpression() throws SyntaxException, LexicalException {
		addExpression();
		while(t.kind() == LT || t.kind() == GT || t.kind() == LE || t.kind() == GE)
		{
			consume();
			addExpression();
		}
	}
	
	public void addExpression() throws SyntaxException, LexicalException {
		multExpression();
		while(t.kind() == PLUS || t.kind() == MINUS)
		{
			consume();
			multExpression();
		}
	}
	
	public void multExpression() throws SyntaxException, LexicalException {
		unaryExpression();
		while( t.kind() == STAR || t.kind() == DIV|| t.kind() == MOD)
		{
			consume();
			unaryExpression();
		}
	}
	
	public void unaryExpression() throws SyntaxException, LexicalException {
		if(t.kind() == PLUS || t.kind() == MINUS)
		{
			consume();
			unaryExpression();
		}
		else
			unaryExpressionNotPlusMinus();
	}
	
	public void unaryExpressionNotPlusMinus() throws SyntaxException, LexicalException {
		if(t.kind() == EXCL)
		{
			consume();
			unaryExpression();
		}
		else
			hashExpression();
	}
	
	public void hashExpression() throws SyntaxException, LexicalException {
		primary();
		while(t.kind() == HASH)
		{
			consume();
			attribute();
		}
	}
	
	public void primary() throws SyntaxException, LexicalException {
		if(t.kind() == INTLIT || t.kind() == IDENT || t.kind() == LPAREN || t.kind() == STRINGLIT || t.kind() == KW_X || t.kind() == KW_Y || t.kind() == CONST || t.kind() == LPIXEL || t.kind() == AT)
		{
			if(t.kind() == LPAREN)
			{
				consume();
				expression();
				if(t.kind() == RPAREN)
					consume();
				else
					throw new SyntaxException(t , "Syntax error");
			}
			else if(t.kind() == LPIXEL)
				pixelConstructor();
			else if(t.kind() == AT)
				argExpression();
			else
				consume();
			if(t.kind() == LSQUARE)
				pixelSelector();
				
		}
		else
			throw new SyntaxException(t , "Syntax error");
	}
	
	public void pixelConstructor() throws SyntaxException, LexicalException {
		if(t.kind() == LPIXEL)
		{
			consume();
			expression();
			if(t.kind() == COMMA)
			{
				consume();
				expression();
				if(t.kind() == COMMA)
				{
					consume();
					expression();
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
	}
	
	public void pixelSelector() throws SyntaxException, LexicalException {
		if(t.kind() == LSQUARE)
		{
			consume();
			expression();
			if(t.kind() == COMMA)
			{
				consume();
				expression();
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
		
	}
	
	public void attribute() throws SyntaxException, LexicalException {
		if(t.kind() == KW_WIDTH || t.kind() == KW_HEIGHT|| t.kind() == KW_RED || t.kind() == KW_GREEN || t.kind() == KW_BLUE)
			consume();
		else
			throw new SyntaxException(t , "Syntax error");
	}
	
	public void argExpression() throws SyntaxException, LexicalException {
		if(t.kind() == AT)
		{
			consume();
			primary();
		}
		else
			throw new SyntaxException(t , "Syntax error");
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
	

}
