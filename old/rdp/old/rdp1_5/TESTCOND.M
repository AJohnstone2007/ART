(*******************************************************************************
*
* RDP release 1.50 by Adrian Johnstone (A.Johnstone@rhbnc.ac.uk) 20 December 1997
*
* testcond.m - a piece of Minicond source to test the Minicond interpreter
*
* This file may be freely distributed. Please mail improvements to the author.
*
*******************************************************************************)

int a=3+4, b=1;

print("a is ", a, "\n");

b=a*2;

print("b is ", b, ", -b is ", -b, "\n");

print(a, " cubed is ", a**3, "\n");

int z = a;

if z==a then print ("z equals a\n") else print("z does not equal a\n");

z=a - 3;

if z==a then print ("z equals a\n") else print("z does not equal a\n");

(* End of testcond.m *)
