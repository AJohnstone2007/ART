# Makefile for RDP release version 1.20 Adrian Johnstone 6 Nov 1994

###############################################################################
# Configuration section: uncomment one of the following blocks

# Configuration for gcc on Unix. Also works for g++ if you set CC = g++
CC	= g++
OBJ = .o
EXE =
DIFF = diff -s
RM = rm
CP = cp
SUPP_DIR = ./rdp_supp/
CFLAGS	= -I$(SUPP_DIR) -Wmissing-prototypes -Wstrict-prototypes -fno-common -Wall -ansi -pedantic
LINK = $(CC) $(CFLAGS) -o ./
MATHS = -lm
HERE = ./
# End of gcc on Unix configuration

# Configuration for Borland C 3.1 on MS-DOS
#CC	= bcc
#OBJ = .obj
#EXE = .exe
#DIFF = fc
#RM = del
#CP = copy
#SUPP_DIR = .\rdp_supp\\
#CFLAGS	= -I$(SUPP_DIR) -A -N -mh
#LINK = $(CC) $(CFLAGS) -o.\\
#MATHS =
#HERE = .\\
# End of Borland C 3.1 on MS-DOS configuration

# End of configuration : don't alter anything below this line
###############################################################################
  
# Standard macros

RDP_SUPP = crash$(OBJ) scan$(OBJ) set$(OBJ) symbol$(OBJ)

# Standard suffix rules

.c$(OBJ):
	$(CC) $(CFLAGS) -c $*.c

# default target: do everything

all: mini_test toy_test pas_test gen_test

# Build the support modules: do this locally in case user wants debug options
crash$(OBJ): 
	$(CC) $(CFLAGS) -c $(SUPP_DIR)crash.c

set$(OBJ):
	$(CC) $(CFLAGS) -c $(SUPP_DIR)set.c

symbol$(OBJ):
	$(CC) $(CFLAGS) -c $(SUPP_DIR)symbol.c

scan$(OBJ):
	$(CC) $(CFLAGS) -c $(SUPP_DIR)scan.c

# compile hand written rdp

rdp$(EXE): rdp$(OBJ) rdp_aux$(OBJ) $(RDP_SUPP)
	$(LINK)rdp$(EXE) rdp$(OBJ) rdp_aux$(OBJ) $(RDP_SUPP)

# generate and compile rdp_gen from rdp.bnf

rdp_gen.c: rdp$(EXE) rdp.bnf
	$(HERE)rdp -ordp_gen rdp

rdp_gen$(EXE): rdp_gen$(OBJ) rdp_aux$(OBJ) $(RDP_SUPP)
	$(LINK)rdp_gen$(EXE) rdp_gen$(OBJ) rdp_aux$(OBJ) $(RDP_SUPP)

# run rdp.bnf through rdp_gen and diff to see if they are the same

gen_test: rdp_gen$(EXE)
	-$(CP) rdp_gen.c rdp_gen1.c
	$(HERE)rdp_gen -ordp_gen rdp
	-$(DIFF) rdp_gen.c rdp_gen1.c
	-$(RM) rdp_gen1.c

# generate, compile and test the pascal parser

pascal.c: rdp$(EXE) pascal.bnf
# Need -F to override dangling else
	$(HERE)rdp -opascal -F pascal

pascal$(EXE): pascal$(OBJ) $(RDP_SUPP)
	$(LINK)pascal$(EXE) pascal$(OBJ) $(RDP_SUPP)

pas_test: pascal$(EXE)
	$(HERE)pascal -v test

# generate, compile and test the toy parser

toy.c: rdp$(EXE) toy.bnf
	$(HERE)rdp -F -otoy toy

toy$(EXE): toy$(OBJ) $(RDP_SUPP)
	$(LINK)toy$(EXE) toy$(OBJ) $(RDP_SUPP) $(MATHS)

toy_test: toy$(EXE)
	$(HERE)toy -v test

# generate, compile and test the mini parser

mini.c: rdp$(EXE) mini.bnf
	$(HERE)rdp -omini mini

mini$(EXE): mini$(OBJ) $(RDP_SUPP)
	$(LINK)mini$(EXE) mini$(OBJ) $(RDP_SUPP)

mini_test: mini$(EXE)
	$(HERE)mini -v test

# get rid of intermediate files

clean:
	-$(RM) *$(OBJ)
	-$(RM) rdp_gen*.*
	-$(RM) pascal.c
	-$(RM) pascal.h
	-$(RM) toy.c
	-$(RM) toy.h
	-$(RM) mini.c
	-$(RM) mini.h

# return to intial distribution state by deleting executables

veryclean: clean
	-$(RM) rdparser.c
	-$(RM) rdparser.h
	-$(RM) rdparser$(EXE)
	-$(RM) $(HERE)rdp$(EXE)
	-$(RM) rdp_gen$(EXE)
	-$(RM) pascal$(EXE)
	-$(RM) toy$(EXE)
	-$(RM) mini$(EXE)
	-$(RM) *.swp
	-$(RM) *.dsk
	-$(RM) *.bak
