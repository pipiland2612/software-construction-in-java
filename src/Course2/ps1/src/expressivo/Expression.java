package expressivo;

import lib6005.parser.GrammarCompiler;
import lib6005.parser.ParseTree;
import lib6005.parser.Parser;

import java.io.File;
import java.util.List;

/**
 * An immutable data type representing a polynomial expression of:
 * + and *
 * nonnegative integers and floating-point numbers
 * variables (case-sensitive nonempty strings of letters)
 *
 * <p>PS1 instructions: this is a required ADT interface.
 * You MUST NOT change its name or package or the names or type signatures of existing methods.
 * You may, however, add additional methods, or strengthen the specs of existing methods.
 * Declare concrete variants of Expression in their own Java source files.
 */
public interface Expression {

    enum ExpressionGrammar {ROOT, SUM, PRODUCT, PRIMITIVE, NUMBER, WHITESPACE}

    /**
     * Parse an expression.
     *
     * @param input expression to parse, as defined in the PS1 handout.
     * @return expression AST for the input
     * @throws IllegalArgumentException if the expression is invalid
     */
    public static Expression parse(String input) {
        try {
            Parser<ExpressionGrammar> parser = GrammarCompiler.compile(new File("Expression.g"), ExpressionGrammar.ROOT);
            ParseTree<ExpressionGrammar> tree = parser.parse(input);
            return buildAST(tree);
        } catch (Exception e) {
            throw new IllegalArgumentException("Invalid expression");
        }
    }

    private static Expression buildAST(ParseTree<ExpressionGrammar> p) {
        switch (p.getName()) {
            case NUMBER:
                return new Number(Double.parseDouble(p.getContents()));
            case PRIMITIVE:
                if (p.childrenByName(ExpressionGrammar.NUMBER).isEmpty()) {
                    return buildAST(p.childrenByName(ExpressionGrammar.SUM).get(0));
                } else {
                    return buildAST(p.childrenByName(ExpressionGrammar.NUMBER).get(0));
                }
            case SUM:
                Expression sumLeft = buildAST(p.childrenByName(ExpressionGrammar.PRODUCT).get(0));
                // Get the remaining element and recursively build tree from those
                List<ParseTree<ExpressionGrammar>> remainingProd = p.childrenByName(ExpressionGrammar.PRODUCT).subList(1, p.children().size());
                for (ParseTree<ExpressionGrammar> child : remainingProd) {
                    sumLeft = new Sum(sumLeft, buildAST(child));
                }
                return sumLeft;
            case PRODUCT:
                Expression productLeft = buildAST(p.childrenByName(ExpressionGrammar.PRIMITIVE).get(0));
                // Get the remaining element and recursively build tree from those
                List<ParseTree<ExpressionGrammar>> remainingElems = p.childrenByName(ExpressionGrammar.PRIMITIVE).subList(1, p.children().size());
                for (ParseTree<ExpressionGrammar> child : remainingElems) {
                    productLeft = new Product(productLeft, buildAST(child));
                }
                return productLeft;
            case ROOT:
                return buildAST(p.childrenByName(ExpressionGrammar.SUM).get(0));
            default:
                throw new RuntimeException("Unexpected tree node" + p);
        }
    }

    /**
     * @return a parsable representation of this expression, such that
     * for all e:Expression, e.equals(Expression.parse(e.toString())).
     */
    @Override
    public String toString();

    /**
     * @param thatObject any object
     * @return true if and only if this and thatObject are structurally-equal
     * Expressions, as defined in the PS1 handout.
     */
    @Override
    public boolean equals(Object thatObject);

    /**
     * @return hash code value consistent with the equals() definition of structural
     * equality, such that for all e1,e2:Expression,
     * e1.equals(e2) implies e1.hashCode() == e2.hashCode()
     */
    @Override
    public int hashCode();

    // TODO more instance methods

    /* Copyright (c) 2015-2017 MIT 6.005 course staff, all rights reserved.
     * Redistribution of original or derived work requires permission of course staff.
     */
}
