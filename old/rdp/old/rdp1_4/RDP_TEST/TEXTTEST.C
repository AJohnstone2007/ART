/****************************************************************************
*
* RDP release 1.40 by Adrian Johnstone (adrian@dcs.rhbnc.ac.uk) 14 Jan 1995
*
* texttest.c - test all textio.c routines. See texttest.ok for correct output
*
* This file may be freely distributed. Please mail improvements to the author.
*
****************************************************************************/
#include "stdio.h"
#include "memalloc.h"
#include "textio.h"

int main(void)
{
/* test routines prior to initialisation */
  FILE * redirected = fopen("textout.txt","w");
  int count;
  char *str;

  if (redirected == NULL)
    text__message(TEXT__FATAL, "Can't open test output file\n");

  for (count = 0; count<2; count++)
  {
    text__printf("\n\ntextio test start\n\n");

    text__printf("after forcing filetype 'text' onto 'adrian.was.here': '%s'\n",
		 text__force_filetype("adrian.was.here", "text"));

    text__printf("after forcing filetype 'text' onto 'adrian.': '%s'\n",
		 text__force_filetype("adrian.", "text"));

    text__printf("after forcing filetype 'text' onto 'adrian': '%s'\n",
		 text__force_filetype("adrian", "text"));

    text__printf("after defaulting filetype 'text' onto 'adrian.was.here': '%s'\n",
		 text__default_filetype("adrian.was.here", "text"));

    text__printf("after defaulting filetype 'text' onto 'adrian.': '%s'\n",
		 text__default_filetype("adrian.", "text"));

    text__printf("after defaulting filetype 'text' onto 'adrian': '%s'\n",
		 text__default_filetype("adrian", "text"));


    text__message(TEXT__ERROR, "This is a test %s\n","error message");

    text__message(TEXT__ERROR_ECHO, "This is a test %s\n","error echo message");

    text__print_total_errors();

    text__printf("Total errors = %u, total warnings = %u\n",text__total_errors(), text__total_warnings());

    text__print_statistics();

    text__init(500, 4, 2, 3);

    str = text__insert_char('Z');
    text__insert_integer(12345);
    text__insert_string("adrian was here");
    text__printf("Inserted text: '%s'\n", str);

    str = text__insert_substring(" identifier", 32);
    text__printf("Inserted text: '%s'\n", str);

    text__get_char();
    if (text__char != EOF)
      text__message(TEXT__FATAL, "failed to read EOF on closed file!\n");
    else
      text__message(TEXT__INFO, "successfully read EOF on closed file\n");

    if (text__open("zzz.zzz.zzz") == NULL)
      text__message(TEXT__INFO,"can't find file 'zzz.zzz.zzz'\n");
    else
      text__message(TEXT__FATAL, "found a non-existent file!\n");

    text__get_char();
    if (text__char != EOF)
      text__message(TEXT__FATAL, "failed to read EOF on closed file!\n");
    else
      text__message(TEXT__INFO, "successfully read EOF on closed file\n");

    if (text__open("text.1") == NULL)
      text__message(TEXT__FATAL, "can't find file 'text.1'\n");
    else
      text__message(TEXT__INFO, "successfully opened 'text.1'\n");

    text__printf("Echoing files to message stream\n");


    while (text__get_char(), text__char != EOF)
    {
      text__printf("%c", text__char);
      if (text__char == '!')
	if (text__open("zzz.zzz.zzz") == NULL)
	  text__message(TEXT__ERROR_ECHO, "can't find include file 'zzz.zzz.zzz'\n");
	else
	  text__message(TEXT__WARNING_ECHO, "opened include file 'text.1'\n");

      if (text__char == '?')
	if (text__open("text.2") == NULL)
	  text__message(TEXT__ERROR_ECHO, "can't find include file 'text.2'\n");
	else
	  text__message(TEXT__WARNING_ECHO, "opened include file 'text.2'\n");

      if (text__char == '$')
	if (text__open("text.3") == NULL)
	  text__message(TEXT__ERROR_ECHO, "can't find include file 'text.2'\n");
	else
	  text__message(TEXT__WARNING_ECHO, "opened include file 'text.3'\n");
    }

    text__redirect(redirected);
    text__echo(1);

  }

  text__redirect(stdout);

  text__printf("textio test end\n\n");

  fclose(redirected);
  return 0;
}
