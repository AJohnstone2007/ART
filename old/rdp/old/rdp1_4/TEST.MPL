(*******************************************************************************
*
* RDP release 1.40 by Adrian Johnstone (adrian@dcs.rhbnc.ac.uk) 30 Jan 1995
*
* test.mpl - a piece of Mini-plus source to test the Mini-plus interpreter
*
* This file may be freely distributed. Please mail improvements to the author.
*
*******************************************************************************)

int a=3+4, b=1;

print("a is ", a, "\n");

b=a*2;

print("b is ", b, ", -b is ", -b, "\n");

int z = a;

if z==a then print ("z equals a\n") else print("z does not equal a\n");

z=a-1;

if z==a then print ("z equals a\n") else print("z does not equal a\n");


(* End of test.mpl *)
