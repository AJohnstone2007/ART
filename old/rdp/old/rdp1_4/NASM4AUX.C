#include <stdio.h>
#include <stdarg.h>
#include "scan.h"
#include "symbol.h"
#include "textio.h"
#include "nasm4aux.h"

unsigned width = 8;
unsigned long *location;
unsigned long data_location;
unsigned long code_location;
unsigned long transfer = 0;
void *last_label;

static FILE *objfile;
char emit_code;

void init(char *outputfilename)
{
	if ((objfile = fopen(outputfilename, "w")) == NULL)
		text__message(TEXT__FATAL, "Unable to open object file");
}

int quit(void)
{
	return (fclose(objfile));
}

/* check that an integer falls within bounds */
void test_size(long int i, unsigned prec)
{
	if (!emit_code)				/* no-op if not emitting... */
		return;

	switch (prec)
	{
 case 1:
		if (i > 15 || i < -8)
			text__message(TEXT__ERROR_ECHO, "Constant too large: only four bits available");
		break;

	case 2:
		if (i > 255 || i < -128)
			text__message(TEXT__ERROR_ECHO, "Constant too large: only eight bits available");
		break;

	case 3:
		if (i > 2047L || i < -20488L)
			text__message(TEXT__ERROR_ECHO, "Constant too large: only 12 bits available");
		break;

	case 4:
		if (i > 65535L || i < -32768L)
			text__message(TEXT__ERROR_ECHO, "Constant too large: only 16 bits available");
		break;

	case 8:						/* can't be wrong */
		break;

	default:
		text__message(TEXT__FATAL, "illegal precision specified in call to test_size()");
		break;
	}
}

int eprintf(char *fmt,...)		/* conditional print to object file */
{
	int i;
	va_list ap;					/* argument list walker */

	va_start(ap, fmt);

	if (emit_code)				/* no-op if not emitting... */
		i = vfprintf(objfile, fmt, ap);	/* ... otherwise pass to fprintf() */

	va_end(ap);

	return (i);					/* for completeness, although not used here */
}


/* send a nibble to object file */
void emit(long int i)
{
	eprintf("%.1lX", i & 0xF);
	(*location)++;
	if (*location > 0x1FFFFL)
	{
		text__message(TEXT__ERROR_ECHO, "Location counter overflow");
		location = 0;
	}
}

/* output multiple nibbles */
void multi_emit(long int i, unsigned prec)
{
	test_size(i, prec);

	while (prec-->0)
	{
		emit(i);
		i>>=4;
	}
}

void multi_emit2(long int i, unsigned prec)
{
	test_size(i, prec);

	if (prec == 1)
		emit(i);
	else
		while (prec>0)
		{
			emit(i>>4);
			emit(i);
			i>>=8;
			prec -= 2;
		}
}

