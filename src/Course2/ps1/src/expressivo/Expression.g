root ::= sum;
@skip whitespace{
	sum ::= product ('+' product)*;
	product ::= primitive ('*' primitive)*;
	primitive ::= number | variable | '(' sum ')';
}
number ::= ([0-9]+)( '.' [0-9]+)?;
variable ::= [A-Za-z]+;
whitespace ::= [ ]+;
