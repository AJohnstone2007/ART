/*******************************************************************************
*
* RDP release 1.50 by Adrian Johnstone (A.Johnstone@rhbnc.ac.uk) 20 December 1997
*
* mvmsim.c - Mini Virtual Machine simulator
*
* This file may be freely distributed. Please mail improvements to the author.
*
*******************************************************************************/
#include <time.h>
#include <stdio.h>
#include <math.h>
#include "arg.h"
#include "memalloc.h"
#include "textio.h"
#include "mvm_def.h"

#define MEM_SIZE 65536lu
char *rdp_sourcefilename;          /* dummy current source file name to stop linker errors in makefile */
int mvmsim_verbose = 0;
int mvmsim_echo_load = 0;
int mvmsim_trace = 0;
unsigned char memory[MEM_SIZE];
unsigned long instructionStartPC = 0;
unsigned long pc = 0;
unsigned long sp = 0;
unsigned long exec_count = 0;
FILE * input_file = NULL;
int in_char = ' '; 

#define get_char in_char = fgetc(input_file)

static void test_address(unsigned long address)
{
  if (address >= MEM_SIZE)
  {
    text_printf("\n"); 
    text_message(TEXT_FATAL, "illegal access to location %lu, PC = %lu\n", address, pc);
  }
}

static int get_memory_byte(unsigned long address)
{
  int val; 
  
  test_address(address); 
  val = memory[address]; 
  
  if (val & 0x40)             /* negative? */
    val =(- 1l << 8)| val; 
  
  return val; 
}

static int get_memory_word(unsigned long address)
{
  int val;
  
  test_address(address + 1); 
  val =((int) memory[address]<< 8)|((int) memory[address + 1]);
  
  if (val & 0x4000)           /* negative? */
    val =(- 1l << 16)| val; 

  return val; 
}

static void put_memory_byte(unsigned long address, int data)
{
  test_address(address); 
  memory[address]=(unsigned char) data; 
}

static void put_memory_word(unsigned long address, int data)
{
  test_address(address + 1); 
  memory[address]=(unsigned char)(data >> 8); 
  memory[address + 1]=(unsigned char) data; 
}

static void skip_space(void)
{
  while (in_char == ' ')      /* skip spaces */
    get_char;
}

static long int get1hex(void)
{
  int ret = 0; 
  
  if (in_char >= '0' && in_char <= '9')
    ret = in_char - '0'; 
  else if (in_char >= 'A' && in_char <= 'F')
    ret = in_char - 'A' + 10; 
  else if (in_char >= 'a' && in_char <= 'f')
    ret = in_char - 'a' + 10; 
  else
  {
    text_printf("\n");
    text_message(TEXT_FATAL, "unexpected non-hexadecimal character in input file\n"); 
  }
  
  get_char; 
  skip_space(); 
  
  return ret; 
}

static long int get2hex(void)
{
  return(get1hex()<< 4)| get1hex(); 
}

static long int get4hex(void)
{
  return(get2hex()<< 8)| get2hex(); 
}

static void mvmsim_load(void)
{
  unsigned long address,
  data; 
  
  while (in_char != EOF)
  {
    while (in_char == '\n' || in_char == ' ')
      get_char;               /* skip empty spaces and lines */
    
    address = get4hex(); 
    
    if (mvmsim_echo_load)
      text_printf("Load address %.4lX ", address); 
    
    if (in_char == '*')       /* get transfer address */
    {
      get_char;               /* skip * */
      pc = get4hex();
      if (mvmsim_echo_load)
        text_printf("*%.4lX", pc);
    }
    else
      while (in_char != '\n')
    {
      data = get2hex();
      if (mvmsim_echo_load)
        text_printf("%.2lX", data);
      put_memory_byte(address++, data);
    }

    get_char;                 /* skip end of line */

    if (mvmsim_echo_load)
      text_printf("\n");
  }
}

static void display(char * op, unsigned dst, unsigned src1, unsigned src2)
{
  if (mvmsim_trace)
    printf("\n%.4X %.4X %s [%.4X] = %.4X <- [%.4X] = %.4X, [%.4X] = %.4X", (unsigned) instructionStartPC, sp, op, dst, get_memory_word(dst)& 0xFFFF, src1, get_memory_word(src1) & 0xFFFF, src2, get_memory_word(src2) & 0xFFFF);
}

  static int operandCount[] = {
  0, /* OP_HALT */
  3, /* OP_ADD */
  3, /* OP_SUB */
  3, /* OP_MUL */
  3, /* OP_DIV */
  3, /* OP_EXP */
  3, /* OP_EQ */
  3, /* OP_NE */
  3, /* OP_GT */
  3, /* OP_GE */
  3, /* OP_LT */
  3, /* OP_LE */
  2, /* OP_CPY */
  2, /* OP_BNE */
  2, /* OP_BEQ */
  1, /* OP_BRA */
  1, /* OP_PRTS */
  1, /* OP_PRTI */
  1, /* OP_CALL */
  0, /* OP_RET */
  1, /* OP_PUSH */
  1, /* OP_POP */
  1, /* OP_LDSP */
  1  /* OP_STSP */
};

static void mvmsim_execute(void)
{
  int stop = 0;

  int operands[3] = {0};

  while (!stop)
  {
    unsigned op, mode;
    int operand;

    instructionStartPC = pc;
    operands[0] = operands[1] = operands[2] = 0;

    op = get_memory_byte(pc++);
    mode = get_memory_byte(pc++);

    /* Now load operands array with the _address_ of the operand - the old mvmsim loaded src operands with the data... */
    for (operand = 0; operand < operandCount[op]; operand++) {
      switch (mode & 0x3) {
        case MODE_CONSTANT:
          operands[operand] = pc;  pc+=2;
          break;

        case MODE_VARIABLE:
          operands[operand] = get_memory_word(pc); pc+=2;
          break;

        case MODE_INDEXED:
          operands[operand] = get_memory_word(pc); pc+=2;           /* address of variable containing base of array */
          operands[operand] = get_memory_word(operands[operand]);   /* get base of array */
          operands[operand] += get_memory_word(pc); pc+=2;          /* add offset */
          break;

        case MODE_SP_INDEXED:
          operands[operand] = sp;
          operands[operand] += get_memory_word(pc); pc+=2;          /* add offset */
          break;

        default: text_message(TEXT_FATAL, "unknown addressing mode found at location %i\n", pc);
      }
      mode >>= 2;
    }

    exec_count++;

    switch (op)
    {
      case OP_ADD:
      put_memory_word(operands[0], get_memory_word(operands[1]) + get_memory_word(operands[2]));
      display("ADD ", operands[0], operands[1], operands[2]);
      break;
      case OP_SUB:
      put_memory_word(operands[0], get_memory_word(operands[1]) - get_memory_word(operands[2]));
      display("SUB ", operands[0], operands[1], operands[2]);
      break;
      case OP_MUL:
      put_memory_word(operands[0], get_memory_word(operands[1]) * get_memory_word(operands[2]));
      display("MUL ", operands[0], operands[1], operands[2]);
      break;
      case OP_DIV:
      put_memory_word(operands[0], get_memory_word(operands[1]) / get_memory_word(operands[2]));
      display("DIV ", operands[0], operands[1], operands[2]);
      break;
      case OP_EXP:
      put_memory_word(operands[0], (int) pow((double) get_memory_word(operands[1]), (double) get_memory_word(operands[2])));
      display("EXP ", operands[0], operands[1], operands[2]);
      break;
      case OP_EQ:
      put_memory_word(operands[0], get_memory_word(operands[1]) == get_memory_word(operands[2]));
      display("EQ  ", operands[0], operands[1], operands[2]);
      break;
      case OP_NE:
      put_memory_word(operands[0], get_memory_word(operands[1]) != get_memory_word(operands[2]));
      display("NE  ", operands[0], operands[1], operands[2]);
      break;
      case OP_GT:
      put_memory_word(operands[0], get_memory_word(operands[1]) > get_memory_word(operands[2]));
      display("GT  ", operands[0], operands[1], operands[2]);
      break;
      case OP_GE:
      put_memory_word(operands[0], get_memory_word(operands[1]) >= get_memory_word(operands[2]));
      display("GE  ", operands[0], operands[1], operands[2]);
      break;
      case OP_LT:
      put_memory_word(operands[0], get_memory_word(operands[1]) < get_memory_word(operands[2]));
      display("LT  ", operands[0], operands[1], operands[2]);
      break;
      case OP_LE:
      put_memory_word(operands[0], get_memory_word(operands[1]) <= get_memory_word(operands[2]));
      display("LE  ", operands[0], operands[1], operands[2]);
      break;
      case OP_CPY:
      put_memory_word(operands[0], get_memory_word(operands[1]));
      display("CPY ", operands[0], operands[1], operands[2]);
      break;
      case OP_BNE:
      display("BNE ", operands[0], operands[1], operands[2]);
      if (get_memory_word(operands[1]) != 0)
        pc = get_memory_word(operands[0]);
      break;
      case OP_BEQ:
      display("BEQ ", operands[0], operands[1], operands[2]);
      if (get_memory_word(operands[1]) == 0)
        pc = get_memory_word(operands[0]);
      break;
      case OP_BRA:
      display("BRA ", operands[0], operands[1], operands[2]);
      pc = get_memory_word(operands[0]);
      break;
      case OP_PRTS:
      display("PRTS", operands[0], operands[1], operands[2]);
      printf("%s", memory + operands[0]);
      break;
      case OP_PRTI:
      display("PRTI", operands[0], operands[1], operands[2]);
      printf("%i", (int) get_memory_word(operands[0]));
      break;
      case OP_HALT:
      display("HALT", operands[0], operands[1], operands[2]);
      printf(" -- Halted --\n");
      stop = 1;
      break;
      case OP_CALL :
      display("CALL ", operands[0], operands[1], operands[2]);
      put_memory_word(--sp, pc);
      pc = get_memory_word(operands[1]);
      break;
      case OP_RET:
      display("RET", operands[0], operands[1], operands[2]);
      pc = get_memory_word(++sp);
      break;
      case OP_PUSH :
      display("PUSH ", operands[0], operands[1], operands[2]);
      put_memory_word(--sp, get_memory_word(operands[1]));
      break;
      case OP_POP:
      display("POP", operands[0], operands[1], operands[2]);
      put_memory_word(operands[0], get_memory_word(++sp));
      break;
      case OP_LDSP :
      display("LDSP ", operands[0], operands[1], operands[2]);
      sp = get_memory_word(operands[1]);
      break;
      case OP_STSP:
      display("STSP", operands[0], operands[1], operands[2]);
      put_memory_word(operands[0], sp);
      break;

      default: 
      display("----", operands[0], operands[1], operands[2]);
      text_printf("\n");
      text_message(TEXT_FATAL, "illegal instruction encountered\n");
      break;
    }
  }
}

int main(int argc, char * argv[])
{
  clock_t mvmsim_finish_time, 
  mvmsim_start_time = clock(); 
  char * mvmsim_sourcefilename; 
  
  arg_message("mvmsim v1.5 - simulator for mvm\n\n" "Usage: mvmsim [options] source"); 

  arg_message(""); 
  arg_boolean('l', "Show load sequence", & mvmsim_echo_load);
  arg_boolean('t', "Print execution trace", & mvmsim_trace); 
  arg_boolean('v', "Set verbose mode", & mvmsim_verbose); 
  arg_message(""); 
  arg_message("You can contact the author (Adrian Johnstone) at:"); 
  arg_message(""); 
  arg_message("Computer Science Department, Royal Holloway, University of London"); 
  arg_message("Egham, Surrey, TW20 0EX UK. Email: A.Johnstone@rhbnc.ac.uk"); 
  
  mvmsim_sourcefilename = * arg_process(argc, argv); 
  
  if (mvmsim_sourcefilename == NULL)
    arg_help("No source file specified"); 
  
  if ((input_file = fopen(mvmsim_sourcefilename, "r"))== NULL)
    arg_help("Unable to open source file"); 
  
  if (mvmsim_verbose)
    text_printf("\nmvmsim v1.5 - simulator for mvm\n\n");
  
  mvmsim_load(); 
  mvmsim_execute(); 
  
  if (mvmsim_verbose)
  {
    mvmsim_finish_time = clock(); 
    text_printf("\n\n%.3f CPU seconds used, %lu MVM instructions executed\n", ((double)(mvmsim_finish_time - mvmsim_start_time))/ CLOCKS_PER_SEC, exec_count); 
  }
  
  return 0; 
}

/* End of mvmsim.c */
