/****************************************************************************
*
* RDP release 1.40 by Adrian Johnstone (adrian@dcs.rhbnc.ac.uk) 14 Jan 1995
*
* scantest.c - test all scanner routines. See scantest.ok for correct output.
*
* This file may be freely distributed. Please mail improvements to the author.
*
****************************************************************************/
#include "stdio.h"
#include "memalloc.h"
#include "textio.h"
#include "scan.h"

enum {T__SHR_BECOMES = T__TOP, T__SHREIK, T__QUERY, T__DOLLAR};

int main(void)
{

  set__ include_directive = SET__NULL;

  set__assign_list(&include_directive, T__SHREIK, T__QUERY, T__DOLLAR, SET__END);

  text__init(20000,100,25,8);
  scan__init(1,1,1,1,NULL);
  text__open("text.1");
  text__get_char();

  text__echo(1);

  scan__load_keyword("!", NULL, T__SHREIK);
  scan__load_keyword("?", NULL, T__QUERY);
  scan__load_keyword("$", NULL, T__DOLLAR);
  scan__load_keyword("'", NULL, T__ISTRING);
  scan__load_keyword("\"", "\\", T__ISTRING_ESC);
  scan__load_keyword("{", "}", T__ICOMMENT);
  scan__load_keyword("<", ">", T__ICOMMENT_VISIBLE);
  scan__load_keyword("/*", "*/", T__ICOMMENT_NEST);
  scan__load_keyword("\\*", "*\\", T__ICOMMENT_NEST_VISIBLE);
  scan__load_keyword("++", NULL, T__ICOMMENT_LINE);
  scan__load_keyword("--", NULL, T__ICOMMENT_LINE_VISIBLE);
  scan__load_keyword(">>=", NULL, T__SHR_BECOMES);

  scan__load_keyword("+", NULL, T__IGNORE);
  scan__load_keyword("-", NULL, T__IGNORE);
  scan__load_keyword(">", NULL, T__IGNORE);
  scan__load_keyword(">>", NULL, T__IGNORE);

  scan__();

  while (!scan__test(T__EOF, NULL))
  {

    text__printf("%c", text__char);

    if (scan__test_set(&include_directive, NULL))
      text__message(TEXT__WARNING, "saw include directive\n");
    if (scan__test(T__SHREIK, NULL))
      if (text__open("zzz.zzz.zzz") == NULL)
	text__message(TEXT__ERROR_ECHO, "can't find include file 'zzz.zzz.zzz'\n");
      else
	text__message(TEXT__WARNING_ECHO, "opened include file 'text.1'\n");

    if (scan__test(T__QUERY, NULL))
      if (text__open("text.2") == NULL)
	text__message(TEXT__ERROR_ECHO, "can't find include file 'text.2'\n");
      else
	text__message(TEXT__WARNING_ECHO, "opened include file 'text.2'\n");

    if (scan__test(T__DOLLAR, NULL))
      if (text__open("text.3") == NULL)
	text__message(TEXT__ERROR_ECHO, "can't find include file 'text.2'\n");
      else
	text__message(TEXT__WARNING_ECHO, "opened include file 'text.3'\n");

    scan__();
  }

  text__print_total_errors();

  return 0;
}
