// Some tidying up here to ensure code works both with archaic Borland compiler and current GNU g++
#if defined(__BORLANDC__)
#include<stdlib.h>
#include<stdio.h>
#include<stdarg.h>
#include<time.h>
#include<iostream.h>
#include<fstream.h>
#include<limits.h>
#include<ctype.h>
#include<time.h>
#else
#include<stdlib.h>
#include<stdio.h>
#include<string.h>
#include<limits.h>

#include<iostream>
#include<iomanip>
#include<fstream>
#include<cstdarg>
#include<limits>
#include<time.h>
using namespace std;
#endif
