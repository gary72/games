/*
 * File:	Roman.java
 *
 * Package:	none
 *
 * Class:	Roman
 *
 * Author:	Gary Sockut
 *
 * Created on March 23, 2017
 *
 * Notes:	See JavaDoc comment below.
 */

// imports:
import java.util.Scanner;
import java.util.LinkedList;

/** Represents a Roman numeral desk calculator.
 * @author Gary Sockut
 * @version $id. Mar 23, 2017 7:00:00 PM $
 *
 * Class:	Roman
 * 
 * Here are the phases of the main method:
 * - Display the introductory text.
 * - Perform these steps for each line that the user enters:
 *   1) Get the line and check for empty.
 *   2) Check for QUIT or EXIT.
 *   3) Tokenize the line, including converting Romans to integers.
 *   4) Parse and evaluate the tokenized line to calculate result as an integer.
 *      There is no need for a parse tree, since we evaluate while parsing.
 *   5) Display the result after converting the integer to Romans.
 */
class Roman {
    static Scanner sc = new Scanner(System.in);	// to read typed lines
    static String ROMAN_LETTERS = "MDCLXVIO";
    	// valid letters in a Roman numeral; stand-alone letter O means zero.

    // valid range of Roman numerals:
    static final int MININT = -3999;	// smallest Roman numeral (-MMMCMXCIX)
    static final int MAXINT = 3999;	// largest Roman numeral (MMMCMXCIX)
    	
    // Most tokens represent text that the user enters within a line.
    // One exceptional token represents the end of a line.
    // These are the codes for token types that can end an expression:
    static final int END = 0;	// token type for end of line
    static final int R_P = 1;	// token type for right parenthesis )
    static final int ENDER_BOUND = 2;	// This isn't actually a token type;
   		// it is the boundary of token types that can end an expression.
   	// These are the codes for token types that can validly begin an operand:
   	static final int L_P = 3;	// token type for left parenthesis (
  	static final int INT = 4;	// token type for Roman integer
  	static final int OPERATOR_BOUND = 5;
		// This isn't actually a token type; it is the boundary of operators.
   	// These are the codes for token types that represent operators:
  	static final int ADD = 6;	// token type for +
   	static final int SUB = 7;	// token type for -
   	static final int MUL = 8;	// token type for *
   	static final int DIV = 9;	// token type for /
   	static final int EXP = 10;	// token type for ** (exponentiate)
   	
    // These arrays of constants are indexed by a digit number (ones, tens,
    // 100s, 1000s); they are used for analyzing Roman letters for that digit:
    static final int[] VALUE_OF_DIGIT = {1, 10, 100, 1000};
    	// the value of one of a digit (Roman I, X, C, M)
    static final char[] ONE_CHAR_FOR_DIGIT = {'I', 'X', 'C', 'M'};
    	// the Roman letter for one of that digit
    static final char[] FIVE_CHAR_FOR_DIGIT = {'V', 'L', 'D', ' '};
    	// the Roman letter for five of that digit; not applicable to 1000s
    static final String[] FOLLOWERS = {"", "VI", "LXVI", "DCLXVI"};
    	// the Roman letters that can begin a digit after the current digit
   	
    /** Main method for Roman numeral desk calculator.
     * @param args arguments (optional; ignored)
     */    
    public static void main(String[] args) {	// any args are ignored
    	InfoForLine lineInfo = new InfoForLine();
    		// info for a line that the user enters
    	int resultOfEvaluation;	// result of evaluation of line
    		
        // Display the introductory text:
    	System.out.println("[] Welcome to the Roman numeral desk calculator!"
    		+ "\n[] Any number of times, you can type an expression and Enter"
            + "\n[] to see the result; type QUIT or EXIT to quit."
            + "\n[] You can end a line with = (but the = has no effect)."
            + "\n[] You can use integers up through MMMCMXCIX (Arabic 3,999)."
            + "\n[] Unlike the original Roman numerals, you can also"
            + "\n[] specify zero, by using the LETTER (NOT DIGIT) O."
            + "\n[] You can also use lower case, parentheses,"
            + "\n[] and these operators: +, -, *, /, and ** (exponent)."
            + "\n[] To achieve the effect of a negative integer,"
            + "\n[] use O, -, and a positive integer; you MUST include the O."
            + "\n[] If you start a line with an operator,"
            + "\n[] the operator's left operand is the previous line's result."
            + "\n[] NO fractions are allowed in expressions or in results.");
        	// Do not support unary minus, to avoid ambiguity between starting
        	// with a negative integer and subtracting from the previous result.

        lineInfo.m_result = 0;
        	// there have not been any lines or results so far
        // Iteration for each line that the user enters:
        while (true) {	// iterate for each line that the user enters
        	lineInfo.m_lineHasError = false;	// no error detected in line yet
        	
        	// 1) Get the line and check for empty:
        	lineInfo.m_line = sc.nextLine().toUpperCase().trim();
        		// the line, in upper case, without leading / trailing blanks
        	if (lineInfo.m_line.equals("=") | lineInfo.m_line.equals(""))
        	{	// only "=" or nothing at all
        		System.out.println(
        			"[] Please type an expression, QUIT, or EXIT.");
        		continue;
        	}	// only "=" or nothing at all
        	if (lineInfo.m_line.indexOf("0") > -1)
        	{	// line contains the DIGIT 0.
        		// Handle this particular invalid character specially,
        		// because it could be hard for a user to find the problem
        		// if we issue a generic "invalid character" message.
        		System.out.println(
        			"[] For zero, specify the LETTER (NOT DIGIT) O.");
        		continue;
        	}	// line contains the DIGIT 0.
        	if (lineInfo.m_line.endsWith("="))	// non-blanks and then "="
        		lineInfo.m_line =
        			lineInfo.m_line.substring(0, lineInfo.m_line.length()-1);
        			// strip off (ignore) the ending "="
        	lineInfo.m_line += " ";	// append blank to ease tokenization

        	// 2) Check for QUIT or EXIT:
        	if (lineInfo.m_line.indexOf("QUIT") +
        		lineInfo.m_line.indexOf("EXIT") > -2)
        	{	// contains QUIT or EXIT
        		System.out.println("[] Bye!  Visit again!");	// say goodbye
        		break;	// quit
        	}	// contains QUIT or EXIT
        	
        	// 3) Tokenize the line, including converting Romans to integers:
        	lineInfo.tokenize();	// tokenize
        	if (lineInfo.m_lineHasError)	// if line has error
        		continue;	// skip parse and evaluate; iterate to next line  		
        		
        	// 4) Parse and evaluate the tokenized line:
        	resultOfEvaluation = lineInfo.parseAndEvaluate(0)[0];
        		// parse and evaluate the line, including error message if error
        	if (lineInfo.m_lineHasError)	// if line has error
        		continue;	// skip display result; iterate to next line  	
        	
        	// 5) Display the result after converting the integer to Roman:
        	lineInfo.m_result = resultOfEvaluation;
        		// update result, now that we know it's error-frree
			// In the next statement, the conditional part tests whether the
			// first token is an operator (whose left operand is the previous
			// line's result).  If true, the displayed result mentions that the
			// result uses the previous line's result.  The purpose is to avoid
			// surprising a user who begins the current line with a minus sign.
			System.out.println("[] Result" +
				((lineInfo.m_tokenizedLine.get(0).m_tokenType > OPERATOR_BOUND)
					? " (which uses previous line's result)" : "") +
				": Roman " + toRoman(lineInfo.m_result) + " (Arabic " +
        		Integer.toString(lineInfo.m_result) + ").");
        		// display result in Roman and Arabic
        	
        };	// iterate for each line that the user enters
    }	// main method
    
    /** Process an error by setting lineHasError & displaying an error message.
     * @param p_theInfoForLine info for the line that contains the error
     * @param p_thePosition the position of the character where error was found
     * @param p_descriptionOfError description of the error 
     */    
    public static void processAnError(
    	InfoForLine p_theInfoForLine,
    	int p_thePosition,
    	String p_descriptionOfError) {
    	p_theInfoForLine.m_lineHasError = true;	// indicate error
    	System.out.println("[] There's " + p_descriptionOfError +
    		" detected at the end of this text:\n[] " +
    		p_theInfoForLine.m_line.substring(0, p_thePosition + 1));
    		// describe the error and its context
    }	// processAnError method

    /** Produce a String that is the Roman numeral representation of an integer,
     * which is assumed to be in the valid range (-3,999 to +3,999).
     * @param p_theInteger the integer
     * @return String the Roman numeral representation
     */    
    public static String toRoman(int p_theInteger) {
    	String theResult = "";	// accumulates the result if it's not zero
    	String arabic = Integer.toString(Math.abs(p_theInteger));
    		// the nonnegative version of the integer, as an Arabic number
    	int d;	// iteration for each possible Arabic digit (ending with ones).
    		// it indexes ONE_CHAR_FOR_DIGIT and FIVE_CHAR_FOR_DIGIT.
    	int aDigitAsInt;	// a digit, as an integer
    	int j;	// iteration for appending a unit char for 1, 2, 3, 6, 7, or 8
    	
    	if (p_theInteger == 0)	// zero
    		return "O";	// return Roman zero (O) immediately
    	if (p_theInteger < 0)	// negative
    		theResult = "-";	// start with minus
    	// The following loop examines each digit and (if the digit is not 0)
    	// appends the Roman letters for that digit:
    	for (d = arabic.length() - 1; d >= 0; d--)
    	{	// for each possible Arabic digit (ending with ones).
    		aDigitAsInt = (arabic.charAt(arabic.length() - 1 - d)) - '0';
    			// capture int for digit.  note that although d decreases in
    			// iteration, index into arabic increases.
    		switch(aDigitAsInt)	// what's the digit's value?
    		{	// handle the digit's value
    		case 9:
    			theResult += ONE_CHAR_FOR_DIGIT[d];
    			theResult += ONE_CHAR_FOR_DIGIT[d + 1];
    				// append CM, XC, or IX
    			break;	// finished with this digit
    		case 4:
    			theResult += ONE_CHAR_FOR_DIGIT[d];
    			theResult += FIVE_CHAR_FOR_DIGIT[d];
    				// append CD, XL, or IV
    			break;	// finished with this digit
    		default:	// not 9 or 4
    			if (aDigitAsInt >= 5)	// at least 5
    			{	// at least 5
    				theResult += FIVE_CHAR_FOR_DIGIT[d];	// append D, L, or V
    				aDigitAsInt -= 5;
    					// subtract the 5; then append remaining letters, if any
    			}	// at least 5
    			for (j = aDigitAsInt; j > 0; j--)
    				// for 1, 2, or 3, or (we have subtracted 5) 6, 7, or 8
    				theResult += ONE_CHAR_FOR_DIGIT[d];	// append C, X, or I
    		}	// handle the digit's value
    	}	// for each possible Arabic digit (ending with ones).
    	return theResult;	// all done.  return the result.
    }	// toRoman method
   
    /** Information for a line that the user enters.
     */
    static class InfoForLine {
    	// members that apply to the entire line:
    	public String m_line;	// the line, which will be converted
     		// to upper case and will have leading and trailing blanks removed.
    		// it includes any intermediate blanks but not a carriage return.
    	public boolean m_lineHasError;	// indicates whether line has error
    	public LinkedList<Token> m_tokenizedLine;	// tokenized version of line
    	public int m_result;	// final result of a line's expression
		// members that apply to an individual character or integer in the line:
		public char m_c1;	// a char from the line
    	public int m_tokenIntValue;	// value of a tokenized integer
    	
    	/** Tokenize a line, including appending a token for end of line.
         */    
    	public void tokenize() {
    		m_tokenizedLine = new LinkedList<Token>();	// empty token list
    		// iteration counters, all listed here to avoid confusion:
    		int i;	// for each character in line
    		int d;	// for each possible digit (thousands through ones)
    		int j;	// for each instance of a repeatable Roman letter, e.g., III
    		
        	for (i = 0; i < m_line.length(); i++)
        	{	// for each char in line; we know that last is blank
        		m_c1 = m_line.charAt(i);	// ith character
        		if (ROMAN_LETTERS.indexOf(m_c1) > -1)	// char is Roman letter
        		{	// try to tokenize as an integer
        			m_tokenIntValue = 0;	// start with 0; add to it later
        			if (m_c1 != 'O')
        				// if int is 'O' (zero), we're done evaluating int.
        				// if int is not 'O', we must evaluate the int:
        				for (d = 3; d >= 0; d--)
        				{	// for each possible digit (thousands through ones).
        					// Each digit other than thousands might have 9
        					// times that digit (e.g., IX), 4 times that digit
        					// (e.g., IV), or 5 times that digit (e.g., V).
        					// Each digit including thousands might have a
        					// series of 1, 2, or 3 of that digit (e.g., III).
        					// Each digit other than thousands might have the
        					// series alone or after the 5 times (e.g., VIII).
        					// This uses some static final arrays that were
        					// declared at the beginning of the Roman class.
        					// When finished tokenizing int,
        					// i should point to the last char of the int.
        					m_c1 = m_line.charAt(i);
        						// reset in case this is not first iteration
        					if (d < 3 && m_c1 == ONE_CHAR_FOR_DIGIT[d] &&
        						m_line.charAt(i+1) == ONE_CHAR_FOR_DIGIT[d + 1])
        					{	// 9 times the digit, e.g., IX
        						m_tokenIntValue += 9 * VALUE_OF_DIGIT[d];
        							// add 9 times the digit
        						i++;	// point to the digit's second char
        						if (d > 0 &&
        							FOLLOWERS[d].indexOf(m_line.charAt(i + 1))
        							> -1)
        							// if there are more chars in this int
        							i++;	// skip to next char
        						continue;	// iterate; we've found entire digit
        					}	// 9 times the digit, e.g., IX
        					if (d < 3 && m_c1 == ONE_CHAR_FOR_DIGIT[d] &&
        						m_line.charAt(i + 1) == FIVE_CHAR_FOR_DIGIT[d])
        					{	// 4 times the digit, e.g., IV
        						m_tokenIntValue += 4 * VALUE_OF_DIGIT[d];
        							// add 4 times the digit
        						i++;	// point to the digit's second char
        						if (d > 0 &&
        							FOLLOWERS[d].indexOf(m_line.charAt(i + 1))
        							> -1)
        							// if there are more chars in this int
        							i++;	// skip to next char
        						continue;	// iterate; we've found entire digit
        					}	// 4 times the digit, e.g., IV
        					if (m_c1 == FIVE_CHAR_FOR_DIGIT[d])
        					{	// 5 char
        						m_tokenIntValue += 5 * VALUE_OF_DIGIT[d];
        							// add 5 times the digit, e.g., V.
        						if ((m_line.charAt(i + 1) ==
        							ONE_CHAR_FOR_DIGIT[d]) ||
        							(d > 0 &&
        							FOLLOWERS[d].indexOf(m_line.charAt(i + 1))
        							> -1))
        								// if there are more chars in this int
        							i++;	// skip to next letter
        						// if we found another char in this int,
        						// we're pointing to it.  if we didn't find it,
        						// we're still pointing to this digit's 5 char.
        						if (m_line.charAt(i) != ONE_CHAR_FOR_DIGIT[d])
        							// we found either a lower digit or a
        							// non-digit, so we're done with this digit
        							continue;	// iterate.
        						// if we found a 1 digit, fall through
        					}	// 5 char
        					if (m_line.charAt(i) == ONE_CHAR_FOR_DIGIT[d])
        					{	// i points to the one char for this digit        						
        						m_tokenIntValue += VALUE_OF_DIGIT[d];
        							// add digit
        						for (j = 1; j <= 2; j++)
        						{	// for each in a series of the one char
        							if (m_line.charAt(i + 1) ==
        								ONE_CHAR_FOR_DIGIT[d])
        							{	// the digit, e.g., I
        								m_tokenIntValue += VALUE_OF_DIGIT[d];
        									// add the digit
        								i++;	// skip to next letter
        							}	// the digit, e.g., I
        						}	// for each in a series of the one char
        						if (d > 0 &&
        							FOLLOWERS[d].indexOf(m_line.charAt(i + 1))
        							> -1)
        							// if there are more chars in this int
        							i++;	// skip to next char
        					}	// i points to the one char for this digit
        					
        				}	// for each possible digit (thousands through ones).
        			if (ROMAN_LETTERS.indexOf(m_line.charAt(i + 1)) > -1)
						processAnError (this, i + 1, "an invalid character");
        					// int followed by another Roman letter is not valid
        			if (! m_lineHasError)
        				m_tokenizedLine.add(new Token(INT, i, m_tokenIntValue));
        					// append a token for integer
        		}	// try to tokenize as an integer
        		else switch (m_c1)
        		{	// try to tokenize as a non-integer; what is the char?
        		case '+':
        			m_tokenizedLine.add(new Token(ADD, i));
        				// append a token for +
        			break;
        		case '-':
        			m_tokenizedLine.add(new Token(SUB, i));
        				// append a token for -
        			break;
        		case '*':
        			if (m_line.charAt(i+1) == '*')
        			{	// ** (exponentiate)
        				i++;	// advance to second asterisk
        				m_tokenizedLine.add(new Token(EXP, i));
        					// append a token for **
        			}	// ** (exponentiate)
        			else m_tokenizedLine.add(new Token(MUL, i));
        				// append a token for *
        			break;
        		case '/':
        			m_tokenizedLine.add(new Token(DIV, i));
        				// append a token for /
        			break;
        		case '(':
        			m_tokenizedLine.add(new Token(L_P, i));
        				// append a token for (
        			break;
        		case ')':
        			m_tokenizedLine.add(new Token(R_P, i));
        				// append a token for )
        			break;
        		case ' ':
        			// do nothing for white space
        			break;
        		default:
					processAnError (this, i, "an invalid character");
        				// any other char is error
        		}	// try to tokenize as a non-integer; what is the char?
        		if (m_lineHasError)	// if line has error
        			i = m_line.length() - 1;
        				// to end the iteration for each CHAR in line
        	}   // for each char in line; we know that last is blank
        	m_tokenizedLine.add(new Token(END, m_line.length() - 1));
        		// append an artificial token for end of line
    	}	// tokenize method

    /** Parse and evaluate the expression
     * in a tokenized line or a parenthesized subset of it.
     * This method might set m_lineHasError by calling processAnError;
     * therefore, caller should test m_lineHasError.
     * @param p_startingTokenNumber - starting point within token list.
     * @return int array containing (0) result of evaluating an expression and
     *	(1) token number of expression's last token (end of line or right paren)
     */    
    public int[] parseAndEvaluate(
    	int p_startingTokenNumber	// starting point within token list.
    	// If starting point is 0, it's first token, we're parsing the outermost
    	// expression, which may begin with either an operand or an operator,
    	// and the expression must end with end of line, not ')'.
    	// If starting point is > 0, it's the token after a '(', we're parsing a
    	// nested (parenthesized) expression, which must begin with an operand,
    	// and the expression must end with ')', not end of line.
    	) {
    		int currentTokenNumber = p_startingTokenNumber;
    			// the current token number, which starts at the starting point
    		int currentTokenType;	// current token's type
    		int expressionEnder = (p_startingTokenNumber == 0) ? END : R_P;
    			// the desired token code (end of line or ')') to end expression
    		int currentOperator;	// token code for current operator
    		int secondOperandValue;	// value of second operand of an operator
    		int currentResult;	// current result of evaluation
    		int[] valuesToReturn = new int[2];
    			// array of ints to return (result and token number)
    		int[] valuesReturned = new int[2];
    			// array returned from a recursive call to parseAndEvaluate
    			
    		// Here are the stages:
    		// 1) Do initial processing, which depends on first token's type.
    		// 2) Iterate on possible pairs of operator token and operand token,
    		//    evaluating until we have found the entire expression.
    		// 3) Return the result.
    		
    		// 1) Do initial processing, which depends on first token's type:
			currentTokenType =
				m_tokenizedLine.get(currentTokenNumber).m_tokenType;
				// get first token's type, which determines the processing
			if (currentTokenType < ENDER_BOUND)
			{	// first token is end of line or right parenthesis
				processAnError(this,
					m_tokenizedLine.get(currentTokenNumber).m_tokenEndIndex,
					"a missing expression");	// error
				return valuesToReturn;	// so return immediately
			}	// first token is end of line or right parenthesis
			switch (currentTokenType)	// if not end of line or right paren
    		{	// not end of line or right paren
        		case INT:	// integer
        			currentResult =
    					m_tokenizedLine.get(currentTokenNumber).m_integerValue;
    					// get int's value
        			break;
        		case L_P:	// first operand is parenthesized expression
    				valuesReturned = parseAndEvaluate(currentTokenNumber + 1);
    					// recursively call to evaluate parenthesized expression
    				if (m_lineHasError)	// there was an error
    					return valuesToReturn;	// so return immediately
    				currentResult = valuesReturned[0];	// value
    				currentTokenNumber = valuesReturned[1];	// token number
        			break;
        		default:	// an operator
        			if (currentTokenNumber == 0)
        			{	// line's first token is an operator 
        				currentResult = m_result;
    						// first operand is previous line's result
    					currentTokenNumber--;	// Do a brief backup.  The
    						// forthcoming iteration on pairs of operator token
    						// and operand token will ++ to initial operator.
        			}	// line's first token is an operator
        			else
        			{	// parenthesized expression's first token is an operator
        				processAnError(this, m_tokenizedLine.
        					get(currentTokenNumber).m_tokenEndIndex,
        					"an invalid expression");	// error
        				return valuesToReturn;	// so return immediately
        			}	// parenthesized expression's first token is an operator
        	}	// not end of line or right paren	
   		
    		// 2) Iterate on possible pairs of operator token and operand token:
    		while(true)
    		{	// iterate on possible pairs of operator token and operand token
    			// 2A) try to find an operator token:
    			currentTokenNumber++;	// advance to next token
    			currentOperator =
    				m_tokenizedLine.get(currentTokenNumber).m_tokenType;
    				// type of token (we want an operator or expression ender)
    			if (currentOperator == expressionEnder)
    				// we found end of expression, instead of operator
    				break;	// we're done with expression, so break out of while
    			if (currentOperator < OPERATOR_BOUND)
    				// if an operator, we have captured it in currentOperator,
    				// so we have finished the finding of an operator token;
    				// do nothing else here.
    				// if not an expression ender or an operator,
    				// error (invalid ender, integer or left parenthesis).
    			{	// it's an error
    				processAnError(this, m_tokenizedLine.
        				get(currentTokenNumber).m_tokenEndIndex,
        				((currentOperator < ENDER_BOUND) ?
        					"an incomplete expression" : "a missing operator"));
        				// error.  invalid ender means incomoplete expression;
        				// integer or left parenthesis means missing operator.
    				return valuesToReturn;	// so return immediately
    			}	// it's an error				
    				
    			// 2B) try to find an operand token:
    			currentTokenNumber++;	// advance to next token
    			currentTokenType =
    				m_tokenizedLine.get(currentTokenNumber).m_tokenType;
    				// token type
    			if (currentTokenType < ENDER_BOUND ||
    				currentTokenType > OPERATOR_BOUND)
    				// if expression ender or operator, error (missing operand).
    			{	// it's an error
    				processAnError(this, m_tokenizedLine.
        				get(currentTokenNumber).m_tokenEndIndex,
        				"a missing operand");	// error
    				return valuesToReturn;	// so return immediately
    			}	// it's an error
				// fall through if not an error:
    			if (currentTokenType == INT)
    				secondOperandValue =
    					m_tokenizedLine.get(currentTokenNumber).m_integerValue;
    					// if operand is int, pick up its int value
    			else
    			{	// operand is parenthesized expression
    				valuesReturned = parseAndEvaluate(currentTokenNumber + 1);
    					// recursively call to evaluate parenthesized expression
    				if (m_lineHasError)	// there was an error
    					return valuesToReturn;	// so return immediately
    				secondOperandValue = valuesReturned[0];	// value
    				currentTokenNumber = valuesReturned[1];	// token number
    			}	// operand is parenthesized expression
    			switch (currentOperator)
    			{	// evaluation depends on operator
 	       			case ADD:
 	       				currentResult += secondOperandValue;	// add
 	       				break;
 	       			case SUB:
 	       				currentResult -= secondOperandValue;	// subtract
 	       				break;
 	       			case MUL:
 	       				currentResult *= secondOperandValue;	// multiply
 	       				break;
 	       			case DIV:
 	       				if (secondOperandValue == 0)	// divide by zero
 	       				{	// error (divide by zero)
 	       					processAnError(this, m_tokenizedLine.
 	       						get(currentTokenNumber).m_tokenEndIndex,
 	       						"a division by zero");	// error
 	       					return valuesToReturn;	// so return immediately
    					}	// error (divide by zero)
 	       				// if not zero, fall through:
 	       				currentResult /= secondOperandValue;	// divide
 	       				break;
 	       			default:	// we know that it's EXP (exponentiation).
 	       				// note that the casting to int in the following
 	       				// statement prevents a result larger than an int.
 	       				currentResult =
        					(int)Math.pow(currentResult, secondOperandValue);
        					// exponentiate
        				break;
        		}	// evaluation depends on operator
        		if (currentResult < MININT || currentResult > MAXINT)
        			{	// error (out of range)
 	       				processAnError(this, m_tokenizedLine.
 	       					get(currentTokenNumber).m_tokenEndIndex,
 	       					"a result out of range");	// error
 	       				return valuesToReturn;	// so return immediately
    				}	// error (out of range)
    		}	// iterate on possible pairs of operator token and operand token

			// 3) Return the result:
			valuesToReturn[0] = currentResult;
			valuesToReturn[1] = currentTokenNumber;
    		return valuesToReturn;	// return the result
    	}	// parseAndEvaluate method
    	
     }	// InfoForLine class
    
    /** A token within the line that the user entered.
     * A LinkedList of tokens is output of tokenization and input to parsing.
     * A token can be a Roman integer, +, -, *, /, **, (, or ).
     */    
    static class Token {
    	int m_tokenType;	// type of token
    	int m_tokenEndIndex;
    		// index (in line) of token's last char, for possible error message
    	int m_integerValue;	// value of token if an integer; else unused

    	// Constructors:
    	
    	/** Create a token that includes integer value.
    	 * @param tokenType - token type.
    	 * @param tokenEndIndex - index of end of token.
    	 * @param integerValue - integer value of token.
    	 */
    	public Token(int tokenType, int tokenEndIndex, int integerValue)
    	{	// constructor that includes integer value
    		m_tokenType = tokenType;
    		m_tokenEndIndex = tokenEndIndex;
    		m_integerValue = integerValue;
    	}	// constructor that includes integer value
    	
    	/** Create a token that omits integer value.
    	 * @param tokenType - token type.
    	 * @param tokenEndIndex - index of end of token.
    	 */
    	public Token(int tokenType, int tokenEndIndex)
    	{	// constructor that omits integer value
    		m_tokenType = tokenType;
    		m_tokenEndIndex = tokenEndIndex;
    	}	// constructor that omits integer value
    }	// Token class
    
}	// Roman class