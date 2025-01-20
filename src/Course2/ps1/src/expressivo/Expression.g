root ::= sum;
@skip whitespace{
	sum ::= product ('+' product)*;
	product ::= primitive ('*' primitive)*;
	primitive ::= number | '(' sum ')';
}
number ::= [0-9]+;

whitespace ::= [ ]+;
