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
				
			}		
			else if(t.kind() == IDENT)
			{
				statement();
			}
			
			if(t.kind() == SEMI)
			{
				consume();
			}else
			{
				throw new SyntaxException(t , "Syntax error");
			}
		}
		
	}


	//make this public for convenience testing
	public void expression() throws SyntaxException, LexicalException {
		
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
				throw new SyntaxException(t , "Syntax error");
			
		}
		else
			throw new SyntaxException(t , "Syntax error");
	}
	
	public void statement() throws SyntaxException, LexicalException {
		//TODO
	}
	
	public void imageOutStatement() throws SyntaxException, LexicalException {
		//TODO
	}
	
	public void imageInStatement() throws SyntaxException, LexicalException {
		//TODO
	}
	
	public void assingmentStatement() throws SyntaxException, LexicalException {
		//TODO
	}
	
	public void loopStatement() throws SyntaxException, LexicalException {
		//TODO
	}
	
	public void andExpression() throws SyntaxException, LexicalException {
		//TODO
	}
	
	public void orExpression() throws SyntaxException, LexicalException {
		//TODO
	}
	
	public void eqExpression() throws SyntaxException, LexicalException {
		//TODO
	}
	
	public void relExpression() throws SyntaxException, LexicalException {
		//TODO
	}
	
	public void addExpression() throws SyntaxException, LexicalException {
		//TODO
	}
	
	public void multExpression() throws SyntaxException, LexicalException {
		//TODO
	}
	
	public void unaryExpression() throws SyntaxException, LexicalException {
		//TODO
	}
	
	public void unaryExpressionNotPlusMinus() throws SyntaxException, LexicalException {
		//TODO
	}
	
	public void hashExpression() throws SyntaxException, LexicalException {
		//TODO
	}
	
	public void primary() throws SyntaxException, LexicalException {
		//TODO
	}
	
	public void pixelConstructor() throws SyntaxException, LexicalException {
		//TODO
	}
	
	public void pixelSelector() throws SyntaxException, LexicalException {
		//TODO
	}
	
	public void attribute() throws SyntaxException, LexicalException {
		//TODO
	}
	
	public void argExpression() throws SyntaxException, LexicalException {
		//TODO
	}
	
	public void constXYSelector() throws SyntaxException, LexicalException {
		//TODO
	}
	
   //TODO--everything else.  Have fun!!
}
