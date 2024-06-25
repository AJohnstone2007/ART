/* Auxilliary routines for nasm4 */
#include "symbol.h"

extern unsigned width;
extern unsigned long *location;
extern unsigned long code_location;
extern unsigned long data_location;
extern unsigned long transfer;
extern void* last_label;
extern char emit_code;

void emit(long int i);
int eprintf(char *fmt,...);
void init(char *outputfilename);
void multi_emit(long int i, unsigned prec);
void multi_emit2(long int i, unsigned prec);
int quit(void);
void test_size(long int i, unsigned prec);
