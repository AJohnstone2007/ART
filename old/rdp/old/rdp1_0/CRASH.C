/****************************************************************************
*
* RDP release 1.00 by Adrian Johnstone (adrian@dcs.rhbnc.ac.uk) 14 Feb 1994
*
* crash.c - fatal error handling and memory allocation
*
* This file may be freely distributed. Please mail improvements to the author.
*
****************************************************************************/
#include <stdio.h>
#include <stdlib.h>
#include <stdarg.h>
#include "crash.h"

void crash__(char *fmt,...)
{
	va_list ap;										/* argument list walker */

	printf("\n");
	va_start(ap, fmt);
	vprintf(fmt, ap);
	va_end(ap);
	printf("\n");

	exit(1);
}


void *crash__malloc(size_t size, char *fmt,...)
{
	void *p = malloc(size);

	if (p == NULL)
	{
		va_list ap;									/* argument list walker */

		printf("\nRan out of memory during allocation of ");
		va_start(ap, fmt);
		vprintf(fmt, ap);
		va_end(ap);
		printf("\n");

		exit(1);
	}

	return p;
}

void *crash__calloc(size_t size, char *fmt,...)
{
	void *p = calloc(1, size);

	if (p == NULL)
	{
		va_list ap;									/* argument list walker */

		printf("\nRan out of memory during allocation of ");
		va_start(ap, fmt);
		vprintf(fmt, ap);
		va_end(ap);
		printf("\n");

		exit(1);
	}

	return p;
}

/* End of crash.c */
