import java.util.Collections;
import java.util.Optional;
import java.util.ArrayList;
import java.util.List;

public class LexicalAnalyser {

    public static List<Token> analyse(String sourceCode) throws LexicalException {
        
        //ArrayList to hold different tokens that are added!
        List<Token> tokenList = new ArrayList<Token>();

        //Spliting string into different array indexes to seperate different possible tokens
        String[] splitString = sourceCode.split("((?<=(\\{|\\}|\\|\\||&&|<|>|!|=|\\+|\\*|-|%|/|\\)|\\(|;|\\s|\"|'))|(?=(\\{|\\}|\\|\\||&&|<|>|=|!|\\+|\\*|-|%|/|\\)|\\(|;|\\s|\"|')))");

        int i = 0;
        
        if(sourceCode.length() > 0) {
            
        while(i < splitString.length){
            if(splitString[i].equals(" ")) {
                // Dont do anything if there is a whitespace
                i += 1;
            }
            else if(splitString[i].equals("=") && splitString[i+1].equals("=") && i != splitString.length - 1){
                //splitString[i+1] checks if there is another = after i
                //check for TWO 'EQUAL' tokens and not be mistaken for '='
                i += 1; 
                tokenList.add(tokenFromString("==").get());
                i +=1;
            }   
            else if(i != 0 && i != splitString.length - 1 && splitString[i+1].equals("\"") && splitString[i-1].equals("\""))
            {  
                //Check for STRING (STRINGLIT) tokens
                tokenList.add(new Token(Token.TokenType.STRINGLIT, splitString[i]));
                i+=1;
            }
            else if(i != 0 && i != splitString.length - 1 && splitString[i+1].equals("'") && splitString[i-1].equals("'"))
            {
                //Check for character tokens
                tokenList.add(new Token(Token.TokenType.CHARLIT, splitString[i]));
                i += 1;
            }
            
            else
            {
                //Different input which doesn't need checking after index i
                Optional<Token> type = tokenFromString(splitString[i]);
                if(type.isPresent())
                {
                    tokenList.add(type.get());
                    i+=1;
                }
                else
                {
                    throw new LexicalException("Invalid input: " + splitString[i]);
                }
            }
        }
        return tokenList;
    }
    else return null;
    }


    private static Optional<Token> tokenFromString(String t) {
        Optional<Token.TokenType> type = tokenTypeOf(t);
        if (type.isPresent())
            return Optional.of(new Token(type.get(), t));
        return Optional.empty();
    }

    private static Optional<Token.TokenType> tokenTypeOf(String t) {
        switch (t) {
            case "public":
                return Optional.of(Token.TokenType.PUBLIC);
            case "class":
                return Optional.of(Token.TokenType.CLASS);
            case "static":
                return Optional.of(Token.TokenType.STATIC);
            case "main":
                return Optional.of(Token.TokenType.MAIN);
            case "{":
                return Optional.of(Token.TokenType.LBRACE);
            case "void":
                return Optional.of(Token.TokenType.VOID);
            case "(":
                return Optional.of(Token.TokenType.LPAREN);
            case "String[]":
                return Optional.of(Token.TokenType.STRINGARR);
            case "args":
                return Optional.of(Token.TokenType.ARGS);
            case ")":
                return Optional.of(Token.TokenType.RPAREN);
            case "int":
            case "char":
            case "boolean":
                return Optional.of(Token.TokenType.TYPE);
            case "=":
                return Optional.of(Token.TokenType.ASSIGN);
            case ";":
                return Optional.of(Token.TokenType.SEMICOLON);
            case "if":
                return Optional.of(Token.TokenType.IF);
            case "for":
                return Optional.of(Token.TokenType.FOR);
            case "while":
                return Optional.of(Token.TokenType.WHILE);
            case "==":
                return Optional.of(Token.TokenType.EQUAL);
            case "+":
                return Optional.of(Token.TokenType.PLUS);
            case "-":
                return Optional.of(Token.TokenType.MINUS);
            case "*":
                return Optional.of(Token.TokenType.TIMES);
            case "/":
                return Optional.of(Token.TokenType.DIVIDE);
            case "%":
                return Optional.of(Token.TokenType.MOD);
            case "}":
                return Optional.of(Token.TokenType.RBRACE);
            case "else":
                return Optional.of(Token.TokenType.ELSE);
            case "System.out.println":
                return Optional.of(Token.TokenType.PRINT);
            case "||":
                return Optional.of(Token.TokenType.OR);
            case "&&":
                return Optional.of(Token.TokenType.AND);
            case "true":
                return Optional.of(Token.TokenType.TRUE);
            case "false":
                return Optional.of(Token.TokenType.FALSE);
            case ">":
                return Optional.of(Token.TokenType.GT);
            case "<":
                return Optional.of(Token.TokenType.LT);
            case ">=":
                return Optional.of(Token.TokenType.GE);
            case "<=":
                return Optional.of(Token.TokenType.LE);
            case "!=":
                return Optional.of(Token.TokenType.NEQUAL);
            case "'":
                return Optional.of(Token.TokenType.SQUOTE);
            case "\"":
                return Optional.of(Token.TokenType.DQUOTE);
        }

        if (t.matches("\\d+"))
            return Optional.of(Token.TokenType.NUM);
        if (Character.isAlphabetic(t.charAt(0)) && t.matches("[\\d|\\w]+")) {
            return Optional.of(Token.TokenType.ID);
        }
        return Optional.empty();
    }

}
