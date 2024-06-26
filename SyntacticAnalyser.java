import java.util.ArrayDeque;
import java.util.Arrays;
import java.util.Deque;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SyntacticAnalyser {

    public static ParseTree parse(List<Token> tokens) throws SyntaxException {
        //Turn the List of Tokens into a ParseTree.

        if(tokens == null){
            throw new SyntaxException("Token List is Empty");
        }

        ParseTree parseTree = new ParseTree();
        Deque<Pair<Symbol,TreeNode>> stack = new ArrayDeque<>();
        stack.add(new Pair(TreeNode.Label.prog, null));
        
        int i = 0;

        while (i < tokens.size()) {

            var stackY = stack.peek().getY();
            var stackX = stack.peek().getX();
            var tokenType = tokens.get(i).getType();

            if (stackX.isVariable()) {
                if (stackX == TreeNode.Label.prog && tokenType == Token.TokenType.PUBLIC) {
                    //Rule 1 <<prog>> -> public class <<ID>> { public static void main ( String[] args ) { <<los>> } }

                    TreeNode currentNode = new TreeNode(TreeNode.Label.prog, null);
                	parseTree.setRoot(currentNode);

                    stack.pop();

                    stack.add(new Pair(Token.TokenType.PUBLIC, currentNode));
                    stack.add(new Pair(Token.TokenType.CLASS, currentNode));
                    stack.add(new Pair(Token.TokenType.ID, currentNode));
                    stack.add(new Pair(Token.TokenType.LBRACE, currentNode));
                    stack.add(new Pair(Token.TokenType.PUBLIC, currentNode));
                    stack.add(new Pair(Token.TokenType.STATIC, currentNode));
                    stack.add(new Pair(Token.TokenType.VOID, currentNode));
                    stack.add(new Pair(Token.TokenType.MAIN, currentNode));
                    stack.add(new Pair(Token.TokenType.LPAREN, currentNode));
                    stack.add(new Pair(Token.TokenType.STRINGARR, currentNode));
                    stack.add(new Pair(Token.TokenType.ARGS, currentNode));
                    stack.add(new Pair(Token.TokenType.RPAREN, currentNode));
                    stack.add(new Pair(Token.TokenType.LBRACE, currentNode));
                    stack.add(new Pair(TreeNode.Label.los, currentNode));
                    stack.add(new Pair(Token.TokenType.RBRACE, currentNode));
                    stack.add(new Pair(Token.TokenType.RBRACE, currentNode));

                } 
                else if (stackX == TreeNode.Label.los && (tokenType == Token.TokenType.SEMICOLON || tokenType == Token.TokenType.TYPE || tokenType == Token.TokenType.PRINT || tokenType == Token.TokenType.WHILE ||
                tokenType == Token.TokenType.FOR || tokenType == Token.TokenType.IF || tokenType == Token.TokenType.ID)) {
                    // Rule 2 (1) <<los>> -> <<stat>> <<los>>
                	TreeNode currentNode = new TreeNode(TreeNode.Label.los, stackY);
                	stackY.addChild(currentNode);
                    stack.pop();
                    stack.addFirst(new Pair(TreeNode.Label.los, currentNode));
                    stack.addFirst(new Pair(TreeNode.Label.stat, currentNode));
                } 
                else if (stackX == TreeNode.Label.los && tokenType == Token.TokenType.RBRACE) {
                    // Rule 2(2) <<los>> -> e
                	TreeNode currentNode = new TreeNode(TreeNode.Label.los, stackY);
                	stackY.addChild(currentNode);
                    stack.pop();
                    TreeNode epsilon = new TreeNode(TreeNode.Label.epsilon, currentNode);
                    currentNode.addChild(epsilon);
                } 
                else if (stackX == TreeNode.Label.stat && tokenType == Token.TokenType.WHILE) {
                    // Rule 3 (1) <<stat>> => <<while>>
                	TreeNode currentNode = new TreeNode(TreeNode.Label.stat, stackY);
                	stackY.addChild(currentNode);
                    stack.pop();
                    stack.addFirst(new Pair(TreeNode.Label.whilestat, currentNode));
                } 
                else if (stackX == TreeNode.Label.stat && tokenType == Token.TokenType.FOR) {
                    // Rule 3 (2) <<stat>> => <<for>>
                	TreeNode currentNode = new TreeNode(TreeNode.Label.stat, stackY);
                	stackY.addChild(currentNode);
                    stack.pop();
                    stack.addFirst(new Pair(TreeNode.Label.forstat, currentNode));
                } 
                else if (stackX == TreeNode.Label.stat && tokenType == Token.TokenType.IF) {
                    // Rule 3 (3) <<stat>> => <<if>>
                	TreeNode currentNode = new TreeNode(TreeNode.Label.stat, stackY);
                	stackY.addChild(currentNode);
                    stack.pop();
                    stack.addFirst(new Pair(TreeNode.Label.ifstat, currentNode));
                } 
                else if (stackX == TreeNode.Label.stat && tokenType == Token.TokenType.ID) {
                    // Rule 3 (4) <<stat>> => <<assign>> ;
                	TreeNode currentNode = new TreeNode(TreeNode.Label.stat, stackY);
                	stackY.addChild(currentNode);
                    stack.pop();
                    stack.addFirst(new Pair(Token.TokenType.SEMICOLON, currentNode));
                    stack.addFirst(new Pair(TreeNode.Label.assign, currentNode));
                } 
                else if (stackX == TreeNode.Label.stat && tokenType == Token.TokenType.TYPE) {
                    // Rule 3 (5) <<stat>> => <<decl>> ;
                	TreeNode currentNode = new TreeNode(TreeNode.Label.stat, stackY);
                	stackY.addChild(currentNode);
                    stack.pop();
                    stack.addFirst(new Pair(Token.TokenType.SEMICOLON, currentNode));
                    stack.addFirst(new Pair(TreeNode.Label.decl, currentNode));
                } 
                else if (stackX == TreeNode.Label.stat && tokenType == Token.TokenType.PRINT) {
                    // Rule 3 (6) <<stat>> => <<print>> ;
                	TreeNode currentNode = new TreeNode(TreeNode.Label.stat, stackY);
                	stackY.addChild(currentNode);
                    stack.pop();
                    stack.addFirst(new Pair(Token.TokenType.SEMICOLON, currentNode));
                    stack.addFirst(new Pair(TreeNode.Label.print, currentNode));
                } 
                else if (stackX == TreeNode.Label.stat && tokenType == Token.TokenType.SEMICOLON) {
                    // Rule 3 (7) <<stat>> => ;
                	TreeNode currentNode = new TreeNode(TreeNode.Label.stat, stackY);
                	stackY.addChild(currentNode);
                    stack.pop();
                    stack.addFirst(new Pair(Token.TokenType.SEMICOLON, currentNode));
                } 
                else if (stackX == TreeNode.Label.whilestat && tokenType == Token.TokenType.WHILE) {
                    // Rule 4 <<while>> -> while ( <<rel expr>> <<bool expr>> ) { <<los>> }
                	TreeNode currentNode = new TreeNode(TreeNode.Label.whilestat, stackY);
                	stackY.addChild(currentNode);
                    stack.pop();
                    stack.addFirst(new Pair(Token.TokenType.RBRACE, currentNode));
                    stack.addFirst(new Pair(TreeNode.Label.los, currentNode));
                    stack.addFirst(new Pair(Token.TokenType.LBRACE, currentNode));
                    stack.addFirst(new Pair(Token.TokenType.RPAREN, currentNode));
                    stack.addFirst(new Pair(TreeNode.Label.boolexpr, currentNode));
                    stack.addFirst(new Pair(TreeNode.Label.relexpr, currentNode));
                    stack.addFirst(new Pair(Token.TokenType.LPAREN, currentNode));
                    stack.addFirst(new Pair(Token.TokenType.WHILE, currentNode));

                } 
                else if (stackX == TreeNode.Label.forstat && tokenType == Token.TokenType.FOR) {
                    // Rule 5 <<for>> -> for ( <<for start>> ; <<rel expr>> <<bool expr>> ; <<for arith>> ) { <<los>> }
                	TreeNode currentNode = new TreeNode(TreeNode.Label.forstat, stackY);
                	stackY.addChild(currentNode);
                    stack.pop();
                    stack.addFirst(new Pair(Token.TokenType.RBRACE,currentNode));
                    stack.addFirst(new Pair(TreeNode.Label.los, currentNode));
                    stack.addFirst(new Pair(Token.TokenType.LBRACE,currentNode));
                    stack.addFirst(new Pair(Token.TokenType.RPAREN,currentNode));
                    stack.addFirst(new Pair(TreeNode.Label.forarith,currentNode));
                    stack.addFirst(new Pair(Token.TokenType.SEMICOLON, currentNode));
                    stack.addFirst(new Pair(TreeNode.Label.boolexpr, currentNode));
                    stack.addFirst(new Pair(TreeNode.Label.relexpr, currentNode));
                    stack.addFirst(new Pair(Token.TokenType.SEMICOLON, currentNode));
                    stack.addFirst(new Pair(TreeNode.Label.forstart, currentNode));
                    stack.addFirst(new Pair(Token.TokenType.LPAREN, currentNode));
                    stack.addFirst(new Pair(Token.TokenType.FOR, currentNode));

                } 
                else if (stackX == TreeNode.Label.forstart && tokenType == Token.TokenType.TYPE) {
                    //Rule 6 (1) <<for start>> → <<decl>>
                	TreeNode currentNode = new TreeNode(TreeNode.Label.forstart, stackY);
                	stackY.addChild(currentNode);
                    stack.pop();
                    stack.addFirst(new Pair(TreeNode.Label.decl, currentNode));
                } 
                else if (stackX == TreeNode.Label.forstart && tokenType == Token.TokenType.ID) {
                    //Rule 6 (2) <<for start>> → <<assign>>
                	TreeNode currentNode = new TreeNode(TreeNode.Label.forstart, stackY);
                	stackY.addChild(currentNode);
                    stack.pop();
                    stack.addFirst(new Pair(TreeNode.Label.assign, currentNode));
                } 
                else if (stackX == TreeNode.Label.forstart && tokenType == Token.TokenType.SEMICOLON) {
                    //Rule 6 (3) <<for start>> → e
                	TreeNode currentNode = new TreeNode(TreeNode.Label.forstart, stackY);
                	stackY.addChild(currentNode);
                    stack.pop();
                    TreeNode epsilon = new TreeNode(TreeNode.Label.epsilon, currentNode);
                    currentNode.addChild(epsilon);
                } 
                else if (stackX == TreeNode.Label.forarith && (tokenType == Token.TokenType.LPAREN || tokenType == Token.TokenType.ID || tokenType == Token.TokenType.NUM)) {
                    //Rule 7 (1) <<for arith>> → <<arith expr>>
                	TreeNode currentNode = new TreeNode(TreeNode.Label.forarith, stackY);
                	stackY.addChild(currentNode);
                    stack.pop();
                    stack.addFirst(new Pair(TreeNode.Label.arithexpr, currentNode));
                } 
                else if (stackX == TreeNode.Label.forarith && tokenType == Token.TokenType.RPAREN) {
                    //Rule 7 (2) <<for arith>> -> e
                	TreeNode currentNode = new TreeNode(TreeNode.Label.forarith, stackY);
                	stackY.addChild(currentNode);
                    stack.pop();
                    TreeNode epsilon = new TreeNode(TreeNode.Label.epsilon, currentNode);
                    currentNode.addChild(epsilon);
                } 
                else if (stackX == TreeNode.Label.ifstat && tokenType == Token.TokenType.IF) {
                    //Rule 8 <<if>> → if ( <<rel expr>> <<bool expr>> ) { <<los>> } <<else if>>
                	TreeNode currentNode = new TreeNode(TreeNode.Label.ifstat, stackY);
                	stackY.addChild(currentNode);
                    stack.pop();
                    stack.addFirst(new Pair(TreeNode.Label.elseifstat, currentNode));
                    stack.addFirst(new Pair(Token.TokenType.RBRACE, currentNode));
                    stack.addFirst(new Pair(TreeNode.Label.los, currentNode));
                    stack.addFirst(new Pair(Token.TokenType.LBRACE, currentNode));
                    stack.addFirst(new Pair(Token.TokenType.RPAREN, currentNode));
                    stack.addFirst(new Pair(TreeNode.Label.boolexpr, currentNode));
                    stack.addFirst(new Pair(TreeNode.Label.relexpr, currentNode));
                    stack.addFirst(new Pair(Token.TokenType.LPAREN, currentNode));
                    stack.addFirst(new Pair(Token.TokenType.IF, currentNode));
                } 
                else if (stackX == TreeNode.Label.elseifstat && tokenType == Token.TokenType.ELSE) {
                    //Rule 9 <<else if>> → <<else?if>> { <<los>> } <<else if>>
                	TreeNode currentNode = new TreeNode(TreeNode.Label.elseifstat, stackY);
                	stackY.addChild(currentNode);
                    stack.pop();
                    stack.addFirst(new Pair(TreeNode.Label.elseifstat, currentNode));
                    stack.addFirst(new Pair(Token.TokenType.RBRACE, currentNode));
                    stack.addFirst(new Pair(TreeNode.Label.los, currentNode));
                    stack.addFirst(new Pair(Token.TokenType.LBRACE, currentNode));
                    stack.addFirst(new Pair(TreeNode.Label.elseorelseif, currentNode));
                } 
                else if (stackX == TreeNode.Label.elseifstat && (tokenType == Token.TokenType.RBRACE || tokenType == Token.TokenType.SEMICOLON || tokenType == Token.TokenType.TYPE
                || tokenType == Token.TokenType.PRINT || tokenType == Token.TokenType.WHILE || tokenType == Token.TokenType.FOR || tokenType == Token.TokenType.IF || tokenType == Token.TokenType.ID)) {
                    //Rule 9 (2) <<else if>> -> e
                	TreeNode currentNode = new TreeNode(TreeNode.Label.elseifstat, stackY);
                	stackY.addChild(currentNode);
                    stack.pop();
                    TreeNode epsilon = new TreeNode(TreeNode.Label.epsilon, currentNode);
                    currentNode.addChild(epsilon);
                } 
                else if (stackX == TreeNode.Label.elseorelseif && tokenType == Token.TokenType.ELSE) {
                    //Rule 10 <<else?if>> → else <<poss if>>
                	TreeNode currentNode = new TreeNode(TreeNode.Label.elseorelseif, stackY);
                	stackY.addChild(currentNode);
                    stack.pop();
                    stack.addFirst(new Pair(TreeNode.Label.possif, currentNode));
                    stack.addFirst(new Pair(Token.TokenType.ELSE, currentNode));
                } 
                else if (stackX == TreeNode.Label.possif && tokenType == Token.TokenType.IF) {
                    //Rule 11 (1) <<poss if>> → if ( <<rel expr>> <<bool expr>> )
                	TreeNode currentNode = new TreeNode(TreeNode.Label.possif, stackY);
                	stackY.addChild(currentNode);
                    stack.pop();
                    stack.addFirst(new Pair(Token.TokenType.RPAREN, currentNode));
                    stack.addFirst(new Pair(TreeNode.Label.boolexpr, currentNode));
                    stack.addFirst(new Pair(TreeNode.Label.relexpr, currentNode));
                    stack.addFirst(new Pair(Token.TokenType.LPAREN, currentNode));
                    stack.addFirst(new Pair(Token.TokenType.IF, currentNode));
                } 
                else if (stackX == TreeNode.Label.possif && tokenType == Token.TokenType.LBRACE) {
                    //Rule 11 (2) <<poss if>> -> e
                	TreeNode currentNode = new TreeNode(TreeNode.Label.possif, stackY);
                	stackY.addChild(currentNode);
                    stack.pop();
                    TreeNode epsilon = new TreeNode(TreeNode.Label.epsilon, currentNode);
                    currentNode.addChild(epsilon);
                } 
                else if (stackX == TreeNode.Label.assign && tokenType == Token.TokenType.ID) {
                    //Rule 12 <<assign>> → <<ID>> = <<expr>>
                	TreeNode currentNode = new TreeNode(TreeNode.Label.assign, stackY);
                	stackY.addChild(currentNode);
                    stack.pop();
                    stack.addFirst(new Pair(TreeNode.Label.expr, currentNode));
                    stack.addFirst(new Pair(Token.TokenType.ASSIGN, currentNode));
                    stack.addFirst(new Pair(Token.TokenType.ID, currentNode));
                } 
                else if (stackX == TreeNode.Label.decl && tokenType == Token.TokenType.TYPE) {
                    //Rule 13 <<decl>> → <<type>> <<ID>> <<poss assign>>
                	TreeNode currentNode = new TreeNode(TreeNode.Label.decl, stackY);
                	stackY.addChild(currentNode);
                    stack.pop();
                    stack.addFirst(new Pair(TreeNode.Label.possassign, currentNode));
                    stack.addFirst(new Pair(Token.TokenType.ID, currentNode));
                    stack.addFirst(new Pair(TreeNode.Label.type, currentNode));
                } 
                else if (stackX == TreeNode.Label.possassign && tokenType == Token.TokenType.ASSIGN) {
                    //Rule 14 (1) <<poss assign>> → = <<expr>>
                	TreeNode currentNode = new TreeNode(TreeNode.Label.possassign, stackY);
                	stackY.addChild(currentNode);
                    stack.pop();
                    stack.addFirst(new Pair(TreeNode.Label.expr, currentNode));
                    stack.addFirst(new Pair(Token.TokenType.ASSIGN, currentNode));
                } 
                else if (stackX == TreeNode.Label.possassign && tokenType == Token.TokenType.SEMICOLON) {
                    //Rule 14 (2) <<poss assign>> → e
                	TreeNode currentNode = new TreeNode(TreeNode.Label.possassign, stackY);
                	stackY.addChild(currentNode);
                    stack.pop();
                    TreeNode epsilon = new TreeNode(TreeNode.Label.epsilon, currentNode);
                    currentNode.addChild(epsilon);
                } 
                else if (stackX == TreeNode.Label.print && tokenType == Token.TokenType.PRINT) {
                    //Rule 15 <<print>> → System.out.println ( <<print expr>> )
                	TreeNode currentNode = new TreeNode(TreeNode.Label.print, stackY);
                	stackY.addChild(currentNode);
                    stack.pop();
                    stack.addFirst(new Pair(Token.TokenType.RPAREN, currentNode));
                    stack.addFirst(new Pair(TreeNode.Label.printexpr, currentNode));
                    stack.addFirst(new Pair(Token.TokenType.LPAREN, currentNode));
                    stack.addFirst(new Pair(Token.TokenType.PRINT, currentNode));
                } 
                else if (stackX == TreeNode.Label.type && tokens.get(i).getValue().get().equals("int")) {
                    //Rule 16 (1) <<type>> → int
                    TreeNode currentNode = new TreeNode(TreeNode.Label.type, stackY);
                    stackY.addChild(currentNode);
                    stack.pop();
                    stack.addFirst(new Pair(Token.TokenType.TYPE, currentNode));
                }
                else if (stackX == TreeNode.Label.type && tokens.get(i).getValue().get().equals("boolean")) {
                    //Rule 16 (2) <<type>> → boolean
                    TreeNode currentNode = new TreeNode(TreeNode.Label.type, stackY);
                    stackY.addChild(currentNode);
                    stack.pop();
                    stack.addFirst(new Pair(Token.TokenType.TYPE, currentNode));
                } 
                else if (stackX == TreeNode.Label.type && tokens.get(i).getValue().get().equals("char")) {
                    //Rule 16 (3) <<type>> → char
                    TreeNode currentNode = new TreeNode(TreeNode.Label.type, stackY);
                    stackY.addChild(currentNode);
                    stack.pop();
                    stack.addFirst(new Pair(Token.TokenType.TYPE, currentNode));
                } 
                else if (stackX == TreeNode.Label.expr && (tokenType == Token.TokenType.TRUE || tokenType == Token.TokenType.LPAREN || tokenType == Token.TokenType.FALSE
                        || tokenType == Token.TokenType.ID
                        || tokenType == Token.TokenType.NUM)) {
                    //Rule 17 (1) <<expr>> → <<rel expr>> <<bool expr>>
                    TreeNode currentNode = new TreeNode(TreeNode.Label.expr, stackY);
                    stackY.addChild(currentNode);
                    stack.pop();
                    stack.addFirst(new Pair(TreeNode.Label.boolexpr, currentNode));
                    stack.addFirst(new Pair(TreeNode.Label.relexpr, currentNode));
                } 
                else if (stackX == TreeNode.Label.expr && tokenType == Token.TokenType.SQUOTE) {
                    //Rule 17 (2) <<expr>> → <<char expr>>
                    TreeNode currentNode = new TreeNode(TreeNode.Label.expr, stackY);
                    stackY.addChild(currentNode);
                    stack.pop();
                    stack.addFirst(new Pair(TreeNode.Label.charexpr, currentNode));
                } 
                else if (stackX == TreeNode.Label.charexpr && tokenType == Token.TokenType.SQUOTE) {
                    //Rule 18 <<char expr>> → ' <<char>> '
                    TreeNode currentNode = new TreeNode(TreeNode.Label.charexpr, stackY);
                    stackY.addChild(currentNode);
                    stack.pop();
                    stack.addFirst(new Pair(Token.TokenType.SQUOTE, currentNode));
                    stack.addFirst(new Pair(Token.TokenType.CHARLIT, currentNode));
                    stack.addFirst(new Pair(Token.TokenType.SQUOTE, currentNode));
                } 
                else if (stackX == TreeNode.Label.boolexpr && (tokenType == Token.TokenType.EQUAL|| tokenType == Token.TokenType.NEQUAL
                || tokenType == Token.TokenType.AND || tokenType == Token.TokenType.OR)) {
                    //Rule 19 (1) <<bool expr>> → <<bool op>> <<rel expr>> <<bool expr>>
                    TreeNode currentNode = new TreeNode(TreeNode.Label.boolexpr, stackY);
                    stackY.addChild(currentNode);
                    stack.pop();
                    stack.addFirst(new Pair(TreeNode.Label.boolexpr, currentNode));
                    stack.addFirst(new Pair(TreeNode.Label.relexpr, currentNode));
                    stack.addFirst(new Pair(TreeNode.Label.boolop, currentNode));
                } 
                else if (stackX == TreeNode.Label.boolexpr && (tokenType == Token.TokenType.RPAREN
                        || tokenType == Token.TokenType.SEMICOLON)) {
                    TreeNode currentNode = new TreeNode(TreeNode.Label.boolexpr, stackY);
                    stackY.addChild(currentNode);
                    //Rule 19 (2) <<bool expr>> → ε
                    stack.pop();
                    TreeNode epsilon = new TreeNode(TreeNode.Label.epsilon, currentNode);
                    currentNode.addChild(epsilon);
                } 
                else if (stackX == TreeNode.Label.boolop && (tokenType == Token.TokenType.EQUAL
                        || tokenType == Token.TokenType.NEQUAL)) {
                    //Rule 20 (1) <<bool op>> → <<bool eq>>
                    TreeNode currentNode = new TreeNode(TreeNode.Label.boolop, stackY);
                    stackY.addChild(currentNode);
                    stack.pop();
                    stack.addFirst(new Pair(TreeNode.Label.booleq, currentNode));
                } 
                else if (stackX == TreeNode.Label.boolop && (tokenType == Token.TokenType.AND
                        || tokenType == Token.TokenType.OR)) {
                    //Rule 20 (2) <<bool op>> →  <<bool log>>
                    TreeNode currentNode = new TreeNode(TreeNode.Label.boolop, stackY);
                    stackY.addChild(currentNode);
                    stack.pop();
                    stack.addFirst(new Pair(TreeNode.Label.boollog, currentNode));
                } 
                else if (stackX == TreeNode.Label.booleq && tokenType == Token.TokenType.EQUAL) {
                    //Rule 21 (1) <<bool eq>> → ==
                    TreeNode currentNode = new TreeNode(TreeNode.Label.booleq, stackY);
                    stackY.addChild(currentNode);
                    stack.pop();
                    stack.addFirst(new Pair(Token.TokenType.EQUAL, currentNode));
                } 
                else if (stackX == TreeNode.Label.booleq && tokenType == Token.TokenType.NEQUAL) {
                    //Rule 21 (2) <<bool eq>> → !=
                    TreeNode currentNode = new TreeNode(TreeNode.Label.booleq, stackY);
                    stackY.addChild(currentNode);
                    stack.pop();
                    stack.addFirst(new Pair(Token.TokenType.NEQUAL,currentNode));
                } 
                else if (stackX == TreeNode.Label.boollog && tokenType == Token.TokenType.AND) {
                    //Rule 22 (1) <<bool log>> → &&
                    TreeNode currentNode = new TreeNode(TreeNode.Label.boollog, stackY);
                    stackY.addChild(currentNode);
                    stack.pop();
                    stack.addFirst(new Pair(Token.TokenType.AND, currentNode));
                } else if (stackX == TreeNode.Label.boollog && tokenType == Token.TokenType.OR) {
                    //Rule 22 (2) <<bool log>> → ||
                    TreeNode currentNode = new TreeNode(TreeNode.Label.boollog, stackY);
                    stackY.addChild(currentNode);
                    stack.pop();
                    stack.addFirst(new Pair(Token.TokenType.OR, currentNode));
                } 
                else if (stackX == TreeNode.Label.relexpr && (tokenType == Token.TokenType.LPAREN
                        || tokenType == Token.TokenType.ID
                        || tokenType == Token.TokenType.NUM)) {
                    //Rule 23 (1) <<rel expr>> → <<arith expr>> <<rel expr'>>
                    TreeNode currentNode = new TreeNode(TreeNode.Label.relexpr, stackY);
                    stackY.addChild(currentNode);
                    stack.pop();
                    stack.addFirst(new Pair(TreeNode.Label.relexprprime, currentNode));
                    stack.addFirst(new Pair(TreeNode.Label.arithexpr, currentNode));
                } 
                else if (stackX == TreeNode.Label.relexpr && tokenType == Token.TokenType.TRUE) {
                    //Rule 23 (2) <<rel expr>> → true
                    TreeNode currentNode = new TreeNode(TreeNode.Label.relexpr, stackY);
                    stackY.addChild(currentNode);
                    stack.pop();
                    stack.addFirst(new Pair(Token.TokenType.TRUE, currentNode));
                } 
                else if (stackX == TreeNode.Label.relexpr && tokenType == Token.TokenType.FALSE) {
                    //Rule 23 (3) <<rel expr>> → false
                    TreeNode currentNode = new TreeNode(TreeNode.Label.relexpr, stackY);
                    stackY.addChild(currentNode);
                    stack.pop();
                    stack.addFirst(new Pair(Token.TokenType.FALSE, currentNode));
                } 
                else if (stackX == TreeNode.Label.relexprprime && (tokenType == Token.TokenType.LT
                        || tokenType == Token.TokenType.GT
                        || tokenType == Token.TokenType.LE
                        || tokenType == Token.TokenType.GE)) {
                    //Rule 24 (1) <<rel expr'>> → <<rel op>> <<arith expr>>
                    TreeNode currentNode = new TreeNode(TreeNode.Label.relexprprime, stackY);
                    stackY.addChild(currentNode);
                    stack.pop();
                    stack.addFirst(new Pair(TreeNode.Label.arithexpr, currentNode));
                    stack.addFirst(new Pair(TreeNode.Label.relop, currentNode));
                } 
                else if (stackX == TreeNode.Label.relexprprime && (tokenType == Token.TokenType.EQUAL
                || tokenType == Token.TokenType.NEQUAL || tokenType == Token.TokenType.RPAREN || tokenType == Token.TokenType.AND
                || tokenType == Token.TokenType.OR || tokenType == Token.TokenType.SEMICOLON)) {
                    //Rule 24 (2) <<rel expr'>> → ε
                    TreeNode currentNode = new TreeNode(TreeNode.Label.relexprprime, stackY);
                    stackY.addChild(currentNode);
                    stack.pop();
                    TreeNode epsilon = new TreeNode(TreeNode.Label.epsilon, currentNode);
                    currentNode.addChild(epsilon);
                } 
                else if (stackX == TreeNode.Label.relop && tokenType == Token.TokenType.LT) {
                    //Rule 25 (1) <<rel op>> → <
                    TreeNode currentNode = new TreeNode(TreeNode.Label.relop, stackY);
                    stackY.addChild(currentNode);
                    stack.pop();
                    stack.addFirst(new Pair(Token.TokenType.LT, currentNode));
                } 
                else if (stackX == TreeNode.Label.relop && tokenType == Token.TokenType.LE) {
                    //Rule 25 (2) <<rel op>> →  <=
                    TreeNode currentNode = new TreeNode(TreeNode.Label.relop, stackY);
                    stackY.addChild(currentNode);
                    stack.pop();
                    stack.addFirst(new Pair(Token.TokenType.LE, currentNode));
                } 
                else if (stackX == TreeNode.Label.relop && tokenType == Token.TokenType.GT) {
                    //Rule 25 (3) <<rel op>> →  >
                    TreeNode currentNode = new TreeNode(TreeNode.Label.relop, stackY);
                    stackY.addChild(currentNode);
                    stack.pop();
                    stack.addFirst(new Pair(Token.TokenType.GT, currentNode));
                } 
                else if (stackX == TreeNode.Label.relop && tokenType == Token.TokenType.GE) {
                    //Rule 25 (4) <<rel op>> → >=
                    TreeNode currentNode = new TreeNode(TreeNode.Label.relop, stackY);
                    stackY.addChild(currentNode);
                    stack.pop();
                    stack.addFirst(new Pair(Token.TokenType.GE, currentNode));
                } 
                else if (stackX == TreeNode.Label.arithexpr && (tokenType == Token.TokenType.LPAREN
                || tokenType == Token.TokenType.NUM || tokenType == Token.TokenType.ID)) {
                    //Rule 26 <<arith expr>> → <<term>> <<arith expr'>>
                    TreeNode currentNode = new TreeNode(TreeNode.Label.arithexpr, stackY);
                    stackY.addChild(currentNode);
                    stack.pop();
                    stack.addFirst(new Pair(TreeNode.Label.arithexprprime, currentNode));
                    stack.addFirst(new Pair(TreeNode.Label.term, currentNode));
                } 
                else if (stackX == TreeNode.Label.arithexprprime && tokenType == Token.TokenType.PLUS) {
                    //Rule 27 (1) <<arith expr'>> → + <<term>> <<arith expr'>>
                    TreeNode currentNode = new TreeNode(TreeNode.Label.arithexprprime, stackY);
                    stackY.addChild(currentNode);
                    stack.pop();
                    stack.addFirst(new Pair(TreeNode.Label.arithexprprime, currentNode));
                    stack.addFirst(new Pair(TreeNode.Label.term, currentNode));
                    stack.addFirst(new Pair(Token.TokenType.PLUS, currentNode));
                } 
                else if (stackX == TreeNode.Label.arithexprprime && tokenType == Token.TokenType.MINUS) {
                    //Rule 27 (2) <<arith expr'>> → - <<term>> <<arith expr'>>
                    TreeNode currentNode = new TreeNode(TreeNode.Label.arithexprprime, stackY);
                    stackY.addChild(currentNode);
                    stack.pop();
                    stack.addFirst(new Pair(TreeNode.Label.arithexprprime, currentNode));
                    stack.addFirst(new Pair(TreeNode.Label.term, currentNode));
                    stack.addFirst(new Pair(Token.TokenType.MINUS, currentNode));
                } 
                else if (stackX == TreeNode.Label.arithexprprime && (tokenType == Token.TokenType.EQUAL || tokenType == Token.TokenType.NEQUAL
                || tokenType == Token.TokenType.LT || tokenType == Token.TokenType.GT || tokenType == Token.TokenType.LE || tokenType == Token.TokenType.GE
                || tokenType == Token.TokenType.RPAREN || tokenType == Token.TokenType.AND || tokenType == Token.TokenType.OR || tokenType == Token.TokenType.SEMICOLON)) {
                    //Rule 27 (3) <<arith expr'>> → ε
                    TreeNode currentNode = new TreeNode(TreeNode.Label.arithexprprime, stackY);
                    stackY.addChild(currentNode);
                    stack.pop();
                    TreeNode epsilon = new TreeNode(TreeNode.Label.epsilon, currentNode);
                    currentNode.addChild(epsilon);
                } 
                else if (stackX == TreeNode.Label.term && (tokenType == Token.TokenType.LPAREN
                || tokenType == Token.TokenType.ID || tokenType == Token.TokenType.NUM)) {
                    //Rule 28 <<term>> → <<factor>> <<term'>>
                    TreeNode currentNode = new TreeNode(TreeNode.Label.term, stackY);
                    stackY.addChild(currentNode);
                    stack.pop();
                    stack.addFirst(new Pair(TreeNode.Label.termprime, currentNode));
                    stack.addFirst(new Pair(TreeNode.Label.factor, currentNode));
                } 
                else if (stackX == TreeNode.Label.termprime && tokenType == Token.TokenType.TIMES) {
                    //Rule 29 (1) <<term'>> → * <<factor>> <<term'>>
                    TreeNode currentNode = new TreeNode(TreeNode.Label.termprime, stackY);
                    stackY.addChild(currentNode);
                    stack.pop();
                    stack.addFirst(new Pair(TreeNode.Label.termprime, currentNode));
                    stack.addFirst(new Pair(TreeNode.Label.factor, currentNode));
                    stack.addFirst(new Pair(Token.TokenType.TIMES,currentNode));
                } 
                else if (stackX == TreeNode.Label.termprime && tokenType == Token.TokenType.DIVIDE) {
                    //Rule 29 (2) <<term'>> → / <<factor>> <<term'>>
                    TreeNode currentNode = new TreeNode(TreeNode.Label.termprime, stackY);
                    stackY.addChild(currentNode);
                    stack.pop();
                    stack.addFirst(new Pair(TreeNode.Label.termprime, currentNode));
                    stack.addFirst(new Pair(TreeNode.Label.factor, currentNode));
                    stack.addFirst(new Pair(Token.TokenType.DIVIDE, currentNode));
                } 
                else if (stackX == TreeNode.Label.termprime && tokenType == Token.TokenType.MOD) {
                    //Rule 29 (3) <<term'>> → % <<factor>> <<term'>>
                    TreeNode currentNode = new TreeNode(TreeNode.Label.termprime, stackY);
                    stackY.addChild(currentNode);
                    stack.pop();
                    stack.addFirst(new Pair(TreeNode.Label.termprime, currentNode));
                    stack.addFirst(new Pair(TreeNode.Label.factor, currentNode));
                    stack.addFirst(new Pair(Token.TokenType.MOD, currentNode));
                } 
                else if (stackX == TreeNode.Label.termprime && (tokenType == Token.TokenType.PLUS || tokenType == Token.TokenType.MINUS
                || tokenType == Token.TokenType.EQUAL || tokenType == Token.TokenType.NEQUAL || tokenType == Token.TokenType.LT || tokenType == Token.TokenType.GT
                || tokenType == Token.TokenType.LE || tokenType == Token.TokenType.GE || tokenType == Token.TokenType.RPAREN
                || tokenType == Token.TokenType.AND || tokenType == Token.TokenType.OR || tokenType == Token.TokenType.SEMICOLON)) {
                    //Rule 29 (4) <<term'>> → ε
                    TreeNode currentNode = new TreeNode(TreeNode.Label.termprime, stackY);
                    stackY.addChild(currentNode);
                    stack.pop();
                    TreeNode epsilon = new TreeNode(TreeNode.Label.epsilon, currentNode);
                    currentNode.addChild(epsilon);
                } 
                else if (stackX == TreeNode.Label.factor && tokenType == Token.TokenType.LPAREN) {
                    //Rule 30 (1) <<factor>> → ( <<arith expr>> )
                    TreeNode currentNode = new TreeNode(TreeNode.Label.factor, stackY);
                    stackY.addChild(currentNode);
                    stack.pop();
                    stack.addFirst(new Pair(Token.TokenType.RPAREN, currentNode));
                    stack.addFirst(new Pair(TreeNode.Label.arithexpr, currentNode));
                    stack.addFirst(new Pair(Token.TokenType.LPAREN, currentNode));
                } 
                else if (stackX == TreeNode.Label.factor && tokenType == Token.TokenType.ID) {
                    //Rule 30 (2) <<factor>> → <<ID>>
                    TreeNode currentNode = new TreeNode(TreeNode.Label.factor, stackY);
                    stackY.addChild(currentNode);
                    stack.pop();
                    stack.addFirst(new Pair(Token.TokenType.ID, currentNode));
                } 
                else if (stackX == TreeNode.Label.factor && tokenType == Token.TokenType.NUM) {
                    //Rule 30 (3) <<factor>> → <<num>>
                    TreeNode currentNode = new TreeNode(TreeNode.Label.factor, stackY);
                    stackY.addChild(currentNode);
                    stack.pop();
                    stack.addFirst(new Pair(Token.TokenType.NUM, currentNode));
                } 
                else if (stackX == TreeNode.Label.printexpr && (tokenType == Token.TokenType.LPAREN || tokenType == Token.TokenType.TRUE
                 || tokenType == Token.TokenType.FALSE || tokenType == Token.TokenType.ID || tokenType == Token.TokenType.NUM)) {
                    //Rule 31 (1) <<print expr>> → <<rel expr>> <<bool expr>>
                    TreeNode currentNode = new TreeNode(TreeNode.Label.printexpr, stackY);
                    stackY.addChild(currentNode);
                    stack.pop();
                    stack.addFirst(new Pair(TreeNode.Label.boolexpr, currentNode));
                    stack.addFirst(new Pair(TreeNode.Label.relexpr, currentNode));
                } 
                else if (stackX == TreeNode.Label.printexpr && tokenType == Token.TokenType.DQUOTE) {
                    //Rule 31 (2) <<print expr>> →"<<string lit>> "
                    TreeNode currentNode = new TreeNode(TreeNode.Label.printexpr, stackY);
                    stackY.addChild(currentNode);
                    stack.pop();
                    stack.addFirst(new Pair(Token.TokenType.DQUOTE, currentNode));
                    stack.addFirst(new Pair(Token.TokenType.STRINGLIT, currentNode));
                    stack.addFirst(new Pair(Token.TokenType.DQUOTE, currentNode));
                }  
                else{
                    throw new SyntaxException("No rule");
                }
            }
            else if(!stackX.isVariable()){
                if(tokenType == stackX){
                    TreeNode currentNode = new TreeNode(TreeNode.Label.terminal, tokens.get(i), stackY);
                    stackY.addChild(currentNode);
                    stack.pop();
                    i++;
                } 
                else{
                  throw new SyntaxException("Syntax Error"); // NO MATCH
                }

            }
        }
        return parseTree;
    }
}
