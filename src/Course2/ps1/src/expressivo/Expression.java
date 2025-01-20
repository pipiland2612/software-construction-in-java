package expressivo;

import lib6005.parser.GrammarCompiler;
import lib6005.parser.ParseTree;
import lib6005.parser.Parser;

import java.io.File;

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

    enum ExpressionGrammar {ROOT, SUM, PRODUCT, PRIMITIVE, NUMBER, VARIABLE, WHITESPACE}

    /**
     * Parse an expression.
     *
     * @param input expression to parse, as defined in the PS1 handout.
     * @return expression AST for the input
     * @throws IllegalArgumentException if the expression is invalid
     */
    public static Expression parse(String input) {
        try {
            Parser<ExpressionGrammar> parser = GrammarCompiler.compile(
                    new File("/Users/batman/Desktop/Java/SCJava/src/Course2/ps1/src/expressivo/Expression.g"),
                    ExpressionGrammar.ROOT
            );
            ParseTree<ExpressionGrammar> tree = parser.parse(input);
            return buildAST(tree);
        } catch (Exception e) {
            throw new IllegalArgumentException("Invalid expression", e);
        }
    }

    private static Expression buildAST(ParseTree<ExpressionGrammar> p) {

        switch (p.getName()) {
            case NUMBER:
                double numberValue = Double.parseDouble(p.getContents());
                return new Number(numberValue);
            case VARIABLE:
                String variableName = p.getContents();
                return new Variable(variableName);
            case PRIMITIVE:
                if (!p.childrenByName(ExpressionGrammar.NUMBER).isEmpty())
                    return buildAST(p.childrenByName(ExpressionGrammar.NUMBER).get(0));

                if (!p.childrenByName(ExpressionGrammar.VARIABLE).isEmpty())
                    return buildAST(p.childrenByName(ExpressionGrammar.VARIABLE).get(0));

                if (!p.childrenByName(ExpressionGrammar.SUM).isEmpty())
                    return buildAST(p.childrenByName(ExpressionGrammar.SUM).get(0));
                else
                    throw new RuntimeException("Unexpected node in PRIMITIVE: " + p);
            case SUM:
                boolean first = true;
                Expression result = null;
                for (ParseTree<ExpressionGrammar> child : p.childrenByName(ExpressionGrammar.PRODUCT)) {
                    if (first) {
                        result = buildAST(child);
                        first = false;
                    } else {
                        result = new Sum(result, buildAST(child));
                    }
                }
                if (first) {
                    throw new RuntimeException("sum must have a non whitespace child:" + p);
                }
                return result;

            case PRODUCT:
                boolean f = true;
                Expression res = null;
                for (ParseTree<ExpressionGrammar> child : p.childrenByName(ExpressionGrammar.PRIMITIVE)) {
                    if (f) {
                        res = buildAST(child);
                        f = false;
                    } else {
                        res = new Product(res, buildAST(child));
                    }
                }
                if (f) {
                    throw new RuntimeException("sum must have a non whitespace child:" + p);
                }
                return res;
            case ROOT:
                return buildAST(p.childrenByName(ExpressionGrammar.SUM).get(0));
            default:
                throw new RuntimeException("Unexpected tree node: " + p);
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

    /**
     * @return the Expression that is differentiated from currnt expression
     */
    public Expression differentiate(String variable);
}
